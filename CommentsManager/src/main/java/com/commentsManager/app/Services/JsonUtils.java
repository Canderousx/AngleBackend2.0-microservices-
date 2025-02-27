package com.commentsManager.app.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error during to JSON conversion: "+e.getMessage());
        }
    }
    public static <T> T readJson(String json, Class<T> clazz){
        try {
            return objectMapper.readValue(json,clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error during JSON to object conversion "+e.getMessage());
        }
    }

    public static <T> T readJson(String json, TypeReference<T> reference){
        try {
            return objectMapper.readValue(json,reference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error during JSON to object conversion "+e.getMessage());
        }
    }
}
