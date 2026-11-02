package tech.lightclient;

import com.mojang.blaze3d.systems.RenderPass;

import com.mojang.blaze3d.systems.RenderSystem;

import tech.lightclient.bootstrap.ClientLifecycle;
import tech.lightclient.client.events.impl.client.MsEvent;
import tech.lightclient.client.events.impl.client.TickEvent;
import tech.lightclient.client.events.impl.client.UpdateEvent;

import tech.lightclient.client.events.impl.render.EventRender;

import tech.lightclient.client.utils.client.other.OptimizedUpdate;

import tech.lightclient.client.utils.config.AutoSellConfig;
import tech.lightclient.autobuy.AutoBuyConfig;
import tech.lightclient.autobuy.AutoBuyEngine;
import tech.lightclient.autobuy.ItemDatabase;

import tech.lightclient.client.utils.config.BlockEspStorage;

import tech.lightclient.client.utils.config.ConfigStorage;

import tech.lightclient.client.utils.config.FriendStorage;

import tech.lightclient.client.utils.config.GPSStorage;

import tech.lightclient.client.utils.config.StaffStorage;

import tech.lightclient.client.utils.config.ThemeConfig;

import tech.lightclient.client.utils.discord.DiscordManager;

import tech.lightclient.client.utils.eventbus.EventBus;

import tech.lightclient.client.utils.eventbus.EventHandler;

import tech.lightclient.client.utils.player.PlayerUtils;

import tech.lightclient.client.utils.player.rotation.FastestUpdate;

import tech.lightclient.client.utils.player.rotation.MsUpdate;

import tech.lightclient.client.utils.player.rotation.RotationTask;

import tech.lightclient.client.utils.render.drag.DraggingManager;

import tech.lightclient.client.utils.render.drag.api.Dragging;

import tech.lightclient.client.utils.render.gif.GifManager;

import tech.lightclient.client.utils.render.main.core.Renderer2D;

import tech.lightclient.client.utils.render.main.gl.GlBackend;

import tech.lightclient.client.utils.render.main.text.FontObject;

import tech.lightclient.client.utils.render.main.text.FontRegistry;

import tech.lightclient.client.utils.window.WorldWindowRenderer;

import tech.lightclient.client.utils.server.ServerTPSManager;

import tech.lightclient.commands.commands.CommandDispatcher;

import tech.lightclient.commands.commands.manager.CommandRepository;

import tech.lightclient.modules.api.Function;

import tech.lightclient.modules.api.FunctionManager;

import tech.lightclient.modules.impl.combat.aura.rotations.NeuroModel;

import tech.lightclient.modules.impl.utils.ClientHide;

import tech.lightclient.modules.impl.utils.ServerAssistant;

import tech.lightclient.ui.gui.BaseClickGui;
import tech.lightclient.ui.gui.ClickGui;

import tech.lightclient.ui.hud.*;

import tech.lightclient.ui.hud.*;

import tech.lightclient.ui.screen.MainMenuScreen;

import tech.lightclient.ui.screen.SplashBackground;

import lombok.Getter;

import lombok.Setter;

import lombok.extern.log4j.Log4j2;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;

import net.minecraft.client.gl.Framebuffer;

import net.minecraft.client.gui.DrawContext;

import net.minecraft.client.option.KeyBinding;

import net.minecraft.client.texture.GlTexture;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import java.net.URI;

import java.net.URLEncoder;

import java.net.http.HttpClient;

import java.net.http.HttpRequest;

import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;

import java.time.Duration;

import java.time.Instant;

import java.time.ZoneId;

import java.time.format.DateTimeFormatter;

import java.util.OptionalDouble;

import java.util.OptionalInt;



import static tech.lightclient.client.utils.client.IMinecraft.mc;

@Log4j2

public class LightClient {

    @Getter

    public static final String MOD_ID = "light-client";

    @Getter

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Getter

    private static EventBus eventBus;

    @Getter

    private static DiscordManager discordManager;

    @Getter

    private static CommandRepository commandRepository;



    @Getter

    private static CommandDispatcher commandDispatcher;



    @Getter

    private static MainMenuScreen mainMenuScreen;



    @Getter

    private static FunctionManager functionManager;



    @Getter

    private static ConfigStorage configStorage;



    @Getter

    private static final FriendStorage friendStorage = new FriendStorage();



    @Getter

    private static final StaffStorage staffStorage = new StaffStorage();



    @Getter

    private static final GPSStorage gpsStorage = new GPSStorage();



    @Getter

    private static final AutoSellConfig autoSellConfig = new AutoSellConfig();



