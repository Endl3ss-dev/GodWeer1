/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.util.math.MathHelper
 *  sg.ec.2Ю
 *  sg.ec.p32
 *  sg.ec.pn
 */
package minced.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import minced.core.ColorUtil;
import minced.core.MathUtil;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.Setting;
import minced.core.ModeSetting;
import minced.core.EventMouseClick;
import minced.core.EventRender2D;
import minced.core.EventMouseRelease;
import minced.core.EventBus;
import minced.core.ThemeColors;
import minced.core.Easings;
import minced.core.AnimatedFloat;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.Minced;
import minced.enums.MAIN;
import minced.manager.Dragging;
import minced.module.visual.Interface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.math.MathHelper;
import sg.ec._2yu;
import sg.ec.p32;
import sg.ec.pn;

public class HudWindow {
    MultiBooleanSetting field12188;
    float field12189;
    float field12190;
    static float field12191;
    List field12192;
    AnimatedFloat field12193 = new AnimatedFloat(0.0f, field12219, Easings.field11952);
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field12194;
    static double field12195;
    boolean field12196;
    String field12197;
    static String field12198;
    static String field12199;
    static String field12200;
    static float field12201;
    float field12202;
    static float field12203;
    float field12204;
    public boolean field12205;
    boolean field12206;
    static HudWindow field12207;
    Dragging field12208;
    AnimatedFloat field12209 = new AnimatedFloat(0.0f, field12220, Easings.field11952);
    boolean field12210;
    float field12211;
    float field12212;
    float field12213;
    float field12214;
    float field12215;
    float field12216;
    float field12217;
    float field12218;
    static float field12219;
    static float field12220;
    List field12221 = new ArrayList();
    float field12222;
    float field12223;
    static float field12224;
    static float field12225;
    static String field12226;
    static String field12227;
    static float field12228;
    static String field12229;
    static double field12230;
    static float field12231;
    static double field12232;
    static double field12233;
    static float field12234;
    static float field12235;
    static float field12236;
    static float field12237;
    static float field12238;
    static float field12239;
    static float field12240;
    static float field12241;
    static String field12242;
    static float field12243;
    static float field12244;

    public MultiBooleanSetting method3449() {
        return this.field12188;
    }

    public float method3450() {
        return this.field12189;
    }

    public void O(float param1) {
        this.field12190 = param1;
    }

    private float method3451() {
        float v1 = field12191;
        for (_2yu v3 : (Iterable<_2yu>)(this.field12192)) {
            v1 += (Float) v3.н();
        }
        return v1;
    }

    public List method3452() {
        return this.field12192;
    }

    public AnimatedFloat method3453() {
        return this.field12193;
    }

    @EventHandler(2)
    public void method3454(EventRender2D param1) {
        double v2 = (double)I.getWindow().getFramebufferWidth() / (double)I.getWindow().getWidth();
        double v4 = (double)I.getWindow().getFramebufferHeight() / (double)I.getWindow().getHeight();
        float v6 = (float)(HudWindow.I.mouse.getX() * v2 / field12194);
        float v7 = (float)(HudWindow.I.mouse.getY() * v4 / field12195);
        if (!this.field12196 && this.field12197.equals(field12198)) {
            this.field12202 = Fonts.field12496[16].method2719(field12199) + Fonts.field12491[12].method2719(field12200) + field12201;
            this.field12190 = ((float)I.getWindow().getScaledWidth() - this.field12202) / 2.0f;
            this.field12204 = (float)I.getWindow().getScaledHeight() / 2.0f + field12203;
        }
        if (!(HudWindow.I.currentScreen instanceof ChatScreen) || !Minced.getInstance().getModuleManager().method187(Interface.class).isEnabled()) {
            if (this.field12205 || this.field12206) {
                this.field12205 = false;
                if (this.field12206) {
                    this.field12206 = false;
                    field12207 = null;
                    this.field12208.save();
                }
            }
            this.field12193.setTarget(0.0f);
            this.field12209.setTarget(0.0f);
            return;
        }
        this.field12210 = this.method3497(v6, v7) && this.field12208.method1662().stream().noneMatch(param0 -> param0 != null && ((HudWindow)param0).field12205) && (field12207 == null || field12207 == this);
        this.field12193.setTarget(this.field12210 || this.field12196 && this.field12206 && field12207 == this ? 1.0f : 0.0f);
        this.field12209.setTarget(this.field12205 ? 1.0f : 0.0f);
        if (this.field12196 && this.field12206) {
            int v8 = I.getWindow().getScaledWidth();
            int v9 = I.getWindow().getScaledHeight();
            float v10 = MathHelper.clamp((float)(v6 - this.field12211), (float)0.0f, (float)((float)v8 - this.field12202));
            float v11 = MathHelper.clamp((float)(v7 - this.field12212), (float)0.0f, (float)((float)v9 - this.field12213));
            this.field12189 = this.field12190;
            this.field12214 = this.field12204;
            this.field12190 = v10;
            this.field12204 = v11;
            if (this.field12209.getValue() > 0.0f) {
                this.field12216 = this.field12190 + this.field12215;
                this.field12218 = this.field12204 + this.field12217;
            }
        }
        if (this.field12209.getValue() > 0.0f && !this.field12192.isEmpty()) {
            this.method3488(param1, v6, v7);
        }
    }

