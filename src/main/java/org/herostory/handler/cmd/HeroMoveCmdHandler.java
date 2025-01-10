package org.herostory.handler.cmd;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.herostory.BroadCaster;
import org.herostory.constants.HeroConstant;
import org.herostory.model.Hero;
import org.herostory.model.HeroStore;
import org.herostory.protobuf.bean.GameMessageProto;

/**
 * @description:
 * @author：yexianchao
 * @date: 2025/1/8/008
 */
public class HeroMoveCmdHandler implements ICmdHandler<GameMessageProto.HeroMoveCmd>{
    @Override
    public  void handle(ChannelHandlerContext channelHandlerContext,GameMessageProto.HeroMoveCmd cmd) {
        //英雄移动请求
        AttributeKey<Integer> attributeKey = AttributeKey.valueOf(HeroConstant.HERO_ID_KEY);
        Integer userId = channelHandlerContext.channel().attr(attributeKey).get();
        if (null == userId) {
            return;
        }
        Hero hero = HeroStore.getHero(userId);
        if (null == hero) {
            return;
        }
        //英雄移动
        long startTime = System.currentTimeMillis();
        hero.move(cmd.getMoveFromPosX(), cmd.getMoveFromPosY(), cmd.getMoveToPosX(), cmd.getMoveToPosY(), startTime);
        GameMessageProto.HeroMoveResult.Builder builder = GameMessageProto.HeroMoveResult.newBuilder();
        builder.setMoveUserId(userId);
        builder.setMoveFromPosX(cmd.getMoveFromPosX());
        builder.setMoveFromPosY(cmd.getMoveFromPosY());
        builder.setMoveToPosX(cmd.getMoveToPosX());
        builder.setMoveToPosY(cmd.getMoveToPosY());
        builder.setMoveStartTime(startTime);
        GameMessageProto.HeroMoveResult result = builder.build();
        //将该英雄移动结果广播到所有客户端
        BroadCaster.broadcast(result);
    }
}