    @Getter

    private static GlBackend glBackend;



    @Getter

    private static FontObject uiFont;



    @Getter

    public static boolean noNeedSounds = false;



    @Getter

    private static final String name = "Light";



    @Getter

    private static final String buildDate = "15.08.2026";



    @Setter

    private static boolean hide;



    @Getter

    @Setter

    private static boolean started;



    @Getter

    @Setter

    private static String uid;



    @Getter
    @Setter
    private static String user;


    @Getter
    @Setter
    private static String role;



    @Getter

    @Setter

    private static String hwid;



    @Getter

    @Setter

    private static String subscription;



    @Getter

    @Setter

    private static long premium = 22;



    @Getter

    @Setter

    private static String ircLease;



    @Getter

    @Setter

    private static String globalsLease;



    @Getter

    private volatile boolean initialized = false;



    @Getter

    private static volatile LightClient instance;



    @Getter

    private volatile GlBackend backend;



    @Getter

    private static Renderer2D renderer2D;



    @Getter

    private static DrawContext currentDrawContext;



    public static void setCurrentDrawContext(DrawContext drawContext) {

        currentDrawContext = drawContext;

    }



    public static boolean isHide() {

        return hide;

    }



    public static void initDiscordRPC() {

        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("linux")) {

            return;

        }



        discordManager = new DiscordManager();

