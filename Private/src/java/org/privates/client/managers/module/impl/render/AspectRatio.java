package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.AspectRatioEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AspectRatio", category = Category.RENDER, description = "Растяг экрана")
public class AspectRatio extends Module {
    public static AspectRatio getInstance() {
        return Instance.get(AspectRatio.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим",
            "16:9", "1:1", "16:10", "3:4", "Custom"
    );

    private final SliderSetting ratio = new SliderSetting(this, "Значение", 0.5F, 0F, 1F, 0.01F).setVisible(() -> mode.is("Custom"));

    @EventHandler
    public void onEvent(AspectRatioEvent event) {
        if (mode.getValue().equals("Custom")) {
            event.setAspectRatio(0.1F + ratio.getValue() * 2.5F);
            return;
        }
        String[] ratio = mode.getValue().split(":");
        float w = Float.parseFloat(ratio[0]);
        float h = Float.parseFloat(ratio[1]);
        event.setAspectRatio(w / h);
    }
}