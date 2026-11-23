// deobf by pusia
package su.catlean.client.features.modules.equip;

import su.catlean.utils.KotlinCompat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Unit;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.InventorySnapshot;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.ActionSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.settings.ItemsSetting;
import su.catlean.api.setting.values.ItemsValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.ui.ClickGuiScreen;
import su.catlean.client.features.ui.panel.components.KitSelector;
import su.catlean.client.services.proxy.ProxyManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.network.PacketUtil;

public final class InventoryManager extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(InventoryManager.class, "openManager", "getOpenManager()Lsu/catlean/cr;", 0), new MutablePropertyReference1Impl(InventoryManager.class, "cleanerList", "getCleanerList()Lsu/catlean/api/setting/values/ItemsValue;", 0), new MutablePropertyReference1Impl(InventoryManager.class, "actionDelay", "getActionDelay()I", 0), new MutablePropertyReference1Impl(InventoryManager.class, "clicksPerAction", "getClicksPerAction()I", 0), new PropertyReference1Impl(InventoryManager.class, "cleaner", "getCleaner()Lsu/catlean/c9;", 0), new PropertyReference1Impl(InventoryManager.class, "cleanerDelay", "getCleanerDelay()I", 0), new PropertyReference1Impl(InventoryManager.class, "cleanChests", "getCleanChests()Z", 0) };
   @NotNull
   public static final InventoryManager INSTANCE = new InventoryManager();
   @NotNull
   private static final ActionSetting openManager = SettingFactory.defaultImpl(INSTANCE, "open-manager", () -> {
      ClickGuiScreen.INSTANCE.setPopupWindow(new KitSelector());
      return Unit.INSTANCE;
   }, null, null, 12, null);
   @NotNull
   private static final ItemsSetting cleanerList = SettingFactory.defaultImpl(INSTANCE, "cleaner-list", new ItemsValue(null, 1, null), null, null, 12, null);
   @NotNull
   private static final IntSetting actionDelay = SettingFactory.defaultImpl(INSTANCE, "action-delay", 0, new IntRange(0, 10), null, null, 24, null);
   @NotNull
   private static final IntSetting clickPerAction = SettingFactory.defaultImpl(INSTANCE, "click-per-action", 1, new IntRange(1, 3), null, null, 24, null);
   @NotNull
   private static final EnumSetting cleaner = SettingFactory.defaultImpl(INSTANCE, "cleaner", InventoryManager.Cleaner.OFF, null, null, 12, null);
   @NotNull
   private static final IntSetting cleanerDelay = SettingFactory.defaultImpl(
      INSTANCE, "cleaner-delay", 50, new IntRange(0, 500), null, () -> INSTANCE.getCleaner() != InventoryManager.Cleaner.OFF, 8, null
   );
   @NotNull
   private static final BooleanSetting cleanChests = SettingFactory.defaultImpl(
      INSTANCE, "clean-chests", false, null, () -> INSTANCE.getCleaner() != InventoryManager.Cleaner.OFF, 4, null
   );
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   private static boolean flag;
   private static int value;

   private InventoryManager() {
      super("inventory-manager", SubCategories.getSubCategory9(), null, 4, null);
   }

   private final ItemsValue getCleanerList() {
      return (ItemsValue)cleanerList.computeObject(this, $$delegatedProperties[1]);
   }

   private final int getActionDelay() {
      return ((Number)actionDelay.computeObject(this, $$delegatedProperties[2])).intValue();
   }

   private final int getClicksPerAction() {
      return ((Number)clickPerAction.computeObject(this, $$delegatedProperties[3])).intValue();
   }

   private final InventoryManager.Cleaner getCleaner() {
      return (InventoryManager.Cleaner)cleaner.computeObject(this, $$delegatedProperties[4]);
   }

   private final int getCleanerDelay() {
      return ((Number)cleanerDelay.computeObject(this, $$delegatedProperties[5])).intValue();
   }

   private final boolean getCleanChests() {
      return (Boolean)cleanChests.computeObject(this, $$delegatedProperties[6]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (value > 0) {
         int value2 = value;
         value = value2 + -1;
      } else if (!ProxyManager.INSTANCE.getInventoryDataSource().getInventorySnapshot().getItems().isEmpty()) {
         int value3 = 0;
         Iterator<Integer> iterator = this.fetchItem(MinecraftHolder.getLocalPlayer().containerMenu).iterator();

         while (iterator.hasNext()) {
            int value4 = ((Number)iterator.next()).intValue();
            ContainerUtil.defaultImpl(value4, 0, null, false, 14, null);
            if (++value3 >= this.getClicksPerAction()) {
               break;
            }
         }

         value = this.getActionDelay();
      }
   }

   private final int fetchItem(Item item, AbstractContainerMenu abstractContainerMenu, IntRange intRange) {
      if (Intrinsics.areEqual(abstractContainerMenu.getCarried().getItem(), item)) {
         return -2;
      } else {
         IntRange intRange2 = this.computeIntRange2(abstractContainerMenu.slots.size());
         int first = intRange.getFirst();
         int last = intRange.getLast();
         if (first <= last) {
            while (true) {
               if (Intrinsics.areEqual(abstractContainerMenu.getSlot(first).getItem().getItem(), item)
                  && !Intrinsics.areEqual(
                     ProxyManager.INSTANCE
                        .getInventoryDataSource()
                        .getInventorySnapshot()
                        .getItems()
                        .get(first - intRange.getFirst() - (intRange.getFirst() == 0 ? intRange2.getFirst() : 0)),
                     item
                  )) {
                  return first;
               }

               if (first == last) {
                  break;
               }

               first++;
            }
         }

         return -1;
      }
   }

   private final IntRange computeIntRange(int value) {
      return switch (value) {
         case 46 -> new IntRange(9, 44);
         case 63 -> new IntRange(0, 62);
         default -> new IntRange(0, 89);
      };
   }

   private final IntRange computeIntRange2(int value) {
      return switch (value) {
         case 46 -> new IntRange(9, 44);
         case 63 -> new IntRange(27, 62);
         default -> new IntRange(54, 89);
      };
   }

   private final ArrayList<Integer> fetchItem(AbstractContainerMenu abstractContainerMenu) {
      ArrayList<Integer> arrayList = new ArrayList();
      int size = abstractContainerMenu.slots.size();
      if (size != 46 && size != 63 && size != 90) {
         return arrayList;
      } else {
         Iterator iterator = ProxyManager.INSTANCE.getInventoryDataSource().getInventorySnapshot().getItems().keySet().iterator();

         while (iterator.hasNext()) {
            int value2 = ((Number)iterator.next()).intValue();
            Item item = (Item)ProxyManager.INSTANCE.getInventoryDataSource().getInventorySnapshot().getItems().get(value2);
            if (!Intrinsics.areEqual(item, Items.AIR)) {
               int first = value2 + this.computeIntRange2(size).getFirst();
               if (!Intrinsics.areEqual(((Slot)abstractContainerMenu.slots.get(first)).getItem().getItem(), item)) {
                  int item2 = this.fetchItem(item, abstractContainerMenu, this.computeIntRange(size));
                  if (item2 == -2) {
                     arrayList.add(first);
                  } else if (item2 != -1) {
                     arrayList.add(item2);
                     arrayList.add(first);
                     if (!Intrinsics.areEqual(((Slot)abstractContainerMenu.slots.get(first)).getItem().getItem(), Items.AIR)) {
                        arrayList.add(item2);
                     }
                  }
               }
            }
         }

         return arrayList;
      }
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      if (this.getCleaner() != InventoryManager.Cleaner.OFF) {
         boolean flag2 = MinecraftHolder.getMinecraft().gui.screen() instanceof InventoryScreen;
         if (MinecraftHolder.getLocalPlayer().containerMenu instanceof ChestMenu && this.getCleanChests()) {
            ChestMenu containerMenu = (ChestMenu)MinecraftHolder.getLocalPlayer().containerMenu;
            String string = MinecraftHolder.getMinecraft().gui.screen().getTitle().getString();
            int value2 = 0;

            for (int i = containerMenu.getContainer().getContainerSize(); value2 < i; value2++) {
               Slot slot = containerMenu.getSlot(value2);
               if (slot.hasItem()
                  && this.getCleanerList().getItems().contains(slot.getItem().getItem())
                  && !KotlinCompat.contains(string, "РђСѓРєС†РёРѕРЅ", false, 2, null)
                  && !KotlinCompat.contains(string, "РїРѕРєСѓРїРєРё", false, 2, null)) {
                  this.updateValue(value2);
               }
            }
         }

         if (this.getCleaner() == InventoryManager.Cleaner.INVENTORY && !flag2) {
            return;
         }

         if (this.getCleaner() == InventoryManager.Cleaner.NOT_IN_INVENTORY && flag2) {
            return;
         }

         for (int i2 = 0; i2 < 36; i2++) {
            if (this.getCleanerList().getItems().contains(MinecraftHolder.getLocalPlayer().getInventory().getItem(i2).getItem())) {
               this.updateValue(i2 < 9 ? i2 + 36 : i2);
            }
         }

         if (flag && stopWatch.testValue(this.getCleanerDelay() + 100)) {
            Packet serverboundContainerClosePacket = (Packet)(new ServerboundContainerClosePacket(MinecraftHolder.getLocalPlayer().containerMenu.containerId));
            PacketUtil.applySend(serverboundContainerClosePacket);
            flag = false;
         }
      }
   }

   @NotNull
   public final InventorySnapshot fetchItem(String name) {
      Map<Integer, Item> linkedHashMap = new LinkedHashMap();

      for (int i = 9; i < 45; i++) {
         linkedHashMap.put(i - 9, ((Slot)MinecraftHolder.getLocalPlayer().containerMenu.slots.get(i)).getItem().getItem());
      }

      return new InventorySnapshot(name, linkedHashMap);
   }

   private final void updateValue(int value) {
      if (stopWatch.testValue2(this.getCleanerDelay())) {
         ContainerUtil.defaultImpl(value, 1, ContainerInput.THROW, false, 8, null);
         flag = true;
      }
   }

   static {
   }

   public static enum Cleaner {
      INVENTORY,
      ALWAYS,
      NOT_IN_INVENTORY,
      OFF;

      private static final InventoryManager.Cleaner[] cleaners = $values();
      private static final EnumEntries<InventoryManager.Cleaner> enumEntries = EnumEntriesKt.enumEntries(cleaners);

      private Cleaner() {
      }

      @NotNull
      public static EnumEntries<InventoryManager.Cleaner> getEnumEntries() {
         return enumEntries;
      }

      private static final InventoryManager.Cleaner[] $values() {
         return new InventoryManager.Cleaner[]{INVENTORY, ALWAYS, NOT_IN_INVENTORY, OFF};
      }
   }
}