    public void method3455(float param1) {
        this.field12216 = param1;
    }

    public float method3456() {
        return this.field12211;
    }

    public void cfr_underscore(boolean param1) {
        this.field12206 = param1;
    }

    public HudWindow(String param1, float param2, float param3, MultiBooleanSetting param4, boolean param5) {
        this.field12192 = new ArrayList();
        this.field12197 = param1;
        this.field12190 = param2;
        this.field12204 = param3;
        this.field12222 = param2;
        this.field12223 = param3;
        this.field12188 = param4;
        this.field12196 = param5;
        this.field12208 = Minced.getInstance().getDraggingManager();
        this.field12208.method1666(this);
        EventBus.method3013(this);
    }

    public float method3457() {
        return this.field12190;
    }

    public float method3458() {
        return this.field12212;
    }

    public void method3459(float param1) {
        this.field12202 = param1;
    }

    private float method3460() {
        float v1 = field12224;
        for (_2yu v3 : (Iterable<_2yu>)(this.field12192)) {
            float v4 = Fonts.field12486[12].method2719((Object)v3 instanceof pn ? ((Setting)((pn)(Object)v3).eh()).getName() : ((Setting)((p32)(Object)v3).衣()).getName());
            v1 = Math.max(v1, v4 + field12225);
        }
        return v1;
    }

    public AnimatedFloat method3461() {
        return this.field12209;
    }

    public float method3462() {
        return this.field12214;
    }

    @EventHandler(2)
    private void method3463(EventRender2D param1) {
        boolean v14;
        String v2 = field12226;
        String v3 = field12227;
        float v4 = Fonts.field12486[10].method2718();
        float v5 = I.getWindow().getScaledWidth();
        float v6 = I.getWindow().getScaledHeight();
        float v7 = ThemeColors.method3214(MAIN.TOOLTIP) / field12228 * this.field12193.getValue();
        int v8 = ColorUtil.method1853(ThemeColors.method3199(MAIN.TOOLTIP), v7);
        List<String> v9 = this.field12196 ? List.of(v2, v3) : List.of(v2);
        double[] v10 = v9.stream().mapToDouble(param0 -> Fonts.field12486[10].method2719((String)param0)).toArray();
        float[] v11 = new float[v10.length];
        for (int v12 = 0; v12 < v10.length; ++v12) {
            v11[v12] = (float)v10[v12];
        }
        float v12 = this.method3478(v9, v4, v6);
        boolean v13 = this.field12197.equals(field12229);
        boolean bl = v14 = v13 && this.field12190 + this.field12202 / 2.0f > v5 / 2.0f;
        boolean v15 = v14 ? false : (double)this.field12190 + Arrays.stream(v10).max().orElse(0.0) + field12230 < (double)v5;
        float v16 = this.field12190 + 2.0f;
        float v17 = this.field12190 + this.field12202 - 2.0f;
        for (int v18 = 0; v18 < v9.size(); ++v18) {
            float v19 = v12 + (float)v18 * (v4 + field12231);
            if (v19 + v4 > v6) {
                v19 = v6 - v4;
            }
            if (v19 < 0.0f) {
                v19 = 0.0f;
            }
            float v20 = MathHelper.clamp((float)(v15 ? v16 : v17 - v11[v18]), (float)0.0f, (float)(v5 - v11[v18]));
            Fonts.field12486[10].method2711(param1.method2997(), v9.get(v18), v20, v19, v8);
        }
    }

    public void method3464(float param1) {
        this.field12215 = param1;
    }

    private boolean method3465(float param1, float param2, EventMouseClick param3) {
        if (MathUtil.cfr_underscore(param1, param2, this.field12216, this.field12218, this.method3460(), this.method3451())) {
            if (param3.method2989() == 0) {
                for (_2yu v5 : (Iterable<_2yu>)(this.field12192)) {
                    if (!((Boolean)v5.д(param1, param2))) continue;
                    v5.cfr_renamed_136(param1, param2, param3.method2989());
                    return true;
                }
            }
            return param3.method2989() == 1 || param3.method2989() == 2;
        }
        if (param3.method2989() <= 2) {
            this.field12205 = false;
            return true;
        }
        return false;
    }

    public List method3466() {
        return this.field12221;
    }

