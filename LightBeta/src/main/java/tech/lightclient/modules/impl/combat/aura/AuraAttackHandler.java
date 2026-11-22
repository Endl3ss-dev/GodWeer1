package tech.lightclient.modules.impl.combat.aura;

import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.utils.player.PlayerUtils;
import tech.lightclient.modules.impl.combat.AuraModule;
import tech.lightclient.modules.impl.combat.aura.rotations.FunTimeSnapRotation;
import tech.lightclient.modules.impl.combat.aura.rotations.NeuroRotation;
import tech.lightclient.modules.impl.combat.aura.rotations.NeuroTestRotation;
import tech.lightclient.modules.impl.combat.aura.rotations.TNeuroRotation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

public final class AuraAttackHandler {

    private final AuraModule aura;
    private PendingAttack pendingAttack;

    public AuraAttackHandler(AuraModule aura) {
        this.aura = aura;
    }

    public synchronized void maintain(MinecraftClient mc, LivingEntity target) {
        if (pendingAttack == null || mc.player == null || mc.interactionManager == null) return;

        if (pendingAttack.sprintPaused) {
            pauseSprintForMaximumDamage(mc);
        }

        if (pendingAttack.attackSent) {
            if (AuraMaxDamageHandler.processAfterAttack(mc, pendingAttack.swap)) {
                AuraShieldBreaker.restoreSlot(mc, pendingAttack.shieldSlot);
                restoreSprintAfterMaximumDamage(mc, pendingAttack);
                pendingAttack = null;
            }
            return;
        }

        if (!pendingAttack.target.isAlive()
                || !aura.isMaximumDamageEnabled()
                || AuraMaxDamageHandler.isExpired(mc, pendingAttack.swap)) {
            cancelPendingAttack(mc);
        }
    }

    public synchronized void tryAttackCurrentTarget(MinecraftClient mc, LivingEntity target) {
        if (mc.player == null || mc.interactionManager == null) return;

        if (pendingAttack != null) {
            if (pendingAttack.attackSent) {
                maintain(mc, target);
                // Once the reverse swap is confirmed, continue through the
                // normal attack path in this same update instead of losing a
                // click cycle to the bookkeeping state.
                if (pendingAttack != null) return;
            }
            if (pendingAttack != null && (!pendingAttack.target.isAlive()
                    || !aura.isMaximumDamageEnabled())) {
                cancelPendingAttack(mc);
                return;
            }
            if (pendingAttack != null && AuraMaxDamageHandler.isExpired(mc, pendingAttack.swap)) {
                cancelPendingAttack(mc);
                return;
            }
            // A prepared swap is valid only for the critical hit it was
            // prepared for. If the player lands or the crit window is lost,
            // restore immediately instead of keeping the item in the offhand.
            if (pendingAttack != null && !pendingAttack.isCriticalWindowValid(mc, aura)) {
                cancelPendingAttack(mc);
                return;
            }
            if (pendingAttack != null && AuraMaxDamageHandler.isSwapStalled(mc, pendingAttack.swap)) {
                cancelPendingAttack(mc);
                return;
            }
            if (pendingAttack != null && !AuraMaxDamageHandler.isReadyToAttack(mc, pendingAttack.swap)) return;
            if (pendingAttack != null) {
                // The airborne critical was proven when the swap started.
                // Do not re-check the moving player's fallDistance here: on
                // strafe/run the next client tick can already report ground,
                // which previously dropped the attack packet entirely.
                performAttack(mc, pendingAttack.target,
                        !AuraModule.isLegacyClickMode() && !aura.shouldBypassCritCheckForAttack(),
                        pendingAttack.swap, pendingAttack.shieldSlot);
            } else {
                attack(mc, target);
            }
            return;
        }

        if (target == null || !target.isAlive()) return;
        if (NeuroTestRotation.isFuntimeAirSwingTick()
                || FunTimeSnapRotation.isAirSwingTick()
                || FunTimeSnapRotation.isSideSnapActive()) {
            return;
        }
        if (!isTargetInAttackRange(mc, target)) return;

        if (aura.canAttackNow() && aura.canAttackTargetNow()) {
            attack(mc, target);
        }
    }

