package org.herostory.handler.cmd;


import com.mongodb.client.model.Filters;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.herostory.BroadCaster;
import org.herostory.constants.HeroConstant;
import org.herostory.db.mongo.MongoDBUtils;
import org.herostory.model.Hero;
import org.herostory.model.HeroStore;
import org.herostory.protobuf.bean.GameMessageProto;

import java.util.List;

/**
 * 英雄登录命令处理器
 */
public class HeroEntryCmdHandler implements ICmdHandler<GameMessageProto.HeroEntryCmd>{

    @Override
    public void handle(ChannelHandlerContext channelHandlerContext, GameMessageProto.HeroEntryCmd cmd) {
        if (null == cmd) {
            return;
        }
        AttributeKey<Integer> attributeKey = AttributeKey.valueOf(HeroConstant.HERO_ID_KEY);
        Integer userId = channelHandlerContext.channel().attr(attributeKey).get();
        List<Hero> heroList =
                MongoDBUtils.findDocuments("hero", Filters.eq("userId", userId), Hero.class);
        if (heroList.isEmpty()) {
            return;
        }
        Hero hero = heroList.get(0);
        String heroAvatar = hero.getHeroAvatar();
        GameMessageProto.HeroEntryResult.Builder builder = GameMessageProto.HeroEntryResult.newBuilder();
        builder.setUserId(userId);
        builder.setHeroAvatar(heroAvatar);
        GameMessageProto.HeroEntryResult result = builder.build();
        //广播登录结果到所有客户端,但是有个问题，只会将当前登录的用户广播到已经登录的用户，但是当前用户不会显示已经登录的其他用户
        BroadCaster.broadcast(result);
    }
}
