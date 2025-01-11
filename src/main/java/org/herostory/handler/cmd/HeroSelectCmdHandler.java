package org.herostory.handler.cmd;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.herostory.BroadCaster;
import org.herostory.constants.HeroConstant;
import org.herostory.db.mongo.HeroRepository;
import org.herostory.model.Hero;
import org.herostory.model.HeroCache;
import org.herostory.protobuf.bean.GameMessageProto;

/**
 * 选择英雄指令处理器
 */
public class HeroSelectCmdHandler implements ICmdHandler<GameMessageProto.SelectHeroCmd> {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(HeroSelectCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext channelHandlerContext, GameMessageProto.SelectHeroCmd cmd) {
        if (null == cmd) {
            return;
        }
        AttributeKey<Integer> attributeKey = AttributeKey.valueOf(HeroConstant.HERO_ID_KEY);
        Integer userId = channelHandlerContext.channel().attr(attributeKey).get();
        if (null == userId) {
            logger.warn("英雄选择警告::当前用户没有缓存信息");
            return;
        }
        Hero hero = HeroCache.getHero(userId);
        if (null == hero) {
            logger.warn("英雄选择警告::当前用户没有缓存信息,userId={}", userId);
            return;
        }
        //更新缓存中的英雄形象
        String heroAvatar = cmd.getHeroAvatar();
        hero.setHeroAvatar(heroAvatar);
        HeroRepository.getInstance().updateHeroAvatar(userId, heroAvatar);
        // 构建返回结果
        GameMessageProto.SelectHeroResult result = GameMessageProto.SelectHeroResult.newBuilder()
                .setHeroAvatar(hero.getHeroAvatar())
                .build();
        BroadCaster.broadcast(result);
    }
}