    private void attack(MinecraftClient mc, LivingEntity target) {
        // Миссы Neuro: с заданной частотой удар превращается в промах —
        // рука махает, кулдаун сбрасывается, прицел визуально уводится в бок,
        // но пакет атаки НЕ отправляется (урона нет). Так копируются миссы.
        if (target != null && aura.shouldNeuroMissThisAttack()) {
            NeuroRotation.triggerMissVisual();
            TNeuroRotation.triggerMissVisual();
            if (mc.player != null) {
                mc.player.swingHand(Hand.MAIN_HAND);
            }
            aura.onAttackPerformedByHandler(System.currentTimeMillis(), false);
            return;
        }

        if (AuraModule.isLegacyClickMode() || aura.shouldBypassCritCheckForAttack()) {
            performAttack(mc, target, false, null, -1);
        } else if (aura.canDealCrit()) {
            performAttack(mc, target, true, null, -1);
        }
    }

    private void performAttack(MinecraftClient mc, LivingEntity target, boolean critical,
                               AuraMaxDamageHandler.Swap preparedSwap, int preparedShieldSlot) {
        if (target == null || !target.isAlive()) {
            cancelPreparedAttack(mc, preparedSwap, preparedShieldSlot);
            return;
        }

        if (target instanceof PlayerEntity player
                && LightClient.getFriendStorage().isFriend(player.getName().getString())) {
            cancelPreparedAttack(mc, preparedSwap, preparedShieldSlot);
            return;
        }

        if (preparedSwap == null
                && AuraChecks.isHoldingCombatWeapon(mc)
                && !AuraModule.isLegacyClickMode()
                && AuraModule.getAttackCooldown() < 0.85f) {
            cancelPreparedAttack(mc, preparedSwap, preparedShieldSlot);
            return;
        }

        // The target was validated before the inventory swap. Repeating a
        // raytrace after the swap makes sprinting/jumping miss the one-tick
        // attack window and turns a successful swap into a no-op.
        if (preparedSwap == null && !passesRaytrace(mc, target)) {
            cancelPreparedAttack(mc, preparedSwap, preparedShieldSlot);
            return;
        }

        AuraMaxDamageHandler.Swap maxDamageSwap = preparedSwap;
        int shieldSlot = preparedShieldSlot;
        if (maxDamageSwap == null) {
            // Never prepare the damage item for a normal/ground attack.
            if (aura.isMaximumDamageEnabled() && aura.canDealMaximumDamageCrit()) {
                // Stop sprint before the first inventory packet. This makes
                // the complete damage cycle deterministic: pause sprint ->
                // swap in -> hit -> swap back -> restore sprint.
                boolean sprintingBefore = mc.player.isSprinting();
                boolean sprintKeyBefore = mc.options.sprintKey.isPressed();
                pauseSprintForMaximumDamage(mc);
                maxDamageSwap = AuraMaxDamageHandler.prepare(mc, aura);
                if (!maxDamageSwap.isActive()) {
                    restoreSprintState(mc, sprintingBefore, sprintKeyBefore);
                }
                if (maxDamageSwap.isActive()) {
                    shieldSlot = AuraShieldBreaker.prepareSlot(
                            mc, target, aura.attack.getByName("Ломать щит").get());
                    pendingAttack = new PendingAttack(target, maxDamageSwap, shieldSlot,
                            sprintingBefore, sprintKeyBefore,
                            System.currentTimeMillis(), mc.player.fallDistance);
                    pendingAttack.sprintPaused = true;
                    if (!AuraMaxDamageHandler.isReadyToAttack(mc, maxDamageSwap)) return;
                }
            }
            if (pendingAttack == null) {
                shieldSlot = AuraShieldBreaker.prepareSlot(
                        mc, target, aura.attack.getByName("Ломать щит").get());
            }
        }

        // The swap is tied to this exact airborne critical window. If the
        // player touches the ground before the packet is sent, restore it
        // immediately and do not send a non-critical hit with the damage item.
        if (maxDamageSwap != null && maxDamageSwap.isActive()
                && (pendingAttack == null || !pendingAttack.isCriticalWindowValid(mc, aura))) {
            if (pendingAttack != null && pendingAttack.swap == maxDamageSwap) {
                cancelPendingAttack(mc);
            } else {
                cancelPrepared(mc, maxDamageSwap, shieldSlot);
            }
            return;
        }

        try {
            AuraShieldBreaker.syncSelectedSlot(mc);
            AuraShieldBreaker.releaseShieldForAttack(mc);

            // The special item stays in offhand while the attack packet is
            // created and processed, so its attack attributes are active.
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
        } finally {
            if (maxDamageSwap == null || !maxDamageSwap.isActive()) {
                AuraShieldBreaker.restoreSlot(mc, shieldSlot);
            }
        }

        if (maxDamageSwap != null && maxDamageSwap.isActive()) {
            pendingAttack.attackSent = true;
            pendingAttack.attackTime = System.currentTimeMillis();
            aura.onAttackPerformedByHandler(pendingAttack.attackTime, critical);
            // Обратный свап уходит сразу же, в этом же тике: пакетный порядок
            // SWAP(урон) -> атака -> SWAP(хил) даёт серверу применить бафы
            // обоих предметов без разрыва между ними
            if (AuraMaxDamageHandler.processAfterAttack(mc, maxDamageSwap)) {
                AuraShieldBreaker.restoreSlot(mc, pendingAttack.shieldSlot);
                restoreSprintAfterMaximumDamage(mc, pendingAttack);
                pendingAttack = null;
            }
        } else {
            aura.onAttackPerformedByHandler(System.currentTimeMillis(), critical);
        }
    }

