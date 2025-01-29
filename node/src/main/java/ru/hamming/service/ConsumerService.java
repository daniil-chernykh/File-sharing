package ru.hamming.service;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Интерфейс для реализации методов которые будут считывать сообщения из брокера сообщений.
 * */
public interface ConsumerService {
    void consumeTextMessageUpdates(Update update);
    void consumeDocMessageUpdates(Update update);
    void consumePhotoMessageUpdates(Update update);
}
