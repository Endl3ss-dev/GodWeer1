/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay
 *  net.minecraft.client.network.ClientPlayerEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package minced.mixin.screen;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import minced.core.ChatCommandHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={StatusEffectsDisplay.class})
public abstract class StatusEffectsDisplayMixin {
    @WrapOperation(method={"drawStatusEffects(Lnet/minecraft/client/gui/DrawContext;II)V"}, at={@At(value="FIELD", target="Lnet/minecraft/client/MinecraftClient;player:Lnet/minecraft/client/network/ClientPlayerEntity;")}, require=0)
    private ClientPlayerEntity mincedbots$useActiveStatusEffectsPlayer(MinecraftClient param1, Operation<ClientPlayerEntity> param2) {
        ClientPlayerEntity v3 = ChatCommandHandler.method385(param1);
        return v3 != null ? v3 : (ClientPlayerEntity)param2.call(new Object[]{param1});
    }
}



