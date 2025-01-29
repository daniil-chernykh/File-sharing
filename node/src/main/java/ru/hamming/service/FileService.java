package ru.hamming.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hamming.entity.AppDocument;
import ru.hamming.entity.AppPhoto;
import ru.hamming.service.enums.LinkType;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
    String generateLink(Long docId, LinkType linkType);
}
