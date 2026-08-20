package minced.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import minced.core.AltManagerPanel;
import minced.core.ColorUtil;
import minced.core.WidgetButton;
import minced.core.HexColor;
import minced.core.Widget;
import minced.core.WidgetIconButton;
import minced.core.WidgetParticles;
import minced.core.Animation;
import minced.core.ThemeColors;
import minced.core.Easings;
import minced.core.Fonts;
import minced.core.KawaseBlur;
import minced.core.RenderUtil;
import minced.enums.MAIN;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.navigation.Navigable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class MainMenuScreen
extends Screen
implements Navigable,
Element,
ParentElement,
Drawable {
    static int Ш5;
    static int Шн;
    static float нб;
    static float но;
    static float Ш5f;

    static String field12052;
    List<Widget> field12053 = new ArrayList<>();
    List<Widget> field12054 = new ArrayList<>();
    boolean field12055 = false;
    static long field12056;
    Animation field12057 = new Animation(Easings.field11959, field12056);
    static long field12058;
    Animation field12059 = new Animation(Easings.field11959, field12058);
    WidgetParticles field12060 = new WidgetParticles();
    static String field12061 = "minced";
    static String field12062 = "images/menu/background.png";
    static Identifier field12063;
    static String field12064 = "minced";
    static String cfr_renamed_125 = "icons/mainmenu/blue_background.png";
    static Identifier field12065;
    static String field12066 = "minced";
    static String field12067 = "icons/mainmenu/black_background.png";
    static Identifier field12068;
    static float field12069;
    static float field12070;
    static float field12071;
    static float field12072;
    static float field12073;
    static float field12074;
    static float field12075;
    static float field12076;
    static float field12077;
    static String field12078;
    static String field12079;
    static float field12080;
    static String field12081;
    static String field12082;
    static float field12083;
    static String field12084;
    static String field12085;
    static float field12086;
    AltManagerPanel field12087;
    static float field12088;
    static String field12089;
    static String field12090;
    static float field12091;
    static float field12092;
    static String field12093;
    static String field12094;
    static float field12095;
    static String field12096;
    static float field12097;
    static String field12098;
    static float field12099;
    static String field12100;
    static float field12101;
    static String field12102;
    static float field12103;
    static String field12104;
    static float field12105;
    static float field12106;
    static float field12107;
    static String field12108;
    static String field12109;
    static String field12110;
    static String field12111;
    static String field12112;
    static float field12113;
    static float field12114;
    static float field12115;
    static float field12116;
    static float field12117;
    static String field12118;
    static float field12119;
    static float field12120;
    static float field12121;
    static float field12122;
    static float field12123;
    static float field12124;
    static float field12125;
    static float field12126;
    static float field12127;
    static float field12128;
    static float field12129;
    static String field12130;
    static String field12131;
    static float field12132;
    static String field12133;
    static String field12134;
    static float field12135;
    static float field12136;
    static float field12137;
    static float field12138;
    static float field12139;
    static String field12140;
    static float field12141;

    public MainMenuScreen() {
        super((Text)Text.literal((String)field12052));
    }

    public static void method3440() {
        field12063 = Identifier.of((String)field12061, (String)field12062);
        field12065 = Identifier.of((String)field12064, (String)cfr_renamed_125);
        field12068 = Identifier.of((String)field12066, (String)field12067);
    }

    private void toggleAltManager() {
        this.field12055 = !this.field12055;
    }

    public void closeAltManager() {
        this.field12055 = false;
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    private void initButtons() {
        this.field12053.clear();
        float v1 = field12069;
        float v2 = field12070;
        float v3 = field12071;
        float v5 = field12072;
        float v6 = field12073;
        float v7 = Math.max(field12074, Fonts.I[20].method2718());
        float v8 = 5.0f * v2 + 4.0f * v3;
        float v9 = v7 + field12075 + v8 + field12076 + v5;
        float v10 = ((float)this.height - v9) / 2.0f;
        float v11 = v10 + v7 + field12077;
        float v12 = (float)this.width / 2.0f - v1 / 2.0f;
        this.field12053.add(new WidgetButton(v12, v11, v1, v2, field12078, field12079, () -> this.client.setScreen((Screen)new SelectWorldScreen((Screen)this))).method2739(13).method2727(field12080).method2737(14));
        this.field12053.add(new WidgetButton(v12, v11 + v2 + v3, v1, v2, field12081, field12082, () -> this.client.setScreen((Screen)new MultiplayerScreen((Screen)this))).method2739(13).method2727(field12083).method2737(14));
        this.field12053.add(new WidgetButton(v12, v11 + (v2 + v3) * 2.0f, v1, v2, field12084, field12085, this::toggleAltManager).method2739(13).method2727(field12086).method2737(14));
        this.field12087 = new AltManagerPanel(this, this.width, this.height);
        this.field12053.add(new WidgetButton(v12, v11 + (v2 + v3) * field12088, v1, v2, field12089, field12090, () -> this.client.setScreen((Screen)new OptionsScreen((Screen)this, this.client.options))).method2739(13).method2727(field12091).method2737(14));
        this.field12053.add(new WidgetButton(v12, v11 + (v2 + v3) * field12092, v1, v2, field12093, field12094, () -> this.client.scheduleStop()).method2739(13).method2727(field12095).method2737(14).method2722(HexColor.method2751(field12096, field12097), HexColor.method2751(field12098, field12099)).method2723(HexColor.method2751(field12100, field12101), HexColor.method2751(field12102, field12103)).method2726(HexColor.method2751(field12104, field12105)));
        this.field12054.clear();
        float v13 = v5 * field12106 + v6 * 2.0f;
        float v14 = (float)this.width / 2.0f - v13 / 2.0f;
        float v15 = v11 + (v2 + v3) * 5.0f + field12107;
        this.field12054.add(new WidgetIconButton(v14, v15, v5, v5, field12108, () -> Util.getOperatingSystem().open(field12112)).method2768(16));
        this.field12054.add(new WidgetIconButton(v14 + v5 + v6, v15, v5, v5, field12109, () -> Util.getOperatingSystem().open(field12111)).method2768(16));
        this.field12054.add(new WidgetIconButton(v14 + (v5 + v6) * 2.0f, v15, v5, v5, field12110, () -> Util.getOperatingSystem().open(field12140)).method2768(16));
    }

    protected void init() {
        super.init();
        this.initButtons();
        if (this.field12087 != null) {
            this.field12087.method157(this.width, this.height);
        }
    }

    public boolean mouseScrolled(double param1, double param3, double param5, double param7) {
        float v9;
        if (this.field12055 && this.field12059.getAnimatedValue() > field12113 && this.field12087.method159(param1, param3, param7, v9 = (1.0f - this.field12059.getAnimatedValue()) * (float)(-this.height))) {
            return true;
        }
        return super.mouseScrolled(param1, param3, param5, param7);
    }

    public boolean charTyped(char param1, int param2) {
        if (this.field12055 && this.field12059.getAnimatedValue() > field12114 && this.field12087.method160(param1, param2)) {
            return true;
        }
        return super.charTyped(param1, param2);
    }

    public boolean mouseClicked(double param1, double param3, int param5) {
        float v6;
        if (this.field12055 && this.field12059.getAnimatedValue() > field12115 && this.field12087.method161(param1, param3, param5, v6 = (1.0f - this.field12059.getAnimatedValue()) * (float)(-this.height))) {
            return true;
        }
        if (!this.field12055 || this.field12057.getAnimatedValue() < field12116) {
            for (Widget v7 : this.field12053) {
                if (!((WidgetButton)v7).method2736(param1, param3, param5)) continue;
                return true;
            }
            for (Widget v7 : this.field12054) {
                if (!((WidgetIconButton)v7).method2736(param1, param3, param5)) continue;
                return true;
            }
        }
        return super.mouseClicked(param1, param3, param5);
    }

    public void render(DrawContext param1, int param2, int param3, float param4) {
        float v26;
        KawaseBlur.field12561.method3585(field12117, 5);
        this.field12060.updatePosition(param2, param3);
        RenderUtil.method397(0.0f, 0.0f, this.width, this.height, 0.0f, HexColor.method2751(field12118, 1.0f));
        RenderUtil.method388(param1.getMatrices(), field12063, 0.0, 0.0, this.width, this.height, new Color(255, 255, 255, 255));
        RenderUtil.method397(0.0f, 0.0f, this.width, this.height, 0.0f, ColorUtil.method1855(ThemeColors.method3199(MAIN.MODULE_VISUAL), 15));
        RenderUtil.method388(param1.getMatrices(), field12065, 0.0, 0.0, this.width, this.height, new Color(255, 255, 255, 10));
        RenderUtil.method388(param1.getMatrices(), field12068, 0.0, 0.0, this.width, this.height, new Color(255, 255, 255, 80));
        float v5 = field12119;
        float v6 = ((float)this.width - v5) / 2.0f;
        float v7 = ((float)this.height - v5) / 2.0f;
        int v8 = ThemeColors.method3199(MAIN.LOGO);
        RenderUtil.method391(v6, v7, v5, v5, field12120, v8, v8, v8, v8, field12121, field12122);
        this.field12057.animate(this.field12055 ? 1.0f : 0.0f);
        this.field12059.animate(this.field12055 ? 1.0f : 0.0f);
        float v9 = this.field12057.getAnimatedValue() * ((float)this.width / 2.0f + field12123);
        float v10 = (1.0f - this.field12059.getAnimatedValue()) * (float)(-this.height);
        float v11 = Math.max(field12124, Fonts.I[20].method2718());
        float v12 = field12125;
        float v13 = field12126;
        float v15 = field12127;
        float v16 = 5.0f * v12 + 4.0f * v13;
        float v17 = v11 + field12128 + v16 + field12129 + v15;
        float v18 = ((float)this.height - v17) / 2.0f;
        float v19 = Fonts.field12496[32].method2719(field12130);
        float v20 = Fonts.I[20].method2719(field12131);
        float v21 = v19 + field12132 + v20;
        float v22 = ((float)this.width - v21) / 2.0f + v9;
        int v23 = ThemeColors.method3199(MAIN.LOGO);
        Fonts.field12496[32].method2711(param1, field12133, v22, v18 + (v11 - Fonts.field12496[32].method2718()) / 2.0f, v23);
        Fonts.I[20].method2711(param1, field12134, v22 + v19 + field12135, v18 + (v11 - Fonts.I[20].method2718()) / 2.0f - field12136, Color.WHITE.getRGB());
        for (Widget v25 : this.field12053) {
            v26 = v25.method2757();
            v25.method2756(v26 + v9);
            ((WidgetButton)v25).method2738(param1, param2, param3, param4);
            v25.method2756(v26);
        }
        for (Widget v25 : this.field12054) {
            v26 = v25.method2757();
            v25.method2756(v26 + v9);
            ((WidgetIconButton)v25).method2738(param1, param2, param3, param4);
            v25.method2756(v26);
        }
        if (this.field12059.getAnimatedValue() > field12137) {
            this.field12087.method144(param1, param2, param3, param4, v10);
        }
        this.field12060.render(param1);
    }

    public boolean mouseReleased(double param1, double param3, int param5) {
        float v6;
        if (this.field12055 && this.field12059.getAnimatedValue() > field12138 && this.field12087.method136(param1, param3, param5, v6 = (1.0f - this.field12059.getAnimatedValue()) * (float)(-this.height))) {
            return true;
        }
        if (!this.field12055 || this.field12057.getAnimatedValue() < field12139) {
            for (Widget v7 : this.field12053) {
                if (!((WidgetButton)v7).method2728(param1, param3, param5)) continue;
                return true;
            }
            for (Widget v7 : this.field12054) {
                if (!((WidgetIconButton)v7).method2728(param1, param3, param5)) continue;
                return true;
            }
        }
        return super.mouseReleased(param1, param3, param5);
    }

    public boolean keyPressed(int param1, int param2, int param3) {
        if (param1 == 256 && this.field12055) {
            this.closeAltManager();
            return true;
        }
        if (this.field12055 && this.field12059.getAnimatedValue() > field12141 && this.field12087.method141(param1, param2, param3)) {
            return true;
        }
        return super.keyPressed(param1, param2, param3);
    }

    static {
        Ш5 = 0;
        Шн = 0;
        нб = 0.0f;
        но = 0.0f;
        Ш5f = 0.0f;
        field12052 = "Minced Main Menu";
        field12056 = 300L;
        field12058 = 300L;
        field12069 = 110.0f;
        field12070 = 25.0f;
        field12071 = 4.0f;
        field12072 = 25.0f;
        field12073 = 8.0f;
        field12074 = 32.0f;
        field12075 = 24.0f;
        field12076 = 24.0f;
        field12077 = 24.0f;
        field12078 = "Singleplayer";
        field12079 = "P";
        field12080 = 5.0f;
        field12081 = "Multiplayer";
        field12082 = "Q";
        field12083 = 5.0f;
        field12084 = "Alt Manager";
        field12085 = "A";
        field12086 = 5.0f;
        field12088 = 3.0f;
        field12089 = "Settings";
        field12090 = "m";
        field12091 = 5.0f;
        field12092 = 4.0f;
        field12093 = "Quit";
        field12094 = "R";
        field12095 = 5.0f;
        field12096 = "FF9696";
        field12097 = 0.01f;
        field12098 = "FF7C7C";
        field12099 = 0.02f;
        field12100 = "FF7766";
        field12101 = 0.02f;
        field12102 = "FF5555";
        field12103 = 0.04f;
        field12104 = "FF5555";
        field12105 = 0.04f;
        field12106 = 3.0f;
        field12107 = 24.0f;
        field12108 = "N";
        field12109 = "O";
        field12110 = "M";
        field12117 = 6.0f;
        field12118 = "090C08";
        field12119 = 250.0f;
        field12120 = 25.0f;
        field12121 = 0.32f;
        field12122 = 100.0f;
        field12123 = 150.0f;
        field12124 = 32.0f;
        field12125 = 25.0f;
        field12126 = 4.0f;
        field12127 = -5.0f;
        field12128 = 24.0f;
        field12129 = 24.0f;
        field12130 = "a";
        field12131 = "cracked by t.me/soezproject";
        field12132 = 4.0f;
        field12133 = "a";
        field12134 = "cracked by t.me/soezproject";
        field12135 = 4.0f;
        field12136 = 0.5f;
        field12137 = 0.01f;
        field12115 = 0.5f;
        field12116 = 0.5f;
        field12138 = 0.5f;
        field12139 = 0.5f;
        field12113 = 0.5f;
        field12141 = 0.5f;
        field12114 = 0.5f;
        field12140 = "https://t.me/soezproject";
        field12111 = "https://t.me/soezproject";
        field12112 = "https://t.me/soezproject";
        MainMenuScreen.method3440();
    }
}




