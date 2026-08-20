/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.math.MathHelper
 *  sLM.4ZXYEo
 */
package minced.core;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import minced.core.PathUtil;
import minced.core.ColorUtil;
import minced.core.MathUtil;
import minced.core.ConfigEntry;
import minced.core.ScissorUtil;
import minced.core.Easings;
import minced.core.AnimatedFloat;
import minced.core.ConfigMeta;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.ConfigState;
import minced.core.Minced;
import minced.core.RecordClass228;
import minced.enums.NEWEST_FIRST;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import sLM._4ZXYEo;

public class GuiConfigsPanel
{
    public static class RecordClass228 {
        public final String label;
        public final String icon;
        public final int iconColor;

        public RecordClass228(String label, String icon, int iconColor) {
            this.label = label;
            this.icon = icon;
            this.iconColor = iconColor;
        }

        public String label() { return label; }
        public String icon() { return icon; }
        public int iconColor() { return iconColor; }
    }
    static float cfr_renamed_5;
    static float ц;
    static float И;
    static float 西;
    static float cfr_renamed_6;
    static float cfr_renamed_7;
    static float g2;
    static float г;
    static String cfr_renamed_8;
    static String цд;

    boolean field177;
    boolean field178;
    StringBuilder field179;
    boolean field180;
    AnimatedFloat field181;
    AnimatedFloat field182;
    float cfr_renamed_4 = 0.0f;
    float field183 = 0.0f;
    NEWEST_FIRST field184 = NEWEST_FIRST.NEWEST_FIRST;
    static float field185;
    AnimatedFloat field186 = new AnimatedFloat(0.0f, field185, Easings.field11969);
    static float field187;
    AnimatedFloat field188 = new AnimatedFloat(0.0f, field187, Easings.field11957);
    static float field189;
    AnimatedFloat field190;
    static float field191;
    AnimatedFloat field192;
    static float field193;
    AnimatedFloat field194;
    static float field195;
    static float field196;
    AnimatedFloat[] field197;
    Map<String, ConfigEntry> field198;
    static float field199;
    static String field200;
    static String field201;
    static String field202;
    static String field203;
    static float field204;
    static float field205;
    static float field206;
    static float field207;
    static float field208;
    static float field209;
    static float field210;
    static float field211;
    static float field212;
    static float field213;
    static float field214;
    static float field215;
    static float field216;
    static float field217;
    static float field218;
    static float field219;
    static float field220;
    static float field221;
    static String field222;
    static String field223;
    static String field224;
    static String field225;
    static float field226;
    static float field227;
    static float field228;
    static float field229;
    static float field230;
    static float field231;
    static float field232;
    static float field233;
    static float field234;
    static float field235;
    static float field236;
    static float field237;
    static float field238;
    static float field239;
    static float field240;
    static float field241;
    static float field242;
    static float field243;
    static float field244;
    static float field245;
    static float field246;
    static float field247;
    static float field248;
    static String field249;
    static float field250;
    static float field251;
    static float field252;
    static float field253;
    static float field254;
    static float field255;
    static float field256;
    static float field257;
    static float field258;
    static float field259;
    static float field260;
    static float field261;
    static float field262;
    static float field263;
    static float field264;
    static float field265;
    static float field266;
    static String field267;
    static String field268;
    static String field269;
    static float field270;
    static float field271;
    static int field272;
    static float field273;
    static float field274;
    static float field275;
    static float field276;
    static float field277;
    static float field278;
    static String field279;
    static float field280;
    static float field281;
    static String field282;
    static float field283;
    static float field284;
    static String field285;
    static float field286;
    static float field287;
    static int field288;
    static float field289;
    static float field290;
    static float field291;
    static float field292;
    static float field293;
    static float field294;
    static float field295;
    static float field296;
    static float field297;
    static float field298;
    static float field299;
    static float field300;
    static float field301;
    static float field302;
    static float field303;
    static float field304;
    static float field305;
    static float field306;
    static float field307;
    static float field308;
    static float field309;
    static float field310;
    static float field311;
    static float field312;
    static float field313;
    static float field314;
    static float field315;
    static float field316;
    static float field317;
    static float field318;
    static float field319;
    static float field320;
    static float field321;
    static float field322;
    static float field323;
    static float field324;
    static float field325;
    static float field326;
    static float field327;
    static float field328;
    static float field329;
    static float field330;
    static float field331;
    static float field332;
    static float field333;
    static float field334;
    float field335;
    static float field336;
    static String field337;
    static float field338;
    static float field339;
    static int field340;
    static float field341;
    static float field342;
    static float field343;
    static float field344;
    static float field345;
    static float field346;
    static String field347;
    static float field348;
    static float field349;
    static float field350;
    static float field351;
    static float field352;
    static int field353;
    static float field354;
    static float field355;
    static float field356;
    static int field357;
    static float field358;
    static float field359;
    static float field360;
    static float field361;
    static float field362;
    static int field363;
    static float field364;
    static float field365;
    static float field366;
    static String field367;
    static float field368;
    static float field369;
    static String field370;
    static String field371;
    static String field372;
    static String field373;
    static String field374;
    static int field375;
    static float field376;
    static float field377;
    static float field378;
    static float field379;
    static String field380;
    static float field381;
    static float field382;
    static int field383;
    static float field384;
    static float field385;
    static double field386;
    static double field387;
    static float field388;
    static float field389;
    static float field390;
    static float field391;
    static float field392;
    static float field393;
    static float field394;
    static float field395;
    static float field396;
    static float field397;
    static String field398;
    static int field399;
    static float field400;
    static float field401;
    static float field402;
    static float field403;
    static float field404;
    static float field405;
    static float field406;
    static float field407;
    static float field408;
    static float field409;
    static int field410;
    static float field411;
    static float field412;
    static float field413;
    static float field414;
    static float field415;
    static float field416;
    static float field417;
    static float field418;
    static int field419;
    static float field420;
    static float field421;
    static float field422;
    static float field423;
    static float field424;
    static float field425;
    static float field426;

