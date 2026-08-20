package org.privates.client.managers.events.player;


import net.minecraft.forge.api.Event;

public class EventKeyboardMouse extends Event {
    private int key;

    public EventKeyboardMouse(int key) {
        this.key = key;
    }

    public int getKey() {
        return this.key;
    }

    public boolean isKeyDown(int key) {
        return this.key == key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
