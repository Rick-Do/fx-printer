package com.fx.app.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fx.app.enums.ProgramRunStatus;
import com.fx.app.enums.ProgramStatus;
import com.printer.base.enums.ProgramType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 程序服务信息
 * @author dongyu
 * @version 1.0
 * @date 2025-07-16 13:42:16
 * @since jdk1.8
 */
@Data
@Accessors(chain = true)
@TableName("PROGRAM_SERVER_INFO")
public class ProgramServerInfo {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 类型
     */
    @TableField("TYPE")
    private ProgramType type;

    /**
     * 服务名称
     */
    @TableField("PROGRAM_NAME")
    private String programName;

    /**
     * 服务状态 (1-启用，0-禁用，-1-删除)
     * {{@link ProgramStatus}}
     */
    @TableField("STATUS")
    private Integer status;

    /**
     * 程序运行状态 (0-空闲-yellow, 1-运行中-green，2-中断-red，4-停止-gray)
     * {{@link ProgramRunStatus}}
     */
    @TableField("RUN_STATUS")
    private Integer runStatus;

    /**
     * 服务描述
     */
    @TableField("DESCRIPTION")
    private String description;

    /**
     * 服务创建时间
     */
    @TableField("CREATE_TIME")
    private Date createTime;



}
