package ru.hamming.service.impl;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hamming.dao.RawDataDAO;
import ru.hamming.entity.RawData;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
public class MainServiceImplTest {

    @Autowired
    private RawDataDAO rawDataDAO;

    @Test
    public void testSaveRawData() {
        Update update = new Update();
        Message message = new Message();

        message.setText("ololo");
        update.setMessage(message);

        RawData rawData = RawData.builder()
                .event(update)
                .build();

        Set<RawData> testData = new HashSet<>();

        testData.add(rawData);
        rawDataDAO.save(rawData);

        Assert.isTrue(testData.contains(rawData), "Entity not found in the set");
    }

}
