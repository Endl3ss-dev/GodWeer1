package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.PlayerUtil;
import org.privates.common.impl.taskript.Script;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoLeave", category = Category.MISC, description = "Ливает если на вас напали опы")
public class AutoLeave extends Module {

    public static AutoLeave getInstance() {
        return Instance.get(AutoLeave.class);
    }

    final ModeSetting mode = new ModeSetting(this, "Режим", "Обычный", "Умный");
    final ModeSetting action = new ModeSetting(this, "Действие", "Kick", "/hub", "/spawn", "/home");
    final SliderSetting distance = new SliderSetting(this, "Расстояние", 50.0f, 1.0f, 100.0f, 1.0f);
    final SliderSetting leaveTime = new SliderSetting(this, "Время выхода (сек)", 30.0f, 5.0f, 300.0f, 1.0f);

    final Script script = new Script();
    boolean reconnecting = false;

    private final Pattern banMessagePattern = Pattern.compile("\\[♨] (\\S+) забанен \\[Подробнее\\]", 256);

    @EventHandler
    private void onEvent(UpdateEvent event) {
        if (mode.getValue().equals("Обычный") && !reconnecting) {
            mc.world.getPlayers().stream()
                    .filter(this::isValidPlayer)
                    .findFirst()
                    .ifPresent(this::performAction);
        }
    }

    @EventHandler
    private void onReceivePacket(PacketEvent event) {
        if (!mode.getValue().equals("Умный") || reconnecting) return;

        IPacket<?> packet = event.getPacket();
        if (packet instanceof SChatPacket packet1) {
            String message = packet1.getChatComponent().getString();
            Matcher matcher = banMessagePattern.matcher(message);
            if (matcher.find()) {
                String bannedPlayer = matcher.group(1);
                performSmartLeave(bannedPlayer);
            }
        }
    }

    @EventHandler
    private void onRender2D(Render2DEvent event) {
        if (reconnecting) {
            script.update();
        }
    }

    private boolean isValidPlayer(PlayerEntity player) {
        return player.isAlive()
                && player.getHealth() > 0.0f
                && player.getDistance(mc.player) <= distance.getValue()
                && player != mc.player
                && !Excellent.inst().friendManager().isFriend(TextFormatting.removeFormatting(player.getGameProfile().getName()));
    }

    private void performAction(PlayerEntity player) {
        if (!action.getValue().equalsIgnoreCase("Kick")) {
            mc.player.sendChatMessage(action.getValue());
            mc.ingameGUI.func_238452_a_(new StringTextComponent("[AutoLeave] " + player.getGameProfile().getName()),
                    new StringTextComponent("test"), -1, -1, -1);
        } else {
            mc.player.connection.getNetworkManager().closeChannel(new StringTextComponent("Вы вышли с сервера! \n" + player.getGameProfile().getName()));
        }
        toggle();
    }

    private void performSmartLeave(String bannedPlayer) {
        if (PlayerUtil.isPvp()) return;

        int server = PlayerUtil.getAnarchy();
        if (server == -1) return;

        reconnecting = true;
        ChatUtil.sendText("/hub");

        script.cleanup()
                .addStep((int) (leaveTime.getValue() * 1000), () -> {
                    if (mc.ingameGUI.overlayPlayerList.header != null &&
                            mc.ingameGUI.overlayPlayerList.header.getString().contains("Режим: Хаб #")) {
                        ChatUtil.sendText("/an" + server);
                    }
                })
                .addStep((int) (leaveTime.getValue() * 1000) + 2000, () -> {
                    for (int i = 0; i < 10; i++) {
                        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(
                                mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), mc.player.isOnGround()));
                    }
                    reconnecting = false;
                    toggle();
                });

        System.out.println(leaveTime.getValue() * 1000 + " - время ожидания перед /an" + server);
    }
}