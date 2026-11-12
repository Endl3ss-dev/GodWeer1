/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11659
 *  net.minecraft.class_12249
 *  net.minecraft.class_2960
 *  net.minecraft.class_4587
 *  net.minecraft.class_4608
 *  net.minecraft.class_591
 *  net.minecraft.class_630
 */
package ru.decide.cosmetics.maksai.hats;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11659;
import net.minecraft.class_12249;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import net.minecraft.class_591;
import net.minecraft.class_630;
import ru.decide.cosmetics.maksai.hats.HatManager;
import ru.decide.cosmetics.maksai.hats.TextureUvAnimation;

@Environment(value=EnvType.CLIENT)
public final class PartBindings {
    private final int[] parts;
    private final float[][] anchors;
    private final float[] offset;

    private PartBindings(int[] p, float[][] a, float[] o) {
        this.parts = p;
        this.anchors = a;
        this.offset = o;
    }

    public static PartBindings parse(JsonObject o, int count, String category) {
        JsonArray p = o.getAsJsonArray("bindings");
        JsonArray a = o.getAsJsonArray("anchors");
        if (p.size() == count && a.size() == 6) {
            int[] ids = new int[count];
            for (int i = 0; i < count; ++i) {
                ids[i] = p.get(i).getAsInt();
                if (ids[i] >= 0 && ids[i] < 6 && ids[i] == p.get(i / 4 * 4).getAsInt()) continue;
                throw new IllegalArgumentException("mixed quad");
            }
            boolean footwear = "shoes".equals(category);
            JsonArray verts = o.getAsJsonArray("vertices");
            if (verts != null && verts.size() == count) {
                int q = 0;
                while (q + 3 < count) {
                    int part = ids[q];
                    if (footwear || part == 4 || part == 5) {
                        float x = 0.0f;
                        for (int k = 0; k < 4; ++k) {
                            x += verts.get(q + k).getAsJsonArray().get(0).getAsFloat();
                        }
                        int leg = x <= 0.0f ? 4 : 5;
                        for (int k = 0; k < 4; ++k) {
                            ids[q + k] = leg;
                        }
                    }
                    q += 4;
                }
            }
            float[][] v = new float[6][3];
            for (int ix = 0; ix < 6; ++ix) {
                for (int j = 0; j < 3; ++j) {
                    v[ix][j] = a.get(ix).getAsJsonArray().get(j).getAsFloat();
                }
            }
            float[] off = new float[3];
            if (o.has("postOffset")) {
                for (int ix = 0; ix < 3; ++ix) {
                    off[ix] = o.getAsJsonArray("postOffset").get(ix).getAsFloat();
                }
            }
            return new PartBindings(ids, v, off);
        }
        throw new IllegalArgumentException("binding count");
    }

    public void submit(class_4587 stack, class_11659 queue, int light, class_591 model, HatManager.Equipped item) {
        float[] mesh = item.vertices();
        float[] frame = item.petMotion() != null && !item.petMotion().failed() ? item.petMotion().frame() : null;
        int[] bones = item.boneIndices();
        TextureUvAnimation.Uv uv = item.uvAnimation() == null ? null : item.uvAnimation().at((double)(System.nanoTime() - item.started()) * 1.0E-9);
        class_630[] modelParts = new class_630[]{model.field_3391, model.field_3398, model.field_3401, model.field_27433, model.field_3392, model.field_3397};
        for (int part = 0; part < 6; ++part) {
            boolean found = false;
            for (int n : this.parts) {
                found |= n == part;
            }
            if (!found) continue;
            int tag = part;
            stack.method_22903();
            modelParts[part].method_22703(stack);
            queue.method_73483(stack, class_12249.method_75994((class_2960)item.entry().texture()), (pose, vertices) -> {
                int nx = 0;
                for (int i = 0; i < mesh.length; i += 8) {
                    if (this.parts[nx] == tag) {
                        float len;
                        int k;
                        float x = mesh[i];
                        float y = mesh[i + 1];
                        float z = mesh[i + 2];
                        float nxx = mesh[i + 5];
                        float ny = mesh[i + 6];
                        float nz = mesh[i + 7];
                        if (frame != null && (k = bones[nx] * 12) + 11 < frame.length) {
                            float X = frame[k] * x + frame[k + 1] * y + frame[k + 2] * z + frame[k + 3];
                            float Y = frame[k + 4] * x + frame[k + 5] * y + frame[k + 6] * z + frame[k + 7];
                            float Z = frame[k + 8] * x + frame[k + 9] * y + frame[k + 10] * z + frame[k + 11];
                            float N = frame[k] * nxx + frame[k + 1] * ny + frame[k + 2] * nz;
                            float M = frame[k + 4] * nxx + frame[k + 5] * ny + frame[k + 6] * nz;
                            float L = frame[k + 8] * nxx + frame[k + 9] * ny + frame[k + 10] * nz;
                            x = X;
                            y = Y;
                            z = Z;
                            nxx = N;
                            ny = M;
                            nz = L;
                        }
                        if ((double)(len = (float)Math.sqrt(nxx * nxx + ny * ny + nz * nz)) > 1.0E-8) {
                            nxx /= len;
                            ny /= len;
                            nz /= len;
                        }
                        float u = mesh[i + 3];
                        float v = mesh[i + 4];
                        if (uv != null) {
                            u = uv.u(u);
                            v = uv.v(v);
                        }
                        vertices.method_56824(pose, x + this.offset[0] - this.anchors[tag][0], y + this.offset[1] - this.anchors[tag][1], z + this.offset[2] - this.anchors[tag][2]).method_39415(-1).method_22913(u, v).method_22922(class_4608.field_21444).method_60803(light).method_60831(pose, nxx, ny, nz);
                    }
                    ++nx;
                }
            });
            stack.method_22909();
        }
    }
}

