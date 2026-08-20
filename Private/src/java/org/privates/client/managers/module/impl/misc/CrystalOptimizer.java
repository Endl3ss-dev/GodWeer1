package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.List;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "CrystalOptimizer", category = Category.COMBAT, description = "Автоматически взрывает кристаллы при том если вы сами его поставили")
public class CrystalOptimizer extends Module {

    public static CrystalOptimizer getInstance() {
        return Instance.get(CrystalOptimizer.class);
    }

    private final SliderSetting explodeSpeed = new SliderSetting(this, "Скорость взрыва", 100.0F, 0.0F, 500.0F, 10.0F);
    private final SliderSetting range = new SliderSetting(this, "Дистанция", 5.0F, 3.0F, 6.0F, 0.5F);

    private final StopWatch explodeTimer = new StopWatch();
    private final List<Integer> toRemove = new ArrayList<>();

    @EventHandler

    public void onUpdate(UpdateEvent event) {
        if (mc.world == null || mc.player == null) return;

        if (explodeTimer.finished(explodeSpeed.getValue())) {
            EnderCrystalEntity crystal = findNearestCrystal();
            if (crystal != null) {
                attackCrystal(crystal);
                explodeTimer.reset();
            }
        }
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (mc.world == null || toRemove.isEmpty()) return;

        for (int entityId : toRemove) {
            mc.world.removeEntityFromWorld(entityId);
        }
        toRemove.clear();
    }

    private EnderCrystalEntity findNearestCrystal() {
        double searchRange = range.getValue();
        AxisAlignedBB searchBox = mc.player.getBoundingBox().grow(searchRange);

        List<EnderCrystalEntity> crystals = mc.world.getEntitiesWithinAABB(
                EnderCrystalEntity.class,
                searchBox,
                crystal -> crystal != null && !crystal.removed
        );

        if (crystals.isEmpty()) return null;

        EnderCrystalEntity nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (EnderCrystalEntity crystal : crystals) {
            double distance = mc.player.getDistance(crystal);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = crystal;
            }
        }

        return nearest;
    }

    private void attackCrystal(EnderCrystalEntity crystal) {
        if (crystal == null || crystal.removed) return;

        mc.playerController.attackEntity(mc.player, crystal);
        mc.player.swingArm(Hand.MAIN_HAND);
        toRemove.add(crystal.getEntityId());
    }

    @Override
    public void onDisable() {
        toRemove.clear();
        super.onDisable();
    }
}