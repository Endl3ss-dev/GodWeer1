/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_634
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_634;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.utils.AutoFixCommand;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_634.class})
public class ChatCommandFixMixin {
    @Inject(method={"method_45730"}, at={@At(value="HEAD")}, cancellable=true)
    private void decide$fixChatCommand(String command, CallbackInfo ci) {
        String fixed = AutoFixCommand.processServerCommand(command);
        if (!fixed.equals(command)) {
            ci.cancel();
            ((class_634)this).method_45730(fixed);
        }
    }
}

