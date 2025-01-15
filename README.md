# 排行榜业务

## 技术栈
1.Redis
2.RocketMQ

## 主要逻辑

1. 英雄登录将信息存储到Redis
使用的是`HSET`,参考代码:`org.herostory.model.Hero.updateRankCache`
2. 在攻击CmdHandler中，如果英雄死亡，将胜负结果发送到RocketMQ
参考代码:`org.herostory.msg.CmdHeroAttackHandler`
3. RocketMQ消费端将结果处理后存储到Redis中
参考代码:`org.herostory.rank.RankService.refreshRankList`
4. 在排行榜CmdHandler中，将Redis中的数据取出来，并排序，返回给客户端
参考代码:`org.herostory.handler.cmd.HeroRankCmdHandler`