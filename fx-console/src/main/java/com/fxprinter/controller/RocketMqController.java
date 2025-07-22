package com.fxprinter.controller;


import com.fx.app.entity.ProgramServerInfo;
import com.fx.app.entity.RocketMqConfig;
import com.fx.app.enums.ProgramRunStatus;
import com.fx.app.enums.ProgramStatus;
import com.fxprinter.model.RocketMqMessageModel;
import com.fxprinter.service.ProgramServerInfoService;
import com.fxprinter.service.RocketMqConfigService;
import com.fxprinter.util.FadeTransitionUtil;
import com.fxprinter.util.PromiseUtil;
import com.fxprinter.util.SvgUtil;
import com.printer.base.enums.ProgramType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

/**
 * rocketMq控制器
 * @author dongyu
 * @version 1.0
 * @date 2025-07-10 13:14:31
 * @since jdk1.8
 */
public class RocketMqController implements Initializable {

    @FXML
    public TextField namesrv;

    @FXML
    public TextField topic;

    @FXML
    public TextField tag;

    @FXML
    public ComboBox<RocketMqMessageModel> messageModel;

    @FXML
    public TextField accessKeyId;

    @FXML
    public TextField accessKeySecret;

    @FXML
    public TextField consumerGroup;

    @FXML
    public TextField programName;

    @FXML
    public TextField programDescribe;


    private Button clearButton;


