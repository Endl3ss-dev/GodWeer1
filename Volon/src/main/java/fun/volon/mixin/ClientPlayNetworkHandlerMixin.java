package fun.volon.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fun.volon.Volon;
import fun.volon.api.events.implement.EventPacket;
import fun.volon.api.storages.implement.helpertstorages.enumvar.ModuleClass;
// import fun.volon.api.utils.baritone.BaritoneAntiStuck;
import fun.volon.api.utils.bot.BotSessionManager;
import fun.volon.api.utils.chat.ChatUtils;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Shadow
    private ClientWorld world;

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void sendChatMessage(@NotNull String message, CallbackInfo ci) {
        // Обработка команд Baritone с префиксом +baritone
        if (message.startsWith("+baritone ")) {
            // Если Unhook активен, проверяем разрешение на использование Baritone
            if (fun.volon.client.modules.impl.misc.Unhook.isUnhooked()) {
                if (!fun.volon.client.modules.impl.misc.Unhook.isBaritoneAllowed()) {
                    ci.cancel();
                    return;
                }
            }
            
            // Конвертируем +baritone команду в обычную # команду для Baritone
            String baritoneCommand = "#" + message.substring(10); // убираем "+baritone " и добавляем "#"
            
            // Отправляем команду напрямую в чат для обработки Baritone
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null && mc.player.networkHandler != null) {
                mc.player.networkHandler.sendChatMessage(baritoneCommand);
            }
            ci.cancel();
            return;
        }
        
        // Блокируем обычные команды Baritone с #
        if (message.startsWith("#")) {
            ci.cancel();
            return;
        }
        
        // Блокируем команды клиента при unhook
        if (fun.volon.client.modules.impl.misc.Unhook.isUnhooked()) {
            // Пропускаем vanilla команды и чат, блокируем только команды клиента
            if (message.startsWith(".")) {
                ci.cancel();
                return;
            }
        }
        
        if (Volon.INSTANCE == null || Volon.INSTANCE.commandStorage == null) return;
        if (message.startsWith(Volon.INSTANCE.commandStorage.getPrefix())) {
            // Блокируем команды клиента при unhook
            if (fun.volon.client.modules.impl.misc.Unhook.isUnhooked()) {
                ci.cancel();
                return;
            }
            
            try {
                Volon.INSTANCE.commandStorage.getDispatcher().execute(message.substring(Volon.INSTANCE.commandStorage.getPrefix().length()), Volon.INSTANCE.commandStorage.getSource());
            } catch (CommandSyntaxException e) {
                ChatUtils.sendMessage(Formatting.RED + "Ошибка в использовании!");
            }
            ci.cancel();
            return;
        }
    }

    @Inject(method = "onEntityVelocityUpdate", at = @At("HEAD"), cancellable = true)
    private void onVelocityUpdate(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        EventPacket event = new EventPacket(packet, EventPacket.Type.RECEIVE);
        event.call();
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "onExplosion", at = @At("HEAD"), cancellable = true)
    private void onExplosion(ExplosionS2CPacket packet, CallbackInfo ci) {
        EventPacket event = new EventPacket(packet, EventPacket.Type.RECEIVE);
        event.call();
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "onEntityPositionSync", at = @At("HEAD"), cancellable = true)
    private void onEntityPositionSync(EntityPositionSyncS2CPacket packet, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (world == null || mc.player == null || mc.world == null) {
            ci.cancel();
        }
    }

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        // BaritoneAntiStuck.onGameMessage(packet.content().getString());
    }

    @Inject(method = "onGameJoin", at = @At("HEAD"))
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        BotSessionManager.finishBotConnectStage();
    }
}
