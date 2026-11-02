package tech.lightclient.mixins.render;


import tech.lightclient.modules.impl.render.ItemReplacer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemModelManager.class)
public abstract class ItemReplacerItemModelManagerMixin {

    @Redirect(
            method = "clearAndUpdate(Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/world/World;Lnet/minecraft/util/HeldItemContext;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/item/ItemModelManager;update(Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/world/World;Lnet/minecraft/util/HeldItemContext;I)V"
            )
    )
    private void light$replaceSwordModel(ItemModelManager instance, ItemRenderState state, ItemStack stack,
                                         ItemDisplayContext displayContext, World world,
                                         HeldItemContext heldItemContext, int seed) {
        LivingEntity entity = heldItemContext == null ? null : heldItemContext.getEntity();
        instance.update(state, ItemReplacer.getRenderStack(stack, entity), displayContext, world, heldItemContext, seed);
    }
}
