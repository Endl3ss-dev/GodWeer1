/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11908
 *  net.minecraft.class_309
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11908;
import net.minecraft.class_309;
import net.minecraft.class_310;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.manager.event_impl.EventKey;
import ru.decide.manager.event_impl.EventKeyRelease;
import ru.decide.manager.events.Event;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_309.class})
public class KeyboardMixin {
    @Shadow
    @Final
    private class_310 field_1678;

    @Inject(method={"method_1466"}, at={@At(value="HEAD")})
    private void onKey(long window, int action, class_11908 input, CallbackInfo ci) {
        Event event;
        class_437 screen = this.field_1678.field_1755;
        int key = input.comp_4795();
        if (key == -1 || key == 0) {
            return;
        }
        if (key >= 0 && key <= 7) {
            return;
        }
        if (action == 1 && screen == null) {
            event = new EventKey(key);
            event.hook();
        }
        if (action == 0 && screen == null) {
            event = new EventKeyRelease(key);
            event.hook();
        }
    }
}

