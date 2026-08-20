/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.AbstractBlock$AbstractBlockState
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.BlockView
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package minced.mixin.world;
import minced.core.EventNoRender;
import minced.core.Event;
import minced.core.EventBus;
import minced.enums.fire;
import net.minecraft.block.AbstractBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={AbstractBlock.AbstractBlockState.class})
public class AbstractBlockAbstractBlockStateMixin {
    @Inject(method={"shouldBlockVision"}, at={@At(value="HEAD")}, cancellable=true)
    public void shouldBlockVision(BlockView param1, BlockPos param2, CallbackInfoReturnable<Boolean> param3) {
        EventNoRender v4 = new EventNoRender(fire.cameraclip);
        EventBus.method3015(((Object)v4));
        if (v4.method2297()) {
            param3.setReturnValue((Boolean)false);
        }
    }
}



