/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_243
 *  net.minecraft.class_4184
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
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_4184;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.manager.event_impl.CameraPositionEvent;
import ru.decide.manager.event_impl.EventRotation;
import ru.decide.module.impl.render.NoRender;
import ru.decide.module.impl.render.WastedDeath;
import ru.decide.utils.render.wasted.WastedState;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_4184.class})
public abstract class CameraMixin {
    @Unique
    private EventRotation night$rotationEvent;
    @Unique
    private float night$originalYaw;
    @Unique
    private float night$originalPitch;
    @Unique
    private boolean night$inverseView;
    @Shadow
    private class_243 field_18712;
    @Shadow
    @Final
    private class_2338.class_2339 field_18713;

    @Inject(method={"method_19321"}, at={@At(value="HEAD")})
    private void onUpdateHead(class_1937 area, class_1297 focusedEntity, boolean thirdPerson, boolean inverseView, float tickProgress, CallbackInfo ci) {
        boolean bl = this.night$inverseView = thirdPerson && inverseView;
        if (focusedEntity != null) {
            this.night$originalYaw = focusedEntity.method_5705(tickProgress);
            this.night$originalPitch = focusedEntity.method_5695(tickProgress);
            this.night$rotationEvent = new EventRotation(this.night$originalYaw, this.night$originalPitch, tickProgress);
            this.night$rotationEvent.hook();
        } else {
            this.night$rotationEvent = null;
        }
    }

    @Inject(method={"method_19318"}, at={@At(value="HEAD")}, cancellable=true)
    private void onClipToSpace(float desiredCameraDistance, CallbackInfoReturnable<Float> cir) {
        WastedDeath wasted = WastedDeath.getInstance();
        if (wasted != null && WastedDeath.isRunning() && wasted.orbitEnabled()) {
            cir.setReturnValue((Object)Float.valueOf(wasted.orbitDistanceNow()));
            return;
        }
        NoRender noRender = NoRender.getInstance();
        if (noRender != null && noRender.isEnabled() && noRender.noCameraClip.getValue().booleanValue()) {
            cir.setReturnValue((Object)Float.valueOf(desiredCameraDistance));
        }
    }

    @Shadow
    public abstract void method_19325(float var1, float var2);

    @Inject(method={"method_19321"}, at={@At(value="TAIL")})
    private void onWastedOrbit(class_1937 area, class_1297 focusedEntity, boolean thirdPerson, boolean inverseView, float tickProgress, CallbackInfo ci) {
        WastedDeath wasted = WastedDeath.getInstance();
        if (wasted == null || !WastedDeath.isRunning() || !wasted.orbitEnabled()) {
            return;
        }
        this.method_19325(wasted.orbitYaw(), wasted.orbitPitch());
        if (!WastedState.isDetached()) {
            return;
        }
        class_243 position = wasted.orbitPosition();
        this.method_19327(position.field_1352, position.field_1351, position.field_1350);
    }

    @Shadow
    public abstract void method_19327(double var1, double var3, double var5);

    @Redirect(method={"method_19321"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_4184;method_19325(FF)V"))
    private void redirectSetRotation(class_4184 instance, float yaw, float pitch) {
        if (this.night$rotationEvent != null && (this.night$rotationEvent.getYaw() != this.night$originalYaw || this.night$rotationEvent.getPitch() != this.night$originalPitch)) {
            if (this.night$inverseView) {
                this.method_19325(this.night$rotationEvent.getYaw() + 180.0f, -this.night$rotationEvent.getPitch());
            } else {
                this.method_19325(this.night$rotationEvent.getYaw(), this.night$rotationEvent.getPitch());
            }
        } else {
            this.method_19325(yaw, pitch);
        }
    }

    @Inject(method={"method_19322"}, at={@At(value="HEAD")}, cancellable=true)
    private void posHook(class_243 pos, CallbackInfo ci) {
        CameraPositionEvent event = new CameraPositionEvent(pos);
        event.hook();
        this.field_18712 = pos = event.getPos();
        this.field_18713.method_10102(pos.field_1352, pos.field_1351, pos.field_1350);
        ci.cancel();
    }
}

