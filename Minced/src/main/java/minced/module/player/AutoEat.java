/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.util.Hand
 */
package minced.module.player;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventUpdate;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class AutoEat
extends Module {
    static String field4490 = "AutoEat";
    static String field4491 = "Автоматически ест еду при низком голоде";
    static String field4492 = "Не использовать с AttackAura";
    BooleanSetting field4493 = new BooleanSetting(field4492, false);
    static String field4494 = "Кол-во голода";
    static float field4495 = 18.0f;
    static float field4496 = 20.0f;
    static float field4497 = 0.5f;
    SliderSetting field4498 = new SliderSetting(field4494, field4495, 1.0f, field4496, field4497);
    static String field4499 = "Есть золотые яблоки";
    BooleanSetting field4500 = new BooleanSetting(field4499, true);
    static String field4501 = "Здоровье для ГА";
    static float field4502 = 14.0f;
    static float field4503 = 20.0f;
    static float field4504 = 0.5f;
    SliderSetting field4505 = new SliderSetting(field4501, field4502, 1.0f, field4503, field4504, () -> (Boolean)this.field4500.getValue());
    static String field4506 = "Приоритет ГА";
    BooleanSetting field4507 = new BooleanSetting(field4506, true, () -> (Boolean)this.field4500.getValue());
    boolean field4508 = false;
    boolean field4509 = false;
    Hand field4510 = null;
    static MinecraftClient I = MinecraftClient.getInstance();
    static float field4511;

    public AutoEat() {
        super(field4490, field4491, Combat.Player);
        this.addSettings(this.field4493, this.field4498, this.field4500, this.field4505, this.field4507);
    }

    @EventHandler(2)
    public void method1132(EventUpdate param1) {
        Object v2;
        if (AutoEat.I.player == null || AutoEat.I.world == null) {
            this.method1137();
            return;
        }
        if (AutoEat.I.currentScreen != null && !(AutoEat.I.currentScreen instanceof InventoryScreen)) {
            this.method1137();
            return;
        }
        if (((Boolean)this.field4493.getValue()).booleanValue() && (v2 = (AttackAura)Minced.getInstance().getModuleManager().method187(AttackAura.class)) != null && ((Module)v2).isEnabled() && AttackAura.method861() != null) {
            this.method1137();
            return;
        }
        v2 = AutoEat.I.player.getOffHandStack();
        ItemStack v3 = AutoEat.I.player.getMainHandStack();
        boolean v4 = this.method1134((ItemStack)v2);
        boolean v5 = this.method1134(v3);
        boolean v6 = v4 || v5;
        boolean v7 = this.method1136((ItemStack)v2) && !v4;
        boolean v8 = this.method1136(v3) && !v5;
        boolean v9 = v7 || v8;
        float v10 = AutoEat.I.player.getHealth() + AutoEat.I.player.getAbsorptionAmount();
        int v11 = AutoEat.I.player.getHungerManager().getFoodLevel();
        if (this.field4509) {
            if (v10 > ((Float)this.field4505.getValue()).floatValue()) {
                this.method1137();
            } else {
                if (AutoEat.I.player.isUsingItem()) {
                    return;
                }
                this.method1137();
            }
        }
        if (((Boolean)this.field4500.getValue()).booleanValue() && v6 && v10 <= ((Float)this.field4505.getValue()).floatValue()) {
            Hand v12 = v4 ? Hand.OFF_HAND : Hand.MAIN_HAND;
            this.method1138(v12, true);
            return;
        }
        if (v9 && v11 <= ((Float)this.field4498.getValue()).intValue()) {
            if (((Boolean)this.field4507.getValue()).booleanValue() && ((Boolean)this.field4500.getValue()).booleanValue() && v6 && v10 <= ((Float)this.field4505.getValue()).floatValue() + field4511) {
                return;
            }
            Hand v12 = v7 ? Hand.OFF_HAND : Hand.MAIN_HAND;
            this.method1138(v12, false);
            return;
        }
        if (this.field4508 && !this.field4509 && AutoEat.I.player.isUsingItem()) {
            if (v11 >= 20) {
                this.method1137();
            }
            return;
        }
        if (this.field4508) {
            this.method1137();
        }
    }

    private boolean method1134(ItemStack param1) {
        if (param1.isEmpty()) {
            return false;
        }
        return param1.getItem() == Items.GOLDEN_APPLE || param1.getItem() == Items.ENCHANTED_GOLDEN_APPLE;
    }

    private boolean method1136(ItemStack param1) {
        if (param1.isEmpty()) {
            return false;
        }
        return param1.contains(DataComponentTypes.FOOD);
    }

    @Override
    public void onDisable() {
        this.method1137();
        super.onDisable();
    }

    private void method1137() {
        if (this.field4508) {
            if (AutoEat.I.player != null && AutoEat.I.player.isUsingItem()) {
                AutoEat.I.interactionManager.stopUsingItem((PlayerEntity)AutoEat.I.player);
            }
            AutoEat.I.options.useKey.setPressed(false);
            this.field4508 = false;
            this.field4509 = false;
            this.field4510 = null;
        }
    }

    private void method1138(Hand param1, boolean param2) {
        if (this.field4508 && this.field4510 == param1 && this.field4509 == param2) {
            if (!AutoEat.I.player.isUsingItem()) {
                AutoEat.I.interactionManager.interactItem((PlayerEntity)AutoEat.I.player, param1);
                AutoEat.I.options.useKey.setPressed(true);
            }
            return;
        }
        if (this.field4508) {
            this.method1137();
        }
        this.field4508 = true;
        this.field4509 = param2;
        this.field4510 = param1;
        AutoEat.I.interactionManager.interactItem((PlayerEntity)AutoEat.I.player, param1);
        AutoEat.I.options.useKey.setPressed(true);
    }

    static {
        field4511 = 4.0f;
    }
}



