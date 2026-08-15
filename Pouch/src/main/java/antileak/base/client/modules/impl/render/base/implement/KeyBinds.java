package antileak.base.client.modules.impl.render.base.implement;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import antileak.base.Punch;
import antileak.base.api.events.implement.EventRender;
import antileak.base.api.utils.animation.AnimationUtils;
import antileak.base.api.utils.animation.Easings;
import antileak.base.api.utils.color.ColorUtils;
import antileak.base.api.utils.draggable.Draggable;
import antileak.base.api.utils.input.KeyBoardUtils;
import antileak.base.api.utils.render.RenderUtils;
import antileak.base.api.utils.render.fonts.msdf.Font;
import antileak.base.api.utils.render.fonts.msdf.Fonts;
import antileak.base.api.utils.render.fonts.ttf.MCFontRenderer;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.impl.render.base.InterfaceProcessing;

import java.util.HashMap;
import java.util.Map;

public class KeyBinds extends InterfaceProcessing {
    private static final float BASE_MIN_WIDTH   = 64f;
    private static final float EXTRA_WIDTH      = 0f;
    private static final float ROW_RIGHT_MARGIN = 25f;
    private static final float ROW_HEIGHT       = 10f;

    private final Map<Module, AnimationUtils> animations = new HashMap<>();
    private final AnimationUtils widthAnimation  = new AnimationUtils(60, 10.5f, Easings.QUAD_OUT);
    private final AnimationUtils heightAnimation = new AnimationUtils(16, 10.5f, Easings.QUAD_OUT);

    private static final String[][] PREVIEW = {
        {"AttackAura", "R"},
        {"AutoSwap", "J"},
        {"TargetPearl", "B"}
    };

    private static final Map<Character, Character> RU_TO_EN = new HashMap<>();
    static {
        String ru = "йцукенгшщзхъфывапролджэячсмитьбюЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ";
        String en = "qwertyuiop[]asdfghjkl;'zxcvbnm,.QWERTYUIOP[]ASDFGHJKL;'ZXCVBNM,.";
        for (int i = 0; i < ru.length(); i++) RU_TO_EN.put(ru.charAt(i), en.charAt(i));
    }

    private Font issue(int size) { return Fonts.getFont("suisse", size); }
    private Font icons(int size) { return Fonts.getFont("icon", size); }
    private MCFontRenderer myyyy(int size) { return antileak.base.api.utils.render.fonts.ttf.Fonts.getFont("myyyy.ttf", size); }

    public KeyBinds(Draggable draggable) { super(draggable); }

    private AnimationUtils getAnimation(Module module) {
        return animations.computeIfAbsent(module, m -> new AnimationUtils(0, 10.5f, Easings.QUAD_OUT));
    }

    private String toEnglish(String text) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) result.append(RU_TO_EN.getOrDefault(c, c));
        return result.toString();
    }

    private int getStableThemeColor() {
        if (!Punch.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            return Punch.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        }
        return ColorUtils.getThemeColor();
    }

    @Override
    public void onRender(EventRender.Default eventRender) {
        renderHud(eventRender);
        super.onRender(eventRender);
    }

    private void renderHud(EventRender.Default event) {
        MatrixStack ms = event.getContext().getMatrices();
        float x = draggable.getX(), y = draggable.getY();
        int theme = getStableThemeColor();

        for (Module m : antileak.base.api.storages.implement.helpertstorages.enumvar.ModuleClass.INSTANCE.getObject()) {
            if (m.getKey() != -1) getAnimation(m).update(m.isEnable() ? 1 : 0);
        }

        float targetWidth = BASE_MIN_WIDTH;
        int activeCount = 0;

        for (Module m : antileak.base.api.storages.implement.helpertstorages.enumvar.ModuleClass.INSTANCE.getObject()) {
            if (m.getKey() != -1 && m.isEnable()) {
                activeCount++;
                String bind = toEnglish(KeyBoardUtils.getBindName(m.getKey()));
                float moduleWidth = issue(12).getWidth(m.getDisplayName()) + issue(10).getWidth(bind) + ROW_RIGHT_MARGIN;
                if (moduleWidth > targetWidth) targetWidth = moduleWidth;
            }
        }

        float targetHeight = 16 + activeCount * ROW_HEIGHT;

        boolean inChat = mc.currentScreen instanceof ChatScreen;
        if (activeCount == 0) {
            if (inChat) {
                targetHeight = 16 + 3 * ROW_HEIGHT;
            } else {
                targetWidth = 0;
                targetHeight = 0;
            }
        }

        widthAnimation.update(targetWidth);
        heightAnimation.update(targetHeight);

        float width  = widthAnimation.getValue() + EXTRA_WIDTH;
        float height = heightAnimation.getValue();
        float rightEdge = x + width;

        if (width < 30 || height < 15) return;

        // Background with Outer Glow
        RenderUtils.drawShadow(ms, x, y, width, height, 6f, 12f, ColorUtils.setAlphaColor(theme, 80));
        RenderUtils.drawRoundedRect(ms, x, y, width, height, 6f, ColorUtils.rgba(20, 20, 20, 230));

        // Bottom Accent Line
        RenderUtils.drawRoundedRect(ms, x + width / 2f - 12f, y + height - 2f, 24f, 2f, 1f, ColorUtils.setAlphaColor(theme, 255));

        // Header "Hotkeys"
        issue(14).drawString(ms, "Hotkeys", x + 5.2f, y + 6.5f, ColorUtils.setAlphaColor(-1, 255));

        // Layout icon
        MCFontRenderer iconFont = myyyy(15);
        if (iconFont != null) {
            iconFont.drawString("A", rightEdge - 13f, y + 7.5f, ColorUtils.setAlphaColor(theme, 255));
        }

        // Rows
        float offsetY = 18f;
        if (activeCount > 0) {
            for (Module m : antileak.base.api.storages.implement.helpertstorages.enumvar.ModuleClass.INSTANCE.getObject()) {
                if (m.getKey() == -1) continue;
                AnimationUtils anim = getAnimation(m);
                float av = anim.getValue();
                if (av <= 0.01f) continue;

                int alpha = (int)(255 * av);
                float textY = y + offsetY - 1;

                // Module name (left)
                issue(12).drawString(ms, m.getDisplayName(), x + 5.2f, textY, ColorUtils.setAlphaColor(-1, alpha));

                // Bind key (right)
                String bind = toEnglish(KeyBoardUtils.getBindName(m.getKey()));
                float keyBoxWidth = Math.max(issue(10).getStringWidth(bind) + 4, 9f);
                float keyBoxX = rightEdge - keyBoxWidth - 5;
                issue(12).drawCenteredString(ms, bind, keyBoxX + keyBoxWidth / 2, textY + 0.8f, ColorUtils.setAlphaColor(theme, alpha));

                offsetY += ROW_HEIGHT * av;
            }
        } else if (inChat) {
            for (String[] p : PREVIEW) {
                issue(12).drawString(ms, p[0], x + 5.2f, y + offsetY - 1, ColorUtils.setAlphaColor(-1, 255));

                float keyBoxWidth = Math.max(issue(10).getStringWidth(p[1]) + 4, 9f);
                float keyBoxX = rightEdge - keyBoxWidth - 5;
                issue(12).drawCenteredString(ms, p[1], keyBoxX + keyBoxWidth / 2, y + offsetY + 0.8f - 1, ColorUtils.setAlphaColor(theme, 255));

                offsetY += ROW_HEIGHT;
            }
        }

        draggable.setWidth(width);
        draggable.setHeight(height);
    }
}