    private void method108() {
        this.field177 = true;
        this.field178 = true;
        this.field179.setLength(0);
        this.field180 = false;
        this.field181.setValue(0.0f);
        this.field182.setValue(0.0f);
    }

    public boolean method109(int param1, int param2) {
        if (!this.field177) {
            return false;
        }
        if (param1 == 256) {
            this.method124();
            return true;
        }
        if (param1 == 259 && this.field178 && !this.field179.isEmpty()) {
            this.field179.deleteCharAt(this.field179.length() - 1);
            return true;
        }
        if (param1 == 257 && this.field178) {
            String v3 = this.field179.toString().trim();
            if (!v3.isEmpty()) {
                Minced.getInstance().getConfigManager().method9(v3);
                this.method124();
            }
            return true;
        }
        if (param1 == 86 && (param2 & 2) != 0 && this.field178) {
            String v3 = MinecraftClient.getInstance().keyboard.getClipboard();
            if (v3 != null) {
                for (char v7 : v3.toCharArray()) {
                    if (this.field179.length() >= 24) break;
                    if (!this.method126(v7)) continue;
                    this.field179.append(v7);
                }
            }
            return true;
        }
        return this.field177;
    }

    public boolean method110(char param1) {
        if (!this.field177 || !this.field178) {
            return false;
        }
        if (this.field179.length() < 24 && this.method126(param1)) {
            this.field179.append(param1);
            return true;
        }
        return true;
    }

    public GuiConfigsPanel() {
        this.field179 = new StringBuilder();
        this.field190 = new AnimatedFloat(0.0f, field189, Easings.field11969);
        this.field192 = new AnimatedFloat(0.0f, field191, Easings.field11969);
        this.field194 = new AnimatedFloat(0.0f, field193, Easings.field11969);
        this.field181 = new AnimatedFloat(0.0f, field195, Easings.field11969);
        this.field182 = new AnimatedFloat(0.0f, field196, Easings.field11957);
        this.field197 = new AnimatedFloat[NEWEST_FIRST.values().length];
        this.field198 = new HashMap<>();
        for (int v1 = 0; v1 < this.field197.length; ++v1) {
            this.field197[v1] = new AnimatedFloat(0.0f, field199, Easings.field11969);
        }
    }

    public boolean method113(float param1, float param2, int param3, int param4, int param5) {
        float v14;
        float v13;
        float v12;
        float v11;
        if (param3 != 0) {
            return false;
        }
        if (this.field177) {
            return this.method118(param1, param2, param4, param5);
        }
        float v6 = param4 + 114;
        float v7 = param5 + 40;
        RecordClass228[] v8 = new RecordClass228[]{new RecordClass228(field200, field201, 0), new RecordClass228(field202, field203, 0)};
        float v9 = v6;
        for (int v10 = 0; v10 < v8.length; ++v10) {
            v11 = Fonts.I[12].method2719(v8[v10].label);
            v12 = v11 + field204;
            if (MathUtil.cfr_underscore(param1, param2, v9, v7, v12, field205)) {
                if (v10 == 0) {
                    this.method108();
                } else {
                    this.method129();
                }
                return true;
            }
            v9 += v12 + field206;
        }
        String v10 = this.field184.field805;
        v11 = Fonts.I[12].method2719(v10) + field207;
        v12 = v6 + field208 - field209 - v11 - field210;
        if (MathUtil.cfr_underscore(param1, param2, v12, v7, v11, field211)) {
            this.field180 = !this.field180;
            return true;
        }
        if (this.field180) {
            v13 = v12;
            v14 = v7 + field212 + 2.0f;
            float v15 = field213;
            NEWEST_FIRST[] v16 = NEWEST_FIRST.values();
            for (int v17 = 0; v17 < v16.length; ++v17) {
                if (!MathUtil.cfr_underscore(param1, param2, v13, v14 + 2.0f + (float)v17 * v15, v11, v15)) continue;
                this.field184 = v16[v17];
                this.field180 = false;
                return true;
            }
            this.field180 = false;
        }
        if (!MathUtil.cfr_underscore(param1, param2, v6, (v13 = v7 + field214 + field215) - field217, field218, (v14 = field216) + field219)) {
            return false;
        }
        List v15 = this.method125();
        for (int v16 = 0; v16 < v15.size(); ++v16) {
            int v19;
            float v21;
            int v18;
            float v20;
            ConfigMeta v17 = (ConfigMeta)v15.get(v16);
            if (!this.method121(v17, v20 = v6 + ((v18 = v16 % 2) == 1 ? field220 : 0.0f), v21 = v13 + this.field183 + (float)(v19 = v16 / 2) * field221, param1, param2)) continue;
            return true;
        }
        return false;
    }

