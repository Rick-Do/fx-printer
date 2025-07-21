package com.fx.app.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-17 13:58:09
 * @since jdk1.8
 */
@Getter
public enum ProgramRunStatus {

    /*0-空闲-yellow, 1-运行中-green，2-中断-red，4-停止-gray*/
    IDLE(0, "空闲", "#FFC107"),

    RUNNING(1, "运行中", "#4CAF50"),

    INTERRUPT(2, "中断", "#F44336"),

    STOP(4, "停止", "#9E9E9E"),

    ;

    @EnumValue
    private final Integer value;

    private final String describe;

    private final String color;

    ProgramRunStatus(int value, String describe, String color) {
        this.value = value;
        this.describe = describe;
        this.color = color;
    }

    public static ProgramRunStatus getByValue(int value) {
        for (ProgramRunStatus status : ProgramRunStatus.values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}
