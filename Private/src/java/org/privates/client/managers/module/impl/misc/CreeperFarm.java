package org.privates.client.managers.module.impl.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.MotionEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;

import java.util.ArrayList;

@ModuleInfo(name = "CreeperFarm", category = Category.MISC)
public class CreeperFarm extends Module {

    private boolean retreating;
    private long lastHitTime;

    @EventHandler
    private void onMotion(MotionEvent e) {
        Entity creeperTarget = calculateCreeper(50);
        if (creeperTarget == null) return;

        long now = System.currentTimeMillis();

        if (retreating) {
            double dx = mc.player.getPosX() - creeperTarget.getPosX();
            double dz = mc.player.getPosZ() - creeperTarget.getPosZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > 6 || now - lastHitTime > 300) {
                retreating = false;
            } else {
                mc.player.getMotion().x = dx / dist * 0.3;
                mc.player.getMotion().z = dz / dist * 0.3;
                return;
            }
        }

        boolean canSee = true;
        boolean inRange = mc.player.getDistance(creeperTarget) <= 3 && mc.player.getDistance(creeperTarget) <= 6;

        if ((!mc.gameSettings.keyBindJump.isKeyDown() && mc.player.isOnGround() || mc.player.fallDistance > 0)
                && mc.player.getCooledAttackStrength(0) >= 0.944f
                && inRange && canSee) {
            mc.playerController.attackEntity(mc.player, creeperTarget);
            mc.player.swingArm(Hand.MAIN_HAND);
            lastHitTime = now;
            retreating = true;
        }
    }

    public LivingEntity calculateCreeper(double range) {
        var targets = new ArrayList<LivingEntity>();

        for (Entity ent : mc.world.getAllEntities()) {
            if (ent instanceof CreeperEntity && ent.isAlive() && Math.abs(mc.player.getPosY() - ent.getPosY()) < 4) {
                double dist = mc.player.getPositionVec()
                        .add(new Vector3d(0, mc.player.getEyeHeight(), 0)).distanceTo(ent.getPositionVec());
                if (dist < range) {
                    targets.add((LivingEntity) ent);
                }
            }
        }

        return targets.stream().min((e1, e2) -> Double.compare(mc.player.getDistance(e1), mc.player.getDistance(e2))).orElse(null);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        mc.player.sendChatMessage("#follow entity creeper");
        mc.player.sendChatMessage("#allowBreak false");
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        mc.player.sendChatMessage("#stop");
    }
}