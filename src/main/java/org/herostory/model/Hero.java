package org.herostory.model;


import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.herostory.HeroDeadException;

/**
 * 英雄
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Hero {
    public Hero(Integer userId, String heroAvatar, Channel channel) {
        this.userId = userId;
        this.heroAvatar = heroAvatar;
        this.channel = channel;
    }

    private Channel channel;

    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 英雄形象
     */
    private String heroAvatar;
    /**
     * 血量:默认100hp
     */
    private Integer hp = 100;

    public synchronized void subHp(int val) {
        if (isDead()) {
            throw new HeroDeadException("英雄：" + this.userId + "已死亡");
        }
        this.hp -= val;
    }
    public synchronized boolean isDead() {
        return this.hp <= 0;
    }
}
