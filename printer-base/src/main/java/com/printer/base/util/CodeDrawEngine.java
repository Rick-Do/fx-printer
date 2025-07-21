package com.printer.base.util;

import cn.hutool.core.util.StrUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 标签绘制引擎
 */
public class CodeDrawEngine {
    private Graphics2D g;

    public CodeDrawEngine(Graphics2D g) {
        this.g = g;
    }

    /**
     * 毫米转像素
     * @param mm 毫米
     * @return
     */
    public static int MmToPx(int mm)
    {
        return (int)((mm / 25.4d) * 300);
    }
    
    /// <summary>
    /// 打竖线
    /// </summary>
    /// <param name="lineHeight">高度</param>
    /// <param name="lineStartX">开始位置X</param>
    /// <param name="lineStartY">开始位置Y</param>
    /// <param name="lineWidth"></param>
    public void DrawVerticalLine(int lineHeight, int lineStartX, int lineStartY, int lineWidth) {
        this.g.setColor(Color.BLACK);
        this.g.fillRect(lineStartX, lineStartY, lineWidth, lineHeight);
    }

    /// <summary>
    /// 打横线
    /// </summary>
    /// <param name="lineHeight">高度</param>
    /// <param name="lineStartX">开始位置X</param>
    /// <param name="lineStartY">开始位置Y</param>
    /// <param name="lineWidth"></param>
    public void DrawHorizontalLine(int lineLength, int lineStartX, int lineStartY, int lineWidth) {
        this.g.setColor(Color.BLACK);
        this.g.fillRect(lineStartX, lineStartY, lineLength, lineWidth);
    }

    /// <summary>
    /// 打印内容
    /// </summary>
    /// <param name="content"></param>
    /// <param name="x"></param>
    /// <param name="y"></param>
    /// <param name="width"></param>
    /// <param name="height"></param>
    public void DrawContent(String content, int x, int y, Font font, int width, int height)
    {
        if (StrUtil.isEmpty(content)){
            return;
        }
        if (font != null){
            g.setFont(font);
        }
        //获取画笔的字体
        Font gFont = g.getFont();
        //通过JLabel获取文本的总长度和总高度
        JLabel jLabel = new JLabel(content);
        FontMetrics fontMetrics = jLabel.getFontMetrics(gFont);
        int texLen = content.length();
        int totalWidth = fontMetrics.stringWidth(content);
        int textHeight = fontMetrics.getHeight();
        int totalHeight = 0;
        if (totalWidth > width) {
            //已经写了多少行
           int alreadyWriteLine = 0;
            //目前一行写的长度
            int nowWidth = 0;
            for (int i = 0; i < texLen; i++) {
                //获取单个字符
                int oneWordWidth = fontMetrics.charWidth(content.charAt(i));
                int tmpWidth = oneWordWidth + nowWidth;
                if (tmpWidth > width) {
                    //如果超过了总长度，要进行换行
                    nowWidth = 0;
                    alreadyWriteLine ++;
                    //每行间距为2
                    int writeY = y + alreadyWriteLine * (textHeight + 1);
                    g.drawString(content.charAt(i) + "", x + nowWidth, writeY+fontMetrics.getAscent());
                    nowWidth = oneWordWidth;
                }else {
                    //当前长度足够，可以直接画
                    int writeY = y + alreadyWriteLine * (textHeight + 1);
                    g.drawString(content.charAt(i) + "", x + nowWidth, writeY+fontMetrics.getAscent());
                    nowWidth = tmpWidth;
                }
            }
            totalHeight = (alreadyWriteLine + 1) * textHeight;
        }else {
            //长度足够，直接画
            g.drawString(content, x, y+fontMetrics.getAscent());
            totalHeight = textHeight;
        }

        if (totalHeight > height) {
            //清除超过的区域
            /*Color raw = g.getColor();
            g.setColor(Color.white);
            int fillHeight = totalHeight - height;
            g.drawRect(x, y + height, width, fillHeight);
            g.setColor(raw);*/
            BufferedImage image = new BufferedImage(width, totalHeight - height, BufferedImage.TYPE_INT_RGB);
            Graphics imgG = image.getGraphics();
            imgG.setColor(Color.WHITE);
            imgG.fillRect(0, 0, image.getWidth(), image.getHeight());
            g.drawImage(image, x, y+height, Color.WHITE, null);
        }


    }

