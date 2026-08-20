/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.math.MathHelper
 *  org.joml.Vector4f
 *  sg.ec.eh
 */
package minced.core;

import java.util.ArrayList;
import java.util.List;
import minced.core.MathUtil;
import minced.core.ScissorUtil;
import minced.core.ColorPickerRenderer;
import minced.core.BindPopup;
import minced.core.ThemeColors;
import minced.core.ColorPickerElement;
import minced.core.BindPopupState;
import minced.core.AnimatedFloat;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.MAIN;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector4f;
import sg.ec.eh;

public class GuiAnimationUtil
implements ColorPickerRenderer {
    Combat field11561;
    static float field11562;
    static float field11563;
    List field11564;
    float field11565;
    AnimatedFloat I;
    float field11566;
    float field11567;
    float field11568;
    float field11569;
    static float field11570;
    static float field11571;
    static float field11572;
    static float field11573;
    static float field11574;
    static float field11575;
    static float field11576;
    static float field11577;
    float field11578 = 0.0f;
    static float field11579;
    static float field11580;
    List field11581;
    static float field11582;
    static float field11583;
    static float field11584;
    static double field11585;
    static float field11586;
    static float field11587;
    static float field11588;
    static float field11589;
    static float field11590;
    static float field11591;
    static float field11592;
    static float field11593;
    static float field11594;
    static float field11595;
    static float field11596;

    public void method3254(Combat param1) {
        this.field11561 = param1;
    }

    @Override
    public boolean method3159(double param1, double param3, int param5, double param6, double param8) {
        float v10 = field11562;
        float v11 = this.l() - v10 - field11563;
        if (MathUtil.cfr_underscore((float)param1, (float)param3, this.method3264(), this.method3256() + v10, this.method3258(), v11)) {
            float v12 = this.method3256() + v10;
            float v13 = v12 + v11;
            for (eh v15 : (Iterable<eh>)(this.field11564)) {
                float v16 = v15.ч();
                float v17 = v16 + (Float)v15.н();
                if (!(v17 > v12) || !(v16 < v13) || !((Boolean)v15.У(param1, param3, param5, param6, param8))) continue;
                return true;
            }
        }
        return false;
    }

    public float method3194() {
        return this.field11565;
    }

    @Override
    public void method3161(DrawContext param1, float param2, float param3, float param4) {
        this.I.setTarget(this.field11565);
        float v5 = this.I.getValue();
        RenderUtil.method390(this.field11566, this.field11567, this.field11568, this.field11569, field11570, ThemeColors.method3199(MAIN.HEADER), 1.0f);
        this.method3255(param1);
        this.method3269(param1, param2, param3, param4, v5);
    }

    private void method3255(DrawContext param1) {
        String v2 = this.field11561.method168();
        String v3 = this.field11561.method169();
        int v4 = (int)(this.field11566 + field11571);
        float v5 = this.field11567 + field11572;
        RenderUtil.method403(this.field11566, this.field11567, this.field11568, field11573, new Vector4f(field11574, 0.0f, field11575, 0.0f), ThemeColors.method3199(MAIN.WINDOW_BG), 1.0f);
        Fonts.field12508[20].method2711(param1, v3, v4 - 1, v5, ThemeColors.method3199(MAIN.HEADER_TEXT));
        Fonts.I[16].method2711(param1, v2, (float)v4 + field11576, v5 - field11577, ThemeColors.method3199(MAIN.HEADER_TEXT));
    }

    public float method3256() {
        return this.field11567;
    }

    @Override
    public boolean method3165(float param1, float param2, int param3) {
        for (eh v5 : (Iterable<eh>)(this.field11564)) {
            if (!((Boolean)v5.У(param1, param2, param3))) continue;
            return true;
        }
        return false;
    }

    public float l() {
        return this.field11569;
    }

    @Override
    public void method3162(int param1, int param2, int param3) {
        for (eh v5 : (Iterable<eh>)(this.field11564)) {
            v5.У(param1, param2, param3);
        }
    }

    public List method3257() {
        return this.field11564;
    }

    public float method3258() {
        return this.field11568;
    }

    public GuiAnimationUtil() {
        this.field11568 = field11579;
        this.field11569 = field11580;
        this.field11564 = new ArrayList();
        this.field11581 = new ArrayList();
        this.I = new AnimatedFloat(0.0f, field11582);
    }

    private void method3259(Combat param1) {
        this.field11561 = param1;
        for (Module v3 : Minced.getInstance().getModuleManager().method178()) {
            if (v3.getCategory() != param1) continue;
            eh v4 = new eh(v3);
            v4.cfr_renamed_168(this);
            this.field11581.add(v4);
        }
        this.field11564.addAll(this.field11581);
    }

    public void method3193(float param1) {
        this.field11578 = param1;
    }

    public AnimatedFloat method3208() {
        return this.I;
    }

    public void method3260(float param1) {
        this.field11566 = param1;
    }

    @Override
    public void method3167(DrawContext param1, float param2, float param3, float param4) {
        for (eh v6 : (Iterable<eh>)(this.field11564)) {
            v6.х(param1, param2, param3, param4);
        }
    }

    @Override
    public void method3158(float param1, float param2, int param3) {
        for (eh v5 : (Iterable<eh>)(this.field11564)) {
            v5.я(param1, param2, param3);
        }
    }

    public void method3211(float param1) {
        this.field11565 = param1;
    }

    @Override
    public boolean method3164(double param1, double param3, double param5, double param7) {
        for (eh v10 : (Iterable<eh>)(this.field11564)) {
            if (!((Boolean)v10.У(param1, param3, param5, param7))) continue;
            return true;
        }
        if (MathUtil.cfr_underscore((float)param1, (float)param3, this.method3264(), this.method3256(), this.method3258(), this.l())) {
            boolean v13;
            float v9 = field11583;
            float v10 = this.l() - v9 - field11584;
            boolean v11 = this.field11578 > v10;
            float v12 = this.field11565;
            this.method3211((float)((double)this.method3194() + param7 * field11585));
            this.field11565 = MathHelper.clamp((float)this.field11565, (float)(-this.field11578 + v10), (float)0.0f);
            boolean bl = v13 = v11 && this.field11565 != v12;
            if (v13) {
                ColorPickerElement.method3219();
                BindPopup.method3169();
                BindPopupState.method3274();
            }
            return v13;
        }
        return false;
    }

    @Override
    public void method3155(int param1, int param2, int param3) {
        for (eh v5 : (Iterable<eh>)(this.field11564)) {
            v5.я(param1, param2, param3);
        }
    }

    @Override
    public boolean method3156(double param1, double param3, double param5, double param7) {
        for (eh v10 : (Iterable<eh>)(this.field11564)) {
            if (!((Boolean)v10.я(param1, param3, param5, param7))) continue;
            return true;
        }
        return false;
    }

    public Combat method3261() {
        return this.field11561;
    }

    public GuiAnimationUtil(Combat param1) {
        this.field11568 = field11586;
        this.field11569 = field11587;
        this.field11564 = new ArrayList();
        this.field11581 = new ArrayList();
        this.I = new AnimatedFloat(0.0f, field11588);
        this.method3259(param1);
    }

    public void method3263(float param1) {
        this.field11568 = param1;
    }

    public float method3201() {
        return this.field11578;
    }

    @Override
    public boolean method3163(double param1, double param3, int param5, double param6, double param8) {
        for (eh v11 : (Iterable<eh>)(this.field11564)) {
            if (!((Boolean)v11.я(param1, param3, param5, param6, param8))) continue;
            return true;
        }
        return false;
    }

    @Override
    public void method3160(float param1, float param2, int param3) {
        for (eh v5 : (Iterable<eh>)(this.field11564)) {
            v5.х(param1, param2, param3);
        }
    }

    public float method3264() {
        return this.field11566;
    }

    public void method3265(float param1) {
        this.field11567 = param1;
    }

    public void method3266(AnimatedFloat param1) {
        this.I = param1;
    }

    public void method3267(float param1) {
        this.field11569 = param1;
    }

    @Override
    public void method3166(char param1, int param2) {
        for (eh v4 : (Iterable<eh>)(this.field11564)) {
            v4.я(param1, param2);
        }
    }

    public List method3268() {
        return this.field11581;
    }

    @Override
    public void method3157(char param1, int param2) {
        for (eh v4 : (Iterable<eh>)(this.field11564)) {
            v4.У(param1, param2);
        }
    }

    private void method3269(DrawContext param1, float param2, float param3, float param4, float param5) {
        float v6 = field11589;
        float v7 = this.l() - v6;
        if (this.field11578 > v7) {
            this.field11565 = Math.clamp(this.field11565, -this.field11578 + v7, 0.0f);
            param5 = Math.clamp(param5, -this.field11578 + v7, 0.0f);
        } else {
            param5 = 0.0f;
            this.field11565 = 0.0f;
        }
        float v8 = this.method3256() + v6;
        float v9 = v8 + v7;
        ScissorUtil.method2621(this.method3264(), this.method3256() + v6, this.method3258(), v7 - 1.0f);
        float v10 = 0.0f;
        for (eh v12 : (Iterable<eh>)(this.field11564)) {
            float v14;
            float v13;
            v12.cfr_renamed_168(this.method3264() + field11590);
            v12.С(this.method3256() + v6 + v10 + param5);
            v12.ч(this.method3258() - field11591);
            v12.з(field11592);
            if (v12.Ы().getValue() > 0.0f) {
                v13 = (float)v12.O().stream().mapToDouble(param0 -> param0.н() * param0.cfr_renamed_168()).sum();
                v14 = field11593 + (v13 + field11594) * v12.Ы().getValue();
                v12.з(v14);
            }
            if ((v14 = (v13 = v12.ч()) + v12.н()) > v8 && v13 < v9) {
                v12.cfr_renamed_136(param1, param2, param3, param4);
            }
            v10 += v12.н() + 2.0f;
        }
        this.field11578 = v10;
        ScissorUtil.method2620();
    }

    @Override
    public void method3154(float param1, float param2, int param3) {
        float v4 = field11595;
        float v5 = this.l() - v4 - field11596;
        if (MathUtil.cfr_underscore(param1, param2, this.method3264(), this.method3256() + v4, this.method3258(), v5)) {
            for (eh v7 : (Iterable<eh>)(this.field11564)) {
                float v8 = v7.ч();
                float v9 = v8 + (Float)v7.н();
                if (!(v9 > this.method3256() + v4) || !(v8 < this.method3256() + v4 + v5) || !MathUtil.cfr_underscore(param1, param2, v7.С(), v7.ч(), v7.з(), v7.н())) continue;
                v7.cfr_renamed_136(param1, param2, param3);
            }
        }
    }

    public void method3270(List param1) {
        this.field11564 = param1;
    }

    static {
        field11586 = 116.0f;
        field11587 = 260.0f;
        field11588 = 10.0f;
        field11570 = 5.0f;
        field11571 = 8.0f;
        field11572 = 9.0f;
        field11573 = 22.5f;
        field11574 = 5.0f;
        field11575 = 5.0f;
        field11576 = 12.5f;
        field11577 = 0.5f;
        field11589 = 24.0f;
        field11590 = 4.0f;
        field11591 = 8.0f;
        field11592 = 17.5f;
        field11593 = 17.5f;
        field11594 = 6.5f;
        field11595 = 28.0f;
        field11596 = 4.0f;
        field11562 = 28.0f;
        field11563 = 4.0f;
        field11583 = 28.0f;
        field11584 = 4.0f;
        field11585 = 20.0;
        field11579 = 116.0f;
        field11580 = 260.0f;
        field11582 = 10.0f;
    }
}



