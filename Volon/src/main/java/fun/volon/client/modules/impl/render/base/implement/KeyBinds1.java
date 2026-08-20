package fun.volon.client.modules.impl.render.base.implement;

import net.minecraft.client.util.math.MatrixStack;
import fun.volon.Volon;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import fun.volon.api.utils.animation.AnimationUtils;
import fun.volon.api.utils.animation.Easings;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.draggable.Draggable;
import fun.volon.api.utils.input.KeyBoardUtils;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.api.utils.scissor.ScissorUtils;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.impl.render.Interface;
import fun.volon.client.modules.impl.render.base.InterfaceProcessing;

import java.util.HashMap;
import java.util.Map;

public class KeyBinds1 extends InterfaceProcessing {

    private final Map<Module, AnimationUtils> animations = new HashMap<>();
    private final AnimationUtils widthAnimation = new AnimationUtils(76, 10.5f, Easings.QUAD_OUT);

    private static final Map<Character, Character> RU_TO_EN = new HashMap<>();
    static {
        String ru = "йцукенгшщзхъфывапролджэячсмитьбюЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ";
        String en = "qwertyuiop[]asdfghjkl;'zxcvbnm,.QWERTYUIOP[]ASDFGHJKL;'ZXCVBNM,.";
        int length = Math.min(ru.length(), en.length());
        for (int i = 0; i < length; i++) {
            RU_TO_EN.put(ru.charAt(i), en.charAt(i));
        }
    }

    private static final String HEADER_GLYPH = "p";
    private static final String TITLE        = "Keybinds";

    private Font text(int size)     { return Fonts.getFont("suisse", size); }
    private Font iconFont(int size) { return Fonts.getFont("icon", size); }
    private Font catFont(int size)  { return Fonts.getFont("icon", size); }

    private static final float RADIUS       = 4f;
    private static final float PAD_X        = 5f;
    private static final float HEADER_H     = 13f;
    private static final float ROW_H        = 13f;
    private static final float BODY_PAD_TOP = 1f;
    private static final float BODY_PAD_BOT = 2f;
    private static final float ICON_GAP     = 2f;
    private static final float MIN_WIDTH    = 76f;
    private static final int   TEXT_FONT_SIZE = 12;
    private static final int   ICON_FONT_SIZE = 15;

    private static final float TEXT_BASE_Y = 5.3f;
    private static final float ICON_BASE_Y = 5.3f;

    private static final int TEXT_WHITE = ColorUtils.rgba(255, 255, 255, 255);

    private static int alphaFromPercent(float percent) {
        int a = (int) (255f * percent / 100f);
        if (a < 0) return 0;
        if (a > 255) return 255;
        return a;
    }
    private static int bodyBg() {
        return ColorUtils.rgba(7, 7, 9,
                alphaFromPercent(Interface.INSTANCE != null ? Interface.INSTANCE.keyBinds1BodyAlpha.get() : 57f));
    }
    private static int headerBg() {
        return ColorUtils.rgba(10, 10, 12,
                alphaFromPercent(Interface.INSTANCE != null ? Interface.INSTANCE.keyBinds1HeaderAlpha.get() : 85f));
    }

    private static int accentColor() {
        if (Volon.INSTANCE == null || Volon.INSTANCE.themeStorage == null) return ColorUtils.getThemeColor();
        var theme = Volon.INSTANCE.themeStorage.getThemes().getTheme();
        if (theme == null) return ColorUtils.getThemeColor();
        if ("Rainbow".equals(theme.getName())) return ColorUtils.getThemeColor();
        return theme.color != null && theme.color.length > 0 ? theme.color[0] : ColorUtils.getThemeColor();
    }

    public KeyBinds1(Draggable draggable) {
        super(draggable);
    }

    private AnimationUtils getAnimation(Module module) {
        return animations.computeIfAbsent(module, m -> new AnimationUtils(0, 10.5f, Easings.QUAD_OUT));
    }

    private static boolean isBound(Module module) {
        return module.getKey() != -1 && module.getKey() != 0;
    }

    private static String toEnglish(String t) {
        StringBuilder result = new StringBuilder();
        for (char c : t.toCharArray()) {
            result.append(RU_TO_EN.getOrDefault(c, c));
        }
        return result.toString();
    }

