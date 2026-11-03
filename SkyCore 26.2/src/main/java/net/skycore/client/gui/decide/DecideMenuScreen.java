package net.skycore.client.gui.decide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.skycore.client.SkycoreClient;
import net.skycore.client.command.ChatMessages;
import net.skycore.client.config.ConfigManager;
import net.skycore.client.gui.decide.DecideAnim.Easing;
import net.skycore.client.gui.decide.DecideAnim.SatoshiAnim;
import net.skycore.client.gui.decide.DecideAnim.SmoothAnim;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.KeyBinds;
import net.skycore.client.module.ModuleManager;
import net.skycore.client.module.render.ClickGUIModule;
import net.skycore.client.module.render.CosmeticsModule;
import net.skycore.client.setting.ActionSetting;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ColorSetting;
import net.skycore.client.setting.KeyBindSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.MultiSelectSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.setting.Setting;
import net.skycore.client.setting.StringSetting;
import net.skycore.client.util.BeginFrameHelper;
import net.skycore.client.util.CurrentGuiGraphicsExtractorHelper;
import net.skycore.client.util.EntriesHelper;
import net.skycore.client.util.TextHeightHelper;
import net.skycore.client.waypoint.WaypointManager;
import org.lwjgl.glfw.GLFW;

/**
 * 1:1 port of Decide's {@code ru.decide.screen.Menu} ("MenuNight") ClickGUI.
 *
 * <p>Geometry, easings, widget proportions, hit boxes and sounds follow the reference client:
 * panel {@code 420x280 * S}, rail {@code 30S} wide with a {@code 16S} icon column, module list
 * {@code 140S}, right column {@code w - 194S}, settings stack inset by {@code 10S}, rows of
 * {@code 25S} (slider), {@code 15S + chips} (mode/multi), {@code 16S} (boolean/bind/string/action)
 * and {@code 16S + 35S} (colour picker). Only the host surface differs: MC 26.2 Mojang mappings and
 * the Skycore module/setting model.
 *
 * <p>Hot-path discipline: hit boxes live in flat arrays refilled while drawing, chip layout is written
 * into reused arrays shared by drawing and hit testing, and every animated scalar, easing instance and
 * formatted label is cached in an identity-keyed map that is only mutated afterwards. No per-frame
 * allocation: no string concatenation, no boxing, no iterators, no {@code computeIfAbsent} lambdas.
 */
public final class DecideMenuScreen extends Screen {
    private static final int PANEL_BASE_W = 420;
    private static final int PANEL_BASE_H = 280;
    private static final int MAX_HITS = 768;
    private static final int MAX_CHIPS = 96;
    private static final int MAX_PARTICLES = 64;

    private static final int HIT_SEARCH = 1;
    private static final int HIT_THEME = 2;
    private static final int HIT_CATEGORY = 3;
    private static final int HIT_MODULE = 4;
    private static final int HIT_MODULE_BIND = 5;
    private static final int HIT_ROW = 6;
    private static final int HIT_BIND_CHIP = 7;
    private static final int HIT_STRING_CHIP = 8;
    private static final int HIT_CHIP = 9;
    private static final int HIT_COLOR_BAR = 10;
    private static final int HIT_COSMETIC_TAB = 11;
    private static final int HIT_COSMETIC_CARD = 12;
    private static final int HIT_CFG_INPUT = 13;
    private static final int HIT_CFG_ADD = 14;
    private static final int HIT_CFG_ROW = 15;
    private static final int HIT_CFG_DEL = 16;
    private static final int HIT_MK_NAME = 17;
    private static final int HIT_MK_X = 18;
    private static final int HIT_MK_Y = 19;
    private static final int HIT_MK_Z = 20;
    private static final int HIT_MK_ADD = 21;
    private static final int HIT_MK_ROW = 22;
    private static final int HIT_MK_DEL = 23;

    private static final int SLOT_BIND_W = 0;
    private static final int SLOT_EDIT_W = 1;
    private static final int SLOT_HUE = 2;
    private static final int SLOT_SAT = 3;
    private static final int SLOT_BRI = 4;
    private static final int SLOT_COUNT = 5;

    private static final String[] COSMETIC_NAV = {
            EntriesHelper.WINGS, EntriesHelper.BACKPACKS, EntriesHelper.HEAD, EntriesHelper.PETS, EntriesHelper.SWORDS
    };
    private static final String[] COSMETIC_LABELS = {"Крылья", "Рюкзаки", "Шапки и маски", "Питомцы", "Мечи"};

    private final ModuleManager modules;
    private final ConfigManager config;
    private final ClickGUIModule clickGui;
    private final EntriesHelper cosmetica;

    // ---- hit boxes (flat arrays, refilled each frame while drawing)
    private final int[] hitType = new int[MAX_HITS];
    private final int[] hitA = new int[MAX_HITS];
    private final int[] hitB = new int[MAX_HITS];
    private final float[] hitX = new float[MAX_HITS];
    private final float[] hitY = new float[MAX_HITS];
    private final float[] hitW = new float[MAX_HITS];
    private final float[] hitH = new float[MAX_HITS];
    private int hitCount;

    // ---- content caches (rebuilt only when query / category / selection changes)
    private final List<ClientModule> visibleModules = new ArrayList<>(192);
    private final List<Setting<?>> visibleSettings = new ArrayList<>(64);
    private final List<EntriesHelper.Entry> cosmeticEntries = new ArrayList<>(160);
    private final List<EntriesHelper.SwordEntry> swordEntries = new ArrayList<>(128);
    private final List<WaypointManager.Waypoint> markerRows = new ArrayList<>(64);
    private final List<String> configRows = new ArrayList<>(32);
    private final Map<ClientModule, String[]> descLines = new IdentityHashMap<>();
    private final Map<ClientModule, float[]> descHeights = new IdentityHashMap<>();
    private final Map<Setting<?>, String> multiLabels = new IdentityHashMap<>();
    private final Map<Setting<?>, String> numberLabels = new IdentityHashMap<>();
    private final Map<Setting<?>, double[]> numberValues = new IdentityHashMap<>();
    private final Map<WaypointManager.Waypoint, String> waypointLabels = new IdentityHashMap<>();

    // ---- animation caches
    private final Map<ClientModule, SmoothAnim> enabledAnims = new IdentityHashMap<>();
    private final Map<ClientModule, SmoothAnim> hoverAnims = new IdentityHashMap<>();
    private final Map<ClientModule, SmoothAnim> selectAnims = new IdentityHashMap<>();
    private final Map<ClientModule, float[]> moduleSmooths = new IdentityHashMap<>();
    private final Map<ClientModule, SatoshiAnim> revealAnims = new IdentityHashMap<>();
    private final Map<ClientModule, SatoshiAnim> moduleBindAnims = new IdentityHashMap<>();
    private final Map<ClientModule, SatoshiAnim> moduleBindHoverAnims = new IdentityHashMap<>();
    private final Map<Setting<?>, SatoshiAnim> settingReveal = new IdentityHashMap<>();
    private final Map<Setting<?>, SatoshiAnim> expandAnims = new IdentityHashMap<>();
    private final Map<Setting<?>, SatoshiAnim> bindAnims = new IdentityHashMap<>();
    private final Map<Setting<?>, SatoshiAnim> pickerAnims = new IdentityHashMap<>();
    private final Map<Setting<?>, SatoshiAnim> editAnims = new IdentityHashMap<>();
    private final Map<Setting<?>, SmoothAnim> settingHover = new IdentityHashMap<>();
    private final Map<Setting<?>, SmoothAnim> settingValue = new IdentityHashMap<>();
    private final Map<Setting<?>, float[]> settingSmooths = new IdentityHashMap<>();
    private final Map<Setting<?>, Map<String, SatoshiAnim>> chipAnims = new IdentityHashMap<>();
    private final Set<Setting<?>> openPickers = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
    private final Set<String> expandedChips = new LinkedHashSet<>();
    private final SatoshiAnim[] cosmeticSelectAnims = new SatoshiAnim[COSMETIC_NAV.length];
    private final SatoshiAnim[] cosmeticHoverAnims = new SatoshiAnim[COSMETIC_NAV.length];

    private final float[] chipDX = new float[MAX_CHIPS];
    private final float[] chipDY = new float[MAX_CHIPS];
    private final float[] chipDW = new float[MAX_CHIPS];

    // ---- particles
    private final float[] particleX = new float[MAX_PARTICLES];
    private final float[] particleY = new float[MAX_PARTICLES];
    private final float[] particleRadius = new float[MAX_PARTICLES];
    private final float[] particleLife = new float[MAX_PARTICLES];
    private final float[] particleDrift = new float[MAX_PARTICLES];
    private final long[] particleBorn = new long[MAX_PARTICLES];
    private int particleCount;
    private long lastParticleAt;
    private long nextParticleDelay = 90L;

    // ---- global animations
    private final SmoothAnim openAnim = new SmoothAnim();
    private final SatoshiAnim categoryReset = new SatoshiAnim(300L, 1.0f, true);
    private final SatoshiAnim searchHover = new SatoshiAnim(300L, 1.0f, false);
    private final SatoshiAnim searchFocus = new SatoshiAnim(300L, 1.0f, false);
    private final SatoshiAnim searchTyped = new SatoshiAnim(300L, 1.0f, false);
    private final SatoshiAnim searchEmpty = new SatoshiAnim(300L, 1.0f, false);
    private final SatoshiAnim logoHover = new SatoshiAnim(300L, 1.0f, false);
    private final float[] caretSmooth = new float[1];

    // ---- scroll state
    private float listScroll;
    private float listScrollTarget;
    private float listMaxScroll;
    private float settingScroll;
    private float settingScrollTarget;
    private float settingMaxScroll;
    private float cosmeticsScroll;
    private float cosmeticsScrollTarget;
    private float cosmeticsMaxScroll;
    private float cosmeticsTabScroll;
    private float cosmeticsTabScrollTarget;
    private float cosmeticsTabMaxScroll;
    private float configScroll;
    private float configScrollTarget;
    private float markerScroll;
    private float markerScrollTarget;

    // ---- layout
    private float scaleFactor = 1.0f;
    private float panelX;
    private float panelY;
    private float panelW;
    private float panelH;
    private float mouseX;
    private float mouseY;

    // ---- state
    private DecideCategory active = DecideCategory.RENDER;
    private ClientModule selectedModule;
    private ClientModule bindingModule;
    private KeyBindSetting bindingSetting;
    private StringSetting editingString;
    private Setting<?> draggingSlider;
    private Setting<?> draggingColor;
    private int draggingColorBar;
    private int activeCosmeticNav;

    private final StringBuilder search = new StringBuilder(32);
    private final StringBuilder stringBuffer = new StringBuilder(128);
    private final StringBuilder configInput = new StringBuilder(32);
    private final StringBuilder markerName = new StringBuilder(32);
    private final StringBuilder markerX = new StringBuilder(16);
    private final StringBuilder markerY = new StringBuilder(16);
    private final StringBuilder markerZ = new StringBuilder(16);
    private String queryText = "";
    private boolean searchActive;
    private boolean configInputFocused;
    private int markerInputFocused = -1;
    private long searchTypeTime = System.currentTimeMillis();
    private boolean caretBlink;
    private long blinkAt;

    private boolean contentDirty = true;
    private int builtQueryHash = Integer.MIN_VALUE;

    private String resultLabel = "0 results";
    private int resultLabelCount = -1;
    private final int[] cosmeticCounts = new int[COSMETIC_NAV.length];
    private final String[] cosmeticCountLabels = new String[COSMETIC_NAV.length];
    private boolean cosmeticCountsReady;
    private String equippedLabel = "Не надето";
    private String equippedName = "";
    private int equippedNav = -1;
    private CosmeticsModule cosmeticsModule;
    private boolean cosmeticsResolved;

    private boolean exit;
    private boolean closing;
    private boolean captureArmed;
    private long captureArmedAt;
    private long closingAt;
    private boolean persistDirty;
    private long lastPersistAt;
    private boolean frameFailed;
    private final float[] hsv = new float[3];

    public DecideMenuScreen(ModuleManager modules, ConfigManager config, ClickGUIModule clickGui) {
        super(Component.literal("Skycore ClickGUI"));
        this.modules = modules;
        this.config = config;
        this.clickGui = clickGui;
        this.cosmetica = new EntriesHelper(modules);
        for (int i = 0; i < COSMETIC_NAV.length; i++) {
            this.cosmeticSelectAnims[i] = new SatoshiAnim(300L, 1.0f, false);
            this.cosmeticHoverAnims[i] = new SatoshiAnim(300L, 1.0f, false);
        }
    }

    // ------------------------------------------------------------------ lifecycle

