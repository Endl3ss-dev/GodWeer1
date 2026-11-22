/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2338
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_638$class_5271
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
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.manager.event_impl.WorldLoadEvent;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_638.class})
public class ClientWorldMixin {
    @Shadow
    @Final
    private class_638.class_5271 field_24430;

    @Inject(method={"<init>"}, at={@At(value="RETURN")})
    public void initHook(CallbackInfo info) {
        new WorldLoadEvent().hook();
    }

    @Inject(method={"method_16109"}, at={@At(value="HEAD")}, cancellable=true)
    private void onScheduleBlockRerenderIfNeeded(class_2338 pos, class_2680 old, class_2680 updated, CallbackInfo ci) {
        if (class_310.method_1551().field_1687 != this) {
            ci.cancel();
        }
    }

    @Inject(method={"method_8413"}, at={@At(value="HEAD")}, cancellable=true)
    private void onUpdateListeners(class_2338 pos, class_2680 oldState, class_2680 newState, int flags, CallbackInfo ci) {
        if (class_310.method_1551().field_1687 != this) {
            ci.cancel();
        }
    }
}

