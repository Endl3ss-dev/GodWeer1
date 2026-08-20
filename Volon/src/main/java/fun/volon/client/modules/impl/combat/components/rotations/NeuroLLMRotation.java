package fun.volon.client.modules.impl.combat.components.rotations;

import fun.volon.api.QClient;
import fun.volon.api.events.EventInvoker;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.implement.EventAttackEntity;
import fun.volon.api.events.implement.EventPacket;
import fun.volon.api.events.implement.EventUpdate;
import fun.volon.api.storages.implement.NeuroLLMStorage;
import fun.volon.api.utils.ai.NeuroRecorder;
import fun.volon.api.utils.chat.ChatUtils;
import fun.volon.api.utils.rotate.Rotation;
import fun.volon.api.utils.rotate.RotationUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Locale;

public class NeuroLLMRotation implements QClient {

    // Синглтон — регистрируется один раз при загрузке
    public static final NeuroLLMRotation INSTANCE = new NeuroLLMRotation();

    private NeuroLLMRotation() {
        // Регистрируем в EventInvoker — работает всегда, даже когда киллаура выключена
        EventInvoker.register(this);
    }

    private long lastAttackMs     = 0;
    private long attackStartMs    = 0;
    private boolean pendingResult = false;
    private Vec3d velocityBeforeAttack = Vec3d.ZERO;
    private double pingNorm = 0.0;

    // Последние данные атаки для лога
    private float lastYaw, lastPitch, lastTargetYaw, lastTargetPitch;
    private double lastDistance;
    private double lastYawOff, lastPitchOff, lastDelay, lastRotSpd;

    // Детект срезания урона
    private float hpBeforeAttack   = -1f;
    private float expectedDamage   = -1f;
    private LivingEntity damageTarget = null;
    private long damageCheckTime   = 0;

    // Последняя известная цель (для обновления lastInput даже без ротации)
    private LivingEntity lastKnownTarget = null;

    // ===== НОВАЯ СИСТЕМА SLOTH-ПОДОБНОЙ РОТАЦИИ =====
    private LivingEntity trackedTarget;
    private float currentYaw;
    private float currentPitch;
    private float velocityYaw;
    private float velocityPitch;
    private double aimPointX;
    private double aimPointY;
    private double aimPointZ;
    private float noiseAngle;
    private final float noiseAmplitude = 1.2F; // Меньше чем у Sloth для обхода
    private int hitPhase;
    private int hitTimer;
    private float pitchBeforeHit;
    private long firstSeenTime;
    private int reactionMs;
    private boolean reactionComplete;
    private float lastSentYaw;
    private float lastSentPitch;
    private float smoothYaw;
    private float smoothPitch;

    // ===== ИНТЕГРАЦИЯ С NEURORECORDER =====
    private boolean useRecordedPattern = false;
    private long lastRecorderCheck = 0;

    // Ключевые слова кика — храним вместе с описанием причины
    private static final String[][] KICK_PATTERNS = {
        {"moved too quickly",        "Слишком быстрое движение (speed/fly)"},
        {"flying is not enabled",    "Полёт не разрешён (fly hack)"},
        {"illegal stance",           "Нелегальная позиция (ground spoof)"},
        {"invalid move",             "Невалидный пакет движения"},
        {"you have been kicked",     "Кик от сервера"},
        {"kicked",                   "Кик от сервера"},
        {"you are banned",           "Бан"},
        {"cheating",                 "Обнаружен чит"},
        {"hacking",                  "Обнаружен хак"},
        {"cheat detected",           "Чит обнаружен"},
        {"flagged",                  "Флаг от античита"},
        {"violation",                "Нарушение правил"},
        {"suspicious activity",      "Подозрительная активность"},
        {"please reconnect",         "Переподключение"},
        {"sent to hub",              "Отправлен в хаб"},
        {"auto-kicked",              "Авто-кик"},
        {"combat log",               "Combat log"},
        {"reach",                    "Флаг: дистанция атаки (reach)"},
        {"timer",                    "Флаг: таймер (speed)"},
        {"speed hack",               "Флаг: speed hack"},
        {"hub",                      "Отправлен в хаб (hub)"},
        {"вы были кикнуты",          "Кик от сервера"},
        {"вы забанены",              "Бан"},
        {"вы отправлены",            "Отправлен в хаб"},
        {"автоматически перемещены", "Авто-перемещение"},
        {"нарушение",                "Нарушение правил"},
        {"читерство",                "Обнаружено читерство"},
        {"подозрительная активность","Подозрительная активность"},
    };