    private static RocketMqConfig config = new RocketMqConfig();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*if (messageModel != null) {
            initMessageModePane();
        }*/
//        initConfigInfo();
//        initListener();
    }

    public void initConfigInfo(Integer programId) {
        PromiseUtil.runInBackground(()->{
            ProgramServerInfo programServerInfo = ProgramServerInfoService.getById(programId);
            if (programServerInfo == null) {
                return;
            }
            programName.setText(programServerInfo.getProgramName());
            programDescribe.setText(programServerInfo.getDescription());
            RocketMqConfig mqConfig = RocketMqConfigService.getConfigByProgramId(programId);
            if (mqConfig == null) {
                return;
            }
            config = mqConfig;
            namesrv.setText(mqConfig.getNamesrv());
            consumerGroup.setText(mqConfig.getConsumer());
            topic.setText(mqConfig.getTopic());
            tag.setText(mqConfig.getTag());
            accessKeyId.setText(mqConfig.getAccessKeyId());
            accessKeySecret.setText(mqConfig.getAccessKeySecret());
        });

    }

    /*private void initListener() {
        if (namesrv != null) {
           namesrv.textProperty().addListener((observable, oldValue, newValue) -> {
            SettingButtonState.getInstance().changeShowState(this::isAttitudeChange);
        });
        }
        if (topic != null) {
            topic.textProperty().addListener((observable, oldValue, newValue) -> {
                SettingButtonState.getInstance().changeShowState(this::isAttitudeChange);
            });
        }
        if (consumerGroup != null) {
            consumerGroup.textProperty().addListener((observable, oldValue, newValue) -> {
                SettingButtonState.getInstance().changeShowState(this::isAttitudeChange);
            });
        }
        if (tag != null) {
            tag.textProperty().addListener((observable, oldValue, newValue) -> {
                SettingButtonState.getInstance().changeShowState(this::isAttitudeChange);
            });
        }
        if (messageModel != null) {
            messageModel.valueProperty().addListener((observable, oldValue, newValue) -> {
                SettingButtonState.getInstance().changeShowState(this::isAttitudeChange);
            });
        }
        if (accessKeyId != null) {
            accessKeyId.textProperty().addListener((observable, oldValue, newValue) -> {
                SettingButtonState.getInstance().changeShowState(this::isAttitudeChange);
            });
        }
        if (accessKeySecret != null) {
            accessKeySecret.textProperty().addListener((observable, oldValue, newValue) -> {
                SettingButtonState.getInstance().changeShowState(this::isAttitudeChange);
            });
        }

    }*/

    private void initMessageModePane() {
        clearButton = new Button();
        clearButton.setGraphic(SvgUtil.loadSvg("downBox", 1D, 1D));
        messageModel.setItems(FXCollections.observableArrayList(
                new RocketMqMessageModel("CLUSTERING", "集群模式"),
                new RocketMqMessageModel("BROADCASTING", "广播模式")
        ));
        //自定义显示
        messageModel.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(RocketMqMessageModel item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.describe());
                } else {
                    setText(null);
                }
            }
        });
        messageModel.setConverter(new StringConverter<>() {
            @Override
            public String toString(RocketMqMessageModel object) {
                if (object == null) {
                    return null;
                }
                return object.describe();
            }

            @Override
            public RocketMqMessageModel fromString(String string) {
                return messageModel.getItems().stream().filter(item -> item.describe().equals(string)).findFirst().orElse(null);
            }
        });
        //动画
        messageModel.setSkin(new ComboBoxListViewSkin<>(messageModel));
        messageModel.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // 获取下拉弹窗的节点
                Node popupContent = ((ComboBoxListViewSkin<?>) messageModel.getSkin()).getPopupContent();
                // 确保只初始化一次动画（或者每次显示都应用初始状态和动画）
                if (popupContent != null) {
                    FadeTransitionUtil.applyFadeTransition(popupContent, 500);
                }
            }
        });
        // 替换箭头按钮区域
        messageModel.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                StackPane arrowButton = (StackPane) messageModel.lookup(".arrow-button");
                if (arrowButton != null) {
                    // 创建容器
                    HBox container = new HBox(clearButton, arrowButton);
                    container.setAlignment(Pos.CENTER_RIGHT);
                    container.setSpacing(5);

                    // 创建新的箭头按钮容器
                    StackPane newArrowButton = new StackPane(container);
                    newArrowButton.getStyleClass().setAll("arrow-button");

                    // 替换原来的箭头按钮
                    StackPane arrowsContainer = (StackPane) arrowButton.getParent();
                    arrowsContainer.getChildren().set(0, newArrowButton);
                }
            }
        });
    }


    /**
     * 属性是否发送变化
     * @return  true-是,false-否
     */
    /*private boolean isAttitudeChange() {
        if (!Objects.equals(namesrv.getText(), config.getNamesrv())) {
            return true;
        }
        if (!Objects.equals(topic.getText(), config.getTopic())) {
            return true;
        }
        if (!Objects.equals(tag.getText(), config.getTag())) {
            return true;
        }
        if (!Objects.equals(consumerGroup.getText(), config.getConsumer())) {
            return true;
        }
        if (!Objects.equals(accessKeyId.getText(), config.getAccessKeyId())) {
            return true;
        }
        if (!Objects.equals(accessKeySecret.getText(), config.getAccessKeySecret())) {
            return true;
        }
        return false;
    }*/

    public void handleSave(ActionEvent event)  {
        boolean isSave = Objects.isNull(config.getId());
        config.setNamesrv(namesrv.getText());
        config.setNamesrv(namesrv.getText());
        config.setTopic(topic.getText());
        config.setTag(tag.getText());
        config.setConsumer(consumerGroup.getText());
        config.setAccessKeyId(accessKeyId.getText());
        config.setAccessKeySecret(accessKeySecret.getText());
        if (isSave){
            //新增数据
            ProgramServerInfo serverInfo = new ProgramServerInfo()
                    .setType(ProgramType.RocketMQ)
                    .setStatus(ProgramStatus.ENABLE.getValue())
                    //初始为空闲状态
                    .setRunStatus(ProgramRunStatus.IDLE.getValue())
                    .setCreateTime(new Date())
                    .setProgramName(programName.getText())
                    .setDescription(programDescribe.getText());
            ProgramServerInfoService.insert(serverInfo);
            config.setProgramId(serverInfo.getId());
             RocketMqConfigService.insert(config);
            return;
        }
        PromiseUtil.runInBackground(()->{
            RocketMqConfigService.update(config);
            ProgramServerInfo serverInfo = ProgramServerInfoService.getById(config.getProgramId());
            if (serverInfo == null) {
                return;
            }
            serverInfo.setProgramName(programName.getText());
            serverInfo.setDescription(programDescribe.getText());
            ProgramServerInfoService.updateById(serverInfo);
        });
    }

   /* public void handleCancel(ActionEvent event) {
        RocketMqConfigService.getConfigById(1, rocketMqConfig -> {
            if (rocketMqConfig == null) {
                return;
            }
            namesrv.setText(rocketMqConfig.getNamesrv());
            topic.setText(rocketMqConfig.getTopic());
            tag.setText(rocketMqConfig.getTag());
            consumerGroup.setText(rocketMqConfig.getConsumer());
            accessKeyId.setText(rocketMqConfig.getAccessKeyId());
            accessKeySecret.setText(rocketMqConfig.getAccessKeySecret());
        }, throwable -> {});
    }*/


}
