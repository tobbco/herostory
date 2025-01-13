package org.herostory.model;

import lombok.Data;

/**
 * 排行榜项
 */
@Data
public class RankItem {
    private Integer rankId;
    private Integer userId;
    private String userName;
    private String heroAvatar;
    private Integer win;
    private Integer lose;
}
