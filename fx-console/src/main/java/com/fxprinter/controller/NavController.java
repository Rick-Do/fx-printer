package com.fxprinter.controller;


import com.fxprinter.util.SvgUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Setter;

import java.net.URL;
import java.util.*;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-07 16:39:51
 * @since jdk1.8
 */
public class NavController implements Initializable {

    @FXML
    public TreeView<Label> navTree;

    @Setter
    private MainController mainController;

    private static final Map<String, Label> navMap = new LinkedHashMap<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initNavMap();
        navTree.setRoot(new TreeItem<>(new Label("导航")));
        navTree.setShowRoot(false);
        List< Label> labels = new ArrayList<>(navMap.values());
        TreeItem<Label> first = null;
        for (Label label : labels) {
            label.getStyleClass().add("nav-button");
            TreeItem<Label> treeItem = new TreeItem<>(label);
            navTree.getRoot().getChildren().add(treeItem);
            if (first == null) {
                first = treeItem;
            }
        }
        navTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && mainController != null) {
                mainController.loadView(newValue.getValue().getId() );
            }
        });
        TreeItem<Label> finalFirst = first;
        Platform.runLater(()-> navTree.getSelectionModel().select(finalFirst));
    }

    private void initNavMap() {

        //服务信息
        Label serviceLabel = new Label("程序服务");
        serviceLabel.setId("program");
        serviceLabel.setGraphic(SvgUtil.loadSvg("program"));
        navMap.put("program", serviceLabel);
        //打印
        Label printerLabel = new Label("打印机");
        printerLabel.setId("printer");
        printerLabel.setGraphic(SvgUtil.loadSvg("printer"));
        navMap.put("printer", printerLabel);

        //文件
        Label fileLabel = new Label("我的文件");
        fileLabel.setId("file");
        fileLabel.setGraphic(SvgUtil.loadSvg("file"));
        navMap.put("file", fileLabel);
        //设置
        Label settingLabel = new Label("系统设置");
        settingLabel.setId("setting");
        settingLabel.setGraphic(SvgUtil.loadSvg("setting"));
        navMap.put("setting", settingLabel);
    }

}
