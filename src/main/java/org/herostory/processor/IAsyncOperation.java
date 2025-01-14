package org.herostory.processor;

/**
 * 异步操作接口
 */
public interface IAsyncOperation {

    default int bindId() {
        return 0;
    }

    /**
     * 异步操作逻辑
     */
    void async();

    /**
     * 异步之后的操作逻辑
     */
    default void callback() {
    }
}
