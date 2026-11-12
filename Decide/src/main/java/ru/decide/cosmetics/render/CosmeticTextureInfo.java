/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1011
 *  net.minecraft.class_2960
 */
package ru.decide.cosmetics.render;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_2960;

@Environment(value=EnvType.CLIENT)
public final class CosmeticTextureInfo {
    private static final int ALPHA_THRESHOLD = 24;
    private static final int[] UNKNOWN = new int[]{0, 0};
    private static final float[] FULL = new float[]{0.0f, 0.0f, 1.0f, 1.0f};
    private static final Map<class_2960, int[]> SIZES = new ConcurrentHashMap<class_2960, int[]>();
    private static final Map<class_2960, float[]> BOUNDS = new ConcurrentHashMap<class_2960, float[]>();

    public static int width(class_2960 id) {
        return CosmeticTextureInfo.size(id)[0];
    }

    public static int height(class_2960 id) {
        return CosmeticTextureInfo.size(id)[1];
    }

    public static int[] size(class_2960 id) {
        return SIZES.computeIfAbsent(id, CosmeticTextureInfo::readSize);
    }

    public static float[] contentBounds(class_2960 id) {
        return BOUNDS.computeIfAbsent(id, CosmeticTextureInfo::readBounds);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static int[] readSize(class_2960 id) {
        try (InputStream in = CosmeticTextureInfo.open(id);){
            if (in == null) {
                int[] nArray = UNKNOWN;
                return nArray;
            }
            byte[] header = in.readNBytes(24);
            if (header.length < 24) {
                int[] nArray = UNKNOWN;
                return nArray;
            }
            int width = CosmeticTextureInfo.readInt(header, 16);
            int height = CosmeticTextureInfo.readInt(header, 20);
            if (width <= 0 || height <= 0) {
                int[] nArray = UNKNOWN;
                return nArray;
            }
            int[] nArray = new int[]{width, height};
            return nArray;
        }
        catch (IOException | RuntimeException e) {
            return UNKNOWN;
        }
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private static float[] readBounds(class_2960 id) {
        int[] size = CosmeticTextureInfo.size(id);
        if (size[0] <= 0 || size[1] <= 0) {
            return null;
        }
        try (InputStream in = CosmeticTextureInfo.open(id);){
            float[] fArray;
            block26: {
                int maxY;
                int minY;
                int maxX;
                int minX;
                int h;
                int w;
                class_1011 image;
                block24: {
                    float[] fArray2;
                    block25: {
                        if (in == null) {
                            float[] fArray3 = null;
                            return fArray3;
                        }
                        byte[] data = in.readAllBytes();
                        image = class_1011.method_49277((byte[])data);
                        try {
                            w = image.method_4307();
                            h = image.method_4323();
                            minX = w;
                            maxX = -1;
                            minY = h;
                            maxY = -1;
                            int step = Math.max(1, Math.min(w, h) / 256);
                            for (int y = 0; y < h; y += step) {
                                for (int x = 0; x < w; x += step) {
                                    if (image.method_61940(x, y) >>> 24 <= 24) continue;
                                    if (x < minX) {
                                        minX = x;
                                    }
                                    if (x > maxX) {
                                        maxX = x;
                                    }
                                    if (y < minY) {
                                        minY = y;
                                    }
                                    if (y <= maxY) continue;
                                    maxY = y;
                                }
                            }
                            if (maxX >= minX && maxY >= minY) break block24;
                            fArray2 = FULL;
                            if (image == null) break block25;
                        }
                        catch (Throwable throwable) {
                            if (image != null) {
                                try {
                                    image.close();
                                }
                                catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        }
                        image.close();
                    }
                    return fArray2;
                }
                fArray = new float[]{(float)minX / (float)w, (float)minY / (float)h, (float)(maxX + 1) / (float)w, (float)(maxY + 1) / (float)h};
                if (image == null) break block26;
                image.close();
            }
            return fArray;
        }
        catch (Exception e) {
            return null;
        }
    }

    private static InputStream open(class_2960 id) {
        return CosmeticTextureInfo.class.getClassLoader().getResourceAsStream("assets/" + id.method_12836() + "/" + id.method_12832());
    }

    private static int readInt(byte[] data, int offset) {
        return (data[offset] & 0xFF) << 24 | (data[offset + 1] & 0xFF) << 16 | (data[offset + 2] & 0xFF) << 8 | data[offset + 3] & 0xFF;
    }

    private CosmeticTextureInfo() {
    }
}

