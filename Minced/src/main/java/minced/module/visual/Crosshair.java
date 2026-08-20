/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 */
package minced.module.visual;
import minced.core.EventHandler;

import minced.core.ColorUtil;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventCrosshair;
import minced.core.RenderUtil;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.movement.FreeCamera;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class Crosshair
extends Module {
    SliderSetting field3310 = new SliderSetting(field3320, 2.0f, 0.0f, field3321, field3322);
    SliderSetting field3311;
    SliderSetting field3312 = new SliderSetting(field3323, field3324, 2.0f, field3325, field3326);
    BooleanSetting field3313;
    static float field3314;
    BooleanSetting field3315;
    static MinecraftClient I = MinecraftClient.getInstance();
    BooleanSetting field3316;
    BooleanSetting field3317;
    static String field3318;
    static String field3319;
    static String field3320;
    static float field3321;
    static float field3322;
    static String field3323;
    static float field3324;
    static float field3325;
    static float field3326;
    static String field3327;
    static float field3328;
    static String field3329;
    static String field3330;
    static String field3331;
    static String field3332;

    private void method926(DrawContext param1, float param2, float param3, float param4) {
        int v10;
        float v5 = ((Float)this.field3310.getValue()).floatValue();
        float v6 = ((Float)this.field3311.getValue()).floatValue();
        float v7 = ((Float)this.field3312.getValue()).floatValue();
        float v8 = (Boolean)this.field3313.getValue() != false ? v5 + field3314 * param4 : v5;
        int n = v10 = (Boolean)this.field3315.getValue() != false && Crosshair.I.targetedEntity != null ? ColorUtil.method1858(255, 64, 64) : -1;
        if (((Boolean)this.field3316.getValue()).booleanValue()) {
            RenderUtil.method394(param1, param2 + v8 - 0.5f, param3 - v6 / 2.0f - 0.5f, v7 + 1.0f, v6 + 1.0f, ColorUtil.method1858(0, 0, 0));
            RenderUtil.method394(param1, param2 - v8 - v7 - 0.5f, param3 - v6 / 2.0f - 0.5f, v7 + 1.0f, v6 + 1.0f, ColorUtil.method1858(0, 0, 0));
            RenderUtil.method394(param1, param2 - v6 / 2.0f - 0.5f, param3 - v8 - v7 - 0.5f, v6 + 1.0f, v7 + 1.0f, ColorUtil.method1858(0, 0, 0));
            RenderUtil.method394(param1, param2 - v6 / 2.0f - 0.5f, param3 + v8 - 0.5f, v6 + 1.0f, v7 + 1.0f, ColorUtil.method1858(0, 0, 0));
            RenderUtil.method394(param1, param2 + v8, param3 - v6 / 2.0f, v7, v6, v10);
            RenderUtil.method394(param1, param2 - v8 - v7, param3 - v6 / 2.0f, v7, v6, v10);
            RenderUtil.method394(param1, param2 - v6 / 2.0f, param3 - v8 - v7, v6, v7, v10);
            RenderUtil.method394(param1, param2 - v6 / 2.0f, param3 + v8, v6, v7, v10);
        } else {
            RenderUtil.method394(param1, param2 + v8, param3 - v6 / 2.0f, v7, v6, v10);
            RenderUtil.method394(param1, param2 - v8 - v7, param3 - v6 / 2.0f, v7, v6, v10);
            RenderUtil.method394(param1, param2 - v6 / 2.0f, param3 - v8 - v7, v6, v7, v10);
            RenderUtil.method394(param1, param2 - v6 / 2.0f, param3 + v8, v6, v7, v10);
        }
        if (((Boolean)this.field3317.getValue()).booleanValue() && v8 > 0.0f) {
            float v12 = param2 - v6 / 2.0f;
            float v13 = param3 - v6 / 2.0f;
            if (((Boolean)this.field3316.getValue()).booleanValue()) {
                RenderUtil.method394(param1, v12 - 0.5f, v13 - 0.5f, v6 + 1.0f, v6 + 1.0f, ColorUtil.method1858(0, 0, 0));
                RenderUtil.method394(param1, v12, v13, v6, v6, v10);
            } else {
                RenderUtil.method394(param1, v12, v13, v6, v6, v10);
            }
        }
    }

    public Crosshair() {
        super(field3318, field3319, Combat.Visuals);
        this.field3311 = new SliderSetting(field3327, 2.0f, 1.0f, field3328, 1.0f);
        this.field3315 = new BooleanSetting(field3329, false);
        this.field3313 = new BooleanSetting(field3330, false);
        this.field3316 = new BooleanSetting(field3331, true);
        this.field3317 = new BooleanSetting(field3332, true);
        this.addSettings(this.field3310, this.field3312, this.field3311, this.field3315, this.field3313, this.field3316, this.field3317);
    }

    @EventHandler(2)
    public void method927(EventCrosshair param1) {
        if (!this.isEnabled()) {
            return;
        }
        boolean v2 = Minced.getInstance().getModuleManager().method187(FreeCamera.class).isEnabled();
        if (!Crosshair.I.options.getPerspective().isFirstPerson() && !v2) {
            return;
        }
        if (Crosshair.I.player == null) {
            return;
        }
        DrawContext v3 = param1.method2299();
        float v4 = param1.method2301();
        float v5 = (float)I.getWindow().getScaledWidth() / 2.0f;
        float v6 = (float)I.getWindow().getScaledHeight() / 2.0f;
        float v7 = 1.0f - Crosshair.I.player.getAttackCooldownProgress(v4);
        this.method926(v3, v5, v6, v7);
        param1.method2298(true);
    }

    static {
        field3318 = "Crosshair";
        field3319 = "Кастомный прицел вместо ванильного";
        field3320 = "Зазор";
        field3321 = 6.0f;
        field3322 = 0.5f;
        field3323 = "Длина";
        field3324 = 3.0f;
        field3325 = 5.0f;
        field3326 = 0.5f;
        field3327 = "Толщина";
        field3328 = 6.0f;
        field3329 = "Менять цвет при наведении";
        field3330 = "Динамический разрыв";
        field3331 = "Обводка";
        field3332 = "Точка в центре";
        field3314 = 8.0f;
    }
}



