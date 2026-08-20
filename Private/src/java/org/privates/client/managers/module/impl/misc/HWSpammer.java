package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.annotations.HolyWorld;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.managers.module.settings.impl.StringSetting;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Category2
@HolyWorld
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "HWSpammer", category = Category.MISC, description = "Спамит сообщением на хв вообще должен через него сделать ркт но мне лень")
public class HWSpammer extends Module {

    public static HWSpammer getInstance() {
        return Instance.get(HWSpammer.class);
    }

    private static final String MODE_SELECTION_TITLE = "выберите режим";
    private static final String ANARCHY_SELECTION_TITLE = "выбор лайт анархии";
    private static final String SOLO_LIGHT = "СолоЛайт";
    private static final String DUO_LIGHT = "ДуоЛайт";
    private static final String TRIO_LIGHT = "ТриоЛайт";
    private static final String CLAN_LIGHT = "КланЛайт";

    private final StopWatch timerUtil = new StopWatch();
    private final StopWatch messagesUtil = new StopWatch();
    private final Map<Integer, Integer> slots = new HashMap<>();

    private final BooleanSetting privateMessageMode = new BooleanSetting(this, "Private msg", false);
    private final BooleanSetting reconnectMode = new BooleanSetting(this, "Multi-Server", true);
    private final BooleanSetting antiAFKMode = new BooleanSetting(this, "AntiAFK", true);
    private final SliderSetting privateInterval = new SliderSetting(this, "Private Interval", 20, 0, 310, 1);
    private final SliderSetting forInterval = new SliderSetting(this, "Интервал цикла (мс)", 500, 50, 5000, 10);
    private final SliderSetting leaveInterval = new SliderSetting(this, "Интервал для выхода (мс)", 500, 50, 5000, 10);
    private final SliderSetting messagesInterval = new SliderSetting(this, "Кд меж сообщениями (мс)", 500, 50, 10000, 10);
    private final SliderSetting messagesCount = new SliderSetting(this, "Кол-во сообщений", 1, 1, 10, 1);
    private final SliderSetting startAnarchy = new SliderSetting(this, "Начальная анархия", 1, 1, 60, 1);
    private final SliderSetting stopAnarchy = new SliderSetting(this, "Конечная анархия", 1, 1, 60, 1);
    private final StringSetting spamMessage = new StringSetting(this, "Сообщение", "Всем привет!");

    private final String lastMessage = ".";
    private int ticksSinceLastMessage = 0;
    private int currentPlayerIndex = 0;
    private int currentServer = 1;
    private int currentMessages = 0;
    private int step = 0;

    public HWSpammer() {
        initializeSlots();
    }

