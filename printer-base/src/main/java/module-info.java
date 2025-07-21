module printer.base {
    exports com.printer.base.util;
    exports com.printer.base.model;
    exports com.printer.base.enums;
    requires cn.hutool;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires java.desktop;
    requires static lombok;
    requires com.alibaba.fastjson2;
}