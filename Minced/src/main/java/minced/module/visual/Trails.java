/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.ShaderProgramKey
 *  net.minecraft.client.gl.ShaderProgramKeys
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.Vec3d
 */
package minced.module.visual;
import minced.core.EventHandler;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import minced.core.ColorUtil;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.ProjectionUtil;
import minced.core.EventRender3D;
import minced.core.EventSwapWorld;
import minced.core.TrailPoint;
import minced.core.ThemeColors;
import minced.core.EntityFilter;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.MAIN;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Trails
extends Module {
    Map cfr_underscore;
    SliderSetting field4329;
    static float field4330;
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field4331;
    static double field4332;
    static float field4333;
    static float field4334;
    static float field4335;
    static float field4336;
    static String field4337;
    static String field4338;
    static String field4339;
    static String field4340;
    static String field4341;
    static String field4342;
    static String field4343;
    MultiBooleanSetting field4344 = new MultiBooleanSetting(field4339, new BooleanSetting(field4340, false), new BooleanSetting(field4341, true), new BooleanSetting(field4342, true), new BooleanSetting(field4343, false));
    static String field4345;
    static float field4346;
    static float field4347;
    static float field4348;
    static String field4349;

    @EventHandler(2)
    public void method1102(EventSwapWorld param1) {
        this.cfr_underscore.clear();
    }

    @EventHandler(2)
    public void method1103(EventRender3D param1) {
        float v2 = param1.O();
        float v3 = ((Float)this.field4329.getValue()).floatValue() * field4330;
        Vec3d v4 = Trails.I.gameRenderer.getCamera().getPos();
        double v5 = v4.x;
        double v7 = v4.y;
        double v9 = v4.z;
        param1.method2348().push();
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFunc((int)770, (int)771);
        Tessellator v11 = Tessellator.getInstance();
        BufferBuilder v12 = v11.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        for (Entity v14 : Trails.I.world.getEntities()) {
            boolean v22;
            if (!this.method1105(v14) || !(v14 instanceof LivingEntity)) continue;
            LivingEntity v15 = (LivingEntity)v14;
            double v16 = v15.prevX + (v15.getX() - v15.prevX) * (double)v2;
            double v18 = v15.prevY + (v15.getY() - v15.prevY) * (double)v2 + field4331 - (v15.isGliding() ? field4332 : 0.0);
            double v20 = v15.prevZ + (v15.getZ() - v15.prevZ) * (double)v2;
            boolean bl = v22 = v15 == Trails.I.player && Trails.I.options.getPerspective().isFirstPerson();
            if (!v22) {
                List v23 = (List)(this.cfr_underscore.computeIfAbsent(v15.getId(), param0 -> new ArrayList()));
                v23.add(new TrailPoint(v16, v18, v20));
            }
            float v23 = v15.getHeight() * (v15.isInSneakingPose() ? field4333 : 1.0f);
            float v24 = v23 * field4334;
            List v25 = (List)this.cfr_underscore.get(v15.getId());
            if (v25 == null) continue;
            TrailPoint v26 = null;
            boolean v27 = true;
            int v28 = 0;
            Iterator v29 = v25.iterator();
            while (v29.hasNext()) {
                float v37;
                float v36;
                float v35;
                TrailPoint v30 = (TrailPoint)v29.next();
                if ((float)(System.currentTimeMillis() - v30.field9293) > v3) {
                    v29.remove();
                    continue;
                }
                v30.нШ = Math.clamp((float)(System.currentTimeMillis() - v30.field9293) / v3, 0.0f, 1.0f) * field4335;
                int v31 = ColorUtil.method1855(ThemeColors.method3199(MAIN.MODULE_VISUAL), (int)((float)(255 * (255 - Math.round(v30.нШ))) / field4336));
                float v32 = (float)(v30.O - v5);
                float v33 = (float)(v30.field9291 - v7);
                float v34 = (float)(v30.field9292 - v9);
                if (v27) {
                    v12.vertex(param1.method2348().peek().getPositionMatrix(), v32, v33, v34).color(v31);
                    v12.vertex(param1.method2348().peek().getPositionMatrix(), v32, v33 + v23, v34).color(v31);
                } else {
                    v35 = (float)(v26.O - v5);
                    v36 = (float)(v26.field9291 - v7);
                    v37 = (float)(v26.field9292 - v9);
                    v12.vertex(param1.method2348().peek().getPositionMatrix(), v35, v36, v37).color(v28);
                    v12.vertex(param1.method2348().peek().getPositionMatrix(), v35, v36 + v23, v37).color(v28);
                }
                v12.vertex(param1.method2348().peek().getPositionMatrix(), v32, v33 + v23, v34).color(v31);
                v12.vertex(param1.method2348().peek().getPositionMatrix(), v32, v33, v34).color(v31);
                if (v27) {
                    v12.vertex(param1.method2348().peek().getPositionMatrix(), v32, v33, v34).color(v31);
                    v12.vertex(param1.method2348().peek().getPositionMatrix(), v32, v33 + v24, v34).color(v31);
                } else {
                    v35 = (float)(v26.O - v5);
                    v36 = (float)(v26.field9291 - v7);
                    v37 = (float)(v26.field9292 - v9);
                    v12.vertex(param1.method2348().peek().getPositionMatrix(), v35, v36, v37).color(v28);
                    v12.vertex(param1.method2348().peek().getPositionMatrix(), v35, v36 + v24, v37).color(v28);
                }
                v12.vertex(param1.method2348().peek().getPositionMatrix(), v32, v33 + v24, v34).color(v31);
                v12.vertex(param1.method2348().peek().getPositionMatrix(), v32, v33, v34).color(v31);
                if (v27) {
                    v12.vertex(param1.method2348().peek().getPositionMatrix(), v32, v33 + v23, v34).color(v31);
                    v12.vertex(param1.method2348().peek().getPositionMatrix(), v32, v33 + v23 - v24, v34).color(v31);
                } else {
                    v35 = (float)(v26.O - v5);
                    v36 = (float)(v26.field9291 - v7);
                    v37 = (float)(v26.field9292 - v9);
                    v12.vertex(param1.method2348().peek().getPositionMatrix(), v35, v36 + v23, v37).color(v28);
                    v12.vertex(param1.method2348().peek().getPositionMatrix(), v35, v36 + v23 - v24, v37).color(v28);
                }
                v12.vertex(param1.method2348().peek().getPositionMatrix(), v32, v33 + v23 - v24, v34).color(v31);
                v12.vertex(param1.method2348().peek().getPositionMatrix(), v32, v33 + v23, v34).color(v31);
                v27 = false;
                v26 = v30;
                v28 = v31;
            }
            if (!v25.isEmpty()) continue;
            this.cfr_underscore.remove(v15.getId());
        }
        // end() throws when nothing was buffered (no trail points yet) — draw only if non-empty.
        BuiltBuffer v38 = v12.endNullable();
        if (v38 != null) {
            BufferRenderer.drawWithGlobalProgram(v38);
        }
        param1.method2348().pop();
        RenderSystem.disableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.depthMask((boolean)true);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public Trails() {
        super(field4337, field4338, Combat.Visuals);
        this.field4329 = new SliderSetting(field4345, field4346, 2.0f, field4347, field4348);
        this.cfr_underscore = new HashMap();
        this.addSettings(this.field4344, this.field4329);
    }

    private boolean method1105(Entity param1) {
        if (!param1.isAlive() || !ProjectionUtil.method2003(param1)) {
            return false;
        }
        if (param1.isInvisible() && !this.field4344.method1898(field4349).booleanValue()) {
            return false;
        }
        if (param1 instanceof PlayerEntity) {
            if (param1 == Trails.I.player) {
                return EntityFilter.method3432(param1, this.field4344);
            }
            return EntityFilter.method3435(param1, this.field4344, false);
        }
        return false;
    }

    @Override
    public void onDisable() {
        this.cfr_underscore.clear();
        super.onDisable();
    }

    static {
        field4337 = "Trails";
        field4338 = "Рисует шлейф по последним позициям существ";
        field4339 = "Отображать";
        field4340 = "Игроков";
        field4341 = "Друзей";
        field4342 = "Себя";
        field4343 = "Невидимых";
        field4345 = "Длина";
        field4346 = 2.5f;
        field4347 = 4.0f;
        field4348 = 0.5f;
        field4330 = 100.0f;
        field4331 = 0.05;
        field4332 = 0.15;
        field4333 = 0.8f;
        field4334 = 0.01f;
        field4335 = 255.0f;
        field4336 = 220.0f;
        field4349 = "Невидимых";
    }
}



