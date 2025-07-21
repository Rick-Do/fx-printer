package com.fx.app.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-17 13:46:04
 * @since jdk1.8
 */
@Getter
public enum ProgramStatus {

    DELETE(-1, "删除"),

    DISABLE(0, "禁用"),

    ENABLE(1, "启用"),

    ;

    @EnumValue
    public final int value;

    private final String describe;

    ProgramStatus(int value, String describe) {
        this.value = value;
        this.describe = describe;
    }
}
