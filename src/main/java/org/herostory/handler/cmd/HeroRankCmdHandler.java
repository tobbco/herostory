package org.herostory.handler.cmd;

import com.google.protobuf.GeneratedMessage;
import io.netty.channel.ChannelHandlerContext;
import org.herostory.model.RankItem;
import org.herostory.protobuf.bean.GameMessageProto;
import org.herostory.rank.RankService;

import java.util.ArrayList;

/**
 * 排行榜
 */
public class HeroRankCmdHandler implements ICmdHandler<GameMessageProto.GetRankCmd> {
    @Override
    public void handle(ChannelHandlerContext channelHandlerContext, GameMessageProto.GetRankCmd cmd) {
        if (null == cmd) {
            return;
        }
        RankService.getInstance().getRankList(rankList -> {
            if (null == rankList) {
                rankList = new ArrayList<>();
            }
            GameMessageProto.GetRankResult.Builder builder = GameMessageProto.GetRankResult.newBuilder();
            for (RankItem rankItem : rankList) {
                GameMessageProto.GetRankResult.RankItem item = GameMessageProto.GetRankResult.RankItem.newBuilder()
                        .setRankId(rankItem.getRankId())
                        .setUserId(rankItem.getUserId())
                        .setUserName(rankItem.getUserName())
                        .setHeroAvatar(rankItem.getHeroAvatar())
                        .setWin(rankItem.getWin())
                        .build();
                builder.addRankItem(item);
            }
            GameMessageProto.GetRankResult rankResult = builder.build();
            channelHandlerContext.writeAndFlush(rankResult);
            return null;
        });
    }
}
