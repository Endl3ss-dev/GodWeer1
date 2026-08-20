/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.hud.ChatHud
 *  net.minecraft.client.gui.hud.InGameHud
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.render.RenderTickCounter
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.Identifier
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.screen;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.function.Function;
import minced.core.ScaleState;
import minced.core.EventCrosshair;
import minced.core.EventNoRender;
import minced.core.EventHotbarRender;
import minced.core.ScaleUtil;
import minced.core.EventRender2D;
import minced.core.Event;
import minced.core.EventBus;
import minced.core.KawaseBlur;
import minced.core.ChatCommandHandler;
import minced.core.Minced;
import minced.enums.fire;
import minced.module.misc.ChatHelper;
import minced.module.visual.Crosshair;
import minced.module.visual.Interface;
import minced.module.visual.ShaderESP;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dreamix.fabricloader.VMBridge;

@Mixin(value={InGameHud.class})
public class InGameHudMixin {
    @Unique
    private static float minced$selectedSlotAnim;
    @Unique
    private static long minced$selectedSlotAnimLastMs;
    private static float field12614 = 1.0f;
    private static float field12615 = 1.0f;
    private static float field12616 = 1.0f;
    private static float field12617 = 1.0f;
    private static float field12618 = 1.0f;
    private static float field12619 = 1.0f;
    private static float field12620 = 1.0f;
    private static float field12621 = 1.0f;
    private static float field12622 = 1.0f;
    private static float field12623 = 1.0f;
    private static float field12624 = 1.0f;
    private static float field12625 = 1.0f;
    private static float field12626 = 1.0f;
    private static float field12627 = 1.0f;
    private static float field12628 = 1.0f;
    private static float field12629 = 1.0f;
    private static float field12630 = 1.0f;
    private static float field12631 = 1.0f;
    private static float field12632 = 1.0f;
    private static float field12633 = 1.0f;
    private static float field12634 = 1.0f;
    private static float field12635 = 1.0f;
    private static float field12636 = 1.0f;
    private static float field12637 = 1.0f;
    private static String field12638 = "field12638";
    private static String field12639 = "field12639";
    private static float field12640 = 1.0f;

