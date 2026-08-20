/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket
 *  net.minecraft.network.packet.s2c.play.GameJoinS2CPacket
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package minced.mixin.network;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import minced.core.EventEntityUseItem;
import minced.core.EventGameJoin;
import minced.core.EventTotemPop;
import minced.core.EventNoRender;
import minced.core.Event;
import minced.core.EventBus;
import minced.core.EntityInterpolation;
import minced.core.ConnectionUtil;
import minced.core.Minced;
import minced.enums.fire;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ru.dreamix.fabricloader.VMBridge;

@Mixin(value={ClientPlayNetworkHandler.class})
public class ClientPlayNetworkHandlerMixin {
    @Shadow
    private ClientWorld field_3699;
    private static String field12666 = "field12666";

    @Inject(method={"sendChatMessage"}, at={@At(value="HEAD")}, cancellable=true)
    private void send(String param1, CallbackInfo param2) {
        if (this instanceof ConnectionUtil) {
            return;
        }
        if (param1.startsWith(Minced.getInstance().getCommandManager().method74())) {
            try {
                Minced.getInstance().getCommandManager().method72().execute(param1.substring(Minced.getInstance().getCommandManager().method74().length()), (Object)Minced.getInstance().getCommandManager().method70());
            } catch (CommandSyntaxException commandSyntaxException) {}
            param2.cancel();
        }
    }

    @Inject(method={"onEntityStatus"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/particle/ParticleManager;addEmitter(Lnet/minecraft/entity/Entity;Lnet/minecraft/particle/ParticleEffect;I)V")}, locals=LocalCapture.CAPTURE_FAILSOFT)
    private void onEntityStatus(EntityStatusS2CPacket param1, CallbackInfo param2, Entity param3, int param4) {
        if (this instanceof ConnectionUtil) {
            return;
        }
        EventTotemPop v5 = new EventTotemPop(param3);
        EventBus.method3015(((Object)v5));
    }

    @Inject(method={"onEntityEquipmentUpdate"}, at={@At(value="HEAD")})
    private void onEntityEquipmentUpdate(EntityEquipmentUpdateS2CPacket param1, CallbackInfo param22) {
        if (this.field_3699 == null) {
            return;
        }
        Entity v3 = this.field_3699.getEntityById(param1.getEntityId());
        if (v3 instanceof LivingEntity) {
            LivingEntity v4 = (LivingEntity)v3;
            param1.getEquipmentList().forEach(param2 -> {
                EquipmentSlot v3_99 = (EquipmentSlot)param2.getFirst();
                ItemStack v4_100 = (ItemStack)param2.getSecond();
                if (v3_99 == EquipmentSlot.MAINHAND) {
                    EventEntityUseItem.method2350(v4.getId(), v4_100.copy(), this.field_3699.getTime());
                } else if (v3_99 == EquipmentSlot.OFFHAND) {
                    EventEntityUseItem.method2352(v4.getId(), v4_100.copy(), this.field_3699.getTime());
                }
            });
        }
    }

    @Inject(method={"onEntityStatus"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/render/GameRenderer;showFloatingItem(Lnet/minecraft/item/ItemStack;)V")}, cancellable=true)
    private void onRender(EntityStatusS2CPacket param1, CallbackInfo param2) {
        if (this instanceof ConnectionUtil) {
            return;
        }
        EventNoRender v3 = new EventNoRender(fire.totem);
        EventBus.method3015(((Object)v3));
        if (v3.method2297()) {
            param2.cancel();
        }
    }

    @Inject(method={"onEntityPositionSync"}, at={@At(value="HEAD")})
    private void mincedbots$onEntityPositionSync(EntityPositionSyncS2CPacket param1, CallbackInfo param2) {
        ClientPlayNetworkHandler v3 = (ClientPlayNetworkHandler)(Object)this;
        if (v3 instanceof ConnectionUtil || v3.getWorld() == null) {
            return;
        }
        Entity v4 = v3.getWorld().getEntityById(param1.id());
        if (v4 instanceof LivingEntity && v4 instanceof EntityInterpolation) {
            EntityInterpolation v5 = (EntityInterpolation)v4;
            Vec3d v6 = param1.values().position();
            v5.setServerPos(v6.x, v6.y, v6.z);
        }
    }

    @Inject(method={"onEntity"}, at={@At(value="HEAD")})
    private void mincedbots$onEntity(EntityS2CPacket param1, CallbackInfo param2) {
        ClientPlayNetworkHandler v3 = (ClientPlayNetworkHandler)(Object)this;
        if (v3 instanceof ConnectionUtil || v3.getWorld() == null) {
            return;
        }
        Entity v4 = param1.getEntity((World)v3.getWorld());
        if (v4 instanceof LivingEntity && v4 instanceof EntityInterpolation) {
            EntityInterpolation v5 = (EntityInterpolation)v4;
            if (param1.isPositionChanged()) {
                Vec3d v6 = v4.getTrackedPosition().withDelta((long)param1.getDeltaX(), (long)param1.getDeltaY(), (long)param1.getDeltaZ());
                v5.setServerPos(v6.x, v6.y, v6.z);
            }
            return;
        }
    }

    @Inject(method={"onGameJoin"}, at={@At(value="TAIL")})
    private void onGameJoin(GameJoinS2CPacket param1, CallbackInfo param2) {
        String v3 = field12666;
        try {
            if (MinecraftClient.getInstance().getCurrentServerEntry() != null) {
                v3 = MinecraftClient.getInstance().getCurrentServerEntry().address;
            }
        } catch (Exception exception) {}
        EventGameJoin v4 = new EventGameJoin(v3);
        EventBus.method3015(((Object)v4));
    }

    static {
        VMBridge.identifyClass(ClientPlayNetworkHandlerMixin.class, "8l7yzhet");
    }
}



