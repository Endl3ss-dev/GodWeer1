/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_408
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_408;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.render.BetterMinecraft;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_408.class})
public abstract class ChatScreenAnimationMixin {
    private static final int FADE_TIME_MS = 170;
    private static final float FADE_OFFSET = 8.0f;
    @Unique
    private boolean decide$wasOpenedLastFrame;
    @Unique
    private long decide$lastOpenTime;
    @Unique
    private float decide$displacement;

    @Unique
    private float decide$chatDisplacement() {
        if (!BetterMinecraft.chatAnimationsEnabled()) {
            return 0.0f;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1724 != null && !this.decide$wasOpenedLastFrame && !client.field_1724.method_6113()) {
            this.decide$wasOpenedLastFrame = true;
            this.decide$lastOpenTime = System.currentTimeMillis();
        }
        float screenFactor = (float)client.method_22683().method_4506() / 1080.0f;
        float timeSinceOpen = Math.min((float)(System.currentTimeMillis() - this.decide$lastOpenTime), 170.0f);
        float alpha = 1.0f - timeSinceOpen / 170.0f;
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        float eased = c3 * alpha * alpha * alpha - c1 * alpha * alpha;
        return eased * 8.0f * screenFactor;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @WrapOperation(method={"method_25394"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_332;method_25294(IIIII)V")})
    private void decide$wrapBackgroundFill(class_332 graphics, int x0, int y0, int x1, int y1, int color, Operation<Void> original) {
        this.decide$displacement = this.decide$chatDisplacement();
        if (this.decide$displacement == 0.0f) {
            original.call(new Object[]{graphics, x0, y0, x1, y1, color});
            return;
        }
        graphics.method_51448().pushMatrix();
        graphics.method_51448().translate(0.0f, this.decide$displacement);
        try {
            original.call(new Object[]{graphics, x0, y0, x1, y1, color});
        }
        finally {
            graphics.method_51448().popMatrix();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @WrapOperation(method={"method_25394"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_437;method_25394(Lnet/minecraft/class_332;IIF)V")})
    private void decide$wrapSuperAndSuggestions(class_408 instance, class_332 graphics, int mouseX, int mouseY, float delta, Operation<Void> original) {
        if (this.decide$displacement == 0.0f) {
            original.call(new Object[]{instance, graphics, mouseX, mouseY, Float.valueOf(delta)});
            return;
        }
        graphics.method_51448().pushMatrix();
        graphics.method_51448().translate(0.0f, this.decide$displacement);
        try {
            original.call(new Object[]{instance, graphics, mouseX, mouseY, Float.valueOf(delta)});
        }
        finally {
            graphics.method_51448().popMatrix();
        }
    }

    @Inject(method={"method_25432"}, at={@At(value="HEAD")})
    private void decide$chatClosed(CallbackInfo ci) {
        this.decide$wasOpenedLastFrame = false;
    }
}

