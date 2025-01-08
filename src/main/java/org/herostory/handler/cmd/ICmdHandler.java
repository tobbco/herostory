package org.herostory.handler.cmd;

import com.google.protobuf.GeneratedMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * CMD指定处理器
 */
public interface ICmdHandler<T extends GeneratedMessage> {

    /**
     * 处理
     * @param channelHandlerContext 通信上下文
     * @param cmd 指令
     */
    public void handle(ChannelHandlerContext channelHandlerContext, T cmd);
}
