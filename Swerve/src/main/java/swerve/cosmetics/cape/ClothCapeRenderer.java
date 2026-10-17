/*
 * This file is part of Arcanic Visuals (https://github.com/arcanic-su/arcanic)
 *
 * Copyright (c) 2026 Arcanic
 *
 * Arcanic Visuals is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Arcanic Visuals is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Arcanic Visuals. If not, see <https://www.gnu.org/licenses/>.
 */
package swerve.cosmetics.cape;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Плащ как 16 гнутых сегментов по физике ткани, с лёгким покачиванием на ветру.
 */
public class ClothCapeRenderer {
    private static final int PART_COUNT = 16;
    private static final float ALPHA = 1.0F;
    private static final float WIND_AMPLITUDE = 1.5F;
    private static final float ARMOR_Z_OFFSET = 0.1F;
    private static final int FULL_BRIGHT = 15728880;
    private static final float HALF_WIDTH = 0.3125F;

    private static final float DEPTH = 0.0625F;
    private static final float SEGMENT = 0.0625F;
    private static final float SEGMENT_V = 0.03125F;

    private static ClothCapeRenderer instance;

    public static ClothCapeRenderer getInstance() {
        if (instance == null) {
            instance = new ClothCapeRenderer();
        }
        return instance;
    }

    // Всё считается на заранее выделенных объектах: в кадре рендера нет новых объектов.
    private final Matrix4f[] segments = new Matrix4f[PART_COUNT];
    private final Vector3f[] frontNormals = new Vector3f[PART_COUNT];
    private final Vector3f[] backNormals = new Vector3f[PART_COUNT];
    private final Vector3f origin = new Vector3f();
    private final Vector3f edgeA = new Vector3f();
    private final Vector3f edgeB = new Vector3f();
    private final Vector3f normal = new Vector3f();
    private final Vector3f upper = new Vector3f();
    private final Vector3f lower = new Vector3f();
    private final Vector4f scratch4 = new Vector4f();
    private final Quaternionf rotX = new Quaternionf();
    private final Quaternionf rotY = new Quaternionf();

    public ClothCapeRenderer() {
        for (int i = 0; i < PART_COUNT; i++) {
            segments[i] = new Matrix4f();
            frontNormals[i] = new Vector3f();
            backNormals[i] = new Vector3f();
        }
    }

    public void render(
            PoseStack pose, MultiBufferSource buffer, int light, AbstractClientPlayer player, PlayerModel model) {
        ResourceLocation texture = Capes.resolve(player);
        if (texture == null) return;
        ClothSimulation sim = ClothSimulations.getInstance().simulationFor(player);
        if (sim == null) return;
        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        boolean underwater = player.isUnderWater();
        pose.pushPose();
        model.root().translateAndRotate(pose);
        model.body.translateAndRotate(pose);
        if (!player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            pose.translate(0.0, 0.0, ARMOR_Z_OFFSET); // поверх нагрудника
        }
        RenderType type = RenderType.entityTranslucent(texture);
        VertexConsumer consumer = buffer.getBuffer(type);
        renderMesh(pose.last().pose(), consumer, sim, partialTick, light, underwater);
        pose.popPose();
    }

