package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.annotations.Rage;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;

@Category1
@Rage
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ElytraBoost", category = Category.MOVEMENT, description = "Ускоряет вашу скорость при пуске феерверка на элитре")
public class ElytraBoost extends Module {

    private final SliderSetting[] yawSpeedSettings = new SliderSetting[9];
    private final SliderSetting[] pitchSpeedSettings = new SliderSetting[9];

    private float currentSpeed = 1.5f;
    private float currentYSpeed = 1.6f;

    public static ElytraBoost getInstance() {
        return Instance.get(ElytraBoost.class);
    }

    public ElytraBoost() {
        String[] angleRanges = new String[]{"0 - 5", "5 - 10", "10 - 15", "15 - 20", "20 - 25", "25 - 30", "30 - 35", "35 - 40", "40 - 45"};

        for (int i = 0; i < this.yawSpeedSettings.length; i++) {
            this.yawSpeedSettings[i] = new SliderSetting(this, "yaw " + angleRanges[i], 1.5f, 1.5f, 2.5f, 0.01f);
        }

        for (int i = 0; i < this.pitchSpeedSettings.length; i++) {
            this.pitchSpeedSettings[i] = new SliderSetting(this, "pitch " + angleRanges[i], 1.5f, 1.5f, 2.5f, 0.01f);
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (!mc.player.isElytraFlying()) return;

        AuraNew aura = AuraNew.getInstance();
        LivingEntity target = aura != null && aura.isEnabled() ? aura.target() : null;

        Vector3d targetPredictedPos = null;
        if (target != null) {
            Vector3d forward = target.getLookVec();
            if (forward.length() > 0) {
                forward = forward.normalize().scale(5.0);
            }
            targetPredictedPos = target.getPositionVec().add(forward);
        }

        if (targetPredictedPos != null && mc.player.getDistanceSq(targetPredictedPos) < 3.2) {
            currentSpeed = 1.2f;
            currentYSpeed = 1.2f;
            return;
        }

        currentYSpeed = 1.6f;

        float normalizedYaw = MathHelper.wrapDegrees(mc.player.rotationYaw);
        float absolutePitch = Math.abs(mc.player.rotationPitch);

        normalizedYaw = convertYawToRange(normalizedYaw);
        absolutePitch = convertYawToRange(absolutePitch);

        float yawSpeed = getSpeedForYaw(normalizedYaw);
        float pitchSpeed = getSpeedForPitch(absolutePitch);

        if (pitchSpeed > yawSpeed) {
            yawSpeed = pitchSpeed;
        }

        if (aura != null && aura.isEnabled() && target != null && !target.isElytraFlying()) {
            yawSpeed = 1.52F;
            pitchSpeed = 1.52F;

            if (mc.player.rotationPitch > 89.0F && mc.player.rotationPitch < 90.0F) {
                pitchSpeed = 2.3F;
            }
        }

        currentSpeed = yawSpeed;
        currentYSpeed = pitchSpeed;
    }

    private float getSpeedForYaw(float yawAngle) {
        int rangeIndex = (int) (yawAngle / 5.0F);
        if (rangeIndex >= this.yawSpeedSettings.length) {
            rangeIndex = this.yawSpeedSettings.length - 1;
        }
        return this.yawSpeedSettings[rangeIndex].getValue();
    }

    private float getSpeedForPitch(float pitchAngle) {
        int rangeIndex = (int) (pitchAngle / 5.0F);
        if (rangeIndex >= this.pitchSpeedSettings.length) {
            rangeIndex = this.pitchSpeedSettings.length - 1;
        }
        return this.pitchSpeedSettings[rangeIndex].getValue();
    }

    private float convertYawToRange(float angle) {
        float absoluteAngle = Math.abs(angle);
        if (absoluteAngle > 90.0F) {
            absoluteAngle = 180.0F - absoluteAngle;
        }
        if (absoluteAngle > 45.0F) {
            absoluteAngle = 90.0F - absoluteAngle;
        }
        return absoluteAngle;
    }

    public float getCurrentSpeed() {
        return isEnabled() ? currentSpeed : 1.5f;
    }

    public float getCurrentYSpeed() {
        return isEnabled() ? currentYSpeed : 1.6f;
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        currentSpeed = 1.5f;
        currentYSpeed = 1.6f;
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        currentSpeed = 1.5f;
        currentYSpeed = 1.6f;
    }
}