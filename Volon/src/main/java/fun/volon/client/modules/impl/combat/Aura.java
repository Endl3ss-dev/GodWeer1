package fun.volon.client.modules.impl.combat;

import lombok.Getter;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MaceItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;

import fun.volon.Volon;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.implement.EventAttackEntity;
import fun.volon.api.events.implement.EventGameUpdate;
import fun.volon.api.events.implement.EventKeyboardInput;
import fun.volon.api.events.implement.EventMoveInput;
import fun.volon.api.events.implement.EventUpdate;
import fun.volon.api.events.implement.EventUpdatePost;
import fun.volon.api.storages.implement.FreeLookStorage;
import fun.volon.api.storages.implement.NeuroAuraStorage;
import fun.volon.api.storages.implement.NeuroLLMStorage;
import fun.volon.api.storages.implement.RotationStorage;
import fun.volon.api.utils.ai.NeuroRecorder;
import fun.volon.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import fun.volon.api.utils.combat.IdealHitUtils;
import fun.volon.api.utils.combat.AntiCheatEvasion;
import fun.volon.api.utils.combat.PredictUtils;
import fun.volon.api.utils.input.MovingUtil;
import fun.volon.api.utils.math.MathUtils;
import fun.volon.api.utils.math.TimerUtils;
import fun.volon.api.utils.player.HotbarUtil;
import fun.volon.api.utils.player.SlotSearchResult;
import fun.volon.api.utils.rotate.MultipointUtils;
import fun.volon.api.utils.rotate.Rotation;
import fun.volon.api.utils.rotate.RotationUtils;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.impl.combat.components.RotationsSystem;
import fun.volon.client.modules.impl.combat.components.interpolation.BestPoint;
import fun.volon.client.modules.impl.combat.components.rotations.LegitRotation;
import fun.volon.client.modules.impl.combat.components.rotations.NeuroLLMRotation;
import fun.volon.client.modules.impl.combat.components.rotations.SlothRotation;
import fun.volon.client.modules.impl.combat.components.rotations.TestRotation;
import fun.volon.client.modules.impl.combat.components.rotations.WellMineRotation;
import fun.volon.client.modules.impl.combat.components.rotations.WhiteRiseRotation;
import fun.volon.client.modules.impl.combat.components.rotations.FunTimeRotation;
import fun.volon.client.modules.impl.combat.components.rotations.LonyGriefRotation;
import fun.volon.client.modules.impl.combat.components.rotations.HolyWorldRotation;
import fun.volon.client.modules.impl.movement.Sprint;
import fun.volon.client.modules.impl.player.AutoEat;
import fun.volon.client.modules.settings.implement.BooleanSetting;
import fun.volon.client.modules.settings.implement.FloatSetting;
import fun.volon.client.modules.settings.implement.ListSetting;
import fun.volon.client.modules.settings.implement.ModeSetting;
import fun.volon.mixin.ILivingEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.minecraft.util.math.MathHelper.wrapDegrees;

public class Aura extends Module {
    public static Aura INSTANCE = new Aura();

    public final ModeSetting rotationType = new ModeSetting("Ротация", "Smooth",
            "Smooth", "Snap", "DataRotation", "Sloth", "NoRotate", "ToolRise", "WhiteRise", "ArtyGrief", "Matrix/Vulcan", "FunTime", "Lony Grief", "HolyWorld");
    ;

    private final ListSetting targets = new ListSetting("Таргеты",
            new BooleanSetting("Игроки", true),
            new BooleanSetting("Голые", true),
            new BooleanSetting("Невидимки", true),
            new BooleanSetting("Мирные", false),
            new BooleanSetting("Мобы", true)
    );

    public final FloatSetting range = new FloatSetting("Дистанция атаки", 3f, 0f, 6f, 0.05f);
    public final FloatSetting aimRange = new FloatSetting("Дистанция наводки", 3f, 0f, 6f, 0.05f);
    public final FloatSetting elytraAimRange = new FloatSetting("Дистанция на элитрах", 50f, 10f, 100f, 0.05f);
    public final BooleanSetting smartCrit = new BooleanSetting("Умные криты", false);
    private final BooleanSetting sprintReset = new BooleanSetting("Сброс спринта", true);
    private final BooleanSetting throughWalls = new BooleanSetting("Бить через стены", true);
    private final BooleanSetting raycast = new BooleanSetting("Проверка на наведение", false);
    private final BooleanSetting unpressShield = new BooleanSetting("Отжимать щит", false);
    private final BooleanSetting breakShield = new BooleanSetting("Ломать щит", true);
    private final BooleanSetting attackOnEating = new BooleanSetting("Не бить когда ешь", true);
    public static BooleanSetting clientLook = new BooleanSetting("Наводка от первого лица", false);
    public final BooleanSetting smoothEvasion = new BooleanSetting("nepidor777", false);
    private final ModeSetting moveFix = new ModeSetting("Коррекция", "Нет", "Нет", "Свободная", "Сфокусированная", "Полная");
    private final ModeSetting priority = new ModeSetting("Приоритет", "Дистанция", "Дистанция", "Здоровье", "Угол", "Никакой");

    // ---- NeuroLLM настройки ----
    private final ListSetting neuroSettings = new ListSetting("Нейро настройки",
            new BooleanSetting("Учиться при кике",       true),
            new BooleanSetting("Учиться при флаге",      true),
            new BooleanSetting("Учиться при промахе",    true),
            new BooleanSetting("Учиться при попадании",  true),
            new BooleanSetting("Адаптировать задержку",  true),
            new BooleanSetting("Адаптировать скорость",  true),
            new BooleanSetting("Адаптировать yaw",       true),
            new BooleanSetting("Адаптировать pitch",     true),
            new BooleanSetting("Experience Replay",      true),
            new BooleanSetting("Показывать статистику",  true)
    ).visible(() -> rotationType.is("NeuroLLM"));

    @Getter
    private LivingEntity target;
    @Getter
    private Vec2f currentRotations = new Vec2f(0f, 0f);
    @Getter
    private Vec2f targetRotations = new Vec2f(0f, 0f);
    @Getter
    private final NeuroAuraStorage dataSystem = new NeuroAuraStorage();
    @Getter
    private final TimerUtils attackTimer = new TimerUtils();
    private final BooleanSetting rwWallBypass = new BooleanSetting("Обход рв стен", false);
    private final BooleanSetting rwWallLookDown = new BooleanSetting("Смотреть вниз", false).visible(rwWallBypass::isState);
    private final WellMineRotation wellMineRotation = new WellMineRotation(this);
    private final TestRotation testRotation = new TestRotation();
    private final SlothRotation slothRotation = new SlothRotation();
    private final WhiteRiseRotation whiteRiseRotation = new WhiteRiseRotation(this);
    private final FunTimeRotation funTimeRotation = new FunTimeRotation(this);
    private final LonyGriefRotation lonyGriefRotation = new LonyGriefRotation(this);
    private final HolyWorldRotation holyWorldRotation = new HolyWorldRotation(this);
    private final NeuroLLMRotation neuroLLMRotation = NeuroLLMRotation.INSTANCE;
    public NeuroLLMRotation getNeuroLLMRotation() { return neuroLLMRotation; }

    private final TimerUtils backTimer = new TimerUtils();

    private TpsSync tpsSync;

    private long cps = 0;
    private boolean needSprintReset = false;
    private boolean sprintResetDone = false;
    private int sprintResetTicks = 0;
    private int ticksToAttack = 0;
    private int snapAttackAge = -1;
    private boolean snapAttackQueued = false;
    private LivingEntity snapAttackTarget = null;
    private LivingEntity lastDataTarget = null;

