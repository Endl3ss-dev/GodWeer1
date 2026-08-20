package org.privates.client.managers.module.impl.render;

import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.SaturationRenderer;

@Category3
@ModuleInfo(name = "SaturationGame", category = Category.RENDER)
public class SaturationGame extends Module {

    private final SliderSetting saturation = new SliderSetting(this, "Насыщенность", 1.5f, 0.0f, 3.0f, 0.1f);
    private final SliderSetting brightness = new SliderSetting(this, "Яркость", 1.0f, 0.5f, 2.0f, 0.1f);
    private final SliderSetting contrast = new SliderSetting(this, "Контраст", 1.0f, 0.5f, 2.0f, 0.1f);

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (mc.player == null || !isEnabled()) return;

        MatrixStack matrix = event.getMatrix();

        float sw = mc.getMainWindow().getScaledWidth();
        float sh = mc.getMainWindow().getScaledHeight();

        SaturationRenderer.registerRenderCall(() ->
                RenderUtil.Rounded.roundedRect(matrix, -10, -10, sw + 20, sh + 20,
                        ColorUtil.getColor(255, 255, 255, 255), Round.of(0)));
        SaturationRenderer.draw(saturation.getValue(), brightness.getValue(), contrast.getValue());
    }
}
