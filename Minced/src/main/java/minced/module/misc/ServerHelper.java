/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 *  net.minecraft.network.packet.s2c.play.ExplosionS2CPacket
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.Hand
 */
package minced.module.misc;
import minced.core.EventHandler;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import minced.core.BindSetting;
import minced.core.ServerHelperReallyworld;
import minced.core.BooleanSetting;
import minced.core.ServerHelperFuntime;
import minced.core.ServerHelperBase;
import minced.core.EventInput;
import minced.core.ServerHelperSpooky;
import minced.core.ServerHelperHolyworld;
import minced.core.ModeSetting;
import minced.core.EventUpdate;
import minced.core.EventPacket;
import minced.core.EventSelectSlot;
import minced.core.EventBus;
import minced.core.EventKey;
import minced.core.Module;
import minced.enums.Combat;
import minced.mixin.accessor.IClientPlayerInteractionManagerMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public class ServerHelper
extends Module {
    ModeSetting field2492 = new ModeSetting(field2512, field2513, field2514, field2515, field2516, field2517);
    static String field2493;
    static String field2494;
    int field2495;
    int field2496;
    static MinecraftClient I = MinecraftClient.getInstance();
    int field2497;
    int field2498 = -1;
    int field2499 = -1;
    static String field2500;
    ServerHelperFuntime field2501;
    ServerHelperReallyworld field2502;
    static String field2503;
    static String field2504;
    static String field2505;
    Map field2506;
    static String field2507;
    static String field2508;
    static String field2509;
    static String field2510;
    static String field2511;
    static String field2512;
    static String field2513 = "SpookyTime";
    static String field2514;
    static String field2515;
    static String field2516;
    static String field2517;
    static String field2518;
    BooleanSetting field2519 = new BooleanSetting(field2518, false, () -> !this.field2492.isMode(field2504));
    static String field2520;
    BindSetting field2521 = new BindSetting(field2520, () -> this.field2492.isMode(field2588));
    static String field2522;
    BindSetting field2523 = new BindSetting(field2522, () -> this.field2492.isMode(field2612));
    static String field2524;
    BindSetting field2525 = new BindSetting(field2524, () -> this.field2492.isMode(field2614));
    static String field2526;
    BindSetting field2527 = new BindSetting(field2526, () -> this.field2492.isMode(field2585));
    static String field2528;
    BindSetting field2529 = new BindSetting(field2528, () -> this.field2492.isMode(field2611));
    static String field2530;
    BindSetting field2531 = new BindSetting(field2530, () -> this.field2492.isMode(field2586));
    static String field2532;
    BindSetting field2533 = new BindSetting(field2532, () -> this.field2492.isMode(field2617));
    static String field2534;
    BindSetting field2535 = new BindSetting(field2534, () -> this.field2492.isMode(field2608));
    static String field2536;
    BindSetting field2537 = new BindSetting(field2536, () -> this.field2492.isMode(field2508));
    static String field2538;
    BindSetting field2539 = new BindSetting(field2538, () -> this.field2492.isMode(field2609));
    static String field2540;
    BindSetting field2541 = new BindSetting(field2540, () -> this.field2492.isMode(field2507));
    static String field2542;
    BooleanSetting field2543 = new BooleanSetting(field2542, false, () -> this.field2492.isMode(field2603));
    static String field2544;
    BindSetting field2545 = new BindSetting(field2544, () -> this.field2492.isMode(field2584));
    static String field2546;
    BindSetting field2547 = new BindSetting(field2546, () -> this.field2492.isMode(field2589));
    static String field2548;
    BindSetting field2549 = new BindSetting(field2548, () -> this.field2492.isMode(field2615));
    static String field2550;
    BindSetting field2551 = new BindSetting(field2550, () -> this.field2492.isMode(field2587));
    static String field2552;
    BindSetting O = new BindSetting(field2552, () -> this.field2492.isMode(field2616));
    static String field2553;
    BindSetting field2554 = new BindSetting(field2553, () -> this.field2492.isMode(field2618));
    static String field2555;
    BindSetting field2556 = new BindSetting(field2555, () -> this.field2492.isMode(field2610));
    static String field2557;
    BooleanSetting field2558 = new BooleanSetting(field2557, false, () -> this.field2492.isMode(field2613));
    static String field2559;
    BooleanSetting field2560 = new BooleanSetting(field2559, false, () -> this.field2492.isMode(field2591));
    static String field2561;
    BindSetting field2562 = new BindSetting(field2561, () -> this.field2492.isMode(field2583));
    static String field2563;
    BindSetting field2564 = new BindSetting(field2563, () -> this.field2492.isMode(field2503));
    static String field2565;
    BindSetting field2566 = new BindSetting(field2565, () -> this.field2492.isMode(field2493));
    static String field2567;
    BindSetting field2568 = new BindSetting(field2567, () -> this.field2492.isMode(field2590));
    static String field2569;
    BindSetting field2570 = new BindSetting(field2569, () -> this.field2492.isMode(field2494));
    static String field2571;
    BindSetting field2572 = new BindSetting(field2571, () -> this.field2492.isMode(field2592));
    static String field2573;
    BindSetting field2574 = new BindSetting(field2573, () -> this.field2492.isMode(field2509));
    static String field2575;
    BooleanSetting field2576 = new BooleanSetting(field2575, false, () -> this.field2492.isMode(field2505));
    ServerHelperSpooky field2577;
    ServerHelperHolyworld field2578;
    static String field2579 = "LonyGrief";
    static String field2580 = "Reallyworld";
    static String field2581 = "SpookyTime";
    static String field2582 = "HolyWorld";
    static String field2583;
    static String field2584;
    static String field2585;
    static String field2586;
    static String field2587;
    static String field2588;
    static String field2589;
    static String field2590;
    static String field2591;
    static String field2592;
    static String field2593;
    static String field2594;
    static String field2595;
    static String field2596;
    static String field2597;
    static String field2598;
    static double field2599;
    static double field2600;
    static double field2601;
    static double field2602;
    static String field2603;
    static String field2604;
    static String field2605;
    static String field2606;
    static String field2607;
    static String field2608;
    static String field2609;
    static String field2610;
    static String field2611;
    static String field2612;
    static String field2613;
    static String field2614;
    static String field2615;
    static String field2616;
    static String field2617;
    static String field2618;

    private static float method659(ExplosionS2CPacket param0, String param1) throws NoSuchFieldException, IllegalAccessException {
        Field v2 = param0.getClass().getDeclaredField(param1);
        v2.setAccessible(true);
        return v2.getFloat(param0);
    }

    @EventHandler(2)
    private void method662(EventUpdate param1) {
        ServerHelperBase v2;
        if (this.field2495 > 0) {
            --this.field2495;
        }
        if (this.field2496 == 6 && ServerHelper.I.player.age >= this.field2497) {
            ServerHelper.I.interactionManager.interactItem((PlayerEntity)ServerHelper.I.player, Hand.MAIN_HAND);
            this.field2496 = 5;
            this.field2497 = ServerHelper.I.player.age + 2;
        }
        if (this.field2496 == 5 && ServerHelper.I.player.age >= this.field2497) {
            ServerHelper.I.player.getInventory().selectedSlot = this.field2498;
            ((IClientPlayerInteractionManagerMixin)ServerHelper.I.interactionManager).invokeSyncSelectedSlot();
            this.field2498 = -1;
            this.field2496 = 0;
        }
        if (this.field2496 == 1 && this.field2495 == 0) {
            this.field2496 = 2;
        }
        if (this.field2496 == 2 && this.field2499 != -1) {
            ServerHelper.I.interactionManager.clickSlot(ServerHelper.I.player.currentScreenHandler.syncId, this.field2499, this.field2498, SlotActionType.SWAP, (PlayerEntity)ServerHelper.I.player);
            ServerHelper.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(ServerHelper.I.player.currentScreenHandler.syncId));
            this.field2496 = 3;
            this.field2497 = ServerHelper.I.player.age + 2;
        }
        if (this.field2496 == 3 && ServerHelper.I.player.age >= this.field2497) {
            ServerHelper.I.interactionManager.interactItem((PlayerEntity)ServerHelper.I.player, Hand.MAIN_HAND);
            this.field2496 = 4;
            this.field2497 = ServerHelper.I.player.age + 3;
        }
        if (this.field2496 == 4 && ServerHelper.I.player.age >= this.field2497) {
            ServerHelper.I.interactionManager.clickSlot(ServerHelper.I.player.currentScreenHandler.syncId, this.field2499, this.field2498, SlotActionType.SWAP, (PlayerEntity)ServerHelper.I.player);
            ServerHelper.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(ServerHelper.I.player.currentScreenHandler.syncId));
            this.field2496 = 0;
            this.field2499 = -1;
            this.field2498 = -1;
            v2 = this.method667();
            if (v2 != null) {
                v2.method1890();
            }
        }
        if ((v2 = this.method667()) == null || !v2.method1889()) {
            return;
        }
        if (this.field2492.isMode(field2500)) {
            return;
        }
        int v3 = -1;
        if (v2 instanceof ServerHelperSpooky) {
            v3 = ((ServerHelperSpooky)v2).method2221();
        } else if (v2 instanceof ServerHelperFuntime) {
            v3 = ((ServerHelperFuntime)v2).method1951();
        } else if (v2 instanceof ServerHelperHolyworld) {
            v3 = ((ServerHelperHolyworld)v2).method2232();
        }
        if (v3 == -1) {
            v2.method1890();
            return;
        }
        int v4 = ServerHelper.I.player.getInventory().selectedSlot;
        if (v3 < 9) {
            ServerHelper.I.player.getInventory().selectedSlot = v3;
            ((IClientPlayerInteractionManagerMixin)ServerHelper.I.interactionManager).invokeSyncSelectedSlot();
            this.field2498 = v4;
            this.field2496 = 6;
            this.field2497 = ServerHelper.I.player.age + 2;
        } else {
            this.field2495 = 2;
            this.field2496 = 1;
            this.field2499 = v3;
            this.field2498 = v4;
        }
        if (v2 instanceof ServerHelperSpooky) {
            ((ServerHelperSpooky)v2).method2224();
        } else if (v2 instanceof ServerHelperFuntime) {
            ((ServerHelperFuntime)v2).method1960();
        } else if (v2 instanceof ServerHelperHolyworld) {
            ((ServerHelperHolyworld)v2).method2228();
        }
    }

    @Override
    public void onDisable() {
        EventBus.method3009(this.field2501);
        EventBus.method3009(this.field2502);
        this.method681();
        ServerHelperBase v1 = this.method667();
        if (v1 != null) {
            v1.method1890();
        }
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        EventBus.method3013(this.field2501);
        EventBus.method3013(this.field2502);
    }

    public void method666(int param1) {
        if (param1 < 0 || ServerHelper.I.player == null) {
            return;
        }
        int v2 = ServerHelper.I.player.getInventory().selectedSlot;
        if (param1 < 9) {
            ServerHelper.I.player.getInventory().selectedSlot = param1;
            ((IClientPlayerInteractionManagerMixin)ServerHelper.I.interactionManager).invokeSyncSelectedSlot();
            this.field2498 = v2;
            this.field2496 = 6;
            this.field2497 = ServerHelper.I.player.age + 2;
        } else {
            this.field2495 = 2;
            this.field2496 = 1;
            this.field2499 = param1;
            this.field2498 = v2;
        }
    }

    private ServerHelperBase method667() {
        return (ServerHelperBase)this.field2506.get(this.field2492.getValue());
    }

    public ServerHelper() {
        super(field2510, field2511, Combat.Miscellaneous);
        this.field2506 = new HashMap();
        this.field2501 = new ServerHelperFuntime(List.of(this.field2535, this.field2537, this.field2539, this.field2541), () -> this.isEnabled() && (Boolean)this.field2543.getValue() != false && this.field2492.isMode(field2593));
        this.field2502 = new ServerHelperReallyworld(List.of(this.field2545, this.field2547, this.field2549, this.field2551, this.O, this.field2554, this.field2556), this.field2558, this.field2560, () -> this.isEnabled() && this.field2492.isMode(field2604), this);
        this.field2577 = new ServerHelperSpooky(List.of(this.field2521, this.field2523, this.field2525, this.field2527, this.field2529, this.field2531, this.field2533));
        this.field2578 = new ServerHelperHolyworld(List.of(this.field2562, this.field2564, this.field2566, this.field2568, this.field2570, this.field2572, this.field2574), this.field2576);
        this.field2506.put(field2579, this.field2501);
        this.field2506.put(field2580, this.field2502);
        this.field2506.put(field2581, this.field2577);
        this.field2506.put(field2582, this.field2578);
        this.addSettings(this.field2492, this.field2519, this.field2521, this.field2523, this.field2525, this.field2527, this.field2529, this.field2531, this.field2533, this.field2535, this.field2537, this.field2539, this.field2541, this.field2543, this.field2545, this.field2547, this.field2551, this.O, this.field2554, this.field2556, this.field2558, this.field2560, this.field2562, this.field2564, this.field2566, this.field2568, this.field2570, this.field2572, this.field2574, this.field2576);
    }

    @EventHandler(2)
    public void method680(EventInput param1) {
        if (this.field2495 > 0 || this.field2496 >= 1 && this.field2496 <= 4) {
            param1.method1973(0.0f);
            param1.method1972(0.0f);
            param1.method1974(false);
            param1.method1970(false);
        }
    }

    private void method681() {
        this.field2496 = 0;
        this.field2498 = -1;
        this.field2499 = -1;
        this.field2497 = 0;
        this.field2495 = 0;
    }

    private static double method683(ExplosionS2CPacket param0, String param1) throws NoSuchFieldException, IllegalAccessException {
        Field v2 = param0.getClass().getDeclaredField(param1);
        v2.setAccessible(true);
        return v2.getDouble(param0);
    }

    @EventHandler(2)
    private void method685(EventPacket param1) {
        if (!(param1.method2466() && this.isEnabled() && this.field2492.isMode(field2594) && ((Boolean)this.field2576.getValue()).booleanValue())) {
            return;
        }
        Packet v32_p = param1.method2467();
        if (v32_p instanceof ExplosionS2CPacket) {
            ExplosionS2CPacket v2 = (ExplosionS2CPacket)v32_p;
            try {
                double v32 = ServerHelper.method683(v2, field2595);
                double v5 = ServerHelper.method683(v2, field2596);
                double v7 = ServerHelper.method683(v2, field2597);
                float v9 = ServerHelper.method659(v2, field2598);
                if (Math.abs(v32) > field2599 || Math.abs(v5) > field2600 || Math.abs(v7) > field2601 || (double)v9 > field2602) {
                    param1.method2298(true);
                }
            } catch (ReflectiveOperationException v10) {
                // field layout differs on this server build; ignore
            }
            return;
        }
    }

    @EventHandler(2)
    public void method688(EventKey param1) {
        if (ServerHelper.I.currentScreen != null) {
            return;
        }
        if (param1.method3599()) {
            return;
        }
        ServerHelperBase v2 = this.method667();
        if (v2 == null) {
            return;
        }
        boolean v3 = (Boolean)this.field2519.getValue();
        if (this.field2492.isMode(field2605)) {
            this.field2577.method2225(v3);
        } else if (this.field2492.isMode(field2606)) {
            this.field2501.method1952(v3);
        } else if (this.field2492.isMode(field2607)) {
            this.field2578.method2229(v3);
        }
        v2.method1888(param1.method3595());
    }

    @EventHandler(2)
    public void method691(EventSelectSlot param1) {
        if (this.field2495 > 0 || this.field2496 >= 1 && this.field2496 <= 6) {
            param1.method2298(true);
        }
    }

    static {
        field2510 = "Server Helper";
        field2511 = "Серверный помощник в использовании предметов";
        field2512 = "Сервер";
        field2514 = "SpookyTime";
        field2515 = "LonyGrief";
        field2516 = "Reallyworld";
        field2517 = "HolyWorld";
        field2518 = "Только с хотбара";
        field2520 = "Дезориентация";
        field2522 = "Явная пыль";
        field2524 = "Пласт";
        field2526 = "Трапка Spooky";
        field2528 = "Огненный смерч";
        field2530 = "Снежок заморозка";
        field2532 = "Божья аура";
        field2534 = "Обычная ливалка";
        field2536 = "Уникальное перышко";
        field2538 = "Ливалка с платформой";
        field2540 = "Уникальная трапка";
        field2542 = "Авто чат-игра (уравнение)";
        field2544 = "Анти полет";
        field2546 = "Зелье Гринча";
        field2548 = "AutoShift (Шифт для шара)";
        field2550 = "Новогодний ужас";
        field2552 = "Эссенция кромешника";
        field2553 = "Снежок";
        field2555 = "Ловушка";
        field2557 = "Закрывать меню";
        field2559 = "Фильтр чата";
        field2561 = "Пузырёк опыта";
        field2563 = "Трапка";
        field2565 = "Взрывная трапка";
        field2567 = "Прощальный гул";
        field2569 = "Взрывная штучка";
        field2571 = "Стан";
        field2573 = "Ком снега";
        field2575 = "Anti-crash (HolyWorld)";
        field2605 = "SpookyTime";
        field2606 = "LonyGrief";
        field2607 = "HolyWorld";
        field2594 = "HolyWorld";
        field2595 = "x";
        field2596 = "y";
        field2597 = "z";
        field2598 = "power";
        field2599 = 1.0E9;
        field2600 = 1.0E9;
        field2601 = 1.0E9;
        field2602 = 1.0E9;
        field2500 = "Reallyworld";
        field2604 = "Reallyworld";
        field2593 = "LonyGrief";
        field2505 = "HolyWorld";
        field2509 = "HolyWorld";
        field2592 = "HolyWorld";
        field2494 = "HolyWorld";
        field2590 = "HolyWorld";
        field2493 = "HolyWorld";
        field2503 = "HolyWorld";
        field2583 = "HolyWorld";
        field2591 = "Reallyworld";
        field2613 = "Reallyworld";
        field2610 = "Reallyworld";
        field2618 = "Reallyworld";
        field2616 = "Reallyworld";
        field2587 = "Reallyworld";
        field2615 = "Reallyworld";
        field2589 = "Reallyworld";
        field2584 = "Reallyworld";
        field2603 = "LonyGrief";
        field2507 = "LonyGrief";
        field2609 = "LonyGrief";
        field2508 = "LonyGrief";
        field2608 = "LonyGrief";
        field2617 = "SpookyTime";
        field2586 = "SpookyTime";
        field2611 = "SpookyTime";
        field2585 = "SpookyTime";
        field2614 = "SpookyTime";
        field2612 = "SpookyTime";
        field2588 = "SpookyTime";
        field2504 = "Reallyworld";
    }
}


