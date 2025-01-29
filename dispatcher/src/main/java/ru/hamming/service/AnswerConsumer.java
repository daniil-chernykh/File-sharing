package ru.hamming.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Происходит считывание ответов из RabbitMQ и далее передавать их в UpdateController
 * */
public interface AnswerConsumer {
    void consume(SendMessage sendMessage);
}
