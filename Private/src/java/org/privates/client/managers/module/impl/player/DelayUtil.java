package org.privates.client.managers.module.impl.player;

public class DelayUtil {
    public static void addTask(long delay, Runnable task) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                task.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
