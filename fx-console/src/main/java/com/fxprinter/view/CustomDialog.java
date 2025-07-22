package com.fxprinter.view;


import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Getter;
import lombok.Setter;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-22 13:06:00
 * @since jdk1.8
 */
public class CustomDialog extends StackPane {
    private final BorderPane dialogLayout = new BorderPane();
    private final Pane backgroundLayer = new Pane();
    private Node centerContent;

    @Getter
    @Setter
    private EventHandler<ActionEvent> submitHandler;

    @Getter
    @Setter
    private String titleText = "提示";

    @Getter
    @Setter
    private Double ratio = 0.8D;


    private boolean showCloseButton = true;
    private Node bottomContent;
    private ScrollPane dialogScrollPane;

    public CustomDialog(Node centerNode) {
        this.centerContent = centerNode;
        initialize();
    }

    public CustomDialog(Node centerNode, String title) {
        this.centerContent = centerNode;
        this.titleText = title;
        initialize();
    }

    public CustomDialog(Node centerNode, String title, boolean showCloseButton) {
        this.centerContent = centerNode;
        this.titleText = title;
        this.showCloseButton = showCloseButton;
        initialize();
    }

    private void initialize() {
        // 1. 创建半透明背景层（点击退出）
        backgroundLayer.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 0.3), CornerRadii.EMPTY, Insets.EMPTY)));
        backgroundLayer.setOnMouseClicked(e -> close());
        backgroundLayer.setPickOnBounds(true);

        // 2. 创建标题栏（顶部区域）
        createTopRegion();

        // 3. 设置中间内容区域
        setupCenterRegion();

        // 4. 创建滚动容器（包裹整个dialogLayout）
        createScrollContainer();
        createButton();
        // 5. 构建布局
        getChildren().addAll(backgroundLayer, dialogScrollPane);
        getStyleClass().add("CustomDialog-root");
    }

    private void createButton() {
        Button closeButton = new Button("取消");
        closeButton.getStyleClass().add("button-cancel");
        closeButton.setOnAction(e -> close());
        Button saveButton = new Button("提交");
        saveButton.getStyleClass().add("button-save");
        saveButton.setOnAction(e->{
            if (submitHandler != null) {
                submitHandler.handle( e);
            }
            close();
        });
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(5);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getStyleClass().add("button-box");
        buttonBox.getChildren().addAll(closeButton, saveButton);
        dialogLayout.setBottom(buttonBox);
    }

    private void createTopRegion() {
        // 创建标题标签
        Label titleLabel = new Label(titleText);
        titleLabel.setFont(Font.font(16));
        titleLabel.getStyleClass().add("CustomDialog-title");

        // 创建关闭按钮
        Button closeButton = new Button("×");
        closeButton.setFont(Font.font(20));
        closeButton.getStyleClass().add("CustomDialog-close-button");
        closeButton.setOnAction(e -> close());
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(showCloseButton);

        // 创建顶部容器
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPrefHeight(40);
        topBar.getStyleClass().add("CustomDialog-top");

        // 添加组件
        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        topBar.getChildren().addAll(leftSpacer, titleLabel, rightSpacer, closeButton);
        topBar.setPadding(new Insets(0, 10, 0, 20));

        dialogLayout.setTop(topBar);
    }

    private void setupCenterRegion() {
        // 设置中心内容样式
        centerContent.getStyleClass().add("CustomDialog-center");

        // 防止点击中心区域关闭弹窗
        centerContent.setOnMouseClicked(Event::consume);

        // 设置内容区域内边距
        if (centerContent instanceof Pane) {
            ((Pane) centerContent).setPadding(new Insets(20));
        } else {
            StackPane wrapper = new StackPane(centerContent);
            wrapper.setPadding(new Insets(20));
            wrapper.getStyleClass().add("CustomDialog-center");
            centerContent = wrapper;
        }

        dialogLayout.setCenter(centerContent);
    }

    private void createScrollContainer() {
        // 创建滚动容器（包裹整个弹窗内容）
        dialogScrollPane = new ScrollPane();
        dialogScrollPane.setContent(dialogLayout);
        dialogScrollPane.setFitToWidth(true);
        dialogScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dialogScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dialogScrollPane.getStyleClass().add("CustomDialog-scroll-pane");

        // 设置弹窗样式
        dialogLayout.getStyleClass().add("CustomDialog-container");

        // 设置滚动容器的最大尺寸限制
        dialogScrollPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    }

    public void setBottomContent(Node bottomNode) {
        removeBottomContent();
        this.bottomContent = bottomNode;
        bottomContent.getStyleClass().add("CustomDialog-bottom");
        dialogLayout.setBottom(bottomContent);
    }

    public void removeBottomContent() {
        dialogLayout.setBottom(null);
        this.bottomContent = null;
    }

    public void show(Window ownerWindow) {
        if (ownerWindow instanceof Stage stage) {

            StackPane rootPane = (StackPane) stage.getScene().getRoot();
            rootPane.getChildren().add(this);
            bindSizeProperties(stage);
            // 监听窗口大小变化
            this.setVisible(false);
            Platform.runLater(()->{
                // 确保在前台
                calculate(stage);
                this.setVisible(true);
                toFront();
            });
        }
    }

    private void bindSizeProperties(Stage stage) {
        // 监听窗口大小变化
        ChangeListener<Number> sizeListener = (obs, oldVal, newVal) -> {
            if (newVal == null) {
                return;
            }
            // 背景层铺满窗口
            backgroundLayer.setPrefSize(stage.getWidth(), stage.getHeight());

            calculate(stage);
        };

        // 绑定窗口属性
        stage.widthProperty().addListener(sizeListener);
        stage.heightProperty().addListener(sizeListener);
        sizeListener.changed(null, null, null); // 初始调用
    }

    private void calculate(Stage stage) {
        // 计算可用高度（窗口高度的90%）
        double availableHeight = stage.getHeight() * ratio;
        dialogScrollPane.setPrefHeight(availableHeight);

        // 计算对话框的理想高度
       /* double prefHeight = calculateDialogHeight();

        // 根据可用空间设置滚动容器的尺寸
        if (prefHeight > availableHeight) {
            // 总高度超过可用空间，启用滚动
            System.out.println("高度超过");
        } else {
            System.out.println("高度小于");
            // 高度小于可用空间，自动使用内容高度
            dialogScrollPane.setPrefHeight(prefHeight);
        }*/

        // 设置最大宽度（窗口宽度的90%）
        dialogScrollPane.setPrefWidth(stage.getWidth() * ratio);

        // 居中显示
        setAlignment(dialogScrollPane, Pos.CENTER);
    }

    private double calculateDialogHeight() {
        double height = 0;

        // 添加顶部高度
        if (dialogLayout.getTop() != null) {
            height += dialogLayout.getTop().prefHeight(-1);
        }

        // 添加中间高度（需要考虑内边距）
        if (dialogLayout.getCenter() != null) {
            Node center = dialogLayout.getCenter();
            height += center.prefHeight(-1);

            // 添加内边距
            if (center instanceof Region) {
                Insets padding = ((Region) center).getPadding();
                height += padding.getTop() + padding.getBottom();
            }
        }

        // 添加底部高度
        if (dialogLayout.getBottom() != null) {
            height += dialogLayout.getBottom().prefHeight(-1);
        }

        return height + 20; // 添加额外缓冲空间
    }

    public void close() {
        // 从父容器移除
        if (getParent() != null) {
            ((Pane) getParent()).getChildren().remove(this);
        }
    }

    // ============== API方法 ==============
    public void setTitle(String title) {
        this.titleText = title;
        // 找到标题标签并更新
        Node top = dialogLayout.getTop();
        if (top instanceof HBox) {
            for (Node node : ((HBox) top).getChildren()) {
                if (node instanceof Label) {
                    ((Label) node).setText(title);
                    break;
                }
            }
        }
    }

    public void setCenterContent(Node centerNode) {
        this.centerContent = centerNode;
        centerContent.getStyleClass().add("CustomDialog-center");
        dialogLayout.setCenter(centerContent);
    }

    public void setShowCloseButton(boolean visible) {
        this.showCloseButton = visible;
        Node top = dialogLayout.getTop();
        if (top instanceof HBox) {
            for (Node node : ((HBox) top).getChildren()) {
                if (node instanceof Button) {
                    node.setVisible(visible);
                    break;
                }
            }
        }
    }

    // 便捷显示方法
    public static void showDialog(Node content, Window owner) {
        new CustomDialog(content).show(owner);
    }

    public static void showDialog(String title, Node content, Window owner) {
        new CustomDialog(content, title).show(owner);
    }

    public static void showDialog(String title, Node content, boolean showCloseButton, Window owner) {
        new CustomDialog(content, title, showCloseButton).show(owner);
    }

}
