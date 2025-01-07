package org.herostory.channel;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.herostory.bean.Hero;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 英雄信道群组
 */
public class HeroChannel {
    private HeroChannel() {
        // 私有构造函数，防止外部实例化
    }


    public static final String USER_ID_KEY = "userId";

    private static class SingletonHolder {
        private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        private static final ConcurrentHashMap<Integer, Hero> channelHeroMap = new ConcurrentHashMap<>();
    }

    public static ChannelGroup getChannelGroup() {
        return SingletonHolder.channelGroup;
    }

    public static ConcurrentMap<Integer, Hero> getChannelHeroMap() {
        return SingletonHolder.channelHeroMap;
    }
}