    /// <summary>
    /// 画条形码
    /// </summary>
    /// <param name="serialNum">条形码数据</param>
    /// <param name="left">指定条形码x</param>
    /// <param name="top">指定条形码y</param>
    /// <param name="width">指定条形码宽度</param>
    /// <param name="height">指定条形码高度</param>
    public void DrawBarCode(String barCodeData, int left, int top, int width, int height) throws WriterException {
        if (StrUtil.isNotEmpty(barCodeData))
        {
            BufferedImage image = getBarCode(barCodeData, width, height);
            this.g.drawImage(image, left, top, null);//画条形码
        }
    }

    /// <summary>
    /// 画二维码
    /// </summary>
    /// <param name="serialNum">二维码数据</param>
    /// <param name="left">指定二维码x</param>
    /// <param name="top">指定二维码y</param>
    /// <param name="width">指定二维码宽度</param>
    /// <param name="height">指定二维码高度</param>
    public void DrawQrCode(String QrCodeData, int left, int top, int width, int height) throws WriterException {
        if (StrUtil.isNotEmpty(QrCodeData)) {
            BufferedImage image = getQrCode(QrCodeData, width, height);
            this.g.drawImage(image, left, top, null);//画二维码
        }
    }

    /// <summary>
    /// 画图片
    /// </summary>
    /// <param name="imageUrl">远程图片地址</param>
    /// <param name="x">坐标x</param>
    /// <param name="y">坐标y</param>
    /// <param name="width">图片宽度</param>
    /// <param name="height">图片高度</param>
    public void DrawImage(String imageUrl, int x, int y, int width, int height) throws IOException {
        g.setPaintMode();
        if (StrUtil.isNotEmpty(imageUrl)) {
            URL url = new URL(imageUrl);
            BufferedImage image = ImageIO.read(url);
            BufferedImage affineImage = ImageUtils.getAffineImage(image, 1.0 * width / image.getWidth(), 1.0 * height / image.getHeight());
            this.g.drawImage(affineImage, x, y, null);//画图
        }
    }

    //FRONT_COLOR：二维码前景色，0x000000 表示黑色
    private static final int FRONT_COLOR = 0x000000;
    //BACKGROUND_COLOR：二维码背景色，0xFFFFFF 表示白色
    //演示用 16 进制表示，和前端页面 CSS 的取色是一样的，注意前后景颜色应该对比明显，如常见的黑白
    private static final int BACKGROUND_COLOR = 0xFFFFFF;

    //核心代码-生成二维码
    private BufferedImage getQrCode(String content, int width, int hight) throws WriterException {
        g.setPaintMode();
        //com.google.zxing.EncodeHintType：编码提示类型,枚举类型
        Map<EncodeHintType, Object> hints = new HashMap();

        //EncodeHintType.CHARACTER_SET：设置字符编码类型
//        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        //EncodeHintType.ERROR_CORRECTION：设置误差校正
        //ErrorCorrectionLevel：误差校正等级，L = ~7% correction、M = ~15% correction、Q = ~25% correction、H = ~30% correction
        //不设置时，默认为 L 等级，等级不一样，生成的图案不同，但扫描的结果是一样的
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);

        //EncodeHintType.MARGIN：设置二维码边距，单位像素，值越小，二维码距离四周越近
        hints.put(EncodeHintType.MARGIN, 1);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, width, hight, hints);
        BufferedImage bufferedImage = new BufferedImage(width, hight, BufferedImage.TYPE_INT_BGR);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < hight; y++) {
                bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? FRONT_COLOR : BACKGROUND_COLOR);
            }
        }
        return bufferedImage;
    }

    /**
     * 设置 条形码参数
     */
    private Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {
        private static final long serialVersionUID = 1L;
        {
            // 设置编码方式
            put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 容错级别 这里选择最高H级别
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            put(EncodeHintType.MARGIN, 1);
        }
    };

    /**
     * 生成 图片缓冲
     *
     * @param vaNumber VA 码
     * @return 返回BufferedImage
     * @author fxbin
     */
    public BufferedImage getBarCode(String vaNumber, int width, int hight) throws WriterException {
        Code128Writer writer = new Code128Writer();
        // 编码内容, 编码类型, 宽度, 高度, 设置参数
        BitMatrix bitMatrix = writer.encode(vaNumber, BarcodeFormat.CODE_128, width, hight, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * 画表格单元
     * @param text 表格内容
     * @param x x轴坐标
     * @param y y轴坐标
     * @param font 字体
     * @param width 宽度
     * @param height 高度
     */
    public void drawTableCell(String text, int x, int y, Font font, int width, int height) {

        //画文字
        DrawContent(text, x , y , font, width , height);
    }
}
