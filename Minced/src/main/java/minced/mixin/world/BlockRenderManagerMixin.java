/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.block.BlockRenderManager
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.BlockRenderView
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.world;
import minced.core.EventNoRender;
import minced.core.Event;
import minced.core.EventBus;
import minced.enums.fire;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={BlockRenderManager.class})
public class BlockRenderManagerMixin {
    @Inject(method={"renderFluid"}, at={@At(value="HEAD")}, cancellable=true)
    private void renderFluid(BlockPos param1, BlockRenderView param2, VertexConsumer param3, BlockState param4, FluidState param5, CallbackInfo param6) {
        if (param5.getFluid() == Fluids.LAVA || param5.getFluid() == Fluids.FLOWING_LAVA) {
            EventNoRender v7 = new EventNoRender(fire.lava);
            EventBus.method3015(((Object)v7));
            if (v7.method2297()) {
                param6.cancel();
            }
        }
    }
}



