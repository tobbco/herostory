package org.herostory.handler.cmd;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.herostory.BroadCaster;
import org.herostory.constants.HeroConstant;
import org.herostory.protobuf.bean.GameMessageProto;

/**
 * @description:
 * @author：yexianchao
 * @date: 2025/1/8/008
 */
public class HeroMoveCmdHandler implements ICmdHandler<GameMessageProto.UserMoveCmd>{
    @Override
    public  void handle(ChannelHandlerContext channelHandlerContext,GameMessageProto.UserMoveCmd cmd) {
        //英雄移动请求
        AttributeKey<Integer> attributeKey = AttributeKey.valueOf(HeroConstant.HERO_ID_KEY);
        Integer userId = channelHandlerContext.channel().attr(attributeKey).get();
        if (null == userId) {
            return;
        }
        GameMessageProto.UserMoveResult.Builder builder = GameMessageProto.UserMoveResult.newBuilder();
        builder.setMoveUserId(userId);
        builder.setMoveToPosX(cmd.getMoveToPosX());
        builder.setMoveToPosY(cmd.getMoveToPosY());
        GameMessageProto.UserMoveResult result = builder.build();
        //将该英雄移动结果广播到所有客户端
        BroadCaster.broadcast(result);
    }
}
