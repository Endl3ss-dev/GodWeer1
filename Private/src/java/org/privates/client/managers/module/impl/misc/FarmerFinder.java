package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.annotations.HolyWorld;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Category1
@HolyWorld
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "FarmerFinder", category = Category.MISC, description = "находит фармеры на holyworld через /invsee + currenttargetname")
public class FarmerFinder extends Module {
    private static final Minecraft mc = Minecraft.getInstance();
    private final List<String> originalPlayerNames = new ArrayList<>();
    private final List<String> currentPlayerNames = new ArrayList<>();
    private ScheduledExecutorService scheduler;
    private final StopWatch timerUtil = new StopWatch();
    private final StopWatch lobbyWaitTimer = new StopWatch();

    private boolean isWaitingAfterHub = false;

    @Override
    public void toggle() {
        super.toggle();
        getTabPlayers();

        if (originalPlayerNames.isEmpty()) {
            sendChatMessage(TextFormatting.RED + "В табе нет других игроков.");
            return;
        }

        currentPlayerNames.clear();
        currentPlayerNames.addAll(originalPlayerNames);

        sendChatMessage(TextFormatting.GREEN + "Начинаю проверку " + currentPlayerNames.size() + " игроков...");
        startChecking();
    }

    @Override
    public void onDisable() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        originalPlayerNames.clear();
        currentPlayerNames.clear();
    }

    private void getTabPlayers() {
        originalPlayerNames.clear();
        if (mc.world == null || mc.player == null) return;

        for (NetworkPlayerInfo info : mc.player.connection.getPlayerInfoMap()) {
            if (info != null && info.getGameProfile() != null) {
                String name = info.getGameProfile().getName();
                if (!name.equals(mc.player.getGameProfile().getName())) {
                    originalPlayerNames.add(name);
                }
            }
        }
    }

    private void startChecking() {
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            if (mc.player == null || mc.world == null) {
                scheduler.shutdownNow();
                return;
            }

            if (isWaitingAfterHub) {
                if (lobbyWaitTimer.finished(5000)) {
                    isWaitingAfterHub = false;
                    lobbyWaitTimer.reset();
                } else {
                    return;
                }
            }

            if (isLobbyServer()) {
                mc.player.sendChatMessage("/hub");
                isWaitingAfterHub = true;
                lobbyWaitTimer.reset();
                return;
            }

            if (currentPlayerNames.isEmpty()) {
                currentPlayerNames.addAll(originalPlayerNames);
                sendChatMessage(TextFormatting.GREEN + "Начинаю новый цикл проверки " + currentPlayerNames.size() + " игроков...");
                return;
            }

            String playerName = currentPlayerNames.remove(0);
            sendChatMessage(TextFormatting.YELLOW + "Проверяю инвентарь игрока: " + TextFormatting.GOLD + playerName);
            mc.player.sendChatMessage("/invsee " + playerName);

            scheduler.schedule(() -> {
                if (mc.player == null || mc.world == null || !(mc.currentScreen instanceof InventoryScreen || mc.currentScreen instanceof ChestScreen)) {
                    return;
                }
                boolean foundFarmerSword = false;
                if (mc.currentScreen instanceof InventoryScreen invScreen) {
                    foundFarmerSword = checkForFarmerSword(invScreen.getContainer().inventorySlots);
                } else if (mc.currentScreen instanceof ChestScreen chestScreen) {
                    foundFarmerSword = checkForFarmerSword(chestScreen.getContainer().inventorySlots);
                }
                if (foundFarmerSword) {
                    String alertMsg = TextFormatting.DARK_RED + "" + TextFormatting.BOLD + "НАЙДЕН МЕЧ ФАРМЕРА: "
                            + TextFormatting.GOLD + playerName.toUpperCase();
                    sendChatMessage(alertMsg);
                }
            }, 1, TimeUnit.SECONDS);
        }, 0, 1, TimeUnit.SECONDS);
    }

    private boolean isLobbyServer() {
        if (mc.player == null || mc.world == null) return false;

        String[] lobbyIndicators = {"лобби", "lobby", "hub", "хаб"};

        try {
            String serverIP = mc.getCurrentServerData() != null ?
                    mc.getCurrentServerData().serverIP.toLowerCase() : "";

            String playerName = mc.player.getName().getString().toLowerCase();

            for (String indicator : lobbyIndicators) {
                if (serverIP.contains(indicator) ||
                        playerName.contains(indicator)) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    private boolean checkForFarmerSword(List<Slot> slots) {
        String[] farmerLevels = {"Фармер II", "Фармер III", "Фармер IV", "Фармер V",
                "Фармер VI", "Фармер VII", "Фармер VIII", "Фармер IX", "Фармер X"};
        for (Slot slot : slots) {
            ItemStack stack = slot.getStack();
            if (!stack.isEmpty() && stack.getItem() == Items.NETHERITE_SWORD) {
                CompoundNBT tag = stack.getTag();
                if (tag != null && tag.contains("display", 10)) {
                    CompoundNBT displayTag = tag.getCompound("display");
                    if (displayTag.contains("Lore", 9)) {
                        ListNBT loreList = displayTag.getList("Lore", 8);
                        for (int i = 0; i < loreList.size(); i++) {
                            String loreLine = loreList.getString(i).toLowerCase();
                            for (String level : farmerLevels) {
                                if (loreLine.contains(level.toLowerCase())) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                String itemName = stack.getDisplayName().getString().toLowerCase();
                for (String level : farmerLevels) {
                    if (itemName.contains(level.toLowerCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void sendChatMessage(String message) {
        if (mc.player != null) {
            mc.player.sendMessage(new StringTextComponent(message), UUID.randomUUID());
        }
    }

    public static FarmerFinder getInstance() {
        return Instance.get(FarmerFinder.class);
    }
}