    private void method114(DrawContext param1, float param2, float param3, float param4, float param5, float param6) {
        float v17;
        float v16;
        boolean v15;
        float v14;
        float v13;
        int v7 = ColorUtil.method1853(Minced.getInstance().getThemeManager().method279(), param4);
        RecordClass228[] v8 = new RecordClass228[]{new RecordClass228(field222, field223, v7), new RecordClass228(field224, field225, ColorUtil.method1853(-1, field226 * param4))};
        AnimatedFloat[] v9 = new AnimatedFloat[]{this.field190, this.field192};
        float v10 = param2;
        for (int v11 = 0; v11 < v8.length; ++v11) {
            RecordClass228 v12 = v8[v11];
            v13 = Fonts.I[12].method2719(v12.label);
            v14 = v13 + field227;
            v15 = MathUtil.cfr_underscore(param5, param6, v10, param3, v14, field228);
            v9[v11].setTarget(v15 ? 1.0f : 0.0f);
            v16 = v9[v11].getValue();
            v17 = (field229 + field230 * v16) * param4;
            RenderUtil.method397(v10, param3, v14, field231, field232, ColorUtil.method1853(-1, v17));
            Fonts.field12502[14].method2711(param1, v12.icon, v10 + field233, param3 + field234, v12.iconColor);
            Fonts.I[12].method2711(param1, v12.label, v10 + field235, param3 + field236, ColorUtil.method1853(-1, param4));
            v10 += v14 + field237;
        }
        String v11 = this.field184.field805;
        float v12 = Fonts.I[12].method2719(v11);
        v13 = v12 + field238;
        v14 = param2 + field239 - field240 - v13 - field241;
        v15 = MathUtil.cfr_underscore(param5, param6, v14, param3, v13, field242);
        this.field194.setTarget(v15 ? 1.0f : 0.0f);
        v16 = this.field194.getValue();
        v17 = (field243 + field244 * v16) * param4;
        RenderUtil.method397(v14, param3, v13, field245, field246, ColorUtil.method1853(-1, v17));
        Fonts.I[12].method2711(param1, v11, v14 + field247, param3 + field248, ColorUtil.method1853(-1, param4));
        this.field186.setTarget(this.field180 ? 1.0f : 0.0f);
        Fonts.field12502[10].method2711(param1, field249, v14 + v13 - field250, param3 + field251, ColorUtil.method1853(-1, field252 * param4));
    }

    private static int method115(int param0, int param1, float param2) {
        param2 = Math.max(0.0f, Math.min(1.0f, param2));
        int v3 = param0 >> 24 & 0xFF;
        int v4 = param0 >> 16 & 0xFF;
        int v5 = param0 >> 8 & 0xFF;
        int v6 = param0 & 0xFF;
        int v7 = param1 >> 24 & 0xFF;
        int v8 = param1 >> 16 & 0xFF;
        int v9 = param1 >> 8 & 0xFF;
        int v10 = param1 & 0xFF;
        int v11 = (int)((float)v3 + (float)(v7 - v3) * param2);
        int v12 = (int)((float)v4 + (float)(v8 - v4) * param2);
        int v13 = (int)((float)v5 + (float)(v9 - v5) * param2);
        int v14 = (int)((float)v6 + (float)(v10 - v6) * param2);
        return v11 << 24 | v12 << 16 | v13 << 8 | v14;
    }

    public boolean method116() {
        return this.field177;
    }

