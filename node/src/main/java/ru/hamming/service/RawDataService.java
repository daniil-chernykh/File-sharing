package ru.hamming.service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hamming.entity.RawData;

public interface RawDataService {
    void save(Update update);
}