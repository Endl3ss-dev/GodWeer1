// deobf by pusia
package su.catlean.client.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import su.catlean.api.category.Category;
import su.catlean.api.category.SubCategory;
import su.catlean.api.data.ModuleValue;
import su.catlean.api.data.ServiceConfig;
import su.catlean.api.data.SettingValue;
import su.catlean.api.event.events.client.InputEvent;
import su.catlean.api.service.Service;
import su.catlean.api.setting.Setting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.attack.AntiPhase;
import su.catlean.client.features.modules.attack.AttributeSwap;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.features.modules.attack.AutoBasePlace;
import su.catlean.client.features.modules.attack.AutoCrystal;
import su.catlean.client.features.modules.attack.AutoDripstope;
import su.catlean.client.features.modules.attack.AutoMine;
import su.catlean.client.features.modules.attack.AutoNetheriteScrap;
import su.catlean.client.features.modules.attack.AutoSweetBerries;
import su.catlean.client.features.modules.attack.AutoWeb;
import su.catlean.client.features.modules.attack.Criticals;
import su.catlean.client.features.modules.attack.HoleFiller;
import su.catlean.client.features.modules.attack.MaceKiller;
import su.catlean.client.features.modules.attack.MoreKnockback;
import su.catlean.client.features.modules.attack.Trap;
import su.catlean.client.features.modules.basic.AntiKnockBack;
import su.catlean.client.features.modules.basic.Avoid;
import su.catlean.client.features.modules.basic.ElytraRecast;
import su.catlean.client.features.modules.basic.GuiMove;
import su.catlean.client.features.modules.basic.HoleAnchor;
import su.catlean.client.features.modules.basic.InfiniteElytra;
import su.catlean.client.features.modules.basic.LegitStrafe;
import su.catlean.client.features.modules.basic.MoveFix;
import su.catlean.client.features.modules.basic.NoPush;
import su.catlean.client.features.modules.basic.Sprint;
import su.catlean.client.features.modules.basic.WindHop;
import su.catlean.client.features.modules.cosmetic.Absorption;
import su.catlean.client.features.modules.cosmetic.Aspect;
import su.catlean.client.features.modules.cosmetic.BuffEffect;
import su.catlean.client.features.modules.cosmetic.CrystalChams;
import su.catlean.client.features.modules.cosmetic.DamageParticles;
import su.catlean.client.features.modules.cosmetic.DamageTint;
import su.catlean.client.features.modules.cosmetic.Dismemberment;
import su.catlean.client.features.modules.cosmetic.FreeLook;
import su.catlean.client.features.modules.cosmetic.FullBright;
import su.catlean.client.features.modules.cosmetic.HandChams;
import su.catlean.client.features.modules.cosmetic.HitFx;
import su.catlean.client.features.modules.cosmetic.NoCameraClip;
import su.catlean.client.features.modules.cosmetic.Particles;
import su.catlean.client.features.modules.cosmetic.PopChams;
import su.catlean.client.features.modules.cosmetic.SkyLanterns;
import su.catlean.client.features.modules.cosmetic.SwingAnimations;
import su.catlean.client.features.modules.cosmetic.ThrowPetard;
import su.catlean.client.features.modules.cosmetic.TotemAnimation;
import su.catlean.client.features.modules.cosmetic.Trajectories;
import su.catlean.client.features.modules.cosmetic.ViewModel;
import su.catlean.client.features.modules.cosmetic.WorldRenderer;
import su.catlean.client.features.modules.equip.AutoArmor;
import su.catlean.client.features.modules.equip.AutoTool;
import su.catlean.client.features.modules.equip.ChestStealer;
import su.catlean.client.features.modules.equip.EcExploit;
import su.catlean.client.features.modules.equip.ElytraReplace;
import su.catlean.client.features.modules.equip.ElytraSwap;
import su.catlean.client.features.modules.equip.ItemScroller;
import su.catlean.client.features.modules.equip.MiddleClick;
import su.catlean.client.features.modules.equip.NoServerSlot;
import su.catlean.client.features.modules.equip.QuiverModule;
import su.catlean.client.features.modules.equip.ReFiller;
import su.catlean.client.features.modules.equip.ToolSaver;
import su.catlean.client.features.modules.equip.XCarry;
import su.catlean.client.features.modules.esp.BreakEsp;
import su.catlean.client.features.modules.esp.HoleEsp;
import su.catlean.client.features.modules.esp.ItemEsp;
import su.catlean.client.features.modules.esp.LogoutSpots;
import su.catlean.client.features.modules.esp.NameTags;
import su.catlean.client.features.modules.esp.PearlPhaseEsp;
import su.catlean.client.features.modules.esp.PlayerEsp;
import su.catlean.client.features.modules.esp.ProjectilePredict;
import su.catlean.client.features.modules.esp.Search;
import su.catlean.client.features.modules.esp.StorageEsp;
import su.catlean.client.features.modules.esp.Tracers;
import su.catlean.client.features.modules.esp.VoidEsp;
import su.catlean.client.features.modules.esp.XRay;
import su.catlean.client.features.modules.legit.AnchorBind;
import su.catlean.client.features.modules.legit.AutoBuff;
import su.catlean.client.features.modules.legit.BowMinecart;
import su.catlean.client.features.modules.legit.BowSpam;
import su.catlean.client.features.modules.legit.CrystalBind;
import su.catlean.client.features.modules.legit.LegitTotem;
import su.catlean.client.features.modules.legit.Reach;
import su.catlean.client.features.modules.misc.AntiResourcePack;
import su.catlean.client.features.modules.misc.Assistant;
import su.catlean.client.features.modules.misc.AutoEat;
import su.catlean.client.features.modules.misc.AutoEz;
import su.catlean.client.features.modules.misc.AutoLeave;
import su.catlean.client.features.modules.misc.AutoPotionBrew;
import su.catlean.client.features.modules.misc.AutoSoup;
import su.catlean.client.features.modules.misc.AutoTrader;
import su.catlean.client.features.modules.misc.ClientInterface;
import su.catlean.client.features.modules.misc.DiscordRpc;
import su.catlean.client.features.modules.misc.ExtraTab;
import su.catlean.client.features.modules.misc.FastLatency;
import su.catlean.client.features.modules.misc.MessageAppend;
import su.catlean.client.features.modules.misc.NameProtect;
import su.catlean.client.features.modules.misc.Notifier;
import su.catlean.client.features.modules.misc.PingSpoof;
import su.catlean.client.features.modules.misc.SourceFiller;
import su.catlean.client.features.modules.misc.Spammer;
import su.catlean.client.features.modules.misc.TapeMouse;
import su.catlean.client.features.modules.misc.TeamCommunication;
import su.catlean.client.features.modules.misc.Test;
import su.catlean.client.features.modules.misc.Translator;
import su.catlean.client.features.modules.player.ActionRecorder;
import su.catlean.client.features.modules.player.AirPlace;
import su.catlean.client.features.modules.player.AntiAim;
import su.catlean.client.features.modules.player.AntiAttack;
import su.catlean.client.features.modules.player.AntiHunger;
import su.catlean.client.features.modules.player.AutoAuth;
import su.catlean.client.features.modules.player.AutoFish;
import su.catlean.client.features.modules.player.AutoMend;
import su.catlean.client.features.modules.player.AutoRespawn;
import su.catlean.client.features.modules.player.AutoTpaAccept;
import su.catlean.client.features.modules.player.BoxShrinker;
import su.catlean.client.features.modules.player.ChorusDelay;
import su.catlean.client.features.modules.player.MultiTask;
import su.catlean.client.features.modules.player.NoDelay;
import su.catlean.client.features.modules.player.NoInteract;
import su.catlean.client.features.modules.player.NoServerRotate;
import su.catlean.client.features.modules.player.PerfectDelay;
import su.catlean.client.features.modules.player.PortalGodMode;
import su.catlean.client.features.modules.player.SpeedMine;
import su.catlean.client.features.modules.player.TraceThrough;
import su.catlean.client.features.modules.protect.AutoGapple;
import su.catlean.client.features.modules.protect.Blocker;
import su.catlean.client.features.modules.protect.Burrow;
import su.catlean.client.features.modules.protect.MainhandTotem;
import su.catlean.client.features.modules.protect.Offhand;
import su.catlean.client.features.modules.protect.SelfTrap;
import su.catlean.client.features.modules.protect.Surround;
import su.catlean.client.features.modules.rage.AntiVoid;
import su.catlean.client.features.modules.rage.AntiWeb;
import su.catlean.client.features.modules.rage.Blink;
import su.catlean.client.features.modules.rage.BoatFly;
import su.catlean.client.features.modules.rage.ElytraFreeze;
import su.catlean.client.features.modules.rage.ElytraPlus;
import su.catlean.client.features.modules.rage.ElytraTarget;
import su.catlean.client.features.modules.rage.EntitySpeed;
import su.catlean.client.features.modules.rage.Flight;
import su.catlean.client.features.modules.rage.HighJump;
import su.catlean.client.features.modules.rage.LevitationControl;
import su.catlean.client.features.modules.rage.MaceElytraTarget;
import su.catlean.client.features.modules.rage.NoFall;
import su.catlean.client.features.modules.rage.NoSlow;
import su.catlean.client.features.modules.rage.Phase;
import su.catlean.client.features.modules.rage.ReverseStep;
import su.catlean.client.features.modules.rage.Scaffold;
import su.catlean.client.features.modules.rage.Speed;
import su.catlean.client.features.modules.rage.Spider;
import su.catlean.client.features.modules.rage.Step;
import su.catlean.client.features.modules.rage.Timer;
import su.catlean.client.features.modules.rage.TridentBoost;
import su.catlean.client.features.modules.rage.WaterSpeed;
import su.catlean.client.features.modules.world.AutoBoneMeal;
import su.catlean.client.features.modules.world.AutoLand;
import su.catlean.client.features.modules.world.AutoShear;
import su.catlean.client.features.modules.world.AutoSign;
import su.catlean.client.features.modules.world.AutoWither;
import su.catlean.client.features.modules.world.FakePlayer;
import su.catlean.client.features.modules.world.FreeCam;
import su.catlean.client.features.modules.world.NoRender;
import su.catlean.client.features.modules.world.Nuker;
import su.catlean.client.features.modules.world.PearlChaser;
import su.catlean.client.features.modules.world.ServerHelper;
import su.catlean.client.features.modules.world.SoilRipper;
import su.catlean.client.features.modules.world.TpsSync;
import su.catlean.client.features.modules.world.Zoom;
import su.catlean.client.features.ui.ClickGuiScreen;
import su.catlean.client.services.proxy.ProxyManager;
import su.catlean.gofra.Flow;
import su.catlean.gofra.Gofra;
import su.catlean.utils.MinecraftHolder;

