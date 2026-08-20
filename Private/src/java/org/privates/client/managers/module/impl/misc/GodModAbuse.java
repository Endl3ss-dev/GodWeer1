package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.player.PlayerUtil;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "GodModAbuse", category = Category.MISC, description = "вы")
public class GodModAbuse extends Module {

    @EventHandler
    public void onPacket(PacketEvent e) {
        IPacket<?> packet = e.getPacket();
        if (PlayerUtil.isPvp() && packet instanceof CPlayerPacket) {

        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (mc.currentScreen instanceof ContainerScreen<?> screen) {

        }
    }
}

