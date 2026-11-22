/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11905
 *  net.minecraft.class_11908
 *  net.minecraft.class_11909
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_3298
 *  net.minecraft.class_332
 *  net.minecraft.class_3532
 *  net.minecraft.class_437
 *  net.minecraft.class_9779
 *  org.lwjgl.glfw.GLFW
 */
package ru.decide.screen;

import java.awt.Color;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_3298;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_437;
import net.minecraft.class_9779;
import org.lwjgl.glfw.GLFW;
import ru.decide.Client;
import ru.decide.command.impl.WayCommand;
import ru.decide.cosmetics.CosmeticManager;
import ru.decide.cosmetics.CosmeticType;
import ru.decide.cosmetics.melee.MeleeRegistry;
import ru.decide.cosmetics.melee.MeleeSettings;
import ru.decide.cosmetics.render.CosmeticGuiPreview;
import ru.decide.manager.Theme;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.settings.Setting;
import ru.decide.module.api.settings.impl.BindSetting;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ButtonSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.MultiBooleanSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.api.settings.impl.StringSetting;
import ru.decide.module.impl.display.ClickGui;
import ru.decide.screen.CrosshairEditor;
import ru.decide.screen.GuiCloseAnimation3D;
import ru.decide.screen.GuiCloseAnimationShatter;
import ru.decide.screen.HandsEditor;
import ru.decide.screen.PreviewEditor;
import ru.decide.screen.editor.OverlayEditor;
import ru.decide.screen.editor.OverlayEditors;
import ru.decide.ui.lv.LvGui;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.animation.satoshi.Direction;
import ru.decide.utils.animation.satoshi.EaseInOutQuad;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.colors.ColorFormatting;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.math.Keyboard;
import ru.decide.utils.math.MathUtil;
import ru.decide.utils.other.ClipboardPaste;
import ru.decide.utils.other.GuiMusicPlayer;
import ru.decide.utils.other.GuiSounds;
import ru.decide.utils.render.ClickGuiDotsPipeline;
import ru.decide.utils.render.GifTexture;
import ru.decide.utils.render.GrayscalePipeline;
import ru.decide.utils.render.HalftoneDotsPipeline;
import ru.decide.utils.render.MenuRaysPipeline;
import ru.decide.utils.render.Render2D;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.ScanLinesPipeline;
import ru.decide.utils.render.Scissor;
import ru.decide.utils.render.ScreenBlur;
import ru.decide.utils.render.font.Font;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public class Menu
extends class_437
implements IMinecraft {
    public static float S = 1.0f;
    public static boolean returnToGlassAfterEditor = false;
    boolean exit = false;
    private boolean use3DAnim = false;
    public Animation glomalAnim = new Animation();
    private final HandsEditor handsEditor = HandsEditor.getInstance();
    private float scaleFix = 1.0f;
    double lastMouseX;
    double lastMouseY;
    public static Theme selectedTheme;
    public static Theme preSelectedTheme;
    public static Theme[] themes;
    public static ru.decide.utils.animation.satoshi.Animation animation14;
    public static ru.decide.utils.animation.satoshi.Animation animCategoryReset;
    public static ru.decide.utils.animation.satoshi.Animation animUserInfo;
    public static ru.decide.utils.animation.satoshi.Animation animation1;
    public static ru.decide.utils.animation.satoshi.Animation animation2;
    public static ru.decide.utils.animation.satoshi.Animation animation3;
    public static ru.decide.utils.animation.satoshi.Animation animation4;
    public Module select = null;
    public Category active = Category.VISUALS;
    private float scrollTarget = 0.0f;
    private float scrollAnim = 0.0f;
    private float maxScroll = 0.0f;
    private float settingScrollTarget = 0.0f;
    private float settingScrollAnim = 0.0f;
    private float settingMaxScroll = 0.0f;
    private SliderSetting draggingSlider = null;
    private BindSetting activeBind = null;
    private Module bindingModule = null;
    private StringSetting activeString = null;
    private String stringBuffer = "";
    private ColorSetting draggingColor = null;
    private int draggingColorBar = 0;
    public static boolean searchActive;
    private static String searchQuery;
    private long searchTypeTime = System.currentTimeMillis();
    private final ru.decide.utils.animation.satoshi.Animation animSearchFocus = new EaseInOutQuad(300, 1.0, Direction.BACKWARDS);
    private final ru.decide.utils.animation.satoshi.Animation animSearchText = new EaseInOutQuad(300, 1.0, Direction.BACKWARDS);
    private final ru.decide.utils.animation.satoshi.Animation animSearchEmpty = new EaseInOutQuad(300, 1.0, Direction.BACKWARDS);
    private static final int SEARCH_LIMIT = 24;
    private final Set<String> expandedModes = new HashSet<String>();
    private String configInput = "";
    private boolean configInputFocused = false;
    private float configScroll = 0.0f;
    private float configScrollTarget = 0.0f;
    private final List<String> configCache = new ArrayList<String>();
    private final List<float[]> configRowRects = new ArrayList<float[]>();
    private final List<float[]> configDelRects = new ArrayList<float[]>();
    private String markerNameInput = "";
    private String markerXInput = "";
    private String markerYInput = "";
    private String markerZInput = "";
    private int markerInputFocused = -1;
    private float markerScroll = 0.0f;
    private float markerScrollTarget = 0.0f;
    private final List<String[]> markerCache = new ArrayList<String[]>();
    private final List<float[]> markerRowRects = new ArrayList<float[]>();
    private final List<float[]> markerDelRects = new ArrayList<float[]>();
    private CosmeticType activeCosmetic = CosmeticType.WINGS;
    private static final String[][] ACCESSORY_GROUPS;
    private String accessoriesGroup = "watch";
    private final List<float[]> accessoryTabRects = new ArrayList<float[]>();
    private float cosmeticsScroll = 0.0f;
    private float cosmeticsScrollAnim = 0.0f;
    private float cosmeticsScrollTarget = 0.0f;
    private float cosmeticsMaxScroll = 0.0f;
    private float cosmeticsTabScroll = 0.0f;
    private float cosmeticsTabScrollAnim = 0.0f;
    private float cosmeticsTabScrollTarget = 0.0f;
    private float cosmeticsTabMaxScroll = 0.0f;
    private boolean meleeInspectBinding = false;
    private final float[] meleeFirstRect = new float[4];
    private final float[] meleeThirdRect = new float[4];
    private final float[] meleeKeyRect = new float[4];
    private final List<CosmeticClickTarget> cosmeticsItemButtons = new ArrayList<CosmeticClickTarget>();
    private final HashMap<String, ru.decide.utils.animation.satoshi.Animation> chipAnims = new HashMap();
    private final GrayscalePipeline grayscalePipeline = new GrayscalePipeline();
    private final ClickGuiDotsPipeline dotsPipeline = new ClickGuiDotsPipeline();
    private final ScanLinesPipeline scanLinesPipeline = new ScanLinesPipeline();
    private final MenuRaysPipeline raysPipeline = new MenuRaysPipeline();
    private final HalftoneDotsPipeline halftonePipeline = new HalftoneDotsPipeline();
    private final List<GuiParticle> particles = new ArrayList<GuiParticle>();
    private long lastParticle;
    private long particleDelay = 90L;
    private final HashMap<String, Float> descHeights = new HashMap();
    private final HashMap<String, float[]> smoothVals = new HashMap();
    private final IdentityHashMap<Setting<?>, ru.decide.utils.animation.satoshi.Animation> visAnims = new IdentityHashMap();
    private final IdentityHashMap<Setting<?>, float[]> chipsHeights = new IdentityHashMap();
    private GifTexture gif;
    private float cfgInputX;
    private float cfgInputY;
    private float cfgInputW;
    private float cfgInputH;
    private float cfgAddX;
    private float cfgAddY;
    private float cfgAddW;
    private float cfgAddH;
    private float cfgListX;
    private float cfgListY;
    private float cfgListW;
    private float cfgListH;
    private float mkNameX;
    private float mkNameY;
    private float mkNameW;
    private float mkNameH;
    private float mkXInputX;
    private float mkXInputY;
    private float mkXInputW;
    private float mkXInputH;
    private float mkYInputX;
    private float mkYInputY;
    private float mkYInputW;
    private float mkYInputH;
    private float mkZInputX;
    private float mkZInputY;
    private float mkZInputW;
    private float mkZInputH;
    private float mkAddX;
    private float mkAddY;
    private float mkAddW;
    private float mkAddH;
    private float mkListX;
    private float mkListY;
    private float mkListW;
    private float mkListH;

    public Menu() {
        super((class_2561)class_2561.method_43470((String)"MenuNight"));
    }

    private static String[][] groupsFor(CosmeticType tab) {
        return tab == CosmeticType.ACCESSORIES ? ACCESSORY_GROUPS : null;
    }

    private String activeGroup() {
        return this.activeCosmetic == CosmeticType.ACCESSORIES ? this.accessoriesGroup : null;
    }

    private void setActiveGroup(String group) {
        if (this.activeCosmetic == CosmeticType.ACCESSORIES) {
            this.accessoriesGroup = group;
        }
    }

    private ru.decide.utils.animation.satoshi.Animation chipAnim(String key) {
        return this.chipAnims.computeIfAbsent(key, k -> new EaseInOutQuad(300, 1.0));
    }

    private ru.decide.utils.animation.satoshi.Animation expandAnim(String key) {
        return this.chipAnims.computeIfAbsent("expand:" + key, k -> new EaseInOutQuad(300, 1.0));
    }

    private boolean effect(String name) {
        ClickGui gui = Client.get().moduleManager().get(ClickGui.class);
        return gui != null && gui.effect.getValue(name);
    }

    private void spawnParticle(int screenWidth, int screenHeight) {
        if (System.currentTimeMillis() - this.lastParticle < this.particleDelay) {
            return;
        }
        this.lastParticle = System.currentTimeMillis();
        this.particleDelay = 70L + (long)(Math.random() * 90.0);
        if (this.particles.size() > 60) {
            return;
        }
        this.particles.add(new GuiParticle((float)(Math.random() * (double)screenWidth), (float)(Math.random() * (double)screenHeight)));
    }

    private static float smoothstep(float edge0, float edge1, float value) {
        float t = class_3532.method_15363((float)((value - edge0) / (edge1 - edge0)), (float)0.0f, (float)1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    private void renderParticles(float globalAnim) {
        for (int i = this.particles.size() - 1; i >= 0; --i) {
            float fade;
            float alpha;
            GuiParticle p = this.particles.get(i);
            float t = p.progress();
            if (t >= 1.0f) {
                this.particles.remove(i);
                continue;
            }
            float grow = 1.0f - (1.0f - t) * (1.0f - t) * (1.0f - t);
            float radius = p.maxRadius * grow * S;
            if (radius < 0.4f || (alpha = (fade = Menu.smoothstep(0.0f, 0.18f, t) * (1.0f - Menu.smoothstep(0.45f, 1.0f, t))) * globalAnim * 0.75f) < 0.004f) continue;
            float thickness = 1.1f + 2.6f * (1.0f - grow);
            float py = p.y - p.drift * grow * S;
            RenderUtil.Render2D.outline(p.x - radius, py - radius, radius * 2.0f, radius * 2.0f, thickness, ColorUtil.replAlpha(ColorUtil.client(), alpha), radius);
        }
    }

    private void drawScanLines(int screenWidth, int screenHeight, float globalAnim) {
        if (globalAnim <= 0.01f) {
            return;
        }
        this.scanLinesPipeline.draw(screenWidth, screenHeight, globalAnim * 0.18f, ColorUtil.getColor(255), 4.0f * S, 1.0f, 50.0f, 3000.0f, 100.0f);
    }

    private float descHeight(Font font, String desc) {
        return this.descHeights.computeIfAbsent(desc + "_" + S, d -> Float.valueOf(font.getWrappedHeight(desc, 125.0f * S, 6.0f * S))).floatValue();
    }

    private float smooth(String key, float target) {
        float[] v = this.smoothVals.computeIfAbsent(key, k -> new float[]{target});
        v[0] = v[0] + (target - v[0]) * 0.2f;
        return v[0];
    }

    private float visAnim(Module f, Setting<?> setting) {
        ru.decide.utils.animation.satoshi.Animation a = this.visAnims.computeIfAbsent(setting, k -> new EaseInOutQuad(300, 1.0));
        a.setDirection(setting.getVisible().get() != false ? Direction.FORWARDS : Direction.BACKWARDS);
        return a.getOutput();
    }

    private float chipsHeight(Iterable<String> values, float width) {
        Font font = Fonts.sf_regular;
        float px = 0.0f;
        float py = 0.0f;
        for (String val : values) {
            float tw = font.getWidth(val, 6.0f * S) + 8.0f * S;
            if (px + tw > width && px > 0.0f) {
                px = 0.0f;
                py += 12.0f * S;
            }
            px += tw + 3.0f * S;
        }
        return py + 10.0f * S;
    }

    private float chipsHeight(Setting<?> key, Iterable<String> values, float width) {
        float[] cached = this.chipsHeights.get(key);
        if (cached != null && cached[0] == width) {
            return cached[1];
        }
        float height = this.chipsHeight(values, width);
        this.chipsHeights.put(key, new float[]{width, height});
        return height;
    }

    private float modeChipsHeight(ModeSetting s, float width) {
        return this.chipsHeight(s, s.values, width);
    }

    private float multiChipsHeight(MultiBooleanSetting s, float width) {
        float[] cached = this.chipsHeights.get(s);
        if (cached != null && cached[0] == width) {
            return cached[1];
        }
        return this.chipsHeight(s, this.multiNames(s), width);
    }

    private String query() {
        return searchQuery.trim().toLowerCase();
    }

    private boolean searching() {
        return !this.query().isEmpty();
    }

    private boolean isNoSearchCategory() {
        return this.active == Category.CONFIGS || this.active == Category.MARKERS;
    }

    private boolean moduleVisible(Module f) {
        if (this.isNoSearchCategory()) {
            return false;
        }
        String q = this.query();
        if (q.isEmpty()) {
            return f.getCategory() == this.active;
        }
        return f.getName().toLowerCase().contains(q) || f.getBigName().toLowerCase().contains(q) || f.getDesc().toLowerCase().contains(q) || f.getCategory().getName().toLowerCase().contains(q);
    }

    private int searchResults() {
        if (this.active == Category.COSMETICS) {
            return this.cosmeticEntriesForDisplay().size();
        }
        int count = 0;
        for (Module f : Client.get().moduleManager().values()) {
            if (!this.moduleVisible(f)) continue;
            ++count;
        }
        return count;
    }

    private List<CosmeticManager.CosmeticEntry> cosmeticEntriesForDisplay() {
        CosmeticManager cosmetics = CosmeticManager.getInstance();
        String q = this.query();
        if (q.isEmpty()) {
            String group = this.activeGroup();
            if (group != null) {
                return cosmetics.getEntriesByGroup(group);
            }
            return cosmetics.getFilteredEntries(this.activeCosmetic.getType());
        }
        ArrayList<CosmeticManager.CosmeticEntry> out = new ArrayList<CosmeticManager.CosmeticEntry>();
        for (CosmeticManager.CosmeticEntry e : cosmetics.getEntries()) {
            String name;
            String string = name = e.name == null ? "" : e.name.toLowerCase();
            if (!name.contains(q) && !Integer.toString(e.index).contains(q)) continue;
            out.add(e);
        }
        return out;
    }

    private void searchChanged() {
        this.searchTypeTime = System.currentTimeMillis();
        this.scrollTarget = 0.0f;
        this.cosmeticsScrollTarget = 0.0f;
    }

    private void clearSearch() {
        if (searchQuery.isEmpty()) {
            return;
        }
        searchQuery = "";
        this.searchChanged();
    }

    private List<String> multiNames(MultiBooleanSetting s) {
        ArrayList<String> names = new ArrayList<String>();
        for (BooleanSetting b : s.getValues()) {
            names.add(b.getName());
        }
        return names;
    }

    protected void method_25426() {
        this.exit = false;
        this.use3DAnim = false;
        GuiCloseAnimationShatter.cancel();
        searchActive = false;
        this.searchTypeTime = System.currentTimeMillis();
        this.bindingModule = null;
        GuiSounds.open();
        GuiMusicPlayer.start(0.15f);
        this.glomalAnim.run(1.0, 0.25, Easings.SINE_OUT);
        selectedTheme = Client.get().guiManager().getCurrentTheme();
        preSelectedTheme = Client.get().guiManager().getCurrentTheme();
        themes = Theme.values();
    }

    public void method_25420(class_332 context, int mouseX, int mouseY, float delta) {
    }

    private void loadGif() {
        block9: {
            if (this.gif != null) {
                return;
            }
            try {
                Optional res = mc.method_1478().method_14486(class_2960.method_60655((String)"decide", (String)"textures/gui.gif"));
                if (!res.isPresent()) break block9;
                try (InputStream in = ((class_3298)res.get()).method_14482();){
                    this.gif = new GifTexture(in);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
        CosmeticGuiPreview.clear();
        this.closeCheck();
        this.glomalAnim.update();
        mouseX = (int)((float)mouseX / this.scaleFix);
        mouseY = (int)((float)mouseY / this.scaleFix);
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
    }

    public void renderOverlay(class_332 context, class_9779 tickCounter) {
        OverlayEditor editor;
        float targetScale = 2.0f;
        float currentScale = mc.method_22683().method_4495();
        this.scaleFix = targetScale / currentScale;
        int screenWidth = (int)((float)mc.method_22683().method_4486() / this.scaleFix);
        int screenHeight = (int)((float)mc.method_22683().method_4502() / this.scaleFix);
        float globalAnim = this.glomalAnim.get();
        if (context != null) {
            context.method_51448().pushMatrix();
        }
        if ((editor = OverlayEditors.active()) != null) {
            float editorMouseX = (float)(Menu.mc.field_1729.method_68879(mc.method_22683()) / (double)this.scaleFix);
            float editorMouseY = (float)(Menu.mc.field_1729.method_68883(mc.method_22683()) / (double)this.scaleFix);
            editor.render(screenWidth, screenHeight, editorMouseX, editorMouseY, globalAnim);
            Render2D.endOverlay();
            if (context != null) {
                context.method_51448().popMatrix();
            }
            return;
        }
        if (this.effect("\u0421\u0435\u0440\u044b\u0439 \u0444\u043e\u043d")) {
            this.grayscalePipeline.draw(globalAnim);
        }
        ScreenBlur.capture(1);
        if (this.effect("\u0420\u0430\u0437\u043c\u044b\u0432\u0430\u0442\u044c \u0444\u043e\u043d")) {
            RenderUtil.Blur.blur(0.0f, 0.0f, screenWidth, screenHeight, globalAnim, 0.0f, ColorUtil.getColor(0, 0));
        }
        if (this.effect("\u0417\u0430\u0442\u0435\u043c\u043d\u044f\u0442\u044c \u0444\u043e\u043d")) {
            RenderUtil.Images.texture(class_2960.method_60655((String)"decide", (String)"textures/frame/background.png"), 0.0f, 0.0f, screenWidth, screenHeight, ColorUtil.multAlpha(ColorUtil.client(), globalAnim));
        }
        float shaderMouseX = (float)(Menu.mc.field_1729.method_68879(mc.method_22683()) / (double)this.scaleFix);
        float shaderMouseY = (float)(Menu.mc.field_1729.method_68883(mc.method_22683()) / (double)this.scaleFix);
        if (this.effect("\u0428\u0435\u0439\u0434\u0435\u0440")) {
            int rayBase = ColorUtil.client();
            this.raysPipeline.draw(globalAnim, ColorUtil.replAlpha(ColorUtil.multDark(rayBase, 0.5f), 0.9f * globalAnim), ColorUtil.replAlpha(rayBase, 0.9f * globalAnim), 0.1f, 0.08f, 0.26f);
        }
        if (this.effect("\u0422\u043e\u0447\u043a\u0438")) {
            this.halftonePipeline.draw(screenWidth, screenHeight, shaderMouseX, shaderMouseY, globalAnim * 0.1f, ColorUtil.getColor(255), 6.0f, 0.7f, 3.0f, 100.0f);
        }
        if (this.effect("\u0421\u043a\u0430\u043d \u043b\u0438\u043d\u0438\u0438")) {
            this.drawScanLines(screenWidth, screenHeight, globalAnim);
        }
        if (this.effect("\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435")) {
            int glowColor = ColorUtil.replAlpha(ColorUtil.client(), globalAnim * 0.5f);
            RenderUtil.Images.texture(class_2960.method_60655((String)"decide", (String)"textures/effects/top_glow.png"), 0.0f, -30.0f + 30.0f * globalAnim, screenWidth, screenHeight, glowColor);
            if (!this.exit && globalAnim > 0.01f && globalAnim < 0.99f) {
                float pulse = (globalAnim > 0.5f ? 1.0f - globalAnim : globalAnim) * 2.0f;
                float scale = (float)screenWidth / 1.75f * globalAnim;
                float cx = (float)screenWidth / 2.0f;
                float cy = (float)screenHeight / 2.0f;
                RenderUtil.Render2D.rect(cx - scale, cy - scale, scale * 2.0f, scale * 2.0f, ColorUtil.getColor(255, 0.2f * pulse), scale);
                RenderUtil.Render2D.glow(cx - scale, cy - scale, scale * 2.0f, scale * 2.0f, ColorUtil.replAlpha(ColorUtil.client(), 0.15f * pulse), scale, 12.0f, 1.0f);
            }
        }
        if (!this.particles.isEmpty()) {
            this.particles.clear();
        }
        ScreenBlur.capture();
        S = Client.get().moduleManager().get(ClickGui.class).size.getValue().floatValue();
        float w = 420.0f * S;
        float h = 280.0f * S;
        float x = (float)screenWidth / 2.0f - w / 2.0f;
        float y = (float)screenHeight / 2.0f - h / 2.0f + (this.exit ? 60.0f * S - 60.0f * S * globalAnim : -60.0f * S + 60.0f * S * globalAnim);
        Font draw = Fonts.sf_regular;
        Font regular = Fonts.sf_regular;
        Font icons = Fonts.icon;
        Font guiicon = Fonts.gui;
        ScreenBlur.capture();
        float ht = 20.0f * S;
        float dotsW = (float)((themes.length - 1) * 14) * S + 8.0f * S;
        float barW = dotsW + 12.0f * S;
        float xtd = x + (w - dotsW) / 2.0f;
        float barX = xtd - 6.0f * S;
        float yt = y + h + 5.0f * S;
        RenderUtil.Render2D.glow(barX, yt, barW, ht - 0.5f * S, ColorUtil.getColor(0, 0.15f * globalAnim), 7.5f * S, 15.0f, 1.0f);
        RenderUtil.Blur.blur(barX, yt, barW, ht, globalAnim, 7.5f * S, ColorUtil.multAlpha(ColorUtil.multDark(ColorUtil.background(), 0.6f), globalAnim));
        RenderUtil.Images.texture(class_2960.method_60655((String)"decide", (String)"textures/frame/rectthemegui.png"), barX, yt, barW, ht, ColorUtil.multAlpha(ColorUtil.client(), globalAnim));
        float ytd = yt + 6.0f * S;
        for (Theme theme : themes) {
            theme.animation.setDirection(theme == selectedTheme ? Direction.FORWARDS : Direction.BACKWARDS);
            float anPC = theme.animation.getOutput();
            RenderUtil.Render2D.rect(xtd + 0.75f * S * anPC, ytd + 0.75f * S * anPC, 8.0f * S - 1.5f * S * anPC, 8.0f * S - 1.5f * S * anPC, ColorUtil.replAlpha(theme.getClient(), (0.5f + 0.5f * anPC) * globalAnim), 8.0f * S);
            RenderUtil.Render2D.outline(xtd - 0.5f * S * anPC, ytd - 0.5f * S * anPC, 8.0f * S + 1.0f * S * anPC, 8.0f * S + 1.0f * S * anPC, 0.25f * S, ColorUtil.replAlpha(theme.getClient(), 1.0f * anPC * globalAnim), 8.0f * S);
            RenderUtil.Render2D.glow(xtd + 0.75f * S * anPC, ytd + 0.75f * S * anPC, 8.0f * S - 1.5f * S * anPC, 8.0f * S - 1.5f * S * anPC, ColorUtil.replAlpha(theme.getClient(), 0.1f * anPC * globalAnim), 8.0f * S, 7.0f, 1.0f);
            xtd += 14.0f * S;
        }
        RenderUtil.Render2D.glow(x, y, w, h - 0.5f * S, ColorUtil.getColor(0, 0.15f * globalAnim), 8.0f * S, 15.0f, 1.0f);
        RenderUtil.Blur.blur(x, y, w, h, globalAnim, 8.0f * S, ColorUtil.multAlpha(ColorUtil.multDark(ColorUtil.background(), 0.6f), globalAnim));
        RenderUtil.Images.texture(class_2960.method_60655((String)"decide", (String)"textures/frame/rectgui.png"), x, y, w, h, ColorUtil.multAlpha(ColorUtil.client(), globalAnim));
        RenderUtil.Render2D.glow(x + 6.0f * S, y + 6.0f * S, 30.0f * S, h - 12.0f * S, ColorUtil.getColor(0, 0.04f * globalAnim), 6.0f * S, 8.0f, 1.0f);
        RenderUtil.Render2D.rect(x + 6.0f * S, y + 6.0f * S, 30.0f * S, h - 12.0f * S, ColorUtil.getColor(0, 0.15f * globalAnim), 6.0f * S);
        float xPanelMini = x + 6.0f * S;
        float yPanelMini = y + 6.0f * S;
        boolean isLogoHov = MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, xPanelMini + 30.0f * S / 2.0f - 12.0f * S / 2.0f, yPanelMini + 7.0f * S, 12.0f * S, 12.0f * S);
        animUserInfo.setDirection(isLogoHov ? Direction.FORWARDS : Direction.BACKWARDS);
        float animL = animUserInfo.getOutput();
        float xAnimADd2 = 16.0f * S - 16.0f * S * animL;
        String sgff = "DecideVisuals" + ColorFormatting.getColor(ColorUtil.replAlpha(ColorUtil.client(), globalAnim * animL)) + " 5.0";
        RenderUtil.Render2D.glow(xPanelMini - draw.getWidth(sgff, 8.0f * S) - 4.0f * S + xAnimADd2, yPanelMini + 7.0f * S, 7.0f * S + draw.getWidth(sgff, 8.0f * S), 14.0f * S, ColorUtil.multAlpha(ColorUtil.getColor(0), globalAnim * animL * 0.1f), 4.0f * S, 6.0f, 1.0f);
        RenderUtil.Blur.blur(xPanelMini - draw.getWidth(sgff, 8.0f * S) - 4.0f * S + xAnimADd2, yPanelMini + 7.0f * S, 7.0f * S + draw.getWidth(sgff, 8.0f * S), 14.0f * S, globalAnim * animL, 4.0f * S, ColorUtil.multAlpha(ColorUtil.background(), globalAnim * animL * 0.2f));
        draw.draw(sgff, xPanelMini - draw.getWidth(sgff, 8.0f * S) + xAnimADd2, yPanelMini + 9.0f * S, 8.0f * S, ColorUtil.getColor(200, globalAnim * animL));
        RenderUtil.Render2D.glow(xPanelMini + 30.0f * S / 2.0f - 16.0f * S / 2.0f + 5.5f * S, yPanelMini + 10.0f * S, 0.1f * S, 0.1f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * animL * 0.15f), 10.0f * S, 12.0f, 1.0f);
        RenderUtil.Images.texture(class_2960.method_60655((String)"decide", (String)"textures/frame/logo.png"), xPanelMini + 30.0f * S / 2.0f - 16.0f * S / 2.0f, yPanelMini + 2.0f * S, 16.0f * S, 16.0f * S, ColorUtil.replAlpha(ColorUtil.getClientColor(1), globalAnim));
        float csgddd = 0.0f;
        for (Category category : Category.values()) {
            csgddd += 20.0f * S;
        }
        float cy = yPanelMini + 28.0f * S;
        for (Category category : Category.values()) {
            category.alphaS.setDirection(this.active == category ? Direction.FORWARDS : Direction.BACKWARDS);
            float anim = category.alphaS.getOutput();
            ru.decide.utils.animation.satoshi.Animation animUF = category.alphaS2;
            boolean isHv = MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, xPanelMini + 30.0f * S / 2.0f - 16.0f * S / 2.0f, cy, 16.0f * S, 16.0f * S);
            animUF.setDirection(isHv ? Direction.FORWARDS : Direction.BACKWARDS);
            float animL2 = animUF.getOutput();
            String name = category.getIcon();
            RenderUtil.Render2D.outline(xPanelMini + 30.0f * S / 2.0f - 16.0f * S / 2.0f - 1.0f * S * anim + 1.0f * S, cy - 1.0f * S * anim + 1.0f * S, 16.0f * S + 2.0f * S * anim - 2.0f * S, 16.0f * S + 2.0f * S * anim - 2.0f * S, 0.5f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * anim), 5.0f * S);
            RenderUtil.Render2D.glow(xPanelMini + 30.0f * S / 2.0f - 12.0f * S / 2.0f + 5.8f * S, cy + 7.8f * S, 0.1f * S, 0.1f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * animL2 * 0.15f * anim), 8.0f * S, 9.0f, 1.0f);
            if (category == Category.MARKERS) {
                RenderUtil.Images.texture(class_2960.method_60655((String)"decide", (String)"textures/hud/marker.png"), xPanelMini + 30.0f * S / 2.0f - 8.0f * S / 2.0f, cy + 4.0f * S, 8.0f * S, 8.0f * S, ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(255, 0.15f * globalAnim + 0.5f * animL2), ColorUtil.client(), anim), globalAnim));
            } else {
                Fonts.decide_2.drawCentered(name, xPanelMini + 30.0f * S / 2.0f, cy + 4.5f * S, 8.0f * S, ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(255, 0.15f * globalAnim + 0.5f * animL2), ColorUtil.client(), anim), globalAnim));
            }
            String sgff2 = category.getName();
            float xAnimADd = 16.0f * S - 16.0f * S * animL2;
            RenderUtil.Render2D.glow(xPanelMini - draw.getWidth(sgff2, 8.0f * S) - 4.0f * S + xAnimADd, cy + 7.0f * S - 6.0f * S, 7.0f * S + draw.getWidth(sgff2, 8.0f * S), 14.0f * S, ColorUtil.multAlpha(ColorUtil.getColor(0), globalAnim * animL2 * 0.1f), 4.0f * S, 6.0f, 1.0f);
            RenderUtil.Blur.blur(xPanelMini - draw.getWidth(sgff2, 8.0f * S) - 4.0f * S + xAnimADd, cy + 7.0f * S - 6.0f * S, 7.0f * S + draw.getWidth(sgff2, 8.0f * S), 14.0f * S, globalAnim * animL2, 4.0f * S, ColorUtil.multAlpha(ColorUtil.background(), globalAnim * animL2 * 0.2f));
            draw.draw(sgff2, xPanelMini - draw.getWidth(sgff2, 8.0f * S) + xAnimADd, cy + 9.0f * S - 6.0f * S, 8.0f * S, ColorUtil.getColor(200, globalAnim * animL2));
            cy += 20.0f * S;
        }
        float xps = x + 6.0f * S + 36.0f * S;
        float yps = y + 6.0f * S;
        boolean searchHover = MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, xps, yps, 140.0f * S, 20.0f * S);
        animation3.setDirection(searchHover || searchActive ? Direction.FORWARDS : Direction.BACKWARDS);
        this.animSearchFocus.setDirection(searchActive ? Direction.FORWARDS : Direction.BACKWARDS);
        this.animSearchText.setDirection(!searchQuery.isEmpty() ? Direction.FORWARDS : Direction.BACKWARDS);
        float hvs = animation3.getOutput();
        float focus = this.animSearchFocus.getOutput();
        float typed = this.animSearchText.getOutput();
        RenderUtil.Render2D.glow(xps, yps, 140.0f * S, 20.0f * S, ColorUtil.getColor(0, 0.04f * globalAnim), 6.0f * S, 8.0f, 1.0f);
        RenderUtil.Render2D.rect(xps, yps, 140.0f * S, 20.0f * S, ColorUtil.overCol(ColorUtil.getColor(0, 0.15f * globalAnim), ColorUtil.getColor(25, 0.3f * globalAnim), Math.max(hvs, focus)), 6.0f * S);
        RenderUtil.Render2D.outline(xps, yps, 140.0f * S, 20.0f * S, 0.5f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * focus), 6.0f * S);
        guiicon.draw("g", xps + 7.0f * S, yps + 7.0f * S, 6.0f * S, ColorUtil.overCol(ColorUtil.getColor(255, globalAnim * (0.3f + 0.5f * hvs)), ColorUtil.replAlpha(ColorUtil.client(), globalAnim * (0.3f + 0.7f * hvs)), hvs));
        float xSearchText = xps + 6.5f * S + 11.0f * S;
        float wSearchText = 140.0f * S - (xSearchText - xps) - 8.0f * S;
        float hidePlaceholder = Math.max(typed, focus);
        if (hidePlaceholder < 0.99f) {
            String placeholderText = this.isNoSearchCategory() ? "\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u0440\u0430\u0437\u0434\u0435\u043b\u044b \u0432\u044b\u0448\u0435" : "Search";
            regular.draw(placeholderText, xSearchText, yps + 5.5f * S + 4.0f * S * hidePlaceholder, 7.0f * S, ColorUtil.getColor(255, globalAnim * (1.0f - hidePlaceholder) * (0.3f + 0.5f * hvs)));
        }
        if (typed > 0.01f) {
            regular.drawFadingTextReverse(searchQuery, xSearchText, yps + 5.5f * S - 4.0f * S * (1.0f - typed), wSearchText, ColorUtil.getColor(255, globalAnim * typed * (0.5f + 0.5f * Math.max(hvs, focus))), 7.0f * S);
        }
        boolean justTyped = System.currentTimeMillis() - this.searchTypeTime < 500L;
        float caretTarget = focus * (justTyped || System.currentTimeMillis() / 500L % 2L == 0L ? 1.0f : 0.0f);
        float caret = this.smooth("search:caret", caretTarget);
        float caretX = this.smooth("search:caretx", xSearchText + Math.min(regular.getWidth(searchQuery, 7.0f * S), wSearchText) + 1.5f * S);
        RenderUtil.Render2D.rect(caretX, yps + 6.5f * S, 0.6f * S, 6.5f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * caret), 0.3f * S);
        float catPanX = x + 6.0f * S + 36.0f * S + 6.0f * S + 140.0f * S;
        float catPanW = w + (6.0f * S - 36.0f * S - 6.0f * S - 140.0f * S - 12.0f * S - 6.0f * S);
        RenderUtil.Render2D.glow(catPanX, y + 6.0f * S, catPanW, 20.0f * S, ColorUtil.getColor(0, 0.04f * globalAnim), 6.0f * S, 8.0f, 1.0f);
        RenderUtil.Render2D.rect(catPanX, y + 6.0f * S, catPanW, 20.0f * S, ColorUtil.getColor(0, 0.15f * globalAnim), 6.0f * S);
        String catName = this.active.getIcon();
        Fonts.decide_2.draw(catName, catPanX + 8.0f * S, y + 12.7f * S + 5.0f * S - 5.0f * S * animCategoryReset.getOutput(), 7.0f * S, ColorUtil.multAlpha(ColorUtil.client(), globalAnim * animCategoryReset.getOutput()));
        draw.draw(this.active.getName(), catPanX + 20.0f * S, y + 11.8f * S + 5.0f * S - 5.0f * S * animCategoryReset.getOutput(), 7.0f * S, ColorUtil.getColor(200, globalAnim * animCategoryReset.getOutput()));
        int found = this.searchResults();
        String foundText = found + (found == 1 ? " result" : " results");
        float xHeader = catPanX;
        float wHeader = catPanW;
        draw.draw(foundText, xHeader + wHeader - 8.0f * S - draw.getWidth(foundText, 6.5f * S), y + 12.2f * S + 5.0f * S - 5.0f * S * typed, 6.5f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * typed));
        if (!this.isCustomPanelCategory()) {
            RenderUtil.Render2D.glow(x + 6.0f * S + 36.0f * S, y + 6.0f * S + 26.0f * S, 140.0f * S, h - 12.0f * S - 26.0f * S, ColorUtil.getColor(0, 0.04f * globalAnim), 7.0f * S, 8.0f, 1.0f);
            RenderUtil.Render2D.rect(x + 6.0f * S + 36.0f * S, y + 6.0f * S + 26.0f * S, 140.0f * S, h - 12.0f * S - 26.0f * S, ColorUtil.getColor(0, 0.15f * globalAnim), 7.0f * S);
            Scissor.enable(x + 6.0f * S + 36.0f * S, y + 6.0f * S + 26.0f * S, 140.0f * S, h - 12.0f * S - 26.0f * S, 2.0f);
        }
        this.scrollAnim += (this.scrollTarget - this.scrollAnim) * 0.2f;
        float xModule = x + 6.0f * S + 36.0f * S + 5.0f * S;
        float yModule = y + 6.0f * S + 30.0f * S - this.scrollAnim;
        float crs = animCategoryReset.getOutput();
        float listTop = y + 6.0f * S + 26.0f * S;
        float listBottom = listTop + (h - 12.0f * S - 26.0f * S);
        boolean drawModuleList = !this.isCustomPanelCategory();
        for (Module f : Client.get().moduleManager().values()) {
            f.getAnimation14().setDirection(this.moduleVisible(f) ? Direction.FORWARDS : Direction.BACKWARDS);
            float canim1 = f.getAnimation14().getOutput();
            if (!(canim1 > 0.0f) || !drawModuleList) continue;
            float descH = this.descHeight(draw, f.getDesc());
            float moduleH = Math.max(20.0f * S, 16.0f * S + descH + 4.0f * S);
            f.getAnimation16().setDirection(f.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
            float moduleEnable = f.getAnimation16().getOutput();
            boolean onScreen = yModule + moduleH >= listTop && yModule <= listBottom;
            boolean isHover = onScreen && MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, xModule, yModule, 130.0f * S, moduleH);
            f.getAnimation12().setDirection(isHover ? Direction.FORWARDS : Direction.BACKWARDS);
            float hanim = f.getAnimation12().getOutput();
            f.animation1.setDirection(f == this.select ? Direction.FORWARDS : Direction.BACKWARDS);
            float selectAnim = f.animation1.getOutput();
            if (!onScreen) {
                yModule += (moduleH + 5.0f * S) * canim1;
                continue;
            }
            RenderUtil.Render2D.rect(xModule, yModule, 130.0f * S, moduleH, ColorUtil.overCol(ColorUtil.getColor(0, 0.15f * globalAnim * canim1), ColorUtil.getColor(25, 0.3f * globalAnim * canim1), hanim), 5.0f * S);
            RenderUtil.Render2D.outline(xModule - 1.0f * S * moduleEnable + 1.0f * S, yModule - 1.0f * S * moduleEnable + 1.0f * S, 130.0f * S + 2.0f * S * moduleEnable - 2.0f * S, moduleH + 2.0f * S * moduleEnable - 2.0f * S, 0.5f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * canim1 * moduleEnable * (1.0f - 0.3f * hanim)), 5.0f * S);
            RenderUtil.Render2D.rect(xModule + 130.0f * S - 14.0f * S - 5.0f * S, yModule + 5.0f * S, 14.0f * S, 8.0f * S, ColorUtil.overCol(ColorUtil.getColor(0, 0.2f * globalAnim * canim1), ColorUtil.replAlpha(ColorUtil.client(), globalAnim * canim1 * (1.0f - 0.3f * hanim)), moduleEnable), 4.0f * S);
            RenderUtil.Render2D.rect(xModule + 130.0f * S - 14.0f * S - 5.0f * S + 1.5f * S + 5.5f * S * moduleEnable, yModule + 5.0f * S + 1.25f * S, 5.5f * S, 5.5f * S, ColorUtil.getColor(255, globalAnim * (0.3f + 0.7f * moduleEnable) * canim1), 4.0f * S);
            boolean bindingNow = this.bindingModule == f;
            ru.decide.utils.animation.satoshi.Animation bindAct = this.chipAnim(f.getName() + ":modbind");
            bindAct.setDirection(bindingNow ? Direction.FORWARDS : Direction.BACKWARDS);
            float bindActive = bindAct.getOutput();
            String keyName = bindingNow ? "..." : (f.getKey() == -1 ? "n/a" : Keyboard.keyName(f.getKey()).replace("NONE", "n/a"));
            float keyW = this.smooth(f.getName() + ":modbindw", draw.getWidth(keyName, 6.0f * S) + 9.0f * S);
            float keyX = xModule + 130.0f * S - 14.0f * S - 5.0f * S - 5.0f * S - keyW;
            float keyY = yModule + 4.0f * S;
            boolean keyHover = MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, keyX, keyY, keyW, 10.0f * S);
            ru.decide.utils.animation.satoshi.Animation bindHov = this.chipAnim(f.getName() + ":modbindhov");
            bindHov.setDirection(keyHover ? Direction.FORWARDS : Direction.BACKWARDS);
            float bindHover = bindHov.getOutput();
            float pulse = bindingNow ? 0.55f + 0.45f * (float)Math.sin((double)((float)(System.currentTimeMillis() % 1000L) / 1000.0f) * Math.PI * 2.0) : 0.0f;
            float bindAccent = Math.max(bindHover * 0.5f, bindActive);
            RenderUtil.Render2D.rect(keyX, keyY, keyW, 10.0f * S, ColorUtil.overCol(ColorUtil.getColor(0, (0.12f + 0.13f * bindHover) * globalAnim * canim1), ColorUtil.replAlpha(ColorUtil.client(), (0.15f + 0.2f * pulse) * globalAnim * canim1), bindActive), 3.0f * S);
            RenderUtil.Render2D.outline(keyX, keyY, keyW, 10.0f * S, 0.5f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * canim1 * bindAccent * (bindingNow ? 0.4f + 0.6f * pulse : 1.0f)), 3.0f * S);
            RenderUtil.Render2D.glow(keyX, keyY, keyW, 10.0f * S, ColorUtil.replAlpha(ColorUtil.client(), 0.12f * globalAnim * canim1 * bindActive * pulse), 3.0f * S, 6.0f, 1.0f);
            regular.drawCentered(keyName, keyX + keyW / 2.0f, keyY + 1.5f * S, 6.0f * S, ColorUtil.replAlpha(ColorUtil.overCol(ColorUtil.getColor(255), ColorUtil.client(), Math.max(bindActive, moduleEnable)), globalAnim * canim1 * (0.35f + 0.35f * moduleEnable + 0.3f * Math.max(bindHover, bindActive))));
            draw.draw(f.getBigName(), xModule + 5.0f * S, yModule + 5.0f * S, 7.0f * S, ColorUtil.getColor(255, globalAnim * canim1 * (0.2f + 0.6f * moduleEnable + 0.2f * hanim)));
            draw.drawWrappedText(f.getDesc(), xModule + 5.0f * S, yModule + 4.0f * S + 12.0f * S, 125.0f * S, ColorUtil.getColor(255, globalAnim * canim1 * (0.1f + 0.5f * moduleEnable + 0.2f * hanim)), 6.0f * S);
            RenderUtil.Render2D.rect(xModule, yModule, 130.0f * S, moduleH, ColorUtil.replAlpha(ColorUtil.client(), 0.3f * globalAnim * selectAnim * (0.2f + 0.5f * moduleEnable) * canim1), 5.0f * S);
            yModule += (moduleH + 5.0f * S) * canim1;
        }
        this.animSearchEmpty.setDirection(!drawModuleList ? Direction.BACKWARDS : (this.searching() && found == 0 ? Direction.FORWARDS : Direction.BACKWARDS));
        float emptyAnim = this.animSearchEmpty.getOutput();
        if (emptyAnim > 0.01f) {
            float xEmpty = x + 6.0f * S + 36.0f * S + 70.0f * S;
            float yEmpty = y + 6.0f * S + 26.0f * S + (h - 12.0f * S - 26.0f * S) / 2.0f - 8.0f * S + 6.0f * S - 6.0f * S * emptyAnim;
            guiicon.drawCentered("g", xEmpty, yEmpty, 8.0f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * emptyAnim * 0.4f));
            draw.drawCentered("\u041d\u0438\u0447\u0435\u0433\u043e \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u043e", xEmpty, yEmpty + 14.0f * S, 7.0f * S, ColorUtil.getColor(255, globalAnim * emptyAnim * 0.5f));
        }
        float contentHeight = yModule + this.scrollAnim - (y + 6.0f * S + 30.0f * S);
        float viewHeight = h - 12.0f * S - 26.0f * S - 4.0f * S;
        this.maxScroll = Math.max(0.0f, contentHeight - viewHeight);
        if (this.scrollTarget > this.maxScroll) {
            this.scrollTarget = this.maxScroll;
        }
        Scissor.reset();
        if (this.active == Category.CONFIGS || this.active == Category.MARKERS) {
            this.renderConfigOrMarkerUi(x, y, w, h, globalAnim, draw, regular, guiicon);
        } else if (this.active == Category.COSMETICS) {
            this.renderCosmeticsUi(context, x, y, w, h, globalAnim, draw, regular);
        }
        if (!this.isCustomPanelCategory()) {
            float xSetBase = x + 6.0f * S + 36.0f * S + 140.0f * S + 6.0f * S;
            float wSetBase = w - (6.0f * S + 36.0f * S + 6.0f * S + 140.0f * S + 6.0f * S);
            float ySetBase = y + 6.0f * S + 26.0f * S;
            float hSetBase = h - 12.0f * S - 26.0f * S;
            RenderUtil.Render2D.glow(xSetBase, ySetBase, wSetBase, hSetBase, ColorUtil.getColor(0, 0.04f * globalAnim), 7.0f * S, 8.0f, 1.0f);
            RenderUtil.Render2D.rect(xSetBase, ySetBase, wSetBase, hSetBase, ColorUtil.getColor(0, 0.15f * globalAnim), 7.0f * S);
            Scissor.enable(xSetBase, ySetBase, wSetBase, hSetBase, 2.0f);
            float xSetting = xSetBase;
            float ySetting = ySetBase;
            float wSetting = wSetBase;
            float hSetting = hSetBase;
            animation2.setDirection(this.select != null ? Direction.FORWARDS : Direction.BACKWARDS);
            this.settingScrollAnim += (this.settingScrollTarget - this.settingScrollAnim) * 0.2f;
            float xST = xSetting + 10.0f * S;
            float yST = ySetting + 10.0f * S - this.settingScrollAnim;
            float wST = wSetting - 20.0f * S;
            float setTop = ySetting;
            float setBottom = ySetting + hSetting;
            for (Module f : Client.get().moduleManager().values()) {
                f.animation3.setDirection(f == this.select ? Direction.FORWARDS : Direction.BACKWARDS);
                float fa = f.animation3.getOutput();
                if (!(fa > 0.0f)) continue;
                for (Setting<?> setting : f.getSettings()) {
                    float bw;
                    boolean hovS;
                    float hover;
                    boolean hovS2;
                    boolean onScreen;
                    float sa;
                    Setting s;
                    float vis;
                    if (setting instanceof SliderSetting && (vis = this.visAnim(f, s = (SliderSetting)setting)) > 0.01f) {
                        sa = fa * vis;
                        float percent = class_3532.method_15363((float)((((SliderSetting)s).getValue().floatValue() - ((SliderSetting)s).min) / (((SliderSetting)s).max - ((SliderSetting)s).min)), (float)0.0f, (float)1.0f);
                        s.getAnimation().update();
                        s.getAnimation().run((double)percent, (double)0.06f, Easings.LINEAR);
                        onScreen = yST + 25.0f * S >= setTop && yST <= setBottom;
                        hovS2 = onScreen && MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, xST, yST, wST, 24.0f * S);
                        ((SliderSetting)s).animation.setDirection(hovS2 ? Direction.FORWARDS : Direction.BACKWARDS);
                        hover = ((SliderSetting)s).animation.getOutput();
                        float track = wST - 12.0f * S;
                        if (onScreen) {
                            RenderUtil.Render2D.glow(xST, yST, wST, 25.0f * S, ColorUtil.getColor(0, 0.06f * globalAnim * sa), 5.0f * S, 7.0f, 1.0f);
                            RenderUtil.Render2D.rect(xST, yST, wST, 25.0f * S, ColorUtil.getColor(40, 0.15f * globalAnim * sa), 5.0f * S);
                            RenderUtil.Render2D.outline(xST, yST, wST, 25.0f * S, 0.5f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * sa * hover), 5.0f * S);
                            draw.draw(s.getName(), xST + 6.0f * S, yST + 5.0f * S, 6.5f * S, ColorUtil.getColor(255, globalAnim * sa * (0.5f + 0.5f * hover)));
                            String val = String.valueOf((double)Math.round((double)((SliderSetting)s).getValue().floatValue() * 100.0) / 100.0);
                            draw.draw(val, xST + wST - 6.0f * S - draw.getWidth(val, 6.0f * S), yST + 5.25f * S, 6.0f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * sa * (0.6f + 0.4f * hover)));
                            RenderUtil.Render2D.rect(xST + 6.0f * S, yST + 16.5f * S, track, 3.0f * S, ColorUtil.getColor(0, globalAnim * sa * 0.15f), 1.5f * S);
                            RenderUtil.Render2D.rect(xST + 6.0f * S, yST + 16.5f * S, track * s.getAnimation().get(), 3.0f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * sa * (0.5f + 0.5f * hover)), 1.5f * S);
                            RenderUtil.Render2D.rect(xST + 6.0f * S + track * s.getAnimation().get() - 3.0f * S, yST + 15.0f * S, 6.0f * S, 6.0f * S, ColorUtil.getColor((int)(200.0f + 55.0f * hover), globalAnim * sa), 6.0f * S);
                            RenderUtil.Render2D.rect(xST + 6.0f * S + track * s.getAnimation().get() - 2.0f * S, yST + 16.0f * S, 4.0f * S, 4.0f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * sa * (0.7f + 0.3f * hover)), 6.0f * S);
                        }
                        if (this.draggingSlider == s) {
                            float perc = MathUtil.clamp(((float)this.lastMouseX - (xST + 6.0f * S)) / track, 0.0f, 1.0f);
                            float newVal = MathUtil.clamp(MathUtil.round(((SliderSetting)s).min + (((SliderSetting)s).max - ((SliderSetting)s).min) * perc, ((SliderSetting)s).increment), ((SliderSetting)s).min, ((SliderSetting)s).max);
                            if (((SliderSetting)s).getValue().floatValue() != newVal) {
                                ((SliderSetting)s).set(Float.valueOf(newVal));
                                GuiSounds.sliderTick(perc);
                            }
                        }
                        yST += 30.0f * S * fa * vis;
                    }
                    if (setting instanceof ModeSetting && (vis = this.visAnim(f, s = (ModeSetting)setting)) > 0.01f) {
                        sa = fa * vis;
                        boolean expanded = this.expandedModes.contains(s.getName());
                        ru.decide.utils.animation.satoshi.Animation ea = this.expandAnim(f.getName() + ":" + s.getName());
                        ea.setDirection(expanded ? Direction.FORWARDS : Direction.BACKWARDS);
                        float expandT = ea.getOutput();
                        float chipsH = this.modeChipsHeight((ModeSetting)s, wST - 12.0f * S) + 4.0f * S;
                        float hMode = 15.0f * S + chipsH * expandT;
                        boolean onScreen2 = yST + hMode >= setTop && yST <= setBottom;
                        boolean hovS3 = onScreen2 && MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, xST, yST, wST, hMode);
                        ((ModeSetting)s).animation.setDirection(hovS3 ? Direction.FORWARDS : Direction.BACKWARDS);
                        float hover2 = ((ModeSetting)s).animation.getOutput();
                        if (onScreen2) {
                            RenderUtil.Render2D.glow(xST, yST, wST, hMode, ColorUtil.getColor(0, 0.06f * globalAnim * sa), 5.0f * S, 7.0f, 1.0f);
                            RenderUtil.Render2D.rect(xST, yST, wST, hMode, ColorUtil.getColor(40, 0.15f * globalAnim * sa), 5.0f * S);
                            RenderUtil.Render2D.outline(xST, yST, wST, hMode, 0.5f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * sa * hover2), 5.0f * S);
                            String current = ((ModeSetting)s).getValue();
                            String arrow = expanded ? " \u25b2" : " \u25bc";
                            draw.drawFadingText(s.getName(), xST + 6.0f * S, yST + 4.5f * S, wST - draw.getWidth(current + arrow, 6.0f * S) - 16.0f * S, ColorUtil.getColor(255, globalAnim * sa * (0.5f + 0.5f * hover2)), 6.5f * S);
                            draw.draw(current + arrow, xST + wST - 6.0f * S - draw.getWidth(current + arrow, 6.0f * S), yST + 4.75f * S, 6.0f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * sa * (0.6f + 0.4f * hover2)));
                            if (expandT > 0.01f) {
                                float chipMaxW = wST - 12.0f * S;
                                float px = 0.0f;
                                float py = 0.0f;
                                for (String val : ((ModeSetting)s).values) {
                                    float tw = draw.getWidth(val, 6.0f * S) + 8.0f * S;
                                    if (px + tw > chipMaxW && px > 0.0f) {
                                        px = 0.0f;
                                        py += 12.0f * S;
                                    }
                                    float cx = xST + 6.0f * S + px;
                                    float cyy = yST + 15.0f * S + py;
                                    ru.decide.utils.animation.satoshi.Animation chip = this.chipAnim(f.getName() + ":" + s.getName() + ":" + val);
                                    chip.setDirection(val.equals(current) ? Direction.FORWARDS : Direction.BACKWARDS);
                                    float sel = chip.getOutput();
                                    RenderUtil.Render2D.rect(cx, cyy, tw, 10.0f * S, ColorUtil.overCol(ColorUtil.getColor(0, 0.25f * globalAnim * sa * expandT), ColorUtil.replAlpha(ColorUtil.client(), globalAnim * sa * (0.5f + 0.5f * hover2) * expandT), sel), 3.0f * S);
                                    regular.drawCentered(val, cx + tw / 2.0f, cyy + 1.5f * S, 6.0f * S, ColorUtil.getColor(255, globalAnim * sa * (0.35f + 0.65f * sel) * (0.7f + 0.3f * hover2) * expandT));
                                    px += tw + 3.0f * S;
                                }
                            }
                        }
                        yST += (hMode + 5.0f * S) * fa * vis;
                    }
                    if (setting instanceof MultiBooleanSetting && (vis = this.visAnim(f, s = (MultiBooleanSetting)setting)) > 0.01f) {
                        sa = fa * vis;
                        float hMulti = 15.0f * S + this.multiChipsHeight((MultiBooleanSetting)s, wST - 12.0f * S) + 4.0f * S;
                        onScreen = yST + hMulti >= setTop && yST <= setBottom;
                        hovS2 = onScreen && MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, xST, yST, wST, hMulti);
                        s.getAnimation1().setDirection(hovS2 ? Direction.FORWARDS : Direction.BACKWARDS);
                        hover = s.getAnimation1().getOutput();
                        if (onScreen) {
                            RenderUtil.Render2D.glow(xST, yST, wST, hMulti, ColorUtil.getColor(0, 0.06f * globalAnim * sa), 5.0f * S, 7.0f, 1.0f);
                            RenderUtil.Render2D.rect(xST, yST, wST, hMulti, ColorUtil.getColor(40, 0.15f * globalAnim * sa), 5.0f * S);
                            RenderUtil.Render2D.outline(xST, yST, wST, hMulti, 0.5f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * sa * hover), 5.0f * S);
                            int total = ((MultiBooleanSetting)s).getValues().size();
                            int selectedCount = 0;
                            for (BooleanSetting b : ((MultiBooleanSetting)s).getValues()) {
                                if (!b.getValue().booleanValue()) continue;
                                ++selectedCount;
                            }
                            String counter = selectedCount + " / " + total;
                            draw.drawFadingText(s.getName(), xST + 6.0f * S, yST + 4.5f * S, wST - draw.getWidth(counter, 6.0f * S) - 16.0f * S, ColorUtil.getColor(255, globalAnim * sa * (0.5f + 0.5f * hover)), 6.5f * S);
                            draw.draw(counter, xST + wST - 6.0f * S - draw.getWidth(counter, 6.0f * S), yST + 4.75f * S, 6.0f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * sa * (0.6f + 0.4f * hover)));
                            float chipMaxW = wST - 12.0f * S;
                            float px = 0.0f;
                            float py = 0.0f;
                            for (BooleanSetting b : ((MultiBooleanSetting)s).getValues()) {
                                float tw = draw.getWidth(b.getName(), 6.0f * S) + 8.0f * S;
                                if (px + tw > chipMaxW && px > 0.0f) {
                                    px = 0.0f;
                                    py += 12.0f * S;
                                }
                                float cx = xST + 6.0f * S + px;
                                float cyy = yST + 15.0f * S + py;
                                ru.decide.utils.animation.satoshi.Animation chip = this.chipAnim(f.getName() + ":" + s.getName() + ":" + b.getName());
                                chip.setDirection(b.getValue() != false ? Direction.FORWARDS : Direction.BACKWARDS);
                                float sel = chip.getOutput();
                                RenderUtil.Render2D.rect(cx, cyy, tw, 10.0f * S, ColorUtil.overCol(ColorUtil.getColor(0, 0.25f * globalAnim * sa), ColorUtil.replAlpha(ColorUtil.client(), globalAnim * sa * (0.5f + 0.5f * hover)), sel), 3.0f * S);
                                regular.drawCentered(b.getName(), cx + tw / 2.0f, cyy + 1.5f * S, 6.0f * S, ColorUtil.getColor(255, globalAnim * sa * (0.35f + 0.65f * sel) * (0.7f + 0.3f * hover)));
                                px += tw + 3.0f * S;
                            }
                        }
                        yST += (hMulti + 5.0f * S) * fa * vis;
                    }
                    if (setting instanceof BooleanSetting && (vis = this.visAnim(f, s = (BooleanSetting)setting)) > 0.01f) {
                        sa = fa * vis;
                        boolean onScreen3 = yST + 16.0f * S >= setTop && yST <= setBottom;
                        hovS = onScreen3 && MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, xST, yST, wST, 16.0f * S);
                        ((BooleanSetting)s).animation.setDirection(hovS ? Direction.FORWARDS : Direction.BACKWARDS);
                        float hover3 = ((BooleanSetting)s).animation.getOutput();
                        ((BooleanSetting)s).animation2.setDirection(((BooleanSetting)s).getValue() != false ? Direction.FORWARDS : Direction.BACKWARDS);
                        float sanimation2 = ((BooleanSetting)s).animation2.getOutput();
                        if (onScreen3) {
                            RenderUtil.Render2D.glow(xST, yST, wST, 16.0f * S, ColorUtil.getColor(0, 0.06f * globalAnim * sa), 5.0f * S, 7.0f, 1.0f);
                            RenderUtil.Render2D.rect(xST, yST, wST, 16.0f * S, ColorUtil.getColor(40, 0.15f * globalAnim * sa), 5.0f * S);
                            RenderUtil.Render2D.outline(xST, yST, wST, 16.0f * S, 0.5f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * sa * hover3), 5.0f * S);
                            draw.draw(s.getName(), xST + 6.0f * S, yST + 4.0f * S, 6.5f * S, ColorUtil.getColor(255, globalAnim * sa * (0.5f + 0.5f * hover3)));
                            RenderUtil.Render2D.rect(xST + wST - 6.0f * S - 12.0f * S, yST + 4.5f * S, 12.0f * S, 7.0f * S, ColorUtil.overCol(ColorUtil.getColor(0, 0.2f * globalAnim * sa), ColorUtil.replAlpha(ColorUtil.client(), globalAnim * sa * (0.5f + 0.5f * hover3)), sanimation2), 3.5f * S);
                            RenderUtil.Render2D.rect(xST + wST - 6.0f * S - 12.0f * S + 1.25f * S + 4.5f * S * sanimation2, yST + 5.75f * S, 4.5f * S, 4.5f * S, ColorUtil.getColor(255, globalAnim * sa * (0.2f + 0.4f * sanimation2 + 0.4f * hover3)), 2.5f * S);
                        }
                        yST += 21.0f * S * fa * vis;
                    }
                    if (setting instanceof ButtonSetting && (vis = this.visAnim(f, s = (ButtonSetting)setting)) > 0.01f) {
                        sa = fa * vis;
                        boolean onScreen4 = yST + 16.0f * S >= setTop && yST <= setBottom;
                        hovS = onScreen4 && MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, xST, yST, wST, 16.0f * S);
                        ru.decide.utils.animation.satoshi.Animation btn = this.chipAnim(f.getName() + ":" + s.getName() + ":btn");
                        btn.setDirection(hovS ? Direction.FORWARDS : Direction.BACKWARDS);
                        hover = btn.getOutput();
                        ((ButtonSetting)s).pressAnim.update();
                        float press = ((ButtonSetting)s).pressAnim.get();
                        float accent = Math.max(hover, press);
                        if (onScreen4) {
                            RenderUtil.Render2D.glow(xST, yST, wST, 16.0f * S, ColorUtil.getColor(0, 0.06f * globalAnim * sa), 5.0f * S, 7.0f, 1.0f);
                            RenderUtil.Render2D.rect(xST, yST, wST, 16.0f * S, ColorUtil.overCol(ColorUtil.getColor(40, 0.15f * globalAnim * sa), ColorUtil.replAlpha(ColorUtil.client(), 0.25f * globalAnim * sa), accent), 5.0f * S);
                            RenderUtil.Render2D.outline(xST, yST, wST, 16.0f * S, 0.5f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * sa * accent), 5.0f * S);
                            RenderUtil.Render2D.glow(xST, yST, wST, 16.0f * S, ColorUtil.replAlpha(ColorUtil.client(), 0.15f * globalAnim * sa * press), 5.0f * S, 8.0f, 1.0f);
                            draw.drawCentered(s.getName(), xST + wST / 2.0f, yST + 4.0f * S, 6.5f * S, ColorUtil.overCol(ColorUtil.getColor(255, globalAnim * sa * (0.5f + 0.5f * hover)), ColorUtil.replAlpha(ColorUtil.client(), globalAnim * sa), press));
                        }
                        yST += 21.0f * S * fa * vis;
                    }
                    if (setting instanceof BindSetting && (vis = this.visAnim(f, s = (BindSetting)setting)) > 0.01f) {
                        sa = fa * vis;
                        boolean onScreen5 = yST + 16.0f * S >= setTop && yST <= setBottom;
                        hovS = onScreen5 && MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, xST, yST, wST, 16.0f * S);
                        ((BindSetting)s).animation.setDirection(hovS ? Direction.FORWARDS : Direction.BACKWARDS);
                        float hover4 = ((BindSetting)s).animation.getOutput();
                        if (onScreen5) {
                            RenderUtil.Render2D.glow(xST, yST, wST, 16.0f * S, ColorUtil.getColor(0, 0.06f * globalAnim * sa), 5.0f * S, 7.0f, 1.0f);
                            RenderUtil.Render2D.rect(xST, yST, wST, 16.0f * S, ColorUtil.getColor(40, 0.15f * globalAnim * sa), 5.0f * S);
                            RenderUtil.Render2D.outline(xST, yST, wST, 16.0f * S, 0.5f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * sa * hover4), 5.0f * S);
                            boolean binding = this.activeBind == s;
                            ru.decide.utils.animation.satoshi.Animation bindAct = this.chipAnim(f.getName() + ":" + s.getName() + ":act");
                            bindAct.setDirection(binding ? Direction.FORWARDS : Direction.BACKWARDS);
                            float act = bindAct.getOutput();
                            String keyName = binding ? "..." : (((BindSetting)s).get() == -1 ? "n/a" : Keyboard.keyName(((BindSetting)s).get()));
                            bw = this.smooth(f.getName() + ":" + s.getName() + ":bw", draw.getWidth(keyName, 6.0f * S) + 10.0f * S);
                            draw.drawFadingText(s.getName(), xST + 6.0f * S, yST + 4.0f * S, wST - bw - 16.0f * S, ColorUtil.getColor(255, globalAnim * sa * (0.5f + 0.5f * hover4)), 6.5f * S);
                            RenderUtil.Render2D.rect(xST + wST - 6.0f * S - bw, yST + 3.0f * S, bw, 10.0f * S, ColorUtil.overCol(ColorUtil.getColor(0, 0.25f * globalAnim * sa), ColorUtil.replAlpha(ColorUtil.client(), 0.15f * globalAnim * sa), act), 3.0f * S);
                            RenderUtil.Render2D.outline(xST + wST - 6.0f * S - bw, yST + 3.0f * S, bw, 10.0f * S, 0.5f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * sa * act), 3.0f * S);
                            RenderUtil.Render2D.glow(xST + wST - 6.0f * S - bw, yST + 3.0f * S, bw, 10.0f * S, ColorUtil.replAlpha(ColorUtil.client(), 0.12f * globalAnim * sa * act), 3.0f * S, 6.0f, 1.0f);
                            regular.drawCentered(keyName, xST + wST - 6.0f * S - bw / 2.0f, yST + 4.5f * S, 6.0f * S, ColorUtil.replAlpha(ColorUtil.overCol(ColorUtil.getColor(255), ColorUtil.client(), act), globalAnim * sa * (0.45f + 0.55f * Math.max(act, hover4))));
                        }
                        yST += 21.0f * S * fa * vis;
                    }
                    if (setting instanceof StringSetting && (vis = this.visAnim(f, s = (StringSetting)setting)) > 0.01f) {
                        sa = fa * vis;
                        boolean onScreen6 = yST + 16.0f * S >= setTop && yST <= setBottom;
                        hovS = onScreen6 && MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, xST, yST, wST, 16.0f * S);
                        s.getAnimation1().setDirection(hovS ? Direction.FORWARDS : Direction.BACKWARDS);
                        float hover5 = s.getAnimation1().getOutput();
                        if (onScreen6) {
                            String shown;
                            RenderUtil.Render2D.glow(xST, yST, wST, 16.0f * S, ColorUtil.getColor(0, 0.06f * globalAnim * sa), 5.0f * S, 7.0f, 1.0f);
                            RenderUtil.Render2D.rect(xST, yST, wST, 16.0f * S, ColorUtil.getColor(40, 0.15f * globalAnim * sa), 5.0f * S);
                            RenderUtil.Render2D.outline(xST, yST, wST, 16.0f * S, 0.5f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * sa * hover5), 5.0f * S);
                            boolean editing = this.activeString == s;
                            ru.decide.utils.animation.satoshi.Animation editAct = this.chipAnim(f.getName() + ":" + s.getName() + ":act");
                            editAct.setDirection(editing ? Direction.FORWARDS : Direction.BACKWARDS);
                            float act = editAct.getOutput();
                            Object object = editing ? this.stringBuffer + (System.currentTimeMillis() / 400L % 2L == 0L ? "_" : "") : (shown = ((StringSetting)s).getValue());
                            if (shown.isEmpty()) {
                                shown = "...";
                            }
                            bw = this.smooth(f.getName() + ":" + s.getName() + ":bw", Math.min(draw.getWidth(shown, 6.0f * S) + 10.0f * S, wST * 0.55f));
                            draw.drawFadingText(s.getName(), xST + 6.0f * S, yST + 4.0f * S, wST - bw - 16.0f * S, ColorUtil.getColor(255, globalAnim * sa * (0.5f + 0.5f * hover5)), 6.5f * S);
                            RenderUtil.Render2D.rect(xST + wST - 6.0f * S - bw, yST + 3.0f * S, bw, 10.0f * S, ColorUtil.overCol(ColorUtil.getColor(0, 0.25f * globalAnim * sa), ColorUtil.replAlpha(ColorUtil.client(), 0.15f * globalAnim * sa), act), 3.0f * S);
                            RenderUtil.Render2D.outline(xST + wST - 6.0f * S - bw, yST + 3.0f * S, bw, 10.0f * S, 0.5f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * sa * act), 3.0f * S);
                            RenderUtil.Render2D.glow(xST + wST - 6.0f * S - bw, yST + 3.0f * S, bw, 10.0f * S, ColorUtil.replAlpha(ColorUtil.client(), 0.12f * globalAnim * sa * act), 3.0f * S, 6.0f, 1.0f);
                            regular.drawFadingText(shown, xST + wST - 6.0f * S - bw + 5.0f * S, yST + 4.5f * S, bw - 8.0f * S, ColorUtil.getColor(255, globalAnim * sa * (0.45f + 0.55f * Math.max(act, hover5))), 6.0f * S);
                        }
                        yST += 21.0f * S * fa * vis;
                    }
                    if (!(setting instanceof ColorSetting) || !((vis = this.visAnim(f, s = (ColorSetting)setting)) > 0.01f)) continue;
                    sa = fa * vis;
                    ((ColorSetting)s).pickerAnim.setDirection(((ColorSetting)s).pickerOpen ? Direction.FORWARDS : Direction.BACKWARDS);
                    float open = ((ColorSetting)s).pickerAnim.getOutput();
                    float hCol = 16.0f * S + open * 35.0f * S;
                    boolean onScreen7 = yST + hCol >= setTop && yST <= setBottom;
                    boolean hovS4 = onScreen7 && MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, xST, yST, wST, hCol);
                    s.getAnimation1().setDirection(hovS4 ? Direction.FORWARDS : Direction.BACKWARDS);
                    float hover6 = s.getAnimation1().getOutput();
                    float bx = xST + 6.0f * S;
                    float bwd = wST - 12.0f * S;
                    int col = ((ColorSetting)s).getValue();
                    String kb = f.getName() + ":" + s.getName();
                    if (onScreen7) {
                        RenderUtil.Render2D.glow(xST, yST, wST, hCol, ColorUtil.getColor(0, 0.06f * globalAnim * sa), 5.0f * S, 7.0f, 1.0f);
                        RenderUtil.Render2D.rect(xST, yST, wST, hCol, ColorUtil.getColor(40, 0.15f * globalAnim * sa), 5.0f * S);
                        RenderUtil.Render2D.outline(xST, yST, wST, hCol, 0.5f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * sa * hover6), 5.0f * S);
                        draw.drawFadingText(s.getName(), xST + 6.0f * S, yST + 4.0f * S, wST - 34.0f * S, ColorUtil.getColor(255, globalAnim * sa * (0.5f + 0.5f * hover6)), 6.5f * S);
                        int pr = (int)this.smooth(kb + ":pr", col >> 16 & 0xFF);
                        int pg = (int)this.smooth(kb + ":pg", col >> 8 & 0xFF);
                        int pb = (int)this.smooth(kb + ":pb", col & 0xFF);
                        float pcx = xST + wST - 6.0f * S - 9.0f * S;
                        float pcy = yST + 4.0f * S;
                        RenderUtil.Render2D.rect(pcx, pcy, 8.0f * S, 8.0f * S, ColorUtil.getColor(pr, pg, pb, globalAnim * sa), 8.0f * S);
                        RenderUtil.Render2D.outline(pcx - 0.75f * S, pcy - 0.75f * S, 9.5f * S, 9.5f * S, 0.25f * S, ColorUtil.getColor(pr, pg, pb, globalAnim * sa * (0.4f + 0.6f * hover6)), 8.0f * S);
                        RenderUtil.Render2D.glow(pcx, pcy, 8.0f * S, 8.0f * S, ColorUtil.getColor(pr, pg, pb, 0.1f * globalAnim * sa), 8.0f * S, 7.0f, 1.0f);
                        if (open > 0.01f) {
                            float[] hsb = Color.RGBtoHSB(col >> 16 & 0xFF, col >> 8 & 0xFF, col & 0xFF, null);
                            float shh = this.smooth(kb + ":h", hsb[0]);
                            float shs = this.smooth(kb + ":s", hsb[1]);
                            float shb = this.smooth(kb + ":b", hsb[2]);
                            float[] smoothHsb = new float[]{shh, shs, shb};
                            int segs = 16;
                            float seg = bwd / (float)segs;
                            for (int bar = 0; bar < 3; ++bar) {
                                float by = yST + 18.0f * S + (float)(bar * 12) * S;
                                for (int i = 0; i < segs; ++i) {
                                    float t0 = (float)i / (float)segs;
                                    float t1 = (float)(i + 1) / (float)segs;
                                    int c0 = switch (bar) {
                                        case 0 -> Color.HSBtoRGB(t0, 1.0f, 1.0f);
                                        case 1 -> Color.HSBtoRGB(shh, t0, shb);
                                        default -> Color.HSBtoRGB(shh, shs, t0);
                                    };
                                    int c1 = switch (bar) {
                                        case 0 -> Color.HSBtoRGB(t1, 1.0f, 1.0f);
                                        case 1 -> Color.HSBtoRGB(shh, t1, shb);
                                        default -> Color.HSBtoRGB(shh, shs, t1);
                                    };
                                    int a0 = ColorUtil.replAlpha(c0, globalAnim * sa * open);
                                    int a1 = ColorUtil.replAlpha(c1, globalAnim * sa * open);
                                    RenderUtil.Render2D.gradientRect(bx + (float)i * seg, by, seg + (i == segs - 1 ? 0.0f : 0.5f * S), 4.0f * S, new int[]{a0, a1, a1, a0}, i == 0 ? 2.0f * S : 0.0f, i == segs - 1 ? 2.0f * S : 0.0f, i == segs - 1 ? 2.0f * S : 0.0f, i == 0 ? 2.0f * S : 0.0f);
                                }
                                float kx = bx + bwd * smoothHsb[bar];
                                int kc = switch (bar) {
                                    case 0 -> Color.HSBtoRGB(shh, 1.0f, 1.0f);
                                    default -> Color.HSBtoRGB(shh, shs, shb);
                                };
                                RenderUtil.Render2D.rect(kx - 3.0f * S, by - 1.0f * S, 6.0f * S, 6.0f * S, ColorUtil.getColor(255, globalAnim * sa * open), 6.0f * S);
                                RenderUtil.Render2D.rect(kx - 2.0f * S, by, 4.0f * S, 4.0f * S, ColorUtil.replAlpha(kc, globalAnim * sa * open), 6.0f * S);
                            }
                        }
                    }
                    if (open > 0.01f && this.draggingColor == s) {
                        float[] hsb = Color.RGBtoHSB(col >> 16 & 0xFF, col >> 8 & 0xFF, col & 0xFF, null);
                        float t = MathUtil.clamp(((float)this.lastMouseX - bx) / bwd, 0.0f, 1.0f);
                        float[] nh = new float[]{hsb[0], hsb[1], hsb[2]};
                        nh[this.draggingColorBar] = t;
                        int rgb = Color.HSBtoRGB(nh[0], nh[1], nh[2]);
                        int newCol = col & 0xFF000000 | rgb & 0xFFFFFF;
                        if (newCol != col) {
                            ((ColorSetting)s).set(newCol);
                            GuiSounds.colorTick(t);
                        }
                    }
                    yST += (hCol + 5.0f * S) * fa * vis;
                }
            }
            float contentHS = yST + this.settingScrollAnim - (ySetting + 10.0f * S);
            this.settingMaxScroll = Math.max(0.0f, contentHS - (hSetting - 14.0f * S));
            if (this.settingScrollTarget > this.settingMaxScroll) {
                this.settingScrollTarget = this.settingMaxScroll;
            }
            Scissor.disable();
        }
        Render2D.endOverlay();
        if (context != null) {
            context.method_51448().popMatrix();
        }
    }

    public void openHandsEditor() {
        this.beforeEditorOpen();
        this.handsEditor.open();
        GuiSounds.editor();
    }

    public void openPreviewEditor(Module target) {
        this.beforeEditorOpen();
        PreviewEditor.getInstance().open(target);
        GuiSounds.editor();
    }

    public void openCrosshairEditor() {
        this.beforeEditorOpen();
        CrosshairEditor.getInstance().open();
        GuiSounds.editor();
    }

    private void beforeEditorOpen() {
        this.exit = false;
        this.glomalAnim.run(1.0, (double)0.2f, Easings.QUAD_OUT);
    }

    public void method_25432() {
        OverlayEditors.closeAll();
        returnToGlassAfterEditor = false;
        super.method_25432();
    }

    private void closeCheck() {
        if (returnToGlassAfterEditor && OverlayEditors.active() == null) {
            returnToGlassAfterEditor = false;
            OverlayEditors.closeAll();
            mc.method_1507((class_437)LvGui.INSTANCE);
            return;
        }
        if (this.exit && this.glomalAnim.isFinished()) {
            this.method_25419();
            GuiMusicPlayer.stop();
            this.exit = false;
        }
    }

    private void closeMenuToWorld() {
        this.exit = false;
        mc.method_1507(null);
        GuiMusicPlayer.stop();
    }

    private boolean isCosmeticsCategory() {
        return this.active == Category.COSMETICS;
    }

    private boolean isConfigCategory() {
        return this.active == Category.CONFIGS;
    }

    private boolean isMarkerCategory() {
        return this.active == Category.MARKERS;
    }

    private boolean isSpecialCategory() {
        return this.isCosmeticsCategory() || this.isConfigCategory() || this.isMarkerCategory();
    }

    private boolean isCustomPanelCategory() {
        return this.isSpecialCategory();
    }

    private void renderConfigOrMarkerUi(float x, float y, float w, float h, float globalAnim, Font draw, Font regular, Font guiicon) {
        Font font = Fonts.sf_regular;
        float xLeft = x + 6.0f * S + 36.0f * S;
        float yTop = y + 6.0f * S + 26.0f * S;
        float wLeft = 140.0f * S;
        float hArea = h - 12.0f * S - 26.0f * S;
        float xRight = x + 6.0f * S + 36.0f * S + 140.0f * S + 6.0f * S;
        float wRight = w - (6.0f * S + 36.0f * S + 6.0f * S + 140.0f * S + 6.0f * S);
        RenderUtil.Render2D.glow(xLeft, yTop, wLeft, hArea, ColorUtil.getColor(0, 0.04f * globalAnim), 7.0f * S, 8.0f, 1.0f);
        RenderUtil.Render2D.rect(xLeft, yTop, wLeft, hArea, ColorUtil.getColor(0, 0.15f * globalAnim), 7.0f * S);
        RenderUtil.Render2D.glow(xRight, yTop, wRight, hArea, ColorUtil.getColor(0, 0.04f * globalAnim), 7.0f * S, 8.0f, 1.0f);
        RenderUtil.Render2D.rect(xRight, yTop, wRight, hArea, ColorUtil.getColor(0, 0.15f * globalAnim), 7.0f * S);
        if (this.isConfigCategory()) {
            this.renderConfigPanel(xLeft, yTop, wLeft, hArea, globalAnim, draw, regular, font);
            this.renderConfigList(xRight, yTop, wRight, hArea, globalAnim, draw, regular, font);
        } else {
            this.renderMarkerPanel(xLeft, yTop, wLeft, hArea, globalAnim, draw, regular, font);
            this.renderMarkerList(xRight, yTop, wRight, hArea, globalAnim, draw, regular, font);
        }
    }

    private void drawInputField(float ix, float iy, float iw, float ih, String text, boolean focused, String placeholder, float globalAnim, Font font) {
        float focusAnim = focused ? 1.0f : 0.0f;
        int bg = ColorUtil.getColor(40, 0.15f * globalAnim);
        int border = ColorUtil.replAlpha(ColorUtil.client(), globalAnim * 0.3f * focusAnim);
        RenderUtil.Render2D.rect(ix, iy, iw, ih, bg, 4.0f * S);
        if (focused) {
            RenderUtil.Render2D.outline(ix, iy, iw, ih, 0.5f * S, border, 4.0f * S);
        }
        if (text.isEmpty() && !focused) {
            String shown = placeholder;
            font.draw(shown, ix + 6.0f * S, iy + (ih - 6.0f * S) / 2.0f, 6.0f * S, ColorUtil.getColor(140, globalAnim * 0.6f));
        } else {
            boolean blink = focused && System.currentTimeMillis() / 450L % 2L == 0L;
            String shown = text + (blink ? "_" : "");
            font.draw(shown, ix + 6.0f * S, iy + (ih - 6.0f * S) / 2.0f, 6.0f * S, ColorUtil.getColor(200, globalAnim));
        }
    }

    private void drawSmallButton(float bx, float by, float bw, float bh, String label, float globalAnim, Font font, boolean hovered) {
        float hp = hovered ? 1.0f : 0.0f;
        int bg = ColorUtil.overCol(ColorUtil.getColor(40, 0.15f * globalAnim), ColorUtil.replAlpha(ColorUtil.client(), 0.2f * globalAnim), hp);
        RenderUtil.Render2D.rect(bx, by, bw, bh, bg, 4.0f * S);
        font.drawCentered(label, bx + bw / 2.0f, by + (bh - 6.0f * S) / 2.0f, 6.0f * S, ColorUtil.getColor(200, globalAnim * (0.6f + 0.4f * hp)));
    }

    private void renderConfigPanel(float xLeft, float yTop, float wLeft, float hArea, float globalAnim, Font draw, Font regular, Font font) {
        float pad = 8.0f * S;
        float inputH = 18.0f * S;
        float btnH = 18.0f * S;
        float gap = 5.0f * S;
        this.cfgInputX = xLeft + pad;
        this.cfgInputY = yTop + pad;
        this.cfgInputW = wLeft - pad * 2.0f;
        this.cfgInputH = inputH;
        this.cfgAddX = xLeft + pad;
        this.cfgAddY = this.cfgInputY + this.cfgInputH + gap;
        this.cfgAddW = wLeft - pad * 2.0f;
        this.cfgAddH = btnH;
        this.drawInputField(this.cfgInputX, this.cfgInputY, this.cfgInputW, this.cfgInputH, this.configInput, this.configInputFocused, "\u0418\u043c\u044f \u043a\u043e\u043d\u0444\u0438\u0433\u0430", globalAnim, font);
        boolean addHov = MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, this.cfgAddX, this.cfgAddY, this.cfgAddW, this.cfgAddH);
        this.drawSmallButton(this.cfgAddX, this.cfgAddY, this.cfgAddW, this.cfgAddH, "+ \u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c", globalAnim, font, addHov);
    }

    private void renderConfigList(float xRight, float yTop, float wRight, float hArea, float globalAnim, Font draw, Font regular, Font font) {
        float pad = 8.0f * S;
        float rowH = 20.0f * S;
        float rowGap = 3.0f * S;
        this.cfgListX = xRight + pad;
        this.cfgListY = yTop + pad;
        this.cfgListW = wRight - pad * 2.0f;
        this.cfgListH = hArea - pad * 2.0f;
        this.configCache.clear();
        this.configCache.addAll(Client.get().configManager().listConfigs());
        float contentH = (float)this.configCache.size() * (rowH + rowGap);
        float maxScroll = Math.max(0.0f, contentH - this.cfgListH);
        this.configScrollTarget = MathUtil.clamp(this.configScrollTarget, 0.0f, maxScroll);
        this.configScroll += (this.configScrollTarget - this.configScroll) * 0.2f;
        this.configRowRects.clear();
        this.configDelRects.clear();
        Scissor.enable(this.cfgListX, this.cfgListY, this.cfgListW, this.cfgListH, 2.0f);
        if (this.configCache.isEmpty()) {
            font.drawCentered("\u041d\u0435\u0442 \u043a\u043e\u043d\u0444\u0438\u0433\u043e\u0432", this.cfgListX + this.cfgListW / 2.0f, this.cfgListY + this.cfgListH / 2.0f - 4.0f * S, 7.0f * S, ColorUtil.getColor(255, globalAnim * 0.15f));
        }
        float ry = this.cfgListY - this.configScroll;
        for (int i = 0; i < this.configCache.size(); ++i) {
            float ha;
            String name = this.configCache.get(i);
            float rh = rowH;
            boolean onScreen = ry + rh >= this.cfgListY && ry <= this.cfgListY + this.cfgListH;
            boolean hov = onScreen && MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, this.cfgListX, ry, this.cfgListW, rh);
            float f = ha = hov ? 1.0f : 0.0f;
            if (onScreen) {
                int rowBg = ColorUtil.overCol(ColorUtil.getColor(0, 0.12f * globalAnim), ColorUtil.getColor(25, 0.25f * globalAnim), ha);
                RenderUtil.Render2D.rect(this.cfgListX, ry, this.cfgListW, rh, rowBg, 4.0f * S);
                boolean isAuto = name.equals("auto");
                Object displayName = isAuto ? name + " (\u0430\u0432\u0442\u043e)" : name;
                draw.draw((String)displayName, this.cfgListX + 8.0f * S, ry + (rh - 7.0f * S) / 2.0f, 7.0f * S, ColorUtil.getColor(255, globalAnim * (0.5f + 0.3f * ha)));
                float delSize = 12.0f * S;
                float delX = this.cfgListX + this.cfgListW - delSize - 4.0f * S;
                float delY = ry + (rh - delSize) / 2.0f;
                boolean delHov = MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, delX, delY, delSize, delSize);
                float dha = delHov ? 1.0f : 0.0f;
                int delBg = ColorUtil.overCol(ColorUtil.getColor(0, 0.15f * globalAnim), ColorUtil.getColor(125, 50, 50, 0.3f * globalAnim), dha);
                RenderUtil.Render2D.rect(delX, delY, delSize, delSize, delBg, 3.0f * S);
                Fonts.icon.drawCentered("W", delX + delSize / 2.0f, delY + delSize / 2.0f - 3.5f, 7.0f * S, ColorUtil.getColor(255, globalAnim * (0.4f + 0.4f * dha)));
                this.configRowRects.add(new float[]{this.cfgListX, ry, this.cfgListW - delSize - 12.0f * S, rh});
                this.configDelRects.add(new float[]{delX, delY, delSize, delSize});
            }
            ry += rh + rowGap;
        }
        Scissor.disable();
    }

    private void renderMarkerPanel(float xLeft, float yTop, float wLeft, float hArea, float globalAnim, Font draw, Font regular, Font font) {
        float pad = 8.0f * S;
        float inputH = 16.0f * S;
        float gap = 4.0f * S;
        float btnH = 18.0f * S;
        float labelH = 8.0f * S;
        float labelY = yTop + pad;
        font.draw("\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435", xLeft + pad, labelY, 5.5f, ColorUtil.getColor(200, globalAnim * 0.5f));
        this.mkNameX = xLeft + pad;
        this.mkNameY = labelY + labelH;
        this.mkNameW = wLeft - pad * 2.0f;
        this.mkNameH = inputH;
        float coordRowY = this.mkNameY + this.mkNameH + gap + 2.0f * S;
        font.draw("X", xLeft + pad, coordRowY, 5.5f, ColorUtil.getColor(200, globalAnim * 0.5f));
        font.draw("Y", xLeft + pad + (wLeft - pad * 2.0f) / 3.0f + gap / 2.0f, coordRowY, 5.5f, ColorUtil.getColor(200, globalAnim * 0.5f));
        font.draw("Z", xLeft + pad + 2.0f * ((wLeft - pad * 2.0f) / 3.0f + gap / 2.0f), coordRowY, 5.5f, ColorUtil.getColor(200, globalAnim * 0.5f));
        float coordInputY = coordRowY + labelH;
        float coordW = (wLeft - pad * 2.0f - gap * 2.0f) / 3.0f;
        this.mkXInputX = xLeft + pad;
        this.mkXInputY = coordInputY;
        this.mkXInputW = coordW;
        this.mkXInputH = inputH;
        this.mkYInputX = xLeft + pad + coordW + gap;
        this.mkYInputY = coordInputY;
        this.mkYInputW = coordW;
        this.mkYInputH = inputH;
        this.mkZInputX = xLeft + pad + 2.0f * (coordW + gap);
        this.mkZInputY = coordInputY;
        this.mkZInputW = coordW;
        this.mkZInputH = inputH;
        this.mkAddX = xLeft + pad;
        this.mkAddY = coordInputY + inputH + gap + 4.0f * S;
        this.mkAddW = wLeft - pad * 2.0f;
        this.mkAddH = btnH;
        this.drawInputField(this.mkNameX, this.mkNameY, this.mkNameW, this.mkNameH, this.markerNameInput, this.markerInputFocused == 0, "\u0418\u043c\u044f \u043c\u0435\u0442\u043a\u0438", globalAnim, font);
        this.drawInputField(this.mkXInputX, this.mkXInputY, this.mkXInputW, this.mkXInputH, this.markerXInput, this.markerInputFocused == 1, "0", globalAnim, font);
        this.drawInputField(this.mkYInputX, this.mkYInputY, this.mkYInputW, this.mkYInputH, this.markerYInput, this.markerInputFocused == 2, "0", globalAnim, font);
        this.drawInputField(this.mkZInputX, this.mkZInputY, this.mkZInputW, this.mkZInputH, this.markerZInput, this.markerInputFocused == 3, "0", globalAnim, font);
        boolean addHov = MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, this.mkAddX, this.mkAddY, this.mkAddW, this.mkAddH);
        this.drawSmallButton(this.mkAddX, this.mkAddY, this.mkAddW, this.mkAddH, "+ \u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c", globalAnim, font, addHov);
    }

    private void renderMarkerList(float xRight, float yTop, float wRight, float hArea, float globalAnim, Font draw, Font regular, Font font) {
        float pad = 8.0f * S;
        float rowH = 20.0f * S;
        float rowGap = 3.0f * S;
        this.mkListX = xRight + pad;
        this.mkListY = yTop + pad;
        this.mkListW = wRight - pad * 2.0f;
        this.mkListH = hArea - pad * 2.0f;
        this.markerCache.clear();
        WayCommand wc = WayCommand.get();
        if (wc != null) {
            for (WayCommand.Waypoint wp : wc.getWaypoints()) {
                this.markerCache.add(new String[]{wp.name(), String.valueOf((int)wp.pos().field_1352), String.valueOf((int)wp.pos().field_1351), String.valueOf((int)wp.pos().field_1350)});
            }
        }
        float contentH = (float)this.markerCache.size() * (rowH + rowGap);
        float maxScroll = Math.max(0.0f, contentH - this.mkListH);
        this.markerScrollTarget = MathUtil.clamp(this.markerScrollTarget, 0.0f, maxScroll);
        this.markerScroll += (this.markerScrollTarget - this.markerScroll) * 0.2f;
        this.markerRowRects.clear();
        this.markerDelRects.clear();
        Scissor.enable(this.mkListX, this.mkListY, this.mkListW, this.mkListH, 2.0f);
        if (this.markerCache.isEmpty()) {
            font.drawCentered("\u041d\u0435\u0442 \u043c\u0435\u0442\u043e\u043a", this.mkListX + this.mkListW / 2.0f, this.mkListY + this.mkListH / 2.0f - 4.0f * S, 7.0f * S, ColorUtil.getColor(255, globalAnim * 0.15f));
        }
        float ry = this.mkListY - this.markerScroll;
        for (int i = 0; i < this.markerCache.size(); ++i) {
            float ha;
            String[] m = this.markerCache.get(i);
            float rh = rowH;
            boolean onScreen = ry + rh >= this.mkListY && ry <= this.mkListY + this.mkListH;
            boolean hov = onScreen && MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, this.mkListX, ry, this.mkListW, rh);
            float f = ha = hov ? 1.0f : 0.0f;
            if (onScreen) {
                int rowBg = ColorUtil.overCol(ColorUtil.getColor(0, 0.12f * globalAnim), ColorUtil.getColor(25, 0.25f * globalAnim), ha);
                RenderUtil.Render2D.rect(this.mkListX, ry, this.mkListW, rh, rowBg, 4.0f * S);
                String label = m[0] + " " + m[1] + " " + m[2] + " " + m[3];
                draw.draw(label, this.mkListX + 8.0f * S, ry + (rh - 7.0f * S) / 2.0f, 6.5f, ColorUtil.getColor(255, globalAnim * (0.5f + 0.3f * ha)));
                float delSize = 12.0f * S;
                float delX = this.mkListX + this.mkListW - delSize - 4.0f * S;
                float delY = ry + (rh - delSize) / 2.0f;
                boolean delHov = MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, delX, delY, delSize, delSize);
                float dha = delHov ? 1.0f : 0.0f;
                int delBg = ColorUtil.overCol(ColorUtil.getColor(0, 0.15f * globalAnim), ColorUtil.getColor(125, 50, 50, 0.3f * globalAnim), dha);
                RenderUtil.Render2D.rect(delX, delY, delSize, delSize, delBg, 3.0f * S);
                Fonts.icon.drawCentered("W", delX + delSize / 2.0f, delY + delSize / 2.0f - 3.5f, 7.0f * S, ColorUtil.getColor(255, globalAnim * (0.4f + 0.4f * dha)));
                this.markerRowRects.add(new float[]{this.mkListX, ry, this.mkListW - delSize - 12.0f * S, rh});
                this.markerDelRects.add(new float[]{delX, delY, delSize, delSize});
            }
            ry += rh + rowGap;
        }
        Scissor.disable();
    }

    private void renderCosmeticsUi(class_332 context, float x, float y, float w, float h, float globalAnim, Font draw, Font regular) {
        float xLeft = x + 6.0f * S + 36.0f * S;
        float yTop = y + 6.0f * S + 26.0f * S;
        float wLeft = 140.0f * S;
        float hArea = h - 12.0f * S - 26.0f * S;
        float xRight = x + 6.0f * S + 36.0f * S + 140.0f * S + 6.0f * S;
        float wRight = w - (6.0f * S + 36.0f * S + 6.0f * S + 140.0f * S + 6.0f * S);
        this.renderCosmeticsSubTabs(xLeft, yTop, wLeft, hArea, globalAnim, draw, regular);
        this.renderCosmeticsGrid(context, xRight, yTop, wRight, hArea, globalAnim, draw, regular);
    }

    private void renderCosmeticsSubTabs(float xLeft, float yTop, float wLeft, float hArea, float globalAnim, Font draw, Font regular) {
        RenderUtil.Render2D.glow(xLeft, yTop, wLeft, hArea, ColorUtil.getColor(0, 0.04f * globalAnim), 7.0f * S, 8.0f, 1.0f);
        RenderUtil.Render2D.rect(xLeft, yTop, wLeft, hArea, ColorUtil.getColor(0, 0.15f * globalAnim), 7.0f * S);
        regular.draw("\u041a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u0438", xLeft + 10.0f * S, yTop + 8.0f * S, 6.5f * S, ColorUtil.getColor(160, 0.6f * globalAnim));
        RenderUtil.Render2D.rect(xLeft + 8.0f * S, yTop + 18.0f * S, wLeft - 16.0f * S, 0.5f * S, ColorUtil.getColor(255, 0.05f * globalAnim), 0.25f * S);
        float subTabH = 18.0f * S;
        float gap = 2.0f * S;
        float pad = 6.0f * S;
        float tabW = wLeft - pad * 2.0f;
        float tabX = xLeft + pad;
        float listTop = yTop + 20.0f * S;
        float listBottom = yTop + hArea - pad;
        float viewH = Math.max(0.0f, listBottom - listTop);
        float contentH = (float)CosmeticType.values().length * (subTabH + gap) - gap;
        this.cosmeticsTabMaxScroll = Math.max(0.0f, contentH - viewH);
        this.cosmeticsTabScrollTarget = MathUtil.clamp(this.cosmeticsTabScrollTarget, 0.0f, this.cosmeticsTabMaxScroll);
        this.cosmeticsTabScroll += (this.cosmeticsTabScrollTarget - this.cosmeticsTabScroll) * 0.2f;
        if (Math.abs(this.cosmeticsTabScrollTarget - this.cosmeticsTabScroll) < 0.01f) {
            this.cosmeticsTabScroll = this.cosmeticsTabScrollTarget;
        }
        Scissor.enable(tabX, listTop, tabW, viewH, 2.0f);
        float ySubTab = listTop - this.cosmeticsTabScroll;
        for (CosmeticType cat : CosmeticType.values()) {
            boolean isSel = this.activeCosmetic == cat;
            boolean inView = ySubTab + subTabH > listTop && ySubTab < listTop + viewH;
            boolean isHov = inView && MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, tabX, ySubTab, tabW, subTabH);
            cat.selectAnim.setDirection(isSel ? Direction.FORWARDS : Direction.BACKWARDS);
            cat.hoverAnim.setDirection(isHov ? Direction.FORWARDS : Direction.BACKWARDS);
            float selA = cat.selectAnim.getOutput();
            float hovA = cat.hoverAnim.getOutput();
            RenderUtil.Render2D.glow(tabX, ySubTab, tabW, subTabH, ColorUtil.getColor(0, 0.04f * globalAnim * (0.5f + 0.5f * selA)), 5.0f * S, 6.0f, 1.0f);
            int bgCol = ColorUtil.overCol(ColorUtil.getColor(35, 0.15f * globalAnim), ColorUtil.getColor(55, 0.3f * globalAnim), Math.max(hovA, selA));
            RenderUtil.Render2D.rect(tabX, ySubTab, tabW, subTabH, bgCol, 5.0f * S);
            if (selA > 0.01f || hovA > 0.01f) {
                RenderUtil.Render2D.outline(tabX, ySubTab, tabW, subTabH, 0.5f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * (0.8f * selA + 0.3f * hovA)), 5.0f * S);
            }
            draw.draw(cat.getName(), tabX + 8.0f * S, ySubTab + 5.0f * S, 7.0f * S, ColorUtil.overCol(ColorUtil.getColor(200, 0.8f * globalAnim), ColorUtil.client(), selA));
            String countStr = String.valueOf(this.cosmeticTabCount(cat));
            float countW = regular.getWidth(countStr, 6.0f * S) + 8.0f * S;
            float countH = 12.0f * S;
            float countX = tabX + tabW - 6.0f * S - countW;
            float countY = ySubTab + (subTabH - countH) / 2.0f;
            int badgeBg = ColorUtil.overCol(ColorUtil.getColor(0, 0.25f * globalAnim), ColorUtil.multAlpha(ColorUtil.client(), 0.25f * globalAnim), selA);
            RenderUtil.Render2D.rect(countX, countY, countW, countH, badgeBg, 3.0f * S);
            regular.drawCentered(countStr, countX + countW / 2.0f, countY + 3.0f * S, 6.0f * S, ColorUtil.overCol(ColorUtil.getColor(160, 0.7f * globalAnim), ColorUtil.client(), selA));
            ySubTab += subTabH + gap;
        }
        Scissor.disable();
    }

    private String activeSlotType() {
        String group = this.activeGroup();
        if (group == null) {
            return this.activeCosmetic.getType();
        }
        if (this.activeCosmetic == CosmeticType.ACCESSORIES) {
            return "neck".equals(group) ? "neck" : "watch";
        }
        return group;
    }

    private int cosmeticTabCount(CosmeticType cat) {
        String[][] anyGroups = Menu.groupsFor(cat);
        if (anyGroups != null) {
            int total = 0;
            for (String[] group : anyGroups) {
                total += CosmeticManager.getInstance().getEntriesByGroup(group[0]).size();
            }
            return total;
        }
        return cat.count();
    }

    private float renderAccessoryGroups(float x, float y, float w, float globalAnim, Font draw) {
        this.accessoryTabRects.clear();
        float pad = 8.0f * S;
        float gap = 6.0f * S;
        float btnH = 16.0f * S;
        String[][] groups = Menu.groupsFor(this.activeCosmetic);
        if (groups == null) {
            return y;
        }
        float btnW = (w - pad * 2.0f - gap * (float)(groups.length - 1)) / (float)groups.length;
        float bx = x + pad;
        for (String[] group : groups) {
            boolean sel = group[0].equals(this.activeGroup());
            boolean hov = MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, bx, y, btnW, btnH);
            int count = CosmeticManager.getInstance().getEntriesByGroup(group[0]).size();
            int bg = sel ? ColorUtil.multAlpha(ColorUtil.client(), 0.22f * globalAnim) : ColorUtil.overCol(ColorUtil.getColor(30, 0.5f * globalAnim), ColorUtil.getColor(52, 0.7f * globalAnim), hov ? 1.0f : 0.0f);
            RenderUtil.Render2D.rect(bx, y, btnW, btnH, bg, 4.0f * S);
            if (sel || hov) {
                RenderUtil.Render2D.outline(bx, y, btnW, btnH, 0.5f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * (sel ? 0.9f : 0.35f)), 4.0f * S);
            }
            String label = group[1] + " " + count;
            draw.drawCentered(label, bx + btnW / 2.0f, y + 4.5f * S, 6.5f * S, sel ? ColorUtil.client() : ColorUtil.getColor(180, 0.85f * globalAnim));
            this.accessoryTabRects.add(new float[]{bx, y, btnW, btnH});
            bx += btnW + gap;
        }
        return y + btnH;
    }

    private void renderCosmeticsGrid(class_332 context, float xRight, float yTop, float wRight, float hArea, float globalAnim, Font draw, Font regular) {
        RenderUtil.Render2D.glow(xRight, yTop, wRight, hArea, ColorUtil.getColor(0, 0.04f * globalAnim), 7.0f * S, 8.0f, 1.0f);
        RenderUtil.Render2D.rect(xRight, yTop, wRight, hArea, ColorUtil.getColor(0, 0.15f * globalAnim), 7.0f * S);
        Object gridTitle = this.searching() ? "\u041f\u043e\u0438\u0441\u043a: " + searchQuery.trim() : this.activeCosmetic.getName();
        draw.draw((String)gridTitle, xRight + 10.0f * S, yTop + 8.0f * S, 7.5f * S, ColorUtil.getColor(230, globalAnim));
        CosmeticManager cosmetics = CosmeticManager.getInstance();
        List<CosmeticManager.CosmeticEntry> entries = this.cosmeticEntriesForDisplay();
        String slotType = this.activeSlotType();
        Integer equippedIndex = cosmetics.getSelectedByType().get(slotType);
        String equippedName = null;
        if (equippedIndex != null) {
            for (CosmeticManager.CosmeticEntry entry : cosmetics.getFilteredEntries(slotType)) {
                if (entry.index != equippedIndex) continue;
                equippedName = entry.name;
                break;
            }
        }
        boolean hasEquipped = equippedName != null;
        Object statusText = hasEquipped ? "\u041d\u0430\u0434\u0435\u0442\u043e: " + equippedName : "\u041d\u0435 \u043d\u0430\u0434\u0435\u0442\u043e";
        regular.draw((String)statusText, xRight + wRight - 10.0f * S - regular.getWidth((String)statusText, 6.5f * S), yTop + 9.0f * S, 6.5f * S, hasEquipped ? ColorUtil.replAlpha(ColorUtil.client(), globalAnim) : ColorUtil.getColor(140, 0.6f * globalAnim));
        RenderUtil.Render2D.rect(xRight + 8.0f * S, yTop + 20.0f * S, wRight - 16.0f * S, 0.5f * S, ColorUtil.getColor(255, 0.05f * globalAnim), 0.25f * S);
        float yGrid = yTop + 24.0f * S;
        if (this.activeGroup() != null) {
            yGrid = this.renderAccessoryGroups(xRight, yGrid, wRight, globalAnim, draw) + 4.0f * S;
        }
        if (this.activeCosmetic == CosmeticType.KNIVES) {
            yGrid = this.renderMeleeSettings(xRight, yGrid, wRight, globalAnim, draw) + 6.0f * S;
        }
        float hGrid = yTop + hArea - 4.0f * S - yGrid;
        Scissor.enable(xRight + 2.0f * S, yGrid, wRight - 4.0f * S, hGrid, 2.0f);
        this.cosmeticsScrollAnim += (this.cosmeticsScrollTarget - this.cosmeticsScrollAnim) * 0.2f;
        float pad = 8.0f * S;
        float gap = 8.0f * S;
        float cardW = (wRight - pad * 2.0f - gap) / 2.0f;
        float previewH = 62.0f * S;
        float btnH = 15.0f * S;
        float cardH = previewH + 34.0f * S;
        int totalItems = entries.size();
        this.cosmeticsItemButtons.clear();
        for (int i = 0; i < totalItems; ++i) {
            boolean isBtnHovered;
            CosmeticManager.CosmeticEntry entry = entries.get(i);
            int id = entry.index;
            int col = i % 2;
            int row = i / 2;
            float cardX = xRight + pad + (float)col * (cardW + gap);
            float cardY = yGrid + pad + (float)row * (cardH + gap) - this.cosmeticsScrollAnim;
            boolean onScreen = cardY + cardH >= yGrid && cardY <= yGrid + hGrid;
            boolean isEquipped = cosmetics.isEquipped(entry);
            boolean isCardHovered = onScreen && MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, cardX, cardY, cardW, cardH);
            float btnW = cardW - 10.0f * S;
            float btnX = cardX + 5.0f * S;
            float btnY = cardY + cardH - btnH - 5.0f * S;
            boolean bl = isBtnHovered = onScreen && MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, btnX, btnY, btnW, btnH);
            if (!onScreen) continue;
            this.cosmeticsItemButtons.add(new CosmeticClickTarget(id, cardX, cardY, cardW, cardH, btnX, btnY, btnW, btnH));
            RenderUtil.Render2D.glow(cardX, cardY, cardW, cardH, ColorUtil.getColor(0, 0.05f * globalAnim), 6.0f * S, 6.0f, 1.0f);
            RenderUtil.Render2D.rect(cardX, cardY, cardW, cardH, ColorUtil.getColor(28, 0.22f * globalAnim), 6.0f * S);
            RenderUtil.Render2D.outline(cardX, cardY, cardW, cardH, 0.5f * S, isEquipped ? ColorUtil.replAlpha(ColorUtil.client(), globalAnim * 0.85f) : ColorUtil.getColor(255, (isCardHovered ? 0.2f : 0.06f) * globalAnim), 6.0f * S);
            Object itemName = entry.name != null && !entry.name.isBlank() ? entry.name : this.activeCosmetic.getName() + " #" + id;
            float prevPad = 4.0f * S;
            float prevX = cardX + prevPad;
            float prevY = cardY + prevPad;
            float prevW = cardW - prevPad * 2.0f;
            RenderUtil.Render2D.rect(prevX, prevY, prevW, previewH, ColorUtil.getColor(12, 0.95f * globalAnim), 4.0f * S);
            float clipX = Math.max(prevX, xRight + 2.0f * S);
            float clipY = Math.max(prevY, yGrid);
            float clipX2 = Math.min(prevX + prevW, xRight + wRight - 2.0f * S);
            float clipY2 = Math.min(prevY + previewH, yGrid + hGrid);
            if (clipX2 > clipX && clipY2 > clipY) {
                CosmeticGuiPreview.queuePreview(entry, prevX, prevY, prevW, previewH, clipX, clipY, clipX2 - clipX, clipY2 - clipY);
            }
            RenderUtil.Render2D.outline(prevX, prevY, prevW, previewH, 0.5f * S, ColorUtil.getColor(40, 0.4f * globalAnim), 4.0f * S);
            float maxLabelW = cardW - 12.0f * S;
            Object displayLabel = itemName;
            if (draw.getWidth((String)displayLabel, 6.2f * S) > maxLabelW) {
                while (((String)displayLabel).length() > 3 && draw.getWidth((String)displayLabel + "...", 6.2f * S) > maxLabelW) {
                    displayLabel = ((String)displayLabel).substring(0, ((String)displayLabel).length() - 1);
                }
                displayLabel = (String)displayLabel + "...";
            }
            draw.draw((String)displayLabel, cardX + 6.0f * S, cardY + previewH + 6.5f * S, 6.2f * S, ColorUtil.getColor(220, globalAnim));
            if (isEquipped) {
                RenderUtil.Render2D.glow(btnX, btnY, btnW, btnH, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * 0.2f), 4.0f * S, 5.0f, 1.0f);
                RenderUtil.Render2D.rect(btnX, btnY, btnW, btnH, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * (isBtnHovered ? 0.4f : 0.28f)), 4.0f * S);
                RenderUtil.Render2D.outline(btnX, btnY, btnW, btnH, 0.5f * S, ColorUtil.replAlpha(ColorUtil.client(), globalAnim * 0.85f), 4.0f * S);
                draw.drawCentered("\u041d\u0430\u0434\u0435\u0442\u043e", btnX + btnW / 2.0f, btnY + 4.0f * S, 6.0f * S, ColorUtil.getColor(255, globalAnim));
                continue;
            }
            int btnBg = ColorUtil.overCol(ColorUtil.getColor(42, 0.2f * globalAnim), ColorUtil.getColor(65, 0.35f * globalAnim), isBtnHovered ? 1.0f : 0.0f);
            RenderUtil.Render2D.rect(btnX, btnY, btnW, btnH, btnBg, 4.0f * S);
            RenderUtil.Render2D.outline(btnX, btnY, btnW, btnH, 0.5f * S, isBtnHovered ? ColorUtil.replAlpha(ColorUtil.client(), globalAnim * 0.4f) : ColorUtil.getColor(70, 0.15f * globalAnim), 4.0f * S);
            draw.drawCentered("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c", btnX + btnW / 2.0f, btnY + 4.0f * S, 6.0f * S, ColorUtil.getColor(190, globalAnim * (isBtnHovered ? 1.0f : 0.7f)));
        }
        int totalRows = (totalItems + 1) / 2;
        float totalContentH = pad * 2.0f + (float)totalRows * (cardH + gap);
        this.cosmeticsMaxScroll = Math.max(0.0f, totalContentH - hGrid);
        if (this.cosmeticsScrollTarget > this.cosmeticsMaxScroll) {
            this.cosmeticsScrollTarget = this.cosmeticsMaxScroll;
        }
        if (entries.isEmpty() && this.searching()) {
            regular.drawCentered("\u041d\u0438\u0447\u0435\u0433\u043e \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u043e", xRight + wRight / 2.0f, yGrid + hGrid / 2.0f - 4.0f * S, 7.0f * S, ColorUtil.getColor(160, 0.7f * globalAnim));
        }
        Scissor.reset();
    }

    private float renderMeleeSettings(float xRight, float yTop, float wRight, float globalAnim, Font draw) {
        float pad = 8.0f * S;
        float gap = 6.0f * S;
        float btnH = 18.0f * S;
        float total = wRight - pad * 2.0f;
        float bw = (total - gap * 2.0f) / 3.0f;
        float x0 = xRight + pad;
        MeleeSettings settings = MeleeSettings.get();
        this.renderMeleeToggle(x0, yTop, bw, btnH, "1-\u0435 \u043b\u0438\u0446\u043e", settings.isFirstPerson(), false, globalAnim, draw, this.meleeFirstRect);
        this.renderMeleeToggle(x0 + bw + gap, yTop, bw, btnH, "3-\u0435 \u043b\u0438\u0446\u043e", settings.isThirdPerson(), false, globalAnim, draw, this.meleeThirdRect);
        this.renderMeleeToggle(x0 + 2.0f * (bw + gap), yTop, bw, btnH, "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f: " + Menu.keyName(settings.getInspectKey()), false, this.meleeInspectBinding, globalAnim, draw, this.meleeKeyRect);
        return yTop + btnH;
    }

    private void renderMeleeToggle(float x, float y, float w, float h, String label, boolean on, boolean highlight, float globalAnim, Font draw, float[] rect) {
        rect[0] = x;
        rect[1] = y;
        rect[2] = w;
        rect[3] = h;
        boolean hover = MathUtil.isHovered((float)this.lastMouseX, (float)this.lastMouseY, x, y, w, h);
        boolean active = on || highlight;
        RenderUtil.Render2D.glow(x, y, w, h, ColorUtil.getColor(0, 0.04f * globalAnim), 5.0f * S, 6.0f, 1.0f);
        int bg = active ? ColorUtil.replAlpha(ColorUtil.client(), globalAnim * (hover ? 0.4f : 0.28f)) : ColorUtil.overCol(ColorUtil.getColor(35, 0.15f * globalAnim), ColorUtil.getColor(55, 0.3f * globalAnim), hover ? 1.0f : 0.0f);
        RenderUtil.Render2D.rect(x, y, w, h, bg, 5.0f * S);
        RenderUtil.Render2D.outline(x, y, w, h, 0.5f * S, active ? ColorUtil.replAlpha(ColorUtil.client(), globalAnim * 0.85f) : ColorUtil.getColor(255, (hover ? 0.2f : 0.06f) * globalAnim), 5.0f * S);
        draw.drawCentered(label, x + w / 2.0f, y + h / 2.0f - 3.0f * S, 6.0f * S, ColorUtil.getColor(active ? 255 : 190, globalAnim));
    }

    private static String keyName(int key) {
        if (key <= 0) {
            return "\u0432\u0402\u201d";
        }
        String name = GLFW.glfwGetKeyName((int)key, (int)0);
        if (name != null && !name.isBlank()) {
            return name.toUpperCase();
        }
        switch (key) {
            case 32: {
                return "SPACE";
            }
            case 256: {
                return "ESC";
            }
            case 257: {
                return "ENTER";
            }
            case 258: {
                return "TAB";
            }
            case 259: {
                return "BACKSPACE";
            }
            case 340: {
                return "LSHIFT";
            }
            case 344: {
                return "RSHIFT";
            }
            case 341: {
                return "LCTRL";
            }
            case 345: {
                return "RCTRL";
            }
            case 342: {
                return "LALT";
            }
            case 346: {
                return "RALT";
            }
        }
        return "KEY " + key;
    }

    private CosmeticManager.CosmeticEntry findCosmeticEntry(int index) {
        for (CosmeticManager.CosmeticEntry entry : this.cosmeticEntriesForDisplay()) {
            if (entry.index != index) continue;
            return entry;
        }
        return null;
    }

    public boolean method_25402(class_11909 click, boolean doubled) {
        float mouseX = (int)(click.comp_4798() / (double)this.scaleFix);
        float mouseY = (int)(click.comp_4799() / (double)this.scaleFix);
        OverlayEditor editor = OverlayEditors.active();
        if (editor != null) {
            return editor.mouseClicked(mouseX, mouseY, click.method_74245());
        }
        if (this.bindingModule != null) {
            this.bindingModule = null;
            GuiSounds.bindReset();
            return true;
        }
        if (this.activeBind != null) {
            if (this.activeBind.allowMouse && click.method_74245() != 0 && click.method_74245() != 1) {
                this.activeBind.set(click.method_74245());
                GuiSounds.bindSet();
            } else {
                GuiSounds.bindReset();
            }
            this.activeBind = null;
            return true;
        }
        if (this.activeString != null) {
            this.activeString.set(this.stringBuffer);
            this.activeString = null;
            GuiSounds.editCommit();
        }
        int screenWidth = (int)((float)mc.method_22683().method_4486() / this.scaleFix);
        int screenHeight = (int)((float)mc.method_22683().method_4502() / this.scaleFix);
        float w = 420.0f * S;
        float h = 280.0f * S;
        float x = (float)screenWidth / 2.0f - w / 2.0f;
        float y = (float)screenHeight / 2.0f - h / 2.0f;
        float ht = 20.0f * S;
        float dotsW = (float)((themes.length - 1) * 14) * S + 8.0f * S;
        float xtd = x + (w - dotsW) / 2.0f;
        float yt = y + h + 5.0f * S;
        float xps = x + 6.0f * S + 36.0f * S;
        float yps = y + 6.0f * S;
        if (MathUtil.isHovered(mouseX, mouseY, xps, yps, 140.0f * S, 20.0f * S)) {
            if (!this.isNoSearchCategory()) {
                if (click.method_74245() == 1) {
                    this.clearSearch();
                    searchActive = false;
                    GuiSounds.searchClear();
                } else if (click.method_74245() == 0) {
                    if (!searchActive) {
                        GuiSounds.editStart();
                    }
                    searchActive = true;
                    this.searchTypeTime = System.currentTimeMillis();
                }
            }
            return true;
        }
        searchActive = false;
        float ytd = yt + 6.0f * S;
        int themeIndex = 0;
        for (Theme theme : themes) {
            if (MathUtil.isHovered(mouseX, mouseY, xtd, ytd, 8.0f * S, 8.0f * S) && click.method_74245() == 0) {
                animation14.reset();
                preSelectedTheme = selectedTheme;
                selectedTheme = theme;
                Client.get().guiManager().setGuiTheme(theme);
                GuiSounds.theme(themeIndex, themes.length);
            }
            xtd += 14.0f * S;
            ++themeIndex;
        }
        float xPanelMini = x + 6.0f * S;
        float yPanelMini = y + 6.0f * S;
        float csgddd = 0.0f;
        for (Category category : Category.values()) {
            csgddd += 20.0f * S;
        }
        float f = yPanelMini + 28.0f * S;
        int catIndex = 0;
        for (Category category : Category.values()) {
            if (MathUtil.isHovered(mouseX, mouseY, xPanelMini + 30.0f * S / 2.0f - 16.0f * S / 2.0f, f, 16.0f * S, 16.0f * S) && click.method_74245() == 0 && (this.active != category || this.searching())) {
                this.clearSearch();
                if (this.active != category) {
                    this.active = category;
                    this.select = null;
                    this.settingScrollTarget = 0.0f;
                    this.settingScrollAnim = 0.0f;
                    this.configInputFocused = false;
                    this.markerInputFocused = -1;
                    this.cosmeticsScrollTarget = 0.0f;
                    this.cosmeticsScrollAnim = 0.0f;
                    this.cosmeticsTabScrollTarget = 0.0f;
                    this.cosmeticsTabScrollAnim = 0.0f;
                    animCategoryReset.reset();
                }
                this.scrollTarget = 0.0f;
                GuiSounds.category(catIndex, Category.values().length);
            }
            f += 20.0f * S;
            ++catIndex;
        }
        if (this.isSpecialCategory() && click.method_74245() == 0) {
            if (this.isCosmeticsCategory()) {
                float xLeft = x + 6.0f * S + 36.0f * S;
                float yTop = y + 6.0f * S + 26.0f * S;
                float wLeft = 140.0f * S;
                float subTabH = 18.0f * S;
                float gap = 2.0f * S;
                float pad = 6.0f * S;
                float tabW = wLeft - pad * 2.0f;
                float tabX = xLeft + pad;
                float listTop = yTop + 20.0f * S;
                float listBottom = yTop + (h - 12.0f * S - 26.0f * S) - pad;
                float viewH = Math.max(0.0f, listBottom - listTop);
                float ySubTab = listTop - this.cosmeticsTabScroll;
                if (this.activeCosmetic == CosmeticType.KNIVES) {
                    if (MathUtil.isHovered(mouseX, mouseY, this.meleeFirstRect[0], this.meleeFirstRect[1], this.meleeFirstRect[2], this.meleeFirstRect[3])) {
                        MeleeSettings.get().toggleFirstPerson();
                        GuiSounds.bindSet();
                        return true;
                    }
                    if (MathUtil.isHovered(mouseX, mouseY, this.meleeThirdRect[0], this.meleeThirdRect[1], this.meleeThirdRect[2], this.meleeThirdRect[3])) {
                        MeleeSettings.get().toggleThirdPerson();
                        GuiSounds.bindSet();
                        return true;
                    }
                    if (MathUtil.isHovered(mouseX, mouseY, this.meleeKeyRect[0], this.meleeKeyRect[1], this.meleeKeyRect[2], this.meleeKeyRect[3])) {
                        this.meleeInspectBinding = true;
                        GuiSounds.bindStart();
                        return true;
                    }
                }
                if (Menu.groupsFor(this.activeCosmetic) != null) {
                    String[][] groups = Menu.groupsFor(this.activeCosmetic);
                    for (int i = 0; groups != null && i < this.accessoryTabRects.size() && i < groups.length; ++i) {
                        float[] r = this.accessoryTabRects.get(i);
                        if (!MathUtil.isHovered(mouseX, mouseY, r[0], r[1], r[2], r[3])) continue;
                        if (!((String)groups[i][0]).equals(this.activeGroup())) {
                            this.setActiveGroup((String)groups[i][0]);
                            this.cosmeticsScrollTarget = 0.0f;
                            this.cosmeticsScrollAnim = 0.0f;
                            GuiSounds.category(i + 1, groups.length + 1);
                        }
                        return true;
                    }
                }
                for (CosmeticType cat : CosmeticType.values()) {
                    boolean inView;
                    boolean bl = inView = ySubTab + subTabH > listTop && ySubTab < listTop + viewH;
                    if (inView && MathUtil.isHovered(mouseX, mouseY, tabX, ySubTab, tabW, subTabH)) {
                        if (this.activeCosmetic != cat) {
                            this.activeCosmetic = cat;
                            this.meleeInspectBinding = false;
                            this.cosmeticsScrollTarget = 0.0f;
                            this.cosmeticsScrollAnim = 0.0f;
                            if (cat == CosmeticType.KNIVES) {
                                MeleeRegistry.preloadAll();
                            }
                            GuiSounds.category(cat.ordinal(), CosmeticType.values().length);
                        }
                        return true;
                    }
                    ySubTab += subTabH + gap;
                }
                for (CosmeticClickTarget target : this.cosmeticsItemButtons) {
                    if (!MathUtil.isHovered(mouseX, mouseY, target.cardX, target.cardY, target.cardW, target.cardH) && !MathUtil.isHovered(mouseX, mouseY, target.btnX, target.btnY, target.btnW, target.btnH)) continue;
                    CosmeticManager.CosmeticEntry entry = this.findCosmeticEntry(target.id);
                    if (entry != null) {
                        CosmeticManager.getInstance().toggle(entry);
                        if (CosmeticManager.getInstance().isEquipped(entry)) {
                            GuiSounds.bindSet();
                        } else {
                            GuiSounds.bindReset();
                        }
                    }
                    return true;
                }
                return true;
            }
            if (this.isConfigCategory()) {
                int i;
                if (MathUtil.isHovered(mouseX, mouseY, this.cfgInputX, this.cfgInputY, this.cfgInputW, this.cfgInputH)) {
                    this.configInputFocused = true;
                    return true;
                }
                if (MathUtil.isHovered(mouseX, mouseY, this.cfgAddX, this.cfgAddY, this.cfgAddW, this.cfgAddH)) {
                    if (!this.configInput.trim().isEmpty()) {
                        Client.get().configManager().save(this.configInput.trim());
                        this.configInput = "";
                        this.configInputFocused = false;
                        GuiSounds.bindSet();
                    }
                    return true;
                }
                for (i = 0; i < this.configDelRects.size(); ++i) {
                    float[] r = this.configDelRects.get(i);
                    if (!MathUtil.isHovered(mouseX, mouseY, r[0], r[1], r[2], r[3])) continue;
                    Client.get().configManager().delete(this.configCache.get(i));
                    GuiSounds.bindReset();
                    return true;
                }
                for (i = 0; i < this.configRowRects.size(); ++i) {
                    float[] r = this.configRowRects.get(i);
                    if (!MathUtil.isHovered(mouseX, mouseY, r[0], r[1], r[2], r[3])) continue;
                    Client.get().configManager().load(this.configCache.get(i));
                    GuiSounds.bindSet();
                    return true;
                }
                this.configInputFocused = false;
            } else {
                int i;
                int[] focusMap = new int[]{0, 1, 2, 3};
                float[][] inputs = new float[][]{{this.mkNameX, this.mkNameY, this.mkNameW, this.mkNameH}, {this.mkXInputX, this.mkXInputY, this.mkXInputW, this.mkXInputH}, {this.mkYInputX, this.mkYInputY, this.mkYInputW, this.mkYInputH}, {this.mkZInputX, this.mkZInputY, this.mkZInputW, this.mkZInputH}};
                boolean hit = false;
                for (i = 0; i < 4; ++i) {
                    if (!MathUtil.isHovered(mouseX, mouseY, inputs[i][0], inputs[i][1], inputs[i][2], inputs[i][3])) continue;
                    this.markerInputFocused = i;
                    hit = true;
                    break;
                }
                if (!hit) {
                    this.markerInputFocused = -1;
                }
                if (MathUtil.isHovered(mouseX, mouseY, this.mkAddX, this.mkAddY, this.mkAddW, this.mkAddH)) {
                    String name = this.markerNameInput.trim();
                    if (!name.isEmpty()) {
                        double my2;
                        double mx2;
                        double d = this.markerXInput.isEmpty() ? (Menu.mc.field_1724 != null ? Menu.mc.field_1724.method_23317() : 0.0) : (mx2 = Double.parseDouble(this.markerXInput));
                        double d2 = this.markerYInput.isEmpty() ? (Menu.mc.field_1724 != null ? Menu.mc.field_1724.method_23318() : 0.0) : (my2 = Double.parseDouble(this.markerYInput));
                        double mz2 = this.markerZInput.isEmpty() ? (Menu.mc.field_1724 != null ? Menu.mc.field_1724.method_23321() : 0.0) : Double.parseDouble(this.markerZInput);
                        WayCommand wc = WayCommand.get();
                        if (wc != null) {
                            wc.addWaypoint(name, mx2, my2, mz2);
                        }
                        this.markerNameInput = "";
                        this.markerXInput = "";
                        this.markerYInput = "";
                        this.markerZInput = "";
                        this.markerInputFocused = -1;
                        GuiSounds.bindSet();
                    }
                    return true;
                }
                for (i = 0; i < this.markerDelRects.size(); ++i) {
                    float[] r = this.markerDelRects.get(i);
                    if (!MathUtil.isHovered(mouseX, mouseY, r[0], r[1], r[2], r[3])) continue;
                    WayCommand wc = WayCommand.get();
                    if (wc != null) {
                        wc.removeWaypoint(this.markerCache.get(i)[0]);
                    }
                    GuiSounds.bindReset();
                    return true;
                }
            }
            return true;
        }
        Font draw = Fonts.sf_regular;
        float xModule = x + 6.0f * S + 36.0f * S + 5.0f * S;
        float yModule = y + 6.0f * S + 30.0f * S - this.scrollAnim;
        boolean insidePanel = MathUtil.isHovered(mouseX, mouseY, x + 6.0f * S + 36.0f * S, y + 6.0f * S + 26.0f * S, 140.0f * S, h - 12.0f * S - 26.0f * S);
        for (Module f2 : Client.get().moduleManager().values()) {
            float canim1 = f2.getAnimation14().getOutput();
            if (!(canim1 > 0.0f)) continue;
            float descH = this.descHeight(draw, f2.getDesc());
            float moduleH = Math.max(20.0f * S, 16.0f * S + descH + 4.0f * S);
            if (insidePanel && canim1 > 0.5f && MathUtil.isHovered(mouseX, mouseY, xModule, yModule, 130.0f * S, moduleH)) {
                String keyName = this.bindingModule == f2 ? "..." : (f2.getKey() == -1 ? "n/a" : Keyboard.keyName(f2.getKey()).replace("NONE", "n/a"));
                float keyW = draw.getWidth(keyName, 6.0f * S) + 9.0f * S;
                float keyX = xModule + 130.0f * S - 14.0f * S - 5.0f * S - 5.0f * S - keyW;
                if (MathUtil.isHovered(mouseX, mouseY, keyX - 2.0f * S, yModule + 2.0f * S, keyW + 4.0f * S, 14.0f * S) && click.method_74245() == 0) {
                    this.bindingModule = f2;
                    GuiSounds.bindStart();
                    return true;
                }
                if (click.method_74245() == 2) {
                    this.bindingModule = f2;
                    GuiSounds.bindStart();
                    return true;
                }
                if (click.method_74245() == 0) {
                    f2.setEnabled(!f2.isEnabled());
                }
                if (click.method_74245() == 1) {
                    this.select = f2 == this.select ? null : f2;
                    this.settingScrollTarget = 0.0f;
                    this.settingScrollAnim = 0.0f;
                    GuiSounds.expand(this.select == f2);
                }
            }
            yModule += (moduleH + 5.0f * S) * canim1;
        }
        float xSetting = x + 6.0f * S + 36.0f * S + 140.0f * S + 6.0f * S;
        float ySetting = y + 6.0f * S + 26.0f * S;
        float wSetting = w - (6.0f * S + 36.0f * S + 6.0f * S + 140.0f * S + 6.0f * S);
        float hSetting = h - 12.0f * S - 26.0f * S;
        if (MathUtil.isHovered(mouseX, mouseY, xSetting, ySetting, wSetting, hSetting) && this.select != null) {
            float xST = xSetting + 10.0f * S;
            float yST = ySetting + 10.0f * S - this.settingScrollAnim;
            float wST = wSetting - 20.0f * S;
            for (Setting<?> setting : this.select.getSettings()) {
                float tw;
                Setting s;
                float vis;
                if (setting instanceof SliderSetting && (vis = this.visAnim(this.select, s = (SliderSetting)setting)) > 0.01f) {
                    if (s.getVisible().get().booleanValue() && click.method_74245() == 0 && MathUtil.isHovered(mouseX, mouseY, xST + 3.0f * S, yST + 11.0f * S, wST - 6.0f * S, 13.0f * S)) {
                        this.draggingSlider = s;
                        GuiSounds.sliderGrab();
                    }
                    yST += 30.0f * S * vis;
                }
                if (setting instanceof ModeSetting && (vis = this.visAnim(this.select, s = (ModeSetting)setting)) > 0.01f) {
                    if (s.getVisible().get().booleanValue() && click.method_74245() == 0) {
                        if (MathUtil.isHovered(mouseX, mouseY, xST, yST, wST, 15.0f * S)) {
                            if (this.expandedModes.contains(s.getName())) {
                                this.expandedModes.remove(s.getName());
                            } else {
                                this.expandedModes.add(s.getName());
                            }
                        } else if (this.expandedModes.contains(s.getName())) {
                            float chipMaxW = wST - 12.0f * S;
                            float px = 0.0f;
                            float py = 0.0f;
                            int chipIndex = 0;
                            for (String val : ((ModeSetting)s).values) {
                                tw = draw.getWidth(val, 6.0f * S) + 8.0f * S;
                                if (px + tw > chipMaxW && px > 0.0f) {
                                    px = 0.0f;
                                    py += 12.0f * S;
                                }
                                if (MathUtil.isHovered(mouseX, mouseY, xST + 6.0f * S + px, yST + 15.0f * S + py, tw, 10.0f * S)) {
                                    if (!val.equals(((ModeSetting)s).getValue())) {
                                        GuiSounds.chip(chipIndex, ((ModeSetting)s).values.size());
                                    }
                                    ((ModeSetting)s).set(val);
                                }
                                px += tw + 3.0f * S;
                                ++chipIndex;
                            }
                        }
                    }
                    boolean expanded = this.expandedModes.contains(s.getName());
                    ru.decide.utils.animation.satoshi.Animation ea = this.expandAnim(this.select.getName() + ":" + s.getName());
                    ea.setDirection(expanded ? Direction.FORWARDS : Direction.BACKWARDS);
                    float expandT = ea.getOutput();
                    float chipsH = this.modeChipsHeight((ModeSetting)s, wST - 12.0f * S) + 4.0f * S;
                    float hMode = 15.0f * S + chipsH * expandT;
                    yST += (hMode + 5.0f * S) * vis;
                }
                if (setting instanceof MultiBooleanSetting && (vis = this.visAnim(this.select, s = (MultiBooleanSetting)setting)) > 0.01f) {
                    float hMulti = 15.0f * S + this.multiChipsHeight((MultiBooleanSetting)s, wST - 12.0f * S) + 4.0f * S;
                    if (s.getVisible().get().booleanValue() && click.method_74245() == 0) {
                        float chipMaxW = wST - 12.0f * S;
                        float px = 0.0f;
                        float py = 0.0f;
                        for (BooleanSetting b : ((MultiBooleanSetting)s).getValues()) {
                            tw = draw.getWidth(b.getName(), 6.0f * S) + 8.0f * S;
                            if (px + tw > chipMaxW && px > 0.0f) {
                                px = 0.0f;
                                py += 12.0f * S;
                            }
                            if (MathUtil.isHovered(mouseX, mouseY, xST + 6.0f * S + px, yST + 15.0f * S + py, tw, 10.0f * S)) {
                                b.set(b.getValue() == false);
                                GuiSounds.chipMulti(b.getValue());
                            }
                            px += tw + 3.0f * S;
                        }
                    }
                    yST += (hMulti + 5.0f * S) * vis;
                }
                if (setting instanceof BooleanSetting && (vis = this.visAnim(this.select, s = (BooleanSetting)setting)) > 0.01f) {
                    if (s.getVisible().get().booleanValue() && click.method_74245() == 0 && MathUtil.isHovered(mouseX, mouseY, xST, yST, wST, 16.0f * S)) {
                        ((BooleanSetting)s).set(((BooleanSetting)s).getValue() == false);
                        GuiSounds.toggle(((BooleanSetting)s).getValue());
                    }
                    yST += 21.0f * S * vis;
                }
                if (setting instanceof ButtonSetting && (vis = this.visAnim(this.select, s = (ButtonSetting)setting)) > 0.01f) {
                    if (s.getVisible().get().booleanValue() && click.method_74245() == 0 && MathUtil.isHovered(mouseX, mouseY, xST, yST, wST, 16.0f * S)) {
                        ((ButtonSetting)s).press();
                        GuiSounds.button();
                    }
                    yST += 21.0f * S * vis;
                }
                if (setting instanceof BindSetting && (vis = this.visAnim(this.select, s = (BindSetting)setting)) > 0.01f) {
                    String keyName;
                    float bw;
                    if (s.getVisible().get().booleanValue() && click.method_74245() == 0 && MathUtil.isHovered(mouseX, mouseY, xST + wST - 6.0f * S - (bw = draw.getWidth(keyName = ((BindSetting)s).get() == -1 ? "n/a" : Keyboard.keyName(((BindSetting)s).get()), 6.0f * S) + 10.0f * S) - 2.0f * S, yST + 1.0f * S, bw + 4.0f * S, 14.0f * S)) {
                        this.activeBind = s;
                        GuiSounds.bindStart();
                    }
                    yST += 21.0f * S * vis;
                }
                if (setting instanceof StringSetting && (vis = this.visAnim(this.select, s = (StringSetting)setting)) > 0.01f) {
                    String shown;
                    float bw;
                    if (s.getVisible().get().booleanValue() && click.method_74245() == 0 && MathUtil.isHovered(mouseX, mouseY, xST + wST - 6.0f * S - (bw = Math.min(draw.getWidth(shown = ((StringSetting)s).getValue().isEmpty() ? "..." : ((StringSetting)s).getValue(), 6.0f * S) + 10.0f * S, wST * 0.55f)) - 2.0f * S, yST + 1.0f * S, bw + 4.0f * S, 14.0f * S)) {
                        this.activeString = s;
                        this.stringBuffer = ((StringSetting)s).getValue();
                        GuiSounds.editStart();
                    }
                    yST += 21.0f * S * vis;
                }
                if (!(setting instanceof ColorSetting) || !((vis = this.visAnim(this.select, s = (ColorSetting)setting)) > 0.01f)) continue;
                float open = ((ColorSetting)s).pickerAnim.getOutput();
                float hCol = 16.0f * S + open * 35.0f * S;
                if (s.getVisible().get().booleanValue() && click.method_74245() == 0) {
                    if (MathUtil.isHovered(mouseX, mouseY, xST, yST, wST, 16.0f * S)) {
                        ((ColorSetting)s).pickerOpen = !((ColorSetting)s).pickerOpen;
                        GuiSounds.picker(((ColorSetting)s).pickerOpen);
                    } else if (((ColorSetting)s).pickerOpen) {
                        for (int bar = 0; bar < 3; ++bar) {
                            float by = yST + 18.0f * S + (float)(bar * 12) * S;
                            if (!MathUtil.isHovered(mouseX, mouseY, xST + 6.0f * S, by - 3.0f * S, wST - 12.0f * S, 10.0f * S)) continue;
                            this.draggingColor = s;
                            this.draggingColorBar = bar;
                            GuiSounds.sliderGrab();
                        }
                    }
                }
                yST += (hCol + 5.0f * S) * vis;
            }
        }
        return super.method_25402(click, doubled);
    }

    public boolean method_25403(class_11909 click, double deltaX, double deltaY) {
        OverlayEditor editor = OverlayEditors.active();
        if (editor != null) {
            return editor.mouseDragged((float)(deltaX / (double)this.scaleFix), (float)(deltaY / (double)this.scaleFix));
        }
        return super.method_25403(click, deltaX, deltaY);
    }

    public boolean method_25406(class_11909 click) {
        OverlayEditor editor = OverlayEditors.active();
        if (editor != null) {
            return editor.mouseReleased(click.method_74245());
        }
        if (this.draggingSlider != null || this.draggingColor != null) {
            GuiSounds.sliderRelease();
        }
        this.draggingSlider = null;
        this.draggingColor = null;
        return super.method_25406(click);
    }

    public boolean method_25404(class_11908 input) {
        int key = input.comp_4795();
        OverlayEditor editor = OverlayEditors.active();
        if (editor != null) {
            if (key == 256) {
                editor.saveAndExit();
            }
            return true;
        }
        if (this.bindingModule != null) {
            boolean reset = key == 256 || key == 261;
            this.bindingModule.setKey(reset ? -1 : key);
            this.bindingModule = null;
            if (reset) {
                GuiSounds.bindReset();
            } else {
                GuiSounds.bindSet();
            }
            return true;
        }
        if (this.activeBind != null) {
            boolean reset = key == 256 || key == 261;
            this.activeBind.set(reset ? -1 : key);
            this.activeBind = null;
            if (reset) {
                GuiSounds.bindReset();
            } else {
                GuiSounds.bindSet();
            }
            return true;
        }
        if (this.meleeInspectBinding) {
            boolean reset;
            boolean bl = reset = key == 256 || key == 261;
            if (!reset) {
                MeleeSettings.get().setInspectKey(key);
            }
            this.meleeInspectBinding = false;
            if (reset) {
                GuiSounds.bindReset();
            } else {
                GuiSounds.bindSet();
            }
            return true;
        }
        if (searchActive) {
            if (this.isNoSearchCategory()) {
                searchActive = false;
            } else if (ClipboardPaste.pasteCombo(key)) {
                searchQuery = ClipboardPaste.append(searchQuery, 24);
                this.searchChanged();
                GuiSounds.type();
            } else if (key == 256) {
                if (!searchQuery.isEmpty()) {
                    this.clearSearch();
                } else {
                    searchActive = false;
                }
                GuiSounds.searchClear();
            } else if (key == 257 || key == 335) {
                searchActive = false;
                GuiSounds.editCommit();
            } else if (key == 259 && !searchQuery.isEmpty()) {
                searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
                this.searchChanged();
                GuiSounds.erase();
            }
            return true;
        }
        if (this.activeString != null) {
            if (ClipboardPaste.pasteCombo(key)) {
                this.stringBuffer = ClipboardPaste.append(this.stringBuffer, 64);
                GuiSounds.type();
            } else if (key == 257 || key == 335) {
                this.activeString.set(this.stringBuffer);
                this.activeString = null;
                GuiSounds.editCommit();
            } else if (key == 256) {
                this.activeString = null;
                GuiSounds.editCancel();
            } else if (key == 259 && !this.stringBuffer.isEmpty()) {
                this.stringBuffer = this.stringBuffer.substring(0, this.stringBuffer.length() - 1);
                GuiSounds.erase();
            }
            return true;
        }
        if (this.isSpecialCategory() && (this.configInputFocused || this.markerInputFocused >= 0)) {
            if (ClipboardPaste.pasteCombo(key)) {
                if (this.configInputFocused) {
                    this.configInput = ClipboardPaste.append(this.configInput, 24);
                } else if (this.markerInputFocused == 0) {
                    this.markerNameInput = ClipboardPaste.append(this.markerNameInput, 24);
                } else if (this.markerInputFocused == 1) {
                    this.markerXInput = ClipboardPaste.append(this.markerXInput, 10);
                } else if (this.markerInputFocused == 2) {
                    this.markerYInput = ClipboardPaste.append(this.markerYInput, 10);
                } else {
                    this.markerZInput = ClipboardPaste.append(this.markerZInput, 10);
                }
                GuiSounds.type();
            }
            if (key == 256 || key == 257 || key == 335) {
                this.configInputFocused = false;
                this.markerInputFocused = -1;
                return true;
            }
            if (key == 259) {
                if (this.configInputFocused && !this.configInput.isEmpty()) {
                    this.configInput = this.configInput.substring(0, this.configInput.length() - 1);
                } else if (this.markerInputFocused == 0 && !this.markerNameInput.isEmpty()) {
                    this.markerNameInput = this.markerNameInput.substring(0, this.markerNameInput.length() - 1);
                } else if (this.markerInputFocused == 1 && !this.markerXInput.isEmpty()) {
                    this.markerXInput = this.markerXInput.substring(0, this.markerXInput.length() - 1);
                } else if (this.markerInputFocused == 2 && !this.markerYInput.isEmpty()) {
                    this.markerYInput = this.markerYInput.substring(0, this.markerYInput.length() - 1);
                } else if (this.markerInputFocused == 3 && !this.markerZInput.isEmpty()) {
                    this.markerZInput = this.markerZInput.substring(0, this.markerZInput.length() - 1);
                }
                GuiSounds.erase();
                return true;
            }
            return true;
        }
        return super.method_25404(input);
    }

    public boolean method_25400(class_11905 input) {
        if (searchActive) {
            if (!this.isNoSearchCategory() && input.method_74227() && searchQuery.length() < 24) {
                searchQuery = searchQuery + input.method_74226();
                this.searchChanged();
                GuiSounds.type();
            }
            return true;
        }
        if (this.activeString != null) {
            if (input.method_74227()) {
                String str = input.method_74226();
                if (!this.activeString.isOnlyNumber() || str.matches("[0-9.,-]+")) {
                    this.stringBuffer = this.stringBuffer + str;
                    GuiSounds.type();
                }
            }
            return true;
        }
        if (this.isSpecialCategory()) {
            if (this.configInputFocused && input.method_74227() && this.configInput.length() < 24) {
                String s = input.method_74226();
                for (int i = 0; i < s.length(); ++i) {
                    char c = s.charAt(i);
                    if (!Character.isLetterOrDigit(c) && c != '_' && c != '-' && c != ' ') continue;
                    this.configInput = this.configInput + c;
                }
                GuiSounds.type();
                return true;
            }
            if (this.markerInputFocused >= 0 && input.method_74227()) {
                String current;
                int maxLen;
                String s = input.method_74226();
                int n = maxLen = this.markerInputFocused == 0 ? 24 : 10;
                String string = this.markerInputFocused == 0 ? this.markerNameInput : (this.markerInputFocused == 1 ? this.markerXInput : (current = this.markerInputFocused == 2 ? this.markerYInput : this.markerZInput));
                if (current.length() < maxLen) {
                    for (int i = 0; i < s.length(); ++i) {
                        boolean ok;
                        char c = s.charAt(i);
                        boolean bl = this.markerInputFocused == 0 ? Character.isLetterOrDigit(c) || c == '_' || c == ' ' : (ok = Character.isDigit(c) || c == '-' || c == '.');
                        if (!ok) continue;
                        if (this.markerInputFocused == 0) {
                            this.markerNameInput = this.markerNameInput + c;
                            continue;
                        }
                        if (this.markerInputFocused == 1) {
                            this.markerXInput = this.markerXInput + c;
                            continue;
                        }
                        if (this.markerInputFocused == 2) {
                            this.markerYInput = this.markerYInput + c;
                            continue;
                        }
                        this.markerZInput = this.markerZInput + c;
                    }
                    GuiSounds.type();
                }
                return true;
            }
        }
        return super.method_25400(input);
    }

    public boolean method_25401(double mouseXRaw, double mouseYRaw, double horizontalAmount, double verticalAmount) {
        float h;
        int screenHeight;
        float y;
        float w;
        float mouseX = (float)(mouseXRaw / (double)this.scaleFix);
        float mouseY = (float)(mouseYRaw / (double)this.scaleFix);
        OverlayEditor editor = OverlayEditors.active();
        if (editor != null) {
            return editor.mouseScrolled(mouseX, mouseY, verticalAmount);
        }
        int screenWidth = (int)((float)mc.method_22683().method_4486() / this.scaleFix);
        float x = (float)screenWidth / 2.0f - (w = 420.0f * S) / 2.0f;
        if (MathUtil.isHovered(mouseX, mouseY, x + 6.0f * S + 36.0f * S, (y = (float)(screenHeight = (int)((float)mc.method_22683().method_4502() / this.scaleFix)) / 2.0f - (h = 280.0f * S) / 2.0f) + 6.0f * S + 26.0f * S, 140.0f * S, h - 12.0f * S - 26.0f * S)) {
            if (this.isCosmeticsCategory()) {
                float before = this.cosmeticsTabScrollTarget;
                this.cosmeticsTabScrollTarget = MathUtil.clamp((float)((double)this.cosmeticsTabScrollTarget - verticalAmount * 25.0 * (double)S), 0.0f, this.cosmeticsTabMaxScroll);
                if (this.cosmeticsTabScrollTarget != before) {
                    GuiSounds.scroll();
                }
                return true;
            }
            if (this.isSpecialCategory()) {
                return true;
            }
            float before = this.scrollTarget;
            this.scrollTarget = MathUtil.clamp((float)((double)this.scrollTarget - verticalAmount * 25.0 * (double)S), 0.0f, this.maxScroll);
            if (this.scrollTarget != before) {
                GuiSounds.scroll();
            }
            return true;
        }
        if (MathUtil.isHovered(mouseX, mouseY, x + 6.0f * S + 36.0f * S, y + 6.0f * S + 26.0f * S, w - (6.0f * S + 36.0f * S + 6.0f * S), h - 12.0f * S - 26.0f * S)) {
            if (this.isSpecialCategory()) {
                if (this.isCosmeticsCategory()) {
                    float before = this.cosmeticsScrollTarget;
                    this.cosmeticsScrollTarget = MathUtil.clamp((float)((double)this.cosmeticsScrollTarget - verticalAmount * 25.0 * (double)S), 0.0f, this.cosmeticsMaxScroll);
                    if (this.cosmeticsScrollTarget != before) {
                        GuiSounds.scroll();
                    }
                } else if (this.isConfigCategory()) {
                    float before = this.configScrollTarget;
                    this.configScrollTarget = MathUtil.clamp((float)((double)this.configScrollTarget - verticalAmount * 25.0 * (double)S), 0.0f, Float.MAX_VALUE);
                    if (this.configScrollTarget != before) {
                        GuiSounds.scroll();
                    }
                } else {
                    float before = this.markerScrollTarget;
                    this.markerScrollTarget = MathUtil.clamp((float)((double)this.markerScrollTarget - verticalAmount * 25.0 * (double)S), 0.0f, Float.MAX_VALUE);
                    if (this.markerScrollTarget != before) {
                        GuiSounds.scroll();
                    }
                }
                return true;
            }
            float before = this.settingScrollTarget;
            this.settingScrollTarget = MathUtil.clamp((float)((double)this.settingScrollTarget - verticalAmount * 25.0 * (double)S), 0.0f, this.settingMaxScroll);
            if (this.settingScrollTarget != before) {
                GuiSounds.scroll();
            }
            return true;
        }
        return super.method_25401(mouseXRaw, mouseYRaw, horizontalAmount, verticalAmount);
    }

    public boolean method_25421() {
        return false;
    }

    public boolean method_25422() {
        OverlayEditor editor = OverlayEditors.active();
        if (editor != null) {
            editor.saveAndExit();
            if (returnToGlassAfterEditor) {
                returnToGlassAfterEditor = false;
                this.method_25419();
                mc.method_1507((class_437)LvGui.INSTANCE);
            }
            return false;
        }
        if (!this.exit) {
            ClickGui clickGui = Client.get().moduleManager().get(ClickGui.class);
            boolean bl = this.use3DAnim = clickGui != null && clickGui.closeAnimation.is("3D");
            if (this.use3DAnim && GuiCloseAnimation3D.start()) {
                GuiSounds.close();
                return false;
            }
            if (clickGui != null && clickGui.closeAnimation.is("Shatter") && GuiCloseAnimationShatter.start()) {
                GuiSounds.close();
                GuiMusicPlayer.stop();
                return false;
            }
            this.glomalAnim.run(0.0, (double)0.3f, Easings.SINE_IN);
            this.exit = true;
            GuiSounds.close();
        }
        return false;
    }

    static {
        animation14 = new EaseInOutQuad(300, 1.0);
        animCategoryReset = new EaseInOutQuad(300, 1.0);
        animUserInfo = new EaseInOutQuad(300, 1.0);
        animation1 = new EaseInOutQuad(300, 1.0);
        animation2 = new EaseInOutQuad(300, 1.0);
        animation3 = new EaseInOutQuad(300, 1.0);
        animation4 = new EaseInOutQuad(300, 1.0);
        searchActive = false;
        searchQuery = "";
        ACCESSORY_GROUPS = new String[][]{{"watch", "\u0427\u0430\u0441\u044b"}, {"bracelet", "\u0411\u0440\u0430\u0441\u043b\u0435\u0442\u044b"}, {"neck", "\u0428\u0435\u044f"}};
    }

    @Environment(value=EnvType.CLIENT)
    private static final class GuiParticle {
        final float x;
        final float y;
        final float maxRadius;
        final float lifeMs;
        final float drift;
        final long born = System.currentTimeMillis();

        GuiParticle(float x, float y) {
            this.x = x;
            this.y = y;
            this.maxRadius = 10.0f + (float)Math.random() * 7.0f;
            this.lifeMs = 1400.0f + (float)Math.random() * 700.0f;
            this.drift = 4.0f + (float)Math.random() * 6.0f;
        }

        float progress() {
            return (float)(System.currentTimeMillis() - this.born) / this.lifeMs;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static class CosmeticClickTarget {
        final int id;
        final float cardX;
        final float cardY;
        final float cardW;
        final float cardH;
        final float btnX;
        final float btnY;
        final float btnW;
        final float btnH;

        CosmeticClickTarget(int id, float cardX, float cardY, float cardW, float cardH, float btnX, float btnY, float btnW, float btnH) {
            this.id = id;
            this.cardX = cardX;
            this.cardY = cardY;
            this.cardW = cardW;
            this.cardH = cardH;
            this.btnX = btnX;
            this.btnY = btnY;
            this.btnW = btnW;
            this.btnH = btnH;
        }
    }
}

