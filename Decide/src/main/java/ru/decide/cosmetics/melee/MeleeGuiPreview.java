/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package ru.decide.cosmetics.melee;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import ru.decide.cosmetics.melee.BakedMeleeModel;
import ru.decide.cosmetics.melee.BoneMatrices;
import ru.decide.cosmetics.melee.MeleePreloader;
import ru.decide.cosmetics.melee.MeleeSkin;

@Environment(value=EnvType.CLIENT)
public final class MeleeGuiPreview {
    private static final MeleeGuiPreview INSTANCE = new MeleeGuiPreview();
    private static final float MODEL_SCALE = 16.0f;
    private static final int VERTEX_STRIDE = 8;
    private static final int VERTICES_PER_FACE = 4;
    private final Matrix4f local = new Matrix4f();
    private final Quaternionf rotation = new Quaternionf();
    private final Matrix4f combined = new Matrix4f();
    private final BoneMatrices bones = new BoneMatrices();
    private static final Vector3f POSITION = new Vector3f();

    private MeleeGuiPreview() {
    }

    public static boolean available(MeleeSkin skin) {
        return MeleePreloader.get(skin) != null;
    }

    public static float[] bounds(MeleeSkin skin) {
        BakedMeleeModel model = MeleePreloader.get(skin);
        if (model == null) {
            return null;
        }
        MeleeGuiPreview preview = INSTANCE;
        preview.buildRest(model);
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE;
        float maxX = -3.4028235E38f;
        float maxY = -3.4028235E38f;
        float maxZ = -3.4028235E38f;
        boolean any = false;
        for (int bone = 0; bone < model.boneCount(); ++bone) {
            float[] mesh;
            if (!preview.bones.visible[bone] || (mesh = model.meshOf(bone)) == null) continue;
            preview.combined.set((Matrix4fc)preview.bones.world[bone]);
            for (int cursor = 0; cursor < mesh.length; cursor += 8) {
                POSITION.set(mesh[cursor], mesh[cursor + 1], mesh[cursor + 2]);
                preview.combined.transformPosition(POSITION);
                float x = MeleeGuiPreview.POSITION.x;
                float y = MeleeGuiPreview.POSITION.y;
                float z = MeleeGuiPreview.POSITION.z;
                if (x < minX) {
                    minX = x;
                }
                if (x > maxX) {
                    maxX = x;
                }
                if (y < minY) {
                    minY = y;
                }
                if (y > maxY) {
                    maxY = y;
                }
                if (z < minZ) {
                    minZ = z;
                }
                if (z > maxZ) {
                    maxZ = z;
                }
                any = true;
            }
        }
        if (!any) {
            return null;
        }
        float maxDim = Math.max(0.01f, Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ)));
        return new float[]{(minX + maxX) * 0.5f, (minY + maxY) * 0.5f, (minZ + maxZ) * 0.5f, maxDim};
    }

    public static int emit(MeleeSkin skin, Matrix4f base, Sink sink) {
        BakedMeleeModel model = MeleePreloader.get(skin);
        if (model == null) {
            return 0;
        }
        MeleeGuiPreview preview = INSTANCE;
        preview.buildRest(model);
        int written = 0;
        for (int bone = 0; bone < model.boneCount(); ++bone) {
            float[] mesh;
            if (!preview.bones.visible[bone] || (mesh = model.meshOf(bone)) == null) continue;
            preview.combined.set((Matrix4fc)base).mul((Matrix4fc)preview.bones.world[bone]);
            int faces = mesh.length / 32;
            for (int face = 0; face < faces; ++face) {
                int base0 = face * 8 * 4;
                written += preview.emitVertex(mesh, base0, sink);
                written += preview.emitVertex(mesh, base0 + 8, sink);
                written += preview.emitVertex(mesh, base0 + 16, sink);
                written += preview.emitVertex(mesh, base0, sink);
                written += preview.emitVertex(mesh, base0 + 16, sink);
                written += preview.emitVertex(mesh, base0 + 24, sink);
            }
        }
        return written;
    }

    private int emitVertex(float[] mesh, int cursor, Sink sink) {
        POSITION.set(mesh[cursor], mesh[cursor + 1], mesh[cursor + 2]);
        this.combined.transformPosition(POSITION);
        sink.vertex(MeleeGuiPreview.POSITION.x, MeleeGuiPreview.POSITION.y, MeleeGuiPreview.POSITION.z, mesh[cursor + 3], mesh[cursor + 4]);
        return 1;
    }

    private void buildRest(BakedMeleeModel model) {
        int count = model.boneCount();
        this.bones.ensureCapacity(count);
        for (int bone = 0; bone < count; ++bone) {
            this.bones.visible[bone] = true;
            this.local.identity();
            this.applyPivotRotation(model, bone, model.restRotationX(bone), model.restRotationY(bone), model.restRotationZ(bone), 1.0f, 1.0f, 1.0f);
            this.compose(model, bone);
        }
        if (model.getRightHandBone() >= 0) {
            this.bones.visible[model.getRightHandBone()] = false;
        }
        if (model.getLeftHandBone() >= 0) {
            this.bones.visible[model.getLeftHandBone()] = false;
        }
    }

    private void applyPivotRotation(BakedMeleeModel model, int bone, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ) {
        float px = model.pivotX(bone) / 16.0f;
        float py = model.pivotY(bone) / 16.0f;
        float pz = model.pivotZ(bone) / 16.0f;
        this.local.translate(px, py, pz);
        this.rotation.rotationZ(rotZ).rotateY(rotY).rotateX(rotX);
        this.local.rotate((Quaternionfc)this.rotation);
        this.local.scale(scaleX, scaleY, scaleZ);
        this.local.translate(-px, -py, -pz);
    }

    private void compose(BakedMeleeModel model, int bone) {
        int parent = model.parentOf(bone);
        if (parent < 0) {
            this.bones.world[bone].set((Matrix4fc)this.local);
        } else {
            this.bones.world[bone].set((Matrix4fc)this.bones.world[parent]).mul((Matrix4fc)this.local);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Sink {
        public void vertex(float var1, float var2, float var3, float var4, float var5);
    }
}

