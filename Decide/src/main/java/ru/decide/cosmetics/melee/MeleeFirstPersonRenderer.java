/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1007
 *  net.minecraft.class_11659
 *  net.minecraft.class_12249
 *  net.minecraft.class_1268
 *  net.minecraft.class_1306
 *  net.minecraft.class_1309
 *  net.minecraft.class_1799
 *  net.minecraft.class_1921
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4608
 *  net.minecraft.class_742
 *  net.minecraft.class_759
 *  net.minecraft.class_7833
 *  net.minecraft.class_811
 *  net.minecraft.class_898
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package ru.decide.cosmetics.melee;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1007;
import net.minecraft.class_11659;
import net.minecraft.class_12249;
import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4608;
import net.minecraft.class_742;
import net.minecraft.class_759;
import net.minecraft.class_7833;
import net.minecraft.class_811;
import net.minecraft.class_898;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import ru.decide.cosmetics.melee.BakedMeleeModel;
import ru.decide.cosmetics.melee.BoneMatrices;
import ru.decide.cosmetics.melee.MeleeController;
import ru.decide.cosmetics.melee.MeleeHandTransform;
import ru.decide.cosmetics.melee.MeleePreloader;
import ru.decide.cosmetics.melee.MeleeRegistry;
import ru.decide.cosmetics.melee.MeleeSkin;

@Environment(value=EnvType.CLIENT)
public final class MeleeFirstPersonRenderer {
    private static final MeleeFirstPersonRenderer INSTANCE = new MeleeFirstPersonRenderer();
    private static final float MODEL_SCALE = 16.0f;
    private static final float DEGREES_TO_RADIANS = (float)Math.PI / 180;
    private static final float ARM_ALIGN_X = 0.0625f;
    private static final float ARM_ALIGN_Y = 0.5f;
    private static final float EQUIP_DROP = -0.6f;
    private static final int VERTEX_STRIDE = 8;
    private static final Matrix4f OFF_HAND_ITEM_ALIGN = MeleeFirstPersonRenderer.buildOffHandItemAlign();
    private final BoneMatrices primaryBones = new BoneMatrices();
    private final BoneMatrices secondaryBones = new BoneMatrices();
    private boolean primaryClaimed;
    private boolean handMirrored;
    private BakedMeleeModel handModel;
    private class_1799 handItem;
    private float handItemEquipProgress;
    private final Matrix4f local = new Matrix4f();
    private final Matrix4f armPose = new Matrix4f();
    private final Quaternionf rotation = new Quaternionf();

    private MeleeFirstPersonRenderer() {
    }

    public static MeleeFirstPersonRenderer getInstance() {
        return INSTANCE;
    }

    private static Matrix4f buildOffHandItemAlign() {
        Matrix4f align = new Matrix4f().translate(-0.64f, -0.6f, -0.72f).rotate((Quaternionfc)class_7833.field_40716.rotationDegrees(-45.0f)).translate(1.0f, 3.6f, 3.5f).rotate((Quaternionfc)class_7833.field_40718.rotationDegrees(-120.0f)).rotate((Quaternionfc)class_7833.field_40714.rotationDegrees(200.0f)).rotate((Quaternionfc)class_7833.field_40716.rotationDegrees(135.0f)).translate(-5.6f, 0.0f, 0.0f);
        Matrix4f item = new Matrix4f().translate(-0.56f, -0.52f, -0.72f);
        return align.invert().mul((Matrix4fc)item);
    }

    public void beginHands() {
        this.handModel = null;
        this.handItem = null;
        this.primaryClaimed = false;
    }

    public boolean deferHandItem(class_1799 item, float equipProgress) {
        MeleeSkin skin = MeleeRegistry.equipped();
        if (skin == null || MeleePreloader.get(skin) == null) {
            return false;
        }
        this.handItem = item;
        this.handItemEquipProgress = equipProgress;
        return true;
    }

    public void flushHandItem(class_742 player, class_4587 matrices, class_11659 queue, int light) {
        class_1799 item = this.handItem;
        BakedMeleeModel model = this.handModel;
        this.handItem = null;
        if (item == null || model == null) {
            return;
        }
        int handBone = model.getLeftHandBone();
        if (handBone < 0 || handBone >= this.primaryBones.world.length) {
            return;
        }
        matrices.method_22903();
        matrices.method_46416(0.0f, this.handItemEquipProgress * -0.6f, 0.0f);
        if (this.handMirrored) {
            matrices.method_22905(-1.0f, 1.0f, 1.0f);
        }
        this.applyCamera(model, MeleeController.getInstance(), matrices);
        this.applyViewAnchor(model, matrices);
        this.armPose.set((Matrix4fc)this.primaryBones.world[handBone]).translate(0.0625f, 0.5f, 0.0f).mul((Matrix4fc)OFF_HAND_ITEM_ALIGN);
        matrices.method_34425((Matrix4fc)this.armPose);
        if (this.handMirrored) {
            matrices.method_22905(-1.0f, 1.0f, 1.0f);
        }
        class_811 context = this.handMirrored ? class_811.field_4322 : class_811.field_4321;
        class_759 held = class_310.method_1551().method_1561().method_43336();
        held.method_3233((class_1309)player, item, context, matrices, queue, light);
        matrices.method_22909();
    }

