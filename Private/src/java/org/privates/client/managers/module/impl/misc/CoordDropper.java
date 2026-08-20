package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.Minecraft;
import org.privates.client.api.annotations.Category2;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;

@Category2
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "CoordsDropper", category = Category.MISC, description = "Я заебался писать но отправляет кординаты в чат (текущие)")
public class CoordDropper extends Module {
    private final SliderSetting delay = new SliderSetting(this, "Задержка", 30, 10, 120, 1);
    private long lastSentTime = 0;
    private boolean firstMessageSent = false;

    public CoordDropper getInstance() {
        return Instance.get(CoordDropper.class);
    }

    protected void onEnable() {
        super.onEnable();
        sendCoords();
        lastSentTime = System.currentTimeMillis();
        firstMessageSent = true;
    }

    public void onUpdate(UpdateEvent event) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSentTime >= delay.getValue() * 1000) {
            sendCoords();
            lastSentTime = currentTime;
        }
    }

    public void sendCoords() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            double x = mc.player.getPosX();
            double y = mc.player.getPosY();
            double z = mc.player.getPosZ();
            String message = String.format("! %.2f  %.2f  %.2f", x, y, z);
            mc.player.sendChatMessage(message);
        }
    }
}