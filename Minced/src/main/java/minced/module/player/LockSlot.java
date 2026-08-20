/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.item.Items
 *  net.minecraft.util.Formatting
 *  sLM.4ZXYEo
 */
package minced.module.player;
import minced.core.EventHandler;

import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.EventDropItem;
import minced.core.ServerDetector;
import minced.core.ChatUtil;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import sLM._4ZXYEo;

public class LockSlot
extends Module {
    static String cfr_renamed_390;

    static String field6108 = "Lock Slot";
    static String field6109 = "Блокирует возможность выкинуть предет в указанных слотах";
    static String field6110 = "Только при пвп";
    BooleanSetting field6111 = new BooleanSetting(field6110, true);
    static String field6112 = "Выберите слоты";
    static String field6113 = "1";
    static String field6114 = "2";
    static String field6115 = "3";
    static String field6116 = "4";
    static String field6117 = "5";
    static String field6118 = "6";
    static String field6119 = "7";
    static String field6120 = "8";
    static String field6121 = "9";
    MultiBooleanSetting field6122 = new MultiBooleanSetting(field6112, new BooleanSetting(field6113, false), new BooleanSetting(field6114, false), new BooleanSetting(field6115, false), new BooleanSetting(field6116, false), new BooleanSetting(field6117, false), new BooleanSetting(field6118, false), new BooleanSetting(field6119, false), new BooleanSetting(field6120, false), new BooleanSetting(field6121, false));
    static MinecraftClient I = MinecraftClient.getInstance();

    public LockSlot() {
        super(field6108, field6109, Combat.Player);
        this.addSettings(this.field6111, this.field6122);
    }

    @EventHandler(2)
    public void method1456(EventDropItem param1) {
        if (LockSlot.I.player == null || LockSlot.I.player.getInventory() == null || LockSlot.I.player.getMainHandStack().getItem() == Items.AIR) {
            return;
        }
        if (((Boolean)this.field6111.getValue()).booleanValue() && !ServerDetector.method2682()) {
            return;
        }
        int v2 = param1.method2419();
        BooleanSetting v3 = this.field6122.method1899(v2);
        if (((Boolean)v3.getValue()).booleanValue()) {
            param1.method2298(true);
            ChatUtil.method3308(_4ZXYEo.дЧ("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(Formatting.GRAY), (int)(v2 + 1)), new Object[0]);
        }
    }

    static {
        cfr_renamed_390 = "\u0001Выброс предмет из слота \u0001 был заблокирован";
    }
}



