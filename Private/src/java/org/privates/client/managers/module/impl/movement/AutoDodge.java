package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.Items;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.potion.*;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Beta;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.MoveInputEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.InvUtil;
import org.privates.lib.util.time.StopWatch;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Category3
@Beta
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoDodge", category = Category.MOVEMENT, description = "Уворачивается от плохих летящих предметов")
public class AutoDodge extends Module {
    public static AutoDodge getInstance() {
        return Instance.get(AutoDodge.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Авто", "Уворот", "Пласт");

    private final Map<Entity, Vector3d> potions = new HashMap<>();
    private final StopWatch checkTimer = new StopWatch();

    @EventHandler
    public void onMove(MoveInputEvent event) {
        for (Entry<Entity, Vector3d> potion : potions.entrySet()) {
            Entity ent = potion.getKey();
            Vector3d hitPos = potion.getValue();

            if (mode.is("Авто")) {
                if (mc.player.getCooldownTracker().hasCooldown(Items.DRIED_KELP) || !checkTimer.finished(2000) || mc.player.getDistance(ent) > 7) {
                    if (mc.player.getPositionVec().distanceTo(hitPos) > 6) continue;

                    event.setForward(1);
                    event.setJump(!mc.player.isPotionActive(Effects.SPEED) || mc.player.collidedHorizontally);

                    if (event.isJump() && mc.player.isPotionActive(Effects.SLOWNESS) && mc.player.getActivePotionEffect(Effects.SLOWNESS).getAmplifier() > mc.player.getActivePotionEffect(Effects.SPEED).getAmplifier()) {
                        event.setJump(true);
                    }

                    float yaw = calculateYaw(mc.player.getPosX(), mc.player.getPosZ(), hitPos.x, hitPos.z) - 180;
                    mc.player.rotationYaw = yaw;
                } else {
                    if (mc.player.getPositionVec().distanceTo(hitPos) > 6) continue;

                    Vector3d vec = ent.getPositionVec().subtract(mc.player.getEyePosition(mc.getRenderPartialTicks())).normalize();
                    float yaw = (float) Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0F;
                    float pitch = (float) Math.toDegrees(-Math.atan2(vec.y, Math.sqrt(vec.x * vec.x + vec.z * vec.z)));

                    mc.player.rotationYaw = yaw;
                    mc.player.rotationPitch = pitch;

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.DRIED_KELP) && mc.player.getDistance(ent) > 3 && ent.ticksExisted > 1) {
                        InvUtil.findItemAndThrow(Items.DRIED_KELP, mc.player.rotationYaw, mc.player.rotationPitch);
                    }
                }
            } else {
                if (mode.is("Уворот")) {
                    if (mc.player.getPositionVec().distanceTo(hitPos) > 6) continue;

                    event.setForward(1);
                    event.setJump(!mc.player.isPotionActive(Effects.SPEED) || mc.player.collidedHorizontally);

                    float yaw = calculateYaw(mc.player.getPosX(), mc.player.getPosZ(), hitPos.x, hitPos.z) - 180;
                    mc.player.rotationYaw = yaw;
                }

                if (mode.is("Пласт")) {
                    if (mc.player.getPositionVec().distanceTo(hitPos) > 6) continue;

                    Vector3d vec = ent.getPositionVec().subtract(mc.player.getEyePosition(mc.getRenderPartialTicks())).normalize();
                    float yaw = (float) Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0F;
                    float pitch = (float) Math.toDegrees(-Math.atan2(vec.y, Math.sqrt(vec.x * vec.x + vec.z * vec.z)));

                    mc.player.rotationYaw = yaw;
                    mc.player.rotationPitch = pitch;

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.DRIED_KELP) && mc.player.getDistance(ent) > 3 && ent.ticksExisted > 1) {
                        InvUtil.findItemAndThrow(Items.DRIED_KELP, mc.player.rotationYaw, mc.player.rotationPitch);
                    }
                }
            }

