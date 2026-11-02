package tech.lightclient.modules.impl.player;

import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.baritone.BaritoneHelper;
import tech.lightclient.client.utils.baritone.BaritoneRotationHook;
import tech.lightclient.client.utils.player.PlayerInventoryUtil;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import tech.lightclient.modules.impl.render.AncientXray;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

/**
 * Автоматическая ферма древних обломков для сервера с /fly.
 * Движение выполняется напрямую скоростью игрока в режиме Dragon; Baritone
 * здесь намеренно не используется, поэтому модуль не переводит игрока в
 * пеший pathing.
 */
@FunctionAdd(name = "AncientFarm", alias = "Ancient Farm", category = Category.Player,
        description = "Dragon-полёт, TNT-проходы и сбор древних обломков")
public final class AncientFarm extends Function {
    private static final String DRAGON_MODE = "Dragon";
    private static final int TUNNEL_LENGTH = 22;
    private static final int RETREAT_BLOCKS = 13;
    private static final int NEXT_AREA_BLOCKS = 150;
    private static final long MINE_TIMEOUT_MS = 2200L;
    private static final long EXPLOSION_TIMEOUT_MS = 3500L;
    private static final long ACTION_DELAY_MS = 250L;

    private final ModeSetting flightMode = new ModeSetting("Режим полёта", DRAGON_MODE, DRAGON_MODE);
    private final SliderSetting horizontal = new SliderSetting("Скорость Dragon", 1.0f, 0.1f, 5.0f, 0.1f);
    private final SliderSetting vertical = new SliderSetting("Вертикальная скорость", 0.6f, 0.1f, 3.0f, 0.1f);

    private Phase phase = Phase.TRAVEL_TO_MINE;
    private Vec3d direction;
    private Vec3d workOrigin;
    private Vec3d travelTarget;
    private BlockPos actionBlock;
    private BlockPos tntBlock;
    private int tunnelIndex;
    private long phaseStartedAt;
    private long actionStartedAt;
    private long lastActionAt;
    private boolean explosionReceived;
    private boolean attackKeyWasPressed;
    private boolean xrayStartedByFarm;

    private enum Phase {
        TRAVEL_TO_MINE,
        MINE_TUNNEL,
        PLACE_TNT,
        IGNITE_TNT,
        RETREAT,
        WAIT_EXPLOSION,
        LOOT,
        TRAVEL_NEXT_AREA
    }

    public AncientFarm() {
        addSettings(flightMode, horizontal, vertical);
    }

    @Override
    public void onEnable() {
        if (!BaritoneHelper.isAvailable()) {
            sendMessage("§c[AncientFarm] Baritone не найден в mods.");
            setState(false);
            return;
        }
        if (mc.player == null || mc.world == null || !mc.world.getRegistryKey().equals(World.NETHER)) {
            sendMessage("§c[AncientFarm] Модуль работает только в Незере.");
            setState(false);
            return;
        }

        AncientXray xray = getXray();
        if (xray != null && !xray.isState()) {
            xrayStartedByFarm = true;
            xray.setState(true);
        }

        direction = horizontalDirection(mc.player.getYaw());
        BaritoneHelper.applySettings(true, 0.0, false);
        BaritoneRotationHook.enable();
        workOrigin = mc.player.getEntityPos();
        travelTarget = workOrigin.add(direction.multiply(2.0));
        phase = Phase.TRAVEL_TO_MINE;
        phaseStartedAt = System.currentTimeMillis();
        actionBlock = null;
        tntBlock = null;
        tunnelIndex = 0;
        explosionReceived = false;
        super.onEnable();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (!flightMode.is(DRAGON_MODE)) return;

        long now = System.currentTimeMillis();
        switch (phase) {
            case TRAVEL_TO_MINE -> tickTravelToMine(now);
            case MINE_TUNNEL -> tickMineTunnel(now);
            case PLACE_TNT -> tickPlaceTnt(now);
            case IGNITE_TNT -> tickIgniteTnt(now);
            case RETREAT -> tickRetreat(now);
            case WAIT_EXPLOSION -> tickWaitExplosion(now);
            case LOOT -> tickLoot(now);
            case TRAVEL_NEXT_AREA -> tickTravelNextArea(now);
        }
    }

    private void tickTravelToMine(long now) {
        if (!moveTo(travelTarget, horizontal.get().doubleValue(), 1.0)) return;
        workOrigin = mc.player.getEntityPos();
        phase = Phase.MINE_TUNNEL;
        phaseStartedAt = now;
        actionBlock = null;
    }