    private void renderMesh(
            Matrix4f body, VertexConsumer consumer, ClothSimulation sim, float partialTick, int light, boolean underwater) {
        // на полной яркости грани освещаются с обратной стороны — нормали переворачиваются
        boolean flip = light == FULL_BRIGHT;

        for (int i = 0; i < PART_COUNT; i++) {
            applySegmentPose(segments[i], body, sim, partialTick, i, underwater);
            Matrix4f current = segments[i];
            Matrix4f previous = segments[Math.max(i - 1, 0)];
            frontNormals[i].set(normalVec(
                    previous,
                    previous,
                    current,
                    HALF_WIDTH, i * SEGMENT, -DEPTH,
                    -HALF_WIDTH, i * SEGMENT, -DEPTH,
                    HALF_WIDTH, (i + 1) * SEGMENT, -DEPTH,
                    flip));
            backNormals[i].set(normalVec(
                    previous,
                    previous,
                    current,
                    HALF_WIDTH, (i + 1) * SEGMENT, 0.0F,
                    -HALF_WIDTH, (i + 1) * SEGMENT, 0.0F,
                    HALF_WIDTH, i * SEGMENT, 0.0F,
                    flip));
        }

        for (int i = 0; i < PART_COUNT; i++) {
            Matrix4f current = segments[i];
            Matrix4f previous = segments[Math.max(i - 1, 0)];

            if (i == 0) { // верхний край
                float u0 = 0.015625F, u1 = 0.171875F, v0 = 0.0F, v1 = 0.03125F;
                normal.set(normalVec(
                        segments[0],
                        segments[0],
                        segments[0],
                        HALF_WIDTH, 0.0F, 0.0F,
                        -HALF_WIDTH, 0.0F, 0.0F,
                        HALF_WIDTH, 0.0F, DEPTH,
                        flip));
                vertex(consumer, segments[0], HALF_WIDTH, 0.0F, 0.0F, u1, v1, light);
                vertex(consumer, segments[0], -HALF_WIDTH, 0.0F, 0.0F, u0, v1, light);
                vertex(consumer, segments[0], -HALF_WIDTH, 0.0F, -DEPTH, u0, v0, light);
                vertex(consumer, segments[0], HALF_WIDTH, 0.0F, -DEPTH, u1, v0, light);
            }

            if (i == PART_COUNT - 1) { // нижний край
                float u0 = 0.171875F, u1 = 0.328125F, v0 = 0.0F, v1 = 0.03125F;
                normal.set(normalVec(
                        current,
                        current,
                        current,
                        HALF_WIDTH, 1.0F, -DEPTH,
                        -HALF_WIDTH, 1.0F, -DEPTH,
                        HALF_WIDTH, 1.0F, 0.0F,
                        flip));
                vertex(consumer, current, HALF_WIDTH, 1.0F, -DEPTH, u1, v0, light);
                vertex(consumer, current, -HALF_WIDTH, 1.0F, -DEPTH, u0, v0, light);
                vertex(consumer, current, -HALF_WIDTH, 1.0F, 0.0F, u0, v1, light);
                vertex(consumer, current, HALF_WIDTH, 1.0F, 0.0F, u1, v1, light);
            }

            float vTop = SEGMENT_V * (i + 1);
            float vBottom = vTop + SEGMENT_V;
            float top = i * SEGMENT;
            float bottom = (i + 1) * SEGMENT;

            // левая сторона
            float u0 = 0.0F;
            float u1 = 0.015625F;
            normal.set(normalVec(
                    current,
                    current,
                    previous,
                    -HALF_WIDTH, bottom, 0.0F,
                    -HALF_WIDTH, bottom, -DEPTH,
                    -HALF_WIDTH, top, 0.0F,
                    flip));
            vertex(consumer, current, -HALF_WIDTH, bottom, 0.0F, u0, vBottom, light);
            vertex(consumer, current, -HALF_WIDTH, bottom, -DEPTH, u1, vBottom, light);
            vertex(consumer, previous, -HALF_WIDTH, top, -DEPTH, u1, vTop, light);
            vertex(consumer, previous, -HALF_WIDTH, top, 0.0F, u0, vTop, light);

            // правая сторона
            u0 = 0.171875F;
            u1 = 0.1875F;
            normal.set(normalVec(
                    current,
                    current,
                    previous,
                    HALF_WIDTH, bottom, -DEPTH,
                    HALF_WIDTH, bottom, 0.0F,
                    HALF_WIDTH, top, -DEPTH,
                    flip));
            vertex(consumer, current, HALF_WIDTH, bottom, -DEPTH, u0, vBottom, light);
            vertex(consumer, current, HALF_WIDTH, bottom, 0.0F, u1, vBottom, light);
            vertex(consumer, previous, HALF_WIDTH, top, 0.0F, u1, vTop, light);
            vertex(consumer, previous, HALF_WIDTH, top, -DEPTH, u0, vTop, light);

            // лицо спереди: нормали сглажены с соседними сегментами
            u0 = 0.015625F;
            u1 = 0.171875F;
            average(upper, frontNormals[i], frontNormals[Math.max(i - 1, 0)]);
            average(lower, frontNormals[i], frontNormals[Math.min(i + 1, PART_COUNT - 1)]);
            vertex(consumer, previous, HALF_WIDTH, top, -DEPTH, u1, vTop, light, upper);
            vertex(consumer, previous, -HALF_WIDTH, top, -DEPTH, u0, vTop, light, upper);
            vertex(consumer, current, -HALF_WIDTH, bottom, -DEPTH, u0, vBottom, light, lower);
            vertex(consumer, current, HALF_WIDTH, bottom, -DEPTH, u1, vBottom, light, lower);

            // лицо сзади
            u0 = 0.1875F;
            u1 = 0.34375F;
            average(upper, backNormals[i], backNormals[Math.max(i - 1, 0)]);
            average(lower, backNormals[i], backNormals[Math.min(i + 1, PART_COUNT - 1)]);
            vertex(consumer, previous, HALF_WIDTH, top, 0.0F, u0, vTop, light, upper);
            vertex(consumer, previous, -HALF_WIDTH, top, 0.0F, u1, vTop, light, upper);
            vertex(consumer, current, -HALF_WIDTH, bottom, 0.0F, u1, vBottom, light, lower);
            vertex(consumer, current, HALF_WIDTH, bottom, 0.0F, u0, vBottom, light, lower);
        }
    }

