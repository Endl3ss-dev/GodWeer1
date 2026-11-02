package tech.lightclient.modules.impl.movement;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.Vec3d;
import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.events.impl.client.PostEvent;
import tech.lightclient.client.events.impl.client.TickEvent;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.events.impl.player.MoveInputEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.player.PlayerInventoryUtil;
import tech.lightclient.client.utils.player.PlayerUtils;
import tech.lightclient.client.utils.player.TimerController;
import tech.lightclient.client.utils.player.rotation.RotationTask;
import tech.lightclient.client.utils.player.swap.InvBypassMode;
import tech.lightclient.client.utils.player.swap.InventoryTasks;
import tech.lightclient.client.utils.player.swap.SwapConfig;
import tech.lightclient.client.utils.player.swap.SwapInventory;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FunctionAdd(name = "Speed", alias = "Speed", category = Category.Movement, description = "Позволяет двигаться быстрее")
public final class Speed extends Function {
    public static final Speed INSTANCE = new Speed();

    private static final double STRONG_GROUND_BOOST = 0.055D;
    private static final double NORMAL_GROUND_BOOST = 0.03D;
    private static final double BOOST_STRAFE_SPEED = 0.05D;
    private static final double BOOST_VERTICAL_OFFSET = 0.015D;

    private final ModeSetting mode;
    private final ModeSetting swapMode;
    // Collision settings
    private final SliderSetting collisionSpeed;
    private final BooleanSetting collisionTarget;
    private final BooleanSetting collisionOverpass;
    private final Map<Entity, Vec3d> previousPositions;

    private int ticks;
    private int groundTicks;
    private int setbackBoostTicks;

    // Перегон: состояние
    private boolean overpassActive = false;
    private Entity overpassTarget = null;

    public Speed() {
        mode = new ModeSetting("Mode", "RW", "RW", "Collision", "HolyWorld");
        swapMode = new ModeSetting("Режим свапа", "FunTime", "FunTime", "HolyWorld").setVisible(() -> mode.is("HolyWorld"));
        collisionSpeed   = new SliderSetting("Скорость", 0.032f, 0.005f, 0.2f, 0.005f).setVisible(() -> mode.is("Collision"));
        collisionTarget  = new BooleanSetting("Таргет",  false).setVisible(() -> mode.is("Collision"));
        collisionOverpass = new BooleanSetting("Перегон", false).setVisible(() -> mode.is("Collision"));
        previousPositions = new HashMap<>();
        addSettings(mode, swapMode, collisionSpeed, collisionTarget, collisionOverpass);
    }

