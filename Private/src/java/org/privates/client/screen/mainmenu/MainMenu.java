package org.privates.client.screen.mainmenu;

import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.util.Namespaced;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.Excellent;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.player.ChangelogGui;
import org.privates.client.managers.module.impl.player.ReadmeGui;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer2;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.privates.client.utils.math.Mathf.isHover;

public class MainMenu extends Screen implements IMinecraft {
    private static MainMenu instance;
    private long startTime;
    private final Map<String, Animation> menuButtonAnimations = new HashMap<>();
    private final Animation settingsAnimation = new Animation();
    private boolean showSettings = false;
    private final List<DecorationElement> newYearDecorations = new ArrayList<>();
    private final List<DecorationElement> decorations = new ArrayList<>();
    private final List<DecorationElement> waifus = new ArrayList<>();
    private final List<BackgroundElement> backgrounds = new ArrayList<>();
    private static final Map<String, Boolean> elementStates = new HashMap<>();
    private static final Map<String, Float> elementPosX = new HashMap<>();
    private static final Map<String, Float> elementPosY = new HashMap<>();
    private final Map<String, Animation> elementAnimations = new HashMap<>();
    private final Map<String, Float> elementScale = new HashMap<>();
    private final Map<String, Float> elementRotation = new HashMap<>();
    private final Map<String, Float> textScrollOffset = new HashMap<>();
    private final Map<String, Boolean> textScrollDirection = new HashMap<>();
    private final Map<String, Long> textScrollPauseTime = new HashMap<>();
    private final Map<String, Object> customSettings = new HashMap<>();
    private final List<Setting> settings = new ArrayList<>();

    private enum SettingsTab {NEWYEAR_DECORATIONS, DECORATIONS, WAIFUS, BACKGROUNDS, CUSTOMIZATION}

    private SettingsTab currentTab = SettingsTab.NEWYEAR_DECORATIONS;
    private final Animation tabSwitchAnimation = new Animation();
    private DecorationElement draggingElement = null;
    private float dragOffsetX = 0, dragOffsetY = 0;
    private boolean isDraggingEnabled = false;
    private float scrollOffset = 0, scrollVelocity = 0, maxScrollOffset = 0;
    private DecorationElement previewElement = null;
    private BackgroundElement previewBackground = null;
    private float previewZoom = 1.0f;
    private final Animation helpTextAnimation = new Animation();

    public enum Rarity {
        COMMON(0xFFAAAAAA, "a"), RARE(0xFF5555FF, "b"), EPIC(0xFFAA00FF, "c"), LEGENDARY(0xFFFFAA00, "d"), SEASONAL(-1, "e");
        final int color;
        final String icon;

        Rarity(int c, String i) {
            color = c;
            icon = i;
        }
    }

    private static class DecorationElement {
        String texture, name, displayName, icon;
        float posX, posY, baseSize;
        Rarity rarity;

        DecorationElement(String t, String d, float x, float y, float s, Rarity r, String i) {
            texture = t;
            name = t;
            displayName = d;
            posX = x;
            posY = y;
            baseSize = s;
            rarity = r;
            icon = i;
        }
    }

    private static class BackgroundElement {
        String texture, name;
        Rarity rarity;
        String icon;

        BackgroundElement(String t, String n, Rarity r, String i) {
            texture = t;
            name = n;
            rarity = r;
            icon = i;
        }
    }

    private abstract static class Setting {
        String name;
        float x, y, width, height;

        Setting(String n) {
            name = n;
        }

        abstract void render(MatrixStack ms, int mouseX, int mouseY, float scale, float alpha);

        abstract boolean mouseClicked(double mx, double my, int btn);

        abstract void mouseDragged(double mx, double my);

        abstract void mouseReleased(double mx, double my);
    }

    private static class BooleanSetting extends Setting {
        boolean value;
        Animation animation = new Animation();

        BooleanSetting(String n, boolean v) {
            super(n);
            value = v;
        }

        void render(MatrixStack ms, int mx, int my, float s, float a) {
            animation.run(value ? 1.0 : 0.0, 0.3, Easings.CUBIC_OUT, true);
            float ap = (float) animation.getValue();
            int bg = ColorUtil.interpolateColor(ColorUtil.getColor(60, 60, 60), Theme.getInstance().clientColor(), ap);
            RenderUtil.Rounded.roundedRect(ms, x, y, width, height, ColorUtil.multAlpha(bg, a), Round.of(5f * s));
            if (value)
                Fonts.icon_deshux.drawCenter(ms, "n", x + width / 2f, y + height / 2f - 6f * s, ColorUtil.multAlpha(-1, a * ap), 14f * s);
            Fonts.SF_SEMIBOLD.draw(ms, name, x - Fonts.SF_SEMIBOLD.getWidth(name, 10f * s) - 10f * s, y + height / 2f - 5f * s, ColorUtil.multAlpha(-1, a * 0.8f), 10f * s);
        }

        boolean mouseClicked(double mx, double my, int btn) {
            if (isHover(mx, my, x, y, width, height)) {
                value = !value;
                return true;
            }
            return false;
        }

        void mouseDragged(double mx, double my) {
        }

        void mouseReleased(double mx, double my) {
        }
    }

    private static class SliderSetting extends Setting {
        float value, min, max;
        boolean dragging = false;
        String suffix;

        SliderSetting(String n, float v, float mn, float mx, String sf) {
            super(n);
            value = v;
            min = mn;
            max = mx;
            suffix = sf;
        }

        void render(MatrixStack ms, int mx, int my, float s, float a) {
            int theme = Theme.getInstance().clientColor();
            RenderUtil.Rounded.roundedRect(ms, x, y, width, height, ColorUtil.multAlpha(ColorUtil.getColor(30, 35, 45), a * 0.5f), Round.of(4f * s));
            float fillW = (value - min) / (max - min) * width;
            RenderUtil.Rounded.roundedRect(ms, x, y, fillW, height, ColorUtil.multAlpha(theme, a * 0.4f), Round.of(4f * s));
            RenderUtil.Rounded.roundedOutline(ms, x, y, width, height, s, ColorUtil.multAlpha(theme, a * 0.6f), Round.of(4f * s));
            String txt = name + ": " + String.format("%.1f", value) + suffix;
            Fonts.SF_SEMIBOLD.drawCenter(ms, txt, x + width / 2f, y + height / 2f - 5f * s, ColorUtil.multAlpha(-1, a), 9f * s);
        }

        boolean mouseClicked(double mx, double my, int btn) {
            if (isHover(mx, my, x, y, width, height)) {
                dragging = true;
                updateValue(mx);
                return true;
            }
            return false;
        }

        void mouseDragged(double mx, double my) {
            if (dragging) updateValue(mx);
        }

        void mouseReleased(double mx, double my) {
            dragging = false;
        }

        void updateValue(double mx) {
            float p = (float) Math.max(0, Math.min(1, (mx - x) / width));
            value = min + (max - min) * p;
        }
    }

    public MainMenu() {
        super(StringTextComponent.EMPTY);
        instance = this;
        initElements();
        loadSettings();
    }