public final class ModuleManager implements Service {
   @NotNull
   public static final ModuleManager INSTANCE = new ModuleManager();
   @NotNull
   private static final ArrayList items;
   @NotNull
   private static ArrayList<Module> items2 = new ArrayList();
   @NotNull
   private static final ArrayList<Integer> items3 = new ArrayList();

   private ModuleManager() {
   }

   @NotNull
   public final ArrayList getItems() {
      return items;
   }

   @NotNull
   public final ArrayList<Module> getItems2() {
      return items2;
   }

   @NotNull
   public final ArrayList<Integer> getItems3() {
      return items3;
   }

   private final boolean isFlag() {
      ArrayList<Module> items22 = items2;
      Module[] modules = new Module[]{
         ElytraTarget.INSTANCE,
         BowMinecart.INSTANCE,
         ElytraFreeze.INSTANCE,
         Dismemberment.INSTANCE,
         ExtraTab.INSTANCE,
         ThrowPetard.INSTANCE,
         AntiAim.INSTANCE,
         VoidEsp.INSTANCE,
         Aspect.INSTANCE,
         Test.INSTANCE,
         NameProtect.INSTANCE,
         AnchorBind.INSTANCE,
         Spider.INSTANCE,
         Assistant.INSTANCE,
         AutoBoneMeal.INSTANCE,
         PearlPhaseEsp.INSTANCE,
         AutoSweetBerries.INSTANCE,
         MaceElytraTarget.INSTANCE,
         DamageParticles.INSTANCE,
         InfiniteElytra.INSTANCE,
         MoreKnockback.INSTANCE,
         EcExploit.INSTANCE,
         AntiAttack.INSTANCE,
         MainhandTotem.INSTANCE,
         ServerHelper.INSTANCE,
         BoatFly.INSTANCE,
         Zoom.INSTANCE,
         WindHop.INSTANCE,
         LegitStrafe.INSTANCE,
         DamageTint.INSTANCE,
         FreeLook.INSTANCE,
         AutoNetheriteScrap.INSTANCE,
         FastLatency.INSTANCE,
         PingSpoof.INSTANCE,
         AutoMend.INSTANCE,
         Blocker.INSTANCE,
         AutoShear.INSTANCE,
         SoilRipper.INSTANCE,
         SwingAnimations.INSTANCE,
         Particles.INSTANCE,
         AutoTrader.INSTANCE,
         BuffEffect.INSTANCE,
         ActionRecorder.INSTANCE,
         AutoPotionBrew.INSTANCE,
         AutoLand.INSTANCE,
         AntiKnockBack.INSTANCE,
         ChestStealer.INSTANCE,
         NoCameraClip.INSTANCE,
         Avoid.INSTANCE,
         BreakEsp.INSTANCE,
         XRay.INSTANCE,
         ElytraRecast.INSTANCE,
         AutoBasePlace.INSTANCE,
         GuiMove.INSTANCE,
         MoveFix.INSTANCE,
         NoPush.INSTANCE,
         Sprint.INSTANCE,
         AttributeSwap.INSTANCE,
         AntiVoid.INSTANCE,
         AntiWeb.INSTANCE,
         ElytraPlus.INSTANCE,
         Flight.INSTANCE,
         LevitationControl.INSTANCE,
         Blink.INSTANCE,
         PopChams.INSTANCE,
         NoFall.INSTANCE,
         NoSlow.INSTANCE,
         Phase.INSTANCE,
         ReverseStep.INSTANCE,
         Speed.INSTANCE,
         Step.INSTANCE,
         Timer.INSTANCE,
         WaterSpeed.INSTANCE,
         Aura.INSTANCE,
         AutoCrystal.INSTANCE,
         Absorption.INSTANCE,
         SourceFiller.INSTANCE,
         AutoWeb.INSTANCE,
         MessageAppend.INSTANCE,
         Criticals.INSTANCE,
         HoleFiller.INSTANCE,
         AutoBuff.INSTANCE,
         BowSpam.INSTANCE,
         Reach.INSTANCE,
         AutoWither.INSTANCE,
         HighJump.INSTANCE,
         SkyLanterns.INSTANCE,
         AntiPhase.INSTANCE,
         AutoGapple.INSTANCE,
         Burrow.INSTANCE,
         LegitTotem.INSTANCE,
         Offhand.INSTANCE,
         Surround.INSTANCE,
         AutoArmor.INSTANCE,
         AutoTool.INSTANCE,
         ElytraSwap.INSTANCE,
         su.catlean.client.features.modules.equip.InventoryManager.INSTANCE,
         ItemScroller.INSTANCE,
         MiddleClick.INSTANCE,
         AutoMine.INSTANCE,
         NoServerSlot.INSTANCE,
         ReFiller.INSTANCE,
         ToolSaver.INSTANCE,
         XCarry.INSTANCE,
         AntiResourcePack.INSTANCE,
         ClientInterface.INSTANCE,
         Trap.INSTANCE,
         SelfTrap.INSTANCE,
         TeamCommunication.INSTANCE,
         Notifier.INSTANCE,
         DiscordRpc.INSTANCE,
         Spammer.INSTANCE,
         TapeMouse.INSTANCE,
         Translator.INSTANCE,
         AirPlace.INSTANCE,
         ElytraReplace.INSTANCE,
         AntiHunger.INSTANCE,
         AutoAuth.INSTANCE,
         AutoFish.INSTANCE,
         AutoRespawn.INSTANCE,
         AutoTpaAccept.INSTANCE,
         ChorusDelay.INSTANCE,
         MultiTask.INSTANCE,
         NoDelay.INSTANCE,
         TraceThrough.INSTANCE,
         NoServerRotate.INSTANCE,
         PerfectDelay.INSTANCE,
         PortalGodMode.INSTANCE,
         SpeedMine.INSTANCE,
         AutoSign.INSTANCE,
         FakePlayer.INSTANCE,
         FreeCam.INSTANCE,
         NoRender.INSTANCE,
         Nuker.INSTANCE,
         PearlChaser.INSTANCE,
         Scaffold.INSTANCE,
         CrystalChams.INSTANCE,
         FullBright.INSTANCE,
         HandChams.INSTANCE,
         HitFx.INSTANCE,
         TotemAnimation.INSTANCE,
         Trajectories.INSTANCE,
         ViewModel.INSTANCE,
         WorldRenderer.INSTANCE,
         HoleAnchor.INSTANCE,
         CrystalBind.INSTANCE,
         Search.INSTANCE,
         HoleEsp.INSTANCE,
         EntitySpeed.INSTANCE,
         ItemEsp.INSTANCE,
         LogoutSpots.INSTANCE,
         AutoDripstope.INSTANCE,
         TpsSync.INSTANCE,
         BoxShrinker.INSTANCE,
         AutoLeave.INSTANCE,
         AutoSoup.INSTANCE,
         AutoEat.INSTANCE,
         NameTags.INSTANCE,
         ProjectilePredict.INSTANCE,
         PlayerEsp.INSTANCE,
         StorageEsp.INSTANCE,
         Tracers.INSTANCE,
         NoInteract.INSTANCE,
         AutoEz.INSTANCE,
         MaceKiller.INSTANCE,
         TridentBoost.INSTANCE,
         QuiverModule.INSTANCE
      };
      return items22.addAll(CollectionsKt.listOf(modules));
   }

