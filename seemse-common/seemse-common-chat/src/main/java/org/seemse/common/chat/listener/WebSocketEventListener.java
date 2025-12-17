package org.seemse.common.chat.listener;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.seemse.common.chat.entity.chat.ChatChoice;
import org.seemse.common.chat.entity.chat.ChatCompletionResponse;
import org.seemse.common.chat.entity.chat.Message;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;

/**
 *  OpenAI流式输出Socket接收
 *
 * @author https:www.unfbx.com
 * @date 2023-03-23
 */
@Slf4j
public class WebSocketEventListener extends EventSourceListener {

    private WebSocketSession session;

    /**
     * 消息结束标识
     */
    private final String msgEnd = "[DONE]";

    public WebSocketEventListener(WebSocketSession session) {
        this.session = session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.info("OpenAI建立Socket连接...");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SneakyThrows
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        log.info("OpenAI返回数据：{}", data);

        if (data.equals(msgEnd)) {
            log.info("OpenAI返回数据结束了");
            session.sendMessage(new TextMessage(msgEnd));
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            // 1. 先解析为 JsonNode，判断类型
            JsonNode rootNode = mapper.readTree(data);

            // 2. 判断是否是知识库的检索结果（根据字段特征）
            if (rootNode.has("type") && "retrieval_result".equals(rootNode.get("type").asText())) {
                // 处理知识库响应
                String answer = rootNode.has("answer") ? rootNode.get("answer").asText() : "";
                session.sendMessage(new TextMessage(answer));

            } else if (rootNode.has("choices")) {
                // 处理标准的 chat.completion.chunk 流式响应
                ChatCompletionResponse response = mapper.treeToValue(rootNode, ChatCompletionResponse.class);
                ChatChoice choice = response.getChoices().get(0);
                Message delta = choice.getDelta();

                // 如果 content 是 null 或空字符串，可以跳过发送
                if (delta.getContent() != null && !delta.getContent().isEmpty()) {
                    String deltaJson = mapper.writeValueAsString(delta);
                    session.sendMessage(new TextMessage(deltaJson));
                } else {
                    // 可选：发送 role 等基础信息
                    String deltaJson = mapper.writeValueAsString(delta);
                    session.sendMessage(new TextMessage(deltaJson));
                }

            } else {
                // 其他未知格式，直接原样转发（可选）
                log.warn("未知响应格式: {}", data);
                session.sendMessage(new TextMessage("{}")); // 避免前端卡住
            }

        } catch (Exception e) {
            log.error("【💥 解析SSE失败】原始数据: {}", data, e);
            // 💡 关键：不要抛异常中断连接，继续处理后续 chunk
            session.sendMessage(new TextMessage("{}")); // 发送空对象避免前端报错
        }
    }


    @Override
    public void onClosed(EventSource eventSource) {
        log.info("OpenAI关闭Socket连接...");
    }


    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        if (Objects.isNull(response)) {
            return;
        }
        ResponseBody body = response.body();
        if (Objects.nonNull(body)) {
            // 返回非流式回复内容
            log.error("Socket连接异常data：{}，异常：{}", body.string(), t);
        } else {
            log.error("Socket连接异常data：{}，异常：{}", response, t);
        }
        eventSource.cancel();
    }
}