    private void initElements() {
        newYearDecorations.add(new DecorationElement("snowman.png", "Снеговик", 0.15f, 0.85f, 120f, Rarity.SEASONAL, "a"));
        newYearDecorations.add(new DecorationElement("elka.png", "Новогодняя Ёлка", 0.85f, 0.75f, 120f, Rarity.SEASONAL, "b"));
        newYearDecorations.add(new DecorationElement("nosok.png", "Новогодний Носок", 0.05f, 0.45f, 120f, Rarity.SEASONAL, "c"));
        decorations.add(new DecorationElement("kotik.png", "Котик", 0.2f, 0.2f, 100f, Rarity.RARE, "de"));
        decorations.add(new DecorationElement("sobaka.png", "Собочка", 0.2f, 0.2f, 100f, Rarity.RARE, "dw"));
        decorations.add(new DecorationElement("dangelexa.png", "DangerLyoha", 0.2f, 0.2f, 100f, Rarity.RARE, "dq"));
        decorations.add(new DecorationElement("mem.png", "Мем 1", 0.2f, 0.2f, 100f, Rarity.RARE, "da"));
        decorations.add(new DecorationElement("mem2.png", "Мем 2", 0.2f, 0.2f, 100f, Rarity.RARE, "df"));
        decorations.add(new DecorationElement("mem3.png", "Мем 3", 0.2f, 0.2f, 100f, Rarity.RARE, "dd"));
        decorations.add(new DecorationElement("mem4.png", "мем 4", 0.2f, 0.2f, 100f, Rarity.RARE, "dg"));
        waifus.add(new DecorationElement("h.png", "Waifu 1", 0.1f, 0.7f, 150f, Rarity.EPIC, "g"));
        waifus.add(new DecorationElement("h2.png", "Waifu 2", 0.9f, 0.7f, 150f, Rarity.LEGENDARY, "h"));
        waifus.add(new DecorationElement("h3.png", "Waifu 3", 0.1f, 0.3f, 150f, Rarity.RARE, "i"));
        waifus.add(new DecorationElement("h4.png", "Waifu 4", 0.9f, 0.3f, 150f, Rarity.COMMON, "j"));
        waifus.add(new DecorationElement("h5.png", "Waifu 5", 0.3f, 0.5f, 150f, Rarity.EPIC, "k"));
        waifus.add(new DecorationElement("h6.png", "Waifu 6", 0.7f, 0.5f, 150f, Rarity.RARE, "l"));
        waifus.add(new DecorationElement("h7.png", "Waifu 7", 0.5f, 0.2f, 150f, Rarity.LEGENDARY, "m"));
        backgrounds.add(new BackgroundElement("background2.png", "Default BG", Rarity.COMMON, "n"));
        backgrounds.add(new BackgroundElement("oboi1.png", "Улучшеная волна", Rarity.RARE, "o"));
        backgrounds.add(new BackgroundElement("oboi2.png", "Glow Волна", Rarity.EPIC, "p"));
        backgrounds.add(new BackgroundElement("oboi3.png", "Листья", Rarity.EPIC, "q"));
        backgrounds.add(new BackgroundElement("oboi4.png", "Ворон", Rarity.COMMON, "r"));
        backgrounds.add(new BackgroundElement("oboi5.png", "Новогодний", Rarity.SEASONAL, "s"));
        backgrounds.add(new BackgroundElement("oboi6.png", "Шумоподавление", Rarity.EPIC, "t"));
        backgrounds.add(new BackgroundElement("oboi7.png", "Странный", Rarity.RARE, "u"));
        backgrounds.add(new BackgroundElement("oboi8.png", "Улучшеный", Rarity.RARE, "v"));
        backgrounds.add(new BackgroundElement("oboi9.png", "Красивый Майнкрафт", Rarity.LEGENDARY, "w"));
        backgrounds.add(new BackgroundElement("oboi10.png", "Майнкрафт 1", Rarity.LEGENDARY, "z"));
        backgrounds.add(new BackgroundElement("oboi11.png", "Майнкрафт 2 ", Rarity.LEGENDARY, "1"));
        backgrounds.add(new BackgroundElement("oboi12.png", "Майнкрафт 3", Rarity.LEGENDARY, "2"));
        backgrounds.add(new BackgroundElement("oboi13.png", "Майнкрафт 4 ", Rarity.LEGENDARY, "3"));
        backgrounds.add(new BackgroundElement("oboi14.png", "Майнкрафт 5", Rarity.LEGENDARY, "4"));
        backgrounds.add(new BackgroundElement("oboi15.png", "Майнкрафт 6", Rarity.LEGENDARY, "5"));
        backgrounds.add(new BackgroundElement("oboi16.png", "Майнкрафт 7", Rarity.LEGENDARY, "6"));
        backgrounds.add(new BackgroundElement("oboi17.png", "Майнкрафт 8", Rarity.LEGENDARY, "7"));
        backgrounds.add(new BackgroundElement("oboi19.png", "Майнкрафт 10", Rarity.LEGENDARY, "9"));
        backgrounds.add(new BackgroundElement("oboi20.png", "Пиздатая альтуха", Rarity.LEGENDARY, "92"));
        backgrounds.add(new BackgroundElement("oboi21.png", "Вздрочнуть и убрать", Rarity.LEGENDARY, "93"));
        backgrounds.add(new BackgroundElement("oboi22.png", "Windows glow up", Rarity.LEGENDARY, "94"));
        backgrounds.add(new BackgroundElement("oboi23.png", "Пиздатые сиськи", Rarity.LEGENDARY, "95"));
        backgrounds.add(new BackgroundElement("oboi24.png", "Сиськи пиздатые", Rarity.LEGENDARY, "96"));
        backgrounds.add(new BackgroundElement("oboi25.png", "Альтушка жопа", Rarity.LEGENDARY, "97"));
        backgrounds.add(new BackgroundElement("oboi26.png", "Пиздатый майнкрафт", Rarity.LEGENDARY, "98"));
        backgrounds.add(new BackgroundElement("oboi27.png", "Пиздатый майнкрафт 2", Rarity.LEGENDARY, "985"));


        List<DecorationElement> all = new ArrayList<>();
        all.addAll(newYearDecorations);
        all.addAll(decorations);
        all.addAll(waifus);
        for (DecorationElement e : all) {
            elementStates.putIfAbsent(e.name, false);
            elementPosX.putIfAbsent(e.name, e.posX);
            elementPosY.putIfAbsent(e.name, e.posY);
            elementAnimations.put(e.name, new Animation());
            elementScale.put(e.name, 0f);
            elementRotation.put(e.name, 0f);
            textScrollOffset.put(e.name, 0f);
            textScrollDirection.put(e.name, true);
            textScrollPauseTime.put(e.name, 0L);
        }
    }

    private void initSettings() {
    }

