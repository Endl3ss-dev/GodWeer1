// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionfc;
import su.catlean.api.event.events.render.EquipArmEvent;
import su.catlean.api.event.events.render.SwingArmEvent;
import su.catlean.api.event.events.render.SwingDurationEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.NotificationManager;
import su.catlean.gofra.Flow;

public final class SwingAnimations extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(SwingAnimations.class, "mode", "getMode()Lsu/catlean/fi;", 0), new MutablePropertyReference1Impl(SwingAnimations.class, "speed", "getSpeed()I", 0) };
   @NotNull
   public static final SwingAnimations INSTANCE = new SwingAnimations();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", SwingAnimations.Mode.Default, null, null, 12, null);
   @NotNull
   private static final IntSetting speed = SettingFactory.defaultImpl(INSTANCE, "speed", 12, new IntRange(0, 64), null, null, 24, null);
   private static float amount;

   private SwingAnimations() {
      super("swing-animations", SubCategories.getSubCategory6(), null, 4, null);
   }

   private final SwingAnimations.Mode getMode() {
      return (SwingAnimations.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final int getSpeed() {
      return ((Number)speed.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   @Flow
   private final void onSwingDuration(SwingDurationEvent swingDurationEvent) {
      swingDurationEvent.setValue(this.getSpeed());
      swingDurationEvent.cancel();
   }

   @Flow
   private final void onEquipArm(EquipArmEvent equipArmEvent) {
      if (equipArmEvent.getArm() == HumanoidArm.RIGHT) {
         amount = equipArmEvent.getProgress();
      }
   }

   @Flow
   private final void onSwingArm(SwingArmEvent swingArmEvent) {
      if (swingArmEvent.getArm() == HumanoidArm.RIGHT) {
         swingArmEvent.getMatrices().translate(-0.56F, 0.52F - amount * -0.6F, 0.72F);
         swingArmEvent.cancel();
         switch (SwingAnimations.WhenMappings.values[this.getMode().ordinal()]) {
            case 1:
               this.render2(swingArmEvent.getMatrices(), swingArmEvent.getArm(), 0.0F);
               ViewModel.INSTANCE.render(swingArmEvent.getMatrices());
               this.render(swingArmEvent.getMatrices(), swingArmEvent.getArm(), swingArmEvent.getSwingProgress());
               ViewModel.INSTANCE.render2(swingArmEvent.getMatrices());
               break;
            case 2:
               this.render2(swingArmEvent.getMatrices(), swingArmEvent.getArm(), 0.0F);
               ViewModel.INSTANCE.render(swingArmEvent.getMatrices());
               double value = ((Number)NotificationManager.Notification.Type.OUT_ELASTIC.getCallback().invoke((double)swingArmEvent.getSwingProgress()))
                  .doubleValue();
               swingArmEvent.getMatrices().mulPose((Quaternionfc)Axis.XP.rotationDegrees((float)value * -90.0F));
               ViewModel.INSTANCE.render2(swingArmEvent.getMatrices());
               break;
            case 3:
               this.render2(swingArmEvent.getMatrices(), swingArmEvent.getArm(), 0.0F);
               ViewModel.INSTANCE.render(swingArmEvent.getMatrices());
               double value2 = ((Number)NotificationManager.Notification.Type.OUT_ELASTIC.getCallback().invoke((double)swingArmEvent.getSwingProgress()))
                  .doubleValue();
               swingArmEvent.getMatrices().mulPose((Quaternionfc)Axis.XP.rotationDegrees((float)value2 * -360.0F));
               ViewModel.INSTANCE.render2(swingArmEvent.getMatrices());
               break;
            case 4:
               this.render2(swingArmEvent.getMatrices(), swingArmEvent.getArm(), 0.0F);
               ViewModel.INSTANCE.render(swingArmEvent.getMatrices());
               double value3 = ((Number)NotificationManager.Notification.Type.OUT_SINE.getCallback().invoke((double)swingArmEvent.getSwingProgress()))
                  .doubleValue();
               swingArmEvent.getMatrices().mulPose((Quaternionfc)Axis.XP.rotationDegrees((float)value3 * -360.0F));
               ViewModel.INSTANCE.render2(swingArmEvent.getMatrices());
               break;
            case 5:
               this.render2(swingArmEvent.getMatrices(), swingArmEvent.getArm(), 0.0F);
               ViewModel.INSTANCE.render(swingArmEvent.getMatrices());
               double value4 = ((Number)NotificationManager.Notification.Type.OUT_BACK.getCallback().invoke((double)swingArmEvent.getSwingProgress()))
                  .doubleValue();
               swingArmEvent.getMatrices().mulPose((Quaternionfc)Axis.XP.rotationDegrees((float)value4 * -360.0F));
               ViewModel.INSTANCE.render2(swingArmEvent.getMatrices());
               break;
            case 6:
               float swingProgress = (float)Math.sin(Mth.sqrt(swingArmEvent.getSwingProgress()) * (float) Math.PI);
               this.render2(swingArmEvent.getMatrices(), swingArmEvent.getArm(), 0.0F);
               ViewModel.INSTANCE.render(swingArmEvent.getMatrices());
               swingArmEvent.getMatrices().mulPose((Quaternionfc)Axis.XP.rotationDegrees(50.0F));
               swingArmEvent.getMatrices().mulPose((Quaternionfc)Axis.YP.rotationDegrees(-30.0F * (1.0F - swingProgress) - 30.0F));
               swingArmEvent.getMatrices().mulPose((Quaternionfc)Axis.ZP.rotationDegrees(110.0F));
               ViewModel.INSTANCE.render2(swingArmEvent.getMatrices());
               break;
            case 7:
               float swingProgress2 = (float)Math.sin(Mth.sqrt(swingArmEvent.getSwingProgress()) * (float) Math.PI);
               swingArmEvent.getMatrices().translate(0.0, 0.0, 0.0);
               this.render2(swingArmEvent.getMatrices(), swingArmEvent.getArm(), 0.0F);
               ViewModel.INSTANCE.render(swingArmEvent.getMatrices());
               swingArmEvent.getMatrices().mulPose((Quaternionfc)Axis.XP.rotationDegrees(50.0F));
               swingArmEvent.getMatrices().mulPose((Quaternionfc)Axis.YP.rotationDegrees(-60.0F * swingProgress2 - 50));
               swingArmEvent.getMatrices().mulPose((Quaternionfc)Axis.ZP.rotationDegrees(110.0F));
               ViewModel.INSTANCE.render2(swingArmEvent.getMatrices());
               break;
            case 8:
               float swingProgress3 = (float)Math.sin(Mth.sqrt(swingArmEvent.getSwingProgress()) * (float) Math.PI);
               this.render2(swingArmEvent.getMatrices(), swingArmEvent.getArm(), 0.0F);
               ViewModel.INSTANCE.render(swingArmEvent.getMatrices());
               swingArmEvent.getMatrices().mulPose((Quaternionfc)Axis.XP.rotationDegrees(50.0F));
               swingArmEvent.getMatrices().mulPose((Quaternionfc)Axis.YP.rotationDegrees(-60.0F));
               swingArmEvent.getMatrices().mulPose((Quaternionfc)Axis.ZP.rotationDegrees(110.0F + 20.0F * swingProgress3));
               ViewModel.INSTANCE.render2(swingArmEvent.getMatrices());
               break;
            case 9:
               float swingProgress4 = (float)Math.sin(Mth.sqrt(swingArmEvent.getSwingProgress()) * (float) Math.PI);
               this.render2(swingArmEvent.getMatrices(), swingArmEvent.getArm(), 0.0F);
               swingArmEvent.getMatrices().translate(0.0, 0.0, -swingProgress4 / 4.0);
               ViewModel.INSTANCE.render(swingArmEvent.getMatrices());
               swingArmEvent.getMatrices().mulPose((Quaternionfc)Axis.XP.rotationDegrees(-120.0F));
               ViewModel.INSTANCE.render2(swingArmEvent.getMatrices());
               break;
            case 10:
               float swingProgress5 = (float)Math.sin(Mth.sqrt(swingArmEvent.getSwingProgress()) * (float) Math.PI);
               this.render2(swingArmEvent.getMatrices(), swingArmEvent.getArm(), 0.0F);
               ViewModel.INSTANCE.render(swingArmEvent.getMatrices());
               swingArmEvent.getMatrices().mulPose((Quaternionfc)Axis.XP.rotationDegrees(-((float)Math.sin(swingArmEvent.getSwingProgress() * 3.0F)) * 60.0F));
               swingArmEvent.getMatrices().mulPose((Quaternionfc)Axis.ZP.rotationDegrees(-60.0F * swingProgress5));
               ViewModel.INSTANCE.render2(swingArmEvent.getMatrices());
               break;
            case 11:
               if (swingArmEvent.getSwingProgress() > 0.0F) {
                  float swingProgress6 = (float)Math.sin(Mth.sqrt(swingArmEvent.getSwingProgress()) * (float) Math.PI);
                  swingArmEvent.getMatrices().translate(0.56F, amount * -0.2F - 0.5F, -0.7F);
                  ViewModel.INSTANCE.render(swingArmEvent.getMatrices());
                  swingArmEvent.getMatrices().mulPose((Quaternionfc)Axis.YP.rotationDegrees(45.0F));
                  swingArmEvent.getMatrices().mulPose((Quaternionfc)Axis.XP.rotationDegrees(swingProgress6 * -85.0F));
                  if (ViewModel.INSTANCE.getEnabled()) {
                     swingArmEvent.getMatrices()
                        .translate(-0.1F * ViewModel.INSTANCE.getScale(), 0.28F * ViewModel.INSTANCE.getScale(), 0.2F * ViewModel.INSTANCE.getScale());
                  } else {
                     swingArmEvent.getMatrices().translate(-0.1F, 0.28F, 0.2F);
                  }

                  swingArmEvent.getMatrices().mulPose((Quaternionfc)Axis.XP.rotationDegrees(-85.0F));
                  ViewModel.INSTANCE.render2(swingArmEvent.getMatrices());
               } else {
                  float swingProgress7 = -0.4F * (float)Math.sin(Mth.sqrt(swingArmEvent.getSwingProgress()) * (float) Math.PI);
                  float swingProgress8 = 0.2F * (float)Math.sin(Mth.sqrt(swingArmEvent.getSwingProgress()) * (float) (Math.PI * 2));
                  float swingProgress9 = -0.2F * (float)Math.sin(swingArmEvent.getSwingProgress() * (float) Math.PI);
                  swingArmEvent.getMatrices().translate(swingProgress7, swingProgress8, swingProgress9);
                  this.render2(swingArmEvent.getMatrices(), swingArmEvent.getArm(), amount);
                  this.render(swingArmEvent.getMatrices(), swingArmEvent.getArm(), swingArmEvent.getSwingProgress());
               }
         }
      }
   }

   private final void render(PoseStack poseStack, HumanoidArm humanoidArm, float amount) {
      int value = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
      float amount2 = (float)Math.sin(amount * amount * (float) Math.PI);
      poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(value * (45.0F + amount2 * -20.0F)));
      float amount3 = (float)Math.sin(Mth.sqrt(amount) * (float) Math.PI);
      poseStack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(value * amount3 * -20.0F));
      poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(amount3 * -80.0F));
      poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(value * -45.0F));
   }

   private final void render2(PoseStack poseStack, HumanoidArm humanoidArm, float amount) {
      poseStack.translate((humanoidArm == HumanoidArm.RIGHT ? 1 : -1) * 0.56F, -0.52F + amount * -0.6F, -0.72F);
   }

   static {
   }

   public static enum Mode {
      Default,
      Bonk,
      Rubber,
      Akrien,
      Flip,
      Snap,
      Hook,
      Swipe,
      Tap,
      Inject,
      Slap;

      private static final SwingAnimations.Mode[] modes = $values();
      private static final EnumEntries<SwingAnimations.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<SwingAnimations.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final SwingAnimations.Mode[] $values() {
         return new SwingAnimations.Mode[]{Default, Bonk, Rubber, Akrien, Flip, Snap, Hook, Swipe, Tap, Inject, Slap};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[SwingAnimations.Mode.values().length];

         try {
            values2[SwingAnimations.Mode.Default.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[SwingAnimations.Mode.Bonk.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[SwingAnimations.Mode.Rubber.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values2[SwingAnimations.Mode.Flip.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         try {
            values2[SwingAnimations.Mode.Snap.ordinal()] = 5;
         } catch (NoSuchFieldError exception5) {
         }

         try {
            values2[SwingAnimations.Mode.Hook.ordinal()] = 6;
         } catch (NoSuchFieldError exception6) {
         }

         try {
            values2[SwingAnimations.Mode.Swipe.ordinal()] = 7;
         } catch (NoSuchFieldError exception7) {
         }

         try {
            values2[SwingAnimations.Mode.Tap.ordinal()] = 8;
         } catch (NoSuchFieldError exception8) {
         }

         try {
            values2[SwingAnimations.Mode.Inject.ordinal()] = 9;
         } catch (NoSuchFieldError exception9) {
         }

         try {
            values2[SwingAnimations.Mode.Slap.ordinal()] = 10;
         } catch (NoSuchFieldError exception10) {
         }

         try {
            values2[SwingAnimations.Mode.Akrien.ordinal()] = 11;
         } catch (NoSuchFieldError exception11) {
         }

         values = values2;
      }
   }
}