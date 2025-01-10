package org.herostory;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import org.herostory.constants.HeroConstant;
import org.herostory.protobuf.bean.GameMessageProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * cmd命令识别器
 */
public final class CmdRecognizer {
    private static final Logger logger = LoggerFactory.getLogger(CmdRecognizer.class);
    /**
     * 初始化消息构建器
     */
    private static final Map<Integer, GeneratedMessage> cmdBuilderMap = new HashMap<>();
    /**
     * 消息结果与消息结果命令映射
     */
    private static final Map<Class<?>, Integer> cmdIdMap = new HashMap<>();

    static {
        //获取所有的内部类
        Class<?>[] declaredClasses = GameMessageProto.class.getDeclaredClasses();
        for (Class<?> declaredClass : declaredClasses) {
            if (!GeneratedMessage.class.isAssignableFrom(declaredClass)) {
                //如果不是消息类型跳过
                continue;
            }
            //将类名转为小写
            String clazzName = declaredClass.getSimpleName().toLowerCase();
            //获取命令ID
            for (GameMessageProto.GameCmdId cmdId : GameMessageProto.GameCmdId.values()) {
                //将名称去除_且转为小写,例如USER_LOGIN_CMD转为userlogincmd
                String cmdName = cmdId.name().replace("_", "").toLowerCase();
                if (!clazzName.startsWith(cmdName)) {
                    //如果类名不是命令ID开头,则跳过
                    continue;
                }
                try {
                    //通过反射获取默认实例
                    GeneratedMessage defaultInstance = (GeneratedMessage) declaredClass.getDeclaredMethod(HeroConstant.GET_DEFAULT_INSTANCE_METHOD_NAME).invoke(declaredClass);
                    logger.info("CMD关联 {}<=>{}", cmdId.getNumber(), declaredClass.getName());
                    //将命令ID与默认实例映射到cmdBuilderMap中
                    cmdBuilderMap.put(cmdId.getNumber(), defaultInstance);
                    //将命令ID与类名映射到cmdIdMap中
                    cmdIdMap.put(declaredClass, cmdId.getNumber());
                } catch (Exception e) {
                    logger.error("cmd命令初始化异常", e);
                }

            }
        }
    }

    private CmdRecognizer() {
    }

    /**
     * 根据命令ID获取对应的Message.Builder对象
     * 此方法用于根据给定的命令ID，从预定义的映射中检索并返回相应的Message.Builder对象，
     * 以便调用者可以构建特定类型的消息对象如果命令ID不存在于映射中，或者命令ID无效，
     * 则方法返回null
     *
     * @param cmdId 命令ID，用于标识特定的消息类型如果ID无效（小于0），方法将返回null
     * @return 如果找到对应命令ID的消息构建器，则返回该构建器；否则返回null
     */
    public static Message.Builder getBuilderByCmdId(int cmdId) {
        // 检查传入的命令ID是否为无效值
        if (cmdId < 0) {
            return null;
        }
        // 从映射中获取与命令ID关联的GeneratedMessage对象
        GeneratedMessage generatedMessage = cmdBuilderMap.get(cmdId);
        // 检查获取到的对象是否为空
        if (null == generatedMessage) {
            return null;
        }
        // 返回与获取到的GeneratedMessage对象相同类型的新的Builder对象
        return generatedMessage.newBuilderForType();
    }

    /**
     * 根据类获取对应的命令ID
     *
     * @param clazz 要查询的类，不能为null
     * @return 返回类对应的命令ID如果类为null或者在映射中找不到对应的命令ID，则返回-1
     */
    public static int getCmdIdByClass(Class<?> clazz) {
        // 初始化命令ID为-1，表示未找到对应的命令ID
        int cmdId = -1;
        // 检查输入的类是否为null
        if (null == clazz) {
            // 如果类为null，则直接返回默认的命令ID
            return cmdId;
        }
        // 使用类作为键从映射中获取对应的命令ID，如果映射中没有该类，则返回默认值-1
        return cmdIdMap.getOrDefault(clazz, cmdId);
    }
}
