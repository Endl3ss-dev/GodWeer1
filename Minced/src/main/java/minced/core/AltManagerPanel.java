/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.util.Identifier
 *  sLM.4ZXYEo
 *  sg.ec.П必$Ч
 */
package minced.core;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import minced.core.ColorUtil;
import minced.core.AccountEntry;
import minced.core.FontRenderer;
import minced.core.WidgetButton;
import minced.core.HexColor;
import minced.core.Widget;
import minced.core.WidgetTextField;
import minced.core.WidgetAccountList;
import minced.core.Animation;
import minced.core.ThemeColors;
import minced.core.Easings;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.Minced;
import minced.enums.CRACKED;
import minced.enums.IDLE;
import minced.enums.MAIN;
import minced.gui.MainMenuScreen;
import minced.manager.Account;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.DefaultSkinHelper;
import sLM._4ZXYEo;
import sg.ec.pBi;

public class AltManagerPanel
{
    static float нХ;
    static float нж;
    static float нГ;
    static float нг;
    static float н3;
    static float ню;
    static float нэ;
    static float не;
    static float н衣;
    static float нВ;
    static float нЗ;
    static float нк;
    static float нЫ;
    static String cfr_renamed_9;
    static String cfr_renamed_10;
    static String cfr_renamed_11;
    static String cfr_renamed_12;

    WidgetButton field427;
    WidgetButton field428;
    WidgetButton field429;
    WidgetButton var_minced_core_ClsClass482_I;
    WidgetButton field430;
    WidgetButton field431;
    WidgetButton field432;
    static String field433;
    static String field434;
    static String field435;
    static String field436;
    static String field437;
    static String field438;
    static String field439;
    static String field440;
    static String field441;
    static String field442;
    static String field443;
    static String field444;
    static String field445;
    static String field446;
    static String field447;
    static String field448;
    static String field449;
    static String field450;
    static String field451;
    static String field452;
    static String field453;
    static String field454;
    static String field455;
    static String field456;
    static String field457;
    static String field458;
    static String field459;
    static String field460;
    static String field461;
    static String field462;
    static String field463;
    static String field464;
    static String field465;
    static String field466;
    static String field467;
    static String field468;
    static String field469;
    static String field470;
    static String field471;
    static String field472;
    static String field473;
    static String field474;
    static String field475;
    static String field476;
    static String field477;
    static String field478;
    static String field479;
    static String field480;
    static String field481;
    static String field482;
    static String field483;
    static String field484;
    static String field485;
    static String field486;
    static String field487;
    static String field488;
    static String field489;
    static String field490;
    static String field491;
    static String field492;
    static String field493;
    static String field494;
    static String field495;
    static String field496;
    static String field497;
    static String field498;
    static String field499;
    static String field500;
    static String field501;
    static String field502;
    static String field503;
    static String field504;
    static String field505;
    static String field506;
    static String field507;
    static String field508;
    static String field509;
    static String field510;
    static String field511;
    static String field512;
    static String field513;
    static String field514;
    static String field515;
    static String field516;
    static String field517;
    static String field518;
    static String field519;
    static String field520;
    static String field521;
    static String field522;
    static String field523;
    static String field524;
    static String field525;
    static String field526;
    static String field527;
    static String field528;
    static String field529;
    static String field530;
    static String field531;
    static String field532;
    static String field533;
    static String field534;
    static String field535;
    static String field536;
    static String field537;
    static String field538;
    static String field539;
    static String field540;
    static String field541;
    static String field542;
    static String field543;
    static String field544;
    static String field545;
    static String field546;
    static String field547;
    static String field548;
    static String field549;
    static String field550;
    static String field551;
    static String field552;
    static String field553;
    static String field554;
    static String field555;
    static String field556;
    static String field557;
    static String field558;
    static String field559;
    static String field560;
    static String field561;
    static String field562;
    static String field563;
    static String field564;
    static String field565;
    static String field566;
    static String field567;
    static String field568;
    static String field569;
    Random field570 = new Random();
    CRACKED field571 = CRACKED.CRACKED;
    static long field572;
    Animation field573 = new Animation(Easings.field11957, 1.0f, field572);
    static long field574;
    Animation field575 = new Animation(Easings.field11957, 0.0f, field574);
    static long field576;
    Animation field577 = new Animation(Easings.field11957, field576);
    static long field578;
    Animation field579 = new Animation(Easings.field11957, 0.0f, field578);
    String field580 = "";
    int field581 = 0;
    IDLE field582 = IDLE.IDLE;
    String field583 = "";
    String field584 = "";
    MainMenuScreen field585;
    float field586;
    float field587;
    Account var_minced_manager_Account_I;
    static float field588;
    static float field589;
    static float field590;
    static float field591;
    static float field592;
    static float field593;
    static float field594;
    static float field595;
    WidgetTextField field596;
    WidgetAccountList field597;
    float field598;
    float field599;
    static float field600;
    static float field601;
    static float field602;
    static float field603;
    static float field604;
    static float field605;
    static String field606;
    static String field607;
    Object field608;
    static String field609;
    static String field610;
    static String field611;
    static float field612;
    float field613;
    static float field614;
    float field615;
    static float field616;
    static float field617;
    static float field618;
    static float field619;
    static float field620;
    static float field621;
    static float field622;
    static float field623;
    static String field624;
    static float field625;
    static String field626;
    static float field627;
    static float field628;
    static float field629;
    static String field630;
    static float field631;
    static String field632;
    static float field633;
    static float field634;
    static float field635;
    static String field636;
    static float field637;
    static float field638;
    static float field639;
    static float field640;
    static float field641;
    static float field642;
    static String field643;
    static float field644;
    static String field645;
    static float field646;
    static float field647;
    static float field648;
    static String field649;
    static float field650;
    static String field651;
    static float field652;
    static float field653;
    static String field654;
    static float field655;
    static String field656;
    static float field657;
    static float field658;
    static String field659;
    static float field660;
    static float field661;
    static float field662;
    static float field663;
    static float field664;
    static float field665;
    static float field666;
    static float field667;
    static float field668;
    static String field669;
    static String field670;
    static float field671;
    static float field672;
    static float field673;
    static float field674;
    static float field675;
    static float field676;
    static float field677;
    float field678;
    static float field679;
    static String field680;
    static String field681;
    static MinecraftClient var_net_minecraft_client_MinecraftClient_I = MinecraftClient.getInstance();
    static String field682;
    static float field683;
    static float field684;
    static float field685;
    static float field686;
    static float field687;
    static String field688;
    static int field689;
    static String field690;
    static float field691;
    static float field692;
    static float field693;
    static String field694;
    static float field695;
    static float field696;
    static float field697;
    static float field698;
    static String field699;
    static float field700;
    static float field701;
    static int field702;
    static float field703;
    static String field704;
    static float field705;
    static String field706;
    static float field707;
    static String field708;
    static String field709;
    static float field710;
    static String field711;
    static String field712;
    static String[] field713;
    static float field714;
    static float field715;
    static float field716;
    static float field717;
    static float field718;
    static float field719;
    static String field720;
    static String field721;
    static String field722;
    static String field723;
    static float field724;
    static float field725;
    static float field726;

