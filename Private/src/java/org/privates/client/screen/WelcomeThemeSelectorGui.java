package org.privates.client.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Namespaced;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.privates.client.Excellent;
import org.privates.client.managers.module.impl.client.FlatGui;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.screen.mainmenu.MainMenu;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer2;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.privates.client.api.interfaces.IMinecraft.mc;
import static org.privates.client.utils.math.Mathf.isHover;

public class WelcomeThemeSelectorGui extends Screen {

    // Цвета теперь берутся из Theme клиента
    private int BG_COLOR, TITLE_COLOR, SUB_COLOR;
    private int BTN_BG, BTN_BORDER, BTN_TEXT;
    private int BTN2_BG, BTN2_BORDER, BTN2_TEXT;
    private int CUR_LABEL, THEME_BORDER;
    private int CARD_BG, CARD_BORDER;
    
    private void updateColorsFromTheme() {
        int themeColor = Theme.getInstance().clientColor();
        int textColor = Theme.getInstance().textColor();
        int enabledColor = Theme.getInstance().enabledColor();
        
        BG_COLOR = ColorUtil.setAlpha(themeColor, 0xFF);
        TITLE_COLOR = ColorUtil.setAlpha(textColor, 0x99);
        SUB_COLOR = ColorUtil.setAlpha(textColor, 0xFF);
        
        BTN_BG = ColorUtil.setAlpha(themeColor, 0x4D);
        BTN_BORDER = ColorUtil.setAlpha(textColor, 0x80);
        BTN_TEXT = ColorUtil.setAlpha(textColor, 0xFF);
        
        BTN2_BG = ColorUtil.setAlpha(enabledColor, 0xFF);
        BTN2_BORDER = ColorUtil.setAlpha(textColor, 0xFF);
        BTN2_TEXT = ColorUtil.setAlpha(textColor, 0xFF);
        
        CUR_LABEL = ColorUtil.setAlpha(textColor, 0xFF);
        THEME_BORDER = ColorUtil.setAlpha(textColor, 0xFF);
        
        CARD_BG = ColorUtil.setAlpha(themeColor, 0x4D);
        CARD_BORDER = ColorUtil.setAlpha(textColor, 0x80);
    }

    private static class Anim {
        private double target, current;
        private long startTime;
        private double startVal, duration;

        Anim() {
            this.current = 0;
            this.target = 0;
            this.duration = 0;
        }

        void set(double v) {
            current = v;
            target = v;
            duration = 0;
        }

        void run(double t, double dur) {
            if (Math.abs(target - t) < 0.001) return;
            target = t;
            startVal = current;
            startTime = System.currentTimeMillis();
            duration = dur * 1000;
        }

        void update() {
            if (duration <= 0) {
                current = target;
                return;
            }
            double elapsed = System.currentTimeMillis() - startTime;
            double progress = Math.min(1.0, elapsed / duration);
            double ease = progress < 0.5 
                ? 4 * progress * progress * progress 
                : 1 - Math.pow(-2 * progress + 2, 3) / 2;
            current = startVal + (target - startVal) * ease;
            if (progress >= 1.0) {
                current = target;
                duration = 0;
            }
        }

        float get() {
            return (float) current;
        }

        boolean isFinished() {
            return duration <= 0;
        }
    }

    private static class ThemePreset {
        final String name;
        final int color, borderColor;
        final Anim appear = new Anim(), hover = new Anim();
        float x, y;

        ThemePreset(String n, int c, int b) {
            name = n;
            color = c;
            borderColor = b;
        }
    }

    private static class SelectionOption {
        final String texture, name, value;
        final Anim appear = new Anim(), hover = new Anim();
        float x, y, width, height;

        SelectionOption(String t, String n, String v) {
            texture = t;
            name = n;
            value = v;
        }
    }

    private enum Stage {WELCOME, THEME, CUSTOM_THEME, GUI, BACKGROUND}

    private final List<List<ThemePreset>> themePages = new ArrayList<>();
    private final List<SelectionOption> guis = new ArrayList<>(), backgrounds = new ArrayList<>();
    private final Anim open = new Anim(), close = new Anim(), title = new Anim(), subtitle = new Anim();
    private final Anim content = new Anim(), transition = new Anim(), preview = new Anim();
    private final Anim continueBtn = new Anim(), moreBtn = new Anim(), plusBtn = new Anim();
    private float gradientPhase = 0f;
    private long lastGradientUpdate = 0;
    private boolean closing = false, firstLaunch = false, previewClosing = false;
    private Stage stage = Stage.WELCOME;
    private ThemePreset hoveredTheme = null, selectedTheme = null;
    private SelectionOption hoveredOption = null, selectedOption = null, previewOption = null;
    private int currentThemePage = 0;
    private int customColor1 = Theme.getInstance().clientColor();
    private int customColor2 = Theme.getInstance().textColor();
    private int customColor3 = Theme.getInstance().enabledColor();
    private int activeColorPicker = -1;
    private float colorPickerHue = 0f, colorPickerSat = 1f, colorPickerBri = 1f;

    public WelcomeThemeSelectorGui() {
        super(new StringTextComponent("Theme Selector"));
        firstLaunch = !Excellent.inst().configManager().hasCompletedWelcome();
        initThemes();

        customColor1 = Theme.getInstance().clientColor();
        customColor2 = Theme.getInstance().textColor();
        customColor3 = Theme.getInstance().enabledColor();

        guis.add(new SelectionOption("gui1.png", "Click Gui", "Click Gui"));
        guis.add(new SelectionOption("gui2.png", "Umbrella Gui", "Umbrella Gui"));
        guis.add(new SelectionOption("gui3.png", "Cs Gui", "Cs Gui"));
        guis.add(new SelectionOption("gui4.png", "Panel", "Panel"));
        backgrounds.add(new SelectionOption("background2.png", "Фон 1", "background2.png"));
        backgrounds.add(new SelectionOption("oboi24.png", "Фон 2", "oboi24.png"));
        backgrounds.add(new SelectionOption("oboi23.png", "Фон 3", "oboi23.png"));
        backgrounds.add(new SelectionOption("oboi12.png", "Фон 4", "oboi12.png"));
    }

    private float getScale() {
        int w = mc.getMainWindow().getWidth(), h = mc.getMainWindow().getHeight();
        if (w >= 3840 && h >= 2160) return 1.5f;
        if (w >= 2560 && h >= 1440) return 1.0f;
        if (w >= 1920 && h >= 1080) return 0.75f;
        return 0.6f;
    }

