package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.events.render.Render3DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.draw.RenderFactory;
import org.privates.client.utils.render.shader.BlurShader;
import org.privates.client.utils.render.shader.impl.BloomShader;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "GlassHands", category = Category.RENDER, description = "Glass shader for hands/items")
public class GlassHands extends Module {

    public static GlassHands getInstance() {
        return Instance.get(GlassHands.class);
    }

    public final BooleanSetting glow = new BooleanSetting(this, "Glow", true);
    public final SliderSetting glowSize = new SliderSetting(this, "Glow Size", 4F, 1F, 6F, 1F)
            .setVisible(() -> glow.getValue());
    public final SliderSetting glowSaturation = new SliderSetting(this, "Glow Saturation", 1.0F, 0.0F, 4F, 0.05F)
            .setVisible(() -> glow.getValue());

    public final BooleanSetting blur = new BooleanSetting(this, "Blur", true);
    public final SliderSetting blurSize = new SliderSetting(this, "Blur Size", 4F, 1F, 6F, 1F)
            .setVisible(() -> blur.getValue());
    public final SliderSetting blurSaturation = new SliderSetting(this, "Blur Saturation", 1.0F, 0.0F, 2.0F, 0.05F)
            .setVisible(() -> blur.getValue());

    private final BloomShader bloomShader = new BloomShader();
    private final BlurShader blurShader = new BlurShader();

    @EventHandler
    public void onPostWorld(Render3DEvent.PostWorld event) {
        if (!isEnabled() || mc.player == null || mc.world == null) return;
        if (!mc.gameSettings.getPointOfView().firstPerson()) return;

        if (glow.getValue()) {
            bloomShader.addTask3D(() -> mc.gameRenderer.renderHand(
                    event.getMatrix(),
                    event.getActiveRenderInfo(),
                    event.getPartialTicks(),
                    true, false, false
            ));
            bloomShader.draw(
                    glowSize.getValue().intValue(),
                    glowSize.getValue(),
                    BloomShader.RenderType.CAMERA,
                    glowSaturation.getValue()
            );
        }

        if (blur.getValue()) {
            blurShader.addTask3D(() -> mc.gameRenderer.renderHand(
                    event.getMatrix(),
                    event.getActiveRenderInfo(),
                    event.getPartialTicks(),
                    true, false, false
            ));
            blurShader.draw(
                    blurSize.getValue().intValue(),
                    blurSize.getValue(),
                    BlurShader.RenderType.CAMERA,
                    blurSaturation.getValue()
            );
        }
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (!isEnabled()) return;

        if (glow.getValue()) {
            RenderFactory.addTask(() -> bloomShader.draw(
                    glowSize.getValue().intValue(),
                    glowSize.getValue(),
                    BloomShader.RenderType.DISPLAY,
                    glowSaturation.getValue()
            ));
        }

        if (blur.getValue()) {
            RenderFactory.addTask(() -> blurShader.draw(
                    blurSize.getValue().intValue(),
                    blurSize.getValue(),
                    BlurShader.RenderType.DISPLAY,
                    blurSaturation.getValue()
            ));
        }
    }
}
