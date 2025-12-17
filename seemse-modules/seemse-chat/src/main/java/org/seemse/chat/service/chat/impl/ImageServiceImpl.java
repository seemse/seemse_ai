package org.seemse.chat.service.chat.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ImageServiceImpl implements IChatService {

    @Autowired
    private IChatModelService chatModelService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Weaviate 地址
    private static final String WEAVIATE_HOST = "http://192.168.2.202:16038";

    // 不应缓存的工具（实时性要求高）
    private static final Set<String> NON_CACHEABLE_TOOLS = Set.of(
            "getCurrentTime", "getWeather", "sendEmail"
    );

    // ========== 生成语义 kid：基于用户问题 embedding 的哈希 ==========
    private String generateSemanticKid(String userQuery, ChatModelVo modelVo) {
        try {
            float[] vec = getEmbedding(userQuery, modelVo);
            // 使用前16维生成稳定哈希（避免浮点精度差异）
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < Math.min(16, vec.length); i++) {
                // 四舍五入到小数点后5位，转为整数字符串
                long scaled = Math.round(vec[i] * 100000);
                sb.append(scaled >= 0 ? "p" + scaled : "n" + (-scaled));
            }
            String raw = sb.toString();
            return "q_" + md5Hash(raw).substring(0, 16); // 取 MD5 前16位作为 kid
        } catch (Exception e) {
            log.warn("生成 semantic kid 失败，回退到 query hash", e);
            return "q_" + md5Hash(userQuery).substring(0, 16);
        }
    }

    private String md5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 不可用", e);
        }
    }

    // ========== 获取 Embedding ==========
    private float[] getEmbedding(String text, ChatModelVo modelVo) {
        try {
            String baseUrl = modelVo.getApiHost().replace("/v1/chat/completions", "/v1/embeddings");
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", "BAAI/bge-m3");
            body.put("input", text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + modelVo.getApiKey());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<?> data = (List<?>) response.getBody().get("data");
                if (data != null && !data.isEmpty()) {
                    Map<String, Object> first = (Map<String, Object>) data.get(0);
                    List<Number> embedding = (List<Number>) first.get("embedding");

                    float[] vector = new float[embedding.size()];
                    for (int i = 0; i < embedding.size(); i++) {
                        vector[i] = embedding.get(i).floatValue();
                    }
                    return vector;
                }
            }
            throw new RuntimeException("Embedding 返回为空或格式错误");
        } catch (Exception e) {
            log.error("获取 Embedding 失败，文本: {}", text, e);
            throw new RuntimeException("Embedding 调用失败", e);
        }
    }

    // ========== 安全格式化向量（无空格）==========
    private String formatVector(float[] vec) {
        if (vec == null || vec.length == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vec.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(String.format(Locale.US, "%.6f", vec[i]));
        }
        sb.append("]");
        return sb.toString();
    }

    // ========== 确保 Weaviate Class 存在 ==========
    private void ensureClassExists(String className) {
        String checkUrl = WEAVIATE_HOST + "/v1/schema/" + className;
        try {
            restTemplate.headForHeaders(checkUrl);
            return; // 已存在
        } catch (Exception ignored) {
        }

        List<Map<String, Object>> properties = Arrays.asList(
                Map.of("name", "text", "dataType", Collections.singletonList("text")),
                Map.of("name", "docId", "dataType", Collections.singletonList("string")),
                Map.of("name", "kid", "dataType", Collections.singletonList("string")),
                Map.of("name", "fid", "dataType", Collections.singletonList("string"))
        );

        Map<String, Object> classDef = Map.of(
                "class", className,
                "vectorizer", "none",
                "properties", properties
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(classDef, headers);

        try {
            restTemplate.postForEntity(WEAVIATE_HOST + "/v1/schema", entity, Map.class);
            log.info("✅ 创建 Weaviate class: {}", className);
        } catch (Exception ex) {
            log.warn("创建 class {} 可能已存在", className, ex);
        }
    }

    // ========== 存入 LocalKnowledge{kid} ==========
    private void storeToLocalKnowledge(List<Map<String, Object>> items, String kid, ChatModelVo modelVo) {
        if (items == null || items.isEmpty() || kid == null) return;

        String className = "LocalKnowledge" + kid;
        ensureClassExists(className);

        List<Map<String, Object>> objects = new ArrayList<>();
        for (Map<String, Object> item : items) {
            try {
                String text = extractTextFromItem(item);
                String docId = Optional.ofNullable(item.get("docId")).map(Object::toString).orElse(UUID.randomUUID().toString());
                String fid = Optional.ofNullable(item.get("fid")).map(Object::toString).orElse("default");

                float[] vector = getEmbedding(text, modelVo);

                Map<String, Object> obj = new LinkedHashMap<>();
                obj.put("class", className);
                obj.put("properties", Map.of(
                        "text", text,
                        "docId", docId,
                        "kid", kid,
                        "fid", fid
                ));
                obj.put("vector", vector);
                obj.put("id", UUID.randomUUID().toString());

                objects.add(obj);
            } catch (Exception e) {
                log.warn("跳过一条无法处理的数据", e);
            }
        }

        if (!objects.isEmpty()) {
            String url = WEAVIATE_HOST + "/v1/batch/objects";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(Map.of("objects", objects), headers);
            try {
                ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);

                if (resp.getStatusCode().is2xxSuccessful()) {
                    log.info("✅ 成功存入 {} 条数据到 {}", objects.size(), className);
                } else {
                    log.warn("Weaviate 批量插入失败，状态码: {}, 响应: {}",
                            resp.getStatusCode(),
                            resp.getBody() != null ? resp.getBody() : "无响应体");
                }
            } catch (Exception ex) {
                log.warn("Weaviate 插入异常", ex);
            }
        }
    }

    private String extractTextFromItem(Map<String, Object> item) throws JsonProcessingException {
        // 优先取 content，其次拼接其他字段
        if (item.containsKey("content")) {
            return String.valueOf(item.get("content")).trim();
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : item.entrySet()) {
            if (!"docId".equals(entry.getKey()) && !"fid".equals(entry.getKey())) {
                sb.append(entry.getValue()).append(" ");
            }
        }
        String text = sb.toString().trim();
        return text.isEmpty() ? objectMapper.writeValueAsString(item) : text;
    }

    // ========== 从 LocalKnowledge{kid} 检索 ==========
    private List<String> searchFromLocalKnowledge(String query, String kid, ChatModelVo modelVo) {
        try {
            float[] queryVec = getEmbedding(query, modelVo);
            String vectorStr = formatVector(queryVec);
            String className = "LocalKnowledge" + kid;

            String gql = String.format(
                    "{ Get { %s(nearVector: { vector: %s }, limit: 3) { text _additional { distance } } } }",
                    className, vectorStr
            );

            String url = WEAVIATE_HOST + "/v1/graphql";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(Map.of("query", gql), headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                return Collections.emptyList();
            }

            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
            if (data == null) return Collections.emptyList();

            Map<String, Object> get = (Map<String, Object>) data.get("Get");
            List<Map<String, Object>> results = (List<Map<String, Object>>) get.get(className);
            if (results == null) return Collections.emptyList();

            return results.stream()
                    .map(r -> (String) r.get("text"))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("检索 LocalKnowledge 异常", e);
            return Collections.emptyList();
        }
    }

    // ========== 检查 LocalKnowledge{kid} 是否有数据 ==========
    private boolean hasLocalKnowledgeData(String kid) {
        if (kid == null || kid.trim().isEmpty()) return false;
        String className = "LocalKnowledge" + kid;
        String gql = String.format("{ Aggregate { %s { meta { count } } } }", className);
        String url = WEAVIATE_HOST + "/v1/graphql";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(Map.of("query", gql), headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                if (data != null) {
                    Map<String, Object> aggregate = (Map<String, Object>) data.get("Aggregate");
                    if (aggregate != null) {
                        List<Map<String, Object>> list = (List<Map<String, Object>>) aggregate.get(className);
                        if (list != null && !list.isEmpty()) {
                            Number count = (Number) ((Map<String, Object>) ((Map<String, Object>) list.get(0)).get("meta")).get("count");
                            return count != null && count.intValue() > 0;
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            log.error("检查 LocalKnowledge{} 数据存在性异常", kid, e);
            return false;
        }
    }

    // ========== 工具辅助方法 ==========
    private List<Map<String, Object>> fetchAvailableTools() {
        try {
            ResponseEntity<List> response = restTemplate.exchange(
                    "http://localhost:8081/tools",
                    HttpMethod.GET,
                    null,
                    List.class
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            log.warn("获取工具列表失败", e);
            return Collections.emptyList();
        }
    }

    private String sendNonStreamingAndGetContent(ChatModelVo modelVo, ChatCompletion request) {
        try {
            String url = modelVo.getApiHost();
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", request.getModel());
            body.put("messages", request.getMessages());
            body.put("stream", false);
            body.put("temperature", request.getTemperature());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + modelVo.getApiKey());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<?> choices = (List<?>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> msg = (Map<String, Object>) ((Map<String, Object>) choices.get(0)).get("message");
                    return (String) msg.get("content");
                }
            }
            throw new RuntimeException("API 返回无效");
        } catch (Exception e) {
            log.error("调用模型失败", e);
            throw new RuntimeException("模型调用失败", e);
        }
    }

    private String callRemoteMcpTool(String methodName, Map<String, Object> params) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("jsonrpc", "2.0");
            payload.put("method", methodName);
            payload.put("params", params != null ? params : Collections.emptyMap());
            payload.put("id", UUID.randomUUID().toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "http://localhost:8081/invoke", entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Object result = response.getBody().get("result");
                return result != null ? result.toString() : "无结果";
            }
            return "调用失败: " + response.getStatusCode();
        } catch (Exception e) {
            log.error("调用工具 [{}] 异常", methodName, e);
            return "工具调用异常: " + e.getMessage();
        }
    }

    private Map<String, Object> extractToolCallFromText(String text) {
        try {
            String clean = text.trim();
            if (clean.startsWith("```")) {
                int start = clean.indexOf('{');
                int end = clean.lastIndexOf('}') + 1;
                if (start != -1 && end > start) {
                    clean = clean.substring(start, end);
                }
            }
            if (clean.startsWith("{") && clean.endsWith("}")) {
                Map<String, Object> obj = objectMapper.readValue(clean, Map.class);
                if (obj.containsKey("method") && obj.containsKey("params")) {
                    return obj;
                }
            }
        } catch (Exception e) {
            log.warn("JSON 解析失败: {}", text, e);
        }
        return null;
    }

    // ========== 核心 chat 方法 ==========
    @Override
    public SseEmitter chat(ChatRequest chatRequest, SseEmitter emitter) {
        ChatModelVo modelVo = chatModelService.selectModelByName(chatRequest.getModel());
        if (modelVo == null) {
            ChatServiceHelper.onStreamError(emitter, "模型未配置");
            emitter.complete();
            return emitter;
        }

        List<Message> messages = new ArrayList<>(chatRequest.getMessages());
        if (messages.isEmpty()) {
            ChatServiceHelper.onStreamError(emitter, "消息为空");
            emitter.complete();
            return emitter;
        }

        // 构建 system prompt
        List<Map<String, Object>> tools = fetchAvailableTools();
        StringBuilder sb = new StringBuilder();
        sb.append("你是一个智能助手，可以调用以下工具来回答用户问题。请严格遵守规则：\n");
        for (Map<String, Object> t : tools) {
            Map<String, Object> func = (Map<String, Object>) t.get("function");
            if (func != null) {
                sb.append("- 工具名: ").append(func.get("name"))
                        .append(", 描述: ").append(func.get("description") != null ? func.get("description") : "无")
                        .append("\n");
            }
        }
        sb.append("\n如果需要调用工具，请仅输出一个合法的 JSON 对象，格式为：\n")
                .append("{\"method\":\"工具名\",\"params\":{\"参数1\":\"值1\"}}\n")
                .append("不要包含任何其他文字、解释、Markdown 或换行。");

        List<Message> checkMessages = new ArrayList<>();
        checkMessages.add(Message.builder().role(Message.Role.SYSTEM).content(sb.toString()).build());
        checkMessages.addAll(messages);

        ChatCompletion checkReq = ChatCompletion.builder()
                .model(modelVo.getModelName())
                .messages(checkMessages)
                .stream(false)
                .temperature(0.0)
                .build();

        String firstResponse = sendNonStreamingAndGetContent(modelVo, checkReq);
        log.info("模型首次响应: {}", firstResponse);

        Map<String, Object> toolCall = extractToolCallFromText(firstResponse);
        List<Message> finalMessages = new ArrayList<>(messages);

        if (toolCall != null) {
            String methodName = (String) toolCall.get("method");
            Map<String, Object> params = (Map<String, Object>) toolCall.get("params");

            log.info("🔧 检测到工具调用: method={}, params={}", methodName, params);

            String userQuery = messages.get(messages.size() - 1).getContent();
            String autoKid = generateSemanticKid(userQuery, modelVo);
            boolean shouldCache = !NON_CACHEABLE_TOOLS.contains(methodName);

            if (shouldCache && hasLocalKnowledgeData(autoKid)) {
                log.info("✅ 语义缓存命中: LocalKnowledge{}", autoKid);
                List<String> retrieved = searchFromLocalKnowledge(userQuery, autoKid, modelVo);
                if (!retrieved.isEmpty()) {
                    finalMessages.add(Message.builder()
                            .role(Message.Role.USER)
                            .content("根据以下信息回答问题：\n" + String.join("\n", retrieved))
                            .build());
                } else {
                    finalMessages.add(Message.builder()
                            .role(Message.Role.USER)
                            .content("缓存中未检索到相关内容。")
                            .build());
                }
            } else {
                String rawToolResult = callRemoteMcpTool(methodName, params);

                // 尝试解析为结构化数据
                List<Map<String, Object>> items = parseToolResult(rawToolResult);

                if (shouldCache && !items.isEmpty()) {
                    storeToLocalKnowledge(items, autoKid, modelVo);
                    log.info("💾 已缓存结果到 LocalKnowledge{}", autoKid);

                    // 立即检索用于本次回答
                    List<String> retrieved = searchFromLocalKnowledge(userQuery, autoKid, modelVo);
                    if (!retrieved.isEmpty()) {
                        finalMessages.add(Message.builder()
                                .role(Message.Role.USER)
                                .content("根据以下信息回答问题：\n" + String.join("\n", retrieved))
                                .build());
                    } else {
                        finalMessages.add(Message.builder()
                                .role(Message.Role.USER)
                                .content("工具返回了数据，但检索未命中。原始结果：" + rawToolResult)
                                .build());
                    }
                } else {
                    // 不缓存 or 解析失败 → 直接使用原始结果
                    finalMessages.add(Message.builder()
                            .role(Message.Role.USER)
                            .content("你调用了工具 '" + methodName + "'，结果是：" + rawToolResult + "。请据此回答。")
                            .build());
                }
            }
        }

        // 流式回答
        OpenAiStreamClient streamClient = ChatConfig.createOpenAiStreamClient(
                modelVo.getApiHost(), modelVo.getApiKey());
        SSEEventSourceListener listener = ChatServiceHelper.createOpenAiListener(emitter, chatRequest);

        ChatCompletion finalReq = ChatCompletion.builder()
                .model(modelVo.getModelName())
                .messages(finalMessages)
                .stream(true)
                .temperature(0.7)
                .build();

        streamClient.streamChatCompletion(finalReq, listener);
        return emitter;
    }

    // ========== 解析工具返回结果为 List<Map> ==========
    private List<Map<String, Object>> parseToolResult(String rawResult) {
        try {
            Object parsed = objectMapper.readValue(rawResult, Object.class);
            if (parsed instanceof List) {
                return (List<Map<String, Object>>) parsed;
            } else if (parsed instanceof Map) {
                return Collections.singletonList((Map<String, Object>) parsed);
            } else {
                // 非结构化文本，包装成一条记录
                return Collections.singletonList(Map.of("content", rawResult));
            }
        } catch (Exception e) {
            log.warn("工具结果解析为结构化数据失败，当作纯文本处理", e);
            return Collections.singletonList(Map.of("content", rawResult));
        }
    }

    @Override
    public String getCategory() {
        return ChatModeType.IMAGE.getCode();
    }
}
