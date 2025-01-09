package org.herostory.handler.cmd;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.herostory.BroadCaster;
import org.herostory.HeroDeadException;
import org.herostory.constants.HeroConstant;
import org.herostory.model.Hero;
import org.herostory.model.HeroStore;
import org.herostory.protobuf.bean.GameMessageProto;

/**
 * 英雄攻击命令处理器
 */
public class HeroAttackCmdHandler implements ICmdHandler<GameMessageProto.UserAttackCmd> {
    @Override
    public void handle(ChannelHandlerContext channelHandlerContext, GameMessageProto.UserAttackCmd cmd) {
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
        GameMessageProto.UserAttackResult attackResult = GameMessageProto.UserAttackResult.newBuilder()
                .setAttkUserId(userId)
                .setTargetUserId(cmd.getTargetUserId())
                .build();
        //广播攻击结果到所有客户端
        BroadCaster.broadcast(attackResult);

        //构建减血结果
        GameMessageProto.UserSubtractHpResult subtractHpResult = GameMessageProto.UserSubtractHpResult.newBuilder()
                .setTargetUserId(cmd.getTargetUserId())
                .setSubtractHp(HeroConstant.DEFAULT_SUBTRACT_HP)
                .build();
        Hero hero = HeroStore.getHero(cmd.getTargetUserId());
        try {
            hero.subHp(HeroConstant.DEFAULT_SUBTRACT_HP);
            if (hero.isDead()) {
                deadBroadcast(cmd.getTargetUserId());
            }
        } catch (HeroDeadException e) {
            deadBroadcast(cmd.getTargetUserId());
        }
        //广播减血结果到所有客户端
        BroadCaster.broadcast(subtractHpResult);
    }

    /**
     * 英雄死亡广播
     *
     * @param targetUserId 死亡的用户ID
     */
    private void deadBroadcast(Integer targetUserId) {
        //构建英雄死亡结果
        GameMessageProto.UserDieResult dieResult = GameMessageProto.UserDieResult.newBuilder()
                .setTargetUserId(targetUserId)
                .build();
        BroadCaster.broadcast(dieResult);
    }
}
