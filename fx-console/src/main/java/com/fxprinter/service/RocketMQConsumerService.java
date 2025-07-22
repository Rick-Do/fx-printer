package com.fxprinter.service;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.fx.app.util.PrintUtil;
import com.fx.app.entity.RocketMqConfig;
import com.printer.base.model.PrintDataDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-15 10:46:26
 * @since jdk1.8
 */
public class RocketMQConsumerService {


    private DefaultMQPushConsumer consumer;

    @Getter
    private ObservableList<String> messageLog = FXCollections.observableArrayList();

    @Getter
    private boolean running = false;


    /**
     * 绘制图片并打印
     */
    public void start(RocketMqConfig rocketMqConfig) {
        //获取rocketmq配置
        try {
            if (rocketMqConfig == null) {
                throw new RuntimeException("请先配置RocketMq");
            }

            //连接rocketmq
            String accessKeyId = rocketMqConfig.getAccessKeyId();
            if (StrUtil.isNotEmpty(accessKeyId)) {
                consumer = new DefaultMQPushConsumer(rocketMqConfig.getConsumer(), new AclClientRPCHook(
                        new SessionCredentials(accessKeyId, rocketMqConfig.getAccessKeySecret())
                ));
            }else {
                consumer = new DefaultMQPushConsumer(rocketMqConfig.getConsumer());
            }
            consumer.setNamesrvAddr(rocketMqConfig.getNamesrv());
            consumer.setMessageModel(MessageModel.CLUSTERING);
            consumer.subscribe(rocketMqConfig.getTopic(), StrUtil.isEmpty(rocketMqConfig.getTag()) ? "*" : rocketMqConfig.getTag());

            long time = new Date().getTime();

            consumer.registerMessageListener((MessageListenerConcurrently)(msgs, context) -> {
                for (MessageExt msg : msgs) {
                    long storeTimestamp = msg.getStoreTimestamp();
                    //时间戳小于当前一分钟
                    if (storeTimestamp < time - 3000) {
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    System.out.println("接收到消息：" + new String(msg.getBody(), StandardCharsets.UTF_8));
                    String message = new String(msg.getBody(), StandardCharsets.UTF_8);
                    PrintDataDTO dataDTO = JSON.parseObject(message, PrintDataDTO.class);
                    try {
                        PrintUtil.docPrintPaint(dataDTO, "printerImg");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

            });
            System.out.println("开始监听");
            consumer.start();
            running = true;
            System.out.println("启动rocketmq成功");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stopConsumer() {
        if (consumer != null) {
            consumer.shutdown();
            running = false;
            Platform.runLater(() -> messageLog.add("消费者已停止"));
        }
    }

}