    private void method135(float param1) {
        for (Widget v5 : this.method140()) {
            v5.method2763(v5.method2758() + param1);
        }
    }

    public boolean method136(double param1, double param3, int param5, float param6) {
        if (param5 != 0) {
            return false;
        }
        this.method135(param6);
        if (this.field427.method2728(param1, param3, param5)) {
            this.method149(param6);
            return true;
        }
        if (this.field428.method2728(param1, param3, param5)) {
            this.method149(param6);
            return true;
        }
        if (this.field429.method2728(param1, param3, param5)) {
            this.method149(param6);
            return true;
        }
        if (this.var_minced_core_ClsClass482_I.method2728(param1, param3, param5)) {
            this.method149(param6);
            return true;
        }
        if (this.field430.method2728(param1, param3, param5)) {
            this.method149(param6);
            return true;
        }
        if (this.field431.method2728(param1, param3, param5)) {
            this.method149(param6);
            return true;
        }
        if (this.field432.method2728(param1, param3, param5)) {
            this.method149(param6);
            return true;
        }
        this.method149(param6);
        return false;
    }

    private String method137() {
        int v7;
        String v5;
        String[] v1 = new String[]{field433, field434, field435, field436, field437, field438, field439, field440, field441, field442, field443, field444, field445, field446, field447, field448, field449, field450, field451, field452, field453, field454, field455, field456, field457, field458, field459, field460, field461, field462, field463, field464, field465, field466, field467, field468, field469, field470, field471, field472, field473, field474, field475, field476, field477, field478, field479, field480, field481, field482, field483, field484, field485, field486, field487, field488, field489, field490, field491, field492, field493, field494, field495, field496, field497, field498, field499, field500, field501, field502, field503, field504, field505, field506, field507, field508, field509, field510, field511, field512, field513, field514, field515, field516, field517, field518, field519, field520, field521, field522, field523, field524, field525, field526, field527, field528, field529, field530, field531, field532, field533, field534, field535, field536, field537, field538, field539, field540, field541, field542};
        String[] v2 = new String[]{field543, ""};
        Random v3 = new Random();
        String v4 = v1[v3.nextInt(110)];
        while ((v5 = v1[v3.nextInt(v1.length)]).equals(v4)) {
        }
        String v6 = v2[v3.nextInt(v2.length)];
        Object v8 = _4ZXYEo.弟5("makeConcatWithConstants", new Object[]{}, (String)v4, (String)v6, (String)v5, (int)(v7 = v3.nextInt(999) + 1));
        String result = ((String)v8).replace(" ", "_").trim();
        if (result.length() > 16) {
            result = result.substring(0, 16);
        }
        return result;
    }

