/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  sg.ec.ЧХ$Ч
 */
package minced.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import minced.core.ColorUtil;
import minced.core.HudTheme;
import minced.core.BooleanSetting;
import minced.core.ModeSetting;
import minced.core.StaffList;
import minced.core.ScissorUtil;
import minced.core.HudComponent;
import minced.core.Animation;
import minced.core.EventRender2D;
import minced.core.PlayerEntryComparator;
import minced.core.Easings;
import minced.core.HudWindow;
import minced.core.Fonts;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sg.ec.chhUpper;

public class StaffOnlineHud
implements HudComponent {
    static String field9864;
    static String field9865;
    static String field9866;
    static String field9867;
    static ModeSetting field9868;
    static String field9869;
    static BooleanSetting field9870;
    Map field9871 = new HashMap();
    Map field9872 = new HashMap();
    static long field9873;
    Animation field9874 = new Animation(Easings.field11952, 0.0f, field9873);
    static long field9875;
    Animation field9876 = new Animation(Easings.field11952, 0.0f, field9875);
    static long field9877;
    Animation field9878 = new Animation(Easings.field11952, 0.0f, field9877);
    HudWindow field9879;
    static MinecraftClient I = MinecraftClient.getInstance();
    static float field9880;
    static float field9881;
    static String field9882;
    static String field9883;
    static float field9884;
    static float field9885;
    static String field9886;
    static float field9887;
    static float field9888;
    static float field9889;
    static float field9890;
    static float field9891;
    static float field9892;
    static float field9893;
    static float field9894;
    static float field9895;
    static double field9896;
    static String field9897;
    static String field9898;
    static String field9899;
    static float field9900;
    static float field9901;
    static float field9902;
    static float field9903;
    static float field9904;
    static float field9905;
    static float field9906;
    static float field9907;

    public static void method2866() {
        field9868 = new ModeSetting(field9864, field9865, field9866, field9867);
        field9870 = new BooleanSetting(field9869, true);
    }

    public StaffOnlineHud(HudWindow param1) {
        this.field9879 = param1;
    }

    @Override
    @EventHandler(2)
    public void method2818(EventRender2D param12) {
        DrawContext v2 = param12.method2997();
        List v3 = StaffList.method2578() != null ? StaffList.method2578().method2572() : new ArrayList();
        v3.sort((param0, param1) -> {
            String v2_106 = Formatting.strip(((PlayerEntryComparator)param0).method3286().getString());
            String v3_107 = Formatting.strip(((PlayerEntryComparator)param1).method3286().getString());
            float v4 = Fonts.I[13].method2719(v2_106) + Fonts.I[13].method2719(((PlayerEntryComparator)param0).method3284());
            float v5 = Fonts.I[13].method2719(v3_107) + Fonts.I[13].method2719(((PlayerEntryComparator)param1).method3284());
            return Float.compare(v5, v4);
        });
        boolean v4 = v3.isEmpty();
        boolean v5 = StaffOnlineHud.I.currentScreen instanceof ChatScreen;
        float v6 = this.field9879.method3457();
        float v7 = this.field9879.method3496();
        float v8 = this.field9879.method3499();
        float v9 = field9880;
        float v10 = field9881;
        this.field9878.animate(!v4 || v5 ? 1.0f : 0.0f);
        float v11 = this.field9878.getAnimatedValue();
        if (v11 <= 0.0f) {
            this.field9879.method3459(0.0f);
            this.field9879.method3483(0.0f);
            return;
        }
        HudTheme.method1871(v6, v7, v8, v9 + this.field9876.getAnimatedValue(), v11, v10, v10, v10, v10);
        ScissorUtil.method2621(v6, v7, v8, v9 + this.field9876.getAnimatedValue());
        float v12 = Fonts.field12504[16].method2719(field9882);
        Fonts.I[14].method2711(v2, field9883, v6 + field9884, v7 + field9885, HudTheme.method1873(v11));
        Fonts.field12504[16].method2711(v2, field9886, v6 + v8 - v12 - field9887, v7 + field9888, HudTheme.method1875(v11));
        float v13 = field9889;
        int v14 = 0;
        int v15 = 0;
        HashMap<String, PlayerEntryComparator> v16 = new HashMap<String, PlayerEntryComparator>();
        for (PlayerEntryComparator v18 : (Iterable<PlayerEntryComparator>)(v3)) {
            v16.put(v18.method3284(), v18);
            this.field9872.put(v18.method3284(), v18);
        }
        for (PlayerEntryComparator v18 : (Iterable<PlayerEntryComparator>)(v3)) {
            String v33;
            int v32;
            boolean v34;
            String v19 = v18.method3284();
            chhUpper.ch v20 = (chhUpper.ch)(this.field9871.computeIfAbsent(v19, param0 -> new chhUpper.ch()));
            v20.Й.animate(1.0f);
            float v21 = v20.Й.getAnimatedValue();
            float v22 = field9890 + (float)v15 * field9891;
            if (v20.и.getAnimatedValue() == 0.0f && v21 < field9892) {
                v20.и.setAnimatedValue(v22);
                v20.и.setValue(v22);
                v20.и.setTarget(v22);
                v20.и.setFinished(true);
            } else {
                v20.и.animate(v22);
            }
            String v23 = Formatting.strip((String)v18.method3286().getString());
            float v24 = Fonts.I[13].method2719(v23);
            float v25 = Fonts.I[13].method2719(v19);
            float v26 = (Boolean)field9870.getValue() != false ? field9893 : 0.0f;
            float v27 = v26 + v24 + v25 + field9894;
            v13 = Math.max(v13, v27);
            float v28 = v7 + v20.и.getAnimatedValue() - 2.0f;
            float v29 = v6 + field9895;
            MutableText v30 = Text.literal((String)"").append(v18.method3286()).append((Text)Text.literal((String)v19));
            PlayerEntity v31 = StaffOnlineHud.I.world != null ? (PlayerEntity)StaffOnlineHud.I.world.getPlayers().stream().filter(param1 -> param1.getName().getString().equals(v19)).findFirst().orElse(null) : null;
            boolean bl = v34 = v31 != null && StaffOnlineHud.I.player != null && (double)StaffOnlineHud.I.player.distanceTo((Entity)v31) <= field9896;
            if (v18.method3285()) {
                v32 = ColorUtil.method1853(ColorUtil.method1858(255, 60, 60), v21);
                v33 = field9897;
            } else if (v34) {
                v32 = ColorUtil.method1853(ColorUtil.method1858(255, 200, 0), v21);
                v33 = field9898;
            } else {
                v32 = ColorUtil.method1853(ColorUtil.method1858(60, 255, 60), v21);
                v33 = field9899;
            }
            float v35 = Fonts.I[14].method2719(v33);
            float v36 = v6 + v8 - v35 - field9900;
            float v37 = Math.max(0.0f, v8 - (v29 - v6) - v35 - field9901);
            if (v21 > field9902) {
                Fonts.I[13].method2715(v2, (Text)v30, v29, v28 + field9903, HudTheme.method1873(v21), v37);
            }
            Fonts.I[13].method2711(v2, v33, v36, v28 + field9904, v32);
            ++v14;
            ++v15;
        }
        this.field9871.keySet().removeIf(param1 -> !v16.containsKey(param1));
        boolean v17 = v5 && v4;
        this.field9874.animate(v17 ? field9905 : v13);
        this.field9876.animate(field9906 + (float)v14 * field9907);
        this.field9879.method3459(this.field9874.getAnimatedValue());
        this.field9879.method3483(v9 + (v4 ? 0.0f : this.field9876.getAnimatedValue()));
        ScissorUtil.method2620();
    }

    static {
        field9880 = 16.0f;
        field9881 = 5.0f;
        field9882 = "w";
        field9883 = "Staff online";
        field9884 = 5.0f;
        field9885 = 7.0f;
        field9886 = "w";
        field9887 = 4.5f;
        field9888 = 7.5f;
        field9889 = 75.0f;
        field9890 = 18.0f;
        field9891 = 11.0f;
        field9892 = 0.1f;
        field9893 = 14.0f;
        field9894 = 30.0f;
        field9895 = 5.0f;
        field9896 = 100.0;
        field9897 = "Vanish";
        field9898 = "Near";
        field9899 = "Active";
        field9900 = 4.5f;
        field9901 = 9.0f;
        field9902 = 0.05f;
        field9903 = 5.5f;
        field9904 = 5.5f;
        field9905 = 75.0f;
        field9906 = 4.0f;
        field9907 = 11.0f;
        field9873 = 100L;
        field9875 = 100L;
        field9877 = 200L;
        field9864 = "Режим";
        field9865 = "Обычный";
        field9866 = "Обычный";
        field9867 = "LonyGrief";
        field9869 = "Отображать скин";
        StaffOnlineHud.method2866();
    }
}