    private void initializeSlots() {
        slots.clear();
        int start = 18;
        for (int i = 1; i <= 14; i++) {
            slots.put(i, start);
            start++;
        }
        start = 18;
        for (int i = 15; i <= 30; i++) {
            slots.put(i, start);
            start++;
        }
        start = 18;
        for (int i = 31; i <= 43; i++) {
            slots.put(i, start);
            start++;
        }
        start = 18;
        for (int i = 44; i <= 60; i++) {
            slots.put(i, start);
            start++;
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (antiAFKMode.getValue() && mc.player.isOnGround()) {
            mc.player.jump();
        }

        if (reconnectMode.getValue()) {
            runReconnectMode();
        } else if (privateMessageMode.getValue()) {
            runPrivateMessageMode();
        }
    }

    private void runPrivateMessageMode() {
        ticksSinceLastMessage++;
        if (ticksSinceLastMessage >= privateInterval.getValue()) {
            List<AbstractClientPlayerEntity> players = mc.world.getPlayers();

            if (players.isEmpty()) {
                print("Нет игроков для отправки сообщения.");
                resetPrivateMessageMode();
                return;
            }

            AbstractClientPlayerEntity targetPlayer = findNextPlayer(players);
            if (targetPlayer == null) {
                resetPrivateMessageMode();
                return;
            }

            sendPrivateMessage(targetPlayer);
            ticksSinceLastMessage = 0;
        }
    }

    private AbstractClientPlayerEntity findNextPlayer(List<AbstractClientPlayerEntity> players) {
        for (int i = 0; i < players.size(); i++) {
            AbstractClientPlayerEntity player = players.get(currentPlayerIndex);
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
        return null;
    }

    private void sendPrivateMessage(AbstractClientPlayerEntity targetPlayer) {
        String playerName = targetPlayer.getName().getString();
        String fullCommand = "/msg " + playerName + " " + spamMessage.getValue();
        mc.player.sendChatMessage(fullCommand);
    }

    private void resetPrivateMessageMode() {
        currentPlayerIndex = 0;
        step = 6;
    }

    private void runReconnectMode() {
        switch (step) {
            case 0 -> handleHubCommand();
            case 1 -> handleMenuCommand();
            case 2 -> handleModeSelection();
            case 3 -> handleAnarchySelection();
            case 4 -> handleSlotClick();
            case 5 -> handleSpamMessage();
            case 6 -> handleServerSwitch();
        }
    }

    private void handleHubCommand() {
        if (timerUtil.finished(leaveInterval.getValue().longValue())) {
            timerUtil.reset();
            mc.player.sendChatMessage("/hub");
            step = 1;
        }
    }

    private void handleMenuCommand() {
        if (timerUtil.finished(forInterval.getValue().longValue())) {
            timerUtil.reset();
            mc.player.sendChatMessage("/menu");
            step = 2;
        }
    }

    private void handleModeSelection() {
        if (timerUtil.finished(forInterval.getValue().longValue())) {
            timerUtil.reset();
            if (!hasChange(MODE_SELECTION_TITLE)) {
                step = 1;
                return;
            }
            mc.playerController.windowClick(mc.player.openContainer.windowId, 12, 0, ClickType.PICKUP, mc.player);
            step = 3;
        }
    }

    private void handleAnarchySelection() {
        if (timerUtil.finished(forInterval.getValue().longValue())) {
            timerUtil.reset();
            if (!hasChange(ANARCHY_SELECTION_TITLE)) {
                step = 1;
                return;
            }
            String mode = getModeForServer(currentServer);
            if (mode == null) {
                currentServer = startAnarchy.getValue().intValue();
                step = 1;
                return;
            }
            mc.playerController.windowClick(mc.player.openContainer.windowId, findSlotByName(mode), 0, ClickType.PICKUP, mc.player);
            step = 4;
        }
    }

    private void handleSlotClick() {
        if (timerUtil.finished(forInterval.getValue().longValue())) {
            timerUtil.reset();
            if (!hasChange(ANARCHY_SELECTION_TITLE)) {
                step = 1;
                return;
            }
            if (slots.containsKey(currentServer)) {
                mc.playerController.windowClick(mc.player.openContainer.windowId, slots.get(currentServer), 0, ClickType.PICKUP, mc.player);
            }
            step = 5;
        }
    }

    private void handleSpamMessage() {
        if (privateMessageMode.getValue()) {
            runPrivateMessageMode();
            return;
        }
        if (timerUtil.finished(forInterval.getValue().longValue())) {
            if (messagesUtil.finished(messagesInterval.getValue().longValue())) {
                if (currentMessages < messagesCount.getValue().intValue()) {
                    currentMessages++;
                    sendMessage();
                    if (currentMessages >= messagesCount.getValue().intValue()) {
                        timerUtil.reset();
                        currentMessages = 0;
                        step = 6;
                    }
                } else {
                    timerUtil.reset();
                    currentMessages = 0;
                    step = 6;
                }
            }
        }
    }

    private void sendMessage() {
        String msg = spamMessage.getValue();
        if (!msg.startsWith("!")) msg = "!" + msg;
        mc.player.sendChatMessage(msg);
    }

    private void handleServerSwitch() {
        if (timerUtil.finished(leaveInterval.getValue().longValue())) {
            timerUtil.reset();
            currentServer++;
            if (currentServer > stopAnarchy.getValue().intValue()) {
                currentServer = startAnarchy.getValue().intValue();
                spamMessage.set("Всем привет!");
            }
            step = 0;
        }
    }

    private String getModeForServer(int server) {
        if (server >= 1 && server <= 14) return SOLO_LIGHT;
        if (server >= 15 && server <= 30) return DUO_LIGHT;
        if (server >= 31 && server <= 43) return TRIO_LIGHT;
        if (server >= 44 && server <= 60) return CLAN_LIGHT;
        return null;
    }

    private boolean hasChange(String title) {
        if (mc.currentScreen instanceof ChestScreen chestScreen) {
            return chestScreen.getTitle().getString().toLowerCase().contains(title);
        }
        return false;
    }

    private int findSlotByName(String name) {
        for (int i = 0; i < mc.player.openContainer.getInventory().size(); i++) {
            ItemStack stack = mc.player.openContainer.getInventory().get(i);
            if (stack != null && stack.getDisplayName().getString().contains(name)) {
                return i;
            }
        }
        return -1;
    }

    private void print(String message) {
        if (mc.ingameGUI != null && mc.ingameGUI.getChatGUI() != null) {
            mc.ingameGUI.getChatGUI().printChatMessage(
                    new net.minecraft.util.text.StringTextComponent("§b[HWSpammer]§r " + message)
            );
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        currentServer = startAnarchy.getValue().intValue();
        step = 0;
        currentMessages = 0;
        ticksSinceLastMessage = 0;
        currentPlayerIndex = 0;
        timerUtil.reset();
        messagesUtil.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        step = 0;
        currentMessages = 0;
        ticksSinceLastMessage = 0;
        currentPlayerIndex = 0;
    }
}