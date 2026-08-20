/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.render.item.HeldItemRenderer
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Arm
 *  net.minecraft.util.Hand
 *  net.minecraft.util.math.MathHelper
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package minced.mixin.render;
import minced.core.HandsShader;
import minced.core.ServiceRegistry;
import minced.core.BotView;
import minced.core.EventViewModel;
import minced.core.RotationManager;
import minced.core.EventEquipOffset;
import minced.core.EventSwingArm;
import minced.core.Event;
import minced.core.EventBus;
import minced.core.ChatCommandHandler;
import minced.core.Minced;
import minced.mixin.accessor.IHeldItemRendererMixin;
import minced.module.visual.SeeInvisibles;
import minced.module.visual.ShaderHands;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ru.dreamix.fabricloader.VMBridge;

@Mixin(value={HeldItemRenderer.class})
public abstract class HeldItemRendererMixin {
    @Unique
    private Hand currentHand;
    @Unique
    private Arm minced$equipArm;
    @Unique
    private float minced$equipProgress;
    private static float field12647 = -0.4f;
    private static float field12648 = (float)Math.PI;
    private static float field12649 = 0.2f;
    private static float field12650 = (float)(Math.PI * 2.0);
    private static float field12651 = -0.2f;
    private static float field12652 = (float)Math.PI;
    private static float field12653 = 1.0f;
    private static float field12654 = 1.0f;

    @Inject(method={"updateHeldItems"}, at={@At(value="HEAD")})
    private void mincedbots$beginBotViewHeldItemsScope(CallbackInfo param1) {
        BotView v2 = (BotView)ServiceRegistry.method2118(BotView.class);
        if (v2 != null) {
            v2.method2216();
        }
    }

    @Inject(method={"updateHeldItems"}, at={@At(value="RETURN")})
    private void mincedbots$endBotViewHeldItemsScope(CallbackInfo param1) {
        BotView v2 = (BotView)ServiceRegistry.method2118(BotView.class);
        if (v2 != null) {
            v2.method2208();
        }
    }

    @Inject(method={"renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"}, at={@At(value="HEAD")})
    private void minced$captureBeforeShaderHands(float param1, MatrixStack param2, VertexConsumerProvider.Immediate param3, ClientPlayerEntity param4, int param5, CallbackInfo param6) {
        if (this.minced$isShaderHandsEnabled()) {
            HandsShader.method1933().method1920();
        }
    }

    @Inject(method={"renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw()V", shift=At.Shift.AFTER)})
    private void minced$finishShaderHandsPass(float param1, MatrixStack param2, VertexConsumerProvider.Immediate param3, ClientPlayerEntity param4, int param5, CallbackInfo param6) {
        if (this.minced$isShaderHandsEnabled()) {
            HandsShader.method1933().method1922();
        }
    }

    @Unique
    private boolean minced$isShaderHandsEnabled() {
        if (Minced.getInstance() == null || Minced.getInstance().getModuleManager() == null) {
            return false;
        }
        ShaderHands v1 = (ShaderHands)Minced.getInstance().getModuleManager().method187(ShaderHands.class);
        return v1 != null && v1.isEnabled();
    }

    @ModifyVariable(method={"renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"}, at=@At(value="HEAD"), ordinal=0, argsOnly=true)
    private ClientPlayerEntity mincedbots$activePlayerInHandRender(ClientPlayerEntity param1) {
        MinecraftClient v2 = MinecraftClient.getInstance();
        ClientPlayerEntity v3 = v2 != null ? ChatCommandHandler.method385(v2) : null;
        return v3 != null ? v3 : param1;
    }

    @ModifyVariable(method={"renderFirstPersonItem"}, at=@At(value="HEAD"), argsOnly=true)
    private Hand captureHand(Hand param1) {
        this.currentHand = param1;
        return param1;
    }

    @Inject(method="applySwingOffset", at=@At("HEAD"), cancellable=true)
    private void minced$onApplySwingOffset(MatrixStack matrices, Arm arm, float swingProgress, CallbackInfo ci) {
        if (this.currentHand == null) {
            this.currentHand = Hand.MAIN_HAND;
        }
        EventSwingArm v8 = new EventSwingArm(swingProgress, this.currentHand, matrices);
        EventBus.method3015(((Object)v8));
        if (v8.method2297()) {
            ci.cancel();
        }
    }

    @Redirect(method={"renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;getPitch(F)F"))
    private float pitch(ClientPlayerEntity param1, float param2) {
        return RotationManager.method245();
    }

    @Redirect(method={"renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;getYaw(F)F"))
    private float yaw(ClientPlayerEntity param1, float param2) {
        return RotationManager.method255();
    }

    @Inject(method={"renderFirstPersonItem"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/util/math/MatrixStack;push()V", shift=At.Shift.AFTER)}, locals=LocalCapture.CAPTURE_FAILHARD)
    private void render(AbstractClientPlayerEntity param1, float param2, float param3, Hand param4, float param5, ItemStack param6, float param7, MatrixStack param8, VertexConsumerProvider param9, int param10, CallbackInfo param11, boolean param12, Arm param13) {
        EventBus.method3015(((Object)new EventViewModel(param8, param13)));
    }

    @Redirect(method={"renderMapInOneHand"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;isInvisible()Z"))
    private boolean redirectIsInvisibleInMapOneHand(ClientPlayerEntity param1) {
        SeeInvisibles v2 = (SeeInvisibles)Minced.getInstance().getModuleManager().method187(SeeInvisibles.class);
        return param1.isInvisible() && (v2 == null || !v2.isEnabled());
    }

    @Redirect(method={"renderMapInBothHands"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;isInvisible()Z"))
    private boolean redirectIsInvisibleInMapBothHands(ClientPlayerEntity param1) {
        SeeInvisibles v2 = (SeeInvisibles)Minced.getInstance().getModuleManager().method187(SeeInvisibles.class);
        return param1.isInvisible() && (v2 == null || !v2.isEnabled());
    }

    @Redirect(method={"renderFirstPersonItem"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/AbstractClientPlayerEntity;isInvisible()Z"))
    private boolean redirectIsInvisibleInFirstPerson(AbstractClientPlayerEntity param1) {
        SeeInvisibles v2 = (SeeInvisibles)Minced.getInstance().getModuleManager().method187(SeeInvisibles.class);
        return param1.isInvisible() && (v2 == null || !v2.isEnabled());
    }

    @Inject(method={"applyEquipOffset"}, at={@At(value="HEAD")})
    private void minced$captureEquipArgs(MatrixStack param1, Arm param2, float param3, CallbackInfo param4) {
        this.minced$equipArm = param2;
        this.minced$equipProgress = param3;
    }

    @ModifyArg(method={"applyEquipOffset"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"), index=1)
    private float modifyEquipOffsetY(float param1) {
        EventEquipOffset v2 = new EventEquipOffset(this.minced$equipArm, this.minced$equipProgress, field12653);
        EventBus.method3015(((Object)v2));
        float v3 = param1 - this.minced$equipProgress * field12654;
        return v3 + this.minced$equipProgress * v2.method2539();
    }

    static {
        VMBridge.identifyClass(HeldItemRendererMixin.class, "46TH8Zmd");
    }
}



