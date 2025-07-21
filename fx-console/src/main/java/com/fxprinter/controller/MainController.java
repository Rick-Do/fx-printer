package com.fxprinter.controller;


import com.fxprinter.util.FadeTransitionUtil;
import com.fxprinter.util.PluginUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 主容器
 * @author dongyu
 * @version 1.0
 * @date 2025-07-07 10:59:46
 * @since jdk1.8
 */
public class MainController implements Initializable {

    @FXML
    private StackPane contentPane;

    @FXML
    private NavController navContentController;

    public void loadView(String fxmlFile) {
        // 加载新视图
        Node newView = PluginUtil.loadComponentPlugin(fxmlFile);
        // 淡入淡出切换
        FadeTransitionUtil.applyFadeTransition(contentPane, newView, 200);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navContentController.setMainController(this);
    }
}