    private void initThemes() {
        int[][][] data = {
                {{0xB31E3A5F, 0xFF2E4A6F}, {0xB33B5998, 0xFF4B69A8}, {0xB35B9BD5, 0xFF6BABF5}, {0xB387CEEB, 0xFF97DEFB}, {0xB340E0D0, 0xFF50F0E0}},
                {{0xB38B0000, 0xFF9B1010}, {0xB3DC143C, 0xFFEC2454}, {0xB3FF6B6B, 0xFFFF7B7B}, {0xB3FF7F50, 0xFFFF8F60}, {0xB3FF69B4, 0xFFFF79C4}},
                {{0xB3483D8B, 0xFF584D9B}, {0xB39370DB, 0xFFA380EB}, {0xB3BA6BFF, 0xFFCA7BFF}, {0xB3E6E6FA, 0xFFF6F6FF}, {0xB3800080, 0xFF901090}},
                {{0xB3006400, 0xFF107410}, {0xB332CD32, 0xFF42DD42}, {0xB390EE90, 0xFFA0FEA0}, {0xB373FF73, 0xFF83FF83}, {0xB398FF98, 0xFFA8FFA8}},
                {{0xB3FF8C00, 0xFFFF9C10}, {0xB3FFA500, 0xFFFFB510}, {0xB3FFCC80, 0xFFFFDC90}, {0xB3FFD700, 0xFFFFE710}, {0xB3FFFF80, 0xFFFFFF90}},
                {{0xB31A1A1A, 0xFF2A2A2A}, {0xB3333333, 0xFF434343}, {0xB3666666, 0xFF767676}, {0xB3999999, 0xFFA9A9A9}, {0xB3E0E0E0, 0xFFF0F0F0}}
        };
        String[][] names = {
                {"Тёмно-синий", "Синий", "Голубой", "Светло-голубой", "Бирюзовый"},
                {"Тёмно-красный", "Красный", "Светло-красный", "Коралловый", "Розовый"},
                {"Тёмно-фиолетовый", "Фиолетовый", "Светло-фиолетовый", "Лавандовый", "Пурпурный"},
                {"Тёмно-зелёный", "Зелёный", "Светло-зелёный", "Лаймовый", "Мятный"},
                {"Тёмно-оранжевый", "Оранжевый", "Светло-оранжевый", "Жёлтый", "Светло-жёлтый"},
                {"Чёрный", "Тёмно-серый", "Серый", "Светло-серый", "Белый"}
        };
        for (int p = 0; p < data.length; p++) {
            List<ThemePreset> page = new ArrayList<>();
            for (int i = 0; i < data[p].length; i++)
                page.add(new ThemePreset(names[p][i], data[p][i][0], data[p][i][1]));
            themePages.add(page);
        }
    }

    @Override
    protected void init() {
        super.init();
        updateColorsFromTheme();
        if (!firstLaunch) {
            showQuickWelcome();
            return;
        }
        open.run(1.0, 0.8);
        close.set(0.0);
        transition.set(1.0);
        scheduleAnimations(new Object[][]{{300, title, 1.0, 0.8}, {500, subtitle, 1.0, 0.6}, {900, continueBtn, 1.0, 0.5}});
    }

    private void showQuickWelcome() {
        new Thread(() -> {
            try {
                title.run(1.0, 0.6);
                subtitle.run(1.0, 0.6);
                Thread.sleep(2000);
                mc.displayScreen(new MainMenu());
            } catch (InterruptedException ignored) {
            }
        }).start();
    }

    private void scheduleAnimations(Object[][] anims) {
        new Thread(() -> {
            try {
                long prev = 0;
                for (Object[] a : anims) {
                    long delay = ((Number) a[0]).longValue();
                    Thread.sleep(delay - prev);
                    prev = delay;
                    ((Anim) a[1]).run((double) a[2], (double) a[3]);
                }
            } catch (InterruptedException ignored) {
            }
        }).start();
    }

    @Override
    public void onClose() {
        if (closing) return;
        closing = true;
        close.run(1.0, 0.4);
        open.run(0.0, 0.4);
    }

    private void updateAllAnimations() {
        open.update();
        close.update();
        title.update();
        subtitle.update();
        content.update();
        transition.update();
        preview.update();
        continueBtn.update();
        moreBtn.update();
        plusBtn.update();
        for (List<ThemePreset> page : themePages)
            for (ThemePreset t : page) {
                t.appear.update();
                t.hover.update();
            }
        guis.forEach(g -> {
            g.appear.update();
            g.hover.update();
        });
        backgrounds.forEach(b -> {
            b.appear.update();
            b.hover.update();
        });
    }

    @Override
    public void render(MatrixStack ms, int mx, int my, float pt) {
        if (closing && close.isFinished() && open.isFinished()) {
            mc.displayScreen(null);
            return;
        }
        updateAllAnimations();
        updateColorsFromTheme();
        long now = System.currentTimeMillis();
        float delta = (now - lastGradientUpdate) / 1000f;
        lastGradientUpdate = now;
        gradientPhase += delta * 0.5f;
        if (gradientPhase > 2f) gradientPhase -= 2f;
        float alpha = open.get() * (1f - close.get());
        if (alpha <= 0.01f) return;
        float s = getScale();
        RenderUtil.drawBackground(ms);
        if (!firstLaunch) {
            renderQuickWelcome(ms, alpha, s);
            super.render(ms, mx, my, pt);
            return;
        }
        float cx = width / 2f, cy = height / 2f, sa = transition.get();
        switch (stage) {
            case WELCOME -> renderWelcome(ms, mx, my, cx, cy, alpha, sa, s);
            case THEME -> renderThemeSelection(ms, mx, my, cx, cy, alpha, sa, s);
            case CUSTOM_THEME -> renderCustomTheme(ms, mx, my, cx, cy, alpha, sa, s);
            case GUI -> renderGuiSelection(ms, mx, my, cx, cy, alpha, sa, s);
            case BACKGROUND -> renderBackgroundSelection(ms, mx, my, cx, cy, alpha, sa, s);
        }
        if (previewOption != null) renderPreview(ms, mx, my, alpha, s);
        super.render(ms, mx, my, pt);
    }

