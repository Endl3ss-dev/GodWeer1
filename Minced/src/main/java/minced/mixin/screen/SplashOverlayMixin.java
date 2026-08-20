/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.SplashOverlay
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.resource.ResourceReload
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.screen;
import java.util.Optional;
import java.util.function.Consumer;
import minced.gui.MincedLoadingOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceReload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SplashOverlay.class})
public class SplashOverlayMixin {
    @Unique
    private static MincedLoadingOverlay loadingOverlay = null;

    @Inject(method={"<init>"}, at={@At(value="HEAD")})
    private static void initCustomOverlay(MinecraftClient param0, ResourceReload param1, Consumer<Optional<Throwable>> param2, boolean param3, CallbackInfo param4) {
        loadingOverlay = new MincedLoadingOverlay(param0, param1, param2, param3);
    }

    @Inject(method={"init"}, at={@At(value="HEAD")}, cancellable=true)
    private static void cancelInit(TextureManager param0, CallbackInfo param1) {
        param1.cancel();
    }

    @Inject(method={"render"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderCustomOverlay(DrawContext param1, int param2, int param3, float param4, CallbackInfo param5) {
        param5.cancel();
        loadingOverlay.render(param1, param2, param3, param4);
    }
}