    private float lastYaw = 0f;
    private float lastPitch = 0f;

    // Droid rotation state
    private float[] droidLerpRotation = new float[]{0f, 0f};
    private boolean spookyIsLookingAway = false;
    private long spookyLastLookAwayTime = 0L;
    private long spookyLookAwayStartTime = 0L;
    private float spookyLookAwayTargetYaw = 0f;
    private float spookyLookAwayTargetPitch = 0f;
    private long spookyLastPitchUpdateTime = 0L;

    public static float adjYaw;
    public static float adjPitch;
    public static float otvodkaYaw;
    public static float otvodkaPitch;

    public boolean isRotated;

    public Aura() {
        super("AttackAura", "Автоматически наводиться и бьёт таргета", ModuleCategory.COMBAT);
        addSettings(rotationType, smoothEvasion, targets, range, aimRange, elytraAimRange, smartCrit, sprintReset,
                attackOnEating, throughWalls, rwWallBypass, rwWallLookDown, raycast, unpressShield, breakShield, clientLook, moveFix, priority, neuroSettings);
    }


    @EventLink
    public void onPlayerTick(EventUpdate e) {
        if (mc.player == null || mc.world == null) return;

        // Синхронизируем режим отводки с настройкой
        AntiCheatEvasion.INSTANCE.setMode(
            smoothEvasion.isState(),
            false
        );

        if (tpsSync == null && Volon.INSTANCE != null && Volon.INSTANCE.moduleStorage != null) {
            tpsSync = ModuleClass.tpsSync;
        }

        lastYaw++;
        updateTarget();

        if (dataSystem.isRecording()) {
            LivingEntity recordTarget = findTargetForRecording();
            dataSystem.recordTick(recordTarget, mc.player.getYaw(), mc.player.getPitch());
        }

        // Синхронизируем чекбоксы с NeuroLLMStorage
        if (rotationType.is("NeuroLLM")) {
            NeuroLLMStorage.learnOnKick      = neuroSettings.is("Учиться при кике");
            NeuroLLMStorage.learnOnFlag      = neuroSettings.is("Учиться при флаге");
            NeuroLLMStorage.learnOnMiss      = neuroSettings.is("Учиться при промахе");
            NeuroLLMStorage.learnOnHit       = neuroSettings.is("Учиться при попадании");
            NeuroLLMStorage.adaptDelay       = neuroSettings.is("Адаптировать задержку");
            NeuroLLMStorage.adaptRotSpeed    = neuroSettings.is("Адаптировать скорость");
            NeuroLLMStorage.adaptYawOffset   = neuroSettings.is("Адаптировать yaw");
            NeuroLLMStorage.adaptPitchOffset = neuroSettings.is("Адаптировать pitch");
            NeuroLLMStorage.experienceReplay = neuroSettings.is("Experience Replay");
            NeuroLLMStorage.showStats        = neuroSettings.is("Показывать статистику");

            // Показываем статистику каждые 2 секунды
            if (NeuroLLMStorage.showStats && mc.player.age % 40 == 0) {
                mc.player.sendMessage(net.minecraft.text.Text.literal(neuroLLMRotation.getStats()), true);
            }

            // Детектируем флаг по кикбэку
            if (neuroLLMRotation.detectFlag()) {
                neuroLLMRotation.onAttackResult(false, true);
            }

            // Проверяем срезание урона каждый тик
            neuroLLMRotation.tickDamageCheck();
        }
    }

    @EventLink
    public void onAttackEntity(EventAttackEntity event) {
        if (mc.player == null || mc.world == null) return;
        if (event.getPlayer() != mc.player) return;
        if (!(event.getTarget() instanceof LivingEntity living)) return;
        if (!isValidTarget(living)) return;

        target = living;
    }

    @EventLink
    public void onMoveInput(EventMoveInput event) {
        if (needSprintReset) {
            event.setForward(0);
            event.setStrafe(0);
            needSprintReset = false;
            sprintResetDone = true;
            sprintResetTicks = 0;
            return;
        }

        applyMoveFix(event);
    }

    private void applyMoveFix(EventMoveInput event) {
        if (mc.player == null || target == null || rotationType.is("NoRotate") || moveFix.getIndex() == 0) {
            return;
        }

        if (moveFix.getIndex() == 1) {
            MovingUtil.fixMovementFree(event);
        }
    }

    @EventLink
    public void onKeyboardInput(EventKeyboardInput event) {
        if (mc.player == null || mc.world == null || target == null || rotationType.is("NoRotate")) {
            return;
        }

        float correctionYaw = getCorrectionYaw();
        if (moveFix.getIndex() == 2) {
            event.setYaw(correctionYaw, mc.player.getYaw());
        } else if (moveFix.getIndex() == 3) {
            event.setYaw(correctionYaw, getTargetDirectionYaw());
        }
    }

    private float getCorrectionYaw() {
        if (RotationStorage.instance != null && RotationStorage.instance.targetRotation() != null) {
            return RotationStorage.instance.targetRotation().getYaw();
        }
        return mc.player.getYaw();
    }

    private float getTargetDirectionYaw() {
        return RotationUtils.getRotations(target.getBoundingBox().getCenter()).x;
    }

    private void applyMovementCorrection(EventMoveInput event, float yaw, float directionYaw) {
        float forward = event.getForward();
        float strafe = event.getStrafe();
        if (forward == 0.0F && strafe == 0.0F) {
            return;
        }

        double angle = MathHelper.wrapDegrees(Math.toDegrees(MovingUtil.direction(directionYaw, forward, strafe)));
        float closestForward = 0.0F;
        float closestStrafe = 0.0F;
        float closestDifference = Float.MAX_VALUE;

        for (float predictedForward = -1.0F; predictedForward <= 1.0F; predictedForward += 1.0F) {
            for (float predictedStrafe = -1.0F; predictedStrafe <= 1.0F; predictedStrafe += 1.0F) {
                if (predictedForward == 0.0F && predictedStrafe == 0.0F) {
                    continue;
                }

                double predictedAngle = MathHelper.wrapDegrees(Math.toDegrees(MovingUtil.direction(yaw, predictedForward, predictedStrafe)));
                double difference = Math.abs(angle - predictedAngle);
                if (difference < closestDifference) {
                    closestDifference = (float) difference;
                    closestForward = predictedForward;
                    closestStrafe = predictedStrafe;
                }
            }
        }

        event.setForward(closestForward);
        event.setStrafe(closestStrafe);
    }

    @EventLink
    private void onGameUpdate(EventGameUpdate e) {
        if (mc.player == null || mc.world == null || target == null) return;
        rotate();
    }


    @EventLink
    public void onTick(EventUpdate e) {
        if (mc.player == null || mc.world == null) return;

        if (ticksToAttack > 0) {
            ticksToAttack--;
        }

        if (sprintResetDone) {
            sprintResetTicks++;
        }

        boolean packetCrits = ModuleClass.packetCriticals.isEnable()
                && mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING);

        if (!packetCrits) {
            processAttack();
        }

