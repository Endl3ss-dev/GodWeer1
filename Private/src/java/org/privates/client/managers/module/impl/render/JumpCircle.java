package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14C;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.api.interfaces.IRender;
import org.privates.client.managers.events.player.JumpEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.events.world.WorldChangeEvent;
import org.privates.client.managers.events.world.WorldLoadEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.animation.util.Easing;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;
import org.privates.client.utils.render.shader.ShaderManager;

import java.util.ArrayList;
import java.util.List;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "JumpCircle", category = Category.RENDER, description = "Оставляет круг после прыжка")
public class JumpCircle extends Module implements IRender {
    public static JumpCircle getInstance() {
        return Instance.get(JumpCircle.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Классический", "Блоки");
    private final BooleanSetting blockShowFill = new BooleanSetting(this, "Заливка блоков", true).setVisible(() -> mode.is("Блоки"));

    private final ModeSetting blockFillMode = new ModeSetting(this, "Стиль заливки", "Обычный", "Cosmic Nebula")
            .setVisible(() -> mode.is("Блоки") && blockShowFill.getValue());
    private final ModeSetting colorMode = new ModeSetting(this, "Цвет", "Тема", "Кастомный")
            .setVisible(() -> mode.is("Классический") || (mode.is("Блоки") && !blockFillMode.is("Cosmic Nebula")));
    private final ColorSetting customColor = new ColorSetting(this, "Свой цвет")
            .setVisible(() -> colorMode.is("Кастомный") && (mode.is("Классический") || (mode.is("Блоки") && !blockFillMode.is("Cosmic Nebula"))));

    // ── Классический ──
    private final SliderSetting circleSize = new SliderSetting(this, "Свечение", 0.25F, 0.1F, 0.35F, 0.05F).setVisible(() -> mode.is("Классический"));
    private final SliderSetting circleDelay = new SliderSetting(this, "Время", 1500F, 750F, 5000F, 50F);
    private final SliderSetting circleRadius = new SliderSetting(this, "Размер", 1F, 0.5F, 1.5F, 0.05F).setVisible(() -> mode.is("Классический"));

    // ── Блоки: заливка ──


    private final SliderSetting nebulaDensity = new SliderSetting(this, "Nebula Density", 1.0F, 0.3F, 2.5F, 0.1F)
            .setVisible(() -> mode.is("Блоки") && blockShowFill.getValue() && blockFillMode.is("Cosmic Nebula"));

    // ── Блоки: общие ──
    private final SliderSetting blockMaxRadius = new SliderSetting(this, "Макс. радиус", 5F, 1F, 15F, 0.5F).setVisible(() -> mode.is("Блоки"));
    private final SliderSetting blockRingWidth = new SliderSetting(this, "Толщина кольца", 1.5F, 0.5F, 5F, 0.25F).setVisible(() -> mode.is("Блоки"));
    private final SliderSetting blockFillAlpha = new SliderSetting(this, "Прозрачность заливки", 0.4F, 0.05F, 1F, 0.05F)
            .setVisible(() -> mode.is("Блоки") && blockShowFill.getValue() && !blockFillMode.is("Cosmic Nebula"));
    private final SliderSetting blockOutlineAlpha = new SliderSetting(this, "Прозрачность контура", 0.8F, 0.05F, 1F, 0.05F).setVisible(() -> mode.is("Блоки"));
    private final BooleanSetting blockShowOutline = new BooleanSetting(this, "Контур блоков", true).setVisible(() -> mode.is("Блоки"));
    private final SliderSetting blockLineThickness = new SliderSetting(this, "Толщина линий", 1.5F, 0.5F, 5F, 0.1F)
            .setVisible(() -> mode.is("Блоки") && blockShowOutline.getValue());

    private final ModeSetting expandEasing = new ModeSetting(this, "Анимация расширения", "EXPO_OUT",
            "LINEAR", "QUAD_IN", "QUAD_OUT", "QUAD_IN_OUT", "CUBIC_IN", "CUBIC_OUT", "CUBIC_IN_OUT",
            "QUART_IN", "QUART_OUT", "QUART_IN_OUT", "QUINT_IN", "QUINT_OUT", "QUINT_IN_OUT",
            "SINE_IN", "SINE_OUT", "SINE_IN_OUT", "CIRC_IN", "CIRC_OUT", "CIRC_IN_OUT",
            "ELASTIC_IN", "ELASTIC_OUT", "ELASTIC_IN_OUT", "EXPO_IN", "EXPO_OUT", "EXPO_IN_OUT",
            "BACK_IN", "BACK_OUT", "BACK_IN_OUT", "BOUNCE_IN", "BOUNCE_OUT", "BOUNCE_IN_OUT"
    ).setVisible(() -> mode.is("Блоки"));

    private final ModeSetting fadeInEasing = new ModeSetting(this, "Анимация появления", "EXPO_OUT",
            "LINEAR", "QUAD_IN", "QUAD_OUT", "QUAD_IN_OUT", "CUBIC_IN", "CUBIC_OUT", "CUBIC_IN_OUT",
            "QUART_IN", "QUART_OUT", "QUART_IN_OUT", "QUINT_IN", "QUINT_OUT", "QUINT_IN_OUT",
            "SINE_IN", "SINE_OUT", "SINE_IN_OUT", "CIRC_IN", "CIRC_OUT", "CIRC_IN_OUT",
            "ELASTIC_IN", "ELASTIC_OUT", "ELASTIC_IN_OUT", "EXPO_IN", "EXPO_OUT", "EXPO_IN_OUT",
            "BACK_IN", "BACK_OUT", "BACK_IN_OUT", "BOUNCE_IN", "BOUNCE_OUT", "BOUNCE_IN_OUT"
    ).setVisible(() -> mode.is("Блоки"));

    private final ModeSetting fadeOutEasing = new ModeSetting(this, "Анимация ухода", "EXPO_IN",
            "LINEAR", "QUAD_IN", "QUAD_OUT", "QUAD_IN_OUT", "CUBIC_IN", "CUBIC_OUT", "CUBIC_IN_OUT",
            "QUART_IN", "QUART_OUT", "QUART_IN_OUT", "QUINT_IN", "QUINT_OUT", "QUINT_IN_OUT",
            "SINE_IN", "SINE_OUT", "SINE_IN_OUT", "CIRC_IN", "CIRC_OUT", "CIRC_IN_OUT",
            "ELASTIC_IN", "ELASTIC_OUT", "ELASTIC_IN_OUT", "EXPO_IN", "EXPO_OUT", "EXPO_IN_OUT",
            "BACK_IN", "BACK_OUT", "BACK_IN_OUT", "BOUNCE_IN", "BOUNCE_OUT", "BOUNCE_IN_OUT"
    ).setVisible(() -> mode.is("Блоки"));

    private final SliderSetting fadeInDuration = new SliderSetting(this, "Время появления", 0.15F, 0.01F, 0.5F, 0.01F).setVisible(() -> mode.is("Блоки"));
    private final SliderSetting fadeOutStart = new SliderSetting(this, "Начало ухода", 0.5F, 0.1F, 0.9F, 0.05F).setVisible(() -> mode.is("Блоки"));
    private final SliderSetting expandSpeed = new SliderSetting(this, "Скорость расширения", 1.5F, 0.5F, 3.0F, 0.1F).setVisible(() -> mode.is("Блоки"));

    private final List<CircleEntity> circles = new ArrayList<>();

    @Override
    public void toggle() {
        super.toggle();
        clear();
    }

    @EventHandler
    public void onEvent(WorldChangeEvent e) {
        clear();
    }

    @EventHandler
    public void onEvent(WorldLoadEvent e) {
        clear();
    }

    @EventHandler
    public void onEvent(JumpEvent event) {
        if (mc.player == null) return;
        addCircle(mc.player);
    }

    @EventHandler
    public void onEvent(Render3DLastEvent event) {
        if (circles.isEmpty()) return;
        circles.removeIf(c -> c.getDeltaTime() >= 1.0F);
        if (circles.isEmpty()) return;

        if (mode.is("Классический")) renderClassic(event);
        else renderBlocks(event);
    }

    private int getActiveColor() {
        return colorMode.is("Кастомный") ? customColor.getValue() : Theme.getInstance().clientColor();
    }

    private Easing getEasingByName(String name) {
        return switch (name) {
            case "LINEAR" -> Easings.LINEAR;
            case "QUAD_IN" -> Easings.QUAD_IN;
            case "QUAD_OUT" -> Easings.QUAD_OUT;
            case "QUAD_IN_OUT" -> Easings.QUAD_IN_OUT;
            case "CUBIC_IN" -> Easings.CUBIC_IN;
            case "CUBIC_OUT" -> Easings.CUBIC_OUT;
            case "CUBIC_IN_OUT" -> Easings.CUBIC_IN_OUT;
            case "QUART_IN" -> Easings.QUART_IN;
            case "QUART_OUT" -> Easings.QUART_OUT;
            case "QUART_IN_OUT" -> Easings.QUART_IN_OUT;
            case "QUINT_IN" -> Easings.QUINT_IN;
            case "QUINT_OUT" -> Easings.QUINT_OUT;
            case "QUINT_IN_OUT" -> Easings.QUINT_IN_OUT;
            case "SINE_IN" -> Easings.SINE_IN;
            case "SINE_OUT" -> Easings.SINE_OUT;
            case "SINE_IN_OUT" -> Easings.SINE_IN_OUT;
            case "CIRC_IN" -> Easings.CIRC_IN;
            case "CIRC_OUT" -> Easings.CIRC_OUT;
            case "CIRC_IN_OUT" -> Easings.CIRC_IN_OUT;
            case "ELASTIC_IN" -> Easings.ELASTIC_IN;
            case "ELASTIC_OUT" -> Easings.ELASTIC_OUT;
            case "ELASTIC_IN_OUT" -> Easings.ELASTIC_IN_OUT;
            case "EXPO_IN" -> Easings.EXPO_IN;
            case "EXPO_OUT" -> Easings.EXPO_OUT;
            case "EXPO_IN_OUT" -> Easings.EXPO_IN_OUT;
            case "BACK_IN" -> Easings.BACK_IN;
            case "BACK_OUT" -> Easings.BACK_OUT;
            case "BACK_IN_OUT" -> Easings.BACK_IN_OUT;
            case "BOUNCE_IN" -> Easings.BOUNCE_IN;
            case "BOUNCE_OUT" -> Easings.BOUNCE_OUT;
            case "BOUNCE_IN_OUT" -> Easings.BOUNCE_IN_OUT;
            default -> Easings.EXPO_OUT;
        };
    }

    // ══════════════════════════════════════════════
    //  КЛАССИЧЕСКИЙ
    // ══════════════════════════════════════════════

    private void renderClassic(Render3DLastEvent event) {
        final MatrixStack matrix = event.getMatrix();
        RenderUtil3D.setupWorldRenderer();
        Vector3d cameraPos = RenderUtil3D.cameraPos();
        matrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        setupDraw(matrix, () -> circles.forEach(circle -> drawCircle(matrix, circle)));
        matrix.translate(cameraPos.x, cameraPos.y, cameraPos.z);
        RenderUtil3D.cleanupWorldRenderer();
    }

    // ══════════════════════════════════════════════
    //  БЛОКИ
    // ══════════════════════════════════════════════

    private void renderBlocks(Render3DLastEvent event) {
        if (mc.world == null) return;

        MatrixStack matrixStack = event.getMatrix();
        matrixStack.push();
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrixStack.getLast().getMatrix());

        double renderX = mc.getRenderManager().renderPosX();
        double renderY = mc.getRenderManager().renderPosY();
        double renderZ = mc.getRenderManager().renderPosZ();
        RenderSystem.translated(-renderX, -renderY, -renderZ);

        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableCull();

        if (blockShowOutline.getValue()) {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            RenderSystem.lineWidth(blockLineThickness.getValue());
        }

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();
        int baseColor = getActiveColor();
        boolean isNebula = blockShowFill.getValue() && blockFillMode.is("Cosmic Nebula");

        Easing expandEase = getEasingByName(expandEasing.getValue());
        Easing fadeInEase = getEasingByName(fadeInEasing.getValue());
        Easing fadeOutEase = getEasingByName(fadeOutEasing.getValue());

        float fadeInDur = fadeInDuration.getValue();
        float fadeOutSt = fadeOutStart.getValue();
        float expSpeed = expandSpeed.getValue();

        for (CircleEntity circle : circles) {
            float rawProgress = Math.min(circle.getDeltaTime(), 1.0F);

            float expandInput = Math.min(rawProgress * expSpeed, 1.0F);
            float easedExpand = (float) expandEase.ease(expandInput);

            float currentRadius = easedExpand * blockMaxRadius.getValue();
            float innerRadius = Math.max(0, currentRadius - blockRingWidth.getValue());

            float fadeAlpha;
            if (rawProgress < fadeInDur) {
                fadeAlpha = (float) fadeInEase.ease(Math.min(rawProgress / fadeInDur, 1.0F));
            } else if (rawProgress > fadeOutSt) {
                float p = 1.0F - (rawProgress - fadeOutSt) / (1.0F - fadeOutSt);
                fadeAlpha = (float) fadeOutEase.ease(Math.max(Math.min(p, 1.0F), 0.0F));
            } else {
                fadeAlpha = 1.0F;
            }
            fadeAlpha = Math.max(0, Math.min(1, fadeAlpha));

            if (fadeAlpha < 0.005F || currentRadius < 0.01F) continue;

            int blockY = (int) Math.floor(circle.pos.y - 0.5);
            int centerBlockX = (int) Math.floor(circle.pos.x);
            int centerBlockZ = (int) Math.floor(circle.pos.z);
            int scanRadius = (int) Math.ceil(currentRadius) + 1;

            for (int dx = -scanRadius; dx <= scanRadius; dx++) {
                for (int dz = -scanRadius; dz <= scanRadius; dz++) {
                    BlockPos pos = new BlockPos(centerBlockX + dx, blockY, centerBlockZ + dz);
                    BlockState state = mc.world.getBlockState(pos);
                    if (state.isAir()) continue;

                    VoxelShape shape = state.getShape(mc.world, pos);
                    if (shape.isEmpty()) continue;

                    for (AxisAlignedBB aabb : shape.toBoundingBoxList()) {
                        double blockMinX = pos.getX() + aabb.minX;
                        double blockMinZ = pos.getZ() + aabb.minZ;
                        double blockMaxX = pos.getX() + aabb.maxX;
                        double blockMaxZ = pos.getZ() + aabb.maxZ;
                        double blockMaxY = pos.getY() + aabb.maxY;

                        double closestX = Math.max(blockMinX, Math.min(circle.pos.x, blockMaxX));
                        double closestZ = Math.max(blockMinZ, Math.min(circle.pos.z, blockMaxZ));
                        double distSq = (closestX - circle.pos.x) * (closestX - circle.pos.x)
                                + (closestZ - circle.pos.z) * (closestZ - circle.pos.z);

                        double farthestX = (Math.abs(blockMinX - circle.pos.x) > Math.abs(blockMaxX - circle.pos.x)) ? blockMinX : blockMaxX;
                        double farthestZ = (Math.abs(blockMinZ - circle.pos.z) > Math.abs(blockMaxZ - circle.pos.z)) ? blockMinZ : blockMaxZ;
                        double farDistSq = (farthestX - circle.pos.x) * (farthestX - circle.pos.x)
                                + (farthestZ - circle.pos.z) * (farthestZ - circle.pos.z);

                        double innerRadSq = (double) innerRadius * innerRadius;
                        double outerRadSq = (double) currentRadius * currentRadius;

                        if (farDistSq < innerRadSq) continue;
                        if (distSq > outerRadSq) continue;

                        // Плавный фейд по расстоянию от центра кольца
                        float blockAlpha = fadeAlpha;
                        double dist = Math.sqrt(distSq);
                        float ringCenter = (innerRadius + currentRadius) / 2.0F;
                        float halfRing = (currentRadius - innerRadius) / 2.0F;
                        if (halfRing > 0.01F) {
                            float distFromCenter = Math.abs((float) dist - ringCenter) / halfRing;
                            distFromCenter = Math.min(distFromCenter, 1.0F);
                            float smoothFade = 1.0F - distFromCenter * distFromCenter * distFromCenter;
                            blockAlpha *= smoothFade;
                        }
                        if (blockAlpha < 0.01F) continue;

                        // ── Заливка ──
                        if (blockShowFill.getValue()) {
                            if (isNebula) {
                                drawNebulaOnTopFace(tess, buffer,
                                        blockMinX, blockMaxY, blockMinZ,
                                        blockMaxX, blockMaxZ,
                                        blockAlpha);
                            } else {
                                int fc = ColorUtil.setAlpha(baseColor, (int) (blockFillAlpha.getValue() * 255 * blockAlpha));
                                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                                renderTopFace(buffer, blockMinX, blockMaxY, blockMinZ, blockMaxX, blockMaxY, blockMaxZ, fc);
                                tess.draw();
                            }
                        }

                        // ── Контур ──
                        if (blockShowOutline.getValue()) {
                            int oc = ColorUtil.setAlpha(baseColor, (int) (blockOutlineAlpha.getValue() * 255 * blockAlpha));
                            buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                            renderTopFaceOutline(buffer, blockMinX, blockMaxY, blockMinZ, blockMaxX, blockMaxY, blockMaxZ, oc);
                            tess.draw();
                        }
                    }
                }
            }
        }

        if (blockShowOutline.getValue()) GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.translated(renderX, renderY, renderZ);
        RenderSystem.popMatrix();
        matrixStack.pop();
    }

    /**
     * Рисует шейдер туманности прямо на верхней грани блока в мировых координатах.
     * blockAlpha управляет плотностью — туманность тускнеет вместе с кольцом.
     */
    private void drawNebulaOnTopFace(Tessellator tess, BufferBuilder buf,
                                     double minX, double y, double minZ,
                                     double maxX, double maxZ,
                                     float blockAlpha) {
        ShaderManager shader = ShaderManager.cosmicNebula;

        RenderSystem.enableTexture(); // шейдеру нужна текстура

        shader.load();
        shader.setUniformf("u_texel_size",
                1.0F / mc.getFramebuffer().framebufferWidth,
                1.0F / mc.getFramebuffer().framebufferHeight);
        shader.setUniformf("u_time", (System.currentTimeMillis() % 1000000) / 1000.0F);
        shader.setUniformf("u_nebula_density", nebulaDensity.getValue() * blockAlpha);
        shader.setUniformi("u_diffuse_sampler", 0);
        shader.setUniformi("u_other_sampler", 20);

        // Текстура фрейма как фон
        GL13.glActiveTexture(GL13.GL_TEXTURE20);
        mc.getFramebuffer().bindFramebufferTexture();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        mc.getFramebuffer().bindFramebufferTexture();

        // Рисуем верхнюю грань с UV 0..1
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buf.pos(minX, y, minZ).tex(0, 0).endVertex();
        buf.pos(maxX, y, minZ).tex(1, 0).endVertex();
        buf.pos(maxX, y, maxZ).tex(1, 1).endVertex();
        buf.pos(minX, y, maxZ).tex(0, 1).endVertex();
        tess.draw();

        shader.unload();

        GL13.glActiveTexture(GL13.GL_TEXTURE20);
        GlStateManager.bindTexture(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GlStateManager.bindTexture(0);

        RenderSystem.disableTexture(); // возвращаем состояние как было
    }

    // ══════════════════════════════════════════════
    //  Helpers
    // ══════════════════════════════════════════════

    private void renderTopFace(BufferBuilder b, double minX, double y, double minZ, double maxX, double maxY, double maxZ, int color) {
        float r = ((color >> 16) & 0xFF) / 255f, g = ((color >> 8) & 0xFF) / 255f,
                bl = (color & 0xFF) / 255f, a = ((color >> 24) & 0xFF) / 255f;
        b.pos(minX, y, minZ).color(r, g, bl, a).endVertex();
        b.pos(minX, y, maxZ).color(r, g, bl, a).endVertex();
        b.pos(maxX, y, maxZ).color(r, g, bl, a).endVertex();
        b.pos(maxX, y, minZ).color(r, g, bl, a).endVertex();
    }

    private void renderTopFaceOutline(BufferBuilder b, double minX, double y, double minZ, double maxX, double maxY, double maxZ, int color) {
        float r = ((color >> 16) & 0xFF) / 255f, g = ((color >> 8) & 0xFF) / 255f,
                bl = (color & 0xFF) / 255f, a = ((color >> 24) & 0xFF) / 255f;
        b.pos(minX, y, minZ).color(r, g, bl, a).endVertex();
        b.pos(maxX, y, minZ).color(r, g, bl, a).endVertex();
        b.pos(maxX, y, maxZ).color(r, g, bl, a).endVertex();
        b.pos(minX, y, maxZ).color(r, g, bl, a).endVertex();
        b.pos(minX, y, minZ).color(r, g, bl, a).endVertex();
    }

    private void drawCircle(final MatrixStack matrix, final CircleEntity circle) {
        final net.minecraft.util.math.vector.Matrix4f matrix4f = matrix.getLast().getMatrix();
        final int color = getActiveColor();
        final float yOffset = 0.005F;
        float deltaTime = circle.getDeltaTime();

        float animationProgress;
        if (deltaTime < 0.5F) animationProgress = deltaTime * 2.0F;
        else animationProgress = 1.0F + (deltaTime - 0.5F) * 2.0F;

        float easedProgress = (float) Easings.BACK_OUT.ease(Math.min(animationProgress, 1.0F));
        float finalProgress = animationProgress <= 1.0F ? easedProgress : easedProgress + (animationProgress - 1.0F);
        float alphaValue = 1.0F - deltaTime;
        float radiusValue = finalProgress * circleRadius.getValue();
        float sizeValue = circleSize.getValue();

        BUFFER.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (float i = 0; i <= 360; i += 1) {
            float dx = (float) -Math.sin(Math.toRadians(i));
            float dz = (float) Math.cos(Math.toRadians(i));
            BUFFER.pos(matrix4f, (float) (circle.pos.x + dx * radiusValue), (float) circle.pos.y + yOffset, (float) (circle.pos.z + dz * radiusValue))
                    .color(ColorUtil.rgba(ColorUtil.red(color), ColorUtil.green(color), ColorUtil.blue(color), (int) (alphaValue * 255))).endVertex();
            BUFFER.pos(matrix4f, (float) (circle.pos.x + dx * (1F + sizeValue) * radiusValue), (float) circle.pos.y + yOffset, (float) (circle.pos.z + dz * (1F + sizeValue) * radiusValue))
                    .color(ColorUtil.rgba(ColorUtil.red(color), ColorUtil.green(color), ColorUtil.blue(color), 0)).endVertex();
        }
        TESSELLATOR.draw();

        BUFFER.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (float i = 0; i <= 360; i += 1) {
            float dx = (float) -Math.sin(Math.toRadians(i));
            float dz = (float) Math.cos(Math.toRadians(i));
            BUFFER.pos(matrix4f, (float) (circle.pos.x + dx * radiusValue), (float) circle.pos.y + yOffset, (float) (circle.pos.z + dz * radiusValue))
                    .color(ColorUtil.rgba(ColorUtil.red(color), ColorUtil.green(color), ColorUtil.blue(color), (int) (alphaValue * 255))).endVertex();
            BUFFER.pos(matrix4f, (float) (circle.pos.x + dx * (1F - sizeValue) * radiusValue), (float) circle.pos.y + yOffset, (float) (circle.pos.z + dz * (1F - sizeValue) * radiusValue))
                    .color(ColorUtil.rgba(ColorUtil.red(color), ColorUtil.green(color), ColorUtil.blue(color), 0)).endVertex();
        }
        TESSELLATOR.draw();

        BUFFER.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        float lineWidth = 0.0075F;
        for (float i = 0; i <= 360; i += 1) {
            float dx = (float) -Math.sin(Math.toRadians(i));
            float dz = (float) Math.cos(Math.toRadians(i));
            BUFFER.pos(matrix4f, (float) (circle.pos.x + dx * (1F - lineWidth) * radiusValue), (float) circle.pos.y + yOffset, (float) (circle.pos.z + dz * (1F - lineWidth) * radiusValue))
                    .color(ColorUtil.rgba(255, 255, 255, (int) (alphaValue * 255))).endVertex();
            BUFFER.pos(matrix4f, (float) (circle.pos.x + dx * (1F + lineWidth) * radiusValue), (float) circle.pos.y + yOffset, (float) (circle.pos.z + dz * (1F + lineWidth) * radiusValue))
                    .color(ColorUtil.rgba(255, 255, 255, (int) (alphaValue * 255))).endVertex();
        }
        TESSELLATOR.draw();
    }

    private void addCircle(final Entity entity) {
        Vector3d vec = getEntityPosition(entity).add(0.D, .005D, 0.D);
        circles.add(new CircleEntity(vec, System.currentTimeMillis(), circleDelay.getValue().intValue()));
    }

    private Vector3d getEntityPosition(final Entity entity) {
        return RenderUtil3D.interpolate(entity, mc.getRenderPartialTicks());
    }

    private void clear() {
        if (!circles.isEmpty()) circles.clear();
    }

    private void setupDraw(final MatrixStack matrix, final net.mojang.blaze3d.systems.IRenderCall render) {
        matrix.push();
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.alphaFunc(GL14C.GL_GREATER, 0f);
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        final boolean light = GL11.glIsEnabled(GL11.GL_LIGHTING);
        if (light) RenderSystem.disableLighting();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        RenderSystem.blendFunc(GL14C.GL_SRC_ALPHA, GL14C.GL_ONE);
        render.execute();
        RenderSystem.blendFunc(GL14C.GL_SRC_ALPHA, GL14C.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.clearCurrentColor();
        RenderSystem.shadeModel(GL11.GL_FLAT);
        if (light) RenderSystem.enableLighting();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.alphaFunc(GL14C.GL_GREATER, 0.1F);
        RenderSystem.enableAlphaTest();
        matrix.pop();
    }

    private record CircleEntity(Vector3d pos, long time, int aliveTime) {
        public float getDeltaTime() {
            return (System.currentTimeMillis() - time) / (float) aliveTime;
        }
    }
}