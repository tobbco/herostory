package org.herostory.model;


import com.mongodb.Function;
import com.mongodb.client.model.Filters;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.conversions.Bson;
import org.herostory.HeroDeadException;
import org.herostory.UsernamePasswordException;
import org.herostory.db.mongo.HeroRepository;
import org.herostory.db.mongo.MongoDBUtils;
import org.herostory.processor.AsyncProcessor;
import org.herostory.processor.IAsyncOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;
import java.util.List;

/**
 * 英雄
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Hero {
    private static final Logger logger = LoggerFactory.getLogger(Hero.class);
    public Hero(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 英雄形象
     */
    private String heroAvatar;
    /**
     * 血量:默认100hp
     */
    private Integer hp = 100;

    /**
     * 英雄移动属性
     */
    @BsonIgnore
    private MoveState moveState = new MoveState();

    /**
     * 英雄移动
     *
     * @param fromPosX  起始位置x坐标
     * @param fromPosY  起始位置y坐标
     * @param toPosX    终点位置x坐标
     * @param toPosY    终点位置y坐标
     * @param startTime 开始移动的时间 时间戳 单位:毫秒 System.currentTimeMillis()
     */
    public void move(float fromPosX, float fromPosY, float toPosX, float toPosY, long startTime) {
        this.moveState.setFromPosX(fromPosX);
        this.moveState.setFromPosY(fromPosY);
        this.moveState.setToPosX(toPosX);
        this.moveState.setToPosY(toPosY);
        this.moveState.setStartTime(startTime);
    }

    public void subHp(int val) {
        if (isDead()) {
            throw new HeroDeadException("英雄：" + this.userId + "已死亡");
        }
        this.hp -= val;
    }

    public synchronized boolean isDead() {
        return this.hp <= 0;
    }

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @param callback 回调
     */
    public static void login(String username, String password, Function<Hero, Void> callback) {
        IAsyncOperation operation = new IAsyncOperation() {
            private Hero hero;

            @Override
            public int bindId() {
                //计算绑定id，绑定id为用户名最后一个字符的ascii码
                try {
                    return username.charAt(username.length() - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }

            @Override
            public void async() {
                this.hero = HeroRepository.getInstance().findOrCreate(new Hero(username, password));
                if (!this.hero.getPassword().equals(password)) {
                    throw new UsernamePasswordException();
                }
            }

            @Override
            public void callback() {
                callback.apply(hero);
            }
        };
        AsyncProcessor.getInstance().process(operation);

    }
}
