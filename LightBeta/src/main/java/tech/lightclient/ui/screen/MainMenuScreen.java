package tech.lightclient.ui.screen;

import com.adl.nativeprotect.User;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.FontOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.client.tweaks.crosshair.DrawContextFloatDrawTexture;
import tech.lightclient.client.utils.config.AltStorage;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.render.GpuCompatibility;
import tech.lightclient.client.utils.render.animations.Easings;
import tech.lightclient.client.utils.render.animations.impl.SimpleLinearAnimation;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontObject;
import tech.lightclient.client.utils.render.main.text.FontRegistry;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static tech.lightclient.client.utils.client.IMinecraft.mc;

public class MainMenuScreen extends Screen {
    private static final Identifier BACKGROUND_TEXTURE = Identifier.of("light", "mainmenu/mainmenuscreen.png");
    private static final Identifier ACCOUNT_ICON = Identifier.of("light", "icon.png");
    private static final Identifier MULTIPLAYER_BUTTON_TEXTURE = Identifier.of("light", "mainmenu/multiplayer.png");
    private static final Identifier SINGLEPLAYER_BUTTON_TEXTURE = Identifier.of("light", "mainmenu/singleplayer.png");
    private static final Identifier ACCOUNTS_BUTTON_TEXTURE = Identifier.of("light", "mainmenu/accounts.png");
    private static final Identifier OPTIONS_BUTTON_TEXTURE = Identifier.of("light", "mainmenu/options.png");
    private static final Identifier QUIT_GAME_BUTTON_TEXTURE = Identifier.of("light", "mainmenu/quit-game.png");

    private static final int PRIMARY_TEXT_COLOR = 0xFFF7F8FF;
    private static final int SECONDARY_TEXT_COLOR = 0xFFB7BED2;
    private static final int MUTE_TEXT_COLOR = 0xFF8A92A8;
    private static final int CARD_FILL = 0x18FFFFFF;
    private static final int CARD_OUTLINE = 0x22FFFFFF;
    private static final int SEARCH_BOX_COLOR = 0x80181C26;
    private static final int SECTION_LINE = 0x18FFFFFF;
    private static final int ACCOUNT_BACKGROUND_PRIORITY = 2;
    private static final int ACCOUNT_AVATAR_PRIORITY = 1;

    private static final float HOME_LEFT_PADDING = 46f;
    private static final float HOME_BUTTON_WIDTH = 420f;
    private static final float HOME_BUTTON_HEIGHT = 48f;
    private static final float HOME_BUTTON_SPACING = 16f;
    private static final float HOME_START_Y_OFFSET = 108f;
    private static final float HOME_TEXT_OFFSET_Y = 120f;
    private static final float HOME_SIDEBAR_RIGHT_GAP = HOME_LEFT_PADDING;
    private static final int HOME_SIDEBAR_COLOR = 0xF20D0F13;
    private static final int HOME_ENTRANCE_DURATION_MS = 462;

    private final List<ButtonComponent> buttons = new ArrayList<>();
    private final EnumMap<SettingsTab, ButtonWidget> tabButtons = new EnumMap<>(SettingsTab.class);
    private final SimpleLinearAnimation homeEntranceAnimation = new SimpleLinearAnimation(HOME_ENTRANCE_DURATION_MS, Easings.EASE_IN_OUT_CUBIC);

    private Integer previousMaxFps;
    private boolean sessionPrepared;
    private boolean homeEntrancePlayed;
    private boolean homeExitInProgress;
    private boolean homeExited;
    private Runnable homeExitAction;
    private boolean settingsView;
    private SettingsTab currentTab = SettingsTab.MAIN;

    private int leftColumnX;
    private int leftColumnWidth;
    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;
    private int tabsY;
    private int contentY;

    public MainMenuScreen() {
        super(Text.of("Main Menu"));
    }