   public final void applyIsEmpty(Module module) {
      var items22 = items2;
      boolean flag;
      if (items22 instanceof Collection && items22.isEmpty()) {
         flag = false;
      } else {
         Iterator<Module> iterator = items22.iterator();

         while (true) {
            if (!iterator.hasNext()) {
               flag = false;
               break;
            }

            Module object = iterator.next();
            Module module2 = (Module)object;
            if (Intrinsics.areEqual(module2.getId(), module.getId())) {
               flag = true;
               break;
            }
         }
      }

      if (!flag) {
         items2.add(module);
         this.applyGetMessage(module);
         Gofra.INSTANCE.unplug(module);
         if (module.getEnabled()) {
            Gofra.INSTANCE.plug(module);
            module.update2();
         }
      }
   }

   public final void update() {
      for (Object items2Item : items2) {
         Module module = (Module)items2Item;
         this.applyGetMessage(module);
      }
   }

   private final void applyGetMessage(Module module) {
      for (Object items5Item : module.getItems5()) {
         Setting setting = (Setting)items5Item;
         Iterable modules = ProxyManager.INSTANCE.getModuleDataSource().getServiceConfig().getModules();
         Iterator iterator = modules.iterator();

         Object catlean;
         while (true) {
            if (!iterator.hasNext()) {
               catlean = null;
               break;
            }

            Object object = iterator.next();
            ModuleValue moduleValue = (ModuleValue)object;
            if (Intrinsics.areEqual(moduleValue.getId(), module.getId())) {
               catlean = object;
               break;
            }
         }

         catlean = (ModuleValue)catlean;
         if ((ModuleValue)catlean != null) {
            ModuleValue moduleValue2 = (ModuleValue)catlean;
            Iterable settings = moduleValue2.getSettings();
            Iterator iterator2 = settings.iterator();

            while (true) {
               if (iterator2.hasNext()) {
                  Object object2 = iterator2.next();
                  SettingValue settingValue = (SettingValue)object2;
                  if (!Intrinsics.areEqual(settingValue.getId(), setting.getId())) {
                     continue;
                  }

                  catlean = object2;
               } else {
                  catlean = null;
               }

               catlean = (SettingValue)catlean;
               if ((SettingValue)catlean != null) {
                  SettingValue settingValue2 = (SettingValue)catlean;

                  try {
                     setting.applyDecodeFromString(settingValue2.getValue());
                  } catch (Exception exception) {
                     setting.update();
                     catlean = MinecraftHolder.getCatlean();
                     String text = setting.getText();
                     String text3 = module.getText3();
                     String message = exception.getMessage();
                     String text2 = text3;
                     String text4 = text;
                     ((org.slf4j.Logger)catlean).warn("Could not parse setting: " + text4 + " in " + text2 + ": " + message);
                  }
               }
               break;
            }
         }
      }
   }

