/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1269
 *  net.minecraft.class_1269$class_9860
 *  net.minecraft.class_1269$class_9861
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_636
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_636;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.manager.event_impl.AttackEvent;
import ru.decide.manager.event_impl.UsingItemEvent;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_636.class})
public class ClientPlayerInteractionManagerMixin {
    @Inject(method={"method_2919"}, at={@At(value="RETURN")})
    public void interactItemHook(class_1657 player, class_1268 hand, CallbackInfoReturnable<class_1269> cir) {
        class_1269.class_9860 success;
        Object object = cir.getReturnValue();
        if (object instanceof class_1269.class_9860 && !(success = (class_1269.class_9860)object).comp_2909().equals((Object)class_1269.class_9861.field_52427)) {
            UsingItemEvent event = new UsingItemEvent(0);
            event.hook();
        }
    }

    @Inject(method={"method_2918"}, at={@At(value="HEAD")}, cancellable=true)
    private void onAttackEntity(class_1657 player, class_1297 target, CallbackInfo ci) {
        AttackEvent event = new AttackEvent(target);
        event.hook();
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method={"method_2897"}, at={@At(value="HEAD")}, cancellable=true)
    public void stopUsingItemHook(CallbackInfo ci) {
        UsingItemEvent event = new UsingItemEvent(2);
        event.hook();
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method={"method_2919"}, at={@At(value="HEAD")}, cancellable=true)
    private void gameModeHook(class_1657 player, class_1268 hand, CallbackInfoReturnable<class_1269> cir) {
        UsingItemEvent event = new UsingItemEvent(-1);
        event.hook();
        if (event.isCancelled()) {
            cir.setReturnValue((Object)class_1269.field_5811);
        }
    }
}

