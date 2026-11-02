package releon.ru.api.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import lightning.sdk.LightningSDK;
import lightning.sdk.annotation.LgVM;
import lightning.sdk.annotation.LgVM.Level;
import net.minecraft.client.MinecraftClient;
import releon.ru.api.events.annotation.SubscribeEvent;
import releon.ru.api.events.bus.EventBus;
import releon.ru.api.events.impl.TickEvent;
import releon.ru.api.module.impl.combat.AimAssist;
import releon.ru.api.module.impl.combat.AntiBot;
import releon.ru.api.module.impl.combat.AuraModule;
import releon.ru.api.module.impl.combat.AutoCrystal;
import releon.ru.api.module.impl.combat.AutoGApple;
import releon.ru.api.module.impl.combat.AutoMaceSwap;
import releon.ru.api.module.impl.combat.AutoSwap;
import releon.ru.api.module.impl.combat.AutoTotem;
import releon.ru.api.module.impl.combat.BackTracks;
import releon.ru.api.module.impl.combat.Criticals;
import releon.ru.api.module.impl.combat.HitBoxModule;
import releon.ru.api.module.impl.combat.HitSound;
import releon.ru.api.module.impl.combat.NoFriendDamage;
import releon.ru.api.module.impl.combat.NoInteract;
import releon.ru.api.module.impl.combat.ShiftTap;
import releon.ru.api.module.impl.combat.TriggerBot;
import releon.ru.api.module.impl.combat.Velocity;
import releon.ru.api.module.impl.display.Appearance;
import releon.ru.api.module.impl.misc.AutoAuth;
import releon.ru.api.module.impl.misc.AutoDuel;
import releon.ru.api.module.impl.misc.AutoEvent;
import releon.ru.api.module.impl.misc.AutoLeave;
import releon.ru.api.module.impl.misc.AutoLes;
import releon.ru.api.module.impl.misc.AutoMine;
import releon.ru.api.module.impl.misc.AutoTpAccept;
import releon.ru.api.module.impl.misc.ClientSounds;
import releon.ru.api.module.impl.misc.DeathCoords;
import releon.ru.api.module.impl.misc.ElytraHelper;
import releon.ru.api.module.impl.misc.ExpCrafter;
import releon.ru.api.module.impl.misc.JoinerHelper;
import releon.ru.api.module.impl.misc.ServerHelper;
import releon.ru.api.module.impl.misc.ServerRPSpoofer;
import releon.ru.api.module.impl.misc.UseTracker;
import releon.ru.api.module.impl.movement.AirStuck;
import releon.ru.api.module.impl.movement.AutoSprint;
import releon.ru.api.module.impl.movement.ElytraTarget;
import releon.ru.api.module.impl.movement.Fly;
import releon.ru.api.module.impl.movement.InventoryMove;
import releon.ru.api.module.impl.movement.NoSlow;
import releon.ru.api.module.impl.movement.NoWeb;
import releon.ru.api.module.impl.movement.Scaffold;
import releon.ru.api.module.impl.movement.Speed;
import releon.ru.api.module.impl.movement.Spider;
import releon.ru.api.module.impl.movement.Strafe;
import releon.ru.api.module.impl.movement.SuperFireWork;
import releon.ru.api.module.impl.movement.TargetStrafe;
import releon.ru.api.module.impl.movement.Timer;
import releon.ru.api.module.impl.player.AHHelper;
import releon.ru.api.module.impl.player.AntiDrop;
import releon.ru.api.module.impl.player.AutoBuy;
import releon.ru.api.module.impl.player.AutoRespawn;
import releon.ru.api.module.impl.player.AutoTool;
import releon.ru.api.module.impl.player.AutoUse;
import releon.ru.api.module.impl.player.AutoWarden;
import releon.ru.api.module.impl.player.ChestStealer;
import releon.ru.api.module.impl.player.ClanUpgrade;
import releon.ru.api.module.impl.player.ClickPearl;
import releon.ru.api.module.impl.player.ExpFarm;
import releon.ru.api.module.impl.player.FastBreak;
import releon.ru.api.module.impl.player.FreeCam;
import releon.ru.api.module.impl.player.FreeLook;
import releon.ru.api.module.impl.player.NameProtect;
import releon.ru.api.module.impl.player.NoDelay;
import releon.ru.api.module.impl.player.NoEntityTrace;
import releon.ru.api.module.impl.player.NoPush;
import releon.ru.api.module.impl.player.OpenWalls;
import releon.ru.api.module.impl.player.SpConnect;
import releon.ru.api.module.impl.player.WindJump;
import releon.ru.api.module.impl.visual.Ambience;
import releon.ru.api.module.impl.visual.Animations;
import releon.ru.api.module.impl.visual.Arrows;
import releon.ru.api.module.impl.visual.AspectRatio;
import releon.ru.api.module.impl.visual.BlockESP;
import releon.ru.api.module.impl.visual.BlockOverlay;
import releon.ru.api.module.impl.visual.DashTrail;
import releon.ru.api.module.impl.visual.ESP;
import releon.ru.api.module.impl.visual.FogBlur;
import releon.ru.api.module.impl.visual.GlassHands;
import releon.ru.api.module.impl.visual.GoldHPIndicator;
import releon.ru.api.module.impl.visual.HolMyItems;
import releon.ru.api.module.impl.visual.Hud;
import releon.ru.api.module.impl.visual.ItemPhysics;
import releon.ru.api.module.impl.visual.JumpCircle;
import releon.ru.api.module.impl.visual.KillEffect;
import releon.ru.api.module.impl.visual.NoRender;
import releon.ru.api.module.impl.visual.Particles;
import releon.ru.api.module.impl.visual.Predictions;
import releon.ru.api.module.impl.visual.RichDog;
import releon.ru.api.module.impl.visual.SeeInvisible;
import releon.ru.api.module.impl.visual.ShaderHand;
import releon.ru.api.module.impl.visual.SwingAnimation;
import releon.ru.api.module.impl.visual.TargetESP;
import releon.ru.api.module.impl.visual.ViewModel;
import releon.ru.api.module.impl.visual.WardenHelper;
import releon.ru.api.settings.bind.KeyBind;
import releon.ru.utils.inventory.InventoryFlowManager;
import releon.ru.utils.network.Network;

