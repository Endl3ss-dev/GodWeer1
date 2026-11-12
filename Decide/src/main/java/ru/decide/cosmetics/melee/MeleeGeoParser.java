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
 */
package ru.decide.cosmetics.melee;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.cosmetics.melee.MeleeBone;
import ru.decide.cosmetics.melee.MeleeCube;
import ru.decide.cosmetics.melee.MeleeFace;
import ru.decide.cosmetics.melee.MeleeGeoModel;
import ru.decide.cosmetics.melee.MeleeVec3;
import ru.decide.cosmetics.melee.MeleeVertex;

@Environment(value=EnvType.CLIENT)
public final class MeleeGeoParser {
    private static final int FACE_CORNERS = 4;
    private static final int UV_QUARTER_TURN = 90;
    private static final int UV_FULL_TURN = 360;

    private MeleeGeoParser() {
    }

    public static MeleeGeoModel parse(String json) {
        try {
            return MeleeGeoParser.parse(JsonParser.parseString((String)json).getAsJsonObject());
        }
        catch (Exception e) {
            return null;
        }
    }

    private static MeleeGeoModel parse(JsonObject root) {
        JsonArray geometry = root.getAsJsonArray("minecraft:geometry");
        if (geometry == null || geometry.size() == 0) {
            return null;
        }
        JsonObject first = geometry.get(0).getAsJsonObject();
        JsonObject description = first.getAsJsonObject("description");
        int texW = description != null && description.has("texture_width") ? description.get("texture_width").getAsInt() : 64;
        int texH = description != null && description.has("texture_height") ? description.get("texture_height").getAsInt() : 64;
        MeleeGeoModel model = new MeleeGeoModel();
        model.textureWidth = texW;
        model.textureHeight = texH;
        JsonArray bones = first.getAsJsonArray("bones");
        if (bones != null) {
            List<float[]> claimed = MeleeGeoParser.collectFrontRects(bones);
            HashMap<String, MeleeBone> byName = new HashMap<String, MeleeBone>();
            for (JsonElement element2 : bones) {
                MeleeBone bone = MeleeGeoParser.parseBone(element2.getAsJsonObject(), texW, texH, claimed);
                byName.put(bone.name, bone);
            }
            for (JsonElement element2 : bones) {
                JsonObject object = element2.getAsJsonObject();
                MeleeBone bone = (MeleeBone)byName.get(object.get("name").getAsString());
                if (object.has("parent") && !object.get("parent").isJsonNull()) {
                    MeleeBone parent = (MeleeBone)byName.get(object.get("parent").getAsString());
                    if (parent != null) {
                        parent.children.add(bone);
                        bone.parent = parent;
                        continue;
                    }
                    model.roots.add(bone);
                    continue;
                }
                model.roots.add(bone);
            }
        }
        return model;
    }

    private static List<float[]> collectFrontRects(JsonArray bones) {
        ArrayList<float[]> rects = new ArrayList<float[]>();
        for (JsonElement boneElement : bones) {
            JsonObject bone = boneElement.getAsJsonObject();
            if (!bone.has("cubes")) continue;
            for (JsonElement cubeElement : bone.getAsJsonArray("cubes")) {
                JsonObject cube = cubeElement.getAsJsonObject();
                if (!cube.has("uv") || !cube.get("uv").isJsonArray()) continue;
                JsonArray uv = cube.getAsJsonArray("uv");
                float[] size = MeleeGeoParser.readVec(cube, "size", new float[]{1.0f, 1.0f, 1.0f});
                float inflate = cube.has("inflate") ? cube.get("inflate").getAsFloat() : 0.0f;
                float sx = size[0] + inflate * 2.0f;
                float sy = size[1] + inflate * 2.0f;
                float sz = size[2] + inflate * 2.0f;
                rects.add(MeleeGeoParser.boxRect("north", uv.get(0).getAsFloat(), uv.get(1).getAsFloat(), sx, sy, sz));
            }
        }
        return rects;
    }

