/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  com.llamalad7.mixinextras.injector.v2.WrapWithCondition
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11659
 *  net.minecraft.class_1268
 *  net.minecraft.class_1306
 *  net.minecraft.class_1764
 *  net.minecraft.class_1799
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_742
 *  net.minecraft.class_746
 *  net.minecraft.class_759
 *  net.minecraft.class_7833
 *  org.joml.Quaternionfc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.Slice
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11659;
import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1764;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_742;
import net.minecraft.class_746;
import net.minecraft.class_759;
import net.minecraft.class_7833;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.manager.event_impl.GlassHandsRenderEvent;
import ru.decide.manager.event_impl.HandAnimationEvent;
import ru.decide.manager.event_impl.RenderItemEvent;
import ru.decide.module.impl.render.GlassHands;
import ru.decide.module.impl.render.Hands;
import ru.decide.module.impl.render.NoRender;
import ru.decide.module.impl.render.SwingAnimation;
import ru.decide.module.impl.utils.consumable.ConsumableHandler;
import ru.decide.screen.HandsEditor;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_759.class})
public abstract class HeldItemRendererMixin {
    @WrapWithCondition(method={"method_3222"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_4587;method_22907(Lorg/joml/Quaternionfc;)V", ordinal=1)})
    private boolean cancelMapRotationX(class_4587 matrices, Quaternionfc quaternion) {
        return false;
    }

    @WrapWithCondition(method={"method_3222"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_4587;method_22907(Lorg/joml/Quaternionfc;)V", ordinal=2)})
    private boolean cancelMapRotationY(class_4587 matrices, Quaternionfc quaternion) {
        return false;
    }

    @WrapOperation(method={"method_22976"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_4587;method_22907(Lorg/joml/Quaternionfc;)V", ordinal=0)}, slice={@Slice(from=@At(value="INVOKE", target="Lnet/minecraft/class_759;method_33303(Lnet/minecraft/class_746;)Lnet/minecraft/class_759$class_5773;"), to=@At(value="FIELD", target="Lnet/minecraft/class_759$class_5773;field_28387:Z"))})
    private void replacePitchRotation(class_4587 matrices, Quaternionfc quaternion, Operation<Void> original, @Local(ordinal=3) float h) {
        if (!NoRender.getInstance().removeCamreZalupa.getValue().booleanValue()) {
            float cameraPitch = class_310.method_1551().field_1773.method_19418().method_19329();
            matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees((cameraPitch - h) * 0.1f));
        }
    }

    @WrapOperation(method={"method_22976"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_4587;method_22907(Lorg/joml/Quaternionfc;)V", ordinal=1)}, slice={@Slice(from=@At(value="INVOKE", target="Lnet/minecraft/class_759;method_33303(Lnet/minecraft/class_746;)Lnet/minecraft/class_759$class_5773;"), to=@At(value="FIELD", target="Lnet/minecraft/class_759$class_5773;field_28387:Z"))})
    private void replaceYawRotation(class_4587 matrices, Quaternionfc quaternion, Operation<Void> original, @Local(ordinal=4) float i) {
        if (!NoRender.getInstance().removeCamreZalupa.getValue().booleanValue()) {
            float cameraYaw = class_310.method_1551().field_1773.method_19418().method_19330();
            matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((cameraYaw - i) * 0.1f));
        }
    }

    @ModifyExpressionValue(method={"method_22976"}, at={@At(value="FIELD", target="Lnet/minecraft/class_759$class_5773;field_28387:Z")})
    private boolean decide$showEditorMainHand(boolean original) {
        return HandsEditor.getInstance().isActive() || original;
    }

    @ModifyExpressionValue(method={"method_22976"}, at={@At(value="FIELD", target="Lnet/minecraft/class_759$class_5773;field_28388:Z")})
    private boolean decide$showEditorOffHand(boolean original) {
        return HandsEditor.getInstance().isActive() || original;
    }

    @ModifyArg(method={"method_22976"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_759;method_3228(Lnet/minecraft/class_742;FFLnet/minecraft/class_1268;FLnet/minecraft/class_1799;FLnet/minecraft/class_4587;Lnet/minecraft/class_11659;I)V", ordinal=0), index=5)
    private class_1799 decide$useEditorMainHandItem(class_1799 original) {
        if (!HandsEditor.getInstance().isActive()) {
            return original;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return original;
        }
        return HandsEditor.getInstance().previewStackFor(player.method_6068());
    }

    @ModifyArg(method={"method_22976"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_759;method_3228(Lnet/minecraft/class_742;FFLnet/minecraft/class_1268;FLnet/minecraft/class_1799;FLnet/minecraft/class_4587;Lnet/minecraft/class_11659;I)V", ordinal=1), index=5)
    private class_1799 decide$useEditorOffHandItem(class_1799 original) {
        if (!HandsEditor.getInstance().isActive()) {
            return original;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return original;
        }
        return HandsEditor.getInstance().previewStackFor(player.method_6068().method_5928());
    }

    @Inject(method={"method_22976"}, at={@At(value="HEAD")})
    private void onRenderItemPre(float tickProgress, class_4587 matrices, class_11659 queue, class_746 player, int light, CallbackInfo ci) {
        HandsEditor editor = HandsEditor.getInstance();
        if (editor.isActive()) {
            editor.captureBeforeHands();
            return;
        }
        GlassHands glassHands = GlassHands.getInstance();
        if (glassHands != null && glassHands.isEnabled()) {
            new GlassHandsRenderEvent(GlassHandsRenderEvent.Phase.PRE, matrices, tickProgress).hook();
        }
    }

    @Inject(method={"method_22976"}, at={@At(value="TAIL")})
    private void onRenderItemPost(float tickProgress, class_4587 matrices, class_11659 queue, class_746 player, int light, CallbackInfo ci) {
        if (HandsEditor.getInstance().isActive()) {
            return;
        }
        GlassHands glassHands = GlassHands.getInstance();
        if (glassHands != null && glassHands.isEnabled()) {
            new GlassHandsRenderEvent(GlassHandsRenderEvent.Phase.POST, matrices, tickProgress).hook();
        }
    }

    @Inject(method={"method_3228"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_4587;method_22903()V", shift=At.Shift.AFTER, ordinal=0)})
    private void onRenderFirstPersonItem(class_742 player, float tickProgress, float pitch, class_1268 hand, float swingProgress, class_1799 item, float equipProgress, class_4587 matrices, class_11659 orderedRenderCommandQueue, int light, CallbackInfo ci) {
        Hands hands;
        class_1306 arm = hand == class_1268.field_5808 ? player.method_6068() : player.method_6068().method_5928();
        RenderItemEvent event = new RenderItemEvent(matrices, hand, arm);
        event.hook();
        if (!item.method_7960() && (hands = Hands.get()) != null) {
            hands.applyHandTranslation(matrices, arm);
        }
    }

    @WrapWithCondition(method={"method_3228"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_759;method_3224(Lnet/minecraft/class_4587;Lnet/minecraft/class_1306;F)V", ordinal=4)})
    private boolean cancelEquipOffsetForCustomAnim(class_759 instance, class_4587 matrices, class_1306 arm, float equipProgress) {
        if (arm != class_310.method_1551().field_1724.method_6068()) {
            return true;
        }
        SwingAnimation hands = SwingAnimation.get();
        if (hands == null || !hands.isEnabled() || !hands.auraCheck()) {
            return true;
        }
        return hands.swingMode.is("\u041e\u0442\u043a\u043b\u044e\u0447\u0435\u043d\u043e");
    }

    @WrapOperation(method={"method_3228"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_759;method_65816(FLnet/minecraft/class_4587;ILnet/minecraft/class_1306;)V")})
    private void handAnimationHook(class_759 instance, float swingProgress, class_4587 matrices, int armX, class_1306 arm, Operation<Void> original, @Local(ordinal=0, argsOnly=true) class_742 player, @Local(ordinal=0, argsOnly=true) class_1268 hand, @Local(ordinal=3, argsOnly=true) float equipProgress, @Local(ordinal=0, argsOnly=true) class_1799 item) {
        if (item.method_7909() instanceof class_1764) {
            original.call(new Object[]{instance, Float.valueOf(swingProgress), matrices, armX, arm});
            return;
        }
        HandAnimationEvent event = new HandAnimationEvent(matrices, hand, swingProgress, equipProgress, arm);
        event.hook();
        if (!event.isCancelled()) {
            original.call(new Object[]{instance, Float.valueOf(swingProgress), matrices, armX, arm});
        }
    }

    @Inject(method={"method_3228"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_759;method_3233(Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;Lnet/minecraft/class_811;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;I)V", ordinal=0)})
    public void injectBeforeRenderCrossBowItem(class_742 player, float tickProgress, float pitch, class_1268 hand, float swingProgress, class_1799 item, float equipProgress, class_4587 matrices, class_11659 orderedRenderCommandQueue, int light, CallbackInfo ci) {
        Hands viewModel = Hands.get();
        if (viewModel.shouldApplyTransforms()) {
            boolean isMainHand = hand == class_1268.field_5808;
            class_1306 arm = isMainHand ? player.method_6068() : player.method_6068().method_5928();
            viewModel.applyHandScale(matrices, arm);
            HandsEditor.getInstance().updateHandBounds(arm, matrices);
        }
    }

    @Inject(method={"method_3228"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_759;method_3233(Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;Lnet/minecraft/class_811;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;I)V", ordinal=1)})
    public void injectBeforeRenderItem(class_742 player, float tickDelta, float pitch, class_1268 hand, float swingProgress, class_1799 item, float equipProgress, class_4587 matrices, class_11659 orderedRenderCommandQueue, int light, CallbackInfo ci) {
        Hands viewModel = Hands.get();
        if (viewModel.shouldApplyTransforms()) {
            boolean isMainHand = hand == class_1268.field_5808;
            class_1306 arm = isMainHand ? player.method_6068() : player.method_6068().method_5928();
            viewModel.applyHandScale(matrices, arm);
            HandsEditor.getInstance().updateHandBounds(arm, matrices);
        }
    }

    @Inject(method={"method_65910"}, at={@At(value="HEAD")}, cancellable=true)
    private void onShouldSkipHandAnimationOnSwap(CallbackInfoReturnable<Boolean> cir) {
        if (ConsumableHandler.isEnabled() && ConsumableHandler.shouldSkipHandAnimationOnSwap()) {
            cir.setReturnValue((Object)true);
        }
    }
}

