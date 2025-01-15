package org.herostory.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Converts a Java object to a JSON string.
     *
     * @param object The object to convert.
     * @return The JSON string representation of the object.
     */
    public static String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Failed to json string", e);
            throw new RuntimeException("Failed to json string", e);
        }
    }

    /**
     * Converts a JSON string to a Java object.
     *
     * @param jsonString The JSON string to convert.
     * @param clazz      The class of the object to create.
     * @param <T>        The type of the object.
     * @return The Java object representation of the JSON string.
     */
    public static <T> T toBean(String jsonString, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            logger.error("Failed to deserialize JSON: {}", jsonString, e);
            throw new RuntimeException("Failed to deserialize JSON: " + jsonString, e);
        }
    }

    /**
     * Converts one Java object to another Java object of a different type.
     *
     * @param source The source object.
     * @param targetClass The class of the target object.
     * @param <T> The type of the target object.
     * @return The converted target object.
     */
    public static <T> T toBean(Object source, Class<T> targetClass) {
        try {
            String jsonString = toJsonString(source);
            return toBean(jsonString, targetClass);
        } catch (Exception e) {
            logger.error("Failed to convert object", e);
            throw new RuntimeException("Failed to convert object", e);
        }
    }
}