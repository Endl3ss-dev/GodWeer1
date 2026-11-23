// deobf by pusia
package su.catlean.client.features.modules.rage;

import kotlin.NoWhenBranchMatchedException;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.MoveEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.world.WorldUtil;

public final class LevitationControl extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new PropertyReference1Impl(LevitationControl.class, "mode", "getMode()Lsu/catlean/mk;", 0)};
   @NotNull
   public static final LevitationControl INSTANCE = new LevitationControl();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", LevitationControl.Mode.NCP, null, null, 12, null);

   private LevitationControl() {
      super("levitation-control", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final LevitationControl.Mode getMode() {
      return (LevitationControl.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   @Flow
   public final void onMove(MoveEvent e) {
      if (MinecraftHolder.getLocalPlayer().hasEffect(MobEffects.LEVITATION)) {
         int amplifier = MinecraftHolder.getLocalPlayer().getEffect(MobEffects.LEVITATION).getAmplifier();
         switch (LevitationControl.WhenMappings.values[this.getMode().ordinal()]) {
            case 1:
               if (MinecraftHolder.getMinecraft().options.keyJump.isDown()) {
                  e.setY(e.getY() + (0.05 * (amplifier + 1) - e.getY()) * 0.2);
               } else if (MinecraftHolder.getMinecraft().options.keyShift.isDown()) {
                  e.setY(-0.08);
                  e.setX(0.0);
                  e.setZ(0.0);
               } else if (!WorldUtil.INSTANCE.isFlag4() && !(WorldUtil.INSTANCE.getAmount() > 0.0F)) {
                  e.setY(0.0);
               } else {
                  e.setY(0.004);
               }

               e.cancel();
               break;
            case 2:
               if (MinecraftHolder.getMinecraft().options.keyJump.isDown()) {
                  e.setY(0.1);
               } else if (MinecraftHolder.getMinecraft().options.keyShift.isDown()) {
                  e.setY(-0.1);
               } else {
                  e.setY(0.0);
               }

               e.cancel();
               break;
            case 3:
               MinecraftHolder.getLocalPlayer().removeEffect(MobEffects.LEVITATION);
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }
      }
   }

   static {
   }

   public static enum Mode {
      NCP,
      DEFAULT,
      ANTI;

      private static final LevitationControl.Mode[] modes = $values();
      private static final EnumEntries<LevitationControl.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<LevitationControl.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final LevitationControl.Mode[] $values() {
         return new LevitationControl.Mode[]{NCP, DEFAULT, ANTI};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[LevitationControl.Mode.values().length];

         try {
            values2[LevitationControl.Mode.NCP.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[LevitationControl.Mode.DEFAULT.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[LevitationControl.Mode.ANTI.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         values = values2;
      }
   }
}