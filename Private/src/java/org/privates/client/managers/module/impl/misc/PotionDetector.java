package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.screen.notification.Notification;
import org.privates.client.screen.notification.NotificationType;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;

import java.util.*;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "PotionDetector", category = Category.PLAYER, description = "Отслеживает брошенные зелья и уведомляет о попаданиях")
public class PotionDetector extends Module {

    public static PotionDetector getInstance() {
        return Instance.get(PotionDetector.class);
    }

    private final SliderSetting radius = new SliderSetting(this, "Радиус", 50.0f, 10.0f, 100.0f, 1.0f);
    private final BooleanSetting showHitChance = new BooleanSetting(this, "Показывать шанс попадания", true);
    private final BooleanSetting showEffects = new BooleanSetting(this, "Показывать эффекты", true);
    private final BooleanSetting useNotifications = new BooleanSetting(this, "Использовать уведомления", false);

    private final Map<Integer, PotionData> trackedPotions = new HashMap<>();
    private final Map<String, Set<String>> playerNotifiedPotions = new HashMap<>();

    private final LinkedList<Long> recentNotifications = new LinkedList<>();
    private static final int MAX_NOTIFICATIONS_PER_SECOND = 5;
    private static final long ONE_SECOND_MS = 1000;

    @Override
    public void onEnable() {
        super.onEnable();
        trackedPotions.clear();
        playerNotifiedPotions.clear();
        recentNotifications.clear();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        trackedPotions.clear();
        playerNotifiedPotions.clear();
        recentNotifications.clear();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.world == null || mc.player == null) return;

        cleanupOldNotificationTimestamps();

