package swerve.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import swerve.mixins.accessor.MouseHandlerAccessor;
import swerve.modules.impl.render.Graffiti;
import swerve.utils.movement.ElytraFlightRotation;
import swerve.utils.input.KeyBoardUtils;
import swerve.utils.rotation.FreeLook;
import swerve.utils.render.spatial.SpatialMouse;
import swerve.utils.render.spatial.SpatialState;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.screens.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MouseHandler.class, priority = 1100)
public class MixinMouseHandler {

    // [+] SpatialGui: накопленные дельты мыши нужны режиму прицела (turnPlayer отменяется).
    @Shadow
    private double accumulatedDX;

    @Shadow
    private double accumulatedDY;

    @Unique
    private double swerve$preReleaseX;

    @Unique
    private double swerve$preReleaseY;

    // Мышиные бинды модулей. [-] Перехват клика/колеса для ViewModel удалён: он шёл через
    // заглушки, всегда возвращавшие false, а ввод меню получает от Screen напрямую.
    // Отмена здесь обрезала бы островок и панели.
    @Inject(method = "onPress", at = @At("HEAD"))
    private void swerve$onMousePress(long window, int button, int action, int modifiers, CallbackInfo ci) {
        if (window != Minecraft.getInstance().getWindow().getWindow()) {
            return;
        }
        KeyBoardUtils.callMouse(button, action);
        // [+] Graffiti: нажатие ПКМ запоминается до тика — быстрый клик успевает
        // отпуститься между тиками, и опрос isDown его уже не увидит.
        Graffiti.onMousePress(button, action);
        // [+] SpatialGui: взмах рукой по клику на панели от первого лица (по константе модуля).
        SpatialMouse.onPress(action);
    }

    // [+] SpatialGui: клики/скролл/наведение получают координаты курсора на панели, а не в окне.
    @ModifyExpressionValue(method = {"onPress", "onScroll", "handleAccumulatedMovement"},
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHandler;xpos:D"))
    private double swerve$spatialX(double original) {
        return SpatialMouse.overrideActive() ? SpatialMouse.toRaw(true, SpatialMouse.lastPos(true, original)) : original;
    }

    @ModifyExpressionValue(method = {"onPress", "onScroll", "handleAccumulatedMovement"},
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHandler;ypos:D"))
    private double swerve$spatialY(double original) {
        return SpatialMouse.overrideActive() ? SpatialMouse.toRaw(false, SpatialMouse.lastPos(false, original)) : original;
    }

    // [+] SpatialGui: физическое движение мыши запоминается, ваниле отдаётся позиция на панели.
    @WrapMethod(method = "onMove")
    private void swerve$spatialMove(long handle, double x, double y, Operation<Void> original) {
        Minecraft mc = Minecraft.getInstance();
        if (handle != mc.getWindow().getWindow()) {
            return;
        }
        MouseHandlerAccessor accessor = (MouseHandlerAccessor) this;
        if (x == -50.0 && y == -50.0 && !accessor.swerve$isMouseGrabbed()) {
            original.call(handle, x, y);
            return;
        }
        SpatialMouse.remapMove(accessor, x, y);
        original.call(handle, SpatialMouse.moveX, SpatialMouse.moveY);
    }

    // [+] SpatialGui: в режиме прицела мышь крутит камеру панели, а не игрока.
    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void swerve$spatialTurn(double time, CallbackInfo ci) {
        if (!SpatialState.isCrosshairMode()) {
            return;
        }
        if (!SpatialMouse.overrideActive()) {
            SpatialMouse.addFreeLookDelta(this.accumulatedDX, this.accumulatedDY);
        }
        this.accumulatedDX = 0.0;
        this.accumulatedDY = 0.0;
        ci.cancel();
    }

    // Adapted from AlphaDLC/src/main/java/zov/alphadlc/mixin/MouseMixin.java
    // Vanilla has already applied sensitivity, smoothing, spyglass scaling and invert-Y here.
    // [+] Молчаливая ротация ауры перехватывает ту же точку: тело крутит аура,
    //     дельты мыши уходят в свободный обзор (FreeLookComponent из Private).
    @Redirect(method = "turnPlayer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
    private void swerve$elytraFreeLook(LocalPlayer player, double yaw, double pitch) {
        if (FreeLook.active()) {
            FreeLook.turn(yaw, pitch);
        } else if (ElytraFlightRotation.isActive()) {
            ElytraFlightRotation.turn(yaw, pitch);
        } else {
            player.turn(yaw, pitch);
        }
    }

    // [+] SpatialGui: releaseMouse ставит курсор в центр окна — для панели возвращаем прежнюю позицию.
    @Inject(method = "releaseMouse", at = @At("HEAD"))
    private void swerve$spatialPreRelease(CallbackInfo ci) {
        MouseHandlerAccessor accessor = (MouseHandlerAccessor) this;
        swerve$preReleaseX = accessor.swerve$getXpos();
        swerve$preReleaseY = accessor.swerve$getYpos();
    }

    @Inject(method = "releaseMouse", at = @At("TAIL"))
    private void swerve$spatialPostRelease(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        Screen screen = mc.screen;
        if (SpatialState.isCrosshairMode() || screen == null || !SpatialState.hookable(screen)) {
            return;
        }
        if (swerve$preReleaseX < 0 || swerve$preReleaseY < 0
                || swerve$preReleaseX > mc.getWindow().getScreenWidth()
                || swerve$preReleaseY > mc.getWindow().getScreenHeight()) {
            return;
        }
        MouseHandlerAccessor accessor = (MouseHandlerAccessor) this;
        accessor.swerve$setXpos(swerve$preReleaseX);
        accessor.swerve$setYpos(swerve$preReleaseY);
    }
}
