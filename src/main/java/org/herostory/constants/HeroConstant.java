package org.herostory.constants;


public class HeroConstant {
    private HeroConstant() {

    }

    /**
     * 英雄key
     */
    public static final String HERO_ID_KEY = "userId";
    /**
     * 处理方法名
     */
    public static final String HANDLE_METHOD_NAME = "handle";

    /**
     * 调用协议中的默认实例方法名
     */
    public static final String GET_DEFAULT_INSTANCE_METHOD_NAME = "getDefaultInstance";
    /**
     * 默认减血量
     */
    public static final Integer DEFAULT_SUBTRACT_HP = 10;

    /**
     * 测试游戏地址
     */
    public static final String TEST_GAME_URL_STEP010 = "http://cdn0001.afrxvk.cn/hero_story/demo/step010/index.html?serverAddr=127.0.0.1:%s&userId=1";
    /**
     * 解决了游戏页面刷新英雄移动问题
     */
    public static final String TEST_GAME_URL_STEP020 = "http://cdn0001.afrxvk.cn/hero_story/demo/step020/index.html?serverAddr=127.0.0.1:%s&userId=1";
}
