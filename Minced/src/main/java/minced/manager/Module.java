/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  sLM.4ZXYEo
 */
package minced.manager;

import minced.core.EventHandler;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import minced.core.BooleanSetting;
import minced.core.SoundPlayer;
import minced.core.Setting;
import minced.core.NotificationManager;
import minced.core.EventBus;
import minced.core.EventKey;
import minced.core.Minced;
import minced.enums.KeyCode;
import minced.enums.TOGGLE;
import minced.module.combat.AimAssist;
import minced.module.combat.AimBot;
import minced.module.player.AimingItems;
import minced.module.movement.AirJump;
import minced.module.movement.AirStuck;
import minced.module.visual.Ambience;
import minced.module.player.AntiAFK;
import minced.module.combat.AntiBot;
import minced.module.misc.AntiCrash;
import minced.module.visual.ArmorDurabilityView;
import minced.module.visual.Arrows;
import minced.module.visual.AspectRatio;
import minced.module.combat.AttackAura;
import minced.module.misc.AucHelper;
import minced.module.misc.AutoAccept;
import minced.module.combat.AutoAnchor;
import minced.module.player.AutoArmor;
import minced.module.combat.AutoCrystal;
import minced.module.player.AutoEat;
import minced.module.misc.AutoExchange;
import minced.module.combat.AutoExplosion;
import minced.module.player.AutoFarm;
import minced.module.player.AutoFish;
import minced.module.combat.AutoGApple;
import minced.module.player.AutoJoiner;
import minced.module.movement.AutoJump;
import minced.module.player.AutoLeave;
import minced.module.combat.AutoMace;
import minced.module.combat.AutoMinecart;
import minced.module.combat.AutoPotion;
import minced.module.player.AutoRespawn;
import minced.module.misc.AutoShulker;
import minced.module.combat.AutoSwap;
import minced.module.player.AutoTool;
import minced.module.combat.AutoTotem;
import minced.module.combat.AutoTrap;
import minced.module.combat.BackTrack;
import minced.module.movement.BedrockClip;
import minced.module.movement.Blink;
import minced.module.visual.BlockESP;
import minced.module.visual.BlockOverlay;
import minced.module.misc.Bots;
import minced.module.misc.ChatHelper;
import minced.module.player.ChestStealer;
import minced.module.visual.ChinaHat;
import minced.module.misc.ClickFriend;
import minced.module.player.ClickPearl;
import minced.module.visual.Cosmetics;
import minced.module.visual.Crosshair;
import minced.module.misc.CrystalOptimizer;
import minced.module.misc.DeathCoords;
import minced.module.misc.EcSaver;
import minced.module.misc.ElytraHelper;
import minced.module.movement.ElytraJump;
import minced.module.movement.ElytraRecast;
import minced.module.movement.ElytraResolver;
import minced.module.movement.ElytraTarget;
import minced.module.movement.ElytraWhatsapp;
import minced.module.visual.Emotions;
import minced.module.visual.EntityESP;
import minced.module.player.FastBreak;
import minced.module.player.FastExp;
import minced.module.movement.FastSwimming;
import minced.module.visual.FireworkESP;
import minced.module.misc.FlagDetector;
import minced.module.movement.Flight;
import minced.module.movement.FreeCamera;
import minced.module.visual.FullBright;
import minced.module.movement.GrimGlide;
import minced.module.movement.GuiMove;
import minced.module.movement.HighJump;
import minced.module.combat.HitBoxes;
import minced.module.visual.Interface;
import minced.module.misc.InventoryPlus;
import minced.module.misc.ItemHelper;
import minced.module.visual.ItemPhysic;
import minced.module.player.ItemScroller;
import minced.module.player.ItemsCooldown;
import minced.module.movement.Jesus;
import minced.module.visual.JumpCircle;
import minced.module.combat.KBDisplacement;
import minced.module.misc.KeyFinder;
import minced.module.visual.KillEffect;
import minced.module.player.LockSlot;
import minced.module.misc.MiningCounter;
import minced.module.misc.NameProtect;
import minced.module.combat.NoEntityTrace;
import minced.module.movement.NoFall;
import minced.module.combat.NoFriendDamage;
import minced.module.player.NoInteract;
import minced.module.movement.NoJumpDelay;
import minced.module.player.NoPearlTeleport;
import minced.module.movement.NoPush;
import minced.module.combat.NoSlotChange;
import minced.module.movement.NoSlow;
import minced.module.combat.NoVelocity;
import minced.module.movement.NoWeb;
import minced.module.visual.ObjectItem;
import minced.module.misc.OpenWalls;
import minced.module.combat.PacketCriticals;
import minced.module.visual.Particles;
import minced.module.player.PearlPhase;
import minced.module.player.PearlTarget;
import minced.module.player.PearlTracker;
import minced.module.misc.PotionTracker;
import minced.module.visual.Prediction;
import minced.module.visual.Removals;
import minced.module.misc.SRPSpoof;
import minced.module.misc.ScoreboardHealth;
import minced.module.visual.SeeInvisibles;
import minced.module.misc.ServerHelper;
import minced.module.visual.ShaderESP;
import minced.module.visual.ShaderHands;
import minced.module.movement.Speed;
import minced.module.movement.Spider;
import minced.module.movement.Sprint;
import minced.module.movement.SuperFirework;
import minced.module.visual.SwordAnimations;
import minced.module.visual.Tags;
import minced.module.misc.TapeMouse;
import minced.module.combat.TargetStrafe;
import minced.module.visual.ThirdPerson;
import minced.module.movement.Timer;
import minced.module.misc.ToggleSounds;
import minced.module.visual.Trails;
import minced.module.visual.Trajectory;
import minced.module.misc.TrashTalk;
import minced.module.combat.TriggerBot;
import minced.module.misc.UseTracker;
import minced.module.movement.ViaWaterFix;
import minced.module.visual.ViewModel;
import minced.module.movement.WaterSpeed;
import minced.module.movement.WindHop;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import sLM._4ZXYEo;

