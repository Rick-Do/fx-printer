package com.fxprinter.controller;


import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.fxprinter.util.SvgUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javax.print.PrintService;
import java.awt.print.PrinterJob;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-11 16:54:24
 * @since jdk1.8
 */
public class PrinterController implements Initializable {

    @FXML
    public Button refresh;

    @FXML
    public FlowPane flowPane;

    private static List<PrintService> printerList = new ArrayList<>();

    @FXML
    public ScrollPane stackPaneContainer;

    @FXML
    public TextField filerText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refresh.setGraphic(SvgUtil.loadSvg("refresh", 2.5D, 2.5D, "#626262"));
        stackPaneContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        flowPane.minWidthProperty().bind(stackPaneContainer.widthProperty());
        initPrinterList();
        for (PrintService printService : printerList) {
            VBox card = createCard(printService);
            flowPane.getChildren().add(card);
        }

        refresh.setOnMouseClicked(this::handleRefresh);
        filerText.textProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(()->{
                flowPane.getChildren().clear();
                if (StrUtil.isNotEmpty(newValue)) {
                    for (PrintService printService : printerList) {
                        if (!StrUtil.contains(printService.getName(), newValue)) {
                            continue;
                        }
                        VBox card = createCard(printService);
                        flowPane.getChildren().add(card);
                    }
                }else {
                    flowPane.getChildren().clear();
                    flowPane.getChildren().addAll(printerList.stream().map(this::createCard).toList());
                }
            });

        });
    }

    private void handleRefresh(MouseEvent mouseEvent) {
        initPrinterList();
        Platform.runLater(() -> {
            flowPane.getChildren().clear();
            for (PrintService printService : printerList) {
                VBox card = createCard(printService);
                flowPane.getChildren().add(card);
            }
        });
    }

    private void initPrinterList() {

        PrintService[] printServices = PrinterJob.lookupPrintServices();
        printerList = ListUtil.toList(printServices);
    }

    private VBox createCard(PrintService printService) {
        VBox card = new VBox();
        card.setAlignment(Pos.TOP_CENTER);
        card.setSpacing(10);
        card.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        //线条
        card.setBorder(new Border(new BorderStroke(
                Color.gray(0.1),
                BorderStrokeStyle.SOLID,
                new CornerRadii(4),
                BorderWidths.DEFAULT
        )));
        card.getStyleClass().add("printer-card");
        card.setPrefSize(200, 200);
        card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        // 1. 图片区域
        Region imageSection = createImageSection(printService);

        // 2. 文字区域
        Region textSection = createTextSection(printService);

        card.getChildren().addAll(imageSection, textSection);
        return card;
    }

    private Region createTextSection(PrintService printService) {
        VBox textSection = new VBox();
        textSection.setAlignment(Pos.CENTER);
        textSection.setSpacing(0);
        textSection.getChildren().add(new Text(printService.getName()));
        return textSection;
    }

    private Region createImageSection(PrintService printService) {
        Node node = SvgUtil.loadSvg("printer", 15D, 15D, "#626262");
        StackPane stackPane = new StackPane(node);
        stackPane.setAlignment(Pos.CENTER);
        stackPane.setPrefSize(100, 100);
        return stackPane;
    }
}
