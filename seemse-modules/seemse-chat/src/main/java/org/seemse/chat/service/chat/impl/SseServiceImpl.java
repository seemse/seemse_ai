package org.seemse.chat.service.chat.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.seemse.chat.enums.promptTemplateEnum;
import org.seemse.chat.factory.ChatServiceFactory;
import org.seemse.chat.service.chat.IChatCostService;
import org.seemse.chat.service.chat.IChatService;
import org.seemse.chat.service.chat.ISseService;
import org.seemse.chat.support.ChatRetryHelper;
import org.seemse.chat.support.RetryNotifier;
import org.seemse.chat.util.SSEUtil;
import org.seemse.common.chat.entity.Tts.TextToSpeech;
import org.seemse.common.chat.entity.chat.Message;
import org.seemse.common.chat.entity.files.UploadFileResponse;
import org.seemse.common.chat.entity.whisper.WhisperResponse;
import org.seemse.common.chat.openai.OpenAiStreamClient;
import org.seemse.common.chat.request.ChatRequest;
import org.seemse.common.core.utils.DateUtils;
import org.seemse.common.core.utils.StringUtils;
import org.seemse.common.core.utils.file.FileUtils;
import org.seemse.common.core.utils.file.MimeTypeUtils;
import org.seemse.common.satoken.utils.LoginHelper;
import org.seemse.domain.bo.ChatSessionBo;
import org.seemse.domain.bo.QueryVectorBo;
import org.seemse.domain.vo.ChatModelVo;
import org.seemse.domain.vo.KnowledgeInfoVo;
import org.seemse.domain.vo.PromptTemplateVo;
import org.seemse.service.IChatModelService;
import org.seemse.service.IChatSessionService;
import org.seemse.service.IKnowledgeInfoService;
import org.seemse.service.IPromptTemplateService;
import org.seemse.service.VectorStoreService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * @author ageer
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SseServiceImpl implements ISseService {

    private final OpenAiStreamClient openAiStreamClient;
    private final VectorStoreService vectorStoreService;
    private final IChatCostService chatCostService;
    private final IChatModelService chatModelService;
    private final ChatServiceFactory chatServiceFactory;
    private final IChatSessionService chatSessionService;
    private final IKnowledgeInfoService knowledgeInfoService;
    private final IPromptTemplateService promptTemplateService;

    private ChatModelVo chatModelVo;

    @Override
    public SseEmitter sseChat(ChatRequest chatRequest, HttpServletRequest request) {
        SseEmitter sseEmitter = new SseEmitter(0L);
        try {
            // 保底：记录 token
            try {
                chatRequest.setToken(StpUtil.getTokenValue());
            } catch (Exception ignore) { }

            // ✅ 提取用户最新问题（在知识库插入前）
            String userPrompt = extractLatestUserPrompt(chatRequest.getMessages());
            chatRequest.setPrompt(userPrompt);

            // 构建消息列表（包含系统提示、知识库等）
            buildChatMessageList(chatRequest, userPrompt);

            // 设置角色
            chatRequest.setRole(Message.Role.USER.getName());

            if (LoginHelper.isLogin()) {
                chatRequest.setUserId(LoginHelper.getUserId());

                // 创建新会话
                if (chatRequest.getSessionId() == null) {
                    ChatSessionBo chatSessionBo = new ChatSessionBo();
                    chatSessionBo.setUserId(chatCostService.getUserId());
                    chatSessionBo.setSessionTitle(getFirst10Characters(userPrompt));
                    chatSessionBo.setSessionContent(userPrompt);
                    chatSessionService.insertByBo(chatSessionBo);
                    chatRequest.setSessionId(chatSessionBo.getId());
                }

                // 保存用户消息（不计费）
                chatCostService.saveMessage(chatRequest);
            }

            // 自动选择模型
            IChatService chatService = autoSelectModelAndGetService(chatRequest);

            if (Boolean.TRUE.equals(chatRequest.getAutoSelectModel())) {
                ChatModelVo currentModel = this.chatModelVo;
                String currentCategory = currentModel.getCategory();
                ChatRetryHelper.executeWithRetry(
                        currentModel,
                        currentCategory,
                        chatModelService,
                        sseEmitter,
                        (modelForTry, onFailure) -> {
                            chatRequest.setModel(modelForTry.getModelName());
                            RetryNotifier.setFailureCallback(sseEmitter, onFailure);
                            try {
                                autoSelectServiceByCategoryAndInvoke(chatRequest, sseEmitter, modelForTry.getCategory());
                            } finally { }
                        }
                );
            } else {
                chatService.chat(chatRequest, sseEmitter);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            SSEUtil.sendErrorEvent(sseEmitter, e.getMessage());
        }
        return sseEmitter;
    }

    /**
     * 从消息列表中提取最新的用户提问
     */
    private String extractLatestUserPrompt(List<Message> messages) {
        if (CollectionUtil.isEmpty(messages)) return "";

        // 从后往前找最后一个 user 消息
        for (int i = messages.size() - 1; i >= 0; i--) {
            Message msg = messages.get(i);
            if (Message.Role.USER.getName().equals(msg.getRole())) {
                return extractContent(msg.getContent());
            }
        }
        return "";
    }

    /**
     * 安全提取 content 内容（兼容 String 和 List）
     */
    private String extractContent(Object content) {
        if (content == null) return "";
        if (content instanceof String str) {
            return str;
        } else if (content instanceof List<?> list && !list.isEmpty()) {
            return list.stream()
                    .map(Object::toString)
                    .filter(StringUtils::isNotEmpty)
                    .findFirst()
                    .orElse("");
        }
        return content.toString();
    }

    /**
     * 构建消息列表：系统提示 + 知识库上下文 + 原始消息
     */
    private void buildChatMessageList(ChatRequest chatRequest, String userPrompt) {
        List<Message> messages = chatRequest.getMessages();

        // 处理知识库逻辑（可能插入多条 context）
        String sysPrompt = processKnowledgeBase(chatRequest, messages, userPrompt);

        // 插入系统提示词（必须在最前面）
        Message systemMessage = Message.builder()
                .role(Message.Role.SYSTEM)
                .content(sysPrompt)
                .build();
        messages.add(0, systemMessage);

        chatRequest.setSysPrompt(sysPrompt);
    }

    /**
     * 处理知识库：查询向量库，插入上下文
     */
    private String processKnowledgeBase(ChatRequest chatRequest, List<Message> messages, String userPrompt) {
        if (StringUtils.isEmpty(chatRequest.getKid())) {
            return getPromptTemplatePrompt(promptTemplateEnum.VECTOR.getDesc());
        }

        try {
            KnowledgeInfoVo knowledgeInfoVo = knowledgeInfoService.queryById(Long.valueOf(chatRequest.getKid()));
            if (knowledgeInfoVo == null) {
                log.warn("知识库信息不存在，kid: {}", chatRequest.getKid());
                return getPromptTemplatePrompt(promptTemplateEnum.VECTOR.getDesc());
            }

            ChatModelVo chatModel = chatModelService.selectModelByName(knowledgeInfoVo.getEmbeddingModelName());
            if (chatModel == null) {
                log.warn("向量模型配置不存在，模型名称: {}", knowledgeInfoVo.getEmbeddingModelName());
                return getPromptTemplatePrompt(promptTemplateEnum.VECTOR.getDesc());
            }

            QueryVectorBo queryVectorBo = buildQueryVectorBo(userPrompt, chatRequest.getKid(), chatModel, knowledgeInfoVo);
            List<String> nearestList = vectorStoreService.getQueryVector(queryVectorBo);

            // ✅ 插入知识库内容，加前缀避免混淆
            addKnowledgeMessages(messages, nearestList);

            return getKnowledgeSystemPrompt(knowledgeInfoVo);

        } catch (Exception e) {
            log.error("处理知识库信息失败: {}", e.getMessage(), e);
            return getPromptTemplatePrompt(promptTemplateEnum.VECTOR.getDesc());
        }
    }

    private QueryVectorBo buildQueryVectorBo(String query, String kid, ChatModelVo chatModel, KnowledgeInfoVo knowledgeInfoVo) {
        QueryVectorBo queryVectorBo = new QueryVectorBo();
        queryVectorBo.setQuery(query);
        queryVectorBo.setKid(kid);
        queryVectorBo.setApiKey(chatModel.getApiKey());
        queryVectorBo.setBaseUrl(chatModel.getApiHost());
        queryVectorBo.setVectorModelName(knowledgeInfoVo.getVectorModelName());
        queryVectorBo.setEmbeddingModelName(knowledgeInfoVo.getEmbeddingModelName());
        queryVectorBo.setMaxResults(knowledgeInfoVo.getRetrieveLimit());
        return queryVectorBo;
    }

    /**
     * ✅ 插入知识库消息，使用【知识库】前缀
     */
    private void addKnowledgeMessages(List<Message> messages, List<String> nearestList) {
        for (String content : nearestList) {
            Message kbMessage = Message.builder()
                    .role(Message.Role.USER) // 或可改为 "system" 如果模型支持
                    .content("【知识库】" + content)
                    .build();
            messages.add(kbMessage);
        }
    }

    private String getKnowledgeSystemPrompt(KnowledgeInfoVo knowledgeInfoVo) {
        String sysPrompt = knowledgeInfoVo.getSystemPrompt();
        if (StringUtils.isEmpty(sysPrompt)) {
            sysPrompt = "###角色设定\n" +
                    "你是一个智能知识助手，专注于利用上下文中的信息来提供准确和相关的回答。\n" +
                    "###指令\n" +
                    "当用户的问题与上下文知识匹配时，利用上下文信息进行回答。如果问题与上下文不匹配，运用自身的推理能力生成合适的回答。\n" +
                    "###限制\n" +
                    "确保回答清晰简洁，避免提供不必要的细节。始终保持语气友好\n" +
                    "当前时间：" + DateUtils.getDate();
        }
        return sysPrompt;
    }

    private String getPromptTemplatePrompt(String category) {
        PromptTemplateVo promptTemplateVo = promptTemplateService.queryByCategory(category);
        if (Objects.isNull(promptTemplateVo) || StringUtils.isEmpty(promptTemplateVo.getTemplateContent())) {
            return getDefaultSystemPrompt();
        }
        return promptTemplateVo.getTemplateContent();
    }

    private String getDefaultSystemPrompt() {
        String sysPrompt = chatModelVo != null ? chatModelVo.getSystemPrompt() : null;
        if (StringUtils.isEmpty(sysPrompt)) {
            sysPrompt = "你是一个由seemse_ai开发的人工智能助手，名字叫seemse人工智能助手。"
                    + "你擅长中英文对话，能够理解并处理各种问题，提供安全、有帮助、准确的回答。"
                    + "当前时间：" + DateUtils.getDate()
                    + "#注意：回复之前注意结合上下文和工具返回内容进行回复。";
        }
        return sysPrompt;
    }

    public static String getFirst10Characters(String str) {
        return str.length() > 10 ? str.substring(0, 10) : str;
    }

    private IChatService autoSelectModelAndGetService(ChatRequest chatRequest) {
        try {
            if (Boolean.TRUE.equals(chatRequest.getHasAttachment())) {
                chatModelVo = selectModelByCategory("image");
            } else if (Boolean.TRUE.equals(chatRequest.getAutoSelectModel())) {
                chatModelVo = selectModelByCategory("chat");
            } else {
                chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());
            }

            if (chatModelVo == null) {
                throw new IllegalStateException("未找到模型名称：" + chatRequest.getModel());
            }
            chatRequest.setModel(chatModelVo.getModelName());
            return chatServiceFactory.getChatService(chatModelVo.getCategory());
        } catch (Exception e) {
            log.error("模型选择和服务获取失败: {}", e.getMessage(), e);
            throw new IllegalStateException("模型选择和服务获取失败: " + e.getMessage());
        }
    }

    private void autoSelectServiceByCategoryAndInvoke(ChatRequest chatRequest, SseEmitter sseEmitter, String category) {
        IChatService service = chatServiceFactory.getChatService(category);
        service.chat(chatRequest, sseEmitter);
    }

    private ChatModelVo selectModelByCategory(String category) {
        ChatModelVo model = chatModelService.selectModelByCategoryWithHighestPriority(category);
        if (model == null) {
            throw new IllegalStateException("未找到" + category + "分类的模型配置");
        }
        return model;
    }

    @Override
    public ResponseEntity<Resource> textToSpeed(TextToSpeech textToSpeech) {
        ResponseBody body = openAiStreamClient.textToSpeech(textToSpeech);
        if (body != null) {
            InputStreamResource resource = new InputStreamResource(body.byteStream());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public WhisperResponse speechToTextTranscriptionsV2(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot convert an empty MultipartFile");
        }
        if (!FileUtils.isValidFileExtention(file, MimeTypeUtils.AUDIO__EXTENSION)) {
            throw new IllegalStateException("File Extention not supported");
        }

        File fileA = new File(System.getProperty("java.io.tmpdir") + File.separator + file.getOriginalFilename());
        try {
            file.transferTo(fileA);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert MultipartFile to File", e);
        }
        return openAiStreamClient.speechToTextTranscriptions(fileA);
    }

    @Override
    public UploadFileResponse upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload an empty MultipartFile");
        }
        if (!FileUtils.isValidFileExtention(file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION)) {
            throw new IllegalStateException("File Extention not supported");
        }
        return openAiStreamClient.uploadFile("fine-tune", convertMultiPartToFile(file));
    }

    private File convertMultiPartToFile(MultipartFile multipartFile) {
        File file = null;
        try {
            String originalFileName = multipartFile.getOriginalFilename();
            String extension = ".tmp";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            Path tempFile = Files.createTempFile(null, extension);
            file = tempFile.toFile();

            try (InputStream inputStream = multipartFile.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(file)) {
                byte[] bytes = new byte[1024];
                int read;
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            }
        } catch (IOException e) {
            log.error("文件转换失败", e);
        }
        return file;
    }
}
