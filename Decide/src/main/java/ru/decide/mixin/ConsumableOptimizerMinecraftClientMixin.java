/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1799
 *  net.minecraft.class_1839
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1799;
import net.minecraft.class_1839;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.utils.consumable.ConsumableHandler;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_310.class})
public class ConsumableOptimizerMinecraftClientMixin {
    @Inject(method={"method_1583"}, at={@At(value="HEAD")}, cancellable=true)
    private void onDoItemUse(CallbackInfo ci) {
        if (!ConsumableHandler.isEnabled()) {
            return;
        }
        class_310 client = (class_310)this;
        class_746 player = client.field_1724;
        if (player == null) {
            return;
        }
        if (ConsumableHandler.STATE.isWaitingForServer()) {
            boolean offConsumable;
            class_1799 mainHand = player.method_6047();
            class_1799 offHand = player.method_6079();
            class_1839 mainAction = mainHand.method_7976();
            class_1839 offAction = offHand.method_7976();
            boolean mainConsumable = mainAction == class_1839.field_8950 || mainAction == class_1839.field_8946;
            boolean bl = offConsumable = offAction == class_1839.field_8950 || offAction == class_1839.field_8946;
            if (mainConsumable || offConsumable) {
                ci.cancel();
            }
        }
    }
}

