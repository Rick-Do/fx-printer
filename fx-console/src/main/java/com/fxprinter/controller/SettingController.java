package com.fxprinter.controller;


import com.fxprinter.state.SettingButtonState;
import com.fxprinter.util.FadeTransitionUtil;
import com.fxprinter.util.PluginUtil;
import com.fxprinter.util.SvgUtil;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

/**
 * 设置页面控制器
 * @author dongyu
 * @version 1.0
 * @date 2025-07-08 13:11:40
 * @since jdk1.8
 */
public class SettingController implements Initializable {

    @FXML
    public ToggleButton basicParam;


    @FXML
    public HBox menu;

    @FXML
    public ToggleButton serialization;

    @FXML
    public StackPane contentPage;

    private final ToggleGroup menuGroup = new ToggleGroup();

    @FXML
    public ScrollPane mainSettingScrollPane;

    @FXML
    public Button saveButton;

    @FXML
    public Button cancelButton;

    @FXML
    @Getter
    private HBox buttonContainer;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 设置导航按钮组
        initMenuAndIcon();
        //滚动监听
        scrollPaneListener();
        initButtonEvent();
        //事件加载完成后的事件
        Platform.runLater(this::buttonContainerChange);

    }

    private void initButtonEvent() {
        //添加按钮图标
        saveButton.setGraphic(SvgUtil.loadSvg("save", 3D, 3D, "#ffffff"));
        cancelButton.setGraphic(SvgUtil.loadSvg("cancel", 3D, 3D, "#000000"));
        buttonContainer.opacityProperty().bind(SettingButtonState.getInstance().settingButtonProperty().opacityProperty());
        buttonContainer.visibleProperty().bind(SettingButtonState.getInstance().settingButtonProperty().visibleProperty());
        saveButton.onMouseClickedProperty().bind(SettingButtonState.getInstance().saveButtonProperty().onMouseClickedProperty());
        cancelButton.onMouseClickedProperty().bind(SettingButtonState.getInstance().cancelButtonProperty().onMouseClickedProperty());

    }


    /**
     * 初始化导航按钮组
     */
    private void initMenuAndIcon() {
        menu.getChildren().filtered(node -> node instanceof ToggleButton)
                .forEach(btn -> ((ToggleButton)btn).setToggleGroup(menuGroup));


        //初始化图标
        basicParam.setGraphic(SvgUtil.loadSvg("base"));
        serialization.setGraphic(SvgUtil.loadSvg("serialization"));
        menuGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String fxmlFile = ((ToggleButton) newValue).getId() + ".fxml";
                CompletableFuture.runAsync(()->{
                    //切换隐藏按钮
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(200));
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.setOnFinished(e -> SettingButtonState.getInstance().settingButtonProperty().setVisible(false));
                    fadeOut.play();
                });
                loadContentView(fxmlFile, 200);
            }
        });
        //初始化内容数据
        Toggle toggle = menuGroup.getToggles().get(0);
        menuGroup.selectToggle(toggle);
        loadContentView(((ToggleButton) toggle).getId() + ".fxml", 200);
    }

    /**
     * 滚动监听
     */
    private void scrollPaneListener() {
        mainSettingScrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            double sensitivityFactor = 3.3;
            // 修改垂直滚动增量
            if (Math.abs(event.getDeltaY()) > 0) {
                event.consume(); // 阻止事件继续传递

                // 计算新的滚动位置
                double deltaY = event.getDeltaY() * sensitivityFactor;
                mainSettingScrollPane.setVvalue(mainSettingScrollPane.getVvalue() - deltaY / mainSettingScrollPane.getContent().getBoundsInLocal().getHeight());
            }

            // 修改水平滚动增量（如果需要）
            if (Math.abs(event.getDeltaX()) > 0) {
                event.consume();
                double deltaX = event.getDeltaX() * sensitivityFactor;
                mainSettingScrollPane.setHvalue(mainSettingScrollPane.getHvalue() - deltaX / mainSettingScrollPane.getContent().getBoundsInLocal().getWidth());
            }
        });

        mainSettingScrollPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                buttonContainerChange();
            }
        });

    }

    /**
     * 保存和取消按钮容器位置
     */
    private void buttonContainerChange() {
        double height = mainSettingScrollPane.getHeight();
        double layoutX = mainSettingScrollPane.getLayoutX();
        double layoutY = mainSettingScrollPane.getLayoutY();
        buttonContainer.setLayoutX(layoutX  + 10);
        buttonContainer.setLayoutY(layoutY + height - 70);
    }


    /**
     * 加载内容视图
     * @param fxmlFile fxml文件
     * @param duration 动画时间
     */
    public void loadContentView(String fxmlFile, Integer duration) {
        // 加载新视图
        Node newView = PluginUtil.loadComponentPlugin(fxmlFile);
        // 淡入淡出切换
        FadeTransitionUtil.applyFadeTransition(contentPage, newView, duration);
    }

}
