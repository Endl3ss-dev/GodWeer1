/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_266
 *  net.minecraft.class_269
 *  net.minecraft.class_332
 *  net.minecraft.class_355
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_266;
import net.minecraft.class_269;
import net.minecraft.class_332;
import net.minecraft.class_355;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.render.BetterMinecraft;
import ru.decide.utils.render.TabAnimationAccess;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_355.class})
public abstract class PlayerListHudMixin
implements TabAnimationAccess {
    @Unique
    private static final long TAB_ANIMATION_MS = 300L;
    @Unique
    private long decide$animationStart;
    @Unique
    private long decide$animationDuration;
    @Unique
    private float decide$animationFrom;
    @Unique
    private float decide$animationTarget;
    @Unique
    private boolean decide$visible;
    @Unique
    private boolean decide$scaledForAnimation;

    @Inject(method={"method_1921"}, at={@At(value="HEAD")})
    private void decide$trackVisibility(boolean visible, CallbackInfo ci) {
        if (visible == this.decide$visible) {
            return;
        }
        float current = this.currentScale();
        this.decide$visible = visible;
        this.decide$animationFrom = current;
        this.decide$animationTarget = visible ? 1.0f : 0.0f;
        this.decide$animationStart = System.currentTimeMillis();
        this.decide$animationDuration = Math.max(1L, (long)Math.round(300.0f * Math.abs(this.decide$animationTarget - this.decide$animationFrom)));
    }

    @Inject(method={"method_1919"}, at={@At(value="HEAD")})
    private void decide$beginTabAnimation(class_332 context, int windowWidth, class_269 scoreboard, class_266 objective, CallbackInfo ci) {
        this.decide$scaledForAnimation = BetterMinecraft.tabAnimationEnabled();
        if (!this.decide$scaledForAnimation) {
            return;
        }
        float scale = Math.max(0.01f, Math.min(1.15f, this.currentScale()));
        context.method_51448().pushMatrix();
        context.method_51448().translate((float)windowWidth / 2.0f, 10.0f);
        context.method_51448().scale(scale, scale);
        context.method_51448().translate((float)(-windowWidth) / 2.0f, -10.0f);
    }

    @Inject(method={"method_1919"}, at={@At(value="RETURN")})
    private void decide$endTabAnimation(class_332 context, int windowWidth, class_269 scoreboard, class_266 objective, CallbackInfo ci) {
        if (!this.decide$scaledForAnimation) {
            return;
        }
        context.method_51448().popMatrix();
        this.decide$scaledForAnimation = false;
    }

    @Override
    public boolean decide$shouldRenderClosingTab() {
        return BetterMinecraft.tabAnimationEnabled() && !this.decide$visible && this.currentScale() > 0.01f;
    }

    @Unique
    private float currentScale() {
        long duration = Math.max(1L, this.decide$animationDuration);
        float progress = Math.min(1.0f, (float)(System.currentTimeMillis() - this.decide$animationStart) / (float)duration);
        float eased = this.decide$animationTarget > this.decide$animationFrom ? PlayerListHudMixin.easeOutBack(progress) : PlayerListHudMixin.easeInBack(progress);
        return this.decide$animationFrom + (this.decide$animationTarget - this.decide$animationFrom) * eased;
    }

    @Unique
    private static float easeOutBack(float value) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        float t = value - 1.0f;
        return 1.0f + c3 * t * t * t + c1 * t * t;
    }

    @Unique
    private static float easeInBack(float value) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        return c3 * value * value * value - c1 * value * value;
    }
}

