package org.privates.client.managers.module.impl.misc;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.annotations.Funtime;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.StringSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Category2
@Funtime
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ClanInvestor", description = "Автоматически вкладывает в клан выбранное количество монет, при достижении определённого баланса", category = Category.MISC)
public class ClanInvestor extends Module {
    public static ClanInvestor getInstance() {
        return Instance.get(ClanInvestor.class);
    }

    private final StringSetting investAmount = new StringSetting(this, "Закидывать", "10000", true);
    private final StringSetting minBalanceToInvest = new StringSetting(this, "Оставить Балансс", "100000", true);

    private int lastInvestTime = 0;
    private int checkTimer = 0;
    private int playerBalance = 0;
    private boolean waitingForBalanceResponse = false;
    private long lastBalanceRequestTime = 0;

    @Override
    protected void onEnable() {
        super.onEnable();
        lastInvestTime = 0;
        checkTimer = 0;
        playerBalance = 0;
        ChatUtil.addText("В ваш клан будет вложено: " + investAmount.getValue() + " монет");
    }

    private List<String> getSidebarText() {
        List<String> lines = new ArrayList<>();

        if (mc.world == null) return lines;

        Scoreboard scoreboard = mc.world.getScoreboard();
        if (scoreboard == null) return lines;

        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
        if (objective == null) return lines;

        Collection<Score> scores = scoreboard.getSortedScores(objective);

        for (Score score : scores) {
            String playerName = score.getPlayerName();
            if (playerName != null && !playerName.startsWith("#")) {
                String scoreText = getScoreDisplayForString(playerName);
                if (!scoreText.isEmpty()) {
                    lines.add(scoreText);
                }
            }
        }

        return lines;
    }

    private String getScoreDisplayForString(String name) {
        return name.replaceAll("§[0-9a-fk-or]", "");
    }

    public void onChatMessage(String message) {
        if (!waitingForBalanceResponse) return;

        String cleanMessage = stripColorCodes(message);

        if (cleanMessage.contains("баланс") || cleanMessage.contains("balance") || cleanMessage.contains("$")) {
            try {
                Pattern currencyPattern = Pattern.compile("\\$\\s*([\\d,\\s]+(?:\\.\\d+)?)");
                Matcher currencyMatcher = currencyPattern.matcher(cleanMessage);

                if (currencyMatcher.find()) {
                    String fullValue = currencyMatcher.group(1);
                    String cleanValue = fullValue.replaceAll("[,\\s]", "");

                    if (cleanValue.contains(".")) {
                        String integerPart = cleanValue.split("\\.")[0];
                        playerBalance = Integer.parseInt(integerPart);
                    } else {
                        playerBalance = Integer.parseInt(cleanValue);
                    }

                    waitingForBalanceResponse = false;
                    return;
                } else if (cleanMessage.matches(".*\\d+.*")) {
                    String digitsOnly = cleanMessage.replaceAll("[^0-9]", "");
                    playerBalance = Integer.parseInt(digitsOnly);
                    waitingForBalanceResponse = false;
                    return;
                }
            } catch (Exception e) {
                ChatUtil.addText("Ошибка: " + e.getMessage());
            }

            waitingForBalanceResponse = false;
        }
    }

    private String stripColorCodes(String text) {
        if (text == null) return "";
        return text.replaceAll("§[0-9a-fk-or]", "");
    }

    private void sendInvestCommand() {
        if (mc.player == null) return;

        String amountStr = investAmount.getValue();
        int amount;

        try {
            int minBalance = 0;
            try {
                minBalance = Integer.parseInt(minBalanceToInvest.getValue());
            } catch (NumberFormatException e) {
            }

            if (playerBalance < minBalance) {
                return;
            }

            amount = Integer.parseInt(amountStr);

            ChatUtil.sendText("/clan invest " + amount);
            ChatUtil.addText("В ваш клан вложено " + amount + " монет");
            lastInvestTime = (int) (System.currentTimeMillis() / 1000);
        } catch (NumberFormatException e) {
        }
    }

    private void requestPlayerBalance() {
        if (mc.player == null) return;

        if (waitingForBalanceResponse) {
            if (System.currentTimeMillis() - lastBalanceRequestTime > 10000) {
                waitingForBalanceResponse = false;
            } else {
                return;
            }
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBalanceRequestTime < 2000) {
            return;
        }

        ChatUtil.sendText("/bal");
        waitingForBalanceResponse = true;
        lastBalanceRequestTime = currentTime;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        int currentTime = (int) (System.currentTimeMillis() / 1000);

        if (currentTime % 5 == 0 && checkTimer != currentTime) {
            checkTimer = currentTime;

            if (!waitingForBalanceResponse && currentTime - lastInvestTime >= 5) {
                requestPlayerBalance();
            }
        }
    }

    @EventHandler
    public void onEvent(PacketEvent e) {

        if (!e.isSend() && e.getPacket() instanceof SChatPacket packet && waitingForBalanceResponse) {
            String message = packet.getChatComponent().getString();
            onChatMessage(message);

            if (!waitingForBalanceResponse) {
                int currentTime = (int) (System.currentTimeMillis() / 1000);
                if (currentTime - lastInvestTime >= 5) {
                    sendInvestCommand();
                }
            }
        }
    }
}