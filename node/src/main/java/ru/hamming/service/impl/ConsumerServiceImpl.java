package ru.hamming.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hamming.service.ConsumerService;
import ru.hamming.service.MainService;
import ru.hamming.service.ProducerService;

import static ru.hamming.model.RabbitQueue.*;


@Service
@Component
//@Log4j
public class ConsumerServiceImpl implements ConsumerService {

    private static final Logger log = Logger.getLogger(ConsumerServiceImpl.class);

    private final MainService mainService;
//    private final ProducerService producerService;

    public ConsumerServiceImpl(ProducerService producerService, MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessageUpdates(Update update) {
        log.debug("NODE: Text message is received");
        mainService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumeDocMessageUpdates(Update update) {
        log.debug("NODE: Document message is received");
        mainService.processDocMessage(update);
    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumePhotoMessageUpdates(Update update) {
        log.debug("NODE: Photo message is received");
        mainService.processPhotoMessage(update);
    }
}