   public final void applyParseBoolean(ServiceConfig serviceConfig) {
      for (Object items2Item : items2) {
         Module module;
         Boolean booleanValue;
         label58: {
            module = (Module)items2Item;
            if (serviceConfig != null) {
               List modules = serviceConfig.getModules();
               if (modules != null) {
                  Iterable iterable = modules;
                  Iterator iterator = iterable.iterator();

                  while (true) {
                     if (!iterator.hasNext()) {
                        booleanValue = null;
                        break;
                     }

                     Object object = iterator.next();
                     ModuleValue moduleValue = (ModuleValue)object;
                     if (Intrinsics.areEqual(module.getId(), moduleValue.getId())) {
                        booleanValue = (Boolean)object;
                        break;
                     }
                  }

                  ModuleValue moduleValue2 = (ModuleValue)(Object)booleanValue;
                  if (moduleValue2 != null) {
                     List settings = moduleValue2.getSettings();
                     if (settings != null) {
                        Iterable iterable2 = settings;
                        Iterator iterator2 = iterable2.iterator();

                        while (true) {
                           if (!iterator2.hasNext()) {
                              booleanValue = null;
                              break;
                           }

                           Object object2 = iterator2.next();
                           SettingValue settingValue = (SettingValue)object2;
                           if (Intrinsics.areEqual(settingValue.getId(), "enabled")) {
                              booleanValue = (Boolean)object2;
                              break;
                           }
                        }

                        SettingValue settingValue2 = (SettingValue)(Object)booleanValue;
                        if (settingValue2 != null) {
                           String value = settingValue2.getValue();
                           if (value != null) {
                              booleanValue = Boolean.parseBoolean(value);
                              break label58;
                           }
                        }
                     }
                  }
               }
            }

            booleanValue = null;
         }

         Boolean booleanValue2 = booleanValue;
         Gofra.INSTANCE.unplug(module);
         if (module.getEnabled()) {
            Gofra.INSTANCE.plug(module);
            if (!Intrinsics.areEqual(module.getEnabled(), booleanValue2)) {
               module.update2();
            }
         } else if (serviceConfig != null && !Intrinsics.areEqual(module.getEnabled(), booleanValue2)) {
            module.update4();
         }
      }
   }

