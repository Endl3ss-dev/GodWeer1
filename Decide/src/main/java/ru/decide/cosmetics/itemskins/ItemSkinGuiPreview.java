/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3298
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.cosmetics.itemskins;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.cosmetics.itemskins.ItemSkin;

@Environment(value=EnvType.CLIENT)
public final class ItemSkinGuiPreview {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"DecideVisuals:ItemSkinPreview");
    private static final ModelCache CACHE = new ModelCache();

    private ItemSkinGuiPreview() {
    }

    public static boolean available(ItemSkin skin) {
        return skin != null && CACHE.get(ItemSkinGuiPreview.modelName(skin)) != null;
    }

    public static float[] bounds(ItemSkin skin) {
        float[] fArray;
        BakedModel model = CACHE.get(ItemSkinGuiPreview.modelName(skin));
        if (model == null) {
            fArray = null;
        } else {
            float[] fArray2 = new float[4];
            fArray2[0] = model.cx;
            fArray2[1] = model.cy;
            fArray2[2] = model.cz;
            fArray = fArray2;
            fArray2[3] = model.maxDim;
        }
        return fArray;
    }

    public static class_2960 texture(ItemSkin skin) {
        BakedModel model = CACHE.get(ItemSkinGuiPreview.modelName(skin));
        return model == null ? skin.getIcon() : model.texture;
    }

    public static int emit(ItemSkin skin, Matrix4f base, Sink sink) {
        BakedModel model = CACHE.get(ItemSkinGuiPreview.modelName(skin));
        if (model == null) {
            return 0;
        }
        int written = 0;
        Vector3f p = new Vector3f();
        for (Quad q : model.quads) {
            written += ItemSkinGuiPreview.emitVertex(q, 0, base, sink, p);
            written += ItemSkinGuiPreview.emitVertex(q, 1, base, sink, p);
            written += ItemSkinGuiPreview.emitVertex(q, 2, base, sink, p);
            written += ItemSkinGuiPreview.emitVertex(q, 0, base, sink, p);
            written += ItemSkinGuiPreview.emitVertex(q, 2, base, sink, p);
            written += ItemSkinGuiPreview.emitVertex(q, 3, base, sink, p);
        }
        return written;
    }

    private static int emitVertex(Quad q, int i, Matrix4f base, Sink sink, Vector3f p) {
        p.set(q.x[i], q.y[i], q.z[i]);
        base.transformPosition(p);
        sink.vertex(p.x, p.y, p.z, q.u[i], q.v[i]);
        return 1;
    }

    private static String modelName(ItemSkin skin) {
        String key = skin.getKey();
        long now = System.currentTimeMillis();
        if ("bow".equals(skin.getKind().type())) {
            long phase = now / 950L % 6L;
            return key + (switch ((int)phase) {
                case 1 -> "_pull1";
                case 2, 3 -> "_pull2";
                default -> "_idle";
            });
        }
        if ("crossbow".equals(skin.getKind().type())) {
            long phase = now / 1000L % 8L;
            return key + (switch ((int)phase) {
                case 1 -> "_pull0";
                case 2 -> "_pull1";
                case 3, 4 -> "_pull2";
                case 5 -> "_arrow";
                case 6 -> "_firework";
                default -> "_idle";
            });
        }
        if ("golden_apple".equals(skin.getKind().type()) || "enchanted_golden_apple".equals(skin.getKind().type())) {
            long phase = now / 1200L % 6L;
            return key + (switch ((int)phase) {
                case 1 -> "_bite1";
                case 2, 3 -> "_bite2";
                case 4, 5 -> "_bite3";
                default -> "_whole";
            });
        }
        return key;
    }

    private static class_2960 textureId(String ref) {
        class_2960 raw = class_2960.method_12829((String)ref);
        if (raw == null) {
            return class_2960.method_60655((String)"minecraft", (String)"textures/misc/missingno.png");
        }
        String path = raw.method_12832();
        if (path.startsWith("textures/")) {
            return class_2960.method_60655((String)raw.method_12836(), (String)(path.endsWith(".png") ? path : path + ".png"));
        }
        return class_2960.method_60655((String)raw.method_12836(), (String)("textures/" + path + ".png"));
    }

    private static BakedModel bake(JsonArray elements, class_2960 texture) {
        ArrayList<Quad> quads = new ArrayList<Quad>(elements.size() * 6);
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE;
        float maxX = -3.4028235E38f;
        float maxY = -3.4028235E38f;
        float maxZ = -3.4028235E38f;
        Vector3f p = new Vector3f();
        for (JsonElement element2 : elements) {
            JsonObject e = element2.getAsJsonObject();
            float[] from = ItemSkinGuiPreview.read3(e.getAsJsonArray("from"));
            float[] to = ItemSkinGuiPreview.read3(e.getAsJsonArray("to"));
            float[] origin = new float[]{8.0f, 8.0f, 8.0f};
            String axis = "z";
            float angle = 0.0f;
            if (e.has("rotation")) {
                JsonObject r = e.getAsJsonObject("rotation");
                if (r.has("origin")) {
                    origin = ItemSkinGuiPreview.read3(r.getAsJsonArray("origin"));
                }
                if (r.has("axis")) {
                    axis = r.get("axis").getAsString();
                }
                if (r.has("angle")) {
                    angle = r.get("angle").getAsFloat();
                }
            }
            Quaternionf rot = ItemSkinGuiPreview.rotation(axis, angle);
            JsonObject faces = e.getAsJsonObject("faces");
            if (faces == null) continue;
            for (String faceName : new String[]{"north", "south", "east", "west", "up", "down"}) {
                if (!faces.has(faceName)) continue;
                JsonObject face = faces.getAsJsonObject(faceName);
                float[] uv = ItemSkinGuiPreview.read4(face.getAsJsonArray("uv"));
                Quad q = ItemSkinGuiPreview.quad(faceName, from, to, origin, rot, uv);
                quads.add(q);
                for (int i = 0; i < 4; ++i) {
                    minX = Math.min(minX, q.x[i]);
                    maxX = Math.max(maxX, q.x[i]);
                    minY = Math.min(minY, q.y[i]);
                    maxY = Math.max(maxY, q.y[i]);
                    minZ = Math.min(minZ, q.z[i]);
                    maxZ = Math.max(maxZ, q.z[i]);
                }
            }
        }
        if (quads.isEmpty()) {
            return null;
        }
        float cx = (minX + maxX) * 0.5f;
        float cy = (minY + maxY) * 0.5f;
        float cz = (minZ + maxZ) * 0.5f;
        float maxDim = Math.max(0.01f, Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ)));
        return new BakedModel(quads.toArray(new Quad[0]), texture, cx, cy, cz, maxDim);
    }

    private static Quad quad(String face, float[] a, float[] b, float[] o, Quaternionf rot, float[] uv) {
        float[][] fArrayArray;
        switch (face) {
            case "north": {
                float[][] fArrayArray2 = new float[4][];
                fArrayArray2[0] = new float[]{a[0], a[1], a[2]};
                fArrayArray2[1] = new float[]{b[0], a[1], a[2]};
                fArrayArray2[2] = new float[]{b[0], b[1], a[2]};
                fArrayArray = fArrayArray2;
                fArrayArray2[3] = new float[]{a[0], b[1], a[2]};
                break;
            }
            case "south": {
                float[][] fArrayArray3 = new float[4][];
                fArrayArray3[0] = new float[]{b[0], a[1], b[2]};
                fArrayArray3[1] = new float[]{a[0], a[1], b[2]};
                fArrayArray3[2] = new float[]{a[0], b[1], b[2]};
                fArrayArray = fArrayArray3;
                fArrayArray3[3] = new float[]{b[0], b[1], b[2]};
                break;
            }
            case "west": {
                float[][] fArrayArray4 = new float[4][];
                fArrayArray4[0] = new float[]{a[0], a[1], b[2]};
                fArrayArray4[1] = new float[]{a[0], a[1], a[2]};
                fArrayArray4[2] = new float[]{a[0], b[1], a[2]};
                fArrayArray = fArrayArray4;
                fArrayArray4[3] = new float[]{a[0], b[1], b[2]};
                break;
            }
            case "east": {
                float[][] fArrayArray5 = new float[4][];
                fArrayArray5[0] = new float[]{b[0], a[1], a[2]};
                fArrayArray5[1] = new float[]{b[0], a[1], b[2]};
                fArrayArray5[2] = new float[]{b[0], b[1], b[2]};
                fArrayArray = fArrayArray5;
                fArrayArray5[3] = new float[]{b[0], b[1], a[2]};
                break;
            }
            case "up": {
                float[][] fArrayArray6 = new float[4][];
                fArrayArray6[0] = new float[]{a[0], b[1], b[2]};
                fArrayArray6[1] = new float[]{b[0], b[1], b[2]};
                fArrayArray6[2] = new float[]{b[0], b[1], a[2]};
                fArrayArray = fArrayArray6;
                fArrayArray6[3] = new float[]{a[0], b[1], a[2]};
                break;
            }
            default: {
                float[][] fArrayArray7 = new float[4][];
                fArrayArray7[0] = new float[]{a[0], a[1], a[2]};
                fArrayArray7[1] = new float[]{b[0], a[1], a[2]};
                fArrayArray7[2] = new float[]{b[0], a[1], b[2]};
                fArrayArray = fArrayArray7;
                fArrayArray7[3] = new float[]{a[0], a[1], b[2]};
            }
        }
        float[][] c = fArrayArray;
        float[] xs = new float[4];
        float[] ys = new float[4];
        float[] zs = new float[4];
        Vector3f v = new Vector3f();
        for (int i = 0; i < 4; ++i) {
            v.set(c[i][0] - o[0], c[i][1] - o[1], c[i][2] - o[2]);
            rot.transform(v);
            xs[i] = v.x + o[0];
            ys[i] = v.y + o[1];
            zs[i] = v.z + o[2];
        }
        float[] us = new float[]{uv[0] / 16.0f, uv[2] / 16.0f, uv[2] / 16.0f, uv[0] / 16.0f};
        float[] vs = new float[]{uv[1] / 16.0f, uv[1] / 16.0f, uv[3] / 16.0f, uv[3] / 16.0f};
        return new Quad(xs, ys, zs, us, vs);
    }

    private static Quaternionf rotation(String axis, float degrees) {
        float r = (float)Math.toRadians(degrees);
        return switch (axis) {
            case "x" -> new Quaternionf((float)Math.sin(r / 2.0f), 0.0f, 0.0f, (float)Math.cos(r / 2.0f));
            case "y" -> new Quaternionf(0.0f, (float)Math.sin(r / 2.0f), 0.0f, (float)Math.cos(r / 2.0f));
            default -> new Quaternionf(0.0f, 0.0f, (float)Math.sin(r / 2.0f), (float)Math.cos(r / 2.0f));
        };
    }

    private static float[] read3(JsonArray a) {
        return new float[]{a.get(0).getAsFloat(), a.get(1).getAsFloat(), a.get(2).getAsFloat()};
    }

    private static float[] read4(JsonArray a) {
        return new float[]{a.get(0).getAsFloat(), a.get(1).getAsFloat(), a.get(2).getAsFloat(), a.get(3).getAsFloat()};
    }

    @Environment(value=EnvType.CLIENT)
    private static final class ModelCache {
        private final Map<String, BakedModel> cache = new HashMap<String, BakedModel>();

        private ModelCache() {
        }

        BakedModel get(String name) {
            BakedModel model = this.cache.get(name);
            if (model == null) {
                model = this.load(name);
                this.cache.put(name, model == null ? BakedModel.MISSING : model);
            }
            return model == BakedModel.MISSING ? null : model;
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        private BakedModel load(String name) {
            class_2960 id = class_2960.method_60655((String)"decide", (String)("models/item_skin/" + name + ".json"));
            try (InputStream in = ((class_3298)class_310.method_1551().method_1478().method_14486(id).orElseThrow()).method_14482();){
                JsonObject root = JsonParser.parseString((String)new String(in.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
                JsonObject textures = root.getAsJsonObject("textures");
                String tex = textures != null && textures.has("0") ? textures.get("0").getAsString() : "decide:item/item_skin/" + name;
                class_2960 texId = ItemSkinGuiPreview.textureId(tex);
                JsonArray elements = root.getAsJsonArray("elements");
                if (elements == null) {
                    BakedModel bakedModel2 = null;
                    return bakedModel2;
                }
                BakedModel bakedModel = ItemSkinGuiPreview.bake(elements, texId);
                return bakedModel;
            }
            catch (Throwable t) {
                LOGGER.warn("Failed to load item skin preview model {}", (Object)id, (Object)t);
                return null;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record BakedModel(Quad[] quads, class_2960 texture, float cx, float cy, float cz, float maxDim) {
        static final BakedModel MISSING = new BakedModel(new Quad[0], class_2960.method_60655((String)"minecraft", (String)"textures/misc/missingno.png"), 8.0f, 8.0f, 8.0f, 16.0f);
    }

    @Environment(value=EnvType.CLIENT)
    private record Quad(float[] x, float[] y, float[] z, float[] u, float[] v) {
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Sink {
        public void vertex(float var1, float var2, float var3, float var4, float var5);
    }
}

