package org.herostory.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 英雄
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Hero {
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 英雄形象
     */
    private String heroAvatar;
}
