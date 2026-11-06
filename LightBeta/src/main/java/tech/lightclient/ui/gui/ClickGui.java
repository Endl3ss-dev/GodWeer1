package tech.lightclient.ui.gui;

import com.adl.nativeprotect.User;
import tech.lightclient.LightClient;
import tech.lightclient.client.utils.player.PlayerUtils;
import tech.lightclient.client.utils.render.CursorHelper;
import tech.lightclient.client.utils.render.animations.Easings;
import tech.lightclient.client.utils.render.animations.impl.SimpleLinearAnimation;
import tech.lightclient.client.utils.render.color.ColorUtils;
import tech.lightclient.client.utils.render.gif.GifTexture;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontObject;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.ui.gui.elements.CsColorPicker;
import tech.lightclient.ui.gui.elements.CsAutoBuyWidget;
import tech.lightclient.ui.gui.elements.CsConfigWidget;
import tech.lightclient.ui.gui.elements.CsModeListComponent;
import tech.lightclient.ui.gui.elements.CsScriptsWidget;
import tech.lightclient.ui.gui.elements.CsSettingComponent;
import tech.lightclient.ui.gui.elements.CsStringComponent;
import tech.lightclient.ui.gui.elements.ListAppearAnimator;
import tech.lightclient.ui.gui.elements.TextInputField;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import net.minecraft.client.input.CharInput;
import tech.lightclient.modules.api.settings.impl.BindSetting;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ColorSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.api.Setting;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static tech.lightclient.client.utils.client.IMinecraft.mc;

public class ClickGui extends BaseClickGui {
    private static final float PANEL_WIDTH = 452f;
    private static final float PANEL_HEIGHT = 258f;
    private static final float PANEL_ROUNDING = 6f;
    // --- new csgui-style top bar layout ---
    private static final float TOP_BAR_HEIGHT = 27f;
    private static final float TOP_BAR_AVATAR_SIZE = 16f;
    private static final int GEAR_INTRO_DURATION_MS = 1120;
    private static final float TOP_BAR_SIDE_INSET = 9f;
    private static final float TAB_ICON_SLOT = 19f;
    private static final float TAB_ICON_SIZE = 7.4f;
    private static final int CATEGORY_ICON_FADE_IN_DURATION_MS = 840;
    private static final float CATEGORY_ICON_FADE_IN_STAGGER = 0.08f;
    // CLICKGUI_TTF icon glyphs sit high after centeredBaselineOffset; the old left menu
    // compensated with ~1.22×size (7.2 at size 5.9). Reused here so every icon is centred.
    private static final float ICON_BASELINE_FIX = 1.1f;
    private static final float TAB_PILL_PADDING_X = 5f;
    private static final float TAB_PILL_PADDING_Y = 4.5f;
    private static final float CONTENT_INSET = 9f;
    private static final float CONTENT_TOP_GAP = 7f;
    private static final float BOTTOM_BAR_HEIGHT = 18f;
    private static final int GRID_COLUMNS = 3;
    private static final MenuEntry[] BOTTOM_MENU_ITEMS = new MenuEntry[]{
            new MenuEntry("events", "Events", "", null),
            new MenuEntry("cosmetics", "Cosmetics", "", null)
    };
    private static final float LEGACY_PANEL_ROUNDING = 5f;
    private static final float LEFT_SECTION_RATIO = 0.36f;
    private static final float SECTION_GAP = 3f;
    private static final Identifier CLICK_GUI_GIF = Identifier.of("light", "images/clickgui.gif");
    private static final Identifier CUSTOM_CLICK_GUI_GIF = Identifier.of("light", "external/clickgui_custom.gif");
    private static final float TOP_LEFT_SECTION_HEIGHT = 56f;
    private static final float TOP_LEFT_INNER_INSET = 6f;
    private static final float TOP_LEFT_MEDIA_INSET = 3f;
    private static final float TOP_LEFT_MEDIA_HOVER_GROWTH = 2f;
    private static final int TOP_LEFT_INNER_ALPHA = 34;
    private static final int TOP_LEFT_MEDIA_HOVER_ALPHA = 28;
    private static final float LEFT_MENU_TOP_OFFSET = 6f;
    private static final float LEFT_MENU_LEFT_INSET = 8f;
    private static final float LEFT_MENU_RIGHT_INSET = 8f;
    private static final float LEFT_MENU_ROW_HEIGHT = 16f;
    private static final float LEFT_MENU_ROW_GAP = 1f;
    private static final float LEFT_MENU_SECTION_GAP = 6f;
    private static final float LEFT_MENU_AUTOBUY_GAP = 5f;
    private static final float LEFT_MENU_SECTION_INSET_X = 4f;
    private static final float LEFT_MENU_SECTION_TOP_INSET_Y = 0.5f;
    private static final float LEFT_MENU_SECTION_BOTTOM_INSET_Y = 2f;
    private static final float LEFT_MENU_TEXT_SIZE = 8.8f;
    private static final float LEFT_MENU_ICON_SIZE = 5.9f;
    private static final float LEFT_MENU_ICON_GAP = 2f;
    private static final float LEFT_MENU_TEXT_X_OFFSET = 7f;
    private static final float LEFT_MENU_TEXT_Y_OFFSET = 1.5f;
    private static final float LEFT_MENU_ICON_Y_OFFSET = 7.2f;
    private static final float LEFT_MENU_HOVER_SHIFT_X = 0.5f;
    private static final float LEFT_MENU_HOVER_TEXT_SIZE_BOOST = 0.45f;
    private static final float LEFT_MENU_HOVER_ICON_SIZE_BOOST = 0.45f;
    private static final float LEFT_MENU_SELECTED_TEXT_SIZE_BOOST = 0.5f;
    private static final float LEFT_MENU_SELECTED_ICON_SIZE_BOOST = 0.5f;
    private static final float LEFT_MENU_HOVER_ANIM_SPEED = 12f;
    private static final float RIGHT_CONTENT_INSET = 8f;
    private static final float RIGHT_CARD_GAP = 6f;
    private static final float RIGHT_CARD_CORNER = 5f;
    private static final float RIGHT_CARD_PADDING_X = 8f;
    private static final float RIGHT_CARD_PADDING_TOP = 7f;
    private static final float RIGHT_CARD_PADDING_BOTTOM = 8f;
    private static final float RIGHT_CARD_TITLE_DESC_GAP = 6f;
    private static final float RIGHT_CARD_TITLE_SIZE = 8.8f;
    private static final float RIGHT_CARD_BIND_SIZE = 7.1f;
    private static final float RIGHT_CARD_DESC_SIZE = 6.8f;
    private static final int FUNCTION_LIST_SLIDE_IN_DURATION_MS = 720;
    private static final float FUNCTION_LIST_SLIDE_IN_STAGGER = 0.075f;
    private static final float FUNCTION_LIST_SLIDE_IN_DISTANCE = 34f;
    private static final int BOTTOM_TAB_SLIDE_IN_DURATION_MS = 720;
    private static final float BOTTOM_TAB_SLIDE_IN_STAGGER = 0.12f;
    private static final float BOTTOM_TAB_SLIDE_IN_DISTANCE = 22f;
    private static final int SEARCH_SLIDE_IN_DURATION_MS = 720;
    private static final float SEARCH_SLIDE_IN_DISTANCE = 22f;
    private static final float SEARCH_SLIDE_START_PROGRESS = 0.35f;
    private static final int SPECIAL_WIDGET_SLIDE_IN_DURATION_MS = 1875;
    private static final float SPECIAL_WIDGET_SLIDE_IN_DISTANCE = 34f;
    private static final float FUNCTION_LIST_SLIDE_IN_MAX_DELAY = 0.8f;
    /** Дистанция выдвижения элементов поэтапных списков (Events/Cosmetics). */
    private static final float LIST_APPEAR_SLIDE_DISTANCE = 14f;
    private static final float FOOTER_TEXT_LEFT_INSET = 8f;
    private static final float FOOTER_SECTION_HEIGHT = 28f;
    private static final float FOOTER_TEXT_TOP_INSET = 10f;
    private static final float FOOTER_TEXT_LINE_GAP = 9f;
    private static final float FOOTER_TEXT_SIZE = 7.4f;
    private static final float FOOTER_ICON_RIGHT_INSET = 9f;
    private static final float FOOTER_ICON_SIZE = 10.4f;
    private static final float FOOTER_ICON_HOVER_SIZE_BOOST = 0.5f;
    private static final float FOOTER_ICON_HITBOX_PADDING = 1.2f;
    private static final float FOOTER_ICON_Y_OFFSET = 10f;
    private static final float SEARCH_BOX_HEIGHT = 17f;
    private static final float SEARCH_BOX_SIDE_INSET = 18f;
    private static final float SEARCH_BOX_MIN_GAP = 4f;
    private static final float SEARCH_BOX_TEXT_SIZE = 7.2f;
    private static final float SEARCH_BOX_TEXT_INSET = 7f;
    private static final float POPUP_WIDTH = 125f;
    private static final float POPUP_HEIGHT = 190f;
    private static final float POPUP_CLOSE_BUTTON_SIZE = 16f;
    private static final float POPUP_CLOSE_RIGHT_INSET = 3f;
    private static final float POPUP_CLOSE_TOP_INSET = 2f;
    private static final float POPUP_ANIM_SPEED = 12f;
    private static final float POPUP_TITLE_LEFT_INSET = 6f;
    private static final float POPUP_TITLE_TOP_INSET = 6f;
    private static final float POPUP_TITLE_TEXT_SIZE = 6.8f;
    private static final float POPUP_CONTENT_INSET = 7f;
    private static final float POPUP_SETTING_ROW_HEIGHT = 18f;
    private static final float POPUP_SETTING_TOP_OFFSET = 18f;
    // --- module cards with inline (always expanded) settings ---
    private static final float MODULE_CARD_ROUNDING = 5f;
    private static final float MODULE_CARD_PAD_X = 5f;
    private static final float MODULE_CARD_PAD_Y = 3f;
    private static final float MODULE_HEADER_HEIGHT = 15f;
    private static final float MODULE_NAME_SIZE = 7.2f;
    private static final float MODULE_BIND_SIZE = 5.8f;
    private static final float MODULE_SETTINGS_INSET_X = 3f;
    private static final float MODULE_COLUMN_GAP = 5f;
    private static final float MODULE_ROW_GAP = 4f;
    /** Ниже этой ширины контента колонок становится меньше, чем {@link #GRID_COLUMNS}. */
    private static final float MODULE_MIN_COLUMN_WIDTH = 128f;
    /**
     * Компоненты настроек нарисованы в своих пиксельных размерах (шрифт 13.5, строка 28)
     * под панель шириной 1140. Компактная панель рисует их с этим коэффициентом от
     * scaleFactor, поэтому пропорции совпадают с образцом на любом масштабе интерфейса.
     */
    private static final float MODULE_SETTINGS_SCALE = 0.45f;
    /** Отступы между настройками — в собственных пикселях компонентов. */
    private static final float MODULE_SETTINGS_ROW_GAP = 4f;
    private static final float MODULE_SETTINGS_BOTTOM_PAD = 2f;
    private static final int LEFT_MENU_ICON_COLOR = ColorUtils.rgba(210, 226, 255, 230);
    private static final int LEFT_MENU_SECTION_ALPHA = 64;
    private static final int LEFT_MENU_FIRST_SECTION_END = 4;
    private static final int LEFT_MENU_SECOND_SECTION_START = 5;
    private static final int LEFT_MENU_AUTOBUY_INDEX = 5;
    private static final MenuEntry[] LEFT_MENU_ITEMS = new MenuEntry[]{
            new MenuEntry("combat", "Combat", "O", Category.Combat),
            new MenuEntry("movement", "Movement", "Q", Category.Movement),
            new MenuEntry("render", "Render", "B", Category.Render),
            new MenuEntry("player", "Misc", "C", Category.Player),
            new MenuEntry("utilities", "Utilities", "G", Category.Utilities),
            new MenuEntry("autobuy", "AutoBuy", "H", null),
            new MenuEntry("configs", "Configs", "M", null),
            new MenuEntry("scripts", "Scripts", "T", null)
    };
    private static Identifier activeClickGuiGif = CLICK_GUI_GIF;

