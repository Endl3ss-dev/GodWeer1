package net.skycore.client;

import java.nio.file.Path;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.skycore.client.account.AccountManager;
import net.skycore.client.bot.BedWarsBot;
import net.skycore.client.bot.BotConfig;
import net.skycore.client.command.CommandManager;
import net.skycore.client.config.ConfigManager;
import net.skycore.client.config.MacroManager;
import net.skycore.client.config.NameList;
import net.skycore.client.event.ClientTickEvent;
import net.skycore.client.event.EventBus;
import net.skycore.client.gui.decide.DecideGuiMovement;
import net.skycore.client.gui.decide.DecideMenuScreen;
import net.skycore.client.gui.menu.MenuPrefs;
import net.skycore.client.gui.menu.MenuScreen;
import net.skycore.client.gui.workspace.WorkspaceScreen;
import net.skycore.client.hud.HudRenderer;
import net.skycore.client.integration.DiscordStatus;
import net.skycore.client.module.BindManager;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.KeyBinds;
import net.skycore.client.module.ModuleManager;
import net.skycore.client.module.combat.CrystalAuraModule;
import net.skycore.client.module.combat.ElytraTargetModule;
import net.skycore.client.module.render.Box3DModule;
import net.skycore.client.module.render.ClickGUIModule;
import net.skycore.client.module.render.CometsModule;
import net.skycore.client.module.render.CubesModule;
import net.skycore.client.module.render.DuplisheModule;
import net.skycore.client.module.render.DynamicSurroundingsModule;
import net.skycore.client.module.render.JumpCirclesModule;
import net.skycore.client.module.render.PredictionModule;
import net.skycore.client.module.render.ThunderShatterModule;
import net.skycore.client.module.render.TrailsModule;
import net.skycore.client.module.render.WorldTweaksModule;
import net.skycore.client.theme.ThemeManager;
import net.skycore.client.util.WorldSkyRenderer;
import net.skycore.client.util.RenderGuiHelper;
import net.skycore.client.util.SelectAndSaveHelper;
import net.skycore.client.util.SpoofElytraAsChestplateHelper;
import net.skycore.client.util.SynchronizeHelper;
import net.skycore.client.waypoint.WaypointManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SkycoreClient implements ClientModInitializer {
   private static final EventBus EVENTS = new EventBus();
   private static ConfigManager config;
   private static final ThemeManager THEMES = new ThemeManager();
   private static final WaypointManager WAYPOINTS = new WaypointManager();
   public static final String MOD_ID = "skycore";
   public static final Logger LOGGER = LoggerFactory.getLogger("skycore");
   private static HudRenderer interfaceRenderer;
   private static final DiscordStatus DISCORD = new DiscordStatus();
   private static final MacroManager MACROS = new MacroManager();
   private static final BindManager BINDS = new BindManager();
   private static CommandManager commands;
   private static final NameList STAFF = new NameList();
   private static final NameList CUSTOM_NAMES = new NameList();
   private static final ModuleManager MODULES = new ModuleManager();

   public static MacroManager macros() {
      return MACROS;
   }

   public static HudRenderer interfaceRenderer() {
      return interfaceRenderer;
   }

   public static ModuleManager modules() {
      return MODULES;
   }

   public static WaypointManager waypoints() {
      return WAYPOINTS;
   }

   public static NameList staff() {
      return STAFF;
   }

   @Override
   public void onInitializeClient() {
      MODULES.registerDefaults();
      SelectAndSaveHelper.initialize(Path.of("skycore/beta-language.json"));
      AccountManager.INSTANCE.initialize(Path.of("skycore/beta-alts.json"));
      config = new ConfigManager(MODULES, THEMES, Path.of("skycore/beta-local.json"));
      // Оформление перенесённого меню живёт отдельным файлом: общий конфиг описывает
      // модули, их настройки и темы, и менять его формат ради меню не нужно.
      MenuPrefs.load();
      // Настройки бота BedWars — тоже отдельный файл (skycore/bot.json): бот не модуль,
      // поэтому в формат пользовательских конфигов он не подмешивается.
      BotConfig.load();
      commands = new CommandManager();
      interfaceRenderer = new HudRenderer(MODULES, config);
      Minecraft instance = Minecraft.getInstance();
      ClickGUIModule clickGUIModule = MODULES.get(ClickGUIModule.class).orElseThrow();
      clickGUIModule.setOpenAction(() -> {
         // Workspace — основное меню: ветка по умолчанию (и всё, что не названо явно
         // внутри него) открывает его, а не старый экран.
         if (clickGUIModule.menuStyle().is("Decide")) {
            instance.setScreenAndShow(new DecideMenuScreen(MODULES, config, clickGUIModule));
         } else if (clickGUIModule.menuStyle().is("Skycore")) {
            instance.setScreenAndShow(new RenderGuiHelper(MODULES, THEMES, config, clickGUIModule));
         } else if (clickGUIModule.menuStyle().is("Swerve")) {
            instance.setScreenAndShow(new MenuScreen(MODULES, config));
         } else {
            openWorkspace(instance);
         }
      });
      ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);
      var targetEspRenderer = MODULES.get(net.skycore.client.module.render.TargetESPModule.class).orElseThrow();
      var petRenderer = MODULES.get(net.skycore.client.module.render.PetsModule.class).orElseThrow();
      LevelRenderEvents.END_MAIN.register(levelRenderContext -> {
         Minecraft instance2 = Minecraft.getInstance();
         CameraRenderState cameraRenderState = instance2.gameRenderer.gameRenderState().levelRenderState.cameraRenderState;
         WorldTweaksModule worldTweaksModule = WorldTweaksModule.enabled();
         if (worldTweaksModule == null || !worldTweaksModule.usesSky()) {
            WorldSkyRenderer.INSTANCE.render(cameraRenderState);
         }

         if (worldTweaksModule != null) {
            worldTweaksModule.renderSkyOverlay(cameraRenderState);
         }

         targetEspRenderer.renderWorld();
         MODULES.get(CubesModule.class).ifPresent(cubesModule -> {
            cubesModule.renderWorld();
         });
         MODULES.get(CometsModule.class).ifPresent(cometsModule -> cometsModule.renderWorld());
         MODULES.get(DynamicSurroundingsModule.class).ifPresent(dynamicSurroundingsModule -> {
            dynamicSurroundingsModule.renderWorld();
         });
         MODULES.get(Box3DModule.class).ifPresent(box3DModule -> {
            box3DModule.renderWorld();
         });
         MODULES.get(ThunderShatterModule.class).ifPresent(thunderShatterModule -> {
            thunderShatterModule.renderWorld();
         });
         MODULES.get(JumpCirclesModule.class).ifPresent(jumpCirclesModule -> {
            jumpCirclesModule.renderWorld();
         });
         MODULES.get(TrailsModule.class).ifPresent(trailsModule -> {
            trailsModule.renderWorld();
         });
         PredictionModule.INSTANCE.renderWorld();
         petRenderer.renderWorld();
         MODULES.get(DuplisheModule.class).ifPresent(duplisheModule -> {
            duplisheModule.renderWorld();
         });
         CrystalAuraModule.INSTANCE.renderWorld();
         ElytraTargetModule.INSTANCE.renderPredictCube();
      });
      config.load();
   }

   /**
    * Открывает основное меню — Workspace.
    *
    * <p>Вынесено отдельным методом ради одной страховки: если экран не удалось
    * построить (например, из-за несовместимого состояния настроек), клиент не должен
    * оставаться без интерфейса вообще — тогда открывается старый экран, который умеет
    * всё остальное. Исключение при этом логируется, а не глотается молча.
    */
   private void openWorkspace(Minecraft instance) {
      try {
         instance.setScreenAndShow(new WorkspaceScreen(MODULES, config));
      } catch (RuntimeException failure) {
         LOGGER.error("Workspace menu failed to open, falling back to the legacy screen", failure);
         ClickGUIModule clickGui = MODULES.get(ClickGUIModule.class).orElse(null);
         instance.setScreenAndShow(clickGui == null
                 ? new MenuScreen(MODULES, config)
                 : new RenderGuiHelper(MODULES, THEMES, config, clickGui));
      }
   }

   private void onEndTick(Minecraft minecraft) {
      SpoofElytraAsChestplateHelper.tick();
      SynchronizeHelper.synchronize();
      // Привод сценария бота BedWars. Сейчас метод пуст (заглушка), но вызывается
      // именно отсюда: настоящая реализация будет опрашивать подключения и переводить
      // ботов между этапами, и для этого ей нужен один такт на клиентский тик.
      BedWarsBot.INSTANCE.tick();
      if (minecraft.gui.screen() == null) {
         long windowHandle = minecraft.getWindow().handle();

         for (ClientModule clientModule : MODULES.getModules()) {
            int keyBind = clientModule.getKeyBind();
            if (keyBind >= 0 && !KeyBinds.isPrimaryMouseButton(keyBind)) {
               BINDS.update(clientModule, KeyBinds.isPressed(windowHandle, keyBind));
            }
         }
      }

      MODULES.tickEnabled();
      // «Бегать с открытым GUI» у Decide-меню: экран не забирает клавиши движения,
      // поэтому их состояние подставляется из GLFW на каждом клиентском такте.
      DecideGuiMovement.tick(minecraft, MODULES.get(ClickGUIModule.class).orElse(null));
      EVENTS.post(new ClientTickEvent(minecraft));
   }

   public static ThemeManager themes() {
      return THEMES;
   }

   public static NameList customNames() {
      return CUSTOM_NAMES;
   }

   public static EventBus events() {
      return EVENTS;
   }

   public static CommandManager commands() {
      return commands;
   }

   public static DiscordStatus discord() {
      return DISCORD;
   }

   public static ConfigManager config() {
      return config;
   }
}
