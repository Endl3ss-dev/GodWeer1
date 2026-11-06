/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2535
 *  net.minecraft.class_2547
 *  net.minecraft.class_2596
 *  net.minecraft.class_2815
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2535;
import net.minecraft.class_2547;
import net.minecraft.class_2596;
import net.minecraft.class_2815;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.manager.event_impl.EventPacket;
import ru.decide.manager.event_impl.ScreenCloseEvent;
import ru.decide.module.impl.utils.consumable.ConsumableHandler;
import ru.decide.utils.annotation.IMinecraft;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_2535.class})
public class ClientConnectionMixin {
    @Unique
    private static boolean stackOverflowFix;

    @Inject(method={"method_10759"}, at={@At(value="HEAD")}, cancellable=true)
    private static <T extends class_2547> void triggerReceivePacketEvent(class_2596<T> packet, class_2547 listener, CallbackInfo ci) {
        EventPacket event = new EventPacket(packet, EventPacket.Type.RECEIVE);
        event.hook();
        if (event.isCancelled()) {
            ci.cancel();
        }
        if (ConsumableHandler.isEnabled()) {
            ConsumableHandler.handlePacket(packet, ci);
        }
    }

    @Inject(method={"method_10743"}, at={@At(value="HEAD")}, cancellable=true)
    public void triggerSendPacketEvent(class_2596<?> packet, CallbackInfo ci) {
        class_2596<?> newPacket;
        if (stackOverflowFix) {
            return;
        }
        EventPacket event = new EventPacket(packet, EventPacket.Type.SEND);
        event.hook();
        if (event.isCancelled()) {
            ci.cancel();
        }
        if ((newPacket = event.getPacket()) != packet) {
            ci.cancel();
            stackOverflowFix = true;
            IMinecraft.mc.method_1562().method_52787(newPacket);
            stackOverflowFix = false;
        }
        if (packet instanceof class_2815) {
            class_2815 closePacket = (class_2815)packet;
            class_310 mc = class_310.method_1551();
            ScreenCloseEvent screenCloseEvent = new ScreenCloseEvent(mc.field_1755, closePacket.method_36168());
            screenCloseEvent.hook();
            if (screenCloseEvent.isCancelled()) {
                ci.cancel();
            }
        }
    }
}

