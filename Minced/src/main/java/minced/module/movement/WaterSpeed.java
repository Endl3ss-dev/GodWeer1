/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.BlockView
 */
package minced.module.movement;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.EventUpdate;
import minced.core.TimerUtil;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

public class WaterSpeed
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    boolean field2689;
    int field2690;
    BooleanSetting field2691;
    static float field2692;
    static double field2693;
    static double field2694;
    static double field2695;
    static double field2696;
    ModeSetting field2697 = new ModeSetting(field2701, field2702, field2703, field2704, field2705);
    static String field2698;
    static String field2699;
    static String field2700;
    static String field2701;
    static String field2702;
    static String field2703;
    static String field2704;
    static String field2705;
    static String field2706;
    static float field2707;
    static float field2708;
    static float field2709;
    SliderSetting field2710 = new SliderSetting(field2706, field2707, field2708, 2.0f, field2709, () -> this.field2697.isMode(field2698));
    static String field2711;
    BooleanSetting field2712 = new BooleanSetting(field2711, false, () -> this.field2697.isMode(field2714));
    static String field2713;
    static String field2714;
    static String field2715;
    static String field2716;
    static double field2717;
    static float field2718;
    static float field2719;
    static String field2720;
    static String field2721;
    static String field2722;
    static String field2723;
    static double field2724;
    static double field2725;
    static double field2726;
    static double field2727;

    private void method719() {
        boolean v1;
        if (!WaterSpeed.I.player.isTouchingWater()) {
            if (this.field2689) {
                WaterSpeed.I.options.jumpKey.setPressed(WaterSpeed.I.options.jumpKey.isPressed());
                WaterSpeed.I.options.sneakKey.setPressed(WaterSpeed.I.options.sneakKey.isPressed());
                this.field2689 = false;
            }
            this.field2690 = 0;
            return;
        }
        this.field2689 = true;
        ++this.field2690;
        boolean bl = v1 = (Boolean)this.field2691.getValue() != false && this.method720();
        if (v1) {
            WaterSpeed.I.options.jumpKey.setPressed(false);
            WaterSpeed.I.options.sneakKey.setPressed(true);
            return;
        }
        int v2 = this.field2690 % 10;
        if ((float)v2 < field2692) {
            WaterSpeed.I.options.jumpKey.setPressed(true);
            WaterSpeed.I.options.sneakKey.setPressed(false);
        } else {
            WaterSpeed.I.options.jumpKey.setPressed(false);
            WaterSpeed.I.options.sneakKey.setPressed(true);
        }
    }

    private boolean method720() {
        BlockPos v1 = WaterSpeed.I.player.getBlockPos().up();
        return WaterSpeed.I.world.getBlockState(v1).isAir();
    }

    private void method721() {
        if (!WaterSpeed.I.options.jumpKey.isPressed()) {
            return;
        }
        if (!WaterSpeed.I.player.isTouchingWater()) {
            return;
        }
        BlockPos v1 = WaterSpeed.I.player.getBlockPos();
        FluidState v2 = WaterSpeed.I.world.getFluidState(v1);
        if (!v2.isIn(FluidTags.WATER)) {
            return;
        }
        double v3 = (float)v1.getY() + v2.getHeight((BlockView)WaterSpeed.I.world, v1);
        double v5 = WaterSpeed.I.player.getEyeY();
        if (v5 >= v3 - field2693 && v5 <= v3 + field2694) {
            Vec3d v7 = WaterSpeed.I.player.getVelocity();
            WaterSpeed.I.player.setVelocity(v7.x, field2695, v7.z);
            double v8 = Math.sqrt(WaterSpeed.I.player.getVelocity().x * WaterSpeed.I.player.getVelocity().x + WaterSpeed.I.player.getVelocity().z * WaterSpeed.I.player.getVelocity().z);
            TimerUtil.method3546(v8 * field2696);
        }
    }

    public WaterSpeed() {
        super(field2699, field2700, Combat.Movement);
        this.field2691 = new BooleanSetting(field2713, false, () -> (Boolean)this.field2712.getValue());
        this.addSettings(this.field2697, this.field2710, this.field2712, this.field2691);
    }

    private void method723() {
        float v1 = (float)Math.toRadians(WaterSpeed.I.player.getYaw());
        double v2 = WaterSpeed.I.player.input.movementForward;
        double v4 = WaterSpeed.I.player.input.movementSideways;
        if (v2 == 0.0 && v4 == 0.0) {
            return;
        }
        double v6 = ((Float)this.field2710.getValue()).floatValue();
        double v8 = -Math.sin(v1) * v6;
        double v10 = Math.cos(v1) * v6;
        if (v2 < 0.0) {
            v8 = -v8;
            v10 = -v10;
        }
        WaterSpeed.I.player.setVelocity(v8, WaterSpeed.I.player.getVelocity().y, v10);
    }

    @EventHandler(2)
    public void method726(EventUpdate param1) {
        if (WaterSpeed.I.player == null || WaterSpeed.I.world == null) {
            return;
        }
        if (this.field2697.isMode(field2715)) {
            this.method727();
            return;
        }
        this.method728();
        if (this.field2697.isMode(field2716) && ((Boolean)this.field2712.getValue()).booleanValue()) {
            this.method719();
        }
    }

    private void method727() {
        if (WaterSpeed.I.player.isSwimming() || WaterSpeed.I.player.isGliding() || WaterSpeed.I.player.isSneaking()) {
            return;
        }
        double v1 = WaterSpeed.I.player.getBoundingBox().maxY - WaterSpeed.I.player.getBoundingBox().minY;
        if (v1 >= field2717) {
            return;
        }
        float v3 = WaterSpeed.I.player.hasStatusEffect(StatusEffects.SPEED) ? field2718 : field2719;
        TimerUtil.method3546(v3);
    }

    private void method728() {
        if (!WaterSpeed.I.player.isTouchingWater()) {
            return;
        }
        if (!WaterSpeed.I.player.isSwimming() && !this.field2697.isMode(field2720)) {
            return;
        }
        if (this.field2697.isMode(field2721)) {
            this.method729();
        } else if (this.field2697.isMode(field2722)) {
            this.method723();
        } else if (this.field2697.isMode(field2723)) {
            this.method721();
        }
    }

    @Override
    public void onEnable() {
        this.field2690 = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.field2690 = 0;
        if (WaterSpeed.I.player != null) {
            WaterSpeed.I.options.jumpKey.setPressed(false);
            WaterSpeed.I.options.sneakKey.setPressed(false);
        }
        super.onDisable();
    }

    private void method729() {
        if (WaterSpeed.I.player.age % (15 + WaterSpeed.I.player.getId() % 11) != 0) {
            return;
        }
        Vec3d v1 = WaterSpeed.I.player.getVelocity();
        double v2 = v1.x;
        double v4 = v1.y;
        double v6 = v1.z;
        double v8 = Math.sqrt(v2 * v2 + v6 * v6);
        double v10 = field2724 + Math.random() * field2725;
        double v12 = field2726;
        if (v8 > field2727) {
            if (v8 * v10 > v12) {
                double v14 = v12 / v8;
                WaterSpeed.I.player.setVelocity(v2 * v14, v4, v6 * v14);
            } else {
                WaterSpeed.I.player.setVelocity(v2 * v10, v4, v6 * v10);
            }
        }
    }

    static {
        field2699 = "WaterSpeed";
        field2700 = "Позволяет ускоренно двигаться в воде";
        field2701 = "Режим";
        field2702 = "Motion";
        field2703 = "Motion";
        field2704 = "Legit";
        field2705 = "Swim Boost";
        field2706 = "Скорость";
        field2707 = 0.5f;
        field2708 = 0.1f;
        field2709 = 0.05f;
        field2711 = "Авто пробел/шифт";
        field2713 = "Не всплывать";
        field2715 = "PolarBack";
        field2716 = "Legit";
        field2692 = 5.5f;
        field2720 = "Swim Boost";
        field2721 = "Legit";
        field2722 = "Motion";
        field2723 = "Swim Boost";
        field2724 = 1.01;
        field2725 = 0.015;
        field2726 = 0.209;
        field2727 = 0.01;
        field2693 = 0.2;
        field2694 = 0.2;
        field2695 = 0.2;
        field2696 = 5.0;
        field2717 = 1.5;
        field2718 = 0.32f;
        field2719 = 0.3f;
        field2714 = "Legit";
        field2698 = "Motion";
    }
}



