package antileak.base.client.modules.impl.misc;

import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventUpdate;
import antileak.base.client.modules.Module;
import net.minecraft.client.gui.screen.DeathScreen;

public class DeathCoords extends Module {

    public static DeathCoords INSTANCE = new DeathCoords();

    private boolean hasPrinted = false;

    public DeathCoords() {
        super("DeathCoords", "Пишет координаты смерти в чат", ModuleCategory.MISC);
    }

    @EventLink
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null) return;

        if (mc.player.getHealth() <= 0.0f && mc.currentScreen instanceof DeathScreen && !hasPrinted) {
            int x = (int) mc.player.getX();
            int y = (int) mc.player.getY();
            int z = (int) mc.player.getZ();
            mc.player.sendMessage(
                    net.minecraft.text.Text.literal("§cКоординаты смерти: §fX: " + x + " Y: " + y + " Z: " + z),
                    false
            );
            hasPrinted = true;
        } else if (mc.player.getHealth() > 0.0f) {
            hasPrinted = false;
        }
    }
}
