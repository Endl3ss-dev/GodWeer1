/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1041
 *  net.minecraft.class_11910
 *  net.minecraft.class_310
 *  net.minecraft.class_312
 *  net.minecraft.class_408
 *  net.minecraft.class_437
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1041;
import net.minecraft.class_11910;
import net.minecraft.class_310;
import net.minecraft.class_312;
import net.minecraft.class_408;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ru.decide.manager.event_impl.EventKey;
import ru.decide.manager.event_impl.EventLook;
import ru.decide.manager.event_impl.MousePressEvent;
import ru.decide.manager.event_impl.MouseReleaseEvent;
import ru.decide.module.impl.utils.EffectSounds;
import ru.decide.screen.editor.OverlayEditor;
import ru.decide.screen.editor.OverlayEditors;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_312.class})
public class MouseMixin {
    @Shadow
    @Final
    private class_310 field_1779;

    @Inject(method={"method_1601"}, at={@At(value="HEAD")})
    private void onMouseButton(long window, class_11910 input, int action, CallbackInfo ci) {
        OverlayEditor editor = OverlayEditors.active();
        if (editor != null && (action == 1 || action == 0)) {
            editor.rawMouseButton(this.editorMouseX(this.field_1779.field_1729.method_1603()), this.editorMouseY(this.field_1779.field_1729.method_1604()), input.comp_4801(), action == 1);
        }
        if (action == 1 && this.field_1779.field_1755 == null || action == 1 && this.field_1779.field_1755 instanceof class_408) {
            EventKey event = new EventKey(input.method_74228());
            event.hook();
        }
        if (action == 1 && this.field_1779.field_1755 == null || action == 1 && this.field_1779.field_1755 instanceof class_408) {
            double mouseX = this.field_1779.field_1729.method_1603();
            double mouseY = this.field_1779.field_1729.method_1604();
            double d0 = mouseX * (double)this.field_1779.method_22683().method_4486() / (double)this.field_1779.method_22683().method_4480();
            double d1 = mouseY * (double)this.field_1779.method_22683().method_4502() / (double)this.field_1779.method_22683().method_4507();
            class_437 screen = this.field_1779.field_1755;
            MousePressEvent event = new MousePressEvent(input.comp_4801(), action, input.comp_4797(), screen, d0, d1);
            event.hook();
        }
        if (action != 0) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1755 == null) {
            return;
        }
        double x = mc.field_1729.method_1603() * (double)mc.method_22683().method_4486() / (double)mc.method_22683().method_4480();
        double y = mc.field_1729.method_1604() * (double)mc.method_22683().method_4502() / (double)mc.method_22683().method_4507();
        new MouseReleaseEvent(x, y, input.comp_4801(), mc.field_1755).hook();
    }

    @Inject(method={"method_1600"}, at={@At(value="TAIL")})
    private void onEditorCursorPos(long window, double x, double y, CallbackInfo ci) {
        EffectSounds.noteUserActivity();
        OverlayEditor editor = OverlayEditors.active();
        if (editor != null) {
            editor.rawMouseMoved(this.editorMouseX(x), this.editorMouseY(y));
        }
    }

    @Unique
    private float editorMouseX(double rawX) {
        float scaleFix = 2.0f / (float)this.field_1779.method_22683().method_4495();
        return (float)(class_312.method_68880((class_1041)this.field_1779.method_22683(), (double)rawX) / (double)scaleFix);
    }

    @Unique
    private float editorMouseY(double rawY) {
        float scaleFix = 2.0f / (float)this.field_1779.method_22683().method_4495();
        return (float)(class_312.method_68884((class_1041)this.field_1779.method_22683(), (double)rawY) / (double)scaleFix);
    }

    @Inject(method={"method_1606"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_746;method_5872(DD)V")}, locals=LocalCapture.CAPTURE_FAILHARD, cancellable=true)
    private void onLook(double timeDelta, CallbackInfo ci, double i, double j) {
        class_310 client = class_310.method_1551();
        if (client != null && client.field_1724 != null) {
            EventLook event = new EventLook(i, j);
            event.hook();
            if (!event.isCancelled()) {
                client.field_1724.method_5872(event.yaw, event.pitch);
            }
            ci.cancel();
        }
    }
}

