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


    private MoveState moveState = new MoveState();

    /**
     * 英雄移动
     * @param fromPosX  起始位置x坐标
     * @param fromPosY  起始位置y坐标
     * @param toPosX    终点位置x坐标
     * @param toPosY    终点位置y坐标
     * @param startTime 开始移动的时间 时间戳 单位:毫秒 System.currentTimeMillis()
     */
    public  void move(float fromPosX, float fromPosY, float toPosX, float toPosY, long startTime) {
        this.moveState.setFromPosX(fromPosX);
        this.moveState.setFromPosY(fromPosY);
        this.moveState.setToPosX(toPosX);
        this.moveState.setToPosY(toPosY);
        this.moveState.setStartTime(startTime);
    }

    public  void subHp(int val) {
        if (isDead()) {
            throw new HeroDeadException("英雄：" + this.userId + "已死亡");
        }
        this.hp -= val;
    }

    public synchronized boolean isDead() {
        return this.hp <= 0;
    }
}