    @Override
    protected void init() {
        this.hitCount = 0;
        this.particleCount = 0;
        this.exit = false;
        this.closing = false;
        this.captureArmed = false;
        this.search.setLength(0);
        this.queryText = "";
        this.searchActive = false;
        this.selectedModule = null;
        this.bindingModule = null;
        this.bindingSetting = null;
        this.editingString = null;
        this.draggingSlider = null;
        this.draggingColor = null;
        this.configInputFocused = false;
        this.markerInputFocused = -1;
        this.openPickers.clear();
        this.expandedChips.clear();
        this.listScroll = 0.0f;
        this.listScrollTarget = 0.0f;
        this.settingScroll = 0.0f;
        this.settingScrollTarget = 0.0f;
        this.cosmeticsScroll = 0.0f;
        this.cosmeticsScrollTarget = 0.0f;
        this.cosmeticsTabScroll = 0.0f;
        this.cosmeticsTabScrollTarget = 0.0f;
        this.configScroll = 0.0f;
        this.configScrollTarget = 0.0f;
        this.markerScroll = 0.0f;
        this.markerScrollTarget = 0.0f;
        this.contentDirty = true;
        this.builtQueryHash = Integer.MIN_VALUE;
        this.frameFailed = false;
        // Шрифты грузятся здесь, а не в первом кадре: несовместимый атлас должен показать
        // сообщение и не открывать меню, а не падать посреди отрисовки.
        try {
            DecideFonts.sfRegular();
            DecideFonts.decide2();
            DecideFonts.guiIcon();
        } catch (RuntimeException failure) {
            SkycoreClient.LOGGER.error("Skycore Decide GUI: font atlas failed to load", failure);
            ChatMessages.displayError("ClickGUI: шрифты Decide не загрузились (детали в логе)");
            this.exit = true;
            this.frameFailed = true;
            this.openAnim.setValue(0.0f);
            return;
        }
        DecideTheme.ensureLoaded();
        DecideCloseAnimation.cancel();
        this.openAnim.setValue(0.0f);
        this.openAnim.run(1.0f, 0.25f, Easing.SINE_OUT);
        this.categoryReset.resetToZero();
        this.categoryReset.restartForwards();
        DecideSounds.open();
        DecideMusic.start(0.15f);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /** True while a text field owns the keyboard, so GUI movement must stay disabled. */
    public boolean isCapturingTextInput() {
        return this.searchActive || this.editingString != null
                || this.configInputFocused || this.markerInputFocused >= 0;
    }

    @Override
    public void removed() {
        DecideGuiMovement.release(Minecraft.getInstance());
        DecideMusic.stop();
        this.persistDirty = true;
        this.flushPersist(System.currentTimeMillis());
        super.removed();
        // Тесты и /reload пересоздают шрифты: кэш указывал бы на удалённые текстуры.
        DecideFonts.reset();
        DecideTheme.ensureLoaded();
    }

    // ------------------------------------------------------------------ layout

    private void measure() {
        this.scaleFactor = (float) this.clickGui.decideSize().get().doubleValue();
        if (this.scaleFactor < 0.4f) {
            this.scaleFactor = 0.4f;
        }
        float open = this.openAnim.get();
        this.panelW = PANEL_BASE_W * this.scaleFactor;
        this.panelH = PANEL_BASE_H * this.scaleFactor;
        float slide = 60.0f * this.scaleFactor * (1.0f - open);
        this.panelX = (this.width - this.panelW) * 0.5f;
        this.panelY = (this.height - this.panelH) * 0.5f + (this.exit ? slide : -slide);
    }

    private float s(float value) {
        return value * this.scaleFactor;
    }

    private boolean hovered(float x, float y, float w, float h) {
        return this.mouseX >= x && this.mouseX <= x + w && this.mouseY >= y && this.mouseY <= y + h;
    }

    private void addHit(int type, int a, int b, float x, float y, float w, float h) {
        if (this.hitCount >= MAX_HITS || w <= 0.0f || h <= 0.0f) {
            return;
        }
        this.hitType[this.hitCount] = type;
        this.hitA[this.hitCount] = a;
        this.hitB[this.hitCount] = b;
        this.hitX[this.hitCount] = x;
        this.hitY[this.hitCount] = y;
        this.hitW[this.hitCount] = w;
        this.hitH[this.hitCount] = h;
        this.hitCount++;
    }

    private int findHit(double x, double y) {
        for (int i = this.hitCount - 1; i >= 0; i--) {
            if (x >= this.hitX[i] && x <= this.hitX[i] + this.hitW[i]
                    && y >= this.hitY[i] && y <= this.hitY[i] + this.hitH[i]) {
                return i;
            }
        }
        return -1;
    }

    private float listX() {
        return this.panelX + this.s(42.0f);
    }

    private float listY() {
        return this.panelY + this.s(32.0f);
    }

    private float listW() {
        return this.s(140.0f);
    }

    private float listH() {
        return this.panelH - this.s(12.0f) - this.s(26.0f);
    }

    private float columnX() {
        return this.panelX + this.s(188.0f);
    }

    private float columnW() {
        return this.panelW - this.s(194.0f);
    }

    // ------------------------------------------------------------------ caches

    private float smoothSlot(float[] box, int slot, float target) {
        box[slot] += (target - box[slot]) * 0.2f;
        if (Math.abs(target - box[slot]) < 0.01f) {
            box[slot] = target;
        }
        return box[slot];
    }

    private float smoothModule(ClientModule module, int slot, float target) {
        float[] box = this.moduleSmooths.get(module);
        if (box == null) {
            box = new float[SLOT_COUNT];
            box[slot] = target;
            this.moduleSmooths.put(module, box);
            return target;
        }
        return this.smoothSlot(box, slot, target);
    }

    private float[] settingSmooth(Setting<?> setting) {
        float[] box = this.settingSmooths.get(setting);
        if (box == null) {
            box = new float[SLOT_COUNT];
            this.settingSmooths.put(setting, box);
        }
        return box;
    }

    private float smoothSetting(Setting<?> setting, int slot, float target) {
        return this.smoothSlot(this.settingSmooth(setting), slot, target);
    }

    private SmoothAnim enabledAnim(ClientModule module) {
        SmoothAnim cached = this.enabledAnims.get(module);
        if (cached == null) {
            cached = new SmoothAnim();
            cached.setValue(module.isEnabled() ? 1.0f : 0.0f);
            this.enabledAnims.put(module, cached);
        }
        return cached;
    }

    private SmoothAnim hoverAnim(ClientModule module) {
        SmoothAnim cached = this.hoverAnims.get(module);
        if (cached == null) {
            cached = new SmoothAnim();
            cached.setValue(0.0f);
            this.hoverAnims.put(module, cached);
        }
        return cached;
    }

    private SmoothAnim selectAnim(ClientModule module) {
        SmoothAnim cached = this.selectAnims.get(module);
        if (cached == null) {
            cached = new SmoothAnim();
            cached.setValue(0.0f);
            this.selectAnims.put(module, cached);
        }
        return cached;
    }

    private SatoshiAnim moduleReveal(ClientModule module) {
        SatoshiAnim cached = this.revealAnims.get(module);
        if (cached == null) {
            cached = new SatoshiAnim(300L, 1.0f, false);
            this.revealAnims.put(module, cached);
        }
        return cached;
    }

    private SatoshiAnim moduleBindAnim(ClientModule module) {
        SatoshiAnim cached = this.moduleBindAnims.get(module);
        if (cached == null) {
            cached = new SatoshiAnim(300L, 1.0f, false);
            this.moduleBindAnims.put(module, cached);
        }
        return cached;
    }

    private SatoshiAnim moduleBindHoverAnim(ClientModule module) {
        SatoshiAnim cached = this.moduleBindHoverAnims.get(module);
        if (cached == null) {
            cached = new SatoshiAnim(300L, 1.0f, false);
            this.moduleBindHoverAnims.put(module, cached);
        }
        return cached;
    }

    private SatoshiAnim settingRevealAnim(Setting<?> setting) {
        SatoshiAnim cached = this.settingReveal.get(setting);
        if (cached == null) {
            cached = new SatoshiAnim(300L, 1.0f, true);
            this.settingReveal.put(setting, cached);
        }
        return cached;
    }

    private SatoshiAnim expandAnim(Setting<?> setting) {
        SatoshiAnim cached = this.expandAnims.get(setting);
        if (cached == null) {
            cached = new SatoshiAnim(300L, 1.0f, false);
            this.expandAnims.put(setting, cached);
        }
        return cached;
    }

    private SatoshiAnim bindAnim(Setting<?> setting) {
        SatoshiAnim cached = this.bindAnims.get(setting);
        if (cached == null) {
            cached = new SatoshiAnim(300L, 1.0f, false);
            this.bindAnims.put(setting, cached);
        }
        return cached;
    }

    private SatoshiAnim pickerAnim(Setting<?> setting) {
        SatoshiAnim cached = this.pickerAnims.get(setting);
        if (cached == null) {
            cached = new SatoshiAnim(300L, 1.0f, false);
            this.pickerAnims.put(setting, cached);
        }
        return cached;
    }

    private SatoshiAnim editAnim(Setting<?> setting) {
        SatoshiAnim cached = this.editAnims.get(setting);
        if (cached == null) {
            cached = new SatoshiAnim(300L, 1.0f, false);
            this.editAnims.put(setting, cached);
        }
        return cached;
    }

    private SmoothAnim settingHoverAnim(Setting<?> setting) {
        SmoothAnim cached = this.settingHover.get(setting);
        if (cached == null) {
            cached = new SmoothAnim();
            cached.setValue(0.0f);
            this.settingHover.put(setting, cached);
        }
        return cached;
    }

    private SmoothAnim settingValueAnim(Setting<?> setting, boolean initial) {
        SmoothAnim cached = this.settingValue.get(setting);
        if (cached == null) {
            cached = new SmoothAnim();
            cached.setValue(initial ? 1.0f : 0.0f);
            this.settingValue.put(setting, cached);
        }
        return cached;
    }

    private SatoshiAnim chipAnim(Setting<?> setting, String value) {
        Map<String, SatoshiAnim> map = this.chipAnims.get(setting);
        if (map == null) {
            map = new HashMap<>();
            this.chipAnims.put(setting, map);
        }
        SatoshiAnim cached = map.get(value);
        if (cached == null) {
            cached = new SatoshiAnim(300L, 1.0f, false);
            map.put(value, cached);
        }
        return cached;
    }

    private CosmeticsModule cosmetics() {
        if (!this.cosmeticsResolved) {
            this.cosmeticsResolved = true;
            this.cosmeticsModule = this.modules.get(CosmeticsModule.class).orElse(null);
        }
        return this.cosmeticsModule;
    }

    // ------------------------------------------------------------------ content

    private void ensureContent() {
        int hash = this.queryHash();
        if (!this.contentDirty && hash == this.builtQueryHash) {
            return;
        }
        this.contentDirty = false;
        this.builtQueryHash = hash;
        this.rebuildLists();
    }

    private int queryHash() {
        int hash = 17 * 31 + this.active.ordinal();
        hash = hash * 31 + this.activeCosmeticNav;
        for (int i = 0; i < this.search.length(); i++) {
            hash = hash * 31 + this.search.charAt(i);
        }
        return hash;
    }

    private void rebuildLists() {
        this.visibleModules.clear();
        this.visibleSettings.clear();
        this.cosmeticEntries.clear();
        this.swordEntries.clear();
        this.markerRows.clear();
        this.configRows.clear();
        this.multiLabels.clear();
        this.numberLabels.clear();

        String query = this.queryText.isEmpty() ? "" : this.queryText.trim().toLowerCase(Locale.ROOT);
        boolean noSearch = this.noSearchCategory();
        for (ClientModule module : this.modules.getModules()) {
            boolean visible = !noSearch && this.moduleVisible(module, query);
            this.moduleReveal(module).setDirection(visible);
            if (visible) {
                this.visibleModules.add(module);
            }
        }
        if (!this.active.isSpecial() && this.selectedModule != null) {
            for (Setting<?> setting : this.selectedModule.getSettings()) {
                boolean visible = setting.isVisible();
                this.settingRevealAnim(setting).setDirection(visible);
                if (visible) {
                    this.visibleSettings.add(setting);
                    if (setting instanceof MultiSelectSetting multi) {
                        this.multiLabels.put(setting, this.multiLabel(multi));
                    } else if (setting instanceof NumberSetting number) {
                        this.numberLabels.put(setting, this.formatNumber(number));
                    }
                }
            }
        }
        if (this.active == DecideCategory.COSMETICS) {
            String nav = COSMETIC_NAV[this.activeCosmeticNav];
            if (EntriesHelper.SWORDS.equals(nav)) {
                this.swordEntries.addAll(this.cosmetica.swords(query));
            } else {
                this.cosmeticEntries.addAll(this.cosmetica.entries(nav, query));
            }
            this.refreshEquipped();
        } else if (this.active == DecideCategory.CONFIGS) {
            this.configRows.addAll(this.config.listProfiles());
        } else if (this.active == DecideCategory.MARKERS) {
            this.markerRows.addAll(SkycoreClient.waypoints().getWaypoints());
            for (int i = 0; i < this.markerRows.size(); i++) {
                WaypointManager.Waypoint waypoint = this.markerRows.get(i);
                if (!this.waypointLabels.containsKey(waypoint)) {
                    this.waypointLabels.put(waypoint, this.waypointLabel(waypoint));
                }
            }
        }
        TextHeightHelper font = DecideFonts.sfRegular();
        float size = this.s(6.0f);
        float width = this.s(125.0f);
        for (int i = 0; i < this.visibleModules.size(); i++) {
            ClientModule module = this.visibleModules.get(i);
            float[] cached = this.descHeights.get(module);
            if (cached == null || cached[0] != width || cached[1] != size) {
                String[] lines = DecideDraw.wrapLines(font, module.getDescription(), width, size);
                this.descLines.put(module, lines);
                this.descHeights.put(module, new float[]{width, size, lines.length * DecideDraw.lineHeight(font, size)});
            }
        }
        this.resultLabelCount = -1;
    }

    private String multiLabel(MultiSelectSetting setting) {
        StringBuilder builder = new StringBuilder(8);
        builder.append(setting.get().size()).append(" / ").append(setting.getModes().size());
        return builder.toString();
    }

    private String waypointLabel(WaypointManager.Waypoint waypoint) {
        StringBuilder builder = new StringBuilder(28);
        builder.append(waypoint.getDisplayName()).append("  ")
                .append(Math.round(waypoint.x())).append(' ')
                .append(Math.round(waypoint.y())).append(' ')
                .append(Math.round(waypoint.z()));
        return builder.toString();
    }

    private void refreshEquipped() {
        int nav = this.activeCosmeticNav;
        String name;
        if (EntriesHelper.SWORDS.equals(COSMETIC_NAV[nav])) {
            CosmeticsModule module = this.cosmetics();
            String selected = module == null ? null : module.selectedSword();
            name = selected == null ? "" : selected;
        } else {
            EntriesHelper.Entry entry = this.cosmetica.selectedForNav(COSMETIC_NAV[nav]);
            name = entry == null ? "" : entry.name();
        }
        this.equippedNav = nav;
        this.equippedName = name;
        this.equippedLabel = name.isEmpty() ? "Не надето" : "Надето";
    }

    private void ensureCosmeticCounts() {
        if (this.cosmeticCountsReady) {
            return;
        }
        this.cosmeticCountsReady = true;
        for (int i = 0; i < COSMETIC_NAV.length; i++) {
            int count = EntriesHelper.SWORDS.equals(COSMETIC_NAV[i])
                    ? this.cosmetica.swords("").size()
                    : this.cosmetica.entries(COSMETIC_NAV[i], "").size();
            this.cosmeticCounts[i] = count;
            this.cosmeticCountLabels[i] = Integer.toString(count);
        }
    }

    private boolean searching() {
        return this.queryText.length() > 0;
    }

    private boolean noSearchCategory() {
        return this.active == DecideCategory.CONFIGS || this.active == DecideCategory.MARKERS;
    }

    private boolean moduleVisible(ClientModule module, String query) {
        DecideCategory category = DecideCategory.forHost(module.getCategory());
        if (query.isEmpty()) {
            return category == this.active;
        }
        return module.getName().toLowerCase(Locale.ROOT).contains(query)
                || module.getDescription().toLowerCase(Locale.ROOT).contains(query)
                || category.title().toLowerCase(Locale.ROOT).contains(query);
    }

    private float descHeightOf(ClientModule module) {
        float[] cached = this.descHeights.get(module);
        return cached == null ? 0.0f : cached[2];
    }

    private int resultCount() {
        if (this.active == DecideCategory.COSMETICS) {
            return this.swordEntries.size() + this.cosmeticEntries.size();
        }
        return this.visibleModules.size();
    }

    private String resultLabel(int count) {
        if (count == this.resultLabelCount) {
            return this.resultLabel;
        }
        this.resultLabelCount = count;
        StringBuilder builder = new StringBuilder(12);
        builder.append(count).append(count == 1 ? " result" : " results");
        this.resultLabel = builder.toString();
        return this.resultLabel;
    }

    // ------------------------------------------------------------------ frame

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.openAnim.update();
        long now = System.currentTimeMillis();
        if (now - this.blinkAt > 250L) {
            this.blinkAt = now;
            this.caretBlink = !this.caretBlink;
        }

        if (this.captureArmed) {
            if (DecideCloseAnimation.consumeReadyToCloseScreen()) {
                this.captureArmed = false;
                this.closing = true;
                this.closingAt = now;
                this.releaseScreen();
                return;
            }
            if (now - this.captureArmedAt > 1500L) {
                // Снимок кадра не состоялся (смена размера окна, потеря контекста): вместо того
                // чтобы оставить меню висящим без реакции на ввод, выходим обычной анимацией.
                DecideCloseAnimation.cancel();
                this.captureArmed = false;
                this.exit = true;
                this.openAnim.run(0.0f, 0.2f, Easing.SINE_IN);
                DecideMusic.stop();
                return;
            }
        }
        if (this.closing) {
            if (now - this.closingAt > 80L) {
                this.releaseScreen();
            }
            return;
        }
        if (this.exit && this.openAnim.isFinished()) {
            this.particleCount = 0;
            this.releaseScreen();
            return;
        }

        try {
            this.measure();
            this.ensureContent();
            this.flushPersist(now);
        } catch (RuntimeException failure) {
            this.drawFailure(failure);
            return;
        }

        CurrentGuiGraphicsExtractorHelper.enter2D(extractor, this.minecraft == null ? null : this.minecraft.getDeltaTracker());
        BeginFrameHelper.beginFrame();
        this.hitCount = 0;
        try {
            this.drawFrame();
            BeginFrameHelper.flush();
        } catch (RuntimeException failure) {
            // Экран уже вошёл в 2D-контекст: сначала выравниваем состояние кадра, только потом
            // сообщаем об ошибке, иначе следующий кадр получит непарный scissor и упадёт снова.
            BeginFrameHelper.beginFrame();
            this.drawFailure(failure);
        } finally {
            CurrentGuiGraphicsExtractorHelper.exit2D();
        }
    }

    private void drawFrame() {
        this.drawEffects();
        this.drawThemeBar();
        this.drawPanel();
        this.drawRail();
        this.drawSearchBar();
        this.drawHeaderBar();
        if (this.active.isSpecial()) {
            this.drawSpecial();
        } else {
            this.drawModuleList();
            this.drawSettingsPanel();
        }
        this.drawParticles();
        DecideCloseAnimation.queueCaptureMarker();
    }

    /**
     * A failure inside one widget must not take the client down with it: the frame is reported once,
     * the click GUI is closed, and the game keeps running with a chat message instead of a crash
     * screen. Drawing the rest of the frame here would filter through half-built state.
     */
    private void drawFailure(RuntimeException failure) {
        if (this.frameFailed) {
            return;
        }
        this.frameFailed = true;
        SkycoreClient.LOGGER.error("Skycore Decide GUI: frame aborted, closing the menu", failure);
        ChatMessages.displayError("ClickGUI: ошибка отрисовки, меню закрыто (детали в логе)");
        this.hitCount = 0;
        this.particleCount = 0;
        this.exit = true;
        this.openAnim.setValue(0.0f);
        // Смена экрана прямо посреди кадра ломает состояние рендера, поэтому закрытие
        // откладывается на начало следующей отрисовки — там экран снимается штатно.
        DecideMusic.stop();
        this.persistDirty = true;
        this.flushPersist(System.currentTimeMillis());
    }

    private void releaseScreen() {
        if (this.minecraft != null && this.minecraft.gui.screen() == this) {
            DecideGuiMovement.release(this.minecraft);
            this.minecraft.setScreenAndShow(null);
        }
    }

    // ------------------------------------------------------------------ backdrop effects

    private void drawEffects() {
        float open = this.openAnim.get();
        int client = DecideTheme.currentClientColor();
        if (this.clickGui.effectEnabled(ClickGUIModule.EFFECT_GRAYSCALE)) {
            DecideBackdrop.drawGrayscale(this.width, this.height, open);
        }
        if (this.clickGui.effectEnabled(ClickGUIModule.EFFECT_BLUR)) {
            DecideDraw.blur(0.0f, 0.0f, this.width, this.height, open * 0.9f, 0.0f,
                    DecideDraw.multAlpha(DecideDraw.multDark(DecideTheme.currentBackgroundColor(), 0.6f), open));
        }
        if (this.clickGui.effectEnabled(ClickGUIModule.EFFECT_DIM)) {
            DecideDraw.texture(DecideDraw.TEX_BACKGROUND, 0.0f, 0.0f, this.width, this.height,
                    DecideDraw.multAlpha(client, open * 0.5f));
            DecideDraw.rect(0.0f, 0.0f, this.width, this.height,
                    DecideDraw.multAlpha(DecideDraw.multDark(DecideTheme.currentBackgroundColor(), 0.4f),
                            open * 0.55f), 0.0f);
        }
        if (this.clickGui.effectEnabled(ClickGUIModule.EFFECT_SHADER)) {
            DecideBackdrop.drawRays(this.width, this.height,
                    (float) (System.currentTimeMillis() % 600000L) / 1000.0f,
                    DecideDraw.replAlpha(DecideDraw.multDark(client, 0.5f), 0.9f * open),
                    DecideDraw.replAlpha(client, 0.9f * open));
        }
        if (this.clickGui.effectEnabled(ClickGUIModule.EFFECT_DOTS)) {
            DecideBackdrop.drawHalftone(this.width, this.height, this.mouseX, this.mouseY,
                    this.scaleFactor, open * 0.1f);
        }
        if (this.clickGui.effectEnabled(ClickGUIModule.EFFECT_SCANLINES)) {
            float phase = (float) (System.currentTimeMillis() % 3000L) / 3000.0f;
            DecideBackdrop.drawScanlines(this.width, this.height, this.s(4.0f), this.s(100.0f), phase,
                    open * 0.18f);
        }
        if (this.clickGui.effectEnabled(ClickGUIModule.EFFECT_GLOW)) {
            DecideDraw.texture(DecideDraw.TEX_TOP_GLOW, 0.0f, -this.s(30.0f) + this.s(30.0f) * open,
                    this.width, this.height, DecideDraw.replAlpha(client, open * 0.5f));
            if (!this.exit && open > 0.01f && open < 0.99f) {
                float pulse = (open > 0.5f ? 1.0f - open : open) * 2.0f;
                float size = (float) this.width / 1.75f * open;
                float cx = this.width * 0.5f;
                float cy = this.height * 0.5f;
                DecideDraw.glow(cx - size, cy - size, size * 2.0f, size * 2.0f,
                        DecideDraw.replAlpha(client, 0.15f * pulse), size, this.s(12.0f));
            }
        }
    }

