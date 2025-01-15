package org.herostory.rocketmq;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.herostory.rank.RankService;
import org.herostory.util.ConfigUtil;
import org.herostory.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

public class MqConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MqConsumer.class);

    private MqConsumer() {
    }

    public static void start() throws IOException, ClientException {
        RocketMQConfig config = ConfigUtil.loadConfig("rocketmq.properties", RocketMQConfig.class);
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(config.getEndpoint())
                .build();

        String tag = "*";
        FilterExpression filterExpression = new FilterExpression(tag, FilterExpressionType.TAG);

        provider.newPushConsumerBuilder()
                .setClientConfiguration(clientConfiguration)
                // 设置消费者分组。
                .setConsumerGroup(config.getConsumerGroup())
                // 设置预绑定的订阅关系。
                .setSubscriptionExpressions(Collections.singletonMap(config.getTopic(), filterExpression))
                // 设置消费监听器。
                .setMessageListener(messageView -> {
                    // 处理消息并返回消费结果。
                    String body = StandardCharsets.UTF_8.decode(messageView.getBody()).toString();
                    logger.info("消息ID-{},消费时间:{},消息内容为：{}", messageView.getMessageId(), LocalDateTime.now(), body);
                    RankMessage rankMessage = JsonUtil.toBean(body, RankMessage.class);
                    RankService.getInstance().refreshRankList(rankMessage);
                    return ConsumeResult.SUCCESS;
                })
                .build();

        try {
            //将线程挂起，让消费者一直处于监听状态。
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
