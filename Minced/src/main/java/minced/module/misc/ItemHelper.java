/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.ConsumableComponent
 *  net.minecraft.component.type.NbtComponent
 *  net.minecraft.component.type.PotionContentsComponent
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$TooltipContext
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.item.tooltip.TooltipType
 *  net.minecraft.item.tooltip.TooltipType$Default
 *  net.minecraft.nbt.NbtByte
 *  net.minecraft.nbt.NbtByteArray
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtDouble
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.nbt.NbtFloat
 *  net.minecraft.nbt.NbtInt
 *  net.minecraft.nbt.NbtIntArray
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtLong
 *  net.minecraft.nbt.NbtLongArray
 *  net.minecraft.nbt.NbtShort
 *  net.minecraft.nbt.NbtString
 *  net.minecraft.registry.Registries
 *  net.minecraft.screen.PlayerScreenHandler
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  sLM.4ZXYEo
 */
package minced.module.misc;
import minced.core.EventHandler;

import java.util.ArrayList;
import java.util.List;
import minced.core.ColorUtil;
import minced.core.BindSetting;
import minced.core.ColorSetting;
import minced.core.BooleanSetting;
import minced.core.EventItemCooldown;
import minced.core.ItemSetting;
import minced.core.EventHandledScreenRenderPre;
import minced.core.NotificationManager;
import minced.core.EventHotbarRender;
import minced.core.EventRenderTooltip;
import minced.core.ChatUtil;
import minced.core.InventoryUtil;
import minced.core.EventKey;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sLM._4ZXYEo;

