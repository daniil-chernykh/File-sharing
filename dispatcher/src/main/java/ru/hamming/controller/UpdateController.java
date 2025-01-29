package ru.hamming.controller;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hamming.service.UpdateProducer;
import ru.hamming.utils.MessageUtils;

import static ru.hamming.model.RabbitQueue.*;

/**
 * Класс контроллер, который будет распределять входящие сообщения от ТГ бота
 */
@Component
@Log4j
public class UpdateController {

    private TelegramBot telegramBot; // объект для отправки ответных сообщений
    private MessageUtils messageUtils; // утилита для работы с сообщениями
    private UpdateProducer updateProducer; // обработчик обновлений

    private static final Logger log = Logger.getLogger(UpdateController.class); // логгер для отладки

    // Конструктор класса, в который передаются зависимости
    public UpdateController(UpdateProducer updateProducer, MessageUtils messageUtils) {
        this.updateProducer = updateProducer;
        this.messageUtils = messageUtils;
    }

    // Метод для регистрации TelegramBot
    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    // Метод для первичной валидации входящих данных
    public void processUpdate(Update update) {
        // Проверка на null
        if (update == null) {
            log.error("Received update is null");
            return;
        }

        // Проверяем, является ли сообщение частью обновления
        if (update.hasMessage()) {
            distributeMessagesByType(update); // обработка входящих сообщений
        } else {
            log.error("Unsupported message type is received: " + update);
        }
    }

    // Метод для распределения сообщений в зависимости от их типа
    private void distributeMessagesByType(Update update) {
        var message = update.getMessage(); // Получаем сообщение из обновления

        // распределяем сообщения по очередям брокера в зависимости от типа входящих данных
        if (message.hasText()) {
            processTextMessage(update); // обработка текстовых сообщений
        } else if (message.hasDocument()) {
            processDocumentMessage(update); // обработка документов
        } else if (message.hasPhoto()) {
            processPhotoMessage(update); // обработка фото
        } else {
            setUnsupportedMessageTypeView(update); // обработка неподдерживаемых типов сообщений
        }
    }

    // Метод для обработки неподдерживаемых типов сообщений
    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Неподдерживаемый тип сообщения");

        setView(sendMessage);
    }

    // Метод для уведомления о получении файла
    private void setFileIsReceivedView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Файл получен! Обрабатывается...");

        setView(sendMessage);
    }

    // Метод для отправки ответного сообщения
    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    // Метод для обработки фотографий
    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update); // Отправляем обновление для обработки
        setFileIsReceivedView(update); // Уведомляем о получении
    }

    // Метод для обработки документов
    private void processDocumentMessage(Update update) {

        updateProducer.produce(DOC_MESSAGE_UPDATE, update); // Отправляем обновление для обработки
        setFileIsReceivedView(update); // Уведомляем о получении
    }

    // Метод для обработки текстовых сообщений
    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update); // Отправляем обновление для обработки
    }
}
