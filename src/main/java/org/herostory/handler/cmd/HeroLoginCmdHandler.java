package org.herostory.handler.cmd;


import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.herostory.BroadCaster;
import org.herostory.constants.HeroConstant;
import org.herostory.model.Hero;
import org.herostory.model.HeroStore;
import org.herostory.protobuf.bean.GameMessageProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 英雄登录命令处理器
 */
public class HeroLoginCmdHandler implements ICmdHandler<GameMessageProto.HeroLoginCmd>{
    private static final Logger logger = LoggerFactory.getLogger(HeroLoginCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext channelHandlerContext, GameMessageProto.HeroLoginCmd cmd) {
        if (null == cmd) {
            return;
        }
        logger.info("Hero login cmd: {}", cmd);
        String userName = cmd.getUserName();
        String password = cmd.getPassword();

        Hero hero = Hero.login(userName, password);
        if (null == hero) {
            logger.error("Hero login fail");
            return;
        }
        channelHandlerContext.channel().attr(AttributeKey.valueOf(HeroConstant.HERO_ID_KEY)).set(hero.getUserId());
        GameMessageProto.HeroLoginResult.Builder builder = GameMessageProto.HeroLoginResult.newBuilder();
        builder.setUserId(hero.getUserId());
        builder.setHeroAvatar(hero.getHeroAvatar());
        //将登录结果封装成到全局登录用户中
        HeroStore.addHero(hero.getUserId(), hero.getHeroAvatar(),channelHandlerContext.channel());

        GameMessageProto.HeroLoginResult result = builder.build();
        //广播登录结果到所有客户端,但是有个问题，只会将当前登录的用户广播到已经登录的用户，但是当前用户不会显示已经登录的其他用户
        BroadCaster.broadcast(result);
    }
}
