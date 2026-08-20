package fun.volon.client.ui.clickgui;

import net.minecraft.client.util.math.MatrixStack;
import fun.volon.api.storages.implement.ThemeStorage;
import fun.volon.api.utils.animation.gui.GuiAnimation;
import fun.volon.api.utils.animation.gui.GuiEasing;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.input.CursorManager;
import fun.volon.api.utils.math.HoveringUtils;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.client.modules.settings.implement.ThemeSetting;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class ThemeComponent extends Component {
    private final ThemeSetting setting;
    private final Map<ThemeStorage.Themes, GuiAnimation> checkAnims = new HashMap<>();

    public ThemeComponent(ThemeSetting setting) {
        this.setting = setting;
        for (ThemeStorage.Themes theme : setting.getThemes()) {
            checkAnims.put(theme, new GuiAnimation(GuiEasing.QUINTIC_OUT, 350));
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        float alpha = Math.max(Math.min(getAlphaAnimSetting().getValue(), 1), 0);
        float startX = x + 2;
        float currentX = startX;
        float circleY = y + 8;

        for (ThemeStorage.Themes themeEntry : setting.getThemes()) {
            GuiAnimation checkAnim = checkAnims.computeIfAbsent(themeEntry,
                    k -> new GuiAnimation(GuiEasing.QUINTIC_OUT, 350));
            checkAnim.run(setting.getValue() == themeEntry);

            if (HoveringUtils.isHovered(mouseX, mouseY, currentX - 1, circleY - 1, 10, 10)) {
                CursorManager.requestHand();
            }

            if (checkAnim.getValue() > 0.01f) {
                float a = checkAnim.getValue() * alpha;
                RenderUtils.drawRoundedRect(matrices, currentX - 1.5f, circleY - 1.5f, 11, 11, 5.5f,
                        ColorUtils.rgba(255, 255, 255, (int) (255 * a)));
            }

            drawTheme(matrices, themeEntry, currentX, circleY, alpha, checkAnim.getValue());

            currentX += 14;
        }

        height = 20f;
    }

    private void drawTheme(MatrixStack matrices, ThemeStorage.Themes entry, float tx, float ty, float alpha, float check) {
        fun.volon.api.storages.implement.helpertstorages.Theme t = entry.getTheme();
        boolean rainbow = "Rainbow".equalsIgnoreCase(t.getName());
        int alphaInt = (int) (255 * alpha);

        if (rainbow) {
            int[] rainbowColors = new int[4];
            for (int i = 0; i < 4; i++) {
                rainbowColors[i] = Color.HSBtoRGB(i / 4f, 0.8f, 1f);
            }
            RenderUtils.drawGradientRect(matrices, tx, ty, 9, 9, 4.5f,
                    ColorUtils.setAlphaColor(rainbowColors[0], alphaInt),
                    ColorUtils.setAlphaColor(rainbowColors[1], alphaInt),
                    ColorUtils.setAlphaColor(rainbowColors[2], alphaInt),
                    ColorUtils.setAlphaColor(rainbowColors[3], alphaInt));
        } else {
            int c1 = t.color.length > 0 ? t.color[0] : ClickGuiColors.theme();
            int c2 = t.color.length > 1 ? t.color[1] : c1;
            float brightness = 0.5f + check * 0.5f;
            int colorLeft = brighter(ColorUtils.setAlphaColor(c1, alphaInt), brightness);
            int colorRight = brighter(ColorUtils.setAlphaColor(c2, alphaInt), brightness);
            RenderUtils.drawGradientRect(matrices, tx, ty, 9, 9, 4.5f,
                    colorLeft, colorRight, colorLeft, colorRight);
        }
    }

    private static int brighter(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) * factor));
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) * factor));
        int b = Math.min(255, (int) ((color & 0xFF) * factor));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        float currentX = x + 2;
        float circleY = y + 8;
        for (ThemeStorage.Themes themeEntry : setting.getThemes()) {
            if (HoveringUtils.isHovered(mouseX, mouseY, currentX - 1, circleY - 1, 10, 10)) {
                if (button == 0 && setting.getValue() != themeEntry) {
                    setting.setValue(themeEntry);
                }
            }
            currentX += 14;
        }
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
