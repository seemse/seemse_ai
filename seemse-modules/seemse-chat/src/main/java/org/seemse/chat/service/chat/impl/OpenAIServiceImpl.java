package org.seemse.chat.service.chat.impl;

import io.modelcontextprotocol.client.McpSyncClient;
import lombok.extern.slf4j.Slf4j;
import org.seemse.chat.config.ChatConfig;
import org.seemse.chat.enums.ChatModeType;
import org.seemse.chat.listener.SSEEventSourceListener;
import org.seemse.chat.service.chat.IChatService;
import org.seemse.chat.support.ChatServiceHelper;
import org.seemse.common.chat.entity.chat.ChatCompletion;
import org.seemse.common.chat.entity.chat.Message;
import org.seemse.common.chat.openai.OpenAiStreamClient;
import org.seemse.common.chat.request.ChatRequest;
import org.seemse.domain.vo.ChatModelVo;
import org.seemse.service.IChatModelService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;


/**
 * @author ageer
 */
@Service
@Slf4j
public class OpenAIServiceImpl implements IChatService {

    @Autowired
    private IChatModelService chatModelService;

    @Value("${spring.ai.mcp.client.enabled}")
    private Boolean enabled;

    private final ChatClient chatClient;

    public OpenAIServiceImpl(ChatClient.Builder chatClientBuilder, List<McpSyncClient> mcpSyncClients) {
        this.chatClient = chatClientBuilder
                .defaultOptions(
                        OpenAiChatOptions.builder().model("gpt-4o-mini").build())
                .defaultTools(new SyncMcpToolCallbackProvider(mcpSyncClients))
                .build();
    }

    @Override
    public SseEmitter chat(ChatRequest chatRequest,SseEmitter emitter) {
        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());
        OpenAiStreamClient openAiStreamClient = ChatConfig.createOpenAiStreamClient(chatModelVo.getApiHost(), chatModelVo.getApiKey());
        List<Message> messages = chatRequest.getMessages();
        if (enabled) {
            String toolString = mcpChat(chatRequest.getPrompt());
            Message userMessage = Message.builder().content("工具返回信息："+toolString).role(Message.Role.USER).build();
            messages.add(userMessage);
        }
        SSEEventSourceListener listener = ChatServiceHelper.createOpenAiListener(emitter, chatRequest);
        ChatCompletion completion = ChatCompletion
                .builder()
                .messages(messages)
                .model(chatRequest.getModel())
                .stream(true)
                .build();
        try {
            openAiStreamClient.streamChatCompletion(completion, listener);
        } catch (Exception ex) {
            ChatServiceHelper.onStreamError(emitter, ex.getMessage());
            throw ex;
        }
        return emitter;
    }

    public String mcpChat(String prompt){
        return this.chatClient.prompt(prompt).call().content();
    }

    @Override
    public String getCategory() {
        return ChatModeType.CHAT.getCode();
    }

}