public class Module
{
    static String cfr_renamed_358;

    AttackAura field731;
    List<minced.core.Module> field732 = new CopyOnWriteArrayList<>();
    Map field733 = new HashMap();
    static Object field734;
    PacketCriticals field735;
    GuiMove field736;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field737;
    static String field738;
    static String field739;
    static String field740;

    public AttackAura method177() {
        return this.field731;
    }

    public List<minced.core.Module> method178() {
        return this.field732;
    }

    private void method179(minced.core.Module param1) {
        this.field732.add(param1);
        this.field733.put(param1.getClass(), param1);
    }

    public static void method180() {
        field734 = null;
    }

    public Map O() {
        return this.field733;
    }

    public PacketCriticals method181() {
        return this.field735;
    }

    public GuiMove method182() {
        return this.field736;
    }

    public void method183() {
        this.field735 = new PacketCriticals();
        this.method179(this.field735);
        this.field731 = new AttackAura();
        this.method179(this.field731);
        this.method179(new AutoExplosion());
        this.method179(new AutoCrystal());
        this.method179(new AutoMinecart());
        this.method179(new AutoSwap());
        this.method179(new TriggerBot());
        this.method179(new AutoTotem());
        this.method179(new HitBoxes());
        this.method179(new NoEntityTrace());
        this.method179(new NoFriendDamage());
        this.method179(new AutoTrap());
        this.method179(new NoVelocity());
        this.method179(new AutoGApple());
        this.method179(new NoSlotChange());
        this.method179(new TargetStrafe());
        this.method179(new AutoMace());
        this.method179(new AntiBot());
        this.method179(new AutoAnchor());
        this.method179(new AimBot());
        this.method179(new AimAssist());
        this.method179(new BackTrack());
        this.method179(new KBDisplacement());
        this.method179(new Timer());
        this.method179(new Sprint());
        this.method179(new NoWeb());
        this.method179(new Flight());
        this.method179(new GrimGlide());
        this.method179(new NoPush());
        this.method179(new NoSlow());
        this.method179(new ElytraRecast());
        this.method179(new SuperFirework());
        this.method179(new ElytraResolver());
        this.method179(new ElytraTarget());
        this.method179(new HighJump());
        this.method179(new ElytraWhatsapp());
        this.method179(new ElytraJump());
        this.method179(new Speed());
        this.method179(new Blink());
        this.method179(new AutoJump());
        this.method179(new AirJump());
        this.method179(new Jesus());
        this.method179(new Spider());
        this.method179(new NoJumpDelay());
        this.method179(new BedrockClip());
        this.method179(new NoFall());
        this.method179(new WindHop());
        this.method179(new FastSwimming());
        this.method179(new WaterSpeed());
        this.method179(new ClickPearl());
        this.method179(new PearlPhase());
        this.method179(new PearlTarget());
        this.method179(new AntiAFK());
        this.method179(new AutoArmor());
        this.method179(new AutoLeave());
        this.method179(new AutoJoiner());
        this.field736 = new GuiMove();
        this.method179(this.field736);
        this.method179(new LockSlot());
        this.method179(new AutoPotion());
        this.method179(new AirStuck());
        this.method179(new ChestStealer());
        this.method179(new NoInteract());
        this.method179(new FastExp());
        this.method179(new AutoTool());
        this.method179(new ViaWaterFix());
        this.method179(new FastBreak());
        this.method179(new ItemScroller());
        this.method179(new AutoFarm());
        this.method179(new AutoEat());
        this.method179(new AutoFish());
        this.method179(new AutoRespawn());
        this.method179(new ItemsCooldown());
        this.method179(new AimingItems());
        this.method179(new PearlTracker());
        this.method179(new NoPearlTeleport());
        this.method179(new Ambience());
        this.method179(new AspectRatio());
        this.method179(new Trajectory());
        this.method179(new Interface());
        this.method179(new Tags());
        this.method179(new EntityESP());
        this.method179(new SwordAnimations());
        this.method179(new ViewModel());
        this.method179(new ShaderHands());
        this.method179(new ShaderESP());
        this.method179(new FireworkESP());
        this.method179(new ThirdPerson());
        this.method179(new FreeCamera());
        this.method179(new FullBright());
        this.method179(new ArmorDurabilityView());
        this.method179(new ItemPhysic());
        this.method179(new Removals());
        this.method179(new SeeInvisibles());
        this.method179(new BlockESP());
        this.method179(new Arrows());
        this.method179(new Prediction());
        this.method179(new Trails());
        this.method179(new JumpCircle());
        this.method179(new Particles());
        this.method179(new ChinaHat());
        this.method179(new Cosmetics());
        this.method179(new Emotions());
        this.method179(new Crosshair());
        this.method179(new ObjectItem());
        this.method179(new BlockOverlay());
        this.method179(new KillEffect());
        this.method179(new ToggleSounds());
        this.method179(new ServerHelper());
        this.method179(new MiningCounter());
        this.method179(new TapeMouse());
        this.method179(new SRPSpoof());
        this.method179(new KeyFinder());
        this.method179(new InventoryPlus());
        this.method179(new ScoreboardHealth());
        this.method179(new DeathCoords());
        this.method179(new NameProtect());
        this.method179(new ClickFriend());
        this.method179(new PotionTracker());
        this.method179(new ItemHelper());
        this.method179(new AutoAccept());
        this.method179(new AutoExchange());
        this.method179(new AucHelper());
        this.method179(new ChatHelper());
        this.method179(new ElytraHelper());
        this.method179(new UseTracker());
        this.method179(new AutoShulker());
        this.method179(new OpenWalls());
        this.method179(new EcSaver());
        this.method179(new CrystalOptimizer());
        this.method179(new AntiCrash());
        this.method179(new TrashTalk());
        this.method179(new FlagDetector());
        this.method179(new Bots());
        this.field732.sort(Comparator.comparing(minced.core.Module::getName));
        EventBus.method3013(this);
    }