    private void tickMineTunnel(long now) {
        if (tunnelIndex >= TUNNEL_LENGTH) {
            tntBlock = BlockPos.ofFloored(workOrigin.add(direction.multiply(TUNNEL_LENGTH)));
            phase = Phase.PLACE_TNT;
            phaseStartedAt = now;
            actionBlock = null;
            return;
        }

        BlockPos target = BlockPos.ofFloored(workOrigin.add(direction.multiply(tunnelIndex + 1)));
        Block block = mc.world.getBlockState(target).getBlock();
        if (block == Blocks.AIR || block == Blocks.CAVE_AIR || block == Blocks.VOID_AIR) {
            tunnelIndex++;
            actionBlock = null;
            return;
        }

        if (actionBlock == null || !actionBlock.equals(target)) {
            actionBlock = target;
            actionStartedAt = now;
        }

        Vec3d stand = Vec3d.ofCenter(target).subtract(direction.multiply(1.6));
        if (!moveTo(stand, horizontal.get().doubleValue(), 0.85)) return;
        aimAt(Vec3d.ofCenter(target));

        int pickaxe = findPickaxeHotbar();
        if (pickaxe >= 0) selectSlot(pickaxe);
        mc.options.attackKey.setPressed(true);
        attackKeyWasPressed = true;
        BaritoneHelper.breakBlock(target, Direction.getFacing(direction.x, direction.y, direction.z).getOpposite());

        if (!mc.world.getBlockState(target).isOf(block)) {
            tunnelIndex++;
            actionBlock = null;
        } else if (now - actionStartedAt >= MINE_TIMEOUT_MS) {
            // Не зависаем на неподдающемся блоке: продолжаем проход.
            tunnelIndex++;
            actionBlock = null;
        }
    }

    private void tickPlaceTnt(long now) {
        if (tntBlock == null) {
            nextArea();
            return;
        }
        releaseAttackKey();
        Vec3d stand = Vec3d.ofCenter(tntBlock).subtract(direction.multiply(2.0));
        if (!moveTo(stand, horizontal.get().doubleValue(), 0.85)) return;
        aimAt(Vec3d.ofCenter(tntBlock.down()));

        if (now - lastActionAt < ACTION_DELAY_MS) return;
        if (mc.world.getBlockState(tntBlock).isOf(Blocks.TNT)) {
            phase = Phase.IGNITE_TNT;
            phaseStartedAt = now;
            return;
        }

        if (PlayerInventoryUtil.runWithItemInMainHand(Items.TNT, true, () -> placeBlock(tntBlock))) {
            lastActionAt = now;
        } else {
            sendMessage("§c[AncientFarm] Не найден TNT.");
            nextArea();
        }
    }

    private void tickIgniteTnt(long now) {
        Vec3d stand = Vec3d.ofCenter(tntBlock).subtract(direction.multiply(2.0));
        if (!moveTo(stand, horizontal.get().doubleValue(), 0.85)) return;
        aimAt(Vec3d.ofCenter(tntBlock));
        if (now - lastActionAt < ACTION_DELAY_MS) return;

        if (PlayerInventoryUtil.runWithItemInMainHand(Items.FLINT_AND_STEEL,
                true, () -> useOnBlock(tntBlock))) {
            explosionReceived = false;
            phase = Phase.RETREAT;
            phaseStartedAt = now;
            lastActionAt = now;
        } else {
            sendMessage("§c[AncientFarm] Не найдено огниво.");
            nextArea();
        }
    }

    private void tickRetreat(long now) {
        Vec3d target = Vec3d.ofCenter(tntBlock).subtract(direction.multiply(RETREAT_BLOCKS));
        if (!moveTo(target, horizontal.get().doubleValue(), 0.85)) return;
        phase = Phase.WAIT_EXPLOSION;
        phaseStartedAt = now;
    }

    private void tickWaitExplosion(long now) {
        stopFlying();
        if (explosionReceived || now - phaseStartedAt >= EXPLOSION_TIMEOUT_MS) {
            phase = Phase.LOOT;
            phaseStartedAt = now;
        }
    }

