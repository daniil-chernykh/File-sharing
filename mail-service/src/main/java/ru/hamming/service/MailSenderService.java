package ru.hamming.service;

import ru.hamming.dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}
