/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.GenericContainerScreen
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.screen.GenericContainerScreenHandler
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.Hand
 *  sLM.4ZXYEo
 */
package minced.module.player;
import minced.core.EventHandler;

import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.EventUpdate;
import minced.core.EventSwapWorld;
import minced.core.ClientState;
import minced.core.InventoryUtil;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.LOAD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import sLM._4ZXYEo;

public class AutoJoiner
extends Module {
    static String cfr_renamed_366;

    static MinecraftClient I = MinecraftClient.getInstance();
    static String field6001;
    SliderSetting field6002;
    ClientState field6003;
    SliderSetting field6004;
    static long field6005;
    ModeSetting field6006 = new ModeSetting(field6010, field6011, field6012);
    static String field6007;
    static String field6008;
    static String field6009;
    static String field6010;
    static String field6011;
    static String field6012;
    static String field6013;
    static float field6014;
    static String field6015;
    static float field6016;
    static float field6017;

    /*
     * Enabled aggressive block sorting
     */
    @EventHandler(2)
    public void I(EventUpdate param1) {
        if (I == null || AutoJoiner.I.player == null || AutoJoiner.I.interactionManager == null) {
            return;
        }
        Screen v3 = AutoJoiner.I.currentScreen;
        if (!(v3 instanceof GenericContainerScreen)) {
            return;
        }
        GenericContainerScreen v2 = (GenericContainerScreen)v3;
        GenericContainerScreenHandler v3x = (GenericContainerScreenHandler)v2.getScreenHandler();
        boolean v4 = false;
        int v5 = 0;
        while (v5 < v3x.slots.size()) {
            Slot v6 = (Slot)v3x.slots.get(v5);
            if (!v6.getStack().isEmpty()) {
                String v7 = v6.getStack().getName().getString();
                if (v7.contains(field6001) && !v4) {
                    AutoJoiner.I.interactionManager.clickSlot(v3x.syncId, v6.id, 0, SlotActionType.PICKUP, (PlayerEntity)AutoJoiner.I.player);
                }
                if (v7.contains((CharSequence)((Object)_4ZXYEo.фГ("makeConcatWithConstants", new Object[]{}, (int)((Float)this.field6002.getValue()).intValue()))) && this.field6003.method3113((long)((Float)this.field6004.getValue()).intValue() * field6005)) {
                    AutoJoiner.I.interactionManager.clickSlot(v3x.syncId, v6.id, 0, SlotActionType.PICKUP, (PlayerEntity)AutoJoiner.I.player);
                    this.field6003.method3114();
                    v4 = true;
                }
            }
            ++v5;
        }
        return;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (this.field6006.isMode(field6007)) {
            this.method1426();
        }
    }

    private void method1426() {
        int v1 = InventoryUtil.method3336(Items.COMPASS);
        if (v1 == -1) {
            return;
        }
        AutoJoiner.I.player.getInventory().selectedSlot = v1;
        AutoJoiner.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(v1));
        AutoJoiner.I.interactionManager.interactItem((PlayerEntity)AutoJoiner.I.player, Hand.MAIN_HAND);
    }

    @Override
    public void onDisable() {
        this.field6003.method3114();
        super.onDisable();
    }

    public AutoJoiner() {
        super(field6008, field6009, Combat.Player);
        this.field6002 = new SliderSetting(field6013, 1.0f, 1.0f, field6014, 1.0f);
        this.field6004 = new SliderSetting(field6015, field6016, 1.0f, field6017, 1.0f);
        this.field6003 = new ClientState();
        this.addSettings(this.field6006, this.field6002, this.field6004);
    }

    @EventHandler(2)
    public void method1427(EventSwapWorld param1) {
        if (param1.method2421() == LOAD.LOAD) {
            this.toggle();
        }
    }

    static {
        field6008 = "Auto Joiner";
        field6009 = "Позволяет автоматически подключаться к выбранному серверу";
        field6010 = "Режим";
        field6011 = "ReallyWorld";
        field6012 = "ReallyWorld";
        field6013 = "Гриферский мир";
        field6014 = 54.0f;
        field6015 = "Скорость";
        field6016 = 3.0f;
        field6017 = 20.0f;
        field6001 = "ГРИФЕРСКОЕ ВЫЖИВАНИЕ (1.16.5-1.20.4)";
        cfr_renamed_366 = "ГРИФ #\u0001 (1.16.5+)";
        field6005 = 100L;
        field6007 = "ReallyWorld";
    }
}



