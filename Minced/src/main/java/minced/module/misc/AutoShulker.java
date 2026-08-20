/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.Hand
 */
package minced.module.misc;
import minced.core.EventHandler;

import java.util.Arrays;
import java.util.List;
import minced.core.EventUpdate;
import minced.core.ChatUtil;
import minced.core.InventoryUtil;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public class AutoShulker
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    boolean field5942 = false;
    static String field5943;
    static String field5944;
    int field5945 = 0;
    int field5946 = 0;
    ItemStack field5947 = ItemStack.EMPTY;
    int field5948 = 0;
    static List field5949;
    static String field5950;
    static String field5951;

    private boolean method1411() {
        if (!(AutoShulker.I.currentScreen instanceof ShulkerBoxScreen)) {
            return false;
        }
        ScreenHandler v1 = AutoShulker.I.player.currentScreenHandler;
        if (v1 == null) {
            return false;
        }
        for (int v2 = 0; v2 < 27; ++v2) {
            Slot v3 = (Slot)v1.slots.get(v2);
            if (v3 != null && !v3.getStack().isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.field5942 = false;
        if (AutoShulker.I.currentScreen instanceof ShulkerBoxScreen) {
            AutoShulker.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(AutoShulker.I.player.currentScreenHandler.syncId));
            AutoShulker.I.player.closeHandledScreen();
        }
    }

    @EventHandler(2)
    public void method1412(EventUpdate param1) {
        if (AutoShulker.I.player == null || AutoShulker.I.world == null) {
            return;
        }
        int v2 = this.method1414();
        if (v2 == -1) {
            ChatUtil.method3308(field5943, new Object[0]);
            this.toggle();
            return;
        }
        if (!this.field5942) {
            int v3 = v2 < 9 ? v2 + 36 : v2;
            AutoShulker.I.interactionManager.clickSlot(AutoShulker.I.player.currentScreenHandler.syncId, v3, 7, SlotActionType.SWAP, (PlayerEntity)AutoShulker.I.player);
            AutoShulker.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(7));
            AutoShulker.I.interactionManager.interactItem((PlayerEntity)AutoShulker.I.player, Hand.MAIN_HAND);
            this.field5942 = true;
            return;
        }
        if (AutoShulker.I.currentScreen instanceof ShulkerBoxScreen && this.method1411()) {
            ChatUtil.method3308(field5944, new Object[0]);
            AutoShulker.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(AutoShulker.I.player.currentScreenHandler.syncId));
            AutoShulker.I.player.closeHandledScreen();
            this.field5942 = false;
            ++this.field5945;
            return;
        }
        if (!(AutoShulker.I.currentScreen instanceof ShulkerBoxScreen)) {
            return;
        }
        ScreenHandler v3 = AutoShulker.I.player.currentScreenHandler;
        ItemStack v4 = AutoShulker.I.player.getInventory().getStack(7);
        if (!v4.isEmpty() && v4.getItem() != Items.SHULKER_BOX) {
            Slot v5;
            if (v3 != null && v3.slots.size() > 34 && (v5 = (Slot)v3.slots.get(34)) != null && v5.hasStack()) {
                AutoShulker.I.interactionManager.clickSlot(v3.syncId, v5.id, 0, SlotActionType.QUICK_MOVE, (PlayerEntity)AutoShulker.I.player);
            }
            this.field5946 = 0;
            this.field5947 = v4;
        } else {
            ++this.field5946;
        }
        if (this.field5946 > 25) {
            AutoShulker.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(AutoShulker.I.player.currentScreenHandler.syncId));
            AutoShulker.I.player.closeHandledScreen();
            this.toggle();
        }
        ++this.field5948;
    }

    public static void method1413() {
        field5949 = Arrays.asList(Items.MAGENTA_SHULKER_BOX, Items.RED_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.PINK_SHULKER_BOX);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.field5942 = false;
        this.field5948 = 0;
        this.field5946 = 0;
        this.field5945 = 0;
        this.field5947 = ItemStack.EMPTY;
    }

    public AutoShulker() {
        super(field5950, field5951, Combat.Miscellaneous);
    }

    private int method1414() {
        for (int v1 = this.field5945; v1 < field5949.size(); ++v1) {
            int v2 = InventoryUtil.method3336((Item)field5949.get(v1));
            if (v2 == -1) continue;
            this.field5945 = v1;
            return v2;
        }
        return -1;
    }

    static {
        field5950 = "Auto Shulker";
        field5951 = "Автоматически складывает предметы в шалкер-бокс";
        field5943 = "Шалкера нет в инвентаре, не куда складывать";
        field5944 = "Шалкер заполнен, ищем второй, если такой есть";
        AutoShulker.method1413();
    }
}



