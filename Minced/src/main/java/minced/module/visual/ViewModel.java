/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.Arm
 */
package minced.module.visual;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.SliderSetting;
import minced.core.EventViewModel;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;

public class ViewModel
extends Module {
    SliderSetting field4383 = new SliderSetting(field4391, 0.0f, field4392, 2.0f, field4393);
    SliderSetting field4384 = new SliderSetting(field4394, 0.0f, field4395, 2.0f, field4396);
    SliderSetting field4385 = new SliderSetting(field4397, 0.0f, field4398, 2.0f, field4399);
    SliderSetting field4386 = new SliderSetting(field4400, 0.0f, field4401, 2.0f, field4402);
    SliderSetting field4387 = new SliderSetting(field4403, 0.0f, field4404, 2.0f, field4405);
    SliderSetting field4388 = new SliderSetting(field4406, 0.0f, field4407, 2.0f, field4408);
    static String field4389 = "View Model";
    static String field4390 = "Позволяет изменить положение рук";
    static String field4391 = "Правая рука X";
    static float field4392 = -2.0f;
    static float field4393 = 0.1f;
    static String field4394 = "Правая рука Y";
    static float field4395 = -2.0f;
    static float field4396 = 0.1f;
    static String field4397 = "Правая рука Z";
    static float field4398 = -2.0f;
    static float field4399 = 0.1f;
    static String field4400 = "Левая рука X";
    static float field4401 = -2.0f;
    static float field4402 = 0.1f;
    static String field4403 = "Левая рука Y";
    static float field4404 = -2.0f;
    static float field4405 = 0.1f;
    static String field4406 = "Левая рука Z";
    static float field4407 = -2.0f;
    static float field4408 = 0.1f;

    @EventHandler(2)
    public void method1109(EventViewModel param1) {
        MatrixStack v2 = param1.method2311();
        if (param1.cfr_renamed_0() == Arm.RIGHT) {
            v2.translate(((Float)this.field4383.getValue()).floatValue(), ((Float)this.field4384.getValue()).floatValue(), ((Float)this.field4385.getValue()).floatValue());
        } else {
            v2.translate(((Float)this.field4386.getValue()).floatValue(), ((Float)this.field4387.getValue()).floatValue(), ((Float)this.field4388.getValue()).floatValue());
        }
    }

    public ViewModel() {
        super(field4389, field4390, Combat.Visuals);
        this.addSettings(this.field4383, this.field4384, this.field4385, this.field4386, this.field4387, this.field4388);
    }
}



