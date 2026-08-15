package antileak.base.client.modules.impl.render.models;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CustomModelRenderer {
    private final RabbitModel rabbitModel;

    public static final Identifier RABBIT_TEXTURE = Identifier.of("punch", "textures/models/rabbit.png");

    public CustomModelRenderer() {
        this.rabbitModel = new RabbitModel();
    }

    public void render(AbstractClientPlayerEntity player, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light,
                       PlayerEntityModel baseModel) {
        matrices.push();

        if (player.isBaby()) {
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.translate(0.0f, 1.5f, 0.0f);
        }

        rabbitModel.setAngles(player, baseModel, tickDelta);

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(RABBIT_TEXTURE));
        rabbitModel.render(matrices, vertexConsumer, light);

        matrices.pop();
    }
}
