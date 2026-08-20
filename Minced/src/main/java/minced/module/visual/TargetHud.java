/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.client.util.DefaultSkinHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Vector4f
 */
package minced.module.visual;
import minced.core.EventHandler;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import minced.core.ColorUtil;
import minced.core.BooleanSetting;
import minced.core.TargetHudPoint;
import minced.core.ServerDetector;
import minced.core.HudComponent;
import minced.core.Animation;
import minced.core.EventRender2D;
import minced.core.ThemeColors;
import minced.core.Easings;
import minced.core.HudWindow;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.Minced;
import minced.enums.MAIN;
import minced.module.combat.AttackAura;
import minced.module.misc.ScoreboardHealth;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;

public class TargetHud
implements HudComponent {
    static float field9768;
    static float field9769;
    static float field9770;
    static float field9771;
    static float field9772;
    static float field9773;
    static float field9774;
    static float field9775;
    static float field9776;
    static float field9777;
    static float field9778;
    static float field9779;
    static BooleanSetting field9780;
    Animation field9781;
    static float field9782;
    Animation field9783;
    static float field9784;
    static float field9785;
    static float field9786;
    static float field9787;
    static float field9788;
    static float field9789;
    static float field9790;
    HudWindow field9791;
    Entity field9792 = null;
    static String field9793;
    Animation field9794;
    Animation field9795;
    Animation field9796;
    CopyOnWriteArrayList field9797;
    static float field9798;
    static float field9799;
    static float field9800;
    static BooleanSetting field9801;
    float field9802 = 0.0f;
    static double field9803;
    static double field9804;
    static float field9805;
    static float field9806;
    static float field9807;
    static float field9808;
    static float field9809;
    static float field9810;
    static float field9811;
    static float field9812;
    static float field9813;
    static float field9814;
    static float field9815;
    static float field9816;
    static float field9817;
    static float field9818;
    static float field9819;
    static float field9820;
    static float field9821;
    static String field9822;
    DecimalFormat field9823 = new DecimalFormat(field9822, new DecimalFormatSymbols(Locale.US));
    static long field9824;
    static long field9825;
    static long field9826;
    static long field9827;
    Animation field9828;
    static long field9829;
    static long field9830;
    static BooleanSetting field9831;
    static float field9832;
    static float field9833;
    static float field9834;
    static float field9835;
    static float field9836;
    static float field9837;
    static float field9838;
    static float field9839;
    static String field9840;
    static float field9841;
    static String field9842;
    static float field9843;
    static float field9844;
    static String field9845;
    static String field9846;
    static String field9847;
    static BooleanSetting field9848;
    static String field9849;
    static String field9850;
    static BooleanSetting field9851;
    static float field9852;
    static float field9853;
    static float field9854;
    static float field9855;
    static float field9856;
    static String field9857;
    static float field9858;
    static float field9859;
    static MinecraftClient I = MinecraftClient.getInstance();
    static float field9860;
    static float field9861;
    static float field9862;
    static float field9863;

    private float method2850(LivingEntity param1) {
        boolean v2 = Minced.getInstance().getModuleManager().method187(ScoreboardHealth.class).isEnabled();
        if (v2) {
            float v4;
            float v3 = ServerDetector.method2681(param1);
            if ((double)v3 == Math.floor(v4 = param1.getHealth() + param1.getAbsorptionAmount())) {
                return param1.getHealth();
            }
            return Math.max(0.0f, v3);
        }
        return param1.getHealth();
    }

    private void method2852(float param1, float param2, float param3, float param4, float param5, float param6, float param7, float param8) {
        if (param3 <= 0.0f) {
            return;
        }
        RenderUtil.method397(param1 + field9768, param2 + field9769, field9770, field9771, 1.0f, ColorUtil.method1853(ThemeColors.method3199(MAIN.MAIN), param8));
        float v9 = param1 + field9772;
        float v10 = param2 + field9773;
        float v11 = field9774;
        float v12 = field9775;
        Vector4f v13 = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        int v14 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
        int v15 = ColorUtil.method1851(v14, field9776);
        float v16 = ThemeColors.method3214(MAIN.MODULE_VISUAL) / field9777 * param8;
        RenderUtil.method399(v9, v10, v11, v12, v13, v14, v14, v15, v15, v16 * field9778);
        RenderUtil.method399(v9, v10, param4, v12, v13, v14, v14, v15, v15, v16 * field9779);
        RenderUtil.method399(v9, v10, param3, v12, v13, v14, v14, v15, v15, v16);
        if (((Boolean)field9780.getValue()).booleanValue() && param6 > 0.0f) {
            float v17 = this.field9781.getAnimatedValue();
            float v18 = field9782 * Math.min(v17 / param7, 1.0f);
            float v19 = this.field9783.getAnimatedValue();
            float v20 = field9784 * Math.min(v19 / param7, 1.0f);
            int v21 = ColorUtil.method1858(255, 210, 0);
            int v22 = ColorUtil.method1851(v21, field9785);
            RenderUtil.method399(v9, v10, v20, v12, v13, ColorUtil.method1851(v22, field9786), ColorUtil.method1851(v22, field9787), ColorUtil.method1851(v21, field9788), ColorUtil.method1851(v21, field9789), param8 * field9790);
            RenderUtil.method399(v9, v10, v18, v12, v13, v22, v22, v21, v21, param8);
        }
    }

    @Override
    @EventHandler(2)
    public void method2818(EventRender2D param1) {
        Entity v7;
        float v2 = this.field9791.method3457();
        float v3 = this.field9791.method3496();
        Minced.getInstance().getModuleManager().method177();
        LivingEntity v4 = AttackAura.method861();
        Entity v5 = this.method2861(v4);
        float v6 = v5 != null ? 1.0f : 0.0f;
        Entity entity = v7 = v5 != null ? v5 : this.field9792;
        if (v7 instanceof LivingEntity) {
            float v10;
            LivingEntity v8 = (LivingEntity)v7;
            float v9 = this.method2850(v8);
            float f = v10 = ServerDetector.method2684(field9793) ? 0.0f : v8.getAbsorptionAmount();
            if (v5 != this.field9792 && v5 != null) {
                TargetHud.method2860(this.field9794, v9);
                TargetHud.method2860(this.field9795, v9);
                TargetHud.method2860(this.field9781, v10);
                TargetHud.method2860(this.field9783, v10);
            }
            this.field9794.animate(v9);
            this.field9795.animate(v9);
            this.field9781.animate(v10);
            this.field9783.animate(v10);
        }
        this.method2854(v6);
        if (this.field9796.getAnimatedValue() <= 0.0f && !this.field9797.isEmpty()) {
            this.field9797.clear();
        }
        if (this.field9796.getAnimatedValue() > 0.0f) {
            this.method2853(param1.method2997(), v5 != null ? v5 : this.field9792, v2, v3);
        }
        this.field9792 = v5 != null ? v5 : this.field9792;
        this.field9791.method3483(field9798);
        this.field9791.method3459(field9799);
    }

    private void method2853(DrawContext param1, Entity param2, float param3, float param4) {
        float v7;
        float v6;
        LivingEntity v5;
        if (param2 instanceof LivingEntity) {
            v5 = (LivingEntity)param2;
            v6 = this.field9796.getAnimatedValue();
            float f = v7 = v5.hurtTime > 0 ? Math.min(field9800, (float)v5.hurtTime / (float)v5.maxHurtTime) : 0.0f;
        } else {
            return;
        }
        if (((Boolean)field9801.getValue()).booleanValue() && v7 > this.field9802) {
            for (int v8 = 0; v8 < 16; ++v8) {
                this.field9797.add(new TargetHudPoint(new Vec3d(field9803, field9804, 0.0)));
            }
        }
        this.field9802 = v7;
        float v8 = this.method2850(v5);
        float v9 = v5.getAbsorptionAmount();
        float v10 = v5.getMaxHealth();
        float v11 = Math.min(this.field9794.getAnimatedValue() / v10, 1.0f);
        float v12 = Math.min(this.field9795.getAnimatedValue() / v10, 1.0f);
        float v13 = field9805 * v11;
        float v14 = field9806 * v12;
        int v15 = ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_TEXT), ThemeColors.method3214(MAIN.WINDOW_TEXT) / field9807 * v6);
        int v16 = ColorUtil.method1853(ThemeColors.method3199(MAIN.MODULE_VISUAL), ThemeColors.method3214(MAIN.MODULE_VISUAL) / field9808 * v6);
        RenderUtil.method391(param3 - field9809, param4 - field9810, field9811, field9812, field9813, ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), v6), ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), v6), ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), v6), ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), v6), v6, field9814);
        RenderUtil.method390(param3, param4, field9815, field9816, field9817, ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), v6), v6);
        RenderUtil.method397(param3, param4, field9818, field9819, field9820, ColorUtil.method1853(ThemeColors.method3199(MAIN.MODULE_VISUAL), field9821 * v6));
        this.method2852(param3, param4, v13, v14, v8, v9, v10, v6);
        this.method2856(param1, param2, param3, param4, v8, v9, v15, v16);
        this.method2862(param3, param4, v6);
        this.method2859(param1, param2, param3, param4, v15, v6);
    }

    public TargetHud(HudWindow param1) {
        this.field9794 = new Animation(Easings.field11957, 0.0f, field9824);
        this.field9795 = new Animation(Easings.field11952, 0.0f, field9825);
        this.field9796 = new Animation(Easings.field11957, 0.0f, field9826);
        this.field9828 = new Animation(Easings.field11957, 1.0f, field9827);
        this.field9781 = new Animation(Easings.field11957, 0.0f, field9829);
        this.field9783 = new Animation(Easings.field11952, 0.0f, field9830);
        this.field9797 = new CopyOnWriteArrayList();
        this.field9791 = param1;
    }

    private void method2854(float param1) {
        this.field9796.animate(param1);
        this.field9828.animate((Boolean)field9831.getValue() != false && param1 > 0.0f ? 1.0f : 0.0f);
    }

    private void method2855(DrawContext param1, LivingEntity param2, float param3, float param4, float param5) {
        ArrayList<ItemStack> v6 = new ArrayList<ItemStack>();
        ItemStack v7 = param2.getOffHandStack();
        ItemStack v8 = param2.getMainHandStack();
        if (!v7.isEmpty()) {
            v6.add(v7);
        }
        if (!v8.isEmpty()) {
            v6.add(v8);
        }
        for (ItemStack v10 : param2.getArmorItems()) {
            if (v10.isEmpty()) continue;
            v6.add(v10);
        }
        int v9 = v6.size();
        if (v9 > 0) {
            float v10 = param3 + field9832;
            for (ItemStack v12 : v6) {
                RenderUtil.method398(param1, v12, v10, param4 - field9833, field9834, -1, param5);
                v10 += field9835;
            }
        }
    }

    private void method2856(DrawContext param1, Entity param2, float param3, float param4, float param5, float param6, int param7, int param8) {
        String v9 = param2 instanceof PlayerEntity
            ? ((PlayerEntity)param2).getGameProfile().getName()
            : param2.getName().getString();
        Fonts.I[15].method2711(param1, v9, param3 + field9836, param4 + field9837, param7);
        Fonts.I[14].method2711(param1, this.field9823.format(param5 + param6), param3 + field9839 + Fonts.I[14].method2719(field9840), param4 + field9841, param7);
        Fonts.I[14].method2711(param1, field9842, param3 + field9843, param4 + field9844, param8);
    }

    public static void method2857() {
        field9780 = new BooleanSetting(field9845, true);
        field9801 = new BooleanSetting(field9846, true);
        field9848 = new BooleanSetting(field9847, false);
        field9831 = new BooleanSetting(field9849, false);
        field9851 = new BooleanSetting(field9850, false);
    }

    public CopyOnWriteArrayList method2858() {
        return this.field9797;
    }

    private void method2859(DrawContext param1, Entity param2, float param3, float param4, int param5, float param6) {
        if (param2 instanceof PlayerEntity) {
            Identifier v7 = param2 instanceof AbstractClientPlayerEntity ? ((AbstractClientPlayerEntity)param2).getSkinTextures().texture() : DefaultSkinHelper.getTexture();
            RenderUtil.method389(v7, (LivingEntity)param2, param3 + field9852, param4 + field9853, field9854, field9855, field9856, param6);
        } else {
            Fonts.field12500[36].method2711(param1, field9857, param3 + field9858, param4 + field9859, param5);
        }
    }

    private static void method2860(Animation param0, float param1) {
        param0.setAnimatedValue(param1);
        param0.setValue(param1);
        param0.setTarget(param1);
        param0.setFinished(true);
    }

    private Entity method2861(LivingEntity param1) {
        return param1 == null && TargetHud.I.currentScreen instanceof ChatScreen ? TargetHud.I.player : param1;
    }

    private void method2862(float param1, float param2, float param3) {
        this.field9797.removeIf(param0 -> System.currentTimeMillis() - ((TargetHudPoint)param0).field9156 > ((TargetHudPoint)param0).field9159);
        for (TargetHudPoint v5 : (Iterable<TargetHudPoint>)(this.field9797)) {
            v5.method2647(param1, param2);
            float v6 = 1.0f - (float)(System.currentTimeMillis() - v5.field9156) / (float)v5.field9159;
            float v7 = field9860;
            RenderUtil.method397((float)v5.field9148.x - field9861, (float)v5.field9148.y - field9862, v7 * 2.0f, v7 * 2.0f, v7 - 1.0f, ColorUtil.method1855(ThemeColors.method3199(MAIN.MODULE_VISUAL), (int)(field9863 * v5.field9160 * v6 * param3)));
        }
    }

    static {
        field9793 = "funtime";
        field9798 = 36.0f;
        field9799 = 100.0f;
        field9800 = 0.5f;
        field9803 = 20.0;
        field9804 = 20.0;
        field9805 = 61.0f;
        field9806 = 61.0f;
        field9807 = 255.0f;
        field9808 = 255.0f;
        field9809 = 6.0f;
        field9810 = 6.0f;
        field9811 = 117.0f;
        field9812 = 52.0f;
        field9813 = 8.0f;
        field9814 = 6.0f;
        field9815 = 105.0f;
        field9816 = 38.5f;
        field9817 = 8.0f;
        field9818 = 105.0f;
        field9819 = 38.5f;
        field9820 = 8.0f;
        field9821 = 0.05f;
        field9860 = 2.3f;
        field9861 = 5.0f;
        field9862 = 5.0f;
        field9863 = 255.0f;
        field9852 = 4.0f;
        field9853 = 4.0f;
        field9854 = 31.0f;
        field9855 = 31.0f;
        field9856 = 6.0f;
        field9857 = "N";
        field9858 = 10.0f;
        field9859 = 13.0f;
        field9832 = 5.0f;
        field9833 = 9.0f;
        field9834 = 0.5f;
        field9835 = 9.5f;
        field9768 = 37.5f;
        field9769 = 28.0f;
        field9770 = 61.0f;
        field9771 = 6.0f;
        field9772 = 37.5f;
        field9773 = 28.0f;
        field9774 = 61.0f;
        field9775 = 6.0f;
        field9776 = 0.5f;
        field9777 = 255.0f;
        field9778 = 0.3f;
        field9779 = 0.75f;
        field9782 = 35.0f;
        field9784 = 35.0f;
        field9785 = 0.3f;
        field9786 = 0.6f;
        field9787 = 0.6f;
        field9788 = 0.8f;
        field9789 = 0.8f;
        field9790 = 0.6f;
        field9836 = 37.5f;
        field9837 = 7.5f;
        field9838 = 45.0f;
        field9839 = 37.5f;
        field9840 = "Health:";
        field9841 = 18.5f;
        field9842 = "Health:";
        field9843 = 37.5f;
        field9844 = 18.5f;
        field9822 = "0.0";
        field9824 = 667L;
        field9825 = 667L;
        field9826 = 200L;
        field9827 = 133L;
        field9829 = 667L;
        field9830 = 667L;
        field9845 = "Золотые сердца";
        field9846 = "Частицы";
        field9847 = "При наведении";
        field9849 = "Отображать броню";
        field9850 = "Прозрачный фон";
        TargetHud.method2857();
    }
}




