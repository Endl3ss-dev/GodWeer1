/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.item.Items
 */
package minced.module.combat;
import minced.core.EventHandler;

import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventPostUpdate;
import minced.core.Module;
import minced.enums.Combat;
import minced.mixin.accessor.IMinecraftClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public final class AutoGApple
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    BooleanSetting field1511;
    SliderSetting field1512 = new SliderSetting(field1516, field1517, 1.0f, field1518, field1519);
    boolean field1513;
    static String field1514;
    static String field1515;
    static String field1516;
    static float field1517;
    static float field1518;
    static float field1519;
    static String field1520;

    @EventHandler(2)
    public void method516(EventPostUpdate param1) {
        if (!(AutoGApple.I.player.getOffHandStack().isEmpty() || AutoGApple.I.player.getOffHandStack().getItem() != Items.GOLDEN_APPLE && AutoGApple.I.player.getOffHandStack().getItem() != Items.ENCHANTED_GOLDEN_APPLE)) {
            float f = AutoGApple.I.player.getHealth();
            float f2 = (Boolean)this.field1511.getValue() != false ? AutoGApple.I.player.getAbsorptionAmount() : 0.0f;
            if (f + f2 <= ((Float)this.field1512.getValue()).floatValue()) {
                this.field1513 = true;
                if (AutoGApple.I.currentScreen != null && !AutoGApple.I.player.isUsingItem()) {
                    ((IMinecraftClient)I).onItemUse();
                } else {
                    AutoGApple.I.options.useKey.setPressed(true);
                }
            } else if (this.field1513) {
                this.field1513 = false;
                AutoGApple.I.options.useKey.setPressed(false);
            }
        } else if (this.field1513) {
            this.field1513 = false;
            AutoGApple.I.options.useKey.setPressed(false);
        }
    }

    public AutoGApple() {
        super(field1514, field1515, Combat.Combat);
        this.field1511 = new BooleanSetting(field1520, false);
        this.addSettings(this.field1512, this.field1511);
    }

    static {
        field1514 = "Auto GApple";
        field1515 = "Автоматически использует золотые яблоки при низком уровне здоровья";
        field1516 = "Здоровье";
        field1517 = 14.0f;
        field1518 = 20.0f;
        field1519 = 0.5f;
        field1520 = "Золотые сердца";
    }
}



