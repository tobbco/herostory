package org.herostory.rocketmq;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.herostory.util.ConfigUtil;
import org.herostory.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MqProducer {

    private static final Logger logger = LoggerFactory.getLogger(MqProducer.class);
    private static volatile Producer producer;
    private static  ClientServiceProvider provider;
    private static RocketMQConfig config;

    private MqProducer() {
        // Private constructor to prevent instantiation
    }
    static {
        try {
            config = ConfigUtil.loadConfig("rocketmq.properties",RocketMQConfig.class);
            provider = ClientServiceProvider.loadService();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }

    public static Producer getInstance() throws ClientException {
        if (producer == null) {
            synchronized (MqProducer.class) {
                if (producer == null) {
                    ClientConfiguration configuration = ClientConfiguration.newBuilder()
                            .setEndpoints(config.getEndpoint())
                            .build();
                    producer = provider.newProducerBuilder()
                            .setTopics(config.getTopic())
                            .setClientConfiguration(configuration)
                            .build();
                }
            }
        }
        return producer;
    }

    public static void sendMessage(String messageBody) throws ClientException {
        Producer producer = getInstance();
        Message message = provider.newMessageBuilder()
                .setTopic(config.getTopic())
                .setBody(messageBody.getBytes())
                .build();

        try {
            SendReceipt sendReceipt = producer.send(message);
            logger.info("Send message successfully, messageId={}", sendReceipt.getMessageId());
        } catch (ClientException e) {
            logger.error("Failed to send message", e);
        }
    }

    public static void close() {
        if (producer != null) {
            try {
                producer.close();
            } catch (IOException e) {
                logger.error("Failed to close producer", e);
            }
        }
    }

    public static void main(String[] args) throws ClientException {
        RankMessage message = new RankMessage(1,2);
        sendMessage(JsonUtil.toJsonString(message));
        close(); // Important: Close the producer when finished
    }
}