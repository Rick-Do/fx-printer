package com.fxprinter.controller;


import com.fxprinter.model.OptionItem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleGroup;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 * @author dongyu
 * @date 2025-07-21 17:03:39
 * @version 1.0
 * @since jdk1.8
 */
public class BasicParamController implements Initializable {

    @FXML
    public ToggleGroup darkModeGroup;

    @FXML
    public ComboBox<OptionItem> language;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxCellFactory(language);
    }

    // 通用的ComboBox单元格工厂设置方法
    private void setupComboBoxCellFactory(ComboBox<OptionItem> comboBox) {
        comboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<OptionItem> call(ListView<OptionItem> param) {
                return new ListCell<OptionItem>() {
                    @Override
                    protected void updateItem(OptionItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setDisable(false);
                        } else {
                            setText(item.getValue());

                            // 根据item的disabled属性设置单元格禁用状态
                            setDisable(item.isDisabled());

                            // 为禁用的项添加特殊样式
                            if (item.isDisabled()) {
                                getStyleClass().add("baseParam-disabled-item");
                            } else {
                                getStyleClass().remove("baseParam-disabled-item");
                            }
                        }
                    }
                };
            }
        });

        // 设置按钮单元格工厂，以便下拉按钮显示正确的值
        comboBox.setButtonCell(new ListCell<OptionItem>() {
            @Override
            protected void updateItem(OptionItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getValue());
                }
            }
        });
    }


}
