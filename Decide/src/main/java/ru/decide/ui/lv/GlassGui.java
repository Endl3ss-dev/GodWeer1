/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 */
package ru.decide.ui.lv;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_437;
import ru.decide.Client;
import ru.decide.module.api.Module;
import ru.decide.ui.fonts.FontsLV;
import ru.decide.ui.lv.LvBindPopup;
import ru.decide.ui.lv.LvCategory;
import ru.decide.ui.lv.LvEventsRenderer;
import ru.decide.ui.lv.LvModuleList;
import ru.decide.ui.lv.LvRectUtil;
import ru.decide.ui.lv.LvSearchField;
import ru.decide.ui.lv.LvSettingsPopup;
import ru.decide.ui.lv.LvThemesRenderer;
import ru.decide.ui.theme.AccentGradient;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.ui.util.Position;
import ru.decide.ui.util.anim.Decelerate;
import ru.decide.ui.util.anim.Direction;
import ru.decide.ui.util.anim.GuiMotionAnimation;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.other.GuiSounds;
import ru.decide.utils.render.Draw;
import ru.decide.utils.render.Render2D;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.ScreenBlur;

@Environment(value=EnvType.CLIENT)
public class GlassGui
extends class_437
implements IMinecraft {
    private static final float PANEL_W = 430.0f;
    private static final float PANEL_H = 290.0f;
    private static final float SIDEBAR_W = 110.0f;
    private static final float CONTENT_X_OFF = 117.0f;
    private static final float CONTENT_INSET = 122.0f;
    private static final float CONTENT_HEIGHT = 280.0f;
    private static final float CONTENT_Y_OFFSET = 5.0f;
    private static final float CAT_COL_TOP = 34.0f;
    private static final float CAT_HEADER_H = 20.0f;
    private static final float CAT_SUB_GAP = 4.0f;
    private static final float CAT_SUB_ROW_H = 18.0f;
    private static final float CAT_OTHERS_GAP = 12.0f;
    private static final float CAT_EVENTS_GAP = 3.0f;
    private static final float CAT_OTHER_ROW_H = 19.0f;
    private static final float CATEGORY_FADE_SEC = 0.15f;
    private static final String[] EVENT_SUBS = new String[]{"Discord", "Telegram"};
    private static final String[] EVENT_SUB_IMAGES = new String[]{"decide:images/discord.png", "decide:images/telegram.png"};
    private static final LvCategory[] CATEGORIES = new LvCategory[]{LvCategory.VISUALS, LvCategory.HUD, LvCategory.UTILITIES};
    public static final GlassGui INSTANCE = new GlassGui();
    private final LvModuleList moduleList = new LvModuleList();
    private final LvSearchField search = new LvSearchField();
    private final LvBindPopup bindPopup = new LvBindPopup();
    private final LvSettingsPopup settingsPopup = new LvSettingsPopup();
    private final LvThemesRenderer themesRenderer = new LvThemesRenderer();
    private final LvEventsRenderer eventsRenderer = new LvEventsRenderer();
    private final GuiMotionAnimation screenAnim = new GuiMotionAnimation();
    private final Map<LvCategory, Decelerate> categoryAnims = new EnumMap<LvCategory, Decelerate>(LvCategory.class);
    private float categoryT = 1.0f;
    private float themesRowT = 1.0f;
    private long lastNs = System.nanoTime();
    private LvCategory targetCategory = null;
    private LvCategory contentCategory = null;
    private boolean soundPlayed;
    private float scaleFix = 1.0f;

    private GlassGui() {
        super((class_2561)class_2561.method_43470((String)"GlassGui"));
    }

    private static Decelerate createAnim(int ms) {
        Decelerate d = new Decelerate().setMs(ms).setValue(1.0);
        d.setDirection(Direction.BACKWARDS);
        d.counter.setTime(System.currentTimeMillis() - 10000L);
        return d;
    }

    private static int rgba(int r, int g, int b, float a) {
        int a2 = Math.max(0, Math.min(255, Math.round(a)));
        return a2 <= 0 ? 0 : a2 << 24 | r << 16 | g << 8 | b;
    }

    private static int rgba(int r, int g, int b, int a, float f) {
        int a2 = Math.max(0, Math.min(255, Math.round((float)a * f)));
        return a2 <= 0 ? 0 : a2 << 24 | r << 16 | g << 8 | b;
    }

    private Decelerate getCategoryAnim(LvCategory c) {
        return this.categoryAnims.computeIfAbsent(c, k -> GlassGui.createAnim(200));
    }

    private boolean isModuleView() {
        return this.contentCategory != null && this.contentCategory != LvCategory.THEMES && this.contentCategory != LvCategory.EVENTS;
    }

    private List<Module> filteredModules(LvCategory category) {
        String q = this.search.getText().trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) {
            return this.moduleList.getModules(category);
        }
        ArrayList<Module> result = new ArrayList<Module>();
        for (Module m : Client.get().moduleManager().values()) {
            if (!m.getName().toLowerCase(Locale.ROOT).contains(q)) continue;
            result.add(m);
        }
        return result;
    }

    public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
        this.scaleFix = 2.0f / (float)mc.method_22683().method_4495();
        mouseX = (int)((float)mouseX / this.scaleFix);
        mouseY = (int)((float)mouseY / this.scaleFix);
        this.screenAnim.updateFrame();
        if (this.screenAnim.isCloseFinished() && GlassGui.mc.field_1755 == this) {
            this.screenAnim.snapClosed();
            mc.method_1507(null);
            return;
        }
        this.renderOverlay(context);
    }

    public void method_25420(class_332 context, int mouseX, int mouseY, float delta) {
    }

    public void renderOverlay(class_332 context) {
        if (context != null) {
            context.method_51448().pushMatrix();
        }
        Render2D.beginOverlay();
        float alpha = this.screenAnim.alpha();
        int screenWidth = (int)((float)mc.method_22683().method_4486() / this.scaleFix);
        int screenHeight = (int)((float)mc.method_22683().method_4502() / this.scaleFix);
        ScreenBlur.capture(2);
        RenderUtil.Blur.blur(0.0f, 0.0f, screenWidth, screenHeight, alpha, ColorUtil.getColor(0, alpha * 0.2f));
        this.renderPanel(context);
        Render2D.endOverlay();
        if (context != null) {
            context.method_51448().popMatrix();
        }
    }

    private void renderPanel(class_332 ctx) {
        float alpha = this.screenAnim.alpha();
        if (alpha <= 0.01f) {
            return;
        }
        float sw = Position.screenWidth();
        float sh = Position.screenHeight();
        float px = sw / 2.0f - 215.0f;
        float py = sh / 2.0f - 145.0f;
        long now = System.nanoTime();
        float dt = Math.min(0.1f, (float)(now - this.lastNs) / 1.0E9f);
        this.lastNs = now;
        Draw.rect(-5.0f, -5.0f, sw + 10.0f, sh + 10.0f, GlassGui.rgba(0, 0, 0, 60, alpha));
        LvRectUtil.drawClientRect(px, py, 430.0f, 290.0f, 12.0f, alpha);
        LvRectUtil.drawPanelBg(px + 5.0f, py + 5.0f, 110.0f, 280.0f, 12.0f, 0.0f, 0.0f, 12.0f, alpha);
        LvRectUtil.drawPanelBg(px + 117.0f, py + 5.0f, 308.0f, 280.0f, 0.0f, 12.0f, 12.0f, 0.0f, alpha);
        this.renderSidebar(px + 5.0f, py + 2.0f, 290.0f, alpha);
        this.themesRowT = Math.min(1.0f, this.themesRowT + dt / 0.15f);
        float contentAlpha = alpha * this.categoryT;
        boolean canRender = !this.bindPopup.isVisible() && !this.settingsPopup.isVisible();
        this.moduleList.setAppearComposite(canRender);
        this.themesRenderer.setAppearComposite(canRender);
        if (contentAlpha > 0.01f && this.contentCategory == LvCategory.EVENTS) {
            this.eventsRenderer.render(ctx, px, py, 430.0f, contentAlpha, dt);
        } else if (contentAlpha > 0.01f && this.contentCategory == LvCategory.THEMES) {
            this.themesRenderer.render(ctx, px, py, 430.0f, contentAlpha, this.categoryT, dt);
        } else if (contentAlpha > 0.01f && this.contentCategory != null) {
            List<Module> list = this.filteredModules(this.contentCategory);
            this.moduleList.render(ctx, px, py, 430.0f, contentAlpha, this.categoryT, dt, this.contentCategory, list);
            if (list.isEmpty() && this.search.hasText()) {
                this.renderNoResults(px, py, 430.0f, contentAlpha);
            }
        }
        if (this.isModuleView() && alpha > 0.01f) {
            this.renderModuleHeader(ctx, px, py, 430.0f, alpha);
        }
        if (this.settingsPopup.isVisible()) {
            this.settingsPopup.render(ctx, alpha);
        }
        if (this.bindPopup.isVisible()) {
            this.bindPopup.render(ctx, alpha);
        }
    }

    private void renderSidebar(float x, float y, float h, float alpha) {
        float midY = y + 13.0f;
        String icon = "x";
        float iconSize = 11.7f;
        float iconW = FontsLV.DECIDE.width(icon, iconSize);
        String title = "DecideVisuals";
        float titleSize = 14.3f;
        float titleW = FontsLV.SMALL_PIXEL.width(title, titleSize);
        float tx = x + (110.0f - (iconW + 7.8f + titleW)) * 0.5f;
        AccentGradient.msdfIcon("decidevisuals", icon, tx, midY - iconSize * 0.5f + 0.5f, iconSize, 225.0f * alpha, 0.1f);
        FontsLV.SMALL_PIXEL.draw(title, tx + iconW + 7.8f, midY - titleSize * 0.5f + 0.5f, titleSize, GlassGui.rgba(255, 255, 255, 255, alpha));
        float catY = y + 34.0f;
        String modIcon = "h";
        float modIconW = FontsLV.DECIDE.width(modIcon, 8.0f);
        AccentGradient.msdfIcon("decidevisuals", modIcon, x + 9.0f, catY + 10.0f - 4.0f + 1.5f, 8.0f, 255.0f * alpha, 0.15f);
        FontsLV.MONTSERRAT_MEDIUM.draw("Modules", x + 9.0f + modIconW + 6.0f, catY + 10.0f - 4.0f + 0.5f, 8.0f, GlassGui.rgba(255, 255, 255, 255, alpha));
        float subY = catY + 20.0f + 4.0f;
        float lineX = x + 12.0f;
        float lineTop = subY + 9.0f;
        float lineBot = subY + 36.0f + 9.0f;
        Draw.rect(lineX, lineTop, 1.0f, lineBot - lineTop, GlassGui.rgba(255, 255, 255, 36, alpha));
        for (int i = 0; i < CATEGORIES.length; ++i) {
            LvCategory cat = CATEGORIES[i];
            float rowY = subY + (float)i * 18.0f + 9.0f;
            float f31 = this.getCategoryAnim(cat).getOutput().floatValue();
            int n2 = Math.min(255, 140 + Math.round(f31 * 115.0f));
            String catIcon = String.valueOf(cat.getIcon());
            float ciW = FontsLV.DECIDE.width(catIcon, 7.0f);
            if (f31 > 0.01f) {
                float fW = (ciW + 5.0f + FontsLV.MONTSERRAT_MEDIUM.width(cat.getDisplayName(), 7.0f)) * f31;
                AccentGradient.fillHorizontal(lineX + 3.0f, rowY + 8.5f - 2.0f, fW, 0.75f, 0.625f, 88.0f * f31 * alpha);
            }
            AccentGradient.msdfIcon("decidevisuals", catIcon, lineX + 3.0f, rowY - 3.5f + 1.5f - 2.0f, 7.0f, (float)n2 * alpha, (float)i / 2.0f);
            FontsLV.MONTSERRAT_MEDIUM.draw(cat.getDisplayName(), lineX + 3.0f + ciW + 5.0f, rowY - 3.5f + 0.5f - 2.0f, 7.0f, GlassGui.rgba(255, 255, 255, n2, alpha));
            String count = String.valueOf(this.moduleList.getModules(cat).size());
            float countW = FontsLV.MONTSERRAT_MEDIUM.width(count, 5.5f);
            FontsLV.MONTSERRAT_MEDIUM.draw(count, x + 110.0f - 10.0f - countW, rowY - 2.75f + 0.5f - 2.0f, 5.5f, GlassGui.rgba(255, 255, 255, Math.round(80.0f + 80.0f * f31), alpha));
        }
        float infoY = subY + 54.0f + 3.0f + 10.0f;
        String infoIcon = "e";
        float infoIconW = FontsLV.DECIDE.width(infoIcon, 8.0f);
        AccentGradient.msdfIcon("decidevisuals", infoIcon, x + 9.0f, infoY - 4.0f + 1.5f, 8.0f, 255.0f * alpha, 0.6f);
        FontsLV.MONTSERRAT_MEDIUM.draw("Information", x + 9.0f + infoIconW + 6.0f, infoY - 4.0f + 0.5f, 8.0f, GlassGui.rgba(255, 255, 255, 255, alpha));
        float subStartY = infoY + 20.0f + 4.0f;
        float subLineTop = subStartY + 9.0f;
        float subLineBot = subStartY + (float)(EVENT_SUBS.length - 1) * 18.0f + 9.0f;
        Draw.rect(lineX, subLineTop, 1.0f, subLineBot - subLineTop, GlassGui.rgba(255, 255, 255, 36, alpha));
        for (int i = 0; i < EVENT_SUBS.length; ++i) {
            float rowY = subStartY + (float)i * 18.0f + 9.0f;
            float iconSz = 7.0f;
            float n2 = 200.0f * alpha;
            Draw.texture(class_2960.method_60654((String)EVENT_SUB_IMAGES[i]), lineX + 3.0f, rowY - 3.5f + 1.5f - 2.0f, iconSz, iconSz, GlassGui.rgba(255, 255, 255, Math.round(n2)));
            FontsLV.MONTSERRAT_MEDIUM.draw(EVENT_SUBS[i], lineX + 3.0f + iconSz + 5.0f, rowY - 3.5f + 0.5f - 2.0f, iconSz, GlassGui.rgba(255, 255, 255, Math.round(n2)));
        }
        if (this.themesRowT > 0.01f) {
            float themesY = subStartY + (float)EVENT_SUBS.length * 18.0f + 12.0f + 9.5f;
            float f50 = this.getCategoryAnim(LvCategory.THEMES).getOutput().floatValue();
            int n6 = Math.round(Math.min(255.0f, 140.0f + f50 * 115.0f) * this.themesRowT);
            String tIcon = String.valueOf(LvCategory.THEMES.getIcon());
            float tIconW = FontsLV.DECIDE.width(tIcon, 7.5f);
            AccentGradient.msdfIcon("decidevisuals", tIcon, x + 9.0f, themesY - 3.75f + 1.5f - 2.0f, 7.5f, (float)n6 * alpha, 0.85f);
            FontsLV.MONTSERRAT_MEDIUM.draw("Themes", x + 9.0f + tIconW + 6.0f, themesY - 3.5f + 0.5f - 2.0f, 7.0f, GlassGui.rgba(255, 255, 255, n6, alpha));
        }
    }

    private void renderModuleHeader(class_332 ctx, float px, float py, float pw, float alpha) {
        float cx = px + 117.0f;
        float cy = py + 5.0f;
        float cw = pw - 122.0f;
        LvRectUtil.drawPanelBg(cx, cy, cw, 26.0f, 0.0f, 12.0f, 0.0f, 0.0f, alpha);
        float avX = cx + cw - 6.0f - 18.0f;
        float avY = cy + 4.0f;
        Draw.rect(avX, avY, 18.0f, 18.0f, ThemeManager.rgba(0, 90.0f * alpha), 9.0f);
        Draw.outline(avX, avY, 18.0f, 18.0f, 0.8f, ThemeManager.rgba(0xFFFFFF, 30.0f * alpha), 9.0f);
        String name = mc.method_1548().method_1676();
        float nameW = FontsLV.MONTSERRAT_MEDIUM.width(name, 6.5f);
        FontsLV.MONTSERRAT_MEDIUM.draw(name, avX - 5.0f - nameW, avY + 5.0f, 6.5f, GlassGui.rgba(255, 255, 255, 230, alpha));
        float searchX = cx + 10.0f;
        float searchW = Math.max(60.0f, avX - Math.max(nameW, 0.0f) - 10.0f - searchX);
        float searchY = cy + 6.0f;
        this.search.render(ctx, searchX, searchY, searchW, 14.0f, alpha, Position.mouseX(), Position.mouseY(), 0.016f);
    }

    private void renderNoResults(float px, float py, float pw, float alpha) {
        float cx = px + 117.0f;
        float cw = pw - 122.0f;
        String text = "\u041d\u0438\u0447\u0435\u0433\u043e \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u043e";
        float tw = FontsLV.MONTSERRAT_MEDIUM.width(text, 6.5f);
        FontsLV.MONTSERRAT_MEDIUM.draw(text, cx + (cw - tw) * 0.5f, py + 5.0f + 30.0f + 125.0f - 3.0f, 6.5f, GlassGui.rgba(255, 255, 255, 110, alpha));
    }

    protected void method_25426() {
        this.screenAnim.startOpening();
        this.moduleList.warmup();
        this.targetCategory = LvCategory.VISUALS;
        this.contentCategory = LvCategory.VISUALS;
        this.categoryT = 1.0f;
        if (!this.soundPlayed) {
            GuiSounds.open();
            this.soundPlayed = true;
        }
    }

    public void method_25419() {
        this.moduleList.scrollbarRelease();
        this.eventsRenderer.scrollbarRelease();
        this.settingsPopup.close();
        this.search.blur();
        this.bindPopup.releaseDrag();
        GuiSounds.close();
        if (GlassGui.mc.field_1755 == this) {
            mc.method_1507(null);
        }
    }

    public void method_25432() {
        this.soundPlayed = false;
        super.method_25432();
    }

    public boolean method_25422() {
        return false;
    }

    public boolean method_25421() {
        return false;
    }

    public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        float contentH = 260.0f;
        if (this.isModuleView()) {
            this.moduleList.scroll((float)verticalAmount, contentH);
        } else if (this.contentCategory == LvCategory.EVENTS) {
            this.eventsRenderer.scroll((float)verticalAmount, contentH);
        }
        return true;
    }
}

