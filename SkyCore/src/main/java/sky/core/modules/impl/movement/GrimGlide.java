package sky.core.modules.impl.movement;

import com.darkmagician6.eventapi.eventapinew.EventTarget;
import net.minecraft.entity.Entity;
import net.minecraft.item.UseAction;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import sky.core.SkyCore;
import sky.core.events.EventOnTravelPost;
import sky.core.events.EventPacket;
import sky.core.events.EventUpdate;
import sky.core.modules.Category;
import sky.core.modules.Module;
import sky.core.modules.ModuleManager;

public class GrimGlide extends Module {

    private int flagTicks;
    private boolean isFlag;

    public GrimGlide() {
        super("Glide Fly", "Бесконечный полет на элитре", Category.Movement);
    }

    public static boolean isGlideFlyActive() {
        if (mc.player == null || !mc.player.isElytraFlying()) {
            return false;
        }
        ModuleManager manager = SkyCore.getInstance().getModuleManager();
        if (manager == null) {
            return false;
        }
        GrimGlide grimGlide = (GrimGlide) manager.getModule(GrimGlide.class);
        if (grimGlide != null && grimGlide.isEnabled()) {
            return true;
        }
        Flight flight = (Flight) manager.getModule(Flight.class);
        return flight != null && flight.isEnabled() && flight.isGlideFlyMode();
    }

    private static void blockEating() {
        if (mc.player == null) {
            return;
        }
        if (mc.player.isHandActive() && isConsumableUse(mc.player.getActiveItemStack().getUseAction())) {
            mc.playerController.onStoppedUsingItem(mc.player);
        }
        mc.gameSettings.keyBindUseItem.setPressed(false);
    }

    private static boolean isConsumableUse(UseAction action) {
        return action == UseAction.EAT || action == UseAction.DRINK;
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if (mc.player == null) {
            return;
        }

        if (event.isReceive() && event.getPacket() instanceof SPlayerPositionLookPacket) {
            flagTicks = 2;
            isFlag = true;
        }

        if (event.isSend() && event.getPacket() instanceof CPlayerPacket) {
            if (mc.player.isElytraFlying()) {
                if (flagTicks == 0 && !isFlag) {
                    mc.player.connection.sendPacketWithoutEvent(new CPlayerPacket(true));
                    event.setCancelled(true);
                }
            }
            isFlag = false;
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (flagTicks > 0) {
            flagTicks--;
        }
        if (isGlideFlyActive()) {
            blockEating();
        }
    }

    @EventTarget
    public void onTravelPost(EventOnTravelPost event) {
        if (!isEnabled() || mc.player == null || !mc.player.isElytraFlying()) {
            return;
        }

        Vector3d oldVelocity = mc.player.getMotion();
        Vector3d lookVec = mc.player.getLookVec();
        float pitchRad = mc.player.rotationPitch * ((float) Math.PI / 180F);
        double horizontalLook = Math.sqrt(lookVec.x * lookVec.x + lookVec.z * lookVec.z);
        double horizontalSpeed = Math.sqrt(Entity.horizontalMag(oldVelocity));
        boolean falling = mc.player.getMotion().y <= 0.0D;
        double gravity = falling && mc.player.isPotionActive(Effects.SLOW_FALLING)
                ? Math.min(0.08D, 0.01D)
                : 0.08D;
        double pitchFactor = MathHelper.square(Math.cos(pitchRad));
        oldVelocity = oldVelocity.add(0.0D, gravity * (-1.0D + pitchFactor * 0.75D), 0.0D);

        if (oldVelocity.y < 0.0D && horizontalLook > 0.0D) {
            double lift = oldVelocity.y * -0.1D * pitchFactor;
            oldVelocity = oldVelocity.add(lookVec.x * lift / horizontalLook, lift, lookVec.z * lift / horizontalLook);
        }

        if (pitchRad < 0.0F && horizontalLook > 0.0D) {
            double dive = horizontalSpeed * (double) (-MathHelper.sin(pitchRad)) * 0.04D;
            oldVelocity = oldVelocity.add(-lookVec.x * dive / horizontalLook, dive * 3.2D, -lookVec.z * dive / horizontalLook);
        }

        if (horizontalLook > 0.0D) {
            oldVelocity = oldVelocity.add(
                    (lookVec.x / horizontalLook * horizontalSpeed - oldVelocity.x) * 0.1D,
                    0.0D,
                    (lookVec.z / horizontalLook * horizontalSpeed - oldVelocity.z) * 0.1D
            );
        }

        double yaw = Math.toRadians(mc.player.rotationYaw);
        double forwardX = -Math.sin(yaw);
        double forwardZ = Math.cos(yaw);

        if (flagTicks >= 1) {
            double boost = 0.09D;
            event.setOldVelocity(oldVelocity.mul(0.99D, 0.9800000190734863D, 0.99D).add(forwardX * boost, 0.03D, forwardZ * boost));
        } else {
            event.setOldVelocity(oldVelocity.mul(0.3D, 0.3D, 0.3D));
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        flagTicks = 0;
        isFlag = false;
    }

    @Override
    public void onDisable() {
        flagTicks = 0;
        isFlag = false;
        blockEating();
        super.onDisable();
    }
}
