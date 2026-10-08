package swerve.modules.impl.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import swerve.managers.FriendManager;
import swerve.managers.ModuleManager;
import swerve.managers.ThemeManager;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.utils.render.ShaderUtils;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class Trails extends Module {

    private static final int MAX_POINTS_PER_PLAYER = 192;
    private static final int MAX_SIDES = 16;
    private static final double MIN_STEP_SQ = 0.0016;
    private static final long MIN_STEP_MS = 25L;
    private static final float TAU = (float) (Math.PI * 2.0);

    private static final int SHAPE_SMOKE = 0;
    private static final int SHAPE_RIBBON = 1;
    private static final int SHAPE_TUBE = 2;
    private static final int SHAPE_HELIX = 3;
    private static final int SHAPE_DNA = 4;
    private static final int SHAPE_RINGS = 5;
    private static final int SHAPE_CROSS = 6;
    private static final int SHAPE_WINGS = 7;
    private static final int SHAPE_PARTICLES = 8;

    private static boolean eventsRegistered;

    private final ModeSetting shape = new ModeSetting("Форма", "Дым",
            "Дым", "Лента", "Труба", "Спираль", "ДНК", "Кольца", "Крест", "Крылья", "Частицы");
    private final ModeSetting colorMode = new ModeSetting("Окрас", "Тема", "Тема", "Радуга", "Астрал", "Пламя");
    private final FloatSetting length = new FloatSetting("Длина", 1.2F, 0.2F, 4.0F, 0.1F);
    private final FloatSetting height = new FloatSetting("Высота", 1.0F, 0.2F, 2.5F, 0.05F);
    private final FloatSetting thickness = new FloatSetting("Толщина", 0.35F, 0.05F, 1.5F, 0.05F)
            .visible(() -> !shape.is("Дым"));
    private final FloatSetting sides = new FloatSetting("Грани", 8.0F, 3.0F, 16.0F, 1.0F)
            .visible(() -> shape.is("Труба") || shape.is("Кольца"));
    private final FloatSetting twist = new FloatSetting("Витки", 3.0F, 0.5F, 12.0F, 0.5F)
            .visible(() -> shape.is("Спираль") || shape.is("ДНК"));
    private final FloatSetting density = new FloatSetting("Плотность", 6.0F, 1.0F, 16.0F, 1.0F)
            .visible(() -> shape.is("Кольца") || shape.is("Частицы"));
    private final FloatSetting speed = new FloatSetting("Скорость", 1.0F, 0.1F, 4.0F, 0.1F);
    private final FloatSetting frequency = new FloatSetting("Частота", 4.0F, 0.5F, 16.0F, 0.5F);
    private final FloatSetting glow = new FloatSetting("Свечение", 1.0F, 0.0F, 3.0F, 0.1F);
    private final FloatSetting opacity = new FloatSetting("Прозрачность", 0.85F, 0.05F, 1.0F, 0.05F);
    private final FloatSetting distort = new FloatSetting("Искажение", 0.5F, 0.0F, 1.5F, 0.05F);
    private final BooleanSetting additive = new BooleanSetting("Аддитивность", true);
    private final BooleanSetting showOthers = new BooleanSetting("На Других", true);
    private final BooleanSetting showFriends = new BooleanSetting("На Друзьях", true);

    private final Map<UUID, ArrayDeque<TrailPoint>> trails = new HashMap<>();

    private final float[] px = new float[MAX_POINTS_PER_PLAYER];
    private final float[] py = new float[MAX_POINTS_PER_PLAYER];
    private final float[] pz = new float[MAX_POINTS_PER_PLAYER];
    private final float[] pc = new float[MAX_POINTS_PER_PLAYER];
    private final int[] pa = new int[MAX_POINTS_PER_PLAYER];
    private final float[] tanX = new float[MAX_POINTS_PER_PLAYER];
    private final float[] tanY = new float[MAX_POINTS_PER_PLAYER];
    private final float[] tanZ = new float[MAX_POINTS_PER_PLAYER];
    private final float[] rgtX = new float[MAX_POINTS_PER_PLAYER];
    private final float[] rgtY = new float[MAX_POINTS_PER_PLAYER];
    private final float[] rgtZ = new float[MAX_POINTS_PER_PLAYER];
    private final float[] upX = new float[MAX_POINTS_PER_PLAYER];
    private final float[] upY = new float[MAX_POINTS_PER_PLAYER];
    private final float[] upZ = new float[MAX_POINTS_PER_PLAYER];
    private final float[] ringSin = new float[MAX_SIDES + 1];
    private final float[] ringCos = new float[MAX_SIDES + 1];

    private int ringSides;

    public Trails() {
        super("Trails", "Объёмный дымный след за игроками", ModuleCategory.RENDER);
        addSettings(shape, colorMode, length, height, thickness, sides, twist, density,
                speed, frequency, glow, opacity, distort, additive, showOthers, showFriends);
        registerEvents();
    }

    private static void registerEvents() {
        if (eventsRegistered) {
            return;
        }
        eventsRegistered = true;
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            Trails module = ModuleManager.get(Trails.class);
            if (module != null && module.isEnable()) {
                module.onWorldRender(context);
            }
        });
    }

    @Override
    public void onDisable() {
        trails.clear();
        super.onDisable();
    }

    private void onWorldRender(WorldRenderContext context) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        PoseStack poseStack = context.matrixStack();
        if (poseStack == null) {
            return;
        }

        CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.TRAIL);
        if (shader == null) {
            return;
        }

        long now = System.currentTimeMillis();
        long lifetimeMs = (long) (length.get() * 1000.0F);
        float partialTick = context.tickCounter().getGameTimeDeltaPartialTick(true);
        UUID selfId = minecraft.player.getUUID();

        collectPoints(minecraft, now, partialTick);
        expirePoints(now, lifetimeMs);

        if (trails.isEmpty()) {
            return;
        }

        boolean firstPerson = minecraft.options.getCameraType().isFirstPerson();
        Vec3 camera = context.camera().getPosition();
        int shapeId = shape.getIndex();

        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);
        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.enableBlend();
        if (additive.get()) {
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        } else {
            RenderSystem.defaultBlendFunc();
        }
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);

        RenderSystem.setShader(ShaderUtils.TRAIL);

        float timeSec = (now % 1000000L) / 1000.0F;
        ShaderUtils.set(shader, "Time", timeSec);
        ShaderUtils.set(shader, "Speed", speed.get());
        ShaderUtils.set(shader, "Frequency", frequency.get());
        ShaderUtils.set(shader, "Glow", glow.get());
        ShaderUtils.set(shader, "Opacity", opacity.get());
        ShaderUtils.set(shader, "Distort", distort.get());
        ShaderUtils.set(shader, "Closed", shapeId == SHAPE_TUBE || shapeId == SHAPE_RINGS ? 1 : 0);
        ShaderUtils.set(shader, "ColorMode", colorMode.getIndex());
        ShaderUtils.setColor(shader, "BaseColor", ThemeManager.getColor());
        ShaderUtils.setColor(shader, "AccentColor", ThemeManager.getColor(1));

        int sideCount = (int) sides.get();
        if (shapeId == SHAPE_TUBE || shapeId == SHAPE_RINGS) {
            prepareRing(sideCount);
        }

        float heightScale = height.get();
        float lifetime = Math.max(1.0F, lifetimeMs);
        float radius = thickness.get();
        int step = (int) density.get();

        for (Map.Entry<UUID, ArrayDeque<TrailPoint>> entry : trails.entrySet()) {
            // Свой след пишем всегда, но рисуем только в 3-м лице: в 1-м он размазывается по камере.
            if (firstPerson && entry.getKey().equals(selfId)) {
                continue;
            }

            int n = fillArrays(entry.getValue(), now, lifetime, heightScale);
            if (n < 2) {
                continue;
            }

            boolean billboard = shapeId == SHAPE_RIBBON || shapeId == SHAPE_HELIX
                    || shapeId == SHAPE_DNA || shapeId == SHAPE_PARTICLES;
            computeFrames(n, billboard, camera.x, camera.y, camera.z);

            switch (shapeId) {
                case SHAPE_RIBBON -> renderRibbon(matrix, n, radius);
                case SHAPE_TUBE -> renderTube(matrix, n, radius, sideCount);
                case SHAPE_HELIX -> renderHelix(matrix, n, radius, twist.get(), timeSec, 1);
                case SHAPE_DNA -> renderHelix(matrix, n, radius, twist.get(), timeSec, 2);
                case SHAPE_RINGS -> renderRings(matrix, n, radius, sideCount, step);
                case SHAPE_CROSS -> renderCross(matrix, n, radius);
                case SHAPE_WINGS -> renderWings(matrix, n, radius);
                case SHAPE_PARTICLES -> renderParticles(matrix, n, radius, step, camera.x, camera.y, camera.z);
                default -> renderSmoke(matrix, n);
            }
        }

        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private void collectPoints(Minecraft minecraft, long now, float partialTick) {
        for (AbstractClientPlayer player : minecraft.level.players()) {
            if (!player.isAlive() || player.isSpectator()) {
                continue;
            }

            if (player != minecraft.player) {
                boolean friend = FriendManager.isFriend(player);
                if (friend ? !showFriends.get() : !showOthers.get()) {
                    continue;
                }
            }

            double x = Mth.lerp(partialTick, player.xo, player.getX());
            double y = Mth.lerp(partialTick, player.yo, player.getY());
            double z = Mth.lerp(partialTick, player.zo, player.getZ());

            ArrayDeque<TrailPoint> points = trails.computeIfAbsent(player.getUUID(), uuid -> new ArrayDeque<>());
            TrailPoint last = points.peekLast();
            // Не пишем точку каждый кадр: иначе на месте стояния лента вырождается в один пиксель.
            if (last != null) {
                double dx = x - last.x();
                double dy = y - last.y();
                double dz = z - last.z();
                if (dx * dx + dy * dy + dz * dz < MIN_STEP_SQ && now - last.time() < MIN_STEP_MS) {
                    continue;
                }
            }

            points.addLast(new TrailPoint(x, y, z, player.getBbHeight(), now));
            while (points.size() > MAX_POINTS_PER_PLAYER) {
                points.pollFirst();
            }
        }
    }

    private void expirePoints(long now, long lifetimeMs) {
        Iterator<Map.Entry<UUID, ArrayDeque<TrailPoint>>> iterator = trails.entrySet().iterator();
        while (iterator.hasNext()) {
            ArrayDeque<TrailPoint> points = iterator.next().getValue();
            while (!points.isEmpty() && now - points.peekFirst().time() > lifetimeMs) {
                points.pollFirst();
            }
            if (points.isEmpty()) {
                iterator.remove();
            }
        }
    }

    private void prepareRing(int sideCount) {
        if (ringSides == sideCount) {
            return;
        }
        ringSides = sideCount;
        float stepAngle = TAU / sideCount;
        for (int i = 0; i <= sideCount; i++) {
            float angle = i * stepAngle;
            ringSin[i] = Mth.sin(angle);
            ringCos[i] = Mth.cos(angle);
        }
    }

    private int fillArrays(ArrayDeque<TrailPoint> points, long now, float lifetime, float heightScale) {
        int i = 0;
        for (TrailPoint point : points) {
            px[i] = (float) point.x();
            py[i] = (float) point.y();
            pz[i] = (float) point.z();
            pc[i] = (float) point.y() + point.height() * heightScale * 0.5F;
            float age = (now - point.time()) / lifetime;
            pa[i] = (int) (Mth.clamp(1.0F - age, 0.0F, 1.0F) * 255.0F);
            i++;
            if (i >= MAX_POINTS_PER_PLAYER) {
                break;
            }
        }
        return i;
    }

    // Строим локальный базис (касательная / право / верх) на каждой точке пути один раз за кадр
    private void computeFrames(int n, boolean billboard, double camX, double camY, double camZ) {
        float prevX = 0.0F;
        float prevY = 0.0F;
        float prevZ = 1.0F;

        for (int i = 0; i < n; i++) {
            int a = i > 0 ? i - 1 : i;
            int b = i < n - 1 ? i + 1 : i;

            float dx = px[b] - px[a];
            float dy = py[b] - py[a];
            float dz = pz[b] - pz[a];
            float len = Mth.sqrt(dx * dx + dy * dy + dz * dz);
            if (len < 1.0E-4F) {
                dx = prevX;
                dy = prevY;
                dz = prevZ;
            } else {
                float inv = 1.0F / len;
                dx *= inv;
                dy *= inv;
                dz *= inv;
            }
            prevX = dx;
            prevY = dy;
            prevZ = dz;
            tanX[i] = dx;
            tanY[i] = dy;
            tanZ[i] = dz;

            float ax;
            float ay;
            float az;
            if (billboard) {
                ax = (float) (px[i] - camX);
                ay = (float) (pc[i] - camY);
                az = (float) (pz[i] - camZ);
            } else {
                ax = 0.0F;
                ay = 1.0F;
                az = 0.0F;
            }

            float rx = dy * az - dz * ay;
            float ry = dz * ax - dx * az;
            float rz = dx * ay - dy * ax;
            float rl = Mth.sqrt(rx * rx + ry * ry + rz * rz);
            if (rl < 1.0E-4F) {
                rx = dz;
                ry = 0.0F;
                rz = -dx;
                rl = Mth.sqrt(rx * rx + ry * ry + rz * rz);
                if (rl < 1.0E-4F) {
                    rx = 1.0F;
                    ry = 0.0F;
                    rz = 0.0F;
                    rl = 1.0F;
                }
            }
            float rinv = 1.0F / rl;
            rx *= rinv;
            ry *= rinv;
            rz *= rinv;
            rgtX[i] = rx;
            rgtY[i] = ry;
            rgtZ[i] = rz;

            upX[i] = ry * dz - rz * dy;
            upY[i] = rz * dx - rx * dz;
            upZ[i] = rx * dy - ry * dx;
        }
    }

    private void renderSmoke(Matrix4f matrix, int n) {
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX_COLOR);
        float step = 1.0F / (n - 1);
        for (int i = 0; i < n; i++) {
            float u = i * step;
            float top = pc[i] + (pc[i] - py[i]);
            buffer.addVertex(matrix, px[i], top, pz[i]).setUv(u, 1.0F).setColor(255, 255, 255, pa[i]);
            buffer.addVertex(matrix, px[i], py[i], pz[i]).setUv(u, 0.0F).setColor(255, 255, 255, pa[i]);
        }
        draw(buffer);
    }

    private void renderRibbon(Matrix4f matrix, int n, float radius) {
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX_COLOR);
        float step = 1.0F / (n - 1);
        for (int i = 0; i < n; i++) {
            float u = i * step;
            float ox = rgtX[i] * radius;
            float oy = rgtY[i] * radius;
            float oz = rgtZ[i] * radius;
            buffer.addVertex(matrix, px[i] + ox, pc[i] + oy, pz[i] + oz).setUv(u, 1.0F).setColor(255, 255, 255, pa[i]);
            buffer.addVertex(matrix, px[i] - ox, pc[i] - oy, pz[i] - oz).setUv(u, 0.0F).setColor(255, 255, 255, pa[i]);
        }
        draw(buffer);
    }

    private void renderTube(Matrix4f matrix, int n, float radius, int sideCount) {
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        float step = 1.0F / (n - 1);
        float vStep = 1.0F / sideCount;

        for (int i = 0; i < n - 1; i++) {
            float u0 = i * step;
            float u1 = (i + 1) * step;
            int a0 = pa[i];
            int a1 = pa[i + 1];

            for (int s = 0; s < sideCount; s++) {
                float c0 = ringCos[s] * radius;
                float s0 = ringSin[s] * radius;
                float c1 = ringCos[s + 1] * radius;
                float s1 = ringSin[s + 1] * radius;
                float v0 = s * vStep;
                float v1 = (s + 1) * vStep;

                addRingVertex(buffer, matrix, i, c0, s0, u0, v0, a0);
                addRingVertex(buffer, matrix, i, c1, s1, u0, v1, a0);
                addRingVertex(buffer, matrix, i + 1, c1, s1, u1, v1, a1);
                addRingVertex(buffer, matrix, i + 1, c0, s0, u1, v0, a1);
            }
        }
        draw(buffer);
    }

    private void addRingVertex(BufferBuilder buffer, Matrix4f matrix, int i, float c, float s, float u, float v, int alpha) {
        float x = px[i] + rgtX[i] * c + upX[i] * s;
        float y = pc[i] + rgtY[i] * c + upY[i] * s;
        float z = pz[i] + rgtZ[i] * c + upZ[i] * s;
        buffer.addVertex(matrix, x, y, z).setUv(u, v).setColor(255, 255, 255, alpha);
    }

    private void renderHelix(Matrix4f matrix, int n, float radius, float twistAmount, float time, int strands) {
        float step = 1.0F / (n - 1);
        float width = radius * 0.35F;

        for (int strand = 0; strand < strands; strand++) {
            float phase = strand * (TAU * 0.5F) + time * 1.5F;
            BufferBuilder buffer = Tesselator.getInstance()
                    .begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX_COLOR);

            for (int i = 0; i < n; i++) {
                float u = i * step;
                float angle = u * twistAmount * TAU + phase;
                float cos = Mth.cos(angle) * radius;
                float sin = Mth.sin(angle) * radius;

                float cx = px[i] + rgtX[i] * cos + upX[i] * sin;
                float cy = pc[i] + rgtY[i] * cos + upY[i] * sin;
                float cz = pz[i] + rgtZ[i] * cos + upZ[i] * sin;

                float ox = rgtX[i] * width;
                float oy = rgtY[i] * width;
                float oz = rgtZ[i] * width;

                buffer.addVertex(matrix, cx + ox, cy + oy, cz + oz).setUv(u, 1.0F).setColor(255, 255, 255, pa[i]);
                buffer.addVertex(matrix, cx - ox, cy - oy, cz - oz).setUv(u, 0.0F).setColor(255, 255, 255, pa[i]);
            }
            draw(buffer);
        }

        if (strands < 2) {
            return;
        }

        // Перемычки ДНК между двумя нитями
        BufferBuilder rungs = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        float half = width * 0.6F;
        for (int i = 0; i < n; i += 4) {
            float u = i * step;
            float angle = u * twistAmount * TAU + time * 1.5F;
            float cos = Mth.cos(angle) * radius;
            float sin = Mth.sin(angle) * radius;

            float ax = px[i] + rgtX[i] * cos + upX[i] * sin;
            float ay = pc[i] + rgtY[i] * cos + upY[i] * sin;
            float az = pz[i] + rgtZ[i] * cos + upZ[i] * sin;
            float bx = px[i] - rgtX[i] * cos - upX[i] * sin;
            float by = pc[i] - rgtY[i] * cos - upY[i] * sin;
            float bz = pz[i] - rgtZ[i] * cos - upZ[i] * sin;

            float tx = tanX[i] * half;
            float ty = tanY[i] * half;
            float tz = tanZ[i] * half;

            rungs.addVertex(matrix, ax + tx, ay + ty, az + tz).setUv(u, 1.0F).setColor(255, 255, 255, pa[i]);
            rungs.addVertex(matrix, bx + tx, by + ty, bz + tz).setUv(u, 0.0F).setColor(255, 255, 255, pa[i]);
            rungs.addVertex(matrix, bx - tx, by - ty, bz - tz).setUv(u, 0.0F).setColor(255, 255, 255, pa[i]);
            rungs.addVertex(matrix, ax - tx, ay - ty, az - tz).setUv(u, 1.0F).setColor(255, 255, 255, pa[i]);
        }
        draw(rungs);
    }

    private void renderRings(Matrix4f matrix, int n, float radius, int sideCount, int every) {
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        float step = 1.0F / (n - 1);
        float inner = radius * 0.72F;
        float vStep = 1.0F / sideCount;

        for (int i = 0; i < n; i += every) {
            float u = i * step;
            int alpha = pa[i];

            for (int s = 0; s < sideCount; s++) {
                float co = ringCos[s] * radius;
                float so = ringSin[s] * radius;
                float ci = ringCos[s] * inner;
                float si = ringSin[s] * inner;
                float co2 = ringCos[s + 1] * radius;
                float so2 = ringSin[s + 1] * radius;
                float ci2 = ringCos[s + 1] * inner;
                float si2 = ringSin[s + 1] * inner;
                float v0 = s * vStep;
                float v1 = (s + 1) * vStep;

                addRingVertex(buffer, matrix, i, ci, si, u, v0, alpha);
                addRingVertex(buffer, matrix, i, co, so, u, v0, alpha);
                addRingVertex(buffer, matrix, i, co2, so2, u, v1, alpha);
                addRingVertex(buffer, matrix, i, ci2, si2, u, v1, alpha);
            }
        }
        draw(buffer);
    }

    private void renderCross(Matrix4f matrix, int n, float radius) {
        float step = 1.0F / (n - 1);

        BufferBuilder horizontal = Tesselator.getInstance()
                .begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX_COLOR);
        for (int i = 0; i < n; i++) {
            float u = i * step;
            float ox = rgtX[i] * radius;
            float oy = rgtY[i] * radius;
            float oz = rgtZ[i] * radius;
            horizontal.addVertex(matrix, px[i] + ox, pc[i] + oy, pz[i] + oz).setUv(u, 1.0F).setColor(255, 255, 255, pa[i]);
            horizontal.addVertex(matrix, px[i] - ox, pc[i] - oy, pz[i] - oz).setUv(u, 0.0F).setColor(255, 255, 255, pa[i]);
        }
        draw(horizontal);

        BufferBuilder vertical = Tesselator.getInstance()
                .begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX_COLOR);
        for (int i = 0; i < n; i++) {
            float u = i * step;
            float ox = upX[i] * radius;
            float oy = upY[i] * radius;
            float oz = upZ[i] * radius;
            vertical.addVertex(matrix, px[i] + ox, pc[i] + oy, pz[i] + oz).setUv(u, 1.0F).setColor(255, 255, 255, pa[i]);
            vertical.addVertex(matrix, px[i] - ox, pc[i] - oy, pz[i] - oz).setUv(u, 0.0F).setColor(255, 255, 255, pa[i]);
        }
        draw(vertical);
    }

    private void renderWings(Matrix4f matrix, int n, float span) {
        float step = 1.0F / (n - 1);

        for (int side = 0; side < 2; side++) {
            float sign = side == 0 ? 1.0F : -1.0F;
            BufferBuilder buffer = Tesselator.getInstance()
                    .begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX_COLOR);

            for (int i = 0; i < n; i++) {
                float u = i * step;
                // Крыло раскрывается к хвосту и слегка загибается вверх
                float spread = span * (1.0F - u) * 2.0F;
                float lift = spread * 0.45F;

                float ox = rgtX[i] * spread * sign + upX[i] * lift;
                float oy = rgtY[i] * spread * sign + upY[i] * lift;
                float oz = rgtZ[i] * spread * sign + upZ[i] * lift;

                buffer.addVertex(matrix, px[i] + ox, pc[i] + oy, pz[i] + oz).setUv(u, 1.0F).setColor(255, 255, 255, pa[i]);
                buffer.addVertex(matrix, px[i], pc[i], pz[i]).setUv(u, 0.0F).setColor(255, 255, 255, pa[i]);
            }
            draw(buffer);
        }
    }

    private void renderParticles(Matrix4f matrix, int n, float size, int every, double camX, double camY, double camZ) {
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        float step = 1.0F / (n - 1);

        for (int i = 0; i < n; i += every) {
            float u = i * step;
            float half = size * (0.35F + u * 0.65F);

            float vx = (float) (px[i] - camX);
            float vy = (float) (pc[i] - camY);
            float vz = (float) (pz[i] - camZ);
            float vl = Mth.sqrt(vx * vx + vy * vy + vz * vz);
            if (vl < 1.0E-4F) {
                continue;
            }
            float vinv = 1.0F / vl;
            vx *= vinv;
            vy *= vinv;
            vz *= vinv;

            // Правый вектор экрана: cross(worldUp, view), у него нет Y-компоненты
            float rx = vz;
            float rz = -vx;
            float rl = Mth.sqrt(rx * rx + rz * rz);
            if (rl < 1.0E-4F) {
                rx = 1.0F;
                rz = 0.0F;
                rl = 1.0F;
            }
            float rinv = 1.0F / rl;
            rx *= rinv;
            rz *= rinv;

            float ux = vy * rz;
            float uy = vz * rx - vx * rz;
            float uz = -vy * rx;
            float ul = Mth.sqrt(ux * ux + uy * uy + uz * uz);
            if (ul < 1.0E-4F) {
                continue;
            }
            float uinv = half / ul;
            ux *= uinv;
            uy *= uinv;
            uz *= uinv;

            rx *= half;
            rz *= half;

            float cx = px[i];
            float cy = pc[i];
            float cz = pz[i];
            int alpha = pa[i];

            buffer.addVertex(matrix, cx - rx - ux, cy - uy, cz - rz - uz).setUv(u, 0.0F).setColor(255, 255, 255, alpha);
            buffer.addVertex(matrix, cx + rx - ux, cy - uy, cz + rz - uz).setUv(u, 0.0F).setColor(255, 255, 255, alpha);
            buffer.addVertex(matrix, cx + rx + ux, cy + uy, cz + rz + uz).setUv(u, 1.0F).setColor(255, 255, 255, alpha);
            buffer.addVertex(matrix, cx - rx + ux, cy + uy, cz - rz + uz).setUv(u, 1.0F).setColor(255, 255, 255, alpha);
        }
        draw(buffer);
    }

    private void draw(BufferBuilder buffer) {
        MeshData mesh = buffer.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }
    }

    private record TrailPoint(double x, double y, double z, float height, long time) {
    }
}