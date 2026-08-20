/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.module.misc;
import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.Module;
import minced.enums.Combat;

public class ToggleSounds
extends Module {
    static ModeSetting field4028;
    static String field4029;
    static String field4030;
    static String field4031;
    static String field4032;
    static String field4033;
    static String field4034;
    static String field4035;
    static String field4036;
    static String field4037;
    static String field4038;
    static String field4039;
    static String field4040;
    static String field4041;
    static String field4042 = "Тип 1";
    static String field4043;
    static String field4044;
    static String field4045;
    static String field4046;
    static String field4047;
    static float field4048;
    static float field4049;
    public static SliderSetting field4050;
    static String field4051;
    static String field4052;

    public static String method1052(boolean param0) {
        String string;
        String v1 = (String)field4028.getValue();
        int v3 = -1;
        switch (v1.hashCode()) {
            case 1010305146: {
                if (!v1.equals(field4029)) break;
                v3 = 0;
                break;
            }
            case 1010305147: {
                if (!v1.equals(field4030)) break;
                v3 = 1;
                break;
            }
            case 1010305148: {
                if (!v1.equals(field4031)) break;
                v3 = 2;
                break;
            }
            case 1010305149: {
                if (!v1.equals(field4032)) break;
                v3 = 3;
            }
        }
        switch (v3) {
            case 0: {
                if (param0) {
                    string = field4033;
                    break;
                }
                string = field4034;
                break;
            }
            case 1: {
                if (param0) {
                    string = field4035;
                    break;
                }
                string = field4036;
                break;
            }
            case 2: {
                if (param0) {
                    string = field4037;
                    break;
                }
                string = field4038;
                break;
            }
            case 3: {
                if (param0) {
                    string = field4039;
                    break;
                }
                string = field4040;
                break;
            }
            default: {
                string = null;
            }
        }
        return string;
    }

    public static void method1053() {
        field4028 = new ModeSetting(field4041, field4042, field4043, field4044, field4045, field4046);
        field4050 = new SliderSetting(field4047, field4048, 0.0f, field4049, 1.0f);
    }

    public ToggleSounds() {
        super(field4051, field4052, Combat.Miscellaneous);
        this.addSettings(field4028, field4050);
    }

    static {
        field4051 = "Toggle Sounds";
        field4052 = "Воспроизводит звуки при изменении состояния модулей";
        field4029 = "Тип 1";
        field4030 = "Тип 2";
        field4031 = "Тип 3";
        field4032 = "Тип 4";
        field4033 = "enabled0";
        field4034 = "disabled0";
        field4035 = "enabled1";
        field4036 = "disabled1";
        field4037 = "enabled2";
        field4038 = "disabled2";
        field4039 = "enabled3";
        field4040 = "disabled3";
        field4041 = "Тип звука";
        field4043 = "Тип 1";
        field4044 = "Тип 2";
        field4045 = "Тип 3";
        field4046 = "Тип 4";
        field4047 = "Громкость";
        field4048 = 75.0f;
        field4049 = 100.0f;
        ToggleSounds.method1053();
    }
}
