package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.TickEvent;
import org.privates.client.managers.events.player.MotionEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.rotation.FreeLookHandler;
import org.privates.client.utils.rotation.RotationPlaner;
import org.privates.common.impl.taskript.Script;
import org.privates.lib.util.time.StopWatch;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Spider2", category = Category.MOVEMENT)
public class Spider2 extends Module {
    private final ModeSetting mode = new ModeSetting(this, "Режим", "Matrix", "Funtime triphook", "Funtime bamboo");
    private final SliderSetting spiderSpeed = new SliderSetting(this, "Скорость", 2.0f, 1.0f, 10.0f, 0.05f)
            .setVisible(() -> mode.is("Matrix") || mode.is("Funtime bamboo"));
    private final Script script = new Script();
    private final StopWatch stopWatch = new StopWatch();

    @Override
    public void onEnable() {
        super.onEnable();
        stopWatch.reset();
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (mode.is("Funtime triphook")) {
            RotationPlaner.setRotationServere(new Rotation(FreeLookHandler.getFreeYaw(), 90), 360, 4, 10);

            BlockPos placePos = mc.player.getPosition().down();
            Vector3d vector3d = new Vector3d(mc.player.getPosX(), placePos.getY(), mc.player.getPosZ());
            BlockRayTraceResult result = new BlockRayTraceResult(vector3d, Direction.UP, placePos, false);

            int tripwireSlot = findItemInHotbar(Items.TRIPWIRE_HOOK);
            if (tripwireSlot != -1) {
                mc.player.connection.sendPacket(new CHeldItemChangePacket(tripwireSlot));
                mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, result));
                mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
            }

            BlockPos playerPos = new BlockPos(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ());
            BlockPos downPos = mc.player.getPosition().down();
            if (mc.world.getBlockState(playerPos).getBlock() == Blocks.TRIPWIRE_HOOK ||
                    mc.world.getBlockState(downPos).getBlock() == Blocks.TRIPWIRE_HOOK) {
                BlockRayTraceResult waterRayTraceResult;
                if (mc.world.getBlockState(downPos).getBlock() != Blocks.TRIPWIRE_HOOK) {
                    waterRayTraceResult = new BlockRayTraceResult(mc.player.getPositionVec(), Direction.UP, playerPos, false);
                } else {
                    waterRayTraceResult = new BlockRayTraceResult(mc.player.getPositionVec(), Direction.UP, downPos, false);
                }
                inventorySwapClick(Items.WATER_BUCKET, waterRayTraceResult);
                mc.player.setMotion(mc.player.getMotion().x, 0.42, mc.player.getMotion().z);
            }
        } else if (mode.is("Funtime bamboo")) {
            if (!mc.player.collidedHorizontally) {
                return;
            }

            int bambooSlot = findItemInHotbar(Items.BAMBOO);
            if (bambooSlot == -1) {
                toggle();
                return;
            }

            BlockPos playerPos = new BlockPos(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ());
            BlockPos bambooPos = findTopBamboo(playerPos);

            if (bambooPos == null) {
                placeBamboo(bambooSlot, playerPos.down());
            } else {
                double distanceToTop = mc.player.getPosY() - bambooPos.getY();
                if (distanceToTop > 0.5 && distanceToTop < 1.5) {
                    placeBamboo(bambooSlot, bambooPos.up());
                }
            }

            long speed = MathHelper.clamp((long) (300 - (spiderSpeed.getValue() * 50)), 0, 300);
            if (stopWatch.finished(speed)) {
                mc.player.setOnGround(true);
                mc.player.jump();
                mc.player.fallDistance = 0;
                stopWatch.reset();
            }
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (mc.player == null || mc.world == null) return;

        if (mode.is("Matrix")) {
            if (!mc.player.collidedHorizontally) {
                return;
            }
            long speedDelay = MathHelper.clamp((long) (500 - (spiderSpeed.getValue() * 50)), 0, 500);
            if (stopWatch.finished(speedDelay)) {
                mc.player.setOnGround(true);
                mc.player.jump();
                stopWatch.reset();
            }
        }
    }

    @EventHandler
    public void onMotion(MotionEvent e) {
        if (mc.player == null) return;

        if (mode.is("Matrix")) {
            if (!mc.player.collidedHorizontally) {
                return;
            }
            e.setOnGround(true);
        } else if (mode.is("Funtime triphook")) {
            mc.player.rotationPitchHead = 90.0F;
            e.setPitch(90.0F);
        } else if (mode.is("Funtime bamboo")) {
            e.setOnGround(true);
        }
    }

    private void placeBamboo(int slot, BlockPos targetPos) {
        Vector3d target = new Vector3d(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
        Vector3d playerPos = mc.player.getPositionVec();
        Vector3d lookVec = target.subtract(playerPos).normalize();
        float yaw = (float) Math.toDegrees(Math.atan2(lookVec.x, lookVec.z));
        float pitch = (float) Math.toDegrees(-Math.asin(lookVec.y));

        BlockRayTraceResult rayTrace = new BlockRayTraceResult(target, Direction.UP, targetPos, false);

        mc.player.connection.sendPacket(new CHeldItemChangePacket(slot));
        mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, rayTrace));
        mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
    }

    private BlockPos findTopBamboo(BlockPos startPos) {
        BlockPos current = startPos;

        while (mc.world.getBlockState(current).getBlock() == Blocks.BAMBOO && current.getY() > 0) {
            current = current.down();
        }

        current = current.up();
        if (mc.world.getBlockState(current).getBlock() != Blocks.BAMBOO) {
            return null;
        }

        while (mc.world.getBlockState(current.up()).getBlock() == Blocks.BAMBOO && current.getY() < 256) {
            current = current.up();
        }

        return current;
    }

    private int findItemInHotbar(Item item) {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    private void inventorySwapClick(Item item, BlockRayTraceResult rayTraceResult) {
        if (mc.player == null || mc.world == null) return;

        int slot = findItemInHotbar(item);
        if (slot != -1) {
            mc.player.connection.sendPacket(new CHeldItemChangePacket(slot));
            mc.playerController.rightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, rayTraceResult);
            mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        stopWatch.reset();

        try {
            if (mc.player != null) {
                mc.player.rotationPitchHead = mc.player.rotationPitch;
                mc.player.fallDistance = 0;
            }
        } catch (Exception ignored) {
        }
    }
}