    public synchronized void onPacket(EventPacket event) {
        if (pendingAttack != null) {
            AuraMaxDamageHandler.onPacket(event, pendingAttack.swap);
        }
    }

    public synchronized boolean isMaximumDamageSprintPaused() {
        // Keep sprint disabled for the whole transaction, including the
        // short post-hit window before reverse-swap is acknowledged.
        return pendingAttack != null && pendingAttack.sprintPaused;
    }

    public synchronized boolean hasPendingAttack() {
        return pendingAttack != null;
    }

    public synchronized void cancelPendingAttack(MinecraftClient mc) {
        if (pendingAttack == null) return;

        boolean restored = AuraMaxDamageHandler.cancel(mc, pendingAttack.swap);
        if (!restored && mc.player != null
                && mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
            // A container opened during the short swap window can otherwise
            // keep the damage item in the offhand indefinitely. Close that
            // temporary handler and retry on the normal player inventory.
            mc.player.closeHandledScreen();
            restored = AuraMaxDamageHandler.cancel(mc, pendingAttack.swap);
        }
        if (pendingAttack.swap != null && pendingAttack.swap.isActive() && !restored) {
            // Keep the state alive until the player inventory screen is
            // available again; dropping it here would leave the damage item
            // permanently stuck in the offhand.
            return;
        }
        AuraShieldBreaker.restoreSlot(mc, pendingAttack.shieldSlot);
        restoreSprintAfterMaximumDamage(mc, pendingAttack);
        pendingAttack = null;
    }

    private void cancelPrepared(MinecraftClient mc, AuraMaxDamageHandler.Swap swap, int shieldSlot) {
        if (swap != null && swap.isActive()) {
            AuraMaxDamageHandler.cancel(mc, swap);
        }
        AuraShieldBreaker.restoreSlot(mc, shieldSlot);
    }

    private void cancelPreparedAttack(MinecraftClient mc,
                                      AuraMaxDamageHandler.Swap swap,
                                      int shieldSlot) {
        cancelPrepared(mc, swap, shieldSlot);
        if (swap != null && swap.isActive()) {
            cancelPendingAttack(mc);
        }
    }

    private void pauseSprintForMaximumDamage(MinecraftClient mc) {
        if (mc.player == null) return;
        // setSprinting(false) changes only the client-side input state. Send
        // the command immediately so the server processes the following
        // inventory swap and attack as non-sprinting (critical) packets.
        if (mc.player.isSprinting() || mc.options.sprintKey.isPressed()) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(
                    mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        }
        mc.player.setSprinting(false);
        mc.options.sprintKey.setPressed(false);
    }

    private void restoreSprintAfterMaximumDamage(MinecraftClient mc, PendingAttack attack) {
        if (attack == null || !attack.sprintPaused || mc.player == null) return;
        attack.sprintPaused = false;
        restoreSprintState(mc, attack.sprintingBefore, attack.sprintKeyBefore);
    }

