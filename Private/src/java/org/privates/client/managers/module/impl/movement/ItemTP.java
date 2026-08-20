package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.annotations.PVE;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

import java.util.*;

@Category3
@PVE
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ItemTP", category = Category.MOVEMENT, description = "Телепортирует вас к предмету")
public class ItemTP extends Module {

    public static ItemTP getInstance() {
        return Instance.get(ItemTP.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Default", "wellmine", "LonyGrief", "CakeWorld", "SpookyTime");

    private final SliderSetting searchRadius = new SliderSetting(this, "Радиус поиска", 100.0f, 10.0f, 200.0f, 10.0f);

    private final SliderSetting tpDelay = new SliderSetting(this, "Задержка ТП (сек)", 30.0f, 10.0f, 180.0f, 1.0f)
            .setVisible(() -> mode.is("wellmine"));

    private final BooleanSetting rotateToPlayer = new BooleanSetting(this, "Ротация на игрока", false);

    private final BooleanSetting rotateOnLowHp = new BooleanSetting(this, "Только при низком ХП", false)
            .setVisible(rotateToPlayer::getValue);

    private final SliderSetting rotateHpThreshold = new SliderSetting(this, "Порог ХП", 8.0f, 1.0f, 20.0f, 1.0f)
            .setVisible(() -> rotateToPlayer.getValue() && rotateOnLowHp.getValue());

    private final MultiItemSetting targetItemsSetting = new MultiItemSetting(
            this,
            "Целевые предметы",
            new ArrayList<>(Arrays.asList(
                    Items.GOLDEN_APPLE,
                    Items.NETHERITE_SWORD,
                    Items.NETHERITE_CHESTPLATE,
                    Items.NETHERITE_HELMET,
                    Items.NETHERITE_BOOTS,
                    Items.NETHERITE_LEGGINGS,
                    Items.DIAMOND_CHESTPLATE,
                    Items.DIAMOND_HELMET,
                    Items.DIAMOND_BOOTS,
                    Items.DIAMOND_LEGGINGS,
                    Items.NETHERITE_PICKAXE,
                    Items.DIAMOND_PICKAXE,
                    Items.ELYTRA,
                    Items.ENCHANTED_GOLDEN_APPLE,
                    Items.PLAYER_HEAD,
                    Items.TOTEM_OF_UNDYING,
                    Items.SPLASH_POTION,
                    Items.PHANTOM_MEMBRANE,
                    Items.CONDUIT,
                    Items.SHULKER_BOX
            ))
    );

    private final ModeSetting leaveMethodDefault = new ModeSetting(this, "Способ выхода", "/hub", "/home home", "Никуда")
            .setVisible(() -> mode.is("Default"));

    private final ModeSetting accountRole = new ModeSetting(this, "Роль", "Получатель", "Отправитель")
            .setVisible(() -> mode.is("wellmine"));

    private final StringSetting receiverNick = new StringSetting(this, "Ник получателя", "")
            .setVisible(() -> mode.is("wellmine") && accountRole.is("Отправитель"));

    private final ModeSetting leaveMethod = new ModeSetting(this, "Способ выхода", "/hub", "/home home", ".vclip 100", "Никуда")
            .setVisible(() -> mode.is("wellmine") || mode.is("LonyGrief") || mode.is("CakeWorld") || mode.is("SpookyTime"));

    private final StopWatch tpaTimer = new StopWatch();

    private boolean tpaSent = false;
    private boolean teleportedToItem = false;
    private int previousInventoryCount = 0;
    private boolean waitingForItems = false;
    private boolean hasTeleported = false;
    private Vector3d lastItemPosition = null;

    @Override
    public void onEnable() {
        super.onEnable();
        tpaTimer.reset();
        tpaSent = false;
        teleportedToItem = false;
        waitingForItems = false;
        hasTeleported = false;
        lastItemPosition = null;

        if (mc.player != null) {
            previousInventoryCount = countTargetItemsInInventory();

            if (mode.is("wellmine") && accountRole.is("Отправитель")) {
                String nick = receiverNick.getValue();
                if (nick != null && !nick.isEmpty()) {
                    mc.player.sendChatMessage("/tpa " + nick);
                    tpaSent = true;
                    waitingForItems = true;
                    tpaTimer.reset();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        tpaSent = false;
        teleportedToItem = false;
        waitingForItems = false;
        hasTeleported = false;
        lastItemPosition = null;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        handleRotation();

        if (mode.is("Default")) {
            handleDefaultMode();
        } else if (mode.is("wellmine")) {
            handleWellmineMode();
        } else if (mode.is("LonyGrief")) {
            handleLonyGriefMode();
        } else if (mode.is("CakeWorld")) {
            handleCakeWorldMode();
        } else if (mode.is("SpookyTime")) {
            handleSpookyTimeMode();
        }
    }

    private void handleRotation() {
        if (!rotateToPlayer.getValue()) return;

        if (rotateOnLowHp.getValue()) {
            if (mc.player.getHealth() >= rotateHpThreshold.getValue()) return;
        }

        PlayerEntity nearestPlayer = findNearestPlayer();
        if (nearestPlayer != null) {
            float[] rotations = calculateRotations(nearestPlayer);
            RotationComponent.update(new Rotation(rotations[0], rotations[1]), 360, 360, 0, 5);
        }
    }

    private PlayerEntity findNearestPlayer() {
        PlayerEntity nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            double distance = mc.player.getDistanceSq(player);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = player;
            }
        }

        return nearest;
    }

    private float[] calculateRotations(PlayerEntity target) {
        double deltaX = target.getPosX() - mc.player.getPosX();
        double deltaY = (target.getPosY() + target.getEyeHeight()) - (mc.player.getPosY() + mc.player.getEyeHeight());
        double deltaZ = target.getPosZ() - mc.player.getPosZ();

        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, distance));

        return new float[]{yaw, MathHelper.clamp(pitch, -90.0f, 90.0f)};
    }

    private void handleDefaultMode() {
        if (!hasTeleported) {
            Optional<ItemEntity> nearestItem = findNearestTargetItem();
            if (nearestItem.isPresent()) {
                ItemEntity item = nearestItem.get();
                Vector3d itemPos = item.getPositionVec();
                teleport(itemPos.getX(), itemPos.getY(), itemPos.getZ());
                hasTeleported = true;
                previousInventoryCount = countTargetItemsInInventory();
            }
        } else {
            int currentCount = countTargetItemsInInventory();
            if (currentCount > previousInventoryCount) {
                sendLeaveCommand(leaveMethodDefault.getValue());
                toggle();
            }
        }
    }

    private void handleWellmineMode() {
        if (accountRole.is("Получатель")) {
            handleReceiverRole();
        } else {
            handleSenderRole();
        }
    }

    private void handleReceiverRole() {
        if (!teleportedToItem) {
            Optional<ItemEntity> nearestItem = findNearestTargetItem();
            if (nearestItem.isPresent()) {
                ItemEntity item = nearestItem.get();
                Vector3d itemPos = item.getPositionVec();
                teleport(itemPos.getX(), itemPos.getY(), itemPos.getZ());
                teleportedToItem = true;
                previousInventoryCount = countTargetItemsInInventory();
                mc.player.sendChatMessage("/tpaccept");
            }
        } else {
            int currentCount = countTargetItemsInInventory();
            if (currentCount > previousInventoryCount) {
                sendLeaveCommand(leaveMethod.getValue());
                toggle();
            }
        }
    }

    private void handleSenderRole() {
        String nick = receiverNick.getValue();
        if (nick == null || nick.isEmpty()) {
            return;
        }

        if (tpaTimer.finished((long) (tpDelay.getValue() * 1000))) {
            mc.player.sendChatMessage("/tpa " + nick);
            tpaTimer.reset();
        }

        if (waitingForItems) {
            int currentCount = countTargetItemsInInventory();
            if (currentCount > previousInventoryCount) {
                sendLeaveCommand(leaveMethod.getValue());
                waitingForItems = false;
                toggle();
            }
        }
    }

    private void handleLonyGriefMode() {
        if (!hasTeleported) {
            Optional<ItemEntity> nearestItem = findNearestTargetItem();
            if (nearestItem.isPresent()) {
                ItemEntity item = nearestItem.get();
                Vector3d itemPos = item.getPositionVec();
                teleport(itemPos.getX(), itemPos.getY(), itemPos.getZ());
                hasTeleported = true;
                previousInventoryCount = countTargetItemsInInventory();
            }
        } else {
            int currentCount = countTargetItemsInInventory();
            if (currentCount > previousInventoryCount) {
                sendLeaveCommand(leaveMethod.getValue());
                toggle();
            }
        }
    }

    private void handleCakeWorldMode() {
        if (!hasTeleported) {
            Optional<ItemEntity> nearestItem = findNearestTargetItem();
            if (nearestItem.isPresent()) {
                ItemEntity item = nearestItem.get();
                Vector3d itemPos = item.getPositionVec();
                teleportOnlyY(itemPos.getY());
                hasTeleported = true;
                previousInventoryCount = countTargetItemsInInventory();
            }
        } else {
            int currentCount = countTargetItemsInInventory();
            if (currentCount > previousInventoryCount) {
                sendLeaveCommand(leaveMethod.getValue());
                toggle();
            }
        }
    }

    private void handleSpookyTimeMode() {
        if (!hasTeleported) {
            Optional<ItemEntity> nearestItem = findNearestTargetItem();
            if (nearestItem.isPresent()) {
                ItemEntity item = nearestItem.get();
                Vector3d itemPos = item.getPositionVec();
                teleportOnlyY(itemPos.getY());
                hasTeleported = true;
                previousInventoryCount = countTargetItemsInInventory();
            }
        } else {
            int currentCount = countTargetItemsInInventory();
            if (currentCount > previousInventoryCount) {
                sendLeaveCommand(leaveMethod.getValue());
                toggle();
            }
        }
    }

    private void sendLeaveCommand(String method) {
        if (method.equals("Никуда")) return;
        if (method.equals("/hub")) {
            ChatUtil.sendText("/hub");
        } else if (method.equals("/home home")) {
            ChatUtil.sendText("/home home");
        } else if (method.equals(".vclip 100")) {
            ChatUtil.sendText(".vclip 100");

        }
    }

    private int countTargetItemsInInventory() {
        if (mc.player == null) return 0;

        int count = 0;
        List<Item> targetItems = targetItemsSetting.getValue();

        for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && targetItems.contains(stack.getItem())) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private void teleport(double x, double y, double z) {
        if (mc.player == null || mc.player.connection == null) return;

        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y, z, true));
        mc.player.setPosition(x, y, z);
    }

    private void teleportOnlyY(double targetY) {
        if (mc.player == null || mc.player.connection == null) return;

        double currentX = mc.player.getPosX();
        double currentZ = mc.player.getPosZ();
        double currentY = mc.player.getPosY();

        double step = 10.0;
        double direction = targetY > currentY ? 1.0 : -1.0;
        double distance = Math.abs(targetY - currentY);

        int steps = (int) Math.ceil(distance / step);

        for (int i = 1; i <= steps; i++) {
            double nextY;
            if (i == steps) {
                nextY = targetY;
            } else {
                nextY = currentY + (direction * step * i);
            }
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(currentX, nextY, currentZ, true));
        }

        mc.player.setPosition(currentX, targetY, currentZ);
    }

    private Optional<ItemEntity> findNearestTargetItem() {
        if (mc.world == null || mc.player == null) return Optional.empty();

        List<Item> targetItems = targetItemsSetting.getValue();

        return mc.world.getEntitiesWithinAABB(
                        ItemEntity.class,
                        mc.player.getBoundingBox().grow(searchRadius.getValue()),
                        entity -> entity != null &&
                                entity.getItem() != null &&
                                targetItems.contains(entity.getItem().getItem())
                ).stream()
                .filter(Objects::nonNull)
                .min(Comparator.comparingDouble(entity -> entity.getDistanceSq(mc.player)));
    }

    public SliderSetting getSearchRadius() {
        return searchRadius;
    }
}