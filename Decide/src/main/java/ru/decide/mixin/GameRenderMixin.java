/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11228
 *  net.minecraft.class_11246
 *  net.minecraft.class_1799
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_4184
 *  net.minecraft.class_437
 *  net.minecraft.class_4587
 *  net.minecraft.class_490
 *  net.minecraft.class_757
 *  net.minecraft.class_758
 *  net.minecraft.class_758$class_4596
 *  net.minecraft.class_7833
 *  net.minecraft.class_9779
 *  net.minecraft.class_9920
 *  net.minecraft.class_9922
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11228;
import net.minecraft.class_11246;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4184;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_490;
import net.minecraft.class_757;
import net.minecraft.class_758;
import net.minecraft.class_7833;
import net.minecraft.class_9779;
import net.minecraft.class_9920;
import net.minecraft.class_9922;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.Client;
import ru.decide.cosmetics.render.CosmeticGuiPreview;
import ru.decide.inventorypreset.InventoryPresetOverlay;
import ru.decide.manager.DragComponent;
import ru.decide.manager.event_impl.EventDisplay;
import ru.decide.module.impl.render.Cinematic;
import ru.decide.module.impl.render.GlassVapor;
import ru.decide.module.impl.render.NoRender;
import ru.decide.module.impl.render.WastedDeath;
import ru.decide.screen.GuiCloseAnimation3D;
import ru.decide.screen.GuiCloseAnimationShatter;
import ru.decide.screen.HandsEditor;
import ru.decide.screen.Menu;
import ru.decide.ui.lv.LvGui;
import ru.decide.utils.render.Render2D;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.wasted.WastedPipeline;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_757.class})
public abstract class GameRenderMixin {
    @Shadow
    @Final
    private class_310 field_4015;
    @Shadow
    @Final
    private class_11228 field_59965;
    @Shadow
    @Final
    private class_758 field_60793;
    @Shadow
    @Final
    private class_9920 field_53066;
    @Shadow
    @Final
    class_11246 field_59966;
    @Shadow
    @Final
    private class_4184 field_18765;

    @Inject(method={"close"}, at={@At(value="RETURN")})
    private void onClose(CallbackInfo ci) {
        if (Client.get() != null && Client.get().render2D() != null) {
            Client.get().render2D().close();
        }
    }

