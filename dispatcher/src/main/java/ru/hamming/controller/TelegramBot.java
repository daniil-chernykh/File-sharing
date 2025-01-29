package ru.hamming.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

//import java.util.logging.Logger;

/** Класс, который отвечает за взаимодействие сервера с телеграммом
 * будет задействована технология LongPolling
 *
 * */
@Component
public class TelegramBot extends TelegramWebhookBot {
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.uri}")
    private String botUri;
    private UpdateController updateController;

    public TelegramBot(UpdateController updateController) {
        this.updateController = updateController;
    }

    @PostConstruct
    public void init() {
        updateController.registerBot(this);
        try {
            var setWebhook = SetWebhook.builder().url(botUri).build();
            this.setWebhook(setWebhook);
        } catch (TelegramApiException e) {
            log.error(e);

        }
    }


    private static final Logger log = Logger.getLogger(TelegramBot.class);

    @Override
    public String getBotToken() {
        return botToken;
    }


    @Override
    public String getBotUsername() {
        return botName;
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

    @Override
    public String getBotPath() {
        return "/update";
    }
}