    private String method138(String param1) {
        String string;
        if (param1 == null) {
            return "";
        }
        int v3 = -1;
        switch (param1.hashCode()) {
            case 1241514277: {
                if (!param1.equals(field544)) break;
                v3 = 0;
                break;
            }
            case -1191219685: {
                if (!param1.equals(field545)) break;
                v3 = 1;
                break;
            }
            case 18916780: {
                if (!param1.equals(field546)) break;
                v3 = 2;
                break;
            }
            case -593163200: {
                if (!param1.equals(field547)) break;
                v3 = 3;
                break;
            }
            case -850446236: {
                if (!param1.equals(field548)) break;
                v3 = 4;
                break;
            }
            case -1686541473: {
                if (!param1.equals(field549)) break;
                v3 = 5;
                break;
            }
            case 1544742046: {
                if (!param1.equals(field550)) break;
                v3 = 6;
                break;
            }
            case 2129353516: {
                if (!param1.equals(field551)) break;
                v3 = 7;
                break;
            }
            case -1220787733: {
                if (!param1.equals(field552)) break;
                v3 = 8;
                break;
            }
            case 1139352117: {
                if (!param1.equals(field553)) break;
                v3 = 9;
                break;
            }
            case 423086605: {
                if (!param1.equals(field554)) break;
                v3 = 10;
                break;
            }
            case 800095283: {
                if (!param1.equals(field555)) break;
                v3 = 11;
            }
        }
        switch (v3) {
            case 0: {
                string = field556;
                break;
            }
            case 1: {
                string = field557;
                break;
            }
            case 2: 
            case 3: {
                string = field558;
                break;
            }
            case 4: {
                string = field559;
                break;
            }
            case 5: {
                string = field560;
                break;
            }
            case 6: {
                string = field561;
                break;
            }
            case 7: {
                string = field562;
                break;
            }
            case 8: {
                string = field563;
                break;
            }
            case 9: {
                string = field564;
                break;
            }
            case 10: {
                string = field565;
                break;
            }
            case 11: {
                string = field566;
                break;
            }
            default: {
                string = param1.replace(field567, "").replace(field568, field569);
            }
        }
        return string;
    }

    public AltManagerPanel(MainMenuScreen param1, float param2, float param3) {
        this.field585 = param1;
        this.field586 = param2;
        this.field587 = param3;
        this.var_minced_manager_Account_I = Minced.getInstance().getAccountManager();
        this.method148();
    }

    private void method139() {
        int v1 = ThemeColors.method3199(MAIN.LOGO);
        boolean v2 = this.field571 == CRACKED.CRACKED;
        this.field427.method2722(v2 ? ColorUtil.method1853(v1, field588) : HexColor.method2750(Color.WHITE, field589), v2 ? ColorUtil.method1853(v1, field590) : HexColor.method2750(Color.WHITE, field591));
        this.field428.method2722(v2 ? HexColor.method2750(Color.WHITE, field592) : ColorUtil.method1853(v1, field593), v2 ? HexColor.method2750(Color.WHITE, field594) : ColorUtil.method1853(v1, field595));
    }

    private Widget[] method140() {
        return new Widget[]{this.field427, this.field428, this.field596, this.field429, this.var_minced_core_ClsClass482_I, this.field430, this.field431, this.field432, this.field597};
    }

    public boolean method141(int param1, int param2, int param3) {
        if (this.field571 == CRACKED.CRACKED) {
            if (this.field596.method2772() && param1 == 257) {
                this.method163();
                return true;
            }
            return this.field596.cfr_renamed_0(param1, param2, param3);
        }
        return false;
    }

    private void method142(CRACKED param1) {
        if (this.field571 == param1) {
            return;
        }
        this.field571 = param1;
        this.method139();
        this.method145();
        this.field579.setAnimatedValue(1.0f);
        this.field579.setTarget(1.0f);
        this.field579.restart();
    }

