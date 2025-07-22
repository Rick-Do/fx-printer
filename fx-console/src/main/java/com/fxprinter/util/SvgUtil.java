package com.fxprinter.util;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.fxprinter.model.SvgNode;
import com.fxprinter.model.SvgNodePath;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-08 21:34:26
 * @since jdk1.8
 */
public class SvgUtil {

    public static Map<String, SvgNode> svgNodeMap;

    static {
        svgNodeMap = new HashMap<>(16);
        //加载出所有的svg数据到内存中
        JSONArray svgNodes = JSON.parseArray(SvgUtil.class.getResource("/front/svg/svg.json"));
        if (svgNodes != null) {
            for (Object obj : svgNodes) {
                SvgNode svgNode = JSON.parseObject(JSON.toJSONBytes(obj), SvgNode.class);
                svgNodeMap.put(svgNode.getName(), svgNode);
            }
        }
    }


    public static Node loadSvg(String name) {
        return loadSvg(name, 3.5D, 3.5D);
    }

    public static Node loadSvg(String name, String color) {
        return loadSvg(name, 3.5D, 3.5D, color);
    }

    public static Node loadSvg(String name, double width, double height) {
        return loadSvg(name, width, height, "#1296db");
    }
    public static Node loadSvg(String name, double width, double height, String color) {
        SvgNode svgNode = svgNodeMap.get(name);
        if (svgNode == null) {
            return null;
        }
        Group group = new Group();
        for (SvgNodePath path : svgNode.getPaths()) {
            SVGPath svgPath = new SVGPath();
            svgPath.setContent(path.getContent());
            svgPath.getTransforms().add(new Scale(width/svgNode.getWidth(), height/svgNode.getHeight()));
            if (StrUtil.isEmpty( color) && StrUtil.isNotEmpty(path.getFill())) {
                svgPath.setFill(Color.valueOf(path.getFill()));
            }else if (StrUtil.isNotEmpty( color)) {
                svgPath.setFill(Color.valueOf(color));
            }

            group.getChildren().add(svgPath);
        }
        return group;
    }


}
