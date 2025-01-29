//package ru.hamming.service.impl;
//
//import lombok.extern.log4j.Log4j;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.api.objects.User;
//import ru.hamming.dao.AppUserDAO;
//import ru.hamming.dao.RawDataDAO;
//import ru.hamming.entity.AppDocument;
//import ru.hamming.entity.AppPhoto;
//import ru.hamming.entity.AppUser;
//import ru.hamming.entity.RawData;
//import ru.hamming.entity.enums.UserState;
//import ru.hamming.exceptions.UploadFileException;
//import ru.hamming.service.FileService;
//import ru.hamming.service.MainService;
//import ru.hamming.service.ProducerService;
//import ru.hamming.service.enums.ServiceCommands;
//
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static ru.hamming.entity.enums.UserState.BASIC_STATE;
//import static ru.hamming.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
//import static ru.hamming.service.enums.ServiceCommands.*;
//
///**
// * Данный класс является связующим звеном между базой данных и ConsumerServiceImpl, который передает сообщения из RabbitMQ
// * */
//@Service
//@Component
//@Log4j
//public class MainServiceImpl implements MainService {
//
//    private final RawDataDAO rawDataDAO;
//    private final ProducerService producerService;
//    private final AppUserDAO appUserDAO;
//    private final FileService fileService;
//
//    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
//
//    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO, FileService fileService) {
//        this.rawDataDAO = rawDataDAO;
//        this.producerService = producerService;
//        this.appUserDAO = appUserDAO;
//        this.fileService = fileService;
//    }
//
////    @Override
////    public void processTextMessage(Update update) {
////        saveRawData(update);
////        var appUser = findOrSaveAppUser(update);
////        var userState = appUser.getState();
////        var text = update.getMessage().getText();
////        var output = "";
////
////        var serviceCommand = ServiceCommands.fromValue(text);
////
////        if (CANCEL.equals(serviceCommand)) {
////            output = cancelProcess(appUser);
////        }
////        else if (BASIC_STATE.equals(userState)) {
////            output = processServiceCommand(appUser, text);
////        }
////        else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
////            //TODO добавить обработку email
////        }else {
////            log.error("Unknown user state: " + userState);
////            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
////        }
////
////        var chatId = update.getMessage().getChatId();
////        sendAnswer(output, chatId);
////
////
////    }
//
//    @Override
//    public void processTextMessage(Update update) {
//        try {
//            saveRawData(update);
//            var appUser = findOrSaveAppUser(update);
//            var userState = appUser.getState();
//            var text = update.getMessage().getText();
//            var output = "";
//
//            if(text == null) {
//                log.error("Text message is null: " + update);
//                output = "Произошла ошибка! Повторите попытку позже!";
//                var chatId = update.getMessage().getChatId();
//                sendAnswer(output, chatId);
//                return;
//            }
//            var serviceCommand = ServiceCommands.fromValue(text);
//
//            if (CANCEL.equals(serviceCommand)) {
//                output = cancelProcess(appUser);
//            }
//            else if (BASIC_STATE.equals(userState)) {
//                output = processServiceCommand(appUser, text);
//            }
//            else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
//                //TODO добавить обработку email
//            }else {
//                log.error("Unknown user state: " + userState);
//                output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
//            }
//            var chatId = update.getMessage().getChatId();
//            sendAnswer(output, chatId);
//            log.info("Text message processed successfully for user : " + appUser.getId());
//        } catch (Exception e) {
//            log.error("Error processing text message: ", e);
//            e.printStackTrace();
//            var chatId = update.getMessage().getChatId();
//            sendAnswer("Произошла ошибка! Повторите попытку позже!", chatId);
//        }
//    }
//
////    @Override
////    public void processDocMessage(Update update) {
////        saveRawData(update);
////        var appUser = findOrSaveAppUser(update);
////        var chatId = update.getMessage().getChatId();
////
////        if (isNotAllowToSendContent(chatId, appUser)) {
////            return;
////        }
////
////        try {
////            AppDocument document = fileService.processDoc(update.getMessage());
////            //TODO добавить метод генерации ссылки для скачивания документа
////            var answer = "Документ успешно загружен! Ссылка для скачивания: http://test.ru/get-doc/777";
////            sendAnswer(answer, chatId);
////        } catch (UploadFileException uploadFileException) {
////            log.error(uploadFileException);
////            String error = "К сожалению, загрузка файла не удалась. Повторите попытку позже";
////            sendAnswer(error, chatId);
////        }
////
////    }
////
////    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
////        var userState = appUser.getState();
////        if (!appUser.getIsActive()) {
////            var error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента.";
////            sendAnswer(error,chatId);
////        } else if (!BASIC_STATE.equals(userState)) {
////            var error = "Отмените текущую команду с помощью /cancel для отправки файлов.";
////            sendAnswer(error,chatId);
////            return true;
////        }
////
////        return false;
////    }
////
////    @Override
////    public void processPhotoMessage(Update update) {
////        saveRawData(update);
////        var appUser = findOrSaveAppUser(update);
////        var chatId = update.getMessage().getChatId();
////
////        if (isNotAllowToSendContent(chatId, appUser)) {
////            return;
////        }
////
////        try {
////            AppPhoto photo = fileService.processPhoto(update.getMessage());
////            //TODO добавить логику сохранения документа
////            var answer = "Фото успешно загружен! Ссылка для скачивания: http://test.ru/get-photo/777";
////            sendAnswer(answer,chatId);
////        } catch (UploadFileException uploadFileException) {
////            log.error(uploadFileException);
////            String error = "К сожалению, загрузка файла не удалась. Повторите попытку позже";
////            sendAnswer(error, chatId);
////        }
////
////    }
//
//
//    @Override
//    public void processDocMessage(Update update) {
//        log.info("Start processing doc message");
//        CompletableFuture.runAsync(() ->{
//            saveRawData(update);
//            var appUser = findOrSaveAppUser(update);
//            var chatId = update.getMessage().getChatId();
//
//            if (isNotAllowToSendContent(chatId, appUser)) {
//                return;
//            }
//            try {
//                CompletableFuture<AppDocument> documentFuture = CompletableFuture.supplyAsync(() -> fileService.processDoc(update.getMessage()),executorService);
//                documentFuture.thenAccept(document -> {
//                    var answer = "Документ успешно загружен! Ссылка для скачивания: http://test.ru/file/get-doc/" + document.getId();
//                    sendAnswer(answer, chatId);
//                    log.info("Doc message processed successfully:  " + document.getId());
//                }).exceptionally(e ->{
//                    log.error("Error while processing doc: ", e);
//                    String error = "К сожалению, загрузка файла не удалась. Повторите попытку позже";
//                    sendAnswer(error, chatId);
//                    return null;
//                });
//            } catch (Exception e) {
//                log.error("Error while processing doc: ", e);
//                String error = "К сожалению, загрузка файла не удалась. Повторите попытку позже";
//                sendAnswer(error, chatId);
//            }
//        },executorService);
//        log.info("Finish processing doc message");
//    }
//
//
//    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
//        var userState = appUser.getState();
//        if (!appUser.getIsActive()) {
//            var error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента.";
//            sendAnswer(error,chatId);
//            return true;
//        } else if (!BASIC_STATE.equals(userState)) {
//            var error = "Отмените текущую команду с помощью /cancel для отправки файлов.";
//            sendAnswer(error,chatId);
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public void processPhotoMessage(Update update) {
//        log.info("Start processing photo message");
//        CompletableFuture.runAsync(() ->{
//            saveRawData(update);
//            var appUser = findOrSaveAppUser(update);
//            var chatId = update.getMessage().getChatId();
//
//            if (isNotAllowToSendContent(chatId, appUser)) {
//                return;
//            }
//
//            try {
//                CompletableFuture<AppPhoto> photoFuture =  CompletableFuture.supplyAsync(() -> fileService.processPhoto(update.getMessage()), executorService);
//                photoFuture.thenAccept(photo ->{
//                    var answer = "Фото успешно загружено! Ссылка для скачивания: http://test.ru/file/get-photo/" + photo.getId();
//                    sendAnswer(answer, chatId);
//                    log.info("Photo message processed successfully:  " + photo.getId());
//                }).exceptionally(e -> {
//                    log.error("Error while processing photo: ", e);
//                    String error = "К сожалению, загрузка файла не удалась. Повторите попытку позже";
//                    sendAnswer(error, chatId);
//                    return null;
//                });
//            } catch (Exception e) {
//                log.error("Error while processing photo: ", e);
//                String error = "К сожалению, загрузка файла не удалась. Повторите попытку позже";
//                sendAnswer(error, chatId);
//            }
//        }, executorService);
//        log.info("Finish processing photo message");
//    }
//
//
//
//
//
//    // ответ пользователю на его действия
//    private void sendAnswer(String output, Long chatId) {
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setChatId(chatId);
//        sendMessage.setText(output);
//        producerService.producerAnswer(sendMessage);
//    }
//
//    // обработка команд всех кроме /cancel
////    private String processServiceCommand(AppUser appUser, String cmd) {
////
////        var serviceCommand = ServiceCommands.fromValue(cmd);
////
////        if (REGISTRATION.equals(serviceCommand)) {
////            //TODO добавить регистрацию
////            return "Временно не доступно";
////        } else if (HELP.equals(serviceCommand)) {
////            return help();
////        } else if (START.equals(serviceCommand)) {
////            return start();
////        }else {
////            return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
////        }
////    }
//
//    private String processServiceCommand(AppUser appUser, String cmd) {
//        try {
//            log.info("Processing command: " + cmd + " for user: " + appUser.getId());
//            if (cmd == null) {
//                log.error("Command is null");
//                return "Произошла ошибка! Повторите попытку позже!";
//            }
//            var serviceCommand = ServiceCommands.fromValue(cmd);
//
//            if (REGISTRATION.equals(serviceCommand)) {
//                //TODO добавить регистрацию
//                return "Временно не доступно";
//            } else if (HELP.equals(serviceCommand)) {
//                return help();
//            } else if (START.equals(serviceCommand)) {
//                return start();
//            } else {
//                log.error("Unknown command: " + cmd + " for user: " + appUser.getId());
//                return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
//            }
//        } catch (Exception e) {
//            log.error("Error while processing command: ", e);
//            e.printStackTrace();
//            return "Произошла ошибка! Повторите попытку позже!";
//        }
//    }
//
//
//
//    private String start() {
//        return "Приветствую! Чтобы посмотреть список доступных команд введите /help";
//    }
//
//    private String help() {
//        return "Список доступных команд:\n"
//                + "/cancel -  отмена выполнения текущей команды\n"
//                + "/registration - регистрация пользователя\n";
//    }
//
//    // данный метод будет устанавливать текущему пользователю базовое состояние и сохранять обновленные данные в БД
//    // обработка команды /cancel
////    private String cancelProcess(AppUser appUser) {
////        appUser.setState(BASIC_STATE);
////        appUserDAO.save(appUser);
////
////        return "Команда отменена!";
////    }
//
//    private String cancelProcess(AppUser appUser) {
//        try {
//            log.info("Canceling process for user: " + appUser.getId());
//            appUser.setState(BASIC_STATE);
//            appUserDAO.save(appUser);
//            log.info("Process canceled successfully for user: " + appUser.getId());
//            return "Команда отменена!";
//        } catch (Exception e) {
//            log.error("Error while canceling process: ", e);
//            e.printStackTrace();
//            return "Произошла ошибка при отмене команды!";
//        }
//    }
//
//    // пробуем искать в БД текущего пользователя
//    private AppUser findOrSaveAppUser(Update update) {
//        var telegramUser = update.getMessage().getFrom();
//        // объект уже представлен в БД имеет заполненный первичный ключ и связан с сессией Hibernate (проще говоря - этот объект уже есть в БД)
//        AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
//
//        if (persistentAppUser == null) {
//            // пока данный объект не находится в БД
//            AppUser transientAppUser = AppUser.builder()
//                    .telegramUserId(telegramUser.getId())
//                    .username(telegramUser.getUserName())
//                    .firstName(telegramUser.getFirstName())
//                    .lastName(telegramUser.getLastName())
//                    //TODO изменить значение по умолчанию после добавления регистрации
//                    .isActive(true)
//                    .state(BASIC_STATE)
//                    .build();
//
//            return appUserDAO.save(transientAppUser);
//        }
//
//        return persistentAppUser;
//    }
//
//    private void saveRawData(Update update) {
//        RawData rawData = RawData.builder()
//                .event(update)
//                .build();
//
////        rawData.save(rawData);
//
//    }
//}


package ru.hamming.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hamming.dao.AppUserDAO;
import ru.hamming.dao.RawDataDAO;
import ru.hamming.entity.AppDocument;
import ru.hamming.entity.AppPhoto;
import ru.hamming.entity.AppUser;
import ru.hamming.entity.RawData;
import ru.hamming.exceptions.UploadFileException;
import ru.hamming.service.*;
import ru.hamming.service.enums.LinkType;
import ru.hamming.service.enums.ServiceCommands;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.hamming.service.enums.ServiceCommands.*;
//import static ru.hamming.entity.UserState.BASIC_STATE;
//import static ru.hamming.entity.UserState.WAIT_FOR_EMAIL_STATE;
import static ru.hamming.entity.enums.UserState.*;

@Log4j
@Service
@Component
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;
    private final AppUserService appUserService;
    private final MessageSender messageSender;
    private final RawDataService rawDataService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO, FileService fileService, AppUserService appUserService, MessageSender messageSender, RawDataService rawDataService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
        this.appUserService = appUserService;
        this.messageSender = messageSender;
        this.rawDataService = rawDataService;
    }


    @Override
    public void processTextMessage(Update update) {
        try {
            saveRawData(update);
            var appUser = findOrSaveAppUser(update);
            var userState = appUser.getState();
            var text = update.getMessage().getText();
            var output = "";

            if(text == null) {
                log.error("Text message is null: " + update);
                output = "Произошла ошибка! Повторите попытку позже!";
                var chatId = update.getMessage().getChatId();
                sendAnswer(output, chatId);
                return;
            }
            var serviceCommand = ServiceCommands.fromValue(text);

            if (CANCEL.equals(serviceCommand)) {
                output = cancelProcess(appUser);
            }
            else if (BASIC_STATE.equals(userState)) {
                output = processServiceCommand(appUser, text);
            }
            else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
                output = appUserService.setEmail(appUser, text);
            }else {
                log.error("Unknown user state: " + userState);
                output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
            }
            var chatId = update.getMessage().getChatId();
            sendAnswer(output, chatId);
            log.info("Text message processed successfully for user : " + appUser.getId());
        } catch (Exception e) {
            log.error("Error processing text message: ", e);
            e.printStackTrace();
            var chatId = update.getMessage().getChatId();
            sendAnswer("Произошла ошибка! Повторите попытку позже!", chatId);
        }
    }

    @Override
    public void processDocMessage(Update update) {
        log.info("Start processing doc message");
        CompletableFuture.runAsync(() ->{
            saveRawData(update);
            var appUser = findOrSaveAppUser(update);
            var chatId = update.getMessage().getChatId();

            if (isNotAllowToSendContent(chatId, appUser)) {
                return;
            }
            try {
                CompletableFuture<AppDocument> documentFuture = CompletableFuture.supplyAsync(() -> fileService.processDoc(update.getMessage()),executorService);
                documentFuture.thenAccept(document -> {
                    String link = fileService.generateLink(document.getId(), LinkType.GET_DOC);
                    var answer = "Документ успешно загружен! Ссылка для скачивания:" + link;
                    sendAnswer(answer, chatId);
                    log.info("Doc message processed successfully:  " + document.getId());
                }).exceptionally(e ->{
                    log.error("Error while processing doc: ", e);
                    String error = "К сожалению, загрузка файла не удалась. Повторите попытку позже";
                    sendAnswer(error, chatId);
                    return null;
                });
            } catch (Exception e) {
                log.error("Error while processing doc: ", e);
                String error = "К сожалению, загрузка файла не удалась. Повторите попытку позже";
                sendAnswer(error, chatId);
            }
        },executorService);
        log.info("Finish processing doc message");
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if (!appUser.getIsActive()) {
            var error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента.";
            sendAnswer(error,chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            var error = "Отмените текущую команду с помощью /cancel для отправки файлов.";
            sendAnswer(error,chatId);
            return true;
        }
        return false;
    }

    @Override
    public void processPhotoMessage(Update update) {
        log.info("Start processing photo message");
        CompletableFuture.runAsync(() ->{
            saveRawData(update);
            var appUser = findOrSaveAppUser(update);
            var chatId = update.getMessage().getChatId();

            if (isNotAllowToSendContent(chatId, appUser)) {
                return;
            }

            try {
                CompletableFuture<AppPhoto> photoFuture =  CompletableFuture.supplyAsync(() -> fileService.processPhoto(update.getMessage()), executorService);
                photoFuture.thenAccept(photo -> {
                    String link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
                    var answer = "Фото успешно загружено! Ссылка для скачивания: " + link;
                    sendAnswer(answer, chatId);
                    log.info("Photo message processed successfully:  " + photo.getId());
                }).exceptionally(e -> {
                    log.error("Error while processing photo: ", e);
                    String error = "К сожалению, загрузка файла не удалась. Повторите попытку позже";
                    sendAnswer(error, chatId);
                    return null;
                });
            } catch (Exception e) {
                log.error("Error while processing photo: ", e);
                String error = "К сожалению, загрузка файла не удалась. Повторите попытку позже";
                sendAnswer(error, chatId);
            }
        }, executorService);
        log.info("Finish processing photo message");
    }

    // ответ пользователю на его действия
    private void sendAnswer(String output, Long chatId) {
        messageSender.sendAnswer(output, chatId);
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        try {
            log.info("Processing command: " + cmd + " for user: " + appUser.getId());
            if (cmd == null) {
                log.error("Command is null");
                return "Произошла ошибка! Повторите попытку позже!";
            }
            var serviceCommand = ServiceCommands.fromValue(cmd);

            if (REGISTRATION.equals(serviceCommand)) {
                return appUserService.registerUser(appUser);
            } else if (HELP.equals(serviceCommand)) {
                return help();
            } else if (START.equals(serviceCommand)) {
                return start();
            } else {
                log.error("Unknown command: " + cmd + " for user: " + appUser.getId());
                return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
            }
        } catch (Exception e) {
            log.error("Error while processing command: ", e);
            e.printStackTrace();
            return "Произошла ошибка! Повторите попытку позже!";
        }
    }


    private String start() {
        return "Приветствую! Чтобы посмотреть список доступных команд введите /help";
    }

    private String help() {
        return "Список доступных команд:\n"
                + "/cancel -  отмена выполнения текущей команды\n"
                + "/registration - регистрация пользователя\n";
    }


    private String cancelProcess(AppUser appUser) {
        try {
            log.info("Canceling process for user: " + appUser.getId());
            appUser.setState(BASIC_STATE);
            appUserDAO.save(appUser);
            log.info("Process canceled successfully for user: " + appUser.getId());
            return "Команда отменена!";
        } catch (Exception e) {
            log.error("Error while canceling process: ", e);
            e.printStackTrace();
            return "Произошла ошибка при отмене команды!";
        }
    }


    private AppUser findOrSaveAppUser(Update update) {
        try {
            log.info("Start find or save user, Update: " + update);
            var telegramUser = update.getMessage().getFrom();

            if(telegramUser == null) {
                log.error("Telegram user is null: " + update);
                return null;
            }
            AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());

            if (persistentAppUser == null) {
                AppUser transientAppUser = AppUser.builder()
                        .telegramUserId(telegramUser.getId())
                        .username(telegramUser.getUserName())
                        .firstName(telegramUser.getFirstName())
                        .lastName(telegramUser.getLastName())
                        .isActive(false)
                        .state(BASIC_STATE)
                        .build();

                log.info("New user created: " + transientAppUser);
                return appUserDAO.save(transientAppUser);
            }
            log.info("User found: " + persistentAppUser);
            return persistentAppUser;
        } catch (Exception e) {
            log.error("Error while find or save user: " , e);
            e.printStackTrace();
            return null;
        }
    }

    private void saveRawData(Update update) {
        rawDataService.save(update);
    }
}