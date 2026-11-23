// deobf by pusia
package su.catlean.client.features.modules.equip;

import java.util.List;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.equipment.Equippable;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.basic.ElytraRecast;
import su.catlean.client.features.modules.rage.ElytraPlus;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.inventory.ItemTypeUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.player.PlayerUtil;
import su.catlean.utils.world.WorldUtil;

public final class AutoArmor extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AutoArmor.class, "head", "getHead()Lsu/catlean/_k;", 0), new PropertyReference1Impl(AutoArmor.class, "body", "getBody()Lsu/catlean/_k;", 0), new PropertyReference1Impl(AutoArmor.class, "tights", "getTights()Lsu/catlean/_k;", 0), new PropertyReference1Impl(AutoArmor.class, "feet", "getFeet()Lsu/catlean/_k;", 0), new PropertyReference1Impl(AutoArmor.class, "elytraPriority", "getElytraPriority()Lsu/catlean/sq;", 0), new PropertyReference1Impl(AutoArmor.class, "delay", "getDelay()I", 0), new PropertyReference1Impl(AutoArmor.class, "oldVersion", "getOldVersion()Z", 0), new PropertyReference1Impl(AutoArmor.class, "pauseInventory", "getPauseInventory()Z", 0), new PropertyReference1Impl(AutoArmor.class, "noMove", "getNoMove()Z", 0), new PropertyReference1Impl(AutoArmor.class, "ignoreCurse", "getIgnoreCurse()Z", 0), new PropertyReference1Impl(AutoArmor.class, "facePlaceBait", "getFacePlaceBait()Z", 0) };
   @NotNull
   public static final AutoArmor INSTANCE = new AutoArmor();
   @NotNull
   private static final EnumSetting head = SettingFactory.defaultImpl(INSTANCE, "head", AutoArmor.Head.PROTECTION, null, null, 12, null);
   @NotNull
   private static final EnumSetting body = SettingFactory.defaultImpl(INSTANCE, "body", AutoArmor.Head.PROTECTION, null, null, 12, null);
   @NotNull
   private static final EnumSetting tights = SettingFactory.defaultImpl(INSTANCE, "tights", AutoArmor.Head.PROTECTION, null, null, 12, null);
   @NotNull
   private static final EnumSetting feet = SettingFactory.defaultImpl(INSTANCE, "feet", AutoArmor.Head.PROTECTION, null, null, 12, null);
   @NotNull
   private static final EnumSetting elytraPriority = SettingFactory.defaultImpl(
      INSTANCE, "elytra-priority", AutoArmor.ElytraPriority.IGNORE, null, null, 12, null
   );
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 1, new IntRange(0, 10), null, null, 24, null);
   @NotNull
   private static final BooleanSetting oldVersion = SettingFactory.defaultImpl(INSTANCE, "old-version", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting pauseInventory = SettingFactory.defaultImpl(INSTANCE, "pause-inventory", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting noMove = SettingFactory.defaultImpl(INSTANCE, "no-move", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting ignoreCurse = SettingFactory.defaultImpl(INSTANCE, "ignore-curse", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting facePlaceBait = SettingFactory.defaultImpl(INSTANCE, "face-place-bait", false, null, null, 12, null);
   @NotNull
   private static PlayerUtil playerUtil = new PlayerUtil();
   @NotNull
   private static PlayerUtil playerUtil2 = new PlayerUtil();
   @NotNull
   private static final List items;

   private AutoArmor() {
      super("auto-armor", SubCategories.getSubCategory9(), null, 4, null);
   }

   private final AutoArmor.Head getHead() {
      return (AutoArmor.Head)head.computeObject(this, $$delegatedProperties[0]);
   }

   private final AutoArmor.Head getBody() {
      return (AutoArmor.Head)body.computeObject(this, $$delegatedProperties[1]);
   }

   private final AutoArmor.Head getTights() {
      return (AutoArmor.Head)tights.computeObject(this, $$delegatedProperties[2]);
   }

   private final AutoArmor.Head getFeet() {
      return (AutoArmor.Head)feet.computeObject(this, $$delegatedProperties[3]);
   }

   private final AutoArmor.ElytraPriority getElytraPriority() {
      return (AutoArmor.ElytraPriority)elytraPriority.computeObject(this, $$delegatedProperties[4]);
   }

   private final int getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[5])).intValue();
   }

   private final boolean getOldVersion() {
      return (Boolean)oldVersion.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getPauseInventory() {
      return (Boolean)pauseInventory.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getNoMove() {
      return (Boolean)noMove.computeObject(this, $$delegatedProperties[8]);
   }

   private final boolean getIgnoreCurse() {
      return (Boolean)ignoreCurse.computeObject(this, $$delegatedProperties[9]);
   }

   private final boolean getFacePlaceBait() {
      return (Boolean)facePlaceBait.computeObject(this, $$delegatedProperties[10]);
   }

   @NotNull
   public final PlayerUtil getPlayerUtil() {
      return playerUtil;
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (MinecraftHolder.getMinecraft().gui.screen() == null || !this.getPauseInventory() || MinecraftHolder.getMinecraft().gui.screen() instanceof ChatScreen
         )
       {
         if (playerUtil.testValue(this.getDelay())) {
            Iterable items2 = items;

            for (Object object : items2) {
               AutoArmor.ArmorData armorData = (AutoArmor.ArmorData)object;
               armorData.applyGetInventory();
            }

            if (MinecraftHolder.getLocalPlayer().tickCount % 10 == 0 && this.getFacePlaceBait()) {
               ContainerUtil.applyHandleContainerInput(5, 0, ContainerInput.QUICK_MOVE, true);
               playerUtil2.update();
            }

            for (int i = 0; i < 36; i++) {
               ItemStack item = MinecraftHolder.getLocalPlayer().getInventory().getItem(i);
               int item2 = this.fetchItem(item);
               if (item2 > 0) {
                  for (AutoArmor.ArmorData item3 : (Iterable<su.catlean.client.features.modules.equip.AutoArmor.ArmorData>)items) {
                     if ((!this.getFacePlaceBait() || playerUtil2.testValue(1) || item3.getEquipmentSlot() != EquipmentSlot.HEAD)
                        && item3.getEquipmentSlot() == (ItemTypeUtil.testItemStack3(item) ? this.fetchIndex(item) : EquipmentSlot.CHEST)
                        && item2 > item3.getValue2()
                        && item2 > item3.getValue4()) {
                        item3.setValue3(i);
                        item3.setValue4(item2);
                     }
                  }
               }
            }

            for (AutoArmor.ArmorData item4 : (Iterable<su.catlean.client.features.modules.equip.AutoArmor.ArmorData>)items) {
               int value3 = item4.getValue3();
               if (value3 != -1) {
                  if ((item4.getValue2() == -1 || !this.getOldVersion()) && value3 < 9) {
                     su.catlean.client.services.InventoryManager.INSTANCE.updateValue(value3);
                     ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
                     Packet serverboundSwingPacket = (Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
                     PacketUtil.applySend(serverboundSwingPacket);
                     su.catlean.client.services.InventoryManager.INSTANCE.update();
                  } else {
                     if (WorldUtil.INSTANCE.isFlag4() && this.getNoMove()) {
                        return;
                     }

                     int value = value3 < 9 ? 36 + value3 : value3;
                     ContainerUtil.defaultImpl(value, 0, null, false, 14, null);
                     int value2 = item4.getValue() - 34 + (39 - item4.getValue()) * 2;
                     boolean flag2 = item4.getValue2() == -1;
                     Object object2 = null;
                     byte byteValue = 6;
                     boolean flag3 = flag2;
                     Object object3 = null;
                     byte byteValue2 = 0;
                     int value4 = value2;
                     ContainerUtil.defaultImpl(value4, byteValue2, (ContainerInput)object3, flag3, byteValue, object2);
                     if (item4.getValue2() != -1) {
                        ContainerUtil.defaultImpl(value, 0, null, true, 6, null);
                     }
                  }

                  playerUtil.update();
                  return;
               }
            }
         }
      }
   }

   private final int fetchItem(ItemStack itemStack) {
      if (ItemTypeUtil.testItemStack3(itemStack) || Intrinsics.areEqual(itemStack.getItem(), Items.ELYTRA)) {
         int value = 0;
         EquipmentSlot index = this.fetchIndex(itemStack);
         if (Intrinsics.areEqual(itemStack.getItem(), Items.ELYTRA)) {
            if (!ElytraPlus.INSTANCE.checkGetDamageValue(itemStack)) {
               return 0;
            }

            boolean flag = this.getElytraPriority() == AutoArmor.ElytraPriority.ELYTRA_PLUS
               && (ElytraRecast.INSTANCE.getEnabled() || ElytraPlus.INSTANCE.getEnabled());
            boolean flag2 = this.getElytraPriority() == AutoArmor.ElytraPriority.IGNORE
               && Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getInventory().getItem(38).getItem(), Items.ELYTRA);
            if (flag || flag2 || this.getElytraPriority() == AutoArmor.ElytraPriority.ALWAYS) {
               value = 999;
            }
         }

         byte byteValue = 1;
         byte byteValue2 = 1;
         switch (AutoArmor.WhenMappings.values[index.ordinal()]) {
            case 1:
               if (this.getHead() == AutoArmor.Head.PROTECTION) {
                  byteValue2 *= 2;
               } else {
                  byteValue *= 2;
               }
               break;
            case 2:
               if (this.getBody() == AutoArmor.Head.PROTECTION) {
                  byteValue2 *= 2;
               } else {
                  byteValue *= 2;
               }
               break;
            case 3:
               if (this.getTights() == AutoArmor.Head.PROTECTION) {
                  byteValue2 *= 2;
               } else {
                  byteValue *= 2;
               }
               break;
            case 4:
               if (this.getFeet() == AutoArmor.Head.PROTECTION) {
                  byteValue2 *= 2;
               } else {
                  byteValue *= 2;
               }
         }

         if (itemStack.isEnchanted()) {
            ItemEnchantments enchantmentsForCrafting = EnchantmentHelper.getEnchantmentsForCrafting(itemStack);
            Reference identifierItem = (Reference)((Registry)MinecraftHolder.getClientLevel().registryAccess().lookup(Enchantments.PROTECTION.registryKey()).get())
               .get(Enchantments.PROTECTION.identifier())
               .get();
            Reference identifierItem2 = (Reference)((Registry)MinecraftHolder.getClientLevel().registryAccess().lookup(Enchantments.BLAST_PROTECTION.registryKey()).get())
               .get(Enchantments.BLAST_PROTECTION.identifier())
               .get();
            Reference identifierItem3 = (Reference)((Registry)MinecraftHolder.getClientLevel().registryAccess().lookup(Enchantments.BINDING_CURSE.registryKey()).get())
               .get(Enchantments.BINDING_CURSE.identifier())
               .get();
            if (enchantmentsForCrafting.keySet().contains(identifierItem)) {
               value += enchantmentsForCrafting.getLevel((Holder)identifierItem) * byteValue2;
            }

            if (enchantmentsForCrafting.keySet().contains(identifierItem2)) {
               value += enchantmentsForCrafting.getLevel((Holder)identifierItem2) * byteValue;
            }

            if (enchantmentsForCrafting.keySet().contains(identifierItem3) && this.getIgnoreCurse()) {
               value = -999;
            }
         }

         if (itemStack.has(DataComponents.ATTRIBUTE_MODIFIERS)) {
            List modifiers;
            label97: {
               ItemAttributeModifiers attributE_MODIFIERS = (ItemAttributeModifiers)itemStack.get(DataComponents.ATTRIBUTE_MODIFIERS);
               if (attributE_MODIFIERS != null) {
                  modifiers = attributE_MODIFIERS.modifiers();
                  if (modifiers != null) {
                     break label97;
                  }
               }

               modifiers = CollectionsKt.emptyList();
            }

            for (Entry entry : (Iterable<net.minecraft.world.item.component.ItemAttributeModifiers.Entry>)modifiers) {
               if (Intrinsics.areEqual(entry.attribute(), Attributes.ARMOR)
                  || Intrinsics.areEqual(entry.attribute(), Attributes.ARMOR_TOUGHNESS) && entry.modifier().operation() != null) {
                  double amount = entry.modifier().amount();

                  double value2 = switch (AutoArmor.WhenMappings.values2[entry.modifier().operation().ordinal()]) {
                     case 1 -> amount;
                     case 2 -> amount * MinecraftHolder.getLocalPlayer().getAttributeBaseValue(entry.attribute());
                     case 3 -> amount * value;
                     default -> throw new NoWhenBranchMatchedException();
                  };
                  value += (int)value2;
               }
            }
         }

         return value;
      } else {
         return !itemStack.isEmpty() ? 0 : -1;
      }
   }

   private final EquipmentSlot fetchIndex(ItemStack itemStack) {
      if (itemStack.has(DataComponents.GLIDER)) {
         return EquipmentSlot.CHEST;
      } else {
         return switch (((Equippable)itemStack.get(DataComponents.EQUIPPABLE)).slot().getIndex()) {
            case 0 -> EquipmentSlot.FEET;
            case 1 -> EquipmentSlot.LEGS;
            case 2 -> EquipmentSlot.CHEST;
            default -> EquipmentSlot.HEAD;
         };
      }
   }

   public static final int computeValue(AutoArmor autoArmor, ItemStack itemStack) {
      return autoArmor.fetchItem(itemStack);
   }

   static {
      AutoArmor.ArmorData[] armorDatas = new AutoArmor.ArmorData[]{
         new AutoArmor.ArmorData(EquipmentSlot.FEET, 36, -1, -1, -1),
         new AutoArmor.ArmorData(EquipmentSlot.LEGS, 37, -1, -1, -1),
         new AutoArmor.ArmorData(EquipmentSlot.CHEST, 38, -1, -1, -1),
         new AutoArmor.ArmorData(EquipmentSlot.HEAD, 39, -1, -1, -1)
      };
      items = CollectionsKt.listOf(armorDatas);
   }

   public static final class ArmorData {
      @NotNull
      private final EquipmentSlot equipmentSlot;
      private final int value;
      private int value2;
      private int value3;
      private int value4;

      public ArmorData(EquipmentSlot equipmentSlot, int value, int value2, int value3, int value4) {
         this.equipmentSlot = equipmentSlot;
         this.value = value;
         this.value2 = value2;
         this.value3 = value3;
         this.value4 = value4;
      }

      @NotNull
      public final EquipmentSlot getEquipmentSlot() {
         return this.equipmentSlot;
      }

      public final int getValue() {
         return this.value;
      }

      public final int getValue2() {
         return this.value2;
      }

      public final void setValue2(int value) {
         this.value2 = value;
      }

      public final int getValue3() {
         return this.value3;
      }

      public final void setValue3(int value) {
         this.value3 = value;
      }

      public final int getValue4() {
         return this.value4;
      }

      public final void setValue4(int value) {
         this.value4 = value;
      }

      public final void applyGetInventory() {
         this.value2 = AutoArmor.computeValue(AutoArmor.INSTANCE, MinecraftHolder.getLocalPlayer().getInventory().getItem(this.value));
         this.value3 = -1;
         this.value4 = -1;
      }

      @NotNull
      public final EquipmentSlot getEquipmentSlot2() {
         return this.equipmentSlot;
      }

      public final int getValue5() {
         return this.value;
      }

      public final int getValue6() {
         return this.value2;
      }

      public final int getValue7() {
         return this.value3;
      }

      public final int getValue8() {
         return this.value4;
      }

      @NotNull
      @Override
      public String toString() {
         int value42 = this.value4;
         int value32 = this.value3;
         int value22 = this.value2;
         int value5 = this.value;
         EquipmentSlot equipmentSlot2 = this.equipmentSlot;
         return "ArmorData(equipmentSlot=" + equipmentSlot2 + ", armorSlot=" + value5 + ", prevProt=" + value22 + ", newSlot=" + value32 + ", newProtection=" + value42 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = this.equipmentSlot.hashCode();
         hashCode = hashCode * 31 + Integer.hashCode(this.value);
         hashCode = hashCode * 31 + Integer.hashCode(this.value2);
         hashCode = hashCode * 31 + Integer.hashCode(this.value3);
         return hashCode * 31 + Integer.hashCode(this.value4);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof AutoArmor.ArmorData armorData)) {
            return false;
         } else if (this.equipmentSlot != armorData.equipmentSlot) {
            return false;
         } else if (this.value != armorData.value) {
            return false;
         } else if (this.value2 != armorData.value2) {
            return false;
         } else {
            return this.value3 != armorData.value3 ? false : this.value4 == armorData.value4;
         }
      }
   }

   public static enum ElytraPriority {
      NONE,
      ALWAYS,
      ELYTRA_PLUS,
      IGNORE;

      private static final AutoArmor.ElytraPriority[] elytraPrioritys = $values();
      private static final EnumEntries<AutoArmor.ElytraPriority> enumEntries = EnumEntriesKt.enumEntries(elytraPrioritys);

      private ElytraPriority() {
      }

      @NotNull
      public static EnumEntries<AutoArmor.ElytraPriority> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoArmor.ElytraPriority[] $values() {
         return new AutoArmor.ElytraPriority[]{NONE, ALWAYS, ELYTRA_PLUS, IGNORE};
      }
   }

   public static enum Head {
      BLAST,
      PROTECTION;

      private static final AutoArmor.Head[] heads = $values();
      private static final EnumEntries<AutoArmor.Head> enumEntries = EnumEntriesKt.enumEntries(heads);

      private Head() {
      }

      @NotNull
      public static EnumEntries<AutoArmor.Head> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoArmor.Head[] $values() {
         return new AutoArmor.Head[]{BLAST, PROTECTION};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;
      public static final int[] values2;

      static {
         int[] values3 = new int[EquipmentSlot.values().length];

         try {
            values3[EquipmentSlot.HEAD.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values3[EquipmentSlot.BODY.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values3[EquipmentSlot.LEGS.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values3[EquipmentSlot.FEET.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         values = values3;
         values3 = new int[Operation.values().length];

         try {
            values3[Operation.ADD_VALUE.ordinal()] = 1;
         } catch (NoSuchFieldError exception5) {
         }

         try {
            values3[Operation.ADD_MULTIPLIED_BASE.ordinal()] = 2;
         } catch (NoSuchFieldError exception6) {
         }

         try {
            values3[Operation.ADD_MULTIPLIED_TOTAL.ordinal()] = 3;
         } catch (NoSuchFieldError exception7) {
         }

         values2 = values3;
      }
   }
}