/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.module.visual;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.EventAspectRatio;
import minced.core.Module;
import minced.enums.Combat;

public class AspectRatio
extends Module {
    static String field6144 = "Aspect Ratio";
    static String field6145 = "Изменяет соотношение сторон экрана";
    static String field6146 = "Соотношение";
    static String field6147 = "16:9";
    static String field6148;
    static String field6149;
    static String field6150;
    static String field6151;
    static String field6152;
    ModeSetting field6153 = new ModeSetting(field6146, field6147, field6148, field6149, field6150, field6151, field6152);
    static String field6154;
    static float field6155;
    static float field6156;
    static float field6157;
    SliderSetting field6158 = new SliderSetting(field6154, 1.0f, field6155, field6156, field6157, () -> this.field6153.isMode(field6166));
    static String field6159;
    static String field6160;
    static String field6161;
    static String field6162;
    static float field6163;
    static float field6164;
    static float field6165;
    static String field6166;

    public AspectRatio() {
        super(field6144, field6145, Combat.Visuals);
        this.addSettings(this.field6153, this.field6158);
    }

    @EventHandler(2)
    public void method1463(EventAspectRatio param1) {
        float f;
        String v3 = (String)this.field6153.getValue();
        int v4 = -1;
        switch (v3.hashCode()) {
            case 51821: {
                if (!v3.equals(field6159)) break;
                v4 = 0;
                break;
            }
            case 1513508: {
                if (!v3.equals(field6160)) break;
                v4 = 1;
                break;
            }
            case 48936: {
                if (!v3.equals(field6161)) break;
                v4 = 2;
                break;
            }
            case 46918548: {
                if (!v3.equals(field6162)) break;
                v4 = 3;
            }
        }
        switch (v4) {
            case 0: {
                f = field6163;
                break;
            }
            case 1: {
                f = field6164;
                break;
            }
            case 2: {
                f = 1.0f;
                break;
            }
            case 3: {
                f = field6165;
                break;
            }
            default: {
                f = ((Float)this.field6158.getValue()).floatValue();
            }
        }
        float v2 = f;
        param1.method2447(v2);
    }

    static {
        field6148 = "4:3";
        field6149 = "16:9";
        field6150 = "1:1";
        field6151 = "16:10";
        field6152 = "Кастомное";
        field6154 = "Значение";
        field6155 = 0.1f;
        field6156 = 5.0f;
        field6157 = 0.1f;
        field6159 = "4:3";
        field6160 = "16:9";
        field6161 = "1:1";
        field6162 = "16:10";
        field6163 = 1.3333334f;
        field6164 = 1.7777778f;
        field6165 = 1.6f;
        field6166 = "Кастомное";
    }
}
