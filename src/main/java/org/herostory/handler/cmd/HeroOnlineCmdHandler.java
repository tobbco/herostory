package org.herostory.handler.cmd;

import io.netty.channel.ChannelHandlerContext;
import org.herostory.model.Hero;
import org.herostory.model.HeroStore;
import org.herostory.protobuf.bean.GameMessageProto;

/**
 * 获取已经登录的用户
 */
public class HeroOnlineCmdHandler implements ICmdHandler<GameMessageProto.OnlineUserCmd> {
    @Override
    public void handle(ChannelHandlerContext channelHandlerContext, GameMessageProto.OnlineUserCmd cmd) {
        //在线用户请求
        GameMessageProto.OnlineUserResult.Builder builder = GameMessageProto.OnlineUserResult.newBuilder();
        for (Hero hero : HeroStore.heroes()) {
            if (null == hero) {
                continue;
            }
            GameMessageProto.OnlineUserResult.UserInfo userInfo = GameMessageProto.OnlineUserResult.UserInfo.newBuilder()
                    .setUserId(hero.getUserId())
                    .setHeroAvatar(hero.getHeroAvatar())
                    .setMoveState(GameMessageProto.OnlineUserResult.UserInfo.MoveState.newBuilder()
                            .setFromPosX(hero.getMoveState().getFromPosX())
                            .setFromPosY(hero.getMoveState().getFromPosY())
                            .setToPosX(hero.getMoveState().getToPosX())
                            .setToPosY(hero.getMoveState().getToPosY())
                            .setStartTime(hero.getMoveState().getStartTime())
                            .build())
                    .build();
            builder.addUserInfo(userInfo);
        }
        GameMessageProto.OnlineUserResult result = builder.build();
        channelHandlerContext.writeAndFlush(result);
    }
}
