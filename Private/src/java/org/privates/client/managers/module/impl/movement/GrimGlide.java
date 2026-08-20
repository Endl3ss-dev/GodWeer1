package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.MotionEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "GrimGlide", category = Category.MOVEMENT, description = "Глайд на элитре")
public class GrimGlide extends Module {

    public static GrimGlide getInstance() {
        return Instance.get(GrimGlide.class);
    }

    private final StopWatch timer = new StopWatch();
    private int ticksTwo = 0;

    @EventHandler
    public void onMotion(MotionEvent event) {
        if (mc.player == null || mc.world == null || !mc.player.isElytraFlying()) return;

        ticksTwo++;
        Vector3d pos = mc.player.getPositionVec();
        float yaw = mc.player.rotationYaw;
        double forward = 0.085;

        double dx = -Math.sin(Math.toRadians(yaw)) * forward;
        double dz = Math.cos(Math.toRadians(yaw)) * forward;

        mc.player.setMotion(dx * 1.25f, mc.player.getMotion().y - 0.01f, dz * 1.25f);

        if (timer.finished(45)) {
            mc.player.setPosition(
                    pos.getX() + dx,
                    pos.getY(),
                    pos.getZ() + dz
            );
            timer.reset();
        }

        mc.player.setMotion(dx * 1.25f, mc.player.getMotion().y + 0.015f, dz * 1.25f);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ticksTwo = 0;
        timer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        ticksTwo = 0;
    }
}