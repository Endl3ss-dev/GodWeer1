package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.SmoothMotionRenderer;
import org.privates.client.utils.render.draw.RenderUtil;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "MotionBlur", category = Category.RENDER, description = "Плавное размытие движения экрана")
public class MotionBlur extends Module {

    final SliderSetting smoothness = new SliderSetting(this, "Smoothness", 0.62f, 0.05f, 1.0f, 0.05f);
    final SliderSetting sharpness = new SliderSetting(this, "Sharpness", 0.38f, 0.0f, 1.0f, 0.05f);
    final SliderSetting strength = new SliderSetting(this, "Сила", 0.75f, 0.15f, 1.0f, 0.05f);
    final BooleanSetting adaptive = new BooleanSetting(this, "Адаптивный", true);

    float time;
    float prevYaw;
    float prevPitch;
    boolean inited;

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (mc.player == null || !mc.isGameFocused()) return;

        float sw = mc.getMainWindow().getScaledWidth();
        float sh = mc.getMainWindow().getScaledHeight();
        MatrixStack ms = event.getMatrix();

        if (!inited) {
            prevYaw = mc.player.rotationYaw;
            prevPitch = mc.player.rotationPitch;
            inited = true;
        }

        float yawDelta = Math.abs(net.minecraft.util.math.MathHelper.wrapDegrees(mc.player.rotationYaw - prevYaw));
        float pitchDelta = Math.abs(mc.player.rotationPitch - prevPitch);
        prevYaw = mc.player.rotationYaw;
        prevPitch = mc.player.rotationPitch;

        float motionFactor = Math.min(1.0f, (yawDelta * 0.018f) + (pitchDelta * 0.028f));
        float adaptiveMix = adaptive.getValue() ? (0.45f + motionFactor * 0.85f) : 1.0f;
        float finalSmooth = Math.min(1.0f, smoothness.getValue() * adaptiveMix);
        float finalSharp = Math.max(0.0f, Math.min(1.0f, sharpness.getValue() - motionFactor * 0.12f));

        time += 0.016f;

        int overlayAlpha = (int) (70f * strength.getValue() * (adaptive.getValue() ? (0.4f + motionFactor * 0.6f) : 1.0f));
        SmoothMotionRenderer.registerRenderCall(() ->
                RenderUtil.Rounded.roundedRect(
                        ms,
                        -12,
                        -12,
                        sw + 24,
                        sh + 24,
                        ColorUtil.getColor(255, 255, 255, Math.max(0, Math.min(255, overlayAlpha))),
                        Round.of(0)
                )
        );
        SmoothMotionRenderer.draw(finalSmooth, finalSharp, time);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        inited = false;
        time = 0f;
    }
}

