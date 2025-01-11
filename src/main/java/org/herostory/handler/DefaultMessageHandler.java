package org.herostory.handler;

import com.google.protobuf.GeneratedMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.herostory.BroadCaster;
import org.herostory.constants.HeroConstant;
import org.herostory.model.HeroCache;
import org.herostory.processor.MainProcessor;
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
        //断开连接时删除缓存的英雄信息
        HeroCache.removeHero(userId);
        GameMessageProto.HeroDisconnectResult.Builder builder = GameMessageProto.HeroDisconnectResult.newBuilder();
        builder.setQuitUserId(userId);
        GameMessageProto.HeroDisconnectResult result = builder.build();
        BroadCaster.broadcast(result);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object message) {
        if (message instanceof GeneratedMessage generatedMessage) {
            MainProcessor.getInstance().process(channelHandlerContext, generatedMessage);
        }
    }


}
