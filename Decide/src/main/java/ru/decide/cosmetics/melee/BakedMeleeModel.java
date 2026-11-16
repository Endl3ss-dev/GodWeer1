/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package ru.decide.cosmetics.melee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.decide.cosmetics.melee.MeleeAnimParser;
import ru.decide.cosmetics.melee.MeleeBone;
import ru.decide.cosmetics.melee.MeleeClip;
import ru.decide.cosmetics.melee.MeleeCube;
import ru.decide.cosmetics.melee.MeleeFace;
import ru.decide.cosmetics.melee.MeleeGeoModel;
import ru.decide.cosmetics.melee.MeleeVertex;

@Environment(value=EnvType.CLIENT)
public final class BakedMeleeModel {
    public static final int VERTEX_STRIDE = 8;
    public static final int VERTICES_PER_FACE = 4;
    public static final String BONE_RIGHT_HAND = "righthand_pos";
    public static final String BONE_LEFT_HAND = "lefthand_pos";
    public static final String BONE_CAMERA = "camera";
    public static final String BONE_VIEW_ANCHOR = "idle_view";
    private static final float MODEL_SCALE = 16.0f;
    private final String[] boneNames;
    private final int[] boneParents;
    private final float[] bonePivots;
    private final float[] boneRestRotation;
    private final float[][] boneMeshes;
    private final Map<String, Integer> boneIndex;
    private final Map<String, MeleeClip> clips;
    private final int rightHandBone;
    private final int leftHandBone;
    private final int cameraBone;
    private final int viewAnchorBone;

    private BakedMeleeModel(String[] boneNames, int[] boneParents, float[] bonePivots, float[] boneRestRotation, float[][] boneMeshes, Map<String, Integer> boneIndex, Map<String, MeleeClip> clips) {
        this.boneNames = boneNames;
        this.boneParents = boneParents;
        this.bonePivots = bonePivots;
        this.boneRestRotation = boneRestRotation;
        this.boneMeshes = boneMeshes;
        this.boneIndex = boneIndex;
        this.clips = clips;
        this.rightHandBone = boneIndex.getOrDefault(BONE_RIGHT_HAND, -1);
        this.leftHandBone = boneIndex.getOrDefault(BONE_LEFT_HAND, -1);
        this.cameraBone = boneIndex.getOrDefault(BONE_CAMERA, -1);
        this.viewAnchorBone = boneIndex.getOrDefault(BONE_VIEW_ANCHOR, this.cameraBone);
    }

    public int boneCount() {
        return this.boneNames.length;
    }

    public int parentOf(int bone) {
        return this.boneParents[bone];
    }

    public float pivotX(int bone) {
        return this.bonePivots[bone * 3];
    }

    public float pivotY(int bone) {
        return this.bonePivots[bone * 3 + 1];
    }

    public float pivotZ(int bone) {
        return this.bonePivots[bone * 3 + 2];
    }

    public float restRotationX(int bone) {
        return this.boneRestRotation[bone * 3];
    }

    public float restRotationY(int bone) {
        return this.boneRestRotation[bone * 3 + 1];
    }

    public float restRotationZ(int bone) {
        return this.boneRestRotation[bone * 3 + 2];
    }

    public float[] meshOf(int bone) {
        return this.boneMeshes[bone];
    }

    public String nameOf(int bone) {
        return this.boneNames[bone];
    }

    public Map<String, Integer> getBoneIndex() {
        return this.boneIndex;
    }

    public MeleeClip clip(String name) {
        return this.clips.get(name);
    }

    public boolean hasClip(String name) {
        return this.clips.containsKey(name);
    }

    public int getRightHandBone() {
        return this.rightHandBone;
    }

    public int getLeftHandBone() {
        return this.leftHandBone;
    }

    public int getCameraBone() {
        return this.cameraBone;
    }

    public int getViewAnchorBone() {
        return this.viewAnchorBone;
    }

    public boolean markBranches(List<String> names, boolean[] visible) {
        Arrays.fill(visible, false);
        if (names.isEmpty()) {
            return false;
        }
        boolean any = false;
        for (String name : names) {
            Integer bone = this.boneIndex.get(name);
            if (bone == null) continue;
            visible[bone.intValue()] = true;
            any = true;
        }
        if (!any) {
            return false;
        }
        for (int i = 0; i < visible.length; ++i) {
            int parent = this.boneParents[i];
            if (parent < 0 || !visible[parent]) continue;
            visible[i] = true;
        }
        return true;
    }