    private void method143() {
        Account v1 = this.method152();
        if (v1 == null) {
            return;
        }
        List v2 = v1.getFavoriteAccounts();
        v2.sort(Comparator.comparingLong(AccountEntry::getCreatedAt).reversed());
        List v3 = v1.getRegularAccounts();
        v3.sort(Comparator.comparingLong(AccountEntry::getCreatedAt).reversed());
        this.field597.method2800(v2, v3, v1.getSelectedAccountName());
        this.method164();
    }

    public void method144(DrawContext param1, int param2, int param3, float param4, float param5) {
        this.method135(param5);
        boolean v6 = this.field571 == CRACKED.CRACKED;
        this.field573.animate(v6 ? 1.0f : 0.0f);
        this.field575.animate(v6 ? 0.0f : 1.0f);
        this.field577.animate(v6 ? this.field598 : this.field599);
        this.field579.animate(0.0f);
        float v7 = this.field573.getAnimatedValue();
        float v8 = this.field575.getAnimatedValue();
        this.field596.method2762(v7 > field600);
        this.field429.method2762(v7 > field601);
        this.var_minced_core_ClsClass482_I.method2762(v7 > field602);
        this.field430.method2762(v8 > field603);
        this.method155(param1, param5);
        this.field427.method2738(param1, param2, param3, param4);
        this.field428.method2738(param1, param2, param3, param4);
        if (v7 > field604) {
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)v7);
            this.field596.method2738(param1, param2, param3, param4);
            this.field429.method2738(param1, param2, param3, param4);
            this.var_minced_core_ClsClass482_I.method2738(param1, param2, param3, param4);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
        if (v8 > field605) {
            boolean v9;
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)v8);
            boolean bl = v9 = this.field582 != IDLE.IDLE && this.field582 != IDLE.SUCCESS && this.field582 != IDLE.ERROR;
            if (!v9) {
                this.field430.method2762(true);
                this.field430.method2738(param1, param2, param3, param4);
            } else {
                this.field430.method2762(false);
            }
            this.method151(param1, param5);
            this.field431.method2762(v9);
            this.field431.method2764(v9);
            if (v9) {
                this.field431.method2738(param1, param2, param3, param4);
            }
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
        this.method153(param1, param5);
        this.field597.method2738(param1, param2, param3, param4);
        this.field432.method2738(param1, param2, param3, param4);
        this.method149(param5);
    }

    private void method145() {
        boolean v1 = this.field571 == CRACKED.CRACKED;
        this.field596.method2764(v1);
        this.field429.method2764(v1);
        this.var_minced_core_ClsClass482_I.method2764(v1);
        this.field430.method2764(!v1);
        if (!v1) {
            this.field596.method2771(false);
        }
    }

    private void method146() {
        if (this.field582 == IDLE.INITIALIZING || this.field582 == IDLE.AWAITING_BROWSER || this.field582 == IDLE.PROCESSING) {
            return;
        }
        this.field582 = IDLE.INITIALIZING;
        this.field583 = field606;
        this.field584 = "";
        this.method165();
        // IAS Microsoft auth server has been removed.
        this.field608 = null;
        this.field583 = field610;
    }

    private void method148() {
        this.field613 = (this.field586 - field612) / 2.0f;
        this.field615 = (this.field587 - field614) / 2.0f;
        float v1 = this.field613 + field616;
        float v2 = field617;
        float v3 = field618;
        float v4 = field619;
        float v5 = this.field615 + field620 + (v3 - v4) / 2.0f;
        float v6 = (v2 - field622) / 2.0f;
        this.field427 = new WidgetButton(v1, v5 += field621, v6, field623, field624, () -> this.method142(CRACKED.CRACKED)).method2739(13).method2727(field625).method2730(field626).method2737(12).method2740(field627);
        this.field428 = new WidgetButton(v1 + v6 + field628, v5, v6, field629, field630, () -> this.method142(CRACKED.MICROSOFT)).method2739(13).method2727(field631).method2730(field632).method2737(12).method2740(field633);
        this.method139();
        this.field596 = new WidgetTextField(v1, v5 += field634, v2, field635);
        this.field596.cfr_renamed_0(field636).cfr_renamed_0(16).cfr_renamed_0(field637).method2773(13).method2774(field638);
        float v7 = v2 * field640;
        float v8 = v2 - v7 - field641;
        this.field429 = new WidgetButton(v1, v5 += field639, v7, field642, field643, this::method163).method2739(13).method2727(field644).method2730(field645).method2737(12).method2740(field646);
        this.var_minced_core_ClsClass482_I = new WidgetButton(v1 + v7 + field647, v5, v8, field648, field649, this::method162).method2739(13).method2727(field650).method2730(field651).method2737(12).method2740(field652);
        float v9 = this.field596.method2758();
        this.field430 = new WidgetButton(v1, v9, v2, field653, field654, this::method146).method2739(13).method2727(field655).method2730(field656).method2737(12).method2740(field657);
        this.field431 = new WidgetButton(v1, v9, v2, field658, field659, this::method156).method2739(13).method2727(field660);
        this.field431.method2762(false);
        this.field431.method2764(false);
        this.method145();
        this.field598 = v5 += field661;
        this.field599 = this.field430.method2758() + field662 + field663;
        this.field577.setAnimatedValue(this.field571 == CRACKED.CRACKED ? this.field598 : this.field599);
        float v10 = this.field613 + field664;
        float v11 = this.field615 + field665 + field666;
        this.field432 = new WidgetButton(v10, v11, field667, field668, field669, this.field585::closeAltManager).method2730(field670).method2739(13).method2727(field671).method2737(12).method2740(field672);
        float v12 = this.field613 + field673 + field674 + field675;
        float v13 = field676;
        this.field678 = field677;
        float v14 = this.field615 + (field679 - this.field678) / 2.0f;
        this.field597 = new WidgetAccountList(v12, v14, v13, this.field678);
        this.field597.method2809(this.method152());
        this.field597.method2816(this::method164);
        this.method143();
    }

    private void method149(float param1) {
        for (Widget v5 : this.method140()) {
            v5.method2763(v5.method2758() - param1);
        }
    }

    private Identifier method150(String param1) {
        PlayerListEntry v2;
        if (param1 == null || param1.isEmpty() || field680.equals(param1)) {
            return DefaultSkinHelper.getTexture();
        }
        if (var_net_minecraft_client_MinecraftClient_I.getNetworkHandler() != null && (v2 = var_net_minecraft_client_MinecraftClient_I.getNetworkHandler().getPlayerListEntry(param1)) != null) {
            return v2.getSkinTextures().texture();
        }
        return DefaultSkinHelper.getTexture();
    }

    private void method151(DrawContext param1, float param2) {
        if (this.field582 == IDLE.IDLE || this.field582 == IDLE.SUCCESS) {
            return;
        }
        float v3 = this.field613 + field683;
        float v4 = field684;
        float v5 = this.field577.getAnimatedValue() + param2 - field685;
        FontRenderer v6 = Fonts.field12486[13];
        FontRenderer v7 = Fonts.field12491[12];
        float v8 = v5 + v6.method2718() + field686 + v7.method2718() + field687;
        FontRenderer v9 = Fonts.field12491[11];
        switch (this.field582) {
            case INITIALIZING: 
            case AWAITING_BROWSER: 
            case PROCESSING: {
                String v11 = this.field583.isEmpty() ? field688 : this.field583;
                float v12 = v9.method2719(v11);
                v9.method2711(param1, v11, v3 + (v4 - v12) / 2.0f, v8, field689);
                break;
            }
            case ERROR: {
                Object v11;
                Object object = v11 = this.field584.isEmpty() ? field690 : this.field584;
                if (v9.method2719((String)v11) > v4) {
                    while (v9.method2719((String)((Object)_4ZXYEo.弟а("makeConcatWithConstants", new Object[]{}, (String)v11))) > v4 && ((String)v11).length() > 10) {
                        v11 = ((String)v11).substring(0, ((String)v11).length() - 1);
                    }
                    v11 = _4ZXYEo.弟м("makeConcatWithConstants", new Object[]{}, (String)v11);
                }
                float v12 = v9.method2719((String)v11);
                int v13 = new Color(255, 150, 150).getRGB();
                v9.method2711(param1, (String)v11, v3 + (v4 - v12) / 2.0f, v8, v13);
            }
        }
    }

    private Account method152() {
        if (this.var_minced_manager_Account_I == null) {
            this.var_minced_manager_Account_I = Minced.getInstance().getAccountManager();
        }
        return this.var_minced_manager_Account_I;
    }

    private void method153(DrawContext param1, float param2) {
        float v3 = this.field613 + field691;
        float v4 = field692;
        float v5 = this.field577.getAnimatedValue() + param2 - field693;
        FontRenderer v6 = Fonts.field12486[13];
        FontRenderer v7 = Fonts.field12491[12];
        String v8 = this.field580.isEmpty() ? field694 : this.field580;
        float v9 = field695;
        float v10 = field696;
        float v11 = v6.method2719(v8);
        float v12 = v9 + v10 + v11;
        float v13 = v3 + (v4 - v12) / 2.0f;
        float v15 = v5 - v9 / 2.0f + field697;
        RenderUtil.method397(v13, v15, v9, v9, v9 / 2.0f - field698, HexColor.method2751(field699, field700));
        Identifier v16 = this.method150(this.field580);
        RenderUtil.method389(v16, null, v13, v15, v9, v9, v9 / 2.0f - field701, 1.0f);
        v6.method2711(param1, v8, v13 + v9 + v10, v5, field702);
        v5 += v6.method2718() + field703;
        FontRenderer v17 = Fonts.field12502[12];
        int v18 = HexColor.method2751(field704, field705);
        String v19 = _4ZXYEo.弟ы("makeConcatWithConstants", new Object[]{}, (int)this.field581);
        float v20 = v17.method2719(field706);
        float v21 = field707;
        float v22 = v20 + v21 + v7.method2719((String)((Object)v19));
        float v23 = v3 + (v4 - v22) / 2.0f;
        v17.method2711(param1, field708, v23, v5, v18);
        v7.method2711(param1, (String)((Object)v19), v23 + v20 + v21, v5, v18);
    }

    public static void method154() {
        field702 = new Color(255, 255, 255).getRGB();
        field689 = HexColor.method2751(field709, field710);
        field713 = new String[]{field711, field712};
    }

    private void method155(DrawContext param1, float param2) {
        float v3 = this.field613 + field714;
        float v4 = field715;
        float v5 = field716;
        float v6 = field717;
        float v7 = this.field579.getAnimatedValue();
        float v8 = this.field615 + param2 + field718 + (v5 - v6) / 2.0f + field719 * v7;
        float v9 = 1.0f - v7;
        String v10 = this.field571 == CRACKED.CRACKED ? field720 : field721;
        String v11 = this.field571 == CRACKED.CRACKED ? field722 : field723;
        FontRenderer v12 = Fonts.I[14];
        FontRenderer v13 = Fonts.field12486[13];
        float v14 = v12.method2719(v10);
        v12.method2711(param1, v10, v3 + (v4 - v14) / 2.0f, v8, ColorUtil.method1853(field702, v9));
        float v15 = v13.method2719(v11);
        v13.method2711(param1, v11, v3 + (v4 - v15) / 2.0f, v8 + field724, ColorUtil.method1853(field689, v9 / field725 * field726));
    }

    private void method156() {
        this.field582 = IDLE.IDLE;
        this.field583 = "";
        this.field584 = "";
        this.method165();
    }

    public void method157(float param1, float param2) {
        this.field586 = param1;
        this.field587 = param2;
        this.method148();
    }

    public boolean method159(double param1, double param3, double param5, float param7) {
        this.method135(param7);
        boolean v8 = this.field597.method2811(param1, param3, param5);
        this.method149(param7);
        return v8;
    }

    public boolean method160(char param1, int param2) {
        if (this.field571 == CRACKED.CRACKED) {
            return this.field596.method2775(param1, param2);
        }
        return false;
    }

    public boolean method161(double param1, double param3, int param5, float param6) {
        if (param5 != 0) {
            return false;
        }
        this.method135(param6);
        if (this.field427.method2736(param1, param3, param5)) {
            this.field596.method2771(false);
            this.method149(param6);
            return true;
        }
        if (this.field428.method2736(param1, param3, param5)) {
            this.field596.method2771(false);
            this.method149(param6);
            return true;
        }
        if (this.field596.method2736(param1, param3, param5)) {
            this.method149(param6);
            return true;
        }
        if (this.field429.method2736(param1, param3, param5)) {
            this.method149(param6);
            return true;
        }
        if (this.var_minced_core_ClsClass482_I.method2736(param1, param3, param5)) {
            this.method149(param6);
            return true;
        }
        if (this.field430.method2736(param1, param3, param5)) {
            this.method149(param6);
            return true;
        }
        if (this.field431.method2736(param1, param3, param5)) {
            this.method149(param6);
            return true;
        }
        if (this.field432.method2736(param1, param3, param5)) {
            this.method149(param6);
            return true;
        }
        if (this.field597.method2736(param1, param3, param5)) {
            this.field596.method2771(false);
            this.method149(param6);
            return true;
        }
        this.field596.method2771(false);
        this.method149(param6);
        return true;
    }

    private void method162() {
        String v1 = this.method137();
        this.field596.method2778(v1);
    }

    private void method163() {
        Account v1 = this.method152();
        if (v1 == null) {
            return;
        }
        String v2 = this.field596.method2779().replace(" ", "_").trim();
        if (v2.isEmpty()) {
            return;
        }
        if (v1.findAccount(v2) != null) {
            return;
        }
        v1.addAccount(v2, this.field571);
        v1.login(v2);
        AccountEntry v3 = v1.findAccount(v2);
        if (v3 != null) {
            this.field597.method2795();
            this.field597.method2805(v3, true);
        }
        this.field596.method2778("");
        this.field596.method2771(false);
        this.method164();
    }

    private void method164() {
        Account v1 = this.method152();
        if (v1 == null) {
            return;
        }
        this.field580 = v1.getSelectedAccountName();
        if (this.field580 == null || this.field580.isEmpty()) {
            this.field580 = var_net_minecraft_client_MinecraftClient_I.getSession().getUsername();
        }
        this.field581 = v1.getAccountCount();
    }

    private void method165() {
        this.field608 = null;
    }

    static {
        нХ = 0.0f;
        нж = 0.0f;
        нГ = 0.0f;
        нг = 0.0f;
        н3 = 0.0f;
        ню = 0.0f;
        нэ = 0.0f;
        не = 0.0f;
        н衣 = 0.0f;
        нВ = 0.0f;
        нЗ = 0.0f;
        нк = 0.0f;
        field702 = -1;
        field689 = Integer.MAX_VALUE;
        нЫ = 0.0f;
        field572 = 200L;
        field574 = 200L;
        field576 = 200L;
        field578 = 200L;
        field612 = 620.0f;
        field614 = 340.0f;
        field616 = 16.0f;
        field617 = 175.0f;
        field618 = 308.0f;
        field619 = 168.0f;
        field620 = 16.0f;
        field621 = 30.0f;
        field622 = 4.0f;
        field623 = 24.0f;
        field624 = "Cracked";
        field625 = 5.0f;
        field626 = "H";
        field627 = 4.0f;
        field628 = 4.0f;
        field629 = 24.0f;
        field630 = "Microsoft";
        field631 = 5.0f;
        field632 = "I";
        field633 = 4.0f;
        field634 = 29.0f;
        field635 = 28.0f;
        field636 = "Username";
        field637 = 4.0f;
        field638 = 8.0f;
        field639 = 33.0f;
        field640 = 0.6f;
        field641 = 4.0f;
        field642 = 24.0f;
        field643 = "Create";
        field644 = 4.0f;
        field645 = "A";
        field646 = 4.0f;
        field647 = 4.0f;
        field648 = 24.0f;
        field649 = "Random";
        field650 = 4.0f;
        field651 = "J";
        field652 = 4.0f;
        field653 = 24.0f;
        field654 = "Login";
        field655 = 4.0f;
        field656 = "I";
        field657 = 4.0f;
        field658 = 24.0f;
        field659 = "Cancel";
        field660 = 4.0f;
        field661 = 36.0f;
        field662 = 24.0f;
        field663 = 12.0f;
        field664 = 270.0f;
        field665 = 340.0f;
        field666 = 8.0f;
        field667 = 80.0f;
        field668 = 20.0f;
        field669 = "Back";
        field670 = "L";
        field671 = 4.0f;
        field672 = 4.0f;
        field673 = 16.0f;
        field674 = 175.0f;
        field675 = 16.0f;
        field676 = 397.0f;
        field677 = 220.99998f;
        field679 = 340.0f;
        field588 = 0.04f;
        field589 = 0.01f;
        field590 = 0.06f;
        field591 = 0.02f;
        field592 = 0.01f;
        field593 = 0.04f;
        field594 = 0.02f;
        field595 = 0.06f;
        field606 = "Starting server...";
        field607 = "Authorization successful! You can close this page.";
        field609 = "Minced-MSAuth";
        field610 = "Starting server...";
        field611 = "Failed to start auth server";
        field544 = "ias.login.initializing";
        field545 = "ias.login.server";
        field546 = "ias.login.msacToMsaMsr";
        field547 = "ias.login.msrToMsaMsr";
        field548 = "ias.login.msaToXbl";
        field549 = "ias.login.xblToXsts";
        field550 = "ias.login.xstsToMca";
        field551 = "ias.login.mcaToMcp";
        field552 = "ias.login.processing";
        field553 = "ias.login.encrypting";
        field554 = "ias.login.decrypting";
        field555 = "ias.login.finalizing";
        field556 = "Initializing...";
        field557 = "Waiting for browser...";
        field558 = "Getting Microsoft tokens...";
        field559 = "Authenticating with Xbox Live...";
        field560 = "Getting XSTS token...";
        field561 = "Authenticating with Minecraft...";
        field562 = "Getting Minecraft profile...";
        field563 = "Processing...";
        field564 = "Encrypting tokens...";
        field565 = "Decrypting tokens...";
        field566 = "Finalizing...";
        field567 = "ias.login.";
        field568 = "ias.error.";
        field569 = "Error: ";
        field433 = "lost";
        field434 = "sigma";
        field435 = "ezzka";
        field436 = "steven";
        field437 = "alexsh";
        field438 = "bomb";
        field439 = "star";
        field440 = "night";
        field441 = "wolf";
        field442 = "pro";
        field443 = "dark";
        field444 = "shadow";
        field445 = "phantom";
        field446 = "stealth";
        field447 = "blaze";
        field448 = "frost";
        field449 = "storm";
        field450 = "void";
        field451 = "cosmic";
        field452 = "neon";
        field453 = "cyber";
        field454 = "ghost";
        field455 = "ancient";
        field456 = "epic";
        field457 = "legend";
        field458 = "mystic";
        field459 = "ninja";
        field460 = "samurai";
        field461 = "viking";
        field462 = "dragon";
        field463 = "slayer";
        field464 = "hunter";
        field465 = "killer";
        field466 = "warrior";
        field467 = "assassin";
        field468 = "ranger";
        field469 = "mage";
        field470 = "wizard";
        field471 = "sniper";
        field472 = "berserk";
        field473 = "king";
        field474 = "lord";
        field475 = "master";
        field476 = "pro";
        field477 = "god";
        field478 = "demon";
        field479 = "phoenix";
        field480 = "wolf";
        field481 = "tiger";
        field482 = "raider";
        field483 = "quantum";
        field484 = "nova";
        field485 = "vortex";
        field486 = "chaos";
        field487 = "omega";
        field488 = "alpha";
        field489 = "sigma";
        field490 = "zenith";
        field491 = "nebula";
        field492 = "infinity";
        field493 = "morgenshtern";
        field494 = "velocity";
        field495 = "oblivion";
        field496 = "spectre";
        field497 = "enigma";
        field498 = "paradox";
        field499 = "crimson";
        field500 = "onyx";
        field501 = "sapphire";
        field502 = "ember";
        field503 = "mountain";
        field504 = "volcano";
        field505 = "tundra";
        field506 = "desert";
        field507 = "ocean";
        field508 = "forest";
        field509 = "river";
        field510 = "sky";
        field511 = "thunder";
        field512 = "lightning";
        field513 = "hurricane";
        field514 = "tsunami";
        field515 = "avalanche";
        field516 = "blizzard";
        field517 = "inferno";
        field518 = "abyss";
        field519 = "canyon";
        field520 = "glacier";
        field521 = "monsoon";
        field522 = "typhoon";
        field523 = "cyborg";
        field524 = "android";
        field525 = "hacker";
        field526 = "virus";
        field527 = "firewall";
        field528 = "neural";
        field529 = "matrix";
        field530 = "bit";
        field531 = "byte";
        field532 = "pixel";
        field533 = "robot";
        field534 = "drone";
        field535 = "laser";
        field536 = "plasma";
        field537 = "nano";
        field538 = "bybenci";
        field539 = "izrailya";
        field540 = "ivycraft";
        field541 = "bi4ben";
        field542 = "podrushka";
        field543 = "_";
        cfr_renamed_9 = "\u0001\u0001\u0001\u0001";
        field600 = 0.01f;
        field601 = 0.01f;
        field602 = 0.01f;
        field603 = 0.01f;
        field604 = 0.01f;
        field605 = 0.01f;
        field714 = 16.0f;
        field715 = 175.0f;
        field716 = 308.0f;
        field717 = 168.0f;
        field718 = 16.0f;
        field719 = -10.0f;
        field720 = "Cracked Login";
        field721 = "Microsoft Login";
        field722 = "Create account using only a username";
        field723 = "Login with your Microsoft account";
        field724 = 14.0f;
        field725 = 255.0f;
        field726 = 155.0f;
        field680 = "No account";
        field681 = "textures/entity/player/wide/steve.png";
        field682 = "textures/entity/player/wide/steve.png";
        field683 = 16.0f;
        field684 = 175.0f;
        field685 = 0.5f;
        field686 = 5.0f;
        field687 = 8.0f;
        field688 = "Processing...";
        field690 = "Error";
        cfr_renamed_10 = "\u0001...";
        cfr_renamed_11 = "\u0001...";
        field691 = 16.0f;
        field692 = 175.0f;
        field693 = 0.5f;
        field694 = "No account";
        field695 = 8.0f;
        field696 = 3.0f;
        field697 = 1.5f;
        field698 = 0.5f;
        field699 = "FFFFFF";
        field700 = 0.08f;
        field701 = 0.5f;
        field703 = 5.0f;
        field704 = "FFFFFF";
        field705 = 0.7f;
        cfr_renamed_12 = "Total: \u0001";
        field706 = "D";
        field707 = 3.0f;
        field708 = "D";
        field709 = "FFFFFF";
        field710 = 0.5f;
        field711 = "Daun";
        field712 = "Autist";
        AltManagerPanel.method154();
    }
}