    private float panelX;
    private float panelY;
    private float panelWidth;
    private float panelHeight;
    private float screenWidth;
    private float screenHeight;
    private float leftSectionWidth;
    private float rightSectionX;
    private float rightSectionWidth;
    private float leftMenuX;
    private float leftMenuY;
    private float leftMenuWidth;
    private float leftMenuRowHeight;
    private float leftMenuRowGap;
    private final AtomicBoolean gifPickerOpen = new AtomicBoolean();
    private volatile Path pendingCustomGif;
    private float footerIconHoverAnimation;
    private float footerIconBaseX;
    private float footerIconBaseY;
    private float footerIconBaseWidth;
    private float footerIconBaseSize;
    private float searchBoxX;
    private float searchBoxY;
    private float searchBoxWidth;
    private float searchBoxHeight;
    private float settingsPopupAnimation;
    private float settingsPopupAnchorX;
    private float settingsPopupAnchorY;
    private float settingsPopupX;
    private float settingsPopupY;
    private float settingsPopupWidth;
    private float settingsPopupHeight;
    private float settingsPopupCloseX;
    private float settingsPopupCloseY;
    private float settingsPopupCloseWidth;
    private float settingsPopupCloseHeight;
    private float settingsPopupScaleRowX;
    private float settingsPopupScaleRowY;
    private float settingsPopupScaleRowWidth;
    private float settingsPopupScaleRowHeight;
    private float settingsPopupColorRowX;
    private float settingsPopupColorRowY;
    private float settingsPopupColorRowWidth;
    private float settingsPopupColorRowHeight;
    private float settingsPopupBindRowX;
    private float settingsPopupBindRowY;
    private float settingsPopupBindRowWidth;
    private float settingsPopupBindRowHeight;
    private float settingsPopupDarkModeRowX;
    private float settingsPopupDarkModeRowY;
    private float settingsPopupDarkModeRowWidth;
    private float settingsPopupDarkModeRowHeight;
    private float settingsPopupDragOffsetX;
    private float settingsPopupDragOffsetY;
    private CsSettingComponent<?> settingsPopupScaleComponent;
    private CsSettingComponent<?> settingsPopupColorComponent;
    private CsSettingComponent<?> settingsPopupBindComponent;
    private CsSettingComponent<?> settingsPopupDarkModeComponent;
    private final List<FunctionCardBounds> functionCardBounds = new ArrayList<>();
    private final List<FunctionBindBounds> functionBindBounds = new ArrayList<>();
    /** Настройки, отрисованные внутри карточек модулей в текущем кадре (в своих координатах). */
    private final List<InlineSettingBounds> functionSettingBounds = new ArrayList<>();
    /** Масштаб, с которым компоненты настроек рисуются внутри карточек. */
    private float settingsScale = 1f;
    private final List<TabBounds> tabBounds = new ArrayList<>();
    private final List<TabBounds> bottomTabBounds = new ArrayList<>();
    private final tech.lightclient.ui.gui.events.EventsPanel eventsPanel = new tech.lightclient.ui.gui.events.EventsPanel();
    private float eventsScroll;
    private float eventsTargetScroll;
    private float eventsMaxScroll;
    private float cosmeticsScroll;
    private float cosmeticsTargetScroll;
    private float cosmeticsMaxScroll;
    private final List<CosmeticBounds> cosmeticBounds = new ArrayList<>();
    /** Поэтапное появление/исчезновение карточек косметики. */
    private final ListAppearAnimator<tech.lightclient.ui.gui.cosmetics.CosmeticsManager.CosmeticItem> cosmeticsAnimator =
            new ListAppearAnimator<>(tech.lightclient.ui.gui.cosmetics.CosmeticsManager.CosmeticItem::name);
    private float avatarX;
    private float avatarY;
    private float avatarSize;
    private final Map<Function, Float> moduleToggleAnim = new HashMap<>();
    private final Map<String, Float> menuHoverAnimations = new HashMap<>();
    private final Map<String, Float> menuSelectionAnimations = new HashMap<>();
    private final Map<Setting<?>, CsSettingComponent<?>> moduleSettingsComponents = new IdentityHashMap<>();
    /** The large legacy pages are kept as widgets and rendered inside the compact right pane. */
    private final CsAutoBuyWidget autoBuyWidget = new CsAutoBuyWidget();
    private final CsConfigWidget configWidget = new CsConfigWidget();
    private final CsScriptsWidget scriptsWidget = new CsScriptsWidget();
    private final TextInputField searchInput = new TextInputField()
            .setPlaceholder("Search")
            .setMaxLength(32)
            .setOnChange(() -> {
                resetFunctionListState();
                resetFunctionListSlideIn();
            });
    private long lastUiAnimTime = System.currentTimeMillis();
    private float functionListViewportX;
    private float functionListViewportY;
    private float functionListViewportWidth;
    private float functionListViewportHeight;
    private float functionListScroll;
    private float functionListTargetScroll;
    private float functionListMaxScroll;
    private final SimpleLinearAnimation functionListSlideIn = new SimpleLinearAnimation(
            FUNCTION_LIST_SLIDE_IN_DURATION_MS, Easings.EASE_IN_OUT_CUBIC);
    private boolean functionListSlideInStarted;
    private final SimpleLinearAnimation bottomTabsSlideIn = new SimpleLinearAnimation(
            BOTTOM_TAB_SLIDE_IN_DURATION_MS, Easings.EASE_IN_OUT_CUBIC);
    private boolean bottomTabsSlideInStarted;
    private final SimpleLinearAnimation searchSlideIn = new SimpleLinearAnimation(
            SEARCH_SLIDE_IN_DURATION_MS, Easings.EASE_IN_OUT_CUBIC);
    private boolean searchSlideInStarted;
    private final SimpleLinearAnimation specialWidgetSlideIn = new SimpleLinearAnimation(
            SPECIAL_WIDGET_SLIDE_IN_DURATION_MS, Easings.EASE_IN_OUT_CUBIC);
    private boolean specialWidgetSlideInStarted;
    private final SimpleLinearAnimation categoryIconsFadeIn = new SimpleLinearAnimation(
            CATEGORY_ICON_FADE_IN_DURATION_MS, Easings.EASE_IN_OUT_CUBIC);
    private boolean categoryIconsFadeInStarted;
    private final SimpleLinearAnimation gearIntroAnimation = new SimpleLinearAnimation(
            GEAR_INTRO_DURATION_MS, Easings.EASE_IN_OUT_CUBIC);
    private boolean gearIntroAnimationStarted;
    private float uiAnimationDeltaSeconds;
    private Category selectedCategory = Category.Combat;
    private String selectedMenuId = "combat";
    private Function bindingFunction;
    private boolean settingsPopupOpen = false;
    private boolean settingsPopupDragging = false;
    private float specialWidgetX;
    private float specialWidgetY;
    private float specialWidgetWidth;
    private float specialWidgetHeight;
    private float specialWidgetScaleX;
    private float specialWidgetScaleY;

    public static void preloadAssets() {
        GifTexture.ensureLoaded(CLICK_GUI_GIF);
    }

    @Override
    protected void resetAnimations() {
        super.resetAnimations();
        resetFunctionListSlideIn();
        bottomTabsSlideIn.setImmediate(false);
        bottomTabsSlideInStarted = false;
        searchSlideIn.setImmediate(false);
        searchSlideInStarted = false;
        specialWidgetSlideIn.setImmediate(false);
        specialWidgetSlideInStarted = false;
        categoryIconsFadeIn.setImmediate(false);
        categoryIconsFadeInStarted = false;
        gearIntroAnimation.setImmediate(false);
        gearIntroAnimationStarted = false;
        cosmeticsAnimator.restart();
        eventsPanel.restartAppearAnimations();
        functionSettingBounds.clear();
        resetInlineSettingsInputState();
        // При повторном открытии меню настройки уже раскрыты — без анимации появления.
        CsSettingComponent.requestSuppressVisibility(3);
    }