    @Override
    public void onRender(EventRender.Default eventRender) {
        MatrixStack m = eventRender.getContext().getMatrices();
        float x = draggable.getX();
        float y = draggable.getY();

        Font hIcon  = iconFont(ICON_FONT_SIZE);
        Font hTitle = text(TEXT_FONT_SIZE);
        Font rName  = text(TEXT_FONT_SIZE);
        Font rKey   = text(TEXT_FONT_SIZE);
        Font rIcon  = catFont(ICON_FONT_SIZE);

        for (Module module : ModuleClass.INSTANCE.getObject()) {
            if (isBound(module)) getAnimation(module).update(module.isEnable() ? 1 : 0);
        }

        float headerGroupW = hIcon.getWidth(HEADER_GLYPH) + ICON_GAP + hTitle.getWidth(TITLE);
        float width = Math.max(MIN_WIDTH, PAD_X + headerGroupW + PAD_X);
        float bodyH = 0f;
        int visible = 0;

        for (Module module : ModuleClass.INSTANCE.getObject()) {
            if (!isBound(module)) continue;
            float a = getAnimation(module).getValue();
            if (a <= 0.01f) continue;
            visible++;

            String key = toEnglish(KeyBoardUtils.getKeyName(module.getKey()));
            String iconStr = module.getCategory() == null ? "" : module.getCategory().getIcons();
            float iconW = iconStr.isEmpty() ? 0 : rIcon.getWidth(iconStr) + ICON_GAP;
            float rowW = PAD_X + iconW + rName.getWidth(module.getDisplayName())
                       + 6f + rKey.getWidth(key) + PAD_X;
            if (rowW > width) width = rowW;
            bodyH += ROW_H * a;
        }

        widthAnimation.update(width);
        width = widthAnimation.getValue();
        float height = HEADER_H + (visible > 0 ? BODY_PAD_TOP + bodyH + BODY_PAD_BOT : 0f);

        float scaleMul = 1f;
        if (Interface.INSTANCE != null) {
            scaleMul = Interface.INSTANCE.keyBinds1Scale.get() / 100f;
            if (scaleMul < 0.5f) scaleMul = 0.5f;
            if (scaleMul > 2.0f) scaleMul = 2.0f;
        }

        int bodyBg = bodyBg();
        int headerBg = headerBg();
        int headerAlpha = (headerBg >>> 24) & 0xFF;
        int bodyAlpha   = (bodyBg   >>> 24) & 0xFF;
        boolean shadow  = Interface.INSTANCE != null && Interface.INSTANCE.keyBinds1Shadow.isState();
        boolean outline = Interface.INSTANCE != null && Interface.INSTANCE.keyBinds1Outline.isState();
        float panelH = (visible == 0) ? HEADER_H : height;

        m.push();
        m.translate(x, y, 0);
        m.scale(scaleMul, scaleMul, 1f);
        m.translate(-x, -y, 0);

        float scaledWidth = width * scaleMul;
        float scaledPanelH = panelH * scaleMul;

        if (isBlurEnabled()) {
            // blur отключён — вызывает краш
        }
        if (visible == 0) {
            if (headerAlpha > 0) RenderUtils.drawRoundedRect(m, x, y, width, HEADER_H, RADIUS, headerBg);
        } else {
            float bodyY = y + HEADER_H;
            float bodyHeight = height - HEADER_H;
            if (headerAlpha > 0) {
                ScissorUtils.push();
                ScissorUtils.setFromComponentCoordinates(x, y, scaledWidth, HEADER_H * scaleMul);
                RenderUtils.drawRoundedRect(m, x, y, width, HEADER_H + 1f, RADIUS, RADIUS, 0f, 0f, headerBg);
                ScissorUtils.pop();
                ScissorUtils.unset();
            }
            if (bodyAlpha > 0) {
                ScissorUtils.push();
                ScissorUtils.setFromComponentCoordinates(x, bodyY, scaledWidth, bodyHeight * scaleMul);
                RenderUtils.drawRoundedRect(m, x, bodyY - 1f, width, bodyHeight + 1f, 0f, 0f, RADIUS, RADIUS, bodyBg);
                ScissorUtils.pop();
                ScissorUtils.unset();
            }
        }
        if (outline) {
            int outlineColor = ColorUtils.rgba(0, 0, 0, 220);
            RenderUtils.drawRoundedRectOutline(m, x, y, width, panelH, RADIUS, RADIUS, RADIUS, RADIUS, 0.6f, outlineColor);
        }
        if (shadow) {
            float strength = Interface.INSTANCE != null ? Interface.INSTANCE.keyBinds1ShadowStrength.get() : 70f;
            int sa = (int) (255f * strength / 100f);
            if (sa < 0) sa = 0; if (sa > 255) sa = 255;
            RenderUtils.drawShadowOuter(m, x, y, width, panelH, RADIUS, 10f, ColorUtils.rgba(0, 0, 0, sa));
        }

        int accent = accentColor();
        float gx = x + (width - headerGroupW) / 2f;
        hIcon.drawStringWithShadow(m, HEADER_GLYPH, gx, y + ICON_BASE_Y, accent);
        hTitle.drawStringWithShadow(m, TITLE,
                gx + hIcon.getWidth(HEADER_GLYPH) + ICON_GAP, y + TEXT_BASE_Y, TEXT_WHITE);

        if (visible > 0) {
            ScissorUtils.push();
            ScissorUtils.setFromComponentCoordinates(x, y, scaledWidth, height * scaleMul);

            float rowY = y + HEADER_H + BODY_PAD_TOP;
            for (Module module : ModuleClass.INSTANCE.getObject()) {
                if (!isBound(module)) continue;
                float a = getAnimation(module).getValue();
                if (a <= 0.01f) continue;

                int alpha = (int) (255 * a);
                int rowTextColor = ColorUtils.setAlphaColor(TEXT_WHITE, alpha);
                int accentColor  = ColorUtils.setAlphaColor(accent, alpha);

                String catGlyph = module.getCategory() == null ? "" : module.getCategory().getIcons();
                float curX = x + PAD_X;
                if (!catGlyph.isEmpty()) {
                    rIcon.drawStringWithShadow(m, catGlyph, curX, rowY + ICON_BASE_Y, accentColor);
                    curX += rIcon.getWidth(catGlyph) + ICON_GAP;
                }

                float textBaseY = rowY + TEXT_BASE_Y;
                rName.drawStringWithShadow(m, module.getDisplayName(), curX, textBaseY, rowTextColor);

                String key = toEnglish(KeyBoardUtils.getKeyName(module.getKey()));
                float keyX = x + width - PAD_X - rKey.getWidth(key);
                rKey.drawStringWithShadow(m, key, keyX, textBaseY, accentColor);

                rowY += ROW_H * a;
            }
            ScissorUtils.pop();
            ScissorUtils.unset();
        }

        m.pop();

        draggable.setWidth(scaledWidth);
        draggable.setHeight(height * scaleMul);
    }
}
