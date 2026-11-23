// deobf by pusia
package su.catlean.client.features.modules.misc;

import kotlin.Unit;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.InventoryManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.ContainerUtil;

public final class AutoSoup extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AutoSoup.class, "health", "getHealth()F", 0), new MutablePropertyReference1Impl(AutoSoup.class, "switchMode", "getSwitchMode()Lsu/catlean/uv;", 0) };
   @NotNull
   public static final AutoSoup INSTANCE = new AutoSoup();
   @NotNull
   private static final FloatSetting minHealth = SettingFactory.defaultImpl(INSTANCE, "min-health", 5.0F, RangesKt.rangeTo(1.0F, 20.0F), null, null, 24, null);
   @NotNull
   private static final EnumSetting switchValue = SettingFactory.defaultImpl(INSTANCE, "switch", AutoSoup.Switch.SILENT_FULL, null, null, 12, null);

   private AutoSoup() {
      super("auto-soup", SubCategories.getSubCategory11(), null, 4, null);
   }

   private final float getHealth() {
      return ((Number)minHealth.computeObject(this, $$delegatedProperties[0])).floatValue();
   }

   private final AutoSoup.Switch getSwitchMode() {
      return (AutoSoup.Switch)switchValue.computeObject(this, $$delegatedProperties[1]);
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (MinecraftHolder.getLocalPlayer().getHealth() <= this.getHealth()) {
         InventoryManager.INSTANCE.checkOrdinal(Items.MUSHROOM_STEW, this.getSwitchMode(), () -> {
            ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
            return Unit.INSTANCE;
         });
      }
   }

   static {
   }

   public static enum Switch {
      NORMAL,
      SILENT,
      INVENTORY_SILENT,
      INVENTORY_NORMAL,
      SILENT_FULL,
      NORMAL_FULL;

      private static final AutoSoup.Switch[] switchValues = $values();
      private static final EnumEntries<AutoSoup.Switch> enumEntries = EnumEntriesKt.enumEntries(switchValues);

      private Switch() {
      }

      @NotNull
      public static EnumEntries<AutoSoup.Switch> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoSoup.Switch[] $values() {
         return new AutoSoup.Switch[]{NORMAL, SILENT, INVENTORY_SILENT, INVENTORY_NORMAL, SILENT_FULL, NORMAL_FULL};
      }
   }
}