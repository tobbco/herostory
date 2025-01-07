package org.herostory.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
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
            //将登录结果封装成到全局登录用户中
            HeroChannel.getChannelHeroMap().put(userId, new Hero(userId, heroAvatar));
            //将登录的用户id设置到通道中
            channelHandlerContext.channel().attr(AttributeKey.valueOf(HeroChannel.USER_ID_KEY)).set(userId);
            GameMessageProto.UserLoginResult result = builder.build();
            //广播登录结果到所有客户端,但是有个问题，只会将当前登录的用户广播到已经登录的用户，但是当前用户不会显示已经登录的其他用户
            HeroChannel.getChannelGroup().writeAndFlush(result);
        } else if (o instanceof GameMessageProto.OnlineUserCmd) {
            //在线用户请求
            GameMessageProto.OnlineUserResult.Builder builder = GameMessageProto.OnlineUserResult.newBuilder();
            for (Hero hero : HeroChannel.getChannelHeroMap().values()) {
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
        } else if (o instanceof GameMessageProto.UserMoveCmd cmd) {
            //英雄移动请求
            AttributeKey<Integer> attributeKey = AttributeKey.valueOf(HeroChannel.USER_ID_KEY);
            Integer userId = channelHandlerContext.channel().attr(attributeKey).get();
            if (null == userId) {
                return;
            }

            GameMessageProto.UserMoveResult.Builder builder = GameMessageProto.UserMoveResult.newBuilder();
            builder.setMoveUserId(userId);
            builder.setMoveToPosX(cmd.getMoveToPosX());
            builder.setMoveToPosY(cmd.getMoveToPosY());
            GameMessageProto.UserMoveResult result = builder.build();
            //将该英雄移动结果广播到所有客户端
            HeroChannel.getChannelGroup().writeAndFlush(result);
        }
    }
}
