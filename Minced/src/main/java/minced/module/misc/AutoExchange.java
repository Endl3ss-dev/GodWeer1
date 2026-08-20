/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item$TooltipContext
 *  net.minecraft.item.Items
 *  net.minecraft.item.tooltip.TooltipType
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.text.Text
 */
package minced.module.misc;
import minced.core.EventHandler;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import minced.core.SliderSetting;
import minced.core.EventUpdate;
import minced.core.ChatUtil;
import minced.core.EventKey;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class AutoExchange
extends Module {
    static String field1365 = "Auto Exchange";
    static String field1366 = "Автоматически покупает коины через /exchange";
    static String field1367 = "Коины";
    static float field1368 = 100.0f;
    static float field1369 = 300.0f;
    SliderSetting field1370 = new SliderSetting(field1367, field1368, 1.0f, field1369, 1.0f);
    int field1371 = -1;
    int field1372 = -1;
    static MinecraftClient I = MinecraftClient.getInstance();
    Screen field1373;
    int field1374;
    int field1375;
    static String field1376;
    static String field1377;
    static Pattern field1378;
    static String field1379;
    static Pattern field1380;
    static String field1381;
    static Pattern field1382;
    static String field1383;
    static Pattern field1384;
    static String field1385;
    static Pattern field1386;
    static long field1387;
    static long field1388;
    static String field1389;
    static String field1390;
    static String field1391;
    static String field1392;
    static String field1393;
    static String field1394;
    static String field1395;
    static String field1396;
    static String field1397;
    static String field1398;
    static String field1399;
    static long field1400;

    public AutoExchange() {
        super(field1365, field1366, Combat.Miscellaneous);
        this.addSettings(this.field1370);
    }

    private void method475() {
        Screen v1 = AutoExchange.I.currentScreen;
        if (v1 != this.field1373) {
            this.field1373 = v1;
            this.field1374 = AutoExchange.I.player.age;
            this.field1375 = ThreadLocalRandom.current().nextInt(3, 5);
        }
    }

    private static int method476(List param0, Pattern param1) {
        for (Text v3 : (Iterable<Text>)(param0)) {
            Matcher v4 = param1.matcher(v3.getString());
            if (!v4.find()) continue;
            return Integer.parseInt(v4.group(1).replace(field1376, ""));
        }
        return -1;
    }

    public static void method477() {
        field1378 = Pattern.compile(field1377);
        field1380 = Pattern.compile(field1379);
        field1382 = Pattern.compile(field1381);
        field1384 = Pattern.compile(field1383);
        field1386 = Pattern.compile(field1385);
    }

    private Slot method478(ScreenHandler param1) {
        Slot v2 = null;
        long v3 = field1387;
        for (Slot v6 : param1.slots) {
            if (v6 == null || !v6.hasStack()) continue;
            List v7 = v6.getStack().getTooltip(Item.TooltipContext.DEFAULT, (PlayerEntity)AutoExchange.I.player, this.method486());
            long v8 = AutoExchange.method488(v7, field1380);
            int v10 = AutoExchange.method476(v7, field1382);
            if (v8 == field1388 || v10 == -1 || v10 < this.field1371 || v8 <= v3) continue;
            v3 = v8;
            v2 = v6;
        }
        return v2;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.method487();
        this.field1371 = (int)((Float)this.field1370.getValue()).floatValue();
    }

    private void method479(ScreenHandler param1) {
        Slot v2 = this.method478(param1);
        if (v2 == null) {
            return;
        }
        int v3 = AutoExchange.method476(v2.getStack().getTooltip(Item.TooltipContext.DEFAULT, (PlayerEntity)AutoExchange.I.player, this.method486()), field1382);
        if (v3 < this.field1371) {
            return;
        }
        this.method481(param1, v2.id, 1);
        this.field1372 = v2.id;
    }

    private void method480(ScreenHandler param1) {
        Slot v42;
        if (AutoExchange.I.player.age % 10 != 0) {
            return;
        }
        int v2 = -1;
        for (Slot v42_154 : param1.slots) {
            int v5;
            if (v42_154 == null || !v42_154.hasStack() || (v5 = AutoExchange.method476(v42_154.getStack().getTooltip(Item.TooltipContext.DEFAULT, (PlayerEntity)AutoExchange.I.player, this.method486()), field1384)) == -1) continue;
            v2 = v5;
            break;
        }
        if (v2 == -1) {
            return;
        }
        if (v2 == this.field1371) {
            Slot v3 = this.method484(param1);
            if (v3 != null) {
                this.method481(param1, v3.id, 0);
                this.toggle();
            }
            return;
        }
        int v3 = this.field1371 - v2;
        if (v3 <= 0) {
            return;
        }
        v42 = this.method482(param1, v3);
        if (v42 != null) {
            this.method481(param1, v42.id, 0);
        }
    }

    private void method481(ScreenHandler param1, int param2, int param3) {
        if (AutoExchange.I.interactionManager == null) {
            return;
        }
        AutoExchange.I.interactionManager.clickSlot(param1.syncId, param2, param3, SlotActionType.PICKUP, (PlayerEntity)AutoExchange.I.player);
        AutoExchange.I.player.currentScreenHandler.onSlotClick(param2, param3, SlotActionType.PICKUP, (PlayerEntity)AutoExchange.I.player);
    }

    private Slot method482(ScreenHandler param1, int param2) {
        Slot v3 = null;
        int v4 = 0;
        block0: for (Slot v6 : param1.slots) {
            if (v6 == null || !v6.hasStack() || v6.getStack().getItem() != Items.LIME_STAINED_GLASS_PANE) continue;
            List v7 = v6.getStack().getTooltip(Item.TooltipContext.DEFAULT, (PlayerEntity)AutoExchange.I.player, this.method486());
            for (Text v9 : (Iterable<Text>)(v7)) {
                Matcher v10 = field1386.matcher(v9.getString());
                if (!v10.find()) continue;
                int v11 = Integer.parseInt(v10.group(1));
                if (v11 <= 0 || v11 > param2 || v11 <= v4) continue block0;
                v4 = v11;
                v3 = v6;
                continue block0;
            }
        }
        return v3;
    }

    @EventHandler(2)
    public void method483(EventKey param1) {
        if (param1.method3599() && param1.method3595() == 256 && this.isEnabled()) {
            ChatUtil.method3308(field1389, new Object[0]);
            this.toggle();
        }
    }

    private Slot method484(ScreenHandler param1) {
        for (Slot v3 : param1.slots) {
            if (v3 == null || !v3.hasStack()) continue;
            List v4 = v3.getStack().getTooltip(Item.TooltipContext.DEFAULT, (PlayerEntity)AutoExchange.I.player, this.method486());
            boolean v5 = false;
            int v6 = -1;
            for (Text v8 : (Iterable<Text>)(v4)) {
                Matcher v10;
                String v9 = v8.getString();
                if (v9.contains(field1390)) {
                    v5 = true;
                }
                if (!(v10 = field1384.matcher(v9)).find()) continue;
                v6 = Integer.parseInt(v10.group(1));
            }
            if (!v5 || v6 != this.field1371) continue;
            return v3;
        }
        return null;
    }

    @EventHandler(2)
    public void method485(EventUpdate param1) {
        HandledScreen v2;
        if (AutoExchange.I.player == null || AutoExchange.I.world == null) {
            return;
        }
        if (this.field1371 <= 0) {
            return;
        }
        if (AutoExchange.I.currentScreen == null) {
            if (AutoExchange.I.player.age % 10 == 0) {
                AutoExchange.I.player.networkHandler.sendChatCommand(field1391);
            }
            this.field1372 = -1;
            this.field1373 = null;
            return;
        }
        ScreenHandler v3s;
        Screen v3 = AutoExchange.I.currentScreen;
        if (v3 instanceof HandledScreen) {
            v2 = (HandledScreen)v3;
            v3s = AutoExchange.I.player.currentScreenHandler;
            if (v3s == null) {
                return;
            }
        } else {
            return;
        }
        this.method475();
        if (AutoExchange.I.player.age < this.field1374 + this.field1375) {
            return;
        }
        String v4 = v2.getTitle().getString();
        int v5 = this.method489(v3s);
        if (v5 != -1 && v5 < this.field1371) {
            ChatUtil.method3308(field1392, new Object[0]);
            AutoExchange.I.player.closeHandledScreen();
            this.toggle();
            return;
        }
        if (!v4.contains(field1393) && !v4.contains(field1394)) {
            AutoExchange.I.player.closeHandledScreen();
            this.field1372 = -1;
            return;
        }
        if (v4.contains(field1395) && AutoExchange.I.player.age % 10 == 0 && this.field1372 != -1) {
            this.field1372 = -1;
        }
        if (this.field1372 != -1) {
            if (v4.contains(field1396)) {
                this.method480(v3s);
            }
            return;
        }
        if (v4.contains(field1397) && !v4.contains(field1398)) {
            this.method479(v3s);
        }
    }

    private TooltipType method486() {
        return AutoExchange.I.options.advancedItemTooltips ? TooltipType.ADVANCED : TooltipType.BASIC;
    }

    private void method487() {
        this.field1371 = -1;
        this.field1372 = -1;
        this.field1373 = null;
        this.field1374 = 0;
        this.field1375 = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.method487();
    }

    private static long method488(List param0, Pattern param1) {
        for (Text v3 : (Iterable<Text>)(param0)) {
            Matcher v4 = param1.matcher(v3.getString());
            if (!v4.find()) continue;
            return Long.parseLong(v4.group(1).replace(field1399, ""));
        }
        return field1400;
    }

    private int method489(ScreenHandler param1) {
        for (Slot v3 : param1.slots) {
            int v4;
            if (v3 == null || !v3.hasStack() || (v4 = AutoExchange.method476(v3.getStack().getTooltip(Item.TooltipContext.DEFAULT, (PlayerEntity)AutoExchange.I.player, this.method486()), field1378)) == -1) continue;
            return v4;
        }
        return -1;
    }

    static {
        field1389 = "Команда успешно деактивирована";
        field1391 = "exchange";
        field1392 = "Команда деактивирована. Указанное кол-во коинов закупки, не соответствует вашему балансу коинов.";
        field1393 = "Биржа";
        field1394 = "Покупка";
        field1395 = "Биржа";
        field1396 = "Покупка";
        field1397 = "Биржа";
        field1398 = "Покупка";
        field1387 = -1L;
        field1388 = -1L;
        field1390 = "[ЛКМ] — Приобрести";
        field1376 = ",";
        field1399 = ",";
        field1400 = -1L;
        field1377 = "Ваш баланс:\\s*(\\d+)\\s*\\|";
        field1379 = "Курс:\\s*([\\d,]+)\\s*¤";
        field1381 = "Ожидается:\\s*(\\d+)\\s*\\|";
        field1383 = "Вы отдадите:\\s*(\\d+)\\s*\\|";
        field1385 = "▶ Добавить (\\d+) \\|❘\\| \\(коинов\\)";
        AutoExchange.method477();
    }
}



