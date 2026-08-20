/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer
 *  net.minecraft.client.render.entity.state.PlayerEntityRenderState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package minced.mixin.render;
import minced.core.EventNoRender;
import minced.core.Event;
import minced.core.EventBus;
import minced.enums.fire;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={StuckArrowsFeatureRenderer.class})
public class StuckArrowsFeatureRendererMixin {
    @Inject(method={"getObjectCount"}, at={@At(value="HEAD")}, cancellable=true)
    protected void getObjectCount(PlayerEntityRenderState param1, CallbackInfoReturnable<Integer> param2) {
        EventNoRender v3 = new EventNoRender(fire.arrows);
        EventBus.method3015(((Object)v3));
        if (v3.method2297()) {
            param2.setReturnValue((Integer)0);
        }
    }
}



