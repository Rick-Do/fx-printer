package com.fxprinter.view;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.DefaultProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-16 08:57:06
 * @since jdk1.8
 */
@DefaultProperty("gifImage")
public class GifImagView extends ImageView {

    private final List<FrameData> frames = new ArrayList<>();
    private Timeline timeline;
    private int playCount = 0;
    private int currentLoop = 0;

    public GifImagView(InputStream gifStream) {
        loadGif(gifStream);
    }

    public GifImagView(String gifPath) {
        this(GifImagView.class.getResourceAsStream(gifPath));
    }

    public GifImagView() {
    }

    public void loadGif(String gifPath) {
        loadGif(GifImagView.class.getResourceAsStream(gifPath));
    }


    private void loadGif(InputStream gifStream) {
        try (ImageInputStream input = ImageIO.createImageInputStream(gifStream)) {
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
            if (!readers.hasNext()) throw new RuntimeException("No GIF reader found");

            ImageReader reader = readers.next();
            reader.setInput(input);
            int frameCount = reader.getNumImages(true);

            // 读取所有帧和延迟时间
            for (int i = 0; i < frameCount; i++) {
                BufferedImage bufferedImage = reader.read(i);
                Image frame = convertToFxImage(bufferedImage);
                int delay = getFrameDelay(reader, i);
                frames.add(new FrameData(frame, delay));
            }

            if (!frames.isEmpty()) {
                setImage(frames.get(0).image);
            }
            reader.dispose();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load GIF", e);
        }
    }

    private Image convertToFxImage(BufferedImage image) {
        if (image == null) return null;
        WritableImage result = new WritableImage(image.getWidth(), image.getHeight());
        PixelWriter writer = result.getPixelWriter();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                writer.setArgb(x, y, image.getRGB(x, y));
            }
        }
        return result;
    }

    private int getFrameDelay(ImageReader reader, int index) {
        // 从元数据获取延迟时间 (单位: 1/100秒)
        try {
            javax.imageio.metadata.IIOMetadata metadata = reader.getImageMetadata(index);
            String delayStr = metadata.getAsTree("javax_imageio_gif_image_1.0")
                    .getFirstChild()
                    .getFirstChild()
                    .getAttributes()
                    .getNamedItem("delayTime")
                    .getNodeValue();
            return Integer.parseInt(delayStr) * 10; // 转换为毫秒
        } catch (Exception e) {
            return 100; // 默认100ms
        }
    }

    public void play() {
        if (frames.isEmpty() || timeline != null) return;

        timeline = new Timeline();
        double totalDuration = 0;

        // 创建关键帧序列
        for (int i = 0; i < frames.size(); i++) {
            FrameData frame = frames.get(i);
            final int index = i;

            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(totalDuration),
                    e -> setImage(frames.get(index).image)
            );

            timeline.getKeyFrames().add(keyFrame);
            totalDuration += frame.delayMs;
        }

        // 设置循环次数
        if (playCount > 0) {
            timeline.setCycleCount(playCount);
            timeline.setOnFinished(e -> currentLoop = 0);
        } else {
            timeline.setCycleCount(Timeline.INDEFINITE);
        }

        timeline.play();
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    public void setPlayCount(int count) {
        this.playCount = Math.max(0, count);
    }

    /**
     * @param delayMs 帧显示时间(毫秒)
     */ // 帧数据结构
    private record FrameData(Image image, int delayMs) { }

    public void dispose() {
        stop();
        frames.clear();
    }


}
