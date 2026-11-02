/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1657
 *  net.minecraft.class_266
 *  net.minecraft.class_269
 *  net.minecraft.class_310
 *  net.minecraft.class_329
 *  net.minecraft.class_332
 *  net.minecraft.class_355
 *  net.minecraft.class_437
 *  net.minecraft.class_8646
 *  net.minecraft.class_9779
 *  org.joml.Matrix3x2f
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1657;
import net.minecraft.class_266;
import net.minecraft.class_269;
import net.minecraft.class_310;
import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_355;
import net.minecraft.class_437;
import net.minecraft.class_8646;
import net.minecraft.class_9779;
import org.joml.Matrix3x2f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.Client;
import ru.decide.manager.DragComponent;
import ru.decide.manager.event_impl.EventDisplay;
import ru.decide.module.impl.render.BetterMinecraft;
import ru.decide.module.impl.render.CrossHair;
import ru.decide.module.impl.render.NoRender;
import ru.decide.module.impl.render.emotions.EmotionWheelManager;
import ru.decide.utils.render.Render2D;
import ru.decide.utils.render.ScreenBlur;
import ru.decide.utils.render.TabAnimationAccess;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_329.class})
public abstract class InGameHudMixin {
    @Shadow
    @Final
    private class_355 field_2015;

    @Inject(method={"method_1753"}, at={@At(value="TAIL")})
    public void onRender(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null) {
            return;
        }
        ScreenBlur.frame();
        class_437 screen = mc.field_1755;
        if (this.isLoadingScreen(screen)) {
            return;
        }
        context.method_71048();
        Render2D.beginOverlay();
        context.method_51448().pushMatrix();
        EventDisplay event = new EventDisplay(context, tickCounter.method_60637(false));
        Client.get().componentManager().get(DragComponent.class).post((Matrix3x2f)context.method_51448());
        event.hook();
        Client.get().render2D().flushAll();
        context.method_51448().popMatrix();
        Render2D.endOverlay();
    }

    @Inject(method={"method_1765"}, at={@At(value="HEAD")}, cancellable=true)
    private void onRenderStatusEffectOverlay(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method={"method_1736"}, at={@At(value="HEAD")}, cancellable=true)
    private void removeVanillaCrosshair(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
        try {
            class_310 mc = class_310.method_1551();
            if (mc.field_1755 != null || EmotionWheelManager.getInstance().isWheelOpen()) {
                ci.cancel();
                return;
            }
            CrossHair crosshairModule = CrossHair.getInstance();
            if (crosshairModule.isEnabled()) {
                ci.cancel();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Unique
    private boolean isLoadingScreen(class_437 screen) {
        if (screen == null) {
            return false;
        }
        String className = screen.getClass().getSimpleName().toLowerCase();
        String fullName = screen.getClass().getName().toLowerCase();
        if (className.contains("loading")) {
            return true;
        }
        if (className.contains("progress")) {
            return true;
        }
        if (className.contains("connecting")) {
            return true;
        }
        if (className.contains("downloading")) {
            return true;
        }
        if (className.contains("terrain")) {
            return true;
        }
        if (className.contains("generating")) {
            return true;
        }
        if (className.contains("saving")) {
            return true;
        }
        if (className.contains("reload")) {
            return true;
        }
        if (className.contains("resource")) {
            return true;
        }
        if (className.contains("pack")) {
            return true;
        }
        return fullName.contains("mojang");
    }

    @Inject(method={"method_61980"}, at={@At(value="HEAD")}, cancellable=true)
    private void onRenderNauseaOverlay(class_332 context, float nauseaStrength, CallbackInfo ci) {
        NoRender noRender = NoRender.getInstance();
        if (noRender != null && noRender.isEnabled() && noRender.ignoreZalupa.getValue().booleanValue()) {
            ci.cancel();
        }
    }

    @Inject(method={"method_55803"}, at={@At(value="HEAD")}, cancellable=true)
    private void onRenderScoreboard(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
        NoRender noRender = NoRender.getInstance();
        if (noRender != null && noRender.isEnabled() && noRender.ignoreScoreboard.getValue().booleanValue()) {
            ci.cancel();
        }
    }

    @Inject(method={"method_70837"}, at={@At(value="HEAD")}, cancellable=true)
    private void onRenderBossBar(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
        NoRender noRender = NoRender.getInstance();
        if (noRender != null && noRender.isEnabled() && noRender.ignoreBossBar.getValue().booleanValue()) {
            ci.cancel();
        }
    }

    @ModifyArg(method={"method_1759"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_332;method_52706(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/class_2960;IIII)V", ordinal=1), index=2, require=1)
    private int decide$animateHotbarSelection(int targetX) {
        return BetterMinecraft.animateHotbarSelectionX(targetX);
    }

    @Inject(method={"method_58477"}, at={@At(value="RETURN")})
    private void decide$renderSaturation(class_332 context, class_1657 player, int top, int right, CallbackInfo ci) {
        BetterMinecraft.renderSaturation(context, player, top, right);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @WrapMethod(method={"method_55805"})
    private void decide$liftHotbarWithChat(class_332 context, class_9779 tickCounter, Operation<Void> original) {
        float offset = BetterMinecraft.chatHotbarLiftOffset();
        if (offset <= 0.01f) {
            original.call(new Object[]{context, tickCounter});
            return;
        }
        context.method_51448().pushMatrix();
        context.method_51448().translate(0.0f, -offset);
        try {
            original.call(new Object[]{context, tickCounter});
        }
        finally {
            context.method_51448().popMatrix();
        }
    }

    @Inject(method={"method_55804"}, at={@At(value="TAIL")})
    private void decide$renderClosingTabList(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
        class_355 class_3552 = this.field_2015;
        if (!(class_3552 instanceof TabAnimationAccess)) {
            return;
        }
        TabAnimationAccess tab = (TabAnimationAccess)class_3552;
        if (!tab.decide$shouldRenderClosingTab()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || mc.field_1690.field_1907.method_1434()) {
            return;
        }
        class_269 scoreboard = mc.field_1687.method_8428();
        class_266 objective = scoreboard.method_1189(class_8646.field_45156);
        this.field_2015.method_1919(context, context.method_51421(), scoreboard, objective);
    }
}

