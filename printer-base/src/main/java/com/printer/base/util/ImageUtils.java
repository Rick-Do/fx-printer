package com.printer.base.util;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImageUtils {
    public static BufferedImage getAffineImage(BufferedImage image, double wRatio, double hRatio) {
        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wRatio, hRatio), null);
        return ato.filter(image, null);
    }
}