    private void drawParticles() {
        if (!this.clickGui.effectEnabled(ClickGUIModule.EFFECT_PARTICLES)) {
            this.particleCount = 0;
            return;
        }
        if (this.openAnim.get() < 0.5f) {
            return;
        }
        long now = System.currentTimeMillis();
        float open = this.openAnim.get();
        int client = DecideTheme.currentClientColor();
        for (int i = this.particleCount - 1; i >= 0; i--) {
            float progress = (float) (now - this.particleBorn[i]) / this.particleLife[i];
            if (progress >= 1.0f) {
                this.removeParticle(i);
                continue;
            }
            float grow = 1.0f - (1.0f - progress) * (1.0f - progress) * (1.0f - progress);
            float radius = this.particleRadius[i] * grow * this.scaleFactor;
            float fade = smoothstep(0.0f, 0.18f, progress) * (1.0f - smoothstep(0.45f, 1.0f, progress));
            float alpha = fade * open * 0.75f;
            if (radius < 0.4f || alpha < 0.004f) {
                continue;
            }
            float thickness = 1.1f + 2.6f * (1.0f - grow);
            float py = this.particleY[i] - this.particleDrift[i] * grow * this.scaleFactor;
            DecideDraw.outline(this.particleX[i] - radius, py - radius, radius * 2.0f, radius * 2.0f,
                    thickness, DecideDraw.replAlpha(client, alpha), radius);
        }
        if (now - this.lastParticleAt >= this.nextParticleDelay && this.particleCount < MAX_PARTICLES) {
            this.lastParticleAt = now;
            this.nextParticleDelay = 70L + (long) (Math.random() * 90.0);
            int index = this.particleCount++;
            this.particleX[index] = (float) (Math.random() * this.width);
            this.particleY[index] = (float) (Math.random() * this.height);
            this.particleRadius[index] = 10.0f + (float) Math.random() * 7.0f;
            this.particleLife[index] = 1400.0f + (float) Math.random() * 700.0f;
            this.particleDrift[index] = 4.0f + (float) Math.random() * 6.0f;
            this.particleBorn[index] = now;
        }
    }

    private void removeParticle(int index) {
        int last = --this.particleCount;
        if (index == last) {
            return;
        }
        this.particleX[index] = this.particleX[last];
        this.particleY[index] = this.particleY[last];
        this.particleRadius[index] = this.particleRadius[last];
        this.particleLife[index] = this.particleLife[last];
        this.particleDrift[index] = this.particleDrift[last];
        this.particleBorn[index] = this.particleBorn[last];
    }

