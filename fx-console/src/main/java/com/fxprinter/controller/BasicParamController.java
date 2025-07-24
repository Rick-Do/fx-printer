package com.fxprinter.controller;


import cn.hutool.core.util.StrUtil;
import com.fx.app.entity.BaseParamConfig;
import com.fx.app.entity.ProgramServerInfo;
import com.fxprinter.model.OptionItem;
import com.fxprinter.service.BaseParamConfigService;
import com.fxprinter.state.SettingButtonState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

import java.io.File;
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

    @FXML
    public TextField directoryPath;

    @FXML
    public Button browseButton;

    private BaseParamConfig config;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxCellFactory(language);
        config = BaseParamConfigService.getConfig();
        if (config == null) {
            config = new BaseParamConfig();
            //默认简体中文
            config.setLanguage("SimpleChinese");
            //默认浅色模式
            config.setDarkMode("light");
        }
        //初始化语言配置
        initLanguage();
        //初始化深色模式配置
        initDarkMode();
        directoryPath.setText(config.getFileSavePath());

        SettingButtonState.getInstance().saveButtonProperty().setOnMouseClicked(this::handleSave);
        SettingButtonState.getInstance().cancelButtonProperty().setOnMouseClicked(this::handleCancel);
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
        }
        language.setOnAction(this::handleLanguageChange);
    }

    private void handleLanguageChange(ActionEvent event) {
        String languageVal = language.getValue().getValue();
        //如果语言配置项与之前不同，则保存
        config.setLanguage(languageVal);
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


    @FXML
    private void onBrowseButtonClick(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择打印文件存储目录");

        // 设置初始目录（可选）
        if (directoryPath.getText() != null && !directoryPath.getText().isEmpty()) {
            File initialDir = new File(directoryPath.getText());
            if (initialDir.exists()) {
                directoryChooser.setInitialDirectory(initialDir);
            }
        }

        File selectedDirectory = directoryChooser.showDialog(browseButton.getScene().getWindow());

        if (selectedDirectory != null) {
            String absolutePath = selectedDirectory.getAbsolutePath();
            directoryPath.setText(absolutePath);
            SettingButtonState.getInstance().changeShowState(this::isAttitudeChange);
        }
    }

    private void handleCancel(MouseEvent mouseEvent) {
        directoryPath.setText(config.getFileSavePath());
        SettingButtonState.getInstance().changeShowState(this::isAttitudeChange);
    }

    private void handleSave(MouseEvent mouseEvent) {
        config.setFileSavePath(directoryPath.getText());
        BaseParamConfigService.saveOrUpdate(config);
        SettingButtonState.getInstance().changeShowState(this::isAttitudeChange);
    }

    private Boolean isAttitudeChange() {
        return !Objects.equals(config.getFileSavePath(), directoryPath.getText());
    }



}
