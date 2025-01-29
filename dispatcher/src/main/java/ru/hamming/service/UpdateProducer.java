package ru.hamming.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Происходит передача обновление в RabbitMQ
 * */
public interface UpdateProducer {

    void produce(String rabbitQueue, Update update);

}
