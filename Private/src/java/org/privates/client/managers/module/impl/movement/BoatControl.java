package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.AttackEvent;
import org.privates.client.managers.events.player.MotionEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;

/**
 * @author ConeTin
 * @since 22 июл. 2024 г.
 */
@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "BoatControl", category = Category.MOVEMENT)
public class BoatControl extends Module {

    BoatEntity target;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        this.target = null;
        for (Entity ent : mc.world.getAllEntities()) {
            if (ent instanceof BoatEntity entity && mc.player.getDistance(entity) < 3f) {
                this.target = entity;
                break;
            }
        }

        if (this.target != null) {
            mc.player.fallDistance = 1;
        }
    }

    @EventHandler
    public void onMotion(MotionEvent event) {
        if (this.target != null) {
            // Устанавливаем скорость игрока (аналог Move.setSpeed(1))
            double speed = 1.0;
            double yaw = Math.toRadians(mc.player.rotationYaw);
            mc.player.setMotion(-Math.sin(yaw) * speed, mc.player.getMotion().y, Math.cos(yaw) * speed);

            if (mc.player.fallDistance > 0.1f) {
                // Вертикальное движение не изменяем, оставляем как есть
            }
            if (mc.player.getDistance(this.target) > 2.5f) {
                if (mc.player.getPositionVec().add(mc.player.getMotion()).distanceTo(target.getPositionVec()) > mc.player.getDistance(target)) {
                    mc.player.setMotion(0, 0, 0); // Заменяем event.setMotion(Vector3d.ZERO)
                }
            }
        }
    }

    @EventHandler
    public void onAttack(AttackEvent event) {
        if (this.target != null) {
            Vector3d pos = mc.player.getPositionVec();
            mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(pos.x, pos.y + 0.1f, pos.z, mc.player.rotationYaw, mc.player.rotationPitch, false));
            mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(pos.x, pos.y, pos.z, mc.player.rotationYaw, mc.player.rotationPitch, false));
        }
    }
}