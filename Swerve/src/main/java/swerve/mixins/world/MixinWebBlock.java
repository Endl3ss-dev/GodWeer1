package swerve.mixins.world;

import swerve.Swerve;
import swerve.managers.ModuleManager;
import swerve.modules.impl.movement.NoWeb;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WebBlock.class)
public abstract class MixinWebBlock {

    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void swerve$entityInsideHead(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        NoWeb module = ModuleManager.get(NoWeb.class);
        if (module == null || !module.isEnable()) return;
        try {
            if (module.onCobwebHead(entity, state)) {
                ci.cancel();
            }
        } catch (Exception exception) {
            Swerve.LOGGER.error("Module 'NoWeb' failed during entityInside HEAD", exception);
            module.setEnabled(false);
        }
    }

    @Inject(method = "entityInside", at = @At("RETURN"))
    private void swerve$entityInsideReturn(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        NoWeb module = ModuleManager.get(NoWeb.class);
        if (module == null || !module.isEnable()) return;
        try {
            module.onCobwebReturn(entity, state);
        } catch (Exception exception) {
            Swerve.LOGGER.error("Module 'NoWeb' failed during entityInside RETURN", exception);
            module.setEnabled(false);
        }
    }
}
