/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1282
 *  net.minecraft.class_1291
 *  net.minecraft.class_1292
 *  net.minecraft.class_1293
 *  net.minecraft.class_1294
 *  net.minecraft.class_1309
 *  net.minecraft.class_1799
 *  net.minecraft.class_310
 *  net.minecraft.class_6880
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Constant
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyConstant
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1282;
import net.minecraft.class_1291;
import net.minecraft.class_1292;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_6880;
import net.minecraft.class_746;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.manager.event_impl.EventJump;
import ru.decide.manager.event_impl.SwingDurationEvent;
import ru.decide.module.impl.player.NoDelay;
import ru.decide.module.impl.render.ModelCollapse;
import ru.decide.module.impl.utils.consumable.ConsumableHandler;
import ru.decide.utils.math.MathUtil;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1309.class})
public abstract class LivingEntityMixin {
    @Shadow
    public abstract boolean method_6059(class_6880<class_1291> var1);

    @Shadow
    @Nullable
    public abstract class_1293 method_6112(class_6880<class_1291> var1);

    @ModifyConstant(method={"method_6007"}, constant={@Constant(intValue=10)})
    private int modifyJumpTicks(int original) {
        NoDelay noDelay;
        class_1309 self = (class_1309)this;
        if (self instanceof class_746 && (noDelay = NoDelay.get()) != null && noDelay.isEnabled() && noDelay.jump.getValue().booleanValue()) {
            return MathUtil.randomInt(0, 3);
        }
        return original;
    }

    @Inject(method={"method_6028"}, at={@At(value="HEAD")}, cancellable=true)
    private void swingProgressHook(CallbackInfoReturnable<Integer> cir) {
        class_310 client = class_310.method_1551();
        if (this != client.field_1724) {
            return;
        }
        SwingDurationEvent event = new SwingDurationEvent();
        event.hook();
        if (event.isCancelled()) {
            float animation = event.getAnimation();
            animation = class_1292.method_5576((class_1309)client.field_1724) ? (animation *= (float)(6 - (1 + class_1292.method_5575((class_1309)client.field_1724)))) : (animation *= (float)(this.method_6059((class_6880<class_1291>)class_1294.field_5901) ? 6 + (1 + this.method_6112((class_6880<class_1291>)class_1294.field_5901).method_5578()) * 2 : 6));
            cir.setReturnValue((Object)((int)animation));
        }
    }

    @Inject(method={"method_6043"}, at={@At(value="HEAD")})
    public void jumpYo(CallbackInfo ci) {
        class_1309 self = (class_1309)this;
        if (self instanceof class_746) {
            new EventJump().hook();
        }
    }

    @Inject(method={"method_37119"}, at={@At(value="HEAD")}, cancellable=true)
    private void onTickItemStackUsage(class_1799 stack, CallbackInfo ci) {
        class_1309 self = (class_1309)this;
        if (self instanceof class_746) {
            class_746 player = (class_746)self;
            if (ConsumableHandler.isEnabled()) {
                ConsumableHandler.handleItemStackUsage(stack, ci);
            }
        }
    }

    @Inject(method={"method_6078"}, at={@At(value="HEAD")})
    private void onDeathCollapse(class_1282 source, CallbackInfo ci) {
        ModelCollapse.notifyEntityDied((class_1309)this);
    }

    @Inject(method={"method_6033"}, at={@At(value="HEAD")})
    private void onSetHealthCollapse(float health, CallbackInfo ci) {
        if (health > 0.0f) {
            return;
        }
        class_1309 self = (class_1309)this;
        if (self.method_6032() > 0.0f) {
            ModelCollapse.notifyEntityDied(self);
        }
    }
}

