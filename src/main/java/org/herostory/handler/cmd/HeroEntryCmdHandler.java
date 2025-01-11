package org.herostory.handler.cmd;


import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.herostory.BroadCaster;
import org.herostory.constants.HeroConstant;
import org.herostory.model.Hero;
import org.herostory.model.HeroCache;
import org.herostory.protobuf.bean.GameMessageProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 英雄登录命令处理器
 */
public class HeroEntryCmdHandler implements ICmdHandler<GameMessageProto.HeroEntryCmd> {
    private final static Logger logger = LoggerFactory.getLogger(HeroEntryCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext channelHandlerContext, GameMessageProto.HeroEntryCmd cmd) {
        if (null == cmd) {
            return;
        }
        AttributeKey<Integer> attributeKey = AttributeKey.valueOf(HeroConstant.HERO_ID_KEY);
        Integer userId = channelHandlerContext.channel().attr(attributeKey).get();
        Hero hero = HeroCache.getHero(userId);
        if (null == hero) {
            logger.warn("英雄入口警告::当前用户没有缓存信息,userId={}",userId);
            return;
        }
        GameMessageProto.HeroEntryResult.Builder builder = GameMessageProto.HeroEntryResult.newBuilder();
        builder.setUserId(userId);
        builder.setHeroAvatar(hero.getHeroAvatar());
        GameMessageProto.HeroEntryResult result = builder.build();
        //广播登录结果到所有客户端,但是有个问题，只会将当前登录的用户广播到已经登录的用户，但是当前用户不会显示已经登录的其他用户
        BroadCaster.broadcast(result);
    }
}
