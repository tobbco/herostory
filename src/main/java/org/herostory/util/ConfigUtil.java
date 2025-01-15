package org.herostory.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.herostory.rocketmq.RocketMQConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {

    @SuppressWarnings("unchecked")
    public static Properties loadConfig(String configFileName) throws IOException {
        Properties properties = new Properties();
        String fileExtension = configFileName.substring(configFileName.lastIndexOf(".") + 1);

        ClassLoader classLoader = ConfigUtil.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(configFileName)) {
            if (inputStream == null) {
                throw new IOException("Config file not found: " + configFileName);
            }
            if ("properties".equalsIgnoreCase(fileExtension)) {
                properties.load(inputStream);
            } else if ("json".equalsIgnoreCase(fileExtension)) {
                ObjectMapper objectMapper = new ObjectMapper();
                java.util.Map<String, String> jsonMap = objectMapper.readValue(inputStream, java.util.Map.class);
                jsonMap.forEach(properties::setProperty);
            } else {
                throw new IllegalArgumentException("Unsupported config file extension: " + fileExtension);
            }
        }
        return properties;
    }
    public static <T> T loadConfig(String configFileName, Class<T> clazz) throws IOException {
        ClassLoader classLoader = ConfigUtil.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(configFileName)) {
            if (inputStream == null) {
                throw new IOException("Config file not found: " + configFileName);
            }
            String fileExtension = configFileName.substring(configFileName.lastIndexOf(".") + 1);
            if ("properties".equalsIgnoreCase(fileExtension)) {
                Properties properties = new Properties();
                properties.load(inputStream);
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.convertValue(properties, clazz); // 使用 convertValue 方法转换
            } else if ("json".equalsIgnoreCase(fileExtension)) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(inputStream, clazz);
            } else {
                throw new IllegalArgumentException("Unsupported config file extension: " + fileExtension);
            }
        }
    }
    public static void main(String[] args) throws IOException {
        Properties config = loadConfig("rocketmq.properties");
        //Properties config = loadConfig("rocketmq.json") json配置
        System.out.println("nameSrvAddr: " + config.getProperty("nameSrvAddr"));
        System.out.println("producerGroup: " + config.getProperty("producerGroup"));
        System.out.println("consumerGroup: " + config.getProperty("consumerGroup"));
        System.out.println("topic: " + config.getProperty("topic"));

        RocketMQConfig rocketMQConfig = loadConfig("rocketmq.properties", RocketMQConfig.class);
        System.out.println("rocket mq config:"+rocketMQConfig);
    }
}