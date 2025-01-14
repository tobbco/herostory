package org.herostory.rank;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.herostory.db.jedis.RedisUtil;
import org.herostory.model.RankItem;
import org.herostory.processor.AsyncProcessor;
import org.herostory.processor.IAsyncOperation;
import org.slf4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 排行业务
 */
public final class RankService {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(RankService.class);
    private RankService() {}

    private static class Holder {
        static RankService instance = new RankService();
    }



    public static RankService getInstance() {
        return Holder.instance;
    }

    public void getRankList(Function<List<RankItem>, Void> callback) {
        IAsyncOperation operation = new IAsyncOperation() {
            private List<RankItem> rankList;

            @Override
            public void async() {
                try (Jedis redis = RedisUtil.getRedis()) {
                    //获取排行榜数据
                    List<Tuple> rank = redis.zrangeWithScores("RankWin", 0, 9);
                    if (null == rank || rank.isEmpty()) {
                        logger.warn("排行榜为空");
                        return;
                    }
                    int rankId = 0;
                    rankList = new ArrayList<>();
                    for (Tuple tuple : rank) {
                        int userId = Integer.parseInt(tuple.getElement());
                        int win = (int)tuple.getScore();
                        RankItem rankItem = new RankItem();
                        rankItem.setRankId(++rankId);
                        rankItem.setUserId(userId);
                        //从缓存中获取英雄信息
                        String heroInfo = redis.hget("hero_" + userId, "HeroInfo");
                        if (null == heroInfo) {
                            continue;
                        }
                        JSONObject jsonObject = JSON.parseObject(heroInfo);
                        rankItem.setUserName(jsonObject.getString("userName"));
                        rankItem.setHeroAvatar(jsonObject.getString("heroAvatar"));
                        rankItem.setWin(win);
                        rankList.add(rankItem);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(),e);
                }
            }

            @Override
            public void callback() {
                try {
                    callback.apply(this.rankList);
                } catch (Exception e) {
                    logger.error("获取排行榜数据失败:{}",e.getMessage(),e);
                }
            }
        };
        AsyncProcessor.getInstance().process(operation);
    }




}
