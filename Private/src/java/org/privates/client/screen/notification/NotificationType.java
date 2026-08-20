package org.privates.client.screen.notification;

public enum NotificationType {

    WARNING('a', false),
    ERROR('b', false),
    INFO('c', false),
    EFFECT_END('d', true),
    ITEM_PICKUP('e', true),
    SWAP('f', true);

    final char icon;
    final boolean customIcon;

    NotificationType(char icon, boolean customIcon) {
        this.icon = icon;
        this.customIcon = customIcon;
    }

    public boolean hasCustomIcon() {
        return customIcon;
    }
}