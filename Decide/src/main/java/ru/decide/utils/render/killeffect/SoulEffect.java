/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10017
 *  net.minecraft.class_10799
 *  net.minecraft.class_11658
 *  net.minecraft.class_11659
 *  net.minecraft.class_11661
 *  net.minecraft.class_11683
 *  net.minecraft.class_11788
 *  net.minecraft.class_11793
 *  net.minecraft.class_12075
 *  net.minecraft.class_12247
 *  net.minecraft.class_1297
 *  net.minecraft.class_1304
 *  net.minecraft.class_1309
 *  net.minecraft.class_156
 *  net.minecraft.class_1799
 *  net.minecraft.class_1921
 *  net.minecraft.class_1937
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_3730
 *  net.minecraft.class_4587
 *  net.minecraft.class_4587$class_4665
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4608
 *  net.minecraft.class_4618
 *  net.minecraft.class_638
 *  net.minecraft.class_742
 *  net.minecraft.class_745
 *  net.minecraft.class_761
 *  net.minecraft.class_898
 *  net.minecraft.class_9799
 */
package ru.decide.utils.render.killeffect;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10017;
import net.minecraft.class_10799;
import net.minecraft.class_11658;
import net.minecraft.class_11659;
import net.minecraft.class_11661;
import net.minecraft.class_11683;
import net.minecraft.class_11788;
import net.minecraft.class_11793;
import net.minecraft.class_12075;
import net.minecraft.class_12247;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_156;
import net.minecraft.class_1799;
import net.minecraft.class_1921;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3730;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_4618;
import net.minecraft.class_638;
import net.minecraft.class_742;
import net.minecraft.class_745;
import net.minecraft.class_761;
import net.minecraft.class_898;
import net.minecraft.class_9799;
import ru.decide.mixin.RenderLayerAccessor;
import ru.decide.mixin.RenderSetupAccessor;
import ru.decide.mixin.TextureSpecAccessor;
import ru.decide.mixin.WorldRendererAccessor;
import ru.decide.utils.render.NoopVertexConsumer;
import ru.decide.utils.render.killeffect.KillEffectMath;

@Environment(value=EnvType.CLIENT)
public final class SoulEffect {
    private static final int MAX_GHOSTS = 6;
    private static final float GHOST_PITCH = -12.0f;
    private static final float SPIN_MS = 650.0f;
    private static final SoulEffect INSTANCE = new SoulEffect();
    private static final class_11661 QUEUE = new class_11661();
    private static final class_11683 MODEL_RENDERER = new class_11683();
    private static final class_11793 PART_RENDERER = new class_11793();
    private static final class_4618 OUTLINE_SOURCE = new class_4618();
    private static final Function<class_2960, class_1921> GHOST_LAYERS = class_156.method_34866(SoulEffect::buildGhostLayer);
    private final GhostSource source = new GhostSource();
    private final Map<class_1921, class_2960> textureCache = new HashMap<class_1921, class_2960>();
    private final List<Ghost> ghosts = new ArrayList<Ghost>();
    private final List<class_1309> pending = new ArrayList<class_1309>();
    private static final class_4587 BLANK = new class_4587();

    private SoulEffect() {
    }

    public static SoulEffect get() {
        return INSTANCE;
    }

    public synchronized void queue(class_1309 entity) {
        if (entity == null) {
            return;
        }
        this.pending.add(entity);
    }

    public synchronized void clear() {
        this.ghosts.clear();
        this.pending.clear();
        QUEUE.method_72953();
    }

