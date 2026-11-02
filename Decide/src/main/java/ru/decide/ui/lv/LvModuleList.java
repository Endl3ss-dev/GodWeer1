/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_332
 */
package ru.decide.ui.lv;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;
import ru.decide.Client;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.ui.fonts.FontsLV;
import ru.decide.ui.lv.LvCategory;
import ru.decide.ui.lv.LvScrollBar;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.ui.util.Position;
import ru.decide.ui.util.anim.Decelerate;
import ru.decide.ui.util.anim.Direction;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.math.Keyboard;
import ru.decide.utils.render.Draw;
import ru.decide.utils.render.Scissor;

@Environment(value=EnvType.CLIENT)
public final class LvModuleList {
    private static final float FADE_OUT_DURATION = 0.15f;
    private static final int ROW_FADE_MS = 380;
    private static final int QUICK_ROW_FADE_MS = 320;
    private static final float SLIDE_PX = 6.0f;
    private static final float CARD_RADIUS = 6.0f;
    private static final float CARD_GAP = 5.0f;
    private static final float PAD_X = 8.0f;
    private static final float NAME_SIZE = 7.5f;
    private static final float NAME_TOP = 7.0f;
    private static final float TITLE_CY = 10.75f;
    private static final float DESC_SIZE = 6.0f;
    private static final float DESC_LINE_H = 7.0f;
    private static final float DESC_TOP = 19.0f;
    private static final float DESC_RIGHT_PAD = 4.0f;
    private static final float DESC_BOTTOM_PAD = 7.0f;
    private static final float NO_DESC_H = 22.0f;
    private static final float TOGGLE_RIGHT_PAD = 8.0f;
    private static final float GEAR_GAP = 7.0f;
    private static final float GEAR_SIZE = 7.0f;
    private static final String GEAR_GLYPH = "f";
    private static final float CHECK_SIZE = 8.5f;
    private static final float CHECK_RADIUS = 2.9750001f;
    private static final float CHECK_RING_THICKNESS = Math.max(0.5f, 0.425f);
    public static final float HEADER_OFFSET = 30.0f;
    private final Map<String, Decelerate> enableAnims = new HashMap<String, Decelerate>();
    private final Map<String, Float> hoverAnims = new HashMap<String, Float>();
    private final Map<String, Float> gearHoverAnims = new HashMap<String, Float>();
    private final Map<String, Float> gearOpenAnims = new HashMap<String, Float>();
    private final Map<String, List<String>> descLinesCache = new HashMap<String, List<String>>();
    private final Map<String, Float> baseHeightCache = new HashMap<String, Float>();
    private float scroll = 0.0f;
    private float scrollTarget = 0.0f;
    private float contentH = 0.0f;
    private LvCategory lastRendered = null;
    private final Map<Integer, Decelerate> rowAppearAnims = new HashMap<Integer, Decelerate>();
    private int appearFadeMs = 320;
    private long appearBaseMs;
    private boolean appearInitialFrame;
    private final LvScrollBar scrollBar = new LvScrollBar();
    private boolean transitioning = false;
    private boolean quickAppear = false;
    private List<Module> fadingOut = Collections.emptyList();
    private float fadeOutTime = 0.0f;
    private String focusName;
    private long focusUntilMs;
    private boolean focusScrollPending;
    private float focusDimT;
    private boolean appearComposite;
    private int lastModulesSig;

    public void warmup() {
        int color = 0x1FFFFFF;
        String glyphs = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 .,:;!?-_()[]{}<>/\\|@#$%^&*+=\"'`\u0410\u0411\u0412\u0413\u0414\u0415\u0401\u0416\u0417\u0418\u0419\u041a\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042a\u042b\u042c\u042d\u042e\u042f\u0430\u0431\u0432\u0433\u0434\u0435\u0451\u0436\u0437\u0438\u0439\u043a\u043b\u043c\u043d\u043e\u043f\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044a\u044b\u044c\u044d\u044e\u044f";
        FontsLV.MONTSERRAT_MEDIUM.draw(glyphs, -500.0f, -500.0f, 6.0f, color);
        for (LvCategory cat : LvCategory.values()) {
            for (Module module : this.getModules(cat)) {
                this.enableAnims.computeIfAbsent(module.getName(), n -> LvModuleList.createAnim(220));
                FontsLV.DECIDE.msdf(String.valueOf(cat.getIcon()), -500.0f, -500.0f, 6.5f, color);
                FontsLV.MONTSERRAT_MEDIUM.draw(module.getBigName(), -500.0f, -500.0f, 7.5f, color);
                String desc = module.getDesc() != null ? module.getDesc() : "";
                FontsLV.MONTSERRAT_MEDIUM.draw(desc, -500.0f, -500.0f, 6.0f, color);
                FontsLV.MONTSERRAT_MEDIUM.width(module.getBigName(), 7.5f);
                FontsLV.MONTSERRAT_MEDIUM.width(desc, 6.0f);
            }
        }
        FontsLV.DECIDE.msdf(GEAR_GLYPH, -500.0f, -500.0f, 7.0f, color);
        Draw.rect(-500.0f, -500.0f, 30.0f, 30.0f, color, 4.0f, 4.0f, 4.0f, 4.0f);
        Draw.outline(-500.0f, -500.0f, 30.0f, 30.0f, 0.7f, color, 4.0f, 4.0f, 4.0f, 4.0f);
    }

