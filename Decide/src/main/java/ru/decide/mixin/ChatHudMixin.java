/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_338
 *  net.minecraft.class_338$class_12233
 *  net.minecraft.class_7469
 *  net.minecraft.class_7591
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_338;
import net.minecraft.class_7469;
import net.minecraft.class_7591;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.render.BetterMinecraft;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_338.class})
public abstract class ChatHudMixin {
    private static final int FADE_TIME_MS = 150;
    @Shadow
    private int field_2066;
    @Unique
    private long decide$lastMessageTime;

    @Shadow
    private int method_44752() {
        return 0;
    }

    @Inject(method={"method_44811"}, at={@At(value="TAIL")})
    private void decide$trackMessageAdded(class_2561 contents, class_7469 signature, class_7591 tag, CallbackInfo ci) {
        if (BetterMinecraft.chatAnimationsEnabled()) {
            this.decide$lastMessageTime = System.currentTimeMillis();
        }
    }

    @Unique
    private float decide$displacement() {
        if (this.field_2066 != 0) {
            return 0.0f;
        }
        float maxDisplacement = (float)this.method_44752() * 0.8f;
        long lifetime = System.currentTimeMillis() - this.decide$lastMessageTime;
        float alpha = Math.min((float)lifetime / 150.0f, 1.0f);
        return maxDisplacement - alpha * maxDisplacement;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @WrapOperation(method={"method_75804"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_338;method_1805(Lnet/minecraft/class_338$class_12233;IIZ)V")})
    private void decide$wrapRender(class_338 instance, class_338.class_12233 queue, int x, int y, boolean focused, Operation<Void> original, @Local(argsOnly=true) class_332 graphics) {
        if (!BetterMinecraft.chatAnimationsEnabled()) {
            original.call(new Object[]{instance, queue, x, y, focused});
            return;
        }
        float displacement = this.decide$displacement();
        if (displacement == 0.0f) {
            original.call(new Object[]{instance, queue, x, y, focused});
            return;
        }
        graphics.method_51448().pushMatrix();
        graphics.method_51448().translate(0.0f, displacement);
        try {
            original.call(new Object[]{instance, queue, x, y, focused});
        }
        finally {
            graphics.method_51448().popMatrix();
        }
    }
}

