package org.herostory.handler.cmd;


import com.google.protobuf.GeneratedMessage;
import org.herostory.constants.HeroConstant;
import org.herostory.util.ClassScanner;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CmdHandler工厂类，用于管理和获取命令处理器。
 */
public class CmdHandlerFactory {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(CmdHandlerFactory.class);
    private static final Map<Class<?>, ICmdHandler<? extends GeneratedMessage>> cmdHandlers = new HashMap<>();

    static {
        init();
    }

    private CmdHandlerFactory() {
    }

    /**
     * 初始化方法，扫描并注册所有实现了ICmdHandler接口的类。
     */
    @SuppressWarnings("unchecked")
    private static void init() {
        try {
            List<ICmdHandler> handlers = ClassScanner.scanClasses(CmdHandlerFactory.class.getPackageName(), true, ICmdHandler.class);
            for (ICmdHandler handler : handlers) {
                scanHandler(handler);
            }
        } catch (Exception e) {
            logger.error("初始化CmdHandler异常", e);
        }
    }

    /**
     * 扫描并注册单个命令处理器。
     *
     * @param handler 需要扫描的命令处理器实例
     */
    @SuppressWarnings("unchecked")
    private static void scanHandler(ICmdHandler handler) {
        Method[] declaredMethods = handler.getClass().getDeclaredMethods();
        for (Method method : declaredMethods) {
            //只处理 handle 方法
            if (method.getName().equals(HeroConstant.HANDLE_METHOD_NAME)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 2) {
                    Class<?> parameterType = parameterTypes[1];
                    if (GeneratedMessage.class.isAssignableFrom(parameterType)) {
                        cmdHandlers.put(parameterType, handler);
                    }
                }
            }
        }
    }

    /**
     * 根据消息类型获取对应的命令处理器。
     *
     * @param clazz 消息类型的Class对象
     * @return 对应的命令处理器实例，如果不存在则返回null
     */
    public static ICmdHandler<? extends GeneratedMessage> getCmdHandler(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        return cmdHandlers.get(clazz);
    }
}
