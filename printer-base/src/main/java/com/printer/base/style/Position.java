package com.printer.base.style;

import lombok.Data;

/**
 * 位置
 * @author dongyu
 * @date 2024/5/17 11:22:32
 */
@Data
public class Position {

    /**
     * x轴坐标
     */
    private Integer x;

    /**
     * y轴坐标
     */
    private Integer y;

    public Position(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

}
