package antileak.base.mixin;

import antileak.base.Punch;
import antileak.base.client.modules.impl.render.CustomModels;
import antileak.base.client.modules.impl.render.models.CustomModelRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class MixinPlayerEntityRenderer {

    @Shadow
    protected EntityModel model;

    @Unique
    private CustomModelRenderer customModelRenderer;

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void renderCustomModel(LivingEntityRenderState state, MatrixStack matrices,
                                   VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!(state instanceof PlayerEntityRenderState playerState)) return;
        if (!(model instanceof PlayerEntityModel playerModel)) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;

        Entity entity = mc.world.getEntityById(playerState.id);
        if (!(entity instanceof AbstractClientPlayerEntity player)) return;

        CustomModels mod = CustomModels.INSTANCE;
        if (mod == null || !mod.isEnable()) return;

        boolean isSelf = player == mc.player;
        boolean isFriend = Punch.INSTANCE.friendStorage != null && Punch.INSTANCE.friendStorage.isFriend(player.getName().getString());
        if (mod.onlySelf.isState() && !isSelf && !(mod.friends.isState() && isFriend)) return;

        if (customModelRenderer == null) {
            customModelRenderer = new CustomModelRenderer();
        }

        playerModel.setAngles(playerState);

        float tickDelta = mc.getRenderTickCounter().getTickDelta(true);
        matrices.push();
        matrices.translate(0.0, player.getHeight() / 2.0, 0.0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - playerState.bodyYaw));
        matrices.multiply(new Quaternionf().rotationX((float) Math.PI));
        matrices.multiply(new Quaternionf().rotationY((float) Math.PI));
        matrices.translate(0.0, -player.getHeight() / 2.0, 0.0);
        matrices.translate(0.0, -0.05, 0.0);
        matrices.scale(1.25f, 1.25f, 1.25f);
        customModelRenderer.render(player, 0, tickDelta, matrices, vertexConsumers, light, playerModel);
        matrices.pop();
        ci.cancel();
    }
}
