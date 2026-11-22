/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1011
 *  net.minecraft.class_10444
 *  net.minecraft.class_10444$class_10446
 *  net.minecraft.class_1058
 *  net.minecraft.class_1799
 *  net.minecraft.class_1844
 *  net.minecraft.class_1937
 *  net.minecraft.class_310
 *  net.minecraft.class_7764
 *  net.minecraft.class_777
 *  net.minecraft.class_811
 *  net.minecraft.class_9282
 *  net.minecraft.class_9334
 *  net.minecraft.class_9848
 */
package ru.decide.module.impl.render.lootview;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_10444;
import net.minecraft.class_1058;
import net.minecraft.class_1799;
import net.minecraft.class_1844;
import net.minecraft.class_1937;
import net.minecraft.class_310;
import net.minecraft.class_7764;
import net.minecraft.class_777;
import net.minecraft.class_811;
import net.minecraft.class_9282;
import net.minecraft.class_9334;
import net.minecraft.class_9848;
import ru.decide.mixin.ItemLayerRenderStateAccessor;
import ru.decide.mixin.ItemRenderStateAccessor;
import ru.decide.mixin.SpriteContentsAccessor;
import ru.decide.module.impl.render.lootview.LootShape;

@Environment(value=EnvType.CLIENT)
public final class LootViewShapes {
    private static final int GRID = 28;
    private static final float CELL_SIZE = 0.017857144f;
    private static final float HALF = 14.0f;
    private static final Map<Long, LootShape> cache = new HashMap<Long, LootShape>();

    private LootViewShapes() {
    }

    public static LootShape get(class_1799 stack, int points) {
        long key = LootViewShapes.cacheKey(stack, points);
        LootShape cached = cache.get(key);
        if (cached != null) {
            return cached;
        }
        LootShape shape = null;
        try {
            shape = LootViewShapes.build(stack, points);
        }
        catch (RuntimeException ignored) {
            shape = null;
        }
        if (shape == null) {
            shape = LootViewShapes.fallback(points, -1);
        }
        cache.put(key, shape);
        return shape;
    }

    public static void clear() {
        cache.clear();
    }

    private static long cacheKey(class_1799 stack, int points) {
        class_1844 potion = (class_1844)stack.method_58694(class_9334.field_49651);
        class_9282 dyed = (class_9282)stack.method_58694(class_9334.field_49644);
        int disc = potion != null ? potion.method_8064() : 0;
        disc = disc * 31 + (dyed != null ? dyed.comp_2384() : 0);
        return (long)System.identityHashCode(stack.method_7909()) * -7046029254386353131L ^ ((long)disc & 0xFFFFFFFFL) << 8 ^ (long)points;
    }

    private static LootShape build(class_1799 stack, int points) {
        class_310 mc = class_310.method_1551();
        class_10444 state = new class_10444();
        mc.method_65386().method_65598(state, stack, class_811.field_4318, (class_1937)mc.field_1687, null, 0);
        ItemRenderStateAccessor stateAccessor = (ItemRenderStateAccessor)state;
        class_10444.class_10446[] layers = stateAccessor.decide$layers();
        if (layers == null) {
            return null;
        }
        int layerCount = Math.min(stateAccessor.decide$layerCount(), layers.length);
        boolean[] mask = new boolean[784];
        float[] bucketW = new float[4096];
        float[] bucketR = new float[4096];
        float[] bucketG = new float[4096];
        float[] bucketB = new float[4096];
        HashSet<class_1058> seenSprites = new HashSet<class_1058>();
        boolean sampled = false;
        for (int i = 0; i < layerCount; ++i) {
            List quads;
            ItemLayerRenderStateAccessor layerAccessor;
            class_10444.class_10446 layer = layers[i];
            if (layer == null || (layerAccessor = (ItemLayerRenderStateAccessor)layer).decide$specialModelType() != null || (quads = layer.method_67997()) == null || quads.isEmpty()) continue;
            int[] tints = layerAccessor.decide$tints();
            for (class_777 quad : quads) {
                int tint;
                class_1058 sprite = quad.comp_3724();
                if (sprite == null || !seenSprites.add(sprite)) continue;
                int tintIndex = quad.comp_3722();
                int n = tint = tints != null && tintIndex >= 0 && tintIndex < tints.length ? tints[tintIndex] : -1;
                if (!LootViewShapes.sampleSprite(sprite, tint, mask, bucketW, bucketR, bucketG, bucketB)) continue;
                sampled = true;
            }
        }
        if (!sampled) {
            return null;
        }
        return LootViewShapes.scatter(mask, points, LootViewShapes.dominantColor(bucketW, bucketR, bucketG, bucketB));
    }