    @Override
    public void onEnable() {
        resetRWState(true);
        resetHolyState();
        if (mode.is("HolyWorld")) {
            equipHolyElytra();
            // Пауза на время свапа - после него спид начинает работать
            holyStartDelayTicks = swapMode.is("HolyWorld") ? 3 : 10;
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        previousPositions.clear();
        resetRWState(true);
        boolean wasHoly = mode.is("HolyWorld");
        resetHolyState();
        if (wasHoly) {
            unequipHolyElytra();
        }
        overpassActive = false;
        overpassTarget = null;
        super.onDisable();
    }

    // ==== HolyWorld: элитра-хопы (автопрыжок, автозапуск полёта, наклон вниз) ====

    private static final String HOLY_ROTATION_TASK = "holyworld";
    private static final int HOLY_ROTATION_PRIORITY = 40;

    private boolean holyWasOnGround;
    /** Тики ожидания, пока идёт стартовый свап элитры - полётный режим не активен. */
    private int holyStartDelayTicks;

    @EventHandler
    public void onHolyMoveInput(MoveInputEvent event) {
        if (!mode.is("HolyWorld") || mc.player == null || !hasHolyElytra()) {
            return;
        }

        // Идёт стартовый свап элитры - полётный режим ещё не работает
        if (holyStartDelayTicks > 0) {
            return;
        }

        // Постоянно летим вперёд
        event.forward(true);
        event.backward(false);

        boolean onGround = mc.player.isOnGround();
        boolean gliding = mc.player.isGliding();

        if (onGround) {
            // Стоим на земле - прыгаем, чтобы запустить цепочку подлётов
            event.jump(true);
            holyWasOnGround = true;
            return;
        }

        if (gliding) {
            holyWasOnGround = false;
            return;
        }

        // В воздухе без полёта: после прыжка с земли ждём момент, иначе сразу расправляем крылья
        if (!holyWasOnGround) {
            startHolyFallFlying();
        } else {
            holyWasOnGround = false;
        }
    }

    @EventHandler
    public void onHolyTick(TickEvent event) {
        if (!mode.is("HolyWorld")) return;

        // Отсчёт задержки старта, пока идёт фазовый свап элитры
        if (holyStartDelayTicks > 0) {
            holyStartDelayTicks--;
            return;
        }

        if (!hasHolyElytra() || !mc.player.isGliding()) return;

        // Держим взгляд к земле - набор скорости на элитрах
        RotationTask.create(HOLY_ROTATION_TASK, HOLY_ROTATION_PRIORITY);
        RotationTask.setTargetRotation(mc.player.getYaw(), 68.0F, 1000.0F, HOLY_ROTATION_PRIORITY);
    }

    /**
     * Надевает элитру при включении режима.
     * FunTime - фазовый свап (короткая остановка движения), HolyWorld - мгновенный свап без остановки.
     * Если элитра уже надета - ничего не делаем.
     */
    private void equipHolyElytra() {
        if (mc.player == null || mc.interactionManager == null) return;

        ItemStack chest = mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if (chest.getItem().equals(Items.ELYTRA)) return;

        Slot target = PlayerInventoryUtil.getSlot(Items.ELYTRA);
        if (target != null) {
            swapArmor(target.id, true);
        }
    }

    /**
     * Возвращает нагрудник при выключении режима.
     * Если элитра не надета - ничего не делаем.
     */
    private void unequipHolyElytra() {
        if (mc.player == null || mc.interactionManager == null) return;

        ItemStack chest = mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if (!chest.getItem().equals(Items.ELYTRA)) return;

        Slot chestplate = PlayerInventoryUtil.slots()
                .filter(s -> s.getStack().getItem().toString().contains("chestplate"))
                .findFirst().orElse(null);

        if (chestplate != null) {
            swapArmor(chestplate.id, false);
        }
    }

    /**
     * FunTime - как было до режимов: надевание фазовым свапом (короткая остановка), снятие мгновенным.
     * HolyWorld - мгновенный свап через очередь, без остановки движения.
     */
    private void swapArmor(int from, boolean phased) {
        if (swapMode.is("HolyWorld")) {
            SwapConfig.invBypassMode = InvBypassMode.HOLY_WORLD;
            SwapConfig.inventoryStopMoving = false;
            if (!InventoryTasks.isRealCompleted(Speed.class)) return;
            InventoryTasks.addTask(Speed.class, () -> SwapInventory.swapSlots(from, 6, true));
        } else if (phased) {
            PlayerInventoryUtil.swapItemsPhasedDirect(from, 6, true);
        } else {
            PlayerInventoryUtil.swapItemsDirect(from, 6, true);
        }
    }

    private boolean hasHolyElytra() {
        return mc.player != null
                && mc.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA);
    }

