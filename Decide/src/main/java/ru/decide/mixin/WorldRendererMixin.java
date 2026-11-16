/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_12076
 *  net.minecraft.class_1294
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_276
 *  net.minecraft.class_310
 *  net.minecraft.class_4184
 *  net.minecraft.class_4587
 *  net.minecraft.class_761
 *  net.minecraft.class_769
 *  net.minecraft.class_9779
 *  net.minecraft.class_9922
 *  net.minecraft.class_9975
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector4f
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12076;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_761;
import net.minecraft.class_769;
import net.minecraft.class_9779;
import net.minecraft.class_9922;
import net.minecraft.class_9975;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.module.impl.render.AtmosphereFog;
import ru.decide.module.impl.render.ExplosionWave;
import ru.decide.module.impl.render.JumpCircle;
import ru.decide.module.impl.render.NoRender;
import ru.decide.module.impl.render.ShaderEsp;
import ru.decide.module.impl.render.ShaderSky;
import ru.decide.module.impl.render.weather.WeatherRenderer;
import ru.decide.utils.render.ShaderSkyRenderer;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_761.class})
public class WorldRendererMixin {
    @Shadow
    private @Nullable class_276 field_53080;
    @Shadow
    private @Nullable class_769 field_4112;

    @Inject(method={"method_3254"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void espCancelOutlineDraw(CallbackInfo ci) {
        ShaderEsp esp = ShaderEsp.getInstance();
        if (esp != null && esp.isEnabled() && !ShaderEsp.getAllTargets().isEmpty()) {
            ci.cancel();
        }
    }

    @Inject(method={"method_62215"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_9975;method_62302(I)V", shift=At.Shift.AFTER)}, require=0)
    private static void renderShaderSkyLayer(GpuBufferSlice fogBuffer, class_12076 skyRenderState, class_9975 skyRendering, CallbackInfo ci) {
        ShaderSky shaderSky = ShaderSky.getInstance();
        if (shaderSky == null || !shaderSky.isEnabled() || shaderSky.mode.is("Blur")) {
            return;
        }
        ShaderSkyRenderer.getInstance().renderCelestialShader();
    }

    @Inject(method={"method_22710"}, at={@At(value="HEAD")})
    private void captureWeatherFrameMatrix(class_9922 allocator, class_9779 tickCounter, boolean renderBlockOutline, class_4184 camera, Matrix4f positionMatrix, Matrix4f basicProjectionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci) {
        WeatherRenderer.captureFrame(positionMatrix, basicProjectionMatrix);
    }

    @Inject(method={"method_22710"}, at={@At(value="RETURN")})
    private void publishWorldRenderEvent(class_9922 allocator, class_9779 tickCounter, boolean renderBlockOutline, class_4184 camera, Matrix4f positionMatrix, Matrix4f basicProjectionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci) {
        ExplosionWave explosionWave;
        JumpCircle jumpCircle;
        ShaderSky shaderSky;
        class_4587 stack = new class_4587();
        Matrix4f basePositionMatrix = new Matrix4f((Matrix4fc)positionMatrix);
        stack.method_34425((Matrix4fc)new Matrix4f((Matrix4fc)basePositionMatrix));
        new EventRender3D(stack, tickCounter.method_60637(true), projectionMatrix).hook();
        AtmosphereFog atmosphereFog = AtmosphereFog.getInstance();
        if (atmosphereFog != null && atmosphereFog.isEnabled()) {
            try {
                atmosphereFog.renderFog(class_310.method_1551().method_1522(), positionMatrix, projectionMatrix, tickCounter.method_60637(true));
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        ShaderEsp.getInstance().customFboReference = this.field_53080;
        if (ShaderEsp.getInstance().isEnabled()) {
            ShaderEsp.getInstance().onRender3D();
        }
        if ((shaderSky = ShaderSky.getInstance()) != null && shaderSky.isEnabled()) {
            ShaderSkyRenderer.getInstance().renderSky();
        }
        if ((jumpCircle = JumpCircle.getInstance()) != null && jumpCircle.isEnabled()) {
            try {
                jumpCircle.renderSouls(class_310.method_1551().method_1522(), positionMatrix, projectionMatrix);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                jumpCircle.renderRefraction(class_310.method_1551().method_1522(), positionMatrix, projectionMatrix);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if ((explosionWave = ExplosionWave.getInstance()) != null && explosionWave.isEnabled()) {
            try {
                explosionWave.renderWaves(class_310.method_1551().method_1522(), positionMatrix, projectionMatrix);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    @Inject(method={"method_43788"}, at={@At(value="HEAD")}, cancellable=true)
    private void onHasBlindnessOrDarkness(class_4184 camera, CallbackInfoReturnable<Boolean> cir) {
        NoRender noRender = NoRender.getInstance();
        if (noRender == null || !noRender.isEnabled()) {
            return;
        }
        class_1297 entity = camera.method_19331();
        if (!(entity instanceof class_1309)) {
            return;
        }
        class_1309 livingEntity = (class_1309)entity;
        boolean hasBlindness = livingEntity.method_6059(class_1294.field_5919);
        boolean hasDarkness = livingEntity.method_6059(class_1294.field_38092);
        if (noRender.ignoreZalupa.getValue().booleanValue() && hasBlindness && !hasDarkness) {
            cir.setReturnValue((Object)false);
        }
        if (noRender.ignoreZalupa.getValue().booleanValue() && hasDarkness && !hasBlindness) {
            cir.setReturnValue((Object)false);
        }
        if (noRender.ignoreZalupa.getValue().booleanValue()) {
            cir.setReturnValue((Object)false);
        }
    }

    @Inject(method={"method_18146"}, at={@At(value="HEAD")}, cancellable=true)
    private void onScheduleBlockRenders(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, CallbackInfo ci) {
        if (this.field_4112 == null) {
            ci.cancel();
        }
    }

    @Inject(method={"method_8571"}, at={@At(value="HEAD")}, cancellable=true)
    private void onScheduleChunkRender(int x, int y, int z, CallbackInfo ci) {
        if (this.field_4112 == null) {
            ci.cancel();
        }
    }
}

