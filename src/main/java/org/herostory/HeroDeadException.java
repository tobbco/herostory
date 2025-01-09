package org.herostory;
/**
 * 英雄死亡异常
 * @author hxh
 *
 */
public class HeroDeadException extends RuntimeException{

    public HeroDeadException(String message) {
        super(message);
    }
}
