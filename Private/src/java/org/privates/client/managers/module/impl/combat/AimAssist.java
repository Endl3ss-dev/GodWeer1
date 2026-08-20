package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector4f;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.target.TargetComponent;
import org.privates.client.managers.events.other.GameUpdateEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.rotation.AuraUtil;
import org.privates.client.utils.rotation.SensUtil;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AimAssist", category = Category.COMBAT, description = "Помогает в прицеливание")
public class AimAssist extends Module {
    public static AimAssist getInstance() {
        return Instance.get(AimAssist.class);
    }

    private final SliderSetting distance = new SliderSetting(this, "Дистанция", 4.5F, 3F, 6F, 0.1F);
    private final SliderSetting lazinessH = new SliderSetting(this, "Плавность H", 10F, 1F, 100F, 1F);
    private final SliderSetting lazinessV = new SliderSetting(this, "Плавность V", 10F, 1F, 100F, 1F);
    public LivingEntity target;

    @Override

    public void toggle() {
        super.toggle();
        TargetComponent.clearTarget();
        TargetComponent.updateTargetList();
        this.target = null;
    }

    @EventHandler

    public void onEvent(UpdateEvent event) {
        this.target = TargetComponent.getTarget(this.distance.getValue());
    }

    @EventHandler
    public void onEvent(GameUpdateEvent event) {
        updateRotation();
    }


    private void updateRotation() {
        if (this.target == null) {
            return;
        }

        this.rotate(this.target, this.lazinessH.getValue(), this.lazinessV.getValue());
    }


    public void rotate(LivingEntity target, float lazinessH, float lazinessV) {
        final Vector4f rotation = AuraUtil.calculateRotation(target);

        mc.player.rotationYaw = smoothRotation(mc.player.rotationYaw, rotation.x, lazinessH);
        mc.player.rotationPitch = MathHelper.clamp(smoothRotation(mc.player.rotationPitch, rotation.y, lazinessV), -90F, 90F);
    }


    private float smoothRotation(float currentAngle, double targetAngle, float smoothFactor) {
        float angleDifference = (float) MathHelper.wrapDegrees(targetAngle - currentAngle);
        float adjustmentSpeed = Math.abs(angleDifference / smoothFactor);
        float angleAdjustment = (adjustmentSpeed * Math.signum(angleDifference));

        if (Math.abs(angleAdjustment) > Math.abs(angleDifference)) {
            angleAdjustment = angleDifference;
        }

        return currentAngle + SensUtil.getSens(angleAdjustment);
    }

}