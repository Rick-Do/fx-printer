package com.fx.app.util;


import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.printer.base.enums.BatchPrintEnum;
import com.printer.base.model.PrintDataDTO;
import com.printer.base.model.PrintJsonDTO;
import com.printer.base.util.CodeDrawEngine;
import com.printer.base.util.ImageUtils;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterJob;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-15 10:39:05
 * @since jdk1.8
 */
public class PrintUtil {


    public static synchronized void docPrintPaint(PrintDataDTO dataDTO, String imgPath) throws Exception {
        Optional<PrintService> printerOption =
                Arrays.stream(PrinterJob.lookupPrintServices()).filter(printerJob -> printerJob.getName().equals(dataDTO.getPrinterName())).findFirst();
        List<BufferedImage> images = createImages(dataDTO);
        for (BufferedImage image : images) {
            //是否进行压缩？
            double zipRate = 100.0 / dataDTO.getTemplateWidth();
            BufferedImage affineImage = ImageUtils.getAffineImage(image, zipRate, zipRate);
            if (printerOption.isPresent()) {
                //只有打印机匹配上才会生成图片
                exportPng(affineImage, imgPath);
                //生成图片
                Media defaultParam = getMediaSizeByPrinter(printerOption.get());
                Doc doc = getDocByImage(affineImage);
                DocPrintJob job = printerOption.get().createPrintJob();
                PrintRequestAttributeSet attributeSet = getPrintRequestAttributeSet(defaultParam, dataDTO);
                job.print(doc, attributeSet);
            }

        }
    }

    private static List<BufferedImage> createImages(PrintDataDTO dataDTO) {
        Integer templateHeight = dataDTO.getTemplateHeight();
        List<BufferedImage> images = new ArrayList<>();

        try {
            //定义一个新的
            BufferedImage bufferedImage = generateNewImage(dataDTO);
            images.add(bufferedImage);
            CodeDrawEngine drawEngine = generateNewEngine(dataDTO, bufferedImage);
            List<PrintJsonDTO> printJson = dataDTO.getPrintJson();
            //按照从上到下的位置画
            printJson.sort(Comparator.comparingInt(o -> o.getPosition().getY()));
            //定义一个起始刻度
            int currentHeight = 0;
            // 绘制标签组件
            for (PrintJsonDTO jsonDTO : printJson) {
                BatchPrintEnum batchPrintEnum = BatchPrintEnum.parseEnumByType(jsonDTO.getDataType());
                if (batchPrintEnum == null) {
                    continue;
                }
                Integer y = jsonDTO.getPosition().getY();
                y = y - currentHeight;
                if (y + jsonDTO.getHeight()  > templateHeight) {
                    //重新生成一个画板
                    BufferedImage newImage = generateNewImage(dataDTO);
                    images.add(newImage);
                    drawEngine = generateNewEngine(dataDTO, newImage);
                    currentHeight += y;
                    y = 0;
                }
                switch (batchPrintEnum) {
                    case BASIC_IMAGE:
                        drawEngine.DrawImage(jsonDTO.getSrc(),
                                CodeDrawEngine.MmToPx(jsonDTO.getPosition().getX()),
                                CodeDrawEngine.MmToPx(y),
                                CodeDrawEngine.MmToPx(jsonDTO.getWidth()),
                                CodeDrawEngine.MmToPx(jsonDTO.getHeight()));
                        break;
                    case QRCODE:
                        drawEngine.DrawQrCode(jsonDTO.getRule(),
                                CodeDrawEngine.MmToPx(jsonDTO.getPosition().getX()),
                                CodeDrawEngine.MmToPx(y),
                                CodeDrawEngine.MmToPx(jsonDTO.getWidth()),
                                CodeDrawEngine.MmToPx(jsonDTO.getHeight()));
                        break;
                    case BARCODE:
                        drawEngine.DrawBarCode(jsonDTO.getRule(),
                                CodeDrawEngine.MmToPx(jsonDTO.getPosition().getX()),
                                CodeDrawEngine.MmToPx(y),
                                CodeDrawEngine.MmToPx(jsonDTO.getWidth()),
                                CodeDrawEngine.MmToPx(jsonDTO.getHeight()));
                        break;
                    case HORIZONTAL_LINE:
                        drawEngine.DrawHorizontalLine(CodeDrawEngine.MmToPx(jsonDTO.getWidth()),
                                CodeDrawEngine.MmToPx(jsonDTO.getPosition().getX()),
                                CodeDrawEngine.MmToPx(y),
                                CodeDrawEngine.MmToPx(Integer.parseInt(jsonDTO.getStyle().getBorderWidth().replace("px", ""))));
                        break;
                    case VERTICAL_LINE:
                        drawEngine.DrawVerticalLine(CodeDrawEngine.MmToPx(jsonDTO.getHeight()),
                                CodeDrawEngine.MmToPx(jsonDTO.getPosition().getX()),
                                CodeDrawEngine.MmToPx(y),
                                CodeDrawEngine.MmToPx(Integer.parseInt(jsonDTO.getStyle().getBorderWidth().replace("px", ""))));
                        break;
                    case TABLE_CELL:
                    case BASIC_TEXT:
                        if (StrUtil.isNotEmpty(jsonDTO.getRule())) {
                            jsonDTO.setText(jsonDTO.getRule());
                        }
                        int fondSize = CodeDrawEngine.MmToPx(Integer.parseInt(jsonDTO.getStyle().getFontSize().replace("px", "")));
                        int fs = StrUtil.equalsIgnoreCase(jsonDTO.getStyle().getFontWeight(), "bolder") ? Font.BOLD : Font.PLAIN;
                        Font font = new Font("微软雅黑", fs, fondSize);
                        drawEngine.DrawContent(jsonDTO.getText(),
                                CodeDrawEngine.MmToPx(jsonDTO.getPosition().getX()),
                                CodeDrawEngine.MmToPx(y),
                                font,
                                CodeDrawEngine.MmToPx(jsonDTO.getWidth()),
                                CodeDrawEngine.MmToPx(jsonDTO.getHeight()));
                        break;
                }
            }

        } catch (Throwable ex) {
        }

        return images;
    }

