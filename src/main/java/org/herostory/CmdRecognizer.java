package org.herostory;

import com.google.protobuf.Message;
import org.herostory.protobuf.bean.GameMessageProto;

/**
 * cmd命令识别器
 */
public final class CmdRecognizer {
    private CmdRecognizer() {
    }

    public static Message.Builder getBuilderByCmdId(int cmdId) {
        Message.Builder builder;
        switch (cmdId) {
            case GameMessageProto.GameMsgId.USER_LOGIN_CMD_VALUE:
                builder = GameMessageProto.UserLoginCmd.newBuilder();
                break;
            case GameMessageProto.GameMsgId.ONLINE_USER_CMD_VALUE:
                builder = GameMessageProto.OnlineUserCmd.newBuilder();
                break;
            case GameMessageProto.GameMsgId.USER_MOVE_CMD_VALUE:
                builder = GameMessageProto.UserMoveCmd.newBuilder();
                break;
            default:
                builder = null;
        }
        return builder;
    }

    public static int getCmdIdByClass(Object msg) {
        int cmdId = -1;
        //如果是登录响应
        if (msg instanceof GameMessageProto.UserLoginResult) {
            cmdId = GameMessageProto.GameMsgId.USER_LOGIN_RESULT_VALUE;
        } else if (msg instanceof GameMessageProto.OnlineUserResult) {
            cmdId = GameMessageProto.GameMsgId.ONLINE_USER_RESULT_VALUE;
        } else if (msg instanceof GameMessageProto.UserMoveResult) {
            cmdId = GameMessageProto.GameMsgId.USER_MOVE_RESULT_VALUE;
        } else if (msg instanceof GameMessageProto.UserDisconnectResult) {
            cmdId = GameMessageProto.GameMsgId.USER_DISCONNECT_RESULT_VALUE;
        }
        return cmdId;
    }
}
