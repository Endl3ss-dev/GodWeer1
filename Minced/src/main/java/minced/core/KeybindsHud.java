/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ChatScreen
 */
package minced.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import minced.core.HudTheme;
import minced.core.ScissorUtil;
import minced.core.HudComponent;
import minced.core.Animation;
import minced.core.EventRender2D;
import minced.core.Easings;
import minced.core.HudWindow;
import minced.core.Fonts;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.KeyCode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;

public class KeybindsHud
implements HudComponent {
    static MinecraftClient var_net_minecraft_client_MinecraftClient_I = MinecraftClient.getInstance();
    HudWindow field9569;
    static float field9570;
    static float field9571;
    Animation field9572;
    Animation field9573;
    static String field9574;
    static String field9575;
    static float field9576;
    static float field9577;
    static String field9578;
    static float field9579;
    static float field9580;
    static float field9581;
    Map var_java_util_Map_I;
    static float field9582;
    static float field9583;
    static float field9584;
    static float field9585;
    static float field9586;
    static float I5;
    static float field9587;
    static float field9588;
    static float field9589;
    static float II;
    Animation field9590 = new Animation(Easings.field11952, 0.0f, field9593);
    static float I2;
    static float field9591;
    static float field9592;
    static long field9593;
    static long field9594;
    static long field9595;
    static long field9596;
    static long field9597;
    static float I7;

    @Override
    @EventHandler(2)
    public void method2818(EventRender2D param1) {
        int v17;
        DrawContext v2 = param1.method2997();
        List<Module> v3 = Minced.getInstance().getModuleManager().method178().stream().filter(param0 -> param0.field1078 > I7 || param0.isEnabled()).filter(param0 -> param0.getKey() != -100).toList();
        boolean v4 = v3.isEmpty();
        boolean v5 = KeybindsHud.var_net_minecraft_client_MinecraftClient_I.currentScreen instanceof ChatScreen;
        float v6 = this.field9569.method3457();
        float v7 = this.field9569.method3496();
        float v8 = this.field9569.method3499();
        float v9 = field9570;
        int v10 = v3.size();
        float v11 = field9571;
        this.field9572.animate(!v4 || v5 ? 1.0f : 0.0f);
        float v12 = this.field9572.getAnimatedValue();
        if (v12 <= 0.0f) {
            this.field9569.method3459(0.0f);
            this.field9569.method3483(0.0f);
            return;
        }
        HudTheme.method1871(v6, v7, v8, v9 + this.field9573.getAnimatedValue(), v12, v11, v11, v11, v11);
        ScissorUtil.method2621(v6, v7, v8, v9 + this.field9573.getAnimatedValue());
        float v13 = Fonts.field12504[16].method2719(field9574);
        Fonts.I[14].method2711(v2, field9575, v6 + field9576, v7 + field9577, HudTheme.method1873(v12));
        Fonts.field12504[16].method2711(v2, field9578, v6 + v8 - v13 - field9579, v7 + field9580, HudTheme.method1875(v12));
        int v14 = 0;
        float v15 = field9581;
        int v16 = 0;
        for (v17 = 0; v17 < v10; ++v17) {
            Module v18 = v3.get(v17);
            boolean v19 = v18.isEnabled();
            if (v19) {
                ++v14;
            }
            Animation[] v20 = (Animation[])(this.var_java_util_Map_I.computeIfAbsent(v18, param0 -> new Animation[]{new Animation(Easings.field11952, 0.0f, field9596), new Animation(Easings.field11952, 0.0f, field9597)}));
            float v21 = field9582 - (v16 == 0 ? 0.0f : (float)v16 * field9583) + (float)v17 * field9584;
            if (v20[1].getAnimatedValue() == 0.0f && v20[0].getAnimatedValue() < field9585) {
                v20[1].setAnimatedValue(v21);
                v20[1].setValue(v21);
                v20[1].setTarget(v21);
                v20[1].setFinished(true);
            } else {
                v20[1].animate(v21);
            }
            v20[0].animate(v18.isEnabled() ? 1.0f : 0.0f);
            float v22 = v20[0].getAnimatedValue();
            if (v22 < field9586) {
                v20[1].setAnimatedValue(0.0f);
                v20[1].setValue(0.0f);
                v20[1].setTarget(0.0f);
                v20[1].setFinished(true);
            }
            v18.field1078 = v22;
            v18.field1077 = v20[1].getAnimatedValue();
            float v23 = v7 + v18.field1077 - 2.0f;
            String v24 = v18.getName();
            String v25 = KeyCode.getDisplayName(v18.getKey());
            float v26 = Fonts.I[13].method2719(v25);
            float v27 = Fonts.I[13].method2719(v24);
            Fonts.I[13].method2711(v2, v24, v6 + I5, v23 + field9587, HudTheme.method1873(v22));
            Fonts.I[13].method2711(v2, v25, v6 + v8 - v26 - field9588, v23 + field9589, HudTheme.method1875(v22));
            if (!v19) {
                ++v16;
            }
            v15 = Math.max(v15, v26 + v27 + II);
        }
        v17 = v5 && v4 ? 1 : 0;
        this.field9590.animate(v17 != 0 ? I2 : v15);
        this.field9573.animate(field9591 + (float)v14 * field9592);
        this.var_java_util_Map_I.keySet().retainAll(v3);
        this.field9569.method3459(this.field9590.getAnimatedValue());
        this.field9569.method3483(v9 + (v4 ? 0.0f : this.field9573.getAnimatedValue()));
        ScissorUtil.method2620();
    }

    public KeybindsHud(HudWindow param1) {
        this.field9573 = new Animation(Easings.field11952, 0.0f, field9594);
        this.field9572 = new Animation(Easings.field11952, 0.0f, field9595);
        this.var_java_util_Map_I = new HashMap();
        this.field9569 = param1;
    }

    static {
        field9570 = 16.0f;
        field9571 = 5.0f;
        field9574 = "t";
        field9575 = "Keybinds";
        field9576 = 5.0f;
        field9577 = 7.0f;
        field9578 = "t";
        field9579 = 4.5f;
        field9580 = 7.5f;
        field9581 = 75.0f;
        field9582 = 18.0f;
        field9583 = 10.0f;
        field9584 = 11.0f;
        field9585 = 0.1f;
        field9586 = 0.01f;
        I5 = 5.5f;
        field9587 = 5.5f;
        field9588 = 4.5f;
        field9589 = 5.5f;
        II = 14.0f;
        I2 = 75.0f;
        field9591 = 4.0f;
        field9592 = 11.0f;
        field9593 = 100L;
        field9594 = 100L;
        field9595 = 200L;
        field9596 = 200L;
        field9597 = 44L;
        I7 = 0.01f;
    }
}