    public boolean renderFirstPerson(class_742 player, class_1268 hand, class_4587 matrices, class_11659 queue, int light, float equipProgress) {
        MeleeSkin skin = MeleeRegistry.equipped();
        if (skin == null) {
            return false;
        }
        class_1306 arm = hand == class_1268.field_5808 ? player.method_6068() : player.method_6068().method_5928();
        boolean mirrored = arm == class_1306.field_6182;
        boolean primary = !this.primaryClaimed;
        MeleeController controller = primary ? MeleeController.getInstance() : MeleeController.offHand();
        BakedMeleeModel model = controller.prepare(player, hand);
        if (model == null) {
            return false;
        }
        BoneMatrices bones = primary ? this.primaryBones : this.secondaryBones;
        matrices.method_22903();
        matrices.method_46416(0.0f, equipProgress * -0.6f, 0.0f);
        if (mirrored) {
            matrices.method_22905(-1.0f, 1.0f, 1.0f);
        }
        this.applyCamera(model, controller, matrices);
        this.applyViewAnchor(model, matrices);
        this.buildWorld(bones, model, controller.pose(), controller.hidden());
        this.submitGeometry(bones, model, skin, matrices, queue, light, mirrored);
        if (primary) {
            this.submitArm(bones, model, model.getRightHandBone(), true, mirrored, player, matrices, queue, light);
            this.submitArm(bones, model, model.getLeftHandBone(), false, mirrored, player, matrices, queue, light);
        }
        matrices.method_22909();
        if (primary) {
            this.primaryClaimed = true;
            this.handMirrored = mirrored;
            this.handModel = model;
        }
        return true;
    }

