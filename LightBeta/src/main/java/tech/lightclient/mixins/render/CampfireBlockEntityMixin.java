package tech.lightclient.mixins.render;


import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlockEntity.class)
public class CampfireBlockEntityMixin {

    @Inject(method = "clientTick", at = @At("HEAD"), cancellable = true)
    private static void preventMultiThreadRandom(World world, net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state, CampfireBlockEntity blockEntity, CallbackInfo ci) {
        if (!MinecraftClient.getInstance().isOnThread()) {
            ci.cancel();
        }
    }
}
