/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  sLM.4ZXYEo
 *  sg.ec.2>
 */
package minced.core;

import java.util.ArrayList;
import java.util.List;
import minced.core.BindSetting;
import minced.core.ServerHelperBase;
import minced.core.ChatUtil;
import minced.enums.KeyCode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import sLM._4ZXYEo;
import sg.ec.CfrRenamed298;

public class ServerHelperSpooky
extends ServerHelperBase {
    static String чЩ;
    static String чд;
    static String ч7;

    static String field8260;
    boolean field8261;
    String field8262;
    String[] field8263 = new String[]{field8275, field8276, field8277, field8278, field8279, field8280, field8281};
    List field8264;
    String[] field8265 = new String[]{field8282, field8283, field8284, field8285, field8286, field8287, field8288};
    static String field8266;
    static String field8267;
    MinecraftClient var_net_minecraft_client_MinecraftClient_I;
    static String field8268;
    static float field8269;
    static String field8270;
    Item[] field8271 = new Item[]{Items.ENDER_EYE, Items.SUGAR, Items.DRIED_KELP, Items.NETHERITE_SCRAP, Items.FIRE_CHARGE, Items.SNOWBALL, Items.PHANTOM_MEMBRANE};
    boolean field8272;
    String field8273;
    Item var_net_minecraft_item_Item_I;
    static String field8274;
    static String field8275 = "Дезориентация";
    static String field8276 = "Явная пыль";
    static String field8277 = "Пласт";
    static String field8278 = "Трапка Spooky";
    static String field8279 = "Огненный смерч";
    static String field8280 = "Снежок заморозка";
    static String field8281 = "Божья аура";
    static String field8282 = "дезориентац";
    static String field8283 = "явная пыль";
    static String field8284 = "пласт";
    static String field8285 = "трапка";
    static String field8286 = "смерч";
    static String field8287 = "заморозк";
    static String field8288 = "божья аура";

    @Override
    public String method1895() {
        return field8260;
    }

    @Override
    public boolean method1889() {
        return this.field8261 && this.field8262 != null;
    }

    public int method2221() {
        if (this.field8262 == null) {
            return -1;
        }
        return this.method2223(this.field8262);
    }

    @Override
    public void method1890() {
        this.method2224();
    }

    @Override
    public List method1894(boolean param1) {
        ArrayList<CfrRenamed298> v2 = new ArrayList<CfrRenamed298>();
        for (int v3 = 0; v3 < this.field8263.length && v3 < this.field8264.size(); ++v3) {
            String v5;
            int v4 = this.method2223(this.field8265[v3]);
            if (v4 == -1) {
                v5 = field8266;
            } else if (param1 && v4 >= 9) {
                v5 = field8267;
            } else {
                ItemStack v6 = this.var_net_minecraft_client_MinecraftClient_I.player.getInventory().getStack(v4);
                if (this.var_net_minecraft_client_MinecraftClient_I.player.getItemCooldownManager().isCoolingDown(v6)) {
                    float v7 = this.var_net_minecraft_client_MinecraftClient_I.player.getItemCooldownManager().getCooldownProgress(v6, var_net_minecraft_client_MinecraftClient_I.getRenderTickCounter().getLastDuration());
                    v5 = String.format(field8268, Float.valueOf(v7 * field8269));
                } else {
                    v5 = field8270;
                }
            }
            int v6 = (Integer)((BindSetting)this.field8264.get(v3)).getValue();
            String v7 = v6 == -1 ? "" : KeyCode.getDisplayName(v6);
            String v8 = this.field8263[v3].length() > 8 ? this.field8263[v3].substring(0, 8) : this.field8263[v3];
            v2.add(new CfrRenamed298(this.field8263[v3], v8, v5, this.field8271[v3], v7, -1));
        }
        return v2;
    }

    private void method2222(String param1, String param2, Item param3) {
        int v4 = this.method2223(param2);
        if (v4 == -1) {
            ChatUtil.method3308(_4ZXYEo.иу("makeConcatWithConstants", new Object[]{}, (String)param1), new Object[0]);
            return;
        }
        if (this.field8272 && v4 >= 9) {
            ChatUtil.method3308(_4ZXYEo.иВ("makeConcatWithConstants", new Object[]{}, (String)param1), new Object[0]);
            return;
        }
        if (this.var_net_minecraft_client_MinecraftClient_I.player.getItemCooldownManager().isCoolingDown(param3.getDefaultStack())) {
            ChatUtil.method3308(_4ZXYEo.и0("makeConcatWithConstants", new Object[]{}, (String)param1), new Object[0]);
            return;
        }
        this.field8261 = true;
        this.field8273 = param1;
        this.field8262 = param2;
        this.var_net_minecraft_item_Item_I = param3;
    }

    private int method2223(String param1) {
        if (var_net_minecraft_client_MinecraftClient_I == null || this.var_net_minecraft_client_MinecraftClient_I.player == null || param1 == null || param1.isEmpty()) {
            return -1;
        }
        String v2 = param1.toLowerCase();
        for (int v3 = 0; v3 < 36; ++v3) {
            String v5;
            String v6;
            ItemStack v4 = this.var_net_minecraft_client_MinecraftClient_I.player.getInventory().getStack(v3);
            if (v4.isEmpty() || !(v6 = (v5 = v4.getName().getString()).replaceAll(field8274, "").toLowerCase()).contains(v2)) continue;
            return v3;
        }
        return -1;
    }

    public ServerHelperSpooky(List param1) {
        this.field8264 = param1;
    }

    public void method2224() {
        this.field8261 = false;
        this.field8273 = null;
        this.field8262 = null;
        this.var_net_minecraft_item_Item_I = null;
    }

    @Override
    public boolean method1888(int param1) {
        for (int v2 = 0; v2 < this.field8263.length && v2 < this.field8264.size(); ++v2) {
            if ((Integer)((BindSetting)this.field8264.get(v2)).getValue() == -1 || (Integer)((BindSetting)this.field8264.get(v2)).getValue() != param1) continue;
            this.method2222(this.field8263[v2], this.field8265[v2], this.field8271[v2]);
            return true;
        }
        return false;
    }

    public void method2225(boolean param1) {
        this.field8272 = param1;
    }

    static {
        field8260 = "SpookyTime";
        field8266 = "—";
        field8267 = "—";
        field8268 = "%.0fс";
        field8269 = 20.0f;
        field8270 = "✓";
        чЩ = "§c\u0001 не найден в инвентаре";
        чд = "§c\u0001 не в хотбаре";
        ч7 = "§c\u0001 на кулдауне";
        field8274 = "§[0-9a-fk-or]";
    }
}



