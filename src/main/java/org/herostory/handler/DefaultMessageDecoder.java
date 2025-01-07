package org.herostory.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.herostory.protobuf.bean.GameMessageProto;
import org.slf4j.Logger;


public class DefaultMessageDecoder extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DefaultMessageDecoder.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == ctx || !(msg instanceof BinaryWebSocketFrame frame)) {
            return;
        }
        try {
            ByteBuf content = frame.content();
            //读取2个字节，获取消息长度
            content.readShort();
            //读取2个字节，获取消息类型
            int cmdId = content.readShort();
            LOGGER.info("cmdId:{}", cmdId);
            //获取可读字节数
            int readable = content.readableBytes();
            //读取消息体
            byte[] bodyBytes = new byte[readable];
            //将内容读取到字节数组中
            content.readBytes(bodyBytes);
            GameMessageProto.UserLoginCmd cmd = null;
            switch (cmdId) {
                case GameMessageProto.GameMsgId.USER_LOGIN_CMD_VALUE:
                    cmd = GameMessageProto.UserLoginCmd.parseFrom(bodyBytes);
                    break;
            }
            if (null != cmd) {
                ctx.fireChannelRead(cmd);
            }
        } catch (Exception e) {
            LOGGER.error("消息解码异常", e);
        }
    }
}