    public void setAppearComposite(boolean v) {
        this.appearComposite = v;
    }

    public void render(class_332 dc, float x, float y, float w, float f14, float f15, float dt, LvCategory category, List<Module> modules) {
        float contentX = x + 117.0f;
        float baseY = y + 5.0f + 30.0f;
        float cardW = LvModuleList.colW(w - 122.0f);
        float col0X = contentX + 5.0f;
        float col1X = col0X + cardW + 5.0f;
        float viewH = 250.0f;
        float lerpFactor = 1.0f - (float)Math.exp(-dt * 14.0f);
        this.scroll += (this.scrollTarget - this.scroll) * lerpFactor;
        if (Math.abs(this.scrollTarget - this.scroll) < 0.05f) {
            this.scroll = this.scrollTarget;
        }
        if (this.transitioning) {
            if (this.fadeOutTime > 0.0f) {
                this.fadeOutTime -= dt;
                float alpha = f14 * Math.max(0.0f, this.fadeOutTime / 0.15f);
                this.renderCards(dc, contentX, baseY, w, cardW, col0X, col1X, viewH, alpha, f15, dt, this.fadingOut, false);
            }
        } else {
            boolean focusing;
            int sig = LvModuleList.modulesSignature(modules);
            boolean needsComposite = this.appearComposite && (category != this.lastRendered || sig != this.lastModulesSig || this.scrollBar.isDragging() || Math.abs(this.scrollTarget - this.scroll) > 0.05f || this.hasUnfinishedAppear());
            this.lastModulesSig = sig;
            if (category != this.lastRendered) {
                this.lastRendered = category;
                this.startRowAnims(LvModuleList.rowCount(modules.size()));
            }
            this.contentH = this.totalContentH(modules, cardW);
            float maxScroll = Math.max(0.0f, this.contentH - viewH);
            this.scrollTarget = Math.max(0.0f, Math.min(this.scrollTarget, maxScroll));
            int focusIdx = this.focusName != null ? LvModuleList.indexOfModule(modules, this.focusName) : -1;
            boolean bl = focusing = focusIdx >= 0 && System.currentTimeMillis() < this.focusUntilMs;
            if (this.focusScrollPending && focusIdx >= 0) {
                float focusTop = 5.0f + this.cardTopInColumn(modules, focusIdx, cardW);
                float focusH = this.baseCardHeight(modules.get(focusIdx), cardW);
                this.scrollTarget = Math.max(0.0f, Math.min(maxScroll, focusTop - (viewH - focusH) * 0.5f));
                this.focusScrollPending = false;
            }
            this.focusDimT += ((focusing ? 1.0f : 0.0f) - this.focusDimT) * (1.0f - (float)Math.exp(-dt * 8.0f));
            if (!focusing && this.focusDimT < 0.01f && !this.focusScrollPending) {
                this.focusName = null;
            }
            float sbX = x + w - 7.5f;
            float sbTrackH = viewH - 6.0f - LvModuleList.contentEdgeInset();
            float sbY = baseY + 3.0f;
            float scrollResult = this.scrollBar.render(sbX, sbY, sbTrackH, viewH, this.contentH, this.scroll, f14);
            if (this.scrollBar.isDragging()) {
                this.scroll = scrollResult;
                this.scrollTarget = scrollResult;
            }
            this.renderCards(dc, contentX, baseY, w, cardW, col0X, col1X, viewH, f14, f15, dt, modules, true);
            this.appearInitialFrame = false;
        }
    }

