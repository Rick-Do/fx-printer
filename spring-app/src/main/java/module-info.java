module fx.app {
    exports com.fx.app.util;
    exports com.fx.app.entity;
    exports com.fx.app.mapper;
    requires cn.hutool;
    requires fastjson;
    requires java.desktop;
    requires static lombok;
    requires spring.beans;
    requires spring.boot.autoconfigure;
    requires printer.base;
    requires com.baomidou.mybatis.plus.annotation;
    requires com.baomidou.mybatis.plus.core;

    opens com.fx.app.entity to com.baomidou.mybatis.plus.core;
    exports com.fx.app.enums;
}