public final class ModuleManager {
   private final EventBus eventBus;
   private final List<Module> modules = new ArrayList<>();
   private final Map<String, Module> byName = new HashMap<>();
   private final Map<Class<? extends Module>, Module> byType = new HashMap<>();
   private final Map<ModuleCategory, List<Module>> byCategory = new EnumMap<>(ModuleCategory.class);
   private final Map<ModuleCategory, List<Module>> byCategoryViews = new EnumMap<>(ModuleCategory.class);
   private Collection<Module> modulesView = List.of();
   private final Map<Module, Boolean> bindStates = new IdentityHashMap<>();
   private Runnable dirtyListener = () -> {
   };

   public ModuleManager(EventBus eventBus) {
      this.eventBus = eventBus;
   }

   @LgVM(Level.FULL)
   public void init() {
      if (LightningSDK.checkAuthAlive()) {
         this.registerDefaults();
         this.eventBus.register(this);
      }
   }

   public void setDirtyListener(Runnable dirtyListener) {
      this.dirtyListener = dirtyListener == null ? () -> {
      } : dirtyListener;
   }

   public void register(Module module) {
      String key = this.normalize(module.getName());
      if (!this.byName.containsKey(key)) {
         this.modules.add(module);
         this.byName.put(key, module);
         this.byType.putIfAbsent((Class<? extends Module>)module.getClass(), module);
         this.byCategory.computeIfAbsent(module.getCategory(), ignored -> new ArrayList<>()).add(module);
         module.addStateListener(changedModule -> this.dirtyListener.run());
         module.initialize(this.eventBus);
         this.sortViews();
      }
   }

   public Collection<Module> getModules() {
      return this.modulesView;
   }

   public List<Module> getByCategory(ModuleCategory category) {
      return this.byCategoryViews.getOrDefault(category, List.of());
   }

   public Optional<Module> getByName(String name) {
      return Optional.ofNullable(this.byName.get(this.normalize(name)));
   }

   public <T extends Module> Optional<T> getByType(Class<T> type) {
      Module module = this.byType.get(type);
      if (type.isInstance(module)) {
         return Optional.of(type.cast(module));
      } else {
         for (Module candidate : this.modules) {
            if (type.isInstance(candidate)) {
               return Optional.of(type.cast(candidate));
            }
         }

         return Optional.empty();
      }
   }

   public boolean isEnabled(Class<? extends Module> type) {
      return this.getByType(type).filter(Module::isEnabled).isPresent();
   }

   public Optional<AuraModule> getAura() {
      return this.getByType(AuraModule.class);
   }

   public Optional<AuraModule> getKillAura() {
      return this.getAura();
   }

   public Optional<HitBoxModule> getHitBox() {
      return this.getByType(HitBoxModule.class);
   }

   public boolean isHitBoxEnabled() {
      return this.isEnabled(HitBoxModule.class);
   }

   public Optional<NoInteract> getNoInteract() {
      return this.getByType(NoInteract.class);
   }

   public boolean isNoInteractEnabled() {
      return this.isEnabled(NoInteract.class);
   }

   public Optional<ServerRPSpoofer> getServerRPSpoofer() {
      return this.getByType(ServerRPSpoofer.class);
   }

   public boolean isServerRPSpooferEnabled() {
      return this.isEnabled(ServerRPSpoofer.class);
   }

   @SubscribeEvent
   private void onPreTick(TickEvent.Pre event) {
      InventoryFlowManager.update();
   }

