/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10055
 *  net.minecraft.class_11659
 *  net.minecraft.class_12249
 *  net.minecraft.class_1921
 *  net.minecraft.class_2960
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4608
 *  net.minecraft.class_7833
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package ru.decide.cosmetics.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_11659;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4608;
import net.minecraft.class_7833;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import ru.decide.cosmetics.render.CosmeticCapeData;
import ru.decide.cosmetics.render.CosmeticTextureInfo;
import ru.decide.mixin.cosmetics.PlayerCapeStateAccessor;

@Environment(value=EnvType.CLIENT)
public final class CosmeticCommandCapeRenderer {
    private static final float X0 = -0.3125f;
    private static final float X1 = 0.3125f;
    private static final float Y0 = 0.0f;
    private static final float Y1 = 1.0f;
    private static final float Z0 = -0.0625f;
    private static final float Z1 = 0.0f;
    private static final float GRID_U = 22.0f;
    private static final float GRID_V = 17.0f;

    public static void render(CosmeticCapeData cape, class_4587 matrices, class_11659 queue, int light, float timeSeconds, class_10055 state) {
        if (cape == null || cape.getTexture() == null) {
            return;
        }
        matrices.method_22903();
        matrices.method_22904(0.0, 0.0, 0.125);
        float capeLean = 0.0f;
        float capeFlap = 0.0f;
        float capeLean2 = 0.0f;
        if (state instanceof PlayerCapeStateAccessor) {
            PlayerCapeStateAccessor accessor = (PlayerCapeStateAccessor)state;
            capeLean = accessor.decide$capeLean();
            capeFlap = accessor.decide$capeFlap();
            capeLean2 = accessor.decide$capeLean2();
        }
        float lean = 6.0f + capeLean / 2.0f + capeFlap;
        float lean2 = capeLean2 / 2.0f;
        matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(lean));
        matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(lean2));
        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(180.0f - lean2));
        CapeUv uv = CosmeticCommandCapeRenderer.frameUv(cape, timeSeconds);
        class_1921 layer = class_12249.method_75994((class_2960)cape.getTexture());
        queue.method_73483(matrices, layer, (entry, consumer) -> CosmeticCommandCapeRenderer.emit(consumer, entry.method_23761(), uv, light));
        matrices.method_22909();
    }

    private static CapeUv frameUv(CosmeticCapeData cape, float timeSeconds) {
        int textureWidth = cape.getTextureWidth();
        int textureHeight = cape.getTextureHeight();
        if (cape.isAtlasFrame()) {
            if (cape.getFrameCount() > 1 && cape.getFrameHeight() > 0 && textureHeight > 0) {
                float frameSeconds = (float)Math.max(1, cape.getFrameTime()) / 20.0f;
                int frame = (int)(timeSeconds / frameSeconds) % cape.getFrameCount();
                float frameHeight = cape.getFrameHeight();
                return new CapeUv(0.0f, (float)frame * frameHeight / (float)textureHeight, 1.0f, (float)(frame + 1) * frameHeight / (float)textureHeight, true);
            }
            return new CapeUv(0.0f, 0.0f, 1.0f, 1.0f, true);
        }
        if (cape.getFrameCount() > 1 && cape.getFrameHeight() > 0 && textureHeight > 0) {
            float frameSeconds = (float)Math.max(1, cape.getFrameTime()) / 20.0f;
            int frame = (int)(timeSeconds / frameSeconds) % cape.getFrameCount();
            return new CapeUv(0.0f, (float)(frame * cape.getFrameHeight()) / (float)textureHeight, Math.min(1.0f, (float)cape.getFrameWidth() / (float)Math.max(1, textureWidth)), (float)((frame + 1) * cape.getFrameHeight()) / (float)textureHeight, false);
        }
        float[] bounds = CosmeticTextureInfo.contentBounds(cape.getTexture());
        if (bounds != null) {
            return new CapeUv(bounds[0], bounds[1], bounds[2], bounds[3], true);
        }
        if (textureWidth > 0 && textureHeight > 0 && Math.abs((float)textureWidth / (float)textureHeight - 2.0f) < 0.25f) {
            return new CapeUv(0.0f, 0.0f, 0.34f, 0.525f, true);
        }
        return new CapeUv(0.0f, 0.0f, 1.0f, 1.0f, false);
    }

    private static void emit(class_4588 vc, Matrix4f m, CapeUv uv, int light) {
        if (uv.atlas()) {
            CosmeticCommandCapeRenderer.emitAtlasCube(vc, m, uv, light);
        } else {
            CosmeticCommandCapeRenderer.emitFlatCape(vc, m, uv, light);
        }
    }

    private static void emitAtlasCube(class_4588 vc, Matrix4f m, CapeUv uv, int light) {
        float du = (uv.u1() - uv.u0()) / 22.0f;
        float dv = (uv.v1() - uv.v0()) / 17.0f;
        CosmeticCommandCapeRenderer.quad(vc, m, 0.3125f, 0.0f, -0.0625f, -0.3125f, 0.0f, -0.0625f, -0.3125f, 1.0f, -0.0625f, 0.3125f, 1.0f, -0.0625f, uv.u0() + 11.0f * du, uv.v0() + 1.0f * dv, uv.u0() + 1.0f * du, uv.v0() + 1.0f * dv, uv.u0() + 1.0f * du, uv.v0() + 17.0f * dv, uv.u0() + 11.0f * du, uv.v0() + 17.0f * dv, 0.0f, 0.0f, -1.0f, light);
        CosmeticCommandCapeRenderer.quad(vc, m, -0.3125f, 0.0f, 0.0f, 0.3125f, 0.0f, 0.0f, 0.3125f, 1.0f, 0.0f, -0.3125f, 1.0f, 0.0f, uv.u0() + 22.0f * du, uv.v0() + 1.0f * dv, uv.u0() + 12.0f * du, uv.v0() + 1.0f * dv, uv.u0() + 12.0f * du, uv.v0() + 17.0f * dv, uv.u0() + 22.0f * du, uv.v0() + 17.0f * dv, 0.0f, 0.0f, 1.0f, light);
        CosmeticCommandCapeRenderer.quad(vc, m, -0.3125f, 0.0f, -0.0625f, -0.3125f, 0.0f, 0.0f, -0.3125f, 1.0f, 0.0f, -0.3125f, 1.0f, -0.0625f, uv.u0() + 1.0f * du, uv.v0() + 1.0f * dv, uv.u0(), uv.v0() + 1.0f * dv, uv.u0(), uv.v0() + 17.0f * dv, uv.u0() + 1.0f * du, uv.v0() + 17.0f * dv, -1.0f, 0.0f, 0.0f, light);
        CosmeticCommandCapeRenderer.quad(vc, m, 0.3125f, 0.0f, 0.0f, 0.3125f, 0.0f, -0.0625f, 0.3125f, 1.0f, -0.0625f, 0.3125f, 1.0f, 0.0f, uv.u0() + 12.0f * du, uv.v0() + 1.0f * dv, uv.u0() + 11.0f * du, uv.v0() + 1.0f * dv, uv.u0() + 11.0f * du, uv.v0() + 17.0f * dv, uv.u0() + 12.0f * du, uv.v0() + 17.0f * dv, 1.0f, 0.0f, 0.0f, light);
        CosmeticCommandCapeRenderer.quad(vc, m, 0.3125f, 1.0f, -0.0625f, -0.3125f, 1.0f, -0.0625f, -0.3125f, 1.0f, 0.0f, 0.3125f, 1.0f, 0.0f, uv.u0() + 21.0f * du, uv.v0() + 1.0f * dv, uv.u0() + 11.0f * du, uv.v0() + 1.0f * dv, uv.u0() + 11.0f * du, uv.v0(), uv.u0() + 21.0f * du, uv.v0(), 0.0f, 1.0f, 0.0f, light);
        CosmeticCommandCapeRenderer.quad(vc, m, 0.3125f, 0.0f, 0.0f, -0.3125f, 0.0f, 0.0f, -0.3125f, 0.0f, -0.0625f, 0.3125f, 0.0f, -0.0625f, uv.u0() + 11.0f * du, uv.v0() + 1.0f * dv, uv.u0() + 1.0f * du, uv.v0() + 1.0f * dv, uv.u0() + 1.0f * du, uv.v0(), uv.u0() + 11.0f * du, uv.v0(), 0.0f, -1.0f, 0.0f, light);
    }

    private static void emitFlatCape(class_4588 vc, Matrix4f m, CapeUv uv, int light) {
        float u0 = uv.u0();
        float v0 = uv.v0();
        float u1 = uv.u1();
        float v1 = uv.v1();
        float edge = Math.max(4.8828125E-4f, (u1 - u0) * 0.02f);
        float dv = v1 - v0;
        float strip = dv * 0.1f;
        CosmeticCommandCapeRenderer.quad(vc, m, 0.3125f, 0.0f, -0.0625f, -0.3125f, 0.0f, -0.0625f, -0.3125f, 1.0f, -0.0625f, 0.3125f, 1.0f, -0.0625f, u1, v0, u0, v0, u0, v1, u1, v1, 0.0f, 0.0f, -1.0f, light);
        CosmeticCommandCapeRenderer.quad(vc, m, -0.3125f, 0.0f, 0.0f, 0.3125f, 0.0f, 0.0f, 0.3125f, 1.0f, 0.0f, -0.3125f, 1.0f, 0.0f, u1, v0, u0, v0, u0, v1, u1, v1, 0.0f, 0.0f, 1.0f, light);
        CosmeticCommandCapeRenderer.quad(vc, m, -0.3125f, 0.0f, -0.0625f, -0.3125f, 0.0f, 0.0f, -0.3125f, 1.0f, 0.0f, -0.3125f, 1.0f, -0.0625f, u0, v0, u0 + edge, v0, u0 + edge, v1, u0, v1, -1.0f, 0.0f, 0.0f, light);
        CosmeticCommandCapeRenderer.quad(vc, m, 0.3125f, 0.0f, 0.0f, 0.3125f, 0.0f, -0.0625f, 0.3125f, 1.0f, -0.0625f, 0.3125f, 1.0f, 0.0f, u1, v0, u1 - edge, v0, u1 - edge, v1, u1, v1, 1.0f, 0.0f, 0.0f, light);
        CosmeticCommandCapeRenderer.quad(vc, m, 0.3125f, 1.0f, -0.0625f, -0.3125f, 1.0f, -0.0625f, -0.3125f, 1.0f, 0.0f, 0.3125f, 1.0f, 0.0f, u1 - edge, v0 + strip, u0 + edge, v0 + strip, u0 + edge, v0, u1 - edge, v0, 0.0f, 1.0f, 0.0f, light);
        CosmeticCommandCapeRenderer.quad(vc, m, 0.3125f, 0.0f, 0.0f, -0.3125f, 0.0f, 0.0f, -0.3125f, 0.0f, -0.0625f, 0.3125f, 0.0f, -0.0625f, u1 - edge, v1 - strip, u0 + edge, v1 - strip, u0 + edge, v1, u1 - edge, v1, 0.0f, -1.0f, 0.0f, light);
    }

    private static void quad(class_4588 vc, Matrix4f m, float ax, float ay, float az, float bx, float by, float bz, float cx, float cy, float cz, float dx, float dy, float dz, float aU, float aV, float bU, float bV, float cU, float cV, float dU, float dV, float nx, float ny, float nz, int light) {
        CosmeticCommandCapeRenderer.vertex(vc, m, ax, ay, az, aU, aV, nx, ny, nz, light);
        CosmeticCommandCapeRenderer.vertex(vc, m, bx, by, bz, bU, bV, nx, ny, nz, light);
        CosmeticCommandCapeRenderer.vertex(vc, m, cx, cy, cz, cU, cV, nx, ny, nz, light);
        CosmeticCommandCapeRenderer.vertex(vc, m, dx, dy, dz, dU, dV, nx, ny, nz, light);
    }

    private static void vertex(class_4588 vc, Matrix4f m, float x, float y, float z, float u, float v, float nx, float ny, float nz, int light) {
        vc.method_22918((Matrix4fc)m, x, y, z).method_1336(255, 255, 255, 255).method_22913(u, v).method_22922(class_4608.field_21444).method_60803(light).method_22914(nx, ny, nz);
    }

    private CosmeticCommandCapeRenderer() {
    }

    @Environment(value=EnvType.CLIENT)
    private record CapeUv(float u0, float v0, float u1, float v1, boolean atlas) {
    }
}

