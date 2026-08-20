/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.item.Items
 */
package minced.module.player;
import minced.core.EventHandler;

import minced.core.BindSetting;
import minced.core.ModeSetting;
import minced.core.EventUpdate;
import minced.core.InventoryUtil;
import minced.core.EventKey;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public class ClickPearl
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    boolean O;
    ModeSetting field2228;
    static String field2229;
    BindSetting field2230 = new BindSetting(field2233);
    static String field2231;
    static String field2232;
    static String field2233;
    static String field2234;
    static String field2235;
    static String field2236;
    static String field2237;

    @EventHandler(2)
    public void method603(EventUpdate param1) {
        this.method604();
    }

    private void method604() {
        if (ClickPearl.I.player == null || ClickPearl.I.world == null || !this.O) {
            return;
        }
        if (ClickPearl.I.player.getItemCooldownManager().isCoolingDown(Items.ENDER_PEARL.getDefaultStack())) {
            this.O = false;
            return;
        }
        if (this.field2228.isMode(field2229)) {
            InventoryUtil.method3325(Items.ENDER_PEARL);
        } else {
            InventoryUtil.method3323(Items.ENDER_PEARL);
        }
        this.O = false;
    }

    @EventHandler(2)
    public void method605(EventKey param1) {
        if (param1.method3599() && !param1.method3594() && ClickPearl.I.currentScreen == null && param1.method3595() == ((Integer)this.field2230.getValue()).intValue()) {
            this.O = true;
        }
    }

    public ClickPearl() {
        super(field2231, field2232, Combat.Player);
        this.field2228 = new ModeSetting(field2234, field2235, field2236, field2237);
        this.addSettings(this.field2230, this.field2228);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.O = false;
    }

    static {
        field2231 = "Click Pearl";
        field2232 = "Throws an ender pearl by key bind";
        field2233 = "Кнопка нажатия";
        field2234 = "Мод";
        field2235 = "Default";
        field2236 = "Default";
        field2237 = "Legit";
        field2229 = "Default";
    }
}