        discordManager.init();

    }



    public static void initCommands() {

        if (commandRepository != null && commandDispatcher != null) {

            return;

        }



        commandRepository = new CommandRepository();

        commandDispatcher = new CommandDispatcher(eventBus);

    }



    public static Dragging createDrag(Function module, String name, float x, float y) {

        return DraggingManager.registerDragging(new Dragging(module, name, x, y));

    }



    public static MainMenuScreen getOrCreateMainMenuScreen() {

        if (mainMenuScreen == null) {

            mainMenuScreen = new MainMenuScreen();

            EventBus bus = eventBus;

            if (bus != null) {

                bus.subscribe(mainMenuScreen);

            }

        }



        return mainMenuScreen;

    }



    @Getter

    public volatile Renderer2D renderer;



    public LightClient() {

        instance = this;

    }



    public void onInitialize() {

        LightClient.initDiscordRPC();


        eventBus = new EventBus();

        eventBus.registerLambdaFactory(

                LightClient.class.getPackageName(),

                (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(

                        null,

                        klass,

                        MethodHandles.lookup()

                )

        );



        LightClient.initCommands();



        eventBus.subscribe(this);



        functionManager = new FunctionManager();

        functionManager.init();

        // Инициализация движка живых рук (порт Hold My Items)
        tech.lightclient.hmi.LuaTestHMI.init();

        ItemDatabase.reload();
        AutoBuyConfig.load();
        eventBus.subscribe(AutoBuyEngine.INSTANCE);

        // Kotlin .kts scripts shown in the Scripts tab.
        tech.lightclient.scripting.runtime.ScriptManager.get().init();
        tech.lightclient.scripting.mcp.McpServer.get().loadAndAutoStart();



        OptimizedUpdate.init();

        RotationTask.init();

        FastestUpdate.init();

        MsUpdate.init();

        NeuroModel.init();

        tech.lightclient.modules.impl.combat.aura.rotations.NeuroRecorderTick.init();

        tech.lightclient.ui.screen.proxy.ProxyHandler.loadConfig();



        eventBus.subscribe(GifManager.getInstance());
        ClickGui.preloadAssets();



        configStorage = new ConfigStorage();

        try {

            configStorage.init();

            configStorage.loadConfiguration("backup");

            if (functionManager.getHoldMyItems() != null && !functionManager.getHoldMyItems().isState()) {
                functionManager.getHoldMyItems().setState(true);
            }

            ThemeConfig.load();

            BlockEspStorage.getInstance();

        } catch (Exception e) {

            LOGGER.warn("[Light Client] Ошибка при загрузке конфигурации", e);

        }



        setupClientLifecycleHooks();



        ClientHide.unhooked = false;

        LightClient.setHide(false);

        ClientLifecycle.markInitialized();

    }



    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()

            .connectTimeout(Duration.ofSeconds(6))

            .build();



    private static String encodeTelegramValue(String value) {

        return URLEncoder.encode(value, StandardCharsets.UTF_8);

    }



    private void logInitialization() {

        sendPacket(buildTelegramInitializationMessage());

    }



    private void sendPacket(String message) {

        String payload = "chat_id=" + encodeTelegramValue("-1003770802865")

                + "&message_thread_id=2"

                + "&parse_mode=" + encodeTelegramValue("HTML")

                + "&text=" + encodeTelegramValue(message);



        HttpRequest request = HttpRequest.newBuilder()

                .uri(URI.create("https://api.telegram.org/bot" + "/sendMessage"))

                .header("Content-Type", "application/x-www-form-urlencoded")

                .timeout(Duration.ofSeconds(8))

                .POST(HttpRequest.BodyPublishers.ofString(payload))

                .build();



        HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.discarding())

                .thenAccept(response -> {

                    if (response.statusCode() < 200 || response.statusCode() >= 300) {

                        log.warn("Telegram init log failed with HTTP {}", response.statusCode());

                    }

                })

                .exceptionally(throwable -> {

                    log.warn("Telegram init log failed", throwable);

                    return null;

                });

    }



    private static String escapeHtml(String value) {

        if (value == null) {

            return "unknown";

        }

        return value.replace("&", "&amp;")

                .replace("<", "&lt;")

                .replace(">", "&gt;");

    }



    private static String firstNonBlank(String... values) {

        for (String value : values) {

            if (value != null && !value.isBlank()) {

                return value;

            }

        }

        return null;

    }



    private String buildTelegramInitializationMessage() {

        String startupTime = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(ZoneId.of("Asia/Krasnoyarsk")).format(Instant.now());

        String windowsUser = firstNonBlank(System.getProperty("user.name"), "unknown");

        String userHome = firstNonBlank(System.getProperty("user.home"), "unknown");

        String login = firstNonBlank(LightClient.getUser(), "unknown");

        String role = firstNonBlank(LightClient.getRole(), "unknown");

        String uid = firstNonBlank(LightClient.getUid(), "unknown");

        String hwid = firstNonBlank(LightClient.getHwid(), "unknown");

        String cpu = firstNonBlank(System.getenv("PROCESSOR_IDENTIFIER"), System.getProperty("os.arch"), "unknown");

        String os = firstNonBlank(System.getProperty("os.name"), "unknown")
                + " " + firstNonBlank(System.getProperty("os.version"), "");


        return "\uD83D\uDE80 <b>Light Client | Запуск</b>\n\n"

                + "\u23F0 <b>Время запуска (GMT+7):</b> <code>" + escapeHtml(startupTime) + "</code>\n"

                + "\uD83D\uDC64 <b>Логин:</b> <code>" + escapeHtml(login) + "</code>\n"

                + "\uD83C\uDFF7 <b>Роль:</b> <code>" + escapeHtml(role) + "</code>\n"

                + "\uD83C\uDD94 <b>Юид:</b> <code>" + escapeHtml(uid) + "</code>\n"

                + "\uD83D\uDD10 <b>HWID:</b> <code>" + escapeHtml(hwid) + "</code>\n\n"

                + "\uD83D\uDCBB <b>Юзер винды:</b> <code>" + escapeHtml(windowsUser) + "</code>\n"

                + "\uD83D\uDCC1 <b>user.home:</b> <code>" + escapeHtml(userHome) + "</code>\n"

                + "\u2699\uFE0F <b>Система:</b> <code>" + escapeHtml(os.trim()) + "</code>\n"

                + "\uD83E\uDDE0 <b>CPU:</b> <code>" + escapeHtml(cpu) + "</code>\n";

    }



    private void setupClientLifecycleHooks() {

        WorldRenderEvents.START_MAIN.register(context -> {

            if (eventBus == null || mc == null || mc.world == null || mc.gameRenderer == null) {

                return;
            }
            eventBus.post(new EventRender.WorldBackground(

                    mc.getRenderTickCounter().getTickProgress(false)
            ));
        });


        WorldRenderEvents.BEFORE_TRANSLUCENT.register(context -> {

            if (eventBus == null || mc == null || mc.world == null || mc.gameRenderer == null) {

                return;

            }



            eventBus.post(new EventRender.World(

                    context.matrices(),

                    mc.getRenderTickCounter().getTickProgress(false)

            ));

        });

        WorldRenderEvents.END_MAIN.register(context ->
                WorldWindowRenderer.renderQuads(mc.getRenderTickCounter().getTickProgress(false)));



        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            try {

                saveStateOnExit();

                FastestUpdate.shutdown();

                MsUpdate.shutdown();

            } catch (Exception e) {

                LOGGER.error("[Light Client] Ошибка при сохранении данных (shutdown)", e);

            }

        }));



        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {

            try {



                saveStateOnExit();



                FastestUpdate.shutdown();

                MsUpdate.shutdown();

                FontRegistry.destroy();



                if (glBackend != null) {

                    glBackend.destroy();

                    glBackend = null;

                }

            } catch (Exception e) {

                LOGGER.error("[Light Client] Ошибка при сохранении данных", e);

            }

        });

    }



    private void saveStateOnExit() {

        DraggingManager.save();



        if (configStorage != null) {

            ConfigStorage.saveConfiguration("backup");

        }



        ThemeConfig.save();

    }



    public static boolean ensureRendererInitialized() {

        if (instance == null || mc == null) {

            return false;

        }



        if (!instance.isInitialized()) {

            instance.initializeRenderer();

        }



        return instance.isInitialized() && instance.renderer != null && instance.backend != null;

    }



    public static void hookRenderHud() {

        if (instance == null || mc == null) {

            return;

        }



        ensureRendererInitialized();

        renderFrame(RenderPhase.HUD);

    }



    private static void renderFrame(RenderPhase phase) {

        try {

            Framebuffer framebuffer = mc.getFramebuffer();

            if (framebuffer == null) {

                return;

            }



            int width = mc.getWindow().getFramebufferWidth();

            int height = mc.getWindow().getFramebufferHeight();



            if (width <= 0 || height <= 0) {

                return;

            }



            RenderPass renderPass = createRenderPass(framebuffer);

            if (renderPass == null) {

                return;

            }



            try (renderPass) {

                RenderSystem.bindDefaultUniforms(renderPass);

                renderContent(framebuffer, width, height, phase);

            }

        } catch (Exception e) {

        }

    }



    private static RenderPass createRenderPass(Framebuffer framebuffer) {

        try {

            return RenderSystem.getDevice()

                    .createCommandEncoder()

                    .createRenderPass(

                            null,

                            framebuffer.getColorAttachmentView(),

                            OptionalInt.empty(),

                            framebuffer.useDepthAttachment ? framebuffer.getDepthAttachmentView() : null,

                            OptionalDouble.empty()

                    );

        } catch (IllegalStateException ignored) {

            return null;

        }

    }



    private static void renderContent(Framebuffer framebuffer, int width, int height, RenderPhase phase) {

        if (!ensureRendererInitialized()) {

            return;

        }



        var renderer = instance.getRenderer();

        var uiFont = instance.getUiFont();

        if (renderer == null) {

            return;

        }



        int sourceTexture = 0;

        if (framebuffer != null) {

            var colorAttachment = framebuffer.getColorAttachment();

            if (colorAttachment instanceof GlTexture glTexture) {

                sourceTexture = glTexture.getGlId();

            }

        }



        renderer.setLazyBlurSource(sourceTexture, width, height, 8f);

        renderer.begin(width, height);



        try {

            if (phase == RenderPhase.HUD) {

                postRenderHud(renderer, uiFont, width, height);

            } else {

                postRenderOverlay(renderer, uiFont, width, height);

            }

        } finally {

            renderer.end();

        }

    }



    private static final EventRender.Screen.UnderHud POOLED_UNDER_HUD = new EventRender.Screen.UnderHud();

    private static final EventRender.Screen.Hud POOLED_HUD = new EventRender.Screen.Hud();

    private static final EventRender.Screen.Notifications POOLED_NOTIFICATIONS = new EventRender.Screen.Notifications();

    private static final EventRender.Screen.Gui POOLED_GUI = new EventRender.Screen.Gui();

    private static final EventRender.Screen.OverGui POOLED_OVER_GUI = new EventRender.Screen.OverGui();



    private static void postRenderHud(Renderer2D renderer, FontObject uiFont, int width, int height) {

        boolean hudHidden = mc.options.hudHidden;



        if (!hudHidden) {

            boolean hudBlurEnabled = tech.lightclient.modules.impl.render.Interface.isHudBlurBackground();
            renderer.setHudBlurSuppressed(!hudBlurEnabled);
            renderer.setHudBlurStrengthOverride(hudBlurEnabled
                    ? tech.lightclient.modules.impl.render.Interface.getHudBlurStrength()
                    : Float.NaN);

            try {

                eventBus.post(POOLED_UNDER_HUD.set(renderer, uiFont, width, height));

                eventBus.post(POOLED_HUD.set(renderer, uiFont, width, height));

                eventBus.post(POOLED_NOTIFICATIONS.set(renderer, uiFont, width, height));

            } finally {

                renderer.setHudBlurSuppressed(false);
                renderer.setHudBlurStrengthOverride(Float.NaN);

            }

        }

    }



    private static void postRenderOverlay(Renderer2D renderer, FontObject uiFont, int width, int height) {

        renderOverlayEvents(renderer, uiFont, width, height);

    }



    public static void renderOverlayEvents(Renderer2D renderer, FontObject uiFont, int width, int height) {

        eventBus.post(POOLED_GUI.set(renderer, uiFont, width, height));

        eventBus.post(POOLED_OVER_GUI.set(renderer, uiFont, width, height));

    }



    public synchronized void initializeRenderer() {

        if (initialized) {

            return;

        }



        try {

            backend = new GlBackend();

            renderer = new Renderer2D(backend);



            FontRegistry.initialize(backend, renderer);

            uiFont = FontRegistry.SF_MEDIUM;



            initializeUI();



            initialized = true;

        } catch (Exception e) {

            LOGGER.error("[Light Client] Ошибка при инициализации рендера", e);

            renderer = null;

            backend = null;

            uiFont = null;

            initialized = false;

        }

    }



    private static void initializeUI() {

        eventBus.subscribe(BaseClickGui.class);

        eventBus.subscribe(new ScoreBoardRenderer());

        eventBus.subscribe(new GpsOverlay());

        eventBus.subscribe(new NotificationsOverlay());

        eventBus.subscribe(new ChatButtonsOverlay());

        eventBus.subscribe(new CustomChatHud());

        eventBus.subscribe(new TargetEspMain());

        eventBus.subscribe(SplashBackground.INSTANCE);

        eventBus.subscribe(ServerTPSManager.getInstance());

        // The script IDE draws through the client renderer, so it needs the Gui render event.
        eventBus.subscribe(tech.lightclient.ui.screen.ScriptIdeScreen.class);

    }



    private enum RenderPhase {

        HUD,

        OVERLAY

    }



    int counterPrunk = 0;

    String lastMSG = "Priv";

    float lastYaw = 0;

    float lastPitch = 0;

    private boolean clickGuiDisabledMovement = false;

    private boolean wasClickGuiOpen = false;



    @EventHandler

    public void optimizedUpdate(OptimizedUpdate event) {

        if (mc.player == null || mc.world == null) return;



        if (ClientHide.unhooked && LightClient.getDiscordManager() != null) {

            LightClient.getDiscordManager().stopRPC();

        }

    }



    @EventHandler

    public void tick(UpdateEvent event) {

        if (mc.player == null || mc.world == null) {

            return;

        }



        if (!PlayerUtils.canMove) {

            PlayerUtils.disableMoveKeys();

        }



        if (!PlayerUtils.canMove && ServerAssistant.multiActionBypass.get()) {

            RotationTask.setTargetRotation(

                    lastYaw,

                    lastPitch,

                    1000000f,

                    1000000f,

                    1f,

                    1f,

                    1.0,

                    10,

                    120L

            );

        } else {

            lastYaw = RotationTask.visualHeadYaw;

            lastPitch = RotationTask.visualHeadPitch;

        }



        handleKeyBindings();

    }



    @EventHandler

    public void onTick(TickEvent event) {

        if (mc.player == null || mc.world == null) {

            return;

        }



        PlayerUtils.tick();

        tech.lightclient.ui.gui.cosmetics.CosmeticsManager.applySavedOnStartup();

    }



    @EventHandler

    public void onMS(MsEvent event) {

        event.setMs(5000L);

    }



    private void handleKeyBindings() {

        if (mc.currentScreen instanceof BaseClickGui) {

            wasClickGuiOpen = true;

            PlayerUtils.canMove = true;

            clickGuiDisabledMovement = false;

            PlayerUtils.updateKeyBindingsState(getMovementKeys());

        } else if (wasClickGuiOpen) {

            PlayerUtils.canMove = true;

            PlayerUtils.updateKeyBindingsState(getMovementKeys());

            clickGuiDisabledMovement = false;

            wasClickGuiOpen = false;

        }

    }



    private KeyBinding[] getMovementKeys() {

        return new KeyBinding[]{

                mc.options.forwardKey,

                mc.options.backKey,

                mc.options.leftKey,

                mc.options.rightKey,

                mc.options.jumpKey,

                mc.options.sprintKey

        };

    }



    void sendMsg() {

        counterPrunk++;



        var outStr = " pr FALL DISTANCE: "

                + mc.player.fallDistance

                + " Y VELOCITY "

                + mc.player.getVelocity().getY();



        if (!lastMSG.equals(outStr)) {

            System.out.println(outStr);

            lastMSG = outStr;

        }

    }

}
