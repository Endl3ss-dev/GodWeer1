package antileak.base.client.modules.impl.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import antileak.base.Punch;
import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventRender;
import antileak.base.api.utils.color.ColorUtils;
import antileak.base.api.utils.render.RenderUtils;
import antileak.base.client.modules.Module;

public class Saturation extends Module {

    public static Saturation INSTANCE = new Saturation();

    public Saturation() {
        super("Saturation", "Отображение насыщенности", ModuleCategory.RENDER);
    }

    @EventLink
    public void onRender(EventRender.Default event) {
        if (!this.isEnable()) return;

        PlayerEntity player = mc.player;
        if (player == null) return;

        float saturation = player.getHungerManager().getSaturationLevel();
        if (saturation <= 0) return;

        int scaledWidth = mc.getWindow().getScaledWidth();
        int scaledHeight = mc.getWindow().getScaledHeight();

        var theme = Punch.INSTANCE.themeStorage.getThemes().getTheme();
        int colorTheme = !theme.getName().equals("Rainbow") ? theme.color[0] : ColorUtils.getThemeColor();
        int colorTheme2 = ColorUtils.darken(colorTheme, 0.4f);

        float barX = scaledWidth / 2f + 11;
        float barY = scaledHeight - 46;
        float barW = 80;
        float barH = 3.5f;
        float progress = MathHelper.clamp(saturation / 20f, 0f, 1f);

        MatrixStack matrices = event.getContext().getMatrices();

        float alpha = 1f;
        int bgColor = ColorUtils.applyAlpha(ColorUtils.darken(colorTheme2, 0.72f), (int)(alpha * 0.26f * 255));
        RenderUtils.drawRoundedRect(matrices, barX, barY, barW, barH, 2f, bgColor);

        float filledW = barW * progress;
        if (filledW > 0) {
            int fillLeft = ColorUtils.applyAlpha(colorTheme2, (int)(alpha * 255));
            int fillRight = ColorUtils.applyAlpha(colorTheme, (int)(alpha * 255));
            RenderUtils.drawGradientRect(matrices, barX, barY, filledW, barH, 2f, fillLeft, fillRight, true);
        }
    }
}
