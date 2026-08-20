/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ChatScreen
 *  sLM.4ZXYEo
 */
package minced.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import minced.core.ColorUtil;
import minced.core.BooleanSetting;
import minced.core.Setting;
import minced.core.ModeSetting;
import minced.core.Render2DBatch;
import minced.core.HudComponent;
import minced.core.Animation;
import minced.core.EventRender2D;
import minced.core.ThemeColors;
import minced.core.Easings;
import minced.core.HudWindow;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.MAIN;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import sLM._4ZXYEo;

public class ModuleListHud
implements HudComponent {
    static String cfr_renamed_241;
    static String cfr_renamed_242;
    static String cfr_renamed_243;
    static String cfr_renamed_244;

    static BooleanSetting field9598;
    static long field9599;
    static float field9600;
    static long field9601;
    static String field9602;
    static BooleanSetting field9603;
    static String field9604;
    static float field9605;
    static long field9606;
    Animation field9607 = new Animation(Easings.field11952, 0.0f, field9606);
    Map cfr_renamed_4 = new HashMap();
    HudWindow field9608;
    static MinecraftClient I = MinecraftClient.getInstance();
    static float field9609;
    static float field9610;
    static float field9611;
    static float field9612;
    static float field9613;
    static String field9614;
    static float field9615;
    static float field9616;
    static float field9617;
    static float field9618;
    static float field9619;
    static float field9620;
    static float field9621;
    static float field9622;
    static float field9623;
    static float field9624;
    static float field9625;
    static float field9626;
    static float field9627;
    static float field9628;
    static float field9629;
    static float field9630;
    static float field9631;
    static float field9632;
    static float field9633;
    static double field9634;
    static double field9635;

    private float method2821(Module param1) {
        float v2 = Fonts.I[13].method2719(param1.getName());
        if (!((Boolean)field9598.getValue()).booleanValue()) {
            return v2;
        }
        String v3 = this.method2826(param1);
        float v4 = v3.isEmpty() ? 0.0f : Fonts.I[13].method2719((String)((Object)_4ZXYEo.фш("makeConcatWithConstants", new Object[]{}, (String)v3)));
        return v2 + v4;
    }

    public static void method2823() {
        field9603 = new BooleanSetting(field9602, false);
        field9598 = new BooleanSetting(field9604, true);
    }

    private List method2824() {
        ArrayList<Module> v1 = new ArrayList<Module>();
        for (Module v3 : Minced.getInstance().getModuleManager().method178()) {
            v3.getToggleAnimation().setTarget(v3.isEnabled() ? 1.0f : 0.0f);
            if (!(v3.getToggleAnimation().getValue() > field9605) || v3.getCategory().equals(Combat.Visuals) || v3.getCategory().equals(Combat.Miscellaneous)) continue;
            v1.add(v3);
        }
        v1.sort(Comparator.comparingDouble(param1 -> -this.method2821((Module)param1)));
        return v1;
    }

    public ModuleListHud(HudWindow param1) {
        this.field9608 = param1;
    }

    @Override
    @EventHandler(2)
    public void method2818(EventRender2D param12) {
        float v1422;
        Object v1322;
        DrawContext v2 = param12.method2997();
        Render2DBatch.method2668(v2);
        float v3 = this.field9608.method3457();
        float v4 = this.field9608.method3496();
        List v5 = this.method2824();
        boolean v6 = ModuleListHud.I.currentScreen instanceof ChatScreen || !v5.isEmpty();
        this.field9607.animate(v6 ? 1.0f : 0.0f);
        float v7 = this.field9607.getAnimatedValue();
        if (v7 <= field9609) {
            Render2DBatch.method2651();
            this.field9608.method3483(0.0f);
            this.field9608.method3459(0.0f);
            return;
        }
        ArrayList<Module> v9 = new ArrayList<Module>();
        for (int v10 = 0; v10 < v5.size(); ++v10) {
            Module module = (Module)v5.get(v10);
            int v12 = v10;
            Animation[] v1323 = (Animation[])this.cfr_renamed_4.computeIfAbsent(module, param1 -> new Animation[]{new Animation(Easings.field11952, 0.0f, field9599), new Animation(Easings.field11952, (float)v12 * field9600, field9601)});
            module.getToggleAnimation().setTarget(module.isEnabled() ? 1.0f : 0.0f);
            float v1422_141 = (float)v10 * field9610;
            v1323[1].animate(v1422_141);
            v1323[0].animate(module.getToggleAnimation().getValue() > field9611 ? 1.0f : 0.0f);
        }
        for (Map.Entry entry : (Iterable<java.util.Map.Entry>)(this.cfr_renamed_4.entrySet())) {
            if (v5.contains(entry.getKey())) continue;
            Animation[] v12 = (Animation[])entry.getValue();
            v12[0].animate(0.0f);
            if (!v12[0].isFinished() || !(v12[0].getAnimatedValue() <= field9612)) continue;
            v9.add((Module)entry.getKey());
        }
        float v10 = 0.0f;
        for (Map.Entry v12 : (Iterable<java.util.Map.Entry>)(this.cfr_renamed_4.entrySet())) {
            v1322 = (Module)v12.getKey();
            Animation[] v1422_155 = (Animation[])v12.getValue();
            if (!(v1422_155[0].getAnimatedValue() > field9613)) continue;
            float v15 = Fonts.I[13].method2719(((Module)v1322).getName());
            String v16 = (Boolean)field9598.getValue() != false ? this.method2826((Module)v1322) : "";
            float v17 = v16.isEmpty() ? 0.0f : Fonts.I[13].method2719((String)((Object)_4ZXYEo.ф7("makeConcatWithConstants", new Object[]{}, (String)v16)));
            v10 = Math.max(v10, v15 + v17);
        }
        float f = Fonts.I[13].method2719(field9614);
        float v12 = f + field9615;
        float v1322_164 = Math.max(v10 + field9616, v12);
        boolean v15 = v3 + v1322_164 / 2.0f > (v1422 = (float)I.getWindow().getScaledWidth()) / 2.0f;
        int v16 = 0;
        for (Module v18 : (Iterable<Module>)(v5)) {
            float v35;
            float v34;
            float v33;
            float v20;
            Animation[] v19 = (Animation[])this.cfr_renamed_4.get(v18);
            if (v19 == null || (v20 = v7 * v19[0].getAnimatedValue()) <= field9617) continue;
            float v21 = v4 + v19[1].getAnimatedValue();
            String v22 = v18.getName();
            String v23 = (Boolean)field9598.getValue() != false ? this.method2826(v18) : "";
            boolean v24 = !v23.isEmpty();
            float v25 = Fonts.I[13].method2719(v22);
            float v26 = v24 ? Fonts.I[13].method2719((String)((Object)_4ZXYEo.фЭ("makeConcatWithConstants", new Object[]{}, (String)v23))) : 0.0f;
            float v27 = v25 + v26 + field9618;
            float v28 = (1.0f - v19[0].getAnimatedValue()) / field9619;
            int v29 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
            int v30 = ColorUtil.method1851(v29, field9620);
            int v31 = ColorUtil.method1857(10, v16 * 10, v29, v30);
            int v32 = ColorUtil.method1853(v31, ThemeColors.method3214(MAIN.MODULE_VISUAL) / field9621 * v20);
            if (v15) {
                v33 = v3 + v1322_164 - v27 - v28;
                v34 = v33 + v27 - v25 - v26 - field9622;
                v35 = v33 + v27 - field9623;
            } else {
                v33 = v3 + v28;
                v34 = v33 + field9624;
                v35 = v33 - field9625;
            }
            RenderUtil.method391(v33 - field9626, v21 - field9627, v27 + field9628, field9629, field9630, ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), v20), ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), v20), ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), v20), ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), v20), v20, field9631);
            RenderUtil.method390(v33, v21, v27, field9632, 0.0f, (Boolean)field9603.getValue() != false ? ColorUtil.method1855(ThemeColors.method3199(MAIN.MAIN), 0) : ThemeColors.method3199(MAIN.MAIN), v20);
            Render2DBatch.method2672(v35, v21, 1.0f, field9633, v32);
            Fonts.I[13].method2711(v2, v22, v34, (double)v21 + field9634, v32);
            if (v24) {
                int v36 = ColorUtil.method1853(ThemeColors.method3199(MAIN.TEXT), v20);
                Fonts.I[13].method2711(v2, (String)((Object)_4ZXYEo.ф2("makeConcatWithConstants", new Object[]{}, (String)v23)), v34 + v25, (double)v21 + field9635, v36);
            }
            ++v16;
        }
        v9.forEach(this.cfr_renamed_4::remove);
        Render2DBatch.method2651();
        this.field9608.method3483(15 + v5.size() * 10);
        this.field9608.method3459(v1322_164);
    }

    private String method2826(Module param1) {
        for (Setting v3 : param1.getSettings()) {
            ModeSetting v4;
            if (!(v3 instanceof ModeSetting) || !(v4 = (ModeSetting)v3).isVisible()) continue;
            return (String)v4.getValue();
        }
        return "";
    }

    static {
        field9609 = 0.01f;
        field9610 = 11.75f;
        field9611 = 0.1f;
        field9612 = 0.01f;
        field9613 = 0.01f;
        cfr_renamed_241 = " \u0001";
        field9614 = "Список модулей";
        field9615 = 22.5f;
        field9616 = 8.0f;
        field9617 = 0.01f;
        cfr_renamed_242 = " \u0001";
        field9618 = 8.0f;
        field9619 = 7.5f;
        field9620 = 0.3f;
        field9621 = 255.0f;
        field9622 = 4.0f;
        field9623 = 0.5f;
        field9624 = 4.0f;
        field9625 = 0.5f;
        field9626 = 6.0f;
        field9627 = 6.0f;
        field9628 = 12.0f;
        field9629 = 26.0f;
        field9630 = 6.0f;
        field9631 = 6.0f;
        field9632 = 13.75f;
        field9633 = 13.75f;
        field9634 = 5.5;
        cfr_renamed_243 = " \u0001";
        field9635 = 5.5;
        field9605 = 0.1f;
        cfr_renamed_244 = " \u0001";
        field9606 = 200L;
        field9599 = 44L;
        field9600 = 11.75f;
        field9601 = 44L;
        field9602 = "Прозрачный фон";
        field9604 = "Суффиксы";
        ModuleListHud.method2823();
    }
}




