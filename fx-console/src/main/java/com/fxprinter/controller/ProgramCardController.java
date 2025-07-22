package com.fxprinter.controller;


import com.fx.app.entity.ProgramServerInfo;
import com.fx.app.entity.RocketMqConfig;
import com.fx.app.enums.ProgramRunStatus;
import com.fxprinter.service.ProgramServerInfoService;
import com.fxprinter.service.RocketMQConsumerService;
import com.fxprinter.service.RocketMqConfigService;
import com.fxprinter.util.SvgUtil;
import com.printer.base.enums.ProgramType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import lombok.Data;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-15 13:16:40
 * @since jdk1.8
 */
@Data
public class ProgramCardController implements Initializable {

    @FXML
    public BorderPane root;

    @FXML
    public StackPane topPane;

    @FXML
    public StackPane centerPane;

    @FXML
    public StackPane bottomPane;

    private boolean loading = false;

    private ProgramServerInfo serverInfo;

    private RocketMQConsumerService consumerService = new RocketMQConsumerService();


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void init() {
        if (serverInfo == null) {
            return;
        }
        if (ProgramType.RocketMQ.equals(serverInfo.getType())){
            initRocketMQCard();
        }
    }

    public void stop() {
        serverInfo.setRunStatus(ProgramRunStatus.STOP.getValue());
        ProgramServerInfoService.updateById(serverInfo);
        ProgramType type = serverInfo.getType();
        if (Objects.isNull(type)) {
            return;
        }
        switch (type) {
            case RabbitMQ -> consumerService.stopConsumer();
        }
    }

    private void initRocketMQCard() {
        //顶部容器
        //中间图片
        centerPane.getChildren().add(SvgUtil.loadSvg("rocketmq_1", 50, 50, ""));
        topPane.getChildren().add(createTopNode());

        RocketMqConfig config = RocketMqConfigService.getConfigById(serverInfo.getId());
        //底部容器
        bottomPane.getChildren().add(createBottomNode(
                        ()-> consumerService.start(config),
                        ()-> consumerService.stopConsumer())
                );
    }

    private Node createBottomNode(Runnable runningTask, Runnable stopTask) {
        HBox hBox = new HBox();
        hBox.setFillHeight(true);
        hBox.getStyleClass().add("program-bottom");
        hBox.getChildren().add(createBottomLeft());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        hBox.getChildren().add(spacer);
        hBox.getChildren().add(createBottomCenter());
        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);
        hBox.getChildren().add(rightSpacer);
        hBox.getChildren().add(createBottomRight(runningTask, stopTask));
        return hBox;
    }

    private Node createBottomRight(Runnable runningTask, Runnable stopTask) {
        StackPane container = new StackPane();
        Integer runStatus = serverInfo.getRunStatus();
        ProgramRunStatus status = ProgramRunStatus.getByValue(runStatus);
        if (status == null) {
            status = ProgramRunStatus.IDLE;
        }
        String svgComponent = ProgramRunStatus.RUNNING.equals(status) ? "pause" : "start";
        Button button = new Button();
        button.setGraphic(SvgUtil.loadSvg(svgComponent, 5, 5, ""));
        button.getStyleClass().add("program-bottom-button");
        button.setOnMouseClicked(event -> {
            Integer infoRunStatus = serverInfo.getRunStatus();
            if (Objects.equals(infoRunStatus, ProgramRunStatus.RUNNING.getValue())) {
                handleStopButtonClick(stopTask);
            }else {
                handleStartButtonClick(runningTask);
            }
        });
        container.getChildren().add( button);
        return container;
    }

    private Node createBottomLeft() {
        Integer runStatus = serverInfo.getRunStatus();
        ProgramRunStatus status = ProgramRunStatus.getByValue(runStatus);
        if (status == null) {
            status = ProgramRunStatus.IDLE;
        }
        String svgComponent =  switch ( status) {
            case STOP -> "stopped";
            case RUNNING -> "running";
            case INTERRUPT -> "interrupt";
            default -> "idle";
        };
        Node node = SvgUtil.loadSvg(svgComponent, 4, 4, "");
        StackPane container = new StackPane();
        container.getChildren().add(node);
        return container;
    }

    private Node createBottomCenter() {
        StackPane textContainer = new StackPane();
        textContainer.setAlignment(Pos.CENTER); // 容器内容居中
        textContainer.setPadding(new Insets(5));
        // 创建文本标签
        Label textLabel = new Label();
        textLabel.setMaxWidth(200);
        textLabel.setWrapText(true); // 关键：启用自动换行
        textLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER); // 文字居中
        textLabel.setAlignment(javafx.geometry.Pos.CENTER); // 标签内容居中
        textLabel.setText(serverInfo.getDescription());
        textContainer.getChildren().add(textLabel);
        // 创建文本容器（固定宽度400px）
        ScrollPane container = new ScrollPane(textContainer);
        container.setPrefWidth(200); // 设置固定宽度
        container.setMinWidth(200); // 设置固定宽度
        container.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        container.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        container.setStyle("-fx-background-color: transparent");
        textContainer.minWidthProperty().bind(container.widthProperty());
        textContainer.minHeightProperty().bind(container.heightProperty());
        return container;
    }

    private Node createTopNode() {
        String programName = serverInfo.getProgramName();
        HBox hBox = new HBox();

        //标题
        Label label = new Label(programName);
        label.getStyleClass().add("program-title");
        hBox.getChildren().add(label);
        return hBox;
    }

    private void handleStartButtonClick(Runnable runningTask) {
        if (loading) {
            return;
        }
        loading = true;
        runningTask.run();
        serverInfo.setRunStatus(ProgramRunStatus.RUNNING.getValue());
        ProgramServerInfoService.updateById(serverInfo);
        Platform.runLater(()->{
            topPane.getChildren().clear();
            centerPane.getChildren().clear();
            bottomPane.getChildren().clear();
            init();
            loading = false;
        });
    }

    private void handleStopButtonClick(Runnable stopStak) {
        if (loading) {
            return;
        }
        loading = true;
        stopStak.run();
        serverInfo.setRunStatus(ProgramRunStatus.STOP.getValue());
        ProgramServerInfoService.updateById(serverInfo);
        Platform.runLater(()->{
            topPane.getChildren().clear();
            centerPane.getChildren().clear();
            bottomPane.getChildren().clear();
            init();
            loading = false;
        });
    }


}
