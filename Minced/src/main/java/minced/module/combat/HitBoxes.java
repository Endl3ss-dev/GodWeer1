/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 */
package minced.module.combat;
import minced.core.EventHandler;

import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventVelocityMultiplier;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.entity.LivingEntity;

public class HitBoxes
extends Module {
    static String field4738;
    static float field4739;
    static float field4740;
    public static SliderSetting field4741;
    static String field4742;
    public static BooleanSetting field4743;
    static String field4744;
    static String field4745;

    public static void method1187() {
        field4741 = new SliderSetting(field4738, field4739, 0.0f, 1.0f, field4740);
        field4743 = new BooleanSetting(field4742, true);
    }

    public boolean method1188() {
        return this.isEnabled() && (Boolean)field4743.getValue() != false;
    }

    public HitBoxes() {
        super(field4744, field4745, Combat.Combat);
        this.addSettings(field4741, field4743);
    }

    @EventHandler(2)
    public void method1189(EventVelocityMultiplier param1) {
        if (!(param1.method2446() instanceof LivingEntity)) {
            return;
        }
        param1.method2443(((Float)field4741.getValue()).floatValue());
    }

    static {
        field4744 = "Hit Boxes";
        field4745 = "Изменяет размеры хитбоксов существ";
        field4738 = "Размер";
        field4739 = 0.2f;
        field4740 = 0.05f;
        field4742 = "Отображать Размер";
        HitBoxes.method1187();
    }
}

