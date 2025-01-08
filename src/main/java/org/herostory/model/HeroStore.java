package org.herostory.model;

import org.herostory.constants.HeroConstant;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 英雄存储类
 * 该类用于存储和管理游戏中的英雄信息。
 * 可能包括英雄的基本属性、技能、装备等数据的封装与操作。
 */
public final class HeroStore {
    /**
     * 存储英雄信息的映射表，键为英雄ID，值为英雄对象。
     */
    private static final ConcurrentHashMap<Integer, Hero> channelHeroMap = new ConcurrentHashMap<>();
    private HeroStore() {
        throw new UnsupportedOperationException();
    }

    /**
     * 添加英雄到存储中。
     *
     * @param heroId     英雄的唯一标识符
     * @param heroAvatar 英雄对象
     */
    public static void addHero(Integer heroId, String heroAvatar) {
        if (heroId == null || heroAvatar == null) {
            return;
        }
        Hero hero = new Hero(heroId, heroAvatar);
        channelHeroMap.put(heroId, hero);
    }

    /**
     * 添加英雄到存储中。
     *
     * @param hero 英雄信息
     */
    public static void addHero(Hero hero) {
        if (hero == null) {
            return;
        }
        channelHeroMap.put(hero.getUserId(), hero);
    }

    /**
     * 获取英雄信息。
     * @param heroId 英雄id
     * @return 英雄信息
     */
    public static Hero getHero(Integer heroId) {
        if (heroId == null) {
            return null;
        }
        return channelHeroMap.get(heroId);
    }

    /**
     * 移除英雄信息。
     * @param heroId 英雄id
     */
    public static void removeHero(Integer heroId) {
        if (heroId == null) {
            return;
        }
        channelHeroMap.remove(heroId);
    }

    /**
     * 移除英雄信息。
     *
     * @param hero 英雄信息
     */
    public static void removeHero(Hero hero) {
        if (hero == null || hero.getUserId() == null) {
            return;
        }
        channelHeroMap.remove(hero.getUserId());
    }

    /**
     * 获取英雄信息集合。
     * @return 英雄信息集合
     */
    public static Collection<Hero> heroes() {
        return channelHeroMap.values();
    }

}