            break;
        }
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (event.isReceive() && event.getPacket() instanceof SChatPacket packet) {
            String message = packet.getChatComponent().getString();

            if (message.contains("Здесь уже стоит трапка/пласт")) {
                checkTimer.reset();
            }
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        potions.clear();

        for (Entity ent : mc.world.getAllEntities()) {
            if (ent instanceof PotionEntity pot) {
                Vector3d motion = ent.getMotion();
                Vector3d pos = ent.getPositionVec();
                Vector3d prevPos;
                Vector3d startPos = null;
                double distance = 0;
                double ticks = 0;
                for (int i = 0; i < 150; i++) {
                    prevPos = pos;
                    pos = pos.add(motion);
                    distance += prevPos.distanceTo(pos);
                    Vector3d motion1 = ent.getMotion();
                    double speed = Math.hypot(motion1.x, motion1.z);
                    motion = calculateNext(ent, motion);
                    if (startPos == null) startPos = prevPos;
                    RayTraceContext rayTraceContext = new RayTraceContext(
                            prevPos,
                            pos,
                            RayTraceContext.BlockMode.COLLIDER,
                            RayTraceContext.FluidMode.NONE,
                            ent
                    );
                    BlockRayTraceResult blockHitResult = mc.world.rayTraceBlocks(rayTraceContext);
                    boolean isLast = blockHitResult.getType() == RayTraceResult.Type.BLOCK;
                    if (isLast) {
                        for (EffectInstance eff : PotionUtils.getEffectsFromStack(pot.getItem())) {
                            Effect effect = eff.getPotion();
                            EffectInstance inst = new EffectInstance(effect, i, eff.getAmplifier(), eff.isAmbient(), eff.doesShowParticles());
                            if (effect.getEffectType() == EffectType.HARMFUL || effect == Effects.JUMP_BOOST) {
                                potions.put(ent, blockHitResult.getHitVec());
                                break;
                            }
                        }
                        pos = blockHitResult.getHitVec();
                    }
                    if (isLast || pos.y < 0) break;
                    ticks++;
                }
            }
            if (ent instanceof net.minecraft.entity.projectile.TridentEntity) {
                Vector3d motion = ent.getMotion();
                Vector3d pos = ent.getPositionVec();
                Vector3d prevPos;
                Vector3d startPos = null;
                double distance = 0;
                double ticks = 0;
                for (int i = 0; i < 150; i++) {
                    prevPos = pos;
                    pos = pos.add(motion);
                    distance += prevPos.distanceTo(pos);
                    Vector3d motion1 = ent.getMotion();
                    double speed = Math.hypot(motion1.x, motion1.z);
                    motion = calculateNext(ent, motion);
                    if (startPos == null) startPos = prevPos;
                    RayTraceContext rayTraceContext = new RayTraceContext(
                            prevPos,
                            pos,
                            RayTraceContext.BlockMode.COLLIDER,
                            RayTraceContext.FluidMode.NONE,
                            ent
                    );
                    BlockRayTraceResult blockHitResult = mc.world.rayTraceBlocks(rayTraceContext);
                    boolean isLast = blockHitResult.getType() == RayTraceResult.Type.BLOCK;
                    if (isLast) {
                        potions.put(ent, blockHitResult.getHitVec());
                        break;
                    }
                    if (isLast || pos.y < 0) break;
                    ticks++;
                }
            }
        }
    }

    private Vector3d calculateNext(Entity ent, Vector3d motion) {
        motion = motion.scale(0.99);

        if (!ent.hasNoGravity()) {
            motion.y -= ent instanceof ThrowableEntity th ? th.getGravityVelocity() : 0.05F;
        }

        return motion;
    }

    private float calculateYaw(double x1, double z1, double x2, double z2) {
        double deltaX = x2 - x1;
        double deltaZ = z2 - z1;
        float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0F;
        return MathHelper.wrapDegrees(yaw);
    }
}