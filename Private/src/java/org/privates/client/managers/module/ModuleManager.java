package org.privates.client.managers.module;


import org.privates.client.Excellent;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.input.KeyboardPressEvent;
import org.privates.client.managers.events.input.MousePressEvent;
import org.privates.client.managers.module.impl.client.FlatGui;
import org.privates.client.managers.module.impl.client.Targets;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.combat.*;
import org.privates.client.managers.module.impl.combat.Velocity;
import org.privates.client.managers.module.impl.misc.*;
import org.privates.client.managers.module.impl.movement.*;
import org.privates.client.managers.module.impl.player.*;
import org.privates.client.managers.module.impl.player.FakePlayer;
import org.privates.client.managers.module.impl.render.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public final class ModuleManager extends LinkedHashMap<Class<? extends Module>, Module> {

    //так зачем? я на экселлент уже аннотации накидывал
    // они были на приват бае а это экселлент, ну так на экселленте они накинуты... я вчеоа на анидеске фусурту накидывал это сурсы приват клиента он на базе экселлента я бля чё кодер экселлента по вашему

    public void init() {
        add(
                new KTLeave(),
                new DuelAssist(),
                new WaterSpeed(),
                new LegitAura(),
                new ElytraFix(),
                new AutoCrystal(),
                new HitEffect(),
                new ElytraMotion(),
                new WaterSpider(),
                new Radar(),
                new ItemESP(),
                new ScoreboardHealth(),
                new ScaffoldWalk(),
                new NeuroSelf(),
                new CrystalSafe(),
                new EventTracker(),
                new SlothAura(),
                new AutoCard(),
                new CrystalAura(),
                new UseTracker(),
                new SoulFarm(),
                new PotionDetector(),
                new AutoBuyHW(),
                new TpAura(),
                new AutoSellPotion(),
                new SaturationGame(),
                new WavePlayer(),
                new Criticals(),
                new BackTrack(),
                new InvSave(),
                new SelfRotation(),
                new TargetEsp(),
                new AutoPlantsFarm(),
                new AppleFarm(),
                new AutoWeb(),
                new AutoSellSpoky(),
                new RotHitBox(),
                new NameTags2(),
                new LineGlyphs(),
                new ComboCounter(),
                new AfterSlash(),
                new GroundAura(),
                new MotionBlur(),
                new AutoTrap(),
                new TEST(),
                new AutoBrew2(),
                new ChatSettings(),
                new FreeLoock(),
                new TabSettings(),
                new MinecraftSettings(),
                new ClickGuiSettings(),
                new CometParticles(),
                new SRPSpoof(),
                new PortalGodMode(),
                new SpookyFarm(),
                new Nametags(),
                new FireWorkEsp(),
                new Wings(),
                new Nimb(),
                new PlayerBox(),
                new CrystalDmgEsp(),
                new AuraNew(),
                new KillMessage(),
                new FakeMessage(),
                new NotifyHelper(),
                new MultiServerHelper(),
                new TargetPearl(),
                new FakePlayer(),
                new ItemPhysic(),
                new ArenaLeave(),
                new Kagune(),
                new ItemTP(),
                new AutoSetupSpooky(),
                new AutoBrew(),
                new MineHelper(),
                new MotionElytraFlySunrise(),
                new BabyBoy(),
                new HighJump(),
                new ElytraPredictRender(),
                new WorldLines(),
                new DamageIndicator(),
                new AutoInventoryBuy(),
                new AutoBuySpooky(),
                new AutoLeave(),
                new ShotFireWorks(),
                new Globals(),
                new Torus(),
                new NoInteract(),
                new Velocity(),
                new ShaderEsp(),
                new BaseFinderPlus(),
                new AucHelper(),
                new PotionTrajectory(),
                new ElytraTweaks(),
                new ElytraFly(),
                new ElytraJump(),
                new Companion(),
                new HWSpammer(),
                new PigFly(),
                new AutoEat(),
                new GrimGlide(),
                new FlatGui(),
                new CustomModel(),
                new ChunkAnimator(),
                new TriggerBot(),
                new FarmerFinder(),
                new AimAssist(),
                new AntiAFK(),
                new FullBright(),
                new GlassHands(),
                new ElytraBoost(),
                new AutoArmor(),
                new AspectRatio(),
                new EggMan(),
                new BlockOutline(),
                new CoordCube(),
                new Cube3D(),
                new Strafe(),
                new HitBox(),
                new TapeMouse(),
                new ShulkerViewer(),
                new Parkour(),
                new AutoTool(),
                new AutoSoup(),
                new AutoRespawn(),
                new DeathCoords(),
                new AutoFish(),
                new JumpCircle(),
                new TNTTimer(),
                new AutoDodge(),
                new Trails(),
                new ChinaHat(),
                new InventoryHighlight(),
                new HitColor(),
                new CustomHand(),
                new ItemAnimation(),
                new CustomWorld(),
                new NoFriendDamage(),
                new Notifications(),
                new Optimizer(),
                new NoPush(),
                new AutoSwap(),
                new AntiBot(),
                new FireFly(),
                new NoSlow(),
                new NameProtect(),
                new AutoPotion(),
                new AncientBot(),
                new AutoGapple(),
                new ArmorDurability(),
                new AutoTotem(),
                new NoEntityTrace(),
                new AutoAuth(),
                new AutoTpAccept(),
                new BetterMinecraft(),
                new CustomCamera(),
                new FunTimeAssist(),
                new ItemScroller(),
                new SeeInvisibles(),
                new LongJump(),
                new Flight(),
                new Sprint(),
                new FreeCam(),
                new ProjectileHelper(),
                new ClickTP(),
                new Timer(),
                new SuperBow(),
                new Phase(),
                new BoatControl(),
                new NoDelay(),
                new WellMineHelper(),
                new Hud(),
                new TeleportBack(),
                new ClientPlayerHud(),
                new Arrows(),
                new AutoJoin(),
                new CrystalOptimizer(),
                new Jesus(),
                new NoRender(),
                new MoneyFarm(),
                new SexAura(),
                new NoDropSlot(),
                new DiscordRPCModule(),
                new Particles(),
                new AutoPilot(),
                new Timer(),
                new Projectiles(),
                new Esp(),
                new ScreenWalk(),
                new Targets(),
                new AirPlace(),
                new HolyWorldAssist(),
                new ContainerStealer(),
                new ClanUpgrade(),
                new FastBreak(),
                new NoFall(),
                new GodExploit(),
                new TargetStrafe(),
                new Nuker(),
                new AutoLes(),
                new Freezing(),
                new ElytraHelper(),
                new Sneak(),
                new ContainerESP(),
                new ClanInvestor(),
                new InventoryCleaner(),
                new ChatSkammer(),
                new ClickAction(),
                new AntiHunger(),
                new AutoMiner(),
                new AutoDiamondMiner(),
                new XRay(),
                new SkeletonEsp(),
                new Speed(),
                new InvisibleFarmSell(),
                new CoordDropper(),
                new DragonFly(),
                new EnderChestPlus(),
                new AutoBow(),
                new ElytraRecast(),
                new Spider(),
                new FakeLag(),
                new AutoFarm(),
                new AutoHeal(),
                new Theme()

        );

        this.values().stream().filter(Module::isAutoEnabled).forEach(module -> module.setEnabled(true, false));

        Excellent.eventHandler().subscribe(this);
    }


    public void add(Module... modules) {
        Arrays.stream(modules).forEach(module -> this.put(module.getClass(), module));
    }

    public void unregister(Module... modules) {
        Arrays.stream(modules).forEach(module -> this.remove(module.getClass()));
    }

    @EventHandler
    public void onKeyboardPress(KeyboardPressEvent event) {
        if (event.getScreen() == null) {
            this.values().stream().filter(module -> module.getKey() == event.getKey()).forEach(Module::toggle);
        }
    }

    @EventHandler
    public void onMousePress(MousePressEvent event) {
        if (event.getScreen() == null) {
            this.values().stream().filter(module -> module.getKey() == event.getKey()).forEach(Module::toggle);
        }
    }

    public List<Module> getModules(Category category) {
        return values().stream()
                .filter(module -> module.getCategory() == category)
                .collect(Collectors.toList());
    }

    public <T extends Module> T get(final String name) {
        return this.values().stream()
                .filter(module -> module.getName().equalsIgnoreCase(name))
                .map(module -> (T) module)
                .findFirst()
                .orElse(null);
    }

    public <T extends Module> T get(final Class<T> clazz) {
        return this.values().stream()
                .filter(module -> clazz.isAssignableFrom(module.getClass()))
                .map(clazz::cast)
                .findFirst()
                .orElse(null);
    }

    public List<Module> get(final Category category) {
        return this.values().stream()
                .filter(module -> module.getCategory() == category)
                .collect(Collectors.toList());
    }
}
