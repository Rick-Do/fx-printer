package com.fxprinter;

import com.fxprinter.controller.ProgramController;
import com.fxprinter.util.DbUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {

    private Stage primaryStage;

    private final String title = "FxPrinter";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/front/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 980, 700);
        stage.setTitle(title);
        //添加图标
        Image image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("/front/icon.png")));
        stage.getIcons().add(image);
        stage.setScene(scene);
        this.primaryStage = stage;
        setupSystemTray(); // 初始化系统托盘
        stage.setOnCloseRequest(event -> {
            Platform.setImplicitExit(false);
            primaryStage.hide();
            event.consume(); // 阻止默认关闭行为

        });
        stage.show();
    }

    private void setupSystemTray() {
        if (!SystemTray.isSupported()) {
            System.out.println("System tray not supported!");
            return;
        }

        SystemTray tray = SystemTray.getSystemTray();

        PopupMenu popup = new PopupMenu();
        MenuItem openItem = new MenuItem("Open");
        openItem.addActionListener(e -> showStage());
        popup.add(openItem);
        popup.addSeparator();


        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> {
            Platform.exit();
            System.exit(0);
        });

        popup.add(exitItem);
        java.awt.Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/front/icon.png"));
        TrayIcon trayIcon = new TrayIcon(image, primaryStage.getTitle(), popup);
        trayIcon.setImageAutoSize(true);

        // 双击托盘图标恢复窗口
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                  showStage();
                }
            }
        });

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println("无法添加托盘图标: " + e);
        }
    }

    private void showStage() {
        Platform.runLater(() -> {
            // 确保窗口已显示
            if (primaryStage.isIconified()) {
                primaryStage.setIconified(false);
            }

            if (!primaryStage.isShowing()) {
                primaryStage.show();
            }

            // 将窗口置于前端
            primaryStage.toFront();
            primaryStage.requestFocus();
        });
    }

    @Override
    public void init() {
        DbUtil.initDatabase();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("调用了stop方法");
        ProgramController.stop();
        //最后再关闭连接
        DbUtil.closeDataSource();
    }

    public static void main(String[] args) {
        launch(args);
    }
}