    @Override
    protected void init() {
        if (!sessionPrepared) {
            AltStorage.applyLastSelectedAltSession();
            sessionPrepared = true;
        }

        if (!isCustomSettingsAllowed()) {
            this.settingsView = false;
        }

        // This screen instance is reused as the parent screen after closing a
        // sub-menu. Reset its entrance state so the panel comes in again.
        if (!settingsView && homeExited) {
            homeEntranceAnimation.setImmediate(false);
            homeEntrancePlayed = false;
            homeExitInProgress = false;
            homeExited = false;
            homeExitAction = null;
        }

        applyMenuFpsCap();
        updateLayout();
        this.clearChildren();
        this.buttons.clear();
        this.tabButtons.clear();

        if (settingsView) {
            initSettingsWidgets();
        } else {
            initHomeWidgets();
        }
    }

    private void initHomeWidgets() {
        buttons.add(new ButtonComponent("Multiplayer", MULTIPLAYER_BUTTON_TEXTURE, () -> mc.setScreen(new MultiplayerScreen(this))));
        buttons.add(new ButtonComponent("Singleplayer", SINGLEPLAYER_BUTTON_TEXTURE, () -> mc.setScreen(new SelectWorldScreen(this))));
        buttons.add(new ButtonComponent("Accounts", ACCOUNTS_BUTTON_TEXTURE, () -> mc.setScreen(new AltManagerScreen(this))));
        buttons.add(new ButtonComponent("Options", OPTIONS_BUTTON_TEXTURE, () -> mc.setScreen(new OptionsScreen(this, mc.options))));
        buttons.add(new ButtonComponent("Quit Game", QUIT_GAME_BUTTON_TEXTURE, mc::stop));

        layoutHomeButtons();
    }