    private void tickLoot(long now) {
        AncientXray xray = getXray();
        BlockPos target = xray == null ? null : xray.getNearestFoundBlock();
        if (target != null) {
            Vec3d targetPos = Vec3d.ofCenter(target).subtract(direction.multiply(1.5));
            if (!moveTo(targetPos, horizontal.get().doubleValue(), 0.85)) return;
            aimAt(Vec3d.ofCenter(target));
            int pickaxe = findPickaxeHotbar();
            if (pickaxe >= 0) selectSlot(pickaxe);
            mc.options.attackKey.setPressed(true);
            attackKeyWasPressed = true;
            BaritoneHelper.breakBlock(target, Direction.UP);
            if (!mc.world.getBlockState(target).isOf(Blocks.ANCIENT_DEBRIS)) {
                xray.removeFoundBlock(target);
            }
            return;
        }

        if (now - phaseStartedAt > 1800L) nextArea();
    }

    private void tickTravelNextArea(long now) {
        if (!moveTo(travelTarget, horizontal.get().doubleValue(), 1.0)) return;
        workOrigin = mc.player.getEntityPos();
        direction = direction.rotateY((float) (Math.PI / 2.0));
        phase = Phase.MINE_TUNNEL;
        phaseStartedAt = now;
        actionBlock = null;
        tunnelIndex = 0;
    }

    private void nextArea() {
        releaseAttackKey();
        direction = direction.rotateY((float) (Math.PI / 2.0));
        travelTarget = mc.player.getEntityPos().add(direction.multiply(NEXT_AREA_BLOCKS));
        phase = Phase.TRAVEL_NEXT_AREA;
        phaseStartedAt = System.currentTimeMillis();
        tntBlock = null;
    }

    private boolean moveTo(Vec3d target, double speed, double tolerance) {
        Vec3d delta = target.subtract(mc.player.getEntityPos());
        if (delta.lengthSquared() <= tolerance * tolerance) {
            stopFlying();
            return true;
        }
        Vec3d velocity = delta.normalize().multiply(speed);
        mc.player.setVelocity(velocity);
        mc.player.velocityDirty = true;
        return false;
    }

    private void stopFlying() {
        mc.player.setVelocity(Vec3d.ZERO);
        mc.player.velocityDirty = true;
    }

    private void aimAt(Vec3d target) {
        Vec3d eye = mc.player.getEyePos();
        double dx = target.x - eye.x;
        double dy = target.y - eye.y;
        double dz = target.z - eye.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) MathHelper.clamp(-Math.toDegrees(Math.atan2(dy, horizontal)), -90.0, 90.0);
        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
        BaritoneHelper.lookAt(yaw, pitch);
    }

    private void placeBlock(BlockPos pos) {
        BlockPos support = pos.down();
        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(support).add(0.0, 0.5, 0.0),
                Direction.UP, support, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private void useOnBlock(BlockPos pos) {
        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private void selectSlot(int slot) {
        if (slot < 0 || slot > 8) return;
        mc.player.getInventory().setSelectedSlot(slot);
    }

    private int findPickaxeHotbar() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            String id = String.valueOf(Registries.ITEM.getId(stack.getItem()));
            if (id.contains("pickaxe") || id.contains("_pick") || id.contains("drill") || id.contains("hammer")) {
                return i;
            }
        }
        return -1;
    }

    private Vec3d horizontalDirection(float yaw) {
        double radians = Math.toRadians(yaw);
        return new Vec3d(-Math.sin(radians), 0.0, Math.cos(radians)).normalize();
    }

    private void releaseAttackKey() {
        if (attackKeyWasPressed && mc.options != null) {
            mc.options.attackKey.setPressed(false);
            attackKeyWasPressed = false;
        }
    }

    private AncientXray getXray() {
        return LightClient.getFunctionManager() == null ? null
                : LightClient.getFunctionManager().getAncientXray();
    }

    @EventHandler
    public void onPacket(EventPacket event) {
        if (!event.isReceive() || !(event.getPacket() instanceof ExplosionS2CPacket explosion) || tntBlock == null) return;
        if (Vec3d.ofCenter(tntBlock).squaredDistanceTo(explosion.center()) <= 36.0) {
            explosionReceived = true;
        }
    }

    @Override
    public void onDisable() {
        releaseAttackKey();
        stopFlying();
        BaritoneRotationHook.disable();
        BaritoneHelper.stop();
        BaritoneHelper.applySettings(false, 2.0);
        if (xrayStartedByFarm) {
            AncientXray xray = getXray();
            if (xray != null) xray.setState(false);
        }
        xrayStartedByFarm = false;
        super.onDisable();
    }
}