   @SubscribeEvent
   private void onTick(TickEvent.Post event) {
      MinecraftClient client = event.getClient();
      Network.tick();
      this.handleBinds(client);

      for (Module module : this.modules) {
         if (module.isEnabled()) {
            module.onTick(client);
         }
      }
   }

   @LgVM(Level.FULL)
   private void registerDefaults() {
      if (LightningSDK.checkAuthAlive()) {
         this.register(new Appearance());
         this.register(new AntiBot());
         this.register(new AimAssist());
         this.register(new AuraModule());
         this.register(new BackTracks());
         this.register(new AutoCrystal());
         this.register(new AutoGApple());
         this.register(new AutoSwap());
         this.register(new Criticals());
         this.register(new Animations());
         this.register(new AntiDrop());
         this.register(new ExpCrafter());
         this.register(new ShaderHand());
         this.register(new ExpFarm());
         this.register(new AutoMine());
         this.register(new WindJump());
         this.register(new AutoWarden());
         this.register(new AutoBuy());
         this.register(new GoldHPIndicator());
         this.register(new AutoMaceSwap());
         this.register(new RichDog());
         this.register(new JumpCircle());
         this.register(new AutoTotem());
         this.register(new HitBoxModule());
         this.register(new WardenHelper());
         this.register(new HitSound());
         this.register(new NoFriendDamage());
         this.register(new NoInteract());
         this.register(new TriggerBot());
         this.register(new Velocity());
         this.register(new Scaffold());
         this.register(new ShiftTap());
         this.register(new AutoSprint());
         this.register(new Fly());
         this.register(new ElytraTarget());
         this.register(new NoWeb());
         this.register(new NoSlow());
         this.register(new InventoryMove());
         this.register(new Spider());
         this.register(new Speed());
         this.register(new Strafe());
         this.register(new SuperFireWork());
         this.register(new TargetStrafe());
         this.register(new AirStuck());
         this.register(new Timer());
         this.register(new Ambience());
         this.register(new HolMyItems());
         this.register(new Arrows());
         this.register(new AspectRatio());
         this.register(new DashTrail());
         this.register(new BlockESP());
         this.register(new ESP());
         this.register(new FogBlur());
         this.register(new GlassHands());
         this.register(new ShaderHand());
         this.register(new Hud());
         this.register(new ItemPhysics());
         this.register(new KillEffect());
         this.register(new Particles());
         this.register(new Predictions());
         this.register(new SeeInvisible());
         this.register(new SwingAnimation());
         this.register(new ViewModel());
         this.register(new NoRender());
         this.register(new TargetESP());
         this.register(new BlockOverlay());
         this.register(new OpenWalls());
         this.register(new NoPush());
         this.register(new NoEntityTrace());
         this.register(new NoDelay());
         this.register(new FastBreak());
         this.register(new NameProtect());
         this.register(new FreeLook());
         this.register(new FreeCam());
         this.register(new AHHelper());
         this.register(new AutoRespawn());
         this.register(new AutoTool());
         this.register(new AutoUse());
         this.register(new ChestStealer());
         this.register(new ClanUpgrade());
         this.register(new SpConnect());
         this.register(new JoinerHelper());
         this.register(new ServerRPSpoofer());
         this.register(new AutoAuth());
         this.register(new AutoDuel());
         this.register(new AutoEvent());
         this.register(new AutoLeave());
         this.register(new AutoTpAccept());
         this.register(new AutoLes());
         this.register(new ClientSounds());
         this.register(new ClickPearl());
         this.register(new DeathCoords());
         this.register(new ElytraHelper());
         this.register(new ServerHelper());
         this.register(new UseTracker());
      }
   }

   private void handleBinds(MinecraftClient client) {
      if (client != null && client.getWindow() != null && client.currentScreen == null) {
         long handle = client.getWindow().getHandle();

         for (Module module : this.modules) {
            KeyBind bind = module.getBind();
            boolean down = bind.isDown(handle);
            boolean wasDown = this.bindStates.getOrDefault(module, false);
            if (down && !wasDown) {
               module.toggle();
            }

            this.bindStates.put(module, down);
         }
      } else {
         this.bindStates.clear();
      }
   }

   private void register(Module module, String name, String description, ModuleCategory category) {
      module.configure(name, description, category);
      this.register(module);
   }

   private void sortViews() {
      this.modules.sort(Comparator.comparing(module -> module.getName().toLowerCase(Locale.ROOT)));

      for (List<Module> categoryModules : this.byCategory.values()) {
         categoryModules.sort(Comparator.comparing(module -> module.getName().toLowerCase(Locale.ROOT)));
      }

      this.modulesView = Collections.unmodifiableList(this.modules);
      this.byCategoryViews.clear();

      for (Entry<ModuleCategory, List<Module>> entry : this.byCategory.entrySet()) {
         this.byCategoryViews.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
      }
   }

   private String normalize(String name) {
      return name == null ? "" : name.toLowerCase(Locale.ROOT).replace(" ", "");
   }
}
