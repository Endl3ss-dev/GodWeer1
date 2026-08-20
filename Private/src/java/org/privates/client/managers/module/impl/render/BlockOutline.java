package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;
import org.privates.client.utils.render.shader.ShaderManager;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "BlockOutline", description = "Отображает контур блоков при наведении на них", category = Category.RENDER)
public class BlockOutline extends Module {

    public static BlockOutline getInstance() {
        return Instance.get(BlockOutline.class);
    }

    private final SliderSetting thickness = new SliderSetting(this, "Толщина", 1.5F, 0.5F, 5.0F, 0.1F);
    private final BooleanSetting fill = new BooleanSetting(this, "Заливка", true);
    private final ModeSetting fillMode = new ModeSetting(this, "Стиль заливки", "Обычный", "Cosmic Nebula", "Cobweb", "Glow Waves")
            .setVisible(fill::getValue);
    private final SliderSetting fillAlpha = new SliderSetting(this, "Прозрачность заливки", 0.2F, 0.1F, 1.0F, 0.05F)
            .setVisible(fill::getValue);
    private final SliderSetting nebulaDensity = new SliderSetting(this, "Nebula Density", 1.0F, 0.3F, 2.5F, 0.1F)
            .setVisible(() -> fill.getValue() && fillMode.is("Cosmic Nebula"));
    private final SliderSetting cobwebSpeed = new SliderSetting(this, "Cobweb Speed", 0.4F, 0.05F, 2.0F, 0.05F)
            .setVisible(() -> fill.getValue() && fillMode.is("Cobweb"));
    private final SliderSetting cobwebIntensity = new SliderSetting(this, "Cobweb Intensity", 1.0F, 0.2F, 3.0F, 0.1F)
            .setVisible(() -> fill.getValue() && fillMode.is("Cobweb"));
    private final SliderSetting glowWaveSpeed = new SliderSetting(this, "Glow Wave Speed", 1.0F, 0.1F, 3.0F, 0.1F)
            .setVisible(() -> fill.getValue() && fillMode.is("Glow Waves"));
    private final SliderSetting glowWaveIntensity = new SliderSetting(this, "Glow Wave Intensity", 1.2F, 0.3F, 2.5F, 0.1F)
            .setVisible(() -> fill.getValue() && fillMode.is("Glow Waves"));
    private final SliderSetting animationDuration = new SliderSetting(this, "Длительность анимации (мс)", 200F, 50F, 1000F, 10F);
    private final SliderSetting transitionDuration = new SliderSetting(this, "Длительность перелёта (мс)", 150F, 50F, 500F, 10F);

    private enum AnimationState {IDLE, FADING_IN, VISIBLE, FADING_OUT}

    BlockPos currentBlockPos;
    VoxelShape currentShape;
    long animationStartTime;
    AnimationState animationState = AnimationState.IDLE;
    float currentAlphaMultiplier = 0f;

    BlockPos prevBlockPos;
    VoxelShape prevShape;
    long transitionStartTime;
    float transitionProgress = 1.0f;

    @Override
    public void onEnable() {
        resetAnimation();
    }

    @Override
    public void onDisable() {
        resetAnimation();
    }

    private void resetAnimation() {
        currentBlockPos = null;
        currentShape = null;
        prevBlockPos = null;
        prevShape = null;
        animationState = AnimationState.IDLE;
        currentAlphaMultiplier = 0f;
        transitionProgress = 1.0f;
    }

    private float easeOutCubic(float t) {
        float inv = 1.0f - t;
        return 1.0f - inv * inv * inv;
    }

