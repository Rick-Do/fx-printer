package com.printer.base.style;

import com.printer.base.enums.Align;
import lombok.Data;
import lombok.experimental.Accessors;

import java.awt.image.BufferedImage;

/**
 * @author dongyu
 * @date 2024/5/17 13:27:43
 */
@Accessors(chain = true)
@Data
public class Layout {

    private String content;

    private BufferedImage image;

    private Integer fontSize;

    private Integer fontWeight;

    private Align align;

    private Integer col;

}