    private void startHolyFallFlying() {
        if (mc.player == null || mc.getNetworkHandler() == null) {
            return;
        }

        mc.player.startGliding();
        mc.getNetworkHandler().sendPacket(
                new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
    }

    private void resetHolyState() {
        holyWasOnGround = false;
        holyStartDelayTicks = 0;
        if (RotationTask.rotationPriority > HOLY_ROTATION_PRIORITY) {
            RotationTask.remove(HOLY_ROTATION_TASK);
            return;
        }

        RotationTask.rotationState = RotationTask.RotationState.RESET;
        RotationTask.returnStartTime = System.currentTimeMillis() - 100L;
        RotationTask.rotationPriority = 0;
        RotationTask.inactiveMs = 0L;
        RotationTask.needSmoothReset = true;
        RotationTask.resetDelayMs = 400L;
        RotationTask.remove(HOLY_ROTATION_TASK);
    }

    @EventHandler
    public void onUpdate(UpdateEvent ignored) {
        if (mode.is("RW")) {
            if (setbackBoostTicks > 0) {
                setbackBoostTicks--;
            }
            return;
        }

        if (mc.player == null || mc.world == null) {
            return;
        }

        previousPositions.keySet().removeIf(entity -> entity.isRemoved() || !entity.isAlive());

        // Перегон: если цель исчезла — сбрасываем
        if (overpassTarget != null && (overpassTarget.isRemoved() || !overpassTarget.isAlive())) {
            overpassActive = false;
            overpassTarget = null;
        }

        if (!PlayerUtils.hasPlayerMovement()) {
            return;
        }

        final double baseSpeed   = collisionSpeed.get();       // значение слайдера
        final boolean doTarget   = collisionTarget.get();        // Таргет: доп. вжим
        final boolean doOverpass = collisionOverpass.get();      // Перегон

        List<Entity> nearbyPlayers = mc.world.getOtherEntities(mc.player, mc.player.getBoundingBox().expand(1.25D));
        for (Entity entity : nearbyPlayers) {
            if (entity == mc.player || !(entity instanceof PlayerEntity)) {
                continue;
            }

            double offsetX = Math.abs(mc.player.getX() - entity.getX());
            double offsetZ = Math.abs(mc.player.getZ() - entity.getZ());
            if (offsetX > 2.1D || offsetZ > 1.3D) {
                continue;
            }

            double entitySpeed = getEntitySpeed(entity);

            // ── Перегон ──────────────────────────────────────────────────────────
            // Когда враг быстро движется — летим вперёд него и встаём перед лицом.
            if (doOverpass && entitySpeed >= 5.0D) {
                overpassTarget = entity;

                // Угол взгляда врага (его yaw → направление движения)
                double targetYaw = Math.toRadians(entity.getYaw());
                // Хотим оказаться на ~0.6 блока впереди носа противника
                double aheadDist = 0.6D;
                double targetX = entity.getX() - Math.sin(targetYaw) * aheadDist;
                double targetZ = entity.getZ() + Math.cos(targetYaw) * aheadDist;

                double dx = targetX - mc.player.getX();
                double dz = targetZ - mc.player.getZ();
                double dist = Math.sqrt(dx * dx + dz * dz);

                if (dist > 0.05D) {
                    // Двигаемся к точке перед лицом врага с повышенной скоростью
                    double overpassSpeed = baseSpeed * 2.5D;
                    double nx = dx / dist;
                    double nz = dz / dist;
                    mc.player.addVelocity(nx * overpassSpeed, 0.0D, nz * overpassSpeed);
                } else {
                    // Мы уже перед ним — удерживаем позицию, тормозим горизонталь
                    mc.player.setVelocity(mc.player.getVelocity().multiply(0.3D, 1.0D, 0.3D));
                }
                return;
            }

            // ── Медленный/стоящий враг ───────────────────────────────────────────
            if (entitySpeed < 5.0D) {
                List<Entity> touching = mc.world.getOtherEntities(mc.player, mc.player.getBoundingBox().expand(0.1D));
                int touchingCount = 0;
                for (Entity other : touching) {
                    if (other != mc.player
                            && (!(other instanceof ArmorStandEntity) || other instanceof BoatEntity)) {
                        touchingCount++;
                    }
                }

                if (touchingCount > 0) {
                    // Базовый слайдер-буст при касании
                    double[] motion = forward(baseSpeed * 0.625D);
                    mc.player.addVelocity(motion[0], 0.0D, motion[1]);

                    // Таргет: дополнительный направленный вжим прямо в противника
                    if (doTarget) {
                        double dx = entity.getX() - mc.player.getX();
                        double dz = entity.getZ() - mc.player.getZ();
                        double dist = Math.sqrt(dx * dx + dz * dz);
                        if (dist > 1e-4) {
                            double targetPush = baseSpeed * 0.5D;
                            mc.player.addVelocity(
                                    (dx / dist) * targetPush,
                                    0.0D,
                                    (dz / dist) * targetPush);
                        }
                    }
                }
                return;
            }

            // ── Быстро движущийся враг ───────────────────────────────────────────
            List<Entity> colliding = mc.world.getOtherEntities(mc.player, mc.player.getBoundingBox().expand(1.25D));
            int collidingCount = 0;
            for (Entity other : colliding) {
                if (other != mc.player
                        && (!(other instanceof ArmorStandEntity) || other instanceof BoatEntity)
                        && mc.player.distanceTo(other) <= 1.25D) {
                    collidingCount++;
                }
            }

            if (collidingCount > 0) {
                // Основной коллизионный буст со слайдером
                double[] motion = forward(baseSpeed);
                mc.player.addVelocity(motion[0], 0.0D, motion[1]);

                // Таргет: дополнительный вжим прямо в тело противника
                if (doTarget) {
                    double dx = entity.getX() - mc.player.getX();
                    double dz = entity.getZ() - mc.player.getZ();
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    if (dist > 1e-4) {
                        double targetPush = baseSpeed * 0.75D;
                        mc.player.addVelocity(
                                (dx / dist) * targetPush,
                                0.0D,
                                (dz / dist) * targetPush);
                    }
                }
            }
            return;
        }

        // Ни одного подходящего игрока рядом — сбрасываем перегон
        if (overpassActive) {
            overpassActive = false;
            overpassTarget = null;
        }
    }

    @EventHandler
    public void onMovePost(MoveInputEvent event) {
        if (!mode.is("RW") || !canUseRW() || mc.player == null) {
            resetRWState(true);
            return;
        }

        TimerController.setTimerSpeed(1.7F);

        if (ticks > 3) {
            if (setbackBoostTicks >= 1) {
                applyGroundBoost(BOOST_STRAFE_SPEED, BOOST_VERTICAL_OFFSET);
            } else {
                double boost = NORMAL_GROUND_BOOST;
                if (ticks % 2 == 0) {
                    boost = mc.player.isOnGround() ? STRONG_GROUND_BOOST : NORMAL_GROUND_BOOST;
                }
                applyGroundBoost(boost, 0.0D);
            }
        }

        ticks++;
    }

    @EventHandler
    public void onPostUpdate(PostEvent event) {
        if (!mode.is("RW")) {
            return;
        }

        if (!canUseRW()) {
            resetRWState(true);
            return;
        }

        if (ticks % 2 == 0) {
            TimerController.setTimerSpeed(0.3F);
            mc.player.networkHandler.sendPacket(
                    new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        }
    }

    @EventHandler
    public void onMoveInput(MoveInputEvent event) {
        if (!mode.is("RW") || mc.player == null || !PlayerUtils.hasPlayerMovement()) {
            groundTicks = 0;
            return;
        }

        if (mc.player.verticalCollision) {
            groundTicks++;
        } else {
            groundTicks = 0;
        }

        if (groundTicks >= 1) {
            mc.player.jump();
        }
    }

    @EventHandler
    public void onPacket(EventPacket event) {
        if (!mode.is("RW") || mc.player == null || !event.isReceive()) {
            return;
        }

        Packet<?> packet = event.getPacket();
        if (packet instanceof PlayerPositionLookS2CPacket) {
            setbackBoostTicks = 20;
            if (ticks % 3 == 1) {
                ticks++;
            }
            TimerController.setTimerSpeed(1.9F);
        }
    }

    private boolean canUseRW() {
        return mc.player != null
                && mc.world != null
                && mc.player.networkHandler != null
                && PlayerUtils.hasPlayerMovement()
                && !mc.player.hasVehicle()
                && !mc.player.getAbilities().flying;
    }

    private void resetRWState(boolean resetTimer) {
        ticks = 0;
        groundTicks = 0;
        setbackBoostTicks = 0;
        if (resetTimer) {
            TimerController.reset();
        }
    }

    private void applyGroundBoost(double horizontalBoost, double verticalBoost) {
        if (mc.player == null) {
            return;
        }
        double[] motion = forward(horizontalBoost);
        mc.player.addVelocity(motion[0], verticalBoost, motion[1]);
    }

    private double getEntitySpeed(Entity entity) {
        Vec3d position = new Vec3d(entity.getX(), entity.getY(), entity.getZ());
        Vec3d previous = previousPositions.getOrDefault(entity, position);

        double deltaX = position.x - previous.x;
        double deltaZ = position.z - previous.z;
        double blocksPerSecond = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 20.0D;

        previousPositions.put(entity, position);
        return blocksPerSecond;
    }

    private double[] forward(double speedValue) {
        float forward = mc.player.input.getMovementInput().y;
        float sideways = mc.player.input.getMovementInput().x;
        float yaw = mc.player.getYaw();

        if (forward != 0.0F) {
            if (sideways > 0.0F) {
                yaw += forward > 0.0F ? -45.0F : 45.0F;
            } else if (sideways < 0.0F) {
                yaw += forward > 0.0F ? 45.0F : -45.0F;
            }
            sideways = 0.0F;
            forward = forward > 0.0F ? 1.0F : -1.0F;
        }

        double sin = Math.sin(Math.toRadians(yaw + 90.0F));
        double cos = Math.cos(Math.toRadians(yaw + 90.0F));

        return new double[] {
                forward * speedValue * cos + sideways * speedValue * sin,
                forward * speedValue * sin - sideways * speedValue * cos
        };
    }
}
