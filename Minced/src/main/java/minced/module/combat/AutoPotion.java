/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.Hand
 */
package minced.module.combat;
import minced.core.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.EventUpdate;
import minced.core.ServerDetector;
import minced.core.Rotation;
import minced.core.InventoryUtil;
import minced.core.TimerUtil;
import minced.core.AimRotation;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public class AutoPotion
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    BooleanSetting field6018;
    int field6019;
    boolean field6020;
    BooleanSetting field6021;
    BooleanSetting field6022;
    MultiBooleanSetting field6023 = new MultiBooleanSetting(field6037, new BooleanSetting(field6038, true), new BooleanSetting(field6039, true), new BooleanSetting(field6040, true));
    static String field6024;
    static String field6025;
    static String field6026;
    static String field6027;
    static String field6028;
    static String field6029;
    BooleanSetting field6030 = new BooleanSetting(field6041, true);
    static float field6031;
    static float field6032;
    static float field6033;
    static float field6034;
    static String field6035;
    static String field6036;
    static String field6037;
    static String field6038;
    static String field6039;
    static String field6040;
    static String field6041;
    static String field6042;
    static String field6043;
    static String field6044;

    private void method1428(List param1, boolean param2, RegistryEntry param3) {
        int v5;
        if (!param2) {
            return;
        }
        if (AutoPotion.I.player.hasStatusEffect(param3)) {
            return;
        }
        int v4 = InventoryUtil.method3324(true, false, true, false, (StatusEffect)param3.value());
        if (v4 != -1) {
            param1.add(v4);
            return;
        }
        if (!((Boolean)this.field6018.getValue()).booleanValue() && (v5 = InventoryUtil.method3324(false, false, true, false, (StatusEffect)param3.value())) != -1) {
            param1.add(v5);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.field6019 = 20;
        this.field6020 = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.field6020 = false;
    }

    @EventHandler(2)
    private void method1429(EventUpdate param1) {
        boolean v82;
        if (((Boolean)this.field6021.getValue()).booleanValue() && this.field6020) {
            return;
        }
        ++this.field6019;
        ArrayList v2 = new ArrayList();
        if (((Boolean)this.field6022.getValue()).booleanValue()) {
            this.method1428(v2, this.field6023.method1898(field6024), StatusEffects.STRENGTH);
            this.method1428(v2, this.field6023.method1898(field6025), StatusEffects.SPEED);
            this.method1428(v2, this.field6023.method1898(field6026), StatusEffects.FIRE_RESISTANCE);
        } else {
            this.method1428(v2, this.field6023.method1898(field6027), StatusEffects.SPEED);
            this.method1428(v2, this.field6023.method1898(field6028), StatusEffects.STRENGTH);
            this.method1428(v2, this.field6023.method1898(field6029), StatusEffects.FIRE_RESISTANCE);
        }
        if (((Boolean)this.field6030.getValue()).booleanValue() && !ServerDetector.method2682() || this.field6019 < 20 || TimerUtil.method3544(field6031) || v2.isEmpty()) {
            return;
        }
        if (((Boolean)this.field6018.getValue()).booleanValue() && !(v82 = v2.stream().allMatch(param0 -> (Integer)param0 < 9))) {
            ArrayList<Integer> v4 = new ArrayList<Integer>();
            for (Object v6 : v2) {
                if ((Integer)v6 >= 9) continue;
                v4.add((Integer)v6);
            }
            if (v4.isEmpty()) {
                return;
            }
            v2 = v4;
        }
        float v82_134 = ThreadLocalRandom.current().nextFloat(field6032, field6033);
        AimRotation.method367(new Rotation(AutoPotion.I.player.getYaw(), field6034), v82_134, 1, 10);
        Rotation clsClass547 = new Rotation((Entity)AutoPotion.I.player);
        if (clsClass547.method3141(Rotation.method3142()) > 1.0f) {
            return;
        }
        int v4 = AutoPotion.I.player.getInventory().selectedSlot;
        boolean v5 = false;
        for (Integer v7 : (Iterable<Integer>)(v2)) {
            if (v7 < 9) {
                AutoPotion.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(v7.intValue()));
                AutoPotion.I.interactionManager.interactItem((PlayerEntity)AutoPotion.I.player, Hand.MAIN_HAND);
                v5 = true;
                continue;
            }
            if (((Boolean)this.field6018.getValue()).booleanValue()) continue;
            AutoPotion.I.interactionManager.clickSlot(AutoPotion.I.player.currentScreenHandler.syncId, v7.intValue(), v4, SlotActionType.SWAP, (PlayerEntity)AutoPotion.I.player);
            AutoPotion.I.interactionManager.interactItem((PlayerEntity)AutoPotion.I.player, Hand.MAIN_HAND);
            AutoPotion.I.interactionManager.clickSlot(AutoPotion.I.player.currentScreenHandler.syncId, v7.intValue(), v4, SlotActionType.SWAP, (PlayerEntity)AutoPotion.I.player);
            v5 = true;
        }
        AutoPotion.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(v4));
        if (v5) {
            this.field6019 = 0;
            this.field6020 = true;
            if (((Boolean)this.field6021.getValue()).booleanValue()) {
                this.toggle();
            }
        }
    }

    public AutoPotion() {
        super(field6035, field6036, Combat.Combat);
        this.field6021 = new BooleanSetting(field6042, false);
        this.field6018 = new BooleanSetting(field6043, false);
        this.field6022 = new BooleanSetting(field6044, true);
        this.addSettings(this.field6023, this.field6030, this.field6021, this.field6018, this.field6022);
    }

    static {
        field6035 = "Auto Potion";
        field6036 = "Автоматически использует зелья, бросая их под себя";
        field6037 = "Бросать";
        field6038 = "Скорость";
        field6039 = "Сила";
        field6040 = "Огнестойкость";
        field6041 = "Только при пвп";
        field6042 = "Отключать после использования";
        field6043 = "Только с хотбара";
        field6044 = "Приоритет силы";
        field6024 = "Сила";
        field6025 = "Скорость";
        field6026 = "Огнестойкость";
        field6027 = "Скорость";
        field6028 = "Сила";
        field6029 = "Огнестойкость";
        field6031 = 0.5f;
        field6032 = 275.0f;
        field6033 = 444.0f;
        field6034 = 90.0f;
    }
}



