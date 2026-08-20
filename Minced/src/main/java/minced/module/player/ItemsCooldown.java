/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.item.Item
 *  sg.ec.Н$Ч
 */
package minced.module.player;
import minced.core.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventFinishUsing;
import minced.core.EventCooldownTracker;
import minced.core.EventRightClickItemCheck;
import minced.core.EventThrowPearl;
import minced.core.ServerDetector;
import minced.core.ClientState;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import sg.ec.n4;

public class ItemsCooldown
extends Module {
    static BooleanSetting field2398;
    Map field2399 = new HashMap();
    static float field2400;
    static MultiBooleanSetting field2401;
    static String field2402;
    static String field2403;
    static SliderSetting field2404;
    static SliderSetting field2405;
    static SliderSetting var_minced_core_ClsClass276_I;
    static SliderSetting field2406;
    static MinecraftClient var_net_minecraft_client_MinecraftClient_I = MinecraftClient.getInstance();
    static String field2407;
    static String field2408;
    static String field2409;
    static String field2410;
    static String field2411;
    static String field2412;
    static float field2413;
    static float field2414;
    static float field2415;
    static float field2416;
    static String field2417;
    static float field2418;
    static float field2419;
    static float field2420;
    static float field2421;
    static String field2422;
    static float field2423;
    static float field2424;
    static float field2425;
    static float field2426;
    static String field2427;
    static float field2428;
    static float field2429;
    static float field2430;
    static float field2431;
    static String field2432;

    @EventHandler(2)
    public void method643(EventCooldownTracker param1) {
        if (((Boolean)field2398.getValue()).booleanValue() && !ServerDetector.method2682()) {
            return;
        }
        Item v2 = param1.method2405().getItem();
        ClientState v3 = (ClientState)this.field2399.get(v2);
        if (v3 == null) {
            return;
        }
        n4.ch v2c = n4.ch.西((Item)v2);
        float v4 = v2c.ущ() * field2400;
        long v5 = v3.method3112();
        if ((float)v5 >= v4) {
            this.field2399.remove(v2);
            return;
        }
        param1.method2404((float)v5 / v4);
    }

    public ItemsCooldown() {
        super(field2402, field2403, Combat.Player);
        this.addSettings(field2401, field2404, field2405, var_minced_core_ClsClass276_I, field2406, field2398);
    }

    @EventHandler(2)
    public void method645(EventRightClickItemCheck param1) {
        if (((Boolean)field2398.getValue()).booleanValue() && !ServerDetector.method2682()) {
            return;
        }
        Item v2 = param1.method2430().getItem();
        n4.ch v3 = n4.ch.西((Item)v2);
        if (v3 != null && this.field2399.containsKey(v3.西()) && ItemsCooldown.var_net_minecraft_client_MinecraftClient_I.player.getItemCooldownManager().isCoolingDown(v3.西().getDefaultStack())) {
            param1.method2298(true);
        }
    }

    public static void method647() {
        field2401 = new MultiBooleanSetting(field2407, new BooleanSetting(field2408, true), new BooleanSetting(field2409, true), new BooleanSetting(field2410, true), new BooleanSetting(field2411, true));
        field2404 = new SliderSetting(field2412, field2413, field2414, field2415, field2416, () -> (Boolean)field2401.method1899(0).getValue());
        field2406 = new SliderSetting(field2417, field2418, field2419, field2420, field2421, () -> (Boolean)field2401.method1899(3).getValue());
        field2405 = new SliderSetting(field2422, field2423, field2424, field2425, field2426, () -> (Boolean)field2401.method1899(1).getValue());
        var_minced_core_ClsClass276_I = new SliderSetting(field2427, field2428, field2429, field2430, field2431, () -> (Boolean)field2401.method1899(2).getValue());
        field2398 = new BooleanSetting(field2432, true);
    }

    @EventHandler(2)
    public void method648(EventThrowPearl param1) {
        if (((Boolean)field2398.getValue()).booleanValue() && !ServerDetector.method2682()) {
            return;
        }
        Item v2 = param1.method2563().getItem();
        if (n4.ch.西((Item)v2) != null) {
            this.field2399.put(v2, new ClientState());
        }
    }

    @EventHandler(2)
    public void method651(EventFinishUsing param1) {
        if (((Boolean)field2398.getValue()).booleanValue() && !ServerDetector.method2682()) {
            return;
        }
        Item v2 = param1.method2341().getItem();
        if (n4.ch.西((Item)v2) != null) {
            this.field2399.put(v2, new ClientState());
        }
    }

    static {
        field2402 = "Items Cooldown";
        field2403 = "Отображает оставшееся время восстановления для предметов, имеющих перезарядку";
        field2400 = 1000.0f;
        field2407 = "Применять на";
        field2408 = "Золотое яблоко";
        field2409 = "Золотое зачарованное яблоко";
        field2410 = "Эндер жемчуг";
        field2411 = "Хорус";
        field2412 = "Задержка золотого яблока";
        field2413 = 4.5f;
        field2414 = 0.5f;
        field2415 = 15.0f;
        field2416 = 0.05f;
        field2417 = "Задержка золотого зачарованного яблока";
        field2418 = 4.5f;
        field2419 = 0.5f;
        field2420 = 15.0f;
        field2421 = 0.05f;
        field2422 = "Задержка эндер жемчуга";
        field2423 = 14.05f;
        field2424 = 0.5f;
        field2425 = 15.0f;
        field2426 = 0.05f;
        field2427 = "Задержка хоруса";
        field2428 = 2.3f;
        field2429 = 0.5f;
        field2430 = 15.0f;
        field2431 = 0.05f;
        field2432 = "Только при пвп";
        ItemsCooldown.method647();
    }
}



