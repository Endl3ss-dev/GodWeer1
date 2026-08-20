package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ModerLeave", category = Category.MISC)
public class ModerLeave extends Module {
    private static final List<String> ignoredPlayers = new ArrayList<>();
    private static final long HUB_COMMAND_DELAY = 6000L;
    private long lastLiteCommandTime = 0L;

    private final SliderSetting liteCooldown = new SliderSetting(this, "Задержка /lite", 10, 1, 60, 1);
    private final BooleanSetting checkPrefix = new BooleanSetting(this, "Проверка префиксов", true);
    private final BooleanSetting checkNames = new BooleanSetting(this, "Проверка имен", true);

    private final Pattern prefixMatches = Pattern.compile(".*(mod|der|adm|help|wne|хелп|адм|поддержка|кура|own|taf|curat|dev|supp|yt|сотруд|Адм|осн|миум).*");

    public ModerLeave() {
        initIgnoredPlayers();
    }

    private void initIgnoredPlayers() {
        String[] players = {
                "funtik1371xAKA3", "ToplecCHlKA", "klemen", "That0neBear",
                "XiSh3n", "hikomori", "Bupsik", "ApTa_He_UmBa", "gromaforz"
        };

        Collections.addAll(ignoredPlayers, players);
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.world == null || mc.player == null || mc.getConnection() == null) {
            return;
        }

        if (checkNames.getValue()) {
            for (NetworkPlayerInfo playerInfo : mc.getConnection().getPlayerInfoMap()) {
                String playerName = playerInfo.getGameProfile().getName();
                if (ignoredPlayers.contains(playerName)) {
                    executeLeave(playerName);
                    return;
                }
            }
        }

        if (checkPrefix.getValue() && mc.world.getScoreboard() != null) {
            for (ScorePlayerTeam team : mc.world.getScoreboard().getTeams()) {
                String prefix = team.getPrefix().getString();

                if (prefixMatches.matcher(prefix.toLowerCase(Locale.ROOT)).matches()) {
                    for (String playerName : team.getMembershipCollection()) {
                        if (!playerName.equals(mc.player.getName().getString())) {
                            boolean isOnline = mc.getConnection().getPlayerInfoMap().stream()
                                    .anyMatch(info -> info.getGameProfile().getName().equals(playerName));

                            if (isOnline) {
                                executeLeave(playerName + " (Префикс: " + prefix + ")");
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private void executeLeave(String playerName) {
        mc.player.sendChatMessage("/hub");

        ChatUtil.addText(TextFormatting.GRAY + "PrivateBuy | " +
                TextFormatting.RED + "Вы ливнули от " + playerName);

        new Thread(() -> {
            try {
                Thread.sleep(HUB_COMMAND_DELAY);
                processRightClick();
                Thread.sleep(2000L);
                clickSlot(12);
                Thread.sleep(2000L);
                clickSlot(2);
                Thread.sleep(2000L);
                int[] slots = {18, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};
                int randomSlot = slots[(int) (Math.random() * slots.length)];
                clickSlot(randomSlot);
                Thread.sleep(2000L);

                long currentTime = System.currentTimeMillis();
                if (currentTime - lastLiteCommandTime >= liteCooldown.getValue() * 1000) {
                    mc.player.sendChatMessage("/lite");
                    lastLiteCommandTime = currentTime;

                    ChatUtil.addText(TextFormatting.GRAY + "PrivateBuy | " +
                            TextFormatting.GREEN + "ПОЛУЧАЕМ ПИЗДЫ ОТ АЛАНОВЫ");
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void processRightClick() {
        mc.playerController.processRightClick(mc.player, mc.world, Hand.MAIN_HAND);
    }

    private void clickSlot(int slot) {
        mc.playerController.windowClick(
                mc.player.openContainer.windowId,
                slot,
                0,
                ClickType.PICKUP,
                mc.player
        );
    }
}