package tech.lightclient.client.events.impl.client;

import tech.lightclient.client.events.api.Event;
import lombok.Getter;
import net.minecraft.client.gui.screen.Screen;

@Getter
public class EventCloseScreen extends Event {
    private final Screen screen;

    public EventCloseScreen(Screen screen) {
        this.screen = screen;
    }
}
