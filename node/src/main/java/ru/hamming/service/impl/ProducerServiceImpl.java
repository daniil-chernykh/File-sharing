package ru.hamming.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.hamming.service.MessageSender;
import ru.hamming.service.ProducerService;

import static ru.hamming.model.RabbitQueue.ANSWER_MESSAGE;

@Log4j
@Service
@Component
public class ProducerServiceImpl implements ProducerService, MessageSender {

    private final RabbitTemplate rabbitTemplate;

    public ProducerServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void producerAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }
    @Override
    public void sendAnswer(String text, Long chatId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        producerAnswer(sendMessage);
        log.info("Sent answer to chat id: " + chatId);
    }
}