    private static boolean isRectClaimed(List<float[]> rects, float[] rect) {
        for (float[] other : rects) {
            if (!(rect[0] < other[2]) || !(other[0] < rect[2]) || !(rect[1] < other[3]) || !(other[1] < rect[3])) continue;
            return true;
        }
        return false;
    }

    private static MeleeBone parseBone(JsonObject object, int texW, int texH, List<float[]> claimed) {
        MeleeBone bone = new MeleeBone(object.get("name").getAsString());
        if (object.has("pivot")) {
            JsonArray pivot = object.getAsJsonArray("pivot");
            bone.pivotX = -pivot.get(0).getAsFloat();
            bone.pivotY = pivot.get(1).getAsFloat();
            bone.pivotZ = pivot.get(2).getAsFloat();
        }
        if (object.has("rotation")) {
            JsonArray rotation = object.getAsJsonArray("rotation");
            bone.setRotX((float)Math.toRadians(-rotation.get(0).getAsFloat()));
            bone.setRotY((float)Math.toRadians(-rotation.get(1).getAsFloat()));
            bone.setRotZ((float)Math.toRadians(rotation.get(2).getAsFloat()));
        }
        if (object.has("cubes")) {
            for (JsonElement cubeElement : object.getAsJsonArray("cubes")) {
                bone.cubes.add(MeleeGeoParser.parseCube(cubeElement.getAsJsonObject(), texW, texH, claimed));
            }
        }
        return bone;
    }

    private static MeleeCube parseCube(JsonObject object, int texW, int texH, List<float[]> claimed) {
        float[] origin = MeleeGeoParser.readVec(object, "origin", new float[]{0.0f, 0.0f, 0.0f});
        float[] size = MeleeGeoParser.readVec(object, "size", new float[]{1.0f, 1.0f, 1.0f});
        float[] pivot = MeleeGeoParser.readVec(object, "pivot", (float[])origin.clone());
        float[] rotation = MeleeGeoParser.readVec(object, "rotation", new float[]{0.0f, 0.0f, 0.0f});
        float inflate = object.has("inflate") ? object.get("inflate").getAsFloat() : 0.0f;
        boolean mirror = object.has("mirror") && object.get("mirror").getAsBoolean();
        MeleeCube cube = new MeleeCube(size[0], size[1], size[2]);
        cube.pivot = new MeleeVec3(-pivot[0], pivot[1], pivot[2]);
        cube.rotation = new MeleeVec3((float)Math.toRadians(-rotation[0]), (float)Math.toRadians(-rotation[1]), (float)Math.toRadians(rotation[2]));
        cube.inflate = inflate;
        cube.mirror = mirror;
        MeleeGeoParser.buildFaces(cube, origin, size, inflate, object, texW, texH, claimed);
        return cube;
    }