   public static void defaultImpl(ModuleManager moduleManager, ServiceConfig serviceConfig, int value, Object object) {
      if ((value & 1) != 0) {
         serviceConfig = null;
      }

      moduleManager.applyParseBoolean(serviceConfig);
   }

   @Nullable
   public final Module computeModule(String id) {
      Iterable<Module> items22 = items2;
      Iterator<Module> iterator = items22.iterator();

      Module object;
      while (true) {
         if (iterator.hasNext()) {
            Module object2 = iterator.next();
            Module module = (Module)object2;
            if (!Intrinsics.areEqual(module.getId(), id)) {
               continue;
            }

            object = object2;
            break;
         }

         object = null;
         break;
      }

      return (Module)object;
   }

   @Flow
   private final void onInput(InputEvent inputEvent) {
      if (inputEvent.getKey() != -1 && inputEvent.getAction() != InputEvent.Action.Hold) {
         if (inputEvent.getDevice() == InputEvent.Device.Mouse) {
            if (inputEvent.getAction() == InputEvent.Action.Press) {
               items3.add(inputEvent.getKey());
            } else {
               items3.clear();
            }
         }

         if (inputEvent.getKey() == ClientInterface.INSTANCE.getBind().getValue()
            && inputEvent.getAction() == InputEvent.Action.Press
            && MinecraftHolder.getMinecraft().gui.screen() == null) {
            MinecraftHolder.getMinecraft().gui.setScreen(ClickGuiScreen.INSTANCE);
         }

         Iterable<Module> items22 = items2;
         var arrayList = new ArrayList();

         for (Object object : items22) {
            Module module = (Module)object;
            if (module.getBind().getValue() == inputEvent.getKey() && !Intrinsics.areEqual(module, ClientInterface.INSTANCE)) {
               arrayList.add(object);
            }
         }

         for (Object object2 : arrayList) {
            Module module2 = (Module)object2;
            ModuleManager instance = INSTANCE;
            if (!module2.getBind().isFlag()) {
               if (inputEvent.getAction() == InputEvent.Action.Press && MinecraftHolder.getMinecraft().gui.screen() == null) {
                  module2.update5();
               }
            } else if (inputEvent.getAction() == InputEvent.Action.Press) {
               module2.applyPlug();
            } else {
               module2.applyUnplug();
            }
         }
      }
   }

