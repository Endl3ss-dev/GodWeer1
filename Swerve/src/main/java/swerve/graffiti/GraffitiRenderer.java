package swerve.graffiti;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * [+] Отрисовка граффити на гранях блоков.
 *
 * <p>Рисунок — один квад 1x1 по грани блока, сдвинутый наружу на {@value #OFFSET}
 * блока, чтобы не спорить с гранью за глубину: без сдвига на дальних дистанциях
 * из-за точности буфера глубины рисунок «мигает» вместе с блоком.
 *
 * <p>База плоскости грани: {@code u} — вправо для зрителя, {@code v = u × n} — вниз.
 * Поворот ({@code spin}) вращает {@code u} вокруг нормали, {@code v} считается заново,
 * поэтому картинка всегда остаётся в плоскости грани.
 *
 * <p>Батчинг: квады группируются по рисунку, текстура биндится один раз на рисунок.
 * Яркость, прозрачность и проявление — в цвете вершин, поэтому шейдер не нужен:
 * используется ванильный {@link CoreShaders#POSITION_TEX_COLOR}.
 *
 * <p>Поза приходит из {@link WorldRenderContext} уже сдвинутой к камере
 * (так же рисуют ChinaHat и Trails), поэтому мировые координаты сначала
 * сдвигаются на {@code -camera}.
 */
public final class GraffitiRenderer {

    /** Сдвиг от грани блока, в блоках. */
    private static final float OFFSET = 0.0035F;

    /** Длительность проявления нового рисунка, мс. */
    private static final long REVEAL_MS = 320L;

    /** Насколько рисунок «вырастает» при проявлении. */
    private static final float REVEAL_SCALE = 0.26F;

    /** Минимальный вклад яркости: в темноте рисунок не должен становиться чёрным квадратом. */
    private static final float MIN_LIGHT = 0.42F;

    private GraffitiRenderer() {
    }

    /**
     * Рисует все граффити текущего мира.
     *
     * @param context     контекст мира
     * @param spots       рисунки текущего мира и измерения
     * @param opacity     общая прозрачность [0, 1]
     * @param useLighting учитывать освещение блока
     */
    public static void render(WorldRenderContext context, List<GraffitiSpot> spots, float opacity, boolean useLighting) {
        if (spots.isEmpty() || opacity <= 0.01F) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        PoseStack poseStack = context.matrixStack();
        if (level == null || poseStack == null) {
            return;
        }

        Vec3 camera = context.camera().getPosition();
        double maxDistance = Math.max(32.0D, minecraft.options.getEffectiveRenderDistance() * 16.0D);
        long now = System.currentTimeMillis();

        Map<GraffitiDesign, BufferBuilder> batches = new EnumMap<>(GraffitiDesign.class);

        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);
        Matrix4f matrix = poseStack.last().pose();

        try {
            for (int i = 0, size = spots.size(); i < size; i++) {
                GraffitiSpot spot = spots.get(i);
                Vec3 center = center(spot.pos(), spot.face());

                float distanceFade = distanceFade(camera.distanceTo(center), maxDistance);
                if (distanceFade <= 0.0F) {
                    continue;
                }

                float reveal = Mth.clamp((now - spot.placedAt()) / (float) REVEAL_MS, 0.0F, 1.0F);
                float alpha = opacity * distanceFade * (0.35F + 0.65F * reveal);
                if (alpha <= 0.01F) {
                    continue;
                }

                float scale = 1.0F - REVEAL_SCALE * (1.0F - easeOut(reveal));
                float light = useLighting ? lightOf(level, spot.pos(), spot.face()) : 1.0F;

                BufferBuilder buffer = batches.get(spot.design());
                if (buffer == null) {
                    buffer = Tesselator.getInstance()
                            .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                    batches.put(spot.design(), buffer);
                }
                addQuad(buffer, matrix, spot, center, scale, light, alpha);
            }

            if (batches.isEmpty()) {
                return;
            }

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(CoreShaders.POSITION_TEX_COLOR);

            for (Map.Entry<GraffitiDesign, BufferBuilder> entry : batches.entrySet()) {
                MeshData mesh = entry.getValue().build();
                if (mesh == null) {
                    continue;
                }
                AbstractTexture texture = minecraft.getTextureManager().getTexture(entry.getKey().texture());
                if (texture == null || texture.getId() == -1) {
                    // Первый кадр: PNG ещё грузится в GPU. Кадр пропускаем — следующий
                    // возьмёт готовую текстуру, а чёрный квадрат никто не увидит.
                    continue;
                }
                texture.setFilter(true, false);
                RenderSystem.setShaderTexture(0, entry.getKey().texture());
                BufferUploader.drawWithShader(mesh);
            }
        } finally {
            // Состояние возвращается всегда: рендер идёт в общем кадре с ванилью,
            // и «залипший» depthMask(false) испортил бы чужие проходы.
            RenderSystem.depthMask(true);
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            RenderSystem.setShaderTexture(0, 0);
            poseStack.popPose();
        }
    }

    /**
     * Подсветка грани под прицелом — подсказка, куда встанет рисунок.
     *
     * <p>Тот же квад, но цветом темы и без текстуры: ванильный {@link CoreShaders#POSITION_COLOR}
     * даёт ровную заливку, видную и на тёмных, и на светлых блоках.
     */
    public static void highlight(WorldRenderContext context, BlockPos pos, Direction face, int color) {
        PoseStack poseStack = context.matrixStack();
        if (poseStack == null) {
            return;
        }

        Vec3 camera = context.camera().getPosition();
        Vec3 center = center(pos, face);
        Vec3 normal = normal(face);
        Vec3 u = right(face).scale(0.5D);
        Vec3 v = down(face).scale(0.5D);
        Vec3 origin = center.add(normal.scale(OFFSET * 2.0D));

        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = 0.22F;

        poseStack.pushPose();
        try {
            poseStack.translate(-camera.x, -camera.y, -camera.z);
            Matrix4f matrix = poseStack.last().pose();

            BufferBuilder buffer = Tesselator.getInstance()
                    .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            addColoredVertex(buffer, matrix, origin.subtract(u).subtract(v), red, green, blue, alpha);
            addColoredVertex(buffer, matrix, origin.subtract(u).add(v), red, green, blue, alpha);
            addColoredVertex(buffer, matrix, origin.add(u).add(v), red, green, blue, alpha);
            addColoredVertex(buffer, matrix, origin.add(u).subtract(v), red, green, blue, alpha);

            MeshData mesh = buffer.build();
            if (mesh == null) {
                return;
            }
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(CoreShaders.POSITION_COLOR);
            BufferUploader.drawWithShader(mesh);
        } finally {
            RenderSystem.depthMask(true);
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            poseStack.popPose();
        }
    }

    // ---- геометрия

    /** Центр грани блока. */
    public static Vec3 center(BlockPos pos, Direction face) {
        return new Vec3(
                pos.getX() + 0.5D + face.getStepX() * 0.5D,
                pos.getY() + 0.5D + face.getStepY() * 0.5D,
                pos.getZ() + 0.5D + face.getStepZ() * 0.5D);
    }

    /** Нормаль грани. */
    public static Vec3 normal(Direction face) {
        return Vec3.atLowerCornerOf(face.getUnitVec3i());
    }

    /**
     * Направление «вправо» для зрителя, стоящего перед гранью.
     *
     * <p>Для боковых граней — {@code cross(forward, up)}, где {@code forward}
     * смотрит в блок. Для пола и потолка «верх» картинки смотрит на север,
     * а вправо — на восток: иначе рисунок на полу поворачивался бы вслед за камерой.
     */
    public static Vec3 right(Direction face) {
        return switch (face) {
            case NORTH -> new Vec3(-1.0D, 0.0D, 0.0D);
            case SOUTH -> new Vec3(1.0D, 0.0D, 0.0D);
            case EAST -> new Vec3(0.0D, 0.0D, -1.0D);
            case WEST -> new Vec3(0.0D, 0.0D, 1.0D);
            case UP, DOWN -> new Vec3(1.0D, 0.0D, 0.0D);
        };
    }

    /**
     * Направление «вниз» картинки — {@code u × n}.
     *
     * <p>Именно {@code u × n}: в системе Minecraft (Y вверх, нормаль наружу)
     * это даёт для боковых граней вектор вниз, а для потолка — на север.
     */
    public static Vec3 down(Direction face) {
        return right(face).cross(normal(face)).normalize();
    }

    private static void addQuad(BufferBuilder buffer, Matrix4f matrix, GraffitiSpot spot, Vec3 center,
                                float scale, float light, float alpha) {
        Vec3 normal = normal(spot.face());
        Vec3 u = right(spot.face());
        Vec3 v = down(spot.face());

        float spin = spot.spin();
        if (spin != 0.0F) {
            double radians = Math.toRadians(spin);
            double cos = Math.cos(radians);
            double sin = Math.sin(radians);
            // Поворот u вокруг нормали (Родриг): n × u — перпендикуляр в плоскости грани.
            u = u.scale(cos).add(normal.cross(u).scale(sin)).normalize();
            v = u.cross(normal).normalize();
        }

        float half = 0.5F * scale;
        Vec3 origin = center.add(normal.scale(OFFSET));
        Vec3 x = u.scale(half);
        Vec3 y = v.scale(half);

        int value = Math.round(255.0F * Mth.clamp(light, 0.0F, 1.0F));
        int a = Math.round(255.0F * Mth.clamp(alpha, 0.0F, 1.0F));

        // Текстурные координаты: (0,0) — левый верхний угол PNG.
        addTexturedVertex(buffer, matrix, origin.subtract(x).subtract(y), 0.0F, 0.0F, value, a);
        addTexturedVertex(buffer, matrix, origin.subtract(x).add(y), 0.0F, 1.0F, value, a);
        addTexturedVertex(buffer, matrix, origin.add(x).add(y), 1.0F, 1.0F, value, a);
        addTexturedVertex(buffer, matrix, origin.add(x).subtract(y), 1.0F, 0.0F, value, a);
    }

    private static void addTexturedVertex(BufferBuilder buffer, Matrix4f matrix, Vec3 position,
                                          float u, float v, int brightness, int alpha) {
        buffer.addVertex(matrix, (float) position.x, (float) position.y, (float) position.z)
                .setUv(u, v)
                .setColor(brightness, brightness, brightness, alpha);
    }

    private static void addColoredVertex(BufferBuilder buffer, Matrix4f matrix, Vec3 position,
                                         float red, float green, float blue, float alpha) {
        buffer.addVertex(matrix, (float) position.x, (float) position.y, (float) position.z)
                .setColor(red, green, blue, alpha);
    }

    // ---- вспомогательное

    /**
     * Освещение грани: считается по соседнему, открытому блоку — рисунок виден
     * с той стороны, куда смотрит грань, и должен гаснуть вместе с ней в темноте.
     */
    private static float lightOf(Level level, BlockPos pos, Direction face) {
        BlockPos open = pos.relative(face);
        int block = level.getBrightness(LightLayer.BLOCK, open);
        int sky = level.getBrightness(LightLayer.SKY, open) - level.getSkyDarken();
        int light = Math.max(block, Math.max(0, sky));
        float factor = Mth.clamp(light / 15.0F, 0.0F, 1.0F);
        return MIN_LIGHT + (1.0F - MIN_LIGHT) * factor;
    }

    /** Плавное затухание к концу дистанции прорисовки: без него рисунки «щёлкают». */
    private static float distanceFade(double distance, double maxDistance) {
        double start = maxDistance * 0.82D;
        if (distance <= start) {
            return 1.0F;
        }
        if (distance >= maxDistance) {
            return 0.0F;
        }
        return (float) ((maxDistance - distance) / (maxDistance - start));
    }

    private static float easeOut(float t) {
        float inverted = 1.0F - t;
        return 1.0F - inverted * inverted * inverted;
    }
}
