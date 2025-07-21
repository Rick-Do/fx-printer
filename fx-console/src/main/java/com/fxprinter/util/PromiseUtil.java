package com.fxprinter.util;


import javafx.application.Platform;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-17 13:19:55
 * @since jdk1.8
 */
public class PromiseUtil {

    public static <T> void runInBackground(Supplier<T> task, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        new Thread(() -> {
            try {
                T result = task.get();
                Platform.runLater(() -> onSuccess.accept(result));
            } catch (Throwable e) {
                Platform.runLater(() -> onError.accept(e));
            }
        }).start();
    }

    public static <T> void runInBackground(Runnable task) {
        new Thread(() -> {
            try {
                task.run();
            } catch (Throwable e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }).start();
    }

    public static <T> void runInBackground(Supplier<T> task, Consumer<T> onSuccess) {
        new Thread(() -> {
            try {
                T result = task.get();
                Platform.runLater(() -> onSuccess.accept(result));
            } catch (Throwable e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }).start();
    }
}
