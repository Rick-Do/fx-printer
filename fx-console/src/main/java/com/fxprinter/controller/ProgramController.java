package com.fxprinter.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fx.app.entity.ProgramServerInfo;
import com.fxprinter.service.ProgramServerInfoService;
import com.fxprinter.util.PluginUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import lombok.Getter;

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

    private static ContextMenu contextMenu;

    @FXML
    public FlowPane flowPane;

    @FXML
    public ScrollPane stackPaneContainer;

    @Getter
    private static final List<ProgramCardController> cardList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //获取配置
        List<ProgramServerInfo> serverInfos = ProgramServerInfoService.list(new LambdaQueryWrapper<>());
        if (serverInfos == null) {
            return;
        }
        for (ProgramServerInfo serverInfo : serverInfos) {
            ProgramCardController controller = loadCardView();
            if (controller != null) {
                controller.setServerInfo(serverInfo);
                controller.init();
                flowPane.getChildren().add(controller.getRoot());
            }
            cardList.add(controller);
        }
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
