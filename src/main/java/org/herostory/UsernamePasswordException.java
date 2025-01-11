package org.herostory;
/**
 * 用户名或密码异常
 * @author hxh
 *
 */
public class UsernamePasswordException extends RuntimeException{

    public UsernamePasswordException(String message) {
        super(message);
    }

    public UsernamePasswordException() {
        super("用户名或密码错误");
    }
}
