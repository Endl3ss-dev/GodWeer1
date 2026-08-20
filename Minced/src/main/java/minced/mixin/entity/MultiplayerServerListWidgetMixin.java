/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget$Entry
 *  net.minecraft.client.network.ServerInfo
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.util.Identifier
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package minced.mixin.entity;
import java.util.function.Function;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dreamix.fabricloader.VMBridge;

@Mixin(targets={"net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget$ServerEntry"})
public class MultiplayerServerListWidgetMixin {
    @Final
    @Shadow
    private ServerInfo field_19120;
    @Final
    @Shadow
    private MultiplayerScreen field_19118;
    @Final
    @Shadow
    MultiplayerServerListWidget field_19117;
    @Unique
    private static final String PINNED_ADDRESS_2 = "pulsesmp.fun";
    @Unique
    private static final int BG_COLOR_2 = 1434976511;
    private static String field12641 = "field12641";
    private static String field12642 = "field12642";
    private static int field12643 = 1;
    private static String field12644 = "field12644";
    private static double field12645 = 1.0;

    @Unique
    private boolean isPinned() {
        return field12641.equals(this.field_19120.address);
    }

    @Inject(method={"render"}, at={@At(value="HEAD")})
    private void minced$drawPinnedBackground(DrawContext param1, int param2, int param3, int param4, int param5, int param6, int param7, int param8, boolean param9, float param10, CallbackInfo param11) {
        if (field12642.equals(this.field_19120.address)) {
            param1.fill(param4, param3, param4 + param5 - 3, param3 + param6, field12643);
        }
    }

    @Redirect(method={"render"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V"))
    private void minced$skipMoveButtonTextures(DrawContext param1, Function<Identifier, RenderLayer> param2, Identifier param3, int param4, int param5, int param6, int param7) {
        if (this.isPinned() && param3.getPath().startsWith(field12644)) {
            return;
        }
        param1.drawGuiTexture(param2, param3, param4, param5, param6, param7);
    }

    @Inject(method={"mouseClicked"}, at={@At(value="HEAD")}, cancellable=true)
    private void minced$overrideMoveAreaClick(double param1, double param3, int param5, CallbackInfoReturnable<Boolean> param6) {
        if (!this.isPinned()) {
            return;
        }
        double v7 = param1 - (double)this.field_19117.getRowLeft();
        if (v7 >= 0.0 && v7 < field12645) {
            this.field_19118.select((MultiplayerServerListWidget.Entry)(Object)this);
            param6.setReturnValue((Boolean)true);
        }
    }

    static {
        VMBridge.identifyClass(MultiplayerServerListWidgetMixin.class, "FqnB852G");
    }
}



