package org.herostory.handler;

import com.google.protobuf.GeneratedMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.herostory.CmdRecognizer;
import org.slf4j.Logger;

/**
 * 默认消息编码器
 */
public class DefaultMessageEncoder extends ChannelOutboundHandlerAdapter {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(DefaultMessageEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        logger.info("DefaultMessageEncoder.write msg = {}", msg);
        if (null == ctx || !(msg instanceof GeneratedMessage)) {
            super.write(ctx, msg, promise);
            return;
        }
        //根据消息获取消息指令
        int cmdId = CmdRecognizer.getCmdIdByClass(msg);
        if (cmdId < 0) {
            logger.error("未能解析的消息类型,message class: {}", msg.getClass().getName());
            return;
        }
        byte[] byteArray = ((GeneratedMessage) msg).toByteArray();
        //申请一个ByteBuf
        ByteBuf content = ctx.alloc().buffer();
        //写出消息头，前2字节为消息长度，默认设置为0
        content.writeShort(0);
        //写出消息头，后2字节为消息类型
        content.writeShort(cmdId);
        //写出内容
        content.writeBytes(byteArray);
        super.write(ctx, new BinaryWebSocketFrame(content), promise);
    }
}
