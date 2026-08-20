/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
 *  net.minecraft.client.gui.screen.ingame.GenericContainerScreen
 *  net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.EquippableComponent
 *  net.minecraft.component.type.NbtComponent
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ArmorItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.math.MathHelper
 */
package minced.module.combat;
import minced.core.EventHandler;

import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.EventUpdate;
import minced.core.EventPacket;
import minced.core.EventSlotClick;
import minced.core.InventoryUtil;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.MathHelper;

public class NoSlotChange
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    MultiBooleanSetting field1617 = new MultiBooleanSetting(field1627, new BooleanSetting(field1628, true), new BooleanSetting(field1629, false), new BooleanSetting(field1630, false), new BooleanSetting(field1631, false), new BooleanSetting(field1632, false));
    static String field1618;
    static String field1619;
    static String field1620;
    static String field1621;
    static String field1622;
    static String field1623;
    static String field1624;
    static String field1625;
    static String field1626;
    static String field1627;
    static String field1628;
    static String field1629;
    static String field1630;
    static String field1631;
    static String field1632;
    static String field1633;

    private boolean method526(ItemStack param1) {
        if (param1 == null || param1.isEmpty()) {
            return false;
        }
        if (param1.contains(DataComponentTypes.EQUIPPABLE)) {
            EquippableComponent v2 = (EquippableComponent)param1.get(DataComponentTypes.EQUIPPABLE);
            return v2 != null && v2.slot() == EquipmentSlot.CHEST;
        }
        return false;
    }

    @EventHandler(2)
    public void method527(EventUpdate param1) {
        boolean v4;
        if (NoSlotChange.I.currentScreen instanceof GenericContainerScreen || NoSlotChange.I.currentScreen instanceof CreativeInventoryScreen || NoSlotChange.I.currentScreen instanceof ShulkerBoxScreen) {
            return;
        }
        ItemStack v2 = NoSlotChange.I.player.currentScreenHandler.getCursorStack();
        int v3 = InventoryUtil.method3318();
        boolean bl = v4 = v3 != -1;
        if (this.field1617.method1898(field1618).booleanValue() && InventoryUtil.method3336(Items.ELYTRA) == -1 && v4 && v2.getItem() == Items.ELYTRA) {
            NoSlotChange.I.interactionManager.clickSlot(NoSlotChange.I.player.currentScreenHandler.syncId, v3, 1, SlotActionType.PICKUP, (PlayerEntity)NoSlotChange.I.player);
        }
        if (this.field1617.method1898(field1619).booleanValue() && InventoryUtil.method3315() == -1 && v4 && v2.getItem() instanceof ArmorItem && this.method526(v2)) {
            NoSlotChange.I.interactionManager.clickSlot(NoSlotChange.I.player.currentScreenHandler.syncId, v3, 1, SlotActionType.PICKUP, (PlayerEntity)NoSlotChange.I.player);
        }
        if (this.field1617.method1898(field1620).booleanValue() && InventoryUtil.method3336(Items.PLAYER_HEAD) == -1 && v4 && v2.getItem() == Items.PLAYER_HEAD) {
            NoSlotChange.I.interactionManager.clickSlot(NoSlotChange.I.player.currentScreenHandler.syncId, v3, 1, SlotActionType.PICKUP, (PlayerEntity)NoSlotChange.I.player);
        }
        if (this.field1617.method1898(field1621).booleanValue() && this.method528(v2)) {
            NoSlotChange.I.interactionManager.clickSlot(NoSlotChange.I.player.currentScreenHandler.syncId, v3, 1, SlotActionType.PICKUP, (PlayerEntity)NoSlotChange.I.player);
        }
    }

    private boolean method528(ItemStack param1) {
        NbtComponent v52;
        if (param1 == null || param1.isEmpty()) {
            return false;
        }
        NbtComponent v3 = (NbtComponent)param1.getOrDefault(DataComponentTypes.CUSTOM_DATA, (Object)NbtComponent.DEFAULT);
        boolean v2 = !v3.isEmpty() && v3.contains(field1622) ? true : !(v52 = (NbtComponent)param1.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, (Object)NbtComponent.DEFAULT)).isEmpty() && v52.contains(field1623);
        boolean v52_116 = param1.contains(DataComponentTypes.CUSTOM_MODEL_DATA);
        return v2 && v52_116;
    }

    @EventHandler(2)
    public void method529(EventPacket param1) {
        Packet v42;
        if (this.field1617.method1898(field1624).booleanValue() && (v42 = param1.method2467()) instanceof UpdateSelectedSlotS2CPacket) {
            UpdateSelectedSlotS2CPacket v2 = (UpdateSelectedSlotS2CPacket)v42;
            int v5 = v2.slot();
            if (param1.method2466() && v5 != NoSlotChange.I.player.getInventory().selectedSlot) {
                int v42_126 = MathHelper.clamp((int)(NoSlotChange.I.player.getInventory().selectedSlot >= 8 ? NoSlotChange.I.player.getInventory().selectedSlot - 1 : NoSlotChange.I.player.getInventory().selectedSlot + 1), (int)0, (int)8);
                NoSlotChange.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(v42_126));
                NoSlotChange.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(NoSlotChange.I.player.getInventory().selectedSlot));
                param1.method2298(true);
            }
        }
    }

    public NoSlotChange() {
        super(field1625, field1626, Combat.Combat);
        this.addSettings(this.field1617);
    }

    @EventHandler(2)
    public void method530(EventSlotClick param1) {
        Slot v2 = param1.net_minecraft_screen_slot_Slot_I();
        SlotActionType v3 = param1.net_minecraft_screen_slot_SlotActionType_I();
        if (v2 == null || v3 == SlotActionType.SWAP || v3 == SlotActionType.QUICK_MOVE || NoSlotChange.I.currentScreen instanceof GenericContainerScreen || NoSlotChange.I.currentScreen instanceof CreativeInventoryScreen || NoSlotChange.I.currentScreen instanceof ShulkerBoxScreen) {
            return;
        }
        ItemStack v4 = v2.getStack();
        Item v5 = v4.getItem();
        if (((Boolean)this.field1617.method1899(1).getValue()).booleanValue() && v5 == Items.ELYTRA) {
            param1.method2298(true);
        }
        if (((Boolean)this.field1617.method1899(2).getValue()).booleanValue() && v5 instanceof ArmorItem && this.method526(v4)) {
            param1.method2298(true);
        }
        if (((Boolean)this.field1617.method1899(3).getValue()).booleanValue() && v5 == Items.PLAYER_HEAD) {
            param1.method2298(true);
        }
        if (this.field1617.method1898(field1633).booleanValue() && this.method528(v4)) {
            param1.method2298(true);
        }
    }

    static {
        field1625 = "No Slot Change";
        field1626 = "Исправляет ошибки, связанные со сменой предметов в инвентаре";
        field1627 = "На что работать";
        field1628 = "Не свапать слоты";
        field1629 = "Не выкидывать элитру";
        field1630 = "Не выкидывать нагрудник";
        field1631 = "Не выкидывать шар";
        field1632 = "Не выкидывать осколки";
        field1633 = "Не выкидывать осколки";
        field1618 = "Не выкидывать элитру";
        field1619 = "Не выкидывать нагрудник";
        field1620 = "Не выкидывать шар";
        field1621 = "Не выкидывать осколки";
        field1624 = "Не свапать слоты";
        field1622 = "PublicBukkitValues";
        field1623 = "PublicBukkitValues";
    }
}



