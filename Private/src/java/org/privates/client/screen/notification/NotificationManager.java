package org.privates.client.screen.notification;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class NotificationManager {

    private final ArrayList<Notification> notifications = new ArrayList<>();

    public void add(Notification notification) {
        notifications.add(notification);
    }

    public void update() {
        notifications.removeIf(notification ->
                System.currentTimeMillis() - notification.getTime() > notification.getMs()
        );
    }
}