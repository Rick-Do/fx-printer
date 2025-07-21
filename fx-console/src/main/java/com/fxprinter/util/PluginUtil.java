package com.fxprinter.util;


import cn.hutool.core.util.StrUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.SVGPath;

import java.util.Objects;

/**
 * 插件工具类
 * @author dongyu
 * @version 1.0
 * @date 2025-07-07 11:31:47
 * @since jdk1.8
 */
public class PluginUtil {

    /**
     * 加载插件
     * @param fxlPath fxml文件相对于resource的相对路径
     * @return 加载出来的Node节点
     */
    public static Node loadPlugin(String fxlPath) {
        if (!StrUtil.startWith(fxlPath, "/")) {
            fxlPath = "/" + fxlPath;
        }
        FXMLLoader loader = new FXMLLoader(PluginUtil.class.getResource(fxlPath));
        try {
            return loader.load();
        } catch (Exception e) {
            System.out.println("插件加载失败,失败原因：" + e.getMessage());
        }
        return null;
    }

    public static <T> T loadPluginController(String fxmPath) {
        if (!StrUtil.startWith(fxmPath, "/")) {
            fxmPath = "/" + fxmPath;
        }
        if (!StrUtil.endWith(fxmPath, ".fxml")) {
            fxmPath += ".fxml";
        }
        fxmPath = "/front/component" + fxmPath;
        FXMLLoader loader = new FXMLLoader(PluginUtil.class.getResource(fxmPath));
        try {
            loader.load();
            return loader.getController();
        } catch (Exception e) {
            System.out.println("插件加载失败,失败原因：" + e.getMessage());
        }
        return null;
    }

    /**
     * 加载组件插件
     * @param fxlPath fxml文件相对于resource/front/component的相对路径
     * @return 加载出来的Node节点
     */
    public static Node loadComponentPlugin(String fxlPath) {
        fxlPath = "/front/component/" + fxlPath;
        if (!StrUtil.endWith(fxlPath, ".fxml")) {
            fxlPath += ".fxml";
        }
        return loadPlugin(fxlPath);
    }

    public static ImageView loadImageView(String assetName) {
        ImageView imageView = new ImageView();
        imageView.setImage(new Image(Objects.requireNonNull(PluginUtil.class.getResourceAsStream("/front/assets/" + assetName))));
        return imageView;
    }

    public static ImageView loadImageView(String assetName, double width, double height) {
        ImageView imageView = loadImageView(assetName);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        return imageView;
    }

    public static Node loadSVG(String assetName) {
        SVGPath svgPath = new SVGPath();
        return svgPath;
    }

}
