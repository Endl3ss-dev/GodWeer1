/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gl.Framebuffer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.render;
import minced.core.FramebufferBlit;
import net.minecraft.client.gl.Framebuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Framebuffer.class})
public class MixinFramebuffer {
    @Inject(method={"beginWrite"}, at={@At(value="HEAD")}, cancellable=true)
    private void minced$beginWriteHead(boolean param1, CallbackInfo param2) {
        if (!FramebufferBlit.field763) {
            param2.cancel();
        }
    }
}



