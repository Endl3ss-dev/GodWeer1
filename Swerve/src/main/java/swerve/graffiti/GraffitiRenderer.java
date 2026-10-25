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

/**
 * [+] Отрисовка граффити: установленного и призрака под прицелом.
 *
 * <p>Рисунок — один квад 1x1 по грани блока, сдвинутый наружу на {@value #OFFSET}
 * блока: без сдвига на дальних дистанциях рисунок «мигает» вместе с блоком из-за
 * точности буфера глубины. Геометрия берётся из {@link GraffitiQuad} — одна и та же
 * для установленного рисунка и для промежуточных поз призрака.
 *
 * <p>Яркость, прозрачность и оттенок идут в цвете вершин, поэтому шейдер не нужен:
 * используется ванильный {@link CoreShaders#POSITION_TEX_COLOR}. Поза приходит из
 * {@link WorldRenderContext} уже сдвинутой к камере (так же рисуют ChinaHat и Trails),
 * поэтому мировые координаты сначала сдвигаются на {@code -camera}.
 */
public final class GraffitiRenderer {

    /** Сдвиг от грани блока, в блоках. */
    private static final float OFFSET = 0.0035F;

    /** Длительность проявления установленного рисунка, мс. */
    private static final long REVEAL_MS = 300L;

    /** Насколько рисунок «вырастает» при проявлении. */
    private static final float REVEAL_SCALE = 0.22F;

    /** Минимальный вклад яркости: в темноте рисунок не должен становиться чёрным квадратом. */
    private static final float MIN_LIGHT = 0.45F;

    /** Ниже этого косинуса между нормалью и взглядом квад виден ребром — не рисуем. */
    private static final float EDGE_HIDE_BELOW = 0.12F;

    /** Выше этого косинуса квад в полную силу. */
    private static final float EDGE_FULL_ABOVE = 0.42F;

    /** Оттенок «не готов» (идёт кулдаун): призрак гаснет и уходит в серый. */
    private static final float NOT_READY_DIM = 0.55F;

    private GraffitiRenderer() {
    }

