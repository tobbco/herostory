package org.herostory.handler;

import com.google.protobuf.GeneratedMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.herostory.BroadCaster;
import org.herostory.constants.HeroConstant;
import org.herostory.handler.cmd.CmdHandlerFactory;
import org.herostory.handler.cmd.ICmdHandler;
import org.herostory.model.HeroStore;
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
        HeroStore.removeHero(userId);
        GameMessageProto.UserDisconnectResult.Builder builder = GameMessageProto.UserDisconnectResult.newBuilder();
        builder.setQuitUserId(userId);
        GameMessageProto.UserDisconnectResult result = builder.build();
        BroadCaster.broadcast(result);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object message) {
        Class<?> messageClazz = message.getClass();
        LOGGER.info("接收到的消息,messageClazz: {},message: {}", messageClazz.getSimpleName(), message);
        ICmdHandler<? extends GeneratedMessage> cmdHandler = CmdHandlerFactory.getCmdHandler(messageClazz);
        if (null == cmdHandler || !(message instanceof GeneratedMessage)) {
            LOGGER.error("未找到命令处理器,messageClazz: {},handle: {}, message: {}", messageClazz.getSimpleName(), cmdHandler, message);
            return;
        }
        cmdHandler.handle(channelHandlerContext, cast(message));
    }

    /**
     * 类型转换
     * SuppressWarnings("unchecked"):否则会警告: [unchecked] 未经检查的转换
     * @param <T> 转换的类型
     * @param o   对象
     * @return 转换类型后的对象
     */
    @SuppressWarnings("unchecked")
    private static <T extends GeneratedMessage> T cast(Object o) {
        if (null == o) {
            return null;
        }
        return (T) o;
    }
}