    private void renderCards(class_332 dc, float x, float y, float w, float cardW, float col0X, float col1X, float viewH, float alpha, float mouseAlpha, float dt, List<Module> modules, boolean withAppear) {
        Scissor.enable(x, y, w - 122.0f, viewH, 2.0f);
        float slideOffset = (1.0f - mouseAlpha) * 6.0f;
        float mx = Position.mouseX();
        float my = Position.mouseY();
        float lerp = 1.0f - (float)Math.exp(-dt * 16.0f);
        for (int i = 0; i < modules.size(); ++i) {
            String badge;
            float rowProgress;
            Module module = modules.get(i);
            int col = i % 2;
            float cx = col == 0 ? col0X : col1X;
            float ch = this.baseCardHeight(module, cardW);
            float cy = y + 5.0f + this.cardTopInColumn(modules, i, cardW) - this.scroll + slideOffset;
            boolean hover = mx >= cx && mx <= cx + cardW && my >= cy && my <= cy + ch;
            float hov = this.hoverAnims.getOrDefault(module.getName(), Float.valueOf(0.0f)).floatValue();
            hov += ((hover ? 1.0f : 0.0f) - hov) * lerp;
            this.hoverAnims.put(module.getName(), Float.valueOf(hov));
            if (cy + ch < y - 5.0f || cy > y + viewH + 5.0f) continue;
            float f = withAppear ? this.rowAppear(col == 0 ? i / 2 : i / 2) : (rowProgress = 1.0f);
            if (rowProgress < 0.001f) continue;
            float cardAlpha = alpha;
            if (this.focusDimT > 0.01f && !module.getName().equals(this.focusName)) {
                cardAlpha *= 1.0f - 0.75f * this.focusDimT;
            }
            float progress2 = Math.min(1.0f, rowProgress / 0.6f);
            float p2sq = progress2 * progress2;
            cy += (1.0f - progress2) * 6.0f;
            boolean appearing = rowProgress < 0.999f;
            float fadeAlpha = cardAlpha * (!appearing || this.appearComposite ? 1.0f : p2sq);
            float brRadius = 6.0f;
            if (appearing) {
                float scale = 0.85f + 0.15f * progress2;
                dc.method_51448().pushMatrix();
                dc.method_51448().translate(cx + cardW * 0.5f, cy + ch * 0.5f);
                dc.method_51448().scale(scale, scale);
                dc.method_51448().translate(-(cx + cardW * 0.5f), -(cy + ch * 0.5f));
            }
            if ((i & 1) == 1 && cy + ch > y + viewH - 16.0f) {
                float t = Math.max(0.0f, Math.min(1.0f, (cy + ch - (y + viewH - 16.0f)) / 16.0f));
                brRadius = 6.0f + 6.0f * t;
            }
            int bgBase = LvModuleList.rgba(10, 12, 16, (72.0f + 26.0f * hov) * fadeAlpha);
            Draw.rect(cx, cy, cardW, ch, bgBase, 6.0f, 6.0f, brRadius, 6.0f);
            Decelerate enableAnim = this.enableAnims.computeIfAbsent(module.getName(), n -> LvModuleList.createAnim(220));
            enableAnim.setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
            float enableT = enableAnim.getOutput().floatValue();
            if (enableT > 0.01f) {
                int gA = ThemeManager.gradientA(30.0f * enableT * fadeAlpha);
                int gB = ThemeManager.gradientB(30.0f * enableT * fadeAlpha);
                Draw.gradientRect(cx, cy, cardW, ch, new int[]{gA, gB, gB, gA}, 6.0f, 6.0f, brRadius, 6.0f);
            }
            int cardBgBase = LvModuleList.rgba(255, 255, 255, (14.0f + 12.0f * hov) * fadeAlpha);
            int outTL = ColorUtil.overCol(cardBgBase, ThemeManager.gradientA(65.0f * fadeAlpha), enableT);
            int outTR = ColorUtil.overCol(cardBgBase, ThemeManager.gradientB(65.0f * fadeAlpha), enableT);
            Draw.gradientOutline(cx, cy, cardW, ch, 0.6f, outTL, outTR, outTR, outTL, 6.0f, 6.0f, brRadius, 6.0f);
            float textOffsetX = 0.0f;
            int key = module.getKey();
            if (key != -1 && (badge = Keyboard.keyName(key)) != null && !badge.equals("NONE")) {
                float badgeW = Math.max(10.0f, FontsLV.MONTSERRAT_MEDIUM.width(badge, 5.5f) + 6.0f);
                float badgeX = cx + 8.0f;
                float badgeY = cy + 10.75f - 5.0f;
                Draw.rect(badgeX, badgeY, badgeW, 10.0f, LvModuleList.rgba(255, 255, 255, 16.0f * fadeAlpha), 3.0f);
                Draw.outline(badgeX, badgeY, badgeW, 10.0f, 0.5f, LvModuleList.rgba(255, 255, 255, 30.0f * fadeAlpha), 3.0f);
                float tw = FontsLV.MONTSERRAT_MEDIUM.width(badge, 5.5f);
                FontsLV.MONTSERRAT_MEDIUM.draw(badge, badgeX + (badgeW - tw) * 0.5f, badgeY + 1.75f, 5.5f, LvModuleList.rgba(255, 255, 255, 205.0f * fadeAlpha));
                textOffsetX = badgeW + 5.0f;
            }
            float nameX = cx + 8.0f + textOffsetX;
            FontsLV.MONTSERRAT_MEDIUM.draw(module.getBigName(), nameX, cy + 7.0f, 7.5f, LvModuleList.rgba(255, 255, 255, (160.0f + 95.0f * enableT) * fadeAlpha));
            this.renderDesc(module, cx, cy, cardW, fadeAlpha, enableT);
            float checkX = cx + cardW - 8.5f - 8.0f;
            float checkY = cy + 10.75f - 4.25f;
            boolean checkHover = hover && mx >= checkX - 2.0f && mx <= checkX + 8.5f + 2.0f && my >= checkY - 2.0f && my <= checkY + 8.5f + 2.0f;
            LvModuleList.drawModuleCheck(checkX, checkY, enableT, fadeAlpha, checkHover);
            if (!module.getSettings().isEmpty()) {
                float gearW = FontsLV.DECIDE.msdfWidth(GEAR_GLYPH, 7.0f);
                float gearX = checkX - 7.0f - gearW;
                float gearY = cy + 10.75f - 3.5f;
                boolean gearHover = hover && mx >= gearX - 3.0f && mx <= gearX + gearW + 3.0f && my >= cy + 4.0f && my <= cy + 17.5f;
                float gHov = this.gearHoverAnims.getOrDefault(module.getName(), Float.valueOf(0.0f)).floatValue();
                gHov += ((gearHover ? 1.0f : 0.0f) - gHov) * lerp;
                this.gearHoverAnims.put(module.getName(), Float.valueOf(gHov));
                float gOpen = this.gearOpenAnims.getOrDefault(module.getName(), Float.valueOf(0.0f)).floatValue();
                gOpen += (0.0f - gOpen) * (1.0f - (float)Math.exp(-dt * 12.0f));
                this.gearOpenAnims.put(module.getName(), Float.valueOf(gOpen));
                int gearColor = ColorUtil.overCol(LvModuleList.rgba(255, 255, 255, (120.0f + 105.0f * gHov) * fadeAlpha), ThemeManager.accentSoft(235.0f * fadeAlpha), gOpen);
                FontsLV.DECIDE.msdf(GEAR_GLYPH, gearX, gearY, 7.0f, gearColor);
            }
            if (!appearing) continue;
            dc.method_51448().popMatrix();
        }
        Scissor.disable();
    }

