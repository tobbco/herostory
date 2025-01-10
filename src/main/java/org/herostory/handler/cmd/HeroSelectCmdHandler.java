package org.herostory.handler.cmd;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.bson.conversions.Bson;
import org.herostory.BroadCaster;
import org.herostory.constants.HeroConstant;
import org.herostory.db.mongo.MongoDBUtils;
import org.herostory.model.Hero;
import org.herostory.model.HeroStore;
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
        logger.info("Hero select cmd: {}", cmd);
        AttributeKey<Integer> attributeKey = AttributeKey.valueOf(HeroConstant.HERO_ID_KEY);
        Integer userId = channelHandlerContext.channel().attr(attributeKey).get();
        if (null == userId) {
            return;
        }
        Hero hero = HeroStore.getHero(userId);
        if (null == hero) {
            return;
        }
        String heroAvatar = cmd.getHeroAvatar();
        hero.setHeroAvatar(heroAvatar);
        Bson update = Updates.set("heroAvatar", heroAvatar);
        MongoDBUtils.updateDocument("hero", Filters.eq("userId", userId), update, Hero.class);
        GameMessageProto.SelectHeroResult result = GameMessageProto.SelectHeroResult.newBuilder()
                .setHeroAvatar(hero.getHeroAvatar())
                .build();
        BroadCaster.broadcast(result);
    }
}