    private void restoreSprintState(MinecraftClient mc, boolean sprintingBefore, boolean sprintKeyBefore) {
        if (mc.player == null) return;
        if (sprintingBefore
                && (mc.player.forwardSpeed != 0.0f || mc.player.sidewaysSpeed != 0.0f || sprintKeyBefore)
                && !mc.player.isSneaking() && !mc.player.isSwimming()
                && !mc.player.isGliding()) {
            mc.player.setSprinting(true);
            mc.options.sprintKey.setPressed(sprintKeyBefore || sprintingBefore);
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(
                    mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
        } else {
            mc.options.sprintKey.setPressed(sprintKeyBefore);
        }
    }

    private boolean isTargetInAttackRange(MinecraftClient mc, LivingEntity target) {
        double effectiveRange = aura.getEffectiveAttackRange();
        double safeRange = effectiveRange - 0.05;

        if (mc.crosshairTarget instanceof EntityHitResult entityHit && entityHit.getEntity() == target) {
            return true;
        }

        Vec3d playerPos = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        Vec3d targetPos = new Vec3d(target.getX(), target.getY(), target.getZ());
        double horizontalDistanceSq = targetPos.subtract(playerPos).multiply(1, 0, 1).lengthSquared();
        double verticalDistance = Math.abs(targetPos.y - playerPos.y);
        return horizontalDistanceSq <= safeRange * safeRange
                && verticalDistance <= effectiveRange + 1.0;
    }

    private boolean passesRaytrace(MinecraftClient mc, LivingEntity target) {
        float yaw = aura.getRaytraceYawForAttack();
        float pitch = aura.getRaytracePitchForAttack();
        double range = aura.getEffectiveAttackRange();

        var directHit = PlayerUtils.raytraceEntity(
                range, yaw, pitch,
                entity -> entity == target && !entity.isSpectator() && entity.canHit()
        );
        boolean crosshairHitsTarget = mc.crosshairTarget instanceof EntityHitResult entityHit
                && entityHit.getEntity() == target;

        if ((directHit == null || directHit.getEntity() != target)
                && !crosshairHitsTarget
                && !isInsideTargetHitbox(mc, target)) {
            return false;
        }

        return aura.isAttackThroughBlocksEnabled()
                || (AuraChecks.canSeeThroughWall(mc, target) && !AuraChecks.hasGrassOnRay(mc, target));
    }

    private boolean isInsideTargetHitbox(MinecraftClient mc, LivingEntity entity) {
        return mc.player != null
                && entity.getBoundingBox().expand(1.0E-4).contains(mc.player.getEyePos());
    }

    private static final class PendingAttack {
        private final LivingEntity target;
        private final AuraMaxDamageHandler.Swap swap;
        private final int shieldSlot;
        private boolean attackSent;
        private long attackTime;
        private boolean sprintPaused;
        private final boolean sprintingBefore;
        private final boolean sprintKeyBefore;
        private final long criticalPreparedAt;
        private final double criticalFallDistance;

        private PendingAttack(LivingEntity target, AuraMaxDamageHandler.Swap swap,
                              int shieldSlot,
                              boolean sprintingBefore, boolean sprintKeyBefore,
                              long criticalPreparedAt, double criticalFallDistance) {
            this.target = target;
            this.swap = swap;
            this.shieldSlot = shieldSlot;
            this.sprintingBefore = sprintingBefore;
            this.sprintKeyBefore = sprintKeyBefore;
            this.criticalPreparedAt = criticalPreparedAt;
            this.criticalFallDistance = criticalFallDistance;
        }

        private boolean isCriticalWindowValid(MinecraftClient mc, AuraModule aura) {
            if (aura.canDealMaximumDamageCrit()) return true;
            // Inventory clicks and movement packets can consume the last
            // falling tick. Preserve only the short snapshot window that was
            // already proven critical when the damage item was equipped.
            return criticalFallDistance > aura.getCritThreshold()
                    // Strafing/running can delay the inventory callback for
                    // a few client ticks; keep the proven critical snapshot
                    // alive instead of cancelling the swap with no hit.
                    && System.currentTimeMillis() - criticalPreparedAt <= 250L;
        }
    }
}
