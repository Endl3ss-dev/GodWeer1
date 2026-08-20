/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 */
package minced.module.misc;
import minced.core.EventHandler;

import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.EventUpdate;
import minced.core.Module;
import minced.enums.Combat;
import minced.mixin.accessor.IMinecraftClient;
import net.minecraft.client.MinecraftClient;

public class TapeMouse
extends Module {
    static String field6063;
    static String field6064;
    static String field6065;
    static String field6066;
    static ModeSetting field6067;
    static String field6068;
    static float field6069;
    static SliderSetting field6070;
    static String field6071;
    static BooleanSetting field6072;
    static MinecraftClient I = MinecraftClient.getInstance();
    int field6073 = 0;
    static String field6074;
    static String field6075;
    static String field6076;
    static String field6077;

    public static void method1441() {
        field6067 = new ModeSetting(field6063, field6064, field6065, field6066);
        field6070 = new SliderSetting(field6068, 1.0f, 1.0f, field6069, 1.0f);
        field6072 = new BooleanSetting(field6071, true);
    }

    @EventHandler(2)
    public void method1442(EventUpdate param1) {
        if (TapeMouse.I.player == null) {
            return;
        }
        if (((Boolean)field6072.getValue()).booleanValue() && TapeMouse.I.player.isUsingItem()) {
            return;
        }
        ++this.field6073;
        if ((float)this.field6073 >= ((Float)field6070.getValue()).floatValue()) {
            if (field6067.isMode(field6074)) {
                ((IMinecraftClient)I).onAttack();
            } else if (field6067.isMode(field6075)) {
                ((IMinecraftClient)I).onItemUse();
            }
            this.field6073 = 0;
        }
    }

    public TapeMouse() {
        super(field6076, field6077, Combat.Miscellaneous);
        this.addSettings(field6067, field6070, field6072);
    }

    static {
        field6076 = "Tape Mouse";
        field6077 = "Автоматически воспроизводит клик мыши ";
        field6074 = "Левая";
        field6075 = "Правая";
        field6063 = "Клавиша";
        field6064 = "Левая";
        field6065 = "Левая";
        field6066 = "Правая";
        field6068 = "Задержка";
        field6069 = 30.0f;
        field6071 = "Не бить если ешь";
        TapeMouse.method1441();
    }
}



