package com.printer.base.style;

import lombok.Data;

/**
 * 边距样式
 * @author dongyu
 * @date 2024/5/17 11:16:52
 */
@Data
public class Margin {

    /**
     * 上边距
     */
    private int top;

    /**
     * 右边距
     */
    private int right;

    /**
     * 下边距
     */
    private int bottom;

    /**
     * 左边距
     */
    private int left;

    public Margin(int top, int right, int bottom, int left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public Margin(int margin) {
        this.top = margin;
        this.right = margin;
        this.left = margin;
        this.bottom = margin;
    }

    public Margin(int leftRight, int topBottom) {
        this.top = topBottom;
        this.right = leftRight;
        this.left = leftRight;
        this.bottom = topBottom;
    }
}
