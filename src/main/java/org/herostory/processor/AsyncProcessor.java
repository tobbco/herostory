package org.herostory.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步操作处理
 */
public final class AsyncProcessor {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AsyncProcessor.class);
    /**
     * 线程池集合，默认为CPU核数*2
     */
    private static final ExecutorService[] executors = new ExecutorService[Runtime.getRuntime().availableProcessors() * 2];


    /**
     * 使用静态内部类实现单例模式
     */
    private static class Holder {
        private static final AsyncProcessor INSTANCE = new AsyncProcessor();
    }

    private AsyncProcessor() {
        //创建单例线程池
        for (int i = 0; i < executors.length; i++) {
            String name = "async-processor-" + i;
            executors[i] = Executors.newSingleThreadExecutor((r) -> new Thread(r, name));
        }
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
        int bindId = operation.bindId();
        //获取线程池, 根据绑定id取模,保证同一个用户登录操作能够落到同一个线程池，保证操作并发安全问题
        ExecutorService executorService = executors[bindId % executors.length];
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
