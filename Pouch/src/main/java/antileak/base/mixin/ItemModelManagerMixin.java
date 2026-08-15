package antileak.base.mixin;

import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.world.World;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import antileak.base.client.modules.impl.player.ItemReplacer;

@Mixin(ItemModelManager.class)
public class ItemModelManagerMixin {

    @Inject(method = "update(Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V",
            at = @At("HEAD"))
    private void onUpdate(ItemRenderState state, ItemStack stack, ModelTransformationMode transformType, boolean leftHand, World world, LivingEntity entity, int seed, CallbackInfo ci) {
        ItemReplacer.captureForRender(stack, transformType);
    }

    @Inject(method = "updateForLivingEntity(Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/entity/LivingEntity;)V",
            at = @At("HEAD"))
    private void onUpdateForLivingEntity(ItemRenderState state, ItemStack stack, ModelTransformationMode transformType, boolean leftHand, LivingEntity entity, CallbackInfo ci) {
        ItemReplacer.captureForRender(stack, transformType);
    }

    @Inject(method = "updateForNonLivingEntity(Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;Lnet/minecraft/entity/Entity;)V",
            at = @At("HEAD"))
    private void onUpdateForNonLivingEntity(ItemRenderState state, ItemStack stack, ModelTransformationMode transformType, net.minecraft.entity.Entity entity, CallbackInfo ci) {
        ItemReplacer.captureForRender(stack, transformType);
    }
}
