package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.JumpEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.other.ViaUtil;
import org.privates.lib.util.time.StopWatch;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Velocity", category = Category.MOVEMENT)
public class Velocity extends Module {

    public static Velocity getInstance() {
        return Instance.get(Velocity.class);
    }

    // Основные настройки
    private final ModeSetting mode = new ModeSetting(this, "Режим",
            "Grim", "Grim Light", "Grim Heavy", "Grim Stable", "Grim Advanced",
            "Matrix", "Vulcan", "Intave", "Verus", "NCP", "AAC", "Spartan",
            "Vanilla", "Custom", "Zero", "Reduce", "Jump Reset");

    // Настройки для разных режимов
    private final SliderSetting horizontal = new SliderSetting(this, "Горизонтальный %", 0f, 0f, 100f, 1f)
            .setVisible(() -> mode.is("Custom") || mode.is("Reduce"));

    private final SliderSetting vertical = new SliderSetting(this, "Вертикальный %", 0f, 0f, 100f, 1f)
            .setVisible(() -> mode.is("Custom") || mode.is("Reduce"));

    private final SliderSetting chance = new SliderSetting(this, "Шанс срабатывания %", 100f, 0f, 100f, 1f)
            .setVisible(() -> mode.is("Grim Advanced") || mode.is("Matrix"));

    // Grim специфичные настройки
    private final SliderSetting grimDelay = new SliderSetting(this, "Задержка (мс)", 150, 50, 500, 10)
            .setVisible(() -> mode.getValue().contains("Grim"));

    private final SliderSetting grimReduction = new SliderSetting(this, "Уменьшение силы", 0.8f, 0.1f, 1.0f, 0.05f)
            .setVisible(() -> mode.getValue().contains("Grim"));

    private final BooleanSetting onlyGround = new BooleanSetting(this, "Только на земле", false)
            .setVisible(() -> mode.is("Grim Light") || mode.is("Grim Stable"));

    private final BooleanSetting explosions = new BooleanSetting(this, "Блокировать взрывы", true);
    private final BooleanSetting debug = new BooleanSetting(this, "Отладка", false);

    // Внутренние переменные
    private final StopWatch velocityTimer = new StopWatch();
    private final Random random = new Random();
    private boolean receivedVelocity = false;
    private double lastVelocityX, lastVelocityY, lastVelocityZ;
    private int velocityTicks = 0;
    private final boolean shouldCancel = false;

    @EventHandler

    public void onPacket(PacketEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (event.isReceive()) {
            // Обработка пакетов скорости
            if (event.getPacket() instanceof SEntityVelocityPacket packet) {
                if (packet.getEntityID() == mc.player.getEntityId()) {
                    handleVelocityPacket(packet, event);
                }
            }

            // Обработка взрывов
            if (event.getPacket() instanceof SExplosionPacket && explosions.getValue()) {
                handleExplosionPacket((SExplosionPacket) event.getPacket(), event);
            }
        }
    }

    @EventHandler

    public void onUpdate(UpdateEvent event) {
        if (mc.player == null) return;

        // Обновляем состояние velocity
        if (receivedVelocity) {
            velocityTicks++;

            // Применяем специфичную логику для разных режимов
            switch (mode.getValue()) {
                case "Grim":
                    handleGrimUpdate();
                    break;
                case "Grim Light":
                    handleGrimLightUpdate();
                    break;
                case "Grim Heavy":
                    handleGrimHeavyUpdate();
                    break;
                case "Grim Stable":
                    handleGrimStableUpdate();
                    break;
                case "Grim Advanced":
                    handleGrimAdvancedUpdate();
                    break;
                case "Matrix":
                    handleMatrixUpdate();
                    break;
                case "Jump Reset":
                    handleJumpResetUpdate();
                    break;
            }
        }
    }

    @EventHandler

    public void onJump(JumpEvent event) {
        if (mode.is("Jump Reset") && receivedVelocity) {
            event.cancel();
            mc.player.setMotion(mc.player.getMotion().x, 0, mc.player.getMotion().z);
            receivedVelocity = false;
            velocityTicks = 0;

            if (debug.getValue()) {
                ChatUtil.addText(TextFormatting.GREEN + "[Velocity] Jump reset applied");
            }
        }
    }


