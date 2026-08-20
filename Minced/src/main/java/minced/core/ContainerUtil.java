/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.scoreboard.ScoreboardObjective
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.Hand
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.EventUpdate;
import minced.core.InventoryUtil;
import minced.enums.IDLE1;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import sLM._4ZXYEo;

public class ContainerUtil
{
    static String Цй;

    static IDLE1 field787;
    static int field788;
    static int field789;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field790;
    static String field791;
    static String field792;
    static int field793;
    static String field794;
    static String field795;

    private static void method235() {
        field787 = IDLE1.IDLE;
        field788 = -1;
        field789 = 0;
    }

    private void method236(int param1) {
        if (ContainerUtil.I.interactionManager == null || ContainerUtil.I.player == null || ContainerUtil.I.player.currentScreenHandler == null) {
            return;
        }
        ContainerUtil.I.interactionManager.clickSlot(ContainerUtil.I.player.currentScreenHandler.syncId, param1, 0, SlotActionType.PICKUP, (PlayerEntity)ContainerUtil.I.player);
        field789 = 0;
    }

    private void method237() {
        int v1 = InventoryUtil.method3336(Items.COMPASS);
        if (v1 == -1 || ContainerUtil.I.player == null) {
            return;
        }
        ContainerUtil.I.player.getInventory().selectedSlot = v1;
        ContainerUtil.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(v1));
        ContainerUtil.I.interactionManager.interactItem((PlayerEntity)ContainerUtil.I.player, Hand.MAIN_HAND);
    }

    public static int method238() {
        if (ContainerUtil.I.world == null) {
            return -1;
        }
        for (ScoreboardObjective v1 : ContainerUtil.I.world.getScoreboard().getObjectives()) {
            String v2 = v1.getDisplayName().getString();
            if (!v2.contains(field790)) continue;
            return Integer.parseInt(v2.split(field791)[1].replace(field792, ""));
        }
        return -1;
    }

    public static void method239() {
        field793 = -1;
    }

    public static void method240() {
        if (ContainerUtil.I.player == null) {
            return;
        }
        int n = field788 = field793 != -1 ? field793 : ContainerUtil.method238();
        if (field788 <= 0) {
            return;
        }
        field787 = IDLE1.SENT_HUB;
        ContainerUtil.I.player.networkHandler.sendCommand(field794);
        field789 = 0;
    }

    public static void method241(int param0) {
        field793 = param0;
    }

    /*
     * Enabled aggressive block sorting
     */
    @EventHandler(2)
    public void method242(EventUpdate param1) {
        if (field787 == IDLE1.IDLE) {
            return;
        }
        if (field787 == IDLE1.SENT_HUB && ++field789 >= 25) {
            this.method237();
            field787 = IDLE1.USED_COMPASS;
            field789 = 0;
            return;
        }
        if (field787 == IDLE1.USED_COMPASS && !(ContainerUtil.I.currentScreen instanceof HandledScreen)) {
            if (field789 < 25) return;
            this.method237();
            field789 = 0;
            return;
        }
        Screen v3 = ContainerUtil.I.currentScreen;
        if (!(v3 instanceof HandledScreen)) return;
        HandledScreen v2 = (HandledScreen)v3;
        ScreenHandler v6 = v2.getScreenHandler();
        int v4 = 0;
        while (v4 < v6.slots.size()) {
            Slot v5 = (Slot)v6.slots.get(v4);
            if (!v5.getStack().isEmpty()) {
                if (field787 == IDLE1.USED_COMPASS && v5.getStack().getName().getString().contains(field795)) {
                    this.method236(v4);
                    field787 = IDLE1.CLICKED_GRIEF_SURVIVAL;
                    return;
                }
                if (field787 == IDLE1.CLICKED_GRIEF_SURVIVAL && v5.getStack().getName().getString().contains((CharSequence)((Object)_4ZXYEo.иЗ("makeConcatWithConstants", new Object[]{}, (int)field788))) && field789 >= 3) {
                    this.method236(v4);
                    ContainerUtil.method235();
                    return;
                }
            }
            ++v4;
        }
    }

    public static void method243() {
        field789 = 0;
        field787 = IDLE1.IDLE;
        field788 = -1;
        field793 = -1;
    }

    static {
        field789 = 0;
        field788 = -1;
        field793 = -1;
        field794 = "hub";
        field795 = "ГРИФЕРСКОЕ ВЫЖИВАНИЕ (1.16.5-1.20.4)";
        Цй = "ГРИФ #\u0001 (1.16.5+)";
        field790 = "ГРИФ #";
        field791 = "#";
        field792 = " ";
        ContainerUtil.method239();
        ContainerUtil.method243();
    }
}



