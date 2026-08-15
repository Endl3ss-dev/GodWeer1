package antileak.base.api.events.implement;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.DrawContext;
import antileak.base.api.events.Event;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventHandledScreen extends Event {
    DrawContext drawContext;
    int backgroundWidth;
    int backgroundHeight;
}
