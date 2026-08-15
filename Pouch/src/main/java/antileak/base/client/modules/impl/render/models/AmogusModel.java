package antileak.base.client.modules.impl.render.models;

import net.minecraft.client.model.*;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class AmogusModel {
    private ModelPart body;
    private ModelPart eye;
    private ModelPart leftLeg;
    private ModelPart rightLeg;

    private float limbSwing;
    private float limbSwingAmount;

    public AmogusModel() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        root.addChild("body",
                ModelPartBuilder.create()
                        .uv(34, 8).cuboid(-4.0F, 6.0F, -3.0F, 8, 12, 6)
                        .uv(15, 10).cuboid(-3.0F, 9.0F, 3.0F, 6, 8, 3)
                        .uv(26, 0).cuboid(-3.0F, 5.0F, -3.0F, 6, 1, 6),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F)
        );

        root.addChild("eye",
                ModelPartBuilder.create()
                        .uv(0, 10).cuboid(-3.0F, 7.0F, -4.0F, 6, 4, 1),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F)
        );

        root.addChild("leftLeg",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(2.9F, 0.0F, -1.5F, 3, 6, 3),
                ModelTransform.pivot(-2.0F, 18.0F, 0.0F)
        );

        root.addChild("rightLeg",
                ModelPartBuilder.create()
                        .uv(13, 0).cuboid(-5.9F, 0.0F, -1.5F, 3, 6, 3),
                ModelTransform.pivot(2.0F, 18.0F, 0.0F)
        );

        ModelPart modelRoot = TexturedModelData.of(modelData, 64, 64).createModel();

        this.body = modelRoot.getChild("body");
        this.eye = modelRoot.getChild("eye");
        this.leftLeg = modelRoot.getChild("leftLeg");
        this.rightLeg = modelRoot.getChild("rightLeg");
    }

    public void setAngles(AbstractClientPlayerEntity player, PlayerEntityModel baseModel, float tickDelta) {
        limbSwing = player.limbAnimator.getPos(tickDelta);
        limbSwingAmount = player.limbAnimator.getSpeed(tickDelta);

        float legSwing = (float) Math.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        leftLeg.pitch = legSwing;
        rightLeg.pitch = -legSwing;

        eye.pitch = baseModel.head.pitch;
        eye.yaw = baseModel.head.yaw;
        eye.roll = baseModel.head.roll;
    }

    public void renderBody(MatrixStack matrices, VertexConsumer vertices, int light) {
        body.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);
    }

    public void renderEye(MatrixStack matrices, VertexConsumer vertices, int light) {
        eye.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);
    }

    public void renderLegs(MatrixStack matrices, VertexConsumer vertices, int light) {
        leftLeg.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);
        rightLeg.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);
    }
}
