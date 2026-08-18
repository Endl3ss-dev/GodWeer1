package nuclear.module.impl.other;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import nuclear.control.Manager;
import nuclear.control.events.Event;
import nuclear.control.events.impl.player.EventUpdate;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.impl.combat.AttackAura;
import nuclear.module.settings.imp.BooleanSetting;
import nuclear.module.settings.imp.SliderSetting;
import nuclear.utils.misc.PredictUtility;
import nuclear.utils.world.InventoryUtils;

@ModuleDescriptor(
    name = "WebTrap",
    type = ModuleCategory.Other,
    desc = "Places cobwebs at the predicted target position"
)
public class WebTrap extends Module {
    private final SliderSetting range = new SliderSetting("Range", 4.5f, 2.0f, 6.0f, 0.1f);
    private final SliderSetting predictionTicks = new SliderSetting("Prediction", 2.0f, 0.0f, 8.0f, 1.0f);
    private final SliderSetting delayMs = new SliderSetting("Delay", 250.0f, 50.0f, 1000.0f, 50.0f);
    private final BooleanSetting headWeb = new BooleanSetting("Head web", true);
    private final BooleanSetting onlyAuraTarget = new BooleanSetting("Only aura target", true);

    private long lastPlaceTime;

    public WebTrap() {
        addSettings(range, predictionTicks, delayMs, headWeb, onlyAuraTarget);
    }

    @Override
    public boolean onEvent(Event event) {
        if (!(event instanceof EventUpdate) || mc.player == null || mc.world == null) {
            return false;
        }

        if (System.currentTimeMillis() - lastPlaceTime < delayMs.getValue().longValue()) {
            return false;
        }

        PlayerEntity target = findTarget();
        if (target == null) {
            return false;
        }

        Vector3d predicted = target.isElytraFlying()
            ? PredictUtility.predictElytraPos(target, predictionTicks.getValue().intValue())
            : target.getPositionVec().add(target.getMotion().scale(predictionTicks.getValue().doubleValue()));

        BlockPos feet = new BlockPos(predicted.x, predicted.y, predicted.z);
        if (placeCobweb(feet)) {
            lastPlaceTime = System.currentTimeMillis();
            return false;
        }

        if (headWeb.get() && placeCobweb(feet.up())) {
            lastPlaceTime = System.currentTimeMillis();
        }
        return false;
    }

    private PlayerEntity findTarget() {
        if (onlyAuraTarget.get() && AttackAura.target instanceof PlayerEntity) {
            PlayerEntity auraTarget = (PlayerEntity) AttackAura.target;
            if (isValidTarget(auraTarget)) {
                return auraTarget;
            }
            return null;
        }

        PlayerEntity best = null;
        double bestDistance = Double.MAX_VALUE;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (!isValidTarget(player)) {
                continue;
            }

            double distance = mc.player.getDistanceSq(player);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = player;
            }
        }
        return best;
    }

    private boolean isValidTarget(PlayerEntity player) {
        if (player == null || player == mc.player || !player.isAlive()) {
            return false;
        }
        if (Manager.FRIEND_MANAGER != null && Manager.FRIEND_MANAGER.isFriend(player.getName().getString())) {
            return false;
        }
        return mc.player.getDistance(player) <= range.getValue().doubleValue();
    }

    private boolean placeCobweb(BlockPos pos) {
        if (!mc.world.getBlockState(pos).isAir()) {
            return false;
        }

        int slot = findCobwebSlot();
        if (slot == -1) {
            return false;
        }

        PlacementData placement = findPlacement(pos);
        if (placement == null) {
            return false;
        }

        int previousSlot = mc.player.inventory.currentItem;
        if (slot >= 9) {
            mc.playerController.pickItem(slot);
            slot = mc.player.inventory.currentItem;
        } else {
            mc.player.inventory.currentItem = slot;
        }

        BlockRayTraceResult rayTraceResult = new BlockRayTraceResult(
            placement.hitVec,
            placement.face,
            placement.neighbour,
            false
        );

        ActionResultType result = mc.playerController.processRightClickBlock(
            mc.player,
            mc.world,
            Hand.MAIN_HAND,
            rayTraceResult
        );

        mc.player.inventory.currentItem = previousSlot;
        if (result == ActionResultType.SUCCESS) {
            mc.player.swingArm(Hand.MAIN_HAND);
            return true;
        }
        return false;
    }

    private int findCobwebSlot() {
        int hotbarSlot = InventoryUtils.getItem(Items.COBWEB, true);
        if (hotbarSlot != -1) {
            return hotbarSlot;
        }
        return InventoryUtils.getItem(Items.COBWEB, false);
    }

    private PlacementData findPlacement(BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighbour = offset(pos, direction);
            if (mc.world.getBlockState(neighbour).isAir()) {
                continue;
            }

            Vector3d hit = new Vector3d(
                neighbour.getX() + 0.5 + direction.getXOffset() * 0.5,
                neighbour.getY() + 0.5 + direction.getYOffset() * 0.5,
                neighbour.getZ() + 0.5 + direction.getZOffset() * 0.5
            );
            return new PlacementData(neighbour, direction.getOpposite(), hit);
        }
        return null;
    }

    private BlockPos offset(BlockPos pos, Direction direction) {
        return pos.add(direction.getXOffset(), direction.getYOffset(), direction.getZOffset());
    }

    private static class PlacementData {
        private final BlockPos neighbour;
        private final Direction face;
        private final Vector3d hitVec;

        private PlacementData(BlockPos neighbour, Direction face, Vector3d hitVec) {
            this.neighbour = neighbour;
            this.face = face;
            this.hitVec = hitVec;
        }
    }
}
