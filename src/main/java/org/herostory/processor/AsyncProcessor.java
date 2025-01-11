package org.herostory.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步操作处理
 */
public final class AsyncProcessor {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AsyncProcessor.class);
    /**
     * 固定线程池 8个线程
     * 使用Executors.newFixedThreadPool解决单线程下多个io阻塞问题，但是这样会存在另外一个问题，以登录为例：点击两次登录，假设同时进来，会在两个线程中执行，
     * 两个线程会创建两次用户（极端情况），除非在创建用户之前加锁，但是锁的时间不确定。
     */
    private static final ExecutorService executorService = Executors.newFixedThreadPool(8);

    /**
     * 使用静态内部类实现单例模式
     */
    private static class Holder {
        private static final AsyncProcessor INSTANCE = new AsyncProcessor();
    }

    private AsyncProcessor() {

    }

    /**
     * 获取
     *
     * @return 实例
     */
    public static AsyncProcessor getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 处理
     */
    public void process(IAsyncOperation operation) {
        if (null == operation) {
            return;
        }
        //提交任务
        executorService.submit(() -> {
            try {
                //异步线程执行
                operation.async();
                //回到主线程执行
                MainProcessor.getInstance().process(operation::callback);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
    }
}
