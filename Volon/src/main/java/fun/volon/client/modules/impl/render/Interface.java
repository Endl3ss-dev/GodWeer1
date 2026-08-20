package fun.volon.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import fun.volon.Volon;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.Priority;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.utils.animation.AnimationUtils;
import fun.volon.api.utils.animation.Easings;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.math.HoveringUtils;
import fun.volon.api.utils.render.HudBlockOverlayShaderRenderer;
import fun.volon.api.utils.render.HudMotionTracker;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.impl.render.base.HudRectStyle;
import fun.volon.client.modules.impl.render.base.InterfaceProcessing;
import fun.volon.client.ui.clickgui.ClickGui;
import fun.volon.client.modules.impl.render.base.implement.*;
import fun.volon.client.modules.settings.implement.BooleanSetting;
import fun.volon.client.modules.settings.implement.FloatSetting;
import fun.volon.client.modules.settings.implement.ListSetting;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Interface extends Module {

    public static Interface INSTANCE = new Interface();
    private static final ConcurrentHashMap<String, Long> PERF_WARNINGS = new ConcurrentHashMap<>();
    private static final boolean PERF_DEBUG = Boolean.parseBoolean(System.getProperty("Volon.perf.debug", "false"));
    private static final long SLOW_HUD_ELEMENT_NANOS = Long.getLong("Volon.perf.hudMs", 5L) * 1_000_000L;
    private static final long PERF_WARN_COOLDOWN_NANOS = Long.getLong("Volon.perf.cooldownMs", 1000L) * 1_000_000L;

    private final WaterMark waterMark;
    private final KeyBinds keyBinds;
    private final HelperBinds helperBinds;
    private final Potions potions;
    private final Notifications notifications;
    private final TargetHud targetHud;
    private final StaffList staffList;
    private final NowPlaying nowPlaying;
    private final ArmorStatus armorStatus;
    private final ItemCounter itemCounter;
    private final ArrayListHud arrayListHud;
    private final Potions1 potions1;
    private final KeyBinds1 keyBinds1;
    private final StaffList1 staffList1;
    private final WaterMarkV2 waterMarkV2;
    private final TargetHudV2 targetHudV2;
    private boolean targetHudMenuOpen;
    private float targetHudMenuX;
    private float targetHudMenuY;
    private InterfaceProcessing hudContextElement;
    private InterfaceProcessing pendingHudContextElement;
    private float pendingTargetHudMenuX;
    private float pendingTargetHudMenuY;
    private final AnimationUtils targetHudMenuAnimation = new AnimationUtils(0f, 12.5f, Easings.CUBIC_OUT);
    private final AnimationUtils targetHudParticlesBgAnimation = new AnimationUtils(1f, 15.0f, Easings.CUBIC_OUT);
    private final AnimationUtils targetHudParticlesCircleAnimation = new AnimationUtils(1f, 8.2f, Easings.BACK_OUT);
    private final AnimationUtils targetHudBarSwitchAnimation = new AnimationUtils(0f, 7.0f, Easings.CUBIC_OUT);
    private final AnimationUtils targetHudNursultanSwitchAnimation = new AnimationUtils(0f, 7.0f, Easings.CUBIC_OUT);
    private final AnimationUtils waterMarkFpsBgAnimation = new AnimationUtils(1f, 15.0f, Easings.CUBIC_OUT);
    private final AnimationUtils waterMarkFpsCircleAnimation = new AnimationUtils(1f, 8.2f, Easings.BACK_OUT);
    private final AnimationUtils waterMarkMsBgAnimation = new AnimationUtils(1f, 15.0f, Easings.CUBIC_OUT);
    private final AnimationUtils waterMarkMsCircleAnimation = new AnimationUtils(1f, 8.2f, Easings.BACK_OUT);
    private final AnimationUtils waterMarkServerBgAnimation = new AnimationUtils(1f, 15.0f, Easings.CUBIC_OUT);
    private final AnimationUtils waterMarkServerCircleAnimation = new AnimationUtils(1f, 8.2f, Easings.BACK_OUT);
    private final AnimationUtils waterMarkTpsBgAnimation = new AnimationUtils(1f, 15.0f, Easings.CUBIC_OUT);
    private final AnimationUtils waterMarkTpsCircleAnimation = new AnimationUtils(1f, 8.2f, Easings.BACK_OUT);
    private final AnimationUtils waterMarkTpsRowBgAnimation = new AnimationUtils(1f, 15.0f, Easings.CUBIC_OUT);
    private final AnimationUtils waterMarkTpsRowCircleAnimation = new AnimationUtils(1f, 8.2f, Easings.BACK_OUT);
    private final AnimationUtils waterMarkBpsBgAnimation = new AnimationUtils(1f, 15.0f, Easings.CUBIC_OUT);
    private final AnimationUtils waterMarkBpsCircleAnimation = new AnimationUtils(1f, 8.2f, Easings.BACK_OUT);
    private final AnimationUtils waterMarkXyzBgAnimation = new AnimationUtils(1f, 15.0f, Easings.CUBIC_OUT);
    private final AnimationUtils waterMarkXyzCircleAnimation = new AnimationUtils(1f, 8.2f, Easings.BACK_OUT);
    private final AnimationUtils waterMarkServerRowBgAnimation = new AnimationUtils(1f, 15.0f, Easings.CUBIC_OUT);
    private final AnimationUtils waterMarkServerRowCircleAnimation = new AnimationUtils(1f, 8.2f, Easings.BACK_OUT);
    private final AnimationUtils potionStyleSwitchAnimation = new AnimationUtils(0f, 7.0f, Easings.CUBIC_OUT);
    private final AnimationUtils hudRectTypeSwitchAnimation = new AnimationUtils(1f, 7.0f, Easings.CUBIC_OUT);
    private final Map<InterfaceProcessing, AnimationUtils> blurBgAnimations = new IdentityHashMap<>();
    private final Map<InterfaceProcessing, AnimationUtils> blurCircleAnimations = new IdentityHashMap<>();
    private final Map<InterfaceProcessing, AnimationUtils> shadowBgAnimations = new IdentityHashMap<>();
    private final Map<InterfaceProcessing, AnimationUtils> shadowCircleAnimations = new IdentityHashMap<>();
    private final Map<InterfaceProcessing, AnimationUtils> outlineBgAnimations = new IdentityHashMap<>();
    private final Map<InterfaceProcessing, AnimationUtils> outlineCircleAnimations = new IdentityHashMap<>();
    private static final String HUD_HINT_TEXT = "ПКМ - по элементу для открытия настроек";

    private FloatSetting draggedSlider;
    private float draggedSliderTrackX;
    private float draggedSliderTrackW;

    public final BooleanSetting liquidGlassHud = new BooleanSetting("Жидкое стекло", false);
    public final BooleanSetting potionCompactStyle = new BooleanSetting("Компактный список зелий", false);

    // Стили элементов — выбор v1/v2 в настройках модуля
    public final fun.volon.client.modules.settings.implement.ModeSetting potionsStyle =
        new fun.volon.client.modules.settings.implement.ModeSetting("Стиль зелий", "v1", "v1", "v2");
    public final fun.volon.client.modules.settings.implement.ModeSetting keyBindsStyle =
        new fun.volon.client.modules.settings.implement.ModeSetting("Стиль кейбиндов", "v1", "v1", "v2");
    public final fun.volon.client.modules.settings.implement.ModeSetting staffStyle =
        new fun.volon.client.modules.settings.implement.ModeSetting("Стиль стафф листа", "v1", "v1", "v2");
    public final fun.volon.client.modules.settings.implement.ModeSetting targetHudStyle =
        new fun.volon.client.modules.settings.implement.ModeSetting("Стиль таргет худа", "v1", "v1", "v2");

    public final FloatSetting potions1HeaderAlpha  = new FloatSetting("Прозрачность шапки",  87f, 0f, 100f, 1f).visible(() -> false);
    public final FloatSetting potions1BodyAlpha    = new FloatSetting("Прозрачность фона",   57f, 0f, 100f, 1f).visible(() -> false);
    public final FloatSetting keyBinds1HeaderAlpha = new FloatSetting("Прозрачность шапки ",  87f, 0f, 100f, 1f).visible(() -> false);
    public final FloatSetting keyBinds1BodyAlpha   = new FloatSetting("Прозрачность фона ",   57f, 0f, 100f, 1f).visible(() -> false);
    public final FloatSetting staffList1HeaderAlpha= new FloatSetting("Прозрачность шапки  ",  87f, 0f, 100f, 1f).visible(() -> false);
    public final FloatSetting staffList1BodyAlpha  = new FloatSetting("Прозрачность фона  ",   57f, 0f, 100f, 1f).visible(() -> false);
    public final FloatSetting targetHudAlpha       = new FloatSetting("Прозрачность",          57f, 0f, 100f, 1f).visible(() -> false);

    public final BooleanSetting potions1Shadow   = new BooleanSetting("Тень (Зелья v2)",    false).visible(() -> false);
    public final BooleanSetting keyBinds1Shadow  = new BooleanSetting("Тень (Кейбинды v2)", false).visible(() -> false);
    public final BooleanSetting staffList1Shadow = new BooleanSetting("Тень (Стафф v2)",    false).visible(() -> false);
    public final BooleanSetting targetHudShadow  = new BooleanSetting("Тень (Таргет худ)",  false).visible(() -> false);

    public final BooleanSetting potions1Outline   = new BooleanSetting("Обводка (Зелья v2)",    true).visible(() -> false);
    public final BooleanSetting keyBinds1Outline  = new BooleanSetting("Обводка (Кейбинды v2)", true).visible(() -> false);
    public final BooleanSetting staffList1Outline = new BooleanSetting("Обводка (Стафф v2)",    true).visible(() -> false);
    public final BooleanSetting targetHudOutline  = new BooleanSetting("Обводка (Таргет худ)",  false).visible(() -> false);
    public final BooleanSetting notificationsOutline = new BooleanSetting("Обводка (Уведомления)", false).visible(() -> false);

    public final BooleanSetting notificationsShadow = new BooleanSetting("Тень (Уведомления)", false).visible(() -> false);
    public final BooleanSetting notificationsBlur   = new BooleanSetting("Блюр (Уведомления)", false).visible(() -> false);

    public final FloatSetting potions1ShadowStrength   = new FloatSetting("Сила тени (Зелья v2)",    70f, 0f, 100f, 1f).visible(() -> false);
    public final FloatSetting keyBinds1ShadowStrength  = new FloatSetting("Сила тени (Кейбинды v2)", 70f, 0f, 100f, 1f).visible(() -> false);
    public final FloatSetting staffList1ShadowStrength = new FloatSetting("Сила тени (Стафф v2)",    70f, 0f, 100f, 1f).visible(() -> false);
    public final FloatSetting targetHudShadowStrength  = new FloatSetting("Сила тени (Таргет худ)",  70f, 0f, 100f, 1f).visible(() -> false);
    public final FloatSetting notificationsShadowStrength = new FloatSetting("Сила тени (Уведомления)", 70f, 0f, 100f, 1f).visible(() -> false);

    public final FloatSetting notificationsAlpha = new FloatSetting("Прозрачность (Уведомления)", 57f, 0f, 100f, 1f).visible(() -> false);

    public final FloatSetting potions1Scale      = new FloatSetting("Размер (Зелья v2)",     100f, 50f, 200f, 1f).visible(() -> false);
    public final FloatSetting keyBinds1Scale     = new FloatSetting("Размер (Кейбинды v2)",   100f, 50f, 200f, 1f).visible(() -> false);
    public final FloatSetting staffList1Scale    = new FloatSetting("Размер (Стафф v2)",      100f, 50f, 200f, 1f).visible(() -> false);
    public final FloatSetting targetHudScale     = new FloatSetting("Размер (Таргет худ)",    100f, 50f, 200f, 1f).visible(() -> false);
    public final FloatSetting notificationsScale = new FloatSetting("Размер (Уведомления)", 100f, 50f, 200f, 1f).visible(() -> false);

    private final ListSetting hudModules = new ListSetting("Элементы",
            new BooleanSetting("Ватермарка", true),
            new BooleanSetting("Горячие клавиши", true),
            new BooleanSetting("Серверные бинды", true),
            new BooleanSetting("Зелья", true),
            new BooleanSetting("Стафф", true),
            new BooleanSetting("Уведомления", true),
            new BooleanSetting("Таргет худ", true),
            new BooleanSetting("Музыка", true),
            new BooleanSetting("Статус брони", true),
            new BooleanSetting("Счётчик предметов", true),
            new BooleanSetting("ArrayList", true));

    public Interface() {
        super("Interface", "Интерфейс клиента", ModuleCategory.RENDER);
        addSettings(hudModules, liquidGlassHud, potionCompactStyle,
                potionsStyle, keyBindsStyle, staffStyle, targetHudStyle,
                targetHudAlpha, notificationsAlpha,
                potions1Shadow, keyBinds1Shadow, staffList1Shadow, targetHudShadow, notificationsShadow,
                potions1Outline, keyBinds1Outline, staffList1Outline, targetHudOutline, notificationsOutline,
                potions1ShadowStrength, keyBinds1ShadowStrength, staffList1ShadowStrength, targetHudShadowStrength, notificationsShadowStrength,
                notificationsBlur,
                potions1Scale, keyBinds1Scale, staffList1Scale, targetHudScale, notificationsScale,
                potions1HeaderAlpha, potions1BodyAlpha,
                keyBinds1HeaderAlpha, keyBinds1BodyAlpha,
                staffList1HeaderAlpha, staffList1BodyAlpha);
        this.waterMark = new WaterMark(Volon.draggable(this, "WaterMark", 10, 10));
        this.keyBinds = new KeyBinds(Volon.draggable(this, "KeyBinds", 30, 30));
        this.helperBinds = new HelperBinds(Volon.draggable(this, "HelperBinds", 90, 30));
        this.potions = new Potions(Volon.draggable(this, "Potions", 30, 60));
        this.staffList = new StaffList(Volon.draggable(this, "StaffList", 60, 100));
        this.notifications = new Notifications(Volon.draggable(this, "Notifications", 0, 0));
        this.targetHud = new TargetHud(Volon.draggable(this, "TargetHud", 30, 90));
        this.nowPlaying = new NowPlaying(Volon.draggable(this, "NowPlaying", 10, 120));
        this.armorStatus = new ArmorStatus(Volon.draggable(this, "ArmorStatus", 0, 0));
        this.itemCounter = new ItemCounter(Volon.draggable(this, "ItemCounter", 10, 200));
        this.arrayListHud = new ArrayListHud(Volon.draggable(this, "ArrayList", 10, 30));
        this.potions1 = new Potions1(Volon.draggable(this, "Potions1", 200, 30));
        this.keyBinds1 = new KeyBinds1(Volon.draggable(this, "KeyBinds1", 200, 80));
        this.staffList1 = new StaffList1(Volon.draggable(this, "StaffList1", 200, 130));
        this.waterMarkV2 = new WaterMarkV2(Volon.draggable(this, "WaterMarkV2", 10, 40));
        this.targetHudV2 = new TargetHudV2(Volon.draggable(this, "TargetHudV2", 30, 120));
    }

    private Font issue(int size) {
        return Fonts.getFont("suisse", size);
    }

    public static boolean isDropdownStyle() {
        return false;
    }

    private int fadeColorSafe(int color, float progress, int minAlpha) {
        int faded = ColorUtils.applyAlpha(color, progress);
        int a = ColorUtils.getAlpha(faded);
        if (a == 0 && progress > 0.001f) {
            return ColorUtils.setAlphaColor(faded, minAlpha);
        }
        return faded;
    }

    private int fadeTextAlphaSafe(float progress, int maxAlpha, int minAlpha) {
        int alpha = MathHelper.clamp((int) (maxAlpha * progress), 0, maxAlpha);
        if (alpha == 0 && progress > 0.001f) {
            return minAlpha;
        }
        return alpha;
    }

    private int getThemeColor() {
        if (!Volon.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            return Volon.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        }
        return ColorUtils.getThemeColor();
    }
    private boolean isHudElementHovered(InterfaceProcessing element, double mouseX, double mouseY) {
        float width = element.draggable.getWidth();
        float height = element.draggable.getHeight();
        if (width <= 1.0f || height <= 1.0f) {
            return false;
        }
        return HoveringUtils.isHovered(mouseX, mouseY, element.draggable.getX(), element.draggable.getY(), width, height);
    }
    private boolean isHudElementEnabled(InterfaceProcessing element) {
        return element != null && element.draggable.getWidth() > 1.0f && element.draggable.getHeight() > 1.0f;
    }
    private InterfaceProcessing getHoveredHudElement(double mouseX, double mouseY) {
        if (isHudElementEnabled(targetHud) && isHudElementHovered(targetHud, mouseX, mouseY)) return targetHud;
        if (isHudElementEnabled(waterMark) && isHudElementHovered(waterMark, mouseX, mouseY)) return waterMark;
        if (isHudElementEnabled(keyBinds) && isHudElementHovered(keyBinds, mouseX, mouseY)) return keyBinds;
        if (isHudElementEnabled(helperBinds) && isHudElementHovered(helperBinds, mouseX, mouseY)) return helperBinds;
        if (isHudElementEnabled(potions) && isHudElementHovered(potions, mouseX, mouseY)) return potions;
        if (isHudElementEnabled(staffList) && isHudElementHovered(staffList, mouseX, mouseY)) return staffList;
        if (isHudElementEnabled(notifications) && isHudElementHovered(notifications, mouseX, mouseY)) return notifications;
        if (isHudElementEnabled(armorStatus) && isHudElementHovered(armorStatus, mouseX, mouseY)) return armorStatus;
        if (isHudElementEnabled(itemCounter) && isHudElementHovered(itemCounter, mouseX, mouseY)) return itemCounter;
        if (isHudElementEnabled(arrayListHud) && isHudElementHovered(arrayListHud, mouseX, mouseY)) return arrayListHud;
        if (isHudElementEnabled(potions1) && isHudElementHovered(potions1, mouseX, mouseY)) return potions1;
        if (isHudElementEnabled(keyBinds1) && isHudElementHovered(keyBinds1, mouseX, mouseY)) return keyBinds1;
        if (isHudElementEnabled(staffList1) && isHudElementHovered(staffList1, mouseX, mouseY)) return staffList1;
        if (isHudElementEnabled(waterMarkV2) && isHudElementHovered(waterMarkV2, mouseX, mouseY)) return waterMarkV2;
        if (isHudElementEnabled(targetHudV2) && isHudElementHovered(targetHudV2, mouseX, mouseY)) return targetHudV2;
        return null;
    }
    private float getTargetHudMenuWidth() {
        return 100.0f;
    }

    private boolean isV2Element(InterfaceProcessing element) {
        return element == potions1 || element == keyBinds1 || element == staffList1;
    }

    private boolean hasRectStyleSection(InterfaceProcessing element) {
        return !isV2Element(element) && element != targetHud && element != notifications
                && element != potions && element != keyBinds && element != staffList;
    }

    private boolean isHudStyleElement(InterfaceProcessing element) {
        return element == potions || element == keyBinds || element == staffList;
    }

    private boolean showsV2SettingsPanel(InterfaceProcessing element) {
        return isV2Element(element) || (isHudStyleElement(element) && element.isUseV2Style());
    }

    private void applyV2Defaults(InterfaceProcessing element) {
        FloatSetting header = v2HeaderAlphaFor(element);
        FloatSetting body = v2BodyAlphaFor(element);
        BooleanSetting shadow = shadowToggleForV2(element);
        BooleanSetting outline = outlineToggleForV2(element);
        if (header != null) header.setValue(87f);
        if (body != null) body.setValue(57f);
        if (shadow != null) shadow.setState(false);
        if (outline != null) outline.setState(true);
    }

    private void drawHudStyleSwitch(net.minecraft.client.util.math.MatrixStack matrices,
                                    InterfaceProcessing element,
                                    float x, float y, float menuWidth, float menuHeight,
                                    float normalX, float unusualX, float buttonW, float buttonH,
                                    int inactiveColor, int activeLeftColor, int activeRightColor,
                                    float contentProgress, int textAlpha) {
        float sv2Y = y + menuHeight - 13.0f;
        float sv2Anim = element.isUseV2Style() ? 1f : 0f;
        issue(11).draw(matrices, "Стиль", x + 4.7f, sv2Y - 6f,
                ColorUtils.rgba(255, 255, 255, fadeTextAlphaSafe(contentProgress, 180, 2)));
        RenderUtils.drawRoundedRect(matrices, normalX, sv2Y, buttonW, buttonH, 1.5f, inactiveColor);
        RenderUtils.drawRoundedRect(matrices, unusualX, sv2Y, buttonW, buttonH, 1.5f, inactiveColor);
        RenderUtils.drawGradientRect(matrices, MathHelper.lerp(sv2Anim, normalX, unusualX), sv2Y, buttonW, buttonH, 1.5f,
                activeLeftColor, activeRightColor, true);
        issue(11).draw(matrices, "v1", normalX + (buttonW - issue(11).getWidth("v1")) * 0.5f, sv2Y + 3.5f,
                ColorUtils.rgba(255, 255, 255, MathHelper.clamp((int) (textAlpha * (0.65f + 0.35f * (1f - sv2Anim))), 0, 255)));
        issue(11).draw(matrices, "v2", unusualX + (buttonW - issue(11).getWidth("v2")) * 0.5f, sv2Y + 3.5f,
                ColorUtils.rgba(255, 255, 255, MathHelper.clamp((int) (textAlpha * (0.65f + 0.35f * sv2Anim)), 0, 255)));
    }

    private boolean hasV2StyleOption(InterfaceProcessing element) {
        return element == potions || element == keyBinds || element == staffList
                || element == waterMark || element == targetHud;
    }

    private InterfaceProcessing getV2StyleTarget(InterfaceProcessing element) {
        if (element == potions) return potions1;
        if (element == keyBinds) return keyBinds1;
        if (element == staffList) return staffList1;
        if (element == waterMark) return waterMarkV2;
        if (element == targetHud) return targetHudV2;
        return null;
    }

    private float getMenuHeightForElement(InterfaceProcessing element) {
        boolean useV2 = element.isUseV2Style();

        if (isHudStyleElement(element)) {
            if (useV2) {
                BooleanSetting shadowS = shadowToggleForV2(element);
                boolean shadowOn = shadowS != null && shadowS.isState();
                float shadowExtra = shadowOn ? 20.0f : 0.0f;
                return 12.0f + 14.0f + 10.0f + shadowExtra + 10.0f + 20.0f + 20.0f + 20.0f + 10.0f;
            }
            return 23.0f;
        }

        // Для v2 элементов в режиме v2: кнопки(12) + тень(10) + [сила тени(20)] + обводка(10) + шапка(20) + тело(20) + размер(20) + отступы
        if (useV2 && hasV2StyleOption(element)) {
            BooleanSetting shadowS = shadowToggleForV2(element);
            boolean shadowOn = shadowS != null && shadowS.isState();
            float shadowExtra = shadowOn ? 20.0f : 0.0f;
            return 12.0f + 14.0f + 10.0f + shadowExtra + 10.0f + 20.0f + 20.0f + 20.0f + 10.0f;
        }

        // v1 режим — стандартная высота + кнопки v1/v2
        float shadowExtra = 0f;
        BooleanSetting s = shadowToggleFor(element);
        if (s != null && s.isState()) shadowExtra = 20.0f;
        float scaleExtra = hasScaleSetting(element) ? 20.0f : 0f;
        float v2Extra = hasV2StyleOption(element) ? 13.0f : 0f;
        if (element == targetHud) return 82.0f + shadowExtra + scaleExtra + v2Extra;
        if (element == waterMark) return 109.0f + v2Extra;
        if (element == notifications) return 49.0f + shadowExtra + scaleExtra;
        if (isV2Element(element)) return 70.0f + shadowExtra + scaleExtra;
        return 29.0f;
    }

    /** Возвращает shadow-настройку для v2 версии элемента */
    private BooleanSetting shadowToggleForV2(InterfaceProcessing element) {
        if (element == potions || element == potions1)  return potions1Shadow;
        if (element == keyBinds || element == keyBinds1) return keyBinds1Shadow;
        if (element == staffList || element == staffList1) return staffList1Shadow;
        if (element == targetHud) return targetHudShadow;
        return null;
    }

    private BooleanSetting outlineToggleForV2(InterfaceProcessing element) {
        if (element == potions || element == potions1)   return potions1Outline;
        if (element == keyBinds || element == keyBinds1) return keyBinds1Outline;
        if (element == staffList || element == staffList1) return staffList1Outline;
        if (element == targetHud) return targetHudOutline;
        return null;
    }

    private FloatSetting shadowStrengthForV2(InterfaceProcessing element) {
        if (element == potions || element == potions1)   return potions1ShadowStrength;
        if (element == keyBinds || element == keyBinds1) return keyBinds1ShadowStrength;
        if (element == staffList || element == staffList1) return staffList1ShadowStrength;
        if (element == targetHud) return targetHudShadowStrength;
        return null;
    }

    private FloatSetting v2HeaderAlphaFor(InterfaceProcessing element) {
        if (element == potions || element == potions1)   return potions1HeaderAlpha;
        if (element == keyBinds || element == keyBinds1) return keyBinds1HeaderAlpha;
        if (element == staffList || element == staffList1) return staffList1HeaderAlpha;
        return null;
    }

    private FloatSetting v2BodyAlphaFor(InterfaceProcessing element) {
        if (element == potions || element == potions1)   return potions1BodyAlpha;
        if (element == keyBinds || element == keyBinds1) return keyBinds1BodyAlpha;
        if (element == staffList || element == staffList1) return staffList1BodyAlpha;
        return null;
    }

    private FloatSetting scaleForV2(InterfaceProcessing element) {
        if (element == potions || element == potions1)   return potions1Scale;
        if (element == keyBinds || element == keyBinds1) return keyBinds1Scale;
        if (element == staffList || element == staffList1) return staffList1Scale;
        if (element == targetHud) return targetHudScale;
        return null;
    }

    private boolean hasScaleSetting(InterfaceProcessing element) {
        return element == potions1 || element == keyBinds1 || element == staffList1
                || element == targetHud || element == notifications;
    }

    private FloatSetting scaleFor(InterfaceProcessing element) {
        if (element == potions1)  return potions1Scale;
        if (element == keyBinds1) return keyBinds1Scale;
        if (element == staffList1) return staffList1Scale;
        if (element == targetHud) return targetHudScale;
        if (element == notifications) return notificationsScale;
        return null;
    }

    private FloatSetting v2HeaderAlpha(InterfaceProcessing element) {
        if (element == potions1)  return potions1HeaderAlpha;
        if (element == keyBinds1) return keyBinds1HeaderAlpha;
        if (element == staffList1) return staffList1HeaderAlpha;
        return null;
    }
    private FloatSetting v2BodyAlpha(InterfaceProcessing element) {
        if (element == potions1)  return potions1BodyAlpha;
        if (element == keyBinds1) return keyBinds1BodyAlpha;
        if (element == staffList1) return staffList1BodyAlpha;
        return null;
    }
    private BooleanSetting shadowToggleFor(InterfaceProcessing element) {
        if (element == potions1)  return potions1Shadow;
        if (element == keyBinds1) return keyBinds1Shadow;
        if (element == staffList1) return staffList1Shadow;
        if (element == targetHud) return targetHudShadow;
        if (element == notifications) return notificationsShadow;
        return null;
    }
    private BooleanSetting outlineToggleFor(InterfaceProcessing element) {
        if (element == potions1)  return potions1Outline;
        if (element == keyBinds1) return keyBinds1Outline;
        if (element == staffList1) return staffList1Outline;
        if (element == targetHud) return targetHudOutline;
        if (element == notifications) return notificationsOutline;
        return null;
    }
    private FloatSetting shadowStrengthFor(InterfaceProcessing element) {
        if (element == potions1)  return potions1ShadowStrength;
        if (element == keyBinds1) return keyBinds1ShadowStrength;
        if (element == staffList1) return staffList1ShadowStrength;
        if (element == targetHud) return targetHudShadowStrength;
        if (element == notifications) return notificationsShadowStrength;
        return null;
    }

    private AnimationUtils blurBgAnimation(InterfaceProcessing element) {
        return blurBgAnimations.computeIfAbsent(element, e -> new AnimationUtils(1f, 15.0f, Easings.CUBIC_OUT));
    }

    private AnimationUtils blurCircleAnimation(InterfaceProcessing element) {
        return blurCircleAnimations.computeIfAbsent(element, e -> new AnimationUtils(1f, 8.2f, Easings.BACK_OUT));
    }

    private AnimationUtils shadowBgAnimation(InterfaceProcessing element) {
        return shadowBgAnimations.computeIfAbsent(element, e -> new AnimationUtils(1f, 15.0f, Easings.CUBIC_OUT));
    }

    private AnimationUtils shadowCircleAnimation(InterfaceProcessing element) {
        return shadowCircleAnimations.computeIfAbsent(element, e -> new AnimationUtils(1f, 8.2f, Easings.BACK_OUT));
    }

    private AnimationUtils outlineBgAnimation(InterfaceProcessing element) {
        return outlineBgAnimations.computeIfAbsent(element, e -> new AnimationUtils(1f, 15.0f, Easings.CUBIC_OUT));
    }

    private AnimationUtils outlineCircleAnimation(InterfaceProcessing element) {
        return outlineCircleAnimations.computeIfAbsent(element, e -> new AnimationUtils(1f, 8.2f, Easings.BACK_OUT));
    }

    private float getTargetHudMenuHeight() {
        return getMenuHeightForElement(hudContextElement);
    }

    private void clampTargetHudMenuToWindow(float menuWidth, float menuHeight) {
        if (mc == null || mc.getWindow() == null) {
            return;
        }
        float maxX = Math.max(2.0f, mc.getWindow().getScaledWidth() - menuWidth - 2.0f);
        float maxY = Math.max(2.0f, mc.getWindow().getScaledHeight() - menuHeight - 2.0f);
        targetHudMenuX = MathHelper.clamp(targetHudMenuX, 2.0f, maxX);
        targetHudMenuY = MathHelper.clamp(targetHudMenuY, 2.0f, maxY);
    }
    public boolean handleHudContextClick(double mouseX, double mouseY, int button) {
        InterfaceProcessing hoveredElement = getHoveredHudElement(mouseX, mouseY);
        if (button == 1 && hoveredElement != null) {
            if (targetHudMenuOpen && hudContextElement == hoveredElement) {
                targetHudMenuOpen = false;
                pendingHudContextElement = null;
            } else if (targetHudMenuOpen && hudContextElement != null && hudContextElement != hoveredElement) {
                pendingHudContextElement = hoveredElement;
                float menuWidth = getTargetHudMenuWidth();
                float menuHeight = getMenuHeightForElement(hoveredElement);
                pendingTargetHudMenuX = hoveredElement.draggable.getX() + hoveredElement.draggable.getWidth() + 4.0f;
                pendingTargetHudMenuY = hoveredElement.draggable.getY() + 1.5f;
                float saveX = targetHudMenuX;
                float saveY = targetHudMenuY;
                targetHudMenuX = pendingTargetHudMenuX;
                targetHudMenuY = pendingTargetHudMenuY;
                clampTargetHudMenuToWindow(menuWidth, menuHeight);
                pendingTargetHudMenuX = targetHudMenuX;
                pendingTargetHudMenuY = targetHudMenuY;
                targetHudMenuX = saveX;
                targetHudMenuY = saveY;
                targetHudMenuOpen = false;
            } else {
                hudContextElement = hoveredElement;
                pendingHudContextElement = null;
                targetHudMenuOpen = true;
                float menuWidth = getTargetHudMenuWidth();
                float menuHeight = getTargetHudMenuHeight();
                targetHudMenuX = hoveredElement.draggable.getX() + hoveredElement.draggable.getWidth() + 4.0f;
                targetHudMenuY = hoveredElement.draggable.getY() + 1.5f;
                clampTargetHudMenuToWindow(menuWidth, menuHeight);
            }
            return true;
        }
        if (!targetHudMenuOpen || hudContextElement == null) {
            return false;
        }
        float menuWidth = getTargetHudMenuWidth();
        float menuHeight = getTargetHudMenuHeight();
        clampTargetHudMenuToWindow(menuWidth, menuHeight);
        float buttonGap = 3.0f;
        float buttonX = targetHudMenuX + 5.0f;
        float buttonW = (menuWidth - 10.0f - buttonGap) / 2.0f;
        float buttonH = 10.0f;
        float normalButtonX = buttonX;
        float unusualButtonX = buttonX + buttonW + buttonGap;
        boolean hasRectSection = hasRectStyleSection(hudContextElement);
        float rectButtonY = hasRectSection
                ? (hudContextElement == waterMark ? targetHudMenuY + 96.0f : targetHudMenuY + 14.0f)
                : -10000f;
        boolean menuHovered = HoveringUtils.isHovered(mouseX, mouseY, targetHudMenuX, targetHudMenuY, menuWidth, menuHeight);
        boolean rectNormalHovered = hasRectSection && HoveringUtils.isHovered(mouseX, mouseY, normalButtonX, rectButtonY, buttonW, buttonH);
        boolean rectUnusualHovered = hasRectSection && HoveringUtils.isHovered(mouseX, mouseY, unusualButtonX, rectButtonY, buttonW, buttonH);
        if (button == 0 && !menuHovered && hoveredElement == hudContextElement) {
            targetHudMenuOpen = false;
            pendingHudContextElement = null;
            return false;
        }
        if (button == 0 && rectNormalHovered) {
            hudContextElement.setHudRectStyle(HudRectStyle.SHADER);
            return true;
        }
        if (button == 0 && rectUnusualHovered) {
            hudContextElement.setHudRectStyle(HudRectStyle.THREADS);
            return true;
        }
        if (hudContextElement == targetHud) {
            boolean shadowOn = targetHudShadow.isState();
            float shiftY = shadowOn ? 20.0f : 0.0f;
            float buttonY = targetHudMenuY + 24.0f;
            float toggleX = targetHudMenuX + menuWidth - 21.0f;
            float particlesToggleY = targetHudMenuY + 4.0f;
            float shadowToggleY = targetHudMenuY + 37.0f;
            float shadowTrackY = targetHudMenuY + 56.0f;
            float outlineToggleY = targetHudMenuY + 48.0f + shiftY;
            float alphaTrackX = targetHudMenuX + 5.0f;
            float alphaTrackW = menuWidth - 10.0f;
            float alphaTrackY = targetHudMenuY + 71.0f + shiftY;
            boolean normalHovered = HoveringUtils.isHovered(mouseX, mouseY, normalButtonX, buttonY, buttonW, buttonH);
            boolean unusualHovered = HoveringUtils.isHovered(mouseX, mouseY, unusualButtonX, buttonY, buttonW, buttonH);
            boolean particlesHovered = HoveringUtils.isHovered(mouseX, mouseY, toggleX, particlesToggleY, 16.0f, 9.0f);
            boolean shadowHovered = HoveringUtils.isHovered(mouseX, mouseY, toggleX, shadowToggleY, 16.0f, 9.0f);
            boolean outlineHovered = HoveringUtils.isHovered(mouseX, mouseY, toggleX, outlineToggleY, 16.0f, 9.0f);
            if (button == 0 && normalHovered) {
                targetHud.setHealthBarStyleEnabled(false);
                return true;
            }
            if (button == 0 && unusualHovered) {
                targetHud.setHealthBarStyleEnabled(true);
                return true;
            }
            if (button == 0 && particlesHovered) {
                targetHud.setHeadParticlesEnabled(!targetHud.isHeadParticlesEnabled());
                return true;
            }
            if (button == 0 && shadowHovered) {
                targetHudShadow.setState(!targetHudShadow.isState());
                return true;
            }
            if (button == 0 && outlineHovered) {
                targetHudOutline.setState(!targetHudOutline.isState());
                return true;
            }
            if (shadowOn && trySliderClick(mouseX, mouseY, button, targetHudShadowStrength, alphaTrackX, shadowTrackY, alphaTrackW)) return true;
            if (trySliderClick(mouseX, mouseY, button, targetHudAlpha, alphaTrackX, alphaTrackY, alphaTrackW)) return true;
            if (trySliderClick(mouseX, mouseY, button, targetHudScale, alphaTrackX, targetHudMenuY + 91.0f + shiftY, alphaTrackW)) return true;
        } else if (showsV2SettingsPanel(hudContextElement)) {
            BooleanSetting shadowSetting = shadowToggleForV2(hudContextElement);
            if (shadowSetting == null) shadowSetting = shadowToggleFor(hudContextElement);
            boolean shadowOn = shadowSetting != null && shadowSetting.isState();
            float shiftY = shadowOn ? 20.0f : 0.0f;
            float toggleX = targetHudMenuX + menuWidth - 21.0f;
            float shadowToggleY = targetHudMenuY + 4.0f;
            float shadowTrackY = targetHudMenuY + 23.0f;
            float outlineToggleY = targetHudMenuY + 15.0f + shiftY;
            float sliderX = targetHudMenuX + 5.0f;
            float sliderW = menuWidth - 10.0f;
            float headerTrackY = targetHudMenuY + 38.0f + shiftY;
            float bodyTrackY = targetHudMenuY + 58.0f + shiftY;
            boolean shadowHovered = HoveringUtils.isHovered(mouseX, mouseY, toggleX, shadowToggleY, 16.0f, 9.0f);
            boolean outlineHovered = HoveringUtils.isHovered(mouseX, mouseY, toggleX, outlineToggleY, 16.0f, 9.0f);
            if (button == 0 && shadowHovered) {
                if (shadowSetting != null) shadowSetting.setState(!shadowSetting.isState());
                return true;
            }
            if (button == 0 && outlineHovered) {
                BooleanSetting o = outlineToggleForV2(hudContextElement);
                if (o == null) o = outlineToggleFor(hudContextElement);
                if (o != null) o.setState(!o.isState());
                return true;
            }
            FloatSetting shadowStrength = shadowStrengthForV2(hudContextElement);
            if (shadowStrength == null) shadowStrength = shadowStrengthFor(hudContextElement);
            FloatSetting headerAlpha = v2HeaderAlphaFor(hudContextElement);
            if (headerAlpha == null) headerAlpha = v2HeaderAlpha(hudContextElement);
            FloatSetting bodyAlpha = v2BodyAlphaFor(hudContextElement);
            if (bodyAlpha == null) bodyAlpha = v2BodyAlpha(hudContextElement);
            FloatSetting scaleSetting = scaleForV2(hudContextElement);
            if (scaleSetting == null) scaleSetting = scaleFor(hudContextElement);
            if (shadowOn && trySliderClick(mouseX, mouseY, button, shadowStrength, sliderX, shadowTrackY, sliderW)) return true;
            if (trySliderClick(mouseX, mouseY, button, headerAlpha, sliderX, headerTrackY, sliderW)) return true;
            if (trySliderClick(mouseX, mouseY, button, bodyAlpha, sliderX, bodyTrackY, sliderW)) return true;
            if (trySliderClick(mouseX, mouseY, button, scaleSetting, sliderX, targetHudMenuY + 78.0f + shiftY, sliderW)) return true;
        } else if (hudContextElement == notifications) {
            BooleanSetting shadowSetting = notificationsShadow;
            boolean shadowOn = shadowSetting.isState();
            float shiftY = shadowOn ? 20.0f : 0.0f;
            float toggleX = targetHudMenuX + menuWidth - 21.0f;
            float shadowToggleY = targetHudMenuY + 4.0f;
            float shadowTrackY = targetHudMenuY + 23.0f;
            float outlineToggleY = targetHudMenuY + 15.0f + shiftY;
            float sliderX = targetHudMenuX + 5.0f;
            float sliderW = menuWidth - 10.0f;
            float alphaTrackY = targetHudMenuY + 38.0f + shiftY;
            boolean shadowHovered = HoveringUtils.isHovered(mouseX, mouseY, toggleX, shadowToggleY, 16.0f, 9.0f);
            boolean outlineHovered = HoveringUtils.isHovered(mouseX, mouseY, toggleX, outlineToggleY, 16.0f, 9.0f);
            if (button == 0 && shadowHovered) {
                notificationsShadow.setState(!notificationsShadow.isState());
                return true;
            }
            if (button == 0 && outlineHovered) {
                notificationsOutline.setState(!notificationsOutline.isState());
                return true;
            }
            if (shadowOn && trySliderClick(mouseX, mouseY, button, notificationsShadowStrength, sliderX, shadowTrackY, sliderW)) return true;
            if (trySliderClick(mouseX, mouseY, button, notificationsAlpha, sliderX, alphaTrackY, sliderW)) return true;
            if (trySliderClick(mouseX, mouseY, button, notificationsScale, sliderX, targetHudMenuY + 58.0f + shiftY, sliderW)) return true;
        } else if (hudContextElement == waterMark) {
            float baseY = targetHudMenuY + 4.5f;
            float toggleX = targetHudMenuX + menuWidth - 21.0f;
            boolean fpsHovered = HoveringUtils.isHovered(mouseX, mouseY, toggleX, baseY, 16.0f, 9.0f);
            boolean msHovered = HoveringUtils.isHovered(mouseX, mouseY, toggleX, baseY + 10.0f, 16.0f, 9.0f);
            boolean serverHovered = HoveringUtils.isHovered(mouseX, mouseY, toggleX, baseY + 20.0f, 16.0f, 9.0f);
            boolean tpsHovered = HoveringUtils.isHovered(mouseX, mouseY, toggleX, baseY + 30.0f, 16.0f, 9.0f);
            boolean tpsRowHovered = HoveringUtils.isHovered(mouseX, mouseY, toggleX, baseY + 40.0f, 16.0f, 9.0f);
            boolean bpsHovered = HoveringUtils.isHovered(mouseX, mouseY, toggleX, baseY + 50.0f, 16.0f, 9.0f);
            boolean xyzHovered = HoveringUtils.isHovered(mouseX, mouseY, toggleX, baseY + 60.0f, 16.0f, 9.0f);
            boolean serverRowHovered = HoveringUtils.isHovered(mouseX, mouseY, toggleX, baseY + 70.0f, 16.0f, 9.0f);
            if (button == 0 && fpsHovered) {
                waterMark.setShowFps(!waterMark.isShowFps());
                return true;
            }
            if (button == 0 && msHovered) {
                waterMark.setShowMs(!waterMark.isShowMs());
                return true;
            }
            if (button == 0 && serverHovered) {
                waterMark.setShowServer(!waterMark.isShowServer());
                return true;
            }
            if (button == 0 && tpsHovered) {
                waterMark.setShowTps(!waterMark.isShowTps());
                return true;
            }
            if (button == 0 && tpsRowHovered) {
                waterMark.setShowTpsRow(!waterMark.isShowTpsRow());
                return true;
            }
            if (button == 0 && bpsHovered) {
                waterMark.setShowBps(!waterMark.isShowBps());
                return true;
            }
            if (button == 0 && xyzHovered) {
                waterMark.setShowXyz(!waterMark.isShowXyz());
                return true;
            }
            if (button == 0 && serverRowHovered) {
                waterMark.setShowServerRow(!waterMark.isShowServerRow());
                return true;
            }
        }
        // ── V2 переключатель для элементов которые его поддерживают ──
        if (hasV2StyleOption(hudContextElement)) {
            float v2ButtonY = targetHudMenuY + getMenuHeightForElement(hudContextElement) - 11.5f;
            boolean v1Hovered  = HoveringUtils.isHovered(mouseX, mouseY, normalButtonX, v2ButtonY, buttonW, buttonH);
            boolean v2Hovered  = HoveringUtils.isHovered(mouseX, mouseY, unusualButtonX, v2ButtonY, buttonW, buttonH);
            if (button == 0 && v1Hovered) {
                hudContextElement.setUseV2Style(false);
                return true;
            }
            if (button == 0 && v2Hovered) {
                hudContextElement.setUseV2Style(true);
                applyV2Defaults(hudContextElement);
                return true;
            }
        }
        if (button == 0 || button == 1) {
            if (menuHovered) {
                return true;
            }
            if (hoveredElement != hudContextElement) {
                targetHudMenuOpen = false;
                pendingHudContextElement = null;
            }
        }
        return false;
    }
    public void renderHudContextMenu(DrawContext context, int mouseX, int mouseY) {
        if (draggedSlider != null) {
            boolean leftPressed = mc != null && mc.getWindow() != null
                    && GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            if (!leftPressed) {
                draggedSlider = null;
            } else {
                updateDraggedSliderFromMouse(mouseX);
            }
        }
        if (hudContextElement != null && !isHudElementEnabled(hudContextElement)) {
            targetHudMenuOpen = false;
            hudContextElement = null;
            pendingHudContextElement = null;
        }
        targetHudMenuAnimation.update(targetHudMenuOpen ? 1.0f : 0.0f);
        float targetMenuProgress = MathHelper.clamp(targetHudMenuAnimation.getValue(), 0.0f, 1.0f);
        if (!targetHudMenuOpen && targetMenuProgress <= 0.01f) {
            if (pendingHudContextElement != null) {
                hudContextElement = pendingHudContextElement;
                pendingHudContextElement = null;
                targetHudMenuX = pendingTargetHudMenuX;
                targetHudMenuY = pendingTargetHudMenuY;
                targetHudMenuOpen = true;
            } else {
                hudContextElement = null;
            }
        }
        if (!targetHudMenuOpen && targetMenuProgress <= 0.01f && hudContextElement == null) {
            hudContextElement = null;
            return;
        }
        if (hudContextElement == null) {
            return;
        }
        boolean targetContext = hudContextElement == targetHud;
        boolean waterMarkContext = hudContextElement == waterMark;
        boolean hudStyleContext = isHudStyleElement(hudContextElement);
        boolean notificationsContext = hudContextElement == notifications;
        float menuWidth = getTargetHudMenuWidth();
        float menuHeight = getTargetHudMenuHeight();
        clampTargetHudMenuToWindow(menuWidth, menuHeight);
        float x = targetHudMenuX;
        float y = targetHudMenuY;
        int themeColor = getThemeColor();
        float contentProgress = MathHelper.clamp((targetMenuProgress - 0.06f) / 0.94f, 0.0f, 1.0f);
        int textAlpha = fadeTextAlphaSafe(contentProgress, 255, 2);
        var matrices = context.getMatrices();
        matrices.push();
        RenderUtils.drawDefaultHudPanel(matrices, x, y, menuWidth, menuHeight, 3.0f, 3.5f,
                ColorUtils.applyAlpha(ColorUtils.rgba(50, 50, 50, 255), targetMenuProgress),
                ColorUtils.applyAlpha(ColorUtils.darken(themeColor, 0.15f), targetMenuProgress),
                ColorUtils.applyAlpha(ColorUtils.darken(themeColor, 0.05f), targetMenuProgress));
        if (contentProgress <= 0.02f) {
            matrices.pop();
            return;
        }
        float buttonGap = 3.0f;
        float buttonX = x + 5.0f;
        float buttonW = (menuWidth - 10.0f - buttonGap) / 2.0f;
        float buttonH = 10.0f;
        float normalX = buttonX;
        float unusualX = buttonX + buttonW + buttonGap;
        int inactiveColor = ColorUtils.applyAlpha(ColorUtils.rgba(70, 70, 70, 255), contentProgress);
        int activeLeftColor = fadeColorSafe(ColorUtils.darken(themeColor, 0.4f), contentProgress, 2);
        int activeRightColor = fadeColorSafe(themeColor, contentProgress, 2);
        if (targetContext) {
            issue(12).drawStringWithFade(matrices, "Партиклы с головы", x + 4.7f, y + 7.5f, menuWidth - 28.0f,
                    ColorUtils.rgba(255, 255, 255, textAlpha));
            targetHudParticlesBgAnimation.update(targetHud.isHeadParticlesEnabled() ? 1.0f : 0.0f);
            targetHudParticlesCircleAnimation.update(targetHud.isHeadParticlesEnabled() ? 1.0f : 0.0f);
            float bgProgress = targetHudParticlesBgAnimation.getValue();
            float circleProgress = targetHudParticlesCircleAnimation.getValue();
            int particlesOffColor = ColorUtils.darken(themeColor, 0.05f);
            int particlesColor = ColorUtils.interpolateColor(particlesOffColor, themeColor, bgProgress);
            float particlesToggleX = x + menuWidth - 21.0f;
            float particlesToggleY = y + 4.5f;
            RenderUtils.drawGradientRect(matrices, particlesToggleX, particlesToggleY, 16.0f, 9.0f, 3,
                    fadeColorSafe(particlesColor, contentProgress, 2), fadeColorSafe(ColorUtils.darken(particlesColor, 0.65f), contentProgress, 2));
            float particlesCircleX = particlesToggleX + 4.5f + (circleProgress * 6.2f);
            RenderUtils.drawRoundCircle(matrices, particlesCircleX + 0.5f, particlesToggleY + 4.5f, 6.85f,
                    ColorUtils.rgba(255, 255, 255, textAlpha));
            // Вид полоски
            issue(12).draw(matrices, "Вид полоски", x + 4.7f, y + 17.0f,
                    ColorUtils.rgba(255, 255, 255, fadeTextAlphaSafe(contentProgress, 225, 2)));
            float buttonY = y + 24.0f;
            boolean healthBarStyle = targetHud.isHealthBarStyleEnabled();
            targetHudBarSwitchAnimation.update(healthBarStyle ? 1.0f : 0.0f);
            float typeSwitchProgress = MathHelper.clamp(targetHudBarSwitchAnimation.getValue(), 0.0f, 1.0f);
            RenderUtils.drawRoundedRect(matrices, normalX, buttonY, buttonW, buttonH, 1.5f, inactiveColor);
            RenderUtils.drawRoundedRect(matrices, unusualX, buttonY, buttonW, buttonH, 1.5f, inactiveColor);
            float activeX = MathHelper.lerp(typeSwitchProgress, normalX, unusualX);
            RenderUtils.drawGradientRect(matrices, activeX, buttonY, buttonW, buttonH, 1.5f, activeLeftColor, activeRightColor, true);
            String normalText = "Клиентский";
            String unusualText = "Здоровье";
            float normalTextX = normalX + (buttonW - issue(12).getWidth(normalText)) * 0.5f;
            float unusualTextX = unusualX + (buttonW - issue(12).getWidth(unusualText)) * 0.55f;
            int normalTextAlpha = MathHelper.clamp((int) (textAlpha * (0.65f + 0.35f * (1.0f - typeSwitchProgress))), 0, 255);
            int unusualTextAlpha = MathHelper.clamp((int) (textAlpha * (0.65f + 0.35f * typeSwitchProgress)), 0, 255);
            issue(12).draw(matrices, normalText, normalTextX, buttonY + 3.8f, ColorUtils.rgba(255, 255, 255, normalTextAlpha));
            issue(12).draw(matrices, unusualText, unusualTextX, buttonY + 3.8f, ColorUtils.rgba(255, 255, 255, unusualTextAlpha));
            drawShadowToggle(matrices, hudContextElement, targetHudShadow, x + 5f, x + menuWidth - 21.0f, y + 37.0f,
                    themeColor, contentProgress, textAlpha);
            boolean shadowOn = targetHudShadow.isState();
            float shiftY = shadowOn ? 20.0f : 0.0f;
            if (shadowOn) {
                drawAlphaSlider(matrices, targetHudShadowStrength, "Сила тени",
                        x + 5.0f, x + 5.0f, y + 56.0f, menuWidth - 10.0f,
                        themeColor, contentProgress, textAlpha);
            }
            drawOutlineToggle(matrices, hudContextElement, targetHudOutline, x + 5f, x + menuWidth - 21.0f, y + 48.0f + shiftY,
                    themeColor, contentProgress, textAlpha);
            drawAlphaSlider(matrices, targetHudAlpha, "Прозрачность",
                    x + 5.0f, x + 5.0f, y + 71.0f + shiftY, menuWidth - 10.0f,
                    themeColor, contentProgress, textAlpha);
            drawAlphaSlider(matrices, targetHudScale, "Размер",
                    x + 5.0f, x + 5.0f, y + 91.0f + shiftY, menuWidth - 10.0f,
                    themeColor, contentProgress, textAlpha);
            // v1/v2 переключатель для таргет худ
            {
                float sv2Y = y + menuHeight - 13.0f;
                float sv2Anim = targetHud.isUseV2Style() ? 1f : 0f;
                issue(11).draw(matrices, "Стиль", x + 4.7f, sv2Y - 6f, ColorUtils.rgba(255,255,255,fadeTextAlphaSafe(contentProgress,180,2)));
                RenderUtils.drawRoundedRect(matrices, normalX, sv2Y, buttonW, buttonH, 1.5f, inactiveColor);
                RenderUtils.drawRoundedRect(matrices, unusualX, sv2Y, buttonW, buttonH, 1.5f, inactiveColor);
                RenderUtils.drawGradientRect(matrices, MathHelper.lerp(sv2Anim,normalX,unusualX), sv2Y, buttonW, buttonH, 1.5f, activeLeftColor, activeRightColor, true);
                issue(11).draw(matrices, "v1", normalX+(buttonW-issue(11).getWidth("v1"))*0.5f, sv2Y+3.5f, ColorUtils.rgba(255,255,255,MathHelper.clamp((int)(textAlpha*(0.65f+0.35f*(1f-sv2Anim))),0,255)));
                issue(11).draw(matrices, "v2", unusualX+(buttonW-issue(11).getWidth("v2"))*0.5f, sv2Y+3.5f, ColorUtils.rgba(255,255,255,MathHelper.clamp((int)(textAlpha*(0.65f+0.35f*sv2Anim)),0,255)));
            }
        } else if (showsV2SettingsPanel(hudContextElement)) {
            BooleanSetting shadowSetting = shadowToggleForV2(hudContextElement);
            if (shadowSetting == null) shadowSetting = shadowToggleFor(hudContextElement);
            BooleanSetting outlineSetting = outlineToggleForV2(hudContextElement);
            if (outlineSetting == null) outlineSetting = outlineToggleFor(hudContextElement);
            FloatSetting shadowStrength = shadowStrengthForV2(hudContextElement);
            if (shadowStrength == null) shadowStrength = shadowStrengthFor(hudContextElement);
            FloatSetting headerAlpha = v2HeaderAlphaFor(hudContextElement);
            if (headerAlpha == null) headerAlpha = v2HeaderAlpha(hudContextElement);
            FloatSetting bodyAlpha = v2BodyAlphaFor(hudContextElement);
            if (bodyAlpha == null) bodyAlpha = v2BodyAlpha(hudContextElement);
            FloatSetting scaleSetting = scaleForV2(hudContextElement);
            if (scaleSetting == null) scaleSetting = scaleFor(hudContextElement);
            drawV2Panel(matrices, hudContextElement, shadowSetting, outlineSetting, shadowStrength,
                    headerAlpha, bodyAlpha, scaleSetting, x, y + 4.0f, menuWidth, themeColor, contentProgress, textAlpha);
            if (hudStyleContext) {
                drawHudStyleSwitch(matrices, hudContextElement, x, y, menuWidth, menuHeight,
                        normalX, unusualX, buttonW, buttonH, inactiveColor, activeLeftColor, activeRightColor,
                        contentProgress, textAlpha);
            }
        } else if (hudStyleContext) {
            drawHudStyleSwitch(matrices, hudContextElement, x, y, menuWidth, menuHeight,
                    normalX, unusualX, buttonW, buttonH, inactiveColor, activeLeftColor, activeRightColor,
                    contentProgress, textAlpha);
        } else if (waterMarkContext) {
            float wmToggleX = x + menuWidth - 21.0f;
            float wmBaseY = y + 3.5f;
            float wmLabelX = x + 5;
            drawWaterMarkToggle(matrices, "Отображать фпс",    wmLabelX, wmToggleX, wmBaseY,        waterMark.isShowFps(),       waterMarkFpsBgAnimation,        waterMarkFpsCircleAnimation,        themeColor, contentProgress, textAlpha);
            drawWaterMarkToggle(matrices, "Отображать пинг",   wmLabelX, wmToggleX, wmBaseY + 10.0f, waterMark.isShowMs(),        waterMarkMsBgAnimation,         waterMarkMsCircleAnimation,         themeColor, contentProgress, textAlpha);
            drawWaterMarkToggle(matrices, "Отображать сервер", wmLabelX, wmToggleX, wmBaseY + 20.0f, waterMark.isShowServer(),    waterMarkServerBgAnimation,     waterMarkServerCircleAnimation,     themeColor, contentProgress, textAlpha);
            drawWaterMarkToggle(matrices, "Отображать тпс",    wmLabelX, wmToggleX, wmBaseY + 30.0f, waterMark.isShowTps(),       waterMarkTpsBgAnimation,        waterMarkTpsCircleAnimation,        themeColor, contentProgress, textAlpha);
            drawWaterMarkToggle(matrices, "Тпс строка",        wmLabelX, wmToggleX, wmBaseY + 40.0f, waterMark.isShowTpsRow(),    waterMarkTpsRowBgAnimation,     waterMarkTpsRowCircleAnimation,     themeColor, contentProgress, textAlpha);
            drawWaterMarkToggle(matrices, "BPS",               wmLabelX, wmToggleX, wmBaseY + 50.0f, waterMark.isShowBps(),       waterMarkBpsBgAnimation,        waterMarkBpsCircleAnimation,        themeColor, contentProgress, textAlpha);
            drawWaterMarkToggle(matrices, "XYZ",               wmLabelX, wmToggleX, wmBaseY + 60.0f, waterMark.isShowXyz(),       waterMarkXyzBgAnimation,        waterMarkXyzCircleAnimation,        themeColor, contentProgress, textAlpha);
            drawWaterMarkToggle(matrices, "IP сервера",        wmLabelX, wmToggleX, wmBaseY + 70.0f, waterMark.isShowServerRow(), waterMarkServerRowBgAnimation,  waterMarkServerRowCircleAnimation,  themeColor, contentProgress, textAlpha);
        } else if (notificationsContext) {
            BooleanSetting shadowSetting = notificationsShadow;
            drawShadowToggle(matrices, hudContextElement, shadowSetting,
                    x + 5f, x + menuWidth - 21.0f, y + 4.0f,
                    themeColor, contentProgress, textAlpha);
            boolean shadowOn = shadowSetting.isState();
            float shiftY = shadowOn ? 20.0f : 0.0f;
            if (shadowOn) {
                drawAlphaSlider(matrices, notificationsShadowStrength, "Сила тени",
                        x + 5.0f, x + 5.0f, y + 23.0f, menuWidth - 10.0f,
                        themeColor, contentProgress, textAlpha);
            }
            drawOutlineToggle(matrices, hudContextElement, notificationsOutline,
                    x + 5f, x + menuWidth - 21.0f, y + 15.0f + shiftY,
                    themeColor, contentProgress, textAlpha);
            drawAlphaSlider(matrices, notificationsAlpha, "Прозрачность",
                    x + 5.0f, x + 5.0f, y + 38.0f + shiftY, menuWidth - 10.0f,
                    themeColor, contentProgress, textAlpha);
            drawAlphaSlider(matrices, notificationsScale, "Размер",
                    x + 5.0f, x + 5.0f, y + 58.0f + shiftY, menuWidth - 10.0f,
                    themeColor, contentProgress, textAlpha);
        }
        // v1/v2 кнопки и настройки вставлены прямо внутри каждой секции элемента

        if (hasRectStyleSection(hudContextElement)) {
            issue(12).draw(matrices, "Тип ректа", x + 4.7f, waterMarkContext ? y + 89.5f : y + 7.5f,
                    ColorUtils.rgba(255, 255, 255, fadeTextAlphaSafe(contentProgress, 225, 2)));
            float rectButtonY = waterMarkContext ? y + 96.0f : y + 14.0f;            boolean threadsRect = hudContextElement.isHudRectThreads();
            hudRectTypeSwitchAnimation.update(threadsRect ? 1.0f : 0.0f);
            float rectSwitchProgress = MathHelper.clamp(hudRectTypeSwitchAnimation.getValue(), 0.0f, 1.0f);
            RenderUtils.drawRoundedRect(matrices, normalX, rectButtonY, buttonW, buttonH, 1.5f, inactiveColor);
            RenderUtils.drawRoundedRect(matrices, unusualX, rectButtonY, buttonW, buttonH, 1.5f, inactiveColor);
            float rectActiveX = MathHelper.lerp(rectSwitchProgress, normalX, unusualX);
            RenderUtils.drawGradientRect(matrices, rectActiveX, rectButtonY, buttonW, buttonH, 1.5f, activeLeftColor, activeRightColor, true);
            String normalRectText = "Обычный";
            String unusualRectText = "Нитки";
            float normalRectTextX = normalX + (buttonW - issue(12).getWidth(normalRectText)) * 0.5f;
            float unusualRectTextX = unusualX + (buttonW - issue(12).getWidth(unusualRectText)) * 0.5f;
            int normalRectAlpha = MathHelper.clamp((int) (textAlpha * (0.65f + 0.35f * (1.0f - rectSwitchProgress))), 0, 255);
            int unusualRectAlpha = MathHelper.clamp((int) (textAlpha * (0.65f + 0.35f * rectSwitchProgress)), 0, 255);
            issue(12).draw(matrices, normalRectText, normalRectTextX, rectButtonY + 3.8f, ColorUtils.rgba(255, 255, 255, normalRectAlpha));
            issue(12).draw(matrices, unusualRectText, unusualRectTextX, rectButtonY + 3.8f, ColorUtils.rgba(255, 255, 255, unusualRectAlpha));
        }
        matrices.pop();
    }

    /** Рисует кнопки v1/v2 в верхней части меню */
    private void drawV1V2Switch(net.minecraft.client.util.math.MatrixStack m,
                                InterfaceProcessing el,
                                float x, float y, float menuW,
                                float bW, float bH, float normalX, float unusualX,
                                int activeLeft, int activeRight, int inactive,
                                float cp, int ta) {
        boolean useV2 = el.isUseV2Style();
        issue(11).draw(m, "Стиль", x + 4.7f, y + 3.5f, ColorUtils.rgba(255,255,255,fadeTextAlphaSafe(cp,180,2)));
        float anim = useV2 ? 1f : 0f;
        RenderUtils.drawRoundedRect(m, normalX, y + 2f, bW, bH, 1.5f, inactive);
        RenderUtils.drawRoundedRect(m, unusualX, y + 2f, bW, bH, 1.5f, inactive);
        RenderUtils.drawGradientRect(m, MathHelper.lerp(anim,normalX,unusualX), y + 2f, bW, bH, 1.5f, activeLeft, activeRight, true);
        issue(11).draw(m,"v1",normalX+(bW-issue(11).getWidth("v1"))*0.5f, y+5.6f,
                ColorUtils.rgba(255,255,255,MathHelper.clamp((int)(ta*(0.65f+0.35f*(1f-anim))),0,255)));
        issue(11).draw(m,"v2",unusualX+(bW-issue(11).getWidth("v2"))*0.5f, y+5.6f,
                ColorUtils.rgba(255,255,255,MathHelper.clamp((int)(ta*(0.65f+0.35f*anim)),0,255)));
    }

    /** Рисует v2-настройки (тень, обводка, прозрачности, размер) для v2-элемента */
    private void drawV2Panel(net.minecraft.client.util.math.MatrixStack m,
                             InterfaceProcessing v2el,
                             BooleanSetting shadow, BooleanSetting outline,
                             FloatSetting shadowStr, FloatSetting headerA,
                             FloatSetting bodyA, FloatSetting scale,
                             float x, float y, float menuW,
                             int theme, float cp, int ta) {
        boolean shadowOn = shadow != null && shadow.isState();
        float sh = shadowOn ? 20f : 0f;
        drawShadowToggle(m, v2el, shadow, x+5f, x+menuW-21f, y, theme, cp, ta);
        if (shadowOn) drawAlphaSlider(m, shadowStr, "Сила тени", x+5f, x+5f, y+19f, menuW-10f, theme, cp, ta);
        drawOutlineToggle(m, v2el, outline, x+5f, x+menuW-21f, y+11f+sh, theme, cp, ta);
        if (headerA != null) drawAlphaSlider(m, headerA, "Прозрачность шапки", x+5f, x+5f, y+34f+sh, menuW-10f, theme, cp, ta);
        if (bodyA  != null) drawAlphaSlider(m, bodyA,   "Прозрачность фона",   x+5f, x+5f, y+54f+sh, menuW-10f, theme, cp, ta);
        if (scale  != null) drawAlphaSlider(m, scale,   "Размер",               x+5f, x+5f, y+74f+sh, menuW-10f, theme, cp, ta);
    }

    private void drawBlurToggle(net.minecraft.client.util.math.MatrixStack matrices,
                                InterfaceProcessing element,
                                float labelX,
                                float toggleX,
                                float toggleY,
                                int themeColor,
                                float contentProgress,
                                int textAlpha) {
        issue(12).draw(matrices, "Блюр", labelX, toggleY + 3.0f, ColorUtils.rgba(255, 255, 255, textAlpha));
        AnimationUtils bg = blurBgAnimation(element);
        AnimationUtils circle = blurCircleAnimation(element);
        bg.update(element.isBlurEnabled() ? 1.0f : 0.0f);
        circle.update(element.isBlurEnabled() ? 1.0f : 0.0f);
        float bgProgress = bg.getValue();
        float circleProgress = circle.getValue();
        int offColor = ColorUtils.darken(themeColor, 0.05f);
        int toggleColor = ColorUtils.interpolateColor(offColor, themeColor, bgProgress);
        RenderUtils.drawGradientRect(matrices, toggleX, toggleY, 16.0f, 9.0f, 3,
                fadeColorSafe(toggleColor, contentProgress, 2),
                fadeColorSafe(ColorUtils.darken(toggleColor, 0.65f), contentProgress, 2));
        float circleX = toggleX + 4.5f + (circleProgress * 6.2f);
        RenderUtils.drawRoundCircle(matrices, circleX + 0.5f, toggleY + 4.5f, 6.85f, ColorUtils.rgba(255, 255, 255, textAlpha));
    }

    private void drawShadowToggle(net.minecraft.client.util.math.MatrixStack matrices,
                                  InterfaceProcessing element,
                                  BooleanSetting setting,
                                  float labelX,
                                  float toggleX,
                                  float toggleY,
                                  int themeColor,
                                  float contentProgress,
                                  int textAlpha) {
        issue(12).draw(matrices, "Тень", labelX, toggleY + 3.0f, ColorUtils.rgba(255, 255, 255, textAlpha));
        AnimationUtils bg = shadowBgAnimation(element);
        AnimationUtils circle = shadowCircleAnimation(element);
        boolean on = setting != null && setting.isState();
        bg.update(on ? 1.0f : 0.0f);
        circle.update(on ? 1.0f : 0.0f);
        float bgProgress = bg.getValue();
        float circleProgress = circle.getValue();
        int offColor = ColorUtils.darken(themeColor, 0.05f);
        int toggleColor = ColorUtils.interpolateColor(offColor, themeColor, bgProgress);
        RenderUtils.drawGradientRect(matrices, toggleX, toggleY, 16.0f, 9.0f, 3,
                fadeColorSafe(toggleColor, contentProgress, 2),
                fadeColorSafe(ColorUtils.darken(toggleColor, 0.65f), contentProgress, 2));
        float circleX = toggleX + 4.5f + (circleProgress * 6.2f);
        RenderUtils.drawRoundCircle(matrices, circleX + 0.5f, toggleY + 4.5f, 6.85f, ColorUtils.rgba(255, 255, 255, textAlpha));
    }

    private void drawOutlineToggle(net.minecraft.client.util.math.MatrixStack matrices,
                                   InterfaceProcessing element,
                                   BooleanSetting setting,
                                   float labelX,
                                   float toggleX,
                                   float toggleY,
                                   int themeColor,
                                   float contentProgress,
                                   int textAlpha) {
        issue(12).draw(matrices, "Обводка", labelX, toggleY + 3.0f, ColorUtils.rgba(255, 255, 255, textAlpha));
        AnimationUtils bg = outlineBgAnimation(element);
        AnimationUtils circle = outlineCircleAnimation(element);
        boolean on = setting != null && setting.isState();
        bg.update(on ? 1.0f : 0.0f);
        circle.update(on ? 1.0f : 0.0f);
        float bgProgress = bg.getValue();
        float circleProgress = circle.getValue();
        int offColor = ColorUtils.darken(themeColor, 0.05f);
        int toggleColor = ColorUtils.interpolateColor(offColor, themeColor, bgProgress);
        RenderUtils.drawGradientRect(matrices, toggleX, toggleY, 16.0f, 9.0f, 3,
                fadeColorSafe(toggleColor, contentProgress, 2),
                fadeColorSafe(ColorUtils.darken(toggleColor, 0.65f), contentProgress, 2));
        float circleX = toggleX + 4.5f + (circleProgress * 6.2f);
        RenderUtils.drawRoundCircle(matrices, circleX + 0.5f, toggleY + 4.5f, 6.85f, ColorUtils.rgba(255, 255, 255, textAlpha));
    }

    private void drawAlphaSlider(net.minecraft.client.util.math.MatrixStack matrices,
                                 FloatSetting setting,
                                 String label,
                                 float labelX,
                                 float trackX,
                                 float trackY,
                                 float trackW,
                                 int themeColor,
                                 float contentProgress,
                                 int textAlpha) {
        if (setting == null) return;
        String valueText = ((int) setting.get()) + "%";
        float valueW = issue(12).getWidth(valueText);
        issue(12).draw(matrices, label, labelX, trackY - 7.5f,
                ColorUtils.rgba(255, 255, 255, textAlpha));
        issue(12).draw(matrices, valueText, trackX + trackW - valueW, trackY - 7.5f,
                ColorUtils.rgba(255, 255, 255, MathHelper.clamp((int) (textAlpha * 0.75f), 0, 255)));
        int trackBgColor = fadeColorSafe(ColorUtils.rgba(70, 70, 70, 255), contentProgress, 2);
        RenderUtils.drawRoundedRect(matrices, trackX, trackY, trackW, 3f, 1f, trackBgColor);
        float range = setting.getMax() - setting.getMin();
        float fillW = range > 0f ? trackW * (setting.get() - setting.getMin()) / range : 0f;
        fillW = MathHelper.clamp(fillW, 0f, trackW);
        int sliderColor = fadeColorSafe(themeColor, contentProgress, 2);
        int sliderDark = fadeColorSafe(ColorUtils.darken(themeColor, 0.5f), contentProgress, 2);
        RenderUtils.drawGradientRect(matrices, trackX, trackY, fillW, 3f, 1f,
                sliderDark, sliderColor, sliderDark, sliderColor);
        float circleX = trackX + fillW;
        RenderUtils.drawRoundedRect(matrices, circleX - 2.0f, trackY - 1.5f, 4f, 6f, 1.5f,
                fadeColorSafe(ColorUtils.rgba(235, 235, 235, 255), contentProgress, 2));
    }

    private boolean trySliderClick(double mouseX, double mouseY, int button,
                                   FloatSetting setting, float trackX, float trackY, float trackW) {
        if (button != 0 || setting == null) return false;
        if (!HoveringUtils.isHovered(mouseX, mouseY, trackX - 1.0f, trackY - 4.0f, trackW + 2.0f, 11.0f)) return false;
        draggedSlider = setting;
        draggedSliderTrackX = trackX;
        draggedSliderTrackW = trackW;
        updateDraggedSliderFromMouse(mouseX);
        return true;
    }

    private void updateDraggedSliderFromMouse(double mouseX) {
        if (draggedSlider == null) return;
        float range = draggedSlider.getMax() - draggedSlider.getMin();
        if (range <= 0f || draggedSliderTrackW <= 0f) return;
        double rel = (mouseX - draggedSliderTrackX) / draggedSliderTrackW;
        if (rel < 0) rel = 0;
        if (rel > 1) rel = 1;
        double raw = draggedSlider.getMin() + rel * range;
        double inc = draggedSlider.getIncrement();
        if (inc > 0) raw = Math.round(raw / inc) * inc;
        if (raw < draggedSlider.getMin()) raw = draggedSlider.getMin();
        if (raw > draggedSlider.getMax()) raw = draggedSlider.getMax();
        draggedSlider.setValue((float) raw);
    }

    private void renderHudElement(InterfaceProcessing element, EventRender.Default event) {
        long start = PERF_DEBUG ? System.nanoTime() : 0L;
        element.draggable.beginRenderTilt(event.getContext().getMatrices());
        try {
            element.onRender(event);
        } finally {
            element.draggable.endRenderTilt(event.getContext().getMatrices());
            if (PERF_DEBUG) {
                long elapsed = System.nanoTime() - start;
                if (elapsed >= SLOW_HUD_ELEMENT_NANOS) {
                    logSlowHudElement(element, elapsed);
                }
            }
        }
    }

    private void logSlowHudElement(InterfaceProcessing element, long elapsedNanos) {
        String name = element.getClass().getSimpleName();
        long now = System.nanoTime();
        Long lastWarn = PERF_WARNINGS.get(name);
        if (lastWarn != null && now - lastWarn < PERF_WARN_COOLDOWN_NANOS) {
            return;
        }

        PERF_WARNINGS.put(name, now);
        System.out.println(String.format(Locale.ROOT,
                "[PerfDebug] Slow HUD element: Interface -> %s took %.2f ms",
                name,
                elapsedNanos / 1_000_000.0D));
    }

    private void drawWaterMarkToggle(net.minecraft.client.util.math.MatrixStack matrices,
                                     String label,
                                     float labelX,
                                     float toggleX,
                                     float toggleY,
                                     boolean enabled,
                                     AnimationUtils bgAnimation,
                                     AnimationUtils circleAnimation,
                                     int themeColor,
                                     float contentProgress,
                                     int textAlpha) {
        issue(12).draw(matrices, label, labelX, toggleY + 3.0f, ColorUtils.rgba(255, 255, 255, textAlpha));
        bgAnimation.update(enabled ? 1.0f : 0.0f);
        circleAnimation.update(enabled ? 1.0f : 0.0f);
        float bgProgress = bgAnimation.getValue();
        float circleProgress = circleAnimation.getValue();
        int offColor = ColorUtils.darken(themeColor, 0.05f);
        int toggleColor = ColorUtils.interpolateColor(offColor, themeColor, bgProgress);
        RenderUtils.drawGradientRect(matrices, toggleX, toggleY, 16.0f, 9.0f, 3,
                fadeColorSafe(toggleColor, contentProgress, 2),
                fadeColorSafe(ColorUtils.darken(toggleColor, 0.65f), contentProgress, 2));
        float circleX = toggleX + 4.5f + (circleProgress * 6.2f);
        RenderUtils.drawRoundCircle(matrices, circleX + 0.5f, toggleY + 4.5f, 6.85f, ColorUtils.rgba(255, 255, 255, textAlpha));
    }

    public boolean isHudModuleVisible(String name) {
        try { return hudModules.is(name); } catch (Exception ignored) { return false; }
    }

    public void setHudModuleVisible(String name, boolean state) {
        try { hudModules.set(name, state); } catch (Exception ignored) {}
    }

    public java.util.List<String> getHudModuleNames() {
        java.util.List<String> names = new java.util.ArrayList<>();
        for (var s : hudModules.getSettings()) names.add(s.name());
        return names;
    }

    public Map<String, InterfaceProcessing> getConfigurableHudElements() {
        Map<String, InterfaceProcessing> elements = new LinkedHashMap<>();
        elements.put("waterMark", waterMark);
        elements.put("keyBinds", keyBinds);
        elements.put("helperBinds", helperBinds);
        elements.put("potions", potions);
        elements.put("notifications", notifications);
        elements.put("targetHud", targetHud);
        elements.put("staffList", staffList);
        elements.put("nowPlaying", nowPlaying);
        elements.put("armorStatus", armorStatus);
        elements.put("itemCounter", itemCounter);
        elements.put("potions1", potions1);
        elements.put("keyBinds1", keyBinds1);
        elements.put("staffList1", staffList1);
        return elements;
    }

    @EventLink(priority = Priority.LOWEST)
    public void onEvent(final EventRender.Default event) {
        if (mc != null && mc.currentScreen instanceof ClickGui) {
            return;
        }
        boolean showWaterMark = hudModules.is("Ватермарка");
        boolean showKeyBinds = hudModules.is("Горячие клавиши");
        boolean showHelperBinds = hudModules.is("Серверные бинды");
        boolean showPotions = hudModules.is("Зелья");
        // boolean showInformation = hudModules.is("Информация"); // Removed - no such setting exists
        boolean showStaff = hudModules.is("Стафф");
        boolean showNotifications = hudModules.is("Уведомления");
        boolean showTargetHud = hudModules.is("Таргет худ");
        boolean showNowPlaying = hudModules.is("Музыка");
        boolean showArmorStatus = hudModules.is("Статус брони");
        boolean showItemCounter = hudModules.is("Счётчик предметов");
        boolean showArrayList = hudModules.is("ArrayList");
        boolean showPotions1 = false; // управляется через v2Style флаг
        boolean showKeyBinds1 = false;
        boolean showStaffList1 = false;
        boolean showWaterMarkV2 = false;
        boolean showTargetHudV2 = false;

        if (mc != null && mc.getWindow() != null && mc.currentScreen instanceof ChatScreen) {
            Font hintFont = issue(18);
            float x = (mc.getWindow().getScaledWidth() * 0.5f) - (hintFont.getWidth(HUD_HINT_TEXT) * 0.5f);
            hintFont.draw(event.getContext().getMatrices(), HUD_HINT_TEXT, x, 40.0f, -1);
        }
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        HudMotionTracker.update();
        if (RenderUtils.isLiquidGlassHudActive()) {
            HudBlockOverlayShaderRenderer.INSTANCE.beginFrame();
        }
        try {
            if (showWaterMark) {
                if (waterMark.isUseV2Style()) {
                    // Синхронизируем позицию v2 с v1
                    waterMarkV2.draggable.setX(waterMark.draggable.getX());
                    waterMarkV2.draggable.setY(waterMark.draggable.getY());
                    renderHudElement(this.waterMarkV2, event);
                    // Обновляем размеры v1 чтобы draggable работал корректно
                    waterMark.draggable.setWidth(waterMarkV2.draggable.getWidth());
                    waterMark.draggable.setHeight(waterMarkV2.draggable.getHeight());
                } else {
                    renderHudElement(this.waterMark, event);
                }
            }
            if (showKeyBinds) {
                if (keyBinds.isUseV2Style()) {
                    keyBinds1.draggable.setX(keyBinds.draggable.getX());
                    keyBinds1.draggable.setY(keyBinds.draggable.getY());
                    renderHudElement(this.keyBinds1, event);
                    keyBinds.draggable.setWidth(keyBinds1.draggable.getWidth());
                    keyBinds.draggable.setHeight(keyBinds1.draggable.getHeight());
                } else {
                    renderHudElement(this.keyBinds, event);
                }
            }
            if (showHelperBinds) renderHudElement(this.helperBinds, event);
            if (showPotions) {
                potions.setPotionStyleCompact(potionCompactStyle.isState());
                if (potions.isUseV2Style()) {
                    potions1.draggable.setX(potions.draggable.getX());
                    potions1.draggable.setY(potions.draggable.getY());
                    renderHudElement(this.potions1, event);
                    potions.draggable.setWidth(potions1.draggable.getWidth());
                    potions.draggable.setHeight(potions1.draggable.getHeight());
                } else {
                    renderHudElement(this.potions, event);
                }
            }
            if (showStaff) {
                if (staffList.isUseV2Style()) {
                    staffList1.draggable.setX(staffList.draggable.getX());
                    staffList1.draggable.setY(staffList.draggable.getY());
                    renderHudElement(this.staffList1, event);
                    staffList.draggable.setWidth(staffList1.draggable.getWidth());
                    staffList.draggable.setHeight(staffList1.draggable.getHeight());
                } else {
                    renderHudElement(this.staffList, event);
                }
            }
            if (showNotifications) renderHudElement(this.notifications, event);
            if (showTargetHud) {
                if (targetHud.isUseV2Style()) {
                    targetHudV2.draggable.setX(targetHud.draggable.getX());
                    targetHudV2.draggable.setY(targetHud.draggable.getY());
                    renderHudElement(this.targetHudV2, event);
                    targetHud.draggable.setWidth(targetHudV2.draggable.getWidth());
                    targetHud.draggable.setHeight(targetHudV2.draggable.getHeight());
                } else {
                    renderHudElement(this.targetHud, event);
                }
            }
            if (showNowPlaying) renderHudElement(this.nowPlaying, event);
            if (showArmorStatus) renderHudElement(this.armorStatus, event);
            if (showItemCounter) renderHudElement(this.itemCounter, event);
            if (showArrayList) renderHudElement(this.arrayListHud, event);
            // v2 элементы — только если включены отдельно в hudModules (для независимого использования)
            if (showPotions1 && !potions.isUseV2Style()) renderHudElement(this.potions1, event);
            if (showKeyBinds1 && !keyBinds.isUseV2Style()) renderHudElement(this.keyBinds1, event);
            if (showStaffList1 && !staffList.isUseV2Style()) renderHudElement(this.staffList1, event);
            if (showWaterMarkV2 && !waterMark.isUseV2Style()) renderHudElement(this.waterMarkV2, event);
            if (showTargetHudV2 && !targetHud.isUseV2Style()) renderHudElement(this.targetHudV2, event);
        } finally {
            if (RenderUtils.isLiquidGlassHudActive()) {
                HudBlockOverlayShaderRenderer.INSTANCE.finishFrame();
            }
            RenderUtils.resetGuiRenderState();
        }
        if (!(mc.currentScreen instanceof ChatScreen)) {
            targetHudMenuOpen = false;
            pendingHudContextElement = null;
        }
    }
}
