package ru.hamming.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hamming.dao.RawDataDAO;
import ru.hamming.entity.RawData;
import ru.hamming.service.RawDataService;

@Log4j
@Service
public class RawDataServiceImpl implements RawDataService {

    private final RawDataDAO rawDataDAO;

    public RawDataServiceImpl(RawDataDAO rawDataDAO) {
        this.rawDataDAO = rawDataDAO;
    }

    @Override
    public void save(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