    public void method3467(boolean param1) {
        this.field12210 = param1;
    }

    @EventHandler(2)
    public void method3468(EventMouseClick param1) {
        if (!(HudWindow.I.currentScreen instanceof ChatScreen)) {
            return;
        }
        double v2 = (double)I.getWindow().getFramebufferWidth() / (double)I.getWindow().getWidth();
        double v4 = (double)I.getWindow().getFramebufferHeight() / (double)I.getWindow().getHeight();
        float v6 = (float)(HudWindow.I.mouse.getX() * v2 / field12232);
        float v7 = (float)(HudWindow.I.mouse.getY() * v4 / field12233);
        if (param1.method2989() == 1 && MathUtil.cfr_underscore(v6, v7, this.field12190, this.field12204, this.field12202, this.field12213) && this.method3497(v6, v7)) {
            this.field12208.method1686(this);
            this.field12208.method1661(this);
            if (!this.field12205) {
                this.field12215 = v6 - this.field12190;
                this.field12217 = v7 - this.field12204;
                this.field12216 = this.field12190 + this.field12215;
                this.field12218 = this.field12204 + this.field12217;
                this.field12209.setValue(0.0f);
            }
            this.field12205 = !this.field12205;
            return;
        }
        HudWindow v8 = null;
        for (HudWindow v10 : (Iterable<HudWindow>)(this.field12208.method1662())) {
            if (v10 == null || !v10.field12205) continue;
            v8 = v10;
            break;
        }
        if (v8 != null && v8 != this) {
            boolean v9 = MathUtil.cfr_underscore(v6, v7, v8.field12190, v8.field12204, v8.field12202, v8.field12213);
            boolean v10 = MathUtil.cfr_underscore(v6, v7, v8.field12216, v8.field12218, v8.method3460(), v8.method3451());
            if (!v9 && !v10) {
                return;
            }
            return;
        }
        if (this.field12205 && this.method3465(v6, v7, param1)) {
            return;
        }
        if (param1.method2989() == 1 && MathUtil.cfr_underscore(v6, v7, this.field12190, this.field12204, this.field12202, this.field12213)) {
            this.field12208.method1686(this);
            this.field12208.method1661(this);
            if (!this.field12205) {
                this.field12215 = v6 - this.field12190;
                this.field12217 = v7 - this.field12204;
                this.field12216 = this.field12190 + this.field12215;
                this.field12218 = this.field12204 + this.field12217;
                this.field12209.setValue(0.0f);
            }
            this.field12205 = !this.field12205;
            return;
        }
        if (this.field12196 && param1.method2989() == 0 && field12207 == null && this.method3497(v6, v7)) {
            this.field12205 = false;
            this.field12206 = true;
            field12207 = this;
            this.field12211 = v6 - this.field12190;
            this.field12212 = v7 - this.field12204;
            this.field12189 = this.field12190;
            this.field12214 = this.field12204;
            this.field12208.method1661(this);
        }
        if (this.field12196 && param1.method2989() == 2 && MathUtil.cfr_underscore(v6, v7, this.field12190, this.field12204, this.field12202, this.field12213)) {
            if (this.field12205 && MathUtil.cfr_underscore(v6, v7, this.field12216, this.field12218, this.method3460(), this.method3451())) {
                return;
            }
            if (!MathHelper.approximatelyEquals((float)this.field12190, (float)this.field12222) || !MathHelper.approximatelyEquals((float)this.field12204, (float)this.field12223)) {
                this.field12190 = this.field12222;
                this.field12204 = this.field12223;
                this.field12193.setValue(0.0f);
                this.field12208.save();
            }
        }
    }

    public Dragging method3469() {
        return this.field12208;
    }

    public float method3470() {
        return this.field12215;
    }

    public void method3471(float param1) {
        this.field12204 = param1;
    }

    public float method3472() {
        return this.field12222;
    }

    public void method3473(float param1) {
        this.field12218 = param1;
    }

    public float method3475() {
        return this.field12218;
    }

    @EventHandler(2)
    public void method3476(EventMouseRelease param1) {
        if (this.field12196 && HudWindow.I.currentScreen instanceof ChatScreen && param1.method3000() == 0 && this.field12206) {
            this.field12206 = false;
            field12207 = null;
            this.field12208.save();
        }
    }

    public void method3477(boolean param1) {
        this.field12205 = param1;
    }