    @Override
    protected void renderGui(Renderer2D r, int vw, int vh) {
        CursorHelper.setArrow();

        float progress = guiOpenAnimation.getProgress();
        if (progress <= 0.001f || mc == null || mc.getWindow() == null) {
            return;
        }

        float scaleFactor = (float) mc.getWindow().getScaleFactor();
        CsGui.currentRenderScale = scaleFactor;
        screenWidth = vw * scaleFactor;
        screenHeight = vh * scaleFactor;
        updatePopupDragPosition();

        panelWidth = PANEL_WIDTH * scaleFactor;
        panelHeight = PANEL_HEIGHT * scaleFactor;

        float centerX = screenWidth * 0.5f;
        float centerY = screenHeight * 0.5f;
        panelX = centerX - panelWidth * 0.5f;
        panelY = centerY - panelHeight * 0.5f;

        float rounding = PANEL_ROUNDING * scaleFactor;
        float openScale = 0.92f + 0.08f * progress;
        boolean darkMode = tech.lightclient.modules.impl.render.ClickGui.isDarkModeEnabled();
        int panelColor = darkMode
                ? ColorUtils.rgba(0, 0, 0, Math.round(255f * 0.97f))
                : ColorUtils.rgba(
                        28,
                        28,
                        31,
                        Math.round(255f * tech.lightclient.modules.impl.render.ClickGui.getSimpleBackgroundOpacity() * 0.78f)
                );
        int accentColor = getThemeAccentColor();
        uiAnimationDeltaSeconds = getUiAnimationDeltaSeconds();
        float hoverDt = uiAnimationDeltaSeconds;

        // This is the common post-open timeline for all ClickGUI tabs.  Starting
        // it here (rather than inside the module grid) also lets AutoBuy, Configs
        // and Scripts trigger the Search and bottom-tab entrance animations.
        if (!functionListSlideInStarted && !closing && guiOpenAnimation.isFinished()) {
            functionListSlideIn.show();
            functionListSlideInStarted = true;
        }

        r.pushScale(openScale, openScale, centerX, centerY);
        r.pushAlpha(progress);
        try {
            if (tech.lightclient.modules.impl.render.ClickGui.isSimpleBlurEnabled()) {
                r.prepareBlur(tech.lightclient.modules.impl.render.ClickGui.getSimpleBlurStrength());
                r.blur(panelX, panelY, panelWidth, panelHeight, rounding, 0.95f);
            }
            r.rect(panelX, panelY, panelWidth, panelHeight, rounding, panelColor);

            renderTopBar(r, scaleFactor, rounding, accentColor, hoverDt);

            float inset = CONTENT_INSET * scaleFactor;
            float topBarBottom = panelY + TOP_BAR_HEIGHT * scaleFactor;
            r.rect(panelX + inset, topBarBottom, panelWidth - inset * 2f, Math.max(1f, scaleFactor), 0f,
                    ColorUtils.rgba(255, 255, 255, 16));

            float bottomBarH = BOTTOM_BAR_HEIGHT * scaleFactor;
            float gridX = panelX + inset;
            float gridY = topBarBottom + CONTENT_TOP_GAP * scaleFactor;
            float gridW = Math.max(0f, panelWidth - inset * 2f);
            float gridH = Math.max(0f, panelY + panelHeight - inset - bottomBarH - 4f * scaleFactor - gridY);
            if (isBottomTabSelected()) {
                renderBottomPanel(r, scaleFactor, gridX, gridY, gridW, gridH, accentColor);
            } else {
                renderModuleGrid(r, scaleFactor, rounding, gridX, gridY, gridW, gridH, accentColor);
            }
            renderBottomBar(r, scaleFactor, rounding, accentColor, hoverDt,
                    panelY + panelHeight - inset - bottomBarH, bottomBarH);

            settingsPopupAnimation = animateValue(settingsPopupAnimation, settingsPopupOpen ? 1f : 0f, POPUP_ANIM_SPEED, hoverDt);
            if (settingsPopupAnimation > 0.001f) {
                renderSettingsPopup(r, scaleFactor, rounding, panelColor);
            }
            CsColorPicker colorPicker = CsColorPicker.get();
            if (colorPicker != null) {
                // Пикер живёт в координатах того элемента, из которого открыт:
                // настройки карточек масштабируются, флайаут ClickGui — нет.
                float space = colorPickerSpaceScale();
                r.pushScale(space, space, 0f, 0f);
                try {
                    colorPicker.draw(r, Math.round(scaledMouseX() / space), Math.round(scaledMouseY() / space), 255);
                } finally {
                    r.popScale();
                }
                syncActiveColorPickerPreview();
            }
        } finally {
            r.popAlpha();
            r.popTransform();
        }
        CsSettingComponent.tickSuppressVisibility();
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        if (isSpecialTabSelected() && specialWidgetKeyPressed(keyInput.key())) {
            return true;
        }
        if (bindingFunction != null) {
            int keyCode = keyInput.key();
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE) {
                bindingFunction.setBind(-1);
            } else if (!isModifierKey(keyCode)) {
                bindingFunction.setBind(keyCode);
            }
            bindingFunction = null;
            return true;
        }
        boolean bindListening = settingsPopupBindComponent != null
                && CsSettingComponent.listeningBindComp == settingsPopupBindComponent;
        if (bindListening) {
            settingsPopupBindComponent.keyPressed(keyInput.key());
            return true;
        }
        if (CsSettingComponent.listeningBindComp != null) {
            CsSettingComponent.listeningBindComp.keyPressed(keyInput.key());
            return true;
        }
        if (CsSettingComponent.listeningBooleanComp != null) {
            CsSettingComponent.listeningBooleanComp.keyPressed(keyInput.key());
            return true;
        }
        if (CsStringComponent.focusedStringComp != null) {
            CsStringComponent.focusedStringComp.keyPressed(keyInput.key());
            return true;
        }
        CsColorPicker colorPicker = CsColorPicker.get();
        if (colorPicker != null && !colorPicker.isClosing()
                && !tech.lightclient.modules.impl.render.ClickGui.isOpenBindKeyboardKey(keyInput.key())) {
            colorPicker.keyPressed(keyInput.key());
            syncActiveColorPickerPreview();
            return true;
        }
        if (searchInput.isFocused() && handleSearchKeyPressed(keyInput.key(), keyInput.modifiers())) {
            return true;
        }
        if (settingsPopupOpen && keyInput.key() == GLFW.GLFW_KEY_ESCAPE) {
            closeSettingsPopup();
            return true;
        }
        return super.keyPressed(keyInput);
    }

    @Override
    public boolean charTyped(CharInput charInput) {
        if (isSpecialTabSelected() && specialWidgetCharTyped((char) charInput.codepoint())) {
            return true;
        }
        CsColorPicker colorPicker = CsColorPicker.get();
        if (colorPicker != null && !colorPicker.isClosing()) {
            colorPicker.charTyped((char) charInput.codepoint());
            syncActiveColorPickerPreview();
            return true;
        }
        if (CsStringComponent.focusedStringComp != null) {
            CsStringComponent.focusedStringComp.charTyped((char) charInput.codepoint());
            return true;
        }
        if (searchInput.isFocused() && handleSearchCharTyped((char) charInput.codepoint())) {
            return true;
        }
        return super.charTyped(charInput);
    }

    @Override
    protected boolean handleMouseClicked(double mouseX, double mouseY, int button) {
        if (bindingFunction != null) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                // ЛКМ - отменить привязку
                bindingFunction = null;
                return true;
            }
            if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                // ПКМ - сбросить бинд
                bindingFunction.setBind(-1);
                bindingFunction = null;
                return true;
            }
            // Любая другая кнопка мыши, включая колёсико - назначить бинд
            bindingFunction.setBind(1000 + button);
            bindingFunction = null;
            return true;
        }

        CsColorPicker colorPicker = CsColorPicker.get();
        if (colorPicker != null && !colorPicker.isClosing()) {
            // Пока пикер открыт, он забирает клик: внутри панели — работает,
            // мимо — закрывается, не переключая при этом модуль под курсором.
            float space = colorPickerSpaceScale();
            colorPicker.mouseClicked(mouseX / space, mouseY / space, button);
            syncActiveColorPickerPreview();
            return true;
        }

        // Раскрытый список режимов / активное текстовое поле закрываются при клике мимо них.
        InlineSettingBounds clickedSetting = isInsideFunctionList(mouseX, mouseY)
                ? findFunctionSettingBounds(mouseX, mouseY)
                : null;
        blurInlineSettingsExcept(clickedSetting != null ? clickedSetting.component() : null);

        if (settingsPopupOpen) {
            if (button == 0 && isInsidePopupClose(mouseX, mouseY)) {
                closeSettingsPopup();
                return true;
            }
            if (button == 0 && isInsideScaleModeRow(mouseX, mouseY) && settingsPopupScaleComponent != null) {
                settingsPopupScaleComponent.mouseClicked(mouseX, mouseY, button, settingsPopupScaleRowX, settingsPopupScaleRowY);
                return true;
            }
            if (button == 0 && isInsideColorModeRow(mouseX, mouseY) && settingsPopupColorComponent != null) {
                settingsPopupColorComponent.mouseClicked(mouseX, mouseY, button, settingsPopupColorRowX, settingsPopupColorRowY);
                syncActiveColorPickerPreview();
                return true;
            }
            if (isInsideBindModeRow(mouseX, mouseY) && settingsPopupBindComponent != null) {
                settingsPopupBindComponent.mouseClicked(mouseX, mouseY, button, settingsPopupBindRowX, settingsPopupBindRowY);
                return true;
            }
            if (button == 0 && isInsideDarkModeRow(mouseX, mouseY) && settingsPopupDarkModeComponent != null) {
                settingsPopupDarkModeComponent.mouseClicked(mouseX, mouseY, button, settingsPopupDarkModeRowX, settingsPopupDarkModeRowY);
                return true;
            }
            if (isInsideSettingsPopup(mouseX, mouseY)) {
                if (button == 0) {
                    settingsPopupDragging = true;
                    settingsPopupDragOffsetX = (float) mouseX - settingsPopupX;
                    settingsPopupDragOffsetY = (float) mouseY - settingsPopupY;
                }
                return true;
            }
            if (button == 0 && isInsideAvatar(mouseX, mouseY)) {
                closeSettingsPopup();
                return true;
            }
            // click outside closes the settings flyout
            closeSettingsPopup();
        }

        if (button == 0 && isInsideAvatar(mouseX, mouseY)) {
            openSettingsPopup((float) mouseX, (float) mouseY);
            return true;
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT
                && searchInput.mouseClicked(mouseX, mouseY, button, searchBoxX, searchBoxY, searchBoxWidth, searchBoxHeight)) {
            return true;
        }

        int menuIndex = findMenuIndex(mouseX, mouseY);
        if (button == 0 && menuIndex >= 0) {
            MenuEntry item = LEFT_MENU_ITEMS[menuIndex];
            selectTab(item.id(), item.category());
            return true;
        }

        int bottomIndex = findBottomMenuIndex(mouseX, mouseY);
        if (button == 0 && bottomIndex >= 0) {
            selectTab(BOTTOM_MENU_ITEMS[bottomIndex].id(), null);
            return true;
        }

        if ("events".equals(selectedMenuId) && isInsideFunctionList(mouseX, mouseY)
                && eventsPanel.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        if ("cosmetics".equals(selectedMenuId) && button == GLFW.GLFW_MOUSE_BUTTON_LEFT
                && isInsideFunctionList(mouseX, mouseY)) {
            for (CosmeticBounds b : cosmeticBounds) {
                if (mouseX >= b.x() && mouseX <= b.x() + b.w() && mouseY >= b.y() && mouseY <= b.y() + b.h()) {
                    tech.lightclient.ui.gui.cosmetics.CosmeticsManager.get().toggle(b.item());
                    return true;
                }
            }
            return true;
        }

        if (isSpecialTabSelected() && handleSpecialWidgetClick(mouseX, mouseY, button)) {
            return true;
        }

        if (handleFunctionCardClick(mouseX, mouseY, button)) {
            return true;
        }

        return isInsideMainPanels(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        double scale = mc != null && mc.getWindow() != null ? mc.getWindow().getScaleFactor() : 1.0;
        double scaledX = mouseX * scale;
        double scaledY = mouseY * scale;
        if (isCursorOverColorPicker(scaledX, scaledY)) {
            return true;
        }
        if (settingsPopupOpen && isInsideSettingsPopup(scaledX, scaledY)) {
            return true;
        }
        if (isSpecialTabSelected() && handleSpecialWidgetScroll(scaledX, scaledY, verticalAmount)) {
            return true;
        }
        if ("events".equals(selectedMenuId) && isInsideFunctionList(scaledX, scaledY)) {
            if (eventsMaxScroll > 0f) {
                eventsTargetScroll = clamp(eventsTargetScroll + (float) verticalAmount * 40f, -eventsMaxScroll, 0f);
            }
            return true;
        }
        if ("cosmetics".equals(selectedMenuId) && isInsideFunctionList(scaledX, scaledY)) {
            if (cosmeticsMaxScroll > 0f) {
                cosmeticsTargetScroll = clamp(cosmeticsTargetScroll - (float) verticalAmount * 40f, 0f, cosmeticsMaxScroll);
            }
            return true;
        }
        if (isInsideFunctionList(scaledX, scaledY)) {
            InlineSettingBounds hovered = findFunctionSettingBounds(scaledX, scaledY);
            if (hovered != null && hovered.component().mouseScrolled(scaledX / settingsScale,
                    scaledY / settingsScale, hovered.x(), hovered.y())) {
                return true;
            }
            if (functionListMaxScroll > 0f) {
                functionListTargetScroll = clamp(functionListTargetScroll - (float) verticalAmount * 40f, 0f, functionListMaxScroll);
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseReleased(Click click) {
        searchInput.mouseReleased(click.button());
        if (isSpecialTabSelected()) {
            specialWidgetMouseReleased(click.button());
        }
        TextInputField focused = TextInputField.focusedField;
        if (focused != null && focused != searchInput) {
            focused.mouseReleased(click.button());
        }
        if (settingsPopupDragging && click.button() == 0) {
            settingsPopupDragging = false;
            return true;
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        if (searchInput.isDragging() && click.button() == 0) {
            double scale = mc.getWindow().getScaleFactor();
            searchInput.tickDrag(click.x() * scale, click.y() * scale);
            return true;
        }
        if (isSpecialTabSelected()) {
            double scale = mc.getWindow().getScaleFactor();
            if (specialWidgetTickDrag(click.x() * scale, click.y() * scale)) {
                return true;
            }
        }
        // Выделение текста в строковой настройке карточки: курсор нужно перевести
        // в координаты компонентов.
        TextInputField focused = TextInputField.focusedField;
        if (focused != null && focused != searchInput && focused.isDragging() && click.button() == 0) {
            double scale = mc.getWindow().getScaleFactor();
            focused.tickDrag(click.x() * scale / settingsScale, click.y() * scale / settingsScale);
            return true;
        }
        if (settingsPopupOpen && settingsPopupDragging && click.button() == 0) {
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    private boolean isInsideMainPanels(double mouseX, double mouseY) {
        return mouseX >= panelX
                && mouseX <= panelX + panelWidth
                && mouseY >= panelY
                && mouseY <= panelY + panelHeight;
    }

    private boolean handleFunctionCardClick(double mouseX, double mouseY, int button) {
        if (!isInsideFunctionList(mouseX, mouseY)) {
            return false;
        }
        // Настройки раскрыты внутри карточек, поэтому они обрабатывают клик первыми.
        InlineSettingBounds settingBounds = findFunctionSettingBounds(mouseX, mouseY);
        if (settingBounds != null) {
            settingBounds.component().mouseClicked(mouseX / settingsScale, mouseY / settingsScale,
                    button, settingBounds.x(), settingBounds.y());
            syncActiveColorPickerPreview();
            return true;
        }
        FunctionBindBounds bindBounds = findFunctionBindBounds(mouseX, mouseY);
        if (bindBounds != null && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            bindingFunction = bindBounds.function();
            return true;
        }
        FunctionCardBounds card = findFunctionCard(mouseX, mouseY);
        if (card == null) {
            return false;
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            card.function().toggle();
            return true;
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
            bindingFunction = card.function();
            return true;
        }
        return true;
    }

    /** Клик мимо раскрытого списка/поля ввода закрывает его. */
    private void blurInlineSettingsExcept(CsSettingComponent<?> clicked) {
        if (CsModeListComponent.openDropdown != null && CsModeListComponent.openDropdown != clicked
                && moduleSettingsComponents.containsValue(CsModeListComponent.openDropdown)) {
            CsModeListComponent.openDropdown.setExpanded(false);
        }
        if (CsStringComponent.focusedStringComp != null && CsStringComponent.focusedStringComp != clicked
                && moduleSettingsComponents.containsValue(CsStringComponent.focusedStringComp)) {
            CsStringComponent.focusedStringComp.keyPressed(GLFW.GLFW_KEY_ESCAPE);
        }
    }

    private InlineSettingBounds findFunctionSettingBounds(double mouseX, double mouseY) {
        // Границы хранятся в координатах компонентов, курсор — в пикселях экрана.
        double localX = mouseX / settingsScale;
        double localY = mouseY / settingsScale;
        for (int i = functionSettingBounds.size() - 1; i >= 0; i--) {
            InlineSettingBounds bounds = functionSettingBounds.get(i);
            if (localX >= bounds.x() && localX <= bounds.x() + bounds.w()
                    && localY >= bounds.y() && localY <= bounds.y() + bounds.h()) {
                return bounds;
            }
        }
        return null;
    }

    private FunctionBindBounds findFunctionBindBounds(double mouseX, double mouseY) {
        for (FunctionBindBounds bindBounds : functionBindBounds) {
            if (mouseX >= bindBounds.x() && mouseX <= bindBounds.x() + bindBounds.w()
                    && mouseY >= bindBounds.y() && mouseY <= bindBounds.y() + bindBounds.h()) {
                return bindBounds;
            }
        }
        return null;
    }

    private FunctionCardBounds findFunctionCard(double mouseX, double mouseY) {
        for (FunctionCardBounds card : functionCardBounds) {
            if (mouseX >= card.x() && mouseX <= card.x() + card.w()
                    && mouseY >= card.y() && mouseY <= card.y() + card.h()) {
                return card;
            }
        }
        return null;
    }

    private boolean isInsideFunctionList(double mouseX, double mouseY) {
        return mouseX >= functionListViewportX
                && mouseX <= functionListViewportX + functionListViewportWidth
                && mouseY >= functionListViewportY
                && mouseY <= functionListViewportY + functionListViewportHeight;
    }

    private void resetFunctionListState() {
        functionListScroll = 0f;
        functionListTargetScroll = 0f;
        functionListMaxScroll = 0f;
        bindingFunction = null;
    }

    private void resetFunctionListSlideIn() {
        functionListSlideIn.setImmediate(false);
        functionListSlideInStarted = false;
    }

    private boolean isInsideTab(int index, double mouseX, double mouseY) {
        for (TabBounds tab : tabBounds) {
            if (tab.index() == index) {
                return mouseX >= tab.x() && mouseX <= tab.x() + tab.w()
                        && mouseY >= tab.y() && mouseY <= tab.y() + tab.h();
            }
        }
        return false;
    }

    private int findMenuIndex(double mouseX, double mouseY) {
        for (TabBounds tab : tabBounds) {
            if (mouseX >= tab.x() && mouseX <= tab.x() + tab.w()
                    && mouseY >= tab.y() && mouseY <= tab.y() + tab.h()) {
                return tab.index();
            }
        }
        return -1;
    }

    private int findBottomMenuIndex(double mouseX, double mouseY) {
        for (TabBounds tab : bottomTabBounds) {
            if (mouseX >= tab.x() && mouseX <= tab.x() + tab.w()
                    && mouseY >= tab.y() && mouseY <= tab.y() + tab.h()) {
                return tab.index();
            }
        }
        return -1;
    }

    private void selectTab(String id, Category category) {
        boolean selectionChanged = !id.equals(selectedMenuId);
        selectedMenuId = id;
        if (category != null) {
            selectedCategory = category;
        }
        if (selectionChanged) {
            searchInput.setText("");
            searchInput.blur();
            resetFunctionListSlideIn();
            specialWidgetSlideIn.setImmediate(false);
            specialWidgetSlideInStarted = false;
            cosmeticsAnimator.restart();
            eventsPanel.restartAppearAnimations();
            functionSettingBounds.clear();
            resetInlineSettingsInputState();
            // Настройки нового таба должны появиться сразу, без анимации раскрытия.
            CsSettingComponent.requestSuppressVisibility(3);
        }
        resetFunctionListState();
    }

    private boolean isInsideAvatar(double mouseX, double mouseY) {
        return avatarSize > 0f
                && mouseX >= avatarX && mouseX <= avatarX + avatarSize
                && mouseY >= avatarY && mouseY <= avatarY + avatarSize;
    }

    private void renderTopBar(Renderer2D r, float scaleFactor, float rounding, int accentColor, float hoverDt) {
        float barH = TOP_BAR_HEIGHT * scaleFactor;
        float barCenterY = panelY + barH * 0.5f;
        float side = TOP_BAR_SIDE_INSET * scaleFactor;

        // ---- player avatar (right) ---- placeholder = same gif art
        float avSize = TOP_BAR_AVATAR_SIZE * scaleFactor;
        avatarSize = avSize;
        avatarX = panelX + panelWidth - side - avSize;
        avatarY = barCenterY - avSize * 0.5f;
        if (!gearIntroAnimationStarted && !closing && guiOpenAnimation.isFinished()) {
            gearIntroAnimation.show();
            gearIntroAnimationStarted = true;
        }
        float gearProgress = gearIntroAnimationStarted ? gearIntroAnimation.getProgress() : 0f;
        float gearEase = Easings.EASE_IN_OUT_CUBIC.ease(gearProgress);
        boolean avatarHovered = !isCursorBlockedBySettingsPopup(scaledMouseX(), scaledMouseY())
                && isInsideAvatar(scaledMouseX(), scaledMouseY());
        footerIconHoverAnimation = animateValue(footerIconHoverAnimation, avatarHovered ? 1f : 0f, LEFT_MENU_HOVER_ANIM_SPEED, hoverDt);
        // settings gear ("E" glyph of CLICKGUI_TTF)
        float gearSize = avSize * 0.82f + 0.6f * scaleFactor * footerIconHoverAnimation;
        float gearW = FontRegistry.CLICKGUI_TTF.getWidth("E", gearSize);
        float gearCenterY = avatarY + avSize * 0.5f;
        float gearTargetX = avatarX + (avSize - gearW) * 0.5f;
        // Start a short distance beyond the right edge, then clip the glyph to the
        // ClickGUI panel so it does not appear outside while rolling in.
        float gearStartX = panelX + panelWidth + 5f * scaleFactor;
        float gearX = gearStartX + (gearTargetX - gearStartX) * gearEase;
        // Rotate in place around the current glyph centre, so it turns 360° while
        // moving left instead of tracing a circular path around its final position.
        float gearCenterX = gearX + gearW * 0.5f;
        r.pushClipRect((int) Math.floor(panelX), (int) Math.floor(panelY),
                (int) Math.ceil(panelWidth), (int) Math.ceil(panelHeight));
        r.pushTranslation(gearCenterX, gearCenterY);
        r.pushRotation(360f * gearEase);
        try {
            // Draw relative to the moving centre: rotation cannot introduce a
            // vertical translation, so the gear travels strictly right-to-left.
            r.text(FontRegistry.CLICKGUI_TTF, -gearW * 0.5f,
                    FontRegistry.centeredBaselineOffset(FontRegistry.CLICKGUI_TTF, 'E', gearSize)
                            + gearSize * ICON_BASELINE_FIX,
                    gearSize, "E",
                    avatarHovered ? ColorUtils.injectAlpha(accentColor, 240) : ColorUtils.rgba(196, 198, 206, 225));
        } finally {
            r.popRotation();
            r.popTransform();
            r.popClipRect();
        }

        // ---- search box (left of avatar) ----
        float searchW = 92f * scaleFactor;
        searchBoxHeight = 18f * scaleFactor;
        searchBoxWidth = searchW;
        searchBoxX = avatarX - 7f * scaleFactor - searchW;
        searchBoxY = barCenterY - searchBoxHeight * 0.5f;
        float moduleSlideProgress = functionListSlideInStarted ? functionListSlideIn.getProgress() : 0f;
        if (!searchSlideInStarted && !closing && moduleSlideProgress >= SEARCH_SLIDE_START_PROGRESS) {
            searchSlideIn.show();
            searchSlideInStarted = true;
        }
        float searchSlideProgress = searchSlideInStarted ? searchSlideIn.getProgress() : 0f;
        float searchTargetY = searchBoxY;
        searchBoxY = panelY - searchBoxHeight - (1f - searchSlideProgress) * SEARCH_SLIDE_IN_DISTANCE * scaleFactor;
        searchBoxY = searchBoxY + (searchTargetY - searchBoxY) * searchSlideProgress;
        boolean searchHovered = !isCursorBlockedBySettingsPopup(scaledMouseX(), scaledMouseY())
                && isInsideSearchBox(scaledMouseX(), scaledMouseY());
        if (searchHovered) {
            CursorHelper.setIBeam();
        }
        boolean searchFocused = searchInput.isFocused();
        int searchFill = searchFocused ? ColorUtils.rgba(255, 255, 255, 26) : ColorUtils.rgba(255, 255, 255, 12);
        int searchOutline = searchFocused
                ? ColorUtils.injectAlpha(accentColor, 150)
                : (searchHovered ? ColorUtils.rgba(255, 255, 255, 66) : ColorUtils.rgba(255, 255, 255, 30));
        r.pushClipRect((int) Math.floor(panelX), (int) Math.floor(panelY),
                (int) Math.ceil(panelWidth), (int) Math.ceil(panelHeight));
        r.rect(searchBoxX, searchBoxY, searchBoxWidth, searchBoxHeight, rounding * 0.7f, searchFill);
        r.rectOutline(searchBoxX, searchBoxY, searchBoxWidth, searchBoxHeight, rounding * 0.7f, searchOutline, 1f);
        float searchTextSize = 7.2f * scaleFactor;
        searchInput
                .setFont(FontRegistry.DELTA_CLICKGUI)
                .setFontSize(searchTextSize)
                .setPadding(6.5f * scaleFactor, 0f)
                .setColors(
                        ColorUtils.rgba(232, 232, 236, 228),
                        ColorUtils.rgba(146, 146, 154, 214),
                        ColorUtils.rgba(230, 236, 246, 210),
                        ColorUtils.injectAlpha(accentColor, 86)
                );
        searchInput.render(r, searchBoxX, searchBoxY, searchBoxWidth, searchBoxHeight, 1f);
        r.popClipRect();

        // ---- centered category tab strip ----
        tabBounds.clear();
        FontObject iconFont = FontRegistry.CLICKGUI_TTF;
        int tabCount = LEFT_MENU_ITEMS.length;
        float slot = TAB_ICON_SLOT * scaleFactor;
        float padX = TAB_PILL_PADDING_X * scaleFactor;
        float padY = TAB_PILL_PADDING_Y * scaleFactor;
        float stripW = slot * tabCount;
        float pillW = stripW + padX * 2f;
        float pillH = barH - padY * 2f;
        float pillX = panelX + (panelWidth - pillW) * 0.5f;
        float pillY = barCenterY - pillH * 0.5f;
        r.rect(pillX, pillY, pillW, pillH, pillH * 0.5f, ColorUtils.rgba(0, 0, 0, 70));
        if (tech.lightclient.modules.impl.render.ClickGui.isDarkModeEnabled()) {
            r.rectOutline(pillX, pillY, pillW, pillH, pillH * 0.5f,
                    ColorUtils.rgba(255, 255, 255, 210), Math.max(0.25f, scaleFactor * 0.25f));
        }

        if (!categoryIconsFadeInStarted && !closing && guiOpenAnimation.isFinished()) {
            categoryIconsFadeIn.show();
            categoryIconsFadeInStarted = true;
        }
        float categoryFadeProgress = categoryIconsFadeInStarted ? categoryIconsFadeIn.getProgress() : 0f;

        float baseIconSize = TAB_ICON_SIZE * scaleFactor;
        float stripX = pillX + padX;
        for (int i = 0; i < tabCount; i++) {
            MenuEntry item = LEFT_MENU_ITEMS[i];
            float slotX = stripX + i * slot;
            float slotCenterX = slotX + slot * 0.5f;
            tabBounds.add(new TabBounds(i, slotX, pillY, slot, pillH));

            boolean hovered = isInsideTab(i, scaledMouseX(), scaledMouseY());
            boolean selected = item.id().equals(selectedMenuId);
            float hoverAnim = updateMenuHoverAnimation(item.id(), hovered, hoverDt);
            float selAnim = updateMenuSelectionAnimation(item.id(), selected, hoverDt);

            float iconSize = baseIconSize;
            // Scale the finished glyph uniformly around its centre instead of
            // changing the font size, so hover growth preserves its proportions.
            float iconScale = 1f + (0.5f * scaleFactor / baseIconSize) * hoverAnim;
            // selected: only recolor the glyph to accent (no highlight box)
            int baseGray = ColorUtils.rgba(178, 178, 184, Math.round(195 + 45 * hoverAnim));
            int iconColor = selAnim > 0.001f
                    ? ColorUtils.interpolate(baseGray, ColorUtils.injectAlpha(accentColor, 245), selAnim)
                    : baseGray;
            float iconW = iconFont.getWidth(item.icon(), iconSize);
            float iconX = slotCenterX - iconW * 0.5f;
            float iconY = barCenterY + FontRegistry.centeredBaselineOffset(iconFont, item.icon().charAt(0), iconSize)
                    + iconSize * ICON_BASELINE_FIX;
            float iconFade = clamp((categoryFadeProgress - i * CATEGORY_ICON_FADE_IN_STAGGER)
                    / (1f - i * CATEGORY_ICON_FADE_IN_STAGGER), 0f, 1f);
            r.pushAlpha(iconFade);
            r.pushScale(iconScale, iconScale, slotCenterX, barCenterY);
            try {
                r.text(iconFont, iconX, iconY, iconSize, item.icon(), iconColor);
            } finally {
                r.popScale();
                r.popAlpha();
            }
        }
    }

    private boolean isBottomTabSelected() {
        return "events".equals(selectedMenuId) || "cosmetics".equals(selectedMenuId);
    }

    private void renderBottomBar(Renderer2D r, float scaleFactor, float rounding, int accentColor, float hoverDt,
                                float barY, float barH) {
        bottomTabBounds.clear();
        float centerY = barY + barH * 0.5f;
        float pillGap = 6f * scaleFactor;
        float labelSize = 7.4f * scaleFactor;
        float iconGap = 4f * scaleFactor;
        float pillPadX = 8f * scaleFactor;
        float iconSize = 8f * scaleFactor;

        // measure total width
        float totalW = 0f;
        float[] widths = new float[BOTTOM_MENU_ITEMS.length];
        for (int i = 0; i < BOTTOM_MENU_ITEMS.length; i++) {
            float lw = FontRegistry.DELTA_CLICKGUI.getWidth(BOTTOM_MENU_ITEMS[i].label(), labelSize);
            widths[i] = iconSize + iconGap + lw + pillPadX * 2f;
            totalW += widths[i];
        }
        totalW += pillGap * (BOTTOM_MENU_ITEMS.length - 1);
        float x = panelX + (panelWidth - totalW) * 0.5f;

        // Start only after the module cards have completed their entrance.
        if (!bottomTabsSlideInStarted && !closing && functionListSlideInStarted && functionListSlideIn.isFinished()) {
            bottomTabsSlideIn.show();
            bottomTabsSlideInStarted = true;
        }
        float bottomTabsProgress = bottomTabsSlideInStarted ? bottomTabsSlideIn.getProgress() : 0f;

        r.pushClipRect((int) Math.floor(panelX), (int) Math.floor(panelY),
                (int) Math.ceil(panelWidth), (int) Math.ceil(panelHeight));
        try {
            for (int i = 0; i < BOTTOM_MENU_ITEMS.length; i++) {
                MenuEntry item = BOTTOM_MENU_ITEMS[i];
                float pillW = widths[i];
                float pillH = barH;
                float tabDelay = i * BOTTOM_TAB_SLIDE_IN_STAGGER;
                float tabProgress = clamp((bottomTabsProgress - tabDelay) / (1f - tabDelay), 0f, 1f);
                float pillY = barY + (1f - tabProgress) * BOTTOM_TAB_SLIDE_IN_DISTANCE * scaleFactor;
                boolean selected = item.id().equals(selectedMenuId);
                boolean hovered = scaledMouseX() >= x && scaledMouseX() <= x + pillW
                        && scaledMouseY() >= pillY && scaledMouseY() <= pillY + pillH
                        && !isCursorBlockedBySettingsPopup(scaledMouseX(), scaledMouseY());
                bottomTabBounds.add(new TabBounds(i, x, pillY, pillW, pillH));

                int pillCol = selected ? ColorUtils.injectAlpha(accentColor, 40)
                        : ColorUtils.rgba(255, 255, 255, hovered ? 14 : 8);
                if (tabProgress > 0.001f) {
                    r.rect(x, pillY, pillW, pillH, pillH * 0.5f, pillCol);
                    if (selected) {
                        r.rectOutline(x, pillY, pillW, pillH, pillH * 0.5f, ColorUtils.injectAlpha(accentColor, 150), 1f);
                    }

                    int col = selected ? ColorUtils.injectAlpha(accentColor, 240)
                            : ColorUtils.rgba(190, 192, 200, hovered ? 235 : 205);
                    float iconX = x + pillPadX;
                    float iconCy = pillY + pillH * 0.5f;
                    if ("events".equals(item.id())) {
                        drawCalendarIcon(r, iconX, iconCy - iconSize * 0.5f, iconSize, col);
                    } else {
                        drawCosmeticIcon(r, iconX, iconCy - iconSize * 0.5f, iconSize, col);
                    }
                    float textX = iconX + iconSize + iconGap;
                    r.text(FontRegistry.DELTA_CLICKGUI, textX,
                            iconCy + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_CLICKGUI, 'H', labelSize),
                            labelSize, item.label(), col);
                }
                x += pillW + pillGap;
            }
        } finally {
            r.popClipRect();
        }
    }

    /** Simple vector calendar glyph. */
    private void drawCalendarIcon(Renderer2D r, float x, float y, float s, int color) {
        float ring = Math.max(1f, s * 0.12f);
        r.rectOutline(x, y + s * 0.14f, s, s * 0.86f, s * 0.16f, color, ring);
        r.rect(x, y + s * 0.14f, s, s * 0.26f, s * 0.16f, color); // header
        r.rect(x + s * 0.24f, y, ring * 1.4f, s * 0.26f, 0f, color); // hangers
        r.rect(x + s * 0.68f, y, ring * 1.4f, s * 0.26f, 0f, color);
    }

    /** Simple sparkle/diamond cosmetic glyph. */
    private void drawCosmeticIcon(Renderer2D r, float x, float y, float s, int color) {
        float cx = x + s * 0.5f;
        float cy = y + s * 0.5f;
        float t = Math.max(1f, s * 0.14f);
        // 4-point sparkle
        r.rect(cx - t * 0.5f, y + s * 0.08f, t, s * 0.84f, t * 0.5f, color);
        r.rect(x + s * 0.08f, cy - t * 0.5f, s * 0.84f, t, t * 0.5f, color);
    }

    private void renderBottomPanel(Renderer2D r, float scaleFactor, float x, float y, float w, float h, int accentColor) {
        functionListViewportX = x;
        functionListViewportY = y;
        functionListViewportWidth = w;
        functionListViewportHeight = h;
        if ("events".equals(selectedMenuId)) {
            eventsPanel.tick();
            float alpha = guiOpenAnimation.getProgress();
            eventsTargetScroll = clamp(eventsTargetScroll, -eventsMaxScroll, 0f);
            eventsScroll += (eventsTargetScroll - eventsScroll) * 0.18f;
            r.pushClipRect((int) Math.floor(x), (int) Math.floor(y), (int) Math.ceil(w), (int) Math.ceil(h));
            float contentH = eventsPanel.draw(r, x, y, w, h, eventsScroll, alpha, scaledMouseX(), scaledMouseY());
            r.popClipRect();
            eventsMaxScroll = Math.max(0f, contentH - h);
            return;
        }
        // cosmetics: Figura models grid
        tech.lightclient.ui.gui.cosmetics.CosmeticsManager cm = tech.lightclient.ui.gui.cosmetics.CosmeticsManager.get();
        List<tech.lightclient.ui.gui.cosmetics.CosmeticsManager.CosmeticItem> items =
                cm.visible(tech.lightclient.ui.gui.cosmetics.CosmeticsManager.Section.MODELS);
        cosmeticBounds.clear();
        float a = guiOpenAnimation.getProgress();
        cosmeticsAnimator.sync(items);
        cosmeticsAnimator.update(uiAnimationDeltaSeconds);
        List<ListAppearAnimator.Entry<tech.lightclient.ui.gui.cosmetics.CosmeticsManager.CosmeticItem>> entries =
                cosmeticsAnimator.entries();
        if (entries.isEmpty()) {
            String msg = "Нет моделей косметики";
            float size = 8f * scaleFactor;
            float tw = FontRegistry.DELTA_CLICKGUI.getWidth(msg, size);
            r.text(FontRegistry.DELTA_CLICKGUI, x + (w - tw) * 0.5f,
                    y + h * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_CLICKGUI, 'H', size),
                    size, msg, ColorUtils.rgba(160, 162, 170, 210));
            return;
        }

        float gap = 7f * scaleFactor;
        int cols = Math.max(2, Math.round(w / (72f * scaleFactor)));
        float cardW = (w - gap * (cols - 1)) / cols;
        float previewH = cardW * 0.9f;
        float footerH = 15f * scaleFactor;
        float cardH = previewH + footerH;
        int rows = (int) Math.ceil(Math.max(1, items.size()) / (float) cols);
        float totalH = rows * (cardH + gap) - gap;
        cosmeticsMaxScroll = Math.max(0f, totalH - h);
        cosmeticsTargetScroll = clamp(cosmeticsTargetScroll, 0f, cosmeticsMaxScroll);
        cosmeticsScroll += (cosmeticsTargetScroll - cosmeticsScroll) * 0.18f;
        if (Math.abs(cosmeticsTargetScroll - cosmeticsScroll) < 0.1f) cosmeticsScroll = cosmeticsTargetScroll;

        r.pushClipRect((int) Math.floor(x), (int) Math.floor(y), (int) Math.ceil(w), (int) Math.ceil(h));
        int live = 0;
        for (ListAppearAnimator.Entry<tech.lightclient.ui.gui.cosmetics.CosmeticsManager.CosmeticItem> entry : entries) {
            tech.lightclient.ui.gui.cosmetics.CosmeticsManager.CosmeticItem item = entry.value();
            // Живые карточки едут в свои слоты, тающие остаются на месте до конца затухания.
            if (!entry.leaving()) {
                int col = live % cols, row = live / cols;
                live++;
                cosmeticsAnimator.place(entry, x + col * (cardW + gap), y + row * (cardH + gap), uiAnimationDeltaSeconds);
            }
            float progress = entry.progress();
            if (progress <= 0.001f) continue;
            float cxx = entry.x();
            float cyy = entry.y() - cosmeticsScroll + (1f - progress) * LIST_APPEAR_SLIDE_DISTANCE * scaleFactor;
            if (cyy + cardH < y || cyy > y + h) continue;
            boolean sel = cm.isSelected(item);
            boolean hover = !entry.leaving() && scaledMouseX() >= cxx && scaledMouseX() <= cxx + cardW
                    && scaledMouseY() >= cyy && scaledMouseY() <= cyy + cardH;
            if (hover) CursorHelper.setHand();
            if (!entry.leaving()) {
                cosmeticBounds.add(new CosmeticBounds(item, cxx, cyy, cardW, cardH));
            }

            r.pushAlpha(progress);
            try {
                if (tech.lightclient.modules.impl.render.ClickGui.isSimpleBlurEnabled()) {
                    r.prepareBlur(tech.lightclient.modules.impl.render.ClickGui.getSimpleBlurStrength());
                    r.blur(cxx, cyy, cardW, cardH, 8f, a);
                }
                r.rect(cxx, cyy, cardW, cardH, 8f, ColorUtils.rgba(255, 255, 255, hover ? 16 : 9));
                if (sel) {
                    r.rect(cxx, cyy, cardW, cardH, 8f, ColorUtils.injectAlpha(accentColor, 30));
                    r.rectOutline(cxx, cyy, cardW, cardH, 8f, ColorUtils.injectAlpha(accentColor, 235), 1.4f);
                }
                // preview
                Identifier tex = cm.previewTexture(item);
                if (tex != null) {
                    float aspect = cm.previewAspect(item);
                    if (aspect <= 0f) aspect = 1f;
                    float pad = 5f * scaleFactor;
                    float availW = cardW - pad * 2f, availH = previewH - pad * 2f;
                    float iw = availW, ih = availW / aspect;
                    if (ih > availH) { ih = availH; iw = availH * aspect; }
                    float ixx = cxx + (cardW - iw) * 0.5f;
                    float iyy = cyy + pad + (availH - ih) * 0.5f;
                    r.setTextureLinearFilter(tex);
                    r.drawTextureRounded(tex, ixx, iyy, iw, ih, 6f);
                }
                // name
                String nm = fitText(item.name(), FontRegistry.DELTA_CLICKGUI, 6.6f * scaleFactor, cardW - 6f * scaleFactor);
                float tw = FontRegistry.DELTA_CLICKGUI.getWidth(nm, 6.6f * scaleFactor);
                r.text(FontRegistry.DELTA_CLICKGUI, cxx + (cardW - tw) * 0.5f,
                        cyy + previewH + footerH * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_CLICKGUI, 'H', 6.6f * scaleFactor),
                        6.6f * scaleFactor, nm,
                        sel ? ColorUtils.injectAlpha(accentColor, 240) : ColorUtils.rgba(206, 208, 214, 228));
            } finally {
                r.popAlpha();
            }
        }
        r.popClipRect();
    }

    private record CosmeticBounds(tech.lightclient.ui.gui.cosmetics.CosmeticsManager.CosmeticItem item,
                                  float x, float y, float w, float h) {
    }

    private void renderModuleGrid(Renderer2D r, float scaleFactor, float rounding,
                                  float contentX, float contentY, float contentW, float contentH, int gridAccent) {
        functionListViewportX = contentX;
        functionListViewportY = contentY;
        functionListViewportWidth = contentW;
        functionListViewportHeight = contentH;
        functionCardBounds.clear();
        functionBindBounds.clear();
        functionSettingBounds.clear();
        if (contentW <= 0f || contentH <= 0f) {
            functionListMaxScroll = 0f;
            functionListScroll = 0f;
            functionListTargetScroll = 0f;
            return;
        }

        String searchText = searchInput.getText();
        boolean hasSearchQuery = searchText != null && !searchText.isBlank();

        // Search is global: it must not depend on the currently selected tab/category.
        // Special pages are shown only when there is no active module search.
        if (isSpecialTabSelected() && !hasSearchQuery) {
            renderSpecialWidget(r, contentX, contentY, contentW, contentH);
            functionListMaxScroll = 0f;
            functionListScroll = 0f;
            functionListTargetScroll = 0f;
            return;
        }

        MenuEntry selectedEntry = getSelectedMenuEntry();
        if (LightClient.getFunctionManager() == null
                || (!hasSearchQuery && (selectedEntry == null || selectedEntry.category() == null))) {
            functionListMaxScroll = 0f;
            functionListScroll = 0f;
            functionListTargetScroll = 0f;
            return;
        }

        List<Function> functions = hasSearchQuery
                ? LightClient.getFunctionManager().getFunctions()
                : LightClient.getFunctionManager().getFunctionsByCategory(selectedEntry.category());
        if (hasSearchQuery) {
            String normalizedQuery = searchText.toLowerCase(Locale.ROOT);
            functions = functions.stream()
                    .filter(function -> function.isVisible() && matchesSearch(function, normalizedQuery))
                    .toList();
        }
        if (functions.isEmpty()) {
            functionListMaxScroll = 0f;
            functionListScroll = 0f;
            functionListTargetScroll = 0f;
            return;
        }

        float listSlideProgress = functionListSlideInStarted ? functionListSlideIn.getProgress() : 0f;

        float colGap = MODULE_COLUMN_GAP * scaleFactor;
        float rowGap = MODULE_ROW_GAP * scaleFactor;
        // Колонок столько, сколько влезает по минимальной ширине карточки; на штатной
        // панели 452px это ровно GRID_COLUMNS.
        int columns = Math.max(1, Math.min(GRID_COLUMNS,
                (int) ((contentW + colGap) / ((MODULE_MIN_COLUMN_WIDTH + MODULE_COLUMN_GAP) * scaleFactor))));
        float cardW = Math.max(0f, (contentW - colGap * (columns - 1)) / columns);
        settingsScale = Math.max(0.05f, scaleFactor * MODULE_SETTINGS_SCALE);
        float settingsAreaW = Math.max(0f, cardW - (MODULE_CARD_PAD_X + MODULE_SETTINGS_INSET_X) * 2f * scaleFactor);
        float componentWidth = settingsAreaW / settingsScale;

        // Масонри-раскладка: каждая следующая карточка уходит в более короткую колонку.
        float[] measureColumns = new float[columns];
        float[] cardHeights = new float[functions.size()];
        int[] cardColumns = new int[functions.size()];
        float[] cardTops = new float[functions.size()];
        for (int i = 0; i < functions.size(); i++) {
            Function function = functions.get(i);
            int column = shortestColumn(measureColumns);
            float cardH = getModuleCardHeight(function, componentWidth, scaleFactor);
            cardHeights[i] = cardH;
            cardColumns[i] = column;
            cardTops[i] = measureColumns[column];
            measureColumns[column] += cardH + rowGap;
        }
        float totalH = 0f;
        for (float columnHeight : measureColumns) {
            totalH = Math.max(totalH, Math.max(0f, columnHeight - rowGap));
        }

        functionListMaxScroll = Math.max(0f, totalH - contentH);
        functionListTargetScroll = clamp(functionListTargetScroll, 0f, functionListMaxScroll);
        functionListScroll += (functionListTargetScroll - functionListScroll) * 0.18f;
        if (Math.abs(functionListTargetScroll - functionListScroll) < 0.1f) {
            functionListScroll = functionListTargetScroll;
        }
        functionListScroll = clamp(functionListScroll, 0f, functionListMaxScroll);

        Function tooltipFn = null;
        float tooltipX = 0f;
        float tooltipY = 0f;
        r.pushClipRect((int) Math.floor(contentX), (int) Math.floor(contentY), (int) Math.ceil(contentW), (int) Math.ceil(contentH));
        try {
            for (int i = 0; i < functions.size(); i++) {
                Function function = functions.get(i);
                float cardH = cardHeights[i];
                float cardX = contentX + cardColumns[i] * (cardW + colGap);
                float cardDelay = Math.min(FUNCTION_LIST_SLIDE_IN_MAX_DELAY, i * FUNCTION_LIST_SLIDE_IN_STAGGER);
                float cardProgress = clamp((listSlideProgress - cardDelay) / (1f - cardDelay), 0f, 1f);
                if (cardProgress <= 0.001f) {
                    continue;
                }
                float targetCardY = contentY + cardTops[i] - functionListScroll;
                float cardY = targetCardY + (1f - cardProgress) * FUNCTION_LIST_SLIDE_IN_DISTANCE * scaleFactor;
                if (cardY + cardH < contentY || cardY > contentY + contentH) {
                    continue;
                }

                if (renderModuleCard(r, function, cardX, cardY, cardW, cardH, componentWidth,
                        scaleFactor, cardProgress)) {
                    String description = function.getDescription();
                    if (description != null && !description.isBlank()) {
                        tooltipFn = function;
                        tooltipX = scaledMouseX() + 12f * scaleFactor;
                        tooltipY = scaledMouseY() + 12f * scaleFactor;
                    }
                }
            }
        } finally {
            r.popClipRect();
        }

        if (tooltipFn != null) {
            renderModuleTooltip(r, scaleFactor, tooltipFn.getDescription(), tooltipX, tooltipY);
        }
    }

    private static int shortestColumn(float[] columnHeights) {
        int best = 0;
        for (int i = 1; i < columnHeights.length; i++) {
            if (columnHeights[i] < columnHeights[best]) {
                best = i;
            }
        }
        return best;
    }

    /** Высота карточки: шапка + раскрытые настройки (в пикселях экрана). */
    private float getModuleCardHeight(Function function, float componentWidth, float scaleFactor) {
        float headerBlock = (MODULE_CARD_PAD_Y * 2f + MODULE_HEADER_HEIGHT) * scaleFactor;
        return headerBlock + getInlineSettingsHeight(function, componentWidth) * settingsScale;
    }

    /** Суммарная высота раскрытых настроек модуля в координатах компонентов. */
    private float getInlineSettingsHeight(Function function, float componentWidth) {
        float total = 0f;
        boolean first = true;
        for (Setting<?> setting : function.getSettings()) {
            CsSettingComponent<?> component = getModuleSettingsComponent(setting, componentWidth);
            if (!component.shouldRender()) {
                continue;
            }
            float height = component.getAnimatedHeight();
            if (height <= 0.01f) {
                continue;
            }
            if (!first) {
                total += MODULE_SETTINGS_ROW_GAP * component.getVisibilityProgress();
            }
            first = false;
            total += height;
        }
        return total > 0f ? total + MODULE_SETTINGS_BOTTOM_PAD : 0f;
    }

    /** @return true, если курсор находится на шапке карточки (для тултипа описания). */
    private boolean renderModuleCard(Renderer2D r, Function function, float cardX, float cardY,
                                     float cardW, float cardH, float componentWidth,
                                     float scaleFactor, float cardProgress) {
        int accentColor = getThemeAccentColor();
        float headerTop = cardY + MODULE_CARD_PAD_Y * scaleFactor;
        float headerH = MODULE_HEADER_HEIGHT * scaleFactor;
        float headerCenterY = headerTop + headerH * 0.5f;
        float padX = MODULE_CARD_PAD_X * scaleFactor;
        float nameSize = MODULE_NAME_SIZE * scaleFactor;
        float bindSize = MODULE_BIND_SIZE * scaleFactor;
        boolean listening = function == bindingFunction;
        boolean enabled = function.isState();

        functionCardBounds.add(new FunctionCardBounds(function, cardX, headerTop, cardW, headerH));

        boolean cursorFree = !isCursorBlockedBySettingsPopup(scaledMouseX(), scaledMouseY());
        boolean headerHovered = cursorFree
                && scaledMouseX() >= cardX && scaledMouseX() <= cardX + cardW
                && scaledMouseY() >= headerTop && scaledMouseY() <= headerTop + headerH;

        float toggleProgress = updateModuleToggleAnim(function, enabled, uiAnimationDeltaSeconds);
        int baseBg = headerHovered ? ColorUtils.rgba(255, 255, 255, 16) : ColorUtils.rgba(255, 255, 255, 8);
        int cardFill = ColorUtils.interpolate(baseBg, ColorUtils.injectAlpha(accentColor, 26), toggleProgress);
        float cardRounding = MODULE_CARD_ROUNDING * scaleFactor;

        r.pushAlpha(cardProgress);
        try {
            r.rect(cardX, cardY, cardW, cardH, cardRounding, cardFill);
            r.rectOutline(cardX, cardY, cardW, cardH, cardRounding,
                    ColorUtils.interpolate(ColorUtils.rgba(255, 255, 255, 18),
                            ColorUtils.injectAlpha(accentColor, 96), toggleProgress),
                    Math.max(1f, scaleFactor * 0.5f));

            // бинд-пилюля справа в шапке
            String bindLabel = formatFunctionBind(function);
            float bindTextW = FontRegistry.DELTA_CLICKGUI.getWidth(bindLabel, bindSize);
            float bindPillH = Math.max(bindSize + 4f * scaleFactor, 11f * scaleFactor);
            float bindPillW = bindTextW + 8f * scaleFactor;
            float bindPillX = cardX + cardW - padX - bindPillW;
            float bindPillY = headerCenterY - bindPillH * 0.5f;
            int bindBg;
            int bindTextCol;
            if (listening) {
                bindBg = ColorUtils.injectAlpha(accentColor, 210);
                bindTextCol = ColorUtils.rgba(255, 255, 255, 245);
            } else if (function.getBind() > 0) {
                bindBg = ColorUtils.rgba(255, 255, 255, 22);
                bindTextCol = ColorUtils.rgba(196, 198, 206, 225);
            } else {
                bindBg = ColorUtils.rgba(255, 255, 255, 11);
                bindTextCol = ColorUtils.rgba(140, 142, 150, 210);
            }
            r.rect(bindPillX, bindPillY, bindPillW, bindPillH, bindPillH * 0.5f, bindBg);
            r.text(FontRegistry.DELTA_CLICKGUI, bindPillX + (bindPillW - bindTextW) * 0.5f,
                    bindPillY + bindPillH * 0.5f
                            + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_CLICKGUI, 'H', bindSize),
                    bindSize, bindLabel, bindTextCol);
            functionBindBounds.add(new FunctionBindBounds(function,
                    bindPillX - 2f * scaleFactor, bindPillY - 2f * scaleFactor,
                    bindPillW + 4f * scaleFactor, bindPillH + 4f * scaleFactor));

            // название модуля
            float nameAvail = Math.max(8f * scaleFactor, bindPillX - 5f * scaleFactor - (cardX + padX));
            String name = fitText(function.getName(), FontRegistry.DELTA_CLICKGUI, nameSize, nameAvail);
            int nameCol = ColorUtils.interpolate(ColorUtils.rgba(214, 216, 222, 232),
                    ColorUtils.injectAlpha(accentColor, 250), toggleProgress);
            r.text(FontRegistry.DELTA_CLICKGUI, cardX + padX,
                    headerCenterY + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_CLICKGUI, 'H', nameSize),
                    nameSize, name, nameCol);

            renderInlineSettings(r, function, cardX, headerTop + headerH, componentWidth, scaleFactor);
        } finally {
            r.popAlpha();
        }

        if (headerHovered) {
            CursorHelper.setHand();
            return true;
        }
        return false;
    }

    /** Настройки модуля, раскрытые прямо в карточке. */
    private void renderInlineSettings(Renderer2D r, Function function, float cardX, float settingsTop,
                                      float componentWidth, float scaleFactor) {
        if (componentWidth <= 0f) {
            return;
        }
        float areaX = cardX + (MODULE_CARD_PAD_X + MODULE_SETTINGS_INSET_X) * scaleFactor;
        // Компоненты рисуются в своих координатах: экранная точка = координата * settingsScale.
        float x = areaX / settingsScale;
        float y = settingsTop / settingsScale;
        // Под флайаутом ClickGui или пикером цвета настройки не подсвечиваются, но начатое
        // перетаскивание слайдера продолжается — иначе значение прыгнет.
        boolean cursorFree = isLeftMouseHeld()
                || !isCursorBlockedBySettingsPopup(scaledMouseX(), scaledMouseY());
        int mouseX = cursorFree ? Math.round(scaledMouseX() / settingsScale) : Integer.MIN_VALUE;
        int mouseY = cursorFree ? Math.round(scaledMouseY() / settingsScale) : Integer.MIN_VALUE;

        r.pushScale(settingsScale, settingsScale, 0f, 0f);
        try {
            boolean firstVisible = true;
            for (Setting<?> setting : function.getSettings()) {
                CsSettingComponent<?> component = getModuleSettingsComponent(setting, componentWidth);
                if (!component.shouldRender()) {
                    continue;
                }
                float progress = component.getVisibilityProgress();
                float height = component.getAnimatedHeight();
                if (height <= 0.01f) {
                    continue;
                }
                if (!firstVisible) {
                    y += MODULE_SETTINGS_ROW_GAP * progress;
                    // Разделитель не тоньше одного пикселя экрана.
                    r.rect(x, y - MODULE_SETTINGS_ROW_GAP * progress * 0.5f, componentWidth,
                            Math.max(1f, 1f / settingsScale), 0f, ColorUtils.rgba(255, 255, 255, 16));
                }
                firstVisible = false;

                // Появление настройки: масштаб вокруг центра строки, как в образце.
                float appearScale = 0.3f + 0.7f * progress;
                r.pushScale(appearScale, x + componentWidth * 0.5f, y + component.getHeight() * 0.5f);
                try {
                    component.draw(r, x, y, mouseX, mouseY, Math.round(255f * progress));
                } finally {
                    r.popScale();
                }
                if (progress >= 0.5f) {
                    functionSettingBounds.add(new InlineSettingBounds(component, x, y, componentWidth, height));
                }
                y += height;
            }
        } finally {
            r.popScale();
        }
    }

    private void renderModuleTooltip(Renderer2D r, float scaleFactor, String text, float x, float y) {
        float size = 6.8f * scaleFactor;
        float padH = 6f * scaleFactor;
        float padV = 4f * scaleFactor;
        List<String> lines = wrapTextLines(text, FontRegistry.DELTA_CLICKGUI, size, 150f * scaleFactor, 4);
        if (lines.isEmpty()) return;
        float lineH = FontRegistry.DELTA_CLICKGUI.getLineHeight(size);
        float w = 0f;
        for (String line : lines) {
            w = Math.max(w, FontRegistry.DELTA_CLICKGUI.getWidth(line, size));
        }
        float boxW = w + padH * 2f;
        float boxH = lines.size() * lineH + padV * 2f;
        if (x + boxW > screenWidth) x = screenWidth - boxW - 2f * scaleFactor;
        if (y + boxH > screenHeight) y = screenHeight - boxH - 2f * scaleFactor;
        r.rect(x, y, boxW, boxH, 4f * scaleFactor, ColorUtils.rgba(18, 18, 21, 240));
        for (int i = 0; i < lines.size(); i++) {
            r.text(FontRegistry.DELTA_CLICKGUI, x + padH,
                    lineBaseline(FontRegistry.DELTA_CLICKGUI, y + padV + i * lineH, size),
                    size, lines.get(i), ColorUtils.rgba(210, 212, 220, 230));
        }
    }

    private float updateModuleToggleAnim(Function function, boolean enabled, float dt) {
        float current = moduleToggleAnim.getOrDefault(function, enabled ? 1f : 0f);
        float next = animateValue(current, enabled ? 1f : 0f, 14f, dt);
        moduleToggleAnim.put(function, next);
        return next;
    }

    private MenuEntry getSelectedMenuEntry() {
        for (MenuEntry item : LEFT_MENU_ITEMS) {
            if (item.id().equals(selectedMenuId)) {
                return item;
            }
        }
        return null;
    }

    private boolean isSpecialTabSelected() {
        return "autobuy".equals(selectedMenuId)
                || "configs".equals(selectedMenuId)
                || "scripts".equals(selectedMenuId);
    }

    private Object selectedSpecialWidget() {
        return switch (selectedMenuId) {
            case "autobuy" -> autoBuyWidget;
            case "configs" -> configWidget;
            case "scripts" -> scriptsWidget;
            default -> null;
        };
    }

    private void renderSpecialWidget(Renderer2D r, float x, float y, float width, float height) {
        Object widget = selectedSpecialWidget();
        if (widget == null) return;

        float widgetWidth;
        float widgetHeight;
        if (widget instanceof CsAutoBuyWidget w) {
            widgetWidth = w.getWidth();
            widgetHeight = w.getHeight();
        } else if (widget instanceof CsConfigWidget w) {
            widgetWidth = w.getWidth();
            widgetHeight = w.getHeight();
        } else if (widget instanceof CsScriptsWidget w) {
            widgetWidth = w.getWidth();
            widgetHeight = w.getHeight();
        } else {
            return;
        }

        // These widgets are natively 720×600 (old large panel). Fill the new content
        // width and clip to the region; their internal lists handle vertical overflow.
        float widgetScale = width / widgetWidth;
        if (!Float.isFinite(widgetScale) || widgetScale <= 0f) return;
        float drawnHeight = widgetHeight * widgetScale;
        specialWidgetScaleX = widgetScale;
        specialWidgetScaleY = widgetScale;
        specialWidgetWidth = width;
        specialWidgetHeight = Math.min(drawnHeight, height);
        specialWidgetX = x;
        float targetWidgetY = drawnHeight <= height ? y + (height - drawnHeight) * 0.5f : y;
        // Each special page begins its own staged entrance immediately when the
        // user switches to it; it must not wait for the module-card timeline.
        if (!specialWidgetSlideInStarted && !closing) {
            specialWidgetSlideIn.show();
            specialWidgetSlideInStarted = true;
        }
        float widgetProgress = specialWidgetSlideInStarted ? specialWidgetSlideIn.getProgress() : 0f;
        // SimpleLinearAnimation already applies the same Ease In Out Cubic curve
        // used by the module cards; pass that eased progress to AutoBuy's stages.
        float widgetEase = widgetProgress;
        // Special pages follow the module cards: enter from above and settle
        // downward into their final position.
        specialWidgetY = targetWidgetY - SPECIAL_WIDGET_SLIDE_IN_DISTANCE
                * mc.getWindow().getScaleFactor() * (1f - widgetEase);

        if (widget instanceof CsAutoBuyWidget autoBuy) {
            // AutoBuy has its own staged child animations; keep the widget frame
            // stationary so cards, panel and title do not move as one block.
            specialWidgetY = targetWidgetY;
            autoBuy.setEntranceProgress(widgetEase);
        } else if (widget instanceof CsConfigWidget configs) {
            specialWidgetY = targetWidgetY;
            configs.setEntranceProgress(widgetEase);
        } else if (widget instanceof CsScriptsWidget scripts) {
            specialWidgetY = targetWidgetY;
            scripts.setEntranceProgress(widgetEase);
        }

        int mx = specialWidgetMouseX();
        int my = specialWidgetMouseY();
        r.pushClipRect((int) Math.floor(x), (int) Math.floor(y), (int) Math.ceil(width), (int) Math.ceil(height));
        r.pushScale(specialWidgetScaleX, specialWidgetScaleY, specialWidgetX, specialWidgetY);
        try {
            if (widget instanceof CsAutoBuyWidget w) {
                w.draw(r, specialWidgetX, specialWidgetY, Math.round(255f * guiOpenAnimation.getProgress()), mx, my, screenWidth, screenHeight);
            } else if (widget instanceof CsConfigWidget w) {
                w.draw(r, specialWidgetX, specialWidgetY, Math.round(255f * guiOpenAnimation.getProgress()), mx, my, screenWidth, screenHeight);
            } else if (widget instanceof CsScriptsWidget w) {
                w.draw(r, specialWidgetX, specialWidgetY, Math.round(255f * guiOpenAnimation.getProgress()), mx, my, screenWidth, screenHeight);
            }
        } finally {
            r.popScale();
            r.popClipRect();
        }
    }

    private int specialWidgetMouseX() {
        return Math.round(specialWidgetX + (scaledMouseX() - specialWidgetX) / Math.max(0.001f, specialWidgetScaleX));
    }

    private int specialWidgetMouseY() {
        return Math.round(specialWidgetY + (scaledMouseY() - specialWidgetY) / Math.max(0.001f, specialWidgetScaleY));
    }

    private boolean isInsideSpecialWidget(double x, double y) {
        return specialWidgetScaleX > 0f && specialWidgetScaleY > 0f
                && x >= specialWidgetX && x <= specialWidgetX + specialWidgetWidth
                && y >= specialWidgetY && y <= specialWidgetY + specialWidgetHeight;
    }

    private boolean handleSpecialWidgetClick(double mouseX, double mouseY, int button) {
        if (!isInsideSpecialWidget(mouseX, mouseY)) return false;
        double wx = specialWidgetX + (mouseX - specialWidgetX) / specialWidgetScaleX;
        double wy = specialWidgetY + (mouseY - specialWidgetY) / specialWidgetScaleY;
        if (autoBuyWidget == selectedSpecialWidget()) {
            return autoBuyWidget.mouseClicked(wx, wy, button, specialWidgetX, specialWidgetY, screenWidth, screenHeight);
        }
        if (configWidget == selectedSpecialWidget()) {
            return configWidget.mouseClicked(wx, wy, button, specialWidgetX, specialWidgetY, screenWidth, screenHeight);
        }
        return scriptsWidget.mouseClicked(wx, wy, button, specialWidgetX, specialWidgetY, screenWidth, screenHeight);
    }

    private boolean handleSpecialWidgetScroll(double mouseX, double mouseY, double amount) {
        if (!isInsideSpecialWidget(mouseX, mouseY)) return false;
        double wx = specialWidgetX + (mouseX - specialWidgetX) / specialWidgetScaleX;
        double wy = specialWidgetY + (mouseY - specialWidgetY) / specialWidgetScaleY;
        if (autoBuyWidget == selectedSpecialWidget()) {
            return autoBuyWidget.mouseScrolled(wx, wy, amount, specialWidgetX, specialWidgetY, screenWidth, screenHeight);
        }
        if (configWidget == selectedSpecialWidget()) {
            return configWidget.mouseScrolled(wx, wy, amount, specialWidgetX, specialWidgetY, screenWidth, screenHeight);
        }
        return scriptsWidget.mouseScrolled(wx, wy, amount, specialWidgetX, specialWidgetY, screenWidth, screenHeight);
    }

    private boolean specialWidgetKeyPressed(int keyCode) {
        if (autoBuyWidget == selectedSpecialWidget()) return autoBuyWidget.keyPressed(keyCode);
        if (configWidget == selectedSpecialWidget()) return configWidget.keyPressed(keyCode);
        if (scriptsWidget == selectedSpecialWidget()) return scriptsWidget.keyPressed(keyCode);
        return false;
    }

    private boolean specialWidgetCharTyped(char c) {
        if (autoBuyWidget == selectedSpecialWidget()) return autoBuyWidget.charTyped(c);
        if (configWidget == selectedSpecialWidget()) return configWidget.charTyped(c);
        if (scriptsWidget == selectedSpecialWidget()) return scriptsWidget.charTyped(c);
        return false;
    }

    private void specialWidgetMouseReleased(int button) {
        if (autoBuyWidget == selectedSpecialWidget()) autoBuyWidget.mouseReleased(button);
        else if (configWidget == selectedSpecialWidget()) configWidget.mouseReleased(button);
        else if (scriptsWidget == selectedSpecialWidget()) scriptsWidget.mouseReleased(button);
    }

    private boolean specialWidgetTickDrag(double mouseX, double mouseY) {
        if (!isInsideSpecialWidget(mouseX, mouseY)) return false;
        double wx = specialWidgetX + (mouseX - specialWidgetX) / specialWidgetScaleX;
        double wy = specialWidgetY + (mouseY - specialWidgetY) / specialWidgetScaleY;
        if (autoBuyWidget == selectedSpecialWidget()) autoBuyWidget.tickDrag(wx, wy);
        else if (configWidget == selectedSpecialWidget()) configWidget.tickDrag(wx, wy);
        else if (scriptsWidget == selectedSpecialWidget()) scriptsWidget.tickDrag(wx, wy);
        return true;
    }

    private String formatFunctionBind(Function function) {
        if (function == bindingFunction) {
            return "...";
        }
        int bind = function != null ? function.getBind() : 0;
        if (bind <= 0) {
            return "-";
        }
        String name = PlayerUtils.getBindName(bind);
        return name == null || name.isBlank() || name.equalsIgnoreCase("NONE") ? "-" : name;
    }

    private int getThemeAccentColor() {
        ColorSetting accentColorSetting = tech.lightclient.modules.impl.render.ClickGui.getColorSetting();
        return accentColorSetting != null && accentColorSetting.get() != null
                ? accentColorSetting.get()
                : 0xFF5690FF;
    }

    private String fitText(String text, FontObject font, float size, float maxWidth) {
        if (text == null || text.isEmpty() || font == null || maxWidth <= 0f) {
            return "";
        }
        if (font.getWidth(text, size) <= maxWidth) {
            return text;
        }
        String ellipsis = "...";
        float ellipsisW = font.getWidth(ellipsis, size);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String next = sb.toString() + text.charAt(i);
            if (font.getWidth(next, size) + ellipsisW > maxWidth) {
                break;
            }
            sb.append(text.charAt(i));
        }
        return sb.isEmpty() ? ellipsis : sb + ellipsis;
    }

    private List<String> wrapTextLines(String text, FontObject font, float size, float maxWidth, int maxLines) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isBlank() || font == null || maxLines <= 0) {
            return lines;
        }

        String normalized = text.replace('\n', ' ').replace('\r', ' ').trim();
        String[] words = normalized.split("\\s+");
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            String candidate = current.isEmpty() ? word : current + " " + word;
            if (font.getWidth(candidate, size) <= maxWidth) {
                current.setLength(0);
                current.append(candidate);
                continue;
            }

            if (current.isEmpty()) {
                lines.add(fitText(word, font, size, maxWidth));
            } else {
                lines.add(current.toString());
                current.setLength(0);
                current.append(word);
            }

            if (lines.size() == maxLines) {
                lines.set(maxLines - 1, fitText(lines.get(maxLines - 1), font, size, maxWidth));
                return lines;
            }
        }

        if (!current.isEmpty() && lines.size() < maxLines) {
            lines.add(current.toString());
        }

        if (lines.size() > maxLines) {
            return new ArrayList<>(lines.subList(0, maxLines));
        }

        if (words.length > 0 && lines.size() == maxLines) {
            String rebuilt = String.join(" ", lines);
            if (!rebuilt.equals(normalized)) {
                lines.set(maxLines - 1, fitText(lines.get(maxLines - 1), font, size, maxWidth));
            }
        }
        return lines;
    }

    private float lineBaseline(FontObject font, float lineTop, float size) {
        return lineTop + font.getLineHeight(size) * 0.5f + FontRegistry.centeredBaselineOffset(font, 'H', size);
    }

    private boolean isModifierKey(int keyCode) {
        return keyCode == GLFW.GLFW_KEY_LEFT_SHIFT
                || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT
                || keyCode == GLFW.GLFW_KEY_LEFT_CONTROL
                || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL
                || keyCode == GLFW.GLFW_KEY_LEFT_ALT
                || keyCode == GLFW.GLFW_KEY_RIGHT_ALT;
    }

    private boolean handleSearchKeyPressed(int keyCode, int modifiers) {
        return searchInput.keyPressed(keyCode, modifiers);
    }

    private boolean handleSearchCharTyped(char c) {
        return searchInput.charTyped(c);
    }

    private boolean matchesSearch(Function function, String normalizedQuery) {
        if (function == null || normalizedQuery == null || normalizedQuery.isBlank()) {
            return true;
        }
        String name = function.getName() != null ? function.getName().toLowerCase(Locale.ROOT) : "";
        String description = function.getDescription() != null ? function.getDescription().toLowerCase(Locale.ROOT) : "";
        return name.contains(normalizedQuery) || description.contains(normalizedQuery);
    }

    private void renderSettingsPopup(Renderer2D r, float scaleFactor, float rounding, int panelColor) {
        float popupInset = 8f * scaleFactor;
        float popupWidth = Math.min(POPUP_WIDTH * scaleFactor, panelWidth - 28f * scaleFactor);
        float popupInnerInset = TOP_LEFT_INNER_INSET * scaleFactor;
        float contentInset = POPUP_CONTENT_INSET * scaleFactor;
        float headerOffset = POPUP_SETTING_TOP_OFFSET * scaleFactor;
        settingsPopupWidth = Math.max(0f, popupWidth);
        float popupInnerWidth = Math.max(0f, settingsPopupWidth - popupInnerInset * 2f);

        ModeSetting scaleModeSetting = tech.lightclient.modules.impl.render.ClickGui.getScaleModeSetting();
        ColorSetting accentColorSetting = tech.lightclient.modules.impl.render.ClickGui.getColorSetting();
        BindSetting openBindSetting = tech.lightclient.modules.impl.render.ClickGui.getOpenBindSetting();
        BooleanSetting darkModeSetting = tech.lightclient.modules.impl.render.ClickGui.getDarkModeSetting();
        ensurePopupComponents(scaleModeSetting, accentColorSetting, openBindSetting, darkModeSetting, popupInnerWidth);

        float rowWidth = Math.max(0f, popupInnerWidth - contentInset * 2f);
        settingsPopupScaleRowHeight = settingsPopupScaleComponent != null ? settingsPopupScaleComponent.getHeight() : POPUP_SETTING_ROW_HEIGHT * scaleFactor;
        settingsPopupColorRowHeight = settingsPopupColorComponent != null ? settingsPopupColorComponent.getHeight() : POPUP_SETTING_ROW_HEIGHT * scaleFactor;
        settingsPopupBindRowHeight = settingsPopupBindComponent != null ? settingsPopupBindComponent.getHeight() : 24f;
        settingsPopupDarkModeRowHeight = settingsPopupDarkModeComponent != null ? settingsPopupDarkModeComponent.getHeight() : 24f;
        float rowGap = 5f * scaleFactor;
        float popupHeight = popupInnerInset * 2f
                + headerOffset
                + settingsPopupScaleRowHeight
                + rowGap
                + settingsPopupColorRowHeight
                + rowGap
                + settingsPopupBindRowHeight
                + rowGap
                + settingsPopupDarkModeRowHeight
                + contentInset;
        float maxHeight = panelHeight - 28f * scaleFactor;

        settingsPopupHeight = Math.max(0f, Math.min(popupHeight, maxHeight));
        settingsPopupX = clamp(settingsPopupAnchorX, popupInset, screenWidth - settingsPopupWidth - popupInset);
        settingsPopupY = clamp(settingsPopupAnchorY, popupInset, screenHeight - settingsPopupHeight - popupInset);
        settingsPopupCloseWidth = POPUP_CLOSE_BUTTON_SIZE * scaleFactor;
        settingsPopupCloseHeight = POPUP_CLOSE_BUTTON_SIZE * scaleFactor;

        float popupScale = 0.94f + 0.06f * settingsPopupAnimation;
        float popupCenterX = settingsPopupX + settingsPopupWidth * 0.5f;
        float popupCenterY = settingsPopupY + settingsPopupHeight * 0.5f;

        r.pushScale(popupScale, popupScale, popupCenterX, popupCenterY);
        r.pushAlpha(settingsPopupAnimation);
        try {
            if (tech.lightclient.modules.impl.render.ClickGui.isSimpleBlurEnabled()) {
                r.prepareBlur(tech.lightclient.modules.impl.render.ClickGui.getSimpleBlurStrength());
                r.blur(settingsPopupX, settingsPopupY, settingsPopupWidth, settingsPopupHeight, rounding, 0.95f);
            }

            r.rect(settingsPopupX, settingsPopupY, settingsPopupWidth, settingsPopupHeight, rounding, panelColor);
            float popupInnerX = settingsPopupX + popupInnerInset;
            float popupInnerY = settingsPopupY + popupInnerInset;
            float popupInnerHeight = Math.max(0f, settingsPopupHeight - popupInnerInset * 2f);
            float popupInnerRounding = Math.max(0f, rounding - scaleFactor);
            settingsPopupCloseX = popupInnerX + popupInnerWidth - POPUP_CLOSE_RIGHT_INSET * scaleFactor - settingsPopupCloseWidth;
            settingsPopupCloseY = popupInnerY + POPUP_CLOSE_TOP_INSET * scaleFactor;
            boolean closeHovered = isInsidePopupClose(scaledMouseX(), scaledMouseY());
            r.rect(popupInnerX, popupInnerY, popupInnerWidth, popupInnerHeight, popupInnerRounding, ColorUtils.rgba(0, 0, 0, TOP_LEFT_INNER_ALPHA));

            float popupTitleSize = POPUP_TITLE_TEXT_SIZE * scaleFactor;
            r.text(
                    FontRegistry.DELTA_CLICKGUI,
                    popupInnerX + POPUP_TITLE_LEFT_INSET * scaleFactor,
                    popupInnerY + POPUP_TITLE_TOP_INSET * scaleFactor
                            + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_CLICKGUI, 'H', popupTitleSize),
                    popupTitleSize,
                    "\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 ClickGui",
                    ColorUtils.rgba(232, 232, 236, 232)
            );

            float closeTextSize = 6.2f * scaleFactor + 0.2f * scaleFactor * (closeHovered ? 1f : 0f);
            r.text(
                    FontRegistry.DELTA_CLICKGUI,
                    settingsPopupCloseX + (settingsPopupCloseWidth - FontRegistry.DELTA_CLICKGUI.getWidth("X", closeTextSize)) * 0.5f,
                    settingsPopupCloseY + settingsPopupCloseHeight * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_CLICKGUI, 'H', closeTextSize),
                    closeTextSize,
                    "X",
                    closeHovered ? ColorUtils.rgba(255, 255, 255, 242) : ColorUtils.rgba(222, 222, 228, 228)
            );

            float rowX = popupInnerX + contentInset;
            settingsPopupScaleRowX = rowX;
            settingsPopupScaleRowY = popupInnerY + headerOffset;
            settingsPopupScaleRowWidth = rowWidth;
            if (settingsPopupScaleComponent != null) {
                settingsPopupScaleComponent.setWidth(rowWidth);
                settingsPopupScaleComponent.draw(r, settingsPopupScaleRowX, settingsPopupScaleRowY, scaledMouseX(), scaledMouseY(), Math.round(255f * settingsPopupAnimation));
            }

            settingsPopupColorRowX = rowX;
            settingsPopupColorRowY = settingsPopupScaleRowY + settingsPopupScaleRowHeight + rowGap;
            settingsPopupColorRowWidth = rowWidth;
            if (settingsPopupColorComponent != null) {
                settingsPopupColorComponent.setWidth(rowWidth);
                settingsPopupColorComponent.draw(r, settingsPopupColorRowX, settingsPopupColorRowY, scaledMouseX(), scaledMouseY(), Math.round(255f * settingsPopupAnimation));
            }

            settingsPopupBindRowX = rowX;
            settingsPopupBindRowY = settingsPopupColorRowY + settingsPopupColorRowHeight + rowGap;
            settingsPopupBindRowWidth = rowWidth;
            if (settingsPopupBindComponent != null) {
                settingsPopupBindComponent.setWidth(rowWidth);
                settingsPopupBindComponent.draw(r, settingsPopupBindRowX, settingsPopupBindRowY, scaledMouseX(), scaledMouseY(), Math.round(255f * settingsPopupAnimation));
            }

            settingsPopupDarkModeRowX = rowX;
            settingsPopupDarkModeRowY = settingsPopupBindRowY + settingsPopupBindRowHeight + rowGap;
            settingsPopupDarkModeRowWidth = rowWidth;
            if (settingsPopupDarkModeComponent != null) {
                settingsPopupDarkModeComponent.setWidth(rowWidth);
                settingsPopupDarkModeComponent.draw(r, settingsPopupDarkModeRowX, settingsPopupDarkModeRowY, scaledMouseX(), scaledMouseY(), Math.round(255f * settingsPopupAnimation));
            }
        } finally {
            r.popAlpha();
            r.popTransform();
        }
    }

    private boolean isInsideFooterIcon(double mouseX, double mouseY, float iconX, float iconBaselineY, float iconWidth, float iconSize) {
        float padding = FOOTER_ICON_HITBOX_PADDING * (mc != null && mc.getWindow() != null ? (float) mc.getWindow().getScaleFactor() : 1f);
        float top = iconBaselineY - iconSize * 0.9f - padding;
        float bottom = iconBaselineY + iconSize * 0.08f + padding;
        return mouseX >= iconX
                && mouseX <= iconX + iconWidth + padding
                && mouseY >= top
                && mouseY <= bottom;
    }

    private boolean isInsideSearchBox(double mouseX, double mouseY) {
        return searchBoxWidth > 0f
                && searchBoxHeight > 0f
                && mouseX >= searchBoxX
                && mouseX <= searchBoxX + searchBoxWidth
                && mouseY >= searchBoxY
                && mouseY <= searchBoxY + searchBoxHeight;
    }

    private boolean isInsideSettingsPopup(double mouseX, double mouseY) {
        return settingsPopupAnimation > 0.001f
                && mouseX >= settingsPopupX
                && mouseX <= settingsPopupX + settingsPopupWidth
                && mouseY >= settingsPopupY
                && mouseY <= settingsPopupY + settingsPopupHeight;
    }

    private boolean isInsidePopupClose(double mouseX, double mouseY) {
        return settingsPopupAnimation > 0.001f
                && mouseX >= settingsPopupCloseX
                && mouseX <= settingsPopupCloseX + settingsPopupCloseWidth
                && mouseY >= settingsPopupCloseY
                && mouseY <= settingsPopupCloseY + settingsPopupCloseHeight;
    }

    private boolean isInsideScaleModeRow(double mouseX, double mouseY) {
        return settingsPopupAnimation > 0.001f
                && mouseX >= settingsPopupScaleRowX
                && mouseX <= settingsPopupScaleRowX + settingsPopupScaleRowWidth
                && mouseY >= settingsPopupScaleRowY
                && mouseY <= settingsPopupScaleRowY + settingsPopupScaleRowHeight;
    }

    private boolean isInsideColorModeRow(double mouseX, double mouseY) {
        return settingsPopupAnimation > 0.001f
                && mouseX >= settingsPopupColorRowX
                && mouseX <= settingsPopupColorRowX + settingsPopupColorRowWidth
                && mouseY >= settingsPopupColorRowY
                && mouseY <= settingsPopupColorRowY + settingsPopupColorRowHeight;
    }

    private boolean isInsideBindModeRow(double mouseX, double mouseY) {
        return settingsPopupAnimation > 0.001f
                && mouseX >= settingsPopupBindRowX
                && mouseX <= settingsPopupBindRowX + settingsPopupBindRowWidth
                && mouseY >= settingsPopupBindRowY
                && mouseY <= settingsPopupBindRowY + settingsPopupBindRowHeight;
    }

    private boolean isInsideDarkModeRow(double mouseX, double mouseY) {
        return settingsPopupAnimation > 0.001f
                && mouseX >= settingsPopupDarkModeRowX
                && mouseX <= settingsPopupDarkModeRowX + settingsPopupDarkModeRowWidth
                && mouseY >= settingsPopupDarkModeRowY
                && mouseY <= settingsPopupDarkModeRowY + settingsPopupDarkModeRowHeight;
    }

    private boolean isCursorBlockedBySettingsPopup(double mouseX, double mouseY) {
        return isCursorOverColorPicker(mouseX, mouseY)
                || (settingsPopupOpen && isInsideSettingsPopup(mouseX, mouseY));
    }

    /**
     * Пикер открывается либо из настройки модуля (масштабированное пространство),
     * либо из флайаута ClickGui (пиксели экрана).
     */
    private float colorPickerSpaceScale() {
        CsColorPicker colorPicker = CsColorPicker.get();
        if (colorPicker == null) {
            return 1f;
        }
        boolean fromModuleSetting = moduleSettingsComponents.values().stream()
                .anyMatch(component -> component.setting == colorPicker.getSetting());
        return fromModuleSetting ? Math.max(0.05f, settingsScale) : 1f;
    }

    private boolean isCursorOverColorPicker(double mouseX, double mouseY) {
        CsColorPicker colorPicker = CsColorPicker.get();
        if (colorPicker == null || colorPicker.isClosing()) {
            return false;
        }
        float space = colorPickerSpaceScale();
        double localX = mouseX / space;
        double localY = mouseY / space;
        return localX >= colorPicker.getPanelX()
                && localX <= colorPicker.getPanelX() + colorPicker.getPanelW()
                && localY >= colorPicker.getPanelY()
                && localY <= colorPicker.getPanelY() + colorPicker.getPanelH();
    }

    private static boolean isLeftMouseHeld() {
        if (mc == null || mc.getWindow() == null) {
            return false;
        }
        return GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
    }

    private void ensurePopupComponents(ModeSetting scaleModeSetting, ColorSetting accentColorSetting,
                                       BindSetting openBindSetting, BooleanSetting darkModeSetting, float rowWidth) {
        if (settingsPopupScaleComponent == null && scaleModeSetting != null) {
            settingsPopupScaleComponent = CsSettingComponent.create(scaleModeSetting, rowWidth);
        }
        if (settingsPopupColorComponent == null && accentColorSetting != null) {
            settingsPopupColorComponent = CsSettingComponent.create(accentColorSetting, rowWidth);
        }
        if (settingsPopupBindComponent == null && openBindSetting != null) {
            settingsPopupBindComponent = CsSettingComponent.create(openBindSetting, rowWidth);
        }
        if (settingsPopupDarkModeComponent == null && darkModeSetting != null) {
            settingsPopupDarkModeComponent = CsSettingComponent.create(darkModeSetting, rowWidth);
        }
    }

    /** Moves the settings flyout from the frame cursor position, rather than waiting for the next drag callback. */
    private void updatePopupDragPosition() {
        if (settingsPopupOpen && settingsPopupDragging) {
            settingsPopupAnchorX = (float) rawMouseX - settingsPopupDragOffsetX;
            settingsPopupAnchorY = (float) rawMouseY - settingsPopupDragOffsetY;
        }
    }

    private void openSettingsPopup(float mouseX, float mouseY) {
        settingsPopupAnchorX = mouseX;
        settingsPopupAnchorY = mouseY;
        settingsPopupOpen = true;
        settingsPopupDragging = false;
        CsColorPicker.closeIfAny();
        resetInlineSettingsInputState();
    }

    private void closeSettingsPopup() {
        settingsPopupOpen = false;
        settingsPopupDragging = false;
        resetDetachedPopupInputState(settingsPopupScaleComponent, settingsPopupColorComponent,
                settingsPopupBindComponent, settingsPopupDarkModeComponent);
        CsColorPicker.closeIfAny();
    }

    private CsSettingComponent<?> getModuleSettingsComponent(Setting<?> setting, float width) {
        CsSettingComponent<?> component = moduleSettingsComponents.computeIfAbsent(setting, key -> CsSettingComponent.create(key, width));
        component.setWidth(width);
        return component;
    }

    /** Снимает фокус/ожидание бинда с компонентов настроек модулей. */
    private void resetInlineSettingsInputState() {
        if (CsSettingComponent.listeningBindComp != null
                && moduleSettingsComponents.containsValue(CsSettingComponent.listeningBindComp)) {
            CsSettingComponent.listeningBindComp = null;
        }
        if (CsSettingComponent.listeningBooleanComp != null
                && moduleSettingsComponents.containsValue(CsSettingComponent.listeningBooleanComp)) {
            CsSettingComponent.listeningBooleanComp = null;
        }
        if (CsStringComponent.focusedStringComp != null
                && moduleSettingsComponents.containsValue(CsStringComponent.focusedStringComp)) {
            CsStringComponent.focusedStringComp = null;
        }
        if (CsModeListComponent.openDropdown != null
                && moduleSettingsComponents.containsValue(CsModeListComponent.openDropdown)) {
            CsModeListComponent.openDropdown.setExpanded(false);
        }
    }

    private void resetDetachedPopupInputState(CsSettingComponent<?>... keepComponents) {
        boolean keepBind = false;
        boolean keepBoolean = false;
        boolean keepString = false;
        boolean keepDropdown = false;
        if (keepComponents != null) {
            for (CsSettingComponent<?> component : keepComponents) {
                if (component == null) {
                    continue;
                }
                keepBind |= component == CsSettingComponent.listeningBindComp;
                keepBoolean |= component == CsSettingComponent.listeningBooleanComp;
                keepString |= component == CsStringComponent.focusedStringComp;
                keepDropdown |= component == CsModeListComponent.openDropdown;
            }
        }
        if (!keepBind) {
            CsSettingComponent.listeningBindComp = null;
        }
        if (!keepBoolean) {
            CsSettingComponent.listeningBooleanComp = null;
        }
        if (!keepString) {
            CsStringComponent.focusedStringComp = null;
        }
        if (!keepDropdown && CsModeListComponent.openDropdown != null) {
            CsModeListComponent.openDropdown.setExpanded(false);
        }
    }

    private void syncActiveColorPickerPreview() {
        CsColorPicker colorPicker = CsColorPicker.get();
        if (colorPicker != null && colorPicker.getSetting() == tech.lightclient.modules.impl.render.ClickGui.getColorSetting()) {
            tech.lightclient.modules.impl.render.ClickGui.applySelectedAccentColor();
        }
    }

    private float clamp(float value, float min, float max) {
        if (max < min) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }

    private float getUiAnimationDeltaSeconds() {
        long now = System.currentTimeMillis();
        float dt = (now - lastUiAnimTime) / 1000f;
        lastUiAnimTime = now;
        if (!Float.isFinite(dt) || dt < 0f) {
            return 0f;
        }
        return Math.min(dt, 0.05f);
    }

    private float updateMenuHoverAnimation(String id, boolean hovered, float dt) {
        return updateMenuStateAnimation(menuHoverAnimations, id, hovered, dt);
    }

    private float updateMenuSelectionAnimation(String id, boolean selected, float dt) {
        return updateMenuStateAnimation(menuSelectionAnimations, id, selected, dt);
    }

    private float updateMenuStateAnimation(Map<String, Float> stateMap, String id, boolean active, float dt) {
        float current = stateMap.getOrDefault(id, 0f);
        float next = animateMenuHover(current, active ? 1f : 0f, dt);
        if (next <= 0.001f && !active) {
            stateMap.remove(id);
        } else {
            stateMap.put(id, next);
        }
        return next;
    }

    private static float animateMenuHover(float current, float target, float dt) {
        return animateValue(current, target, LEFT_MENU_HOVER_ANIM_SPEED, dt);
    }

    private static float animateValue(float current, float target, float speed, float dt) {
        float step = Math.min(1f, speed * Math.max(0f, dt));
        return current + (target - current) * step;
    }

    private float getMenuRowY(int index) {
        float scaleFactor = mc != null && mc.getWindow() != null ? (float) mc.getWindow().getScaleFactor() : 1f;
        float rowY = leftMenuY + index * (leftMenuRowHeight + leftMenuRowGap);
        if (index >= LEFT_MENU_SECOND_SECTION_START) {
            rowY += LEFT_MENU_SECTION_GAP * scaleFactor;
        }
        if (index >= LEFT_MENU_AUTOBUY_INDEX) {
            rowY += LEFT_MENU_AUTOBUY_GAP * scaleFactor;
        }
        return rowY;
    }

    /**
     * The native dialog must never be run on Minecraft's render thread: it owns the OpenGL
     * context and blocking it while a WinForms dialog pumps its own event loop can terminate
     * the client. The selected file is applied on the next render tick instead.
     */
    private void openCustomGifPicker() {
        if (!isWindows() || !gifPickerOpen.compareAndSet(false, true)) {
            return;
        }

        Thread pickerThread = new Thread(() -> {
            try {
                Path chosenGif = chooseGifWithWindowsDialog();
                if (chosenGif != null) {
                    pendingCustomGif = chosenGif;
                }
            } catch (Throwable ignored) {
                // Cancelling or failing to create a native dialog must leave the GUI usable.
            } finally {
                gifPickerOpen.set(false);
            }
        }, "light-clickgui-gif-picker");
        pickerThread.setDaemon(true);
        pickerThread.start();
    }

    private void applyPendingCustomGif() {
        Path chosenGif = pendingCustomGif;
        if (chosenGif == null) {
            return;
        }
        pendingCustomGif = null;
        activeClickGuiGif = CUSTOM_CLICK_GUI_GIF;
        GifTexture.replaceExternal(CUSTOM_CLICK_GUI_GIF, chosenGif);
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
    }

    private static Path chooseGifWithWindowsDialog() {
        String script = String.join("; ",
                "Add-Type -AssemblyName System.Windows.Forms",
                "$dialog = New-Object System.Windows.Forms.OpenFileDialog",
                "$dialog.Title = 'Select GIF'",
                "$dialog.Filter = 'GIF files (*.gif)|*.gif'",
                "$dialog.FilterIndex = 1",
                "$dialog.CheckFileExists = $true",
                "$dialog.Multiselect = $false",
                "$dialog.RestoreDirectory = $true",
                "if ($dialog.ShowDialog() -eq [System.Windows.Forms.DialogResult]::OK) { [Console]::OutputEncoding = [System.Text.Encoding]::UTF8; Write-Output $dialog.FileName }"
        );

        Process process = null;
        try {
            process = new ProcessBuilder(
                    "powershell.exe",
                    "-NoProfile",
                    "-STA",
                    "-ExecutionPolicy", "Bypass",
                    "-Command", script
            ).redirectErrorStream(true).start();

            String selected = null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isBlank()) {
                        selected = line.trim();
                    }
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0 || selected == null || selected.isBlank()) {
                return null;
            }

            Path path = Path.of(selected).toAbsolutePath().normalize();
            return isGifFile(path) ? path : null;
        } catch (Exception ignored) {
            return null;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private static boolean isGifFile(Path path) {
        if (path == null || !Files.isRegularFile(path)) {
            return false;
        }
        String fileName = path.getFileName() != null ? path.getFileName().toString() : "";
        return fileName.toLowerCase(Locale.ROOT).endsWith(".gif");
    }

    private record FunctionCardBounds(Function function, float x, float y, float w, float h) {
    }

    private record FunctionBindBounds(Function function, float x, float y, float w, float h) {
    }

    /** Границы настройки, раскрытой внутри карточки модуля. */
    private record InlineSettingBounds(CsSettingComponent<?> component, float x, float y, float w, float h) {
    }

    private record MenuEntry(String id, String label, String icon, Category category) {
    }

    private record TabBounds(int index, float x, float y, float w, float h) {
    }
}