    private static BufferedImage generateNewImage(PrintDataDTO dataDTO){
        Integer templateHeight = dataDTO.getTemplateHeight();
        return new BufferedImage(CodeDrawEngine.MmToPx(dataDTO.getTemplateWidth()), CodeDrawEngine.MmToPx(templateHeight), BufferedImage.TYPE_INT_RGB);
    }

    private static CodeDrawEngine generateNewEngine(PrintDataDTO dataDTO, BufferedImage image){
        Integer templateHeight = dataDTO.getTemplateHeight();
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        // 创建一个标签绘制引擎

        // 设置绘制参数
        g.setColor(Color.WHITE); // 设置背景色为白色
        g.fillRect(0, 0, CodeDrawEngine.MmToPx(dataDTO.getTemplateWidth()), CodeDrawEngine.MmToPx(templateHeight)); // 设置颜色区域大小
        g.setColor(Color.BLACK); // 表格线条的颜色
        g.setStroke(new BasicStroke(2.0f)); // 边框加粗
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 消除文本出现锯齿现象
        return new CodeDrawEngine(g);
    }

    /**
     * 获取打印机纸张参数
     * @param printer 打印服务
     * @return 打印纸张参数
     */
    private static Media getMediaSizeByPrinter(PrintService printer) {
        Media defaultParam = (Media) printer.getDefaultAttributeValue(Media.class);
        if (Objects.nonNull(defaultParam)){
            return defaultParam;
        }
        Media[] medias = (Media[]) printer.getSupportedAttributeValues(Media.class, null, null);
        for (Media media : medias) {
            if (media instanceof MediaSizeName && Objects.equals(media.toString(), "main")) {
                return media;
            }
        }
        return null;
    }


    /**
     * 设置打印参数
     * @param defaultParam 默认打印参数
     * @param dataDTO 打印数据对象
     * @return 打印参数
     */
    private static PrintRequestAttributeSet getPrintRequestAttributeSet(Media defaultParam, PrintDataDTO dataDTO) {
        PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
        boolean paramDirection = true;
        if (Objects.nonNull(defaultParam)){
            attributeSet.add(defaultParam);
            if (StrUtil.containsIgnoreCase(defaultParam.toString(), "A4")){
                paramDirection = false;
            }
        } else {
            attributeSet.add(new MediaPrintableArea(0f, 0f, 100f, 100f, MediaPrintableArea.MM));
        }
        boolean direct = dataDTO.getTemplateWidth() > dataDTO.getTemplateHeight();
        if (direct == paramDirection) {
            //相等
            attributeSet.add(OrientationRequested.PORTRAIT);
        }else {
            attributeSet.add(OrientationRequested.LANDSCAPE);
        }
        attributeSet.add(new Copies(Optional.ofNullable(dataDTO.getPrintNum()).orElse(1)));
        return attributeSet;
    }

    /**
     * 根据图片创建打印对象
     * @param image 图片
     * @return 打印对象
     * @throws IOException ImageIO转化错误
     */
    private static Doc getDocByImage(BufferedImage image) throws IOException {
        @Cleanup ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        @Cleanup ByteArrayInputStream fis = new ByteArrayInputStream(os.toByteArray());
        return new SimpleDoc(fis, DocFlavor.INPUT_STREAM.PNG, null);
    }

    private static void exportPng(BufferedImage image, String imgPath) throws IOException {
        @Cleanup ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        @Cleanup ByteArrayInputStream fis = new ByteArrayInputStream(os.toByteArray());
        ClassPathResource resource = new ClassPathResource("/");
        File dir = new File(imgPath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(imgPath + IdUtil.fastSimpleUUID() + ".png");
        @Cleanup FileOutputStream fileOutputStream = new FileOutputStream(file);
        IoUtil.copy(fis, fileOutputStream);
    }

}
