package ru.hamming.service;

public interface MessageSender {
    void sendAnswer(String text, Long chatId);
}