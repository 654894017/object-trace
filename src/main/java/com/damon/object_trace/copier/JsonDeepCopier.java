package com.damon.object_trace.copier;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Use Json to clone(deep copy) object.
 */
public class JsonDeepCopier implements DeepCopier {
    private ObjectMapper objectMapper;

    public JsonDeepCopier() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper = objectMapper;
    }


    @Override
    public <T> T copy(T object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            return objectMapper.readValue(json, (Class<T>) (object.getClass()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