    private void applySegmentPose(
            Matrix4f out, Matrix4f body, ClothSimulation sim, float partialTick, int part, boolean underwater) {
        out.set(body);
        out.translate(0.0, 0.0, 0.125);
        float dx = sim.lerpX(part, partialTick) - sim.lerpX(0, partialTick);
        if (dx > 0.0F) {
            dx = 0.0F; // плащ не заходит внутрь тела
        }
        float dy = sim.lerpY(0, partialTick) - part - sim.lerpY(part, partialTick);
        float dz = sim.lerpZ(0, partialTick) - sim.lerpZ(part, partialTick);
        float bend = getRotation(partialTick, part, sim);
        float wind = naturalWindSwing(part, underwater);
        out.mulPose(rotX.rotationDegrees(6.0F + wind));
        out.mulPose(rotY.rotationDegrees(180.0F));
        out.translate(-dz / 16.0F, dy / 16.0F, dx / 16.0F);
        out.translate(0.0, 0.03, -0.03);
        out.translate(0.0F, part / 16.0F, 0.0F);
        out.mulPose(rotX.rotationDegrees(-bend));
        out.translate(0.0F, -part / 16.0F, 0.0F);
        out.translate(0.0, -0.03, 0.03);
    }

    private float getRotation(float partialTick, int part, ClothSimulation sim) {
        if (part == PART_COUNT - 1) {
            return getRotation(partialTick, part - 1, sim);
        }
        float x0 = sim.lerpX(part, partialTick);
        float y0 = sim.lerpY(part, partialTick);
        float x1 = sim.lerpX(part + 1, partialTick);
        float y1 = sim.lerpY(part + 1, partialTick);
        return (float) (Math.toDegrees(Math.atan2(x1 - x0, y1 - y0)) + 180.0);
    }

    private float naturalWindSwing(int part, boolean underwater) {
        long phase = System.currentTimeMillis() / (underwater ? 9 : 3) % 360L;
        float position = (part + 1) / 16.0F;
        return (float) (Math.sin(Math.toRadians(position * 360.0F - (float) phase)) * WIND_AMPLITUDE);
    }

    private static void average(Vector3f out, Vector3f a, Vector3f b) {
        out.set((a.x + b.x) / 2.0F, (a.y + b.y) / 2.0F, (a.z + b.z) / 2.0F);
    }

    /** Нормаль грани: через три точки, с переворотом на полной яркости. */
    private Vector3f normalVec(
            Matrix4f matA,
            Matrix4f matB,
            Matrix4f matC,
            float ax,
            float ay,
            float az,
            float bx,
            float by,
            float bz,
            float cx,
            float cy,
            float cz,
            boolean flip) {
        origin.set(ax, ay, az);
        edgeA.set(bx, by, bz);
        edgeB.set(cx, cy, cz);
        transform(matA, origin);
        transform(matB, edgeA);
        transform(matC, edgeB);
        edgeA.sub(origin);
        edgeB.sub(origin);
        edgeA.cross(edgeB);
        edgeA.normalize();
        if (flip) {
            edgeA.mul(-1.0F);
        }
        return edgeA;
    }

    private void transform(Matrix4f matrix, Vector3f point) {
        scratch4.set(point.x, point.y, point.z, 1.0F);
        matrix.transform(scratch4);
        point.set(scratch4.x, scratch4.y, scratch4.z);
    }

    private void vertex(
            VertexConsumer consumer,
            Matrix4f matrix,
            float x,
            float y,
            float z,
            float u,
            float v,
            int light) {
        vertex(consumer, matrix, x, y, z, u, v, light, normal);
    }

    private void vertex(
            VertexConsumer consumer,
            Matrix4f matrix,
            float x,
            float y,
            float z,
            float u,
            float v,
            int light,
            Vector3f normal) {
        consumer.addVertex(matrix, x, y, z)
                .setColor(ALPHA, ALPHA, ALPHA, ALPHA)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(normal.x, normal.y, normal.z);
    }
}
