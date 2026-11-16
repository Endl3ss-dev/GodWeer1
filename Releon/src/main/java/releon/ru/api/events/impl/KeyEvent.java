package releon.ru.api.events.impl;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil.Type;
import releon.ru.api.events.Event;

public record KeyEvent(Screen screen, Type type, int key, int action) implements Event {
}