        Set<Integer> currentPotions = new HashSet<>();

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof PotionEntity potionEntity) {
                int entityId = entity.getEntityId();
                double myDistance = mc.player.getDistance(entity);

                if (myDistance > radius.getValue()) continue;

                currentPotions.add(entityId);

                if (!trackedPotions.containsKey(entityId)) {
                    ItemStack potionStack = potionEntity.getItem();
                    trackedPotions.put(entityId, new PotionData(
                            potionStack.copy(),
                            entity.getPosX(),
                            entity.getPosY(),
                            entity.getPosZ()
                    ));
                } else {
                    PotionData data = trackedPotions.get(entityId);
                    data.lastX = entity.getPosX();
                    data.lastY = entity.getPosY();
                    data.lastZ = entity.getPosZ();
                }
            }
        }

        Set<Integer> removedPotions = new HashSet<>(trackedPotions.keySet());
        removedPotions.removeAll(currentPotions);

        for (int entityId : removedPotions) {
            PotionData data = trackedPotions.get(entityId);
            AxisAlignedBB potionBB = new AxisAlignedBB(
                    data.lastX - 4.0,
                    data.lastY - 2.0,
                    data.lastZ - 4.0,
                    data.lastX + 4.0,
                    data.lastY + 2.0,
                    data.lastZ + 4.0
            );

            for (LivingEntity hitEntity : mc.world.getEntitiesWithinAABB(LivingEntity.class, potionBB)) {
                if (!(hitEntity instanceof PlayerEntity player)) continue;

                double dx = player.getPosX() - data.lastX;
                double dz = player.getPosZ() - data.lastZ;
                double distance = Math.sqrt(dx * dx + dz * dz);

                if (distance > 4.0) continue;

                ItemStack potionStack = data.stack;
                String potionKey = getPotionKey(potionStack);
                String playerName = player.getName().getString();

                if (wasAlreadyNotified(playerName, potionKey)) {
                    continue;
                }

                if (!canSendNotification()) {
                    continue;
                }

                List<EffectInstance> effects = PotionUtils.getEffectsFromStack(potionStack);
                double proximity = Math.max(0.0, 1.0 - distance / 4.0);
                double hitChance = proximity * 100.0;

                int potionColorInt = PotionUtils.getColor(potionStack);
                TextFormatting potionColor = getColorFromInt(potionColorInt);

                String potionName = potionStack.getDisplayName().getString();

                String message = TextFormatting.WHITE + playerName +
                        TextFormatting.GRAY + " получил " +
                        potionColor + potionName;

                if (useNotifications.getValue()) {
                    Excellent.inst().notifManager().add(
                            new Notification(NotificationType.INFO, message, 3000, potionStack.copy())
                    );
                } else {
                    ChatUtil.addText(message);
                }

                addNotificationTimestamp();

                if (showHitChance.getValue()) {
                    String hitMessage = TextFormatting.DARK_GRAY + "• " +
                            TextFormatting.WHITE + "Успешность: " +
                            TextFormatting.GREEN + String.format("%.0f%%", hitChance);
                    ChatUtil.addText(hitMessage);
                }

                if (showEffects.getValue() && !effects.isEmpty()) {
                    for (EffectInstance effect : effects) {
                        String effectName = effect.getPotion().getDisplayName().getString();
                        int amplifier = effect.getAmplifier() + 1;
                        int duration = effect.getDuration() / 20;
                        int adjustedDuration = (int) (duration * (hitChance / 100.0));
                        int minutes = adjustedDuration / 60;
                        int seconds = adjustedDuration % 60;

                        String effectMessage = TextFormatting.DARK_GRAY + "• " +
                                TextFormatting.RED + effectName +
                                TextFormatting.RED + " " + toRoman(amplifier) +
                                TextFormatting.GRAY + " (" + minutes + ":" +
                                String.format("%02d", seconds) + ")";
                        ChatUtil.addText(effectMessage);
                    }
                }

                markAsNotified(playerName, potionKey);
            }

            trackedPotions.remove(entityId);
        }

        cleanupOldPlayerData();
    }

    private String getPotionKey(ItemStack potionStack) {
        Potion potion = PotionUtils.getPotionFromItem(potionStack);
        String potionId = Registry.POTION.getKey(potion).toString();

        List<EffectInstance> effects = PotionUtils.getEffectsFromStack(potionStack);
        StringBuilder effectKey = new StringBuilder(potionId);

        for (EffectInstance effect : effects) {
            effectKey.append("_")
                    .append(Registry.EFFECTS.getKey(effect.getPotion()).toString())
                    .append("_")
                    .append(effect.getAmplifier());
        }

        return effectKey.toString();
    }

    private boolean wasAlreadyNotified(String playerName, String potionKey) {
        Set<String> notifiedPotions = playerNotifiedPotions.get(playerName);
        if (notifiedPotions == null) {
            return false;
        }
        return notifiedPotions.contains(potionKey);
    }

    private void markAsNotified(String playerName, String potionKey) {
        playerNotifiedPotions.computeIfAbsent(playerName, k -> new HashSet<>()).add(potionKey);
    }

    private boolean canSendNotification() {
        long currentTime = System.currentTimeMillis();

        long oneSecondAgo = currentTime - ONE_SECOND_MS;
        int recentCount = 0;
        for (Long timestamp : recentNotifications) {
            if (timestamp > oneSecondAgo) {
                recentCount++;
            }
        }

        return recentCount < MAX_NOTIFICATIONS_PER_SECOND;
    }

    private void addNotificationTimestamp() {
        recentNotifications.add(System.currentTimeMillis());
    }

    private void cleanupOldNotificationTimestamps() {
        long currentTime = System.currentTimeMillis();
        long oneSecondAgo = currentTime - ONE_SECOND_MS;

        recentNotifications.removeIf(timestamp -> timestamp <= oneSecondAgo);
    }

    private void cleanupOldPlayerData() {
        if (mc.world == null) return;

        Set<String> activePlayers = new HashSet<>();
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player != null) {
                activePlayers.add(player.getName().getString());
            }
        }

        playerNotifiedPotions.entrySet().removeIf(entry -> !activePlayers.contains(entry.getKey()));

        for (Map.Entry<String, Set<String>> entry : playerNotifiedPotions.entrySet()) {
            if (entry.getValue().size() > 50) {
                entry.getValue().clear();
            }
        }
    }

    private TextFormatting getColorFromInt(int color) {
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        if (red > green && red > blue) {
            return TextFormatting.RED;
        } else if (green > red && green > blue) {
            return TextFormatting.GREEN;
        } else if (blue > red && blue > green) {
            return TextFormatting.BLUE;
        } else if (red > 240 && green > 100 && blue < 100) {
            return TextFormatting.GOLD;
        } else if (red > 174 && green < 100 && blue > 174) {
            return TextFormatting.LIGHT_PURPLE;
        } else if (red < 100 && green > 174 && blue > 174) {
            return TextFormatting.AQUA;
        }
        return TextFormatting.WHITE;
    }

    private String toRoman(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(number);
        };
    }

    private static class PotionData {
        ItemStack stack;
        double lastX;
        double lastY;
        double lastZ;

        PotionData(ItemStack stack, double x, double y, double z) {
            this.stack = stack;
            this.lastX = x;
            this.lastY = y;
            this.lastZ = z;
        }
    }
}