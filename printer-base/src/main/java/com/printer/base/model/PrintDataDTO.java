package com.printer.base.model;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author dongyu
 * @date 2024/5/13 08:46:05
 */
@Data
public class PrintDataDTO {

    private String printerName;

    /**
     * 画布宽度
     */
    private Integer templateWidth;

    /**
     * 打印数量
     */
    private Integer printNum;

    /**
     * 画布高度
     */
    private Integer templateHeight;

    @JSONField(name = "dataItem")
    private List<PrintJsonDTO> printJson;
}
