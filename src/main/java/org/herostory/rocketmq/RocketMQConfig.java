package org.herostory.rocketmq;

import lombok.Data;

@Data
public class RocketMQConfig {
    private String nameSrvAddr;
    private String endpoint;
    private String topic;
    private String producerGroup;
    private String consumerGroup;
}
