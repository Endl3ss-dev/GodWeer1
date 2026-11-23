// deobf by pusia
package su.catlean.client.features.modules.player;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.random.Random;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.ThreadManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.inventory.ContainerUtil;

public final class AutoFish extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AutoFish.class, "detectMode", "getDetectMode()Lsu/catlean/f0;", 0), new PropertyReference1Impl(AutoFish.class, "rodSave", "getRodSave()Z", 0), new PropertyReference1Impl(AutoFish.class, "changeRod", "getChangeRod()Z", 0), new PropertyReference1Impl(AutoFish.class, "legit", "getLegit()Z", 0) };
   @NotNull
   public static final AutoFish INSTANCE = new AutoFish();
   @NotNull
   private static final EnumSetting detectMode = SettingFactory.defaultImpl(INSTANCE, "detect-mode", AutoFish.DetectMode.V1_17, null, null, 12, null);
   @NotNull
   private static final BooleanSetting rodSave = SettingFactory.defaultImpl(INSTANCE, "rod-save", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting changeRod = SettingFactory.defaultImpl(INSTANCE, "change-rod", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting legit = SettingFactory.defaultImpl(INSTANCE, "legit", true, null, null, 12, null);
   private static boolean flag;
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   @NotNull
   private static final StopWatch stopWatch2 = new StopWatch();

   private AutoFish() {
      super("auto-fish", SubCategories.getSubCategory10(), null, 4, null);
   }

   private final AutoFish.DetectMode getDetectMode() {
      return (AutoFish.DetectMode)detectMode.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getRodSave() {
      return (Boolean)rodSave.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getChangeRod() {
      return (Boolean)changeRod.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getLegit() {
      return (Boolean)legit.computeObject(this, $$delegatedProperties[3]);
   }

   @Override
   public void update4() {
      flag = false;
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundSoundPacket
         && this.getDetectMode() == AutoFish.DetectMode.V1_12_2
         && Intrinsics.areEqual(((ClientboundSoundPacket)receivePacket.getPacket()).getSound().value(), SoundEvents.FISHING_BOBBER_SPLASH)
         && MinecraftHolder.getLocalPlayer().fishing != null
         && MinecraftHolder.getLocalPlayer()
               .fishing
               .distanceToSqr(
                  ((ClientboundSoundPacket)receivePacket.getPacket()).getX(),
                  ((ClientboundSoundPacket)receivePacket.getPacket()).getY(),
                  ((ClientboundSoundPacket)receivePacket.getPacket()).getZ()
               )
            < 4.0) {
         this.applyExecute();
      }
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (MinecraftHolder.getLocalPlayer().getMainHandItem().getItem() instanceof FishingRodItem
         && MinecraftHolder.getLocalPlayer().getMainHandItem().getDamageValue() > 52
         && this.getRodSave()) {
         if (this.getChangeRod() && this.getValue() != -1) {
            ChatUtil.defaultImpl(this, ChatUtil.defaultImpl(this, "swapped", new Object[0], false, 4, null), false, 2, null);
            InventoryManager.INSTANCE.applyGetInventory2(this.getValue());
            stopWatch2.update();
         } else {
            this.updateText10(ChatUtil.defaultImpl(this, "almost-broken", new Object[0], false, 4, null));
         }
      }

      if (stopWatch2.testValue(1000)) {
         if (stopWatch.testValue(45000) && MinecraftHolder.getLocalPlayer().getMainHandItem().getItem() instanceof FishingRodItem) {
            ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
            MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
            stopWatch.update();
            stopWatch2.update();
         }

         if (MinecraftHolder.getLocalPlayer().fishing != null && this.getDetectMode() == AutoFish.DetectMode.V1_17) {
            boolean datA_BITING = (Boolean)MinecraftHolder.getLocalPlayer().fishing.getEntityData().get(FishingHook.DATA_BITING);
            if (!flag && datA_BITING) {
               this.applyExecute();
               flag = true;
            } else if (!datA_BITING) {
               flag = false;
            }
         }
      }
   }

   private final void applyExecute() {
      if (this.getLegit()) {
         int nextDouble = (int)(Random.Default.nextDouble() * 150.0 + 200.0);
         ThreadManager instance = ThreadManager.INSTANCE;
         instance.getExecutorService().execute(new AutoFish.Task(nextDouble));
         instance = ThreadManager.INSTANCE;
         int value = nextDouble * 2;
         instance.getExecutorService().execute(new AutoFish.Task2(value));
      } else {
         ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
         MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
         ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
         MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
         stopWatch.update();
      }
   }

   private final int getValue() {
      for (int i = 0; i < 9; i++) {
         if (Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getInventory().getItem(i).getItem(), Items.FISHING_ROD)
            && MinecraftHolder.getLocalPlayer().getInventory().getItem(i).getDamageValue() < 52) {
            return i;
         }
      }

      return -1;
   }

   public static final StopWatch getStopWatch() {
      return stopWatch;
   }

   static {
   }

   public static enum DetectMode {
      V1_12_2,
      V1_17;

      private static final AutoFish.DetectMode[] detectModes = $values();
      private static final EnumEntries<AutoFish.DetectMode> enumEntries = EnumEntriesKt.enumEntries(detectModes);

      private DetectMode() {
      }

      @NotNull
      public static EnumEntries<AutoFish.DetectMode> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoFish.DetectMode[] $values() {
         return new AutoFish.DetectMode[]{V1_12_2, V1_17};
      }
   }

   public static final class Task implements Runnable {
      public final int value;

      public Task(int value) {
         this.value = value;
      }

      @Override
      public final void run() {
         try {
            Thread.sleep(this.value);
         } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return;
         }
         MinecraftHolder.getMinecraft().executeIfPossible(AutoFish.Task.Task2.INSTANCE);
      }

      public static final class Task2 implements Runnable {
         public static final AutoFish.Task.Task2 INSTANCE = new AutoFish.Task.Task2();

         public Task2() {
         }

         @Override
         public final void run() {
            ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
            MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
         }
      }

      public static final class TaskInner implements Runnable {
         public static final AutoFish.Task.TaskInner INSTANCE = new AutoFish.Task.TaskInner();

         public TaskInner() {
         }

         @Override
         public final void run() {
            ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
            MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
            AutoFish.getStopWatch().update();
         }
      }
   }

   public static final class Task2 implements Runnable {
      public final int value;

      public Task2(int value) {
         this.value = value;
      }

      @Override
      public final void run() {
         try {
            Thread.sleep(this.value);
         } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return;
         }
         MinecraftHolder.getMinecraft().executeIfPossible(AutoFish.Task.TaskInner.INSTANCE);
      }
   }
}