    public List<Module> getModules(LvCategory category) {
        Category targetCat;
        if (category == null) {
            return Collections.emptyList();
        }
        switch (category) {
            case VISUALS: {
                Category category2 = Category.VISUALS;
                break;
            }
            case HUD: {
                Category category2 = Category.HUD;
                break;
            }
            case UTILITIES: {
                Category category2 = Category.UTILITIES;
                break;
            }
            default: {
                Category category2 = targetCat = null;
            }
        }
        if (targetCat == null) {
            return Collections.emptyList();
        }
        ArrayList<Module> all = new ArrayList<Module>(Client.get().moduleManager().get(targetCat));
        all.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        return all;
    }

    public float[] cardRect(List<Module> list, int index, float x, float y, float w) {
        if (list != null && index >= 0 && index < list.size()) {
            float cw = LvModuleList.colW(w);
            float col0X = x + 5.0f;
            float col1X = col0X + cw + 5.0f;
            float cx = index % 2 == 0 ? col0X : col1X;
            float cy = y + 5.0f + this.cardTopInColumn(list, index, cw) - this.scroll;
            float ch = this.baseCardHeight(list.get(index), cw);
            return new float[]{cx, cy, cw, ch};
        }
        return null;
    }

    public void scroll(float amount, float maxScrollH) {
        float max = Math.max(0.0f, this.contentH - maxScrollH);
        this.scrollTarget = Math.max(0.0f, Math.min(max, this.scrollTarget - amount * 16.0f));
    }

