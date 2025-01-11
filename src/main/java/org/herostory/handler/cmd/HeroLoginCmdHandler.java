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
public class HeroLoginCmdHandler implements ICmdHandler<GameMessageProto.HeroLoginCmd> {
    private static final Logger logger = LoggerFactory.getLogger(HeroLoginCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext channelHandlerContext, GameMessageProto.HeroLoginCmd cmd) {
        if (null == cmd) {
            return;
        }
        try {
            Hero.login(cmd.getUserName(), cmd.getPassword(), hero -> {
                //将用户id绑定到channel中
                channelHandlerContext.channel().attr(AttributeKey.valueOf(HeroConstant.HERO_ID_KEY)).set(hero.getUserId());
                //构建登录结果
                GameMessageProto.HeroLoginResult.Builder builder = GameMessageProto.HeroLoginResult.newBuilder();
                builder.setUserId(hero.getUserId());
                //将登录结果封装成到全局登录用户中
                HeroCache.addHero(hero);
                GameMessageProto.HeroLoginResult result = builder.build();
                //广播登录结果
                BroadCaster.broadcast(result);
                return null;
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
