package org.privates.client.managers.module.impl.misc;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChatPacket;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.annotations.Rage;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
@Category2
@Rage
@ModuleInfo(name = "DuelAssist", category = Category.MISC, description = "Авто кидает дуели на серверах из mode настройки")
public class DuelAssist extends Module {

    private static final Pattern pattern = Pattern.compile("^\\w{3,16}$");

    private final ModeSetting gameMode = new ModeSetting(this, "Сервер", "FunTime", "ReallyWorld", "FunSky");

    private final ModeSetting Armor = new ModeSetting(this, "Броня соперника", "Незеритовая", "Незеритовая", "Алмазная");

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Шары",
            "Шары", "Щит", "Шипы 3", "Незеритка", "Читерский рай", "Лук", "Классик", "Тотемы", "Нодебафф");

    private double lastPosX, lastPosY, lastPosZ;

    private final List<String> sent = Lists.newArrayList();
    private int currentPlayerIndex = 0;

    private final StopWatch counter = new StopWatch();
    private final StopWatch counter2 = new StopWatch();
    private final StopWatch counterChoice = new StopWatch();
    private final StopWatch counterTo = new StopWatch();

    public DuelAssist() {
        Armor.setVisible(() -> gameMode.is("FunTime"));
        mode.setVisible(() -> gameMode.is("ReallyWorld"));
    }

    @EventHandler
    private void OnUpdate(UpdateEvent e) {
        final List<String> players = getOnlinePlayers();

        double distance = Math.sqrt(Math.pow(lastPosX - mc.player.getPosX(), 2) +
                Math.pow(lastPosY - mc.player.getPosY(), 2) +
                Math.pow(lastPosZ - mc.player.getPosZ(), 2));

        if (distance > 500) {
            toggle();
        }

        lastPosX = mc.player.getPosX();
        lastPosY = mc.player.getPosY();
        lastPosZ = mc.player.getPosZ();

        if (counter2.finished(800L * players.size())) {
            sent.clear();
            currentPlayerIndex = 0;
            counter2.reset();
        }

        if (gameMode.is("ReallyWorld")) {
            if (!players.isEmpty()) {
                if (counter.finished(1000)) {
                    if (currentPlayerIndex >= players.size()) {
                        currentPlayerIndex = 0;
                    }

                    String player = players.get(currentPlayerIndex);
                    if (!sent.contains(player) && !player.equals(mc.session.getProfile().getName())) {
                        mc.player.sendChatMessage("/duel " + player);
                        sent.add(player);
                    }
                    currentPlayerIndex++;
                    counter.reset();
                }

                if (mc.player.openContainer instanceof ChestContainer chest) {
                    if (mc.currentScreen.getTitle().getString().contains("Выбор набора (1/1)")) {
                        if (counterChoice.finished(150)) {
                            if (mode.is("Щит")) {
                                mc.playerController.windowClick(chest.windowId, 0, 0, ClickType.QUICK_MOVE, mc.player);
                            } else if (mode.is("Шипы 3")) {
                                mc.playerController.windowClick(chest.windowId, 1, 0, ClickType.QUICK_MOVE, mc.player);
                            } else if (mode.is("Лук")) {
                                mc.playerController.windowClick(chest.windowId, 2, 0, ClickType.QUICK_MOVE, mc.player);
                            } else if (mode.is("Тотемы")) {
                                mc.playerController.windowClick(chest.windowId, 3, 0, ClickType.QUICK_MOVE, mc.player);
                            } else if (mode.is("Нодебафф")) {
                                mc.playerController.windowClick(chest.windowId, 4, 0, ClickType.QUICK_MOVE, mc.player);
                            } else if (mode.is("Шары")) {
                                mc.playerController.windowClick(chest.windowId, 5, 0, ClickType.QUICK_MOVE, mc.player);
                            } else if (mode.is("Классик")) {
                                mc.playerController.windowClick(chest.windowId, 6, 0, ClickType.QUICK_MOVE, mc.player);
                            } else if (mode.is("Читерский рай")) {
                                mc.playerController.windowClick(chest.windowId, 7, 0, ClickType.QUICK_MOVE, mc.player);
                            } else if (mode.is("Незеритка")) {
                                mc.playerController.windowClick(chest.windowId, 8, 0, ClickType.QUICK_MOVE, mc.player);
                            }
                            counterChoice.reset();
                        }
                    } else if (mc.currentScreen.getTitle().getString().contains("Настройка поединка")) {
                        if (counterTo.finished(150)) {
                            mc.playerController.windowClick(chest.windowId, 0, 0, ClickType.QUICK_MOVE, mc.player);
                            counterTo.reset();
                        }
                    }
                }
            }
        } else if (gameMode.is("FunTime")) {
            final List<String> filteredPlayers = getOnlinePlayersWithArmorFilter();
            for (final String player : filteredPlayers) {
                if (!sent.contains(player) && !player.equals(mc.session.getProfile().getName())) {
                    if (counter.finished(1000)) {
                        mc.player.sendChatMessage("/duel invite " + player);
                        sent.add(player);
                        counter.reset();
                    }
                }
            }
        } else if (gameMode.is("FunSky")) {
            final List<String> filteredPlayers = getOnlinePlayersWithArmorFilter();
            for (final String player : filteredPlayers) {
                if (!sent.contains(player) && !player.equals(mc.session.getProfile().getName())) {
                    if (counter.finished(1000)) {
                        mc.player.sendChatMessage("/duel " + player);
                        sent.add(player);
                        counter.reset();
                    }
                }
            }
        }
    }

    @EventHandler
    private void onPacket(PacketEvent event) {
        if (event.isReceive()) {
            IPacket<?> packet = event.getPacket();

            if (packet instanceof SChatPacket chat) {
                final String text = chat.getChatComponent().getString().toLowerCase();
                if ((text.contains("начало") && text.contains("через") && text.contains("секунд!")) ||
                        (text.equals(""))) {
                    toggle();
                }
            }
        }
    }

    private List<String> getOnlinePlayers() {
        return mc.player.connection.getPlayerInfoMap().stream()
                .map(NetworkPlayerInfo::getGameProfile)
                .map(GameProfile::getName)
                .filter(profileName -> pattern.matcher(profileName).matches())
                .collect(Collectors.toList());
    }

    private List<String> getOnlinePlayersWithArmorFilter() {
        List<String> onlinePlayers = getOnlinePlayers();
        List<String> filteredPlayers = new ArrayList<>();

        for (PlayerEntity player : mc.world.getPlayers()) {
            String playerName = player.getGameProfile().getName();
            if (!onlinePlayers.contains(playerName) || playerName.equals(mc.session.getProfile().getName())) {
                continue;
            }

            double distance = mc.player.getDistance(player);
            if (distance > 250) {
                continue;
            }

            boolean matchesArmor = false;
            if (Armor.is("Незеритовая")) {
                matchesArmor = hasNetheriteArmor(player);
            } else if (Armor.is("Алмазная")) {
                matchesArmor = hasDiamondArmor(player);
            }

            if (matchesArmor) {
                filteredPlayers.add(playerName);
            }
        }

        return filteredPlayers;
    }

    private boolean hasNetheriteArmor(PlayerEntity player) {
        for (EquipmentSlotType slot : new EquipmentSlotType[]{EquipmentSlotType.CHEST, EquipmentSlotType.LEGS,
                EquipmentSlotType.HEAD, EquipmentSlotType.FEET}) {
            ItemStack armor = player.getItemStackFromSlot(slot);
            if (armor.getItem() instanceof ArmorItem armorItem) {
                if (armorItem.getArmorMaterial() == net.minecraft.item.ArmorMaterial.NETHERITE) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasDiamondArmor(PlayerEntity player) {
        for (EquipmentSlotType slot : new EquipmentSlotType[]{EquipmentSlotType.CHEST, EquipmentSlotType.LEGS,
                EquipmentSlotType.HEAD, EquipmentSlotType.FEET}) {
            ItemStack armor = player.getItemStackFromSlot(slot);
            if (armor.getItem() instanceof ArmorItem armorItem) {
                if (armorItem.getArmorMaterial() == net.minecraft.item.ArmorMaterial.DIAMOND) {
                    return true;
                }
            }
        }
        return false;
    }
}