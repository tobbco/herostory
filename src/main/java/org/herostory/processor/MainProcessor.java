package org.herostory.processor;

import com.google.protobuf.GeneratedMessage;
import io.netty.channel.ChannelHandlerContext;
import org.herostory.handler.cmd.CmdHandlerFactory;
import org.herostory.handler.cmd.ICmdHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 主线程处理
 */
public final class MainProcessor {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MainProcessor.class);
    /**
     * 单线程池
     */
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor((r) -> new Thread(r, "MainThreadProcess"));

    /**
     * 使用静态内部类实现单例模式
     */
    private static class Holder {
        private static final MainProcessor INSTANCE = new MainProcessor();
    }

    private MainProcessor() {

    }

    /**
     * 获取
     *
     * @return 实例
     */
    public static MainProcessor getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 处理
     *
     * @param ctx     客户端信道上下文
     * @param message 消息对象
     */
    public void process(ChannelHandlerContext ctx, GeneratedMessage message) {
        if (null == ctx || null == message) {
            return;
        }
        executorService.submit(() -> {
            Class<? extends GeneratedMessage> messageClass = message.getClass();
            logger.info("接收到的客户端消息 {}", messageClass.getName());
            ICmdHandler<? extends GeneratedMessage> cmdHandler = CmdHandlerFactory.getCmdHandler(messageClass);
            if (null == cmdHandler) {
                logger.error("未找到响应的消息指令 {}", messageClass.getName());
                return;
            }
            try {
                cmdHandler.handle(ctx, cast(message));
            } catch (Exception e) {
                logger.error("处理业务异常", e);
            }
        });
    }

    /**
     * 提交任务
     *
     * @param runnable 线程接口
     */
    public void process(Runnable runnable) {
        if (null == runnable) {
            return;
        }
        executorService.submit(runnable);
    }


    /**
     * 类型转换
     * SuppressWarnings("unchecked"):否则会警告: [unchecked] 未经检查的转换
     *
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
