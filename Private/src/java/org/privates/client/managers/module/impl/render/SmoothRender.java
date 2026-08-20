package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.math.MathHelper;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "SmoothRender", category = Category.RENDER)
public class SmoothRender extends Module {
    public static SmoothRender getInstance() {
        return Instance.get(SmoothRender.class);
    }

    private final SliderSetting smoothness = new SliderSetting(this, "Плавность", 0.7f, 0.3f, 0.95f, 0.05f);

    float smoothYaw = 0;
    float smoothPitch = 0;
    float lastYaw = 0;
    float lastPitch = 0;
    boolean initialized = false;

    @Override
    public void onEnable() {
        super.onEnable();
        initialized = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        initialized = false;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null) return;

        if (!initialized) {
            smoothYaw = mc.player.rotationYaw;
            smoothPitch = mc.player.rotationPitch;
            lastYaw = mc.player.rotationYaw;
            lastPitch = mc.player.rotationPitch;
            initialized = true;
            return;
        }

        float currentYaw = mc.player.rotationYaw;
        float currentPitch = mc.player.rotationPitch;

        float yawDiff = MathHelper.wrapDegrees(currentYaw - smoothYaw);
        float pitchDiff = currentPitch - smoothPitch;

        float factor = 1f - 0.001f;

        smoothYaw += yawDiff * factor;
        smoothPitch += pitchDiff * factor;

        smoothPitch = MathHelper.clamp(smoothPitch, -90.0f, 90.0f);
        smoothYaw = MathHelper.wrapDegrees(smoothYaw);

        mc.player.prevRotationYaw = lastYaw;
        mc.player.prevRotationPitch = lastPitch;
        mc.player.prevRotationYawHead = lastYaw;

        mc.player.rotationYaw = smoothYaw;
        mc.player.rotationPitch = smoothPitch;
        mc.player.rotationYawHead = smoothYaw;
        mc.player.renderYawOffset = smoothYaw;

        lastYaw = smoothYaw;
        lastPitch = smoothPitch;
    }
}