    public static BakedMeleeModel bake(MeleeGeoModel model, String animationJson) {
        int i;
        ArrayList<MeleeBone> flat = new ArrayList<MeleeBone>();
        for (MeleeBone root : model.roots) {
            BakedMeleeModel.collect(root, flat);
        }
        int count = flat.size();
        String[] names = new String[count];
        int[] parents = new int[count];
        float[] pivots = new float[count * 3];
        float[] rest = new float[count * 3];
        float[][] meshes = new float[count][];
        HashMap<String, Integer> index = new HashMap<String, Integer>(count);
        for (i = 0; i < count; ++i) {
            index.put(((MeleeBone)flat.get((int)i)).name, i);
        }
        for (i = 0; i < count; ++i) {
            MeleeBone bone = (MeleeBone)flat.get(i);
            names[i] = bone.name;
            parents[i] = bone.parent == null ? -1 : index.getOrDefault(bone.parent.name, -1);
            pivots[i * 3] = bone.pivotX;
            pivots[i * 3 + 1] = bone.pivotY;
            pivots[i * 3 + 2] = bone.pivotZ;
            rest[i * 3] = bone.getRotX();
            rest[i * 3 + 1] = bone.getRotY();
            rest[i * 3 + 2] = bone.getRotZ();
            meshes[i] = BakedMeleeModel.bakeMesh(bone);
        }
        Map<String, MeleeClip> clips = animationJson == null ? Map.of() : MeleeAnimParser.parse(animationJson, index);
        return new BakedMeleeModel(names, parents, pivots, rest, meshes, Map.copyOf(index), clips);
    }

    private static void collect(MeleeBone bone, List<MeleeBone> out) {
        out.add(bone);
        for (MeleeBone child : bone.children) {
            BakedMeleeModel.collect(child, out);
        }
    }

    private static float[] bakeMesh(MeleeBone bone) {
        int vertexCount = 0;
        for (MeleeCube cube : bone.cubes) {
            for (MeleeFace face : cube.faces) {
                if (face == null) continue;
                vertexCount += 4;
            }
        }
        if (vertexCount == 0) {
            return null;
        }
        float[] out = new float[vertexCount * 8];
        Quaternionf rotation = new Quaternionf();
        Vector3f vector = new Vector3f();
        int cursor = 0;
        for (MeleeCube cube : bone.cubes) {
            rotation.rotationZ(cube.rotation.getZ()).rotateY(cube.rotation.getY()).rotateX(cube.rotation.getX());
            float px = cube.pivot.getX() / 16.0f;
            float py = cube.pivot.getY() / 16.0f;
            float pz = cube.pivot.getZ() / 16.0f;
            for (MeleeFace face : cube.faces) {
                if (face == null) continue;
                vector.set(BakedMeleeModel.normalX(cube, face), BakedMeleeModel.normalY(cube, face), BakedMeleeModel.normalZ(cube, face));
                rotation.transform(vector);
                float nx = vector.x;
                float ny = vector.y;
                float nz = vector.z;
                for (MeleeVertex corner : face.corners) {
                    vector.set(corner.position.getX() - px, corner.position.getY() - py, corner.position.getZ() - pz);
                    rotation.transform(vector);
                    out[cursor] = vector.x + px;
                    out[cursor + 1] = vector.y + py;
                    out[cursor + 2] = vector.z + pz;
                    out[cursor + 3] = corner.u;
                    out[cursor + 4] = corner.v;
                    out[cursor + 5] = nx;
                    out[cursor + 6] = ny;
                    out[cursor + 7] = nz;
                    cursor += 8;
                }
            }
        }
        return out;
    }

    private static float normalX(MeleeCube cube, MeleeFace face) {
        float n = face.normal.getX();
        boolean flip = Float.compare(cube.size.getY(), 0.0f) == 0 || Float.compare(cube.size.getZ(), 0.0f) == 0;
        return flip && n < 0.0f ? -n : n;
    }

    private static float normalY(MeleeCube cube, MeleeFace face) {
        float n = face.normal.getY();
        boolean flip = Float.compare(cube.size.getX(), 0.0f) == 0 || Float.compare(cube.size.getZ(), 0.0f) == 0;
        return flip && n < 0.0f ? -n : n;
    }

    private static float normalZ(MeleeCube cube, MeleeFace face) {
        float n = face.normal.getZ();
        boolean flip = Float.compare(cube.size.getX(), 0.0f) == 0 || Float.compare(cube.size.getY(), 0.0f) == 0;
        return flip && n < 0.0f ? -n : n;
    }
}

