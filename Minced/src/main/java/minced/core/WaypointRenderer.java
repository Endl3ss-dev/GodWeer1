/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.Heightmap$Type
 *  org.joml.Vector2f
 *  sLM.4ZXYEo
 *  sg.ec.2у$Ч
 */
package minced.core;

import java.lang.invoke.CallSite;
import java.util.List;
import minced.core.ColorUtil;
import minced.core.ProjectionUtil;
import minced.core.CancellableEvent;
import minced.core.EventRender3D;
import minced.core.Render3D;
import minced.core.Render2DBatch;
import minced.core.ServerDetector;
import minced.core.EventRender2D;
import minced.core.ThemeColors;
import minced.core.Fonts;
import minced.core.Minced;
import minced.enums.MAIN;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import org.joml.Vector2f;
import sLM._4ZXYEo;
import sg.ec._2u;

public class WaypointRenderer
implements CancellableEvent {
    static String Яъ;

    static MinecraftClient I = MinecraftClient.getInstance();
    static double field9109;
    static double field9110;
    static double field9111;
    static double field9112;
    static float field9113;
    static float field9114;
    static String field9115;
    static float field9116;
    static float field9117;
    static float field9118;
    static double field9119;

    @EventHandler(2)
    public void I(EventRender3D param1) {
        if (WaypointRenderer.I.player == null || WaypointRenderer.I.world == null) {
            return;
        }
        List v2 = Minced.getInstance().getWaypointManager().method1729(ServerDetector.method2685());
        if (v2 == null || v2.isEmpty()) {
            return;
        }
        Vec3d v3 = WaypointRenderer.I.gameRenderer.getCamera().getPos();
        MatrixStack v4 = param1.method2348();
        int v5 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
        int v6 = ColorUtil.method1855(v5, 220);
        v4.push();
        v4.translate(-v3.x, -v3.y, -v3.z);
        for (Object v8obj : v2) {
            _2u.ch v8 = (_2u.ch)v8obj;
            if (v8 == null) continue;
            double v9 = v8.Б();
            double v11 = v8.f13();
            double v13 = v8.С();
            if (!Double.isFinite(v11)) {
                v11 = this.method2592(v9, v13);
            }
            int v15 = (int)Math.floor(v9);
            int v16 = (int)Math.floor(v13);
            int v17 = (int)Math.floor(v11);
            Box v18 = new Box((double)v15, (double)v17, (double)v16, (double)v15 + 1.0, (double)v17 + 1.0, (double)v16 + 1.0).expand(field9109);
            Render3D.method2407(v4, v18, v6, 2.0f);
        }
        v4.pop();
    }

    @EventHandler(2)
    public void method2591(EventRender2D param1) {
        if (WaypointRenderer.I.player == null || WaypointRenderer.I.world == null) {
            return;
        }
        List v2 = Minced.getInstance().getWaypointManager().method1729(ServerDetector.method2685());
        if (v2 == null || v2.isEmpty()) {
            return;
        }
        for (Object v4obj : v2) {
            _2u.ch v4 = (_2u.ch)v4obj;
            if (v4 == null) continue;
            double v5 = v4.Б();
            double v7 = v4.f13();
            double v9 = v4.С();
            if (!Double.isFinite(v7)) {
                v7 = this.method2592(v5, v9);
            }
            double v11 = Math.floor(v5) + field9110;
            double v13 = Math.floor(v7) + field9111;
            double v15 = Math.floor(v9) + field9112;
            double v17 = WaypointRenderer.I.player.squaredDistanceTo(v11, v13, v15);
            Vector2f v19 = ProjectionUtil.method2006(v11, v13, v15, param1.method2996());
            if (v19.x == field9113 && v19.y == field9114) continue;
            int v20 = (int)Math.sqrt(v17);
            String v21 = v4.弟();
            if (v21 == null || v21.trim().isEmpty()) {
                v21 = field9115;
            }
            String v22 = _4ZXYEo.ч("makeConcatWithConstants", new Object[]{}, (String)v21, (int)v20);
            float v23 = Fonts.field12486[14].method2719((String)((Object)v22));
            float v24 = field9116;
            float v25 = field9117;
            float v26 = v19.x - v23 / 2.0f - v24;
            float v27 = v19.y - v25;
            float v28 = v23 + v24 * 2.0f;
            float v29 = field9118;
            Render2DBatch.method2664(param1.method2997(), v26, v27, v28, v29, ColorUtil.method1852(0, 0, 0, 120));
            Fonts.field12486[14].method2711(param1.method2997(), (String)((Object)v22), v26 + v24, (double)v27 + field9119, -1);
        }
    }

    private double method2592(double param1, double param3) {
        if (WaypointRenderer.I.world == null || WaypointRenderer.I.player == null) {
            return 0.0;
        }
        BlockPos v5 = BlockPos.ofFloored((double)param1, (double)WaypointRenderer.I.player.getY(), (double)param3);
        int v6 = v5.getX() >> 4;
        int v7 = v5.getZ() >> 4;
        if (!WaypointRenderer.I.world.getChunkManager().isChunkLoaded(v6, v7)) {
            return WaypointRenderer.I.player.getY();
        }
        BlockPos v8 = BlockPos.ofFloored((double)param1, (double)0.0, (double)param3);
        int v9 = WaypointRenderer.I.world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, v8.getX(), v8.getZ());
        return v9 - 1;
    }

    static {
        field9109 = 0.001;
        field9110 = 0.5;
        field9111 = 1.15;
        field9112 = 0.5;
        field9113 = Float.MAX_VALUE;
        field9114 = Float.MAX_VALUE;
        field9115 = "Waypoint";
        Яъ = "\u0001 • \u0001m";
        field9116 = 6.0f;
        field9117 = 3.0f;
        field9118 = 11.0f;
        field9119 = 3.5;
    }
}