    private void method117(DrawContext param1, ConfigMeta param2, ConfigEntry param3, float param4, float param5, float param6, float param7, float param8) {
        boolean v9 = param2.method3447().equals(((ConfigState)Minced.getInstance().getConfigManager().getData()).method69());
        int v10 = Minced.getInstance().getThemeManager().method279();
        boolean v11 = MathUtil.cfr_underscore(param7, param8, param4, param5, field253, field254);
        param3.field7982.setTarget(v11 ? 1.0f : 0.0f);
        float v12 = param3.field7982.getValue();
        float v13 = param3.field7980.getValue();
        float v14 = param6 * v13;
        float v15 = v9 ? field255 : field256;
        float v16 = (v15 + field257 * v12) * v14;
        int v17 = ColorUtil.method1853(v9 ? v10 : -1, v16);
        RenderUtil.method390(param4, param5, field258, field259, field260, v17, v16);
        if (v9) {
            RenderUtil.method397(param4, param5 + field261, field262, field263, field264, ColorUtil.method1853(v10, v14));
        }
        Fonts.I[14].method2711(param1, param2.method3447(), param4 + field265, param5 + field266, ColorUtil.method1853(-1, v14));
        String v18 = param2.method3446();
        if (v18 == null || v18.isBlank() || v18.startsWith(field267)) {
            v18 = field268;
        }
        SimpleDateFormat v19 = new SimpleDateFormat(field269, Locale.getDefault());
        String v20 = v19.format(new Date(param2.method3448()));
        String v21 = _4ZXYEo.иИ("makeConcatWithConstants", new Object[]{}, (String)v18, (String)v20);
        Fonts.I[12].method2706(param1, v21, param4 + field270, param5 + field271, ColorUtil.method1853(field272, v14), field273);
        RenderUtil.method394(param1, param4 + field274, param5 + field275, field276, field277, ColorUtil.method1853(-1, field278 * v14));
        this.method123(param1, field279, param4 + field280, param5 + field281, v10, true, v14, param3.field7984, param7, param8);
        this.method123(param1, field282, param4 + field283, param5 + field284, -1, false, v14, param3.field7986, param7, param8);
        this.method123(param1, field285, param4 + field286, param5 + field287, field288, false, v14, param3.field7988, param7, param8);
    }

    private boolean method118(float param1, float param2, int param3, int param4) {
        float v5 = field289;
        float v7 = (float)param3 + (field291 - v5) / 2.0f;
        float v9 = v7 + field293;
        float v6 = field290;
        float v8 = (float)param4 + (field292 - v6) / 2.0f;
        float v10 = v8 + field294;
        float v11 = v5 - field295;
        float v12 = field296;
        if (MathUtil.cfr_underscore(param1, param2, v9, v10, v11, v12)) {
            this.field178 = true;
            return true;
        }
        float v13 = v7 + field297;
        float v14 = v8 + v6 - field298;
        float v15 = v5 - field299;
        float v16 = field300;
        if (MathUtil.cfr_underscore(param1, param2, v13, v14, v15, v16)) {
            String v17 = this.field179.toString().trim();
            if (!v17.isEmpty()) {
                Minced.getInstance().getConfigManager().method9(v17);
                this.method124();
            }
            return true;
        }
        if (!MathUtil.cfr_underscore(param1, param2, v7, v8, v5, v6)) {
            this.method124();
            return true;
        }
        this.field178 = false;
        return true;
    }

    public void method119() {
        this.method124();
        this.field180 = false;
    }

    public boolean method120(float param1, float param2, double param3, int param5, int param6) {
        if (this.field177) {
            return true;
        }
        float v7 = param5 + 114;
        float v8 = param6 + 40;
        float v9 = v8 + field301 + field302;
        float v10 = field303;
        if (!MathUtil.cfr_underscore(param1, param2, v7, v9 - field304, field305, v10 + field306)) {
            return false;
        }
        this.cfr_renamed_4 = Math.min(this.cfr_renamed_4 + (float)(param3 > 0.0 ? 18 : -18), 0.0f);
        return true;
    }

    private boolean method121(ConfigMeta param1, float param2, float param3, float param4, float param5) {
        if (MathUtil.cfr_underscore(param4, param5, param2 + field307, param3 + field308, field309, field310)) {
            Minced.getInstance().getConfigManager().method9(param1.method3447());
            return true;
        }
        if (MathUtil.cfr_underscore(param4, param5, param2 + field311, param3 + field312, field313, field314)) {
            Minced.getInstance().getConfigManager().method31(param1.method3447());
            return true;
        }
        if (MathUtil.cfr_underscore(param4, param5, param2 + field315, param3 + field316, field317, field318)) {
            Minced.getInstance().getConfigManager().method3(param1.method3447());
            return true;
        }
        return false;
    }

    private void method123(DrawContext param1, String param2, float param3, float param4, int param5, boolean param6, float param7, AnimatedFloat param8, float param9, float param10) {
        float v11 = field319;
        float v12 = field320;
        boolean v13 = MathUtil.cfr_underscore(param9, param10, param3, param4, v11, v12);
        param8.setTarget(v13 ? 1.0f : 0.0f);
        float v14 = param8.getValue();
        float v15 = param6 ? field321 : field322;
        float v16 = (v15 + field323 * v14) * param7;
        int v17 = param6 ? ColorUtil.method1853(param5, v16) : ColorUtil.method1853(-1, v16);
        RenderUtil.method397(param3, param4, v11, v12, field324, v17);
        int v18 = (int)Fonts.I[12].method2719(param2);
        int v19 = v18 + 8;
        int v20 = param6 ? ColorUtil.method1853(-1, param7) : ColorUtil.method1853(param5, param7);
        Fonts.I[12].method2711(param1, param2, param3 + (v11 - (float)v19) / 2.0f + field325, param4 + field326, v20);
    }