    @Inject(method={"renderHotbar"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/util/math/MatrixStack;pop()V", shift=At.Shift.AFTER)})
    private void onRenderHotBar(DrawContext param1, RenderTickCounter param2, CallbackInfo param3) {
        EventBus.method3015(((Object)new EventHotbarRender(param1, param2)));
    }

    @Inject(method={"renderHotbar"}, at={@At(value="HEAD")})
    private void minced$customHotbarBackground(DrawContext param1, RenderTickCounter param2, CallbackInfo param3) {
    }

    @WrapOperation(method={"renderHotbar"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V")})
    private void minced$skipHotbarTexture4(DrawContext param1, Function<Identifier, ?> param2, Identifier param3, int param4, int param5, int param6, int param7, Operation<Void> param8) {
        param8.call(new Object[]{param1, param2, param3, param4, param5, param6, param7});
    }

    private static boolean minced$customHotbarEnabled() {
        if (Minced.getInstance() == null || Minced.getInstance().getModuleManager() == null) {
            return false;
        }
        Interface v0 = (Interface)Minced.getInstance().getModuleManager().method187(Interface.class);
        if (v0 == null || !v0.isEnabled()) {
            return false;
        }
        Boolean v1 = Interface.field2067.method1898(field12638);
        return Boolean.TRUE.equals(v1);
    }

    @Redirect(method={"clear"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/hud/ChatHud;clear(Z)V"))
    private void onChatClear(ChatHud param1, boolean param2) {
        ChatHelper v3 = (ChatHelper)Minced.getInstance().getModuleManager().method187(ChatHelper.class);
        if (v3.isEnabled() && ((Boolean)ChatHelper.field2242.getValue()).booleanValue()) {
            return;
        }
        param1.clear(param2);
    }

    @Inject(method={"renderScoreboardSidebar"}, at={@At(value="HEAD")}, cancellable=true)
    private void onRenderScoreboard(DrawContext param1, RenderTickCounter param2, CallbackInfo param3) {
        EventNoRender v4 = new EventNoRender(fire.scoreboard);
        EventBus.method3015(((Object)v4));
        if (v4.method2297()) {
            param3.cancel();
        }
    }

    @Inject(method={"renderCrosshair"}, at={@At(value="HEAD")}, cancellable=true)
    private void minced$customCrosshair(DrawContext param1, RenderTickCounter param2, CallbackInfo param3) {
        Crosshair v4 = (Crosshair)Minced.getInstance().getModuleManager().method187(Crosshair.class);
        if (v4 != null && v4.isEnabled()) {
            EventCrosshair v5 = new EventCrosshair(param1, param2.getTickDelta(true));
            EventBus.method3015(((Object)v5));
            if (v5.method2297()) {
                param3.cancel();
            }
        }
    }

    @Inject(method={"render"}, at={@At(value="HEAD")})
    private void onRender2D(DrawContext param1, RenderTickCounter param2, CallbackInfo param3) {
        if (ShaderESP.field1900 != null && ShaderESP.field1900.isEnabled()) {
            ShaderESP.field1900.method573();
        }
        ScaleState v4 = ScaleUtil.method2885(2.0f);
        KawaseBlur.field12561.method3585(2.0f, 4);
        EventBus.method3015(((Object)new EventRender2D(param1, param2.getTickDelta(true))));
        ScaleUtil.method2886(v4);
    }

    @Inject(method={"renderStatusEffectOverlay"}, at={@At(value="HEAD")}, cancellable=true)
    private void minced$hideVanillaPotionIcons(DrawContext param1, RenderTickCounter param2, CallbackInfo param3) {
        if (Minced.getInstance() == null || Minced.getInstance().getModuleManager() == null) {
            return;
        }
        Interface v4 = (Interface)Minced.getInstance().getModuleManager().method187(Interface.class);
        if (v4 == null || !v4.isEnabled()) {
            return;
        }
        Boolean v5 = Interface.field2067.method1898(field12639);
        if (Boolean.TRUE.equals(v5)) {
            param3.cancel();
        }
    }

    @Inject(method={"renderVignetteOverlay"}, at={@At(value="HEAD")}, cancellable=true)
    public void vignetta(DrawContext param1, Entity param2, CallbackInfo param3) {
        EventNoRender v4 = new EventNoRender(fire.vignette);
        EventBus.method3015(((Object)v4));
        if (v4.method2297()) {
            param3.cancel();
        }
    }

    @Inject(method={"renderTitleAndSubtitle"}, at={@At(value="HEAD")}, cancellable=true)
    private void renderStatusBars(DrawContext param1, RenderTickCounter param2, CallbackInfo param3) {
        EventNoRender v4 = new EventNoRender(fire.title);
        EventBus.method3015(((Object)v4));
        if (v4.method2297()) {
            param3.cancel();
        }
    }

    @WrapOperation(method={"renderMiscOverlays", "renderSleepOverlay", "renderOverlayMessage", "renderMainHud", "renderHotbar", "renderStatusBars", "renderStatusEffectOverlay", "renderScoreboardSidebar"}, at={@At(value="FIELD", target="Lnet/minecraft/client/MinecraftClient;player:Lnet/minecraft/client/network/ClientPlayerEntity;")})
    private ClientPlayerEntity mincedbots$useActiveHudPlayer(MinecraftClient param1, Operation<ClientPlayerEntity> param2) {
        ClientPlayerEntity v3 = ChatCommandHandler.method385(param1);
        return v3 != null ? v3 : (ClientPlayerEntity)param2.call(new Object[]{param1});
    }

    @WrapOperation(method={"renderMiscOverlays", "renderScoreboardSidebar", "renderMainHud", "renderStatusBars", "renderStatusEffectOverlay"}, at={@At(value="FIELD", target="Lnet/minecraft/client/MinecraftClient;world:Lnet/minecraft/client/world/ClientWorld;")})
    private ClientWorld mincedbots$useActiveHudWorld(MinecraftClient param1, Operation<ClientWorld> param2) {
        ClientWorld v3 = ChatCommandHandler.method378(param1);
        return v3 != null ? v3 : (ClientWorld)param2.call(new Object[]{param1});
    }

    static {
        VMBridge.identifyClass(InGameHudMixin.class, "XzlbY4v3");
        minced$selectedSlotAnim = field12640;
        minced$selectedSlotAnimLastMs = 0L;
    }
}



