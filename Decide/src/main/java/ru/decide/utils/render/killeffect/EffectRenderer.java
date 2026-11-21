/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.opengl.GlStateManager
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10799
 *  net.minecraft.class_12247
 *  net.minecraft.class_1921
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4608
 *  net.minecraft.class_7833
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package ru.decide.utils.render.killeffect;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_7833;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import ru.decide.utils.render.killeffect.BBAnimation;
import ru.decide.utils.render.killeffect.BBModel;
import ru.decide.utils.render.killeffect.EffectManager;

@Environment(value=EnvType.CLIENT)
public final class EffectRenderer {
    private static final int FULL_LIGHT = 0xF000F0;

    private EffectRenderer() {
    }

    public static void render(class_4587 matrices, class_4597 consumers, class_243 camera, EffectManager.ActiveEffect effect) {
        BBModel model = effect.effect().model();
        if (model == null) {
            return;
        }
        matrices.method_22903();
        matrices.method_22904(effect.position().field_1352 - camera.field_1352, effect.position().field_1351 - camera.field_1351, effect.position().field_1350 - camera.field_1350);
        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(effect.spawnYaw() + 180.0f));
        float scale = effect.effect().scale();
        if (scale != 1.0f) {
            matrices.method_22905(scale, scale, scale);
        }
        GlStateManager._enableBlend();
        GlStateManager._blendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        float seconds = effect.timeSeconds();
        BBAnimation animation = model.animations.isEmpty() ? null : model.animations.get(0);
        for (BBModel.Bone bone : model.roots) {
            EffectRenderer.renderBone(bone, matrices, consumers, model, animation, seconds, BBModel.Vec.ZERO, effect.skinTexture());
        }
        GlStateManager._disableBlend();
        matrices.method_22909();
    }

    private static void renderBone(BBModel.Bone bone, class_4587 matrices, class_4597 consumers, BBModel model, BBAnimation animation, float seconds, BBModel.Vec parentOrigin, class_2960 skinTexture) {
        if (!bone.visible()) {
            return;
        }
        BBAnimation.Transform animated = BBAnimation.Transform.IDENTITY;
        if (animation != null) {
            animated = animation.sample(bone.uuid(), seconds);
        }
        matrices.method_22903();
        BBModel.Vec relative = bone.origin().sub(parentOrigin).add(animated.position);
        matrices.method_46416(relative.x() / 16.0f, relative.y() / 16.0f, relative.z() / 16.0f);
        EffectRenderer.rotate(matrices, bone.rotation().add(animated.rotation));
        BBModel.Vec s = animated.scale;
        matrices.method_22905(s.x(), s.y(), s.z());
        for (BBModel.Cube cube : bone.cubes()) {
            EffectRenderer.renderCube(cube, bone.origin(), matrices, consumers, model, skinTexture);
        }
        for (BBModel.Bone child : bone.children()) {
            EffectRenderer.renderBone(child, matrices, consumers, model, animation, seconds, bone.origin(), skinTexture);
        }
        matrices.method_22909();
    }

    private static void renderCube(BBModel.Cube cube, BBModel.Vec boneOrigin, class_4587 matrices, class_4597 consumers, BBModel model, class_2960 skinTexture) {
        matrices.method_22903();
        BBModel.Vec pivot = cube.origin().sub(boneOrigin);
        matrices.method_46416(pivot.x() / 16.0f, pivot.y() / 16.0f, pivot.z() / 16.0f);
        EffectRenderer.rotate(matrices, cube.rotation());
        float x1 = (Math.min(cube.from().x(), cube.to().x()) - cube.origin().x()) / 16.0f;
        float y1 = (Math.min(cube.from().y(), cube.to().y()) - cube.origin().y()) / 16.0f;
        float z1 = (Math.min(cube.from().z(), cube.to().z()) - cube.origin().z()) / 16.0f;
        float x2 = (Math.max(cube.from().x(), cube.to().x()) - cube.origin().x()) / 16.0f;
        float y2 = (Math.max(cube.from().y(), cube.to().y()) - cube.origin().y()) / 16.0f;
        float z2 = (Math.max(cube.from().z(), cube.to().z()) - cube.origin().z()) / 16.0f;
        for (BBModel.Face face : cube.faces()) {
            boolean degenerate;
            float width = x2 - x1;
            float height = y2 - y1;
            float depth = z2 - z1;
            if (degenerate = (switch (face.direction()) {
                case "north", "south" -> {
                    if (width < 5.0E-4f || height < 5.0E-4f) {
                        yield true;
                    }
                    yield false;
                }
                case "east", "west" -> {
                    if (height < 5.0E-4f || depth < 5.0E-4f) {
                        yield true;
                    }
                    yield false;
                }
                case "up", "down" -> {
                    if (width < 5.0E-4f || depth < 5.0E-4f) {
                        yield true;
                    }
                    yield false;
                }
                default -> true;
            })) continue;
            EffectRenderer.renderFace(face, x1, y1, z1, x2, y2, z2, matrices, consumers, model, skinTexture);
        }
        matrices.method_22909();
    }

    private static void renderFace(BBModel.Face face, float x1, float y1, float z1, float x2, float y2, float z2, class_4587 matrices, class_4597 consumers, BBModel model, class_2960 skinTexture) {
        float[][] positions;
        float nx = 0.0f;
        float ny = 0.0f;
        float nz = 0.0f;
        switch (face.direction()) {
            case "north": {
                positions = new float[][]{{x2, y2, z1}, {x2, y1, z1}, {x1, y1, z1}, {x1, y2, z1}};
                nz = -1.0f;
                break;
            }
            case "south": {
                positions = new float[][]{{x1, y2, z2}, {x1, y1, z2}, {x2, y1, z2}, {x2, y2, z2}};
                nz = 1.0f;
                break;
            }
            case "east": {
                positions = new float[][]{{x2, y2, z2}, {x2, y1, z2}, {x2, y1, z1}, {x2, y2, z1}};
                nx = 1.0f;
                break;
            }
            case "west": {
                positions = new float[][]{{x1, y2, z1}, {x1, y1, z1}, {x1, y1, z2}, {x1, y2, z2}};
                nx = -1.0f;
                break;
            }
            case "up": {
                positions = new float[][]{{x1, y2, z1}, {x1, y2, z2}, {x2, y2, z2}, {x2, y2, z1}};
                ny = 1.0f;
                break;
            }
            case "down": {
                positions = new float[][]{{x1, y1, z2}, {x1, y1, z1}, {x2, y1, z1}, {x2, y1, z2}};
                ny = -1.0f;
                break;
            }
            default: {
                return;
            }
        }
        if (face.texture() < 0 || model.textures.isEmpty()) {
            return;
        }
        int texIndex = Math.min(model.textures.size() - 1, face.texture());
        BBModel.Texture texture = model.textures.get(texIndex);
        class_2960 textureId = texture.id();
        if (skinTexture != null && EffectRenderer.isBodyTexture(texture)) {
            textureId = skinTexture;
        }
        class_1921 layer = EffectRenderer.entityCutout(textureId);
        class_4588 vertices = consumers.method_73477(layer);
        float uvW = texture.uvWidth();
        float uvH = texture.uvHeight();
        if (skinTexture != null && EffectRenderer.isBodyTexture(texture)) {
            uvW = 64.0f;
            uvH = 64.0f;
        }
        float u1 = face.u1() / uvW;
        float v1 = face.v1() / uvH;
        float u2 = face.u2() / uvW;
        float v2 = face.v2() / uvH;
        float[][] baseUv = new float[][]{{u1, v1}, {u1, v2}, {u2, v2}, {u2, v1}};
        int uvTurns = Math.floorMod(face.rotation() / 90, 4);
        float[][] uv = new float[4][];
        for (int i = 0; i < 4; ++i) {
            uv[i] = baseUv[(i + uvTurns) % 4];
        }
        Matrix4f mat = matrices.method_23760().method_23761();
        for (int i = 0; i < 4; ++i) {
            vertices.method_22918((Matrix4fc)mat, positions[i][0], positions[i][1], positions[i][2]).method_1336(255, 255, 255, 255).method_22913(uv[i][0], uv[i][1]).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(nx, ny, nz);
        }
    }

    private static boolean isBodyTexture(BBModel.Texture texture) {
        String name = texture.name().toLowerCase();
        return name.contains("body_grey") || name.contains("body_gray") || name.contains("player_body");
    }

    private static void rotate(class_4587 matrices, BBModel.Vec rotation) {
        if (rotation.z() != 0.0f) {
            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(rotation.z()));
        }
        if (rotation.y() != 0.0f) {
            matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(rotation.y()));
        }
        if (rotation.x() != 0.0f) {
            matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(rotation.x()));
        }
    }

    private static class_1921 entityCutout(class_2960 textureId) {
        class_12247 renderSetup = class_12247.method_75927((RenderPipeline)class_10799.field_56904).method_75934("Sampler0", textureId).method_75929(262144).method_75938();
        return class_1921.method_75940((String)"killeffect_entity_cutout", (class_12247)renderSetup);
    }
}

