package tech.lightclient.mixins.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.lightclient.util.SkullProfileNbtFixer;

@Mixin(BlockEntity.class)
public abstract class SkullProfileBlockEntityMixin {

    @Inject(method = "createFromNbt", at = @At("HEAD"))
    private static void light$normalizeOnChunkLoad(
            BlockPos pos,
            BlockState state,
            NbtCompound nbt,
            RegistryWrapper.WrapperLookup registries,
            CallbackInfoReturnable<BlockEntity> cir
    ) {
        SkullProfileNbtFixer.normalize(nbt);
    }
}