    public boolean renderThirdPerson(class_742 player, class_1306 arm, class_4587 matrices, class_11659 queue, int light) {
        MeleeSkin skin = MeleeRegistry.equipped();
        BakedMeleeModel model = MeleePreloader.get(skin);
        if (skin == null || model == null) {
            return false;
        }
        MeleeHandTransform transform = arm == class_1306.field_6182 ? skin.getThirdPersonLeftHand() : skin.getThirdPersonRightHand();
        matrices.method_22903();
        matrices.method_46416((arm == class_1306.field_6182 ? -1.0f : 1.0f) / 16.0f, 0.125f, -0.625f);
        float[] translation = transform.translation();
        matrices.method_46416(translation[0] / 16.0f, translation[1] / 16.0f, translation[2] / 16.0f);
        float[] rotation = transform.rotation();
        matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(rotation[0]));
        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(rotation[1]));
        matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(rotation[2]));
        float[] scale = transform.scale();
        matrices.method_22905(scale[0], scale[1], scale[2]);
        matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(180.0f));
        Integer anchor = model.getBoneIndex().get("thirdperson_hand");
        if (anchor != null) {
            matrices.method_46416(-model.pivotX(anchor) / 16.0f, -model.pivotY(anchor) / 16.0f, -model.pivotZ(anchor) / 16.0f);
        }
        this.buildRest(this.secondaryBones, model);
        this.submitGeometry(this.secondaryBones, model, skin, matrices, queue, light, false);
        matrices.method_22909();
        return true;
    }

    private void buildRest(BoneMatrices bones, BakedMeleeModel model) {
        int count = model.boneCount();
        bones.ensureCapacity(count);
        for (int bone = 0; bone < count; ++bone) {
            bones.visible[bone] = true;
            this.local.identity();
            this.applyPivotRotation(model, bone, model.restRotationX(bone), model.restRotationY(bone), model.restRotationZ(bone), 1.0f, 1.0f, 1.0f);
            this.compose(bones, model, bone);
        }
        this.hideHands(bones, model);
    }

    private void applyCamera(BakedMeleeModel model, MeleeController controller, class_4587 matrices) {
        int camera = model.getCameraBone();
        if (camera < 0) {
            return;
        }
        int base = camera * 9;
        float[] pose = controller.pose();
        if (base + 9 > pose.length) {
            return;
        }
        matrices.method_46416(pose[base + 3] / 16.0f, -pose[base + 4] / 16.0f, -pose[base + 5] / 16.0f);
        matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(-pose[base + 2]));
        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(pose[base + 1]));
        matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(pose[base]));
    }

    private void applyViewAnchor(BakedMeleeModel model, class_4587 matrices) {
        int anchor = model.getViewAnchorBone();
        if (anchor < 0) {
            return;
        }
        matrices.method_46416(-model.pivotX(anchor) / 16.0f, -model.pivotY(anchor) / 16.0f, -model.pivotZ(anchor) / 16.0f);
    }

    private void buildWorld(BoneMatrices bones, BakedMeleeModel model, float[] pose, boolean[] hidden) {
        int count = model.boneCount();
        bones.ensureCapacity(count);
        for (int bone = 0; bone < count; ++bone) {
            bones.visible[bone] = bone >= hidden.length || !hidden[bone];
            int base = bone * 9;
            this.local.identity();
            this.local.translate(-pose[base + 3] / 16.0f, pose[base + 4] / 16.0f, pose[base + 5] / 16.0f);
            this.applyPivotRotation(model, bone, model.restRotationX(bone) - pose[base] * ((float)Math.PI / 180), model.restRotationY(bone) - pose[base + 1] * ((float)Math.PI / 180), model.restRotationZ(bone) + pose[base + 2] * ((float)Math.PI / 180), pose[base + 6], pose[base + 7], pose[base + 8]);
            this.compose(bones, model, bone);
        }
        this.hideHands(bones, model);
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

    private void compose(BoneMatrices bones, BakedMeleeModel model, int bone) {
        int parent = model.parentOf(bone);
        if (parent < 0) {
            bones.world[bone].set((Matrix4fc)this.local);
        } else {
            bones.world[bone].set((Matrix4fc)bones.world[parent]).mul((Matrix4fc)this.local);
        }
    }

    private void hideHands(BoneMatrices bones, BakedMeleeModel model) {
        if (model.getRightHandBone() >= 0) {
            bones.visible[model.getRightHandBone()] = false;
        }
        if (model.getLeftHandBone() >= 0) {
            bones.visible[model.getLeftHandBone()] = false;
        }
    }

    private void submitGeometry(BoneMatrices bones, BakedMeleeModel model, MeleeSkin skin, class_4587 matrices, class_11659 queue, int light, boolean mirrored) {
        class_1921 layer = class_12249.method_76000((class_2960)skin.getTexture());
        this.submitPass(bones, model, layer, matrices, queue, light, false, mirrored);
    }

    private void submitPass(BoneMatrices bones, BakedMeleeModel model, class_1921 layer, class_4587 matrices, class_11659 queue, int light, boolean holyPass, boolean mirrored) {
        int count = model.boneCount();
        queue.method_73483(matrices, layer, (entry, vertices) -> {
            Matrix4f base = entry.method_23761();
            Matrix3f baseNormals = entry.method_23762();
            Matrix4f combined = new Matrix4f();
            Matrix3f normals = new Matrix3f();
            Matrix3f scratch = new Matrix3f();
            Vector3f normal = new Vector3f();
            for (int bone = 0; bone < count; ++bone) {
                int cursor;
                float[] mesh;
                if (!bones.visible[bone] || (mesh = model.meshOf(bone)) == null) continue;
                combined.set((Matrix4fc)base).mul((Matrix4fc)bones.world[bone]);
                scratch.set((Matrix4fc)bones.world[bone]).invert().transpose();
                normals.set((Matrix3fc)baseNormals).mul((Matrix3fc)scratch);
                if (mirrored) {
                    for (cursor = mesh.length - 8; cursor >= 0; cursor -= 8) {
                        MeleeFirstPersonRenderer.submitVertex(vertices, mesh, cursor, light, combined, normals, normal);
                    }
                    continue;
                }
                for (cursor = 0; cursor < mesh.length; cursor += 8) {
                    MeleeFirstPersonRenderer.submitVertex(vertices, mesh, cursor, light, combined, normals, normal);
                }
            }
        });
    }

    private static void submitVertex(class_4588 vertices, float[] mesh, int cursor, int light, Matrix4f combined, Matrix3f normals, Vector3f normal) {
        normal.set(mesh[cursor + 5], mesh[cursor + 6], mesh[cursor + 7]).mul((Matrix3fc)normals).normalize();
        vertices.method_22918((Matrix4fc)combined, mesh[cursor], mesh[cursor + 1], mesh[cursor + 2]).method_22915(1.0f, 1.0f, 1.0f, 1.0f).method_22913(mesh[cursor + 3], mesh[cursor + 4]).method_22922(class_4608.field_21444).method_60803(light).method_22914(normal.x, normal.y, normal.z);
    }

    private void submitArm(BoneMatrices bones, BakedMeleeModel model, int bone, boolean right, boolean mirrored, class_742 player, class_4587 matrices, class_11659 queue, int light) {
        if (bone < 0 || bone >= bones.world.length) {
            return;
        }
        class_898 dispatcher = class_310.method_1551().method_1561();
        class_1007 renderer = dispatcher.method_74405(player);
        if (renderer == null) {
            return;
        }
        class_2960 skinTexture = player.method_52814().comp_1626().comp_3627();
        boolean useRight = right != mirrored;
        this.armPose.set((Matrix4fc)bones.world[bone]).translate(right ? -0.0625f : 0.0625f, 0.5f, 0.0f);
        matrices.method_22903();
        matrices.method_34425((Matrix4fc)this.armPose);
        if (mirrored) {
            matrices.method_22905(-1.0f, 1.0f, 1.0f);
        }
        if (useRight) {
            renderer.method_4220(matrices, queue, light, skinTexture, true);
        } else {
            renderer.method_4221(matrices, queue, light, skinTexture, true);
        }
        matrices.method_22909();
    }
}