        if (dataSystem.isShowStats() && mc.player.age % 40 == 0 && (dataSystem.isRecording() || dataSystem.isUsingNeuro())) {
            mc.player.sendMessage(net.minecraft.text.Text.literal(dataSystem.getStatusString()), true);
        }
    }

    @EventLink
    public void onPost(EventUpdatePost e) {
        if (mc.player == null || mc.world == null) return;

        boolean packetCrits = ModuleClass.INSTANCE.packetCriticals.isEnable()
                && mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING);

        if (packetCrits && mc.player.fallDistance > 0 && mc.player.fallDistance < 1) {
            processAttack();
        }
    }

    private LivingEntity findTargetForRecording() {
        LivingEntity bestTarget = null;
        double bestDistance = 100.0;
        Vec3d eyePos = mc.player.getEyePos();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (living == mc.player) continue;
            if (!living.isAlive() || living.getHealth() <= 0) continue;
            if (living instanceof ArmorStandEntity) continue;

            double distance = eyePos.squaredDistanceTo(living.getBoundingBox().getCenter());
            if (distance > bestDistance) continue;

            bestDistance = distance;
            bestTarget = living;
        }

        return bestTarget;
    }


    private void processAttack() {
        updateTarget();

        if (target != null) {
            if ((shouldAttack() || shouldUseQueuedSnapAttack()) && cps <= System.currentTimeMillis()) {
                if (attackOnEating.isState() && (shouldBlockAttackWhileUsingItem() || AutoEat.shouldSuppressCombat())) return;

                if (sprintReset.isState()
                        && mc.player.isSprinting()
                        && !sprintResetDone
                        && !shouldSkipSprintResetInWater()
                        && !IdealHitUtils.isNoJumpDelayJumpCritWindow()) {
                    needSprintReset = true;
                    return;
                }

                if (sprintReset.isState() && sprintResetDone && sprintResetTicks < 1) {
                    return;
                }

                if (isSnapRotationActive() && !prepareSnapAttack()) {
                    return;
                }
                attack();
                resetSnapAttack();
                sprintResetDone = false;
                sprintResetTicks = 0;
            }
        } else {
            cps = System.currentTimeMillis();
            backTimer.reset();
            adjPitch = 0;
            adjYaw = 0;
            wellMineRotation.reset();
            testRotation.reset();
            slothRotation.reset();
            whiteRiseRotation.reset();
            dataSystem.resetState();
            lastDataTarget = null;
            sprintResetDone = false;
            sprintResetTicks = 0;
            ticksToAttack = 0;
            resetSnapAttack();
        }
    }

    public void Rotate() {
        rotate();
    }


    private void rotate() {
        if (ModuleClass.elytraresolver != null && ModuleClass.elytraresolver.isEscaping()) {
            Vec3d targetPos = ModuleClass.elytraresolver.getEscapePos();
            double diffX = targetPos.x - mc.player.getX();
            double diffY = targetPos.y - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
            double diffZ = targetPos.z - mc.player.getZ();
            double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

            float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0);
            float pitch = (float) -Math.toDegrees(Math.atan2(diffY, dist));

            float gcd = (mc.options.getMouseSensitivity().getValue().floatValue() * 0.6F + 0.2F);
            gcd = gcd * gcd * gcd * 1.2F;
            yaw -= yaw % gcd;
            pitch -= pitch % gcd;

            RotationStorage.update(new Rotation(yaw, pitch), 180, 180, 25, 20, 1, 1, clientLook.isState());
            return;
        }

        if (target == null) return;

        if (rotationType.is("Data") && target != lastDataTarget) {
            dataSystem.resetState();
            lastDataTarget = target;
        }

        if (isSnapRotationActive()) {
            updateSnapRotation(target);
            return;
        }

        RotationsSystem system;

        if (rotationType.is("Smooth")) {
            system = new RotationsSystem() {
                @Override
                public void updateRotations(LivingEntity target) {
                    if (!mc.player.isGliding()) {
                        Vec3d relativePos = target.getPos().add(0, target.getHeight() * 0.6f, 0).subtract(mc.player.getEyePos());
                        final float yaw = (float) wrapDegrees(Math.toDegrees(Math.atan2(relativePos.z, relativePos.x)) - 90);
                        float pitch = (float) (-Math.toDegrees(Math.atan2(relativePos.y, Math.hypot(relativePos.x, relativePos.z))));
                        RotationStorage.update(new Rotation(yaw, pitch), 360, 360, 360, 360, 1, 1, clientLook.isState());
                    } else {
                        Vec3d interpolatedRotation = Vec3d.fromPolar(target.getLerpTargetPitch(), target.getLerpTargetYaw());
                        Vec3d rotationVector = target.getRotationVector();
                        Vec3d relativePos = target.getPos().add(0, target.getHeight() * 0.6f, 0).subtract(mc.player.getEyePos());
                        Vec3d blendedDirection = interpolatedRotation.normalize().lerp(rotationVector, interpolatedRotation.length());
                        if (mc.player.isGliding() && target.isGliding() && ModuleClass.elytraTarget.isEnable()) {
                            relativePos = relativePos.add(blendedDirection.normalize().multiply(ModuleClass.elytraTarget.forward.getValue().floatValue()));
                        }
                        final float yaw = (float) wrapDegrees(Math.toDegrees(Math.atan2(relativePos.z, relativePos.x)) - 90);
                        float pitch = (float) (-Math.toDegrees(Math.atan2(relativePos.y, Math.hypot(relativePos.x, relativePos.z))));
                        RotationStorage.update(new Rotation(yaw, pitch), 360, 360, 360, 360, 1, 1, clientLook.isState());
                    }
                }
            };
        } else if (rotationType.is("ToolRise")) {
            system = wellMineRotation;
        } else if (rotationType.is("Test")) {
            system = testRotation;
        } else if (rotationType.is("Sloth")) {
            system = slothRotation;
        } else if (rotationType.is("FunTime")) {
            system = funTimeRotation;
        } else if (rotationType.is("Lony Grief")) {
            system = lonyGriefRotation;
        } else if (rotationType.is("HolyWorld")) {
            system = holyWorldRotation;
        } else if (rotationType.is("NoRotate")) {
            system = new RotationsSystem() {
                @Override
                public void updateRotations(LivingEntity target) {
                    // На элитре — используем улучшенную ротацию с predict
                    if (mc.player.isGliding() && target.isGliding()) {
                        Vec3d targetPoint;
                        
                        // Проверяем, включен ли predict (ElytraTarget)
                        if (ModuleClass.elytraTarget != null && ModuleClass.elytraTarget.isEnable()) {
                            // Predict включен — наводимся на предсказанный бокс с плавным смещением
                            Vec3d currentPos = target.getBoundingBox().getCenter();
                            
                            // Вычисляем velocity цели
                            Vec3d velocity = new Vec3d(
                                target.getX() - target.prevX,
                                target.getY() - target.prevY,
                                target.getZ() - target.prevZ
                            );
                            
                            // Получаем predict forward из настроек
                            int predictTicks = ModuleClass.elytraTarget.forward.getValue().intValue();
                            
                            // Предсказываем позицию с учетом velocity и направления полета
                            Vec3d interpolatedRotation = Vec3d.fromPolar(target.getLerpTargetPitch(), target.getLerpTargetYaw());
                            Vec3d rotationVector = target.getRotationVector();
                            Vec3d blendedDirection = interpolatedRotation.normalize().lerp(rotationVector, 0.5);
                            
                            // Комбинируем velocity и направление для точного предсказания
                            Vec3d predictedOffset = blendedDirection.normalize()
                                .multiply(predictTicks * 0.6) // Перегон вперед
                                .add(velocity.multiply(predictTicks)); // + текущая скорость
                            
                            targetPoint = currentPos.add(predictedOffset);
                            
                            // Плавное смещение к центру хитбокса (убираем дергание)
                            Vec3d eyePos = mc.player.getEyePos();
                            double distanceToTarget = eyePos.distanceTo(targetPoint);
                            
                            // Если далеко - сильнее смещаемся к predict точке
                            // Если близко - больше к центру хитбокса для точности
                            double smoothFactor = Math.min(1.0, distanceToTarget / 10.0);
                            targetPoint = currentPos.lerp(targetPoint, smoothFactor);
                            
                            // Добавляем небольшое смещение по Y для компенсации падения
                            targetPoint = targetPoint.add(0, target.getHeight() * 0.3f, 0);
                        } else {
                            // Predict выключен — наводимся на текущий хитбокс
                            targetPoint = target.getPos().add(0, target.getHeight() * 0.6f, 0);
                        }
                        
                        // Вычисляем углы к целевой точке
                        Vec3d relativePos = targetPoint.subtract(mc.player.getEyePos());
                        float yaw = (float) wrapDegrees(Math.toDegrees(Math.atan2(relativePos.z, relativePos.x)) - 90);
                        float pitch = (float) (-Math.toDegrees(Math.atan2(relativePos.y, Math.hypot(relativePos.x, relativePos.z))));
                        
                        // Плавная ротация с высокой скоростью для элитры
                        RotationStorage.update(
                            new Rotation(yaw, pitch), 
                            180, 180, // Высокая скорость наведения
                            120, 120, // Средняя скорость возврата
                            1, 1, 
                            clientLook.isState()
                        );
                        return;
                    }
                    
                    // На земле — стандартный NoRotate (смотрит куда смотрит игрок)
                    RotationStorage.update(new Rotation(FreeLookStorage.getFreeYaw(), FreeLookStorage.getFreePitch()), MathUtils.random(100, 170), MathUtils.random(100, 170), MathUtils.random(100, 170), MathUtils.random(100, 170), 1, 6, false);
                }
            };
        } else if (rotationType.is("Data")) {
            system = new RotationsSystem() {
                @Override
                public void updateRotations(LivingEntity target) {
                    boolean focusRotation = shouldFocusDataRotation();
                    Vec3d fallbackPoint = getDataRotationPoint(target);
                    Vec2f fallbackRot = RotationUtils.getRotations(fallbackPoint);
                    float currentYaw = mc.player.getYaw();
                    float currentPitch = mc.player.getPitch();
                    float yawDelta = Math.abs(MathHelper.wrapDegrees(fallbackRot.x - currentYaw));
                    float pitchDelta = Math.abs(fallbackRot.y - currentPitch);
                    boolean hardAcquire = yawDelta > 70.0f || (yawDelta > 42.0f && mc.player.squaredDistanceTo(target) < 9.0);
                    Rotation rotation = null;

                    if (shouldUseElytraPredict(target)) {
                        Vec2f rot = RotationUtils.getRotations(getPredictedPoint(target, fallbackPoint));
                        rotation = new Rotation(rot.x, rot.y);
                    } else if (!hardAcquire) {
                        rotation = dataSystem.getNeuroRotation(
                                target,
                                currentYaw,
                                currentPitch,
                                focusRotation
                        );
                    }

                    if (rotation == null || hardAcquire) {
                        if (hardAcquire) {
                            dataSystem.resetState();
                        }
                        rotation = new Rotation(fallbackRot.x, fallbackRot.y);
                    }

                    targetRotations = new Vec2f(rotation.getYaw(), rotation.getPitch());
                    currentRotations = new Vec2f(currentYaw, currentPitch);

                    float yawSpeed = hardAcquire ? Math.max(95.0f, Math.min(180.0f, yawDelta * 1.45f)) : (focusRotation ? 24.0f : 11.5f);
                    float pitchSpeed = hardAcquire ? Math.max(55.0f, Math.min(110.0f, Math.max(18.0f, pitchDelta * 1.35f))) : (focusRotation ? 18.0f : 9.0f);
                    float yawReturnSpeed = hardAcquire ? yawSpeed : (focusRotation ? 18.0f : 9.0f);
                    float pitchReturnSpeed = hardAcquire ? pitchSpeed : (focusRotation ? 14.0f : 7.0f);

                    RotationStorage.update(
                            rotation,
                            yawSpeed,
                            pitchSpeed,
                            yawReturnSpeed,
                            pitchReturnSpeed,
                            1,
                            1, clientLook.isState()
                    );
                }
            };
        } else if (rotationType.is("ArtyGrief")) {
            system = new RotationsSystem() {
                @Override
                public void updateRotations(LivingEntity target) {
                    handleDroidRotation(target);
                }
            };
        } else if (rotationType.is("NeuroLLM")) {
            system = new RotationsSystem() {
                @Override
                public void updateRotations(LivingEntity target) {
                    if (AntiCheatEvasion.INSTANCE.handleTransitions()) return;

                    // Если активно воспроизведение — применяем записанные смещения к цели
                    if (NeuroRecorder.INSTANCE.isReplaying()) {
                        float[] rot = NeuroRecorder.INSTANCE.getReplayRotation(target);
                        if (rot != null) {
                            currentRotations = new Vec2f(mc.player.getYaw(), mc.player.getPitch());
                            targetRotations  = new Vec2f(rot[0], rot[1]);
                            float[] out = AntiCheatEvasion.INSTANCE.process(rot[0], rot[1], target, "neurollm");
                            RotationStorage.update(
                                new Rotation(out[0], out[1]),
                                360, 360, 360, 360, 1, 1, clientLook.isState()
                            );
                            return;
                        }
                    }

                    // Иначе — нейросеть
                    Vec3d targetVec = target.getPos().add(0, target.getHeight() * 0.6f, 0);
                    Rotation rotation = neuroLLMRotation.getRotation(target, targetVec);

                    currentRotations = new Vec2f(mc.player.getYaw(), mc.player.getPitch());
                    targetRotations  = new Vec2f(rotation.getYaw(), rotation.getPitch());

                    float baseSpeed     = 30.0f;
                    float accuracyBonus = (float) (NeuroLLMStorage.getAccuracy() / 10.0);
                    float yawSpeed      = baseSpeed + accuracyBonus;
                    float pitchSpeed    = baseSpeed + accuracyBonus;

                    float[] out = AntiCheatEvasion.INSTANCE.process(rotation.getYaw(), rotation.getPitch(), target, "neurollm");
                    RotationStorage.update(
                        new Rotation(out[0], out[1]),
                        yawSpeed, pitchSpeed,
                        yawSpeed * 0.8f, pitchSpeed * 0.8f,
                        1, 1, clientLook.isState()
                    );
                }
            };
        } else {

            Vec2f targetRot;
            targetRot = RotationUtils.getRotations(getPredictedRotationPoint(target, target.getLeashPos(1)));


            system = new RotationsSystem() {
                @Override
                public void updateRotations(LivingEntity target) {
                    currentRotations = new Vec2f(mc.player.getYaw(), mc.player.getPitch());
                    RotationStorage.update(new Rotation(targetRot.x, targetRot.y), 360, 360, 360, 360, 1, 1, clientLook.isState());
                }
            };
        }

        system.updateRotations(target);
    }

    private void updateSnapRotation(LivingEntity target) {
        Vec3d point = MultipointUtils.getClosestPoint(target);
        if (point == null) {
            point = target.getBoundingBox().getCenter();
        }

        Vec3d predicted = getPredictedRotationPoint(target, point);
        Vec2f targetRot = RotationUtils.getRotations(predicted);

        targetRotations = targetRot;
        currentRotations = new Vec2f(mc.player.getYaw(), mc.player.getPitch());

        boolean isAttackSnapTick = mc.player.age <= snapAttackAge;

        float finalYaw = targetRot.x;
        float finalPitch = targetRot.y;
        if (!isAttackSnapTick) {
            if (shouldKeepRwWallPitchDown()) {
                finalPitch = 90.0f;
            } else {
                finalYaw = FreeLookStorage.getFreeYaw();
                finalPitch = FreeLookStorage.getFreePitch();
            }
        }

        RotationStorage.update(new Rotation(finalYaw, finalPitch), 360f, 360f, 360f, 360f, 0, 6, clientLook.isState());
    }

    private boolean isSnapRotationActive() {
        return rotationType.is("Snap") || isUsingRwWallSnap();
    }

    private boolean prepareSnapAttack() {
        if (!snapAttackQueued) {
            snapAttackQueued = true;
            snapAttackAge = mc.player.age + 1;
            snapAttackTarget = target;
            return false;
        }

        if (mc.player.age > snapAttackAge) {
            resetSnapAttack();
            return false;
        }

        return isSnapAimReadyForAttack();
    }

    private boolean shouldUseQueuedSnapAttack() {
        if (!snapAttackQueued || mc.player == null || target == null || target != snapAttackTarget) {
            return false;
        }

        if (mc.player.age > snapAttackAge + 1) {
            resetSnapAttack();
            return false;
        }

        return mc.player.age >= snapAttackAge;
    }

    private boolean isSnapAimReadyForAttack() {
        if (target == null || mc.player == null) {
            return false;
        }

        float yawDiff = Math.abs(MathHelper.wrapDegrees(targetRotations.x - mc.player.getYaw()));
        float pitchDiff = Math.abs(targetRotations.y - mc.player.getPitch());
        boolean onTarget = isUsingRwWallSnap()
                || (mc.player.isGliding() && target.isGliding());

        if (!onTarget) {
            EntityHitResult result = getAttackRaycastResult();
            onTarget = result != null && result.getEntity() == target;
        }

        return yawDiff <= 3.0f && pitchDiff <= 2.5f && onTarget;
    }

    private boolean isUsingRwWallSnap() {
        return rwWallBypass.isState() && target != null && isTargetBehindWall(target);
    }

    private boolean shouldKeepRwWallPitchDown() {
        return isUsingRwWallSnap() && rwWallLookDown.isState();
    }

    private boolean shouldSkipSprintResetInWater() {
        return mc.player != null
                && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater())
                && Sprint.INSTANCE != null
                && Sprint.INSTANCE.shouldKeepSprintInWater();
    }

    private EntityHitResult getAttackRaycastResult() {
        Vec3d eyePos = mc.player.getCameraPosVec(1.0F);
        Vec3d lookVec = mc.player.getRotationVec(1.0F);
        float reach = range.getValue().floatValue() * 2.0f;
        Vec3d reachVec = eyePos.add(lookVec.multiply(reach));

        return ProjectileUtil.raycast(
                mc.player,
                eyePos,
                reachVec,
                mc.player.getBoundingBox().expand(reach),
                ex -> ex != mc.player && ex.isAlive(),
                reach * reach
        );
    }

    private boolean isTargetBehindWall(LivingEntity entity) {
        if (entity == null || mc.player == null || mc.world == null) {
            return false;
        }

        return !mc.player.canSee(entity) || hasNarrowRwWallGap(entity);
    }

    private boolean hasNarrowRwWallGap(LivingEntity entity) {
        if (entity == null || mc.player == null || mc.world == null || !rwWallBypass.isState()) {
            return false;
        }

        Vec3d eyePos = mc.player.getEyePos();
        Box box = entity.getBoundingBox();
        double centerX = box.getCenter().x;
        double centerZ = box.getCenter().z;
        Vec3d[] points = new Vec3d[]{
                box.getCenter(),
                getStableBodyPoint(entity),
                new Vec3d(centerX, box.maxY - 0.08, centerZ),
                new Vec3d(centerX, box.minY + 0.12, centerZ),
                new Vec3d(box.minX + 0.04, box.minY + box.getLengthY() * 0.55, centerZ),
                new Vec3d(box.maxX - 0.04, box.minY + box.getLengthY() * 0.55, centerZ),
                new Vec3d(centerX, box.minY + box.getLengthY() * 0.55, box.minZ + 0.04),
                new Vec3d(centerX, box.minY + box.getLengthY() * 0.55, box.maxZ - 0.04)
        };

        int blocked = 0;
        int clear = 0;
        for (Vec3d point : points) {
            BlockHitResult hit = mc.world.raycast(new RaycastContext(
                    eyePos,
                    point,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    mc.player
            ));

            if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                blocked++;
            } else {
                clear++;
            }
        }

        return clear > 0 && blocked >= clear;
    }

    private Vec3d getPredictedRotationPoint(LivingEntity target, Vec3d point) {
        if (mc.player != null
                && target != null
                && mc.player.isGliding()
                && target.isGliding()
                && ModuleClass.elytraTarget != null
                && ModuleClass.elytraTarget.isEnable()) {
            return PredictUtils.bypasselytrahacking(target);
        }
        return point;
    }

    private Vec3d getDataRotationPoint(LivingEntity target) {
        if (isInsideOrNearHitbox(target)) {
            return getPredictedRotationPoint(target, getStableBodyPoint(target));
        }
        Vec3d point = BestPoint.getNearestPoint(target);
        if (point == null) {
            point = MultipointUtils.getClosestPoint(target);
        }
        if (point == null) {
            point = getStableBodyPoint(target);
        }
        return getPredictedRotationPoint(target, point);
    }

    private boolean isInsideOrNearHitbox(LivingEntity target) {
        if (mc.player == null || target == null) {
            return false;
        }
        Vec3d eyePos = mc.player.getEyePos();
        Box box = target.getBoundingBox();
        if (box.expand(0.12).contains(eyePos)) {
            return true;
        }
        Vec3d stablePoint = getStableBodyPoint(target);
        return eyePos.squaredDistanceTo(stablePoint) <= 2.25;
    }

    private Vec3d getStableBodyPoint(LivingEntity target) {
        Box box = target.getBoundingBox();
        return new Vec3d(
                box.getCenter().x,
                box.minY + box.getLengthY() * 0.72,
                box.getCenter().z
        );
    }

    private LivingEntity findTarget() {
        List<LivingEntity> entities = new ArrayList<>();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (!isValidTarget(living)) continue;
            entities.add(living);
        }

        if (entities.isEmpty() || !isEnable()) return null;

        switch (priority.getCurrent()) {
            case "Дистанция" ->
                    entities.sort(Comparator.comparingDouble(entity -> entity.getBoundingBox().getCenter().squaredDistanceTo(mc.player.getEyePos())));
            case "Здоровье" -> entities.sort(Comparator.comparingDouble(LivingEntity::getHealth));
            case "Угол" -> entities.sort(Comparator.comparingDouble(entity -> {
                Vec2f vec = RotationUtils.getRotations(entity.getBoundingBox().getCenter());
                double dy = Math.abs(wrapDegrees(vec.x - mc.player.getYaw()));
                double dp = Math.abs(wrapDegrees(vec.y - mc.player.getPitch()));
                return dy + dp;
            }));
            case "Никакой" -> {
            }
        }

        return entities.isEmpty() ? null : entities.get(0);
    }

    private void updateTarget() {
        if (!isEnable()) {
            target = null;
            return;
        }

        if (target != null && isValidTarget(target)) {
            return;
        }

        target = findTarget();
    }

    private boolean shouldFocusDataRotation() {
        float cooldown = mc.player.getAttackCooldownProgress(1.5f);
        float focusThreshold = Math.max(0.82f, IdealHitUtils.getAICooldown() - 0.08f);
        boolean readyByCooldown = cooldown >= focusThreshold;
        boolean fallingForCrit = !mc.player.isOnGround()
                && mc.player.getVelocity().y < 0.0
                && mc.player.fallDistance > 0.0f;

        return readyByCooldown || fallingForCrit;
    }


    private void attack() {
        Hand blockedShieldHand = null;
        boolean wasBlocking = false;
        
        if (unpressShield.isState()) {
            blockedShieldHand = getBlockingShieldHand();
            if (blockedShieldHand != null) {
                wasBlocking = true;
                mc.interactionManager.stopUsingItem(mc.player);
                // Небольшая задержка для Grim AC
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        tryBreakRwWallBlockPacket();

        boolean shieldBroken = false;
        if (target instanceof PlayerEntity player && player.isBlocking() && breakShield.isState()) {
            shieldBroken = shieldBreak(player);
            // Задержка после ломания щита для Grim AC
            if (shieldBroken) {
                try {
                    Thread.sleep(50); // Увеличена задержка для Grim AC
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // Всегда атакуем после ломания щита или если щит не был сломан
        mc.interactionManager.attackEntity(mc.player, target);
        if (ModuleClass.elytraresolver != null) {
            ModuleClass.elytraresolver.onAuraAttack();
        }
        mc.player.swingHand(Hand.MAIN_HAND);

        // Восстанавливаем блокировку щита с задержкой для Grim AC
        if (wasBlocking && blockedShieldHand != null) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            restoreShieldBlocking(blockedShieldHand);
        }

        if (rotationType.is("WhiteRise")) slothRotation.onAttack();
        if (rotationType.is("Sloth")) whiteRiseRotation.onAttack();
        if (rotationType.is("FunTime")) funTimeRotation.onAttack();

        // NeuroLLM: сохраняем состояние перед атакой и запускаем проверку результата
        if (rotationType.is("NeuroLLM")) {
            neuroLLMRotation.preAttackWithTarget(target);
            neuroLLMRotation.onAttack();

            final LivingEntity attackedTarget = target;
            final float hpBefore = attackedTarget != null ? attackedTarget.getHealth() : 0f;

            new Thread(() -> {
                try {
                    Thread.sleep(150);
                    boolean hit = attackedTarget != null
                            && attackedTarget.isAlive()
                            && attackedTarget.getHealth() < hpBefore;
                    boolean flagged = neuroLLMRotation.detectFlag();
                    neuroLLMRotation.onAttackResult(hit, flagged);
                } catch (InterruptedException ignored) {}
            }, "NeuroLLM-Learn").start();
        }
        long cooldown = 467L;

        if (tpsSync != null && tpsSync.isEnable()) {
            cooldown = (long) (tpsSync.getAdjustedCooldown(cooldown) * 1.1f);
        }

        cps = System.currentTimeMillis() + cooldown;
        ticksToAttack = 10;
        attackTimer.reset();
    }

    private void tryBreakRwWallBlockPacket() {
        if (!rwWallBypass.isState() || target == null || mc.player == null || mc.world == null) return;
        if (mc.player.canSee(target)) return;
        if (mc.player.networkHandler == null) return;

        Vec3d start = mc.player.getEyePos();
        Vec3d end = target.getBoundingBox().getCenter();
        BlockHitResult hit = mc.world.raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                mc.player
        ));

        if (hit == null || hit.getType() != HitResult.Type.BLOCK) return;

        BlockPos blockPos = hit.getBlockPos();
        if (mc.world.getBlockState(blockPos).isAir()) return;
        if (mc.world.getBlockState(blockPos).getHardness(mc.world, blockPos) < 0.0f) return;

        Direction direction = hit.getSide() == null ? Direction.UP : hit.getSide();
        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, direction));
        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));
    }

    private boolean shieldBreak(PlayerEntity entity) {
        SlotSearchResult axeSlot = HotbarUtil.getAxe();
        if (!axeSlot.found()) {
            return false;
        }

        int previousSlot = mc.player.getInventory().selectedSlot;
        if (axeSlot.slot() == previousSlot) {
            // Задержка перед атакой для Grim AC
            try {
                Thread.sleep(8);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            mc.interactionManager.attackEntity(mc.player, entity);
            if (ModuleClass.elytraresolver != null) {
                ModuleClass.elytraresolver.onAuraAttack();
            }
            return true;
        }

        if (axeSlot.isInHotBar()) {
            return attackWithSilentHotbarSlot(entity, axeSlot.slot(), previousSlot);
        }

        if (mc.player.currentScreenHandler.syncId != 0) {
            return false;
        }

        int swapHotbarSlot = findSilentSwapHotbarSlot(previousSlot);
        if (swapHotbarSlot == -1) {
            return false;
        }

        swapInventoryIntoHotbar(axeSlot.slot(), swapHotbarSlot);
        boolean attacked = false;
        try {
            // Задержка после свапа для Grim AC
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            attacked = attackWithSilentHotbarSlot(entity, swapHotbarSlot, previousSlot);
        } finally {
            swapInventoryIntoHotbar(axeSlot.slot(), swapHotbarSlot);
        }
        return attacked;
    }

    private boolean attackWithSilentHotbarSlot(PlayerEntity entity, int attackSlot, int previousSlot) {
        if (mc.player == null || mc.player.networkHandler == null || mc.interactionManager == null) {
            return false;
        }

        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(attackSlot));
        
        // Задержка между сменой слота и атакой для Grim AC
        try {
            Thread.sleep(12);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        try {
            mc.interactionManager.attackEntity(mc.player, entity);
            if (ModuleClass.elytraresolver != null) {
                ModuleClass.elytraresolver.onAuraAttack();
            }
            return true;
        } finally {
            // Задержка перед возвратом слота для Grim AC
            try {
                Thread.sleep(8);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
        }
    }

    private void swapInventoryIntoHotbar(int inventorySlot, int hotbarSlot) {
        if (mc.player == null || mc.interactionManager == null || mc.player.networkHandler == null) return;

        int syncId = mc.player.currentScreenHandler.syncId;
        mc.interactionManager.clickSlot(syncId, inventorySlot, hotbarSlot, net.minecraft.screen.slot.SlotActionType.SWAP, mc.player);
        mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(syncId));
    }

    private int findSilentSwapHotbarSlot(int previousSlot) {
        for (int slot = 8; slot >= 0; slot--) {
            if (slot != previousSlot) {
                return slot;
            }
        }
        return previousSlot >= 0 && previousSlot < 9 ? previousSlot : -1;
    }

    private Hand getBlockingShieldHand() {
        if (mc.player == null || !mc.player.isBlocking()) {
            return null;
        }

        Hand activeHand = mc.player.getActiveHand();
        if (activeHand == null) {
            return null;
        }

        return isShieldStack(mc.player.getStackInHand(activeHand)) ? activeHand : null;
    }

    private boolean shouldBlockAttackWhileUsingItem() {
        if (mc.player == null || !mc.player.isUsingItem()) {
            return false;
        }

        return !(unpressShield.isState() && getBlockingShieldHand() != null);
    }

    private void restoreShieldBlocking(Hand hand) {
        if (mc.player == null || mc.interactionManager == null || hand == null) {
            return;
        }

        if (!isShieldStack(mc.player.getStackInHand(hand)) || mc.player.isUsingItem()) {
            return;
        }

        mc.interactionManager.interactItem(mc.player, hand);
    }

    private boolean isShieldStack(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ShieldItem;
    }

    private boolean isWeapon() {
        Item item = mc.player.getMainHandStack().getItem();
        return item != Items.AIR && (item instanceof SwordItem
                || item instanceof PickaxeItem
                || item instanceof AxeItem
                || item instanceof HoeItem
                || item instanceof ShovelItem
                || item instanceof MaceItem
                || item == Items.MACE);
    }

    private boolean isValidTarget(LivingEntity entity) {
        if (entity == null || entity == mc.player) return false;
        if (!entity.isAlive() || entity.getHealth() <= 0) return false;
        if (entity instanceof ArmorStandEntity) return false;
        if (entity instanceof IronGolemEntity || entity instanceof BatEntity) return false;

        if (AntiBot.checkBot(entity)) return false;

        if (entity instanceof PlayerEntity player) {
            if (!targets.is("Игроки")) return false;
            if (isNaked(player) && !targets.is("Голые")) return false;
            if (player.hasStatusEffect(StatusEffects.INVISIBILITY) && !targets.is("Невидимки")) return false;
            if (Volon.INSTANCE.friendStorage.isFriend(entity.getName().getString())) return false;
        } else if (entity instanceof PassiveEntity || entity instanceof CodEntity) {
            if (!targets.is("Мирные")) return false;
        } else if (entity instanceof HostileEntity) {
            if (!targets.is("Мобы")) return false;
        } else {
            return false;
        }

        Vec3d nearestPoint = BestPoint.getNearestPoint(entity);
        if (nearestPoint == null) nearestPoint = MultipointUtils.getClosestPoint(entity);
        if (mc.player.getEyePos().distanceTo(nearestPoint) > getMaxAimRange()) return false;
        if (!throughWalls.isState() && !rwWallBypass.isState() && !mc.player.canSee(entity)) return false;

        return true;
    }

    private boolean isNaked(PlayerEntity player) {
        for (ItemStack armorStack : player.getArmorItems()) {
            if (!armorStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean shouldAttack() {
        if (mc.player.getAttackCooldownProgress(1.5f) < IdealHitUtils.getAICooldown()) {
            return false;
        }

        EntityHitResult result = getAttackRaycastResult();

        if (raycast.isState() && !isUsingRwWallSnap()
                && (result != null ? result.getEntity() == null || result.getEntity() != target : true)
                && !(mc.player.isGliding() && target.isGliding())) {
            return false;
        }

        if (rotationType.is("Data") && !isUsingRwWallSnap() && !isDataAimReady(result)) {
            return false;
        }

        if (mc.player.isGliding() && target.isGliding()) {
            Vec3d targetPos = target.getPos().add(0, target.getHeight() / 2.0, 0);
            int predict = 0;
            if (ModuleClass.elytraTarget != null && ModuleClass.elytraTarget.isEnable()) {
                predict = ModuleClass.elytraTarget.forward.getValue().intValue();
            }
            Vec3d predictedPos = PredictUtils.predict(target, targetPos, predict);
            if (mc.player.getEyePos().distanceTo(predictedPos) > 5) return false;
        } else {
            double distanceCheck = mc.player.getEyePos().distanceTo(target.getBoundingBox().getCenter());
            Vec3d checkPoint = distanceCheck > 3 ? BestPoint.getNearestPoint(target) : target.getBoundingBox().getCenter();
            if (checkPoint == null) checkPoint = MultipointUtils.getClosestPoint(target);
            if (mc.player.getEyePos().distanceTo(checkPoint) > range.getValue().floatValue()) return false;
        }

        return IdealHitUtils.canCritical(target);
    }

    public boolean isReadyToAttack() {
        if (mc.player == null) return false;
        return mc.player.getAttackCooldownProgress(1.5f) >= IdealHitUtils.getAICooldown();
    }

    public int getWhiteRiseTicksToAttack() {
        return ticksToAttack;
    }

    private boolean isDataAimReady(EntityHitResult result) {
        float yawDiff = Math.abs(MathHelper.wrapDegrees(targetRotations.x - mc.player.getYaw()));
        float pitchDiff = Math.abs(targetRotations.y - mc.player.getPitch());
        boolean closeToAim = yawDiff <= 1.15f && pitchDiff <= 0.9f;
        boolean onTarget = result != null && result.getEntity() == target;

        return closeToAim && onTarget;
    }

    public boolean isAboveWater() {
        BlockPos pos = BlockPos.ofFloored(mc.player.getPos().add(0, -0.4, 0));
        return !mc.player.isSubmergedInWater() && mc.world.getBlockState(pos).isOf(Blocks.WATER);
    }

    public float getAttackCooldown() {
        return MathHelper.clamp(((float) ((ILivingEntity) mc.player).getLastAttackedTicks()) / getAttackCooldownProgressPerTick(), 0.0F, 1.0F);
    }

    public float getAttackCooldownProgressPerTick() {
        return (float) (1.0 / mc.player.getAttributeValue(EntityAttributes.ATTACK_SPEED) * 20);
    }

    private float getMaxAimRange() {
        return mc.player.isGliding()
                ? elytraAimRange.getValue().floatValue()
                : range.getValue().floatValue() + aimRange.getValue().floatValue();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (target != null) backTimer.reset();
        target = null;
        wellMineRotation.reset();
        testRotation.reset();
        slothRotation.reset();
        whiteRiseRotation.reset();
        dataSystem.resetState();
        lastDataTarget = null;
        needSprintReset = false;
        sprintResetDone = false;
        sprintResetTicks = 0;
        ticksToAttack = 0;
        resetSnapAttack();
        // Reset Droid state
        spookyIsLookingAway = false;
        spookyLastLookAwayTime = 0L;
        spookyLastPitchUpdateTime = 0L;
        droidLerpRotation = new float[]{0f, 0f};

        // Плавная отводка при выключении
        AntiCheatEvasion.INSTANCE.setMode(smoothEvasion.isState(), false);
        if (smoothEvasion.isState()) {
            AntiCheatEvasion.INSTANCE.startSmoothReturn();
        } else {
            AntiCheatEvasion.INSTANCE.reset();
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        wellMineRotation.reset();
        testRotation.reset();
        slothRotation.reset();
        whiteRiseRotation.reset();
        dataSystem.resetState();
        lastDataTarget = null;
        needSprintReset = false;
        sprintResetDone = false;
        sprintResetTicks = 0;
        ticksToAttack = 0;
        resetSnapAttack();
        if (mc.player != null) {
            currentRotations = new Vec2f(mc.player.getYaw(), mc.player.getPitch());
            // Init Droid lerp rotation
            droidLerpRotation = new float[]{mc.player.getYaw(), mc.player.getPitch()};
        }

        // Плавный старт при включении
        AntiCheatEvasion.INSTANCE.setMode(smoothEvasion.isState(), false);
        if (smoothEvasion.isState()) {
            AntiCheatEvasion.INSTANCE.startSmoothStart();
        } else {
            AntiCheatEvasion.INSTANCE.reset();
        }
    }

    private void resetSnapAttack() {
        snapAttackAge = -1;
        snapAttackQueued = false;
        snapAttackTarget = null;
    }

    private void handleDroidRotation(LivingEntity target) {
        if (mc.player == null) return;

        // ── НА ЭЛИТРАХ: используем Smooth логику с рандомизацией ──────────────
        if (mc.player.isGliding()) {
            // Smooth rotation logic (как в Smooth режиме)
            Vec3d relativePos = target.getPos().add(0, target.getHeight() * 0.6f, 0).subtract(mc.player.getEyePos());
            
            // Предсказание на элитрах (как в Smooth)
            if (target.isGliding() && ModuleClass.elytraTarget.isEnable()) {
                Vec3d interpolatedRotation = Vec3d.fromPolar(target.getLerpTargetPitch(), target.getLerpTargetYaw());
                Vec3d rotationVector = target.getRotationVector();
                Vec3d blendedDirection = interpolatedRotation.normalize().lerp(rotationVector, interpolatedRotation.length());
                relativePos = relativePos.add(blendedDirection.normalize().multiply(ModuleClass.elytraTarget.forward.getValue().floatValue()));
            }
            
            final float yaw = (float) wrapDegrees(Math.toDegrees(Math.atan2(relativePos.z, relativePos.x)) - 90);
            float pitch = (float) (-Math.toDegrees(Math.atan2(relativePos.y, Math.hypot(relativePos.x, relativePos.z))));
            
            // Применяем ротацию с максимальной скоростью (как Smooth)
            RotationStorage.update(new Rotation(yaw, pitch), 360, 360, 360, 360, 1, 1, clientLook.isState());
            return;
        }

        // ── НА ЗЕМЛЕ: стандартная ArtyGrief логика с рандомизацией ───────────
        // Init lerp rotation if zero
        if (droidLerpRotation[0] == 0f && droidLerpRotation[1] == 0f) {
            droidLerpRotation[0] = mc.player.getYaw();
            droidLerpRotation[1] = mc.player.getPitch();
        }

        // Target point at 80% height (like original)
        Vec3d vecs = new Vec3d(
                target.getX(),
                target.getY() + target.getHeight() * 0.8f,
                target.getZ()
        );
        Vec3d playerPosition = mc.player.getEyePos();
        Vec3d direction = vecs.subtract(playerPosition).normalize();

        float rawYaws = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
        float rawPitchs = (float) MathHelper.clamp(
                -Math.toDegrees(Math.atan2(direction.y, Math.hypot(direction.x, direction.z))),
                -89.0, 89.0
        );

        long now = System.currentTimeMillis();
        java.util.Random rng = new java.util.Random();

        // Check if can crit (attack cooldown >= 0.92)
        boolean canDoCrit = mc.player.getAttackCooldownProgress(1.5f) >= 0.92f;

        if (!canDoCrit) {
            // Trigger look-away every 2000ms when not critting
            if (!spookyIsLookingAway && now - spookyLastLookAwayTime >= 2000L) {
                spookyIsLookingAway = true;
                spookyLookAwayStartTime = now;

                float angleOffset = 180.0f + (rng.nextFloat() * 40.0f - 20.0f);
                angleOffset *= (rng.nextBoolean() ? 1 : -1);
                spookyLookAwayTargetYaw = rawYaws + angleOffset;

                float pitchOffset = 45.0f + (rng.nextFloat() * 40.0f - 20.0f);
                pitchOffset *= (rng.nextBoolean() ? 1 : -1);
                spookyLookAwayTargetPitch = MathHelper.clamp(rawPitchs + pitchOffset, -89.0f, 89.0f);
            }

            if (spookyIsLookingAway) {
                long lookAwayDuration = now - spookyLookAwayStartTime;

                if (lookAwayDuration < 80L) {
                    // Phase 1: look away (ease out cubic)
                    float progress = (float) lookAwayDuration / 80.0f;
                    float easedProgress = 1.0f - (float) Math.pow(1.0f - progress, 3.0);
                    rawYaws += (spookyLookAwayTargetYaw - rawYaws) * easedProgress;
                    rawPitchs += (spookyLookAwayTargetPitch - rawPitchs) * easedProgress;

                } else if (lookAwayDuration < 160L) {
                    // Phase 2: return back (ease in cubic)
                    long returnDuration = lookAwayDuration - 80L;
                    float returnProgress = (float) returnDuration / 80.0f;
                    float easedProgress = (float) Math.pow(returnProgress, 3.0);

                    Vec3d currentVecs = new Vec3d(
                            target.getX(),
                            target.getY() + target.getHeight() * 0.8f,
                            target.getZ()
                    );
                    Vec3d currentDir = currentVecs.subtract(playerPosition).normalize();
                    float targetYaw = (float) Math.toDegrees(Math.atan2(-currentDir.x, currentDir.z));
                    float targetPitch = (float) MathHelper.clamp(
                            -Math.toDegrees(Math.atan2(currentDir.y, Math.hypot(currentDir.x, currentDir.z))),
                            -89.0, 89.0
                    );

                    rawYaws = spookyLookAwayTargetYaw + (targetYaw - spookyLookAwayTargetYaw) * easedProgress;
                    rawPitchs = spookyLookAwayTargetPitch + (targetPitch - spookyLookAwayTargetPitch) * easedProgress;

                } else {
                    spookyIsLookingAway = false;
                    spookyLastLookAwayTime = now;
                }
            }
        } else if (spookyIsLookingAway) {
            // Can crit — cancel look-away, set cooldown so it won't trigger again for 500ms
            spookyIsLookingAway = false;
            spookyLastLookAwayTime = now - 2000L + 500L;
        }

        // Lerp rotation with speed limits
        float yawDelta = MathHelper.wrapDegrees(rawYaws - droidLerpRotation[0]);
        float pitchDelta = MathHelper.wrapDegrees(rawPitchs - droidLerpRotation[1]);

        float yawSpeedMultiplier = spookyIsLookingAway ? 3.0f : 1.0f;

        // GCD sensitivity value
        float gcd = mc.options.getMouseSensitivity().getValue().floatValue() * 0.6f + 0.2f;
        gcd = gcd * gcd * gcd * 1.2f;

        float maxYawSpeed = 8.5f * yawSpeedMultiplier;
        float clampedYaw = Math.min(Math.abs(yawDelta), maxYawSpeed);

        float clampedPitch;
        if (now - spookyLastPitchUpdateTime >= 290L) {
            clampedPitch = Math.min(Math.abs(pitchDelta), 8.0f);
            spookyLastPitchUpdateTime = now;
        } else {
            clampedPitch = Math.min(Math.abs(pitchDelta), 3.0f);
        }

        float finalYaw = droidLerpRotation[0] + (yawDelta > 0.0f ? clampedYaw : -clampedYaw);
        float finalPitch = droidLerpRotation[1] + (pitchDelta > 0.0f ? clampedPitch : -clampedPitch);
        finalPitch = MathHelper.clamp(finalPitch, -89.0f, 89.0f);

        // Apply GCD
        finalYaw -= (finalYaw - droidLerpRotation[0]) % gcd;
        finalPitch -= (finalPitch - droidLerpRotation[1]) % gcd;

        droidLerpRotation[0] = finalYaw;
        droidLerpRotation[1] = finalPitch;

        float rotationSpeed = spookyIsLookingAway ? 8.0f : 3.0f;

        targetRotations = new Vec2f(finalYaw, finalPitch);
        currentRotations = new Vec2f(mc.player.getYaw(), mc.player.getPitch());

        RotationStorage.update(
                new Rotation(finalYaw, finalPitch),
                rotationSpeed * 12f,
                rotationSpeed * 10f,
                rotationSpeed * 4f,
                rotationSpeed * 4f,
                1,
                5,
                clientLook.isState()
        );
    }
}