    private void method124() {
        this.field177 = false;
        this.field178 = false;
    }

    private List<ConfigMeta> method125() {
        Comparator<ConfigMeta> comparator;
        List<String> v1 = (List<String>)Minced.getInstance().getConfigManager().method1();
        ArrayList<ConfigMeta> v2 = new ArrayList<ConfigMeta>();
        for (String v4 : v1) {
            ConfigMeta v5 = Minced.getInstance().getConfigManager().method25(v4);
            if (v5 == null) continue;
            v2.add(v5);
        }
        switch (this.field184) {
            default: {
                throw new MatchException(null, null);
            }
            case NEWEST_FIRST: {
                comparator = Comparator.comparingLong(ConfigMeta::method3448).reversed();
                break;
            }
            case OLDEST_FIRST: {
                comparator = Comparator.comparingLong(ConfigMeta::method3448);
                break;
            }
            case BY_NAME: {
                comparator = Comparator.comparing(param0 -> param0.method3447().toLowerCase(Locale.ROOT));
            }
        }
        Comparator<ConfigMeta> v3 = comparator;
        v2.sort(v3);
        return v2;
    }

    private boolean method126(char param1) {
        if (param1 >= 'a' && param1 <= 'z' || param1 >= 'A' && param1 <= 'Z' || param1 >= '0' && param1 <= '9' || param1 == '_' || param1 == '-' || param1 == '.' || param1 == ' ') {
            return true;
        }
        return param1 >= 'А' && param1 <= 'я' || param1 == 'Ё' || param1 == 'ё';
    }

    public void method127(DrawContext param1, int param2, int param3, float param4, float param5, float param6) {
        float v7 = param2 + 114;
        float v8 = param3 + 40;
        float v9 = field327;
        float v10 = field328;
        this.method114(param1, v7, v8, param4, param5, param6);
        float v11 = v8 + field329 + field330;
        float v12 = v10 - field331 - field332;
        ScissorUtil.method2621(v7, v11 - field333, v9, v12 + field334);
        if (this.cfr_renamed_4 != this.field183) {
            float v13 = Math.min(-(this.field335 - v12), 0.0f);
            this.cfr_renamed_4 = MathHelper.clamp((float)this.cfr_renamed_4, (float)v13, (float)0.0f);
            this.field183 = MathUtil.method1870(this.field183, this.cfr_renamed_4, field336);
        }
        List<ConfigMeta> v13 = this.method125();
        this.method130(v13);
        if (v13.isEmpty()) {
            String v14 = field337;
            float v15 = Fonts.I[13].method2719(v14);
            Fonts.I[13].method2711(param1, v14, v7 + (v9 - v15) / 2.0f - field338, v11 + v12 / 2.0f - field339, ColorUtil.method1853(field340, param4));
        }
        int v14 = 0;
        for (int v15 = 0; v15 < v13.size(); ++v15) {
            ConfigMeta v16 = (ConfigMeta)v13.get(v15);
            int v17 = v15 % 2;
            int v18 = v15 / 2;
            float v19 = v7 + (v17 == 1 ? field341 : 0.0f);
            float v20 = v11 + this.field183 + (float)v18 * field342;
            ConfigEntry v21 = this.method128(v16.method3447());
            v21.field7980.setTarget(1.0f);
            if (v20 + field343 > v11 - field344 && v20 < v11 + v12 + field345) {
                this.method117(param1, v16, v21, v19, v20, param4, param5, param6);
            } else {
                v21.field7982.setTarget(0.0f);
                v21.field7984.setTarget(0.0f);
                v21.field7986.setTarget(0.0f);
                v21.field7988.setTarget(0.0f);
            }
            v14 = v18 + 1;
        }
        this.field335 = (float)v14 * field346;
        ScissorUtil.method2620();
        this.method134(param1, v7, v8, param4, param5, param6);
    }

    private ConfigEntry method128(String param1) {
        return this.field198.computeIfAbsent(param1, param0 -> new ConfigEntry());
    }

    private void method129() {
        try {
            File v1 = new File((String)((Object)_4ZXYEo.ис("makeConcatWithConstants", new Object[]{}, (String)PathUtil.field7558)));
            if (!v1.exists()) {
                v1.mkdirs();
            }
            Runtime.getRuntime().exec(new String[]{field347, v1.getAbsolutePath()});
        } catch (Exception ignored) {
        }
    }

    private void method130(List<ConfigMeta> param12) {
        if (this.field198.isEmpty()) {
            return;
        }
        HashSet<String> v2 = new HashSet<String>();
        for (ConfigMeta v4 : param12) {
            v2.add(v4.method3447());
        }
        this.field198.keySet().removeIf(param1 -> !v2.contains(param1));
    }

    public boolean method131() {
        return this.field177;
    }