    private void handleVelocityPacket(SEntityVelocityPacket packet, PacketEvent event) {
        lastVelocityX = packet.getMotionX() / 8000.0;
        lastVelocityY = packet.getMotionY() / 8000.0;
        lastVelocityZ = packet.getMotionZ() / 8000.0;

        if (debug.getValue()) {
            ChatUtil.addText(String.format(TextFormatting.YELLOW + "[Velocity] Received: X=%.2f, Y=%.2f, Z=%.2f",
                    lastVelocityX, lastVelocityY, lastVelocityZ));
        }

        switch (mode.getValue()) {
            case "Grim":
                handleGrimVelocity(packet, event);
                break;
            case "Grim Light":
                handleGrimLightVelocity(packet, event);
                break;
            case "Grim Heavy":
                handleGrimHeavyVelocity(packet, event);
                break;
            case "Grim Stable":
                handleGrimStableVelocity(packet, event);
                break;
            case "Grim Advanced":
                handleGrimAdvancedVelocity(packet, event);
                break;
            case "Matrix":
                handleMatrixVelocity(packet, event);
                break;
            case "Vulcan":
                handleVulcanVelocity(packet, event);
                break;
            case "Intave":
                handleIntaveVelocity(packet, event);
                break;
            case "Verus":
                handleVerusVelocity(packet, event);
                break;
            case "NCP":
                handleNCPVelocity(packet, event);
                break;
            case "AAC":
                handleAACVelocity(packet, event);
                break;
            case "Spartan":
                handleSpartanVelocity(packet, event);
                break;
            case "Vanilla":
                handleVanillaVelocity(packet, event);
                break;
            case "Custom":
                handleCustomVelocity(packet, event);
                break;
            case "Zero":
                event.cancel();
                break;
            case "Reduce":
                handleReduceVelocity(packet, event);
                break;
            case "Jump Reset":
                handleJumpResetVelocity(packet, event);
                break;
        }
    }

    // === GRIM МЕТОДЫ ===


    private void handleGrimVelocity(SEntityVelocityPacket packet, PacketEvent event) {
        if (!ViaUtil.allowedBypass()) {
            ChatUtil.addTextWithError("Grim velocity требует версию 1.17+");
            return;
        }

        // Базовый Grim - уменьшаем velocity с задержкой
        receivedVelocity = true;
        velocityTicks = 0;
        velocityTimer.reset();

        // Модифицируем пакет
        double reductionFactor = grimReduction.getValue();
        packet.motionX = (int) (packet.motionX * reductionFactor);
        packet.motionY = (int) (packet.motionY * reductionFactor);
        packet.motionZ = (int) (packet.motionZ * reductionFactor);
    }


    private void handleGrimLightVelocity(SEntityVelocityPacket packet, PacketEvent event) {
        if (onlyGround.getValue() && !mc.player.isOnGround()) return;

        // Легкая версия - минимальные модификации
        receivedVelocity = true;
        velocityTicks = 0;

        double lightReduction = 0.9;
        packet.motionX = (int) (packet.motionX * lightReduction);
        packet.motionZ = (int) (packet.motionZ * lightReduction);
        // Y не трогаем для лучшего обхода
    }


    private void handleGrimHeavyVelocity(SEntityVelocityPacket packet, PacketEvent event) {
        // Тяжелая версия - агрессивное уменьшение
        receivedVelocity = true;
        velocityTicks = 0;

        double heavyReduction = grimReduction.getValue() * 0.5;
        packet.motionX = (int) (packet.motionX * heavyReduction);
        packet.motionY = (int) (packet.motionY * heavyReduction);
        packet.motionZ = (int) (packet.motionZ * heavyReduction);

        // Добавляем микро-движения для обхода детекции
        addMicroMovements();
    }


    private void handleGrimStableVelocity(SEntityVelocityPacket packet, PacketEvent event) {
        if (onlyGround.getValue() && !mc.player.isOnGround()) return;

        // Стабильная версия - работает стабильно на большинстве серверов
        receivedVelocity = true;
        velocityTicks = 0;

        // Более консервативное уменьшение
        double stableReduction = 0.85;
        packet.motionX = (int) (packet.motionX * stableReduction);
        packet.motionY = (int) (packet.motionY * 0.95); // Y меньше уменьшаем
        packet.motionZ = (int) (packet.motionZ * stableReduction);
    }


