// deobf by pusia
package su.catlean.client.features.modules.equip;

import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.inventory.ItemTypeUtil;

public final class ToolSaver extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new PropertyReference1Impl(ToolSaver.class, "savePercent", "getSavePercent()I", 0)};
   @NotNull
   public static final ToolSaver INSTANCE = new ToolSaver();
   @NotNull
   private static final IntSetting savePercent = SettingFactory.defaultImpl(INSTANCE, "save-percent", 10, new IntRange(1, 50), null, null, 24, null);

   private ToolSaver() {
      super("tool-saver", SubCategories.getSubCategory9(), null, 4, null);
   }

   private final int getSavePercent() {
      return ((Number)savePercent.computeObject(this, $$delegatedProperties[0])).intValue();
   }

   @Flow
   public final void onPlayerUpdate(PlayerUpdateEvent e) {
      ItemStack mainHandItem = MinecraftHolder.getLocalPlayer().getMainHandItem();
      if (ItemTypeUtil.testItemStack2(mainHandItem)
         || ItemTypeUtil.testItemStack(mainHandItem)
         || mainHandItem.getItem() instanceof AxeItem
         || mainHandItem.getItem() instanceof HoeItem
         || mainHandItem.getItem() instanceof ShovelItem) {
         float maxDamage = mainHandItem.getMaxDamage() - mainHandItem.getDamageValue();
         int maxDamage2 = (int)(maxDamage / mainHandItem.getMaxDamage() * 100.0F);
         if (maxDamage2 <= this.getSavePercent()) {
            MinecraftHolder.getLocalPlayer().getInventory().setSelectedSlot((MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot() + 1) % 8);
            ChatUtil.defaultImpl(this, ChatUtil.defaultImpl(this, "switch-message", new Object[0], false, 4, null), false, 2, null);
         }
      }
   }

   static {
   }
}