    private void layoutHomeButtons() {
        int buttonWidth = Math.round(HOME_BUTTON_WIDTH);
        int buttonHeight = Math.round(HOME_BUTTON_HEIGHT);
        int gap = Math.round(HOME_BUTTON_SPACING);
        int startY = Math.round(this.height / 2f + HOME_START_Y_OFFSET);
        int x = Math.round(HOME_LEFT_PADDING + getHomeSlideOffset());

        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setBounds(x, startY + i * (buttonHeight + gap), buttonWidth, buttonHeight);
        }
    }

    private void initSettingsWidgets() {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Вернуться в меню"), button -> closeSettings())
                .dimensions(panelX, panelY - 28, 144, 18)
                .build());

        int gap = 4;
        int tabCount = SettingsTab.values().length;
        int[] widths = new int[tabCount];
        int totalWidth = 0;

        for (int i = 0; i < tabCount; i++) {
            int naturalWidth = this.textRenderer.getWidth(SettingsTab.values()[i].title) + 16;
            widths[i] = Math.max(56, naturalWidth);
            totalWidth += widths[i];
        }

        int availableWidth = panelWidth - 12;
        if (totalWidth + gap * (tabCount - 1) > availableWidth) {
            int compactWidth = Math.max(48, (availableWidth - gap * (tabCount - 1)) / tabCount);
            for (int i = 0; i < tabCount; i++) {
                widths[i] = compactWidth;
            }
            totalWidth = compactWidth * tabCount;
        }

        int x = panelX + Math.max(0, (panelWidth - (totalWidth + gap * (tabCount - 1))) / 2);

        for (int i = 0; i < tabCount; i++) {
            SettingsTab tab = SettingsTab.values()[i];
            SettingsTab targetTab = tab;
            ButtonWidget button = ButtonWidget.builder(Text.literal(tab.title), widget -> openSettings(targetTab))
                    .dimensions(x, tabsY, widths[i], 16)
                    .build();
            this.tabButtons.put(tab, this.addDrawableChild(button));
            x += widths[i] + gap;
        }

        buildTabContent();
    }

    private void buildTabContent() {
        if (currentTab == SettingsTab.MAIN) {
            buildMainTabContent();
            return;
        }

        List<WidgetFactory> entries = getTabEntries();
        int gapX = 10;
        int gapY = this.height < 900 ? 22 : 24;
        int columnWidth = (panelWidth - gapX) / 2;

        for (int i = 0; i < entries.size(); i++) {
            int col = i % 2;
            int row = i / 2;
            int x = panelX + col * (columnWidth + gapX);
            int y = contentY + row * gapY;
            this.addDrawableChild(entries.get(i).create(x, y, columnWidth));
        }
    }

    private void buildMainTabContent() {
        int gapX = 10;
        int halfWidth = (panelWidth - gapX) / 2;
        int rightX = panelX + halfWidth + gapX;
        int top = tabsY + 42;
        // Keep the quick-settings page compact.  The widgets are rendered at
        // the menu scale, so the old 34/54 pixel jumps became very large
        // empty gaps on screen.
        int rowStep = 28;

        addWidget(option(this.client.options.getSoundVolumeOption(SoundCategory.MASTER)), panelX, top, panelWidth);
        addWidget(option(this.client.options.getShowSubtitles()), panelX, top + rowStep, halfWidth);
        addWidget(option(this.client.options.getDirectionalAudio()), rightX, top + rowStep, halfWidth);

        addWidget(option(this.client.options.getFov()), panelX, top + rowStep * 2, halfWidth);
        addWidget(option(this.client.options.getGamma()), rightX, top + rowStep * 2, halfWidth);
        addWidget(option(this.client.options.getMouseSensitivity()), panelX, top + rowStep * 3, halfWidth);
        addWidget(option(this.client.options.getChatVisibility()), rightX, top + rowStep * 3, halfWidth);

        addWidget(option(this.client.options.getMaxFps()), panelX, top + rowStep * 4, panelWidth);
        addWidget(option(this.client.options.getFullscreen()), panelX, top + rowStep * 5, halfWidth);
        addWidget(option(this.client.options.getEnableVsync()), rightX, top + rowStep * 5, halfWidth);

        addWidget(option(this.client.options.getGuiScale()), panelX, top + rowStep * 6, halfWidth);
        addWidget(option(this.client.options.getViewDistance()), rightX, top + rowStep * 6, halfWidth);
    }

    private void addWidget(WidgetFactory factory, int x, int y, int width) {
        this.addDrawableChild(factory.create(x, y, width));
    }

    private List<WidgetFactory> getTabEntries() {
        if (this.client == null || this.client.options == null) {
            return List.of();
        }

        return switch (currentTab) {
            case MAIN -> List.of();
            case SOUND -> List.of(
                    option(this.client.options.getSoundVolumeOption(SoundCategory.MUSIC)),
                    option(this.client.options.getSoundVolumeOption(SoundCategory.WEATHER)),
                    option(this.client.options.getSoundVolumeOption(SoundCategory.BLOCKS)),
                    option(this.client.options.getSoundVolumeOption(SoundCategory.HOSTILE)),
                    option(this.client.options.getSoundVolumeOption(SoundCategory.PLAYERS)),
                    option(this.client.options.getSoundVolumeOption(SoundCategory.AMBIENT)),
                    option(this.client.options.getSoundVolumeOption(SoundCategory.VOICE)),
                    option(this.client.options.getShowSubtitles()),
                    option(this.client.options.getDirectionalAudio()),
                    option(this.client.options.getSoundDevice()),
                    option(this.client.options.getMusicFrequency())
            );
            case GRAPHICS -> List.of(
                    option(this.client.options.getSimulationDistance()),
                    option(this.client.options.getEntityDistanceScaling()),
                    option(this.client.options.getCloudRenderMode()),
                    option(this.client.options.getParticles()),
                    option(this.client.options.getMipmapLevels()),
                    option(this.client.options.getAo()),
                    option(this.client.options.getBiomeBlendRadius()),
                    option(this.client.options.getPanoramaSpeed()),
                    option(this.client.options.getFovEffectScale()),
                    option(this.client.options.getMenuBackgroundBlurriness())
            );
            case CONTROLS -> List.of(
                    option(this.client.options.getInvertMouseX()),
                    option(this.client.options.getInvertMouseY()),
                    option(this.client.options.getRawMouseInput()),
                    option(this.client.options.getDiscreteMouseScroll()),
                    option(this.client.options.getTouchscreen()),
                    option(this.client.options.getMainArm()),
                    option(this.client.options.getSneakToggled()),
                    option(this.client.options.getSprintToggled()),
                    option(this.client.options.getAutoJump()),
                    action("Клавиши и управление", button -> this.client.setScreen(new ControlsOptionsScreen(this, this.client.options)))
            );
            case LANGUAGE -> List.of(
                    action("Список языков", button -> this.client.setScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager()))),
                    action("Настройки шрифта", button -> this.client.setScreen(new FontOptionsScreen(this, this.client.options))),
                    option(this.client.options.getForceUnicodeFont())
            );
            case CHAT -> List.of(
                    option(this.client.options.getChatColors()),
                    option(this.client.options.getChatLinks()),
                    option(this.client.options.getChatLinksPrompt()),
                    option(this.client.options.getAutoSuggestions()),
                    option(this.client.options.getChatScale()),
                    option(this.client.options.getChatWidth()),
                    option(this.client.options.getChatOpacity()),
                    option(this.client.options.getTextBackgroundOpacity()),
                    option(this.client.options.getChatLineSpacing()),
                    option(this.client.options.getChatHeightFocused()),
                    option(this.client.options.getChatHeightUnfocused()),
                    option(this.client.options.getBackgroundForChatOnly()),
                    option(this.client.options.getHideMatchedNames())
            );
            case RESOURCE_PACKS -> List.of(
                    action("Открыть меню ресурспаков", button -> openResourcePackScreen())
            );
            case ACCESSIBILITY -> List.of(
                    option(this.client.options.getNarrator()),
                    option(this.client.options.getNarratorHotkey()),
                    option(this.client.options.getHighContrastBlockOutline()),
                    option(this.client.options.getNotificationDisplayTime()),
                    option(this.client.options.getDistortionEffectScale()),
                    option(this.client.options.getDarknessEffectScale()),
                    option(this.client.options.getDamageTiltStrength()),
                    option(this.client.options.getGlintSpeed()),
                    option(this.client.options.getGlintStrength()),
                    option(this.client.options.getMonochromeLogo())
            );
        };
    }

    private WidgetFactory option(SimpleOption<?> option) {
        return (x, y, width) -> option.createWidget(this.client.options, x, y, width);
    }

    private WidgetFactory action(String label, ButtonWidget.PressAction action) {
        return (x, y, width) -> ButtonWidget.builder(Text.literal(label), action)
                .dimensions(x, y, width, 20)
                .build();
    }

    private void openSettings(SettingsTab tab) {
        if (!isCustomSettingsAllowed()) {
            return;
        }

        this.settingsView = true;
        this.currentTab = tab;
        this.clearAndInit();
    }

    private void closeSettings() {
        this.settingsView = false;
        this.clearAndInit();
    }

    private void openResourcePackScreen() {
        if (this.client == null) {
            return;
        }

        this.client.setScreen(new PackScreen(
                this.client.getResourcePackManager(),
                manager -> {
                    this.client.options.refreshResourcePacks(manager);
                    this.client.setScreen(this);
                },
                this.client.getResourcePackDir(),
                Text.translatable("options.resourcepack")
        ));
    }

    public boolean shouldForceCustomWidgets() {
        return settingsView && isCustomSettingsAllowed();
    }

    @Override
    public void tick() {
        if (!homeExitInProgress || !homeEntranceAnimation.isFinished()) {
            return;
        }

        Runnable action = homeExitAction;
        homeExitAction = null;
        homeExitInProgress = false;
        homeExited = true;
        if (action != null) {
            action.run();
        }
    }

    @Override
    public void close() {
        if (settingsView) {
            closeSettings();
            return;
        }

        if (this.client != null && this.client.world != null) {
            this.client.setScreen(null);
        }
    }

    @Override
    public void removed() {
        restorePreviousFpsCap();
        super.removed();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    @Override
    protected void renderDarkening(DrawContext context) {
    }

    @Override
    protected void applyBlur(DrawContext context) {
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        updateLayout();

        if (settingsView) {
            drawSettingsChrome(context);
            super.render(context, mouseX, mouseY, delta);
            drawActiveTabIndicator(context);
            return;
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @EventHandler
    public void onRender(EventRender.Screen.Gui event) {
        if (mc.currentScreen != this) {
            return;
        }

        updateLayout();
        Renderer2D renderer = event.getRenderer();

        if (settingsView) {
            renderer.drawTexture(BACKGROUND_TEXTURE, 0f, 0f, event.getViewportWidth(), event.getViewportHeight(), 0xFFFFFFFF);
            renderer.flush();
            if (!GpuCompatibility.isAmd()) {
                renderer.prepareBlurForced(8f);
            }
            return;
        }

        float fade = SharedBackgroundParticles.getBackgroundFadeAlpha();
        renderer.drawTexture(BACKGROUND_TEXTURE, 0f, 0f, event.getViewportWidth(), event.getViewportHeight(), withAlpha(0xFFFFFF, (int) (fade * 255)));
        renderer.flush();
        if (!GpuCompatibility.isAmd()) {
            renderer.prepareBlurForced(8f);
        }
    }

    @EventHandler
    public void onRender(EventRender.Screen.OverGui event) {
        if (mc.currentScreen != this) {
            return;
        }

        updateLayout();
        Renderer2D renderer = event.getRenderer();
        FontObject font = FontRegistry.INTER;

        if (settingsView) {
            renderSettingsUserCard(renderer, font);
            return;
        }

        float fade = SharedBackgroundParticles.getBackgroundFadeAlpha();

        // The existing menu background finishes loading through a fade-in.
        // Do not run the slide underneath that fade: begin only once its alpha
        // is fully opaque, so the left-to-right Ease In Out motion is visible.
        if (!homeEntrancePlayed && fade >= 1f) {
            homeEntranceAnimation.setImmediate(false);
            homeEntranceAnimation.show();
            homeEntrancePlayed = true;
        }
        layoutHomeButtons();

        float mouseX = getMouseX(event.getViewportWidth());
        float mouseY = getMouseY(event.getViewportHeight());

        renderer.pushAlpha(fade);
        renderHomeSidebar(renderer, event.getViewportHeight());
        renderHomeUserCard(renderer, font);
        renderHomeChrome(renderer, font);
        for (ButtonComponent button : buttons) {
            button.render(renderer, font, mouseX, mouseY);
        }
        renderer.popAlpha();
    }

    @Override
    public boolean mouseClicked(Click click, boolean isRepeated) {
        if (!settingsView) {
            if (homeExitInProgress) {
                return true;
            }
            layoutHomeButtons();
            float mouseX = getMouseX(mc.getWindow().getFramebufferWidth());
            float mouseY = getMouseY(mc.getWindow().getFramebufferHeight());
            for (ButtonComponent button : buttons) {
                if (button.isHovered(mouseX, mouseY)) {
                    beginHomeExit(button.onClick);
                    return true;
                }
            }
        }

        return super.mouseClicked(click, isRepeated);
    }

    private void drawSettingsChrome(DrawContext context) {
        int accent = ClientColors.ICON.getRGB();
        int searchWidth = 176;
        int searchHeight = 18;
        int searchX = panelX + panelWidth - searchWidth;
        int searchY = panelY + 4;

        context.drawText(this.textRenderer, Text.literal("ИГРОВЫЕ НАСТРОЙКИ"), panelX, panelY + 6, accent, false);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Собрали быстрые настройки на первой вкладке."), panelX, panelY + 22, PRIMARY_TEXT_COLOR);
        context.drawText(this.textRenderer, Text.literal("Нужное под рукой, остальное разнесено по отдельным страницам."), panelX, panelY + 36, SECONDARY_TEXT_COLOR, false);

        context.fill(searchX, searchY, searchX + searchWidth, searchY + searchHeight, SEARCH_BOX_COLOR);
        drawOutline(context, searchX, searchY, searchWidth, searchHeight, 0x14FFFFFF);
        context.drawText(this.textRenderer, Text.literal("Поиск настройки..."), searchX + 10, searchY + 5, MUTE_TEXT_COLOR, false);

        int hintY = tabsY + 28;
        context.fill(panelX, hintY, panelX + panelWidth, hintY + 16, 0x4211171F);
        drawOutline(context, panelX, hintY, panelWidth, 16, 0x10FFFFFF);
        context.drawText(this.textRenderer, Text.literal(getSectionHint()), panelX + 9, hintY + 4, SECONDARY_TEXT_COLOR, false);

        int crumbY = contentY - 20;
        context.drawText(this.textRenderer, Text.literal(currentTab.breadcrumb + " • доступные опции"), panelX, crumbY, MUTE_TEXT_COLOR, false);
        context.fill(panelX, crumbY + 11, panelX + panelWidth, crumbY + 12, SECTION_LINE);
    }

    private void drawSettingsSectionText(DrawContext context) {
        if (currentTab != SettingsTab.MAIN) {
            return;
        }

        context.drawText(this.textRenderer, Text.literal("Микшер"), panelX + 14, contentY + 4, PRIMARY_TEXT_COLOR, false);
        context.drawText(this.textRenderer, Text.literal("Быстрый звук сверху"), panelX + 14, contentY + 16, MUTE_TEXT_COLOR, false);

        context.drawText(this.textRenderer, Text.literal("Игровой блок"), panelX + 14, contentY + 98, PRIMARY_TEXT_COLOR, false);
        context.drawText(this.textRenderer, Text.literal("Поле зрения, яркость, мышь и чат"), panelX + 14, contentY + 110, MUTE_TEXT_COLOR, false);

        context.drawText(this.textRenderer, Text.literal("Производительность"), panelX + 14, contentY + 192, PRIMARY_TEXT_COLOR, false);
        context.drawText(this.textRenderer, Text.literal("FPS отдельно, чтобы не терялся среди кнопок"), panelX + 14, contentY + 204, MUTE_TEXT_COLOR, false);

        context.drawText(this.textRenderer, Text.literal("Интерфейс"), panelX + 14, contentY + 286, PRIMARY_TEXT_COLOR, false);
        context.drawText(this.textRenderer, Text.literal("Размер и дальность под рукой"), panelX + 14, contentY + 298, MUTE_TEXT_COLOR, false);
    }

    private void drawActiveTabIndicator(DrawContext context) {
        ButtonWidget active = this.tabButtons.get(currentTab);
        if (active == null) {
            return;
        }

        int accent = ClientColors.ICON.getRGB();
        int lineY = active.getY() + active.getHeight() + 3;
        context.fill(active.getX(), lineY, active.getX() + active.getWidth(), lineY + 2, accent);
    }

    private void flushAccountLayer(Renderer2D renderer, int renderedPriority, int nextPriority) {
        if (renderedPriority > nextPriority) {
            renderer.flush();
        }
    }

    private void renderHomeUserCard(Renderer2D renderer, FontObject font) {
        String username = mc.getSession() != null ? mc.getSession().getUsername() : "unknown";
        float x = 20f + getHomeSlideOffset();
        float y = 42f;
        float width = 290f;
        float height = 58f;
        float round = 12f;
        float iconSize = 34f;
        float iconX = x + 14f;
        float iconY = y + (height - iconSize) * 0.5f;

        if (!GpuCompatibility.isAmd()) {
            renderer.blur(x, y, width, height, round, 0.7f);
        }
        renderer.rect(x, y, width, height, round, withAlpha(0x000000, 0));
        renderer.rectOutline(x, y, width, height, round, withAlpha(0xFFFFFF, 20), 1f);
        flushAccountLayer(renderer, ACCOUNT_BACKGROUND_PRIORITY, ACCOUNT_AVATAR_PRIORITY);
        renderer.drawTextureRounded(ACCOUNT_ICON, iconX, iconY, iconSize, iconSize, 0xFFFFFFFF, 8f, false);
        renderer.flush();

        float textX = iconX + iconSize + 14f;
        drawText(renderer, font, textX, y + 10f, 12f, "Текущий аккаунт", 0xFF96969B);
        drawText(renderer, font, textX, y + 27f, 16.5f, username, PRIMARY_TEXT_COLOR);
    }

    private void renderSettingsUserCard(Renderer2D renderer, FontObject font) {
        String username = User.getInstance().profile("username");
        float x = panelX;
        float y = 18f;
        float width = 250f;
        float height = 50f;
        float round = 11f;
        float iconSize = 24f;
        float iconX = x + 10f;
        float iconY = y + (height - iconSize) * 0.5f;

        if (!GpuCompatibility.isAmd()) {
            renderer.blur(x, y, width, height, round, 0.6f);
        }
        renderer.rect(x, y, width, height, round, withAlpha(0xFFFFFF, 18));
        renderer.rectOutline(x, y, width, height, round, withAlpha(0xFFFFFF, 24), 1f);
        flushAccountLayer(renderer, ACCOUNT_BACKGROUND_PRIORITY, ACCOUNT_AVATAR_PRIORITY);
        renderer.drawTextureRounded(ACCOUNT_ICON, iconX, iconY, iconSize, iconSize, 0xFFFFFFFF, 7f, false);
        renderer.flush();

        float textX = iconX + iconSize + 10f;
        drawText(renderer, font, textX, y + 9f, 10.5f, "Текущий аккаунт", 0xFF9EA6B8);
        drawText(renderer, font, textX, y + 21f, 16f, username, PRIMARY_TEXT_COLOR);
    }

    private void renderSettingsPanels(Renderer2D renderer, FontObject font) {
        if (currentTab != SettingsTab.MAIN) {
            return;
        }

        renderSettingsPanel(renderer, font, panelX, contentY, panelWidth, 78f, 0x1E121824, 0x24FFFFFF, true);
        renderSettingsPanel(renderer, font, panelX, contentY + 94, panelWidth, 78f, 0x16121822, 0x1DFFFFFF, false);
        renderSettingsPanel(renderer, font, panelX, contentY + 188, panelWidth, 78f, 0x1C162022, 0x24FFFFFF, true);
        renderSettingsPanel(renderer, font, panelX, contentY + 282, panelWidth, 56f, 0x14131920, 0x1AFFFFFF, false);
    }

    private void renderSettingsPanel(Renderer2D renderer, FontObject font, float x, float y, float width, float height, int fillColor, int outlineColor, boolean accent) {
        float round = 12f;
        renderer.blur(x, y, width, height, round, 0.58f);
        renderer.rect(x, y, width, height, round, fillColor);
        renderer.rectOutline(x, y, width, height, round, outlineColor, 1f);

        if (accent) {
            int accentColor = ClientColors.ICON.getRGB();
            renderer.rect(x + 12f, y + 12f, 38f, 2f, 1f, withAlpha(accentColor, 220));
            drawText(renderer, font, x + width - 78f, y + 12f, 7.2f, "focus", withAlpha(accentColor, 230));
        }
    }

    private void renderHomeChrome(Renderer2D renderer, FontObject font) {
        int accent = ClientColors.ICON.getRGB();
        String username = User.getInstance().profile("username");
        float x = HOME_LEFT_PADDING + getHomeSlideOffset();
        float startY = this.height / 2f + HOME_START_Y_OFFSET;
        float textY = startY - HOME_TEXT_OFFSET_Y;

        drawText(renderer, font, x, textY, 14f, "Давно не виделись, " + username, accent);
        drawText(renderer, font, x, textY + 30f, 25f, "Приветствуем тебя в", PRIMARY_TEXT_COLOR);
        drawText(renderer, font, x, textY + 72f, 32f, "LightClient", accent);
    }

    private void renderHomeSidebar(Renderer2D renderer, float viewportHeight) {
        // Keep the panel close to the buttons while leaving a deliberate small margin at their right.
        float width = HOME_LEFT_PADDING + HOME_BUTTON_WIDTH + HOME_SIDEBAR_RIGHT_GAP;
        renderer.rect(getHomeSlideOffset(), 0f, width, viewportHeight, 0f, HOME_SIDEBAR_COLOR);
    }

    private void beginHomeExit(Runnable action) {
        if (homeExitInProgress || action == null) {
            return;
        }

        homeExitAction = action;
        homeExitInProgress = true;
        homeEntranceAnimation.hide();
    }

    private float getHomeSlideOffset() {
        float sidebarWidth = HOME_LEFT_PADDING + HOME_BUTTON_WIDTH + HOME_SIDEBAR_RIGHT_GAP;
        return -sidebarWidth * (1f - homeEntranceAnimation.getProgress());
    }

    private String getSectionHint() {
        return switch (currentTab) {
            case MAIN -> "Быстрая страница: звук, графика, управление и чат в одном месте.";
            case CONTROLS -> "Основные переключатели здесь, а бинды открываются отдельной кнопкой.";
            case LANGUAGE -> "Полный список языков и шрифтов открывается из кнопок внутри вкладки.";
            case RESOURCE_PACKS -> "Ресурспаки открываются стандартным экраном Minecraft, но прямо отсюда.";
            default -> "Здесь уже более узкие настройки категории без лишнего на первом экране.";
        };
    }

    private void updateLayout() {
        this.leftColumnX = clampInt((int) (this.width * 0.04f), 28, 70);
        this.leftColumnWidth = clampInt((int) (this.width * 0.17f), 200, 250);

        this.panelX = leftColumnX;
        this.panelY = clampInt((int) (this.height * 0.12f), 92, 128);
        this.panelWidth = Math.min(Math.max((int) (this.width * 0.28f), 500), this.width - panelX - 90);
        this.panelHeight = this.height - panelY - 80;
        this.tabsY = panelY + 56;
        this.contentY = panelY + 138;
    }

    private void drawText(Renderer2D renderer, FontObject font, float x, float yTop, float size, String text, int color) {
        float baseline = centerBaseline(font, yTop + size * 0.5f, size);
        renderer.text(font, x, baseline, size, text, color);
    }

    private float centerBaseline(FontObject font, float centerY, float size) {
        return centerY + FontRegistry.centeredBaselineOffset(font, 'H', size);
    }

    private void drawOutline(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y, x + 1, y + height, color);
        context.fill(x + width - 1, y, x + width, y + height, color);
    }

    private void applyMenuFpsCap() {
        if (mc == null || mc.options == null || mc.options.getMaxFps() == null) {
            return;
        }

        int current = mc.options.getMaxFps().getValue();
        if (previousMaxFps == null) {
            previousMaxFps = current;
        }

        if (current < 120) {
            mc.options.getMaxFps().setValue(120);
        }
    }

    private void restorePreviousFpsCap() {
        if (previousMaxFps == null || mc == null || mc.options == null || mc.options.getMaxFps() == null) {
            return;
        }

        mc.options.getMaxFps().setValue(previousMaxFps);
        previousMaxFps = null;
    }

    private float getMouseX(float viewportWidth) {
        return (float) (mc.mouse.getX() * (double) viewportWidth / (double) mc.getWindow().getWidth());
    }

    private float getMouseY(float viewportHeight) {
        return (float) (mc.mouse.getY() * (double) viewportHeight / (double) mc.getWindow().getHeight());
    }

    private static int clampInt(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static int withAlpha(int rgb, int alpha) {
        int a = Math.max(0, Math.min(255, alpha));
        return (a << 24) | (rgb & 0x00FFFFFF);
    }

    private boolean isCustomSettingsAllowed() {
        return this.client != null && this.client.world == null;
    }

    private interface WidgetFactory {
        ClickableWidget create(int x, int y, int width);
    }

    private enum SettingsTab {
        MAIN("Главные", "главные"),
        SOUND("Звуки", "звуки"),
        GRAPHICS("Графика", "графика"),
        CONTROLS("Управление", "управление"),
        LANGUAGE("Язык", "язык"),
        CHAT("Чат", "чат"),
        RESOURCE_PACKS("Ресурспаки", "ресурспаки"),
        ACCESSIBILITY("Возможности", "возможности");

        private final String title;
        private final String breadcrumb;

        SettingsTab(String title, String breadcrumb) {
            this.title = title;
            this.breadcrumb = breadcrumb;
        }
    }
}
