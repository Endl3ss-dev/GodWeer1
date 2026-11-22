package tech.lightclient.modules.impl.movement;

import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.events.impl.player.MoveInputEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.eventbus.EventPriority;
import tech.lightclient.client.utils.player.PlayerUtils;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@FunctionAdd(name = "AirStuck", alias = "Air Stuck", category = Category.Movement, description = "Позволяет зависать в воздухе")
public class AirStuck extends Function {
    public static boolean prank = false;
    private World lastWorld;

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            setState(false);
            return;
        }
        lastWorld = mc.world;
        mc.player.setVelocity(Vec3d.ZERO);
        super.onEnable();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMoveInput(MoveInputEvent event) {
        event.setOverrideForwardBackward(true);
        event.setForwardPressed(false);
        event.setBackwardPressed(false);
        event.setOverrideLeftRight(true);
        event.setLeft(false);
        event.setRight(false);
        event.setOverrideJump(true);
        event.setJumpPressed(false);
        event.cancel();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null || mc.world != lastWorld) {
            setState(false);
            return;
        }
        mc.player.setVelocity(Vec3d.ZERO);
        mc.player.velocityDirty = true;
    }

    @Override
    public void onDisable() {
        if (mc.player != null && !mc.player.isOnGround()) prank = true;
        PlayerUtils.enableMoveKeys();
        lastWorld = null;
        super.onDisable();
    }
}
