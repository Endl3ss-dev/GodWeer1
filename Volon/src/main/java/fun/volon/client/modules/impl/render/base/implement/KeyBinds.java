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
import fun.volon.client.modules.impl.render.base.InterfaceProcessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyBinds extends InterfaceProcessing {
    private final Map<Module, AnimationUtils> animations = new HashMap<>();
    private final AnimationUtils widthAnimation = new AnimationUtils(60, 10.5f, Easings.QUAD_OUT);

    private static final Map<Character, Character> RU_TO_EN = new HashMap<>();
    static {
        String ru = "йцукенгшщзхъфывапролджэячсмитьбюЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ";
        String en = "qwertyuiop[]asdfghjkl;'zxcvbnm,.QWERTYUIOP[]ASDFGHJKL;'ZXCVBNM,.";
        int length = Math.min(ru.length(), en.length());
        for (int i = 0; i < length; i++) {
            RU_TO_EN.put(ru.charAt(i), en.charAt(i));
        }
    }

    public KeyBinds(Draggable draggable) {
        super(draggable);
    }

    private Font issue(int size) { return Fonts.getFont("suisse", size); }
    private Font icon(int size) { return Fonts.getFont("icon1", size); }
    private Font icons(int size) { return Fonts.getFont("icon", size); }

    private AnimationUtils getAnimation(Module module) {
        return animations.computeIfAbsent(module, m -> new AnimationUtils(0, 10.5f, Easings.QUAD_OUT));
    }

    private String toEnglish(String text) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            result.append(RU_TO_EN.getOrDefault(c, c));
        }
        return result.toString();
    }

    @Override
    public void onRender(EventRender.Default eventRender) {
        if (mc == null || mc.player == null) return;

        DefaultStyle(eventRender);
        super.onRender(eventRender);
    }

    public void DefaultStyle(EventRender.Default eventRender) {
        float x = draggable.getX();
        float y = draggable.getY();
        int colorTheme;
        if (!Volon.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            colorTheme = Volon.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        } else {
            colorTheme = ColorUtils.getThemeColor();
        }

        float targetWidth = 64;
        float targetHeight = 16;
        int visibleCount = 0;

        for (Module module : ModuleClass.INSTANCE.getObject()) {
            if (module.getKey() != -1 && module.getKey() != 0) {
                AnimationUtils anim = getAnimation(module);
                anim.update(module.isEnable() ? 1 : 0);
            }
        }

        for (Module module : ModuleClass.INSTANCE.getObject()) {
            if (module.getKey() != -1 && module.getKey() != 0) {
                AnimationUtils anim = getAnimation(module);
                float animValue = anim.getValue();

                if (animValue > 0.01f) {
                    visibleCount++;
                    String keyName = toEnglish(KeyBoardUtils.getKeyName(module.getKey()));
                    float keyWidth = issue(10).getWidth(keyName);
                    float moduleWidth = issue(12).getWidth(module.getDisplayName()) + keyWidth + 20;
                    if (moduleWidth > targetWidth) targetWidth = moduleWidth;
                    targetHeight += 12 * animValue;
                }
            }
        }

        if (visibleCount > 0) targetHeight += 2;

        widthAnimation.update(targetWidth);
        float width = widthAnimation.getValue() + 6;
        float height = targetHeight;

        drawHudBackground(eventRender.getContext().getMatrices(), x, y, width, height, colorTheme);

        // Иконка в начало, затем заголовок
        icon(16).draw(eventRender.getContext().getMatrices(), "f", x + 5f, y + 6.5f, colorTheme);
        issue(14).draw(eventRender.getContext().getMatrices(), "Keybinds", x + 5 + icon(16).getWidth("f") + 3f, y + 6f, -1);

        float offsetY = 18;
        for (Module module : ModuleClass.INSTANCE.getObject()) {
            if (module.getKey() != -1 && module.getKey() != 0) {
                AnimationUtils anim = getAnimation(module);
                float animValue = anim.getValue();

                if (animValue > 0.01f) {
                    ScissorUtils.push();
                    ScissorUtils.setFromComponentCoordinates(x, y, width, height);

                    String keyName = toEnglish(KeyBoardUtils.getKeyName(module.getKey()));
                    int alpha = (int) (255 * animValue);
                    int textColor = ColorUtils.rgba(255, 255, 255, alpha);
                    int accentColor = ColorUtils.setAlphaColor(colorTheme, alpha);

                    // Просто название модуля слева
                    issue(12).draw(eventRender.getContext().getMatrices(), module.getDisplayName(), x + 5.5f, y + 2 + offsetY, textColor);

                    // Клавиша справа
                    float keyX = x + width - issue(10).getWidth(keyName) - 7;
                    issue(10).draw(eventRender.getContext().getMatrices(), keyName, keyX, y + offsetY + 2.8f, accentColor);

                    offsetY += 12 * animValue;
                    ScissorUtils.pop();
                    ScissorUtils.unset();
                }
            }
        }

        draggable.setWidth(width);
        draggable.setHeight(height);
    }

    public void WaveStyle(EventRender.Default eventRender) {
        final MatrixStack context = eventRender.getContext().getMatrices();
        float x = draggable.getX();
        float y = draggable.getY();

        int time = (int) ((System.currentTimeMillis() % 2000) / 2000f * 360f);

        int leftTop = ColorUtils.getThemeColor(time);
        int leftBottom = ColorUtils.getThemeColor(time + 30);
        int centerTop = ColorUtils.getThemeColor(time + 90);
        int centerBottom = ColorUtils.getThemeColor(time + 120);
        int rightTop = ColorUtils.getThemeColor(time + 180);
        int rightBottom = ColorUtils.getThemeColor(time + 210);

        List<Module> activeModules = new ArrayList<>();
        for (final Module module : ModuleClass.INSTANCE.getObject()) {
            if (module.getKey() <= 0 || module.getKey() == 0) {
                module.getAnimka().update(0);
                continue;
            }
            module.getAnimka().update(module.isEnable() ? 1 : 0);
            if (module.getAnimka().getValue() > 0.01f) {
                activeModules.add(module);
            }
        }

        float targetWidth = 84f;
        float height = 18f;
        int visibleModules = 0;

        for (final Module module : activeModules) {
            float animValue = module.getAnimka().getValue();
            if (animValue <= 0.01f) continue;
            visibleModules++;

            String line = module.getDisplayName().toLowerCase() + " >> toggle";
            targetWidth = Math.max(targetWidth, issue(14).getWidth(line) + 7f);
            height += 12f * animValue;
        }

        widthAnimation.update(targetWidth);
        float animatedWidth = widthAnimation.getValue();

        if (visibleModules == 0) {
            float headerHeight = 18f;
            RenderUtils.drawWaveHudHeader(context, x, y, animatedWidth, 15, 0,
                    10, 10, leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);

            String title = "keybinds";
            float titleX = x + (animatedWidth - issue(15).getWidth(title)) / 2.0f;
            issue(15).drawStringWithShadow(eventRender.getContext().getMatrices(), title, titleX, y + 5, -1);

            draggable.setWidth(animatedWidth);
            draggable.setHeight(headerHeight);
            return;
        }

        RenderUtils.drawWaveHudPanel(context, x, y, animatedWidth, height, ColorUtils.rgba(25, 25, 25, 150),
                15, 0, 10, 10,
                leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);

        String title = "keybinds";
        float titleX = x + (animatedWidth - issue(15).getWidth(title)) / 2.0f;
        issue(15).drawStringWithShadow(eventRender.getContext().getMatrices(), title, titleX, y + 5, -1);

        float yOffset = 18f;
        for (final Module module : activeModules) {
            float animValue = module.getAnimka().getValue();
            if (animValue <= 0.01f) continue;

            ScissorUtils.push();
            ScissorUtils.setFromComponentCoordinates(x, y, animatedWidth, height);

            int alpha = (int) (255 * animValue);
            int textColor = ColorUtils.rgba(255, 255, 255, alpha);
            int accentColor = ColorUtils.getThemeColor();

            // Цветная полоска слева от названия модуля (волновой стиль)
            RenderUtils.drawRoundedRect(eventRender.getContext().getMatrices(), x + 5f, y + yOffset + 2f, 2.5f, 8f, 0.5f, accentColor);

            String text = module.getDisplayName().toLowerCase() + " >> " + (module.isEnable() ? "on" : "off");
            float textX = x + 11f;

            issue(14).draw(context, text, textX, y + yOffset + 2, textColor);

            yOffset += 12f * animValue;

            ScissorUtils.unset();
            ScissorUtils.pop();
        }

        draggable.setWidth(animatedWidth);
        draggable.setHeight(height);
    }
}