    private static LootShape scatter(boolean[] mask, int points, int color) {
        ArrayList<Integer> cells = new ArrayList<Integer>(196);
        for (int y = 0; y < 28; ++y) {
            for (int x = 0; x < 28; ++x) {
                if (!mask[y * 28 + x]) continue;
                cells.add(x << 8 | y);
            }
        }
        if (cells.size() < 3) {
            return null;
        }
        cells.sort((a, b) -> {
            int ax = a >> 8;
            int ay = a & 0xFF;
            int bx = b >> 8;
            int by = b & 0xFF;
            int h1 = ax * 73856093 ^ ay * 19349663 ^ (ax + 7) * (ay + 13) * 83492791;
            int h2 = bx * 73856093 ^ by * 19349663 ^ (bx + 7) * (by + 13) * 83492791;
            return Integer.compare(h1, h2);
        });
        int count = Math.min(points, cells.size());
        float[] xs = new float[count];
        float[] zs = new float[count];
        float[] jitter = new float[count];
        for (int k = 0; k < count; ++k) {
            int cell = (Integer)cells.get(k * cells.size() / count);
            int x = cell >> 8;
            int y = cell & 0xFF;
            xs[k] = ((float)x + 0.5f - 14.0f) * 0.017857144f;
            zs[k] = (27.5f - (float)y - 14.0f) * 0.017857144f;
            jitter[k] = 0.78f + 0.44f * LootViewShapes.fract((float)Math.sin((float)k * 12.9898f) * 43758.547f);
        }
        return new LootShape(xs, zs, jitter, color, count);
    }

    private static boolean sampleSprite(class_1058 sprite, int tint, boolean[] mask, float[] bucketW, float[] bucketR, float[] bucketG, float[] bucketB) {
        class_7764 contents = sprite.method_45851();
        SpriteContentsAccessor contentsAccessor = (SpriteContentsAccessor)contents;
        class_1011 image = contentsAccessor.decide$image();
        if (image == null) {
            return false;
        }
        int frameW = Math.min(contentsAccessor.decide$width(), image.method_4307());
        int frameH = Math.min(contentsAccessor.decide$height(), image.method_4323());
        if (frameW <= 0 || frameH <= 0) {
            return false;
        }
        int tintR = tint == -1 ? 255 : class_9848.method_61327((int)tint);
        int tintG = tint == -1 ? 255 : class_9848.method_61329((int)tint);
        int tintB = tint == -1 ? 255 : class_9848.method_61331((int)tint);
        boolean any = false;
        for (int gy = 0; gy < 28; ++gy) {
            int sy = gy * frameH / 28;
            for (int gx = 0; gx < 28; ++gx) {
                int bucket;
                int sx = gx * frameW / 28;
                int pixel = image.method_61940(sx, sy);
                int alpha = class_9848.method_61320((int)pixel);
                if (alpha < 40) continue;
                any = true;
                if (alpha >= 96) {
                    mask[gy * 28 + gx] = true;
                }
                int r = class_9848.method_61327((int)pixel) * tintR / 255;
                int g = class_9848.method_61329((int)pixel) * tintG / 255;
                int b = class_9848.method_61331((int)pixel) * tintB / 255;
                int maxC = Math.max(r, Math.max(g, b));
                int minC = Math.min(r, Math.min(g, b));
                float saturation = maxC > 0 ? (float)(maxC - minC) / (float)maxC : 0.0f;
                float weight = (float)alpha / 255.0f * (0.15f + saturation) * (0.2f + 0.8f * (float)maxC / 255.0f);
                if (weight <= 0.0f) continue;
                int n = bucket = r >> 4 << 8 | g >> 4 << 4 | b >> 4;
                bucketW[n] = bucketW[n] + weight;
                int n2 = bucket;
                bucketR[n2] = bucketR[n2] + (float)r * weight;
                int n3 = bucket;
                bucketG[n3] = bucketG[n3] + (float)g * weight;
                int n4 = bucket;
                bucketB[n4] = bucketB[n4] + (float)b * weight;
            }
        }
        return any;
    }

    private static int dominantColor(float[] bucketW, float[] bucketR, float[] bucketG, float[] bucketB) {
        int best = -1;
        float bestW = 0.0f;
        for (int i = 0; i < 4096; ++i) {
            if (!(bucketW[i] > bestW)) continue;
            bestW = bucketW[i];
            best = i;
        }
        if (best < 0 || bestW <= 0.0f) {
            return -1;
        }
        int r = LootViewShapes.clamp255((int)(bucketR[best] / bestW));
        int g = LootViewShapes.clamp255((int)(bucketG[best] / bestW));
        int b = LootViewShapes.clamp255((int)(bucketB[best] / bestW));
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        float saturation = Math.min(1.0f, hsb[1] * 1.2f);
        float brightness = Math.max(0.55f, hsb[2]);
        return Color.HSBtoRGB(hsb[0], saturation, brightness) | 0xFF000000;
    }

    private static int clamp255(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private static LootShape fallback(int points, int color) {
        float[] xs = new float[points];
        float[] zs = new float[points];
        float[] jitter = new float[points];
        for (int i = 0; i < points; ++i) {
            float angle = (float)Math.PI * 2 * (float)i / (float)points;
            xs[i] = (float)Math.cos(angle) * 0.5f * 0.4f;
            zs[i] = (float)Math.sin(angle) * 0.5f * 0.4f;
            jitter[i] = 0.78f + 0.44f * LootViewShapes.fract((float)Math.sin((float)i * 12.9898f) * 43758.547f);
        }
        return new LootShape(xs, zs, jitter, color, points);
    }

    private static float fract(float value) {
        return value - (float)Math.floor(value);
    }
}

