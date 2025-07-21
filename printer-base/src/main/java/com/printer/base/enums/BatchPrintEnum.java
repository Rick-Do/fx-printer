package com.printer.base.enums;

import lombok.Getter;

/**
 * @author dongyu
 * @date 2024/5/14 10:01:54
 */
@Getter
public enum BatchPrintEnum {
    BARCODE("barCode", "条码"),

    QRCODE("qrCode", "二维码"),

    BASIC_TEXT("basicText", "文本"),

    BASIC_IMAGE("basicImage", "图片"),

    HORIZONTAL_LINE("horizontalLine", "水平线"),

    VERTICAL_LINE("verticalLine", "垂直线"),

    TABLE_CELL("tableCell", "表格单元"),

    ;

    private final String type;

    private final String label;

    BatchPrintEnum(String type, String label) {
        this.type = type;
        this.label = label;
    }

    public static BatchPrintEnum parseEnumByType(String type) {
        for (BatchPrintEnum batchPrintEnum : BatchPrintEnum.values()) {
            if (batchPrintEnum.getType().equals(type)) {
                return batchPrintEnum;
            }
        }
        return null;
    }
}