    private void loadSettings() {
        try {
            Map<String, Object> ms = Excellent.inst().configManager().getMainMenuSettings();
            if (ms == null || ms.isEmpty()) return;

            for (Map.Entry<String, Object> e : ms.entrySet()) {
                String k = e.getKey();
                if (k.startsWith("element_state_")) elementStates.put(k.substring(14), (Boolean) e.getValue());
                else if (k.startsWith("element_posX_"))
                    elementPosX.put(k.substring(13), ((Number) e.getValue()).floatValue());
                else if (k.startsWith("element_posY_"))
                    elementPosY.put(k.substring(13), ((Number) e.getValue()).floatValue());
                else if (k.startsWith("custom_")) customSettings.put(k.substring(7), e.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveSettings() {
        try {
            Map<String, Object> ms = new HashMap<>();
            elementStates.forEach((k, v) -> ms.put("element_state_" + k, v));
            elementPosX.forEach((k, v) -> ms.put("element_posX_" + k, v));
            elementPosY.forEach((k, v) -> ms.put("element_posY_" + k, v));
            customSettings.forEach((k, v) -> ms.put("custom_" + k, v));
            Excellent.inst().configManager().saveMainMenuSettings(ms);
            Excellent.inst().configManager().set();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose() {
        saveSettings();
        super.onClose();
    }

    public static MainMenu getInstance() {
        return instance;
    }

    private float getResolutionScale() {
        return mc.getMainWindow().getWidth() >= 3840 ? 1.5f : mc.getMainWindow().getWidth() >= 2560 ? 1.0f : mc.getMainWindow().getWidth() >= 1920 ? 0.75f : 0.65f;
    }

    @Override
    protected void init() {
        super.init();
        startTime = System.currentTimeMillis();
        String[] items = {"Одиночный", "Мульти", "Аккаунты", "README", "Настройки", "Выход"};
        for (String i : items) {
            Animation a = new Animation();
            a.set(0.0);
            menuButtonAnimations.put(i, a);
        }
        tabSwitchAnimation.set(1.0);
    }

    @Override
    public void render(MatrixStack ms, int mx, int my, float partialTicks) {
        long ct = System.currentTimeMillis();
        float te = ct - startTime;
        menuButtonAnimations.values().forEach(Animation::update);
        settingsAnimation.update();
        tabSwitchAnimation.update();
        elementAnimations.values().forEach(Animation::update);
        helpTextAnimation.update();
        updateElementAnimations();
        updateDragging(mx, my);
        updateTextScrolling();
        float lp = Math.min(1f, te / 500f), dp = Math.min(1f, Math.max(0f, (te - 200f) / 500f)), ap = Math.min(1f, Math.max(0f, (te - 400f) / 500f)), mp = Math.min(1f, Math.max(0f, (te - 800f) / 500f));
        float s = getResolutionScale();
        int th = Theme.getInstance().clientColor(), primaryText = ColorUtil.getColor(200, 220, 255), secondaryText = ColorUtil.getColor(180, 200, 240);
        RenderUtil.drawBackground(ms);
        ms.push();
        renderElements(ms, s);
        renderSettingsButton(ms, mx, my, th, primaryText, s, mp);
        renderChangelogButton(ms, mx, my, th, primaryText, s, mp);
        renderLogo(ms, s, lp);
        renderDescription(ms, secondaryText, s, dp);
        renderAdvantages(ms, th, primaryText, secondaryText, s, ap);
        renderMenuButtons(ms, mx, my, th, primaryText, s, mp);
        ms.pop();
        if (showSettings || settingsAnimation.getValue() > 0.01)
            renderSettings(ms, mx, my, th, primaryText, secondaryText, s);
        if (previewElement != null || previewBackground != null) renderPreview(ms, mx, my, th, primaryText, s);
        super.render(ms, mx, my, partialTicks);
    }

    private void updateTextScrolling() {
        long ct = System.currentTimeMillis();
        List<DecorationElement> all = new ArrayList<>();
        all.addAll(newYearDecorations);
        all.addAll(decorations);
        all.addAll(waifus);
        for (DecorationElement e : all) updateTextScroll(e, ct);
    }

    private void updateTextScroll(DecorationElement e, long ct) {
        String k = e.name;
        float s = getResolutionScale(), aw = (600f * s - 70f * s) / 2f - 120f * s, tw = Fonts.SF_BOLD.getWidth(e.displayName, 11f * s);
        if (tw <= aw) {
            textScrollOffset.put(k, 0f);
            return;
        }
        Long pt = textScrollPauseTime.get(k);
        if (pt != null && ct < pt) return;
        Boolean fwd = textScrollDirection.get(k);
        if (fwd == null) fwd = true;
        float co = textScrollOffset.getOrDefault(k, 0f), mo = tw - aw + 20f * s, spd = 0.5f;
        if (fwd) {
            co += spd;
            if (co >= mo) {
                co = mo;
                textScrollDirection.put(k, false);
                textScrollPauseTime.put(k, ct + 1500);
            }
        } else {
            co -= spd;
            if (co <= 0) {
                co = 0;
                textScrollDirection.put(k, true);
                textScrollPauseTime.put(k, ct + 1500);
            }
        }
        textScrollOffset.put(k, co);
    }

    private void updateDragging(int mx, int my) {
        if (draggingElement != null && isDraggingEnabled) {
            float npx = (mx + dragOffsetX) / (float) width, npy = (my + dragOffsetY) / (float) height;
            elementPosX.put(draggingElement.name, Math.max(0.05f, Math.min(0.95f, npx)));
            elementPosY.put(draggingElement.name, Math.max(0.05f, Math.min(0.95f, npy)));
        }
    }

    private void updateElementAnimations() {
        float t = System.currentTimeMillis() / 1000f;
        List<DecorationElement> all = new ArrayList<>();
        all.addAll(newYearDecorations);
        all.addAll(decorations);
        all.addAll(waifus);
        for (DecorationElement e : all) {
            boolean en = elementStates.getOrDefault(e.name, false);
            Animation a = elementAnimations.get(e.name);
            a.run(en ? 1.0 : 0.0, 0.3, Easings.CUBIC_OUT, true);
            float av = (float) a.getValue(), br = (float) Math.sin(t * 1.5f + e.name.hashCode()) * 0.05f + 0.95f, ts = av * br, cs = elementScale.get(e.name);
            elementScale.put(e.name, Mathf.fast(cs, ts, 8));
            if (e.name.contains("snowman") || e.name.contains("elka") || e.name.contains("nosok"))
                elementRotation.put(e.name, (float) Math.sin(t * 2f + e.name.hashCode()) * 2f * av);
            else elementRotation.put(e.name, 0f);
        }
    }

    private void renderElements(MatrixStack ms, float s) {
        List<DecorationElement> all = new ArrayList<>();
        all.addAll(newYearDecorations);
        all.addAll(decorations);
        all.addAll(waifus);
        for (DecorationElement e : all) {
            float es = elementScale.get(e.name);
            if (es < 0.01f) continue;
            float r = elementRotation.get(e.name), px = elementPosX.getOrDefault(e.name, e.posX), py = elementPosY.getOrDefault(e.name, e.posY);
            float esz = e.baseSize * s * es, ex = width * px - esz / 2f, ey = height * py - esz / 2f;
            ms.push();
            float cx = ex + esz / 2f, cy = ey + esz / 2f;
            ms.translate(cx, cy, 0);
            ms.rotate(net.minecraft.util.math.vector.Vector3f.ZP.rotationDegrees(r));
            ms.translate(-cx, -cy, 0);
            RenderUtil.drawImage(ms, new Namespaced("texture/" + e.texture), ex, ey, esz, esz, true, -1);
            ms.pop();
        }
    }

    private void renderSettingsButton(MatrixStack ms, int mx, int my, int ac, int pt, float s, float p) {
        float bs = 40f * s, bx = width - bs - 15f * s, by = 15f * s;
        boolean h = isHover(mx, my, bx, by, bs, bs);
        BlurRenderer2.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(ms, bx, by, bs, bs, ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, 20), p), Round.of(6.3f * s)));
        BlurRenderer2.draw(6);
        RenderUtil.Rounded.roundedRect(ms, bx, by, bs, bs, ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, 3), p), Round.of(6.3f * s));
        RenderUtil.Rounded.roundedOutline(ms, bx, by, bs, bs, 1.26f * s, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 50), p), Round.of(6.3f * s));
        int ic = ColorUtil.interpolateColor(pt, ac, h ? 1.0f : 0.0f);
        Fonts.iconf.drawCenter(ms, "g", bx - 2 + bs / 2f, by - 1 + bs / 2f - 8f * s, ColorUtil.multAlpha(ic, p), 20f * s);
    }

    private void renderSettings(MatrixStack ms, int mx, int my, int ac, int pt, int st, float s) {
        settingsAnimation.run(showSettings ? 1.0 : 0.0, 0.3, Easings.CUBIC_OUT, true);
        float ap = (float) settingsAnimation.getValue();
        if (ap < 0.01f) return;
        float pw = 700f * s, ph = 550f * s, px = width / 2f - pw / 2f, py = height / 2f - ph / 2f;
        ms.push();
        float pvx = px + pw / 2f, pvy = py + ph / 2f;
        ms.translate(pvx, pvy, 0);
        ms.scale(ap, ap, 1);
        ms.translate(-pvx, -pvy, 0);
        BlurRenderer2.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(ms, px, py, pw, ph, ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, 30), ap), Round.of(10f * s)));
        BlurRenderer2.draw(19);
        RenderUtil.Rounded.roundedRect(ms, px, py, pw, ph, ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, 30), ap), Round.of(10f * s));
        RenderUtil.Rounded.roundedOutline(ms, px, py, pw, ph, 1.5f * s, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 30), ap), Round.of(10f * s));
        Fonts.SF_BOLD.drawCenter(ms, "Настройки главного меню", px + pw / 2f, py + 20f * s, ColorUtil.multAlpha(pt, ap), 16f * s);
        renderHelpText(ms, mx, my, px, py, pt, s, ap);
        renderTabs(ms, mx, my, px, py, pw, ac, pt, s, ap);
        float cy = py + 95f * s, ch = ph - 115f * s;
        switch (currentTab) {
            case NEWYEAR_DECORATIONS:
                renderDecorationsList(ms, mx, my, px, cy, pw, ch, newYearDecorations, ac, pt, st, s, ap);
                break;
            case DECORATIONS:
                renderDecorationsList(ms, mx, my, px, cy, pw, ch, decorations, ac, pt, st, s, ap);
                break;
            case WAIFUS:
                renderDecorationsList(ms, mx, my, px, cy, pw, ch, waifus, ac, pt, st, s, ap);
                break;
            case BACKGROUNDS:
                renderBackgroundsGrid(ms, mx, my, px, cy, pw, ch, ac, pt, s, ap);
                break;
            case CUSTOMIZATION:
                renderCustomization(ms, mx, my, px, cy, pw, ch, ac, pt, st, s, ap);
                break;
        }
        ms.pop();
    }

    private void renderHelpText(MatrixStack ms, int mx, int my, float px, float py, int pt, float s, float a) {
        String txt = "ПКМ - Preview | ЛКМ - Вкл/Выкл | Перетаскивание - Позиция";
        float tx = px + 15f * s, ty = py + 48f * s;
        boolean h = isHover(mx, my, tx, ty, Fonts.SF_REGULAR.getWidth(txt, 8f * s), 10f * s);
        helpTextAnimation.run(h ? 1.0 : 0.0, 0.2, Easings.CUBIC_OUT, true);
        float ha = 0.3f + (float) helpTextAnimation.getValue() * 0.7f;
        Fonts.SF_REGULAR.draw(ms, txt, tx - 300, ty - 100, ColorUtil.multAlpha(pt, a * ha), 10f * s);
    }

    private void renderTabs(MatrixStack ms, int mx, int my, float px, float py, float pw, int ac, int pt, float s, float a) {
        float ty = py + 55f * s, tw = (pw - 60f * s) / 5f, th = 32f * s, tsp = 5f * s;
        String[] tn = {"Новогодние", "Украшения", "Тянки", "Фон", "Пока что не готово"};
        SettingsTab[] tabs = {SettingsTab.NEWYEAR_DECORATIONS, SettingsTab.DECORATIONS, SettingsTab.WAIFUS, SettingsTab.BACKGROUNDS, SettingsTab.CUSTOMIZATION};
        for (int i = 0; i < 5; i++) {
            float tx = px + 20f * s + i * (tw + tsp);
            boolean act = currentTab == tabs[i], h = isHover(mx, my, tx, ty, tw, th);
            int bg = act ? ColorUtil.multAlpha(ac, a * 0.3f) : ColorUtil.multAlpha(ColorUtil.getColor(30, 35, 45), a * (h ? 0.6f : 0.5f));
            RenderUtil.Rounded.roundedRect(ms, tx, ty, tw, th, bg, Round.of(6f * s));
            if (act)
                RenderUtil.Rounded.roundedOutline(ms, tx, ty, tw, th, 1.5f * s, ColorUtil.multAlpha(ac, a * 0.6f), Round.of(6f * s));
            int tc = act ? ColorUtil.multAlpha(ac, a) : ColorUtil.multAlpha(pt, a * 0.7f);
            Fonts.SF_SEMIBOLD.drawCenter(ms, tn[i], tx + tw / 2f, ty + th / 2f - 6f * s, tc, 11f * s);
        }
    }

    private void renderDecorationsList(MatrixStack ms, int mx, int my, float px, float cy, float pw, float ch, List<DecorationElement> els, int ac, int pt, int st, float s, float a) {
        float iw = (pw - 55f * s) / 2f, ih = 90f * s, sp = 15f * s, sx = px + 20f * s;
        int rows = (int) Math.ceil(els.size() / 2.0);
        float tch = rows * (ih + sp);
        maxScrollOffset = Math.max(0, tch - ch + sp);
        scrollVelocity = Mathf.fast(scrollVelocity, scrollOffset, 12);
        scrollOffset = Math.max(0, Math.min(maxScrollOffset, scrollOffset));
        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(px + 10f * s, cy, pw - 20f * s, ch);
        float sy = cy + 10f * s - scrollVelocity, tp = (float) tabSwitchAnimation.getValue();
        for (int i = 0; i < els.size(); i++) {
            int row = i / 2, col = i % 2;
            float ix = sx + col * (iw + sp), iy = sy + row * (ih + sp);
            if (iy + ih < cy || iy > cy + ch) continue;
            renderElementItem(ms, mx, my, els.get(i), ix, iy, iw, ih, ac, pt, st, s, a * tp);
        }
        ScissorExp.unset();
        ScissorExp.pop();
        if (maxScrollOffset > 0) renderScrollbar(ms, px, cy, pw, ch, ac, s, a);
    }

    private void renderScrollbar(MatrixStack ms, float px, float cy, float pw, float ch, int ac, float s, float a) {
        float sw = 4f * s, sx = px + pw - 12f * s, sy = cy + 5f * s, sh = ch - 10f * s;
        RenderUtil.Rounded.roundedRect(ms, sx, sy, sw, sh, ColorUtil.multAlpha(ColorUtil.getColor(40, 45, 55), a * 0.3f), Round.of(2f * s));
        float sp = maxScrollOffset > 0 ? scrollVelocity / maxScrollOffset : 0, thh = Math.max(30f * s, sh * (sh / (sh + maxScrollOffset))), thy = sy + (sh - thh) * sp;
        RenderUtil.Rounded.roundedRect(ms, sx, thy, sw, thh, ColorUtil.multAlpha(ac, a * 0.6f), Round.of(2f * s));
    }

    private void renderElementItem(MatrixStack ms, int mx, int my, DecorationElement e, float x, float y, float w, float h, int ac, int pt, int st, float s, float a) {
        boolean en = elementStates.getOrDefault(e.name, false), hov = isHover(mx, my, x, y, w, h);
        boolean sg = (Boolean) customSettings.getOrDefault("showGlow", true), si = (Boolean) customSettings.getOrDefault("showIcons", true);
        RenderUtil.Rounded.roundedRect(ms, x, y, w, h, ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(20, 25, 35, 100), ColorUtil.multAlpha(ac, 50), hov ? 0.2f : 0.0f), a), Round.of(8f * s));
        RenderUtil.Rounded.roundedOutline(ms, x, y, w, h, s, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 50), a), Round.of(8f * s));
        float ps = 50f * s, pxx = x + 15f * s, py = y + h / 2f - ps / 2f;
        RenderUtil.Rounded.roundedRect(ms, pxx, py, ps, ps, ColorUtil.multAlpha(ColorUtil.getColor(10, 15, 25, 120), a), Round.of(6f * s));
        RenderUtil.drawImage(ms, new Namespaced("texture/" + e.texture), pxx + ps * 0.1f, py + ps * 0.1f, ps * 0.8f, ps * 0.8f, true, ColorUtil.multAlpha(-1, a));
        float tx = pxx + ps + 15f * s, ty = y + h / 2f - 15f * s, aw = w - (tx - x) - 50f * s;
        int rc = getRarityColor(e.rarity, a);
        if (si) {
            Fonts.icon.draw(ms, e.icon, tx, ty, rc, 11f * s);
            tx += 15f * s;
        }
        if (sg) drawGlow(ms, tx, ty, 11f * s, rc);
        float tw = Fonts.SF_BOLD.getWidth(e.displayName, 11f * s), so = textScrollOffset.getOrDefault(e.name, 0f);
        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(tx, ty, aw, 15f * s);
        Fonts.SF_BOLD.draw(ms, e.displayName, tx - so, ty, rc, 11f * s);
        ScissorExp.unset();
        ScissorExp.pop();
        Fonts.SF_REGULAR.draw(ms, "Перетащи для позиции", tx, ty + 15f * s, ColorUtil.multAlpha(st, a * 0.6f), 7f * s);
        float tgs = 30f * s, tgx = x + w - tgs - 15f * s, tgy = y + h / 2f - tgs / 2f;
        Animation an = elementAnimations.get(e.name);
        an.run(en ? 1.0 : 0.0, 0.3, Easings.CUBIC_OUT, true);
        float tgp = (float) an.getValue();
        RenderUtil.Rounded.roundedRect(ms, tgx, tgy, tgs, tgs, ColorUtil.multAlpha(ColorUtil.interpolateColor(ColorUtil.getColor(60, 60, 60), ac, tgp), a), Round.of(5f * s));
        if (en)
            Fonts.icon_deshux.drawCenter(ms, "n", tgx + tgs / 2f, tgy + tgs / 2f - 6f * s, ColorUtil.multAlpha(-1, a * tgp), 14f * s);
    }

    private void renderBackgroundsGrid(MatrixStack ms, int mx, int my, float px, float cy, float pw, float ch, int ac, int pt, float s, float a) {
        float iw = (pw - 100f * s) / 3f, ih = 100f * s, sp = 20f * s, sx = px + 30f * s;
        int rows = (int) Math.ceil(backgrounds.size() / 3.0);
        float tch = rows * (ih + sp);
        maxScrollOffset = Math.max(0, tch - ch + sp);
        scrollVelocity = Mathf.fast(scrollVelocity, scrollOffset, 12);
        scrollOffset = Math.max(0, Math.min(maxScrollOffset, scrollOffset));
        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(px + 10f * s, cy, pw - 20f * s, ch);
        float sy = cy + 10f * s - scrollVelocity;
        int cb = getBackgroundIndex();
        float tp = (float) tabSwitchAnimation.getValue();
        for (int i = 0; i < backgrounds.size(); i++) {
            int row = i / 3, col = i % 3;
            float ix = sx + col * (iw + sp), iy = sy + row * (ih + sp);
            if (iy + ih < cy || iy > cy + ch) continue;
            BackgroundElement bg = backgrounds.get(i);
            boolean sel = cb == i, h = isHover(mx, my, ix, iy, iw, ih);
            int ibg = sel ? ColorUtil.multAlpha(ac, a * tp * 0.4f) : ColorUtil.multAlpha(ColorUtil.getColor(20, 25, 35, 100), a * tp * (h ? 0.8f : 1.0f));
            RenderUtil.Rounded.roundedRect(ms, ix, iy, iw, ih, ibg, Round.of(8f * s));
            int ib = sel ? ColorUtil.multAlpha(ac, a * tp) : ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 50), a * tp);
            RenderUtil.Rounded.roundedOutline(ms, ix, iy, iw, ih, sel ? 2.5f * s : s, ib, Round.of(8f * s));
            RenderUtil.drawImage(ms, new Namespaced("texture/" + bg.texture), ix + 8f * s, iy + 8f * s, iw - 16f * s, ih - 16f * s, true, ColorUtil.multAlpha(-1, a * tp * 0.7f));
            boolean si = (Boolean) customSettings.getOrDefault("showIcons", true);
            int rc = getRarityColor(bg.rarity, a * tp);
            if (si) {
                Fonts.icon.draw(ms, bg.icon, ix + 8f * s, iy + 8f * s, rc, 12f * s);
            }
            Fonts.SF_SEMIBOLD.draw(ms, bg.name, ix + (si ? 25f : 10f) * s, iy + 10f * s, rc, 9f * s);
            if (sel) {
                float cs = 20f * s, cx = ix + iw - cs - 5f * s, cy2 = iy + 5f * s;
                RenderUtil.Rounded.roundedRect(ms, cx, cy2, cs, cs, ColorUtil.multAlpha(ac, a * tp), Round.of(4f * s));
                Fonts.icon_deshux.drawCenter(ms, "n", cx + cs / 2f, cy2 + cs / 2f - 5f * s, ColorUtil.multAlpha(-1, a * tp), 12f * s);
            }
        }
        ScissorExp.unset();
        ScissorExp.pop();
        if (maxScrollOffset > 0) renderScrollbar(ms, px, cy, pw, ch, ac, s, a);
    }

    private void renderCustomization(MatrixStack ms, int mx, int my, float px, float cy, float pw, float ch, int ac, int pt, int secondaryText, float s, float a) {
        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(px + 10f * s, cy, pw - 20f * s, ch);
        float sy = cy + 20f * s, sw = pw - 100f * s, sh = 35f * s, sx = px + 50f * s, spc = 15f * s;
        for (int i = 0; i < settings.size(); i++) {
            Setting setting = settings.get(i);
            setting.x = sx;
            setting.y = sy + i * (sh + spc);
            setting.width = setting instanceof BooleanSetting ? 30f * s : sw;
            setting.height = sh;
            setting.render(ms, mx, my, s, a);
        }
        ScissorExp.unset();
        ScissorExp.pop();
    }

    private void renderPreview(MatrixStack ms, int mx, int my, int ac, int pt, float s) {
        float pw = width * 0.7f, ph = height * 0.7f, px = width / 2f - pw / 2f, py = height / 2f - ph / 2f;
        BlurRenderer2.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(ms, px, py, pw, ph, ColorUtil.getColor(0, 0, 0, 100), Round.of(15f * s)));
        BlurRenderer2.draw(25);
        RenderUtil.Rounded.roundedRect(ms, px, py, pw, ph, ColorUtil.getColor(10, 15, 25, 200), Round.of(15f * s));
        RenderUtil.Rounded.roundedOutline(ms, px, py, pw, ph, 2f * s, ColorUtil.getColor(255, 255, 255, 80), Round.of(15f * s));
        if (previewElement != null) {
            float ps = previewElement.baseSize * s * previewZoom * 3f, pex = px + pw / 2f - ps / 2f, pey = py + ph / 2f - ps / 2f;
            RenderUtil.drawImage(ms, new Namespaced("texture/" + previewElement.texture), pex, pey, ps, ps, true, -1);
            Fonts.SF_BOLD.drawCenter(ms, previewElement.displayName, px + pw / 2f, py + 20f * s, pt, 18f * s);
        } else if (previewBackground != null) {
            float bw = pw - 40f * s, bh = ph - 80f * s;
            RenderUtil.drawImage(ms, new Namespaced("texture/" + previewBackground.texture), px + 20f * s, py + 60f * s, bw, bh, true, -1);
            Fonts.SF_BOLD.drawCenter(ms, previewBackground.name, px + pw / 2f, py + 20f * s, pt, 18f * s);
        }
        Fonts.SF_REGULAR.drawCenter(ms, "Ctrl+Колесико для масштабирования | ПКМ для закрытия", px + pw / 2f, py + ph - 25f * s, ColorUtil.getColor(180, 200, 220), 10f * s);
    }

    private int getRarityColor(Rarity r, float a) {
        if (r == Rarity.SEASONAL) {
            int c1 = 0xFFFF0000, c2 = 0xFF00FF00;
            long t = System.currentTimeMillis() % 2000;
            return ColorUtil.multAlpha(ColorUtil.interpolateColor(c1, c2, (float) t / 2000f), a);
        }
        return ColorUtil.multAlpha(r.color, a);
    }

    private void drawGlow(MatrixStack ms, float x, float y, float sz, int c) {
        float gi = ((Float) customSettings.getOrDefault("glowIntensity", 50f)) / 100f;
        for (int i = 0; i < 3; i++) {
            float off = (i + 1) * 0.5f;
            Fonts.SF_BOLD.draw(ms, "", x - off, y, ColorUtil.multAlpha(c, gi * 0.3f), sz);
            Fonts.SF_BOLD.draw(ms, "", x + off, y, ColorUtil.multAlpha(c, gi * 0.3f), sz);
            Fonts.SF_BOLD.draw(ms, "", x, y - off, ColorUtil.multAlpha(c, gi * 0.3f), sz);
            Fonts.SF_BOLD.draw(ms, "", x, y + off, ColorUtil.multAlpha(c, gi * 0.3f), sz);
        }
    }

    private int getBackgroundIndex() {
        String cb = RenderUtil.getCurrentBackground();
        for (int i = 0; i < backgrounds.size(); i++) if (backgrounds.get(i).texture.equals(cb)) return i;
        return 0;
    }

    private void renderLogo(MatrixStack ms, float s, float p) {
        float ls = 300.8f * s, lx = width / 2f - ls / 2f, ly = 40f * s;
        int tc = Theme.getInstance().clientColor();
        ms.push();
        float cx = lx + ls / 2f, cy = ly + ls / 2f;
        ms.translate(cx, cy, 0);
        float lsc = 0.85f + p * 0.15f;
        ms.scale(lsc, lsc, 1);
        ms.translate(-cx, -cy, 0);
        RenderUtil.drawImage(ms, new Namespaced("texture/logo.png"), lx, ly, ls, ls, true, tc);
        ms.pop();
    }

    private void renderDescription(MatrixStack ms, int st, float s, float p) {
        String txt = "Получай доминацию и удовольствие играя каждую минуту в этот клиент с намидоминация и лучшая кастомизация как с визуальной части так и с пвп в нашем чите насчитывается более 160 полезных функций";
        float dy = 323f * s, mw = 300f * s;
        String[] lns = wrapText(txt, mw, 8.6f * s, s);
        float th = lns.length * 11.9f * s, sy = dy - th / 2f;
        for (int i = 0; i < lns.length; i++) {
            float la = Math.min(1f, p * 1.5f - i * 0.1f);
            Fonts.SF_REGULAR.drawCenter(ms, lns[i], width / 2f, sy + i * 11.9f * s, ColorUtil.multAlpha(st, Math.max(0, la)), 11f * s);
        }
    }

    private void renderAdvantages(MatrixStack ms, int ac, int pt, int st, float s, float p) {
        float cw = 194.04f * s, ch = 94.5f * s, sp = 18.9f * s, rsp = 20f * s, tw = cw * 3 + sp * 2, cx = width / 2f - tw / 2f, fr = 429.66f * s, sr = fr + ch + rsp;
        String[] ttl = {"Оптимизация", "Обновления", "Работоспособность", "Обходы серверов", "Лучший из лучших", "Стабильность"};
        String[] dsc = {"Наш DLC оснащен оптимизированным функционалом, поэтому мы уверенно заявляем: проблемы с лагами в игре исключены.", "Наш DLC имеет скорые обновления, что обеспечивает стабильность работы нового функционала, и поддержку старого.", "В нашем DLC есть много функционала под популярные сервера, тем самым мы не заставим вам скучать.", "", "", "Стабильность работы наших сервисов 99,99%, это гарантирует качественную и беспрерывную работу нашего DLC."};
        float[] tsz = {12.79f * s, 15.02f * s, 12.79f * s, 12.79f * s, 12.79f * s, 12.79f * s};
        Fonts.SF_BOLD.drawCenter(ms, "Наши приемущества", width / 2f, fr - 36.54f * s, ColorUtil.multAlpha(pt, p), 13.86f * s);
        for (int i = 0; i < 6; i++) {
            int r = i / 3, c = i % 3;
            float cdx = cx + (cw + sp) * c, cdy = r == 0 ? fr : sr, ca = Math.min(1f, p * 1.2f - i * 0.08f);
            BlurRenderer2.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(ms, cdx, cdy, cw, ch, ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, 102), ca), Round.of(3.78f * s)));
            BlurRenderer2.draw(6);
            RenderUtil.Rounded.roundedRect(ms, cdx, cdy, cw, ch, ColorUtil.multAlpha(ColorUtil.getColor(25, 25, 25, 35), ca), Round.of(3.78f * s));
            RenderUtil.Rounded.roundedOutline(ms, cdx, cdy, cw, ch, 1.26f * s, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 25), ca), Round.of(3.78f * s));
            Fonts.SF_BOLD.drawCenter(ms, ttl[i], cdx + cw / 2f, cdy + 12.6f * s, ColorUtil.multAlpha(pt, ca), tsz[i]);
            float fsz = 8.93f * s;
            if (ttl[i].equals("Обходы серверов")) drawServerBypass(ms, cdx, cdy, ca, st, fsz, s);
            else if (ttl[i].equals("Лучший из лучших")) drawVisuals(ms, cdx, cdy, ca, ac, fsz, s);
            else {
                String[] dl = wrapText(dsc[i], cw - 18.9f * s, fsz, s);
                float ly = cdy + 34.65f * s;
                for (int j = 0; j < Math.min(dl.length, 6); j++)
                    Fonts.SF_REGULAR.drawCenter(ms, dl[j], cdx + cw / 2f, ly + j * 10.08f * s, ColorUtil.multAlpha(st, ca), fsz);
            }
        }
    }

    private void drawServerBypass(MatrixStack ms, float cx, float cy, float a, int sc, float fs, float s) {
        float sy = cy + 35f * s, ls = 12f * s, ccx = cx + 194.04f * s / 2f;
        String[] srv = {"holyworld", "spookytime", "lonygrief", "wellmine", "cakeworld", "slimeworld", "faketime", "spacetime", "funtime"};
        for (int i = 0; i < 3; i++) {
            float ry = sy + i * ls, rsx = ccx - 60f * s;
            for (int j = 0; j < 3; j++) {
                String sv = srv[i * 3 + j];
                float svx = rsx + j * 60f * s, svw = Fonts.SF_REGULAR.getWidth(sv, fs);
                Fonts.SF_REGULAR.draw(ms, sv, svx - svw / 2f, ry, ColorUtil.multAlpha(sc, a), fs);
            }
        }
    }

    private void drawVisuals(MatrixStack ms, float cx, float cy, float a, int ac, float fs, float s) {
        float sy = cy + 38f * s, ls = 12f * s, ccx = cx + 194.04f * s / 2f;
        String[] v1 = {"Каждая функция идеальна"}, v2 = {"Лучший из лучших"};
        drawVisualRow(ms, v1, ccx, sy, fs, a, ac, s);
        drawVisualRow(ms, v2, ccx, sy + ls, fs, a, ac, s);
    }

    private void drawVisualRow(MatrixStack ms, String[] vs, float cx, float y, float sz, float a, int ac, float s) {
        for (String v : vs) {
            float w = Fonts.SF_REGULAR.getWidth(v, sz);
            Fonts.SF_REGULAR.draw(ms, v, cx - w / 2f, y, ColorUtil.multAlpha(ac, a), sz);
        }
    }

    private void renderChangelogButton(MatrixStack ms, int mx, int my, int ac, int pt, float s, float p) {
        float bw = 125f * s, bh = 40f * s, bx = width / 2f - bw / 2f, by = 60f * s - bh - 10f * s;
        boolean h = isHover(mx, my, bx, by, bw, bh);
        BlurRenderer2.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(ms, bx, by, bw, bh, ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, 20), p), Round.of(6.3f * s)));
        BlurRenderer2.draw(25);
        RenderUtil.Rounded.roundedRect(ms, bx, by, bw, bh, ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, 3), p), Round.of(6.3f * s));
        RenderUtil.Rounded.roundedOutline(ms, bx, by, bw, bh, 1.26f * s, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 50), p), Round.of(6.3f * s));
        Fonts.SF_BOLD.drawCenter(ms, "ChangeLog", bx + bw / 2f, by - 1f + bh / 2f - 7f * s, ColorUtil.multAlpha(ColorUtil.interpolateColor(pt, ac, h ? 1.0f : 0.0f), p), 14f * s);
    }

    private void renderMenuButtons(MatrixStack ms, int mx, int my, int ac, int pt, float s, float p) {
        float bh = 27.31f * s, tw = 420.22f * s, sx = width / 2f - tw / 2f, by = height - 47.88f * s;
        BlurRenderer2.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(ms, sx, by, tw, bh, ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, 50), p), Round.of(3.78f * s)));
        BlurRenderer2.draw(10);
        RenderUtil.Rounded.roundedRect(ms, sx, by, tw, bh, ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, 50), p), Round.of(3.78f * s));
        RenderUtil.Rounded.roundedOutline(ms, sx, by, tw, bh, 0.945f * s, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 15), p), Round.of(3.78f * s));
        String[] mi = {"Одиночный", "Мульти", "Аккаунты", "README", "Настройки", "Выход"};
        float iw = tw / mi.length;
        for (int i = 0; i < mi.length; i++) {
            String it = mi[i];
            float ix = sx + i * iw;
            boolean h = isHover(mx, my, ix, by, iw, bh);
            Animation an = menuButtonAnimations.get(it);
            an.run(h ? 1.0 : 0.0, 0.3, Easings.EXPO_OUT);
            float hp = an.get();
            int tc = ColorUtil.interpolateColor(pt, ac, hp);
            float tsz = 9.56f * s + hp * 0.5f * s;
            ms.push();
            float tcx = ix + iw / 2f, tcy = by - s + bh / 2f - 3.78f * s, thh = Fonts.SF_SEMIBOLD.getHeight(it, tsz);
            ms.translate(tcx, tcy + thh / 2f, 0);
            float tsc = 1.0f + hp * 0.08f;
            ms.scale(tsc, tsc, 1);
            ms.translate(-tcx, -(tcy + thh / 2f), 0);
            Fonts.SF_SEMIBOLD.drawCenter(ms, it, tcx, tcy, ColorUtil.multAlpha(tc, p), tsz);
            ms.pop();
            if (i < mi.length - 1)
                RenderUtil.drawRect(ms, ix + iw - 0.5f * s, by + 6.3f * s, 0.5f * s, bh - 12.6f * s, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 38), p), ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 38), p), ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 38), p), ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 38), p), false, true);
        }
    }

    private String[] wrapText(String t, float mw, float fs, float s) {
        List<String> lns = new ArrayList<>();
        String[] wds = t.split(" ");
        StringBuilder cl = new StringBuilder();
        for (String w : wds) {
            String tl = cl.length() == 0 ? w : cl + " " + w;
            float lw = Fonts.SF_REGULAR.getWidth(tl, fs);
            if (lw > mw && cl.length() > 0) {
                lns.add(cl.toString());
                cl = new StringBuilder(w);
            } else {
                if (cl.length() > 0) cl.append(" ");
                cl.append(w);
            }
        }
        if (cl.length() > 0) lns.add(cl.toString());
        return lns.toArray(new String[0]);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        float s = getResolutionScale();
        if (previewElement != null || previewBackground != null) {
            if (btn == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                previewElement = null;
                previewBackground = null;
                previewZoom = 1.0f;
                return true;
            }
            return true;
        }
        if (!showSettings) {
            List<DecorationElement> all = new ArrayList<>();
            all.addAll(newYearDecorations);
            all.addAll(decorations);
            all.addAll(waifus);
            for (DecorationElement e : all) {
                if (!elementStates.getOrDefault(e.name, false)) continue;
                float px = elementPosX.getOrDefault(e.name, e.posX), py = elementPosY.getOrDefault(e.name, e.posY), es = e.baseSize * s, ex = width * px - es / 2f, ey = height * py - es / 2f;
                if (isHover(mx, my, ex, ey, es, es)) {
                    draggingElement = e;
                    isDraggingEnabled = true;
                    dragOffsetX = ex - (float) mx;
                    dragOffsetY = ey - (float) my;
                    return true;
                }
            }
        }
        float settingsBtnSize = 40f * s, settingsBtnX = width - settingsBtnSize - 15f * s, settingsBtnY = 15f * s;
        if (isHover(mx, my, settingsBtnX, settingsBtnY, settingsBtnSize, settingsBtnSize)) {
            showSettings = !showSettings;
            scrollOffset = 0;
            scrollVelocity = 0;
            return true;
        }
        if (showSettings) {
            float pw = 700f * s, ph = 550f * s, px = width / 2f - pw / 2f, py = height / 2f - ph / 2f;
            float ty = py + 55f * s, tw = (pw - 60f * s) / 5f, th = 32f * s, tsp = 5f * s;
            SettingsTab[] tabs = {SettingsTab.NEWYEAR_DECORATIONS, SettingsTab.DECORATIONS, SettingsTab.WAIFUS, SettingsTab.BACKGROUNDS, SettingsTab.CUSTOMIZATION};
            for (int i = 0; i < 5; i++) {
                float tx = px + 20f * s + i * (tw + tsp);
                if (isHover(mx, my, tx, ty, tw, th) && currentTab != tabs[i]) {
                    currentTab = tabs[i];
                    tabSwitchAnimation.set(0.0);
                    tabSwitchAnimation.run(1.0, 0.3, Easings.CUBIC_OUT, true);
                    scrollOffset = 0;
                    scrollVelocity = 0;
                    return true;
                }
            }
            if (currentTab == SettingsTab.CUSTOMIZATION) {
                for (Setting st : settings)
                    if (st.mouseClicked(mx, my, btn)) {
                        saveSettings();
                        return true;
                    }
            }
            if (currentTab == SettingsTab.BACKGROUNDS) {
                float cy = py + 95f * s, iw = (pw - 100f * s) / 3f, ih = 100f * s, sp = 20f * s, sx = px + 30f * s, sy = cy + 10f * s - scrollVelocity;
                for (int i = 0; i < backgrounds.size(); i++) {
                    int r = i / 3, c = i % 3;
                    float ix = sx + c * (iw + sp), iy = sy + r * (ih + sp);
                    if (isHover(mx, my, ix, iy, iw, ih)) {
                        if (btn == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                            previewBackground = backgrounds.get(i);
                            return true;
                        } else {
                            RenderUtil.setBackground(backgrounds.get(i).texture);
                            Excellent.inst().configManager().saveSelectedBackground(backgrounds.get(i).texture);
                            Excellent.inst().configManager().set();
                            return true;
                        }
                    }
                }
            } else if (currentTab == SettingsTab.NEWYEAR_DECORATIONS || currentTab == SettingsTab.DECORATIONS || currentTab == SettingsTab.WAIFUS) {
                List<DecorationElement> els = currentTab == SettingsTab.NEWYEAR_DECORATIONS ? newYearDecorations : currentTab == SettingsTab.DECORATIONS ? decorations : waifus;
                float cy = py + 95f * s, iw = (pw - 55f * s) / 2f, ih = 90f * s, sp = 15f * s, sx = px + 20f * s, sy = cy + 10f * s - scrollVelocity;
                for (int i = 0; i < els.size(); i++) {
                    int r = i / 2, c = i % 2;
                    float ix = sx + c * (iw + sp), iy = sy + r * (ih + sp);
                    if (isHover(mx, my, ix, iy, iw, ih)) {
                        DecorationElement e = els.get(i);
                        if (btn == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                            previewElement = e;
                            return true;
                        } else {
                            elementStates.put(e.name, !elementStates.getOrDefault(e.name, false));
                            saveSettings();
                            return true;
                        }
                    }
                }
            }
            if (isHover(mx, my, px, py, pw, ph)) return true;
        }
        float menuBtnHeight = 27.31f * s, totalWidth = 420.22f * s, menuStartX = width / 2f - totalWidth / 2f, menuBtnY = height - 47.88f * s;
        String[] mi = {"Одиночный", "Мульти", "Аккаунты", "README", "Настройки", "Выход"};
        float iw = totalWidth / mi.length;
        for (int i = 0; i < mi.length; i++) {
            float ix = menuStartX + i * iw;
            if (isHover(mx, my, ix, menuBtnY, iw, menuBtnHeight)) {
                handleMenuClick(mi[i]);
                return true;
            }
        }
        float cw = 125f * s, ch = 40f * s, cx = width / 2f - cw / 2f, cy = 60f * s - ch - 10f * s;
        if (isHover(mx, my, cx, cy, cw, ch)) {
            mc.displayScreen(new ChangelogGui());
            return true;
        }
        return super.mouseClicked(mx, my, btn);
    }

    @Override
    public void mouseMoved(double mx, double my) {
        if (currentTab == SettingsTab.CUSTOMIZATION) for (Setting st : settings) st.mouseDragged(mx, my);
        super.mouseMoved(mx, my);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        if (draggingElement != null) saveSettings();
        draggingElement = null;
        isDraggingEnabled = false;
        if (currentTab == SettingsTab.CUSTOMIZATION) for (Setting st : settings) st.mouseReleased(mx, my);
        return super.mouseReleased(mx, my, btn);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double d) {
        if (previewElement != null || previewBackground != null) {
            if (Screen.hasControlDown()) {
                previewZoom = Math.max(0.5f, Math.min(5.0f, previewZoom + (float) d * 0.1f));
                return true;
            }
        }
        if (showSettings) {
            float s = getResolutionScale(), pw = 700f * s, ph = 550f * s, px = width / 2f - pw / 2f, py = height / 2f - ph / 2f;
            if (isHover(mx, my, px, py, pw, ph)) {
                scrollOffset -= (float) d * 20f * s;
                scrollOffset = Math.max(0, Math.min(maxScrollOffset, scrollOffset));
                return true;
            }
        }
        return super.mouseScrolled(mx, my, d);
    }

    @Override
    public boolean keyPressed(int k, int sc, int m) {
        if (k == 256) {
            if (previewElement != null || previewBackground != null) {
                previewElement = null;
                previewBackground = null;
                previewZoom = 1.0f;
                return true;
            }
            if (showSettings) {
                showSettings = false;
                scrollOffset = 0;
                scrollVelocity = 0;
                return true;
            }
        }
        return super.keyPressed(k, sc, m);
    }

    private void handleMenuClick(String m) {
        switch (m) {
            case "Одиночный":
                mc.displayScreen(new WorldSelectionScreen(this));
                break;
            case "Мульти":
                mc.displayScreen(new MultiplayerScreen(this));
                break;
            case "Аккаунты":
                mc.displayScreen(Excellent.inst().accountGui());
                break;
            case "README":
                mc.displayScreen(new ReadmeGui());
                break;
            case "Настройки":
                mc.displayScreen(new OptionsScreen(this, minecraft.gameSettings));
                break;
            case "Выход":
                minecraft.shutdown();
                break;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}