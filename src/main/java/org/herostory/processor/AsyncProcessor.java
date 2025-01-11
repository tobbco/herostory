package org.herostory.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步操作处理
 */
public final class AsyncProcessor {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AsyncProcessor.class);
    /**
     * 单线程池
     */
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor((r) -> new Thread(r, "AsyncOperateProcess"));

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