    private static float smoothstep(float edge0, float edge1, float value) {
        float t = DecideDraw.clamp((value - edge0) / (edge1 - edge0), 0.0f, 1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    // ------------------------------------------------------------------ chrome

    private void drawThemeBar() {
        float open = this.openAnim.get();
        int client = DecideTheme.currentClientColor();
        DecideTheme[] themes = DecideTheme.all();
        float barH = this.s(20.0f);
        float dotsW = (themes.length - 1) * this.s(14.0f) + this.s(8.0f);
        float barW = dotsW + this.s(12.0f);
        float dotsX = this.panelX + (this.panelW - dotsW) * 0.5f;
        float barX = dotsX - this.s(6.0f);
        float barY = this.panelY + this.panelH + this.s(5.0f);
        DecideDraw.glow(barX, barY, barW, barH - this.s(0.5f), DecideDraw.getColor(0, 0.15f * open),
                this.s(7.5f), this.s(15.0f));
        DecideDraw.blur(barX, barY, barW, barH, open, this.s(7.5f),
                DecideDraw.multAlpha(DecideDraw.multDark(DecideTheme.currentBackgroundColor(), 0.6f), open));
        DecideDraw.texture(DecideDraw.TEX_RECT_THEME, barX, barY, barW, barH, DecideDraw.multAlpha(client, open));
        DecideTheme selected = DecideTheme.selected();
        float dotX = dotsX;
        float dotY = barY + this.s(6.0f);
        float base = this.s(8.0f);
        for (int i = 0; i < themes.length; i++) {
            DecideTheme theme = themes[i];
            theme.dotAnim.setDirection(theme == selected);
            float value = theme.dotAnim.getOutput();
            float inset = this.s(0.75f) * value;
            DecideDraw.rect(dotX + inset, dotY + inset, base - inset * 2.0f, base - inset * 2.0f,
                    DecideDraw.replAlpha(theme.client(), (0.5f + 0.5f * value) * open), base);
            DecideDraw.outline(dotX - this.s(0.5f) * value, dotY - this.s(0.5f) * value,
                    base + this.s(1.0f) * value, base + this.s(1.0f) * value, this.s(0.25f),
                    DecideDraw.replAlpha(theme.client(), value * open), base);
            DecideDraw.glow(dotX + inset, dotY + inset, base - inset * 2.0f, base - inset * 2.0f,
                    DecideDraw.replAlpha(theme.client(), 0.1f * value * open), base, this.s(7.0f));
            this.addHit(HIT_THEME, i, 0, dotX, dotY, base, base);
            dotX += this.s(14.0f);
        }
    }

    private void drawPanel() {
        float open = this.openAnim.get();
        int client = DecideTheme.currentClientColor();
        DecideDraw.glow(this.panelX, this.panelY, this.panelW, this.panelH - this.s(0.5f),
                DecideDraw.getColor(0, 0.15f * open), this.s(8.0f), this.s(15.0f));
        DecideDraw.blur(this.panelX, this.panelY, this.panelW, this.panelH, open, this.s(8.0f),
                DecideDraw.multAlpha(DecideDraw.multDark(DecideTheme.currentBackgroundColor(), 0.6f), open));
        DecideDraw.texture(DecideDraw.TEX_RECT_GUI, this.panelX, this.panelY, this.panelW, this.panelH,
                DecideDraw.multAlpha(client, open));
        DecideDraw.glow(this.panelX + this.s(6.0f), this.panelY + this.s(6.0f), this.s(30.0f),
                this.panelH - this.s(12.0f), DecideDraw.getColor(0, 0.04f * open), this.s(6.0f), this.s(8.0f));
        DecideDraw.rect(this.panelX + this.s(6.0f), this.panelY + this.s(6.0f), this.s(30.0f),
                this.panelH - this.s(12.0f), DecideDraw.getColor(0, 0.15f * open), this.s(6.0f));
    }

    private void drawRail() {
        float open = this.openAnim.get();
        TextHeightHelper font = DecideFonts.sfRegular();
        TextHeightHelper icons = DecideFonts.decide2();
        int client = DecideTheme.currentClientColor();
        float railX = this.panelX + this.s(6.0f);
        float railY = this.panelY + this.s(6.0f);
        float iconX = railX + this.s(7.0f);
        float logoY = railY + this.s(2.0f);

        this.logoHover.setDirection(this.hovered(iconX, logoY, this.s(16.0f), this.s(16.0f)));
        float logoAnim = this.logoHover.getOutput();
        DecideDraw.glow(iconX + this.s(5.5f), logoY + this.s(10.0f), this.s(0.1f), this.s(0.1f),
                DecideDraw.replAlpha(client, open * logoAnim * 0.15f), this.s(10.0f), this.s(12.0f));
        DecideDraw.texture(DecideDraw.TEX_LOGO, iconX, logoY, this.s(16.0f), this.s(16.0f),
                DecideDraw.replAlpha(DecideTheme.currentLogoColor(), open));
        if (logoAnim > 0.01f) {
            this.drawRailTooltip("DecideVisuals 5.0", iconX, logoY + this.s(7.0f), logoAnim, open, font, client);
        }

        float categoryY = railY + this.s(28.0f);
        DecideCategory[] categories = DecideCategory.all();
        for (int i = 0; i < categories.length; i++) {
            DecideCategory category = categories[i];
            category.activeAnim.setDirection(category == this.active);
            float activeAnim = category.activeAnim.getOutput();
            boolean categoryHovered = this.hovered(iconX, categoryY, this.s(16.0f), this.s(16.0f));
            category.hoverAnim.setDirection(categoryHovered);
            float hoverAnim = category.hoverAnim.getOutput();
            DecideDraw.outline(iconX - this.s(1.0f) * activeAnim + this.s(1.0f),
                    categoryY - this.s(1.0f) * activeAnim + this.s(1.0f),
                    this.s(16.0f) + this.s(2.0f) * activeAnim - this.s(2.0f),
                    this.s(16.0f) + this.s(2.0f) * activeAnim - this.s(2.0f), this.s(0.5f),
                    DecideDraw.replAlpha(client, open * activeAnim), this.s(5.0f));
            DecideDraw.glow(iconX + this.s(5.8f), categoryY + this.s(7.8f), this.s(0.1f), this.s(0.1f),
                    DecideDraw.replAlpha(client, open * hoverAnim * 0.15f * activeAnim), this.s(8.0f), this.s(9.0f));
            int iconColor = DecideDraw.multAlpha(DecideDraw.overCol(
                    DecideDraw.getColor(255, 0.15f * open + 0.5f * hoverAnim), client, activeAnim), open);
            if (category == DecideCategory.MARKERS) {
                DecideDraw.texture(DecideDraw.TEX_MARKER, iconX + this.s(4.0f), categoryY + this.s(4.0f),
                        this.s(8.0f), this.s(8.0f), iconColor);
            } else {
                DecideDraw.textCentered(icons, category.icon(), iconX + this.s(8.0f),
                        categoryY + this.s(4.5f), this.s(8.0f), iconColor);
            }
            if (hoverAnim > 0.01f) {
                this.drawRailTooltip(category.title(), iconX, categoryY, hoverAnim, open, font, client);
            }
            this.addHit(HIT_CATEGORY, i, 0, iconX, categoryY, this.s(16.0f), this.s(16.0f));
            categoryY += this.s(20.0f);
        }
    }

    private void drawRailTooltip(String label, float iconX, float y, float anim, float open,
                                 TextHeightHelper font, int client) {
        float boxW = DecideDraw.textWidth(font, label, this.s(8.0f)) + this.s(7.0f);
        float boxH = this.s(14.0f);
        float boxX = iconX + this.s(16.0f) + this.s(4.0f) - this.s(16.0f) * (1.0f - anim);
        DecideDraw.glow(boxX, y, boxW, boxH, DecideDraw.getColor(0, anim * open * 0.1f), this.s(4.0f), this.s(6.0f));
        DecideDraw.blur(boxX, y, boxW, boxH, open * anim, this.s(4.0f),
                DecideDraw.multAlpha(DecideTheme.currentBackgroundColor(), open * anim * 0.2f));
        DecideDraw.text(font, label, boxX + this.s(3.5f), y + (boxH - this.s(8.0f)) * 0.5f, this.s(8.0f),
                DecideDraw.getColor(200, open * anim));
    }

    private void drawSearchBar() {
        float open = this.openAnim.get();
        TextHeightHelper font = DecideFonts.sfRegular();
        TextHeightHelper icons = DecideFonts.guiIcon();
        int client = DecideTheme.currentClientColor();
        float x = this.panelX + this.s(42.0f);
        float y = this.panelY + this.s(6.0f);
        float w = this.s(140.0f);
        float h = this.s(20.0f);
        boolean noSearch = this.noSearchCategory();
        this.searchHover.setDirection(!noSearch && this.hovered(x, y, w, h));
        this.searchFocus.setDirection(this.searchActive);
        this.searchTyped.setDirection(this.searching());
        this.searchEmpty.setDirection(!noSearch && this.searching() && this.resultCount() == 0);
        float hoverAnim = this.searchHover.getOutput();
        float focusAnim = this.searchFocus.getOutput();
        float typedAnim = this.searchTyped.getOutput();

        DecideDraw.glow(x, y, w, h, DecideDraw.getColor(0, 0.04f * open), this.s(6.0f), this.s(8.0f));
        DecideDraw.rect(x, y, w, h, DecideDraw.overCol(DecideDraw.getColor(0, 0.15f * open),
                DecideDraw.getColor(25, 0.3f * open), Math.max(hoverAnim, focusAnim)), this.s(6.0f));
        DecideDraw.outline(x, y, w, h, this.s(0.5f), DecideDraw.replAlpha(client, open * focusAnim), this.s(6.0f));
        DecideDraw.text(icons, "b", x + this.s(7.0f), y + this.s(7.0f), this.s(6.0f),
                DecideDraw.overCol(DecideDraw.getColor(255, open * (0.3f + 0.5f * hoverAnim)),
                        DecideDraw.replAlpha(client, open * (0.3f + 0.7f * hoverAnim)), hoverAnim));

        float textX = x + this.s(17.5f);
        float textW = w - (textX - x) - this.s(8.0f);
        float hide = Math.max(typedAnim, focusAnim);
        if (hide < 0.99f) {
            DecideDraw.text(font, noSearch ? "Выберите разделы" : "Search", textX,
                    y + this.s(5.5f) + this.s(4.0f) * hide, this.s(7.0f),
                    DecideDraw.getColor(255, open * (1.0f - hide) * (0.3f + 0.5f * hoverAnim)));
        }
        if (typedAnim > 0.01f) {
            DecideDraw.textFadingReverse(font, this.queryText, textX,
                    y + this.s(5.5f) - this.s(4.0f) * (1.0f - typedAnim), textW,
                    DecideDraw.getColor(255, open * typedAnim * (0.5f + 0.5f * Math.max(hoverAnim, focusAnim))),
                    this.s(7.0f));
        }
        boolean justTyped = System.currentTimeMillis() - this.searchTypeTime < 500L;
        float caretTarget = focusAnim * (justTyped || this.caretBlink ? 1.0f : 0.0f);
        this.caretSmooth[0] += (caretTarget - this.caretSmooth[0]) * 0.2f;
        float caretX = textX + Math.min(DecideDraw.textWidth(font, this.queryText, this.s(7.0f)), textW) + this.s(1.5f);
        DecideDraw.rect(caretX, y + this.s(6.5f), this.s(0.6f), this.s(6.5f),
                DecideDraw.replAlpha(client, open * this.caretSmooth[0]), this.s(0.3f));
        this.addHit(HIT_SEARCH, 0, 0, x, y, w, h);
    }

    private void drawHeaderBar() {
        float open = this.openAnim.get();
        TextHeightHelper font = DecideFonts.sfRegular();
        TextHeightHelper icons = DecideFonts.decide2();
        int client = DecideTheme.currentClientColor();
        float x = columnX();
        float y = this.panelY + this.s(6.0f);
        float w = columnW();
        float h = this.s(20.0f);
        DecideDraw.glow(x, y, w, h, DecideDraw.getColor(0, 0.04f * open), this.s(6.0f), this.s(8.0f));
        DecideDraw.rect(x, y, w, h, DecideDraw.getColor(0, 0.15f * open), this.s(6.0f));
        float anim = this.categoryReset.getOutput();
        DecideDraw.text(icons, this.active.icon(), x + this.s(8.0f),
                y + this.s(12.7f) - this.s(5.0f) + this.s(5.0f) * anim, this.s(7.0f),
                DecideDraw.multAlpha(client, open * anim));
        DecideDraw.text(font, this.active.title(), x + this.s(20.0f),
                y + this.s(11.8f) - this.s(5.0f) * (1.0f - anim), this.s(7.0f),
                DecideDraw.getColor(200, open * anim));
        String label = this.resultLabel(this.resultCount());
        float typedAnim = this.searchTyped.getOutput();
        DecideDraw.text(font, label, x + w - this.s(8.0f) - DecideDraw.textWidth(font, label, this.s(6.5f)),
                y + this.s(12.2f) + this.s(5.0f) * typedAnim, this.s(6.5f),
                DecideDraw.replAlpha(client, open * typedAnim));
    }

    private void drawListBackground() {
        float open = this.openAnim.get();
        DecideDraw.glow(this.listX(), this.listY(), this.listW(), this.listH(),
                DecideDraw.getColor(0, 0.04f * open), this.s(7.0f), this.s(8.0f));
        DecideDraw.rect(this.listX(), this.listY(), this.listW(), this.listH(),
                DecideDraw.getColor(0, 0.15f * open), this.s(7.0f));
    }

    private void drawColumnBackground() {
        float open = this.openAnim.get();
        DecideDraw.glow(this.columnX(), this.listY(), this.columnW(), this.listH(),
                DecideDraw.getColor(0, 0.04f * open), this.s(7.0f), this.s(8.0f));
        DecideDraw.rect(this.columnX(), this.listY(), this.columnW(), this.listH(),
                DecideDraw.getColor(0, 0.15f * open), this.s(7.0f));
    }

    // ------------------------------------------------------------------ module list

    private void drawModuleList() {
        float open = this.openAnim.get();
        TextHeightHelper font = DecideFonts.sfRegular();
        int client = DecideTheme.currentClientColor();
        this.drawListBackground();
        float x = this.listX();
        float y = this.listY();
        float w = this.listW();
        float h = this.listH();

        this.listScroll += (this.listScrollTarget - this.listScroll) * 0.2f;
        if (Math.abs(this.listScrollTarget - this.listScroll) < 0.05f) {
            this.listScroll = this.listScrollTarget;
        }
        BeginFrameHelper.pushScissor(x, y, w, h);
        float itemX = x + this.s(5.0f);
        float itemW = this.s(130.0f);
        float itemY = y + this.s(4.0f) - this.listScroll;
        for (int i = 0; i < this.visibleModules.size(); i++) {
            ClientModule module = this.visibleModules.get(i);
            float revealAnim = this.moduleReveal(module).getOutput();
            if (revealAnim <= 0.001f) {
                continue;
            }
            float descH = this.descHeightOf(module);
            float itemH = Math.max(this.s(20.0f), this.s(16.0f) + descH + this.s(4.0f));
            boolean onScreen = itemY + itemH >= y && itemY <= y + h;
            SmoothAnim enabled = this.enabledAnim(module);
            enabled.run(module.isEnabled() ? 1.0f : 0.0f, 0.18f, Easing.QUAD_OUT);
            SmoothAnim hover = this.hoverAnim(module);
            boolean hovered = onScreen && this.hovered(itemX, itemY, itemW, itemH);
            hover.run(hovered ? 1.0f : 0.0f, 0.16f, Easing.QUAD_OUT);
            SmoothAnim select = this.selectAnim(module);
            select.run(module == this.selectedModule ? 1.0f : 0.0f, 0.16f, Easing.QUAD_OUT);
            if (!onScreen) {
                itemY += (itemH + this.s(5.0f)) * revealAnim;
                continue;
            }
            float enabledAnim = enabled.get();
            float hoverAnim = hover.get();
            float selectAnim = select.get();

            DecideDraw.rect(itemX, itemY, itemW, itemH,
                    DecideDraw.overCol(DecideDraw.getColor(0, 0.15f * open * revealAnim),
                            DecideDraw.getColor(25, 0.3f * open * revealAnim), hoverAnim), this.s(5.0f));
            DecideDraw.outline(itemX - this.s(1.0f) * enabledAnim + this.s(1.0f),
                    itemY - this.s(1.0f) * enabledAnim + this.s(1.0f),
                    itemW + this.s(2.0f) * enabledAnim - this.s(2.0f),
                    itemH + this.s(2.0f) * enabledAnim - this.s(2.0f), this.s(0.5f),
                    DecideDraw.replAlpha(client, open * revealAnim * enabledAnim * (1.0f - 0.3f * hoverAnim)),
                    this.s(5.0f));
            float toggleX = itemX + itemW - this.s(19.0f);
            DecideDraw.rect(toggleX, itemY + this.s(5.0f), this.s(14.0f), this.s(8.0f),
                    DecideDraw.overCol(DecideDraw.getColor(0, 0.2f * open * revealAnim),
                            DecideDraw.replAlpha(client, open * revealAnim * (1.0f - 0.3f * hoverAnim)),
                            enabledAnim), this.s(4.0f));
            DecideDraw.rect(toggleX + this.s(1.5f) + this.s(5.5f) * enabledAnim, itemY + this.s(6.25f),
                    this.s(5.5f), this.s(5.5f),
                    DecideDraw.getColor(255, open * (0.3f + 0.7f * enabledAnim) * revealAnim), this.s(4.0f));

            boolean bindingNow = this.bindingModule == module;
            SatoshiAnim bindAnim = this.moduleBindAnim(module);
            bindAnim.setDirection(bindingNow);
            float bindActive = bindAnim.getOutput();
            String keyName = bindingNow ? "..." : DecideKeyNames.of(module.getKeyBind());
            float keyW = this.smoothModule(module, SLOT_BIND_W,
                    DecideDraw.textWidth(font, keyName, this.s(6.0f)) + this.s(9.0f));
            float keyX = itemX + itemW - this.s(24.0f) - keyW;
            float keyY = itemY + this.s(4.0f);
            boolean keyHovered = this.hovered(keyX, keyY, keyW, this.s(10.0f));
            SatoshiAnim keyHoverSat = this.moduleBindHoverAnim(module);
            keyHoverSat.setDirection(keyHovered);
            float keyHover = keyHoverSat.getOutput();
            float pulse = bindingNow
                    ? 0.55f + 0.45f * (float) Math.sin((System.currentTimeMillis() % 1000L) / 1000.0 * Math.PI * 2.0)
                    : 0.0f;
            float accent = Math.max(keyHover * 0.5f, bindActive);
            DecideDraw.rect(keyX, keyY, keyW, this.s(10.0f),
                    DecideDraw.overCol(DecideDraw.getColor(0, (0.12f + 0.13f * keyHover) * open * revealAnim),
                            DecideDraw.replAlpha(client, (0.15f + 0.2f * pulse) * open * revealAnim), bindActive),
                    this.s(3.0f));
            DecideDraw.outline(keyX, keyY, keyW, this.s(10.0f), this.s(0.5f),
                    DecideDraw.replAlpha(client, open * revealAnim * accent
                            * (bindingNow ? 0.4f + 0.6f * pulse : 1.0f)), this.s(3.0f));
            DecideDraw.glow(keyX, keyY, keyW, this.s(10.0f),
                    DecideDraw.replAlpha(client, 0.12f * open * revealAnim * bindActive * pulse),
                    this.s(3.0f), this.s(6.0f));
            DecideDraw.textCentered(font, keyName, keyX + keyW * 0.5f, keyY + this.s(1.5f), this.s(6.0f),
                    DecideDraw.replAlpha(DecideDraw.overCol(DecideDraw.getColor(255), client,
                            Math.max(bindActive, enabledAnim)), open * revealAnim
                            * (0.35f + 0.35f * enabledAnim + 0.3f * Math.max(keyHover, bindActive))));

            DecideDraw.textFading(font, module.getName(), itemX + this.s(5.0f), itemY + this.s(5.0f),
                    this.s(80.0f), DecideDraw.getColor(255,
                            open * revealAnim * (0.2f + 0.6f * enabledAnim + 0.2f * hoverAnim)), this.s(7.0f));
            String[] lines = this.descLines.get(module);
            if (lines != null && lines.length > 0) {
                DecideDraw.drawWrappedLines(font, lines, itemX + this.s(5.0f), itemY + this.s(16.0f),
                        this.s(6.0f), DecideDraw.getColor(255,
                                open * revealAnim * (0.1f + 0.5f * enabledAnim + 0.2f * hoverAnim)));
            }
            DecideDraw.rect(itemX, itemY, itemW, itemH, DecideDraw.replAlpha(client,
                    0.3f * open * selectAnim * (0.2f + 0.5f * enabledAnim) * revealAnim), this.s(5.0f));

            this.addHit(HIT_MODULE, i, 0, itemX, itemY, itemW, itemH);
            this.addHit(HIT_MODULE_BIND, i, 0, keyX - this.s(2.0f), itemY + this.s(2.0f),
                    keyW + this.s(4.0f), this.s(14.0f));
            itemY += (itemH + this.s(5.0f)) * revealAnim;
        }
        float contentHeight = itemY + this.listScroll - y;
        this.listMaxScroll = Math.max(0.0f, contentHeight - (h - this.s(4.0f)));
        if (this.listScrollTarget > this.listMaxScroll) {
            this.listScrollTarget = this.listMaxScroll;
        }
        float emptyAnim = this.searchEmpty.getOutput();
        if (emptyAnim > 0.01f) {
            float centerX = x + w * 0.5f;
            float centerY = y + h * 0.5f - this.s(8.0f) + this.s(6.0f) - this.s(6.0f) * emptyAnim;
            DecideDraw.textCentered(DecideFonts.guiIcon(), "b", centerX, centerY, this.s(8.0f),
                    DecideDraw.replAlpha(client, open * emptyAnim * 0.4f));
            DecideDraw.textCentered(font, "Ничего не найдено", centerX, centerY + this.s(14.0f), this.s(7.0f),
                    DecideDraw.getColor(255, open * emptyAnim * 0.5f));
        }
        BeginFrameHelper.popScissor();
    }

    // ------------------------------------------------------------------ settings

    private void drawSettingsPanel() {
        float open = this.openAnim.get();
        this.drawColumnBackground();
        float x = columnX();
        float y = this.listY();
        float w = columnW();
        float h = this.listH();
        int client = DecideTheme.currentClientColor();
        TextHeightHelper font = DecideFonts.sfRegular();

        if (this.selectedModule == null) {
            DecideDraw.textCentered(font, "ПКМ по модулю — настройки", x + w * 0.5f, y + h * 0.5f - this.s(4.0f),
                    this.s(7.0f), DecideDraw.getColor(255, open * 0.22f));
            return;
        }
        this.settingScroll += (this.settingScrollTarget - this.settingScroll) * 0.2f;
        if (Math.abs(this.settingScrollTarget - this.settingScroll) < 0.05f) {
            this.settingScroll = this.settingScrollTarget;
        }
        if (this.visibleSettings.isEmpty()) {
            DecideDraw.textCentered(font, "Нет доступных настроек", x + w * 0.5f, y + h * 0.5f - this.s(4.0f),
                    this.s(7.0f), DecideDraw.getColor(255, open * 0.22f));
            return;
        }
        float contentX = x + this.s(10.0f);
        float contentY = y + this.s(10.0f) - this.settingScroll;
        float contentW = w - this.s(20.0f);
        float startY = contentY;
        BeginFrameHelper.pushScissor(x, y, w, h);
        for (int i = 0; i < this.visibleSettings.size(); i++) {
            Setting<?> setting = this.visibleSettings.get(i);
            float revealAnim = this.settingRevealAnim(setting).getOutput();
            if (revealAnim <= 0.01f) {
                continue;
            }
            contentY = this.drawSetting(setting, i, contentX, contentY, contentW, revealAnim, open, client);
        }
        float contentHeight = contentY + this.settingScroll - startY + this.s(10.0f);
        this.settingMaxScroll = Math.max(0.0f, contentHeight - (h - this.s(16.0f)));
        if (this.settingScrollTarget > this.settingMaxScroll) {
            this.settingScrollTarget = this.settingMaxScroll;
        }
        BeginFrameHelper.popScissor();
    }

    private float drawSetting(Setting<?> setting, int index, float x, float y, float w,
                              float revealAnim, float open, int client) {
        if (setting instanceof NumberSetting number) {
            return this.drawSlider(number, index, x, y, w, revealAnim, open, client);
        }
        if (setting instanceof ModeSetting mode) {
            return this.drawMode(mode, index, x, y, w, revealAnim, open, client);
        }
        if (setting instanceof MultiSelectSetting multi) {
            return this.drawMulti(multi, index, x, y, w, revealAnim, open, client);
        }
        if (setting instanceof BooleanSetting bool) {
            return this.drawBoolean(bool, index, x, y, w, revealAnim, open, client);
        }
        if (setting instanceof ActionSetting action) {
            return this.drawAction(action, index, x, y, w, revealAnim, open, client);
        }
        if (setting instanceof KeyBindSetting bind) {
            return this.drawBind(bind, index, x, y, w, revealAnim, open, client);
        }
        if (setting instanceof StringSetting text) {
            return this.drawString(text, index, x, y, w, revealAnim, open, client);
        }
        if (setting instanceof ColorSetting color) {
            return this.drawColor(color, index, x, y, w, revealAnim, open, client);
        }
        return y;
    }

    private void drawRowBackground(float x, float y, float w, float h, float alpha, float open,
                                   float hoverAnim, int client) {
        DecideDraw.glow(x, y, w, h, DecideDraw.getColor(0, 0.06f * open * alpha), this.s(5.0f), this.s(7.0f));
        DecideDraw.rect(x, y, w, h, DecideDraw.getColor(40, 0.15f * open * alpha), this.s(5.0f));
        DecideDraw.outline(x, y, w, h, this.s(0.5f),
                DecideDraw.replAlpha(client, open * alpha * hoverAnim), this.s(5.0f));
    }

    private void drawRowName(float x, float y, String name, float alpha, float open, float hoverAnim,
                             TextHeightHelper font) {
        DecideDraw.text(font, name, x + this.s(6.0f), y + this.s(5.0f), this.s(6.5f),
                DecideDraw.getColor(255, open * alpha * (0.5f + 0.5f * hoverAnim)));
    }

    private float drawSlider(NumberSetting setting, int index, float x, float y, float w,
                             float revealAnim, float open, int client) {
        TextHeightHelper font = DecideFonts.sfRegular();
        float height = this.s(25.0f);
        boolean hovered = this.hovered(x, y, w, height);
        SmoothAnim hover = this.settingHoverAnim(setting);
        hover.run(hovered ? 1.0f : 0.0f, 0.16f, Easing.QUAD_OUT);
        float hoverAnim = hover.get();
        this.drawRowBackground(x, y, w, height, revealAnim, open, hoverAnim, client);
        this.drawRowName(x, y, setting.getName(), revealAnim, open, hoverAnim, font);
        String value = this.numberLabel(setting);
        DecideDraw.text(font, value, x + w - this.s(6.0f) - DecideDraw.textWidth(font, value, this.s(6.0f)),
                y + this.s(5.25f), this.s(6.0f),
                DecideDraw.replAlpha(client, open * revealAnim * (0.6f + 0.4f * hoverAnim)));
        float track = w - this.s(12.0f);
        float ratio = (float) setting.ratio();
        float smoothRatio = this.smoothSetting(setting, SLOT_HUE, ratio);
        if (this.draggingSlider == setting) {
            float percent = DecideDraw.clamp((this.mouseX - (x + this.s(6.0f))) / track, 0.0f, 1.0f);
            double raw = setting.getMin() + (setting.getMax() - setting.getMin()) * percent;
            double step = setting.getStep();
            double snapped = step > 0.0 ? Math.round(raw / step) * step : raw;
            snapped = Math.max(setting.getMin(), Math.min(setting.getMax(), snapped));
            if (Math.abs(snapped - setting.get()) > 1.0E-6) {
                setting.set(snapped);
                DecideSounds.sliderTick(percent);
            }
            smoothRatio = percent;
        }
        DecideDraw.rect(x + this.s(6.0f), y + this.s(16.5f), track, this.s(3.0f),
                DecideDraw.getColor(0, open * revealAnim * 0.15f), this.s(1.5f));
        DecideDraw.rect(x + this.s(6.0f), y + this.s(16.5f), track * smoothRatio, this.s(3.0f),
                DecideDraw.replAlpha(client, open * revealAnim * (0.5f + 0.5f * hoverAnim)), this.s(1.5f));
        float knob = x + this.s(6.0f) + track * smoothRatio;
        DecideDraw.glow(knob - this.s(3.0f), y + this.s(15.0f), this.s(6.0f), this.s(6.0f),
                DecideDraw.replAlpha(client, 0.12f * open * revealAnim * hoverAnim), this.s(6.0f), this.s(6.0f));
        DecideDraw.rect(knob - this.s(3.0f), y + this.s(15.0f), this.s(6.0f), this.s(6.0f),
                DecideDraw.getColor((int) (200.0f + 55.0f * hoverAnim), open * revealAnim), this.s(6.0f));
        DecideDraw.rect(knob - this.s(2.0f), y + this.s(16.0f), this.s(4.0f), this.s(4.0f),
                DecideDraw.replAlpha(client, open * revealAnim * (0.7f + 0.3f * hoverAnim)), this.s(6.0f));
        this.addHit(HIT_ROW, index, 0, x, y, w, height);
        return y + this.s(30.0f) * revealAnim;
    }

    private String numberLabel(NumberSetting setting) {
        double value = setting.get();
        double[] cached = this.numberValues.get(setting);
        String label = this.numberLabels.get(setting);
        if (label != null && cached != null && cached[0] == value) {
            return label;
        }
        label = this.formatNumber(setting);
        this.numberLabels.put(setting, label);
        if (cached == null) {
            this.numberValues.put(setting, new double[]{value});
        } else {
            cached[0] = value;
        }
        return label;
    }

    private String formatNumber(NumberSetting setting) {
        double value = setting.get();
        double step = setting.getStep();
        StringBuilder builder = new StringBuilder(12);
        if (step >= 1.0) {
            builder.append(Math.round(value));
        } else if (step >= 0.1) {
            long scaled = Math.round(value * 10.0);
            builder.append(scaled / 10L).append('.').append(Math.abs(scaled % 10L));
        } else {
            long scaled = Math.round(value * 100.0);
            long fraction = Math.abs(scaled % 100L);
            builder.append(scaled / 100L).append('.');
            if (fraction < 10L) {
                builder.append('0');
            }
            builder.append(fraction);
        }
        String suffix = setting.getSuffix();
        if (suffix != null && !suffix.isEmpty()) {
            builder.append(suffix);
        }
        return builder.toString();
    }

    private float drawMode(ModeSetting setting, int index, float x, float y, float w,
                           float revealAnim, float open, int client) {
        TextHeightHelper font = DecideFonts.sfRegular();
        boolean expanded = this.expandedChips.contains(setting.getName());
        SatoshiAnim expandAnim = this.expandAnim(setting);
        expandAnim.setDirection(expanded);
        float expand = expandAnim.getOutput();
        float chipsHeight = this.chipsHeight(setting.getModes(), w - this.s(12.0f), font) + this.s(4.0f);
        float height = this.s(15.0f) + chipsHeight * expand;
        boolean hovered = this.hovered(x, y, w, this.s(15.0f));
        SmoothAnim hover = this.settingHoverAnim(setting);
        hover.run(hovered ? 1.0f : 0.0f, 0.16f, Easing.QUAD_OUT);
        float hoverAnim = hover.get();
        this.drawRowBackground(x, y, w, height, revealAnim, open, hoverAnim, client);
        String current = setting.get();
        DecideDraw.text(font, setting.getName(), x + this.s(6.0f), y + this.s(5.0f), this.s(6.5f),
                DecideDraw.getColor(255, open * revealAnim * (0.5f + 0.5f * hoverAnim)));
        float arrowW = this.s(9.0f);
        float currentW = DecideDraw.textWidth(font, current, this.s(6.0f));
        DecideDraw.text(font, current, x + w - this.s(6.0f) - arrowW - currentW, y + this.s(5.25f), this.s(6.0f),
                DecideDraw.replAlpha(client, open * revealAnim * (0.6f + 0.4f * hoverAnim)));
        float arrowX = x + w - this.s(6.0f) - arrowW * 0.5f;
        float arrowY = y + this.s(7.0f) + (expanded ? this.s(1.0f) : 0.0f);
        DecideDraw.rect(arrowX, arrowY, this.s(5.0f), this.s(1.0f),
                DecideDraw.replAlpha(client, open * revealAnim * 0.7f), this.s(0.5f));
        if (expand > 0.01f) {
            this.drawChips(setting, setting.getModes(), current, x, y + this.s(15.0f), w,
                    revealAnim * expand, open, client, font, hoverAnim);
        }
        this.addChipHits(setting.getModes(), index, x, y + this.s(15.0f), w, font, expanded ? revealAnim : 0.0f);
        this.addHit(HIT_ROW, index, 0, x, y, w, this.s(15.0f));
        return y + (height + this.s(5.0f)) * revealAnim;
    }

    private float drawMulti(MultiSelectSetting setting, int index, float x, float y, float w,
                            float revealAnim, float open, int client) {
        TextHeightHelper font = DecideFonts.sfRegular();
        List<String> modes = setting.getModes();
        float chipsHeight = this.chipsHeight(modes, w - this.s(12.0f), font) + this.s(4.0f);
        float height = this.s(15.0f) + chipsHeight;
        boolean hovered = this.hovered(x, y, w, height);
        SmoothAnim hover = this.settingHoverAnim(setting);
        hover.run(hovered ? 1.0f : 0.0f, 0.16f, Easing.QUAD_OUT);
        float hoverAnim = hover.get();
        this.drawRowBackground(x, y, w, height, revealAnim, open, hoverAnim, client);
        String counter = this.multiLabels.get(setting);
        if (counter == null) {
            counter = this.multiLabel(setting);
            this.multiLabels.put(setting, counter);
        }
        DecideDraw.textFading(font, setting.getName(), x + this.s(6.0f), y + this.s(5.0f),
                w - DecideDraw.textWidth(font, counter, this.s(6.0f)) - this.s(16.0f),
                DecideDraw.getColor(255, open * revealAnim * (0.5f + 0.5f * hoverAnim)), this.s(6.5f));
        DecideDraw.text(font, counter, x + w - this.s(6.0f) - DecideDraw.textWidth(font, counter, this.s(6.0f)),
                y + this.s(5.25f), this.s(6.0f),
                DecideDraw.replAlpha(client, open * revealAnim * (0.6f + 0.4f * hoverAnim)));
        this.drawChips(setting, modes, null, x, y + this.s(15.0f), w, revealAnim, open, client, font, hoverAnim);
        this.addChipHits(modes, index, x, y + this.s(15.0f), w, font, revealAnim);
        this.addHit(HIT_ROW, index, 0, x, y, w, height);
        return y + (height + this.s(5.0f)) * revealAnim;
    }

    private float drawBoolean(BooleanSetting setting, int index, float x, float y, float w,
                              float revealAnim, float open, int client) {
        TextHeightHelper font = DecideFonts.sfRegular();
        float height = this.s(16.0f);
        boolean hovered = this.hovered(x, y, w, height);
        SmoothAnim hover = this.settingHoverAnim(setting);
        hover.run(hovered ? 1.0f : 0.0f, 0.16f, Easing.QUAD_OUT);
        SmoothAnim value = this.settingValueAnim(setting, setting.get());
        value.run(setting.get() ? 1.0f : 0.0f, 0.16f, Easing.QUAD_OUT);
        float valueAnim = value.get();
        float hoverAnim = hover.get();
        this.drawRowBackground(x, y, w, height, revealAnim, open, hoverAnim, client);
        this.drawRowName(x, y, setting.getName(), revealAnim, open, hoverAnim, font);
        float switchX = x + w - this.s(18.0f);
        DecideDraw.rect(switchX, y + this.s(4.5f), this.s(12.0f), this.s(7.0f),
                DecideDraw.overCol(DecideDraw.getColor(0, 0.2f * open * revealAnim),
                        DecideDraw.replAlpha(client, open * revealAnim * (0.5f + 0.5f * hoverAnim)), valueAnim),
                this.s(3.5f));
        DecideDraw.rect(switchX + this.s(1.25f) + this.s(4.5f) * valueAnim, y + this.s(5.75f),
                this.s(4.5f), this.s(4.5f),
                DecideDraw.getColor(255, open * revealAnim * (0.2f + 0.4f * valueAnim + 0.4f * hoverAnim)),
                this.s(2.5f));
        this.addHit(HIT_ROW, index, 0, x, y, w, height);
        return y + this.s(21.0f) * revealAnim;
    }

    private float drawAction(ActionSetting setting, int index, float x, float y, float w,
                             float revealAnim, float open, int client) {
        TextHeightHelper font = DecideFonts.sfRegular();
        float height = this.s(16.0f);
        boolean hovered = this.hovered(x, y, w, height);
        SmoothAnim hover = this.settingHoverAnim(setting);
        hover.run(hovered ? 1.0f : 0.0f, 0.16f, Easing.QUAD_OUT);
        float hoverAnim = hover.get();
        DecideDraw.glow(x, y, w, height, DecideDraw.getColor(0, 0.06f * open * revealAnim), this.s(5.0f), this.s(7.0f));
        DecideDraw.rect(x, y, w, height,
                DecideDraw.overCol(DecideDraw.getColor(40, 0.15f * open * revealAnim),
                        DecideDraw.replAlpha(client, 0.25f * open * revealAnim), hoverAnim), this.s(5.0f));
        DecideDraw.outline(x, y, w, height, this.s(0.5f),
                DecideDraw.replAlpha(client, open * revealAnim * hoverAnim), this.s(5.0f));
        String label = setting.getActionLabel();
        DecideDraw.textCentered(font, label == null ? setting.getName() : label, x + w * 0.5f,
                y + this.s(4.0f), this.s(6.5f),
                DecideDraw.getColor(255, open * revealAnim * (0.5f + 0.5f * hoverAnim)));
        this.addHit(HIT_ROW, index, 0, x, y, w, height);
        return y + this.s(21.0f) * revealAnim;
    }

    private float drawBind(KeyBindSetting setting, int index, float x, float y, float w,
                           float revealAnim, float open, int client) {
        TextHeightHelper font = DecideFonts.sfRegular();
        float height = this.s(16.0f);
        boolean hovered = this.hovered(x, y, w, height);
        SmoothAnim hover = this.settingHoverAnim(setting);
        hover.run(hovered ? 1.0f : 0.0f, 0.16f, Easing.QUAD_OUT);
        float hoverAnim = hover.get();
        this.drawRowBackground(x, y, w, height, revealAnim, open, hoverAnim, client);
        boolean binding = this.bindingSetting == setting;
        SatoshiAnim bindAnim = this.bindAnim(setting);
        bindAnim.setDirection(binding);
        float bindActive = bindAnim.getOutput();
        String label = binding ? "..." : DecideKeyNames.of(setting.get());
        float badgeW = this.smoothSetting(setting, SLOT_BIND_W,
                DecideDraw.textWidth(font, label, this.s(6.0f)) + this.s(10.0f));
        float badgeX = x + w - this.s(6.0f) - badgeW;
        DecideDraw.textFading(font, setting.getName(), x + this.s(6.0f), y + this.s(5.0f),
                w - badgeW - this.s(16.0f),
                DecideDraw.getColor(255, open * revealAnim * (0.5f + 0.5f * hoverAnim)), this.s(6.5f));
        DecideDraw.rect(badgeX, y + this.s(3.0f), badgeW, this.s(10.0f),
                DecideDraw.overCol(DecideDraw.getColor(0, 0.25f * open * revealAnim),
                        DecideDraw.replAlpha(client, 0.15f * open * revealAnim), bindActive), this.s(3.0f));
        DecideDraw.outline(badgeX, y + this.s(3.0f), badgeW, this.s(10.0f), this.s(0.5f),
                DecideDraw.replAlpha(client, open * revealAnim * bindActive), this.s(3.0f));
        DecideDraw.glow(badgeX, y + this.s(3.0f), badgeW, this.s(10.0f),
                DecideDraw.replAlpha(client, 0.12f * open * revealAnim * bindActive), this.s(3.0f), this.s(6.0f));
        DecideDraw.textCentered(font, label, badgeX + badgeW * 0.5f, y + this.s(4.5f), this.s(6.0f),
                DecideDraw.replAlpha(DecideDraw.overCol(DecideDraw.getColor(255), client, bindActive),
                        open * revealAnim * (0.45f + 0.55f * Math.max(bindActive, hoverAnim))));
        this.addHit(HIT_BIND_CHIP, index, 0, badgeX - this.s(2.0f), y + this.s(1.0f),
                badgeW + this.s(4.0f), this.s(14.0f));
        return y + this.s(21.0f) * revealAnim;
    }

    private float drawString(StringSetting setting, int index, float x, float y, float w,
                             float revealAnim, float open, int client) {
        TextHeightHelper font = DecideFonts.sfRegular();
        float height = this.s(16.0f);
        boolean hovered = this.hovered(x, y, w, height);
        SmoothAnim hover = this.settingHoverAnim(setting);
        hover.run(hovered ? 1.0f : 0.0f, 0.16f, Easing.QUAD_OUT);
        float hoverAnim = hover.get();
        this.drawRowBackground(x, y, w, height, revealAnim, open, hoverAnim, client);
        boolean editing = this.editingString == setting;
        SatoshiAnim editAnim = this.editAnim(setting);
        editAnim.setDirection(editing);
        float editActive = editAnim.getOutput();
        String value = editing ? this.stringBuffer.toString() : setting.get();
        if (value == null || value.isEmpty()) {
            value = "...";
        }
        float badgeW = this.smoothSetting(setting, SLOT_EDIT_W,
                Math.min(DecideDraw.textWidth(font, value, this.s(6.0f)) + this.s(10.0f), w * 0.55f));
        float badgeX = x + w - this.s(6.0f) - badgeW;
        DecideDraw.textFading(font, setting.getName(), x + this.s(6.0f), y + this.s(5.0f),
                w - badgeW - this.s(16.0f),
                DecideDraw.getColor(255, open * revealAnim * (0.5f + 0.5f * hoverAnim)), this.s(6.5f));
        DecideDraw.rect(badgeX, y + this.s(3.0f), badgeW, this.s(10.0f),
                DecideDraw.overCol(DecideDraw.getColor(0, 0.25f * open * revealAnim),
                        DecideDraw.replAlpha(client, 0.15f * open * revealAnim), editActive), this.s(3.0f));
        DecideDraw.outline(badgeX, y + this.s(3.0f), badgeW, this.s(10.0f), this.s(0.5f),
                DecideDraw.replAlpha(client, open * revealAnim * editActive), this.s(3.0f));
        DecideDraw.glow(badgeX, y + this.s(3.0f), badgeW, this.s(10.0f),
                DecideDraw.replAlpha(client, 0.12f * open * revealAnim * editActive), this.s(3.0f), this.s(6.0f));
        DecideDraw.textFading(font, value, badgeX + this.s(5.0f), y + this.s(4.5f), badgeW - this.s(8.0f),
                DecideDraw.getColor(255, open * revealAnim * (0.45f + 0.55f * Math.max(editActive, hoverAnim))),
                this.s(6.0f));
        if (editing && this.caretBlink) {
            DecideDraw.rect(badgeX + this.s(5.0f) + Math.min(DecideDraw.textWidth(font, value, this.s(6.0f)),
                            badgeW - this.s(9.0f)) + this.s(0.5f), y + this.s(4.5f), this.s(0.6f), this.s(7.0f),
                    DecideDraw.replAlpha(client, open * revealAnim), this.s(0.3f));
        }
        this.addHit(HIT_STRING_CHIP, index, 0, badgeX - this.s(2.0f), y + this.s(1.0f),
                badgeW + this.s(4.0f), this.s(14.0f));
        return y + this.s(21.0f) * revealAnim;
    }

    private float drawColor(ColorSetting setting, int index, float x, float y, float w,
                            float revealAnim, float open, int client) {
        TextHeightHelper font = DecideFonts.sfRegular();
        boolean expanded = this.openPickers.contains(setting);
        SatoshiAnim pickerAnim = this.pickerAnim(setting);
        pickerAnim.setDirection(expanded);
        float picker = pickerAnim.getOutput();
        float height = this.s(16.0f) + picker * this.s(35.0f);
        boolean hovered = this.hovered(x, y, w, height);
        SmoothAnim hover = this.settingHoverAnim(setting);
        hover.run(hovered ? 1.0f : 0.0f, 0.16f, Easing.QUAD_OUT);
        float hoverAnim = hover.get();
        this.drawRowBackground(x, y, w, height, revealAnim, open, hoverAnim, client);
        int argb = setting.get();
        DecideDraw.textFading(font, setting.getName(), x + this.s(6.0f), y + this.s(5.0f), w - this.s(34.0f),
                DecideDraw.getColor(255, open * revealAnim * (0.5f + 0.5f * hoverAnim)), this.s(6.5f));
        float swatchX = x + w - this.s(15.0f);
        DecideDraw.rect(swatchX, y + this.s(4.0f), this.s(8.0f), this.s(8.0f),
                DecideDraw.replAlpha(argb, open * revealAnim), this.s(8.0f));
        DecideDraw.outline(swatchX - this.s(0.75f), y + this.s(3.25f), this.s(9.5f), this.s(9.5f),
                this.s(0.25f), DecideDraw.replAlpha(argb, open * revealAnim * (0.4f + 0.6f * hoverAnim)),
                this.s(8.0f));
        this.addHit(HIT_ROW, index, 0, x, y, w, this.s(16.0f));
        if (picker <= 0.01f) {
            return y + (height + this.s(5.0f)) * revealAnim;
        }
        float[] box = this.settingSmooth(setting);
        DecideDraw.rgbToHsb((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, this.hsv);
        float hue = this.smoothSlot(box, SLOT_HUE, this.hsv[0]);
        float saturation = this.smoothSlot(box, SLOT_SAT, this.hsv[1]);
        float brightness = this.smoothSlot(box, SLOT_BRI, this.hsv[2]);
        float barX = x + this.s(6.0f);
        float barW = w - this.s(12.0f);
        float alpha = open * revealAnim * picker;
        for (int bar = 0; bar < 3; bar++) {
            float barY = y + this.s(18.0f) + bar * this.s(12.0f);
            float mid = barX + barW * 0.5f;
            DecideDraw.gradientHRect(barX, barY, barW * 0.5f, this.s(4.0f),
                    this.barColor(bar, 0.0f, hue, saturation, brightness, alpha),
                    this.barColor(bar, 0.5f, hue, saturation, brightness, alpha),
                    0.0f, 0.0f, 0.0f, 0.0f);
            DecideDraw.gradientHRect(mid, barY, barW * 0.5f, this.s(4.0f),
                    this.barColor(bar, 0.5f, hue, saturation, brightness, alpha),
                    this.barColor(bar, 1.0f, hue, saturation, brightness, alpha),
                    0.0f, 0.0f, 0.0f, 0.0f);
            float marker = bar == 0 ? hue : bar == 1 ? saturation : brightness;
            float knobX = barX + barW * marker;
            DecideDraw.rect(knobX - this.s(3.0f), barY - this.s(1.0f), this.s(6.0f), this.s(6.0f),
                    DecideDraw.getColor(255, alpha), this.s(6.0f));
            DecideDraw.rect(knobX - this.s(2.0f), barY, this.s(4.0f), this.s(4.0f),
                    this.barColor(bar, marker, hue, saturation, brightness, alpha), this.s(6.0f));
            this.addHit(HIT_COLOR_BAR, index, bar, barX, barY - this.s(3.0f), barW, this.s(10.0f));
        }
        return y + (height + this.s(5.0f)) * revealAnim;
    }

    private int barColor(int bar, float t, float hue, float saturation, float brightness, float alpha) {
        int rgb = bar == 0 ? DecideDraw.hsbToRgb(t, 1.0f, 1.0f)
                : bar == 1 ? DecideDraw.hsbToRgb(hue, t, brightness)
                : DecideDraw.hsbToRgb(hue, saturation, t);
        return DecideDraw.replAlpha(rgb, alpha);
    }

    private float chipsHeight(List<String> values, float width, TextHeightHelper font) {
        float px = 0.0f;
        float py = 0.0f;
        float size = this.s(6.0f);
        for (int i = 0; i < values.size(); i++) {
            float chipW = DecideDraw.textWidth(font, values.get(i), size) + this.s(8.0f);
            if (px + chipW > width && px > 0.0f) {
                px = 0.0f;
                py += this.s(12.0f);
            }
            px += chipW + this.s(3.0f);
        }
        return py + this.s(10.0f);
    }

    /** Single layout pass shared by chip drawing and chip hit testing: no allocations. */
    private int layoutChips(List<String> values, float width, TextHeightHelper font) {
        float px = 0.0f;
        float py = 0.0f;
        float size = this.s(6.0f);
        int count = Math.min(values.size(), MAX_CHIPS);
        for (int i = 0; i < count; i++) {
            float chipW = DecideDraw.textWidth(font, values.get(i), size) + this.s(8.0f);
            if (px + chipW > width && px > 0.0f) {
                px = 0.0f;
                py += this.s(12.0f);
            }
            this.chipDX[i] = px;
            this.chipDY[i] = py;
            this.chipDW[i] = chipW;
            px += chipW + this.s(3.0f);
        }
        return count;
    }

    private void drawChips(Setting<?> setting, List<String> values, String current,
                           float x, float y, float w, float alpha, float open, int client,
                           TextHeightHelper font, float hoverAnim) {
        int count = this.layoutChips(values, w - this.s(12.0f), font);
        boolean multi = setting instanceof MultiSelectSetting;
        float size = this.s(6.0f);
        for (int i = 0; i < count; i++) {
            String value = values.get(i);
            boolean selected = multi
                    ? ((MultiSelectSetting) setting).isSelected(value)
                    : value.equals(current);
            SatoshiAnim chip = this.chipAnim(setting, value);
            chip.setDirection(selected);
            float sel = chip.getOutput();
            float chipX = x + this.s(6.0f) + this.chipDX[i];
            float chipY = y + this.chipDY[i];
            DecideDraw.rect(chipX, chipY, this.chipDW[i], this.s(10.0f),
                    DecideDraw.overCol(DecideDraw.getColor(0, 0.25f * open * alpha),
                            DecideDraw.replAlpha(client, open * alpha * (0.5f + 0.5f * hoverAnim)), sel),
                    this.s(3.0f));
            DecideDraw.textCentered(font, value, chipX + this.chipDW[i] * 0.5f, chipY + this.s(1.5f), size,
                    DecideDraw.getColor(255, open * alpha * (0.35f + 0.65f * sel) * (0.7f + 0.3f * hoverAnim)));
        }
    }

    private void addChipHits(List<String> values, int index, float x, float y, float w,
                             TextHeightHelper font, float alpha) {
        if (alpha <= 0.01f) {
            return;
        }
        int count = this.layoutChips(values, w - this.s(12.0f), font);
        for (int i = 0; i < count; i++) {
            this.addHit(HIT_CHIP, index, i, x + this.s(6.0f) + this.chipDX[i], y + this.chipDY[i],
                    this.chipDW[i], this.s(10.0f));
        }
    }

    // ------------------------------------------------------------------ cosmetics / configs / markers

    private void drawSpecial() {
        float open = this.openAnim.get();
        TextHeightHelper font = DecideFonts.sfRegular();
        TextHeightHelper icons = DecideFonts.guiIcon();
        int client = DecideTheme.currentClientColor();
        if (this.active == DecideCategory.COSMETICS) {
            this.drawCosmeticTabs(open, font, client);
            this.drawColumnBackground();
            this.drawCosmeticGrid(open, font, client);
            return;
        }
        this.drawListBackground();
        this.drawColumnBackground();
        if (this.active == DecideCategory.CONFIGS) {
            this.drawConfigForm(open, font, client);
            this.drawConfigList(open, font, icons, client);
        } else {
            this.drawMarkerForm(open, font, client);
            this.drawMarkerList(open, font, icons, client);
        }
    }

    private void drawCosmeticTabs(float open, TextHeightHelper font, int client) {
        this.drawListBackground();
        this.ensureCosmeticCounts();
        float x = this.listX();
        float y = this.listY();
        float w = this.listW();
        float h = this.listH();
        DecideDraw.text(font, "Категории", x + this.s(10.0f), y + this.s(8.0f), this.s(6.5f),
                DecideDraw.getColor(160, 0.6f * open));
        DecideDraw.rect(x + this.s(8.0f), y + this.s(18.0f), w - this.s(16.0f), this.s(0.5f),
                DecideDraw.getColor(255, 0.05f * open), this.s(0.25f));
        float rowH = this.s(18.0f);
        float gap = this.s(2.0f);
        float pad = this.s(6.0f);
        float tabX = x + pad;
        float tabW = w - pad * 2.0f;
        float top = y + this.s(20.0f);
        float view = Math.max(0.0f, y + h - pad - top);
        float contentHeight = COSMETIC_NAV.length * (rowH + gap) - gap;
        this.cosmeticsTabMaxScroll = Math.max(0.0f, contentHeight - view);
        this.cosmeticsTabScrollTarget = DecideDraw.clamp(this.cosmeticsTabScrollTarget, 0.0f,
                this.cosmeticsTabMaxScroll);
        this.cosmeticsTabScroll += (this.cosmeticsTabScrollTarget - this.cosmeticsTabScroll) * 0.2f;
        if (Math.abs(this.cosmeticsTabScrollTarget - this.cosmeticsTabScroll) < 0.05f) {
            this.cosmeticsTabScroll = this.cosmeticsTabScrollTarget;
        }
        BeginFrameHelper.pushScissor(tabX, top, tabW, view);
        float rowY = top - this.cosmeticsTabScroll;
        for (int i = 0; i < COSMETIC_NAV.length; i++) {
            boolean visible = rowY + rowH > top && rowY < top + view;
            boolean selected = i == this.activeCosmeticNav;
            SatoshiAnim selectAnim = this.cosmeticSelectAnims[i];
            selectAnim.setDirection(selected);
            SatoshiAnim hoverAnim = this.cosmeticHoverAnims[i];
            hoverAnim.setDirection(visible && this.hovered(tabX, rowY, tabW, rowH));
            float sel = selectAnim.getOutput();
            float hov = hoverAnim.getOutput();
            if (visible) {
                DecideDraw.glow(tabX, rowY, tabW, rowH,
                        DecideDraw.getColor(0, 0.04f * open * (0.5f + 0.5f * sel)), this.s(5.0f), this.s(6.0f));
                DecideDraw.rect(tabX, rowY, tabW, rowH,
                        DecideDraw.overCol(DecideDraw.getColor(35, 0.15f * open),
                                DecideDraw.getColor(55, 0.3f * open), Math.max(hov, sel)), this.s(5.0f));
                if (sel > 0.01f || hov > 0.01f) {
                    DecideDraw.outline(tabX, rowY, tabW, rowH, this.s(0.5f),
                            DecideDraw.replAlpha(client, open * (0.8f * sel + 0.3f * hov)), this.s(5.0f));
                }
                DecideDraw.text(font, COSMETIC_LABELS[i], tabX + this.s(8.0f), rowY + this.s(5.0f), this.s(7.0f),
                        DecideDraw.overCol(DecideDraw.getColor(200, 0.8f * open), client, sel));
                float badgeW = DecideDraw.textWidth(font, this.cosmeticCountLabels[i], this.s(6.0f)) + this.s(8.0f);
                float badgeX = tabX + tabW - this.s(6.0f) - badgeW;
                DecideDraw.rect(badgeX, rowY + this.s(3.0f), badgeW, this.s(12.0f),
                        DecideDraw.overCol(DecideDraw.getColor(0, 0.25f * open),
                                DecideDraw.multAlpha(client, 0.25f * open), sel), this.s(3.0f));
                DecideDraw.textCentered(font, this.cosmeticCountLabels[i], badgeX + badgeW * 0.5f,
                        rowY + this.s(6.0f), this.s(6.0f),
                        DecideDraw.overCol(DecideDraw.getColor(160, 0.7f * open), client, sel));
                this.addHit(HIT_COSMETIC_TAB, i, 0, tabX, rowY, tabW, rowH);
            }
            rowY += rowH + gap;
        }
        BeginFrameHelper.popScissor();
    }

    private void drawCosmeticGrid(float open, TextHeightHelper font, int client) {
        float x = columnX();
        float y = this.listY();
        float w = columnW();
        float h = this.listH();
        boolean swords = EntriesHelper.SWORDS.equals(COSMETIC_NAV[this.activeCosmeticNav]);
        if (this.equippedNav != this.activeCosmeticNav) {
            this.refreshEquipped();
        }
        if (this.searching()) {
            DecideDraw.text(font, "Поиск", x + this.s(10.0f), y + this.s(8.0f), this.s(7.5f),
                    DecideDraw.getColor(230, open));
            DecideDraw.textFading(font, this.queryText, x + this.s(48.0f), y + this.s(8.0f), w - this.s(130.0f),
                    DecideDraw.replAlpha(client, open), this.s(7.5f));
        } else {
            DecideDraw.text(font, COSMETIC_LABELS[this.activeCosmeticNav], x + this.s(10.0f), y + this.s(8.0f),
                    this.s(7.5f), DecideDraw.getColor(230, open));
        }
        if (!this.equippedName.isEmpty()) {
            DecideDraw.textFading(font, this.equippedName, x + w - this.s(64.0f), y + this.s(9.0f),
                    this.s(54.0f), DecideDraw.replAlpha(client, open * 0.75f), this.s(6.5f));
        }
        DecideDraw.text(font, this.equippedLabel, x + w - this.s(10.0f)
                        - DecideDraw.textWidth(font, this.equippedLabel, this.s(6.5f)),
                y + this.s(9.0f), this.s(6.5f),
                this.equippedName.isEmpty() ? DecideDraw.getColor(140, 0.6f * open)
                        : DecideDraw.replAlpha(client, open));
        DecideDraw.rect(x + this.s(8.0f), y + this.s(20.0f), w - this.s(16.0f), this.s(0.5f),
                DecideDraw.getColor(255, 0.05f * open), this.s(0.25f));
        float gridY = y + this.s(24.0f);
        float gridH = y + h - this.s(4.0f) - gridY;
        int total = swords ? this.swordEntries.size() : this.cosmeticEntries.size();
        if (total == 0) {
            DecideDraw.textCentered(font, "Ничего не найдено", x + w * 0.5f, gridY + gridH * 0.5f - this.s(4.0f),
                    this.s(7.0f), DecideDraw.getColor(160, 0.7f * open));
            return;
        }
        this.cosmeticsScroll += (this.cosmeticsScrollTarget - this.cosmeticsScroll) * 0.2f;
        if (Math.abs(this.cosmeticsScrollTarget - this.cosmeticsScroll) < 0.05f) {
            this.cosmeticsScroll = this.cosmeticsScrollTarget;
        }
        float pad = this.s(8.0f);
        float gap = this.s(8.0f);
        float cardW = (w - pad * 2.0f - gap) * 0.5f;
        float previewH = this.s(62.0f);
        float btnH = this.s(15.0f);
        float cardH = previewH + this.s(34.0f);
        CosmeticsModule module = this.cosmetics();
        BeginFrameHelper.pushScissor(x + this.s(2.0f), gridY, w - this.s(4.0f), gridH);
        for (int i = 0; i < total; i++) {
            int column = i % 2;
            int row = i / 2;
            float cardX = x + pad + column * (cardW + gap);
            float cardY = gridY + pad + row * (cardH + gap) - this.cosmeticsScroll;
            if (cardY + cardH < gridY || cardY > gridY + gridH) {
                continue;
            }
            boolean hovered = this.hovered(cardX, cardY, cardW, cardH);
            boolean isEquipped = swords
                    ? this.cosmetica.isSwordSelected(this.swordEntries.get(i))
                    : this.cosmetica.isSelected(this.cosmeticEntries.get(i));
            DecideDraw.glow(cardX, cardY, cardW, cardH, DecideDraw.getColor(0, 0.05f * open),
                    this.s(6.0f), this.s(6.0f));
            DecideDraw.rect(cardX, cardY, cardW, cardH, DecideDraw.getColor(28, 0.22f * open), this.s(6.0f));
            DecideDraw.outline(cardX, cardY, cardW, cardH, this.s(0.5f),
                    isEquipped ? DecideDraw.replAlpha(client, open * 0.85f)
                            : DecideDraw.getColor(255, (hovered ? 0.2f : 0.06f) * open), this.s(6.0f));
            float previewPad = this.s(4.0f);
            float previewX = cardX + previewPad;
            float previewY = cardY + previewPad;
            float previewW = cardW - previewPad * 2.0f;
            DecideDraw.rect(previewX, previewY, previewW, previewH, DecideDraw.getColor(12, 0.95f * open),
                    this.s(4.0f));
            if (module != null) {
                if (swords) {
                    DecideDraw.texture(this.swordEntries.get(i).texture(), previewX + this.s(10.0f),
                            previewY + this.s(6.0f), previewW - this.s(20.0f), previewH - this.s(12.0f),
                            DecideDraw.getColor(255, open));
                } else {
                    module.renderer().renderGuiPreview(this.cosmeticEntries.get(i).id(),
                            previewX, previewY, previewW, previewH);
                }
            }
            DecideDraw.outline(previewX, previewY, previewW, previewH, this.s(0.5f),
                    DecideDraw.getColor(40, 0.4f * open), this.s(4.0f));
            String name = swords ? this.swordEntries.get(i).name() : this.cosmeticEntries.get(i).name();
            if (name == null || name.isBlank()) {
                name = COSMETIC_LABELS[this.activeCosmeticNav];
            }
            DecideDraw.textFading(font, name, cardX + this.s(6.0f), cardY + previewH + this.s(6.5f),
                    cardW - this.s(12.0f), DecideDraw.getColor(220, open), this.s(6.2f));
            float btnW = cardW - this.s(10.0f);
            float btnX = cardX + this.s(5.0f);
            float btnY = cardY + cardH - btnH - this.s(5.0f);
            boolean btnHovered = this.hovered(btnX, btnY, btnW, btnH);
            if (isEquipped) {
                DecideDraw.glow(btnX, btnY, btnW, btnH, DecideDraw.replAlpha(client, open * 0.2f),
                        this.s(4.0f), this.s(5.0f));
                DecideDraw.rect(btnX, btnY, btnW, btnH,
                        DecideDraw.replAlpha(client, open * (btnHovered ? 0.4f : 0.28f)), this.s(4.0f));
                DecideDraw.outline(btnX, btnY, btnW, btnH, this.s(0.5f),
                        DecideDraw.replAlpha(client, open * 0.85f), this.s(4.0f));
                DecideDraw.textCentered(font, "Надето", btnX + btnW * 0.5f, btnY + this.s(4.0f), this.s(6.0f),
                        DecideDraw.getColor(255, open));
            } else {
                DecideDraw.rect(btnX, btnY, btnW, btnH,
                        DecideDraw.overCol(DecideDraw.getColor(42, 0.2f * open),
                                DecideDraw.getColor(65, 0.35f * open), btnHovered ? 1.0f : 0.0f), this.s(4.0f));
                DecideDraw.outline(btnX, btnY, btnW, btnH, this.s(0.5f),
                        btnHovered ? DecideDraw.replAlpha(client, open * 0.4f)
                                : DecideDraw.getColor(70, 0.15f * open), this.s(4.0f));
                DecideDraw.textCentered(font, "Использовать", btnX + btnW * 0.5f, btnY + this.s(4.0f), this.s(6.0f),
                        DecideDraw.getColor(190, open * (btnHovered ? 1.0f : 0.7f)));
            }
            this.addHit(HIT_COSMETIC_CARD, i, 0, cardX, cardY, cardW, cardH);
        }
        int rows = (total + 1) / 2;
        float contentHeight = pad * 2.0f + rows * (cardH + gap);
        this.cosmeticsMaxScroll = Math.max(0.0f, contentHeight - gridH);
        if (this.cosmeticsScrollTarget > this.cosmeticsMaxScroll) {
            this.cosmeticsScrollTarget = this.cosmeticsMaxScroll;
        }
        BeginFrameHelper.popScissor();
    }

    private void drawConfigForm(float open, TextHeightHelper font, int client) {
        float x = this.listX();
        float y = this.listY();
        float pad = this.s(8.0f);
        float inputH = this.s(18.0f);
        float gap = this.s(5.0f);
        float inputX = x + pad;
        float inputY = y + pad;
        float inputW = this.listW() - pad * 2.0f;
        this.drawInput(inputX, inputY, inputW, inputH, this.configInput, this.configInputFocused,
                "Имя конфига", open, font, client);
        float buttonY = inputY + inputH + gap;
        this.drawSmallButton(inputX, buttonY, inputW, inputH, "+ Добавить", open, font, client,
                this.hovered(inputX, buttonY, inputW, inputH));
        this.addHit(HIT_CFG_INPUT, 0, 0, inputX, inputY, inputW, inputH);
        this.addHit(HIT_CFG_ADD, 0, 0, inputX, buttonY, inputW, inputH);
    }

    private void drawConfigList(float open, TextHeightHelper font, TextHeightHelper icons, int client) {
        float x = columnX();
        float y = this.listY();
        float w = columnW();
        float h = this.listH();
        float pad = this.s(8.0f);
        float rowH = this.s(20.0f);
        float rowGap = this.s(3.0f);
        float listX = x + pad;
        float listY = y + pad;
        float listW = w - pad * 2.0f;
        float listH = h - pad * 2.0f;
        if (this.configRows.isEmpty()) {
            DecideDraw.textCentered(font, "Нет конфигов", listX + listW * 0.5f, listY + listH * 0.5f - this.s(4.0f),
                    this.s(7.0f), DecideDraw.getColor(255, 0.15f * open));
            return;
        }
        float contentHeight = this.configRows.size() * (rowH + rowGap);
        float maxScroll = Math.max(0.0f, contentHeight - listH);
        this.configScrollTarget = DecideDraw.clamp(this.configScrollTarget, 0.0f, maxScroll);
        this.configScroll += (this.configScrollTarget - this.configScroll) * 0.2f;
        if (Math.abs(this.configScrollTarget - this.configScroll) < 0.05f) {
            this.configScroll = this.configScrollTarget;
        }
        String activeProfile = this.config.activeProfile();
        BeginFrameHelper.pushScissor(listX, listY, listW, listH);
        float rowY = listY - this.configScroll;
        for (int i = 0; i < this.configRows.size(); i++) {
            String name = this.configRows.get(i);
            if (rowY + rowH >= listY && rowY <= listY + listH) {
                boolean hovered = this.hovered(listX, rowY, listW, rowH);
                boolean current = name.equals(activeProfile);
                DecideDraw.rect(listX, rowY, listW, rowH,
                        current ? DecideDraw.multAlpha(client, 0.12f * open)
                                : DecideDraw.overCol(DecideDraw.getColor(0, 0.12f * open),
                                DecideDraw.getColor(25, 0.25f * open), hovered ? 1.0f : 0.0f), this.s(4.0f));
                DecideDraw.textFading(font, name, listX + this.s(8.0f), rowY + this.s(5.0f), listW - this.s(30.0f),
                        DecideDraw.getColor(255, open * (0.5f + 0.3f * (hovered ? 1.0f : 0.0f))), this.s(7.0f));
                float delX = listX + listW - this.s(16.0f);
                this.drawDeleteButton(delX, rowY + this.s(4.0f), open, icons, client);
                this.addHit(HIT_CFG_ROW, i, 0, listX, rowY, listW - this.s(22.0f), rowH);
                this.addHit(HIT_CFG_DEL, i, 0, delX, rowY + this.s(4.0f), this.s(12.0f), this.s(12.0f));
            }
            rowY += rowH + rowGap;
        }
        BeginFrameHelper.popScissor();
    }

    private void drawMarkerForm(float open, TextHeightHelper font, int client) {
        float x = this.listX();
        float y = this.listY();
        float w = this.listW();
        float pad = this.s(8.0f);
        float inputH = this.s(16.0f);
        float gap = this.s(4.0f);
        float labelH = this.s(8.0f);
        DecideDraw.text(font, "Название", x + pad, y + pad, this.s(5.5f), DecideDraw.getColor(200, 0.5f * open));
        float nameY = y + pad + labelH;
        float nameW = w - pad * 2.0f;
        this.drawInput(x + pad, nameY, nameW, inputH, this.markerName, this.markerInputFocused == 0,
                "Имя метки", open, font, client);
        float coordLabelY = nameY + inputH + gap + this.s(2.0f);
        float coordTop = coordLabelY + labelH;
        float coordW = (nameW - gap * 2.0f) / 3.0f;
        DecideDraw.text(font, "X", x + pad, coordLabelY, this.s(5.5f), DecideDraw.getColor(200, 0.5f * open));
        DecideDraw.text(font, "Y", x + pad + coordW + gap, coordLabelY, this.s(5.5f),
                DecideDraw.getColor(200, 0.5f * open));
        DecideDraw.text(font, "Z", x + pad + 2.0f * (coordW + gap), coordLabelY, this.s(5.5f),
                DecideDraw.getColor(200, 0.5f * open));
        this.drawInput(x + pad, coordTop, coordW, inputH, this.markerX, this.markerInputFocused == 1,
                "0", open, font, client);
        this.drawInput(x + pad + coordW + gap, coordTop, coordW, inputH, this.markerY,
                this.markerInputFocused == 2, "0", open, font, client);
        this.drawInput(x + pad + 2.0f * (coordW + gap), coordTop, coordW, inputH, this.markerZ,
                this.markerInputFocused == 3, "0", open, font, client);
        float buttonY = coordTop + inputH + gap + this.s(4.0f);
        this.drawSmallButton(x + pad, buttonY, nameW, inputH, "+ Добавить", open, font, client,
                this.hovered(x + pad, buttonY, nameW, inputH));
        this.addHit(HIT_MK_NAME, 0, 0, x + pad, nameY, nameW, inputH);
        this.addHit(HIT_MK_X, 0, 0, x + pad, coordTop, coordW, inputH);
        this.addHit(HIT_MK_Y, 0, 0, x + pad + coordW + gap, coordTop, coordW, inputH);
        this.addHit(HIT_MK_Z, 0, 0, x + pad + 2.0f * (coordW + gap), coordTop, coordW, inputH);
        this.addHit(HIT_MK_ADD, 0, 0, x + pad, buttonY, nameW, inputH);
    }

    private void drawMarkerList(float open, TextHeightHelper font, TextHeightHelper icons, int client) {
        float x = columnX();
        float y = this.listY();
        float w = columnW();
        float h = this.listH();
        float pad = this.s(8.0f);
        float rowH = this.s(20.0f);
        float rowGap = this.s(3.0f);
        float listX = x + pad;
        float listY = y + pad;
        float listW = w - pad * 2.0f;
        float listH = h - pad * 2.0f;
        if (this.markerRows.isEmpty()) {
            DecideDraw.textCentered(font, "Нет меток", listX + listW * 0.5f, listY + listH * 0.5f - this.s(4.0f),
                    this.s(7.0f), DecideDraw.getColor(255, 0.15f * open));
            return;
        }
        float contentHeight = this.markerRows.size() * (rowH + rowGap);
        float maxScroll = Math.max(0.0f, contentHeight - listH);
        this.markerScrollTarget = DecideDraw.clamp(this.markerScrollTarget, 0.0f, maxScroll);
        this.markerScroll += (this.markerScrollTarget - this.markerScroll) * 0.2f;
        if (Math.abs(this.markerScrollTarget - this.markerScroll) < 0.05f) {
            this.markerScroll = this.markerScrollTarget;
        }
        BeginFrameHelper.pushScissor(listX, listY, listW, listH);
        float rowY = listY - this.markerScroll;
        for (int i = 0; i < this.markerRows.size(); i++) {
            WaypointManager.Waypoint waypoint = this.markerRows.get(i);
            if (rowY + rowH >= listY && rowY <= listY + listH) {
                boolean hovered = this.hovered(listX, rowY, listW, rowH);
                DecideDraw.rect(listX, rowY, listW, rowH,
                        DecideDraw.overCol(DecideDraw.getColor(0, 0.12f * open),
                                DecideDraw.getColor(25, 0.25f * open), hovered ? 1.0f : 0.0f), this.s(4.0f));
                DecideDraw.texture(DecideDraw.TEX_MARKER, listX + this.s(6.0f), rowY + this.s(6.0f),
                        this.s(8.0f), this.s(8.0f), DecideDraw.replAlpha(client, open * 0.75f));
                String label = this.waypointLabels.get(waypoint);
                if (label != null) {
                    DecideDraw.textFading(font, label, listX + this.s(18.0f), rowY + this.s(5.0f),
                            listW - this.s(34.0f),
                            DecideDraw.getColor(255, open * (0.5f + 0.3f * (hovered ? 1.0f : 0.0f))),
                            this.s(6.5f));
                }
                float delX = listX + listW - this.s(16.0f);
                this.drawDeleteButton(delX, rowY + this.s(4.0f), open, icons, client);
                this.addHit(HIT_MK_ROW, i, 0, listX, rowY, listW - this.s(22.0f), rowH);
                this.addHit(HIT_MK_DEL, i, 0, delX, rowY + this.s(4.0f), this.s(12.0f), this.s(12.0f));
            }
            rowY += rowH + rowGap;
        }
        BeginFrameHelper.popScissor();
    }

    private void drawDeleteButton(float x, float y, float open, TextHeightHelper icons, int client) {
        boolean hovered = this.hovered(x, y, this.s(12.0f), this.s(12.0f));
        DecideDraw.rect(x, y, this.s(12.0f), this.s(12.0f),
                DecideDraw.overCol(DecideDraw.getColor(0, 0.15f * open),
                        DecideDraw.getColor(125, 50, 50, 0.3f * open), hovered ? 1.0f : 0.0f), this.s(3.0f));
        DecideDraw.textCentered(icons, "W", x + this.s(6.0f), y + this.s(5.0f), this.s(7.0f),
                DecideDraw.getColor(255, open * (0.4f + 0.4f * (hovered ? 1.0f : 0.0f))));
    }

    private void drawInput(float x, float y, float w, float h, CharSequence text, boolean focused,
                           String placeholder, float open, TextHeightHelper font, int client) {
        DecideDraw.rect(x, y, w, h, DecideDraw.getColor(40, 0.15f * open), this.s(4.0f));
        if (focused) {
            DecideDraw.outline(x, y, w, h, this.s(0.5f), DecideDraw.replAlpha(client, 0.3f * open), this.s(4.0f));
        }
        float textY = y + (h - this.s(6.0f)) * 0.5f;
        if (text.length() == 0 && !focused) {
            DecideDraw.text(font, placeholder, x + this.s(6.0f), textY, this.s(6.0f),
                    DecideDraw.getColor(140, 0.6f * open));
            return;
        }
        String shown = text.toString();
        DecideDraw.textFading(font, shown, x + this.s(6.0f), textY, w - this.s(10.0f),
                DecideDraw.getColor(200, open), this.s(6.0f));
        if (focused && this.caretBlink) {
            float caretX = x + this.s(6.0f) + Math.min(DecideDraw.textWidth(font, shown, this.s(6.0f)),
                    w - this.s(11.0f));
            DecideDraw.rect(caretX + this.s(0.5f), y + this.s(4.0f), this.s(0.6f), this.s(8.0f),
                    DecideDraw.replAlpha(client, open), this.s(0.3f));
        }
    }

    private void drawSmallButton(float x, float y, float w, float h, String label, float open,
                                 TextHeightHelper font, int client, boolean hovered) {
        DecideDraw.rect(x, y, w, h,
                DecideDraw.overCol(DecideDraw.getColor(40, 0.15f * open),
                        DecideDraw.replAlpha(client, 0.2f * open), hovered ? 1.0f : 0.0f), this.s(4.0f));
        DecideDraw.textCentered(font, label, x + w * 0.5f, y + (h - this.s(6.0f)) * 0.5f, this.s(6.0f),
                DecideDraw.getColor(200, open * (0.6f + 0.4f * (hovered ? 1.0f : 0.0f))));
    }

    // ------------------------------------------------------------------ input

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        try {
            return this.handleClick(event);
        } catch (RuntimeException failure) {
            return this.inputFailed("клик", failure);
        }
    }

    private boolean handleClick(MouseButtonEvent event) {
        this.mouseX = (float) event.x();
        this.mouseY = (float) event.y();
        int button = event.button();

        if (this.bindingModule != null) {
            this.bindingModule.setKeyBind(KeyBinds.MOUSE_BASE + button);
            this.bindingModule = null;
            DecideSounds.bindSet();
            this.persist();
            return true;
        }
        if (this.bindingSetting != null) {
            this.bindingSetting.set(KeyBinds.MOUSE_BASE + button);
            this.bindingSetting = null;
            DecideSounds.bindSet();
            this.persist();
            return true;
        }
        if (this.editingString != null) {
            this.commitString();
        }
        if (this.exit || this.closing || this.captureArmed) {
            return true;
        }
        this.measure();
        int hit = this.findHit(event.x(), event.y());
        if (hit < 0) {
            this.blurInputs();
            return false;
        }
        int type = this.hitType[hit];
        int a = this.hitA[hit];
        int b = this.hitB[hit];
        switch (type) {
            case HIT_SEARCH:
                return this.clickSearch(button);
            case HIT_THEME:
                return this.clickTheme(a, button);
            case HIT_CATEGORY:
                return this.clickCategory(a, button);
            case HIT_MODULE:
                return this.clickModule(a, button);
            case HIT_MODULE_BIND:
                return button == 0 ? this.startModuleBind(a) : true;
            case HIT_ROW:
                return this.clickRow(a, button);
            case HIT_BIND_CHIP:
                return this.clickBindChip(a, button);
            case HIT_STRING_CHIP:
                return this.clickStringChip(a, button);
            case HIT_CHIP:
                return this.clickChip(a, b);
            case HIT_COLOR_BAR:
                return this.clickColorBar(a, b, button);
            case HIT_COSMETIC_TAB:
                return this.clickCosmeticTab(a, button);
            case HIT_COSMETIC_CARD:
                return this.clickCosmeticCard(a, button);
            case HIT_CFG_INPUT:
                this.blurSpecialInputs();
                this.searchActive = false;
                this.configInputFocused = true;
                DecideSounds.editStart();
                return true;
            case HIT_CFG_ADD:
                return this.clickConfigAdd(button);
            case HIT_CFG_ROW:
                if (a >= 0 && a < this.configRows.size() && this.config.loadProfile(this.configRows.get(a))) {
                    DecideSounds.bindSet();
                    this.contentDirty = true;
                }
                return true;
            case HIT_CFG_DEL:
                if (a >= 0 && a < this.configRows.size() && this.config.deleteProfile(this.configRows.get(a))) {
                    this.configRows.remove(a);
                    DecideSounds.bindReset();
                }
                return true;
            case HIT_MK_NAME:
                return this.focusMarker(0, button);
            case HIT_MK_X:
                return this.focusMarker(1, button);
            case HIT_MK_Y:
                return this.focusMarker(2, button);
            case HIT_MK_Z:
                return this.focusMarker(3, button);
            case HIT_MK_ADD:
                this.addMarker();
                return true;
            case HIT_MK_ROW:
                return this.clickWaypoint(a, button);
            case HIT_MK_DEL:
                return this.clickMarkerDelete(a, button);
            default:
                return true;
        }
    }

    private boolean clickSearch(int button) {
        if (this.noSearchCategory()) {
            return true;
        }
        if (button == 1) {
            this.searchActive = false;
            this.clearSearch();
            DecideSounds.searchClear();
        } else if (button == 0) {
            if (!this.searchActive) {
                DecideSounds.editStart();
            }
            this.searchActive = true;
            this.searchTypeTime = System.currentTimeMillis();
            this.blurSpecialInputs();
        }
        return true;
    }

    private boolean clickTheme(int index, int button) {
        if (button != 0) {
            return true;
        }
        DecideTheme[] themes = DecideTheme.all();
        DecideTheme theme = themes[Math.floorMod(index, themes.length)];
        if (theme != DecideTheme.selected()) {
            DecideTheme.select(theme);
            DecideSounds.theme(index, themes.length);
        }
        return true;
    }

    private boolean clickCategory(int index, int button) {
        if (button != 0) {
            return true;
        }
        DecideCategory[] categories = DecideCategory.all();
        DecideCategory category = categories[Math.floorMod(index, categories.length)];
        if (category == this.active && !this.searching()) {
            return true;
        }
        this.clearSearch();
        this.searchActive = false;
        if (category != this.active) {
            this.active = category;
            this.selectedModule = null;
            this.settingScrollTarget = 0.0f;
            this.settingScroll = 0.0f;
            this.configInputFocused = false;
            this.markerInputFocused = -1;
            this.cosmeticsScrollTarget = 0.0f;
            this.cosmeticsScroll = 0.0f;
            this.cosmeticsTabScrollTarget = 0.0f;
            this.cosmeticsTabScroll = 0.0f;
            this.listScrollTarget = 0.0f;
            this.categoryReset.resetToZero();
            this.categoryReset.restartForwards();
        }
        this.contentDirty = true;
        DecideSounds.category(index, categories.length);
        return true;
    }

    private boolean clickModule(int index, int button) {
        if (index < 0 || index >= this.visibleModules.size()) {
            return true;
        }
        ClientModule module = this.visibleModules.get(index);
        if (button == 0) {
            module.setEnabled(!module.isEnabled());
            DecideSounds.toggle(module.isEnabled());
            this.persist();
        } else if (button == 1) {
            boolean same = module == this.selectedModule;
            this.selectedModule = same ? null : module;
            this.settingScrollTarget = 0.0f;
            this.settingScroll = 0.0f;
            this.contentDirty = true;
            DecideSounds.expand(!same);
        } else if (button == 2) {
            this.startModuleBind(index);
        }
        return true;
    }

    private boolean startModuleBind(int index) {
        if (index < 0 || index >= this.visibleModules.size()) {
            return true;
        }
        this.bindingModule = this.visibleModules.get(index);
        DecideSounds.bindStart();
        return true;
    }

    private boolean clickRow(int index, int button) {
        if (button != 0 || index < 0 || index >= this.visibleSettings.size()) {
            return true;
        }
        Setting<?> setting = this.visibleSettings.get(index);
        if (setting instanceof NumberSetting) {
            this.draggingSlider = setting;
            DecideSounds.sliderGrab();
        } else if (setting instanceof ModeSetting mode) {
            if (this.expandedChips.contains(mode.getName())) {
                this.expandedChips.remove(mode.getName());
            } else {
                this.expandedChips.add(mode.getName());
            }
        } else if (setting instanceof BooleanSetting bool) {
            bool.set(!bool.get());
            DecideSounds.toggle(bool.get());
            this.persist();
        } else if (setting instanceof ActionSetting action) {
            action.press();
            DecideSounds.button();
            this.persist();
        } else if (setting instanceof ColorSetting color) {
            if (this.openPickers.contains(color)) {
                this.openPickers.remove(color);
                DecideSounds.picker(false);
            } else {
                this.openPickers.add(color);
                DecideSounds.picker(true);
            }
        }
        return true;
    }

    private boolean clickBindChip(int index, int button) {
        if (button != 0 || index < 0 || index >= this.visibleSettings.size()) {
            return true;
        }
        Setting<?> setting = this.visibleSettings.get(index);
        if (setting instanceof KeyBindSetting bind) {
            this.bindingSetting = bind;
            DecideSounds.bindStart();
        }
        return true;
    }

    private boolean clickStringChip(int index, int button) {
        if (button != 0 || index < 0 || index >= this.visibleSettings.size()) {
            return true;
        }
        Setting<?> setting = this.visibleSettings.get(index);
        if (setting instanceof StringSetting text) {
            this.blurSpecialInputs();
            this.searchActive = false;
            this.editingString = text;
            this.stringBuffer.setLength(0);
            String current = text.get();
            if (current != null) {
                this.stringBuffer.append(current);
            }
            DecideSounds.editStart();
        }
        return true;
    }

    private boolean clickChip(int settingIndex, int chipIndex) {
        if (settingIndex < 0 || settingIndex >= this.visibleSettings.size()) {
            return true;
        }
        Setting<?> setting = this.visibleSettings.get(settingIndex);
        if (setting instanceof ModeSetting mode) {
            List<String> values = mode.getModes();
            if (chipIndex >= 0 && chipIndex < values.size()) {
                String value = values.get(chipIndex);
                if (!value.equals(mode.get())) {
                    DecideSounds.chip(chipIndex, values.size());
                }
                mode.set(value);
                this.persist();
            }
        } else if (setting instanceof MultiSelectSetting multi) {
            List<String> values = multi.getModes();
            if (chipIndex >= 0 && chipIndex < values.size()) {
                String value = values.get(chipIndex);
                multi.toggle(value);
                this.multiLabels.put(setting, this.multiLabel(multi));
                DecideSounds.chipMulti(multi.isSelected(value));
                this.persist();
            }
        }
        return true;
    }

    private boolean clickColorBar(int index, int bar, int button) {
        if (button == 0 && index >= 0 && index < this.visibleSettings.size()
                && this.visibleSettings.get(index) instanceof ColorSetting) {
            this.draggingColor = this.visibleSettings.get(index);
            this.draggingColorBar = bar;
            DecideSounds.sliderGrab();
        }
        return true;
    }

    private boolean clickCosmeticTab(int index, int button) {
        if (button != 0) {
            return true;
        }
        int nav = Math.floorMod(index, COSMETIC_NAV.length);
        if (nav != this.activeCosmeticNav) {
            this.activeCosmeticNav = nav;
            this.cosmeticsScrollTarget = 0.0f;
            this.cosmeticsScroll = 0.0f;
            this.contentDirty = true;
            DecideSounds.category(nav, COSMETIC_NAV.length);
        }
        return true;
    }

    private boolean clickCosmeticCard(int index, int button) {
        if (button != 0) {
            return true;
        }
        CosmeticsModule module = this.cosmetics();
        if (module == null) {
            return true;
        }
        if (EntriesHelper.SWORDS.equals(COSMETIC_NAV[this.activeCosmeticNav])) {
            if (index >= 0 && index < this.swordEntries.size()) {
                this.cosmetica.selectSword(this.swordEntries.get(index));
                DecideSounds.bindSet();
                this.refreshEquipped();
                this.persist();
            }
        } else if (index >= 0 && index < this.cosmeticEntries.size()) {
            EntriesHelper.Entry entry = this.cosmeticEntries.get(index);
            boolean was = this.cosmetica.isSelected(entry);
            this.cosmetica.select(entry);
            if (was) {
                DecideSounds.bindReset();
            } else {
                DecideSounds.bindSet();
            }
            this.refreshEquipped();
            this.persist();
        }
        return true;
    }

    private boolean clickConfigAdd(int button) {
        if (button != 0) {
            return true;
        }
        String name = this.configInput.toString().trim();
        if (!name.isEmpty() && this.config.saveProfile(name)) {
            this.configInput.setLength(0);
            this.configRows.clear();
            this.configRows.addAll(this.config.listProfiles());
            DecideSounds.bindSet();
        }
        return true;
    }

    private boolean focusMarker(int slot, int button) {
        if (button != 0) {
            return true;
        }
        this.blurSpecialInputs();
        this.searchActive = false;
        this.markerInputFocused = slot;
        DecideSounds.editStart();
        return true;
    }

    private boolean clickWaypoint(int index, int button) {
        if (button == 0 && index >= 0 && index < this.markerRows.size()) {
            WaypointManager.Waypoint waypoint = this.markerRows.get(index);
            SkycoreClient.waypoints().set(waypoint);
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.player != null) {
                ChatMessages.displayInfo("Активная метка: «" + waypoint.getDisplayName() + "»");
            }
            DecideSounds.button();
        }
        return true;
    }

    private boolean clickMarkerDelete(int index, int button) {
        if (button == 0 && index >= 0 && index < this.markerRows.size()) {
            SkycoreClient.waypoints().removeByName(this.markerRows.get(index).name());
            this.markerRows.remove(index);
            DecideSounds.bindReset();
            this.persist();
        }
        return true;
    }

    private void blurInputs() {
        this.searchActive = false;
        this.configInputFocused = false;
        this.markerInputFocused = -1;
    }

    private void blurSpecialInputs() {
        this.configInputFocused = false;
        this.markerInputFocused = -1;
    }

    private void commitString() {
        if (this.editingString == null) {
            return;
        }
        this.editingString.set(this.stringBuffer.toString());
        this.editingString = null;
        DecideSounds.editCommit();
        this.persist();
    }

    private void addMarker() {
        String name = this.markerName.toString().trim();
        if (name.isEmpty()) {
            DecideSounds.editCancel();
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        double x = this.coordinate(this.markerX, mc, 0);
        double y = this.coordinate(this.markerY, mc, 1);
        double z = this.coordinate(this.markerZ, mc, 2);
        SkycoreClient.waypoints().add(new WaypointManager.Waypoint(name, x, y, z));
        this.markerName.setLength(0);
        this.markerX.setLength(0);
        this.markerY.setLength(0);
        this.markerZ.setLength(0);
        this.markerInputFocused = -1;
        this.contentDirty = true;
        DecideSounds.bindSet();
        this.persist();
    }

    private double coordinate(CharSequence text, Minecraft mc, int axis) {
        if (text.length() > 0) {
            try {
                return Double.parseDouble(text.toString());
            } catch (NumberFormatException ignored) {
                return 0.0;
            }
        }
        if (mc == null || mc.player == null) {
            return 0.0;
        }
        return axis == 0 ? mc.player.getX() : axis == 1 ? mc.player.getY() : mc.player.getZ();
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        try {
            return this.handleRelease(event);
        } catch (RuntimeException failure) {
            return this.inputFailed("отпускание кнопки", failure);
        }
    }

    private boolean handleRelease(MouseButtonEvent event) {
        if (this.draggingSlider != null || this.draggingColor != null) {
            DecideSounds.sliderRelease();
            this.persist();
        }
        this.draggingSlider = null;
        this.draggingColor = null;
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        try {
            return this.handleDrag(event);
        } catch (RuntimeException failure) {
            return this.inputFailed("перетаскивание", failure);
        }
    }

    private boolean handleDrag(MouseButtonEvent event) {
        this.mouseX = (float) event.x();
        this.mouseY = (float) event.y();
        if (this.draggingColor instanceof ColorSetting color) {
            this.measure();
            float barX = this.columnX() + this.s(16.0f);
            float barW = this.columnW() - this.s(32.0f);
            float percent = DecideDraw.clamp((this.mouseX - barX) / barW, 0.0f, 1.0f);
            int argb = color.get();
            DecideDraw.rgbToHsb((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, this.hsv);
            float hue = this.draggingColorBar == 0 ? percent : this.hsv[0];
            float saturation = this.draggingColorBar == 1 ? percent : this.hsv[1];
            float brightness = this.draggingColorBar == 2 ? percent : this.hsv[2];
            int rgb = DecideDraw.hsbToRgb(hue, saturation, brightness);
            int next = (argb & 0xFF000000) | (rgb & 0x00FFFFFF);
            if (next != argb) {
                color.set(next);
                DecideSounds.colorTick(percent);
            }
            return true;
        }
        if (this.draggingSlider != null) {
            return true;
        }
        return false;
    }

    /**
     * One broken interaction closes the menu instead of the game: the input path touches live module
     * state, and a half-applied edit is worse than a closed ClickGUI.
     */
    private boolean inputFailed(String what, RuntimeException failure) {
        SkycoreClient.LOGGER.error("Skycore Decide GUI: {} failed, closing the menu", what, failure);
        ChatMessages.displayError("ClickGUI: ошибка (" + what + "), меню закрыто");
        this.hitCount = 0;
        this.bindingModule = null;
        this.bindingSetting = null;
        this.editingString = null;
        this.draggingSlider = null;
        this.draggingColor = null;
        this.persistDirty = true;
        this.flushPersist(System.currentTimeMillis());
        DecideMusic.stop();
        this.exit = true;
        this.openAnim.setValue(0.0f);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        try {
            return this.handleScroll(mouseX, mouseY, horizontal, vertical);
        } catch (RuntimeException failure) {
            return this.inputFailed("прокрутка", failure);
        }
    }

    private boolean handleScroll(double mouseX, double mouseY, double horizontal, double vertical) {
        if (this.exit || this.closing || this.captureArmed) {
            return true;
        }
        this.measure();
        float x = this.listX();
        float y = this.listY();
        float h = this.listH();
        if (mouseX >= x && mouseX <= x + this.listW() && mouseY >= y && mouseY <= y + h) {
            if (this.active == DecideCategory.COSMETICS) {
                float before = this.cosmeticsTabScrollTarget;
                this.cosmeticsTabScrollTarget = DecideDraw.clamp(this.cosmeticsTabScrollTarget
                        - (float) vertical * this.s(25.0f), 0.0f, this.cosmeticsTabMaxScroll);
                if (this.cosmeticsTabScrollTarget != before) {
                    DecideSounds.scroll();
                }
                return true;
            }
            if (this.active.isSpecial()) {
                return true;
            }
            float before = this.listScrollTarget;
            this.listScrollTarget = DecideDraw.clamp(this.listScrollTarget - (float) vertical * this.s(25.0f),
                    0.0f, this.listMaxScroll);
            if (this.listScrollTarget != before) {
                DecideSounds.scroll();
            }
            return true;
        }
        float columnStart = this.columnX();
        if (mouseX >= columnStart && mouseX <= columnStart + this.columnW() && mouseY >= y && mouseY <= y + h) {
            if (this.active == DecideCategory.COSMETICS) {
                float before = this.cosmeticsScrollTarget;
                this.cosmeticsScrollTarget = DecideDraw.clamp(this.cosmeticsScrollTarget
                        - (float) vertical * this.s(25.0f), 0.0f, this.cosmeticsMaxScroll);
                if (this.cosmeticsScrollTarget != before) {
                    DecideSounds.scroll();
                }
            } else if (this.active == DecideCategory.CONFIGS) {
                this.configScrollTarget = Math.max(0.0f, this.configScrollTarget - (float) vertical * this.s(25.0f));
                DecideSounds.scroll();
            } else if (this.active == DecideCategory.MARKERS) {
                this.markerScrollTarget = Math.max(0.0f, this.markerScrollTarget - (float) vertical * this.s(25.0f));
                DecideSounds.scroll();
            } else {
                float before = this.settingScrollTarget;
                this.settingScrollTarget = DecideDraw.clamp(this.settingScrollTarget
                        - (float) vertical * this.s(25.0f), 0.0f, this.settingMaxScroll);
                if (this.settingScrollTarget != before) {
                    DecideSounds.scroll();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        try {
            return this.handleKey(event);
        } catch (RuntimeException failure) {
            return this.inputFailed("клавиша", failure);
        }
    }

    private boolean handleKey(KeyEvent event) {
        int key = event.key();
        if (this.bindingModule != null) {
            boolean reset = key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_DELETE;
            this.bindingModule.setKeyBind(reset ? -1 : key);
            this.bindingModule = null;
            if (reset) {
                DecideSounds.bindReset();
            } else {
                DecideSounds.bindSet();
            }
            this.persist();
            return true;
        }
        if (this.bindingSetting != null) {
            boolean reset = key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_DELETE;
            this.bindingSetting.set(reset ? KeyBindSetting.UNBOUND : key);
            this.bindingSetting = null;
            if (reset) {
                DecideSounds.bindReset();
            } else {
                DecideSounds.bindSet();
            }
            this.persist();
            return true;
        }
        if (this.searchActive) {
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                if (this.searching()) {
                    this.clearSearch();
                } else {
                    this.searchActive = false;
                }
                DecideSounds.searchClear();
            } else if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
                this.searchActive = false;
                DecideSounds.editCommit();
            } else if (key == GLFW.GLFW_KEY_BACKSPACE && this.search.length() > 0) {
                this.search.setLength(this.search.length() - 1);
                this.queryText = this.search.toString();
                this.searchChanged();
                DecideSounds.erase();
            }
            return true;
        }
        if (this.editingString != null) {
            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
                this.commitString();
            } else if (key == GLFW.GLFW_KEY_ESCAPE) {
                this.editingString = null;
                this.stringBuffer.setLength(0);
                DecideSounds.editCancel();
            } else if (key == GLFW.GLFW_KEY_BACKSPACE && this.stringBuffer.length() > 0) {
                this.stringBuffer.setLength(this.stringBuffer.length() - 1);
                DecideSounds.erase();
            }
            return true;
        }
        if (this.configInputFocused || this.markerInputFocused >= 0) {
            if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
                this.blurSpecialInputs();
                DecideSounds.editCommit();
                return true;
            }
            if (key == GLFW.GLFW_KEY_BACKSPACE) {
                StringBuilder buffer = this.activeSpecialBuffer();
                if (buffer != null && buffer.length() > 0) {
                    buffer.setLength(buffer.length() - 1);
                    DecideSounds.erase();
                }
                return true;
            }
            return true;
        }
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        return false;
    }

    private StringBuilder activeSpecialBuffer() {
        if (this.configInputFocused) {
            return this.configInput;
        }
        return switch (this.markerInputFocused) {
            case 0 -> this.markerName;
            case 1 -> this.markerX;
            case 2 -> this.markerY;
            case 3 -> this.markerZ;
            default -> null;
        };
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        try {
            return this.handleChar(event);
        } catch (RuntimeException failure) {
            return this.inputFailed("ввод символа", failure);
        }
    }

    private boolean handleChar(CharacterEvent event) {
        if (!event.isAllowedChatCharacter()) {
            return false;
        }
        String typed = event.codepointAsString();
        if (this.searchActive) {
            if (!this.noSearchCategory() && this.search.length() < 24) {
                this.search.append(typed);
                this.queryText = this.search.toString();
                this.searchChanged();
                DecideSounds.type();
            }
            return true;
        }
        if (this.editingString != null) {
            int max = this.editingString.getMaxLength();
            for (int i = 0; i < typed.length() && this.stringBuffer.length() < max; i++) {
                this.stringBuffer.append(typed.charAt(i));
            }
            DecideSounds.type();
            return true;
        }
        StringBuilder buffer = this.activeSpecialBuffer();
        if (buffer != null) {
            boolean numeric = !this.configInputFocused && this.markerInputFocused != 0;
            int max = numeric ? 10 : 24;
            for (int i = 0; i < typed.length() && buffer.length() < max; i++) {
                char c = typed.charAt(i);
                boolean allowed = numeric
                        ? Character.isDigit(c) || c == '-' || c == '.'
                        : Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == ' ';
                if (allowed) {
                    buffer.append(c);
                }
            }
            DecideSounds.type();
            return true;
        }
        return false;
    }

    private void clearSearch() {
        if (this.search.length() == 0) {
            return;
        }
        this.search.setLength(0);
        this.queryText = "";
        this.searchChanged();
    }

    private void searchChanged() {
        this.searchTypeTime = System.currentTimeMillis();
        this.listScrollTarget = 0.0f;
        this.cosmeticsScrollTarget = 0.0f;
        this.contentDirty = true;
    }

    // ------------------------------------------------------------------ closing

    @Override
    public void onClose() {
        this.close();
    }

    private void close() {
        if (this.exit || this.closing || this.captureArmed) {
            return;
        }
        this.commitString();
        this.blurInputs();
        this.persist();
        this.measure();
        String mode = this.clickGui.closeAnimation().get();
        if ("3D".equals(mode) && this.armClose(DecideCloseAnimation.MODE_3D)) {
            return;
        }
        if ("Shatter".equals(mode) && this.armClose(DecideCloseAnimation.MODE_SHATTER)) {
            return;
        }
        this.openAnim.run(0.0f, 0.3f, Easing.SINE_IN);
        this.exit = true;
        this.particleCount = 0;
        DecideSounds.close();
        DecideMusic.stop();
    }

    /**
     * Arms a framebuffer capture: the panel still renders this frame, the capture marker copies the
     * finished frame inside {@code GuiRenderer}, and the screen closes on the next extraction.
     */
    private boolean armClose(int mode) {
        if (!DecideCloseAnimation.armCapture(mode, this.panelX, this.panelY, this.panelW, this.panelH,
                this.width, this.height)) {
            return false;
        }
        this.captureArmed = true;
        this.captureArmedAt = System.currentTimeMillis();
        DecideGuiMovement.release(Minecraft.getInstance());
        DecideSounds.close();
        DecideMusic.stop();
        return true;
    }

    /**
     * Writes the live configuration once, but at most every few hundred milliseconds: the menu
     * persists on every toggle, drag tick and text commit, and an unbounded {@code save()} per
     * interaction is what turns a drag into a visible stutter.
     */
    private void persist() {
        this.persistDirty = true;
        long now = System.currentTimeMillis();
        if (now - this.lastPersistAt < 250L) {
            return;
        }
        this.flushPersist(now);
    }

    private void flushPersist(long now) {
        if (!this.persistDirty) {
            return;
        }
        this.persistDirty = false;
        this.lastPersistAt = now;
        ConfigManager manager = SkycoreClient.config();
        if (manager != null) {
            try {
                manager.save();
            } catch (RuntimeException failure) {
                SkycoreClient.LOGGER.warn("Skycore Decide GUI: config save failed", failure);
            }
        }
    }
}