    public Optional<minced.core.Module> method184(String param12) {
        return this.field732.stream().filter(param1 -> param1.getName().equalsIgnoreCase(param12)).findFirst();
    }

    public static void method185(Object param0) {
        field734 = param0;
    }

    @EventHandler(2)
    private void method186(EventKey param1) {
        if (field734 != null) {
            int v2 = param1.method3595();
            if (v2 != 256) {
                Object v3;
                Object v4 = field734;
                if (v4 instanceof minced.core.Module) {
                    v3 = (minced.core.Module)v4;
                    ((minced.core.Module)v3).setKey(v2 == 261 ? -100 : v2);
                }
                if ((v4 = field734) instanceof BooleanSetting) {
                    v3 = (BooleanSetting)v4;
                    ((BooleanSetting)v3).cfr_renamed_0(v2 == 261 ? -100 : v2);
                }
            }
            field734 = null;
            return;
        }
        if (param1.method3595() == 344 && !param1.method3594()) {
            I.setScreen((Screen)Minced.getInstance().getDropDown());
        }
        for (minced.core.Module v3 : this.field732) {
            if (KeyCode.matches(v3.getKey(), param1.method3595())) {
                boolean v4 = false;
                if (v3.getToggleMode() == TOGGLE.HOLD) {
                    v4 = !param1.method3594();
                } else if (v3.getToggleMode() == TOGGLE.TOGGLE && !param1.method3599()) {
                    boolean bl = v4 = !param1.method3594();
                }
                if (v4) {
                    v3.toggle();
                }
            }
            for (Setting v5 : v3.getSettings()) {
                BooleanSetting v6;
                if (!(v5 instanceof BooleanSetting) || !KeyCode.matches((v6 = (BooleanSetting)v5).method1903(), param1.method3595())) continue;
                boolean v7 = v6.method1901() == TOGGLE.TOGGLE;
                boolean v8 = false;
                if (!v7) {
                    v8 = !param1.method3594();
                } else if (!param1.method3599()) {
                    boolean bl = v8 = !param1.method3594();
                }
                if (!v8) continue;
                boolean v9 = (Boolean)v6.getValue() == false;
                v6.setValue(v9);
                if (minced.core.Module.isStateChangeSuppressed()) continue;
                SoundPlayer.method2069(ToggleSounds.method1052(v9));
                NotificationManager.method2456(v9 ? field737 : field738, (String)((Object)_4ZXYEo.弟З("makeConcatWithConstants", new Object[]{}, (String)v6.getName(), (String)(v9 ? field739 : field740))), -1);
            }
        }
    }

    public minced.core.Module method187(Class param1) {
        return (minced.core.Module)this.field733.get(param1);
    }

    public static Object method188() {
        return field734;
    }

    static {
        field737 = "J";
        field738 = "K";
        field739 = "включен";
        field740 = "выключен";
        cfr_renamed_358 = "\u0001 \u0001";
        Module.method180();
    }
}



