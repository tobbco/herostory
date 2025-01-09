package org.herostory.handler.cmd;


import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.herostory.BroadCaster;
import org.herostory.constants.HeroConstant;
import org.herostory.model.HeroStore;
import org.herostory.protobuf.bean.GameMessageProto;

/**
 * 英雄登录命令处理器
 */
public class HeroLoginCmdHandler implements ICmdHandler<GameMessageProto.UserLoginCmd>{

    @Override
    public void handle(ChannelHandlerContext channelHandlerContext, GameMessageProto.UserLoginCmd cmd) {
        //如果是登录请求
        int userId = cmd.getUserId();
        String heroAvatar = cmd.getHeroAvatar();
        GameMessageProto.UserLoginResult.Builder builder = GameMessageProto.UserLoginResult.newBuilder();
        builder.setUserId(userId);
        builder.setHeroAvatar(heroAvatar);
        //将登录结果封装成到全局登录用户中
        HeroStore.addHero(userId, heroAvatar,channelHandlerContext.channel());
        //将登录的用户id设置到通道中
        channelHandlerContext.channel().attr(AttributeKey.valueOf(HeroConstant.HERO_ID_KEY)).set(userId);
        GameMessageProto.UserLoginResult result = builder.build();
        //广播登录结果到所有客户端,但是有个问题，只会将当前登录的用户广播到已经登录的用户，但是当前用户不会显示已经登录的其他用户
        BroadCaster.broadcast(result);
    }
}
