package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.InvUtil;

import java.util.HashSet;
import java.util.Set;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoCart", category = Category.COMBAT, description = "Автоматически ставит рельсу и вагонетку с TNT на место падения стрелы")
public class AutoCard extends Module {

    public static AutoCard getInstance() {
        return Instance.get(AutoCard.class);
    }

    private final BooleanSetting onlyOwnArrows = new BooleanSetting(this, "Только свои стрелы", true);
    private final SliderSetting maxDistance = new SliderSetting(this, "Макс. дистанция", 30.0F, 10.0F, 50.0F, 1.0F);
    private final SliderSetting predictionTicks = new SliderSetting(this, "Тики предсказания", 40, 10, 100, 5);
    private final BooleanSetting onlyHotbar = new BooleanSetting(this, "Только хотбар", true);
    private final SliderSetting placeDelay = new SliderSetting(this, "Задержка размещения (мс)", 50, 0, 300, 10);
    private final SliderSetting railToMinecartDelay = new SliderSetting(this, "Задержка рельса->вагонетка (мс)", 200, 50, 500, 25);
    private final BooleanSetting autoDetonate = new BooleanSetting(this, "Авто-взрыв", true);
    private final SliderSetting detonateDelay = new SliderSetting(this, "Задержка взрыва (мс)", 300, 50, 1000, 50).setVisible(autoDetonate::getValue);

    private final Set<Integer> processedArrows = new HashSet<>();
    private final Set<BlockPos> placedPositions = new HashSet<>();
    private long lastPlaceTime = 0;

    @Override
    public void onEnable() {
        super.onEnable();
        processedArrows.clear();
        placedPositions.clear();
        lastPlaceTime = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        processedArrows.clear();
        placedPositions.clear();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        long now = System.currentTimeMillis();
        if (now - lastPlaceTime < placeDelay.getValue().intValue()) {
            return;
        }

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof AbstractArrowEntity arrow) {
                if (shouldProcessArrow(arrow)) {
                    BlockPos landingPos = predictLandingPosition(arrow);

                    if (landingPos != null && isValidPosition(landingPos)) {
                        if (placeRailAndMinecart(landingPos)) {
                            processedArrows.add(arrow.getEntityId());
                            placedPositions.add(landingPos);
                            lastPlaceTime = now;

                            if (autoDetonate.getValue()) {
                                scheduleDetonate(landingPos);
                            }
                            break;
                        }
                    }
                }
            }
        }

        cleanupProcessedArrows();
    }

    private boolean shouldProcessArrow(AbstractArrowEntity arrow) {
        if (arrow.removed || arrow.isOnGround()) return false;

        int arrowId = arrow.getEntityId();
        if (processedArrows.contains(arrowId)) return false;

        if (onlyOwnArrows.getValue()) {
            Entity shooter = arrow.getShooter();
            if (shooter != mc.player) return false;
        }

        double distance = mc.player.getDistance(arrow);
        if (distance > maxDistance.getValue()) return false;

        Vector3d motion = arrow.getMotion();
        double speed = motion.length();
        return !(speed < 0.1);
    }

    private BlockPos predictLandingPosition(AbstractArrowEntity arrow) {
        Vector3d pos = arrow.getPositionVec();
        Vector3d motion = arrow.getMotion();

        int maxTicks = predictionTicks.getValue().intValue();

        for (int i = 0; i < maxTicks; i++) {
            pos = pos.add(motion);
            motion = motion.scale(0.99).add(0, -0.05, 0);

            BlockPos checkPos = new BlockPos(pos);

            if (!mc.world.isAirBlock(checkPos)) {
                BlockPos above = checkPos.up();
                if (mc.world.isAirBlock(above)) {
                    return above;
                }
                return null;
            }

            if (pos.y < 0) {
                return null;
            }
        }

        return null;
    }

    private boolean isValidPosition(BlockPos pos) {
        if (placedPositions.contains(pos)) {
            return false;
        }

        Vector3d posVec = new Vector3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        double distance = mc.player.getPositionVec().distanceTo(posVec);
        if (distance > maxDistance.getValue()) {
            return false;
        }

        if (!mc.world.isAirBlock(pos)) {
            return false;
        }

        BlockPos below = pos.down();
        return !mc.world.isAirBlock(below);
    }

    private boolean placeRailAndMinecart(BlockPos pos) {
        Slot railSlot = findItemSlot(Items.RAIL);
        Slot minecartSlot = findItemSlot(Items.TNT_MINECART);

        if (railSlot == null || minecartSlot == null) {
            return false;
        }

        int previousSlot = mc.player.inventory.currentItem;

        placeItem(pos, railSlot, true);

        new Thread(() -> {
            try {
                Thread.sleep(railToMinecartDelay.getValue().intValue());
                placeItem(pos, minecartSlot, false);
                Thread.sleep(100);
                mc.player.connection.sendPacket(new CHeldItemChangePacket(previousSlot));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        return true;
    }

    private void placeItem(BlockPos pos, Slot slot, boolean isRail) {
        int slotId = slot.slotNumber;
        int hotbarSlot = slotId - 36;

        if (hotbarSlot >= 0 && hotbarSlot < 9) {
            mc.player.connection.sendPacket(new CHeldItemChangePacket(hotbarSlot));
        }

        Vector3d hitVec;
        BlockRayTraceResult result;

        if (isRail) {
            BlockPos placeOn = pos.down();
            hitVec = new Vector3d(placeOn.getX() + 0.5, placeOn.getY() + 1.0, placeOn.getZ() + 0.5);
            result = new BlockRayTraceResult(hitVec, Direction.UP, placeOn, false);
        } else {
            hitVec = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
            result = new BlockRayTraceResult(hitVec, Direction.UP, pos, false);
        }

        mc.player.connection.sendPacket(new net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, result));
        mc.player.connection.sendPacket(new net.minecraft.network.play.client.CAnimateHandPacket(Hand.MAIN_HAND));
    }

    private void scheduleDetonate(BlockPos pos) {
        new Thread(() -> {
            try {
                Thread.sleep(detonateDelay.getValue().intValue());

                for (Entity entity : mc.world.getAllEntities()) {
                    if (entity instanceof TNTMinecartEntity minecart) {
                        BlockPos minecartPos = minecart.getPosition();
                        if (minecartPos.equals(pos) || minecartPos.equals(pos.down())) {
                            mc.playerController.attackEntity(mc.player, minecart);
                            mc.player.swingArm(Hand.MAIN_HAND);
                            break;
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void cleanupProcessedArrows() {
        processedArrows.removeIf(arrowId -> {
            Entity entity = mc.world.getEntityByID(arrowId);
            return entity == null || entity.removed;
        });

        placedPositions.removeIf(pos -> {
            if (mc.world.getBlockState(pos).getBlock() != Blocks.RAIL) {
                return true;
            }

            Vector3d posVec = new Vector3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            double distance = mc.player.getPositionVec().distanceTo(posVec);
            return distance > maxDistance.getValue() * 2;
        });
    }

    private Slot findItemSlot(net.minecraft.item.Item item) {
        if (onlyHotbar.getValue()) {
            int hotbarIndex = InvUtil.getItemInHotBar(item);
            if (hotbarIndex != -1) {
                return mc.player.container.inventorySlots.get(hotbarIndex + 36);
            }
            return null;
        }
        return InvUtil.getSlot(item);
    }
}