    // -----------------------------------------------------------------------
    // EventLink — работают ВСЕГДА независимо от состояния киллауры
    // -----------------------------------------------------------------------

    /**
     * Каждый тик обновляем lastInput в Storage — чтобы при кике
     * нейросеть знала в каком состоянии мы были.
     */
    @EventLink
    public void onTick(EventUpdate event) {
        if (mc.player == null || mc.world == null) return;

        // Обновляем ping
        if (mc.getNetworkHandler() != null
                && mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()) != null) {
            pingNorm = Math.min(
                mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency() / 500.0,
                1.0
            );
        }

        // Проверяем каждые 100мс нужно ли использовать записанный паттерн
        long now = System.currentTimeMillis();
        if (now - lastRecorderCheck > 100) {
            lastRecorderCheck = now;
            // Используем записанный паттерн если:
            // 1. NeuroRecorder в режиме воспроизведения
            // 2. Или если точность NeuroLLM низкая и есть записанный паттерн
            useRecordedPattern = NeuroRecorder.INSTANCE.isReplaying() || 
                (NeuroLLMStorage.getAccuracy() < 60.0 && NeuroRecorder.INSTANCE.getFrameCount() > 50);
        }

        // Если есть известная цель — обновляем lastInput постоянно
        LivingEntity target = lastKnownTarget;
        if (target != null && target.isAlive()) {
            Vec3d targetVec = target.getPos().add(0, target.getHeight() * 0.6f, 0);
            float currentYaw   = mc.player.getYaw();
            float currentPitch = mc.player.getPitch();
            double distance    = mc.player.getEyePos().distanceTo(targetVec);
            Vec3d tVel = target.getVelocity();
            Vec3d pVel = mc.player.getVelocity();
            long  timeSinceHit = System.currentTimeMillis() - lastAttackMs;

            double[] input = buildInput(currentYaw, currentPitch,
                RotationUtils.getRotations(targetVec).x,
                RotationUtils.getRotations(targetVec).y,
                distance, tVel, pVel, timeSinceHit);

            // Обновляем lastInput в Storage без вызова predict
            NeuroLLMStorage.updateLastInput(input);
        }

