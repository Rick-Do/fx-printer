package com.fxprinter.state;


import com.fxprinter.model.SingleFunction;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-11 11:25:11
 * @since jdk1.8
 */
public class SettingButtonState {

    // 单例模式
    private static volatile SettingButtonState instance = null;

    private final HBox settingButton = new HBox();
    private final Button saveButton = new Button();
    private final Button cancelButton = new Button();

    private SettingButtonState() {
        settingButton.setVisible(false);
    }

    public static SettingButtonState getInstance() {
        if (instance == null) {
            synchronized (SettingButtonState.class) {
                if (instance == null) {
                    instance = new SettingButtonState();
                }
            }
        }
        return instance;
    }

    public HBox settingButtonProperty() {
        return settingButton;
    }
    public Button saveButtonProperty() {
        return saveButton;
    }

    public Button cancelButtonProperty() {
        return cancelButton;
    }

    public void changeShowState(SingleFunction<Boolean> fuc) {
        boolean change = fuc.apply();
        double duration = 500;
        boolean visible = SettingButtonState.getInstance().settingButtonProperty().isVisible();
        if (visible && change) {
            return;
        }
        SettingButtonState.getInstance().settingButtonProperty().setVisible(true);
        FadeTransition fade = new FadeTransition(Duration.millis(duration), SettingButtonState.getInstance().settingButtonProperty());
        fade.setFromValue(change ? 0.0 : 1.0);
        fade.setToValue(change ? 1.0 : 0.0);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(duration), SettingButtonState.getInstance().settingButtonProperty());
        scaleOut.setFromX(1);
        scaleOut.setFromY(1);
        scaleOut.setToX(0.8);
        scaleOut.setToY(0.8);
        ParallelTransition hideAnim = new ParallelTransition(fade, scaleOut);
        hideAnim.setOnFinished(e -> {
            if (!change) {
                SettingButtonState.getInstance().settingButtonProperty().setVisible(false);
            }
        });
        hideAnim.play();
    }


}
