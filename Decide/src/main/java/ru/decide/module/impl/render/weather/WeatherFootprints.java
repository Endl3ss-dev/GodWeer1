/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
 *  net.minecraft.class_10799
 *  net.minecraft.class_12247
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1921
 *  net.minecraft.class_243
 *  net.minecraft.class_290
 *  net.minecraft.class_2902$class_2903
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_638
 *  net.minecraft.class_742
 *  net.minecraft.class_7833
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package ru.decide.module.impl.render.weather;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_290;
import net.minecraft.class_2902;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_638;
import net.minecraft.class_742;
import net.minecraft.class_7833;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import ru.decide.module.impl.render.Weather;

@Environment(value=EnvType.CLIENT)
public final class WeatherFootprints {
    private static final int MAX_PRINTS = 220;
    private static final double SNOW_STEP_SQ = 0.1296;
    private static final double LEAF_STEP_SQ = 0.09;
    private static final class_2960 PLAYER_TEXTURE = class_2960.method_60655((String)"decide", (String)"textures/weather/footprint_player.png");
    private static final class_2960 MOB_TEXTURE = class_2960.method_60655((String)"decide", (String)"textures/weather/footprint_mob.png");
    private static final class_2960 LEAF_TEXTURE = class_2960.method_60655((String)"decide", (String)"textures/weather/leaf_trail.png");
    private static final RenderPipeline PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56864}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/weather/footprints")).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT).build());
    private static final class_1921 PLAYER_LAYER = class_1921.method_75940((String)"weather_footprints_player", (class_12247)class_12247.method_75927((RenderPipeline)PIPELINE).method_75934("Sampler0", PLAYER_TEXTURE).method_75937().method_75929(8192).method_75938());
    private static final class_1921 MOB_LAYER = class_1921.method_75940((String)"weather_footprints_mob", (class_12247)class_12247.method_75927((RenderPipeline)PIPELINE).method_75934("Sampler0", MOB_TEXTURE).method_75937().method_75929(8192).method_75938());
    private static final class_1921 LEAF_LAYER = class_1921.method_75940((String)"weather_leaf_trail", (class_12247)class_12247.method_75927((RenderPipeline)PIPELINE).method_75934("Sampler0", LEAF_TEXTURE).method_75937().method_75929(8192).method_75938());
    private final List<Print> prints = new ArrayList<Print>();
    private final Map<Integer, class_243> lastPositions = new HashMap<Integer, class_243>();
    private final class_9799 allocator = new class_9799(8192);
    private class_638 lastWorld;
    private Weather.Mode lastMode;
    private long nextStep;

    public void tick(Weather module, class_310 client) {
        boolean leaves;
        if (client == null || client.field_1687 == null || module == null || !module.isEnabled()) {
            this.clear();
            return;
        }
        boolean snow = module.mode() == Weather.Mode.WINTER && module.footprints.getValue() != false;
        boolean bl = leaves = module.mode() == Weather.Mode.AUTUMN && module.leafTrail.getValue() != false;
        if (!snow && !leaves) {
            this.clear();
            return;
        }
        if (client.field_1687 != this.lastWorld || module.mode() != this.lastMode) {
            this.clear();
            this.lastWorld = client.field_1687;
            this.lastMode = module.mode();
        }
        long now = System.currentTimeMillis();
        long lifetime = Math.round((leaves ? module.leafTrailTime.getValue() : module.footprintTime.getValue()).floatValue() * 1000.0f);
        this.prints.removeIf(print -> now - print.bornMs > lifetime);
        if ((++this.nextStep & 1L) != 0L) {
            return;
        }
        for (class_1297 entity : client.field_1687.method_18112()) {
            if (!(entity instanceof class_1309)) continue;
            class_1309 living = (class_1309)entity;
            this.track(module, client, living, now, leaves);
        }
    }

    private void track(Weather module, class_310 client, class_1309 entity, long now, boolean leaf) {
        int z;
        double step;
        int id = entity.method_5628();
        boolean player = entity instanceof class_742;
        boolean mobs = leaf ? module.leafTrailMobs.getValue() : module.footprintMobs.getValue();
        boolean self = leaf ? module.leafTrailSelf.getValue() : module.footprintSelf.getValue();
        if (!entity.method_5805() || entity.method_31481() || !mobs && !player || entity == client.field_1724 && !self) {
            this.lastPositions.remove(id);
            return;
        }
        if (!entity.method_24828()) {
            this.lastPositions.remove(id);
            return;
        }
        class_243 position = entity.method_73189();
        class_243 previous = this.lastPositions.put(id, position);
        if (previous == null) {
            return;
        }
        double dx = position.field_1352 - previous.field_1352;
        double dz = position.field_1350 - previous.field_1350;
        double d = step = leaf ? 0.09 : 0.1296;
        if (dx * dx + dz * dz < step) {
            return;
        }
        int x = (int)Math.floor(position.field_1352);
        if (!client.field_1687.method_8393(x >> 4, (z = (int)Math.floor(position.field_1350)) >> 4)) {
            return;
        }
        int top = client.field_1687.method_8624(class_2902.class_2903.field_13203, x, z);
        if (position.field_1351 < (double)top - 0.65 || position.field_1351 > (double)top + 1.35) {
            return;
        }
        float yaw = (float)Math.toDegrees(Math.atan2(dx, dz));
        this.prints.add(new Print(position.field_1352, (double)top + (leaf ? 0.035 : 0.025), position.field_1350, yaw, now, player, leaf, (this.nextStep & 2L) == 0L));
        while (this.prints.size() > 220) {
            this.prints.remove(0);
        }
    }

    public void render(Weather module, WorldRenderContext context) {
        boolean leaves;
        if (module == null || context == null || context.worldState() == null || context.worldState().field_63082 == null) {
            return;
        }
        boolean snow = module.mode() == Weather.Mode.WINTER && module.footprints.getValue() != false;
        boolean bl = leaves = module.mode() == Weather.Mode.AUTUMN && module.leafTrail.getValue() != false;
        if (!snow && !leaves) {
            return;
        }
        if (this.prints.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        long lifetime = Math.max(1L, (long)Math.round((leaves ? module.leafTrailTime.getValue() : module.footprintTime.getValue()).floatValue() * 1000.0f));
        class_243 camera = context.worldState().field_63082.field_63078;
        class_4587 matrices = context.matrices();
        if (matrices == null) {
            return;
        }
        ArrayList<Print> active = new ArrayList<Print>(this.prints.size());
        Iterator<Print> iterator = this.prints.iterator();
        while (iterator.hasNext()) {
            float fade;
            Print print = iterator.next();
            float age = (float)(now - print.bornMs) / (float)lifetime;
            if (age >= 1.0f) {
                iterator.remove();
                continue;
            }
            if (Math.round((print.leaf ? 220.0f : 210.0f) * (fade = 1.0f - WeatherFootprints.smoothstep(0.62f, 1.0f, age))) <= 2) continue;
            active.add(print);
        }
        if (active.isEmpty()) {
            return;
        }
        class_4597.class_4598 immediate = class_4597.method_22991((class_9799)this.allocator);
        WeatherFootprints.drawPass(immediate, PLAYER_LAYER, active, matrices, camera, module, now, lifetime, false, true);
        WeatherFootprints.drawPass(immediate, MOB_LAYER, active, matrices, camera, module, now, lifetime, false, false);
        WeatherFootprints.drawPass(immediate, LEAF_LAYER, active, matrices, camera, module, now, lifetime, true, false);
        immediate.method_22993();
    }

    private static void drawPass(class_4597.class_4598 immediate, class_1921 layer, List<Print> active, class_4587 matrices, class_243 camera, Weather module, long now, long lifetime, boolean leaf, boolean player) {
        class_4588 buffer = immediate.method_73477(layer);
        float scale = (leaf ? module.leafTrailSize.getValue() : module.footprintSize.getValue()).floatValue();
        for (Print print : active) {
            int green;
            int red;
            float length;
            float width;
            float age;
            float fade;
            int alpha;
            if (print.leaf != leaf || !leaf && print.player != player || (alpha = Math.round((leaf ? 220.0f : 210.0f) * (fade = 1.0f - WeatherFootprints.smoothstep(0.62f, 1.0f, age = (float)(now - print.bornMs) / (float)lifetime)))) <= 2) continue;
            float f = leaf ? 0.42f * scale : (width = (player ? 0.15f : 0.18f) * scale);
            float f2 = leaf ? 0.46f * scale : (length = (player ? 0.29f : 0.23f) * scale);
            float side = leaf ? 0.0f : (print.left ? -0.105f : 0.105f);
            matrices.method_22903();
            matrices.method_22904(print.x - camera.field_1352, print.y - camera.field_1351, print.z - camera.field_1350);
            matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(print.yaw));
            if (leaf) {
                matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(WeatherFootprints.hashRotation(print.x, print.z)));
            }
            matrices.method_46416(side, 0.0f, 0.0f);
            Matrix4f matrix = matrices.method_23760().method_23761();
            int n = leaf ? 255 : (red = player ? 205 : 198);
            int n2 = leaf ? 255 : (green = player ? 220 : 212);
            int blue = leaf ? 255 : (player ? 245 : 238);
            buffer.method_22918((Matrix4fc)matrix, -width, 0.0f, -length).method_22913(0.0f, 1.0f).method_1336(red, green, blue, alpha);
            buffer.method_22918((Matrix4fc)matrix, width, 0.0f, -length).method_22913(1.0f, 1.0f).method_1336(red, green, blue, alpha);
            buffer.method_22918((Matrix4fc)matrix, width, 0.0f, length).method_22913(1.0f, 0.0f).method_1336(red, green, blue, alpha);
            buffer.method_22918((Matrix4fc)matrix, -width, 0.0f, length).method_22913(0.0f, 0.0f).method_1336(red, green, blue, alpha);
            matrices.method_22909();
        }
    }

    private static float hashRotation(double x, double z) {
        long h = Double.doubleToLongBits(x * 7.3856093E7 + z * 1.9349663E7);
        h ^= h >>> 33;
        h *= -49064778989728563L;
        h ^= h >>> 33;
        return (float)((double)(h & 0xFFFFL) / 65535.0 * 360.0);
    }

    public void clear() {
        this.prints.clear();
        this.lastPositions.clear();
        this.lastWorld = null;
        this.lastMode = null;
    }

    private static float smoothstep(float edge0, float edge1, float value) {
        float t = Math.max(0.0f, Math.min(1.0f, (value - edge0) / (edge1 - edge0)));
        return t * t * (3.0f - 2.0f * t);
    }

    @Environment(value=EnvType.CLIENT)
    private record Print(double x, double y, double z, float yaw, long bornMs, boolean player, boolean leaf, boolean left) {
    }
}

