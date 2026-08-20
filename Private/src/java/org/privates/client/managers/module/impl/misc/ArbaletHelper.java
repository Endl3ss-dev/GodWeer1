package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Beta;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;

@Category3
@Beta
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ArbaletAssist", category = Category.COMBAT, description = "Помогает нажать кнопку выстрела для арбы")
public class ArbaletHelper extends Module {

    public static ArbaletHelper getInstance() {
        return Instance.get(ArbaletHelper.class);
    }

    private final SliderSetting attackRange = new SliderSetting(this, "Дистанция атаки", 10.0f, 3.0f, 50.0f, 0.1f);

    @EventHandler

    public void onUpdate(UpdateEvent event) {
        if (isHoldingCrossbow()) {
            LivingEntity target = findTarget();
            if (target != null) {
                updateRotation(target);
                if (isCrossbowCharged()) {
                    shoot();
                }
            }
        }
    }


    private boolean isHoldingCrossbow() {
        ItemStack stack = mc.player.getHeldItem(Hand.MAIN_HAND);
        return stack.getItem() instanceof CrossbowItem;
    }


    private LivingEntity findTarget() {
        double range = attackRange.getValue();
        LivingEntity closestEntity = null;
        double closestDistance = range;

        for (LivingEntity entity : mc.world.getEntitiesWithinAABB(LivingEntity.class, mc.player.getBoundingBox().grow(range))) {
            if (entity != mc.player && entity.isAlive()) {
                double distance = mc.player.getDistance(entity);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestEntity = entity;
                }
            }
        }
        return closestEntity;
    }


    private void updateRotation(LivingEntity target) {
        Vector3d targetPos = target.getPositionVec().add(0, target.getEyeHeight() * 0.75, 0);
        Vector3d playerPos = mc.player.getEyePosition(1.0F);
        Vector3d direction = targetPos.subtract(playerPos);

        double distance = MathHelper.sqrt(direction.x * direction.x + direction.z * direction.z);
        double heightDifference = targetPos.y - playerPos.y;

        float yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90;
        float pitch = (float) (-Math.toDegrees(Math.atan2(heightDifference, distance)));

        mc.player.rotationYaw = yaw;
        mc.player.rotationPitch = pitch;
    }

    private boolean isCrossbowCharged() {
        ItemStack stack = mc.player.getHeldItem(Hand.MAIN_HAND);
        return stack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(stack);
    }


    private void shoot() {
        ItemStack stack = mc.player.getHeldItem(Hand.MAIN_HAND);
        if (CrossbowItem.isCharged(stack)) {
            mc.playerController.processRightClick(mc.player, mc.world, Hand.MAIN_HAND);
        }
    }
}