        // Проверяем срезание урона
        tickDamageCheck();
    }

    /**
     * Записываем каждую атаку — чтобы знать что именно делали перед киком.
     */
    @EventLink
    public void onAttackEntity(EventAttackEntity event) {
        if (event.getPlayer() != mc.player) return;
        if (!(event.getTarget() instanceof LivingEntity living)) return;
        lastKnownTarget = living;
        lastAttackMs = System.currentTimeMillis();
    }

    @EventLink
    public void onPacket(EventPacket event) {
        if (event.getType() != EventPacket.Type.RECEIVE) return;

        if (event.getPacket() instanceof DisconnectS2CPacket disconnectPacket) {
            String reason = disconnectPacket.reason().getString();
            String matched = findKickReason(reason.toLowerCase(Locale.ROOT));
            // Любой DisconnectS2CPacket — это кик, даже если причина неизвестна
            String displayReason = matched != null ? matched : "Неизвестная причина";
            ChatUtils.sendMessage("§c[NeuroLLM] Кик: §f" + displayReason + " §7(\"" + reason + "\")");
            ChatUtils.sendMessage("§c[NeuroLLM] Запоминаю паттерн — больше не допущу");
            NeuroLLMStorage.onKick(displayReason);
            pendingResult = false;
            return;
        }

        if (event.getPacket() instanceof GameMessageS2CPacket chatPacket) {
            String raw = chatPacket.content().getString();
            String matched = findKickReason(raw.toLowerCase(Locale.ROOT));
            if (matched != null) {
                ChatUtils.sendMessage("§c[NeuroLLM] Кик через чат: §f" + matched);
                ChatUtils.sendMessage("§7Сообщение: \"" + raw + "\"");
                ChatUtils.sendMessage("§c[NeuroLLM] Запоминаю паттерн — буду бить иначе");
                NeuroLLMStorage.onKick(matched);
                pendingResult = false;
            }
        }
    }

    private String findKickReason(String msg) {
        for (String[] pattern : KICK_PATTERNS) {
            if (msg.contains(pattern[0])) return pattern[1];
        }
        return null;
    }

    // -----------------------------------------------------------------------

    public Rotation getRotation(LivingEntity target, Vec3d targetVec) {
        if (mc.player == null || target == null) {
            float y = mc.player != null ? mc.player.getYaw() : 0;
            float p = mc.player != null ? mc.player.getPitch() : 0;
            return new Rotation(y, p);
        }

        // Проверяем есть ли записанный паттерн для воспроизведения
        if (useRecordedPattern) {
            float[] recorded = NeuroRecorder.INSTANCE.getReplayRotation(target);
            if (recorded != null) {
                // Применяем записанную ротацию с небольшими улучшениями от NeuroLLM
                return applyRecordedRotationWithNeuroEnhancement(target, recorded);
            }
        }

        // Используем Sloth-подобную ротацию с NeuroLLM улучшениями
        return getSlothLikeRotation(target, targetVec);
    }

    /**
     * Применяет записанную ротацию с улучшениями от NeuroLLM
     */
    private Rotation applyRecordedRotationWithNeuroEnhancement(LivingEntity target, float[] recorded) {
        float recordedYaw = recorded[0];
        float recordedPitch = recorded[1];
        boolean shouldAttack = recorded[2] > 0.5f;

        // Получаем небольшие корректировки от NeuroLLM
        Vec3d targetVec = target.getPos().add(0, target.getHeight() * 0.6f, 0);
        double distance = mc.player.getEyePos().distanceTo(targetVec);
        Vec3d tVel = target.getVelocity();
        Vec3d pVel = mc.player.getVelocity();
        long timeSinceHit = System.currentTimeMillis() - lastAttackMs;

        double[] pred = NeuroLLMStorage.predict(
            mc.player.getYaw(), mc.player.getPitch(),
            recordedYaw, recordedPitch,
            distance,
            tVel.x, tVel.y, tVel.z,
            pVel.x, pVel.y, pVel.z,
            timeSinceHit,
            NeuroLLMStorage.getFlagRate(),
            NeuroLLMStorage.getTotalKicks(),
            NeuroLLMStorage.getAccuracy(),
            pingNorm
        );

        // Применяем только небольшие корректировки (20% от NeuroLLM)
        float enhancedYaw = recordedYaw + (float)(pred[0] * 0.2);
        float enhancedPitch = recordedPitch + (float)(pred[1] * 0.2);

        // Сохраняем для лога
        lastYaw = mc.player.getYaw(); 
        lastPitch = mc.player.getPitch();
        lastTargetYaw = recordedYaw; 
        lastTargetPitch = recordedPitch;
        lastDistance = distance;
        lastYawOff = pred[0] * 0.2; 
        lastPitchOff = pred[1] * 0.2;
        lastDelay = pred[2]; 
        lastRotSpd = pred[3];

        return new Rotation(enhancedYaw, MathHelper.clamp(enhancedPitch, -90f, 90f));
    }

    /**
     * Sloth-подобная ротация с NeuroLLM улучшениями
     */
    private Rotation getSlothLikeRotation(LivingEntity target, Vec3d targetVec) {
        boolean playerFlying = mc.player.isGliding();

        // Инициализация при смене цели
        if (trackedTarget != target) {
            trackedTarget = target;
            currentYaw = mc.player.getYaw();
            currentPitch = mc.player.getPitch();
            lastSentYaw = currentYaw;
            lastSentPitch = currentPitch;
            smoothYaw = currentYaw;
            smoothPitch = currentPitch;
            velocityYaw = velocityPitch = 0.0F;

            pickAimPoint(target);
            hitPhase = hitTimer = 0;
            noiseAngle = (float) (Math.random() * Math.PI * 2);

            float angleDiff = measureAngle(target);
            reactionMs = computeReaction(angleDiff);
            firstSeenTime = System.currentTimeMillis();
            reactionComplete = false;
        }

        Vec3d eyePos = mc.player.getEyePos();
        Vec3d targetCenter = getPredictedPoint(target, target.getBoundingBox().getCenter());
        float distance = (float) eyePos.distanceTo(targetCenter);
        float gcd = calcGcd();

        // Фаза реакции (как у Sloth)
        if (!reactionComplete) {
            long elapsed = System.currentTimeMillis() - firstSeenTime;
            if (elapsed < reactionMs) {
                return applyReactionJitter(gcd);
            }
            reactionComplete = true;
        }

        // Генерируем шум (меньше чем у Sloth для обхода)
        float[] noise = generateNoise(distance);

        // Фаза удара (как у Sloth)
        if (hitPhase > 0) {
            return applyHitPhase(noise, gcd);
        }

        // Обновляем точку прицеливания
        if (Math.random() < 0.012) { // Реже чем у Sloth
            pickAimPoint(target);
        }

        // Предсказание движения цели
        Vec3d targetVel = new Vec3d(
            target.getX() - target.prevX,
            target.getY() - target.prevY,
            target.getZ() - target.prevZ
        );

        int predictTicks = shouldUseElytraPredict(target) ? 0 : 2;
        Vec3d predictedCenter = targetCenter.add(targetVel.multiply(predictTicks));
        Vec3d aimPos = predictedCenter.add(aimPointX, aimPointY, aimPointZ);

        // ===== ИНТЕГРАЦИЯ NEUROLLM =====
        // Получаем корректировки от нейросети
        double[] pred = NeuroLLMStorage.predict(
            mc.player.getYaw(), mc.player.getPitch(),
            RotationUtils.getRotations(aimPos).x,
            RotationUtils.getRotations(aimPos).y,
            distance,
            targetVel.x, targetVel.y, targetVel.z,
            mc.player.getVelocity().x, mc.player.getVelocity().y, mc.player.getVelocity().z,
            System.currentTimeMillis() - lastAttackMs,
            NeuroLLMStorage.getFlagRate(),
            NeuroLLMStorage.getTotalKicks(),
            NeuroLLMStorage.getAccuracy(),
            pingNorm
        );

        // Применяем корректировки NeuroLLM к точке прицеливания
        boolean nearPenalty = NeuroLLMStorage.isNearPenaltyPattern(
            buildInput(mc.player.getYaw(), mc.player.getPitch(),
                RotationUtils.getRotations(aimPos).x, RotationUtils.getRotations(aimPos).y,
                distance, targetVel, mc.player.getVelocity(), 
                System.currentTimeMillis() - lastAttackMs)
        );

        // Если рядом с штрафным паттерном — снижаем агрессивность
        float neuroYawOff = (float)(nearPenalty ? pred[0] * 0.4 : pred[0] * 0.7);
        float neuroPitchOff = (float)(nearPenalty ? pred[1] * 0.4 : pred[1] * 0.7);
        
        // Корректируем точку прицеливания
        aimPos = aimPos.add(neuroYawOff * 0.1, neuroPitchOff * 0.1, 0);

        Vec3d direction = aimPos.subtract(eyePos);
        float wantYaw = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0);
        float wantPitch = (float) -Math.toDegrees(Math.atan2(direction.y, direction.horizontalLength()));

        float diffYaw = MathHelper.wrapDegrees(wantYaw - currentYaw);
        float diffPitch = wantPitch - currentPitch;

        // Адаптивная скорость (как у Sloth но с NeuroLLM корректировками)
        float speedMultiplier = calculateSpeedMultiplier(playerFlying, wantYaw, wantPitch, target);
        
        // NeuroLLM может корректировать скорость
        float neuroRotSpd = (float)(nearPenalty ? Math.min(pred[3], 0.15) : pred[3]);
        speedMultiplier *= (0.7f + neuroRotSpd * 0.6f); // Смешиваем со Sloth логикой

        // Spring интерполяция (как у Sloth)
        float stiffness = (0.035F + (float) Math.random() * 0.008F) * speedMultiplier;
        float damping = 0.65F + (0.15F * (1.0F - speedMultiplier));

        float totalDiff = (float) Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
        if (totalDiff > 28.0F) {
            stiffness += 0.015F * speedMultiplier;
        } else if (totalDiff < 3.8F) {
            stiffness *= 0.45F;
        }

        stiffness += MathHelper.clamp((distance - 1.4F) / 6.5F, 0.0F, 0.04F) * speedMultiplier;

        velocityYaw = springInterp(currentYaw, currentYaw + diffYaw, velocityYaw, stiffness, damping);
        velocityPitch = springInterp(currentPitch, wantPitch, velocityPitch, stiffness * 0.85F, damping);

        float maxVelYaw = 6.8F * speedMultiplier;
        float maxVelPitch = 5.2F * speedMultiplier;

        velocityYaw = MathHelper.clamp(velocityYaw, -maxVelYaw, maxVelYaw);
        velocityPitch = MathHelper.clamp(velocityPitch, -maxVelPitch, maxVelPitch);

        currentYaw += velocityYaw;
        currentPitch += velocityPitch;
        currentPitch = MathHelper.clamp(currentPitch, -89.0F, 89.0F);

        // Сглаживание
        float smoothFactor = playerFlying ? (0.25F + speedMultiplier * 0.35F) : 0.8F;
        smoothYaw = smoothLerp(smoothYaw, currentYaw, smoothFactor);
        smoothPitch = smoothLerp(smoothPitch, currentPitch, smoothFactor * 0.92F);

        // Применяем шум и GCD
        float outY = smoothYaw + noise[0];
        float outP = smoothPitch + noise[1];
        outP = MathHelper.clamp(outP, -89.0F, 89.0F);

        outY -= (outY - lastSentYaw) % gcd;
        outP -= (outP - lastSentPitch) % gcd;

        lastSentYaw = outY;
        lastSentPitch = outP;

        // Сохраняем для лога
        lastYaw = mc.player.getYaw(); 
        lastPitch = mc.player.getPitch();
        lastTargetYaw = wantYaw; 
        lastTargetPitch = wantPitch;
        lastDistance = distance;
        lastYawOff = neuroYawOff; 
        lastPitchOff = neuroPitchOff;
        lastDelay = pred[2]; 
        lastRotSpd = neuroRotSpd;

        return new Rotation(outY, outP);
    }

    // -----------------------------------------------------------------------

    public void preAttack() {
        if (mc.player != null) velocityBeforeAttack = mc.player.getVelocity();
        attackStartMs = System.currentTimeMillis();
        pendingResult = true;
    }

    public void onAttack() {
        lastAttackMs = System.currentTimeMillis();
        // Запускаем фазу удара для Sloth-подобной ротации
        hitPhase = 1;
        hitTimer = 0;
        pitchBeforeHit = currentPitch;
    }

    /**
     * Вызывается перед атакой с целью — запоминаем HP и ожидаемый урон.
     */
    public void preAttackWithTarget(LivingEntity target) {
        if (mc.player == null || target == null) return;
        velocityBeforeAttack = mc.player.getVelocity();
        attackStartMs  = System.currentTimeMillis();
        pendingResult  = true;
        damageTarget   = target;
        hpBeforeAttack = target.getHealth();
        expectedDamage = getExpectedDamage();
        damageCheckTime = System.currentTimeMillis() + 300; // проверим через 300мс
    }

    /**
     * Вызывается каждый тик — проверяет срезание урона.
     */
    public void tickDamageCheck() {
        if (damageTarget == null || hpBeforeAttack < 0 || expectedDamage <= 0) return;
        if (System.currentTimeMillis() < damageCheckTime) return;

        float hpAfter = damageTarget.getHealth();
        float actualDamage = hpBeforeAttack - hpAfter;

        // Сбрасываем состояние
        float expected = expectedDamage;
        hpBeforeAttack = -1f;
        expectedDamage = -1f;
        damageTarget   = null;

        if (actualDamage <= 0) return; // не попали или цель умерла

        // Если реальный урон меньше 40% от ожидаемого — срезание
        float ratio = actualDamage / expected;
        if (ratio < 0.4f) {
            NeuroLLMStorage.onDamageReduced(expected, actualDamage);
            ChatUtils.sendMessage(
                "§c[NeuroLLM] §6Срезание урона! §7Ожидалось §f" + String.format("%.1f", expected)
                + " §7нанесено §f" + String.format("%.1f", actualDamage)
                + " §7(" + String.format("%.0f%%", ratio * 100) + "§7)"
            );
            ChatUtils.sendMessage("§c[NeuroLLM] §7Обучаюсь: меняю задержку и смещения");
        }
    }

    /**
     * Считает ожидаемый урон от текущего оружия в руке.
     */
    private float getExpectedDamage() {
        if (mc.player == null) return 1f;
        ItemStack weapon = mc.player.getMainHandStack();
        if (weapon.isEmpty()) return 1f; // кулак ~1 урон

        // Пробуем получить урон через атрибуты компонента
        AttributeModifiersComponent modifiers = weapon.getComponents().get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (modifiers != null) {
            double[] dmg = {1.0};
            modifiers.modifiers().forEach(entry -> {
                if (entry.attribute().value() == EntityAttributes.ATTACK_DAMAGE) {
                    dmg[0] += entry.modifier().value();
                }
            });
            return (float) dmg[0];
        }

        return 1f;
    }

    public void onAttackResult(boolean hit, boolean flagged) {
        if (!pendingResult) return;
        pendingResult = false;

        if (flagged) {
            NeuroLLMStorage.onFlag();
            ChatUtils.sendMessage(
                "§e[NeuroLLM] §cФлаг! §7dist=§f" + String.format("%.2f", lastDistance)
                + " §7yawOff=§f" + String.format("%.2f", lastYawOff)
                + " §7pitchOff=§f" + String.format("%.2f", lastPitchOff)
                + " §7delay=§f" + String.format("%.0f", lastDelay) + "ms"
            );
            ChatUtils.sendMessage("§e[NeuroLLM] §7Обучаюсь: уменьшаю смещения, увеличиваю задержку");
        } else if (hit) {
            NeuroLLMStorage.onHit();
            if (NeuroLLMStorage.showStats) {
                ChatUtils.sendMessage(
                    "§a[NeuroLLM] §aПопадание! §7acc=§f" + String.format("%.1f%%", NeuroLLMStorage.getAccuracy())
                    + " §7hits=§f" + NeuroLLMStorage.getTotalHits()
                    + " §7yawOff=§f" + String.format("%.2f", lastYawOff)
                    + " §7rotSpd=§f" + String.format("%.2f", lastRotSpd)
                );
            }
        } else {
            NeuroLLMStorage.onMiss();
            ChatUtils.sendMessage(
                "§e[NeuroLLM] §6Промах §7dist=§f" + String.format("%.2f", lastDistance)
                + " §7yawOff=§f" + String.format("%.2f", lastYawOff)
                + " §7pitchOff=§f" + String.format("%.2f", lastPitchOff)
            );
            ChatUtils.sendMessage("§e[NeuroLLM] §7Обучаюсь: корректирую углы");
        }
    }

    public void onKick() {
        NeuroLLMStorage.onKick("Неизвестная причина");
        pendingResult = false;
    }

    public boolean detectFlag() {
        if (mc.player == null) return false;
        long elapsed = System.currentTimeMillis() - attackStartMs;
        if (elapsed < 0 || elapsed > 600) return false;
        Vec3d cur = mc.player.getVelocity();
        double kb = Math.abs(cur.x - velocityBeforeAttack.x)
                  + Math.abs(cur.z - velocityBeforeAttack.z);
        return kb > 0.35;
    }

    public long getAttackDelay() {
        return (long) NeuroLLMStorage.getCurrentDelay();
    }

    public String getStats() {
        return NeuroLLMStorage.getStatsLine();
    }

    public void reset() {
        NeuroLLMStorage.resetSession();
        lastAttackMs  = 0;
        pendingResult = false;
        // Сбрасываем также Sloth-подобные переменные
        resetSlothVars();
    }

    // -----------------------------------------------------------------------
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ИЗ SLOTH
    // -----------------------------------------------------------------------

    private void resetSlothVars() {
        trackedTarget = null;
        velocityYaw = velocityPitch = 0.0F;
        aimPointX = aimPointY = aimPointZ = 0.0;
        noiseAngle = 0.0F;
        hitPhase = hitTimer = 0;
        firstSeenTime = 0;
        reactionComplete = false;
        reactionMs = 0;

        if (mc.player != null) {
            currentYaw = mc.player.getYaw();
            currentPitch = mc.player.getPitch();
            lastSentYaw = currentYaw;
            lastSentPitch = currentPitch;
            smoothYaw = currentYaw;
            smoothPitch = currentPitch;
        } else {
            currentYaw = currentPitch = 0.0F;
            lastSentYaw = lastSentPitch = 0.0F;
            smoothYaw = smoothPitch = 0.0F;
        }
    }

    private float calcGcd() {
        double s = mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
        return (float) (s * s * s * 1.2);
    }

    private void pickAimPoint(LivingEntity e) {
        Box bb = e.getBoundingBox();
        double w = bb.maxX - bb.minX;
        double h = bb.maxY - bb.minY;
        double d = bb.maxZ - bb.minZ;

        // Меньший разброс чем у Sloth для лучшей точности
        aimPointX = (Math.random() - 0.5) * w * 0.08;
        aimPointY = (Math.random() - 0.5) * h * 0.07;
        aimPointZ = (Math.random() - 0.5) * d * 0.08;
    }

    private float measureAngle(LivingEntity e) {
        if (mc.player == null) return 0.0F;

        Vec3d eyes = mc.player.getEyePos();
        Vec3d mid = e.getBoundingBox().getCenter();
        Vec3d delta = mid.subtract(eyes);

        float needYaw = (float) Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0F;
        float needPitch = (float) -Math.toDegrees(Math.atan2(delta.y, delta.horizontalLength()));

        float dYaw = Math.abs(MathHelper.wrapDegrees(needYaw - mc.player.getYaw()));
        float dPitch = Math.abs(needPitch - mc.player.getPitch());

        return dYaw + dPitch;
    }

    private int computeReaction(float angle) {
        // Более быстрая реакция чем у Sloth для обхода
        if (angle > 120.0F) return 120 + (int) (Math.random() * 70);
        if (angle > 60.0F) return 70 + (int) (Math.random() * 50);
        if (angle > 25.0F) return 35 + (int) (Math.random() * 25);
        return 8 + (int) (Math.random() * 15);
    }

    private boolean isMovingForward() {
        if (mc.player == null) return false;
        return mc.options.forwardKey.isPressed();
    }

    private boolean isOvertakingTarget(LivingEntity target) {
        if (mc.player == null || target == null) return false;

        Vec3d playerPos = mc.player.getPos();
        Vec3d targetPos = target.getPos();

        Vec3d playerVel = new Vec3d(
                mc.player.getX() - mc.player.prevX,
                mc.player.getY() - mc.player.prevY,
                mc.player.getZ() - mc.player.prevZ
        );

        Vec3d targetVel = new Vec3d(
                target.getX() - target.prevX,
                target.getY() - target.prevY,
                target.getZ() - target.prevZ
        );

        Vec3d toTarget = targetPos.subtract(playerPos).normalize();

        double playerSpeedToTarget = playerVel.dotProduct(toTarget);
        double targetSpeedToPlayer = targetVel.dotProduct(toTarget.multiply(-1));

        double relativeSpeed = playerSpeedToTarget + targetSpeedToPlayer;

        double distance = Math.sqrt(
                Math.pow(playerPos.x - targetPos.x, 2) +
                        Math.pow(playerPos.z - targetPos.z, 2)
        );

        return relativeSpeed > 0.04 && distance < 3.5;
    }

    private float[] generateNoise(float dist) {
        noiseAngle += 0.038F + (float) (Math.random() * 0.015F);

        float scale = MathHelper.clamp(dist / 4.0F, 0.2F, 0.9F);
        float amp = noiseAmplitude * scale;

        float n1 = (float) Math.sin(noiseAngle * 0.85) * 0.35F;
        float n2 = (float) Math.sin(noiseAngle * 1.4 + 0.7) * 0.25F;
        float n3 = (float) Math.cos(noiseAngle * 1.15 + 0.3) * 0.3F;
        float n4 = (float) Math.cos(noiseAngle * 1.7 + 1.4) * 0.2F;

        float yawNoise = (n1 + n2) * amp;
        float pitchNoise = (n3 + n4) * amp * 0.48F;

        yawNoise += ((float) Math.random() - 0.5F) * amp * 0.1F;
        pitchNoise += ((float) Math.random() - 0.5F) * amp * 0.07F;

        return new float[]{yawNoise, pitchNoise};
    }

    private float smoothStep(float x) {
        x = MathHelper.clamp(x, 0.0F, 1.0F);
        return x * x * (3.0F - 2.0F * x);
    }

    private float accelCurve(float x) {
        x = MathHelper.clamp(x, 0.0F, 1.0F);
        return 1.0F - (1.0F - x) * (1.0F - x);
    }

    private float springInterp(float current, float target, float vel, float stiffness, float damping) {
        float diff = target - current;
        float acc = diff * stiffness - vel * damping;
        return vel + acc;
    }

    private float smoothLerp(float from, float to, float alpha) {
        alpha = MathHelper.clamp(alpha, 0.0F, 1.0F);
        float delta = MathHelper.wrapDegrees(to - from);
        return from + delta * alpha;
    }

    private float calculateCurrentAngle(float targetYaw, float targetPitch) {
        float dYaw = Math.abs(MathHelper.wrapDegrees(targetYaw - currentYaw));
        float dPitch = Math.abs(targetPitch - currentPitch);
        return dYaw + dPitch;
    }

    private Vec3d getPredictedPoint(LivingEntity target, Vec3d center) {
        // Простое предсказание движения
        Vec3d velocity = new Vec3d(
            target.getX() - target.prevX,
            target.getY() - target.prevY,
            target.getZ() - target.prevZ
        );
        return center.add(velocity.multiply(1.5)); // Предсказываем на 1.5 тика вперед
    }

    private boolean shouldUseElytraPredict(LivingEntity target) {
        return target.isGliding() || mc.player.isGliding();
    }

    private float calculateSpeedMultiplier(boolean playerFlying, float wantYaw, float wantPitch, LivingEntity target) {
        float speedMultiplier = 1.0F;

        if (playerFlying) {
            float currentAngle = calculateCurrentAngle(wantYaw, wantPitch);

            if (currentAngle > 100.0F) {
                speedMultiplier = 0.2F;
            } else if (currentAngle > 70.0F) {
                float t = (currentAngle - 70.0F) / 30.0F;
                speedMultiplier = MathHelper.lerp(smoothStep(t), 0.4F, 0.2F);
            } else if (currentAngle > 20.0F) {
                float t = (currentAngle - 20.0F) / 50.0F;
                speedMultiplier = MathHelper.lerp(smoothStep(t), 0.7F, 0.4F);
            } else {
                speedMultiplier = 0.7F + (0.3F * (1.0F - currentAngle / 20.0F));
            }
        } else {
            boolean movingForward = isMovingForward();
            boolean overtaking = isOvertakingTarget(target);
            if (movingForward || overtaking) {
                speedMultiplier = 0.55F;
            }
        }

        return speedMultiplier;
    }

    private Rotation applyReactionJitter(float gcd) {
        float jitterY = ((float) Math.random() - 0.5F) * 0.18F;
        float jitterP = ((float) Math.random() - 0.5F) * 0.12F;

        float outY = lastSentYaw + jitterY;
        float outP = MathHelper.clamp(lastSentPitch + jitterP, -89.0F, 89.0F);

        outY -= (outY - lastSentYaw) % gcd;
        outP -= (outP - lastSentPitch) % gcd;

        lastSentYaw = outY;
        lastSentPitch = outP;

        return new Rotation(outY, outP);
    }

    private Rotation applyHitPhase(float[] noise, float gcd) {
        hitTimer++;

        int upDuration = 22;   // Быстрее чем у Sloth
        int downDuration = 18;
        float targetPitchUp = -89.0F;

        if (hitPhase == 1) {
            float t = hitTimer / (float) upDuration;
            t = MathHelper.clamp(t, 0.0F, 1.0F);
            float curved = accelCurve(t);
            currentPitch = MathHelper.lerp(curved, pitchBeforeHit, targetPitchUp);

            if (hitTimer >= upDuration) {
                hitPhase = 2;
                hitTimer = 0;
            }
        } else if (hitPhase == 2) {
            float goal = pitchBeforeHit;
            float t = hitTimer / (float) downDuration;
            t = MathHelper.clamp(t, 0.0F, 1.0F);
            float curved = smoothStep(t);
            currentPitch = MathHelper.lerp(curved, targetPitchUp, goal);

            if (hitTimer >= downDuration) {
                hitPhase = 0;
                hitTimer = 0;
            }
        }

        float outY = currentYaw + noise[0];
        float outP = MathHelper.clamp(currentPitch + noise[1], -89.0F, 89.0F);

        outY -= (outY - lastSentYaw) % gcd;
        outP -= (outP - lastSentPitch) % gcd;

        lastSentYaw = outY;
        lastSentPitch = outP;

        return new Rotation(outY, outP);
    }

    // -----------------------------------------------------------------------

    private double[] buildInput(
            float playerYaw, float playerPitch,
            float targetYaw, float targetPitch,
            double distance,
            Vec3d tVel, Vec3d pVel,
            long timeSinceHit
    ) {
        double[] in = new double[16];
        in[0]  = norm(playerYaw)  / 180.0;
        in[1]  = playerPitch      / 90.0;
        in[2]  = norm(targetYaw)  / 180.0;
        in[3]  = targetPitch      / 90.0;
        in[4]  = Math.min(distance / 6.0, 1.0);
        in[5]  = Math.tanh(tVel.x * 5);
        in[6]  = Math.tanh(tVel.y * 5);
        in[7]  = Math.tanh(tVel.z * 5);
        in[8]  = Math.tanh(pVel.x * 5);
        in[9]  = Math.tanh(pVel.y * 5);
        in[10] = Math.tanh(pVel.z * 5);
        in[11] = Math.min(timeSinceHit / 1000.0, 1.0);
        in[12] = Math.min(NeuroLLMStorage.getFlagRate()   / 100.0, 1.0);
        in[13] = Math.min(NeuroLLMStorage.getTotalKicks() / 10.0,  1.0);
        in[14] = Math.min(NeuroLLMStorage.getAccuracy()   / 100.0, 1.0);
        in[15] = pingNorm;
        return in;
    }

    private static double norm(double angle) {
        while (angle >  180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }
}
