package org.seemse.chat.service.chat.impl;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatRequestModel;
import io.github.ollama4j.models.generate.OllamaStreamHandler;
import lombok.extern.slf4j.Slf4j;
import org.seemse.chat.enums.ChatModeType;
import org.seemse.chat.service.chat.IChatService;
import org.seemse.chat.util.SSEUtil;
import org.seemse.common.chat.entity.chat.Message;
import org.seemse.common.chat.request.ChatRequest;
import org.seemse.domain.vo.ChatModelVo;
import org.seemse.service.IChatModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.seemse.chat.support.RetryNotifier;
import org.seemse.chat.support.ChatServiceHelper;


/**
 * @author ageer
 */
@Service
@Slf4j
public class OllamaServiceImpl implements IChatService {

    @Autowired
    private IChatModelService chatModelService;

    @Override
    public SseEmitter chat(ChatRequest chatRequest, SseEmitter emitter) {
        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());
        String host = chatModelVo.getApiHost();
        List<Message> msgList = chatRequest.getMessages();

        List<OllamaChatMessage> messages = new ArrayList<>();
        for (Message message : msgList) {
            OllamaChatMessage ollamaChatMessage = new OllamaChatMessage();
            ollamaChatMessage.setRole(OllamaChatMessageRole.USER);
            ollamaChatMessage.setContent(message.getContent().toString());
            messages.add(ollamaChatMessage);
        }
        OllamaAPI api = new OllamaAPI(host);
        api.setRequestTimeoutSeconds(100);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(chatRequest.getModel());

        OllamaChatRequestModel requestModel = builder
                .withMessages(messages)
                .build();

        // 异步执行 OllAma API 调用
        CompletableFuture.runAsync(() -> {
            try {
                StringBuilder response = new StringBuilder();
                OllamaStreamHandler streamHandler = (s) -> {
                    String substr = s.substring(response.length());
                    response.append(substr);
                    try {
                        emitter.send(substr);
                    } catch (IOException e) {
                        ChatServiceHelper.onStreamError(emitter, e.getMessage());
                    }
                };
                api.chat(requestModel, streamHandler);
                emitter.complete();
                RetryNotifier.clear(emitter);
            } catch (Exception e) {
                ChatServiceHelper.onStreamError(emitter, e.getMessage());
            }
        });

        return emitter;
    }

    @Override
    public String getCategory() {
        return ChatModeType.OLLAMA.getCode();
    }
}
