package fun.volon.mixin;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fun.volon.client.modules.impl.render.SatelliteFeatureRenderer;
import fun.volon.client.modules.impl.render.CustomModels;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @SuppressWarnings("unchecked")
    @Inject(method = "<init>", at = @At("TAIL"))
    private void Volon$addShoulderPetFeature(EntityRendererFactory.Context context, boolean slim, CallbackInfo ci) {
        FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> rendererContext =
                (FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel>) (Object) this;

        ((LivingEntityRendererAccessor) this).Volon$addFeature(new SatelliteFeatureRenderer(rendererContext, context));
    }

    @Inject(method = "scale(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;)V", at = @At("TAIL"))
    private void Volon$applyCustomModelScale(PlayerEntityRenderState state, MatrixStack matrices, CallbackInfo ci) {
        if (CustomModels.INSTANCE.isEnable()) {
            // Применяем масштабирование для кастомных моделей
            float headScale = CustomModels.INSTANCE.getHeadScale();
            float bodyScale = CustomModels.INSTANCE.getBodyScale();
            float legsScale = CustomModels.INSTANCE.getLegsScale();
            
            // Общее масштабирование модели для компенсации изменений
            float avgScale = (headScale + bodyScale + legsScale) / 3.0f;
            matrices.scale(avgScale * 0.8f, avgScale * 0.8f, avgScale * 0.8f);
        }
    }
}