    private float method3478(List param1, float param2, float param3) {
        float v6;
        boolean v5;
        float v4 = (float)param1.size() * param2 + (float)Math.max(0, param1.size() - 1) * field12234;
        if (this.field12204 >= v4 + field12235) {
            v5 = true;
        } else if (param3 - (this.field12204 + this.field12213) >= v4 + field12236) {
            v5 = false;
        } else {
            boolean bl = v5 = this.field12204 - 1.0f > param3 - (this.field12204 + this.field12213) - field12237;
        }
        if (v5) {
            v6 = this.field12204 - v4;
            if (v6 < 0.0f) {
                v6 = 0.0f;
            }
        } else {
            v6 = this.field12204 + this.field12213 + field12238;
            if (v6 + v4 > param3) {
                v6 = param3 - v4;
            }
        }
        return v6;
    }

    public void method3479(Setting ... param1) {
        for (Setting v5 : param1) {
            Setting v6;
            this.field12221.add(v5);
            if (v5 instanceof BooleanSetting) {
                v6 = (BooleanSetting)v5;
                this.field12192.add(new pn(v6));
            }
            if (!(v5 instanceof ModeSetting)) continue;
            v6 = (ModeSetting)v5;
            this.field12192.add(new p32((ModeSetting)v6));
        }
        this.field12208.method1664(this);
    }

    public float method3480() {
        return this.field12217;
    }

    public void method3481(String param1) {
        this.field12197 = param1;
    }

    public void method3482(float param1) {
        this.field12214 = param1;
    }

    public void method3483(float param1) {
        this.field12213 = param1;
    }

    public float method3484() {
        return this.field12223;
    }

    public void method3485(float param1) {
        this.field12212 = param1;
    }

    public float method3486() {
        return this.field12213;
    }

    public void method3487(float param1) {
        this.field12189 = param1;
    }

    private void method3488(EventRender2D param1, float param2, float param3) {
        float v4 = this.method3460();
        float v5 = this.method3451();
        float v6 = this.field12209.getValue();
        if (v6 <= 0.0f) {
            return;
        }
        RenderUtil.method390(this.field12216, this.field12218 - field12239, v4, v5 + field12240, field12241, ThemeColors.method3199(MAIN.MAIN), v6);
        Fonts.field12486[12].method2711(param1.method2997(), field12242, this.field12216 + field12243, this.field12218 - field12244, ColorUtil.method1853(ThemeColors.method3199(MAIN.HEADER_TEXT), v6));
        float v7 = this.field12218 + 1.0f;
        for (_2yu v9 : (Iterable<_2yu>)(this.field12192)) {
            v9.cfr_renamed_168(this.field12216);
            v9.С(v7);
            v9.ч(v4);
            v9.cfr_renamed_136(param1.method2997(), param2, param3, v6);
            v7 += (Float) v9.н();
        }
    }

    public void method3489(float param1) {
        this.field12217 = param1;
    }

    public static void method3490() {
        field12207 = null;
    }

    public String method3491() {
        return this.field12197;
    }

    public void method3492(float param1) {
        this.field12211 = param1;
    }

    public boolean method3494() {
        return this.field12196;
    }

    public boolean method3495() {
        return this.field12206;
    }

    public float method3496() {
        return this.field12204;
    }

    private boolean method3497(float param1, float param2) {
        List v3 = this.field12208.method1662();
        for (int v4 = v3.size() - 1; v4 >= 0; --v4) {
            HudWindow v5 = (HudWindow)v3.get(v4);
            if (v5 == null || v5.field12188 != null && !v5.field12188.method1898(v5.field12197).booleanValue() || !MathUtil.cfr_underscore(param1, param2, v5.field12190, v5.field12204, v5.field12202, v5.field12213)) continue;
            return v5 == this;
        }
        return false;
    }

    public boolean method3498() {
        return this.field12210;
    }

    public float method3499() {
        return this.field12202;
    }

    public float method3500() {
        return this.field12216;
    }

    public boolean method3501() {
        return this.field12205;
    }

    static {
        field12219 = 10.0f;
        field12220 = 10.0f;
        field12232 = 2.0;
        field12233 = 2.0;
        field12194 = 2.0;
        field12195 = 2.0;
        field12198 = "Уведомления";
        field12199 = "K";
        field12200 = "Это уведомление, кликни на меня для настройки";
        field12201 = 28.0f;
        field12203 = 13.0f;
        field12239 = 10.0f;
        field12240 = 10.0f;
        field12241 = 3.0f;
        field12242 = "Настройки элемента";
        field12243 = 5.0f;
        field12244 = 4.0f;
        field12224 = 75.0f;
        field12225 = 24.0f;
        field12191 = 4.0f;
        field12226 = "ПКМ - Дополнительные настройки";
        field12227 = "СКМ - Сбросить расположение";
        field12228 = 255.0f;
        field12229 = "Список модулей";
        field12230 = 2.0;
        field12231 = 2.5f;
        field12234 = 2.5f;
        field12235 = 4.0f;
        field12236 = 2.5f;
        field12237 = 2.5f;
        field12238 = 3.0f;
        HudWindow.method3490();
    }
}