   static {
      Category[] categorys = new Category[]{
         ModuleManager.Categories.getCategory(),
         ModuleManager.Categories.getCategory2(),
         ModuleManager.Categories.getCategory3(),
         ModuleManager.Categories.getCategory4()
      };
      items = CollectionsKt.arrayListOf(categorys);
      INSTANCE.isFlag();
      INSTANCE.update();
      defaultImpl(INSTANCE, null, 1, null);
   }

   public static final class Categories {
      @NotNull
      private static final Category category;
      @NotNull
      private static final Category category2;
      @NotNull
      private static final Category category3;
      @NotNull
      private static final Category category4;

      @NotNull
      public static final Category getCategory() {
         return category;
      }

      @NotNull
      public static final Category getCategory2() {
         return category2;
      }

      @NotNull
      public static final Category getCategory3() {
         return category3;
      }

      @NotNull
      public static final Category getCategory4() {
         return category4;
      }

      static {
         SubCategory[] subCategorys = new SubCategory[]{SubCategories.getSubCategory2(), SubCategories.getSubCategory3(), SubCategories.getSubCategory()};
         category = new Category("pvp", CollectionsKt.arrayListOf(subCategorys));
         subCategorys = new SubCategory[]{SubCategories.getSubCategory4(), SubCategories.getSubCategory5()};
         category2 = new Category("movement", CollectionsKt.arrayListOf(subCategorys));
         subCategorys = new SubCategory[]{SubCategories.getSubCategory6(), SubCategories.getSubCategory7()};
         category3 = new Category("visual", CollectionsKt.arrayListOf(subCategorys));
         subCategorys = new SubCategory[]{
            SubCategories.getSubCategory8(), SubCategories.getSubCategory9(), SubCategories.getSubCategory10(), SubCategories.getSubCategory11()
         };
         category4 = new Category("utility", CollectionsKt.arrayListOf(subCategorys));
      }
   }
}