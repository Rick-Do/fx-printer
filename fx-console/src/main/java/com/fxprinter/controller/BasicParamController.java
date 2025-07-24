package com.fxprinter.controller;


import cn.hutool.core.util.StrUtil;
import com.fx.app.entity.BaseParamConfig;
import com.fx.app.entity.ProgramServerInfo;
import com.fxprinter.model.OptionItem;
import com.fxprinter.service.BaseParamConfigService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
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

    private BaseParamConfig config;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxCellFactory(language);
        config = BaseParamConfigService.getConfig();
        if (config == null) {
            config = new BaseParamConfig();
        }
        //初始化语言配置
        initLanguage();
        //初始化深色模式配置
        initDarkMode();
    }

    private void initDarkMode() {
        String darkMode = config.getDarkMode();
        if (StrUtil.isNotEmpty(darkMode)) {
            Optional<Toggle> first = darkModeGroup.getToggles().stream().filter(toggle -> Objects.equals(toggle.getUserData().toString(), darkMode)).findFirst();
            first.ifPresent(toggle -> darkModeGroup.selectToggle(toggle));
        }
        darkModeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                config.setDarkMode(newValue.getUserData().toString());
                BaseParamConfigService.saveOrUpdate(config);
            }
        });
    }

    private void initLanguage() {
        if (StrUtil.isNotEmpty(config.getLanguage())) {
            Optional<OptionItem> first = language.getItems().stream().filter(item -> item.getValue().equals(config.getLanguage())).findFirst();
            first.ifPresent(optionItem -> language.getSelectionModel().select(optionItem));
        }else {
            language.getSelectionModel().select(0);
        }
        language.setOnAction(this::handleLanguageChange);
    }

    private void handleLanguageChange(ActionEvent event) {
        config.setLanguage(language.getValue().getValue());
        BaseParamConfigService.saveOrUpdate(config);
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
                            if (StrUtil.isNotEmpty(item.getLabel())) {
                                setText(item.getLabel());
                            }else {
                                setText(item.getValue());
                            }

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
                    if (StrUtil.isNotEmpty(item.getLabel())) {
                        setText(item.getLabel());
                    } else {
                        setText(item.getValue());
                    }
                }
            }
        });
    }


}