    public boolean scrollbarGrab(float x, float y) {
        return this.scrollBar.tryGrab(x, y);
    }

    public void scrollbarRelease() {
        this.scrollBar.release();
    }

    public boolean hitSettingsIcon(float mx, float my, float cx, float cy, float cw, Module module) {
        if (module.getSettings().isEmpty()) {
            return false;
        }
        float checkX = cx + cw - 8.5f - 8.0f;
        float gearW = FontsLV.DECIDE.msdfWidth(GEAR_GLYPH, 7.0f);
        float gearX = checkX - 7.0f - gearW;
        return mx >= gearX - 3.5f && mx <= gearX + gearW + 3.5f && my >= cy + 3.5f && my <= cy + 18.0f;
    }

    public void finishTransition() {
        this.transitioning = false;
        this.fadingOut = Collections.emptyList();
        this.fadeOutTime = 0.0f;
        this.scroll = 0.0f;
        this.scrollTarget = 0.0f;
        this.lastRendered = null;
    }

    public void focusModule(String name) {
        this.focusName = name;
        this.focusUntilMs = System.currentTimeMillis() + 5000L;
        this.focusScrollPending = true;
    }

    private List<String> descLines(Module module, float w) {
        String key = module.getName() + "|" + (int)w;
        List<String> cached = this.descLinesCache.get(key);
        if (cached != null) {
            return cached;
        }
        String desc = module.getDesc() != null ? module.getDesc() : "";
        ArrayList<String> lines = new ArrayList<String>();
        if (!desc.isEmpty()) {
            float maxLineW = w - 16.0f - 4.0f;
            StringBuilder sb = new StringBuilder();
            for (String word : desc.split(" ")) {
                String test;
                String string = test = sb.length() > 0 ? String.valueOf(sb) + " " + word : word;
                if (FontsLV.MONTSERRAT_MEDIUM.width(test, 6.0f) > maxLineW && sb.length() > 0) {
                    lines.add(sb.toString());
                    sb = new StringBuilder(word);
                    continue;
                }
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(word);
            }
            if (sb.length() > 0) {
                lines.add(sb.toString());
            }
        }
        this.descLinesCache.put(key, lines);
        return lines;
    }

    public float baseCardHeight(Module module, float w) {
        String key = module.getName() + "|" + (int)w;
        Float cached = this.baseHeightCache.get(key);
        if (cached != null) {
            return cached.floatValue();
        }
        List<String> lines = this.descLines(module, w);
        float h = lines.isEmpty() ? 22.0f : 19.0f + (float)lines.size() * 7.0f + 7.0f;
        this.baseHeightCache.put(key, Float.valueOf(h));
        return h;
    }

    private void renderDesc(Module module, float x, float y, float w, float alpha, float enableT) {
        List<String> lines = this.descLines(module, w);
        if (!lines.isEmpty()) {
            int color = LvModuleList.rgba(255, 255, 255, (78.0f + 27.0f * enableT) * alpha);
            float ly = y + 19.0f;
            for (String line : lines) {
                FontsLV.MONTSERRAT_MEDIUM.draw(line, x + 8.0f, ly, 6.0f, color);
                ly += 7.0f;
            }
        }
    }

    private float cardTopInColumn(List<Module> list, int index, float w) {
        float top = 0.0f;
        for (int i = index % 2; i < index; i += 2) {
            top += this.baseCardHeight(list.get(i), w) + 5.0f;
        }
        return top;
    }

    private float totalContentH(List<Module> list, float w) {
        float evenH = 0.0f;
        float oddH = 0.0f;
        for (int i = 0; i < list.size(); ++i) {
            float ch = this.baseCardHeight(list.get(i), w) + 5.0f;
            if ((i & 1) == 0) {
                evenH += ch;
                continue;
            }
            oddH += ch;
        }
        float maxH = Math.max(evenH, oddH);
        if (maxH > 0.0f) {
            maxH -= 5.0f;
        }
        return 10.0f + maxH;
    }