    private void handleGrimAdvancedVelocity(SEntityVelocityPacket packet, PacketEvent event) {
        // Продвинутая версия с рандомизацией
        if (random.nextFloat() * 100 > chance.getValue()) return;

        receivedVelocity = true;
        velocityTicks = 0;

        // Рандомное уменьшение для обхода паттерн детекции
        double randomFactor = 0.7 + (random.nextDouble() * 0.3); // 0.7-1.0
        double reduction = grimReduction.getValue() * randomFactor;

        packet.motionX = (int) (packet.motionX * reduction);
        packet.motionY = (int) (packet.motionY * reduction);
        packet.motionZ = (int) (packet.motionZ * reduction);

        // Случайная задержка
        int randomDelay = grimDelay.getValue().intValue() + random.nextInt(100);
        velocityTimer.reset();
    }

    // === ДРУГИЕ АНТИЧИТЫ ===


    private void handleMatrixVelocity(SEntityVelocityPacket packet, PacketEvent event) {
        if (random.nextFloat() * 100 > chance.getValue()) return;

        // Matrix обход - частичная отмена с микро-движениями
        double matrixReduction = 0.6;
        packet.motionX = (int) (packet.motionX * matrixReduction);
        packet.motionY = (int) (packet.motionY * 0.8);
        packet.motionZ = (int) (packet.motionZ * matrixReduction);

        receivedVelocity = true;
        velocityTicks = 0;
    }


    private void handleVulcanVelocity(SEntityVelocityPacket packet, PacketEvent event) {
        // Vulcan обход - постепенное уменьшение
        double vulcanReduction = 0.75;
        packet.motionX = (int) (packet.motionX * vulcanReduction);
        packet.motionY = (int) (packet.motionY * 0.9);
        packet.motionZ = (int) (packet.motionZ * vulcanReduction);
    }


    private void handleIntaveVelocity(SEntityVelocityPacket packet, PacketEvent event) {
        // Intave обход - специфичная логика
        if (mc.player.hurtTime > 0) {
            packet.motionX = (int) (packet.motionX * 0.5);
            packet.motionZ = (int) (packet.motionZ * 0.5);
        }
    }


    private void handleVerusVelocity(SEntityVelocityPacket packet, PacketEvent event) {
        // Verus обход
        packet.motionX = (int) (packet.motionX * 0.83);
        packet.motionY = (int) (packet.motionY * 0.95);
        packet.motionZ = (int) (packet.motionZ * 0.83);
    }


    private void handleNCPVelocity(SEntityVelocityPacket packet, PacketEvent event) {
        // NCP обход
        packet.motionX = (int) (packet.motionX * 0.9);
        packet.motionY = (int) (packet.motionY * 0.95);
        packet.motionZ = (int) (packet.motionZ * 0.9);
    }


    private void handleAACVelocity(SEntityVelocityPacket packet, PacketEvent event) {
        // AAC обход
        if (mc.player.isOnGround()) {
            packet.motionX = 0;
            packet.motionZ = 0;
        } else {
            packet.motionX = (int) (packet.motionX * 0.7);
            packet.motionZ = (int) (packet.motionZ * 0.7);
        }
    }


    private void handleSpartanVelocity(SEntityVelocityPacket packet, PacketEvent event) {
        // Spartan обход
        packet.motionX = (int) (packet.motionX * 0.8);
        packet.motionY = (int) (packet.motionY * 0.85);
        packet.motionZ = (int) (packet.motionZ * 0.8);
    }


    private void handleVanillaVelocity(SEntityVelocityPacket packet, PacketEvent event) {
        // Vanilla - полная отмена
        event.cancel();
    }


    private void handleCustomVelocity(SEntityVelocityPacket packet, PacketEvent event) {
        // Пользовательские настройки
        double hPercent = horizontal.getValue() / 100.0;
        double vPercent = vertical.getValue() / 100.0;

        packet.motionX = (int) (packet.motionX * hPercent);
        packet.motionY = (int) (packet.motionY * vPercent);
        packet.motionZ = (int) (packet.motionZ * hPercent);
    }


