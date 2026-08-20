package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;

import java.util.Comparator;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "SexAura", category = Category.COMBAT, description = "Автоматически приседает на ближайшем игроке")
public class SexAura extends Module {
    public static SexAura getInstance() {
        return Instance.get(SexAura.class);
    }

    // Настройки
    private final SliderSetting maxDistance = new SliderSetting(this, "Макс. дистанция", 10, 3, 15, 0.5f);
    private final SliderSetting approachDistance = new SliderSetting(this, "Дистанция подхода", 0.5f, 0.3f, 2, 0.1f);
    private final SliderSetting sneakDelay = new SliderSetting(this, "Задержка приседания", 70, 50, 200, 10);
    private final BooleanSetting lookAtBody = new BooleanSetting(this, "Смотреть на туловище", true);
    private final BooleanSetting autoMove = new BooleanSetting(this, "Авто подход", true);
    private final BooleanSetting onlyPlayers = new BooleanSetting(this, "Только игроки", true);

    private PlayerEntity target;
    private long lastSneakTime = 0;
    private boolean isSneaking = false;

    @Override
    public void onEnable() {
        target = null;
        lastSneakTime = 0;
        isSneaking = false;
    }

    @Override
    public void onDisable() {
        if (mc.gameSettings != null) {
            mc.gameSettings.keyBindSneak.setPressed(false);
            releaseMovementKeys();
        }
        target = null;
        isSneaking = false;
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (mc.player == null || mc.world == null) return;

        // Проверяем валидность текущей цели
        if (!isValidTarget(target)) {
            findClosestTarget();
        }

        // Если есть цель, выполняем действия
        if (target != null) {
            handleTarget();
        } else {
            // Отпускаем клавиши если нет цели
            if (mc.gameSettings != null) {
                mc.gameSettings.keyBindSneak.setPressed(false);
                releaseMovementKeys();
            }
            isSneaking = false;
        }
    }

    private boolean isValidTarget(PlayerEntity player) {
        if (player == null || player == mc.player) return false;
        if (!player.isAlive()) return false;
        return !(player.getDistance(mc.player) > maxDistance.getValue());
    }

    private void findClosestTarget() {
        target = mc.world.getPlayers().stream()
                .filter(player -> player != mc.player)
                .filter(PlayerEntity::isAlive)
                .filter(player -> player.getDistance(mc.player) <= maxDistance.getValue())
                .min(Comparator.comparingDouble(player -> player.getDistance(mc.player)))
                .orElse(null);
    }

    private void handleTarget() {
        double distance = mc.player.getDistance(target);

        // Наводимся на цель если включено
        if (lookAtBody.getValue()) {
            lookAtTarget();
        }

        // Подходим к цели если включен авто подход
        if (autoMove.getValue() && distance > approachDistance.getValue()) {
            moveTowardsTarget();
        } else {
            releaseMovementKeys();
        }

        // Приседаем с заданным интервалом
        if (distance <= approachDistance.getValue()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastSneakTime >= sneakDelay.getValue()) {
                if (isSneaking) {
                    mc.gameSettings.keyBindSneak.setPressed(false);
                    isSneaking = false;
                } else {
                    mc.gameSettings.keyBindSneak.setPressed(true);
                    isSneaking = true;
                }
                lastSneakTime = currentTime;
            }
        } else {
            mc.gameSettings.keyBindSneak.setPressed(false);
            isSneaking = false;
        }
    }

    private void lookAtTarget() {
        double deltaX = target.getPosX() - mc.player.getPosX();
        double deltaY = target.getPosY() + target.getHeight() / 2 - mc.player.getPosYEye();
        double deltaZ = target.getPosZ() - mc.player.getPosZ();

        double distanceXZ = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) (MathHelper.atan2(deltaZ, deltaX) * (180 / Math.PI)) - 90;
        float pitch = (float) -(MathHelper.atan2(deltaY, distanceXZ) * (180 / Math.PI));

        // Плавное вращение (опционально)
        float yawDiff = yaw - mc.player.rotationYaw;
        float pitchDiff = pitch - mc.player.rotationPitch;

        // Нормализация углов
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;

        mc.player.rotationYaw += yawDiff * 0.3f; // Плавность поворота
        mc.player.rotationPitch += pitchDiff * 0.3f;

        // Ограничение pitch
        mc.player.rotationPitch = MathHelper.clamp(mc.player.rotationPitch, -90, 90);
    }

    private void moveTowardsTarget() {
        mc.gameSettings.keyBindForward.setPressed(true);

        // Опционально: стрейфы для обхода препятствий
        double angle = Math.atan2(target.getPosZ() - mc.player.getPosZ(),
                target.getPosX() - mc.player.getPosX());
        double playerYaw = Math.toRadians(mc.player.rotationYaw + 90);
        double diff = angle - playerYaw;

        while (diff > Math.PI) diff -= 2 * Math.PI;
        while (diff < -Math.PI) diff += 2 * Math.PI;

        if (Math.abs(diff) > Math.PI / 4) {
            mc.gameSettings.keyBindLeft.setPressed(diff > 0);
            mc.gameSettings.keyBindRight.setPressed(diff < 0);
        } else {
            mc.gameSettings.keyBindLeft.setPressed(false);
            mc.gameSettings.keyBindRight.setPressed(false);
        }
    }

    private void releaseMovementKeys() {
        if (mc.gameSettings != null) {
            mc.gameSettings.keyBindForward.setPressed(false);
            mc.gameSettings.keyBindBack.setPressed(false);
            mc.gameSettings.keyBindLeft.setPressed(false);
            mc.gameSettings.keyBindRight.setPressed(false);
        }
    }
}