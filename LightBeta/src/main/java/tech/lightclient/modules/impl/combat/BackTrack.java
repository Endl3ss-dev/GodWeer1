package tech.lightclient.modules.impl.combat;

import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.events.impl.entity.EventAttack;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.render.main.world.WorldGeometryEmitter;
import tech.lightclient.client.utils.render.main.world.WorldLineRenderer;
import tech.lightclient.client.utils.render.main.world.WorldRenderLayers;
import tech.lightclient.client.utils.render.main.world.WorldRenderer;
import tech.lightclient.mixins.accessors.GameRendererAccessor;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@FunctionAdd(name = "BackTrack", alias = "Back Track", category = Category.Combat,
        description = "Delays target position packets to increase attack reach")
public class BackTrack extends Function {
    private final SliderSetting distance = new SliderSetting("Target distance", 3.0f, 1.0f, 10.0f, 0.1f);
    private final SliderSetting delay = new SliderSetting("Packet delay", 150.0f, 10.0f, 1000.0f, 10.0f);
    private final SliderSetting holdTime = new SliderSetting("Remember target", 500.0f, 0.0f, 2000.0f, 50.0f);
    private final SliderSetting afterAttack = new SliderSetting("Work after attack", 1000.0f, 0.0f, 5000.0f, 50.0f);
    private final BooleanSetting renderBox = new BooleanSetting("Render delayed position", true);

    private final Queue<DelayedPacket> inboundQueue = new ConcurrentLinkedQueue<>();
    private final Queue<Packet<?>> releaseQueue = new ConcurrentLinkedQueue<>();

    private Entity target;
    private Vec3d trackedPos;
    private long lastTargetSeen;
    private long lastAttackTime;
    private int releaseDelayMs;

    public BackTrack() {
        addSettings(distance, delay, holdTime, afterAttack, renderBox);
    }

    @EventHandler
    public void onAttack(EventAttack.Swing event) {
        if (nullCheck()) {
            return;
        }

        Entity attacked = event.getTarget();
        if (isValidTarget(attacked)) {
            lastAttackTime = System.currentTimeMillis();
            if (attacked != target) {
                reset(false);
                trackedPos = attacked.getEntityPos();
            }
            target = attacked;
        }
    }

    @EventHandler
    public void onPacket(EventPacket event) {
        if (nullCheck() || !event.isReceive() || event.isCancelled()) {
            return;
        }

        if (inboundQueue.isEmpty() && !isActive()) {
            return;
        }

        Packet<?> packet = event.getPacket();

        // These packets expose current state and must not be delayed.
        if (packet instanceof PlaySoundS2CPacket soundPacket
                && soundPacket.getSound().value() == SoundEvents.ENTITY_PLAYER_HURT) {
            return;
        }
        if (packet instanceof WorldEventS2CPacket || packet instanceof HealthUpdateS2CPacket) {
            return;
        }

        if (target != null) {
            int targetId = target.getId();
            if (packet instanceof EntityPositionS2CPacket positionPacket && positionPacket.entityId() == targetId) {
                updateTrackedPos(target.getEntityPos());
            } else if (packet instanceof EntityVelocityUpdateS2CPacket velocityPacket
                    && velocityPacket.getEntityId() == targetId) {
                updateTrackedPos(target.getEntityPos());
            } else if (packet instanceof EntityDamageS2CPacket damagePacket && damagePacket.entityId() == targetId) {
                release(true);
                return;
            }
        }

        if (shouldDelay(packet)) {
            event.cancel();
            inboundQueue.add(new DelayedPacket(packet, System.currentTimeMillis()));
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent ignored) {
        if (nullCheck()) {
            reset(true);
            return;
        }

        if (isActive()) {
            releaseExpired(false);
        } else {
            reset(false);
        }

        Packet<?> packet;
        while ((packet = releaseQueue.poll()) != null) {
            if (mc.player.networkHandler != null) {
                applyPacket(packet);
            }
        }

        if (inboundQueue.isEmpty() && releaseQueue.isEmpty()) {
            releaseDelayMs = delay.get().intValue();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void applyPacket(Packet<?> packet) {
        ((Packet) packet).apply(mc.player.networkHandler);
    }

    @EventHandler
    public void onRenderWorld(EventRender.World event) {
        if (!renderBox.get() || target == null || trackedPos == null || mc.player == null || mc.world == null) {
            return;
        }

        Camera camera = mc.gameRenderer.getCamera();
        float fov = ((GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, event.getTicks(), true);
        Vec3d cameraPos = camera.getCameraPos();
        double halfWidth = target.getWidth() / 2.0;
        Box box = new Box(
                trackedPos.x - halfWidth,
                trackedPos.y,
                trackedPos.z - halfWidth,
                trackedPos.x + halfWidth,
                trackedPos.y + target.getHeight(),
                trackedPos.z + halfWidth);

        try (WorldRenderer renderer = WorldRenderer.begin(
                mc,
                mc.getRenderTickCounter(),
                camera,
                event.getMatrixStack().peek().getPositionMatrix(),
                mc.gameRenderer.getBasicProjectionMatrix(fov))) {
            WorldGeometryEmitter emitter = new WorldGeometryEmitter(
                    camera,
                    new MatrixStack().peek(),
                    renderer.getBuffer(WorldRenderLayers.POSITION_COLOR_QUADS_ALPHA_SRC_ONE_NO_DEPTH()));
            WorldLineRenderer.box(emitter, box, cameraPos, 0xFF78B4FF, 2.0);
            renderer.flush();
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void onDisable() {
        reset(true);
        super.onDisable();
    }

    private boolean isActive() {
        if (target == null || !target.isAlive() || mc.player == null) {
            return false;
        }

        double distanceToTarget = target.distanceTo(mc.player);
        boolean inRange = distanceToTarget <= distance.get();
        if (inRange) {
            lastTargetSeen = System.currentTimeMillis();
        }

        boolean remembered = System.currentTimeMillis() - lastTargetSeen < holdTime.get().longValue();
        boolean recentlyAttacked = System.currentTimeMillis() - lastAttackTime < afterAttack.get().longValue();
        return (inRange || remembered) && target.age > 10 && recentlyAttacked;
    }

    private boolean isValidTarget(Entity entity) {
        return entity != null
                && entity != mc.player
                && entity instanceof LivingEntity living
                && living.isAlive()
                && entity.distanceTo(mc.player) <= distance.get();
    }

    private boolean shouldDelay(Packet<?> packet) {
        return (packet instanceof EntityPositionS2CPacket || packet instanceof EntityVelocityUpdateS2CPacket)
                && isActive();
    }

    private void updateTrackedPos(Vec3d pos) {
        if (pos != null) {
            trackedPos = pos;
        }
    }

    private void releaseExpired(boolean force) {
        inboundQueue.removeIf(entry -> {
            if (!force && System.currentTimeMillis() - entry.time() < releaseDelayMs) {
                return false;
            }
            releaseQueue.add(entry.packet());
            return true;
        });
    }

    private void release(boolean force) {
        releaseExpired(force);
        if (force) {
            target = null;
            trackedPos = null;
        }
    }

    private void reset(boolean forceRelease) {
        if (forceRelease) {
            release(true);
        } else {
            inboundQueue.clear();
        }
        target = null;
        trackedPos = null;
    }

    private record DelayedPacket(Packet<?> packet, long time) {
    }
}
