package org.herostory.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;

/**
 * 默认消息处理器
 */
public class DefaultMessageHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DefaultMessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) {
        LOGGER.info("the client message was received procedure: {}", o);
    }
}
