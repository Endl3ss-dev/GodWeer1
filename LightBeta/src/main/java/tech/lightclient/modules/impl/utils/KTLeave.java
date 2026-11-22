package tech.lightclient.modules.impl.utils;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.Vec3d;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import net.minecraft.text.Text;

@FunctionAdd(
        name = "KTLEAVE",
        alias = "KT Leave",
        category = Category.Utilities,
        description = "Безопасно покидает сервер выбранным способом"
)
public class KTLeave extends Function {

    private final ModeSetting mode = new ModeSetting(
            "Leave mode",
            "HolyWorld",
            "HolyWorld"
    );

    private boolean hwActive = false;

    public KTLeave() {
        addSettings();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player != null && mc.world != null && mc.player.networkHandler != null) {
            if (this.mode.is("HolyWorld") && this.hwActive) {
                performHWClassicLeave();

            }
        }
        }

    @Override
    public void onEnable() {
        hwActive = true;
        super.onEnable();
    }

    private void performHWClassicLeave() {
        if (mc.player != null && mc.world != null && mc.player.networkHandler != null) {
            for (int i = 0; i < 41; i++) {
                mc.player.setSneaking(true);
                Vec3d pos = mc.player.getEntityPos().add(i, 0.0, i);
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.z, pos.y, pos.x, Math.random() > 0.5, false));
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket((int) (Math.random() * 8.0)));
            }
        }
    }

    @Override
    public void onDisable() {
        hwActive = false;
        super.onDisable();
    }
}
