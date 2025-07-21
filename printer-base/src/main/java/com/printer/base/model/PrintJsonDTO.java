package com.printer.base.model;

import lombok.Data;

/**
 * 打印数据封装对象
 * @author dongyu
 * @date 2024/5/10 09:48:07
 */
@Data
public class PrintJsonDTO {

    private String id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 位置
     */
    private Position position;

    /**
     * 宽度
     */
    private Integer width;

    /**
     * 高度
     */
    private Integer height;

    /**
     * 规则
     */
    private String rule;

    /**
     * 内容
     */
    private String text;

    /**
     * 图片地址
     */
    private String src;

    /**
     * 样式
     */
    private Style style;

    /**
     * 位置
     */
    @Data
    public static class Position {

        private Integer x;

        private Integer y;
    }

    /**
     * 样式
     */
    @Data
    public static class Style {

        private String color;

        private String fontSize;

        private String fontWeight;

        private String borderColor;

        private String borderWidth;

        private String borderStyle;
    }
}