    private float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        return 1.0f + c3 * (float) Math.pow(t - 1.0, 3) + c1 * (float) Math.pow(t - 1.0, 2);
    }

    @EventHandler
    public void onRender3D(Render3DLastEvent event) {
        if (mc.world == null) return;

        BlockPos newTargetPos = null;
        if (mc.objectMouseOver != null && mc.objectMouseOver.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockHit = (BlockRayTraceResult) mc.objectMouseOver;
            if (!mc.world.getBlockState(blockHit.getPos()).isAir())
                newTargetPos = blockHit.getPos();
        }

        long currentTime = System.currentTimeMillis();

        if (newTargetPos != null && !newTargetPos.equals(currentBlockPos)) {
            if (currentBlockPos != null && animationState != AnimationState.IDLE) {
                prevBlockPos = currentBlockPos;
                prevShape = currentShape;
                transitionStartTime = currentTime;
                transitionProgress = 0.0f;
            }
            currentBlockPos = newTargetPos;
            currentShape = mc.world.getBlockState(currentBlockPos).getShape(mc.world, currentBlockPos);

            if (animationState == AnimationState.IDLE || animationState == AnimationState.FADING_OUT) {
                animationState = AnimationState.FADING_IN;
                animationStartTime = currentTime;
            }
        }

        if (newTargetPos == null && animationState != AnimationState.FADING_OUT && currentBlockPos != null) {
            animationState = AnimationState.FADING_OUT;
            animationStartTime = currentTime;
            prevBlockPos = null;
            prevShape = null;
            transitionProgress = 1.0f;
        }

        if (currentBlockPos == null || currentShape == null || currentShape.isEmpty() || animationState == AnimationState.IDLE) {
            currentAlphaMultiplier = 0f;
            return;
        }

        float fadeProgress = Math.min((currentTime - animationStartTime) / animationDuration.getValue(), 1.0F);
        switch (animationState) {
            case FADING_IN:
                currentAlphaMultiplier = easeOutCubic(fadeProgress);
                if (fadeProgress >= 1.0F) animationState = AnimationState.VISIBLE;
                break;
            case FADING_OUT:
                currentAlphaMultiplier = 1.0F - easeOutCubic(fadeProgress);
                if (fadeProgress >= 1.0F) {
                    resetAnimation();
                    return;
                }
                break;
            default:
                currentAlphaMultiplier = 1.0F;
        }

        if (transitionProgress < 1.0f) {
            transitionProgress = Math.min(
                    (currentTime - transitionStartTime) / transitionDuration.getValue(), 1.0f);
        }

        float easedT = Math.max(0.0f, Math.min(easeOutBack(Math.min(transitionProgress, 1.0f)), 1.0f));
        renderBlockOutlineInterpolated(event.getMatrix(), easedT);
    }

    private void renderBlockOutlineInterpolated(MatrixStack matrixStack, float t) {
        AxisAlignedBB curAABB = getBlockAABB(currentShape, currentBlockPos);
        if (curAABB == null) return;

        AxisAlignedBB renderAABB;
        if (prevBlockPos != null && prevShape != null && t < 1.0f) {
            AxisAlignedBB prevAABB = getBlockAABB(prevShape, prevBlockPos);
            renderAABB = (prevAABB != null) ? lerpAABB(prevAABB, curAABB, t) : curAABB;
        } else {
            renderAABB = curAABB;
        }

        renderBlockOutline(matrixStack, renderAABB, currentAlphaMultiplier);
    }

    private AxisAlignedBB getBlockAABB(VoxelShape shape, BlockPos pos) {
        if (shape == null || shape.isEmpty()) return null;
        AxisAlignedBB result = null;
        for (AxisAlignedBB aabb : shape.toBoundingBoxList()) {
            AxisAlignedBB world = new AxisAlignedBB(
                    pos.getX() + aabb.minX, pos.getY() + aabb.minY, pos.getZ() + aabb.minZ,
                    pos.getX() + aabb.maxX, pos.getY() + aabb.maxY, pos.getZ() + aabb.maxZ);
            result = (result == null) ? world : result.union(world);
        }
        return result;
    }

    private AxisAlignedBB lerpAABB(AxisAlignedBB a, AxisAlignedBB b, float t) {
        return new AxisAlignedBB(
                a.minX + (b.minX - a.minX) * t, a.minY + (b.minY - a.minY) * t, a.minZ + (b.minZ - a.minZ) * t,
                a.maxX + (b.maxX - a.maxX) * t, a.maxY + (b.maxY - a.maxY) * t, a.maxZ + (b.maxZ - a.maxZ) * t);
    }

    private void renderBlockOutline(MatrixStack matrixStack, AxisAlignedBB aabb, float alpha) {
        double minX = aabb.minX, maxX = aabb.maxX;
        double minY = aabb.minY, maxY = aabb.maxY;
        double minZ = aabb.minZ, maxZ = aabb.maxZ;

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
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        RenderSystem.lineWidth(thickness.getValue());

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        int base = Theme.getInstance().clientColor();
        int outlineColor = ColorUtil.setAlpha(base, (int) ((base >> 24 & 0xFF) * alpha));
        int plainFillColor = ColorUtil.replAlpha(base, (int) (fillAlpha.getValue() * 255 * alpha));

        boolean isNebula = fill.getValue() && fillMode.is("Cosmic Nebula");
        boolean isCobweb = fill.getValue() && fillMode.is("Cobweb");
        boolean isGlowWaves = fill.getValue() && fillMode.is("Glow Waves");

        if (fill.getValue() && alpha > 0.01F) {
            if (!isNebula && !isCobweb && !isGlowWaves) {
                buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                RenderUtil3D.renderFilledBox(buf, minX, minY, minZ, maxX, maxY, maxZ, plainFillColor);
                tess.draw();
            } else {
                GL11.glEnable(GL11.GL_STENCIL_TEST);
                GL11.glStencilMask(0xFF);
                GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
                GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
                GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_REPLACE, GL11.GL_REPLACE);
                GL11.glColorMask(false, false, false, false);

                buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                RenderUtil3D.renderFilledBox(buf, minX, minY, minZ, maxX, maxY, maxZ,
                        ColorUtil.getColor(255, 255, 255, 255));
                tess.draw();

                GL11.glColorMask(true, true, true, true);
                GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
                GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

                if (isNebula) {
                    drawShaderInWorldspace(minX, minY, minZ, maxX, maxY, maxZ, alpha, ShaderManager.cosmicNebula, false);
                } else if (isCobweb) {
                    drawShaderInWorldspace(minX, minY, minZ, maxX, maxY, maxZ, alpha, ShaderManager.cobweb, true);
                } else if (isGlowWaves) {
                    drawShaderInWorldspace(minX, minY, minZ, maxX, maxY, maxZ, alpha, ShaderManager.blockGlowWaves, false);
                }

                GL11.glDisable(GL11.GL_STENCIL_TEST);
            }
        }

        if (alpha > 0.01F) {
            buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            RenderUtil3D.renderWireframeBox(buf, minX, minY, minZ, maxX, maxY, maxZ, outlineColor);
            tess.draw();
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
        matrixStack.pop();
    }

    private void drawShaderInWorldspace(double minX, double minY, double minZ,
                                        double maxX, double maxY, double maxZ,
                                        float alpha, ShaderManager shader, boolean isCobweb) {
        mc.getFramebuffer().bindFramebufferTexture();

        shader.load();
        shader.setUniformf("u_texel_size",
                1.0F / mc.getFramebuffer().framebufferWidth,
                1.0F / mc.getFramebuffer().framebufferHeight);
        shader.setUniformf("u_time", (System.currentTimeMillis() % 1000000) / 1000.0F);

        if (shader == ShaderManager.cobweb) {
            shader.setUniformf("u_speed", cobwebSpeed.getValue());
            shader.setUniformf("u_intensity", cobwebIntensity.getValue() * alpha);
        } else if (shader == ShaderManager.cosmicNebula) {
            shader.setUniformf("u_nebula_density", nebulaDensity.getValue() * alpha);
        } else if (shader == ShaderManager.blockGlowWaves) {
            shader.setUniformf("u_wave_speed", glowWaveSpeed.getValue());
            shader.setUniformf("u_wave_intensity", glowWaveIntensity.getValue() * alpha);
        }

        shader.setUniformi("u_diffuse_sampler", 0);
        shader.setUniformi("u_other_sampler", 20);

        GL13.glActiveTexture(GL13.GL_TEXTURE20);
        mc.getFramebuffer().bindFramebufferTexture();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        mc.getFramebuffer().bindFramebufferTexture();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        double sizeX = maxX - minX;
        double sizeY = maxY - minY;
        double sizeZ = maxZ - minZ;
        double maxSize = Math.max(Math.max(sizeX, sizeY), sizeZ);

        double centerX = (minX + maxX) / 2.0;
        double centerY = (minY + maxY) / 2.0;
        double centerZ = (minZ + maxZ) / 2.0;

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        buf.pos(minX, minY, minZ).tex((float)((minX - centerX) / maxSize + 0.5), (float)((minZ - centerZ) / maxSize + 0.5)).endVertex();
        buf.pos(maxX, minY, minZ).tex((float)((maxX - centerX) / maxSize + 0.5), (float)((minZ - centerZ) / maxSize + 0.5)).endVertex();
        buf.pos(maxX, minY, maxZ).tex((float)((maxX - centerX) / maxSize + 0.5), (float)((maxZ - centerZ) / maxSize + 0.5)).endVertex();
        buf.pos(minX, minY, maxZ).tex((float)((minX - centerX) / maxSize + 0.5), (float)((maxZ - centerZ) / maxSize + 0.5)).endVertex();

        buf.pos(minX, maxY, minZ).tex((float)((minX - centerX) / maxSize + 0.5), (float)((minZ - centerZ) / maxSize + 0.5)).endVertex();
        buf.pos(minX, maxY, maxZ).tex((float)((minX - centerX) / maxSize + 0.5), (float)((maxZ - centerZ) / maxSize + 0.5)).endVertex();
        buf.pos(maxX, maxY, maxZ).tex((float)((maxX - centerX) / maxSize + 0.5), (float)((maxZ - centerZ) / maxSize + 0.5)).endVertex();
        buf.pos(maxX, maxY, minZ).tex((float)((maxX - centerX) / maxSize + 0.5), (float)((minZ - centerZ) / maxSize + 0.5)).endVertex();

        buf.pos(minX, minY, minZ).tex((float)((minX - centerX) / maxSize + 0.5), (float)((minY - centerY) / maxSize + 0.5)).endVertex();
        buf.pos(minX, maxY, minZ).tex((float)((minX - centerX) / maxSize + 0.5), (float)((maxY - centerY) / maxSize + 0.5)).endVertex();
        buf.pos(maxX, maxY, minZ).tex((float)((maxX - centerX) / maxSize + 0.5), (float)((maxY - centerY) / maxSize + 0.5)).endVertex();
        buf.pos(maxX, minY, minZ).tex((float)((maxX - centerX) / maxSize + 0.5), (float)((minY - centerY) / maxSize + 0.5)).endVertex();

        buf.pos(maxX, minY, maxZ).tex((float)((maxX - centerX) / maxSize + 0.5), (float)((minY - centerY) / maxSize + 0.5)).endVertex();
        buf.pos(maxX, maxY, maxZ).tex((float)((maxX - centerX) / maxSize + 0.5), (float)((maxY - centerY) / maxSize + 0.5)).endVertex();
        buf.pos(minX, maxY, maxZ).tex((float)((minX - centerX) / maxSize + 0.5), (float)((maxY - centerY) / maxSize + 0.5)).endVertex();
        buf.pos(minX, minY, maxZ).tex((float)((minX - centerX) / maxSize + 0.5), (float)((minY - centerY) / maxSize + 0.5)).endVertex();

        buf.pos(minX, minY, maxZ).tex((float)((minZ - centerZ) / maxSize + 0.5), (float)((minY - centerY) / maxSize + 0.5)).endVertex();
        buf.pos(minX, maxY, maxZ).tex((float)((maxZ - centerZ) / maxSize + 0.5), (float)((maxY - centerY) / maxSize + 0.5)).endVertex();
        buf.pos(minX, maxY, minZ).tex((float)((minZ - centerZ) / maxSize + 0.5), (float)((maxY - centerY) / maxSize + 0.5)).endVertex();
        buf.pos(minX, minY, minZ).tex((float)((minZ - centerZ) / maxSize + 0.5), (float)((minY - centerY) / maxSize + 0.5)).endVertex();

        buf.pos(maxX, minY, minZ).tex((float)((minZ - centerZ) / maxSize + 0.5), (float)((minY - centerY) / maxSize + 0.5)).endVertex();
        buf.pos(maxX, maxY, minZ).tex((float)((minZ - centerZ) / maxSize + 0.5), (float)((maxY - centerY) / maxSize + 0.5)).endVertex();
        buf.pos(maxX, maxY, maxZ).tex((float)((maxZ - centerZ) / maxSize + 0.5), (float)((maxY - centerY) / maxSize + 0.5)).endVertex();
        buf.pos(maxX, minY, maxZ).tex((float)((maxZ - centerZ) / maxSize + 0.5), (float)((minY - centerY) / maxSize + 0.5)).endVertex();

        tess.draw();

        shader.unload();

        GL13.glActiveTexture(GL13.GL_TEXTURE20);
        GlStateManager.bindTexture(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GlStateManager.bindTexture(0);
    }
}