package com.fxprinter.model;


import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-08 21:35:24
 * @since jdk1.8
 */
@Data
public class SvgNode {

    @JSONField(name = "path")
    private List<SvgNodePath> paths;

    @JSONField(name = "@attribute_xmlns")
    private String xmlns;

    @JSONField(name = "@attribute_viewBox")
    private String viewBox;

    @JSONField(name = "@attribute_version")
    private String version;

    @JSONField(name = "@attribute_width")
    private Double width;

    @JSONField(name = "@attribute_height")
    private Double height;

    @JSONField(name = "@attribute_p-id")
    private String id;

    @JSONField(name = "@attribute_name")
    private String name;

}
