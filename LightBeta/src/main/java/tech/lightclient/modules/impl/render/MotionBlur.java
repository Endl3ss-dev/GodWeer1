package tech.lightclient.modules.impl.render;

import tech.lightclient.client.utils.render.main.postfx.MotionBlurRenderer;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.SliderSetting;

@FunctionAdd(
        name = "MotionBlur",
        alias = "Motion Blur",
        category = Category.Render,
        description = "Физически основанный MotionBlur, повышающий плавность картинки."
)
public final class MotionBlur extends Function {
    public final SliderSetting smoothness = new SliderSetting("Плавность", 0.68f, 0.0f, 1.0f, 0.01f);

    public MotionBlur() {
        addSettings(smoothness);
    }

    @Override
    public void onEnable() {
        MotionBlurRenderer.getInstance().reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        MotionBlurRenderer.getInstance().reset();
    }

    public MotionBlurRenderer.Settings resolve() {
        float value = Math.max(0.0f, Math.min(1.0f, smoothness.get()));
        float eased = value * value * (3.0f - 2.0f * value);

        MotionBlurRenderer.Settings settings = new MotionBlurRenderer.Settings();
        settings.strength = 0.58f + eased * 0.42f;
        settings.temporalScale = 1.35f + eased * 2.65f;
        settings.samples = 7 + Math.round(eased * 5.0f);
        settings.maxRadius = 38.0f + eased * 90.0f;
        settings.edgeFocus = 0.3f + eased * 0.42f;
        settings.chromaticPhase = 0.22f + eased * 1.12f;
        settings.depthGuard = 0.68f;
        settings.decay = 2.85f - eased * 1.35f;
        settings.activation = 0.035f + (1.0f - eased) * 0.075f;
        return settings;
    }
}
