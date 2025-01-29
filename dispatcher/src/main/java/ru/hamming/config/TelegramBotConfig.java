package ru.hamming.config;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.hamming.controller.TelegramBot;

@Configuration
public class TelegramBotConfig {

    private static final Logger log = Logger.getLogger(TelegramBotConfig.class);

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBot bot) {

        TelegramBotsApi botsApi = null;
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

//        try {
////            botsApi.registerBot(bot);
////            botsApi.registerBot(bot.setWebhook());
//        } catch (TelegramApiException e) {
//            log.error(e);
//        }
        return botsApi;
    }
}
