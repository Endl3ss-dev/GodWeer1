/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 */
package minced.core;

import minced.core.ColorUtil;
import minced.core.HudTheme;
import minced.core.CancellableEvent;
import minced.core.Render2DBatch;
import minced.core.Notifications;
import minced.core.ClientState;
import minced.core.ThemeColors;
import minced.core.AnimatedFloat;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.enums.MAIN;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class NotificationWidget
implements CancellableEvent {
    static float з;

    AnimatedFloat field8965;
    boolean field8966;
    ClientState field8967 = new ClientState();
    static long field8968;
    Identifier field8969;
    String field8970;
    static String field8971;
    Text field8972;
    static float field8973;
    static float field8974;
    static float field8975;
    static float field8976;
    static float field8977;
    static float field8978;
    static float field8979;
    static float field8980;
    static float field8981;
    static float field8982;
    static String field8983;
    static String field8984;
    static String field8985;
    static String field8986;
    static String field8987;
    static String field8988;
    static String field8989;
    static String field8990;
    boolean field8991;
    AnimatedFloat field8992;
    static long field8993;
    static float field8994;
    static float field8995;
    Integer field8996;
    static float field8997;
    static float field8998;
    static float field8999;
    static float field9000;
    static float field9001;
    static float field9002;
    static float field9003;
    static float field9004;
    static float field9005;
    static float field9006;
    static float field9007;
    static float field9008;
    static float field9009;
    static float field9010;
    static float field9011;
    static float field9012;
    static float field9013;
    static float field9014;
    static String field9015;
    static float field9016;
    static float field9017;
    static float field9018;
    static float field9019;
    static float field9020;
    static float field9021;
    static float field9022;
    static float field9023;
    static float field9024;

    public AnimatedFloat method2473() {
        return this.field8965;
    }

    public boolean method2474() {
        return (this.field8966 || this.field8967.method3113(field8968)) && this.field8965.isFinished();
    }

    public Identifier method2475() {
        return this.field8969;
    }

    private void method2476(float param1, float param2, float param3, DrawContext param4) {
        boolean v5 = this.field8970 != null && this.field8970.length() == 1 && field8971.contains(this.field8970.toLowerCase());
        float v6 = v5 ? Fonts.field12502[16].method2719(this.field8970) : Fonts.field12500[16].method2719(this.field8970);
        float v7 = v6 + Fonts.I[12].method2719(this.field8972.getString()) + field8973;
        float v8 = field8974;
        int v9 = ColorUtil.method1853(ThemeColors.method3199(MAIN.MODULE_VISUAL), param3);
        int v10 = ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_TEXT), param3);
        this.method2480(param1, param2, v7, param3);
        if (v5) {
            Fonts.field12502[16].method2711(param4, this.field8970, param1 + field8975, param2 + field8976, v9);
        } else {
            Fonts.field12500[16].method2711(param4, this.field8970, (int)param1 + 5, param2 + Fonts.field12500[16].method2718() - 2.0f, v9);
        }
        Render2DBatch.method2664(param4, (float)((int)param1) + v6 + field8977, param2 + field8978, field8979, field8980, ColorUtil.method1853(ThemeColors.method3199(MAIN.SEPARATOR), ThemeColors.method3214(MAIN.SEPARATOR) / field8981 * param3));
        float v11 = (float)((int)param1) + v6 + field8982;
        float v12 = param2 + Fonts.I[12].method2718() + 2.0f;
        String v13 = this.field8972.getString();
        int v14 = ColorUtil.method1853(ThemeColors.method3199(MAIN.MODULE_VISUAL), param3);
        if (field8983.equals(this.field8970) || field8984.equals(this.field8970)) {
            int v15 = v13.lastIndexOf(' ');
            if (v15 > 0 && v15 < v13.length() - 1) {
                String v16 = v13.substring(0, v15 + 1);
                String v17 = v13.substring(v15 + 1);
                Fonts.I[12].method2711(param4, v16, v11, v12, v10);
                Fonts.I[12].method2711(param4, v17, v11 + Fonts.I[12].method2719(v16), v12, v14);
            } else {
                Fonts.I[12].method2711(param4, v13, v11, v12, v10);
            }
        } else if (field8985.equals(this.field8970)) {
            int v15 = v13.lastIndexOf(' ');
            if (v15 > 0 && v15 < v13.length() - 1) {
                String v16 = v13.substring(0, v15 + 1);
                String v17 = v13.substring(v15 + 1);
                Fonts.I[12].method2711(param4, v16, v11, v12, v10);
                Fonts.I[12].method2711(param4, v17, v11 + Fonts.I[12].method2719(v16), v12, v14);
            } else {
                Fonts.I[12].method2711(param4, v13, v11, v12, v10);
            }
        } else if (v13.contains(field8986) || v13.contains(field8987)) {
            String v15 = v13.contains(field8988) ? field8989 : field8990;
            String v16 = v13.replace(v15, "");
            Fonts.I[12].method2711(param4, v16, v11, v12, v10);
            Fonts.I[12].method2711(param4, v15, v11 + Fonts.I[12].method2719(v16), v12, v14);
        } else {
            Fonts.I[12].method2711(param4, v13, v11, v12, v10);
        }
        this.method2487(param1, param2, v7, v8, v14);
    }

    public void method2477(float param1, float param2, DrawContext param3) {
        if (!this.field8991) {
            this.field8992.setValue(param2);
            this.field8991 = true;
        }
        Render2DBatch.method2668(param3);
        boolean v4 = this.field8966 || this.field8967.method3113(field8993);
        this.field8965.setTarget(v4 ? 0.0f : 1.0f);
        this.field8992.setTarget(this.field8966 && v4 ? param2 - (float)(this.field8969 != null ? 4 : 2) : param2);
        float v5 = this.field8992.getValue();
        float v6 = this.field8965.getValue();
        if (this.field8969 != null) {
            this.method2481(param1, v5, v6, param3);
        } else {
            this.method2476(param1, v5, v6, param3);
        }
        Render2DBatch.method2651();
    }

    public NotificationWidget(Identifier param1, Text param2, Integer param3) {
        this.field8992 = new AnimatedFloat(0.0f, field8994);
        this.field8965 = new AnimatedFloat(0.0f, field8995);
        this.field8966 = false;
        this.field8991 = false;
        this.field8970 = null;
        this.field8972 = param2;
        this.field8969 = param1;
        this.field8996 = param3;
    }

    public void method2478() {
        this.field8966 = true;
    }

    public ClientState method2479() {
        return this.field8967;
    }

    private void method2480(float param1, float param2, float param3, float param4) {
        if (((Boolean)Notifications.field9921.getValue()).booleanValue()) {
            RenderUtil.method390(param1, param2, param3, field8997, field8998, ColorUtil.method1855(ThemeColors.method3199(MAIN.HEADER), 0), param4);
            return;
        }
        HudTheme.method1871(param1, param2, param3, field8999, param4, field9000, field9001, field9002, field9003);
    }

    private void method2481(float param1, float param2, float param3, DrawContext param4) {
        float v5 = Fonts.I[12].method2719(this.field8972.getString()) + field9004;
        float v6 = field9005;
        int v7 = ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_TEXT), param3);
        int v8 = ColorUtil.method1853(ThemeColors.method3199(MAIN.MODULE_VISUAL), param3);
        this.method2480(param1, param2, v5, param3);
        int v9 = (int)(param1 + field9006);
        int v10 = (int)(param2 + field9007);
        Render2DBatch.method2666(this.field8969, v9, v10, field9008, field9009, ColorUtil.method1853(-1, param3));
        Render2DBatch.method2664(param4, (int)param1 + 16, param2 + field9010, field9011, field9012, ColorUtil.method1853(ThemeColors.method3199(MAIN.SEPARATOR), ThemeColors.method3214(MAIN.SEPARATOR) / field9013 * param3));
        Fonts.I[12].method2711(param4, this.field8972.getString(), (int)param1 + 20, param2 + Fonts.I[12].method2718() + 2.0f, v7);
        this.method2487(param1, param2, v5, v6, v8);
    }

    public boolean method2482() {
        return this.field8966;
    }

    public Integer method2483() {
        return this.field8996;
    }

    public float method2484() {
        return this.field8969 != null ? Fonts.I[12].method2719(this.field8972.getString()) + field9014 : (this.field8970 != null && this.field8970.length() == 1 && field9015.contains(this.field8970.toLowerCase()) ? Fonts.field12502[16].method2719(this.field8970) : Fonts.field12500[16].method2719(this.field8970)) + Fonts.I[12].method2719(this.field8972.getString()) + field9016;
    }

    public Text method2485() {
        return this.field8972;
    }

    public String method2486() {
        return this.field8970;
    }

    private void method2487(float param1, float param2, float param3, float param4, int param5) {
        float v6 = Math.min(field9017, (float)this.field8967.method3112());
        float v7 = this.field8966 ? 0.0f : Math.max(0.0f, 1.0f - v6 / field9018);
        float v8 = param1 + field9019;
        float v10 = param2 + param4 - 2.0f;
        float v11 = param3 - field9020;
        float v12 = v11 * v7;
        if (v12 > field9021) {
            RenderUtil.method397(v8, v10, v12, 2.0f, field9022, param5);
        }
    }

    public AnimatedFloat method2488() {
        return this.field8992;
    }

    public boolean method2489() {
        return this.field8991;
    }

    public NotificationWidget(String param1, String param2, int param3) {
        this.field8992 = new AnimatedFloat(0.0f, field9023);
        this.field8965 = new AnimatedFloat(0.0f, field9024);
        this.field8966 = false;
        this.field8991 = false;
        this.field8970 = param1;
        this.field8972 = Text.literal((String)param2);
        this.field8969 = null;
        this.field8996 = param3;
    }

    static {
        з = 0.0f;
        field9023 = 20.0f;
        field9024 = 10.0f;
        field8994 = 20.0f;
        field8995 = 10.0f;
        field8993 = 1500L;
        field8997 = 16.0f;
        field8998 = 3.0f;
        field8999 = 16.0f;
        field9000 = 3.0f;
        field9001 = 3.0f;
        field9002 = 3.0f;
        field9003 = 3.0f;
        field8971 = "abcdef";
        field8973 = 17.0f;
        field8974 = 16.0f;
        field8975 = 4.5f;
        field8976 = 6.5f;
        field8977 = 8.5f;
        field8978 = 4.5f;
        field8979 = 0.5f;
        field8980 = 7.0f;
        field8981 = 255.0f;
        field8982 = 12.0f;
        field8983 = "J";
        field8984 = "K";
        field8985 = "S";
        field8986 = " включен";
        field8987 = " выключен";
        field8988 = " включен";
        field8989 = " включен";
        field8990 = " выключен";
        field9004 = 24.0f;
        field9005 = 16.0f;
        field9006 = 4.5f;
        field9007 = 3.75f;
        field9008 = 8.0f;
        field9009 = 8.0f;
        field9010 = 4.5f;
        field9011 = 0.5f;
        field9012 = 7.0f;
        field9013 = 255.0f;
        field9017 = 1500.0f;
        field9018 = 1500.0f;
        field9019 = 1.5f;
        field9020 = 3.0f;
        field9021 = 0.1f;
        field9022 = 0.5f;
        field8968 = 1500L;
        field9014 = 24.0f;
        field9015 = "abcdef";
        field9016 = 17.0f;
    }
}



