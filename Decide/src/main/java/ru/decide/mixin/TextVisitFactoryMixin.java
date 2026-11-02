/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_5223
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5223;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ru.decide.manager.event_impl.TextFactoryEvent;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_5223.class})
public class TextVisitFactoryMixin {
    @ModifyArg(at=@At(value="INVOKE", target="Lnet/minecraft/class_5223;method_27473(Ljava/lang/String;ILnet/minecraft/class_2583;Lnet/minecraft/class_2583;Lnet/minecraft/class_5224;)Z", ordinal=0), method={"method_27472"}, index=0)
    private static String adjustText(String text) {
        TextFactoryEvent event = new TextFactoryEvent(text);
        event.hook();
        return event.getText();
    }
}