    public synchronized void render(class_4587 matrices, class_243 cam, Settings settings, float partialTick) {
        if (matrices == null || cam == null || settings == null) {
            return;
        }
        this.spawnPending();
        long now = System.currentTimeMillis();
        this.ghosts.removeIf(g -> now - g.bornMs >= settings.durationMs());
        if (this.ghosts.isEmpty()) {
            return;
        }
        class_310 client = class_310.method_1551();
        class_898 dispatcher = client.method_1561();
        class_761 worldRenderer = client.field_1769;
        if (dispatcher == null || worldRenderer == null) {
            return;
        }
        class_11658 worldRenderState = ((WorldRendererAccessor)worldRenderer).decide$worldRenderState();
        if (worldRenderState == null) {
            return;
        }
        class_12075 cameraState = worldRenderState.field_63082;
        if (cameraState == null) {
            return;
        }
        int color = settings.color();
        float lift = class_3532.method_15363((float)settings.brightness(), (float)0.0f, (float)1.0f);
        float tintR = (float)(color >> 16 & 0xFF) / 255.0f;
        float tintG = (float)(color >> 8 & 0xFF) / 255.0f;
        float tintB = (float)(color & 0xFF) / 255.0f;
        tintR += (1.0f - tintR) * lift;
        tintG += (1.0f - tintG) * lift;
        tintB += (1.0f - tintB) * lift;
        float transparency = class_3532.method_15363((float)settings.transparency(), (float)0.0f, (float)1.0f);
        boolean drew = false;
        for (Ghost ghost : this.ghosts) {
            float fadeOut;
            long age = now - ghost.bornMs;
            float t = class_3532.method_15363((float)((float)age / (float)settings.durationMs()), (float)0.0f, (float)1.0f);
            float fadeIn = KillEffectMath.smoothStep(0.0f, 0.08f, t);
            float alpha = (float)(color >>> 24 & 0xFF) / 255.0f * transparency * fadeIn * (fadeOut = 1.0f - KillEffectMath.smoothStep(0.5f, 1.0f, t));
            if (alpha <= 0.004f) continue;
            float rise = KillEffectMath.easeOutCubic(t) * settings.rise();
            float spin = settings.rotate360() ? 360.0f * KillEffectMath.easeOutCubic(class_3532.method_15363((float)((float)age / 650.0f), (float)0.0f, (float)1.0f)) : 0.0f;
            class_1297 entity = ghost.entity;
            double wx = ghost.origin.field_1352;
            double wy = ghost.origin.field_1351 + (double)rise;
            double wz = ghost.origin.field_1350;
            float yaw = ghost.yaw + spin;
            entity.method_5808(wx, wy, wz, yaw, -12.0f);
            entity.field_6038 = wx;
            entity.field_5971 = wy;
            entity.field_5989 = wz;
            entity.field_5982 = yaw;
            entity.field_6004 = -12.0f;
            entity.method_5847(yaw);
            entity.method_5636(yaw);
            if (entity instanceof class_1309) {
                class_1309 living = (class_1309)entity;
                living.field_6259 = yaw;
                living.field_6220 = yaw;
            }
            this.source.setTint(tintR, tintG, tintB, alpha);
            this.source.setPlacement(matrices.method_23760(), (float)(wx - cam.field_1352), (float)(wy - cam.field_1351), (float)(wz - cam.field_1350));
            drew |= this.drawGhost(dispatcher, cameraState, entity, partialTick, 0.0, 0.0, 0.0);
        }
        if (drew) {
            this.source.method_22993();
        }
    }

    private boolean drawGhost(class_898 dispatcher, class_12075 cameraState, class_1297 ghost, float partialTick, double x, double y, double z) {
        try {
            class_10017 state = dispatcher.method_72977(ghost, partialTick);
            if (state == null || state.field_53333) {
                return false;
            }
            state.field_61821 = 0;
            state.field_53337 = null;
            state.field_61823.clear();
            QUEUE.method_72953();
            dispatcher.method_72976(state, cameraState, x, y, z, BLANK, (class_11659)QUEUE);
            int queues = 0;
            for (class_11788 queue : QUEUE.method_73532().values()) {
                ++queues;
                MODEL_RENDERER.method_73001(queue, (class_4597.class_4598)this.source, OUTLINE_SOURCE, (class_4597.class_4598)this.source);
                PART_RENDERER.method_73543(queue, (class_4597.class_4598)this.source, OUTLINE_SOURCE, (class_4597.class_4598)this.source);
            }
            QUEUE.method_72953();
            return queues > 0;
        }
        catch (Throwable ignored) {
            QUEUE.method_72953();
            return false;
        }
    }