public class ItemHelper
extends Module {
    static String cfr_renamed_386;
    static String cfr_renamed_387;
    static String cfr_renamed_388;

    static String field1451;
    static String field1452;
    BooleanSetting field1453;
    ColorSetting field1454;
    BooleanSetting var_minced_core_ClsClass271_I;
    ColorSetting var_minced_core_ClsClass253_I;
    BooleanSetting field1455 = new BooleanSetting(field1483, false);
    ColorSetting field1456 = new ColorSetting(field1484, true, ColorUtil.cfr_renamed_0(field1485), this.field1455::getValue);
    BooleanSetting field1457;
    ColorSetting field1458;
    BooleanSetting field1459;
    ColorSetting field1460;
    static String field1461;
    static String field1462;
    static String field1463;
    static String field1464;
    static String field1465;
    static String field1466;
    static String field1467;
    static String field1468;
    static String field1469;
    static String field1470;
    static String field1471;
    static String field1472;
    static String field1473;
    static String field1474;
    BooleanSetting field1475;
    static float field1476;
    static float field1477;
    static String field1478;
    static double field1479;
    static MinecraftClient var_net_minecraft_client_MinecraftClient_I = MinecraftClient.getInstance();
    BooleanSetting field1480;
    static String field1481;
    static String field1482;
    static String field1483;
    static String field1484;
    static String field1485;
    static String field1486;
    static String field1487;
    static String field1488;
    static String field1489;
    static String field1490;
    static String field1491;
    static String field1492;
    static String field1493;
    static String field1494;
    static String field1495;
    static String field1496;
    static String field1497;
    static String field1498;
    ItemSetting field1499;
    static String field1500;
    BindSetting field1501;
    static String field1502;
    BindSetting field1503;
    static String field1504;
    BindSetting field1505;
    static String field1506;
    static String field1507;
    static String field1508;
    static String field1509;
    static String field1510;

    private void method505(List param1, NbtCompound param2) {
        for (String v4 : param2.getKeys()) {
            NbtElement v5 = param2.get(v4);
            String v6 = this.method508(v5);
            param1.add(Text.literal((String)((Object)_4ZXYEo.фи("makeConcatWithConstants", new Object[]{}, (String)v6, (String)v4))).formatted(Formatting.DARK_GRAY));
        }
    }

    private boolean method506(ItemStack param1) {
        if (!param1.contains(DataComponentTypes.POTION_CONTENTS)) {
            return false;
        }
        PotionContentsComponent v2 = (PotionContentsComponent)param1.get(DataComponentTypes.POTION_CONTENTS);
        if (v2 == null) {
            return false;
        }
        StatusEffect v3 = (StatusEffect)Registries.STATUS_EFFECT.get(Identifier.of((String)field1451, (String)field1452));
        for (StatusEffectInstance v5 : v2.getEffects()) {
            if (!((StatusEffect)v5.getEffectType().value()).equals(v3)) continue;
            return true;
        }
        return false;
    }

    private int method507(ItemStack param1) {
        Item v2 = param1.getItem();
        if (((Boolean)this.field1453.getValue()).booleanValue() && v2 == Items.ENCHANTED_GOLDEN_APPLE) {
            return (Integer)this.field1454.getValue();
        }
        if (((Boolean)this.var_minced_core_ClsClass271_I.getValue()).booleanValue() && v2 == Items.GOLDEN_APPLE) {
            return (Integer)this.var_minced_core_ClsClass253_I.getValue();
        }
        if (((Boolean)this.field1455.getValue()).booleanValue() && this.method506(param1)) {
            return (Integer)this.field1456.getValue();
        }
        if (((Boolean)this.field1457.getValue()).booleanValue() && v2 == Items.TOTEM_OF_UNDYING) {
            return (Integer)this.field1458.getValue();
        }
        if (((Boolean)this.field1459.getValue()).booleanValue() && v2 == Items.PLAYER_HEAD) {
            return (Integer)this.field1460.getValue();
        }
        return 0;
    }

    private String method508(NbtElement param1) {
        if (param1 instanceof NbtByte) {
            return field1461;
        }
        if (param1 instanceof NbtShort) {
            return field1462;
        }
        if (param1 instanceof NbtInt) {
            return field1463;
        }
        if (param1 instanceof NbtLong) {
            return field1464;
        }
        if (param1 instanceof NbtFloat) {
            return field1465;
        }
        if (param1 instanceof NbtDouble) {
            return field1466;
        }
        if (param1 instanceof NbtString) {
            return field1467;
        }
        if (param1 instanceof NbtByteArray) {
            return field1468;
        }
        if (param1 instanceof NbtIntArray) {
            return field1469;
        }
        if (param1 instanceof NbtLongArray) {
            return field1470;
        }
        if (param1 instanceof NbtList) {
            return field1471;
        }
        if (param1 instanceof NbtCompound) {
            return field1472;
        }
        return field1473;
    }

    private void method509(DrawContext param1, ItemStack param2, int param3, int param4) {
        int v5 = this.method507(param2);
        if (v5 != 0) {
            param1.fill(param3, param4, param3 + 16, param4 + 16, v5);
        }
    }

    private void method510(Item param1, String param2) {
        if (InventoryUtil.method3336(param1) == -1) {
            NotificationManager.method2456(field1474, (String)((Object)_4ZXYEo.Цн("makeConcatWithConstants", new Object[]{}, (String)param2)), ColorUtil.method1852(255, 85, 85, 255));
            return;
        }
        InventoryUtil.method3327(param1);
    }

    @EventHandler(2)
    public void method511(EventItemCooldown param1) {
        Item v2 = (Item)Registries.ITEM.get(param1.method203());
        ItemStack v3 = new ItemStack((ItemConvertible)v2);
        if (((Boolean)this.field1475.getValue()).booleanValue() && v3.contains(DataComponentTypes.FOOD)) {
            ConsumableComponent v4 = (ConsumableComponent)v3.get(DataComponentTypes.CONSUMABLE);
            float v5 = v4 != null ? v4.consumeSeconds() : field1476;
            int v6 = v5 <= field1477 ? 16 : 32;
            int v7 = param1.method202();
            if (v7 > v6 && v3.getItem() != Items.DRIED_KELP) {
                param1.method200(v7 - v6);
                MutableText v8 = Text.literal((String)field1478).append(v3.getName()).append((Text)Text.literal((String)((Object)_4ZXYEo.Цю("makeConcatWithConstants", new Object[]{}, (double)((double)v6 / field1479))))).formatted(Formatting.GRAY);
                ChatUtil.method3308(v8, new Object[0]);
            }
        }
    }

    @EventHandler(2)
    public void method512(EventHotbarRender param1) {
        if (ItemHelper.var_net_minecraft_client_MinecraftClient_I.currentScreen instanceof CreativeInventoryScreen) {
            return;
        }
        int v2 = var_net_minecraft_client_MinecraftClient_I.getWindow().getScaledWidth() / 2;
        int v3 = var_net_minecraft_client_MinecraftClient_I.getWindow().getScaledHeight() - 16 - 3;
        for (int v4 = 0; v4 < 9; ++v4) {
            ItemStack v5 = (ItemStack)ItemHelper.var_net_minecraft_client_MinecraftClient_I.player.getInventory().main.get(v4);
            if (v5.isEmpty()) continue;
            this.method509(param1.method2594(), v5, v2 - 90 + v4 * 20 + 2, v3);
        }
    }

    @EventHandler(2)
    public void method513(EventRenderTooltip param1) {
        if (!((Boolean)this.field1480.getValue()).booleanValue()) {
            return;
        }
        NbtComponent v2 = (NbtComponent)param1.field9133.get(DataComponentTypes.CUSTOM_DATA);
        if (v2 == null) {
            return;
        }
        NbtCompound v3 = v2.copyNbt();
        if (v3.isEmpty()) {
            return;
        }
        param1.method2298(true);
        TooltipType.Default v4 = ItemHelper.var_net_minecraft_client_MinecraftClient_I.options.advancedItemTooltips ? TooltipType.ADVANCED : TooltipType.BASIC;
        ArrayList<Text> v5 = new ArrayList<Text>(param1.field9133.getTooltip(Item.TooltipContext.DEFAULT, (PlayerEntity)ItemHelper.var_net_minecraft_client_MinecraftClient_I.player, (TooltipType)v4));
        v5.add(Text.empty());
        this.method505(v5, v3);
        param1.field9134.drawTooltip(ItemHelper.var_net_minecraft_client_MinecraftClient_I.textRenderer, v5, param1.field9135, param1.field9136);
    }

    @EventHandler(2)
    public void method514(EventHandledScreenRenderPre param1) {
        if (ItemHelper.var_net_minecraft_client_MinecraftClient_I.currentScreen instanceof CreativeInventoryScreen || !(param1.method2606() instanceof PlayerScreenHandler)) {
            return;
        }
        for (Slot v3 : param1.method2606().slots) {
            if (v3 == null || !v3.hasStack()) continue;
            this.method509(param1.method2609(), v3.getStack(), param1.method2607() + v3.x, param1.method2608() + v3.y);
        }
    }

    public ItemHelper() {
        super(field1481, field1482, Combat.Miscellaneous);
        this.field1453 = new BooleanSetting(field1486, false);
        this.field1454 = new ColorSetting(field1487, true, ColorUtil.cfr_renamed_0(field1488), this.field1453::getValue);
        this.var_minced_core_ClsClass271_I = new BooleanSetting(field1489, false);
        this.var_minced_core_ClsClass253_I = new ColorSetting(field1490, true, ColorUtil.cfr_renamed_0(field1491), this.var_minced_core_ClsClass271_I::getValue);
        this.field1457 = new BooleanSetting(field1492, false);
        this.field1458 = new ColorSetting(field1493, true, ColorUtil.cfr_renamed_0(field1494), this.field1457::getValue);
        this.field1459 = new BooleanSetting(field1495, false);
        this.field1460 = new ColorSetting(field1496, true, ColorUtil.cfr_renamed_0(field1497), this.field1459::getValue);
        this.field1499 = new ItemSetting(field1498);
        this.field1501 = new BindSetting(field1500);
        this.field1503 = new BindSetting(field1502);
        this.field1505 = new BindSetting(field1504);
        this.field1475 = new BooleanSetting(field1506, false);
        this.field1480 = new BooleanSetting(field1507, false);
        this.addSettings(this.field1455, this.field1453, this.var_minced_core_ClsClass271_I, this.field1457, this.field1459, this.field1499, this.field1501, this.field1503, this.field1505, this.field1475, this.field1480);
    }

    @EventHandler(2)
    public void method515(EventKey param1) {
        if (!param1.method3599() || param1.method3594() || ItemHelper.var_net_minecraft_client_MinecraftClient_I.player == null) {
            return;
        }
        int v2 = param1.method3595();
        if ((Integer)this.field1501.getValue() == v2) {
            this.method510(Items.CHORUS_FRUIT, field1508);
        } else if ((Integer)this.field1503.getValue() == v2) {
            this.method510(Items.GOLDEN_APPLE, field1509);
        } else if ((Integer)this.field1505.getValue() == v2) {
            this.method510(Items.ENCHANTED_GOLDEN_APPLE, field1510);
        }
    }

    static {
        field1481 = "Item Helper";
        field1482 = "Позволяет использовать предмет по нажатию на клавишу";
        field1483 = "Подсвечивать зелье исцеления";
        field1484 = "Цвет подсветки зелья исцеления";
        field1485 = "#FF2AB9";
        field1486 = "Подсвечивать чарки";
        field1487 = "Цвет подсветки чарки";
        field1488 = "#FFAC93";
        field1489 = "Подсвечивать золотые яблоки";
        field1490 = "Цвет подсветки золотого яблока";
        field1491 = "#E7EB56";
        field1492 = "Подсвечивать тотем";
        field1493 = "Цвет подсветки тотема";
        field1494 = "#00FF00";
        field1495 = "Подсвечивать шары";
        field1496 = "Цвет подсветки шаров";
        field1497 = "#FF00FF";
        field1498 = "Быстрый выбор в руку";
        field1500 = "Хорус";
        field1502 = "Гепл";
        field1504 = "Чарка";
        field1506 = "Уменьшать задержку на предметы";
        field1507 = "Отображать nbt предметов";
        field1508 = "Хорус";
        field1509 = "Гепл";
        field1510 = "Чарка";
        field1474 = "Item Helper";
        cfr_renamed_386 = "Нет в инвентаре: \u0001";
        field1476 = 1.6f;
        field1477 = 0.8f;
        field1478 = "Задержка на ";
        field1479 = 20.0;
        cfr_renamed_387 = " уменьшена на ~\u0001 секунды";
        field1451 = "minecraft";
        field1452 = "instant_health";
        cfr_renamed_388 = "- \u0001: \u0001";
        field1461 = "byte";
        field1462 = "short";
        field1463 = "int";
        field1464 = "long";
        field1465 = "float";
        field1466 = "double";
        field1467 = "string";
        field1468 = "byte[]";
        field1469 = "int[]";
        field1470 = "long[]";
        field1471 = "list";
        field1472 = "compound";
        field1473 = "unknown";
    }
}