    private void handleReduceVelocity(SEntityVelocityPacket packet, PacketEvent event) {
        // Простое уменьшение
        double hPercent = horizontal.getValue() / 100.0;
        double vPercent = vertical.getValue() / 100.0;

        packet.motionX = (int) (packet.motionX * hPercent);
        packet.motionY = (int) (packet.motionY * vPercent);
        packet.motionZ = (int) (packet.motionZ * hPercent);
    }


    private void handleJumpResetVelocity(SEntityVelocityPacket packet, PacketEvent event) {
        // Jump Reset - сохраняем velocity для обработки в прыжке
        receivedVelocity = true;
        velocityTicks = 0;
    }


    private void handleExplosionPacket(SExplosionPacket packet, PacketEvent event) {
        switch (mode.getValue()) {
            case "Zero":
            case "Vanilla":
                event.cancel();
                break;
            default:
                // Уменьшаем силу взрыва

                break;
        }
    }

    // === UPDATE МЕТОДЫ ===


    private void handleGrimUpdate() {
        if (velocityTimer.finished(grimDelay.getValue().intValue())) {
            applyGrimMotionReduction();
        }
    }


    private void handleGrimLightUpdate() {
        if (velocityTicks >= 1) {
            // Легкая коррекция движения
            mc.player.setMotion(
                    mc.player.getMotion().x * 0.95,
                    mc.player.getMotion().y,
                    mc.player.getMotion().z * 0.95
            );
            receivedVelocity = false;
        }
    }


    private void handleGrimHeavyUpdate() {
        if (velocityTicks >= 2) {
            // Агрессивная коррекция
            mc.player.setMotion(
                    mc.player.getMotion().x * 0.6,
                    mc.player.getMotion().y * 0.8,
                    mc.player.getMotion().z * 0.6
            );
            receivedVelocity = false;
        }
    }


    private void handleGrimStableUpdate() {
        if (velocityTicks >= 3) {
            // Стабильная коррекция
            mc.player.setMotion(
                    mc.player.getMotion().x * 0.85,
                    mc.player.getMotion().y,
                    mc.player.getMotion().z * 0.85
            );
            receivedVelocity = false;
        }
    }


    private void handleGrimAdvancedUpdate() {
        if (velocityTicks >= 1 + random.nextInt(3)) {
            // Рандомная коррекция
            double randomReduction = 0.7 + (random.nextDouble() * 0.2);
            mc.player.setMotion(
                    mc.player.getMotion().x * randomReduction,
                    mc.player.getMotion().y,
                    mc.player.getMotion().z * randomReduction
            );
            receivedVelocity = false;
        }
    }


    private void handleMatrixUpdate() {
        if (velocityTicks >= 2) {
            // Matrix специфичная коррекция
            addMicroMovements();
            mc.player.setMotion(
                    mc.player.getMotion().x * 0.75,
                    mc.player.getMotion().y,
                    mc.player.getMotion().z * 0.75
            );
            receivedVelocity = false;
        }
    }


    private void handleJumpResetUpdate() {
        // Jump Reset обрабатывается в onJump
        if (velocityTicks >= 10) {
            receivedVelocity = false; // Таймаут
        }
    }

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===


    private void applyGrimMotionReduction() {
        if (!receivedVelocity) return;

        double currentX = mc.player.getMotion().x;
        double currentY = mc.player.getMotion().y;
        double currentZ = mc.player.getMotion().z;

        double reductionFactor = grimReduction.getValue();

        mc.player.setMotion(
                currentX * reductionFactor,
                currentY * reductionFactor,
                currentZ * reductionFactor
        );

        receivedVelocity = false;
        velocityTicks = 0;

        if (debug.getValue()) {
            ChatUtil.addText(TextFormatting.GREEN + "[Velocity] Applied Grim reduction");
        }
    }


    private void addMicroMovements() {
        // Добавляем микро-движения для обхода детекции
        double microX = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.001;
        double microZ = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.001;

        mc.player.setMotion(
                mc.player.getMotion().x + microX,
                mc.player.getMotion().y,
                mc.player.getMotion().z + microZ
        );
    }

    @Override
    public void onEnable() {
        super.onEnable();
        receivedVelocity = false;
        velocityTicks = 0;
        velocityTimer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        receivedVelocity = false;
        velocityTicks = 0;
    }
}