package org.herostory.model;

import lombok.Data;

/**
 * 移动状态
 */
@Data
public class MoveState {
    // 起始位置 X
    float fromPosX;
    // 起始位置 Y
    float fromPosY;
    // 移动到位置 X
    float toPosX;
    // 移动到位置 Y
    float toPosY;
    // 启程时间戳
    long startTime;
}
