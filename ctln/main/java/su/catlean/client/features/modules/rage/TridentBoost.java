// deobf by pusia
package su.catlean.client.features.modules.rage;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.TridentUseEvent;
import su.catlean.api.event.events.player.TridentWeatherEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class TridentBoost extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(TridentBoost.class, "mode", "getMode()Lsu/catlean/v7;", 0), new PropertyReference1Impl(TridentBoost.class, "factor", "getFactor()F", 0), new PropertyReference1Impl(TridentBoost.class, "cooldown", "getCooldown$su_catlean()I", 0), new PropertyReference1Impl(TridentBoost.class, "anyWeather", "getAnyWeather()Z", 0) };
   @NotNull
   public static final TridentBoost INSTANCE = new TridentBoost();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", TridentBoost.Mode.Motion, null, null, 12, null);
   @NotNull
   private static final FloatSetting factor = SettingFactory.defaultImpl(INSTANCE, "factor", 1.0F, RangesKt.rangeTo(0.1F, 20.0F), null, null, 24, null);
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 10, new IntRange(0, 20), null, null, 24, null);
   @NotNull
   private static final BooleanSetting rainSpoof = SettingFactory.defaultImpl(INSTANCE, "rain-spoof", true, null, null, 12, null);

   private TridentBoost() {
      super("trident-boost", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final TridentBoost.Mode getMode() {
      return (TridentBoost.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final float getFactor() {
      return ((Number)factor.computeObject(this, $$delegatedProperties[1])).floatValue();
   }

   public final int getAnyWeather() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[2])).intValue();
   }

   private final boolean isFlag() {
      return (Boolean)rainSpoof.computeObject(this, $$delegatedProperties[3]);
   }

   @Flow
   public final void onTridentUse(TridentUseEvent e) {
      if (MinecraftHolder.getLocalPlayer().getTicksUsingItem() >= this.getAnyWeather()) {
         float tridentSpinAttackStrength = EnchantmentHelper.getTridentSpinAttackStrength(
            MinecraftHolder.getLocalPlayer().getUseItem(), (LivingEntity)MinecraftHolder.getLocalPlayer()
         );
         if ((this.isFlag() || MinecraftHolder.getLocalPlayer().isInWaterOrRain()) && tridentSpinAttackStrength > 0.0F) {
            float yRot = MinecraftHolder.getLocalPlayer().getYRot();
            float xRot = MinecraftHolder.getLocalPlayer().getXRot();
            float value = -((float)Math.sin(yRot * (float) (Math.PI / 180.0))) * (float)Math.cos(xRot * (float) (Math.PI / 180.0));
            float value2 = -((float)Math.sin(xRot * (float) (Math.PI / 180.0)));
            float value3 = (float)Math.cos(yRot * (float) (Math.PI / 180.0)) * (float)Math.cos(xRot * (float) (Math.PI / 180.0));
            float value4 = (float)Math.sqrt(value * value + value2 * value2 + value3 * value3);
            float factor2 = this.getMode() == TridentBoost.Mode.Factor ? this.getFactor() * 3.0F * ((1.0F + tridentSpinAttackStrength) / 4.0F) : this.getFactor();
            value *= factor2 / value4;
            value2 *= factor2 / value4;
            value3 *= factor2 / value4;
            MinecraftHolder.getLocalPlayer().push(value, value2, value3);
            MinecraftHolder.getLocalPlayer().startAutoSpinAttack(20, 8.0F, MinecraftHolder.getLocalPlayer().getUseItem());
            if (MinecraftHolder.getLocalPlayer().onGround()) {
               MinecraftHolder.getLocalPlayer().move(MoverType.SELF, new Vec3(0.0, 1.1999999F, 0.0));
            }

            Holder<SoundEvent> tridenT_THROW = EnchantmentHelper.pickHighestLevel(MinecraftHolder.getLocalPlayer().getUseItem(), EnchantmentEffectComponents.TRIDENT_SOUND)
               .orElse(SoundEvents.TRIDENT_THROW);
            MinecraftHolder.getClientLevel()
               .playSound(null, (Entity)MinecraftHolder.getLocalPlayer(), (SoundEvent)tridenT_THROW.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
         }
      }

      e.cancel();
   }

   @Flow
   public final void onTridentWeather(TridentWeatherEvent e) {
      if (this.isFlag()) {
         e.cancel();
      }
   }

   static {
   }

   public static enum Mode {
      Motion,
      Factor;

      private static final TridentBoost.Mode[] modes = $values();
      private static final EnumEntries<TridentBoost.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<TridentBoost.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final TridentBoost.Mode[] $values() {
         return new TridentBoost.Mode[]{Motion, Factor};
      }
   }
}