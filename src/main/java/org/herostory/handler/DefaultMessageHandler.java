package org.herostory.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.herostory.bean.Hero;
import org.herostory.channel.HeroChannel;
import org.herostory.protobuf.bean.GameMessageProto;
import org.slf4j.Logger;

/**
 * 默认消息处理器
 */
public class DefaultMessageHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DefaultMessageHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        HeroChannel.getChannelGroup().add(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) {
        LOGGER.info("the client message was received procedure: {}", o);

        if (o instanceof GameMessageProto.UserLoginCmd cmd) {
            //如果是登录请求
            int userId = cmd.getUserId();
            String heroAvatar = cmd.getHeroAvatar();
            GameMessageProto.UserLoginResult.Builder builder = GameMessageProto.UserLoginResult.newBuilder();
            builder.setUserId(userId);
            builder.setHeroAvatar(heroAvatar);
            HeroChannel.getChannelHeroMap().put(userId, new Hero(userId, heroAvatar));
            GameMessageProto.UserLoginResult loginResult = builder.build();
            //广播登录结果到所有客户端,但是有个问题，只会将当前登录的用户广播到已经登录的用户，但是当前用户不会显示已经登录的其他用户
            HeroChannel.getChannelGroup().writeAndFlush(loginResult);
        } else if (o instanceof GameMessageProto.OnlineUserCmd) {
            //在线用户请求
            GameMessageProto.OnlineUserResult.Builder builder = GameMessageProto.OnlineUserResult.newBuilder();
            for (Hero hero : HeroChannel.getChannelHeroMap().values()) {
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
}