    public void method132(DrawContext param1, int param2, int param3, float param4, float param5, float param6) {
        this.field188.setTarget(this.field177 ? 1.0f : 0.0f);
        float v7 = this.field188.getValue();
        if (v7 < field348) {
            this.field181.setTarget(0.0f);
            this.field182.setTarget(0.0f);
            return;
        }
        float v8 = field349 * v7 * param4;
        RenderUtil.method390(param2, param3, field350, field351, field352, ColorUtil.method1853(field353, v8), v8);
        RenderUtil.method397(param2, param3, field354, field355, field356, ColorUtil.method1853(field357, v8));
        float v9 = field358;
        float v10 = field359;
        int v11 = (int)((float)param2 + (field360 - v9) / 2.0f);
        int v12 = (int)((float)param3 + (field361 - v10) / 2.0f);
        float v13 = v7 * param4;
        RenderUtil.method397(v11, v12, v9, v10, field362, ColorUtil.method1853(field363, v13));
        RenderUtil.method397(v11, v12, v9, v10, field364, ColorUtil.method1853(-1, field365 * v13));
        int v14 = ColorUtil.method1853(Minced.getInstance().getThemeManager().method279(), v13);
        int v15 = ColorUtil.method1855(Minced.getInstance().getThemeManager().method279(), (int)(field366 * v13));
        int v16 = v11 + 8;
        int v17 = v12 + 9;
        int v18 = (int)Fonts.I[10].method2719(field367);
        int v19 = v18 + 18;
        RenderUtil.method397(v16, v17, v19, field368, field369, v15);
        Fonts.field12502[12].method2711(param1, field370, v16 + 4, v17 + 6, v14);
        Fonts.I[10].method2711(param1, field371, v16 + 14, v17 + 6, ColorUtil.method1853(-1, v13));
        Fonts.I[15].method2711(param1, field372, v11 + 9, v12 + 27, ColorUtil.method1853(-1, v13));
        Fonts.I[12].method2711(param1, field373, v11 + 9, v12 + 38, ColorUtil.method1853(-1, v13));
        Fonts.I[11].method2711(param1, field374, v11 + 9, v12 + 47, ColorUtil.method1853(field375, v13));
        float v20 = v11 + 9;
        float v21 = v12 + 55;
        float v22 = v9 - field376;
        float v23 = field377;
        RenderUtil.method397(v20, v21, v22, v23, field378, ColorUtil.method1853(-1, field379 * v13));
        Fonts.field12502[14].method2711(param1, field380, v20 + field381, v21 + field382, ColorUtil.method1853(field383, v13));
        String v24 = this.field179.toString();
        float v25 = v20 + field384;
        float v26 = v21 + field385;
        Fonts.I[12].method2711(param1, v24, v25, v26, ColorUtil.method1853(-1, v13));
        if (this.field178) {
            float v27 = v25 + Fonts.I[12].method2719(v24) + 1.0f;
            float v28 = (float)((Math.sin((double)System.currentTimeMillis() / field386) + 1.0) / field387);
            RenderUtil.method394(param1, (float)Math.floor(v27), v21 + field388, field389, field390, ColorUtil.method1853(-1, v28 * v13));
        }
        int v27 = v11 + 9;
        int v28 = (int)((float)v12 + v10 - field391);
        float v29 = v9 - field392;
        float v30 = field393;
        boolean v31 = this.field179.toString().trim().isEmpty();
        boolean v32 = !v31 && MathUtil.cfr_underscore(param5, param6, v27, v28, v29, v30);
        this.field181.setTarget(v32 ? 1.0f : 0.0f);
        this.field182.setTarget(v31 ? 0.0f : 1.0f);
        float v33 = this.field181.getValue();
        float v34 = this.field182.getValue();
        int v35 = Minced.getInstance().getThemeManager().method279();
        float v36 = field394;
        float v37 = field395;
        float v38 = field396;
        float v39 = MathHelper.lerp((float)v34, (float)v36, (float)(v37 + v38 * v33));
        int v40 = GuiConfigsPanel.method115(-1, v35, v34);
        int v41 = ColorUtil.method1853(v40, v39 * v13);
        RenderUtil.method397(v27, v28, v29, v30, field397, v41);
        String v42 = field398;
        float v43 = Fonts.I[12].method2719(v42);
        int v44 = ColorUtil.method1853(GuiConfigsPanel.method115(field399, -1, v34), v13);
        Fonts.I[12].method2711(param1, v42, (float)v27 + (v29 - v43) / 2.0f, (float)v28 + field400, v44);
    }

    public void method133() {
        this.cfr_renamed_4 = 0.0f;
        this.field183 = 0.0f;
        for (ConfigEntry v2 : this.field198.values()) {
            v2.field7980.setValue(0.0f);
            v2.field7982.setValue(0.0f);
            v2.field7984.setValue(0.0f);
            v2.field7986.setValue(0.0f);
            v2.field7988.setValue(0.0f);
        }
    }

