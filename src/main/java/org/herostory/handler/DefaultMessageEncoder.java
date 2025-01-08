package org.herostory.handler;

import com.google.protobuf.GeneratedMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.herostory.protobuf.bean.GameMessageProto;
import org.slf4j.Logger;

/**
 * 默认消息编码器
 */
public class DefaultMessageEncoder extends ChannelOutboundHandlerAdapter {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DefaultMessageEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        LOGGER.info("DefaultMessageEncoder.write msg = {}", msg);
        if (null == ctx || !(msg instanceof GeneratedMessage)) {
            super.write(ctx, msg, promise);
            return;
        }
        int cmdId = -1;
        //如果是登录响应
        if (msg instanceof GameMessageProto.UserLoginResult) {
            cmdId = GameMessageProto.GameMsgId.USER_LOGIN_RESULT_VALUE;
        } else if (msg instanceof GameMessageProto.OnlineUserResult) {
            cmdId = GameMessageProto.GameMsgId.ONLINE_USER_RESULT_VALUE;
        } else if (msg instanceof GameMessageProto.UserMoveResult) {
            cmdId = GameMessageProto.GameMsgId.USER_MOVE_RESULT_VALUE;
        } else if (msg instanceof GameMessageProto.UserDisconnectResult) {
            cmdId = GameMessageProto.GameMsgId.USER_DISCONNECT_RESULT_VALUE;
        }
        byte[] byteArray = ((GeneratedMessage)msg).toByteArray();
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
