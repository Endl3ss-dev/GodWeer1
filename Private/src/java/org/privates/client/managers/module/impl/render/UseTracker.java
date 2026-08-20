package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.screen.notification.Notification;
import org.privates.client.screen.notification.NotificationType;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;

import java.util.HashMap;
import java.util.Map;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "UseTracker", category = Category.PLAYER, description = "Отслеживает использование предметов игроками")
public class UseTracker extends Module {

    public static UseTracker getInstance() {
        return Instance.get(UseTracker.class);
    }

    private final BooleanSetting ignoreFriends = new BooleanSetting(this, "Игнорировать друзей", true);
    private final BooleanSetting showSelf = new BooleanSetting(this, "Показывать свои действия", true);
    private final BooleanSetting useNotifications = new BooleanSetting(this, "Уведомления", false);

    private ItemStack lastActiveItem = ItemStack.EMPTY;
    private boolean wasHandActive = false;
    private int lastUseMaxCount = 0;
    private Hand lastActiveHand = null;

    private final Map<String, PlayerHandState> playerHandStates = new HashMap<>();

    @Override
    public void onEnable() {
        super.onEnable();
        playerHandStates.clear();
        lastActiveItem = ItemStack.EMPTY;
        wasHandActive = false;
        lastUseMaxCount = 0;
        lastActiveHand = null;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        playerHandStates.clear();
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (!event.isReceive()) return;

        IPacket<?> packet = event.getPacket();
        if (packet instanceof SEntityStatusPacket status) {
            if (status.getOpCode() == 35) {
                if (mc.world == null) return;

                PlayerEntity player = (PlayerEntity) status.getEntity(mc.world);
                if (player == null) return;

                if (player == mc.player && !showSelf.getValue()) return;

                String name = player.getName().getString();
                if (ignoreFriends.getValue() && isFriend(name)) return;

                ItemStack totemStack = getTotemStack(player);
                sendPopMessage(name, totemStack, player == mc.player);
            }
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        trackSelfUsage();
        trackOtherPlayersUsage();
        cleanupOfflinePlayers();
    }

    private void trackSelfUsage() {
        boolean isHandActive = mc.player.isHandActive();
        ItemStack activeItem = mc.player.getActiveItemStack();
        Hand activeHand = mc.player.getActiveHand();

        if (isHandActive && !activeItem.isEmpty()) {
            UseAction useAction = activeItem.getUseAction();
            if (useAction == UseAction.EAT || useAction == UseAction.DRINK) {
                if (!wasHandActive || lastActiveHand != activeHand) {
                    lastActiveItem = activeItem.copy();
                    lastActiveHand = activeHand;
                    lastUseMaxCount = 0;
                }

                int currentUseMaxCount = mc.player.getItemInUseMaxCount();
                if (currentUseMaxCount > lastUseMaxCount) {
                    lastUseMaxCount = currentUseMaxCount;
                }
            }
        }

        if (wasHandActive && !isHandActive) {
            if (!lastActiveItem.isEmpty()) {
                UseAction useAction = lastActiveItem.getUseAction();
                if (useAction == UseAction.EAT || useAction == UseAction.DRINK) {
                    ItemStack currentItemInHand = ItemStack.EMPTY;
                    if (lastActiveHand == Hand.MAIN_HAND) {
                        currentItemInHand = mc.player.getHeldItemMainhand();
                    } else if (lastActiveHand == Hand.OFF_HAND) {
                        currentItemInHand = mc.player.getHeldItemOffhand();
                    }

                    boolean wasUsed = checkIfItemWasUsed(lastActiveItem, currentItemInHand);

                    if (wasUsed) {
                        String name = mc.player.getName().getString();
                        if (showSelf.getValue() && (!ignoreFriends.getValue() || !isFriend(name))) {
                            sendEatMessage(name, lastActiveItem, true);
                        }
                    }
                }
            }

            lastActiveItem = ItemStack.EMPTY;
            lastUseMaxCount = 0;
            lastActiveHand = null;
        }

        wasHandActive = isHandActive;
    }

    private void trackOtherPlayersUsage() {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == null || player == mc.player) continue;

            String playerName = player.getName().getString();
            if (ignoreFriends.getValue() && isFriend(playerName)) continue;

            ItemStack currentMainHand = player.getHeldItemMainhand();
            ItemStack currentOffHand = player.getHeldItemOffhand();

            PlayerHandState lastState = playerHandStates.get(playerName);
            if (lastState == null) {
                lastState = new PlayerHandState();
                lastState.mainHand = currentMainHand.isEmpty() ? ItemStack.EMPTY : currentMainHand.copy();
                lastState.offHand = currentOffHand.isEmpty() ? ItemStack.EMPTY : currentOffHand.copy();
                playerHandStates.put(playerName, lastState);
                continue;
            }

            trackHandUsage(playerName, currentMainHand, lastState, true);
            trackHandUsage(playerName, currentOffHand, lastState, false);

            lastState.mainHand = currentMainHand.isEmpty() ? ItemStack.EMPTY : currentMainHand.copy();
            lastState.offHand = currentOffHand.isEmpty() ? ItemStack.EMPTY : currentOffHand.copy();
        }
    }

    private void trackHandUsage(String playerName, ItemStack currentHand, PlayerHandState state, boolean isMainHand) {
        ItemStack lastHand = isMainHand ? state.mainHand : state.offHand;
        int useTicks = isMainHand ? state.mainHandUseTicks : state.offHandUseTicks;
        boolean wasUsing = isMainHand ? state.mainHandWasUsing : state.offHandWasUsing;

        boolean isUsing = !currentHand.isEmpty() &&
                (currentHand.getUseAction() == UseAction.EAT || currentHand.getUseAction() == UseAction.DRINK) &&
                ItemStack.areItemsEqual(lastHand, currentHand) &&
                currentHand.getCount() == lastHand.getCount();

        if (isUsing) {
            if (isMainHand) {
                state.mainHandUseTicks++;
                state.mainHandWasUsing = true;
            } else {
                state.offHandUseTicks++;
                state.offHandWasUsing = true;
            }
        } else {
            if (wasUsing && useTicks >= 10 && !lastHand.isEmpty()) {
                UseAction useAction = lastHand.getUseAction();
                if (useAction == UseAction.EAT || useAction == UseAction.DRINK) {
                    ItemStack usedItem = lastHand.copy();
                    boolean wasUsed = checkIfItemWasUsed(lastHand, currentHand);

                    if (wasUsed) {
                        sendEatMessage(playerName, usedItem, false);
                    }
                }
            }

            if (isMainHand) {
                state.mainHandUseTicks = 0;
                state.mainHandWasUsing = false;
            } else {
                state.offHandUseTicks = 0;
                state.offHandWasUsing = false;
            }
        }
    }

    private boolean checkIfItemWasUsed(ItemStack lastItem, ItemStack currentItem) {
        if (currentItem.isEmpty()) {
            return true;
        } else if (lastItem.getItem() != currentItem.getItem()) {
            if (lastItem.getItem() == Items.MILK_BUCKET && currentItem.getItem() == Items.BUCKET) {
                return true;
            } else return lastItem.getItem() == Items.HONEY_BOTTLE && currentItem.getItem() == Items.GLASS_BOTTLE;
        } else {
            return currentItem.getCount() < lastItem.getCount();
        }
    }

    private void cleanupOfflinePlayers() {
        playerHandStates.entrySet().removeIf(entry -> {
            String playerName = entry.getKey();
            return mc.world.getPlayers().stream()
                    .noneMatch(p -> p != null && p.getName().getString().equals(playerName));
        });
    }

    private ItemStack getTotemStack(PlayerEntity player) {
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();

        if (mainHand.getItem() == Items.TOTEM_OF_UNDYING) {
            return mainHand;
        } else if (offHand.getItem() == Items.TOTEM_OF_UNDYING) {
            return offHand;
        }
        return ItemStack.EMPTY;
    }

    private void sendPopMessage(String playerName, ItemStack totemStack, boolean isSelf) {
        boolean isEnchanted = totemStack != null && !totemStack.isEmpty() && totemStack.isEnchanted();
        String enchantmentSymbol = isEnchanted ?
                TextFormatting.GREEN + "✔" :
                TextFormatting.RED + "✘";
        TextFormatting nameColor = isSelf ? TextFormatting.YELLOW : TextFormatting.RED;
        String selfPrefix = isSelf ? TextFormatting.YELLOW + "[СЕБЯ] " : "";

        String message;
        if (totemStack != null && !totemStack.isEmpty()) {
            String totemName = totemStack.getDisplayName().getString();
            message = selfPrefix + enchantmentSymbol + " " + nameColor + playerName +
                    TextFormatting.WHITE + " попнул " + TextFormatting.GOLD + totemName +
                    TextFormatting.WHITE + "!";
        } else {
            String defaultName = "Неизвестный тотем";
            message = selfPrefix + enchantmentSymbol + " " + nameColor + playerName +
                    TextFormatting.WHITE + " попнул " + TextFormatting.GOLD + defaultName +
                    TextFormatting.WHITE + "!";
        }

        if (useNotifications.getValue()) {
            Excellent.inst().notifManager().add(
                    new Notification(NotificationType.INFO, message, 3000, totemStack.copy())
            );
        } else {
            ChatUtil.addText(message);
        }
    }

    private void sendEatMessage(String playerName, ItemStack itemStack, boolean isSelf) {
        TextFormatting nameColor = isSelf ? TextFormatting.YELLOW : TextFormatting.RED;
        String selfPrefix = isSelf ? TextFormatting.YELLOW + "[СЕБЯ] " : "";
        String action = itemStack.getUseAction() == UseAction.EAT ? "съел" : "выпил";
        String itemName = itemStack.getDisplayName().getString();

        String message = selfPrefix + nameColor + playerName + TextFormatting.WHITE + " " +
                action + " " + TextFormatting.GOLD + itemName + TextFormatting.WHITE + "!";

        if (useNotifications.getValue()) {
            Excellent.inst().notifManager().add(
                    new Notification(NotificationType.INFO, message, 3000, itemStack.copy())
            );
        } else {
            ChatUtil.addText(message);
        }
    }

    private boolean isFriend(String playerName) {
        return Excellent.inst().friendManager().isFriend(playerName);
    }

    private static class PlayerHandState {
        ItemStack mainHand = ItemStack.EMPTY;
        ItemStack offHand = ItemStack.EMPTY;
        int mainHandUseTicks = 0;
        int offHandUseTicks = 0;
        boolean mainHandWasUsing = false;
        boolean offHandWasUsing = false;
    }
}