    private void renderGradientBg(MatrixStack ms, float alpha) {
        int r = (BG_COLOR >> 16) & 0xFF, g = (BG_COLOR >> 8) & 0xFF, b = BG_COLOR & 0xFF;
        float p1 = gradientPhase < 1f ? (float) Math.sin(gradientPhase * Math.PI / 2.0) : 1f - (float) Math.sin((gradientPhase - 1f) * Math.PI / 2.0);
        float p2 = gradientPhase < 1f ? 1f - p1 : p1;
        int a = (int) (255 * alpha);
        int c1 = ColorUtil.rgba((int) (r * 0.8f + 30 * p1), (int) (g * 0.8f + 20 * p1), (int) (b * 0.9f + 40 * p1), a);
        int c2 = ColorUtil.rgba((int) (r * 0.6f + 20 * p2), (int) (g * 0.6f + 15 * p2), (int) (b * 0.7f + 30 * p2), a);
        int c3 = ColorUtil.rgba((int) (r * 0.7f + 25 * p1), (int) (g * 0.7f + 18 * p1), (int) (b * 0.8f + 35 * p1), a);
        int c4 = ColorUtil.rgba((int) (r * 0.5f + 15 * p2), (int) (g * 0.5f + 12 * p2), (int) (b * 0.6f + 25 * p2), a);
        RenderUtil.Rounded.drawGradientRound(ms, 0, 0, width, height, 0, c1, c2, c3, c4);
    }

    private void renderQuickWelcome(MatrixStack ms, float alpha, float s) {
        float cx = width / 2f, cy = height / 2f;
        Fonts.SF_BOLD.drawCenter(ms, "Добро пожаловать в Private DLC", cx, cy - 20 * s, ColorUtil.multAlpha(TITLE_COLOR, title.get() * alpha), 30f * s / 1.6f);
        Fonts.SF_SEMIBOLD.drawCenter(ms, "Загрузка...", cx, cy + 30 * s, ColorUtil.multAlpha(SUB_COLOR, subtitle.get() * alpha), 22.5f * s / 1.6f);
    }

    private void renderWelcome(MatrixStack ms, int mx, int my, float cx, float cy, float alpha, float sa, float s) {
        float ta = title.get() * alpha * sa, suba = subtitle.get() * alpha * sa, btnA = continueBtn.get() * alpha * sa;
        Fonts.SF_BOLD.drawCenter(ms, "Добро пожаловать в Private DLC", cx, cy - 40 * s, ColorUtil.multAlpha(TITLE_COLOR, ta), 30f * s / 1.6f);
        Fonts.SF_SEMIBOLD.drawCenter(ms, "Что бы продолжить вы должны выбрать тему", cx, cy, ColorUtil.multAlpha(SUB_COLOR, suba), 22.5f * s / 1.6f);
        float bW = 79.375f * s, bH = 23.125f * s, bX = cx - bW / 2f, bY = cy + 60 * s, bR = 6.25f * s;
        boolean bHov = isHover(mx, my, bX, bY, bW, bH);
        drawBlurBtn(ms, bX, bY, bW, bH + 2, bR, btnA, bHov, "Продолжить", cx, true);
    }

