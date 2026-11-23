// deobf by pusia
package su.catlean.client.features.modules.attack;

import kotlin.NoWhenBranchMatchedException;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.PlayerPredictionService;
import su.catlean.client.services.RotationManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.player.PlayerTargetUtil;

public final class BowAimbot extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(BowAimbot.class, "silent", "getSilent()Z", 0), new MutablePropertyReference1Impl(BowAimbot.class, "predict", "getPredict()Lsu/catlean/si;", 0), new MutablePropertyReference1Impl(BowAimbot.class, "predictTicks", "getPredictTicks()I", 0), new MutablePropertyReference1Impl(BowAimbot.class, "ignoreNaked", "getIgnoreNaked()Z", 0), new MutablePropertyReference1Impl(BowAimbot.class, "ignoreCreative", "getIgnoreCreative()Z", 0) };
   @NotNull
   public static final BowAimbot INSTANCE = new BowAimbot();
   @NotNull
   private static final BooleanSetting silent = SettingFactory.defaultImpl(INSTANCE, "silent", true, null, null, 12, null);
   @NotNull
   private static final EnumSetting predict = SettingFactory.defaultImpl(INSTANCE, "predict", BowAimbot.Predict.SMART, null, null, 12, null);
   @NotNull
   private static final IntSetting predictTicks = SettingFactory.defaultImpl(
      INSTANCE, "predict-ticks", 1, new IntRange(1, 20), null, () -> INSTANCE.getPredict() == BowAimbot.Predict.CUSTOM, 8, null
   );
   @NotNull
   private static final BooleanSetting ignoreNaked = SettingFactory.defaultImpl(INSTANCE, "ignore-naked", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting ignoreCreative = SettingFactory.defaultImpl(INSTANCE, "ignore-creative", true, null, null, 12, null);

   private BowAimbot() {
      super("bow-aimbot", SubCategories.getSubCategory2(), null, 4, null);
   }

   private final BowAimbot.Predict getPredict() {
      return (BowAimbot.Predict)predict.computeObject(this, $$delegatedProperties[1]);
   }

   private final int getPredictTicks() {
      return ((Number)predictTicks.computeObject(this, $$delegatedProperties[2])).intValue();
   }

   private final boolean getIgnoreNaked() {
      return (Boolean)ignoreNaked.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getIgnoreCreative() {
      return (Boolean)ignoreCreative.computeObject(this, $$delegatedProperties[4]);
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      System.out.println(sendPacket.getPacket().getClass().getSimpleName());
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      System.out.println(receivePacket.getPacket().getClass().getSimpleName());
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      Player player = PlayerTargetUtil.INSTANCE.computePlayer(256.0F, AutoCrystal.TargetSort.FOV, this.getIgnoreNaked(), this.getIgnoreCreative());
      if (player != null) {
         Player player2 = player;

         Vec3 vec3 = switch (BowAimbot.WhenMappings.values[this.getPredict().ordinal()]) {
            case 1 -> PlayerPredictionService.INSTANCE.fetchBoundingBox(player2, this.getPredictTicks());
            case 2 -> PlayerPredictionService.INSTANCE.fetchBoundingBox(player2, this.getPredictTicks());
            case 3 -> this.computeVec3((Entity)player2);
            default -> throw new NoWhenBranchMatchedException();
         };
         vec3 = vec3.add(0.0, 0.9, 0.0);
         RotationManager.defaultImpl(RotationManager.INSTANCE, Rotation.vectorUtil.computeRotation(vec3), 0, 2, null);
      }
   }

   static {
   }

   public static enum Predict {
      SMART,
      CUSTOM,
      OFF;

      private static final BowAimbot.Predict[] predicts = $values();
      private static final EnumEntries<BowAimbot.Predict> enumEntries = EnumEntriesKt.enumEntries(predicts);

      private Predict() {
      }

      @NotNull
      public static EnumEntries<BowAimbot.Predict> getEnumEntries() {
         return enumEntries;
      }

      private static final BowAimbot.Predict[] $values() {
         return new BowAimbot.Predict[]{SMART, CUSTOM, OFF};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         Module[] modules = new Module[1];
         int[] values2 = new int[BowAimbot.Predict.values().length];

         try {
            values2[BowAimbot.Predict.SMART.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[BowAimbot.Predict.CUSTOM.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[BowAimbot.Predict.OFF.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         values = values2;
      }
   }
}