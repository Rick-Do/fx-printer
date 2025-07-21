package com.fxprinter.model;


import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-08 21:35:33
 * @since jdk1.8
 */
@Data
public class SvgNodePath {

    @JSONField(name = "@attribute_d")
    private String content;

    @JSONField(name = "@attribute_fill")
    private String fill;

    @JSONField(name = "@attribute_fill-opacity")
    private String opacity;

    @JSONField(name = "@attribute_p-id")
    private String id;

}
