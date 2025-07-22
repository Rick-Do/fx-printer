module com.fxprinter {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.zaxxer.hikari;
    requires org.mybatis;
    requires com.baomidou.mybatis.plus.annotation;
    requires com.baomidou.mybatis.plus.core;
    requires java.sql;
    requires com.baomidou.mybatis.plus.extension;
    requires spring.core;
    requires org.mybatis.spring;
    requires rocketmq.client;
    requires rocketmq.remoting;
    requires rocketmq.acl;
    requires fx.app;
    requires printer.base;
    requires cn.hutool;
    requires static lombok;
    requires fastjson;
    requires rocketmq.common;
    requires com.alibaba.fastjson2;
    requires spring.jdbc;
    requires org.checkerframework.checker.qual;
    requires java.desktop;
    requires com.google.common;


    opens com.fxprinter to javafx.fxml;
    opens com.fxprinter.controller to javafx.fxml;
    opens com.fxprinter.view to javafx.fxml;
    exports com.fxprinter;
    exports com.fxprinter.model;
    exports com.fxprinter.event;
    exports com.fxprinter.controller;
    exports com.fxprinter.view;
}