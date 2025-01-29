package ru.hamming.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

@Converter(autoApply = true)
public class JsonBinaryType implements AttributeConverter<Update, byte[]> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] convertToDatabaseColumn(Update update) {
        try {
            return objectMapper.writeValueAsBytes(update);
        } catch (IOException e) {
            //Log the error using your preferred logging framework
            throw new RuntimeException("Error converting Update to byte[]", e);
        }
    }

    @Override
    public Update convertToEntityAttribute(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return objectMapper.readValue(bytes, Update.class);
        } catch (IOException e) {
            throw new RuntimeException("Error converting byte[] to Update", e);
        }
    }
}