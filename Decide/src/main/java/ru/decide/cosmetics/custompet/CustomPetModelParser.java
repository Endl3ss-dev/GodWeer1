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
package ru.decide.cosmetics.custompet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.HashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.cosmetics.geo.GeoBone;
import ru.decide.cosmetics.geo.GeoCube;
import ru.decide.cosmetics.geo.GeoModel;
import ru.decide.cosmetics.geo.GeoQuad;
import ru.decide.cosmetics.geo.GeoVertex;
import ru.decide.cosmetics.geo.Vec3F;

@Environment(value=EnvType.CLIENT)
public final class CustomPetModelParser {
    private CustomPetModelParser() {
    }

    public static GeoModel parse(String jsonString) {
        try {
            JsonObject json = JsonParser.parseString((String)jsonString).getAsJsonObject();
            return CustomPetModelParser.parseModel(json);
        }
        catch (Exception e) {
            return null;
        }
    }

    private static GeoModel parseModel(JsonObject root) {
        JsonArray geoArray;
        if (root.has("model") && root.get("model").isJsonObject()) {
            root = root.getAsJsonObject("model");
        }
        if ((geoArray = root.getAsJsonArray("minecraft:geometry")) == null || geoArray.isEmpty()) {
            return null;
        }
        JsonObject geo = geoArray.get(0).getAsJsonObject();
        JsonObject desc = geo.getAsJsonObject("description");
        int texW = desc != null && desc.has("texture_width") ? desc.get("texture_width").getAsInt() : 64;
        int texH = desc != null && desc.has("texture_height") ? desc.get("texture_height").getAsInt() : 64;
        GeoModel model = new GeoModel();
        model.textureWidth = texW;
        model.textureHeight = texH;
        JsonArray bonesArray = geo.getAsJsonArray("bones");
        if (bonesArray == null) {
            return model;
        }
        HashMap<String, GeoBone> boneMap = new HashMap<String, GeoBone>();
        for (JsonElement elem : bonesArray) {
            GeoBone bone = CustomPetModelParser.parseBone(elem.getAsJsonObject(), texW, texH);
            boneMap.put(bone.name, bone);
        }
        for (JsonElement elem : bonesArray) {
            JsonObject boneObj = elem.getAsJsonObject();
            String name = boneObj.get("name").getAsString();
            GeoBone bone = (GeoBone)boneMap.get(name);
            if (boneObj.has("parent")) {
                GeoBone parent = (GeoBone)boneMap.get(boneObj.get("parent").getAsString());
                if (parent == null) continue;
                parent.childBones.add(bone);
                bone.parent = parent;
                continue;
            }
            model.topLevelBones.add(bone);
        }
        return model;
    }

    private static GeoBone parseBone(JsonObject boneObj, int texW, int texH) {
        GeoBone bone = new GeoBone(boneObj.get("name").getAsString());
        if (boneObj.has("pivot")) {
            JsonArray pivot = boneObj.getAsJsonArray("pivot");
            bone.rotationPointX = pivot.get(0).getAsFloat();
            bone.rotationPointY = pivot.get(1).getAsFloat();
            bone.rotationPointZ = pivot.get(2).getAsFloat();
        }
        if (boneObj.has("rotation")) {
            JsonArray rot = boneObj.getAsJsonArray("rotation");
            bone.setRotationX((float)Math.toRadians(rot.get(0).getAsFloat()));
            bone.setRotationY((float)Math.toRadians(rot.get(1).getAsFloat()));
            bone.setRotationZ((float)Math.toRadians(rot.get(2).getAsFloat()));
        }
        if (boneObj.has("cubes")) {
            for (JsonElement elem : boneObj.getAsJsonArray("cubes")) {
                bone.childCubes.add(CustomPetModelParser.parseCube(elem.getAsJsonObject(), texW, texH));
            }
        }
        return bone;
    }

    private static GeoCube parseCube(JsonObject cubeObj, int texW, int texH) {
        float[] origin = CustomPetModelParser.floatArray(cubeObj, "origin", new float[]{0.0f, 0.0f, 0.0f});
        float[] size = CustomPetModelParser.floatArray(cubeObj, "size", new float[]{1.0f, 1.0f, 1.0f});
        float[] pivot = CustomPetModelParser.floatArray(cubeObj, "pivot", (float[])origin.clone());
        float[] rot = CustomPetModelParser.floatArray(cubeObj, "rotation", new float[]{0.0f, 0.0f, 0.0f});
        float inflate = cubeObj.has("inflate") ? cubeObj.get("inflate").getAsFloat() : 0.0f;
        GeoCube cube = new GeoCube(size[0], size[1], size[2]);
        cube.pivot = new Vec3F(pivot[0], pivot[1], pivot[2]);
        cube.rotation = new Vec3F((float)Math.toRadians(rot[0]), (float)Math.toRadians(rot[1]), (float)Math.toRadians(rot[2]));
        cube.inflate = inflate;
        CustomPetModelParser.buildCubeQuads(cube, origin, size, inflate, cubeObj, texW, texH);
        return cube;
    }

