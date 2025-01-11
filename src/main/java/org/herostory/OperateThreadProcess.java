package org.herostory;

import com.google.protobuf.GeneratedMessage;
import io.netty.channel.ChannelHandlerContext;
import org.herostory.handler.cmd.CmdHandlerFactory;
import org.herostory.handler.cmd.ICmdHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 操作线程处理
 */
public final class OperateThreadProcess {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OperateThreadProcess.class);
    /**
     * 单线程池
     */
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor((r) -> new Thread(r, "OperateThreadProcess"));

    /**
     * 使用静态内部类实现单例模式
     */
    private static class Holder {
        private static final OperateThreadProcess INSTANCE = new OperateThreadProcess();
    }

    private OperateThreadProcess() {

    }

    /**
     * 获取
     *
     * @return 实例
     */
    public static OperateThreadProcess getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 处理
     *

     */
    public void process(Runnable runnable) {
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