    private static void drawModuleCheck(float x, float y, float enableT, float alpha, boolean hover) {
        Draw.rect(x, y, 8.5f, 8.5f, LvModuleList.rgba(17, 17, 18, 91.0f * alpha), 2.9750001f);
        if (enableT < 0.996f) {
            float fade = alpha * (1.0f - enableT);
            float r1 = 1.2750001f;
            Draw.outline(x + r1, y + r1, 8.5f - r1 * 2.0f, 8.5f - r1 * 2.0f, CHECK_RING_THICKNESS, ThemeManager.accent((hover ? 80.0f : 35.0f) * fade), 2.5500002f);
            float r2 = 2.5500002f;
            Draw.outline(x + r2, y + r2, 8.5f - r2 * 2.0f, 8.5f - r2 * 2.0f, CHECK_RING_THICKNESS, ThemeManager.accent((hover ? 45.0f : 20.0f) * fade), 2.125f);
        }
        if (enableT > 0.004f) {
            float fillAlpha = 255.0f * enableT * alpha;
            int cA = ThemeManager.gradientA(fillAlpha);
            int cB = LvModuleList.checkDarker(ThemeManager.gradientB(fillAlpha), 100);
            Draw.rect(x, y, 8.5f, 8.5f, cA, 2.9750001f, 2.9750001f, cB, cB);
            float inset = 2.125f;
            float inner = 8.5f - inset * 2.0f;
            Draw.rect(x + inset, y + inset, inner, inner, LvModuleList.rgba(255, 255, 255, 255.0f * enableT * alpha), inner * 0.5f);
        }
    }

    private static int checkDarker(int color, int amount) {
        int a = color >>> 24 & 0xFF;
        int r = Math.max(0, (color >> 16 & 0xFF) - amount);
        int g = Math.max(0, (color >> 8 & 0xFF) - amount);
        int b = Math.max(0, (color & 0xFF) - amount);
        return a << 24 | r << 16 | g << 8 | b;
    }

    private static int rgba(int r, int g, int b, float a) {
        int ai = Math.max(0, Math.min(255, Math.round(a)));
        return ai <= 0 ? 0 : new Color(r, g, b, ai).getRGB();
    }

    private static float contentEdgeInset() {
        float radius = 12.0f;
        float inner = radius - 1.0f;
        float edge = radius - (float)Math.sqrt(Math.max(0.0f, radius * radius - inner * inner));
        return Math.max(0.0f, edge + 1.5f - 3.0f);
    }

    private static float colW(float w) {
        return (w - 5.0f - 10.0f) * 0.5f;
    }

    private static int rowCount(int n) {
        return (n + 1) / 2;
    }

    private static int modulesSignature(List<Module> list) {
        int n = list.size();
        if (!list.isEmpty()) {
            n = n * 31 + list.get(0).getName().hashCode();
            n = n * 31 + list.get(list.size() - 1).getName().hashCode();
        }
        return n;
    }

    private static int indexOfModule(List<Module> list, String name) {
        for (int i = 0; i < list.size(); ++i) {
            if (!list.get(i).getName().equals(name)) continue;
            return i;
        }
        return -1;
    }

    private static Decelerate createAnim(int ms) {
        Decelerate d = new Decelerate();
        d.setMs(ms);
        d.setValue(1.0);
        d.setDirection(Direction.BACKWARDS);
        d.counter.setTime(System.currentTimeMillis() - 10000L);
        return d;
    }

    private void startRowAnims(int count) {
        this.rowAppearAnims.clear();
        this.appearFadeMs = this.quickAppear ? 320 : 380;
        this.quickAppear = false;
        this.appearBaseMs = System.currentTimeMillis();
        this.appearInitialFrame = true;
    }

    private boolean hasUnfinishedAppear() {
        for (Decelerate d : this.rowAppearAnims.values()) {
            if (!(d.getOutput().floatValue() < 0.999f)) continue;
            return true;
        }
        return false;
    }

    private float rowAppear(int row) {
        Decelerate d = this.rowAppearAnims.get(row);
        if (d == null) {
            long t = this.appearInitialFrame ? this.appearBaseMs : System.currentTimeMillis();
            d = new Decelerate();
            d.setMs(this.appearFadeMs);
            d.setValue(1.0);
            d.counter.setTime(t);
            this.rowAppearAnims.put(row, d);
        }
        return Math.max(0.0f, Math.min(1.0f, d.getOutput().floatValue()));
    }
}

