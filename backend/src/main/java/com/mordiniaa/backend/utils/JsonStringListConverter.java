package com.mordiniaa.backend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Converter
@Component
public class JsonStringListConverter implements AttributeConverter<List<String>, String> {

    private final ObjectMapper MAPPER;
    private final ObjectReader TYPE;

    public JsonStringListConverter(ObjectMapper objectMapper) {
        this.MAPPER = objectMapper;
        this.TYPE = MAPPER.readerForListOf(String.class);
    }

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try {
            if (attribute == null || attribute.isEmpty())
                return "[]";
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting list to JSON string", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty())
            return Collections.emptyList();

        try {
            return TYPE.readValue(dbData);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON string to list", e);
        }
    }
}
