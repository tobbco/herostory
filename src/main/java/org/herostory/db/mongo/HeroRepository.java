package org.herostory.db.mongo;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;
import org.herostory.constants.HeroConstant;
import org.herostory.model.Hero;

import java.util.List;
import java.util.Random;

/**
 * 英雄仓库
 */
public class HeroRepository {

    private HeroRepository() {
    }

    private static HeroRepository INSTANCE = new HeroRepository();

    public static HeroRepository getInstance() {
        return INSTANCE;
    }

    /**
     * 根据用户ID获取英雄对象
     *
     * @param userId 用户ID，用于查询特定的英雄对象
     * @return 如果找到匹配的英雄，则返回Hero对象；否则返回null
     * <p>
     * 此方法通过调用MongoDBUtils的findDocument方法来查询数据库中的"hero"集合，
     * 使用"userId"字段作为查询条件，寻找与传入的userId匹配的文档
     * 如果找到匹配的文档，该方法会将其转换为Hero对象并返回
     */
    public Hero getByUserId(Integer userId) {
        return MongoDBUtils.findDocument("hero", Filters.eq("userId", userId), Hero.class);
    }

    /**
     * 根据用户名获取英雄对象
     *
     * @param username 用户名，用于查询特定的英雄对象
     * @return 返回匹配用户名的英雄对象，如果未找到则返回null
     */
    public Hero getByUsername(String username) {
        if (null == username) {
            return null;
        }
        // 使用MongoDB工具类查询并返回符合条件的英雄对象
        return MongoDBUtils.findDocument("hero", Filters.eq("username", username), Hero.class);
    }

    public Hero findOrCreate(Hero hero) {
        Hero existHero = getByUsername(hero.getUsername());
        if (null == existHero) {
            hero.setUserId(MongoDBUtils.getNextSequence("hero"));
            hero.setHeroAvatar(HeroConstant.HERO_AVATAR[new Random().nextInt(HeroConstant.HERO_AVATAR.length)]);
            MongoDBUtils.insertDocument("hero", hero);
        } else {
            hero = existHero;
        }
        return hero;
    }

    /**
     * 获取所有英雄列表
     * <p>
     * 此方法通过查询MongoDB数据库中的"hero"集合来获取所有英雄的信息
     * 使用空过滤器确保返回集合中的所有文档，将查询结果转换为Hero对象列表返回
     *
     * @return 包含所有英雄信息的列表
     */
    public List<Hero> heroes() {
        return MongoDBUtils.findDocuments("hero", Filters.empty(), Hero.class);
    }

    /**
     * 更新英雄形象
     * @param userId 用户id
     * @param heroAvatar 英雄形象
     */
    public void updateHeroAvatar(Integer userId, String heroAvatar) {
        Bson update = Updates.set("heroAvatar", heroAvatar);
        MongoDBUtils.updateDocument("hero", Filters.eq("userId", userId), update, Hero.class);
    }
}