    private static void buildCubeQuads(GeoCube cube, float[] origin, float[] size, float inflate, JsonObject cubeObj, int texW, int texH) {
        float ox = origin[0] - inflate;
        float oy = origin[1] - inflate;
        float oz = origin[2] - inflate;
        float w = size[0] + inflate * 2.0f;
        float h = size[1] + inflate * 2.0f;
        float d = size[2] + inflate * 2.0f;
        float u = 0.0f;
        float v = 0.0f;
        boolean perFace = false;
        JsonObject uvObj = null;
        if (cubeObj.has("uv")) {
            JsonElement uv = cubeObj.get("uv");
            if (uv.isJsonArray()) {
                u = uv.getAsJsonArray().get(0).getAsFloat();
                v = uv.getAsJsonArray().get(1).getAsFloat();
            } else if (uv.isJsonObject()) {
                perFace = true;
                uvObj = uv.getAsJsonObject();
            }
        }
        float minX = ox / 16.0f;
        float minY = oy / 16.0f;
        float minZ = oz / 16.0f;
        float maxX = (ox + w) / 16.0f;
        float maxY = (oy + h) / 16.0f;
        float maxZ = (oz + d) / 16.0f;
        if (perFace) {
            cube.quads[0] = CustomPetModelParser.perFace(uvObj, "west", minX, minY, minZ, minX, maxY, maxZ, -1.0f, 0.0f, 0.0f, texW, texH);
            cube.quads[1] = CustomPetModelParser.perFace(uvObj, "east", maxX, minY, minZ, maxX, maxY, maxZ, 1.0f, 0.0f, 0.0f, texW, texH);
            cube.quads[2] = CustomPetModelParser.perFace(uvObj, "down", minX, minY, minZ, maxX, minY, maxZ, 0.0f, -1.0f, 0.0f, texW, texH);
            cube.quads[3] = CustomPetModelParser.perFace(uvObj, "up", minX, maxY, minZ, maxX, maxY, maxZ, 0.0f, 1.0f, 0.0f, texW, texH);
            cube.quads[4] = CustomPetModelParser.perFace(uvObj, "north", minX, minY, minZ, maxX, maxY, minZ, 0.0f, 0.0f, -1.0f, texW, texH);
            cube.quads[5] = CustomPetModelParser.perFace(uvObj, "south", minX, minY, maxZ, maxX, maxY, maxZ, 0.0f, 0.0f, 1.0f, texW, texH);
        } else {
            cube.quads[0] = CustomPetModelParser.boxQuad(minX, minY, minZ, minX, maxY, maxZ, -1.0f, 0.0f, 0.0f, CustomPetModelParser.box(u, v, w, h, d, "west"), texW, texH, "west");
            cube.quads[1] = CustomPetModelParser.boxQuad(maxX, minY, minZ, maxX, maxY, maxZ, 1.0f, 0.0f, 0.0f, CustomPetModelParser.box(u, v, w, h, d, "east"), texW, texH, "east");
            cube.quads[2] = CustomPetModelParser.boxQuad(minX, minY, minZ, maxX, minY, maxZ, 0.0f, -1.0f, 0.0f, CustomPetModelParser.box(u, v, w, h, d, "down"), texW, texH, "down");
            cube.quads[3] = CustomPetModelParser.boxQuad(minX, maxY, minZ, maxX, maxY, maxZ, 0.0f, 1.0f, 0.0f, CustomPetModelParser.box(u, v, w, h, d, "up"), texW, texH, "up");
            cube.quads[4] = CustomPetModelParser.boxQuad(minX, minY, minZ, maxX, maxY, minZ, 0.0f, 0.0f, -1.0f, CustomPetModelParser.box(u, v, w, h, d, "north"), texW, texH, "north");
            cube.quads[5] = CustomPetModelParser.boxQuad(minX, minY, maxZ, maxX, maxY, maxZ, 0.0f, 0.0f, 1.0f, CustomPetModelParser.box(u, v, w, h, d, "south"), texW, texH, "south");
        }
    }

    private static float[] box(float u, float v, float w, float h, float d, String face) {
        float[] fArray;
        switch (face) {
            case "north": {
                float[] fArray2 = new float[4];
                fArray2[0] = u + d;
                fArray2[1] = v + d;
                fArray2[2] = w;
                fArray = fArray2;
                fArray2[3] = h;
                break;
            }
            case "south": {
                float[] fArray3 = new float[4];
                fArray3[0] = u + d + w + d;
                fArray3[1] = v + d;
                fArray3[2] = w;
                fArray = fArray3;
                fArray3[3] = h;
                break;
            }
            case "east": {
                float[] fArray4 = new float[4];
                fArray4[0] = u + d + w;
                fArray4[1] = v + d;
                fArray4[2] = d;
                fArray = fArray4;
                fArray4[3] = h;
                break;
            }
            case "west": {
                float[] fArray5 = new float[4];
                fArray5[0] = u;
                fArray5[1] = v + d;
                fArray5[2] = d;
                fArray = fArray5;
                fArray5[3] = h;
                break;
            }
            case "up": {
                float[] fArray6 = new float[4];
                fArray6[0] = u + d;
                fArray6[1] = v;
                fArray6[2] = w;
                fArray = fArray6;
                fArray6[3] = d;
                break;
            }
            case "down": {
                float[] fArray7 = new float[4];
                fArray7[0] = u + d + w;
                fArray7[1] = v;
                fArray7[2] = w;
                fArray = fArray7;
                fArray7[3] = d;
                break;
            }
            default: {
                float[] fArray8 = new float[4];
                fArray8[0] = u;
                fArray8[1] = v;
                fArray8[2] = w;
                fArray = fArray8;
                fArray8[3] = h;
            }
        }
        return fArray;
    }

