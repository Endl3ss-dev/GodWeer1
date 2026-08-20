package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.network.play.client.CPlayerPacket;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.api.interfaces.IWindow;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.MotionEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.player.FreeCam;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.font.Font;
import org.privates.client.utils.render.font.Fonts;

@Category3
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Freezing", category = Category.PLAYER, description = "Фризит вас в воздухе ")
public class Freezing extends Module implements IWindow {
    public static Freezing getInstance() {
        return Instance.get(Freezing.class);
    }

    private final int TEMP_ENTITY_ID = Integer.MAX_VALUE - 1337;
    private final Font font = Fonts.SF_SEMIBOLD;

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (mc.player == null) return;
        if (!mc.player.isOnGround()) {
            mc.player.setVelocity(0, 0, 0);
        }
    }

    @EventHandler
    public void onMotion(MotionEvent e) {
        if (mc.player == null) return;
        if (!mc.player.isOnGround()) {
            e.cancel();
        }
    }

    @EventHandler
    public void onPacket(PacketEvent e) {
        if (mc.player == null) return;
        if (!mc.player.isOnGround()) {
            if (e.getPacket() instanceof CPlayerPacket) {
                e.cancel();
            }
        }
    }
}