/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.Vec3d
 */
package minced.module.visual;
import minced.core.EventHandler;

import java.util.concurrent.CopyOnWriteArrayList;
import minced.core.ColorUtil;
import minced.core.SliderSetting;
import minced.core.JumpCirclePoint;
import minced.core.EventJump;
import minced.core.EventRenderLevel;
import minced.core.Render2DBatch;
import minced.core.ThemeColors;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.MAIN;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class JumpCircle
extends Module {
    CopyOnWriteArrayList field2178;
    static float field2179;
    static float field2180;
    static float field2181;
    SliderSetting field2182;
    static float field2183;
    static float field2184;
    static MinecraftClient I = MinecraftClient.getInstance();
    static float field2185;
    SliderSetting field2186 = new SliderSetting(field2214, field2215, 1.0f, 2.0f, field2216);
    static float field2187;
    static float field2188;
    static float field2189;
    static float field2190;
    static float field2191;
    static float field2192;
    static float field2193;
    static float field2194;
    static float field2195;
    static Identifier field2196;
    static float field2197;
    static float field2198;
    static float field2199;
    static float field2200;
    static float field2201;
    static float field2202;
    static float field2203;
    static float field2204;
    static float field2205;
    static float field2206;
    static float field2207;
    static float field2208;
    static String field2209 = "minced";
    static String field2210 = "images/game/jumpcircles/default.png";
    static float field2211;
    static String field2212;
    static String field2213;
    static String field2214;
    static float field2215;
    static float field2216;
    static String field2217;
    static float field2218;
    static float field2219;
    static float field2220;

    private int method596(int param1, int param2) {
        int v3 = Math.min(255, ColorUtil.method1854(param1) + param2);
        int v4 = Math.min(255, ColorUtil.method1846(param1) + param2);
        int v5 = Math.min(255, ColorUtil.method1847(param1) + param2);
        int v6 = ColorUtil.cfr_renamed_0(param1);
        return v6 << 24 | v3 << 16 | v4 << 8 | v5;
    }

    @EventHandler(2)
    private void method597(EventRenderLevel param1) {
        if (this.field2178.isEmpty()) {
            return;
        }
        int v2 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
        int v3 = ColorUtil.method1851(v2, field2179);
        long v6 = System.currentTimeMillis();
        for (JumpCirclePoint v9 : (Iterable<JumpCirclePoint>)(this.field2178)) {
            if (!v9.field7934) {
                v9.O.setTarget(field2180);
                if (v9.field7940.getTarget() != 1.0f || !v9.field7940.isFinished()) {
                    v9.field7940.setTarget(1.0f);
                }
                if (v9.O.isFinished()) {
                    v9.field7934 = true;
                    v9.O.setValue(field2181);
                    v9.O.setTarget(1.0f);
                }
            } else {
                v9.O.setTarget(1.0f);
                if (!v9.field7935) {
                    boolean v10;
                    if (v9.field7940.getTarget() != 1.0f || !v9.field7940.isFinished()) {
                        v9.field7940.setTarget(1.0f);
                    }
                    boolean bl = v10 = v6 - v9.field7941 >= (long)(((Float)this.field2182.getValue()).floatValue() * field2183);
                    if (v9.field7940.isFinished() && v10) {
                        v9.field7935 = true;
                        v9.field7940.setSpeed(1.0f);
                        v9.field7940.setTarget(0.0f);
                    }
                } else {
                    if (v9.field7940.getTarget() != 0.0f || !v9.field7940.isFinished()) {
                        v9.field7940.setTarget(0.0f);
                    }
                    if (v9.O.isFinished() && !v9.field7936) {
                        v9.field7936 = true;
                        v9.field7940.setSpeed(field2184);
                        v9.field7940.setValue(v9.field7940.getValue());
                        v9.field7940.setTarget(0.0f);
                    }
                    if (v9.field7940.isFinished()) {
                        this.field2178.remove(v9);
                        continue;
                    }
                }
            }
            Vec3d v10 = JumpCircle.I.gameRenderer.getCamera().getPos();
            float v11 = (float)(v9.field7942.x - v10.x);
            float v12 = (float)(v9.field7942.y - v10.y) + field2185;
            float v13 = (float)(v9.field7942.z - v10.z);
            float v14 = (((Float)this.field2186.getValue()).floatValue() + 1.0f) * v9.O.getValue() * field2187;
            float v15 = Math.max(field2188, v14 * field2189);
            float v16 = v9.field7940.getValue();
            int v17 = ColorUtil.method1853(v2, v16);
            v17 = ColorUtil.cfr_renamed_0(v17) << 24 | Math.min(255, ColorUtil.method1854(v17) + 15) << 16 | Math.min(255, ColorUtil.method1846(v17) + 15) << 8 | Math.min(255, ColorUtil.method1847(v17) + 15);
            float v18 = field2190;
            float v19 = (float)(v6 % (long)2000) / 2000.0f * field2191;
            for (int v20 = 0; v20 < 20; ++v20) {
                float v21 = (float)Math.toRadians((float)v20 * v18);
                float v22 = (float)Math.toRadians((float)(v20 + 1) * v18);
                float v23 = (float)Math.cos(v21);
                float v24 = (float)Math.sin(v21);
                float v25 = (float)Math.cos(v22);
                float v26 = (float)Math.sin(v22);
                float v27 = ((float)v20 * v18 + v19) % field2192 / field2193;
                float v28 = ((float)(v20 + 1) * v18 + v19) % field2194 / field2195;
                float v29 = this.method601(v27);
                float v30 = this.method601(v28);
                int v31 = this.method598(v2, v3, v29, v16);
                int v32 = this.method598(v2, v3, v30, v16);
                int v33 = this.method596(v31, 15);
                int v34 = this.method596(v32, 15);
                Render2DBatch.method2649(field2196, v11 + v23 * v14, v12, v13 + v24 * v14, v11 + v25 * v14, v12, v13 + v26 * v14, v11 + v25 * v15, v12, v13 + v26 * v15, v11 + v23 * v15, v12, v13 + v24 * v15, v33, v34, v17, v17, v23 * field2197 + field2198, v24 * field2199 + field2200, v25 * field2201 + field2202, v26 * field2203 + field2204, field2205, field2206, field2207, field2208);
            }
        }
        Render2DBatch.method2667();
    }

    private int method598(int param1, int param2, float param3, float param4) {
        int v5 = ColorUtil.method1850(param1, param2, param3);
        return ColorUtil.method1853(v5, param4);
    }

    @EventHandler(2)
    private void method599(EventJump param1) {
        Vec3d v2 = new Vec3d(JumpCircle.I.player.getX(), JumpCircle.I.player.getY(), JumpCircle.I.player.getZ());
        this.field2178.add(new JumpCirclePoint(v2));
        this.field2178.add(new JumpCirclePoint(v2));
    }

    public static void method600() {
        field2196 = Identifier.of((String)field2209, (String)field2210);
    }

    private float method601(float param1) {
        if (param1 < field2211) {
            return param1 * 2.0f;
        }
        return (1.0f - param1) * 2.0f;
    }

    public JumpCircle() {
        super(field2212, field2213, Combat.Visuals);
        this.field2182 = new SliderSetting(field2217, 2.0f, field2218, field2219, field2220);
        this.field2178 = new CopyOnWriteArrayList();
        this.addSettings(this.field2186, this.field2182);
    }

    static {
        field2212 = "Jump Circle";
        field2213 = "Добавляет эффект при прыжке";
        field2214 = "Радиус";
        field2215 = 1.5f;
        field2216 = 0.1f;
        field2217 = "Время жизни (сек)";
        field2218 = 0.25f;
        field2219 = 10.0f;
        field2220 = 0.25f;
        field2179 = 0.5f;
        field2180 = 1.1f;
        field2181 = 1.1f;
        field2183 = 1000.0f;
        field2184 = 3.0f;
        field2185 = 0.05f;
        field2187 = 0.5f;
        field2188 = 0.001f;
        field2189 = 0.02f;
        field2190 = 18.0f;
        field2191 = 360.0f;
        field2192 = 360.0f;
        field2193 = 360.0f;
        field2194 = 360.0f;
        field2195 = 360.0f;
        field2197 = 0.5f;
        field2198 = 0.5f;
        field2199 = 0.5f;
        field2200 = 0.5f;
        field2201 = 0.5f;
        field2202 = 0.5f;
        field2203 = 0.5f;
        field2204 = 0.5f;
        field2205 = 0.5f;
        field2206 = 0.5f;
        field2207 = 0.5f;
        field2208 = 0.5f;
        field2211 = 0.5f;
        JumpCircle.method600();
    }
}



