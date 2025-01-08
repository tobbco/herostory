package org.herostory.handler.cmd;

import io.netty.channel.ChannelHandlerContext;
import org.herostory.model.Hero;
import org.herostory.model.HeroStore;
import org.herostory.protobuf.bean.GameMessageProto;

/**
 * @description:
 * @author：yexianchao
 * @date: 2025/1/8/008
 */
public class HeroOnlineCmdHandler implements ICmdHandler<GameMessageProto.OnlineUserCmd>{
    @Override
    public  void handle(ChannelHandlerContext channelHandlerContext,GameMessageProto.OnlineUserCmd cmd) {
        //在线用户请求
        GameMessageProto.OnlineUserResult.Builder builder = GameMessageProto.OnlineUserResult.newBuilder();
        for (Hero hero : HeroStore.heroes()) {
            if (null == hero) {
                continue;
            }
            GameMessageProto.OnlineUserResult.UserInfo userInfo = GameMessageProto.OnlineUserResult.UserInfo.newBuilder()
                    .setUserId(hero.getUserId())
                    .setHeroAvatar(hero.getHeroAvatar())
                    .build();
            builder.addUserInfo(userInfo);
        }
        GameMessageProto.OnlineUserResult result = builder.build();
        channelHandlerContext.writeAndFlush(result);
    }
}
