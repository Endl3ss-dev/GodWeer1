// deobf by pusia
package su.catlean.client.features.modules.player;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.ClosedRange;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.RotationManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.MathUtil;

public final class AntiAim extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AntiAim.class, "yawMode", "getYawMode()Lsu/catlean/c3;", 0), new PropertyReference1Impl(AntiAim.class, "pitchMode", "getPitchMode()Lsu/catlean/c3;", 0), new MutablePropertyReference1Impl(AntiAim.class, "yawDelay", "getYawDelay()I", 0), new MutablePropertyReference1Impl(AntiAim.class, "pitchDelay", "getPitchDelay()I", 0), new MutablePropertyReference1Impl(AntiAim.class, "yawDelta", "getYawDelta()F", 0), new MutablePropertyReference1Impl(AntiAim.class, "pitchDelta", "getPitchDelta()F", 0), new MutablePropertyReference1Impl(AntiAim.class, "yawOffset", "getYawOffset()F", 0), new PropertyReference1Impl(AntiAim.class, "offGroundOnly", "getOffGroundOnly()Z", 0) };
   @NotNull
   public static final AntiAim INSTANCE = new AntiAim();
   @NotNull
   private static final EnumSetting yaw = SettingFactory.defaultImpl(INSTANCE, "yaw", AntiAim.Yaw.OFF, null, null, 12, null);
   @NotNull
   private static final EnumSetting pitch = SettingFactory.defaultImpl(INSTANCE, "pitch", AntiAim.Yaw.OFF, null, null, 12, null);
   @NotNull
   private static final IntSetting pitchDelay = SettingFactory.defaultImpl(INSTANCE, "pitch-delay", 1, new IntRange(1, 180), null, null, 24, null);
   @NotNull
   private static final IntSetting yawDelay = SettingFactory.defaultImpl(INSTANCE, "yaw-delay", 1, new IntRange(1, 180), null, null, 24, null);
   @NotNull
   private static final FloatSetting yawDelta = SettingFactory.defaultImpl(
      INSTANCE, "yaw-delta", 60.0F, RangesKt.rangeTo(-360.0F, 360.0F), null, null, 24, null
   );
   @NotNull
   private static final FloatSetting pitchDelta = SettingFactory.defaultImpl(
      INSTANCE, "pitch-delta", 10.0F, RangesKt.rangeTo(-90.0F, 90.0F), null, null, 24, null
   );
   @NotNull
   private static final FloatSetting yawOffset = SettingFactory.defaultImpl(
      INSTANCE, "yaw-offset", 0.0F, RangesKt.rangeTo(-180.0F, 180.0F), null, null, 24, null
   );
   @NotNull
   private static final BooleanSetting offGroundOnly = SettingFactory.defaultImpl(INSTANCE, "off-ground-only", true, null, null, 12, null);
   private static float amount;
   private static float amount2;
   private static float amount3;
   private static float amount4;

   private AntiAim() {
      super("anti-aim", SubCategories.getSubCategory10(), null, 4, null);
   }

   private final AntiAim.Yaw getYawMode() {
      return (AntiAim.Yaw)yaw.computeObject(this, $$delegatedProperties[0]);
   }

   private final AntiAim.Yaw getPitchMode() {
      return (AntiAim.Yaw)pitch.computeObject(this, $$delegatedProperties[1]);
   }

   private final int getYawDelay() {
      return ((Number)pitchDelay.computeObject(this, $$delegatedProperties[2])).intValue();
   }

   private final int getPitchDelay() {
      return ((Number)yawDelay.computeObject(this, $$delegatedProperties[3])).intValue();
   }

   private final float getYawDelta() {
      return ((Number)yawDelta.computeObject(this, $$delegatedProperties[4])).floatValue();
   }

   private final float getPitchDelta() {
      return ((Number)pitchDelta.computeObject(this, $$delegatedProperties[5])).floatValue();
   }

   private final float getYawOffset() {
      return ((Number)yawOffset.computeObject(this, $$delegatedProperties[6])).floatValue();
   }

   private final boolean getOffGroundOnly() {
      return (Boolean)offGroundOnly.computeObject(this, $$delegatedProperties[7]);
   }

   @Flow(
      priority = 10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (!MinecraftHolder.getMinecraft().options.keyAttack.isDown() && !MinecraftHolder.getMinecraft().options.keyUse.isDown()) {
         if (!MinecraftHolder.getLocalPlayer().onGround() || !this.getOffGroundOnly()) {
            if (this.getPitchMode() == AntiAim.Yaw.RANDOM && MinecraftHolder.getLocalPlayer().tickCount % this.getPitchDelay() == 0) {
               amount2 = MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(90.0F, -90.0F), false, 2, null);
            }

            if (this.getYawMode() == AntiAim.Yaw.RANDOM && MinecraftHolder.getLocalPlayer().tickCount % this.getYawDelay() == 0) {
               amount = MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(0.0F, 360.0F), false, 2, null);
            }

            if (this.getYawMode() == AntiAim.Yaw.SPIN && MinecraftHolder.getLocalPlayer().tickCount % this.getYawDelay() == 0) {
               amount = amount + this.getYawDelta();
               if (amount > 360.0F) {
                  amount = 0.0F;
               }

               if (amount < 0.0F) {
                  amount = 360.0F;
               }
            }

            if (this.getPitchMode() == AntiAim.Yaw.SPIN && MinecraftHolder.getLocalPlayer().tickCount % this.getPitchDelay() == 0) {
               amount2 = amount2 + this.getPitchDelta();
               if (amount2 > 90.0F) {
                  amount2 = -90.0F;
               }

               if (amount2 < -90.0F) {
                  amount2 = 90.0F;
               }
            }

            if (this.getPitchMode() == AntiAim.Yaw.SIN) {
               amount3 = amount3 + this.getPitchDelay() / 10.0F;
               amount2 = (float)(MinecraftHolder.getLocalPlayer().getXRot() + this.getPitchDelta() * Math.sin(amount3));
               amount2 = RangesKt.coerceIn(amount2, -90.0F, 90.0F);
            }

            if (this.getYawMode() == AntiAim.Yaw.SIN) {
               amount4 = amount4 + this.getYawDelay() / 10.0F;
               amount = (float)(MinecraftHolder.getLocalPlayer().getYRot() + this.getYawDelta() * Math.sin(amount4) + this.getYawOffset());
            }

            if (this.getPitchMode() == AntiAim.Yaw.FIX) {
               amount2 = this.getPitchDelta();
            }

            if (this.getYawMode() == AntiAim.Yaw.FIX) {
               amount = this.getYawDelta();
            }

            if (this.getPitchMode() == AntiAim.Yaw.STATIC) {
               amount2 = MinecraftHolder.getLocalPlayer().getXRot() + this.getPitchDelta();
               amount2 = RangesKt.coerceIn(amount2, -90.0F, 90.0F);
            }

            if (this.getYawMode() == AntiAim.Yaw.STATIC) {
               amount = MinecraftHolder.getLocalPlayer().getYRot() % 360 + this.getYawDelta();
            }

            int tickCount = MinecraftHolder.getLocalPlayer().tickCount % (this.getYawDelay() * 2);
            int tickCount2 = MinecraftHolder.getLocalPlayer().tickCount % (this.getPitchDelay() * 2);
            if (this.getPitchMode() == AntiAim.Yaw.JITTER) {
               if (tickCount == 0) {
                  amount2 = this.getPitchDelta() / 2.0F;
               }

               if (tickCount == this.getPitchDelay()) {
                  amount2 = this.getPitchDelta() / -2.0F;
               }
            }

            if (this.getYawMode() == AntiAim.Yaw.JITTER) {
               if (tickCount2 == 0) {
                  amount = this.getYawDelta() / 2.0F + this.getYawOffset() + MinecraftHolder.getLocalPlayer().getYRot();
               }

               if (tickCount2 == this.getYawDelay()) {
                  amount = this.getYawDelta() / -2.0F + this.getYawOffset() + MinecraftHolder.getLocalPlayer().getYRot();
               }
            }

            float amount5 = this.getYawMode() != AntiAim.Yaw.OFF ? amount : MinecraftHolder.getLocalPlayer().getYRot();
            float amount22 = this.getPitchMode() != AntiAim.Yaw.OFF ? amount2 : MinecraftHolder.getLocalPlayer().getXRot();
            RotationManager.INSTANCE.updateRotation(new Rotation(amount5, amount22, false, null, 12, null), 0);
         }
      }
   }

   static {
   }

   public static enum Yaw {
      OFF,
      RANDOM,
      SPIN,
      SIN,
      FIX,
      STATIC,
      JITTER;

      private static final AntiAim.Yaw[] yaws = $values();
      private static final EnumEntries<AntiAim.Yaw> enumEntries = EnumEntriesKt.enumEntries(yaws);

      private Yaw() {
      }

      @NotNull
      public static EnumEntries<AntiAim.Yaw> getEnumEntries() {
         return enumEntries;
      }

      private static final AntiAim.Yaw[] $values() {
         return new AntiAim.Yaw[]{OFF, RANDOM, SPIN, SIN, FIX, STATIC, JITTER};
      }
   }
}