    private static void buildFaces(MeleeCube cube, float[] origin, float[] size, float inflate, JsonObject cubeObject, int texW, int texH, List<float[]> claimed) {
        float x0 = origin[0] - inflate;
        float y0 = origin[1] - inflate;
        float z0 = origin[2] - inflate;
        float sx = size[0] + inflate * 2.0f;
        float sy = size[1] + inflate * 2.0f;
        float sz = size[2] + inflate * 2.0f;
        float minX = -(x0 + sx) / 16.0f;
        float maxX = -x0 / 16.0f;
        float minY = y0 / 16.0f;
        float maxY = (y0 + sy) / 16.0f;
        float minZ = z0 / 16.0f;
        float maxZ = (z0 + sz) / 16.0f;
        JsonObject perFaceUv = null;
        float uvU = 0.0f;
        float uvV = 0.0f;
        if (cubeObject.has("uv")) {
            JsonElement uv = cubeObject.get("uv");
            if (uv.isJsonArray()) {
                JsonArray array = uv.getAsJsonArray();
                uvU = array.get(0).getAsFloat();
                uvV = array.get(1).getAsFloat();
            } else if (uv.isJsonObject()) {
                perFaceUv = uv.getAsJsonObject();
            }
        }
        boolean flipSouth = sz == 0.0f && perFaceUv == null && MeleeGeoParser.isRectClaimed(claimed, MeleeGeoParser.boxRect("south", uvU, uvV, sx, sy, sz));
        cube.faces[0] = MeleeGeoParser.makeFace("north", minX, minY, minZ, maxX, maxY, maxZ, 0.0f, 0.0f, -1.0f, perFaceUv, uvU, uvV, sx, sy, sz, texW, texH, cube.mirror, false);
        cube.faces[1] = MeleeGeoParser.makeFace("south", minX, minY, minZ, maxX, maxY, maxZ, 0.0f, 0.0f, 1.0f, perFaceUv, uvU, uvV, sx, sy, sz, texW, texH, cube.mirror, flipSouth);
        cube.faces[2] = MeleeGeoParser.makeFace("west", minX, minY, minZ, maxX, maxY, maxZ, -1.0f, 0.0f, 0.0f, perFaceUv, uvU, uvV, sx, sy, sz, texW, texH, cube.mirror, false);
        cube.faces[3] = MeleeGeoParser.makeFace("east", minX, minY, minZ, maxX, maxY, maxZ, 1.0f, 0.0f, 0.0f, perFaceUv, uvU, uvV, sx, sy, sz, texW, texH, cube.mirror, false);
        cube.faces[4] = MeleeGeoParser.makeFace("up", minX, minY, minZ, maxX, maxY, maxZ, 0.0f, 1.0f, 0.0f, perFaceUv, uvU, uvV, sx, sy, sz, texW, texH, cube.mirror, false);
        cube.faces[5] = MeleeGeoParser.makeFace("down", minX, minY, minZ, maxX, maxY, maxZ, 0.0f, -1.0f, 0.0f, perFaceUv, uvU, uvV, sx, sy, sz, texW, texH, cube.mirror, false);
    }

    private static MeleeFace makeFace(String face, float x0, float y0, float z0, float x1, float y1, float z1, float nx, float ny, float nz, JsonObject perFaceUv, float uvU, float uvV, float sx, float sy, float sz, int texW, int texH, boolean mirror, boolean flip) {
        float v2;
        float u2;
        float v1;
        float u1;
        int uvRotation = 0;
        if (perFaceUv != null) {
            if (!perFaceUv.has(face)) {
                return null;
            }
            JsonObject faceUv = perFaceUv.getAsJsonObject(face);
            if (faceUv.has("uv_rotation")) {
                int rotation = faceUv.get("uv_rotation").getAsInt();
                if (rotation < 0 || rotation >= 360 || rotation % 90 != 0) {
                    throw new IllegalArgumentException("Invalid face UV rotation: " + rotation);
                }
                uvRotation = rotation / 90;
            }
            JsonArray origin = faceUv.getAsJsonArray("uv");
            JsonArray sizeArr = faceUv.getAsJsonArray("uv_size");
            float ou = origin.get(0).getAsFloat();
            float ov = origin.get(1).getAsFloat();
            u1 = ou / (float)texW;
            v1 = ov / (float)texH;
            u2 = (ou + sizeArr.get(0).getAsFloat()) / (float)texW;
            v2 = (ov + sizeArr.get(1).getAsFloat()) / (float)texH;
        } else {
            String rectFace = face;
            boolean swap = mirror;
            if (flip) {
                rectFace = "north";
                swap = !mirror;
            } else if (mirror && "east".equals(face)) {
                rectFace = "west";
            } else if (mirror && "west".equals(face)) {
                rectFace = "east";
            }
            float[] rect = MeleeGeoParser.boxRect(rectFace, uvU, uvV, sx, sy, sz);
            u1 = rect[swap ? 2 : 0] / (float)texW;
            v1 = rect[1] / (float)texH;
            u2 = rect[swap ? 0 : 2] / (float)texW;
            v2 = rect[3] / (float)texH;
        }
        MeleeVertex[] corners = MeleeGeoParser.buildCorners(face, x0, y0, z0, x1, y1, z1, u1, v1, u2, v2);
        if (uvRotation != 0) {
            int i;
            float[] us = new float[4];
            float[] vs = new float[4];
            for (i = 0; i < 4; ++i) {
                us[i] = corners[i].u;
                vs[i] = corners[i].v;
            }
            for (i = 0; i < 4; ++i) {
                int src = (i + uvRotation) % 4;
                corners[i].u = us[src];
                corners[i].v = vs[src];
            }
        }
        return new MeleeFace(corners, nx, ny, nz);
    }

