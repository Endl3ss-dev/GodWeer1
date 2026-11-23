// deobf by pusia
package su.catlean.client.features.modules.player;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.rage.TridentBoost;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.LocalPlayerAccessor;
import su.catlean.utils.MinecraftHolder;
import net.minecraft.world.entity.animal.equine.Horse;

public final class PerfectDelay extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(PerfectDelay.class, "horse", "getHorse()Lsu/catlean/tv;", 0), new PropertyReference1Impl(PerfectDelay.class, "bow", "getBow()Z", 0), new PropertyReference1Impl(PerfectDelay.class, "crossbow", "getCrossbow()Z", 0), new PropertyReference1Impl(PerfectDelay.class, "trident", "getTrident()Z", 0) };
   @NotNull
   public static final PerfectDelay INSTANCE = new PerfectDelay();
   @NotNull
   private static final EnumSetting horse = SettingFactory.defaultImpl(INSTANCE, "horse", PerfectDelay.Horse.Legit, null, null, 12, null);
   @NotNull
   private static final BooleanSetting bow = SettingFactory.defaultImpl(INSTANCE, "bow", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting crossbow = SettingFactory.defaultImpl(INSTANCE, "crossbow", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting trident = SettingFactory.defaultImpl(INSTANCE, "trident", true, null, null, 12, null);

   private PerfectDelay() {
      super("perfect-delay", SubCategories.getSubCategory10(), null, 4, null);
   }

   private final PerfectDelay.Horse getHorse() {
      return (PerfectDelay.Horse)horse.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getBow() {
      return (Boolean)bow.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getCrossbow() {
      return (Boolean)crossbow.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getTrident() {
      return (Boolean)trident.computeObject(this, $$delegatedProperties[3]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (this.getTrident()
         && MinecraftHolder.getLocalPlayer().getUseItem().getItem() instanceof TridentItem
         && MinecraftHolder.getLocalPlayer().getTicksUsingItem() > (TridentBoost.INSTANCE.getEnabled() ? TridentBoost.INSTANCE.getAnyWeather() : 9)) {
         MinecraftHolder.getMultiPlayerGameMode().releaseUsingItem((Player)MinecraftHolder.getLocalPlayer());
      }

      if (MinecraftHolder.getLocalPlayer().getUseItem().getItem() instanceof CrossbowItem
         && this.getCrossbow()
         && MinecraftHolder.getLocalPlayer().getTicksUsingItem()
            >= 25 - 0.25 * this.fetchItemEnchantmentLevel(MinecraftHolder.getLocalPlayer().getUseItem()) * 20) {
         MinecraftHolder.getMultiPlayerGameMode().releaseUsingItem((Player)MinecraftHolder.getLocalPlayer());
      }

      if (MinecraftHolder.getLocalPlayer().getUseItem().getItem() instanceof BowItem
         && this.getBow()
         && MinecraftHolder.getLocalPlayer().getTicksUsingItem() > 19) {
         MinecraftHolder.getMultiPlayerGameMode().releaseUsingItem((Player)MinecraftHolder.getLocalPlayer());
      }

      if (MinecraftHolder.getLocalPlayer().getControlledVehicle() != null
         && MinecraftHolder.getLocalPlayer().getControlledVehicle() instanceof net.minecraft.world.entity.animal.equine.Horse) {
         if (this.getHorse() == PerfectDelay.Horse.Rage) {
            ((LocalPlayerAccessor)MinecraftHolder.getLocalPlayer()).setJumpRidingScale(1.0F);
         }

         if (this.getHorse() == PerfectDelay.Horse.Legit && MinecraftHolder.getLocalPlayer().getJumpRidingScale() >= 1.0F) {
            MinecraftHolder.getMinecraft().options.keyJump.setDown(false);
         }
      }
   }

   private final float fetchItemEnchantmentLevel(ItemStack itemStack) {
      return EnchantmentHelper.getItemEnchantmentLevel(
         (Holder)((Registry)MinecraftHolder.getClientLevel().registryAccess().lookup(Enchantments.QUICK_CHARGE.registryKey()).get())
            .get(Enchantments.QUICK_CHARGE.identifier())
            .get(),
         (ItemInstance)itemStack
      );
   }

   static {
   }

   public static enum Horse {
      Legit,
      Rage,
      Off;

      private static final PerfectDelay.Horse[] horses = $values();
      private static final EnumEntries<PerfectDelay.Horse> enumEntries = EnumEntriesKt.enumEntries(horses);

      private Horse() {
      }

      @NotNull
      public static EnumEntries<PerfectDelay.Horse> getEnumEntries() {
         return enumEntries;
      }

      private static final PerfectDelay.Horse[] $values() {
         return new PerfectDelay.Horse[]{Legit, Rage, Off};
      }
   }
}