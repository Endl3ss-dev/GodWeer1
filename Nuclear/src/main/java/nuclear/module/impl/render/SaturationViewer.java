package nuclear.module.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import nuclear.control.events.Event;
import nuclear.control.events.impl.render.EventRender;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.utils.font.Fonts;
import nuclear.utils.render.ColorUtils;
import nuclear.utils.render.RenderUtils;

import java.util.Locale;

@ModuleDescriptor(name = "SaturationViewer", type = ModuleCategory.Render, desc = "Показывает настоящие насыщение голода")
public class SaturationViewer extends Module {
    @Override
    public boolean onEvent(Event event) {
        if (!(event instanceof EventRender render) || mc.player == null || mc.world == null || !render.isRender2D()) {
            return false;
        }

        renderSaturation(render.matrixStack);
        return false;
    }

    private void renderSaturation(MatrixStack matrixStack) {
        float saturation = mc.player.getFoodStats().getSaturationLevel();
        String text = String.format(Locale.ROOT, "Sat: %.1f", saturation);

        float screenWidth = mc.getMainWindow().getScaledWidth();
        float screenHeight = mc.getMainWindow().getScaledHeight();
        float textWidth = Fonts.newcode[13].getWidth(text);
        float x = screenWidth / 2.0f + 92.0f - textWidth;
        float y = screenHeight - 58.0f;
        int color = saturation > 10.0f ? ColorUtils.rgba(80, 220, 120, 255) : ColorUtils.rgba(255, 210, 80, 255);

        RenderUtils.Render2D.drawRoundedRect(x - 3.0f, y - 2.0f, textWidth + 6.0f, Fonts.newcode[13].getFontHeight() + 3.0f, 1.5f, ColorUtils.rgba(10, 10, 10, 145));
        Fonts.newcode[13].drawString(matrixStack, text, x, y, color);
    }
}
