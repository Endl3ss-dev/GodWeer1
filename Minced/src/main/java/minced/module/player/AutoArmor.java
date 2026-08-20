/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.EquippableComponent
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ArmorItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.Registries
 *  net.minecraft.screen.slot.SlotActionType
 *  sg.ec.ФС$2
 */
package minced.module.player;
import minced.core.EventHandler;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.ArmorRating;
import minced.core.EventUpdate;
import minced.core.ClientState;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;

public class AutoArmor
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    BooleanSetting field5724;
    ClientState field5725;
    SliderSetting field5726;
    Map field5727;
    BooleanSetting field5728;
    BooleanSetting field5729;
    static float field5730;
    static float field5731;
    static float field5732;
    static float field5733;
    SliderSetting field5734 = new SliderSetting(field5737, field5738, 0.0f, field5739, field5740);
    static String field5735;
    static String field5736;
    static String field5737;
    static float field5738;
    static float field5739;
    static float field5740;
    static String field5741;
    static String field5742;
    static String field5743;
    static float field5744;
    static float field5745;
    static String field5746;
    static String field5747;
    static String field5748;
    static String field5749;
    static String field5750;
    static String field5751;
    static String field5752;
    static String field5753;
    static String field5754;

    private void method1372(ArmorRating param1, EquipmentSlot param2) {
        int v3 = this.method1382(param2);
        ItemStack v4 = AutoArmor.I.player.getInventory().getArmorStack(v3);
        if (((Boolean)this.field5724.getValue()).booleanValue() && param1.Р < 9 && AutoArmor.I.currentScreen == null) {
            int previousSlot = AutoArmor.I.player.getInventory().selectedSlot;
            AutoArmor.I.player.getInventory().selectedSlot = param1.Р;
            AutoArmor.I.interactionManager.interactItem((PlayerEntity)AutoArmor.I.player, AutoArmor.I.player.getActiveHand());
            AutoArmor.I.player.getInventory().selectedSlot = previousSlot;
        } else {
            int v5;
            int n = v5 = param1.Р < 9 ? param1.Р + 36 : param1.Р;
            if (!v4.isEmpty()) {
                if (AutoArmor.I.player.getInventory().getEmptySlot() == -1) {
                    AutoArmor.I.interactionManager.clickSlot(AutoArmor.I.player.currentScreenHandler.syncId, 8 - v3, 0, SlotActionType.THROW, (PlayerEntity)AutoArmor.I.player);
                } else {
                    AutoArmor.I.interactionManager.clickSlot(AutoArmor.I.player.currentScreenHandler.syncId, 8 - v3, 0, SlotActionType.QUICK_MOVE, (PlayerEntity)AutoArmor.I.player);
                }
            }
            AutoArmor.I.interactionManager.clickSlot(AutoArmor.I.player.currentScreenHandler.syncId, v5, 0, SlotActionType.QUICK_MOVE, (PlayerEntity)AutoArmor.I.player);
        }
        this.field5725.method3114();
    }

    private Map method1373() {
        EnumMap v1 = new EnumMap(EquipmentSlot.class);
        v1.put(EquipmentSlot.HEAD, new ArrayList());
        v1.put(EquipmentSlot.CHEST, new ArrayList());
        v1.put(EquipmentSlot.LEGS, new ArrayList());
        v1.put(EquipmentSlot.FEET, new ArrayList());
        int v2 = (Boolean)this.field5724.getValue() != false ? 36 : 27;
        for (int v3 = 0; v3 < v2; ++v3) {
            ArmorRating v6;
            EquipmentSlot v5;
            ItemStack v4 = AutoArmor.I.player.getInventory().getStack(v3);
            if (!this.method1384(v4) || (v5 = this.method1381(v4)) == null || !this.method1374(v5) || !((v6 = new ArmorRating(v3, v4, v5)).method2050() >= ((Float)this.field5726.getValue()).floatValue())) continue;
            ((List)v1.get(v5)).add(v6);
        }
        return v1;
    }

    private boolean method1374(EquipmentSlot param1) {
        return param1 == EquipmentSlot.HEAD || param1 == EquipmentSlot.CHEST || param1 == EquipmentSlot.LEGS || param1 == EquipmentSlot.FEET;
    }

    private void method1375() {
        if (this.field5727.isEmpty()) {
            return;
        }
        EquipmentSlot v1 = null;
        float v2 = 0.0f;
        for (Map.Entry v4 : (Iterable<java.util.Map.Entry>)(this.field5727.entrySet())) {
            EquipmentSlot v5 = (EquipmentSlot)v4.getKey();
            ArmorRating v6 = (ArmorRating)v4.getValue();
            ArmorRating v7 = this.method1379(v5);
            float f = v6.cfr_renamed_4();
            float f2 = v7 != null ? v7.cfr_renamed_4() : 0.0f;
            float v8 = f - f2;
            boolean betterDurability = (Boolean)this.field5729.getValue() != false && v7 != null
                && Math.abs(v6.method2050() - v7.method2050()) <= field5731
                && v6.cfr_renamed_4() > v7.cfr_renamed_4() * field5732;
            if (!(v8 > v2) && !betterDurability) continue;
            v2 = v8;
            v1 = v5;
        }
        if (v1 != null) {
            ArmorRating v3 = (ArmorRating)this.field5727.get(v1);
            this.method1372(v3, v1);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.field5727.clear();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.field5727.clear();
    }

    private boolean method1376(ArmorRating param1, ArmorRating param2) {
        if (param1 == null) {
            return true;
        }
        if (((Boolean)this.field5728.getValue()).booleanValue() && param1.method2050() < ((Float)this.field5726.getValue()).floatValue()) {
            return true;
        }
        if (((Boolean)this.field5729.getValue()).booleanValue()) {
            float v3 = param1.method2050();
            float v4 = param2.method2050();
            if (v4 > v3 + field5730) {
                return true;
            }
            if (Math.abs(v4 - v3) <= field5731) {
                return param2.cfr_renamed_4() > param1.cfr_renamed_4() * field5732;
            }
            return false;
        }
        return param2.cfr_renamed_4() > param1.cfr_renamed_4() * field5733;
    }

    private void method1377() {
        this.field5727.clear();
        Map v1 = this.method1373();
        for (Map.Entry v3 : (Iterable<java.util.Map.Entry>)(v1.entrySet())) {
            ArmorRating v7;
            ArmorRating v6;
            EquipmentSlot v4 = (EquipmentSlot)v3.getKey();
            List v5 = (List)v3.getValue();
            if (v5.isEmpty()) continue;
            if (((Boolean)this.field5729.getValue()).booleanValue()) {
                v5.sort((param0, param1) -> {
                    int v2 = Float.compare(((ArmorRating)param1).method2050(), ((ArmorRating)param0).method2050());
                    if (v2 != 0) {
                        return v2;
                    }
                    return Float.compare(((ArmorRating)param1).cfr_renamed_4(), ((ArmorRating)param0).cfr_renamed_4());
                });
            } else {
                v5.sort((param0, param1) -> Float.compare(((ArmorRating)param1).cfr_renamed_4(), ((ArmorRating)param0).cfr_renamed_4()));
            }
            if (!this.method1376(v6 = this.method1379(v4), v7 = (ArmorRating)v5.get(0))) continue;
            this.field5727.put(v4, v7);
        }
    }

    private ArmorRating method1379(EquipmentSlot param1) {
        int v2 = this.method1382(param1);
        ItemStack v3 = AutoArmor.I.player.getInventory().getArmorStack(v2);
        if (this.method1384(v3)) {
            return new ArmorRating(-1, v3, param1);
        }
        return null;
    }

    @EventHandler(2)
    public void method1380(EventUpdate param1) {
        if (AutoArmor.I.currentScreen != null || AutoArmor.I.player == null) {
            return;
        }
        if (!this.field5725.method3113(((Float)this.field5734.getValue()).longValue())) {
            return;
        }
        this.method1377();
        this.method1375();
    }

    public AutoArmor() {
        super(field5735, field5736, Combat.Player);
        this.field5724 = new BooleanSetting(field5741, true);
        this.field5728 = new BooleanSetting(field5742, true);
        this.field5726 = new SliderSetting(field5743, field5744, 1.0f, field5745, 1.0f);
        this.field5729 = new BooleanSetting(field5746, false);
        this.field5725 = new ClientState();
        this.field5727 = new EnumMap(EquipmentSlot.class);
        this.addSettings(this.field5734, this.field5724, this.field5728, this.field5726, this.field5729);
    }

    private EquipmentSlot method1381(ItemStack param1) {
        Object v2;
        if (!this.method1384(param1)) {
            return null;
        }
        if (param1.contains(DataComponentTypes.EQUIPPABLE) && (v2 = (EquippableComponent)param1.get(DataComponentTypes.EQUIPPABLE)) != null) {
            return ((EquippableComponent)v2).slot();
        }
        String v2x = Registries.ITEM.getId(param1.getItem()).toString();
        if (v2x.contains(field5747) || v2x.contains(field5748)) {
            return EquipmentSlot.HEAD;
        }
        if (v2x.contains(field5749) || ((String)v2x).contains(field5750)) {
            return EquipmentSlot.CHEST;
        }
        if (v2x.contains(field5751) || ((String)v2x).contains(field5752)) {
            return EquipmentSlot.LEGS;
        }
        if (v2x.contains(field5753) || ((String)v2x).contains(field5754)) {
            return EquipmentSlot.FEET;
        }
        return null;
    }

    private int method1382(EquipmentSlot param1) {
        switch (param1) {
            case FEET: {
                return 0;
            }
            case LEGS: {
                return 1;
            }
            case CHEST: {
                return 2;
            }
            case HEAD: {
                return 3;
            }
            default:
                return -1;
        }
    }

    private boolean method1384(ItemStack param1) {
        return param1 != null && !param1.isEmpty() && param1.getItem() instanceof ArmorItem;
    }

    static {
        field5735 = "Auto Armor";
        field5736 = "Автоматически надевает лучшую броню при её поднятии с земли";
        field5737 = "Задержка";
        field5738 = 50.0f;
        field5739 = 500.0f;
        field5740 = 5.0f;
        field5741 = "Использовать хотбар";
        field5742 = "Сохранять броню";
        field5743 = "Мин. прочность";
        field5744 = 10.0f;
        field5745 = 100.0f;
        field5746 = "Приоритет починки";
        field5730 = 5.0f;
        field5731 = 5.0f;
        field5732 = 1.03f;
        field5733 = 1.03f;
        field5747 = "helmet";
        field5748 = "cap";
        field5749 = "chestplate";
        field5750 = "tunic";
        field5751 = "leggings";
        field5752 = "pants";
        field5753 = "boots";
        field5754 = "shoes";
    }
}