    private void spawnPending() {
        if (this.pending.isEmpty()) {
            return;
        }
        ArrayList<class_1309> due = new ArrayList<class_1309>(this.pending);
        this.pending.clear();
        class_638 world = class_310.method_1551().field_1687;
        if (world == null) {
            return;
        }
        for (class_1309 entity : due) {
            if (this.ghosts.size() >= 6) break;
            class_1297 ghost = this.createGhost(world, entity);
            if (ghost == null) continue;
            this.ghosts.add(new Ghost(ghost, new class_243(entity.method_23317(), entity.method_23318(), entity.method_23321()), entity.method_36454(), System.currentTimeMillis()));
        }
    }

    private class_1297 createGhost(class_638 world, class_1309 source) {
        try {
            if (source instanceof class_742) {
                class_742 player = (class_742)source;
                class_745 ghost = new class_745(world, player.method_7334());
                ghost.method_5808(source.method_23317(), source.method_23318(), source.method_23321(), source.method_36454(), source.method_36455());
                ghost.method_5847(source.field_6241);
                ghost.method_5636(source.field_6283);
                ghost.method_18380(source.method_18376());
                ghost.method_5660(source.method_5715());
                ghost.method_24830(true);
                ghost.method_5803(true);
                SoulEffect.clearEquipment((class_1309)ghost);
                return ghost;
            }
            class_1297 ghost = source.method_5864().method_5883((class_1937)world, class_3730.field_16462);
            if (ghost == null) {
                return null;
            }
            ghost.method_5878((class_1297)source);
            ghost.method_5808(source.method_23317(), source.method_23318(), source.method_23321(), source.method_36454(), source.method_36455());
            ghost.method_5847(source.field_6241);
            ghost.method_5636(source.field_6283);
            ghost.method_18380(source.method_18376());
            return ghost;
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static void clearEquipment(class_1309 entity) {
        for (class_1304 slot : class_1304.values()) {
            entity.method_5673(slot, class_1799.field_8037);
        }
    }

    private static class_1921 buildGhostLayer(class_2960 texture) {
        class_12247 setup = class_12247.method_75927((RenderPipeline)class_10799.field_56907).method_75934("Sampler0", texture).method_75937().method_75929(32768).method_75938();
        return class_1921.method_75940((String)("kill_effect_soul_" + texture.method_12832().replace('/', '_')), (class_12247)setup);
    }

    @Environment(value=EnvType.CLIENT)
    private final class GhostSource
    extends class_4597.class_4598 {
        private float tintR;
        private float tintG;
        private float tintB;
        private float alpha;
        private class_4587.class_4665 pose;
        private float placeX;
        private float placeY;
        private float placeZ;
        private final TintedConsumer tinted;

        GhostSource() {
            super(new class_9799(65536), (SequencedMap)Object2ObjectSortedMaps.emptyMap());
            this.tintR = 1.0f;
            this.tintG = 1.0f;
            this.tintB = 1.0f;
            this.alpha = 1.0f;
            this.tinted = new TintedConsumer();
        }

        void setTint(float r, float g, float b, float a) {
            this.tintR = r;
            this.tintG = g;
            this.tintB = b;
            this.alpha = a;
            this.tinted.inner = null;
        }

        void setPlacement(class_4587.class_4665 pose, double x, double y, double z) {
            this.pose = pose;
            this.placeX = (float)x;
            this.placeY = (float)y;
            this.placeZ = (float)z;
        }

        public class_4588 method_73477(class_1921 renderType) {
            class_2960 texture = this.textureOf(renderType);
            if (texture == null) {
                return NoopVertexConsumer.INSTANCE;
            }
            this.tinted.inner = super.method_73477(GHOST_LAYERS.apply(texture));
            this.tinted.tintR = this.tintR;
            this.tinted.tintG = this.tintG;
            this.tinted.tintB = this.tintB;
            this.tinted.alpha = this.alpha;
            this.tinted.pose = this.pose;
            this.tinted.placeX = this.placeX;
            this.tinted.placeY = this.placeY;
            this.tinted.placeZ = this.placeZ;
            return this.tinted;
        }

        private class_2960 textureOf(class_1921 type) {
            class_2960 cached = SoulEffect.this.textureCache.get(type);
            if (cached != null) {
                return cached;
            }
            if (SoulEffect.this.textureCache.size() > 512) {
                SoulEffect.this.textureCache.clear();
            }
            class_2960 resolved = this.resolveTexture(type);
            SoulEffect.this.textureCache.put(type, resolved);
            return resolved;
        }

        private class_2960 resolveTexture(class_1921 type) {
            try {
                class_12247 setup = ((RenderLayerAccessor)type).decide$getRenderSetup();
                if (setup == null) {
                    return null;
                }
                Map<String, Object> textures = ((RenderSetupAccessor)setup).decide$getTextures();
                if (textures == null) {
                    return null;
                }
                Object spec = textures.get("Sampler0");
                if (!(spec instanceof TextureSpecAccessor)) {
                    return null;
                }
                TextureSpecAccessor accessor = (TextureSpecAccessor)spec;
                class_2960 location = accessor.decide$location();
                if (location == null) {
                    return null;
                }
                if (location.method_12832().contains("glint")) {
                    return null;
                }
                return location;
            }
            catch (Throwable ignored) {
                return null;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Settings(float rise, boolean rotate360, int color, long durationMs, float transparency, float brightness) {
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Ghost {
        final class_1297 entity;
        final class_243 origin;
        final float yaw;
        final long bornMs;

        Ghost(class_1297 entity, class_243 origin, float yaw, long bornMs) {
            this.entity = entity;
            this.origin = origin;
            this.yaw = yaw;
            this.bornMs = bornMs;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class TintedConsumer
    implements class_4588 {
        class_4587.class_4665 pose;
        class_4588 inner;
        float tintR = 1.0f;
        float tintG = 1.0f;
        float tintB = 1.0f;
        float alpha = 1.0f;
        float placeX;
        float placeY;
        float placeZ;

        private TintedConsumer() {
        }

        public class_4588 method_22912(float x, float y, float z) {
            this.inner.method_56824(this.pose, x + this.placeX, y + this.placeY, z + this.placeZ);
            return this;
        }

        public class_4588 method_1336(int r, int g, int b, int a) {
            this.inner.method_1336(Math.round((float)r * this.tintR), Math.round((float)g * this.tintG), Math.round((float)b * this.tintB), Math.round((float)a * this.alpha));
            return this;
        }

        public class_4588 method_39415(int argb) {
            this.method_1336(argb >> 16 & 0xFF, argb >> 8 & 0xFF, argb & 0xFF, argb >>> 24 & 0xFF);
            return this;
        }

        public class_4588 method_22913(float u, float v) {
            this.inner.method_22913(u, v);
            return this;
        }

        public class_4588 method_60796(int u, int v) {
            this.inner.method_22922(class_4608.field_21444);
            return this;
        }

        public class_4588 method_22921(int u, int v) {
            this.inner.method_22921(240, 240);
            return this;
        }

        public class_4588 method_22914(float x, float y, float z) {
            this.inner.method_22914(x, y, z);
            return this;
        }

        public class_4588 method_75298(float width) {
            this.inner.method_75298(width);
            return this;
        }
    }
}

