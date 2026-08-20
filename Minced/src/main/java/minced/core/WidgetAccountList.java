/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  sg.ec.sch
 */
package minced.core;

import java.util.ArrayList;
import java.util.List;
import minced.core.MathUtil;
import minced.core.ScissorUtil;
import minced.core.AccountEntry;
import minced.core.FontRenderer;
import minced.core.HexColor;
import minced.core.Widget;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.manager.Account;
import net.minecraft.client.gui.DrawContext;
import sg.ec.sch;

public class WidgetAccountList
extends Widget {
    static float нА;
    static float нЯ;
    static float нЧ;
    static float нЖ;
    static float н诶;
    static float нЦ;
    static float нч;
    static float нр;
    float field9519;
    boolean field9520 = false;
    List<sch> field9521 = new ArrayList<>();
    static float field9522;
    static float field9523;
    List<sch> field9524 = new ArrayList<>();
    float field9525 = 0.0f;
    float field9526 = 0.0f;
    static float field9527;
    static float field9528;
    static float field9529;
    static float field9530;
    static float field9531;
    static float field9532;
    static float field9533;
    static float field9534;
    static float field9535;
    static String field9536;
    static float field9537;
    static int field9538;
    static String field9539;
    static float field9540;
    static int field9541;
    static String field9542;
    static float field9543;
    static int field9544;
    static String field9545;
    static float field9546;
    static int field9547;
    static float field9548;
    Account field9549;
    static float field9550;
    static float field9551;
    static float field9552;
    static float field9553;
    static float field9554;
    static float field9555;
    static float field9556;
    static float field9557;
    static String field9558;
    static float field9559;
    static String field9560;
    static float field9561;
    static float field9562;
    static float field9563;
    static double field9564;
    static float field9565;
    static float field9566;
    static float field9567;
    Runnable field9568;

    @Override
    public boolean method2736(double param1, double param3, int param5) {
        if (!this.field9347 || !this.cfr_underscore || param5 != 0) {
            return false;
        }
        if (!MathUtil.cfr_underscore((float)param1, (float)param3, this.field9388, this.field9389, this.field9386, this.field9387)) {
            return false;
        }
        float v6 = this.method2812();
        float headerHeight = Fonts.field12494[11].method2718() + 2.0f;
        float v8 = this.field9389 + 2.0f - this.field9525;
        if (this.field9520) {
            v8 += headerHeight;
            if (this.method2808(param1, param3, param5, this.field9521, v8, v6)) {
                return true;
            }
            int v9 = (this.field9521.size() + 1) / 2;
            v8 += (float)v9 * field9532 + field9559;
        }
        v8 += headerHeight;
        return this.method2808(param1, param3, param5, this.field9524, v8, v6);
    }

    public void method2795() {
        for (sch v2 : this.field9521) {
            v2.у(false);
        }
        for (sch v2 : this.field9524) {
            v2.у(false);
        }
    }

    private void method2796(sch param1) {
        param1.Ю(() -> {
            this.method2795();
            param1.у(true);
            AccountEntry v2 = param1.Ю();
            if (this.field9549 != null && v2 != null) {
                this.field9549.login(v2.getName());
            }
            this.method2813();
        });
        param1.к(() -> {
            AccountEntry entry = param1.Ю();
            if (entry != null && this.field9549 != null) {
                this.field9549.toggleFavorite(entry.getName());
            }
            if (entry != null && entry.isFavorite()) {
                this.field9524.remove(param1);
                this.field9521.addFirst(param1);
            } else {
                this.field9521.remove(param1);
                this.field9524.addFirst(param1);
            }
            this.field9520 = !this.field9521.isEmpty();
            this.method2810();
            this.method2813();
        });
        param1.衣(() -> {
            AccountEntry entry = param1.Ю();
            if (entry != null && this.field9549 != null) {
                this.field9549.removeAccount(entry.getName());
            }
            this.field9521.remove(param1);
            this.field9524.remove(param1);
            this.field9520 = !this.field9521.isEmpty();
            this.method2810();
            this.method2813();
        });
    }

    private float method2797() {
        return Math.max(0.0f, this.method2799() - this.field9387);
    }

    public WidgetAccountList(float param1, float param2, float param3, float param4) {
        super(param1, param2, param3, param4);
        this.field9519 = param2;
    }

    private float method2798(DrawContext param1, int param2, int param3, float param4, List param5, float param6, float param7, float param8) {
        int v9;
        for (v9 = 0; v9 < param5.size(); ++v9) {
            sch v10 = (sch)param5.get(v9);
            float v11 = this.field9388 + (float)(v9 % 2) * (param7 + field9527);
            float v12 = param6 + (float)(v9 / 2) * field9528;
            v10.о(v11);
            v10.cfr_renamed_240(v12);
            v10.弟(param7);
            v10.f13(param8);
            float v13 = v12 + param8;
            if (!(v13 + field9529 >= this.field9389 - field9530) || !(v13 <= this.field9389 + this.field9387 + field9531)) continue;
            v10.ь(param1, param2, param3, param4);
        }
        v9 = (param5.size() + 1) / 2;
        return param6 + (float)v9 * field9532;
    }

    private float method2799() {
        int v3;
        float v1 = 2.0f;
        float v2 = this.method2814();
        if (this.field9520) {
            v1 = 2.0f + v2;
            v3 = (this.field9521.size() + 1) / 2;
            v1 += (float)v3 * field9533;
            v1 += field9534;
        }
        v1 += v2;
        v3 = (this.field9524.size() + 1) / 2;
        return v1 += (float)v3 * field9535;
    }

    public void method2800(List<AccountEntry> param1, List<AccountEntry> param2, String param3) {
        this.field9520 = !param1.isEmpty();
        this.field9521.clear();
        this.field9524.clear();
        float v4 = this.method2812();
        for (AccountEntry v6 : param1) {
            this.field9521.add(this.method2807(v6, param3, v4));
        }
        for (AccountEntry v6 : param2) {
            this.field9524.add(this.method2807(v6, param3, v4));
        }
        this.method2810();
    }

    public void method2801() {
        this.field9525 = 0.0f;
        this.field9526 = 0.0f;
    }

    public static void method2802() {
        field9538 = HexColor.method2751(field9536, field9537);
        field9541 = HexColor.method2751(field9539, field9540);
        field9544 = HexColor.method2751(field9542, field9543);
        field9547 = HexColor.method2751(field9545, field9546);
    }

    public void method2805(AccountEntry param1, boolean param2) {
        float v3 = this.method2812();
        sch v4 = new sch(this.field9388, this.field9389, v3, field9548);
        v4.Ю(param1);
        v4.Ю(this.field9549);
        v4.у(param2);
        this.method2796(v4);
        if (param1.isFavorite()) {
            this.field9521.add(0, v4);
            this.field9520 = true;
        } else {
            this.field9524.add(0, v4);
        }
        this.method2810();
    }

    private void method2806(int param1, int param2) {
        float v3 = this.method2799();
        if (v3 <= this.field9387) {
            return;
        }
        float v4 = this.method2797();
        float v5 = this.field9388 + this.field9386 - field9550;
        RenderUtil.method397(v5, this.field9389, field9551, this.field9387, field9552, field9541);
        float v6 = this.field9387 / v3;
        float v7 = Math.max(field9553, this.field9387 * v6);
        float v8 = v4 > 0.0f ? this.field9525 / v4 : 0.0f;
        float v9 = this.field9389 + v8 * (this.field9387 - v7);
        boolean v10 = MathUtil.cfr_underscore(param1, param2, v5, v9, field9554, v7);
        RenderUtil.method397(v5, v9, field9555, v7, field9556, v10 ? field9547 : field9544);
    }

    @Override
    public void method2738(DrawContext param1, int param2, int param3, float param4) {
        if (!this.field9347) {
            return;
        }
        this.field9525 += (this.field9526 - this.field9525) * field9557;
        float v5 = this.field9389 - this.field9519 - this.field9525;
        float v6 = this.method2812();
        FontRenderer v7 = Fonts.field12494[11];
        float v8 = v7.method2718();
        ScissorUtil.method2621(this.field9388, this.field9389 - 2.0f, this.field9386, this.field9387 + 2.0f);
        float v9 = this.field9519 + 2.0f;
        if (this.field9520) {
            v7.method2711(param1, field9558, this.field9388, v9 + v5, field9538);
            v9 += v8 + 2.0f;
            v9 = this.method2798(param1, param2, param3, param4, this.field9521, v9, v6, v5);
            v9 += field9559;
        }
        v7.method2711(param1, field9560, this.field9388, v9 + v5, field9538);
        this.method2798(param1, param2, param3, param4, this.field9524, v9 += v8 + 2.0f, v6, v5);
        ScissorUtil.method2620();
        this.method2806(param2, param3);
    }

    private sch method2807(AccountEntry param1, String param2, float param3) {
        sch v4 = new sch(this.field9388, this.field9389, param3, field9561);
        v4.Ю(param1);
        v4.Ю(this.field9549);
        v4.у(param1.getName().equals(param2));
        this.method2796(v4);
        return v4;
    }

    private boolean method2808(double param1, double param3, int param5, List param6, float param7, float param8) {
        for (int v9 = 0; v9 < param6.size(); ++v9) {
            sch v10 = (sch)param6.get(v9);
            float v11 = this.field9388 + (float)(v9 % 2) * (param8 + field9562);
            float v12 = param7 + (float)(v9 / 2) * field9563;
            v10.о(v11);
            v10.cfr_renamed_240(v12);
            v10.弟(param8);
            if (!((Boolean)v10.cfr_renamed_5(param1, param3, param5))) continue;
            return true;
        }
        return false;
    }

    public void method2809(Account param1) {
        this.field9549 = param1;
    }

    private void method2810() {
        float v1 = this.method2797();
        if (this.field9526 > v1) {
            this.field9526 = v1;
        }
        if (this.field9525 > v1) {
            this.field9525 = v1;
        }
    }

    public boolean method2811(double param1, double param3, double param5) {
        if (!this.field9347) {
            return false;
        }
        if (!MathUtil.cfr_underscore((float)param1, (float)param3, this.field9388, this.field9389, this.field9386, this.field9387)) {
            return false;
        }
        this.field9526 = Math.max(0.0f, Math.min(this.method2797(), this.field9526 + (float)(-param5 * field9564)));
        return true;
    }

    private float method2812() {
        return (this.field9386 - field9565 - field9566 - field9567) / 2.0f;
    }

    private void method2813() {
        if (this.field9568 != null) {
            this.field9568.run();
        }
    }

    private float method2814() {
        return Fonts.field12494[11].method2718() + 5.0f;
    }

    public void method2816(Runnable param1) {
        this.field9568 = param1;
    }

    static {
        нА = 0.0f;
        нЯ = 0.0f;
        нЧ = 0.0f;
        нЖ = 0.0f;
        н诶 = 0.0f;
        нЦ = 0.0f;
        field9538 = Integer.MAX_VALUE;
        field9541 = 0xAFFFFFF;
        field9544 = 0x26FFFFFF;
        field9547 = 0x3FFFFFFF;
        нч = 0.0f;
        нр = 0.0f;
        field9561 = 34.0f;
        field9548 = 34.0f;
        field9565 = 6.0f;
        field9566 = 3.0f;
        field9567 = 4.0f;
        field9557 = 0.15f;
        field9558 = "FAVORITES";
        field9559 = 12.0f;
        field9560 = "ACCOUNTS";
        field9527 = 6.0f;
        field9528 = 39.0f;
        field9529 = 34.0f;
        field9530 = 5.0f;
        field9531 = 5.0f;
        field9532 = 39.0f;
        field9550 = 3.0f;
        field9551 = 3.0f;
        field9552 = 1.5f;
        field9553 = 15.0f;
        field9554 = 3.0f;
        field9555 = 3.0f;
        field9556 = 1.5f;
        field9522 = 39.0f;
        field9523 = 12.0f;
        field9562 = 6.0f;
        field9563 = 39.0f;
        field9564 = 25.0;
        field9533 = 39.0f;
        field9534 = 12.0f;
        field9535 = 39.0f;
        field9536 = "FFFFFF";
        field9537 = 0.5f;
        field9539 = "FFFFFF";
        field9540 = 0.04f;
        field9542 = "FFFFFF";
        field9543 = 0.15f;
        field9545 = "FFFFFF";
        field9546 = 0.25f;
        WidgetAccountList.method2802();
    }
}


