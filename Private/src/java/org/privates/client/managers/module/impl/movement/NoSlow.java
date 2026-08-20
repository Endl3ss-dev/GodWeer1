package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.item.UseAction;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.SlowWalkingEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "NoSlow", category = Category.MOVEMENT, description = "Кушать и не замедлятся")
public class NoSlow extends Module {
    public static NoSlow getInstance() {
        return Instance.get(NoSlow.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Mode", "Vanilla", "AAC", "Grim Old", "Grim New", "Vulcan", "Matrix", "Intave", "Updated NCP", "ReallyWorld");
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final StopWatch stopWatch = new StopWatch();

    private int matrixTicks = 0;
    private boolean intaveSwitch = false;
    private int verusTicks = 0;
    private int karhuTicks = 0;
    private boolean shouldCancel = false;
    private int spartanDelay = 0;

    @Override
    protected void onDisable() {
        super.onDisable();
        matrixTicks = 0;
        intaveSwitch = false;
        verusTicks = 0;
        karhuTicks = 0;
        shouldCancel = false;
        spartanDelay = 0;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || !mc.player.isHandActive()) return;

        switch (mode.getValue()) {
            case "Matrix" -> handleMatrixUpdate();
            case "Intave" -> handleIntaveUpdate();
            case "Verus" -> handleVerusUpdate();
            case "Karhu" -> handleKarhuUpdate();
            case "Spartan" -> handleSpartanUpdate();
        }
    }

    @EventHandler
    public void onSlowWalk(SlowWalkingEvent e) {
        if (mc.player == null || mc.player.isElytraFlying()) return;

        switch (mode.getValue()) {
            case "Vanilla" -> handleVanilla(e);
            case "NCP" -> handleNCP(e);
            case "AAC" -> handleAAC(e);
            case "Grim Old" -> handleGrim(e);
            case "Grim New" -> handleGrimLatestMode(e);
            case "Vulcan" -> handleVulcan(e);
            case "Matrix" -> handleMatrix(e);
            case "Intave" -> handleIntave(e);
            case "Verus" -> handleVerus(e);
            case "Updated NCP" -> handleUpdatedNCP(e);
            case "Polar" -> handlePolar(e);
            case "ReallyWorld" -> handleReallyWorld(e);
        }
    }

    private void handleVanilla(SlowWalkingEvent e) {
        e.cancel();
    }

    private void handleNCP(SlowWalkingEvent e) {
        if (mc.player.getActiveHand() == Hand.MAIN_HAND) {
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
        }
        e.cancel();
    }

    private int ticks = 0;

    private void handleGrimLatestMode(SlowWalkingEvent event) {
        if (!mc.player.isHandActive()) {
            ticks = 0;
            return;
        }

        ticks++;

        if (ticks >= 2) {
            event.cancel();
            ticks = 0;
        }
    }

    private void handleAAC(SlowWalkingEvent e) {
        if (mc.player.ticksExisted % 3 == 0) {
            mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                    CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK,
                    BlockPos.ZERO,
                    Direction.DOWN
            ));
        }
        e.cancel();
    }

    private void handleGrim(SlowWalkingEvent e) {
        if (mc.player.getHeldItemOffhand().getUseAction() == UseAction.BLOCK && mc.player.getActiveHand() == Hand.MAIN_HAND ||
                mc.player.getHeldItemOffhand().getUseAction() == UseAction.EAT && mc.player.getActiveHand() == Hand.MAIN_HAND) {
            return;
        }

        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(mc.player.getActiveHand()));
        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(mc.player.getActiveHand() == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND));
        e.cancel();
    }

    private void handleVulcan(SlowWalkingEvent e) {
        if (mc.player.isOnGround() && mc.player.ticksExisted % 2 == 0) {
            mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                    CPlayerDiggingPacket.Action.RELEASE_USE_ITEM,
                    BlockPos.ZERO,
                    Direction.DOWN
            ));
        }
        e.cancel();
    }

    private void handleMatrixUpdate() {
        if (mc.player.isHandActive()) {
            matrixTicks++;
            if (matrixTicks >= 8) {
                shouldCancel = true;
                matrixTicks = 0;
            }
        } else {
            matrixTicks = 0;
            shouldCancel = false;
        }
    }

    private void handleMatrix(SlowWalkingEvent e) {
        if (shouldCancel) {
            if (mc.player.ticksExisted % 4 == 0) {
                int currentSlot = mc.player.inventory.currentItem;
                mc.player.connection.sendPacket(new CHeldItemChangePacket(currentSlot < 8 ? currentSlot + 1 : 0));
                mc.player.connection.sendPacket(new CHeldItemChangePacket(currentSlot));
            }
            e.cancel();
        }
    }

    private void handleIntaveUpdate() {
        if (mc.player.isHandActive() && mc.player.ticksExisted % 10 == 0) {
            intaveSwitch = !intaveSwitch;
        }
    }

    private void handleIntave(SlowWalkingEvent e) {
        if (intaveSwitch) {
            mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                    CPlayerDiggingPacket.Action.RELEASE_USE_ITEM,
                    mc.player.getPosition(),
                    Direction.UP
            ));
            e.cancel();
        }
    }

    private void handleVerusUpdate() {
        if (mc.player.isHandActive()) {
            verusTicks++;
        } else {
            verusTicks = 0;
        }
    }

    private void handleVerus(SlowWalkingEvent e) {
        if (verusTicks > 5 && mc.player.isOnGround()) {
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
            e.cancel();
        }
    }

    private void handleUpdatedNCP(SlowWalkingEvent e) {
        if (mc.player.isOnGround()) {
            mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                    CPlayerDiggingPacket.Action.RELEASE_USE_ITEM,
                    BlockPos.ZERO,
                    Direction.DOWN
            ));
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(mc.player.getActiveHand()));
        }
        e.cancel();
    }

    private void handleKarhuUpdate() {
        if (mc.player.isHandActive()) {
            karhuTicks++;
        } else {
            karhuTicks = 0;
        }
    }

    private void handleKarhu(SlowWalkingEvent e) {
        if (karhuTicks % 3 == 0 && mc.player.isOnGround()) {
            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_SPRINTING));
        }
        e.cancel();
    }

    private void handleSpartanUpdate() {
        if (mc.player.isHandActive()) {
            spartanDelay++;
        } else {
            spartanDelay = 0;
        }
    }

    private void handleSpartan(SlowWalkingEvent e) {
        if (spartanDelay % 7 == 0 && mc.player.isOnGround()) {
            mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                    CPlayerDiggingPacket.Action.SWAP_ITEM_WITH_OFFHAND,
                    BlockPos.ZERO,
                    Direction.DOWN
            ));
        }
        e.cancel();
    }

    private void handleGodsEye(SlowWalkingEvent e) {
        if (mc.player.isOnGround() && mc.player.ticksExisted % 2 == 0) {
            mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                    CPlayerDiggingPacket.Action.DROP_ITEM,
                    BlockPos.ZERO,
                    Direction.DOWN
            ));
            mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                    CPlayerDiggingPacket.Action.DROP_ALL_ITEMS,
                    BlockPos.ZERO,
                    Direction.DOWN
            ));
        }
        e.cancel();
    }

    private void handlePolar(SlowWalkingEvent e) {
        if (mc.player.ticksExisted % 5 == 0) {
            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.PRESS_SHIFT_KEY));
            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.RELEASE_SHIFT_KEY));
        }
        e.cancel();
    }

    private void handleBlocksMC(SlowWalkingEvent e) {
        Hand oppositeHand = mc.player.getActiveHand() == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(oppositeHand));
        mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                CPlayerDiggingPacket.Action.RELEASE_USE_ITEM,
                mc.player.getPosition(),
                Direction.UP
        ));
        e.cancel();
    }

    private void handleHypixel(SlowWalkingEvent e) {
        if (mc.player.ticksExisted % 3 == 0) {
            BlockPos blockBelow = mc.player.getPosition().down();
            mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                    CPlayerDiggingPacket.Action.START_DESTROY_BLOCK,
                    blockBelow,
                    Direction.UP
            ));
            mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                    CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK,
                    blockBelow,
                    Direction.UP
            ));
        }
        e.cancel();
    }

    private void handleFuncraft(SlowWalkingEvent e) {
        if (mc.player.isOnGround()) {
            int slot = mc.player.inventory.currentItem;
            mc.player.connection.sendPacket(new CHeldItemChangePacket((slot + 1) % 9));
            mc.player.connection.sendPacket(new CHeldItemChangePacket(slot));
            mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                    CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK,
                    mc.player.getPosition(),
                    Direction.DOWN
            ));
        }
        e.cancel();
    }


    private void handleReallyWorld(SlowWalkingEvent e) {
        mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK,
                mc.player.getPosition().up(),
                Direction.NORTH
        ));
        e.cancel();
    }

    public boolean isBlockUnderWithMotion() {
        AxisAlignedBB aab = mc.player.getBoundingBox().offset(mc.player.getMotion().x, -0.1, mc.player.getMotion().z);
        return mc.world.getCollisionShapes(mc.player, aab).toList().isEmpty();
    }
}