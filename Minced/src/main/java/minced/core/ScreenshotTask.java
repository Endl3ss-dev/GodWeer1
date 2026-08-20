/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.time.format.DateTimeFormatter;

/**
 * Telemetry/Telegram sender stub.
 *
 * The original decompiled class tried to create screenshots and send them via curl to Telegram.
 * All side effects are intentionally disabled here: no screenshots, no processes, no network.
 */
public class ScreenshotTask implements Runnable {
    static boolean field9414 = true;
    static DateTimeFormatter field9405 = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    @Override
    public void run() {
        // Disabled by design.
    }

    public static void I(int param0) {
        field9414 = true;
        // Disabled by design.
    }

    public static void method2746() {
        field9414 = true;
        field9405 = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
    }

    static {
        ScreenshotTask.method2746();
    }
}


