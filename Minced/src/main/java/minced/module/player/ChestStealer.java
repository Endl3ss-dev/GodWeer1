/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.GenericContainerScreenHandler
 *  net.minecraft.screen.HopperScreenHandler
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.collection.DefaultedList
 */
package minced.module.player;
import minced.core.EventHandler;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventUpdate;
import minced.core.ClientState;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

public class ChestStealer
extends Module {
    static String field5992 = "Chest Stealer";
    static String field5993 = "Автоматически крадёт предметы из сундуков";
    static String field5994 = "Задержка";
    static float field5995 = 100.0f;
    static float field5996 = 1000.0f;
    SliderSetting field5997 = new SliderSetting(field5994, field5995, 0.0f, field5996, 1.0f);
    static String field5998 = "Рандомизация";
    BooleanSetting field5999 = new BooleanSetting(field5998, false);
    ClientState field6000 = new ClientState();
    static MinecraftClient I = MinecraftClient.getInstance();

    public ChestStealer() {
        super(field5992, field5993, Combat.Player);
        this.addSettings(this.field5997, this.field5999);
    }

    private Optional method1421(List param1) {
        int v2 = param1.size() - ChestStealer.I.player.getInventory().main.size();
        List v3 = param1.subList(0, v2);
        List<Slot> v4 = v3.stream().filter(param0 -> !((Slot)param0).getStack().isEmpty()).filter(param0 -> !ChestStealer.I.player.getItemCooldownManager().isCoolingDown(((Slot)param0).getStack().getItem().getDefaultStack())).toList();
        if (v4.isEmpty()) {
            return Optional.empty();
        }
        return (Boolean)this.field5999.getValue() != false ? Optional.of(v4.get(ThreadLocalRandom.current().nextInt(v4.size()))) : v4.stream().findFirst();
    }

    @EventHandler(2)
    private void method1425(EventUpdate param1) {
        ScreenHandler v2 = ChestStealer.I.player.currentScreenHandler;
        if (v2 instanceof GenericContainerScreenHandler || v2 instanceof HopperScreenHandler) {
            DefaultedList v3 = v2.slots;
            if (this.field6000.method3113(((Float)this.field5997.getValue()).longValue())) {
                this.method1421((List)v3).ifPresent(param2 -> {
                    if (ChestStealer.I.player.currentScreenHandler == v2) {
                        ChestStealer.I.interactionManager.clickSlot(v2.syncId, ((Slot)param2).id, 0, SlotActionType.QUICK_MOVE, (PlayerEntity)ChestStealer.I.player);
                        this.field6000.method3114();
                    }
                });
            }
        }
    }
}



