package com.printer.base.enums;

import lombok.Getter;

/**
 * @author dongyu
 * @date 2024/5/22 08:21:49
 */
@Getter
public enum UserPrintServerType {

    DOC_PRINT("docPrintJobPrintServiceImpl")
    ,

    PRINT_JOB_PRINT("printJobPrintServiceImpl")

    ;
    private final String implName;

    UserPrintServerType(String implName) {
        this.implName = implName;
    }
}
