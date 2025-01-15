package org.herostory.rocketmq;

import lombok.Data;

import java.io.Serializable;

/**
 * 排行榜消息
 */
@Data
public class RankMessage implements Serializable {
    // 胜者id
    private Integer winnerId;
    // 输者id
    private Integer loserId;

    public RankMessage() {
    }

    public RankMessage(Integer winnerId, Integer loserId) {
        this.winnerId = winnerId;
        this.loserId = loserId;
    }
}
