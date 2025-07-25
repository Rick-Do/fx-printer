package com.fxprinter.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fx.app.entity.ProgramServerInfo;
import com.fxprinter.MainApplication;
import com.fxprinter.event.ProgramRefreshEvent;
import com.fxprinter.service.ProgramServerInfoService;
import com.fxprinter.util.PluginUtil;
import com.fxprinter.util.SvgUtil;
import com.fxprinter.view.CustomDialog;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import lombok.Getter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-15 13:10:08
 * @since jdk1.8
 */
public class ProgramController implements Initializable {

    private static ContextMenu currentContextMenu;

    @FXML
    public FlowPane flowPane;

    @FXML
    public ScrollPane stackPaneContainer;

    @Getter
    private static final List<ProgramCardController> cardList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //获取配置
        initCard();


    }

    private void initCard() {
        List<ProgramServerInfo> serverInfos = ProgramServerInfoService.list(new LambdaQueryWrapper<>());
        if (serverInfos == null) {
            return;
        }
        for (ProgramServerInfo serverInfo : serverInfos) {
            EventBus eventBus = new EventBus();
            eventBus.register(this);
            ProgramCardController controller = loadCardView();
            if (controller != null) {
                controller.setEventBus(eventBus);
                controller.setServerInfo(serverInfo);
                controller.init();
                flowPane.getChildren().add(controller.getRoot());
            }
            cardList.add(controller);
        }
        addContextMenu();
    }

    private void addContextMenu() {
        currentContextMenu = new ContextMenu();
        //有多种方式
        initContextMenu(currentContextMenu);
        stackPaneContainer.setOnContextMenuRequested(event -> {
            currentContextMenu.show(stackPaneContainer, event.getScreenX(), event.getScreenY());
        });
        stackPaneContainer.setOnMouseClicked(event -> {
            //取消显示右键内容
            currentContextMenu.hide();
        });
    }

    @Subscribe
    public void handleRefresh(ProgramRefreshEvent event) {
        //刷新
        flowPane.getChildren().clear();
        cardList.clear();
        initCard();
    }

    private void initContextMenu(ContextMenu contextMenu) {
        currentContextMenu.getStyleClass().add("material-context-menu");
        contextMenu.getItems().add(createMenuItem("RocketMQ", "RocketMQ", this::openRocketMQ));
        //SpringBoot
        MenuItem springBootMenuItem = createMenuItem("SpringBoot", "springboot", this::openSpringBoot);
        springBootMenuItem.setDisable(true);
        contextMenu.getItems().add(springBootMenuItem);
       // WebSocket
        MenuItem webSocketMenuItem = createMenuItem("WebSocket", "webSocket", this::openWebSocket);
        webSocketMenuItem.setDisable(true);
        contextMenu.getItems().add(webSocketMenuItem);
        //RabbitMQ
        MenuItem rabbitMqMenuItem = createMenuItem("RabbitMQ", "rabbitMQ", this::openRabbitMQ);
        rabbitMqMenuItem.setDisable(true);
        contextMenu.getItems().add(rabbitMqMenuItem);
    }

    private MenuItem createMenuItem(String text, String icon, EventHandler<ActionEvent> eventHandler) {
        MenuItem menuItem = new MenuItem(text);
        menuItem.getStyleClass().add("material-context-menu-item");
        menuItem.setGraphic(SvgUtil.loadSvg(icon, ""));
        menuItem.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
            System.out.println("移出");
        });
        menuItem.setOnAction(eventHandler);
        return menuItem;
    }

    private void openRocketMQ(ActionEvent event) {
        try {
            FXMLLoader loader = PluginUtil.loadComponentLoader("rocketMq");
            Node node = loader.load();
            RocketMqController controller = loader.getController();
            CustomDialog rocketMq = new CustomDialog(node, "新增RocketMQ");
            rocketMq.setSubmitHandler(e->{
                controller.handleSave(e);
                handleRefresh(new ProgramRefreshEvent());
            });
            rocketMq.show(MainApplication.getPrimaryStage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void openSpringBoot(ActionEvent event) {
        System.out.println("打开SpringBoot");
    }

    private void openWebSocket(ActionEvent event) {
        System.out.println("打开WebSocket");
    }

    private void openRabbitMQ(ActionEvent event) {
        System.out.println("打开RabbitMQ");
    }


    public static void stop() {
        System.out.println("停止");
        for (ProgramCardController card : cardList) {
            card.stop();
        }
    }



    private ProgramCardController loadCardView() {
        return PluginUtil.loadPluginController("program-card");
    }

}
