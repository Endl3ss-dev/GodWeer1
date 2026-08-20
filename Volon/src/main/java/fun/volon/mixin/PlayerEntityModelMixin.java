package fun.volon.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fun.volon.client.modules.impl.render.CustomModels;

@Mixin(BipedEntityModel.class)
public class PlayerEntityModelMixin {

    @Shadow @Final public ModelPart head;
    @Shadow @Final public ModelPart body;
    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart leftArm;
    @Shadow @Final public ModelPart rightLeg;
    @Shadow @Final public ModelPart leftLeg;

    @Inject(method = "setAngles(Lnet/minecraft/client/render/entity/state/BipedEntityRenderState;)V", at = @At("TAIL"))
    private void Volon$applyCustomModelTransforms(BipedEntityRenderState state, CallbackInfo ci) {
        if (CustomModels.INSTANCE == null || !CustomModels.INSTANCE.isEnable()) {
            // Сбрасываем масштабирование когда модуль выключен
            head.xScale = 1.0f;
            head.yScale = 1.0f;
            head.zScale = 1.0f;

            body.xScale = 1.0f;
            body.yScale = 1.0f;
            body.zScale = 1.0f;

            rightArm.xScale = 1.0f;
            rightArm.yScale = 1.0f;
            rightArm.zScale = 1.0f;

            leftArm.xScale = 1.0f;
            leftArm.yScale = 1.0f;
            leftArm.zScale = 1.0f;

            rightLeg.xScale = 1.0f;
            rightLeg.yScale = 1.0f;
            rightLeg.zScale = 1.0f;

            leftLeg.xScale = 1.0f;
            leftLeg.yScale = 1.0f;
            leftLeg.zScale = 1.0f;
            return;
        }

        float headScale = CustomModels.INSTANCE.getHeadScale();
        float bodyScale = CustomModels.INSTANCE.getBodyScale();
        float armsScale = CustomModels.INSTANCE.getArmsScale();
        float legsScale = CustomModels.INSTANCE.getLegsScale();

        // Применяем масштабирование к частям модели
        head.xScale = headScale;
        head.yScale = headScale;
        head.zScale = headScale;

        body.xScale = bodyScale;
        body.yScale = bodyScale * 0.9f;
        body.zScale = bodyScale;

        rightArm.xScale = armsScale;
        rightArm.yScale = 1.0f;
        rightArm.zScale = armsScale;

        leftArm.xScale = armsScale;
        leftArm.yScale = 1.0f;
        leftArm.zScale = armsScale;

        rightLeg.xScale = bodyScale * 0.8f;
        rightLeg.yScale = legsScale;
        rightLeg.zScale = bodyScale * 0.8f;

        leftLeg.xScale = bodyScale * 0.8f;
        leftLeg.yScale = legsScale;
        leftLeg.zScale = bodyScale * 0.8f;

        // Корректируем позиции для правильного отображения
        float legYOffset = (1.0f - legsScale) * 12.0f;
        rightLeg.pivotY += legYOffset;
        leftLeg.pivotY += legYOffset;
    }
}
