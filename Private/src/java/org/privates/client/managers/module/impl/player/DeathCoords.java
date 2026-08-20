package org.privates.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.DeathEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "DeathCoords", category = Category.PLAYER, description = "Пишет кординаты сметри в чат")
public class DeathCoords extends Module {
    public static DeathCoords getInstance() {
        return Instance.get(DeathCoords.class);
    }

    @EventHandler
    public void onEvent(DeathEvent event) {
        if (mc.player == null) return;
        int x = (int) mc.player.getPosX();
        int y = (int) mc.player.getPosY();
        int z = (int) mc.player.getPosZ();
        ChatUtil.addText("Координаты смерти -> " + TextFormatting.RED + "X: " + x + ", Y: " + y + ", Z: " + z);
    }
}