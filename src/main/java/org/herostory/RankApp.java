package org.herostory;

import org.apache.rocketmq.client.apis.ClientException;
import org.herostory.rocketmq.MqConsumer;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * 排行榜
 */
public class RankApp {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(RankApp.class);
    public static void main(String[] args) throws IOException, ClientException {
        MqConsumer.start();
        logger.info("排行榜启动成功");
    }
}
