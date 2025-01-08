package org.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 广播
 * final :不可被继承
 */
public final class BroadCaster {
    /**
     * 私有构造方法，防止实例化
     */
    private BroadCaster() {
        throw new UnsupportedOperationException();
    }

    /**
     * channelGroup：用于存储所有已连接的 Channel
     * GlobalEventExecutor.INSTANCE：GlobalEventExecutor.INSTANCE 是 Netty 提供的全局事件执行器，确保所有与 ChannelGroup 相关的操作都在同一个线程中执行，避免多线程并发问题
     */
    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    /**
     * 向频道组中添加频道
     * <p>
     * 当建立新的通信频道时，使用此方法将频道添加到频道组中这有助于统一管理所有频道，
     * 便于后续进行操作，如广播消息、关闭所有频道等
     *
     * @param channel 需要添加到频道组的频道对象确保频道有效且未被添加到其他频道组
     */
    public static void addChannel(Channel channel) {
        if (channel == null) {
            return;
        }
        channelGroup.add(channel);
    }

    /**
     * 从通道组中移除指定的通道
     * 此方法用于维护通道组的实时准确性，当某个通道不再需要时，通过调用此方法将其从通道组中移除
     *
     * @param channel 需要从通道组中移除的通道对象
     */
    public static void removeChannel(Channel channel) {
        if (channel == null) {
            return;
        }
        channelGroup.remove(channel);
    }


    /**
     * 将消息广播到所有通道
     * 此方法用于将给定的消息对象发送到通道组中的所有通道，以便每个通道都能接收到该消息
     * 主要用于消息的群发场景，比如聊天室中的广播消息
     *
     * @param msg 要广播的消息对象，可以是任何类型的对象，但通常应该是消息或数据的封装
     */
    public static void broadcast(Object msg) {
        if (msg == null) {
            return;
        }
        channelGroup.writeAndFlush(msg);
    }
}
