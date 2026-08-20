/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.module.visual;
import minced.core.Module;
import minced.enums.Combat;

public class ArmorDurabilityView
extends Module {
    static float field4691;
    static float field4692;
    static float field4693;
    static float field4694;
    static float field4695;
    static float field4696;
    static float field4697;
    static float field4698;
    static float field4699;
    static float field4700;
    static float field4701;
    static float field4702;
    static float field4703;
    static float field4704;
    static float field4705;
    static float field4706;
    static float field4707;
    static float field4708;
    static float field4709;
    static float field4710;
    static float field4711;
    static float[][] field4712;
    static String field4713;
    static String field4714;
    static float field4715;
    static float field4716;

    public static void method1183() {
        field4712 = new float[][]{{field4691, 0.0f, 0.0f}, {field4692, 0.0f, 0.0f}, {1.0f, 0.0f, 0.0f}, {1.0f, field4693, 0.0f}, {1.0f, field4694, 0.0f}, {1.0f, field4695, 0.0f}, {1.0f, field4696, 0.0f}, {1.0f, 1.0f, 0.0f}, {field4697, 1.0f, 0.0f}, {field4698, 1.0f, 0.0f}, {field4699, 1.0f, 0.0f}, {0.0f, field4700, 0.0f}, {0.0f, field4701, 0.0f}, {0.0f, field4702, 0.0f}, {0.0f, field4703, 0.0f}, {0.0f, 1.0f, 0.0f}, {field4704, 1.0f, field4705}, {field4706, 1.0f, field4707}, {field4708, 1.0f, field4709}, {field4710, 1.0f, field4711}, {1.0f, 1.0f, 1.0f}};
    }

    public ArmorDurabilityView() {
        super(field4713, field4714, Combat.Visuals);
    }

    public static float[] method1184(float param0) {
        if (param0 < 0.0f || param0 > 1.0f) {
            return new float[]{1.0f, 1.0f, 1.0f};
        }
        int v1 = (int)(param0 * field4715 / field4716);
        return field4712[Math.min(v1, field4712.length - 1)];
    }

    static {
        field4713 = "Armor Durability View";
        field4714 = "Красит броню в зависимости от прочности";
        field4715 = 100.0f;
        field4716 = 5.0f;
        field4691 = 0.6f;
        field4692 = 0.8f;
        field4693 = 0.2f;
        field4694 = 0.4f;
        field4695 = 0.6f;
        field4696 = 0.8f;
        field4697 = 0.9f;
        field4698 = 0.7f;
        field4699 = 0.5f;
        field4700 = 0.6f;
        field4701 = 0.7f;
        field4702 = 0.8f;
        field4703 = 0.9f;
        field4704 = 0.2f;
        field4705 = 0.2f;
        field4706 = 0.4f;
        field4707 = 0.4f;
        field4708 = 0.6f;
        field4709 = 0.6f;
        field4710 = 0.8f;
        field4711 = 0.8f;
        ArmorDurabilityView.method1183();
    }
}

