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
public class HeroLoginCmdHandler implements ICmdHandler<GameMessageProto.HeroLoginCmd> {
    private static final Logger logger = LoggerFactory.getLogger(HeroLoginCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext channelHandlerContext, GameMessageProto.HeroLoginCmd cmd) {
        if (null == cmd) {
            return;
        }
        logger.info("Hero login cmd: {}", cmd);
        String userName = cmd.getUserName();
        String password = cmd.getPassword();

        try {
            Hero.login(userName, password, hero -> {
                logger.info("当前登录线程");
                channelHandlerContext.channel().attr(AttributeKey.valueOf(HeroConstant.HERO_ID_KEY)).set(hero.getUserId());
                GameMessageProto.HeroLoginResult.Builder builder = GameMessageProto.HeroLoginResult.newBuilder();
                builder.setUserId(hero.getUserId());
                //将登录结果封装成到全局登录用户中
                HeroStore.addHero(hero);
                GameMessageProto.HeroLoginResult result = builder.build();
                //将当前用户广播给所有用户
                BroadCaster.broadcast(result);
                return null;
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
