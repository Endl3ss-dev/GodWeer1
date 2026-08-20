/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.client.network.ClientConfigurationNetworkHandler
 *  net.minecraft.network.ClientConnection
 *  net.minecraft.network.NetworkState
 *  net.minecraft.network.listener.PacketListener
 *  net.minecraft.registry.DynamicRegistryManager$Immutable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.network;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import minced.core.ConfigNetworkHandler;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientConfigurationNetworkHandler.class})
public class ClientConfigurationNetworkHandlerMixin {
    @WrapOperation(method={"onReady"}, at={@At(value="INVOKE", target="Lnet/minecraft/network/ClientConnection;transitionInbound(Lnet/minecraft/network/NetworkState;Lnet/minecraft/network/listener/PacketListener;)V")})
    private void mincedbots$useBotPlayListener(ClientConnection param1, NetworkState<?> param2, PacketListener param3, Operation<Void> param4, @Local DynamicRegistryManager.Immutable param5) {
        PacketListener v8 = null;
        if (this instanceof ConfigNetworkHandler v7) {
            v8 = v7.mincedbots$createPlayPacketListener(param5);
        }
        param4.call(new Object[]{param1, param2, v8 != null ? v8 : param3});
    }

    @Inject(method={"onReady"}, at={@At(value="TAIL")})
    private void mincedbots$afterReadyTransition(CallbackInfo param1) {
        if (this instanceof ConfigNetworkHandler v3) {
            v3.mincedbots$afterReadyTransition();
        }
    }
}



