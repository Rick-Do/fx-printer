package com.fxprinter.util;


import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-07 16:42:28
 * @since jdk1.8
 */
public class FadeTransitionUtil {

    public static void applyFadeTransition(StackPane container, Node newView, int duration) {
        // 如果当前没有内容，直接添加
        if (container.getChildren().isEmpty()) {
            container.getChildren().add(newView);
            return;
        }

        Node currentView = container.getChildren().get(0);
        container.getChildren().add(newView);

        // 淡出旧视图
        FadeTransition fadeOut = new FadeTransition(Duration.millis(duration), currentView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        // 淡入新视图
        newView.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(duration), newView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeOut.setOnFinished(e -> {
            container.getChildren().remove(currentView);
            fadeIn.play();
        });

        fadeOut.play();
    }

    public static void applyFadeTransition(Node view, Integer  duration) {
        // 初始状态：透明且缩小
        view.setOpacity(0);
        view.setScaleX(1);
        view.setScaleY(1);

        // 创建组合动画（淡入 + 缩放）
        Timeline showAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(view.opacityProperty(), 0),
                        new KeyValue(view.scaleXProperty(), 1),
                        new KeyValue(view.scaleYProperty(), 1)
                ),
                new KeyFrame(Duration.millis(duration),
                        new KeyValue(view.opacityProperty(), 1),
                        new KeyValue(view.scaleXProperty(), 1),
                        new KeyValue(view.scaleYProperty(), 1)
                )
        );

        // 应用动画到弹出窗口显示时
        view.setOpacity(0); // 重置为初始状态
        showAnimation.play(); // 播放动画
    }
}