    /**
     * Установленный рисунок: непрозрачный, с проявлением при переносе.
     *
     * @param spot        запись из хранилища
     * @param opacity     прозрачность из настроек [0, 1]
     * @param useLighting учитывать освещение грани
     */
    public static void renderPlaced(WorldRenderContext context, GraffitiSpot spot, float opacity, boolean useLighting) {
        if (spot == null || opacity <= 0.01F) {
            return;
        }

        long now = System.currentTimeMillis();
        float reveal = Mth.clamp((now - spot.placedAt()) / (float) REVEAL_MS, 0.0F, 1.0F);
        float eased = easeOut(reveal);
        float alpha = opacity * (0.30F + 0.70F * eased);
        float scale = 1.0F - REVEAL_SCALE * (1.0F - eased);

        GraffitiQuad quad = GraffitiQuad.of(spot.pos(), spot.face(), spot.spin()).scaled(scale);
        float light = useLighting ? lightOf(context, spot.pos(), spot.face()) : 1.0F;
        drawQuad(context, spot.design(), quad, alpha, light, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Призрак под прицелом: полупрозрачный, в цвете «можно ставить» или «кулдаун».
     *
     * <p>На перелёте «стена → пол» квад на середине пути повёрнут почти ребром к
     * камере и на кадр превращается в тонкую полоску. Поэтому альфа домножается на
     * косинус между нормалью квада и направлением на камеру: в тот момент, когда
     * рисунок схлопывается в линию, он гаснет. Такая поправка не зависит от того,
     * какие именно грани меняются местами, — она гасит любое положение «ребром».
     *
     * @param quad  текущая (сглаженная) поза призрака
     * @param alpha альфа из {@link GraffitiGhost}
     * @param ready {@code false} — идёт кулдаун, рисунок тускнеет
     */
    public static void renderGhost(WorldRenderContext context, GraffitiDesign design, GraffitiQuad quad,
                                   float alpha, boolean ready) {
        if (design == null || quad == null || alpha <= GraffitiGhost.MIN_VISIBLE) {
            return;
        }

        float light = lightOf(context, quad.pos(), quad.face());
        float tint = ready ? 1.0F : NOT_READY_DIM;
        drawQuad(context, design, quad, alpha, light, tint, tint, tint);
    }

    // ---- отрисовка

    private static void drawQuad(WorldRenderContext context, GraffitiDesign design, GraffitiQuad quad,
                                 float alpha, float light, float tintR, float tintG, float tintB) {
        PoseStack poseStack = context.matrixStack();
        Minecraft minecraft = Minecraft.getInstance();
        if (poseStack == null || minecraft.level == null) {
            return;
        }

        AbstractTexture texture = minecraft.getTextureManager().getTexture(design.texture());
        if (texture == null || texture.getId() == -1) {
            // Первый кадр: PNG ещё грузится в GPU. Кадр пропускаем — следующий
            // возьмёт готовую текстуру, а чёрный квадрат никто не увидит.
            return;
        }

        Vec3 camera = context.camera().getPosition();
        Vec3 normal = quad.normal().scale(OFFSET);
        Vec3 halfRight = quad.right().scale(0.5D);
        Vec3 halfDown = quad.down().scale(0.5D);
        Vec3 origin = quad.center().add(normal);

        // Квад, повёрнутый ребром к камере, — это полоска в один пиксель: гасим его,
        // пока косинус между нормалью и направлением на камеру мал. Для установленного
        // рисунка это тоже верно: в таком ракурсе его всё равно закрывает сам блок.
        Vec3 toCamera = camera.subtract(origin);
        double distance = toCamera.length();
        if (distance > 1.0E-4D) {
            float facing = (float) Math.abs(quad.normal().dot(toCamera.scale(1.0D / distance)));
            float edgeFade = Mth.clamp((facing - EDGE_HIDE_BELOW) / (EDGE_FULL_ABOVE - EDGE_HIDE_BELOW),
                    0.0F, 1.0F);
            alpha *= edgeFade * edgeFade;
            if (alpha <= 0.004F) {
                return;
            }
        }

        int brightness = Math.round(255.0F * Mth.clamp(light, 0.0F, 1.0F));
        int red = Math.round(brightness * Mth.clamp(tintR, 0.0F, 1.0F));
        int green = Math.round(brightness * Mth.clamp(tintG, 0.0F, 1.0F));
        int blue = Math.round(brightness * Mth.clamp(tintB, 0.0F, 1.0F));
        int a = Math.round(255.0F * Mth.clamp(alpha, 0.0F, 1.0F));

        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        poseStack.pushPose();
        try {
            poseStack.translate(-camera.x, -camera.y, -camera.z);
            Matrix4f matrix = poseStack.last().pose();

            // Текстурные координаты: (0,0) — левый верхний угол PNG.
            addVertex(buffer, matrix, origin.subtract(halfRight).subtract(halfDown), 0.0F, 0.0F, red, green, blue, a);
            addVertex(buffer, matrix, origin.subtract(halfRight).add(halfDown), 0.0F, 1.0F, red, green, blue, a);
            addVertex(buffer, matrix, origin.add(halfRight).add(halfDown), 1.0F, 1.0F, red, green, blue, a);
            addVertex(buffer, matrix, origin.add(halfRight).subtract(halfDown), 1.0F, 0.0F, red, green, blue, a);

            MeshData mesh = buffer.build();
            if (mesh == null) {
                return;
            }

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(CoreShaders.POSITION_TEX_COLOR);

            texture.setFilter(true, false);
            RenderSystem.setShaderTexture(0, design.texture());
            BufferUploader.drawWithShader(mesh);
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

    private static void addVertex(BufferBuilder buffer, Matrix4f matrix, Vec3 position,
                                  float u, float v, int red, int green, int blue, int alpha) {
        buffer.addVertex(matrix, (float) position.x, (float) position.y, (float) position.z)
                .setUv(u, v)
                .setColor(red, green, blue, alpha);
    }

    // ---- вспомогательное

    /**
     * Освещение грани: считается по соседнему, открытому блоку — рисунок виден
     * с той стороны, куда смотрит грань, и должен гаснуть вместе с ней в темноте.
     */
    private static float lightOf(WorldRenderContext context, BlockPos pos, Direction face) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) {
            return 1.0F;
        }
        BlockPos open = pos.relative(face);
        int block = level.getBrightness(LightLayer.BLOCK, open);
        int sky = level.getBrightness(LightLayer.SKY, open) - level.getSkyDarken();
        int light = Math.max(block, Math.max(0, sky));
        float factor = Mth.clamp(light / 15.0F, 0.0F, 1.0F);
        return MIN_LIGHT + (1.0F - MIN_LIGHT) * factor;
    }

    private static float easeOut(float t) {
        float inverted = 1.0F - t;
        return 1.0F - inverted * inverted * inverted;
    }
}
