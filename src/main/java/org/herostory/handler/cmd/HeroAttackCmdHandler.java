package org.herostory.handler.cmd;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.apache.rocketmq.client.apis.ClientException;
import org.herostory.BroadCaster;
import org.herostory.HeroDeadException;
import org.herostory.constants.HeroConstant;
import org.herostory.model.Hero;
import org.herostory.model.HeroCache;
import org.herostory.protobuf.bean.GameMessageProto;
import org.herostory.rocketmq.MqProducer;
import org.herostory.rocketmq.RankMessage;
import org.herostory.util.JsonUtil;
import org.slf4j.Logger;

/**
 * 英雄攻击命令处理器
 */
public class HeroAttackCmdHandler implements ICmdHandler<GameMessageProto.HeroAttackCmd> {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(HeroAttackCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext channelHandlerContext, GameMessageProto.HeroAttackCmd cmd) {
        if (null == cmd) {
            return;
        }
        AttributeKey<Integer> attributeKey = AttributeKey.valueOf(HeroConstant.HERO_ID_KEY);
        //获取当前通道绑定的用户ID
        Integer userId = channelHandlerContext.channel().attr(attributeKey).get();
        if (null == userId) {
            return;
        }
        //构建攻击结果
        GameMessageProto.HeroAttackResult attackResult = GameMessageProto.HeroAttackResult.newBuilder()
                .setAttkUserId(userId)
                .setTargetUserId(cmd.getTargetUserId())
                .build();
        //广播攻击结果到所有客户端 是否有必要？测试移除此行代码是没有问题的。
        BroadCaster.broadcast(attackResult);

        //构建减血结果
        GameMessageProto.HeroSubtractHpResult subtractHpResult = GameMessageProto.HeroSubtractHpResult.newBuilder()
                .setTargetUserId(cmd.getTargetUserId())
                .setSubtractHp(HeroConstant.DEFAULT_SUBTRACT_HP)
                .build();
        Hero hero = HeroCache.getHero(cmd.getTargetUserId());
        try {
            if (null == hero) {
                return;
            }
            hero.subHp(HeroConstant.DEFAULT_SUBTRACT_HP);
            if (hero.isDead()) {
                deadBroadcast(userId, cmd.getTargetUserId());
            }
        } catch (HeroDeadException e) {
            deadBroadcast(userId, cmd.getTargetUserId());
        }
        //广播减血结果到所有客户端
        BroadCaster.broadcast(subtractHpResult);
    }

    /**
     * 英雄死亡广播
     *
     * @param userId       攻击的用户
     * @param targetUserId 死亡的用户ID
     */
    private void deadBroadcast(int userId, Integer targetUserId) {
        //构建英雄死亡结果
        GameMessageProto.HeroDieResult dieResult = GameMessageProto.HeroDieResult.newBuilder()
                .setTargetUserId(targetUserId)
                .build();
        BroadCaster.broadcast(dieResult);
        RankMessage message = new RankMessage(userId, targetUserId);
        try {
            MqProducer.sendMessage(JsonUtil.toJsonString(message));
        } catch (ClientException e) {
            logger.error("发送MQ消息失败,消息内容:{}", message, e);
        }
    }
}
