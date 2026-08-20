/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.option.KeyBinding
 *  net.minecraft.network.NetworkPhase
 *  net.minecraft.network.listener.PacketListener
 *  net.minecraft.util.math.BlockPos
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Constant
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyConstant
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package minced.mixin.entity;
import minced.core.EventUpdate;
import minced.core.EventSprint;
import minced.core.EventNoSlow;
import minced.core.RotationManager;
import minced.core.EventNoPush;
import minced.core.EventInventoryClose;
import minced.core.EventMotion;
import minced.core.EventNoRender;
import minced.core.EventPostUpdate;
import minced.core.EventPreTick;
import minced.core.EventPostTick;
import minced.core.Event;
import minced.core.EventBus;
import minced.core.ConnectionUtil;
import minced.enums.Block;
import minced.enums.fire;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ClientPlayerEntity.class})
public class ClientPlayerEntityMixin {
    @Unique
    private EventMotion eventMotion;

    @Inject(method={"tick"}, at={@At(value="HEAD")})
    public void tick(CallbackInfo param1) {
        EventPreTick v2 = new EventPreTick();
        EventBus.method3015(((Object)v2));
    }

    @Inject(method={"tick"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V")})
    private void renderPostHook(CallbackInfo param1) {
        EventBus.method3015(((Object)new EventUpdate()));
    }

    @Inject(method={"tick"}, at={@At(value="TAIL", target="Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V")})
    private void renderPreHook(CallbackInfo param1) {
        EventBus.method3015(((Object)new EventPostUpdate()));
    }

    @Inject(method={"tick"}, at={@At(value="RETURN")})
    private void minced$postTick(CallbackInfo param1) {
        EventBus.method3015(((Object)new EventPostTick()));
    }

    @Redirect(method={"tickNewAi"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
    private float pitchAi(ClientPlayerEntity param1) {
        return RotationManager.method245();
    }

    @Redirect(method={"tickNewAi"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
    private float yawAi(ClientPlayerEntity param1) {
        return RotationManager.method255();
    }

    @Redirect(method={"tickMovement"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal=1))
    private boolean onSprintKeyPressed(KeyBinding param1) {
        EventSprint v2 = new EventSprint();
        EventBus.method3015(((Object)v2));
        if (v2.method2297()) {
            return true;
        }
        return param1.isPressed();
    }

    @Redirect(method={"tickMovement"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean onNoSlow(ClientPlayerEntity param1) {
        if (param1.isUsingItem() && !param1.hasVehicle()) {
            EventNoSlow v2 = new EventNoSlow();
            EventBus.method3015(((Object)v2));
            if (v2.method2297()) {
                return false;
            }
        }
        return param1.isUsingItem();
    }

    @Redirect(method={"canStartSprinting"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean redirectCanStartSprintIsUsingItem(ClientPlayerEntity param1) {
        EventNoSlow v2 = new EventNoSlow();
        EventBus.method3015(((Object)v2));
        if (v2.method2297()) {
            return false;
        }
        return param1.isUsingItem();
    }

    @Inject(method={"closeHandledScreen"}, at={@At(value="HEAD")}, cancellable=true)
    public void onCloseScreen(CallbackInfo param1) {
        ClientPlayerEntity v2 = (ClientPlayerEntity)(Object)this;
        int v3 = v2.currentScreenHandler.syncId;
        EventInventoryClose v4 = new EventInventoryClose(v3);
        EventBus.method3015(((Object)v4));
        if (v4.method2297()) {
            param1.cancel();
        }
    }

    @Inject(method={"sendMovementPackets"}, at={@At(value="HEAD")}, cancellable=true)
    public void onSendMovementPackets(CallbackInfo param1) {
        ClientPlayerEntity v2 = (ClientPlayerEntity)(Object)this;
        ClientPlayNetworkHandler v3 = v2.networkHandler;
        if (v3 instanceof ConnectionUtil) {
            NetworkPhase v5;
            PacketListener v4 = v3.getConnection() != null ? v3.getConnection().getPacketListener() : null;
            NetworkPhase networkPhase = v5 = v4 != null ? v4.getPhase() : null;
            if (v3.getPhase() != NetworkPhase.PLAY || v5 != NetworkPhase.PLAY) {
                param1.cancel();
                return;
            }
        }
        this.eventMotion = new EventMotion(v2.getX(), v2.getY(), v2.getZ(), v2.getYaw(), v2.getPitch(), v2.isOnGround(), v2.isSneaking(), v2.isSprinting());
        EventBus.method3015(((Object)this.eventMotion));
        if (this.eventMotion.method2297()) {
            param1.cancel();
        }
    }

    @Redirect(method={"sendMovementPackets"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;getX()D"))
    private double redirectGetX(ClientPlayerEntity param1) {
        return this.eventMotion.method2518();
    }

    @Redirect(method={"sendMovementPackets"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;getY()D"))
    private double redirectGetY(ClientPlayerEntity param1) {
        return this.eventMotion.method2508();
    }

    @Redirect(method={"sendMovementPackets"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;getZ()D"))
    private double redirectGetZ(ClientPlayerEntity param1) {
        return this.eventMotion.method2513();
    }

    @Redirect(method={"sendMovementPackets"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
    private float redirectGetYaw(ClientPlayerEntity param1) {
        return this.eventMotion.method2514();
    }

    @Redirect(method={"sendMovementPackets"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
    private float redirectGetPitch(ClientPlayerEntity param1) {
        return this.eventMotion.method2515();
    }

    @Redirect(method={"sendMovementPackets"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;isOnGround()Z"))
    private boolean redirectIsOnGround(ClientPlayerEntity param1) {
        return this.eventMotion.method2516();
    }

    @Inject(method={"wouldCollideAt"}, at={@At(value="HEAD")}, cancellable=true)
    private void wouldCollideAt(BlockPos param1, CallbackInfoReturnable<Boolean> param2) {
        EventNoPush v3 = new EventNoPush(Block.Block);
        EventBus.method3015(((Object)v3));
        if (v3.method2297()) {
            param2.setReturnValue((Boolean)false);
        }
    }

    @ModifyConstant(method={"tickNausea"}, constant={@Constant(floatValue=0.006666667f)})
    private float modifyNauseaIntensity(float param1) {
        EventNoRender v2 = new EventNoRender(fire.bad_effects);
        EventBus.method3015(((Object)v2));
        if (v2.method2297()) {
            return 0.0f;
        }
        return param1;
    }
}