    private void method134(DrawContext param1, float param2, float param3, float param4, float param5, float param6) {
        float v7 = this.field186.getValue();
        if (v7 < field401) {
            for (AnimatedFloat v11 : this.field197) {
                v11.setTarget(0.0f);
            }
            return;
        }
        String v8 = this.field184.field805;
        float v9 = Fonts.I[12].method2719(v8);
        float v10 = v9 + field402;
        float v11 = param2 + field403 - field404 - v10 - field405;
        float v13 = param3 + field406 + 2.0f;
        float v14 = field407;
        NEWEST_FIRST[] v15 = NEWEST_FIRST.values();
        float v16 = v14 * (float)v15.length + field408;
        float v17 = v7 * param4;
        RenderUtil.method397(v11, v13, v10, v16, field409, ColorUtil.method1853(field410, v17 - field411));
        RenderUtil.method397(v11, v13, v10, v16, field412, ColorUtil.method1853(-1, field413 * v17));
        boolean v18 = this.field180;
        for (int v19 = 0; v19 < v15.length; ++v19) {
            NEWEST_FIRST v20 = v15[v19];
            float v21 = v13 + 2.0f + (float)v19 * v14;
            boolean v22 = v20 == this.field184;
            boolean v23 = v18 && MathUtil.cfr_underscore(param5, param6, v11, v21, v10, v14);
            this.field197[v19].setTarget(v23 ? 1.0f : 0.0f);
            float v24 = this.field197[v19].getValue();
            if (v24 > field414 && !v22) {
                RenderUtil.method397(v11 + field415, v21, v10 - field416, v14 - 2.0f, field417, ColorUtil.method1853(-1, field418 * v24 * v17));
            }
            int v25 = v22 ? -1 : field419;
            int v27 = ColorUtil.method1853(GuiConfigsPanel.method115(v25, -1, v24), v17);
            if (v22) {
                RenderUtil.method397(v11 + field420, v21, v10 - field421, v14 - 2.0f, field422, ColorUtil.method1855(Minced.getInstance().getThemeManager().method279(), (int)((field423 + field424 * v24) * v17)));
            }
            Fonts.I[12].method2711(param1, v20.field805, v11 + field425, v21 + field426, v27);
        }
    }

