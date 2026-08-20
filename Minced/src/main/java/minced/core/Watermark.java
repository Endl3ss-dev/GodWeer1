/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.Identifier
 *  sLM.4ZXYEo
 */
package minced.core;

import java.util.ArrayList;
import java.util.List;
import minced.core.UserInfo;
import minced.core.HudTheme;
import minced.core.TpsUtil;
import minced.core.HudComponent;
import minced.core.EventRender2D;
import minced.core.HudWindow;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.RecordClass230;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import sLM._4ZXYEo;

public class Watermark
implements HudComponent {
    static String 弟т;
    static String 弟С;

    static MinecraftClient var_net_minecraft_client_MinecraftClient_I = MinecraftClient.getInstance();
    static String field9688;
    static double field9689;
    static double field9690;
    static double field9691;
    static String field9692;
    static float field9693;
    static float field9694;
    static float field9695;
    static float field9696;
    static float field9697;
    static String field9698 = "minced";
    static String field9699 = "images/interface/avatar.png";
    static float field9700;
    static float field9701;
    static float field9702;
    static float field9703;
    static float field9704;
    static float field9705;
    static float field9706;
    static float field9707;
    static float field9708;
    static float field9709;
    static String field9710;
    HudWindow var_minced_core_ClsClass596_I;
    static float field9711;
    static float field9712;
    static float field9713;

    private String method2837() {
        return var_net_minecraft_client_MinecraftClient_I.getCurrentServerEntry() == null ? field9688 : Watermark.var_net_minecraft_client_MinecraftClient_I.getCurrentServerEntry().address;
    }

    private void method2839(DrawContext param1, float param2, float param3, List param4, int param5, int param6) {
        HudTheme.method1877(param2, param3, this.method2840(param4, param5, param6), field9695);
        float v7 = 0.0f;
        for (int v8 = param5; v8 < param6; ++v8) {
            RecordClass230 v9 = (RecordClass230)param4.get(v8);
            String v10 = this.method2841(v9);
            float v11 = Fonts.I[13].method2719(v10);
            float v12 = Fonts.field12496[16].method2719(String.valueOf(v9.icon));
            Fonts.I[13].method2711(param1, v10, param2 + v7 + 2.0f + field9696, param3 + field9697, HudTheme.method1873(1.0f));
            if (param5 == 0 && v8 == 0) {
                RenderUtil.method389(Identifier.of((String)field9698, (String)field9699), null, param2 + v7 + 2.0f + 1.0f, param3 + field9700, field9701, field9702, field9703, field9704);
            } else {
                Fonts.field12496[16].method2711(param1, String.valueOf(v9.icon), param2 + v7 + 2.0f + field9705, param3 + field9706, HudTheme.method1875(1.0f));
            }
            v7 += v11 + v12 + 2.0f + field9707;
        }
    }

    private float method2840(List param1, int param2, int param3) {
        float v4 = 0.0f;
        for (int v5 = param2; v5 < param3; ++v5) {
            RecordClass230 v6 = (RecordClass230)param1.get(v5);
            String v7 = this.method2841(v6);
            boolean v8 = v5 == param2 && (param2 == 3 || param2 == 0);
            float v9 = Fonts.I[13].method2719(v7);
            float v10 = Fonts.field12504[16].method2719(String.valueOf(v6.icon));
            float v11 = v8 ? 2.0f : field9708;
            v4 += v9 + v10 + v11 + field9709;
        }
        return v4;
    }

    private String method2841(RecordClass230 entry) {
        String value = String.valueOf(entry.consumer.get());
        if (entry.icon == 'n') {
            return value + "FPS";
        }
        if (entry.icon == 'p') {
            return value + "TPS";
        }
        return value;
    }

    private List method2842() {
        ArrayList<RecordClass230> v1 = new ArrayList<RecordClass230>();
        v1.add(new RecordClass230('A', () -> {
            if (Watermark.var_net_minecraft_client_MinecraftClient_I.player != null) {
                return Watermark.var_net_minecraft_client_MinecraftClient_I.player.getGameProfile().getName();
            }
            return Watermark.var_net_minecraft_client_MinecraftClient_I.getSession().getUsername();
        }));
        v1.add(new RecordClass230('n', () -> _4ZXYEo.ы("makeConcatWithConstants", new Object[]{}, (int)var_net_minecraft_client_MinecraftClient_I.getCurrentFps())));
        v1.add(new RecordClass230('d', () -> this.method2837()));
        v1.add(new RecordClass230('l', () -> String.format(field9710, Watermark.var_net_minecraft_client_MinecraftClient_I.player.getBlockX(), Watermark.var_net_minecraft_client_MinecraftClient_I.player.getBlockY(), Watermark.var_net_minecraft_client_MinecraftClient_I.player.getBlockZ())));
        v1.add(new RecordClass230('m', () -> {
            double v0 = Watermark.var_net_minecraft_client_MinecraftClient_I.player.getX() - Watermark.var_net_minecraft_client_MinecraftClient_I.player.prevX;
            double v2 = Watermark.var_net_minecraft_client_MinecraftClient_I.player.getZ() - Watermark.var_net_minecraft_client_MinecraftClient_I.player.prevZ;
            double v4 = Math.sqrt(v0 * v0 + v2 * v2);
            v4 = (double)Math.round(v4 * field9689) / field9690;
            float v6 = (float)(v4 * field9691);
            return String.format(field9692, Float.valueOf((float)Math.round(v6 * field9693) / field9694));
        }));
        v1.add(new RecordClass230('p', () -> _4ZXYEo.м("makeConcatWithConstants", new Object[]{}, (int)Math.round(TpsUtil.method2599()))));
        return v1;
    }

    public Watermark(HudWindow param1) {
        this.var_minced_core_ClsClass596_I = param1;
    }

    @Override
    @EventHandler(2)
    public void method2818(EventRender2D param1) {
        if (Watermark.var_net_minecraft_client_MinecraftClient_I.player == null) {
            return;
        }
        float v2 = this.var_minced_core_ClsClass596_I.method3457();
        float v3 = this.var_minced_core_ClsClass596_I.method3496();
        List v4 = this.method2842();
        int v5 = Math.min(3, v4.size());
        boolean v6 = v4.size() > 3;
        float v7 = this.method2840(v4, 0, v5);
        float v8 = v6 ? this.method2840(v4, 3, v4.size()) : 0.0f;
        float v9 = Math.max(v7, v8);
        this.var_minced_core_ClsClass596_I.method3459(v9);
        this.var_minced_core_ClsClass596_I.method3483(v6 ? field9711 : field9712);
        this.method2839(param1.method2997(), v2, v3, v4, 0, v5);
        if (v6) {
            this.method2839(param1.method2997(), v2, v3 + field9713, v4, 3, v4.size());
        }
    }

    static {
        field9711 = 34.0f;
        field9712 = 16.0f;
        field9713 = 18.0f;
        field9708 = 4.5f;
        field9709 = 6.5f;
        field9695 = 16.0f;
        field9696 = 15.0f;
        field9697 = 6.5f;
        field9700 = 3.0f;
        field9701 = 10.0f;
        field9702 = 10.0f;
        field9703 = 3.0f;
        field9704 = 255.0f;
        field9705 = 3.0f;
        field9706 = 6.5f;
        field9707 = 6.0f;
        field9688 = "local";
        弟т = "\u0001TPS";
        field9689 = 10.0;
        field9690 = 10.0;
        field9691 = 20.0;
        field9692 = "%.1fBPS";
        field9693 = 10.0f;
        field9694 = 10.0f;
        field9710 = "%d %d %d";
        弟С = "\u0001FPS";
    }
}



