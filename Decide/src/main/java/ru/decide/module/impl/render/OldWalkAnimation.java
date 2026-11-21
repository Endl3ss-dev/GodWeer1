/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10034
 *  net.minecraft.class_3532
 *  net.minecraft.class_572
 */
package ru.decide.module.impl.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10034;
import net.minecraft.class_3532;
import net.minecraft.class_572;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="Old Walk Animation", desc="\u0421\u0442\u0430\u0440\u0430\u044f \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0445\u043e\u0434\u044c\u0431\u044b (\u0434\u043e 1.9)", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class OldWalkAnimation
extends Module {
    public static OldWalkAnimation getInstance() {
        return Instance.get(OldWalkAnimation.class);
    }

    public static boolean appliesTo(Object state) {
        OldWalkAnimation module = OldWalkAnimation.getInstance();
        return module != null && module.isEnabled();
    }

    public static void applyPose(class_572<?> model, float angle, float amount) {
        if (model == null || amount <= 0.001f) {
            return;
        }
        model.field_3401.field_3654 = class_3532.method_15362((double)(angle * 0.6662f + (float)Math.PI)) * 2.0f * amount;
        model.field_27433.field_3654 = class_3532.method_15362((double)(angle * 0.6662f)) * 2.0f * amount;
        model.field_3401.field_3674 = (class_3532.method_15362((double)(angle * 0.2312f)) + 1.0f) * amount;
        model.field_27433.field_3674 = (class_3532.method_15362((double)(angle * 0.2812f)) - 1.0f) * amount;
    }

    public static void apply(class_572<?> model, class_10034 state) {
        if (state == null || !OldWalkAnimation.appliesTo(state)) {
            return;
        }
        OldWalkAnimation.applyPose(model, state.field_53450, state.field_53451);
    }
}

