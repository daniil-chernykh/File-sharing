package ru.hamming.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Интерфейс для реализации методов которые будут отправлять ответы из модуля (node) в брокер сообщений
 * */
public interface ProducerService {
    void producerAnswer(SendMessage sendMessage);
}
