package ru.hamming.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.jvnet.hk2.annotations.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hamming.service.UpdateProducer;

@Service
@Component
@Log4j
public class UpdateProducerImpl implements UpdateProducer {

    private final RabbitTemplate rabbitTemplate;

    private static final Logger log = Logger.getLogger(UpdateProducer.class);

    public UpdateProducerImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void produce(String rabbitQueue, Update update) {
        log.debug(update.getMessage().getText());
        rabbitTemplate.convertAndSend(rabbitQueue, update);
    }
}