    private static GeoQuad perFace(JsonObject uvObj, String face, float x1, float y1, float z1, float x2, float y2, float z2, float nx, float ny, float nz, int texW, int texH) {
        if (!uvObj.has(face)) {
            return null;
        }
        JsonObject faceObj = uvObj.getAsJsonObject(face);
        JsonArray uv = faceObj.getAsJsonArray("uv");
        JsonArray uvSize = faceObj.getAsJsonArray("uv_size");
        float u = uv.get(0).getAsFloat() / (float)texW;
        float v = uv.get(1).getAsFloat() / (float)texH;
        float uw = uvSize.get(0).getAsFloat() / (float)texW;
        float vh = uvSize.get(1).getAsFloat() / (float)texH;
        return new GeoQuad(CustomPetModelParser.faceVertices(face, x1, y1, z1, x2, y2, z2, u, v, uw, vh), nx, ny, nz);
    }

    private static GeoQuad boxQuad(float x1, float y1, float z1, float x2, float y2, float z2, float nx, float ny, float nz, float[] region, int texW, int texH, String face) {
        float u = region[0] / (float)texW;
        float v = region[1] / (float)texH;
        float uw = region[2] / (float)texW;
        float vh = region[3] / (float)texH;
        return new GeoQuad(CustomPetModelParser.faceVertices(face, x1, y1, z1, x2, y2, z2, u, v, uw, vh), nx, ny, nz);
    }

    private static GeoVertex[] faceVertices(String face, float x1, float y1, float z1, float x2, float y2, float z2, float u, float v, float uw, float vh) {
        float u2 = u + uw;
        float v2 = v + vh;
        GeoVertex[] verts = new GeoVertex[4];
        switch (face) {
            case "north": {
                verts[0] = new GeoVertex(x2, y2, z1, u, v);
                verts[1] = new GeoVertex(x1, y2, z1, u2, v);
                verts[2] = new GeoVertex(x1, y1, z1, u2, v2);
                verts[3] = new GeoVertex(x2, y1, z1, u, v2);
                break;
            }
            case "south": {
                verts[0] = new GeoVertex(x1, y2, z2, u, v);
                verts[1] = new GeoVertex(x2, y2, z2, u2, v);
                verts[2] = new GeoVertex(x2, y1, z2, u2, v2);
                verts[3] = new GeoVertex(x1, y1, z2, u, v2);
                break;
            }
            case "east": {
                verts[0] = new GeoVertex(x2, y2, z2, u, v);
                verts[1] = new GeoVertex(x2, y2, z1, u2, v);
                verts[2] = new GeoVertex(x2, y1, z1, u2, v2);
                verts[3] = new GeoVertex(x2, y1, z2, u, v2);
                break;
            }
            case "west": {
                verts[0] = new GeoVertex(x1, y2, z1, u, v);
                verts[1] = new GeoVertex(x1, y2, z2, u2, v);
                verts[2] = new GeoVertex(x1, y1, z2, u2, v2);
                verts[3] = new GeoVertex(x1, y1, z1, u, v2);
                break;
            }
            case "up": {
                verts[0] = new GeoVertex(x1, y2, z1, u, v);
                verts[1] = new GeoVertex(x1, y2, z2, u, v2);
                verts[2] = new GeoVertex(x2, y2, z2, u2, v2);
                verts[3] = new GeoVertex(x2, y2, z1, u2, v);
                break;
            }
            case "down": {
                verts[0] = new GeoVertex(x2, y1, z1, u, v);
                verts[1] = new GeoVertex(x2, y1, z2, u, v2);
                verts[2] = new GeoVertex(x1, y1, z2, u2, v2);
                verts[3] = new GeoVertex(x1, y1, z1, u2, v);
                break;
            }
        }
        return verts;
    }

    private static float[] floatArray(JsonObject obj, String name, float[] def) {
        if (!obj.has(name)) {
            return def;
        }
        JsonArray arr = obj.getAsJsonArray(name);
        float[] result = new float[arr.size()];
        for (int i = 0; i < arr.size(); ++i) {
            result[i] = arr.get(i).getAsFloat();
        }
        return result;
    }
}

