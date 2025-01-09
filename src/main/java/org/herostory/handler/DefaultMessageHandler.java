package org.herostory.handler;

import com.google.protobuf.GeneratedMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.herostory.BroadCaster;
import org.herostory.MainThreadProcess;
import org.herostory.constants.HeroConstant;
import org.herostory.model.HeroStore;
import org.herostory.protobuf.bean.GameMessageProto;
import org.slf4j.Logger;

/**
 * 默认消息处理器
 */
public class DefaultMessageHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(DefaultMessageHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        BroadCaster.addChannel(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        BroadCaster.removeChannel(ctx.channel());
        AttributeKey<Integer> attributeKey = AttributeKey.valueOf(HeroConstant.HERO_ID_KEY);
        Integer userId = ctx.channel().attr(attributeKey).get();
        if (null == userId) {
            return;
        }
        logger.info("{} 断开连接", userId);
        HeroStore.removeHero(userId);
        GameMessageProto.UserDisconnectResult.Builder builder = GameMessageProto.UserDisconnectResult.newBuilder();
        builder.setQuitUserId(userId);
        GameMessageProto.UserDisconnectResult result = builder.build();
        BroadCaster.broadcast(result);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object message) {
        if (message instanceof GeneratedMessage generatedMessage) {
            MainThreadProcess.getInstance().process(channelHandlerContext, generatedMessage);
        }
    }


}
