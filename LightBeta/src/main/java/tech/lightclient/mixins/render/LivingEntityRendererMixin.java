package tech.lightclient.mixins.render;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import tech.lightclient.LightClient;
import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.entity.EntityRenderEvent;
import tech.lightclient.client.utils.client.IMinecraft;
import tech.lightclient.client.utils.player.rotation.LivingEntityRenderRotation;
import tech.lightclient.modules.impl.render.SeeInvisibles;
import tech.lightclient.ui.hud.TargetEspMain;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> implements IMinecraft {

    @Unique
    private static final Map<LivingEntityRenderState, Float> light$scannerStates =
            Collections.synchronizedMap(new WeakHashMap<>());

    @Shadow
    public abstract M getModel();

    @Shadow
    public abstract Identifier getTexture(S state);

    @Unique
    private EntityRenderEvent light$entityRenderEvent;
    @Unique
    private float light$origBodyYaw;
    @Unique
    private float light$origLastBodyYaw;

    @Unique
    private boolean light$seeInvisiblesRendering;

    @Inject(
            method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
            at = @At("HEAD")
    )
    private void onUpdateRenderStatePre(T livingEntity, S state, float tickDelta, CallbackInfo ci) {
        if (light$entityRenderEvent == null) light$entityRenderEvent = new EntityRenderEvent();
        LightClient.getEventBus().post(light$entityRenderEvent);
        light$entityRenderEvent.setDelta(tickDelta);

        if (livingEntity == mc.player && !LivingEntityRenderRotation.shouldUseOriginalRotation()) {
            light$origBodyYaw = livingEntity.bodyYaw;
            light$origLastBodyYaw = livingEntity.lastBodyYaw;
            livingEntity.bodyYaw = light$entityRenderEvent.getBodyYaw();
            livingEntity.lastBodyYaw = light$entityRenderEvent.getPrevBodyYaw();
        }
    }

    @Inject(
            method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
            at = @At("TAIL")
    )
    private void onUpdateRenderStatePost(T livingEntity, S state, float tickDelta, CallbackInfo ci) {
        if (livingEntity == mc.player && !LivingEntityRenderRotation.shouldUseOriginalRotation()) {
            livingEntity.bodyYaw = light$origBodyYaw;
            livingEntity.lastBodyYaw = light$origLastBodyYaw;
        }
        if (TargetEspMain.isScannerTarget(livingEntity)) {
            light$scannerStates.put(state, TargetEspMain.getScannerAlpha());
        } else {
            light$scannerStates.remove(state);
        }
    }

    @ModifyExpressionValue(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerpAngleDegrees(FFF)F", ordinal = 0))
    private float getLerpedYawHook(float original, @Local(ordinal = 0, argsOnly = true) LivingEntity entity) {
        if (entity == null || light$entityRenderEvent == null) return original;
        if (entity.equals(mc.player)) {
            return LivingEntityRenderRotation.getYaw(light$entityRenderEvent);
        }
        return original;
    }

    @ModifyExpressionValue(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getLerpedPitch(F)F"))
    private float getLerpedPitchHook(float original, @Local(ordinal = 0, argsOnly = true) LivingEntity entity) {
        if (entity == null || light$entityRenderEvent == null) return original;
        if (entity.equals(mc.player)) {
            return LivingEntityRenderRotation.getPitch(light$entityRenderEvent);
        }
        return original;
    }


    @Inject(
            method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
            at = @At("TAIL")
    )
    private void onUpdateRenderStateSeeInvisibles(T livingEntity, S state, float tickDelta, CallbackInfo ci) {
        SeeInvisibles seeInvisibles = LightClient.getFunctionManager().getSeeInvisibles();
        if (seeInvisibles != null && seeInvisibles.isState() && state.invisible && livingEntity instanceof PlayerEntity) {
            if (((Object) this) instanceof PlayerEntityRenderer) {
                state.invisibleToPlayer = false;
            }
        }
    }

    @Inject(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;D)Z", at = @At("HEAD"), cancellable = true)
    private void forceNametagAlways(T livingEntity, double d, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity == MinecraftClient.getInstance().getCameraEntity()) {
            cir.setReturnValue(MinecraftClient.isHudEnabled());
        }

        if (LightClient.getFunctionManager().getNameTags().isState()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isVisible(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;)Z",
            at = @At("RETURN"), cancellable = true)
    private void modifyVisibility(S state, CallbackInfoReturnable<Boolean> cir) {
        SeeInvisibles seeInvisibles = LightClient.getFunctionManager().getSeeInvisibles();
        if (seeInvisibles != null && seeInvisibles.isState() && state.invisible) {
            if (((Object) this) instanceof PlayerEntityRenderer) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getRenderLayer(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;ZZZ)Lnet/minecraft/client/render/RenderLayer;",
            at = @At("RETURN"), cancellable = true)
    private void modifyRenderLayer(S state, boolean visible, boolean translucent, boolean hasOutline, CallbackInfoReturnable<RenderLayer> cir) {
        SeeInvisibles seeInvisibles = LightClient.getFunctionManager().getSeeInvisibles();
        if (seeInvisibles != null && seeInvisibles.isState() && state.invisible) {
            if (((Object) this) instanceof PlayerEntityRenderer) {
                Identifier texture = this.getTexture(state);
                cir.setReturnValue(RenderLayers.entityTranslucent(texture, true));
            }
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
            at = @At("HEAD"), cancellable = true
    )
    private void onRenderHead(S state, MatrixStack matrixStack, OrderedRenderCommandQueue commandQueue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        SeeInvisibles seeInvisibles = LightClient.getFunctionManager().getSeeInvisibles();
        light$seeInvisiblesRendering = seeInvisibles != null && seeInvisibles.isState()
                && state.invisible
                && ((Object) this) instanceof PlayerEntityRenderer;
    }

    @Inject(
            method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/texture/Sprite;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V", shift = At.Shift.AFTER)
    )
    private void light$renderScannerOverlay(S state, MatrixStack matrixStack, OrderedRenderCommandQueue commandQueue,
                                            CameraRenderState cameraRenderState, CallbackInfo ci) {
        Float alpha = light$scannerStates.get(state);
        if (alpha == null || alpha <= 0.01f) {
            return;
        }
        int themeColor = TargetEspMain.getScannerTint();
        int overlayColor = (Math.max(0, Math.min(255, Math.round(alpha * 245.0f))) << 24)
                | (themeColor & 0x00FFFFFF);
        commandQueue.submitModel(this.getModel(), state, matrixStack,
                RenderLayers.entityTranslucentEmissive(TargetEspMain.getScannerTexture(), true),
                state.light, OverlayTexture.DEFAULT_UV, overlayColor, null, state.outlineColor, null);
    }

    @Redirect(
            method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/ColorHelper;mix(II)I")
    )
    private int light$modifySeeInvisiblesAlpha(int a, int b) {
        int result = ColorHelper.mix(a, b);
        if (light$seeInvisiblesRendering) {
            SeeInvisibles seeInvisibles = LightClient.getFunctionManager().getSeeInvisibles();
            float opacity = seeInvisibles.opacity.get();
            int colorValue;
            if (seeInvisibles.colorMode.is("От темы")) {
                colorValue = ClientColors.ICON.getRGB();
                int colorAlpha = (colorValue >> 24) & 0xFF;
                int alphaInt = Math.round(colorAlpha * opacity) << 24;
                colorValue = (colorValue & 0x00FFFFFF) | alphaInt;
            } else {
                colorValue = seeInvisibles.color.get();
                int colorAlpha = (colorValue >> 24) & 0xFF;
                int alphaInt = Math.round(colorAlpha * opacity) << 24;
                colorValue = (colorValue & 0x00FFFFFF) | alphaInt;
            }
            return colorValue;
        }
        return result;
    }
}
