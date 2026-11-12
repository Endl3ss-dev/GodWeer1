package tech.lightclient.mixins.render;


import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.render.EventAspectRatio;
import tech.lightclient.client.events.impl.render.EventFov;
import tech.lightclient.client.utils.math.ProjectionUtil;
import tech.lightclient.client.utils.render.WetSurfaceRenderer;
import tech.lightclient.client.utils.render3d.ColorGradingRenderer;
import tech.lightclient.client.utils.render3d.HandGlowRenderer;
import tech.lightclient.client.utils.render3d.HandsRenderer;
import tech.lightclient.client.utils.render3d.ShaderHandsRenderer;
import tech.lightclient.client.utils.render3d.ShaderTestRenderer;
import tech.lightclient.client.utils.render.main.postfx.MotionBlurRenderer;
import tech.lightclient.modules.impl.render.Ambience;
import tech.lightclient.modules.impl.render.Beautifully;
import tech.lightclient.modules.impl.render.HandBloom;
import tech.lightclient.modules.impl.render.HandGlow;
import tech.lightclient.modules.impl.render.Hands;
import tech.lightclient.modules.impl.render.ShaderHands;
import tech.lightclient.modules.impl.render.ShaderTest;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.lightclient.ui.gui.ItemTextureCache;

import static tech.lightclient.client.utils.client.IMinecraft.mc;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow @Final private GuiRenderer guiRenderer;
    @Shadow @Final GuiRenderState guiState;
    @Shadow @Final private FogRenderer fogRenderer;

    /**
     * Capture pending item icons into their offscreen framebuffers immediately after vanilla
     * commits the shared GuiRenderState for this frame. At this point the {@link GuiRenderer}
     * and its state are still live, so {@link ItemTextureCache} can redirect the renderer to a
     * per-icon framebuffer and produce a texture usable by the post-blit overlay pass.
     */
    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/render/GuiRenderer;render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
            shift = At.Shift.AFTER))
    private void light$captureItemIcons(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci,
                                        @Local DrawContext drawContext) {
        ItemTextureCache.get().flushCaptures(
                drawContext,
                this.guiRenderer,
                this.guiState,
                this.fogRenderer.getFogBuffer(FogRenderer.FogType.NONE));
    }

    private static HandBloom light$getHandBloom() {
        Object function = LightClient.getFunctionManager().getFunctionByName("HandBloom");
        return function instanceof HandBloom handBloom ? handBloom : null;
    }

    private static ShaderHands light$getShaderHands() {
        Object function = LightClient.getFunctionManager().getFunctionByName("ShaderHands");
        return function instanceof ShaderHands shaderHands ? shaderHands : null;
    }

    private void light$applyWorldMotionBlur(boolean tick) {
        if (!tick || mc.world == null || mc.player == null || mc.currentScreen != null) {
            MotionBlurRenderer.getInstance().reset();
            return;
        }
        Object function = LightClient.getFunctionManager().getFunctionByName("MotionBlur");
        if (function instanceof tech.lightclient.modules.impl.render.MotionBlur motionBlur && motionBlur.isState()) {
            MotionBlurRenderer.getInstance().apply(mc, ((GameRenderer) (Object) this).getCamera(), motionBlur.resolve());
        } else {
            MotionBlurRenderer.getInstance().reset();
        }
    }

    /**
     * Гладкий пол — мокрый пол с отражениями. Вызывается внутри renderWorld сразу после
     * отрисовки мира, поэтому в эффект попадают точные матрицы кадра: projectionMatrix —
     * локальная проекция (slot 7), viewMatrix — матрица поворота камеры (slot 14).
     * Мир рендерится относительно камеры, трансляцию добавляет сам WetSurfaceRenderer.
     */
    private void light$applyWetFloor(Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        try {
            if (mc.world == null || mc.player == null) {
                WetSurfaceRenderer.getInstance().reset();
                return;
            }
            Object fn = LightClient.getFunctionManager() != null ? LightClient.getFunctionManager().getFunctionByName("Ambience") : null;
            if (fn instanceof Ambience ambience && ambience.isState() && ambience.smoothFloor.get()) {
                Camera camera = ((GameRenderer) (Object) this).getCamera();
                if (camera == null) return;
                Matrix4f projection;
                if (projectionMatrix != null) {
                    projection = new Matrix4f(projectionMatrix);
                } else {
                    float tickDelta = mc.getRenderTickCounter().getTickProgress(false);
                    float fov = 70.0f;
                    try {
                        fov = ((tech.lightclient.mixins.accessors.GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, tickDelta, true);
                    } catch (Throwable ignored) {}
                    projection = new Matrix4f(mc.gameRenderer.getBasicProjectionMatrix(fov));
                }
                Matrix4f view = viewMatrix != null ? new Matrix4f(viewMatrix) : new Matrix4f()
                        .rotate(camera.getRotation().conjugate(new org.joml.Quaternionf()))
                        .translate((float) -camera.getCameraPos().x, (float) -camera.getCameraPos().y, (float) -camera.getCameraPos().z);
                ambience.renderWetSurfaceFrame(camera, view, projection);
            } else {
                WetSurfaceRenderer.getInstance().reset();
            }
        } catch (Throwable ignored) {
            try { WetSurfaceRenderer.getInstance().reset(); } catch (Throwable ignored2) {}
        }
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/ObjectAllocator;Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V",
            shift = At.Shift.AFTER))
    private void light$wetFloorInWorld(RenderTickCounter tickCounter, CallbackInfo ci,
                                       @Local(ordinal = 0) Matrix4f projectionMatrix,
                                       @Local(ordinal = 1) Matrix4f viewMatrix) {
        light$applyWetFloor(viewMatrix, projectionMatrix);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void light$resetHandGlow(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        HandGlowRenderer.resetOverrides();
        ShaderHandsRenderer.resetOverrides();
        HandsRenderer.resetOverrides();
    }

    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V",
            shift = At.Shift.BEFORE))
    public void hookRender(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci, @Local DrawContext drawContext) {
        if (mc.currentScreen instanceof ProgressScreen) {
            return;
        }

        if (tick && mc.world != null && mc.player != null) {
            ShaderTest shaderTest = LightClient.getFunctionManager().getShaderTest();
            if (shaderTest != null && shaderTest.isState() && ShaderTestRenderer.needsPost()) {
                ShaderTestRenderer.postHandsRender(shaderTest);
            }

            Hands hands = LightClient.getFunctionManager().getHands();
            HandBloom handBloom = light$getHandBloom();
            ShaderHands shaderHands = light$getShaderHands();
            if (((hands != null && hands.isState()) || (handBloom != null && handBloom.isState())) && HandsRenderer.needsPost()) {
                HandsRenderer.postHandsRender(hands, handBloom);
            } else if (shaderHands != null && shaderHands.isState() && ShaderHandsRenderer.needsPost()) {
                ShaderHandsRenderer.postHandsRender(shaderHands);
            } else {
                HandGlow handGlow = LightClient.getFunctionManager().getHandGlow();
                if (handGlow != null && handGlow.isState() && HandGlowRenderer.needsPost()) {
                    HandGlowRenderer.postHandsRender(
                            handGlow.getIntensity(),
                            handGlow.getRadius(),
                            handGlow.getFlamePower(),
                            handGlow.getFlameSpeed(),
                            handGlow.isRainbowFlame()
                    );
                }
            }
        }

        if (Beautifully.shouldApplyColorGrading()) {
            ColorGradingRenderer.applyColorGrading(Beautifully.getSaturation(), Beautifully.getWarmth());
        }

        // World effects are complete here, while the HUD/NameTags are still
        // pending. The blur therefore affects only the rendered world.
        light$applyWorldMotionBlur(tick);

        if (!(mc.options.hudHidden)) {
            LightClient.setCurrentDrawContext(drawContext);
            LightClient.hookRenderHud();
        }
    }

    @Inject(method = "renderHand", at = @At("HEAD"))
    private void light$preHandGlow(float tickDelta, boolean notAffectingFov, Matrix4f viewMatrix, CallbackInfo ci) {
        // ShaderTest (эффект ShaderEsp на руках) имеет наивысший приоритет
        ShaderTest shaderTest = LightClient.getFunctionManager().getShaderTest();
        if (shaderTest != null && shaderTest.isState()) {
            ShaderTestRenderer.preHandCapture();
            return;
        }

        Hands hands = LightClient.getFunctionManager().getHands();
        HandBloom handBloom = light$getHandBloom();
        if ((hands != null && hands.isState()) || (handBloom != null && handBloom.isState())) {
            HandsRenderer.preHandCapture();
            return;
        }

        ShaderHands shaderHands = light$getShaderHands();
        if (shaderHands != null && shaderHands.isState()) {
            ShaderHandsRenderer.preHandCapture();
            return;
        }

        HandGlow handGlow = LightClient.getFunctionManager().getHandGlow();
        if (handGlow != null && handGlow.isState() && handGlow.getIntensity() > 0.01f && handGlow.getRadius() > 0.5f) {
            HandGlowRenderer.preHandCapture();
        }
    }

    @Inject(method = "renderHand", at = @At("TAIL"))
    private void light$postHandGlow(float tickDelta, boolean notAffectingFov, Matrix4f viewMatrix, CallbackInfo ci) {
        ShaderTest shaderTest = LightClient.getFunctionManager().getShaderTest();
        if (shaderTest != null && shaderTest.isState()) {
            ShaderTestRenderer.postHandCapture();
            return;
        }

        Hands hands = LightClient.getFunctionManager().getHands();
        HandBloom handBloom = light$getHandBloom();
        if ((hands != null && hands.isState()) || (handBloom != null && handBloom.isState())) {
            HandsRenderer.postHandCapture();
            return;
        }

        ShaderHands shaderHands = light$getShaderHands();
        if (shaderHands != null && shaderHands.isState()) {
            ShaderHandsRenderer.postHandCapture();
            return;
        }

        HandGlow handGlow = LightClient.getFunctionManager().getHandGlow();
        if (handGlow != null && handGlow.isState() && handGlow.getIntensity() > 0.01f && handGlow.getRadius() > 0.5f) {
            HandGlowRenderer.postHandCapture();
        }
    }

    private static final EventFov light$EVENT_FOV = new EventFov();
    private static final EventAspectRatio light$EVENT_ASPECT = new EventAspectRatio();

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void onGetFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir) {
        if (LightClient.getEventBus() == null) return;
        if (mc.currentScreen != null) return;
        light$EVENT_FOV.setFov((int) (float) cir.getReturnValue());
        light$EVENT_FOV.setTickDelta(tickDelta);
        light$EVENT_FOV.setChangingFov(changingFov);
        light$EVENT_FOV.setCancelled(false);
        LightClient.getEventBus().post(light$EVENT_FOV);
        if (light$EVENT_FOV.isCancelled()) {
            cir.setReturnValue((float) light$EVENT_FOV.getFov());
        }
    }

    @Inject(method = "getBasicProjectionMatrix", at = @At("RETURN"), cancellable = true)
    private void onGetProjectionMatrix(float fov, CallbackInfoReturnable<Matrix4f> cir) {
        if (LightClient.getEventBus() == null) return;
        light$EVENT_ASPECT.setValue(1.0f);
        light$EVENT_ASPECT.setCancelled(false);
        LightClient.getEventBus().post(light$EVENT_ASPECT);
        if (light$EVENT_ASPECT.isCancelled()) {
            float value = light$EVENT_ASPECT.getValue();
            ProjectionUtil.currentAspectMultiplier = value;
            Matrix4f mat = new Matrix4f(cir.getReturnValue());
            mat.scale(value, 1.0f, 1.0f);
            cir.setReturnValue(mat);
        } else {
            ProjectionUtil.currentAspectMultiplier = 1.0f;
        }
    }
}
