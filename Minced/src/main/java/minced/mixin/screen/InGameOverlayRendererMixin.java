/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.hud.InGameOverlayRenderer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.util.math.MatrixStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.screen;
import minced.core.EventNoRender;
import minced.core.Event;
import minced.core.EventBus;
import minced.enums.fire;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={InGameOverlayRenderer.class})
public class InGameOverlayRendererMixin {
    @Inject(method={"renderFireOverlay"}, at={@At(value="HEAD")}, cancellable=true)
    private static void renderFireOverlayHook(MatrixStack param0, VertexConsumerProvider param1, CallbackInfo param2) {
        EventNoRender v3 = new EventNoRender(fire.fire);
        EventBus.method3015(((Object)v3));
        if (v3.method2297()) {
            param2.cancel();
        }
    }

    @Inject(method={"renderUnderwaterOverlay"}, at={@At(value="HEAD")}, cancellable=true)
    private static void renderUnderwaterOverlayHook(MinecraftClient param0, MatrixStack param1, VertexConsumerProvider param2, CallbackInfo param3) {
        EventNoRender v4 = new EventNoRender(fire.underwater_blur);
        EventBus.method3015(((Object)v4));
        if (v4.method2297()) {
            param3.cancel();
        }
    }
}