    private static float[] boxRect(String face, float u, float v, float sx, float sy, float sz) {
        switch (face) {
            case "up": {
                return new float[]{u + sz, v, u + sz + sx, v + sz};
            }
            case "down": {
                return new float[]{u + sz + sx, v, u + sz + sx * 2.0f, v + sz};
            }
            case "west": {
                return new float[]{u, v + sz, u + sz, v + sz + sy};
            }
            case "north": {
                return new float[]{u + sz, v + sz, u + sz + sx, v + sz + sy};
            }
            case "east": {
                return new float[]{u + sz + sx, v + sz, u + sz * 2.0f + sx, v + sz + sy};
            }
            case "south": {
                return new float[]{u + sz * 2.0f + sx, v + sz, u + sz * 2.0f + sx * 2.0f, v + sz + sy};
            }
        }
        return new float[]{u, v, u, v};
    }

    private static MeleeVertex[] buildCorners(String face, float x0, float y0, float z0, float x1, float y1, float z1, float u1, float v1, float u2, float v2) {
        MeleeVertex[] corners = new MeleeVertex[4];
        switch (face) {
            case "north": {
                corners[0] = new MeleeVertex(x0, y1, z0, u1, v1);
                corners[1] = new MeleeVertex(x1, y1, z0, u2, v1);
                corners[2] = new MeleeVertex(x1, y0, z0, u2, v2);
                corners[3] = new MeleeVertex(x0, y0, z0, u1, v2);
                break;
            }
            case "south": {
                corners[0] = new MeleeVertex(x1, y1, z1, u1, v1);
                corners[1] = new MeleeVertex(x0, y1, z1, u2, v1);
                corners[2] = new MeleeVertex(x0, y0, z1, u2, v2);
                corners[3] = new MeleeVertex(x1, y0, z1, u1, v2);
                break;
            }
            case "west": {
                corners[0] = new MeleeVertex(x0, y1, z1, u1, v1);
                corners[1] = new MeleeVertex(x0, y1, z0, u2, v1);
                corners[2] = new MeleeVertex(x0, y0, z0, u2, v2);
                corners[3] = new MeleeVertex(x0, y0, z1, u1, v2);
                break;
            }
            case "east": {
                corners[0] = new MeleeVertex(x1, y1, z0, u1, v1);
                corners[1] = new MeleeVertex(x1, y1, z1, u2, v1);
                corners[2] = new MeleeVertex(x1, y0, z1, u2, v2);
                corners[3] = new MeleeVertex(x1, y0, z0, u1, v2);
                break;
            }
            case "up": {
                corners[0] = new MeleeVertex(x0, y1, z0, u1, v1);
                corners[1] = new MeleeVertex(x1, y1, z0, u2, v1);
                corners[2] = new MeleeVertex(x1, y1, z1, u2, v2);
                corners[3] = new MeleeVertex(x0, y1, z1, u1, v2);
                break;
            }
            case "down": {
                corners[0] = new MeleeVertex(x0, y0, z1, u1, v1);
                corners[1] = new MeleeVertex(x1, y0, z1, u2, v1);
                corners[2] = new MeleeVertex(x1, y0, z0, u2, v2);
                corners[3] = new MeleeVertex(x0, y0, z0, u1, v2);
                break;
            }
            default: {
                corners[0] = new MeleeVertex(0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
                corners[1] = corners[0];
                corners[2] = corners[0];
                corners[3] = corners[0];
            }
        }
        return corners;
    }

    private static float[] readVec(JsonObject object, String key, float[] fallback) {
        if (!object.has(key)) {
            return fallback;
        }
        JsonArray array = object.getAsJsonArray(key);
        float[] result = new float[array.size()];
        for (int i = 0; i < array.size(); ++i) {
            result[i] = array.get(i).getAsFloat();
        }
        return result;
    }
}

