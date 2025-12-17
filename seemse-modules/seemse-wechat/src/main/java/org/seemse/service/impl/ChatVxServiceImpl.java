package org.seemse.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.seemse.common.chat.entity.chat.ChatCompletion;
import org.seemse.common.chat.entity.chat.ChatCompletionResponse;
import org.seemse.common.chat.entity.chat.Message;
import org.seemse.common.chat.openai.OpenAiStreamClient;
import org.seemse.service.IChatVxService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatVxServiceImpl implements IChatVxService {

    private final OpenAiStreamClient openAiStreamClient;

    @Override
    public String chat(String prompt) {
        List<Message> messageList = new ArrayList<>();
        Message message = Message.builder().role(Message.Role.USER).content(prompt).build();
        messageList.add(message);
        ChatCompletion chatCompletion = ChatCompletion
            .builder()
            .messages(messageList)
            .model("gpt-4o-mini")
            .stream(false)
            .build();
        ChatCompletionResponse chatCompletionResponse = openAiStreamClient.chatCompletion(chatCompletion);
        return chatCompletionResponse.getChoices().get(0).getMessage().getContent().toString();
    }

}
