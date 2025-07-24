package com.fx.app.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-24 13:54:22
 * @since jdk1.8
 */
@Data
@TableName("base_param_config")
public class BaseParamConfig {

    @TableId(type = IdType.AUTO, value = "id")
    private Integer id;

    @TableField(value = "language")
    private String language;

    @TableField(value = "dark_mode")
    private String darkMode;

    @TableField(value = "file_save_path")
    private String fileSavePath;

    @TableField(value = "create_time")
    private Date createTime;



}
