package com.fxprinter.model;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-11 16:30:31
 * @since jdk1.8
 */
@FunctionalInterface
public interface SingleFunction<T> {

    T apply();
}
