package ru.hamming.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.hamming.dao.AppUserDAO;
import ru.hamming.dto.MailParams;
import ru.hamming.entity.AppUser;
import ru.hamming.entity.enums.UserState;
import ru.hamming.service.AppUserService;
import ru.hamming.utils.CryptoTool;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import static ru.hamming.entity.enums.UserState.BASIC_STATE;

@Log4j
@Service
public class AppUserServiceImpl implements AppUserService {
    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;

//    @Value("${service.mail.uri}")
//    private String mailServiceUri;
//    @Value("${service.mail.uri}")
    @Value("${service.mail.uri}")
    private String mailServiceUri;

    public AppUserServiceImpl(AppUserDAO appUserDAO, CryptoTool cryptoTool) {
        this.appUserDAO = appUserDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public AppUser findOrSave(Update update) {
        try {
            log.info("Start find or save user, Update: " + update);
            User telegramUser = update.getMessage().getFrom();

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
                        .isActive(true)
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

    @Override
    public String setEmail(AppUser appUser, String email) {
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException e) {
//            throw new RuntimeException(e);
            return "Введите пожалуйста, корректный email. Для отмены команды введите /cancel";
        }

        var optional = appUserDAO.findByEmail(email);

        if (optional.isEmpty()) {
            appUser.setEmail(email);
            appUser.setState(BASIC_STATE);
            appUser = appUserDAO.save(appUser);

            var cryptoUserId = cryptoTool.hashOf(appUser.getId());
            var responce = sendRequestToMailService(cryptoUserId, email);

            if (responce.getStatusCode() != HttpStatus.OK) {
                var message = String.format("Отправка эл. письма на почту %s не удалась.", email);
                log.error(message);
                appUser.setEmail(null);
                appUserDAO.save(appUser);
                return message;
            }

            return "Вам на почту было отправлено письмо."
                    + "Перейдите по ссылке в письме для подтверждения регистрации.";
        } else {
          return "Этот email уже используется. Введите корректный email."
                  +"Для отмены команды введите /cancel";
        }
    }

    private ResponseEntity<String> sendRequestToMailService(String cryptoUserId, String email) {
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        var mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();

        var request = new HttpEntity<MailParams>(mailParams, headers);
        return restTemplate.exchange(
                mailServiceUri,
                HttpMethod.POST,
                request,
                String.class
        );
    }

    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.getIsActive()) {
            return "Вы уже зарегистрированы!";
        } else if (appUser.getEmail() != null) {
            return "Вам на почту уже отправлено письмо. "
                    + "Перейдите по ссылке в письме для подтверждения регистрации.";
        }

        appUser.setState(UserState.WAIT_FOR_EMAIL_STATE);
        appUserDAO.save(appUser);
        return "Введите пожалуйста, ваш email:";
    }
}