package tech.lightclient.modules.impl.render;

import tech.lightclient.LightClient;
import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.render.drag.DraggingManager;
import tech.lightclient.client.utils.render.drag.api.Vec2i;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.color.ColorUtils;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.*;
import tech.lightclient.ui.hud.CustomHotbarHud;
import tech.lightclient.ui.hud.MediaPlayer;
import tech.lightclient.ui.hud.star.StarHud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FunctionAdd(name = "Interface", alias = "Interface", category = Category.Render, description = "Настройка интерфейса")
public class Interface extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(Interface.class);

    private static final float DEFAULT_HUD_SCALE = 1.0f;

    private static final float MIN_HUD_SCALE = 0.5f;

    private static final float MAX_HUD_SCALE = 2.0f;

    public static final String HUD_SCALE_KEY = "scale";

    public static final String HUD_ROUNDING_KEY = "rounding";

    public static final String CUSTOM_HOTBAR_NAME = "Кастом хотбар";

    public static final String MEDIA_PLAYER_NAME = "Музыка";

    private static final String[] UNIFIED_HUD_SCALE_SCOPES = {
            "Star Остров",
            "Star Ватермарка",
            "Star Мир",
            "Star FPS",
            "Star Эффекты",
            "Star Бинды",
            "Star Предметы",
            "Star Таргет",
            "Star Нотификации",
            Notifications.SETTINGS_SCOPE,
            MediaPlayer.SETTINGS_SCOPE
    };

    public static ModeSetting mode = new ModeSetting("Режим интерфейса", "Жидкое стекло", "Жидкое стекло", "Минимализм");

    public static SliderSetting glassOpacity = new SliderSetting("Прозрачность", 20f, 0f, 100f, 1f)
            .setVisible(() -> mode.is("Жидкое стекло"));

    public static SliderSetting glassStrength = new SliderSetting("Сила эффекта", 25f, 0f, 100f, 1f)
            .setVisible(() -> mode.is("Жидкое стекло"));

    public static SliderSetting glassDistortion = new SliderSetting("Искажение", 0.08f, -0.2f, 0.2f, 0.01f)
            .setVisible(() -> mode.is("Жидкое стекло"));

    public static SliderSetting glassBlur = new SliderSetting("Сила размытия", 0.5f, 0f, 8f, 0.25f)
            .setVisible(() -> mode.is("Жидкое стекло"));

    public static ModeListSetting elements = new ModeListSetting("Элементы",
            new BooleanSetting("Ватермарка", true),
            new BooleanSetting("Информация", true),
            new BooleanSetting("Активные бинды", true),
            new BooleanSetting("Забинженные предметы", true),
            new BooleanSetting("Активный таргет", true),
            new BooleanSetting("Эффекты", true),
            new BooleanSetting("FPS", true),
            new BooleanSetting(CUSTOM_HOTBAR_NAME, true),
            new BooleanSetting("Кастомные сердца", true),
            new BooleanSetting("Кастомная броня", true),
            new BooleanSetting("Кастомная еда", true),
            new BooleanSetting("Нотификации", true),
            new BooleanSetting(MEDIA_PLAYER_NAME, true)
    );

    private static final float DEFAULT_SHADOW_BLUR = 7.5f;

    private static final float DEFAULT_SHADOW_OPACITY = 12f;

    private static final float DEFAULT_SHADOW_SPREAD = 1f;

    private static final int DARK_BACKGROUND = 0xFF0F0F16;
    private static final int DARK_TEXT = 0xFFE8E8F2;
    private static final int ACCENT = 0xFF5690FF;
    private static final int ACCENT_HOVER = 0xFF669EFF;

    private static float glassAnim = 1f;

    private static boolean appliedDarkTheme = false;

    private StarHud starHud;

    private MediaPlayer mediaPlayer;

    public Interface() {

        super.addSettings(
                mode, glassOpacity, glassStrength, glassDistortion, glassBlur,
                elements
        );

        Vec2i initPos = new Vec2i(10, 10);
        LightClient.createDrag(this, Notifications.SETTINGS_SCOPE, initPos.getX(), initPos.getY());

        this.starHud = StarHud.get(this);

        try {
            this.mediaPlayer = new MediaPlayer(LightClient.createDrag(this, MediaPlayer.SETTINGS_SCOPE, 10, 60));
            LightClient.getEventBus().subscribe(this.mediaPlayer);
        } catch (Throwable throwable) {
            LOGGER.error("MediaPlayer init failed", throwable);
            this.mediaPlayer = null;
        }

        DraggingManager.load();

        normalizeHudScaleValues();

        applyThemePalette();

        setState(true);

    }

    @EventHandler
    public void onTick(UpdateEvent e) {

        glassAnim += ((isLiquidGlass() ? 1f : 0f) - glassAnim) * 0.15f;
        if (glassAnim < 0.001f) glassAnim = 0f;
        if (glassAnim > 0.999f) glassAnim = 1f;

        applyThemePalette();

    }

    private static void applyThemePalette() {
        if (appliedDarkTheme) {
            return;
        }
        appliedDarkTheme = true;
        ClientColors.setThemeColors(
                DARK_BACKGROUND,
                DARK_TEXT,
                ACCENT,
                ACCENT,
                ACCENT_HOVER
        );
    }

    @EventHandler
    public void onRender(EventRender.Screen.Hud event) {

        if (mc.world == null) {
            return;
        }

        try {
            if (starHud != null) {
                starHud.render(event, elements);
            }
        } catch (Throwable throwable) {
            LOGGER.error("Star HUD render failed", throwable);
        }

        renderHudElement(event, CUSTOM_HOTBAR_NAME,
                CustomHotbarHud.shouldUseCustomHotbar(), () -> CustomHotbarHud.render(event));

        if (mediaPlayer != null) {
            boolean mediaEnabled = true;
            try {
                BooleanSetting mediaSetting = elements.getByName(MEDIA_PLAYER_NAME);
                mediaEnabled = mediaSetting == null || mediaSetting.get();
            } catch (Throwable ignored) {
            }
            if (mediaEnabled) {
                renderHudElement(event, MediaPlayer.SETTINGS_SCOPE, true, () -> mediaPlayer.render(event));
            }
        }

    }

    private static void renderHudElement(EventRender.Screen.Hud event, String scope,
                                         boolean enabled, Runnable renderAction) {
        if (!enabled) {
            return;
        }

        Renderer2D renderer = event.getRenderer();
        renderer.pushScale(getHudScale(scope));
        try {
            renderAction.run();
        } catch (Throwable throwable) {
            LOGGER.error("HUD element '{}' render failed", scope, throwable);
        } finally {
            renderer.popScale();
        }
    }

    @EventHandler
    public void onOverGui(EventRender.Screen.OverGui event) {
        try {
            if (starHud != null) {
                starHud.renderPopups(event);
            }
        } catch (Throwable throwable) {
            LOGGER.error("Star HUD popups render failed", throwable);
        }
    }

    public static void hideElement(String name) {
        if (name == null || elements == null) {
            return;
        }
        BooleanSetting entry = elements.getByName(name);
        if (entry != null) {
            entry.set(false);
        }
    }

    public static void showElement(String name) {
        if (name == null || elements == null) {
            return;
        }
        BooleanSetting entry = elements.getByName(name);
        if (entry != null) {
            entry.set(true);
        }
    }

    public static boolean isCustomHotbarEnabled() {
        if (elements == null) {
            return false;
        }
        BooleanSetting setting = elements.getByName(CUSTOM_HOTBAR_NAME);
        return setting != null && setting.get();
    }

    public static boolean isLiquidGlass() {
        return mode != null && mode.is("Жидкое стекло");
    }

    public static boolean glassSelected() {
        return isLiquidGlass();
    }

    public static float glass() {
        return glassAnim;
    }

    public static float minimalizm() {
        return 1.0F - glass();
    }

    public static boolean showGlass() {
        return glass() > 0.0F;
    }

    public static boolean showMinimalizm() {
        return glass() < 1.0F;
    }

    public static float glassOpacityValue() {
        return settingValue(glassOpacity, 20f);
    }

    public static float glassStrengthValue() {
        return settingValue(glassStrength, 25f);
    }

    public static float glassDistortionValue() {
        return settingValue(glassDistortion, 0.08f);
    }

    public static float glassBlurValue() {
        return settingValue(glassBlur, 0.5f);
    }

    public static float glassRoundingValue() {
        return 7f;
    }

    private static float settingValue(SliderSetting setting, float fallback) {
        try {
            if (setting == null) {
                return fallback;
            }
            float value = setting.get().floatValue();
            return Float.isFinite(value) ? value : fallback;
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    public static float getInterfaceScale() {

        return DEFAULT_HUD_SCALE;

    }

    public static float getHudScale(String scope) {

        float fallback = getInterfaceScale();

        if (scope == null || scope.isBlank()) {

            return fallback;

        }

        float value = DraggingManager.getHudFloat(scope, HUD_SCALE_KEY, fallback);

        if (!Float.isFinite(value) || value <= 0f) {

            return fallback;

        }

        return clampHudScale(value);

    }

    private static void normalizeHudScaleValues() {

        if (DraggingManager.hudFloatSettings == null) {

            return;

        }

        for (String scope : UNIFIED_HUD_SCALE_SCOPES) {

            var section = DraggingManager.hudFloatSettings.get(scope);

            if (section == null) {

                continue;

            }

            Float current = section.get(HUD_SCALE_KEY);

            if (current == null) {

                continue;

            }

            if (!Float.isFinite(current) || current <= 0f) {

                DraggingManager.clearHudFloat(scope, HUD_SCALE_KEY);

            }

        }

    }

    private static float clampHudScale(float value) {

        return Math.max(MIN_HUD_SCALE, Math.min(MAX_HUD_SCALE, value));

    }

    public static float scalePos(float value) {

        float scale = getInterfaceScale();

        if (!Float.isFinite(scale) || scale <= 0f) {

            return value;

        }

        return value / scale;

    }

    public static float scalePos(String scope, float value) {

        float scale = getHudScale(scope);

        if (!Float.isFinite(scale) || scale <= 0f) {

            return value;

        }

        return value / scale;

    }

    public static float getHudRoundingScale() {

        return 1f;

    }

    public static float getHudRounding(float baseRounding) {

        return baseRounding * getHudRoundingScale();

    }

    public static float getHudRounding(String scope, float baseRounding) {

        return baseRounding * getHudRoundingScale() * (getHudScale(scope) / Math.max(0.01f, getInterfaceScale()));

    }

    public static int getHudRoundedInt(float baseRounding) {

        return Math.max(0, Math.round(getHudRounding(baseRounding)));

    }

    public static int getHudRoundedInt(String scope, float baseRounding) {

        return Math.max(0, Math.round(getHudRounding(scope, baseRounding)));

    }

    public static float getHudShadowBlur() {

        return DEFAULT_SHADOW_BLUR;

    }

    public static boolean isHudShadowEnabled() {

        return true;

    }

    public static int getAlphaDurationMs() {
        return 300;
    }

    public static int getScaleDurationMs() {
        return 300;
    }

    public static boolean isBounceAnimation() {
        return false;
    }

    public static float animatedScale(float startScale, float progress) {
        return animatedScale(startScale, progress, false);
    }

    public static float animatedScale(float startScale, float progress, boolean hiding) {
        float alphaDuration = getAlphaDurationMs();
        float scaleDuration = getScaleDurationMs();
        float scaleProgress = progress;
        if (scaleDuration > 0f && alphaDuration > 0f) {
            scaleProgress = Math.min(1f, progress * (alphaDuration / scaleDuration));
        }
        float t = Math.max(0f, Math.min(1f, scaleProgress));
        return startScale + (1f - startScale) * t;
    }

    public static boolean isDefaultOutlineColorEnabled() {
        return false;
    }

    public static int[] getDefaultOutlineGlowColors(float alphaProgress) {
        return new int[4];
    }

    public static float getHudShadowOpacity() {

        return DEFAULT_SHADOW_OPACITY;

    }

    public static float getHudShadowSpread() {

        return DEFAULT_SHADOW_SPREAD;

    }

    public static boolean isHudBlurBackground() {
        return isLiquidGlass();
    }

    public static float getHudBlurStrength() {
        return Math.max(1f, glassBlurValue() * 1.25f);
    }

    public static void renderHudBackground(Renderer2D renderer, float x, float y, float width, float height,
                                           float rounding, float alpha) {
        if (renderer == null || width <= 0f || height <= 0f) {
            return;
        }

        float opacity = Math.max(0f, Math.min(1f, alpha));
        if (opacity <= 0f) {
            return;
        }

        if (isHudBlurBackground()) {
            renderer.blur(x, y, width, height, rounding, opacity);
            // Прозрачность стекла: чем выше glassOpacity, тем плотнее тёмная тонировка.
            float tint = 0.20f + 0.80f * (glassOpacityValue() / 100f);
            renderer.rect(x, y, width, height, rounding,
                    ColorUtils.rgba(17, 17, 24, Math.round(130f * tint * opacity)));
            // Сила эффекта: морозный светлый слой, придающий «матовость» стеклу.
            float frost = glassStrengthValue() / 100f;
            if (frost > 0.001f) {
                renderer.rect(x, y, width, height, rounding,
                        ColorUtils.rgba(255, 255, 255, Math.round(70f * frost * opacity)));
            }
            renderer.skipNextHudBackgroundRect(x, y, width, height);
        } else {
            float overlay = 0.15f + 0.55f * (glassOpacityValue() / 100f);
            renderer.rect(x, y, width, height, rounding, ColorUtils.rgba(20, 20, 20, Math.round(255f * overlay * opacity)));
        }
    }

    public static float getHudEditorGridSize() {

        return 12f;

    }

    public static boolean isHudEditorSnapToGridEnabled() {

        return true;

    }

    public static float getHudEditorSnapThreshold() {

        return 10f;

    }

    public static boolean isHudEditorSmartLinesEnabled() {

        return true;

    }
}