    private void drawBlurBtn(MatrixStack ms, float x, float y, float w, float h, float r, float a, boolean hov, String text, float textCx, boolean primary, float textOffsetY) {
        float s = getScale();
        if (primary) {
            BlurRenderer2.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(ms, x, y, w, h, ColorUtil.multAlpha(BTN_BG, a), Round.of(r)));
            BlurRenderer2.draw(31);
            RenderUtil.Rounded.roundedRect(ms, x, y, w, h, ColorUtil.multAlpha(BTN_BG, a), Round.of(r));
            RenderUtil.Rounded.roundedOutline(ms, x, y, w, h, 1.25f * s, ColorUtil.multAlpha(BTN_BORDER, a * (hov ? 1f : 0.7f)), Round.of(r));
            float textSize = 10f * s;
            float textHeight = Fonts.SF_BOLD.getHeight(text, textSize);
            Fonts.SF_BOLD.drawCenter(ms, text, textCx, y + h / 2f - textHeight / 2f + textOffsetY, ColorUtil.multAlpha(BTN_TEXT, a), textSize);
        } else {
            RenderUtil.Rounded.roundedRect(ms, x, y, w, h, ColorUtil.multAlpha(BTN2_BG, a), Round.of(r));
            RenderUtil.Rounded.roundedOutline(ms, x, y, w, h, 1.25f * s, ColorUtil.multAlpha(BTN2_BORDER, a * (hov ? 1f : 0.7f)), Round.of(r));
            float textSize = 10f * s;
            float textHeight = Fonts.SF_BOLD.getHeight(text, textSize);
            Fonts.SF_BOLD.drawCenter(ms, text, textCx, y + h / 2f - textHeight / 2f + textOffsetY, ColorUtil.multAlpha(BTN2_TEXT, a), textSize);
        }
    }

    private void drawBlurBtn(MatrixStack ms, float x, float y, float w, float h, float r, float a, boolean hov, String text, float textCx, boolean primary) {
        drawBlurBtn(ms, x, y, w, h, r, a, hov, text, textCx, primary, 0f);
    }

    private void renderThemeSelection(MatrixStack ms, int mx, int my, float cx, float cy, float alpha, float sa, float s) {
        float ta = title.get() * alpha * sa, suba = subtitle.get() * alpha * sa, ca = content.get() * alpha * sa;
        float btnA = continueBtn.get() * alpha * sa, moreA = moreBtn.get() * alpha * sa;
        Fonts.SF_BOLD.drawCenter(ms, "Выберите тему", cx, cy - 120 * s, ColorUtil.multAlpha(TITLE_COLOR, ta), 30f * s / 1.6f);
        Fonts.SF_SEMIBOLD.drawCenter(ms, "Выберите цвет на свой вкус!", cx, cy - 85 * s, ColorUtil.multAlpha(SUB_COLOR, suba), 22.5f * s / 1.6f);
        Fonts.SF_BOLD.drawCenter(ms, "Текущая", cx, cy - 55 * s, ColorUtil.multAlpha(CUR_LABEL, ca), 20f * s / 1.6f);

        List<ThemePreset> cur = themePages.get(currentThemePage);
        hoveredTheme = null;
        float tSz = 71.875f * s, tSp = 11.875f * s, tW = tSz * 5 + tSp * 4 + tSz + tSp, tX = cx - tW / 2f, tY = cy - 20 * s, tR = 12.5f * s;

        for (int i = 0; i < cur.size(); i++) {
            ThemePreset t = cur.get(i);
            t.x = tX + i * (tSz + tSp);
            t.y = tY;
            boolean ih = isHover(mx, my, t.x, t.y, tSz, tSz);
            if (ih && t.appear.get() > 0.5f) hoveredTheme = t;
            t.hover.run(ih ? 1.0 : 0.0, 0.3);
        }
        for (ThemePreset t : cur) {
            float ap = t.appear.get(), hv = t.hover.get(), sz = tSz + hv * 4 * s, off = hv * 2 * s;
            ms.push();
            ms.translate(t.x + tSz / 2f, t.y + tSz / 2f, 0);
            ms.scale(ap, ap, 1);
            ms.translate(-(t.x + tSz / 2f), -(t.y + tSz / 2f), 0);
            RenderUtil.Rounded.roundedRect(ms, t.x - off, t.y - off, sz, sz, ColorUtil.multAlpha(t.color, ca), Round.of(tR));
            RenderUtil.Rounded.roundedOutline(ms, t.x - off, t.y - off, sz, sz, (1.875f + hv * 0.5f) * s, ColorUtil.multAlpha(t == selectedTheme ? 0xFFFFFFFF : t.borderColor, ca * (0.7f + hv * 0.3f)), Round.of(tR));
            ms.pop();
        }

        float aX = tX + 5 * (tSz + tSp), aY = tY;
        boolean aH = isHover(mx, my, aX, aY, tSz, tSz);
        plusBtn.run(aH ? 1.0 : 0.0, 0.3);
        float pA = plusBtn.get(), pSz = tSz + pA * 4 * s, pOff = pA * 2 * s;
        ms.push();
        ms.translate(aX + tSz / 2f, aY + tSz / 2f, 0);
        ms.scale(ca, ca, 1);
        ms.translate(-(aX + tSz / 2f), -(aY + tSz / 2f), 0);
        RenderUtil.Rounded.roundedOutline(ms, aX - pOff, aY - pOff, pSz, pSz, 2.5f * s, ColorUtil.multAlpha(0x80FFFFFF, ca * (0.5f + pA * 0.3f)), Round.of(tR));
        Fonts.SF_BOLD.drawCenter(ms, "+", aX + tSz / 2f, aY + tSz - 93 / 2f - 12 * s, ColorUtil.multAlpha(0x80FFFFFF, ca * (0.5f + pA * 0.3f)), 35f * s);
        ms.pop();

        float bY2 = cy + 80 * s, cW = 79.375f * s, cH = 23.125f * s, mW = 93.75f * s, bSp = 10f * s, tbW = cW + mW + bSp, bSX = cx - tbW / 2f, bR = 6.25f * s;
        drawBlurBtn(ms, bSX, bY2, cW, cH, bR, btnA, isHover(mx, my, bSX, bY2, cW, cH), "Продолжить", bSX + cW / 2f, true);
        float mX = bSX + cW + bSp;
        drawBlurBtn(ms, mX, bY2, mW, cH, bR, moreA, isHover(mx, my, mX, bY2, mW, cH), "Страница " + (currentThemePage + 1) + "/" + themePages.size() + " →", mX + mW / 2f, false);
    }


    private void renderCustomTheme(MatrixStack ms, int mx, int my, float cx, float cy, float alpha, float sa, float s) {
        float ta = title.get() * alpha * sa, suba = subtitle.get() * alpha * sa, ca = content.get() * alpha * sa, btnA = continueBtn.get() * alpha * sa;
        Fonts.SF_BOLD.drawCenter(ms, "Создание темы", cx, cy - 140 * s, ColorUtil.multAlpha(TITLE_COLOR, ta), 30f * s / 1.6f);
        Fonts.SF_SEMIBOLD.drawCenter(ms, "Настройте свои цвета", cx, cy - 105 * s, ColorUtil.multAlpha(SUB_COLOR, suba), 22.5f * s / 1.6f);

        float pW = 180 * s, pH = 120 * s, sp = 30 * s, totalW = pW * 3 + sp * 2, startX = cx - totalW / 2f, pY = cy - 40 * s;
        String[] labels = {"Цвет клиента", "Цвет текста", "Цвет вкл. функции"};
        int[] colors = {customColor1, customColor2, customColor3};
        for (int i = 0; i < 3; i++) {
            float px = startX + i * (pW + sp);
            BlurRenderer2.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(ms, px, pY, pW, pH, ColorUtil.multAlpha(CARD_BG, ca), Round.of(10 * s)));
            BlurRenderer2.draw(31);
            RenderUtil.Rounded.roundedRect(ms, px, pY, pW, pH, ColorUtil.multAlpha(CARD_BG, ca), Round.of(10 * s));
            RenderUtil.Rounded.roundedOutline(ms, px, pY, pW, pH, 1.25f * s, ColorUtil.multAlpha(CARD_BORDER, ca), Round.of(10 * s));
            float labelSize = 10f * s;
            float labelHeight = Fonts.SF_BOLD.getHeight(labels[i], labelSize);
            Fonts.SF_BOLD.drawCenter(ms, labels[i], px + pW / 2f, pY + 15 * s - labelHeight / 2f + 5 * s, ColorUtil.multAlpha(BTN_TEXT, ca), labelSize);
            float pvX = px + 10 * s, pvY = pY + 35 * s, pvW = pW - 20 * s, pvH = 60 * s;
            RenderUtil.Rounded.roundedRect(ms, pvX, pvY, pvW, pvH, ColorUtil.multAlpha(colors[i], ca), Round.of(6 * s));
            RenderUtil.Rounded.roundedOutline(ms, pvX, pvY, pvW, pvH, 1 * s, ColorUtil.multAlpha(0xFFFFFFFF, ca * 0.3f), Round.of(6 * s));
            if (isHover(mx, my, pvX, pvY, pvW, pvH)) {
                float hintSize = 8f * s;
                float hintHeight = Fonts.SF_SEMIBOLD.getHeight("Нажмите для выбора", hintSize);
                Fonts.SF_SEMIBOLD.drawCenter(ms, "Нажмите для выбора", px + pW / 2f, pvY + pvH / 2f - hintHeight / 2f, ColorUtil.multAlpha(0xFFFFFFFF, ca * 0.8f), hintSize);
            }
        }
        if (activeColorPicker >= 0) renderColorPicker(ms, mx, my, cx, cy + 90 * s, alpha * sa, s);

        float bY = cy + 100 * s, cW = 79.375f * s, cH = 23.125f * s, bW2 = 79.375f * s, bSp = 10f * s, tbW = cW + bW2 + bSp, bSX = cx - tbW / 2f, bR = 6.25f * s;
        drawBlurBtn(ms, bSX, bY, bW2, cH, bR, btnA, isHover(mx, my, bSX, bY, bW2, cH), "Назад", bSX + bW2 / 2f, false);
        float cX = bSX + bW2 + bSp;
        drawBlurBtn(ms, cX, bY, cW, cH, bR, btnA, isHover(mx, my, cX, bY, cW, cH), "Применить", cX + cW / 2f, true);
    }


    private void applyCustomTheme() {
        Theme.getInstance().client().set(customColor1);
        Theme.getInstance().text().set(customColor2);
        Theme.getInstance().enabledText().set(customColor3);
    }

    private void renderColorPicker(MatrixStack ms, int mx, int my, float cx, float cy, float alpha, float s) {
        cy = cy + 50;

        float pW = 200 * s, pH = 150 * s, px = cx - pW / 2f, py = cy;
        BlurRenderer2.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(ms, px, py, pW, pH, ColorUtil.multAlpha(0xFF1A1A1A, alpha), Round.of(8 * s)));
        BlurRenderer2.draw(20);
        RenderUtil.Rounded.roundedRect(ms, px, py, pW, pH, ColorUtil.multAlpha(0xFF1A1A1A, alpha), Round.of(8 * s));
        RenderUtil.Rounded.roundedOutline(ms, px, py, pW, pH, 1 * s, ColorUtil.multAlpha(CARD_BORDER, alpha), Round.of(8 * s));

        float sbX = px + 10 * s, sbY = py + 10 * s, sbW = pW - 40 * s, sbH = pH - 50 * s;
        for (int i = 0; i < sbW; i++)
            for (int j = 0; j < sbH; j++) {
                int c = Color.HSBtoRGB(colorPickerHue, i / sbW, 1f - (j / sbH));
                RectUtil.drawRect(ms, sbX + i, sbY + j, 1, 1, ColorUtil.multAlpha(c, alpha));
            }
        float hX = px + pW - 25 * s, hY = py + 10 * s, hW = 15 * s, hH = sbH;
        for (int i = 0; i < hH; i++) {
            int c = Color.HSBtoRGB(i / hH, 1f, 1f);
            RectUtil.drawRect(ms, hX, hY + i, hW, 1, ColorUtil.multAlpha(c, alpha));
        }
        float slY = hY + colorPickerHue * hH;
        RenderUtil.Rounded.roundedOutline(ms, hX - 2 * s, slY - 2 * s, hW + 4 * s, 4 * s, 1 * s, ColorUtil.multAlpha(0xFFFFFFFF, alpha), Round.of(2 * s));
        float curX = sbX + colorPickerSat * sbW, curY = sbY + (1f - colorPickerBri) * sbH;
        RenderUtil.Rounded.roundedOutline(ms, curX - 4 * s, curY - 4 * s, 8 * s, 8 * s, 1.5f * s, ColorUtil.multAlpha(0xFFFFFFFF, alpha), Round.of(4 * s));

        float aY = py + pH - 30 * s, aW = 60 * s, aH = 20 * s, aX = cx - aW / 2f;
        boolean aHov = isHover(mx, my, aX, aY, aW, aH);
        RenderUtil.Rounded.roundedRect(ms, aX, aY, aW, aH, ColorUtil.multAlpha(BTN_BG, alpha), Round.of(4 * s));
        RenderUtil.Rounded.roundedOutline(ms, aX, aY, aW, aH, 1 * s, ColorUtil.multAlpha(BTN_BORDER, alpha * (aHov ? 1f : 0.7f)), Round.of(4 * s));
        float okSize = 9f * s;
        float okHeight = Fonts.SF_BOLD.getHeight("OK", okSize);
        Fonts.SF_BOLD.drawCenter(ms, "OK", aX + aW / 2f, aY + aH / 2f - okHeight / 2f, ColorUtil.multAlpha(BTN_TEXT, alpha), okSize);
    }

    private void renderCardSelection(MatrixStack ms, int mx, int my, float cx, float cy, float alpha, float sa, float s,
                                     String titleText, String subText, List<SelectionOption> options, float cardW, float cardH) {
        float ta = title.get() * alpha * sa, suba = subtitle.get() * alpha * sa, ca = content.get() * alpha * sa, btnA = continueBtn.get() * alpha * sa;
        Fonts.SF_BOLD.drawCenter(ms, titleText, cx, cy - 140 * s, ColorUtil.multAlpha(TITLE_COLOR, ta), 30f * s / 1.6f);
        Fonts.SF_SEMIBOLD.drawCenter(ms, subText, cx, cy - 105 * s, ColorUtil.multAlpha(SUB_COLOR, suba), 22.5f * s / 1.6f);
        hoveredOption = null;
        float cSp = 20f * s, cTW = cardW * options.size() + cSp * (options.size() - 1), cSX = cx - cTW / 2f, cY = cy - 40 * s, cR = 11.25f * s, pR = 7.5f * s, pP = 12f * s;

        for (int i = 0; i < options.size(); i++) {
            SelectionOption o = options.get(i);
            o.x = cSX + i * (cardW + cSp);
            o.y = cY;
            o.width = cardW;
            o.height = cardH;
            boolean ih = isHover(mx, my, o.x, o.y, cardW, cardH);
            if (ih && o.appear.get() > 0.5f) hoveredOption = o;
            o.hover.run(ih ? 1.0 : 0.0, 0.3);
        }
        for (SelectionOption o : options) {
            float ap = o.appear.get(), hv = o.hover.get(), sz = 1f + hv * 0.02f;
            boolean isSel = o == selectedOption;
            ms.push();
            ms.translate(o.x + cardW / 2f, o.y + cardH / 2f, 0);
            ms.scale(ap * sz, ap * sz, 1);
            ms.translate(-(o.x + cardW / 2f), -(o.y + cardH / 2f), 0);
            BlurRenderer2.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(ms, o.x, o.y, cardW, cardH, ColorUtil.multAlpha(0xFF1A1A20, ca), Round.of(cR)));
            BlurRenderer2.draw(31);
            RenderUtil.Rounded.roundedRect(ms, o.x, o.y, cardW, cardH, ColorUtil.multAlpha(0xFF1A1A20, ca), Round.of(cR));
            RenderUtil.Rounded.roundedOutline(ms, o.x, o.y, cardW, cardH, 1.25f * s, ColorUtil.multAlpha(isSel ? 0xFFFFFFFF : CARD_BORDER, ca * (0.7f + hv * 0.3f)), Round.of(cR));
            float pvW = cardW - pP * 2, pvH = cardH - pP * 2, pvX = o.x + pP, pvY = o.y + pP;
            RenderUtil.Rounded.roundedRect(ms, pvX, pvY, pvW, pvH, ColorUtil.multAlpha(0xFF252530, ca), Round.of(pR));
            if (o.texture != null && !o.texture.isEmpty())
                RenderUtil.drawImage(ms, new Namespaced("texture/" + o.texture), pvX, pvY, pvW, pvH, true, ColorUtil.multAlpha(-1, ca));
            RenderUtil.Rounded.roundedOutline(ms, pvX, pvY, pvW, pvH, 1.25f * s, ColorUtil.multAlpha(THEME_BORDER, ca), Round.of(pR));
            ms.pop();
        }
    }

    private void renderGuiSelection(MatrixStack ms, int mx, int my, float cx, float cy, float alpha, float sa, float s) {
        float cardW = 200f * s, cardH = 140f * s;
        renderCardSelection(ms, mx, my, cx, cy, alpha, sa, s, "Выберите интерфейс", "Подбери лучший под себя!", guis, cardW, cardH);
        float ca = content.get() * alpha * sa, btnA = continueBtn.get() * alpha * sa;
        Fonts.SF_SEMIBOLD.drawCenter(ms, "ПКМ - Предпросмотр", cx, cy + cardH / 2f + 50 * s, ColorUtil.multAlpha(SUB_COLOR, ca), 10f * s);
        Fonts.SF_SEMIBOLD.drawCenter(ms, "ЛКМ - Выбрать", cx, cy + cardH / 2f + 65 * s, ColorUtil.multAlpha(SUB_COLOR, ca), 10f * s);
        float bW = 79.375f * s, bH = 23.125f * s, bX = cx - bW / 2f, bY = cy + cardH / 2f + 90 * s, bR = 6.25f * s;
        drawBlurBtn(ms, bX, bY, bW, bH, bR, btnA, isHover(mx, my, bX, bY, bW, bH), "Продолжить", cx, true);
    }

    private void renderBackgroundSelection(MatrixStack ms, int mx, int my, float cx, float cy, float alpha, float sa, float s) {
        float cardW = 150f * s, cardH = 150f * s;
        renderCardSelection(ms, mx, my, cx, cy, alpha, sa, s, "Последний шаг", "Выберите фон главного меню", backgrounds, cardW, cardH);
        float btnA = continueBtn.get() * alpha * sa;
        float bY = cy + 5 + cardH / 2f + 70 * s, cW = 79.375f * s, cH = 23.125f * s, kW = 106.875f * s, bSp = 10f * s, tbW = cW + kW + bSp, bSX = cx - tbW / 2f, bR = 6.25f * s;
        drawBlurBtn(ms, bSX, bY, cW, cH, bR, btnA, isHover(mx, my, bSX, bY, cW, cH), "Продолжить", bSX + cW / 2f, true);
        float kX = bSX + cW + bSp;
        drawBlurBtn(ms, kX, bY, kW, cH, bR, btnA, isHover(mx, my, kX, bY, kW, cH), "Оставить текущий", kX + kW / 2f, false);
    }

    private void renderPreview(MatrixStack ms, int mx, int my, float alpha, float s) {
        preview.run(previewClosing ? 0.0 : 1.0, 0.25);
        float pa = preview.get() * alpha;
        if (pa < 0.01f) {
            if (previewClosing) {
                previewOption = null;
                previewClosing = false;
            }
            return;
        }
        BlurRenderer2.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(ms, 0, 0, width, height, ColorUtil.multAlpha(0xFF000000, pa * 0.7f), Round.of(0)));
        BlurRenderer2.draw(15);
        RenderUtil.Rounded.roundedRect(ms, 0, 0, width, height, ColorUtil.multAlpha(0xFF000000, pa * 0.5f), Round.of(0));
        float pw = 560f * s, ph = 340f * s, px = width / 2f - pw / 2f, py = height / 2f - ph / 2f - 20 * s, cR = 18.75f * s, pR = 12.5f * s, pP = 20f * s;
        ms.push();
        ms.translate(width / 2f, py + ph / 2f, 0);
        ms.scale(pa, pa, 1);
        ms.translate(-width / 2f, -(py + ph / 2f), 0);
        RenderUtil.Rounded.roundedRect(ms, px, py, pw, ph, ColorUtil.multAlpha(0xFF1A1A20, pa), Round.of(cR));
        RenderUtil.Rounded.roundedOutline(ms, px, py, pw, ph, 1.5f * s, ColorUtil.multAlpha(CARD_BORDER, pa), Round.of(cR));
        float iW = pw - pP * 2, iH = ph - pP * 2, iX = px + pP, iY = py + pP;
        RenderUtil.Rounded.roundedRect(ms, iX, iY, iW, iH, ColorUtil.multAlpha(0xFF252530, pa), Round.of(pR));
        if (previewOption.texture != null && !previewOption.texture.isEmpty())
            RenderUtil.drawImage(ms, new Namespaced("texture/" + previewOption.texture), iX, iY, iW, iH, true, ColorUtil.multAlpha(-1, pa));
        RenderUtil.Rounded.roundedOutline(ms, iX, iY, iW, iH, 1.25f * s, ColorUtil.multAlpha(THEME_BORDER, pa), Round.of(pR));
        ms.pop();
        float bW = 79.375f * s, bH = 23.125f * s, bX = width / 2f - bW / 2f, bY = py + ph + 30 * s, bR = 6.25f * s;
        boolean bHov = isHover(mx, my, bX, bY, bW, bH);
        RenderUtil.Rounded.roundedRect(ms, bX, bY, bW, bH, ColorUtil.multAlpha(BTN_BG, pa), Round.of(bR));
        RenderUtil.Rounded.roundedOutline(ms, bX, bY, bW, bH, 1.25f * s, ColorUtil.multAlpha(BTN_BORDER, pa * (bHov ? 1f : 0.7f)), Round.of(bR));
        float textSize = 10f * s;
        float textHeight = Fonts.SF_BOLD.getHeight("Выбрать", textSize);
        Fonts.SF_BOLD.drawCenter(ms, "Выбрать", width / 2f, bY + bH / 2f - textHeight / 2f, ColorUtil.multAlpha(BTN_TEXT, pa), textSize);
        Fonts.SF_SEMIBOLD.drawCenter(ms, "ESC - Закрыть", width / 2f, bY + bH + 15 * s, ColorUtil.multAlpha(SUB_COLOR, pa), 10f * s);
    }

    private void nextStage() {
        transition.set(0.0);
        transition.run(1.0, 0.6);
        title.set(0.0);
        subtitle.set(0.0);
        content.set(0.0);
        continueBtn.set(0.0);
        moreBtn.set(0.0);
        plusBtn.set(0.0);
        selectedOption = null;
        switch (stage) {
            case WELCOME -> {
                stage = Stage.THEME;
                startStaggered(themePages.get(currentThemePage), 60, true);
            }
            case THEME -> {
                stage = Stage.GUI;
                startStaggered(guis, 80, false);
            }
            case CUSTOM_THEME -> {
                stage = Stage.GUI;
                startStaggered(guis, 80, false);
            }
            case GUI -> {
                stage = Stage.BACKGROUND;
                startStaggered(backgrounds, 80, false);
            }
            case BACKGROUND -> {
                Excellent.inst().configManager().markWelcomeCompleted();
                Excellent.inst().configManager().set();
                mc.displayScreen(new MainMenu());
            }
        }
    }

    private void startStaggered(List<?> items, long stagger, boolean withMore) {
        new Thread(() -> {
            try {
                Thread.sleep(200);
                title.run(1.0, 0.6);
                Thread.sleep(150);
                subtitle.run(1.0, 0.5);
                Thread.sleep(200);
                content.run(1.0, 0.5);
                for (int i = 0; i < items.size(); i++) {
                    int idx = i;
                    new Thread(() -> {
                        try {
                            Thread.sleep(idx * stagger);
                            Object item = items.get(idx);
                            if (item instanceof ThemePreset t) t.appear.run(1.0, 0.6);
                            else if (item instanceof SelectionOption o) o.appear.run(1.0, 0.6);
                        } catch (InterruptedException ignored) {
                        }
                    }).start();
                }
                Thread.sleep(Math.max(400, items.size() * stagger + 100));
                continueBtn.run(1.0, 0.5);
                if (withMore) {
                    Thread.sleep(100);
                    moreBtn.run(1.0, 0.5);
                }
            } catch (InterruptedException ignored) {
            }
        }).start();
    }

    private void startCustomThemeAnimations() {
        scheduleAnimations(new Object[][]{{200, title, 1.0, 0.6}, {350, subtitle, 1.0, 0.5}, {550, content, 1.0, 0.5}, {850, continueBtn, 1.0, 0.5}});
    }

    private void switchThemePage() {
        themePages.get(currentThemePage).forEach(t -> t.appear.set(0.0));
        plusBtn.set(0.0);
        currentThemePage = (currentThemePage + 1) % themePages.size();
        List<ThemePreset> next = themePages.get(currentThemePage);
        for (int i = 0; i < next.size(); i++) {
            int idx = i;
            new Thread(() -> {
                try {
                    Thread.sleep(idx * 60L);
                    next.get(idx).appear.run(1.0, 0.6);
                } catch (InterruptedException ignored) {
                }
            }).start();
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        float s = getScale(), cx = width / 2f, cy = height / 2f;

        if (previewOption != null) {
            if (btn == 0) {
                float pw = 560f * s, ph = 340f * s, py = height / 2f - ph / 2f - 20 * s;
                float bW = 79.375f * s, bH = 23.125f * s, bX = width / 2f - bW / 2f, bY = py + ph + 30 * s;
                if (isHover(mx, my, bX, bY, bW, bH)) {
                    if (stage == Stage.GUI) {
                        FlatGui.getInstance().guiType().set(previewOption.value);
                        selectedOption = previewOption;
                    } else if (stage == Stage.BACKGROUND) {
                        RenderUtil.setBackground(previewOption.texture);
                        Excellent.inst().configManager().saveSelectedBackground(previewOption.texture);
                        selectedOption = previewOption;
                    }
                    previewClosing = true;
                    return true;
                }
            }
            previewClosing = true;
            return true;
        }

        if (stage == Stage.CUSTOM_THEME && activeColorPicker >= 0) {
            float pW = 200 * s, pH = 150 * s, px = cx - pW / 2f, py = cy + 90 * s + 50;
            float sbX = px + 10 * s, sbY = py + 10 * s, sbW = pW - 40 * s, sbH = pH - 50 * s;
            float hX = px + pW - 25 * s, hY = py + 10 * s, hW = 15 * s, hH = sbH;
            if (isHover(mx, my, sbX, sbY, sbW, sbH)) {
                colorPickerSat = clamp01((mx - sbX) / sbW);
                colorPickerBri = clamp01(1 - (my - sbY) / sbH);
                updateCustomColor();
                return true;
            }
            if (isHover(mx, my, hX, hY, hW, hH)) {
                colorPickerHue = clamp01((my - hY) / hH);
                updateCustomColor();
                return true;
            }
            float aY = py + pH - 30 * s, aW = 60 * s, aH = 20 * s, aX = cx - aW / 2f;
            if (isHover(mx, my, aX, aY, aW, aH)) {
                activeColorPicker = -1;
                return true;
            }
        }

        if (btn == 0) {
            switch (stage) {
                case WELCOME -> {
                    float bW = 79.375f * s, bH = 23.125f * s, bX = cx - bW / 2f, bY = cy + 60 * s;
                    if (isHover(mx, my, bX, bY, bW, bH)) {
                        nextStage();
                        return true;
                    }
                }
                case THEME -> {
                    if (hoveredTheme != null) {
                        selectedTheme = hoveredTheme;
                        Theme.getInstance().client().set(hoveredTheme.color & 0x00FFFFFF | 0xFF000000);
                        return true;
                    }
                    float tSz = 71.875f * s, tSp = 11.875f * s, tW = tSz * 5 + tSp * 4 + tSz + tSp, tX = cx - tW / 2f, tY = cy - 20 * s;
                    float aX = tX + 5 * (tSz + tSp);
                    if (isHover(mx, my, aX, tY, tSz, tSz)) {
                        stage = Stage.CUSTOM_THEME;
                        transition.set(0.0);
                        transition.run(1.0, 0.6);
                        title.set(0.0);
                        subtitle.set(0.0);
                        content.set(0.0);
                        continueBtn.set(0.0);
                        startCustomThemeAnimations();
                        return true;
                    }
                    float bY2 = cy + 80 * s, cW = 79.375f * s, cH = 23.125f * s, mW = 93.75f * s, bSp = 10f * s, tbW = cW + mW + bSp, bSX = cx - tbW / 2f;
                    if (isHover(mx, my, bSX, bY2, cW, cH)) {
                        nextStage();
                        return true;
                    }
                    float mX = bSX + cW + bSp;
                    if (isHover(mx, my, mX, bY2, mW, cH)) {
                        switchThemePage();
                        return true;
                    }
                }
                case CUSTOM_THEME -> {
                    float pW2 = 180 * s, sp2 = 30 * s, tW2 = pW2 * 3 + sp2 * 2, sX2 = cx - tW2 / 2f, pY2 = cy - 40 * s;
                    for (int i = 0; i < 3; i++) {
                        float px2 = sX2 + i * (pW2 + sp2), pvX = px2 + 10 * s, pvY = pY2 + 35 * s, pvW = pW2 - 20 * s, pvH = 60 * s;
                        if (isHover(mx, my, pvX, pvY, pvW, pvH)) {
                            activeColorPicker = i;
                            int[] cols = {customColor1, customColor2, customColor3};
                            float[] hsb = Color.RGBtoHSB((cols[i] >> 16) & 0xFF, (cols[i] >> 8) & 0xFF, cols[i] & 0xFF, null);
                            colorPickerHue = hsb[0];
                            colorPickerSat = hsb[1];
                            colorPickerBri = hsb[2];
                            return true;
                        }
                    }
                    float bY3 = cy + 100 * s, cW2 = 79.375f * s, cH2 = 23.125f * s, bW3 = 79.375f * s, bSp2 = 10f * s, tbW2 = cW2 + bW3 + bSp2, bSX2 = cx - tbW2 / 2f;
                    if (isHover(mx, my, bSX2, bY3, bW3, cH2)) {
                        stage = Stage.THEME;
                        transition.set(0.0);
                        transition.run(1.0, 0.6);
                        title.set(0.0);
                        subtitle.set(0.0);
                        content.set(0.0);
                        continueBtn.set(0.0);
                        moreBtn.set(0.0);
                        startStaggered(themePages.get(currentThemePage), 60, true);
                        return true;
                    }
                    float cX2 = bSX2 + bW3 + bSp2;
                    if (isHover(mx, my, cX2, bY3, cW2, cH2)) {
                        applyCustomTheme();
                        nextStage();
                        return true;
                    }
                }
                case GUI -> {
                    if (hoveredOption != null) {
                        selectedOption = hoveredOption;
                        FlatGui.getInstance().guiType().set(hoveredOption.value);
                        return true;
                    }
                    float cH3 = 140f * s, bW4 = 79.375f * s, bH4 = 23.125f * s, bX4 = cx - bW4 / 2f, bY4 = cy + cH3 / 2f + 90 * s;
                    if (isHover(mx, my, bX4, bY4, bW4, bH4)) {
                        nextStage();
                        return true;
                    }
                }
                case BACKGROUND -> {
                    if (hoveredOption != null) {
                        selectedOption = hoveredOption;
                        RenderUtil.setBackground(hoveredOption.texture);
                        Excellent.inst().configManager().saveSelectedBackground(hoveredOption.texture);
                        return true;
                    }
                    float cH4 = 150f * s, bY5 = cy + 5 + cH4 / 2f + 70 * s, cW3 = 79.375f * s, cH5 = 23.125f * s, kW = 106.875f * s, bSp3 = 10f * s, tbW3 = cW3 + kW + bSp3, bSX3 = cx - tbW3 / 2f;
                    if (isHover(mx, my, bSX3, bY5, cW3, cH5)) {
                        nextStage();
                        return true;
                    }
                    float kX = bSX3 + cW3 + bSp3;
                    if (isHover(mx, my, kX, bY5, kW, cH5)) {
                        nextStage();
                        return true;
                    }
                }
            }
        } else if (btn == 1 && (stage == Stage.GUI || stage == Stage.BACKGROUND) && hoveredOption != null) {
            previewOption = hoveredOption;
            preview.set(0.0);
            previewClosing = false;
            return true;
        }
        return super.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int btn, double deltaX, double deltaY) {
        float s = getScale(), cx = width / 2f, cy = height / 2f;
        if (stage == Stage.CUSTOM_THEME && activeColorPicker >= 0 && btn == 0) {
            float pW = 200 * s, pH = 150 * s, px = cx - pW / 2f, py = cy + 90 * s + 50;
            float sbX = px + 10 * s, sbY = py + 10 * s, sbW = pW - 40 * s, sbH = pH - 50 * s;
            float hX = px + pW - 25 * s, hY = py + 10 * s, hW = 15 * s, hH = sbH;
            if (isHover(mx, my, sbX - 10, sbY - 10, sbW + 20, sbH + 20)) {
                colorPickerSat = clamp01((mx - sbX) / sbW);
                colorPickerBri = clamp01(1 - (my - sbY) / sbH);
                updateCustomColor();
                return true;
            }
            if (isHover(mx, my, hX - 10, hY - 10, hW + 20, hH + 20)) {
                colorPickerHue = clamp01((my - hY) / hH);
                updateCustomColor();
                return true;
            }
        }
        return super.mouseDragged(mx, my, btn, deltaX, deltaY);
    }

    private float clamp01(double v) {
        return (float) Math.max(0, Math.min(1, v));
    }

    private void updateCustomColor() {
        int rgb = Color.HSBtoRGB(colorPickerHue, colorPickerSat, colorPickerBri);
        int c = 0xFF000000 | (rgb & 0x00FFFFFF);
        switch (activeColorPicker) {
            case 0 -> customColor1 = c;
            case 1 -> customColor2 = c;
            case 2 -> customColor3 = c;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            if (previewOption != null) {
                previewClosing = true;
                return true;
            }
            if (stage == Stage.CUSTOM_THEME && activeColorPicker >= 0) {
                activeColorPicker = -1;
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void drawBackground(MatrixStack ms) {
        Theme hud = Theme.getInstance();
        int w = mc.getMainWindow().getScaledWidth(), h = mc.getMainWindow().getScaledHeight();
        RectUtil.drawRect(ms, 0, 0, w, h, ColorUtil.multDark(hud.clientColor(), 0.08f));
        drawImage(ms, new Namespaced("texture/background2.png"), 0, 0, w, h, true, hud.clientColor());
    }

    public void drawImage(MatrixStack ms, ResourceLocation loc, float x, float y, float w, float h, boolean bloom, int color) {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        mc.getTextureManager().bindTexture(loc);
        RectUtil.drawRect(ms, x, y, w, h, color, bloom, true);
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }
}