    @Inject(method={"method_3192"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_11228;method_70890(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", shift=At.Shift.BEFORE)})
    private void beforeGuiRender(class_9779 tickCounter, boolean tick, CallbackInfo ci) {
        if (this.field_4015.field_1687 == null || this.field_4015.field_1724 == null) {
            return;
        }
        GlassVapor.applyPending();
        if (!WastedDeath.isRunning()) {
            return;
        }
        WastedPipeline.apply(1.0f, 0.97f, 0.94f, 0.35f);
    }

    @Inject(method={"method_3192"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_11228;method_70890(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", shift=At.Shift.AFTER)})
    private void afterGuiRender(class_9779 tickCounter, boolean tick, CallbackInfo ci) {
        if (this.field_4015.field_1687 == null || this.field_4015.field_1724 == null) {
            return;
        }
        if (this.isLoadingScreen(this.field_4015.field_1755)) {
            return;
        }
        if (this.field_4015.method_18506() != null) {
            return;
        }
        if (!this.shouldRenderOnTop(this.field_4015.field_1755)) {
            return;
        }
        this.field_59966.method_70926();
        int mouseX = (int)this.field_4015.field_1729.method_68879(this.field_4015.method_22683());
        int mouseY = (int)this.field_4015.field_1729.method_68883(this.field_4015.method_22683());
        float tickDelta = tickCounter.method_60637(false);
        class_332 context = new class_332(this.field_4015, this.field_59966, mouseX, mouseY);
        class_437 class_4372 = this.field_4015.field_1755;
        if (class_4372 instanceof Menu) {
            Menu menu = (Menu)class_4372;
            menu.renderOverlay(context, tickCounter);
        } else if (this.field_4015.field_1755 instanceof LvGui) {
            context.method_71048();
            Render2D.beginOverlay();
            context.method_51448().pushMatrix();
            EventDisplay event = new EventDisplay(context, tickDelta);
            Client.get().componentManager().get(DragComponent.class).post((Matrix3x2f)context.method_51448());
            event.hook();
            Client.get().render2D().flushAll();
            context.method_51448().popMatrix();
            Render2D.endOverlay();
        }
        if (this.field_4015.field_1755 instanceof class_490) {
            InventoryPresetOverlay.render(context, mouseX, mouseY);
        }
        this.field_59965.method_70890(this.field_60793.method_71109(class_758.class_4596.field_60101));
        CosmeticGuiPreview.renderPending();
        GuiCloseAnimationShatter.render();
    }

    @ModifyExpressionValue(method={"method_3188"}, at={@At(value="INVOKE", target="Ljava/lang/Math;max(FF)F", ordinal=0)})
    private float onNauseaDistortion(float original) {
        NoRender noRender = NoRender.getInstance();
        if (noRender != null && noRender.isEnabled() && noRender.ignoreZalupa.getValue().booleanValue()) {
            return 0.0f;
        }
        return original;
    }

    @Inject(method={"method_3189"}, at={@At(value="HEAD")}, cancellable=true)
    private void onShowFloatingItem(class_1799 stack, CallbackInfo ci) {
        NoRender noRender = NoRender.getInstance();
        if (noRender != null && noRender.isEnabled() && noRender.ignoreTotemPop.getValue().booleanValue()) {
            ci.cancel();
        }
    }

    @Unique
    private boolean shouldRenderOnTop(class_437 screen) {
        if (screen == null) {
            return true;
        }
        if (screen instanceof Menu) {
            return true;
        }
        if (screen instanceof LvGui) {
            return true;
        }
        return screen instanceof class_490;
    }

    @Unique
    private boolean isLoadingScreen(class_437 screen) {
        if (screen == null) {
            return false;
        }
        String className = screen.getClass().getSimpleName().toLowerCase();
        String fullName = screen.getClass().getName().toLowerCase();
        if (className.contains("loading")) {
            return true;
        }
        if (className.contains("progress")) {
            return true;
        }
        if (className.contains("connecting")) {
            return true;
        }
        if (className.contains("downloading")) {
            return true;
        }
        if (className.contains("terrain")) {
            return true;
        }
        if (className.contains("generating")) {
            return true;
        }
        if (className.contains("saving")) {
            return true;
        }
        if (className.contains("reload")) {
            return true;
        }
        if (className.contains("resource")) {
            return true;
        }
        if (className.contains("pack")) {
            return true;
        }
        return fullName.contains("mojang");
    }

    @Shadow
    public abstract float method_3196(class_4184 var1, float var2, boolean var3);

    @Inject(method={"method_3188"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_4597$class_4598;method_22993()V", shift=At.Shift.AFTER)})
    private void captureHandsForEditor(class_9779 tickCounter, CallbackInfo ci) {
        HandsEditor editor = HandsEditor.getInstance();
        if (editor.isActive()) {
            editor.captureAfterHands();
        }
    }

    @Inject(method={"method_3188"}, at={@At(value="TAIL")})
    private void afterWorldRender(class_9779 tickCounter, CallbackInfo ci) {
        if (this.field_4015.field_1687 == null || this.field_4015.field_1724 == null) {
            return;
        }
        Cinematic cinematic = Cinematic.getInstance();
        if (cinematic == null) {
            return;
        }
        try {
            cinematic.render((class_9922)this.field_53066);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    @Inject(method={"method_3188"}, at={@At(value="INVOKE_STRING", target="Lnet/minecraft/class_3695;method_15405(Ljava/lang/String;)V", args={"ldc=hand"})})
    public void hookWorldRender(class_9779 tickCounter, CallbackInfo ci, @Local(ordinal=0) Matrix4f projection, @Local(ordinal=1) Matrix4f view, @Local(ordinal=0) float tickDelta, @Local class_4587 matrixStack) {
        if (this.field_4015.field_1687 == null || this.field_4015.field_1724 == null) {
            return;
        }
        if (GuiCloseAnimation3D.isActive()) {
            GuiCloseAnimation3D.render(view, tickDelta);
        }
        class_4587 worldSpaceStack = new class_4587();
        worldSpaceStack.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(this.field_18765.method_19329()));
        worldSpaceStack.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(this.field_18765.method_19330() + 180.0f));
        RenderUtil.Render3D.lastProjMat.set((Matrix4fc)this.field_4015.field_1773.method_22973(this.method_3196(this.field_18765, tickDelta, true)));
        RenderUtil.Render3D.lastModMat.set((Matrix4fc)RenderSystem.getModelViewMatrix());
        RenderUtil.Render3D.lastWorldSpaceMatrix.set((Matrix4fc)worldSpaceStack.method_23760().method_23761());
    }
}

