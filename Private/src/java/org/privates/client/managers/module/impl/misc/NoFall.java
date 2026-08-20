package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.other.ViaUtil;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "NoFall", description = "Предотвращает получение урона после падения с высокого места", category = Category.MOVEMENT)
public class NoFall extends Module {
    public static NoFall getInstance() {
        return Instance.get(NoFall.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Grim");

    @Override
    public void toggle() {
        super.toggle();
        if (!ViaUtil.allowedBypass()) {
            ChatUtil.addText("Используйте версию 1.17.1 и выше, для корректной работы модуля!");
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (ViaUtil.allowedBypass() && mc.player.fallDistance > 2.5) {
            if (mc.player.getServerBrand().toLowerCase().contains("captcha")) {
                return;
            }

            ViaUtil.sendPositionPacket(mc.player.getPosX(), mc.player.getPosY() + 1e-6, mc.player.getPosZ(), mc.player.rotationYaw, mc.player.rotationPitch, false);
            mc.player.fallDistance = 0;
        }
    }
}
