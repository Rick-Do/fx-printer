package com.fx.app.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-11 14:02:49
 * @since jdk1.8
 */
@Data
@Accessors(chain = true)
@TableName("ROCKET_MQ_CONFIG")
public class RocketMqConfig {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @TableField("NAMESRV")
    private String namesrv;

    @TableField("CONSUMER")
    private String consumer;

    @TableField("PROGRAM_ID")
    private String programId;

    @TableField("TOPIC")
    private String topic;

    @TableField("TAG")
    private String tag;

    @TableField("ACCESS_KEY_ID")
    private String accessKeyId;

    @TableField("ACCESS_KEY_SECRET")
    private String accessKeySecret;

    @TableField("MESSAGE_MODEL")
    private String messageModel;

    @TableField("REMARK")
    private String remark;

}
