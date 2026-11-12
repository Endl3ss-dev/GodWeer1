/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_2739
 *  net.minecraft.class_2767
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_634
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_2739;
import net.minecraft.class_2767;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_634;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.utils.consumable.ConsumableHandler;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_634.class})
public class ConsumableOptimizerNetworkHandlerMixin {
    @Inject(method={"method_11146"}, at={@At(value="HEAD")}, cancellable=true)
    private void onPlaySound(class_2767 packet, CallbackInfo ci) {
        if (!ConsumableHandler.isEnabled()) {
            return;
        }
        ConsumableHandler.handleServerSounds((class_3414)packet.method_11894().comp_349(), ci);
    }

    @Inject(method={"method_11093"}, at={@At(value="HEAD")}, cancellable=true)
    private void onEntityTrackerUpdate(class_2739 packet, CallbackInfo ci) {
        if (!ConsumableHandler.isEnabled()) {
            return;
        }
        if (!ConsumableHandler.STATE.isWaitingForServer()) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null || client.field_1724 == null) {
            return;
        }
        class_1297 entity = client.field_1687.method_8469(packet.comp_1127());
        if (entity == client.field_1724) {
            ci.cancel();
        }
    }
}

