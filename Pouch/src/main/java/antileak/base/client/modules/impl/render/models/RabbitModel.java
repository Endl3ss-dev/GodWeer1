package antileak.base.client.modules.impl.render.models;

import net.minecraft.client.model.*;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class RabbitModel {
    private ModelPart root;
    private ModelPart rabbitBone;
    private ModelPart rabbitHead;
    private ModelPart rabbitLarm;
    private ModelPart rabbitRarm;
    private ModelPart rabbitLleg;
    private ModelPart rabbitRleg;

    public RabbitModel() {
        ModelData modelData = new ModelData();
        ModelPartData rootData = modelData.getRoot();

        ModelPartData boneData = rootData.addChild("rabbitBone",
                ModelPartBuilder.create()
                        .uv(28, 45).cuboid(-5.0F, -13.0F, -5.0F, 10, 11, 8),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F)
        );

        boneData.addChild("rabbitHead",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-3.0F, 0.0F, -4.0F, 6, 1, 6)
                        .uv(56, 0).cuboid(-5.0F, -9.0F, -5.0F, 2, 3, 2)
                        .uv(56, 0).cuboid(3.0F, -9.0F, -5.0F, 2, 3, 2)
                        .uv(0, 45).cuboid(-4.0F, -11.0F, -4.0F, 8, 11, 8)
                        .uv(46, 0).cuboid(1.0F, -20.0F, 0.0F, 3, 9, 1)
                        .uv(46, 0).cuboid(-4.0F, -20.0F, 0.0F, 3, 9, 1),
                ModelTransform.pivot(0.0F, -14.0F, -1.0F)
        );

        boneData.addChild("rabbitLarm",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(0.0F, 0.0F, -2.0F, 2, 8, 4),
                ModelTransform.pivot(5.0F, -13.0F, -1.0F)
        );

        boneData.addChild("rabbitRarm",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-2.0F, 0.0F, -2.0F, 2, 8, 4),
                ModelTransform.pivot(-5.0F, -13.0F, -1.0F)
        );

        boneData.addChild("rabbitLleg",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-2.0F, 0.0F, -2.0F, 4, 2, 4),
                ModelTransform.pivot(3.0F, -2.0F, -1.0F)
        );

        boneData.addChild("rabbitRleg",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-2.0F, 0.0F, -2.0F, 4, 2, 4),
                ModelTransform.pivot(-3.0F, -2.0F, -1.0F)
        );

        ModelPart modelRoot = TexturedModelData.of(modelData, 64, 64).createModel();

        this.root = modelRoot;
        this.rabbitBone = modelRoot.getChild("rabbitBone");
        this.rabbitHead = rabbitBone.getChild("rabbitHead");
        this.rabbitLarm = rabbitBone.getChild("rabbitLarm");
        this.rabbitRarm = rabbitBone.getChild("rabbitRarm");
        this.rabbitLleg = rabbitBone.getChild("rabbitLleg");
        this.rabbitRleg = rabbitBone.getChild("rabbitRleg");
    }

    public void setAngles(AbstractClientPlayerEntity player, PlayerEntityModel baseModel, float tickDelta) {
        rabbitHead.pitch = baseModel.head.pitch;
        rabbitHead.yaw = baseModel.head.yaw;
        rabbitHead.roll = baseModel.head.roll;

        rabbitLarm.pitch = baseModel.leftArm.pitch;
        rabbitLarm.yaw = baseModel.leftArm.yaw;
        rabbitLarm.roll = baseModel.leftArm.roll;

        rabbitRarm.pitch = baseModel.rightArm.pitch;
        rabbitRarm.yaw = baseModel.rightArm.yaw;
        rabbitRarm.roll = baseModel.rightArm.roll;

        rabbitLleg.pitch = baseModel.leftLeg.pitch;
        rabbitLleg.yaw = baseModel.leftLeg.yaw;
        rabbitLleg.roll = baseModel.leftLeg.roll;

        rabbitRleg.pitch = baseModel.rightLeg.pitch;
        rabbitRleg.yaw = baseModel.rightLeg.yaw;
        rabbitRleg.roll = baseModel.rightLeg.roll;
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light) {
        root.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);
    }
}