    static {
        cfr_renamed_5 = 0.0f;
        ц = 0.0f;
        И = 0.0f;
        西 = 0.0f;
        cfr_renamed_6 = 0.0f;
        cfr_renamed_7 = 0.0f;
        g2 = 0.0f;
        г = 0.0f;
        field185 = 14.0f;
        field187 = 12.0f;
        field189 = 14.0f;
        field191 = 14.0f;
        field193 = 14.0f;
        field195 = 14.0f;
        field196 = 10.0f;
        field199 = 14.0f;
        field327 = 334.5f;
        field328 = 224.5f;
        field329 = 16.0f;
        field330 = 6.0f;
        field331 = 16.0f;
        field332 = 6.0f;
        field333 = 4.0f;
        field334 = 8.0f;
        field336 = 14.0f;
        field337 = "У вас пока нет сохранённых конфигов";
        field338 = 6.0f;
        field339 = 4.0f;
        field340 = -8750470;
        field341 = 165.0f;
        field342 = 65.0f;
        field343 = 60.0f;
        field344 = 80.0f;
        field345 = 20.0f;
        field346 = 65.0f;
        field348 = 0.01f;
        field349 = 0.6f;
        field350 = 448.5f;
        field351 = 272.5f;
        field352 = 8.0f;
        field353 = -16777216;
        field354 = 448.5f;
        field355 = 272.5f;
        field356 = 8.0f;
        field357 = -16777216;
        field358 = 155.0f;
        field359 = 100.0f;
        field360 = 448.5f;
        field361 = 272.5f;
        field362 = 8.0f;
        field363 = -15790321;
        field364 = 8.0f;
        field365 = 0.04f;
        field366 = 38.0f;
        field367 = "ДЕЙСТВИЕ  С  КОНФИГУРАЦИЕЙ";
        field368 = 13.0f;
        field369 = 3.0f;
        field370 = "f";
        field371 = "ДЕЙСТВИЕ  С  КОНФИГУРАЦИЕЙ";
        field372 = "Создание конфигурации";
        field373 = "Название конфигурации";
        field374 = "Отображаемое имя конфигурации";
        field375 = -8750470;
        field376 = 18.0f;
        field377 = 16.0f;
        field378 = 4.0f;
        field379 = 0.025f;
        field380 = "H";
        field381 = 5.0f;
        field382 = 7.0f;
        field383 = -8750470;
        field384 = 16.0f;
        field385 = 6.5f;
        field386 = 320.0;
        field387 = 2.0;
        field388 = 4.0f;
        field389 = 0.5f;
        field390 = 8.0f;
        field391 = 22.5f;
        field392 = 18.0f;
        field393 = 16.0f;
        field394 = 0.03f;
        field395 = 0.18f;
        field396 = 0.22f;
        field397 = 4.0f;
        field398 = "Создать  конфигурацию";
        field399 = -8750470;
        field400 = 6.5f;
        field222 = "Создать";
        field223 = "I";
        field224 = "Импорт";
        field225 = "t";
        field226 = 0.45f;
        field227 = 22.0f;
        field228 = 16.0f;
        field229 = 0.02f;
        field230 = 0.08f;
        field231 = 16.0f;
        field232 = 4.0f;
        field233 = 4.5f;
        field234 = 7.0f;
        field235 = 16.0f;
        field236 = 7.0f;
        field237 = 5.0f;
        field238 = 32.0f;
        field239 = 448.5f;
        field240 = 114.0f;
        field241 = 8.0f;
        field242 = 16.0f;
        field243 = 0.02f;
        field244 = 0.08f;
        field245 = 16.0f;
        field246 = 4.0f;
        field247 = 8.0f;
        field248 = 7.0f;
        field249 = "z";
        field250 = 11.0f;
        field251 = 8.0f;
        field252 = 0.55f;
        field401 = 0.01f;
        field402 = 32.0f;
        field403 = 448.5f;
        field404 = 114.0f;
        field405 = 8.0f;
        field406 = 16.0f;
        field407 = 16.0f;
        field408 = 4.0f;
        field409 = 4.0f;
        field410 = -15790321;
        field411 = 0.05f;
        field412 = 4.0f;
        field413 = 0.04f;
        field414 = 0.001f;
        field415 = 3.0f;
        field416 = 6.0f;
        field417 = 3.0f;
        field418 = 0.05f;
        field419 = -6645094;
        field420 = 3.0f;
        field421 = 6.0f;
        field422 = 3.0f;
        field423 = 28.0f;
        field424 = 18.0f;
        field425 = 8.0f;
        field426 = 6.0f;
        field253 = 160.0f;
        field254 = 60.0f;
        field255 = 0.05f;
        field256 = 0.025f;
        field257 = 0.03f;
        field258 = 160.0f;
        field259 = 60.0f;
        field260 = 8.0f;
        field261 = 11.0f;
        field262 = 0.5f;
        field263 = 12.0f;
        field264 = 0.25f;
        field265 = 8.0f;
        field266 = 11.0f;
        field267 = "Minced.getInstance";
        field268 = "Unknown";
        field269 = "dd.MM.yyyy";
        cfr_renamed_8 = "Создатель: \u0001 | Создан: \u0001";
        field270 = 8.0f;
        field271 = 21.5f;
        field272 = -8750470;
        field273 = 105.0f;
        field274 = 8.0f;
        field275 = 32.5f;
        field276 = 144.0f;
        field277 = 0.5f;
        field278 = 0.05f;
        field279 = "Сохранить";
        field280 = 8.0f;
        field281 = 38.0f;
        field282 = "Загрузить";
        field283 = 57.5f;
        field284 = 38.0f;
        field285 = "Удалить";
        field286 = 107.0f;
        field287 = 38.0f;
        field288 = -38037;
        field319 = 45.0f;
        field320 = 16.0f;
        field321 = 0.1f;
        field322 = 0.03f;
        field323 = 0.1f;
        field324 = 3.0f;
        field325 = 4.5f;
        field326 = 7.0f;
        field200 = "Создать";
        field201 = "v";
        field202 = "Импорт";
        field203 = "u";
        field204 = 22.0f;
        field205 = 16.0f;
        field206 = 5.0f;
        field207 = 32.0f;
        field208 = 448.5f;
        field209 = 114.0f;
        field210 = 8.0f;
        field211 = 16.0f;
        field212 = 16.0f;
        field213 = 16.0f;
        field214 = 16.0f;
        field215 = 6.0f;
        field216 = 202.5f;
        field217 = 4.0f;
        field218 = 334.5f;
        field219 = 8.0f;
        field220 = 165.0f;
        field221 = 65.0f;
        field307 = 8.0f;
        field308 = 38.0f;
        field309 = 45.0f;
        field310 = 16.0f;
        field311 = 57.5f;
        field312 = 38.0f;
        field313 = 45.0f;
        field314 = 16.0f;
        field315 = 107.0f;
        field316 = 38.0f;
        field317 = 45.0f;
        field318 = 16.0f;
        field289 = 155.0f;
        field290 = 100.0f;
        field291 = 448.5f;
        field292 = 272.5f;
        field293 = 9.0f;
        field294 = 55.0f;
        field295 = 18.0f;
        field296 = 16.0f;
        field297 = 9.0f;
        field298 = 25.0f;
        field299 = 18.0f;
        field300 = 16.0f;
        field301 = 16.0f;
        field302 = 6.0f;
        field303 = 202.5f;
        field304 = 4.0f;
        field305 = 334.5f;
        field306 = 8.0f;
        цд = "\u0001custom\\";
        field347 = "explorer";
    }
}



