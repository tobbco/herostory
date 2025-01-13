# 集成redis

## 分支业务

实现redis排行榜功能
1. redis中使用`hset`缓存用户信息
缓存示例: `hset hero_<id>  HeroInfo {userId: 1, heroAvatar:'xxx'}`
2. redis中使用`zadd`缓存用户胜利次数
缓存示例: `zadd hero_<id>  win 1`
3. redis中使用`zadd`缓存用户失败次数
缓存示例: `zadd hero_<id>  lose 1`



## 引入redis依赖
我们使用的jedis作为redis的依赖，所以需要引入jedis的依赖。

```groovy
implementation 'redis.clients:jedis:5.3.0-beta1'
```

## 创建RedisUtil
详情参考 <RedisUtil>

## 用户登录时存储相关缓存信息