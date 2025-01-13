package org.herostory.handler.cmd;

import io.netty.channel.ChannelHandlerContext;
import org.herostory.model.Hero;
import org.herostory.model.HeroCache;
import org.herostory.protobuf.bean.GameMessageProto;
import org.slf4j.Logger;

import java.util.Collection;

/**
 * 获取已经登录的用户
 */
public class HeroOnlineCmdHandler implements ICmdHandler<GameMessageProto.OnlineHeroCmd> {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(HeroOnlineCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, GameMessageProto.OnlineHeroCmd cmd) {
        //在线用户请求
        GameMessageProto.OnlineHeroResult.Builder builder = GameMessageProto.OnlineHeroResult.newBuilder();
        Collection<Hero> heroes = HeroCache.heroes();
        logger.info("当前在线的英雄列表:{}", heroes);
        for (Hero hero : heroes) {
            if (null == hero) {
                continue;
            }
            GameMessageProto.OnlineHeroResult.HeroInfo userInfo = GameMessageProto.OnlineHeroResult.HeroInfo.newBuilder()
                    .setUserId(hero.getUserId())
                    .setHeroAvatar(hero.getHeroAvatar())
                    .setUserName(hero.getUsername())
                    .setMoveState(GameMessageProto.OnlineHeroResult.HeroInfo.MoveState.newBuilder()
                            .setFromPosX(hero.getMoveState().getFromPosX())
                            .setFromPosY(hero.getMoveState().getFromPosY())
                            .setToPosX(hero.getMoveState().getToPosX())
                            .setToPosY(hero.getMoveState().getToPosY())
                            .setStartTime(hero.getMoveState().getStartTime())
                            .build())
                    .build();
            builder.addUserInfo(userInfo);
        }
        GameMessageProto.OnlineHeroResult result = builder.build();
        ctx.writeAndFlush(result);
    }
}
