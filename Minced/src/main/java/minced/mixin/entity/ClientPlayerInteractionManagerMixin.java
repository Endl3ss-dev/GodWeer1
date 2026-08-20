/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.network.ClientPlayerInteractionManager
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package minced.mixin.entity;
import minced.core.EventClickBlockRight;
import minced.core.EventClickSlot;
import minced.core.EventAttack;
import minced.core.Event;
import minced.core.EventBus;
import minced.mixin.accessor.IClientPlayerInteractionManagerMixin;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ClientPlayerInteractionManager.class})
public class ClientPlayerInteractionManagerMixin {
    @Shadow
    @Final
    private ClientPlayNetworkHandler field_3720;

    @Inject(method={"interactBlock"}, at={@At(value="HEAD")}, cancellable=true)
    private void interactBlock(ClientPlayerEntity param1, Hand param2, BlockHitResult param3, CallbackInfoReturnable<ActionResult> param4) {
        EventClickBlockRight v5 = new EventClickBlockRight(param1, (ClientWorld)param1.getWorld(), param2, param3);
        EventBus.method3015(((Object)v5));
        if (v5.method2297()) {
            ((IClientPlayerInteractionManagerMixin)((Object)this)).invokeSyncSelectedSlot();
            param4.setReturnValue((ActionResult)ActionResult.PASS);
        }
    }

    @Inject(method={"attackEntity"}, at={@At(value="HEAD")}, cancellable=true)
    public void attackEntity(PlayerEntity param1, Entity param2, CallbackInfo param3) {
        EventAttack v4 = new EventAttack(param2);
        EventBus.method3015(((Object)v4));
        if (param2.equals((Object)param1) || v4.method2297()) {
            param3.cancel();
        }
    }

    @Redirect(method={"clickSlot"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private void onClickSlot(ClientPlayNetworkHandler param1, Packet<?> param2) {
        if (param2 instanceof ClickSlotC2SPacket v3) {
            EventClickSlot v4 = new EventClickSlot(v3.getSyncId(), v3.getRevision(), v3.getSlot(), v3.getButton(), v3.getActionType(), v3.getStack().copy(), v3.getModifiedStacks());
            EventBus.method3015(((Object)v4));
            if (!v4.method2297()) {
                this.field_3720.sendPacket((Packet)new ClickSlotC2SPacket(v4.method2315(), v4.method2319(), v4.method2320(), v4.method2317(), v4.method2313(), v4.method2323(), v4.method2314()));
            }
        }
    }
}



