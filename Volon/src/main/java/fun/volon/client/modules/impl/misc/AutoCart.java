package fun.volon.client.modules.impl.misc;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.implement.EventUpdate;
import fun.volon.client.modules.Module;

import java.util.HashSet;
import java.util.Set;

/**
 * AutoCart — отслеживает стрелы игрока → предсказывает точку падения →
 * ставит рельсу + TNT-вагонетку. Аналог standalone-мода AutoCart,
 * интегрированный в архитектуру Volon.
 *
 * Стрелять нужно вручную (своим луком) — модуль лишь ловит летящие
 * стрелы и реагирует на их приземление.
 *
 * Фикс оригинального бага: трава/кустарники/снежный слой не являются
 * replaceable по yarn, но рельса туда ставится (растительность/снег
 * просто исчезает). См. {@link #isRailReplaceable(BlockState)}.
 */
public class AutoCart extends Module {

    public static AutoCart INSTANCE = new AutoCart();

    private static final int MAX_PREDICTION_TICKS = 80;
    private static final long CART_PLACE_DELAY_MS = 100L;
    private static final double RANGE = 5.0;
    private static final double RANGE_SQ = RANGE * RANGE;

    private final Set<Integer> knownArrows = new HashSet<>();
    private final Set<Integer> processedArrows = new HashSet<>();
    private long nextCartPlaceAttemptMs;

    public AutoCart() {
        super("AutoCart", "Ставит рельсу + TNT вагонетку в точку падения стрелы", ModuleCategory.MISC);
    }

    @Override
    public void onEnable() {
        knownArrows.clear();
        processedArrows.clear();
        nextCartPlaceAttemptMs = 0L;
        cacheExistingArrows();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        knownArrows.clear();
        processedArrows.clear();
        nextCartPlaceAttemptMs = 0L;
        super.onDisable();
    }

    @EventLink
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) {
            return;
        }

        Set<Integer> aliveArrows = new HashSet<>();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof PersistentProjectileEntity projectile) || projectile instanceof TridentEntity) {
                continue;
            }
            if (projectile.getOwner() != mc.player) {
                continue;
            }

            int id = projectile.getId();
            aliveArrows.add(id);
            knownArrows.add(id);

            if (processedArrows.contains(id)) {
                continue;
            }
            if (!hasRequiredItemsInHotbar()) {
                continue;
            }
            if (!shouldProcessArrow(projectile)) {
                continue;
            }

            LandingPrediction prediction = predictLanding(projectile);
            if (prediction == null) {
                continue;
            }
            if (mc.player.squaredDistanceTo(Vec3d.ofCenter(prediction.placePos())) > RANGE_SQ) {
                processedArrows.add(id);
                continue;
            }

            if (placeCartAt(prediction.placePos()) || projectile.isOnGround()) {
                processedArrows.add(id);
            }
        }

        knownArrows.retainAll(aliveArrows);
        processedArrows.retainAll(aliveArrows);
    }

    // ======================= Стрелы =======================

    private void cacheExistingArrows() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof PersistentProjectileEntity projectile) || projectile instanceof TridentEntity) {
                continue;
            }
            if (projectile.getOwner() == mc.player) {
                knownArrows.add(projectile.getId());
            }
        }
    }

    private boolean hasRequiredItemsInHotbar() {
        return searchItemHotbar(Items.RAIL) != -1
                && searchItemHotbar(Items.TNT_MINECART) != -1;
    }

    private boolean shouldProcessArrow(PersistentProjectileEntity projectile) {
        return !projectile.isOnGround() && projectile.getVelocity().lengthSquared() > 1.0E-4;
    }

    private LandingPrediction predictLanding(PersistentProjectileEntity arrow) {
        if (mc.world == null) {
            return null;
        }

        Vec3d pos = arrow.getPos();
        Vec3d motion = arrow.getVelocity();

        for (int tick = 1; tick <= MAX_PREDICTION_TICKS; tick++) {
            Vec3d prevPos = pos;
            pos = pos.add(motion);
            motion = updateMotion(arrow, prevPos, motion);

            BlockHitResult hit = raycast(prevPos, pos, arrow);
            if (hit.getType() != HitResult.Type.MISS) {
                BlockPos placePos = resolvePlacePos(hit);
                if (placePos != null) {
                    return new LandingPrediction(placePos, tick);
                }
                return null;
            }

            if (pos.y < mc.world.getBottomY() - 2) {
                break;
            }
        }
        return null;
    }

    private Vec3d updateMotion(PersistentProjectileEntity arrow, Vec3d pos, Vec3d motion) {
        if (mc.world == null) {
            return motion;
        }
        FluidState fluidState = mc.world.getBlockState(BlockPos.ofFloored(pos)).getFluidState();
        boolean inWater = fluidState.isIn(FluidTags.WATER);
        double drag = inWater ? 0.6 : 0.99;
        return motion.multiply(drag).add(0.0, -arrow.getFinalGravity(), 0.0);
    }

    private BlockHitResult raycast(Vec3d start, Vec3d end, Entity entity) {
        return mc.world.raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                entity
        ));
    }

    private BlockPos resolvePlacePos(BlockHitResult hit) {
        if (mc.world == null) {
            return null;
        }

        BlockPos basePos = hit.getBlockPos();
        if (isRail(basePos)) {
            return basePos;
        }

        BlockPos preferred = hit.getSide() == Direction.UP ? basePos.up() : basePos;
        if (canPlaceRailAt(preferred)) {
            return preferred;
        }

        BlockPos up = basePos.up();
        if (canPlaceRailAt(up)) {
            return up;
        }

        BlockPos down = basePos.down();
        if (canPlaceRailAt(down)) {
            return down;
        }
        return null;
    }

    // ======================= Установка =======================

    private boolean placeCartAt(BlockPos placePos) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) {
            return false;
        }

        int railSlot = searchItemHotbar(Items.RAIL);
        int cartSlot = searchItemHotbar(Items.TNT_MINECART);
        if (railSlot == -1 || cartSlot == -1) {
            return false;
        }

        int prevSlot = getSelectedSlot();
        boolean placed;

        try {
            if (!isRail(placePos) && !placeRail(placePos, railSlot)) {
                return false;
            }
            if (hasTntMinecart(placePos)) {
                return true;
            }
            if (System.currentTimeMillis() < nextCartPlaceAttemptMs) {
                return false;
            }
            if (placeTntMinecart(placePos, cartSlot)) {
                nextCartPlaceAttemptMs = System.currentTimeMillis() + CART_PLACE_DELAY_MS;
                restockMinecartSlot(cartSlot);
            }
            placed = hasTntMinecart(placePos);
        } finally {
            setSelectedSlot(prevSlot);
        }
        return placed;
    }

    private boolean isRail(BlockPos pos) {
        return mc.world != null && mc.world.getBlockState(pos).isIn(BlockTags.RAILS);
    }

    /**
     * Можно ли поставить рельсу на эту позицию.
     *
     * Исправление оригинального бага: обычный {@link BlockState#isReplaceable()}
     * возвращает false для травы ({@link Blocks#SHORT_GRASS}, {@link Blocks#FERN},
     * {@link Blocks#TALL_GRASS}, {@link Blocks#LARGE_FERN}, {@link Blocks#DEAD_BUSH}),
     * цветов и снежного слоя ({@link Blocks#SNOW}), хотя рельса туда ставится
     * без проблем — мешающий блок просто уничтожается. Поэтому дополнительно
     * учитываем эти блоки через {@link #isRailReplaceable(BlockState)}.
     */
    private boolean canPlaceRailAt(BlockPos pos) {
        if (mc.world == null) {
            return false;
        }
        if (isRail(pos)) {
            return true;
        }

        BlockState state = mc.world.getBlockState(pos);
        if (!isRailReplaceable(state)) {
            return false;
        }

        BlockPos support = pos.down();
        return mc.world.getBlockState(support).isSideSolidFullSquare(mc.world, support, Direction.UP);
    }

    /**
     * Является ли блок «удаляемым» при установке рельсы.
     * К replaceable блокам добавляем растительность и снег — рельса ставится
     * поверх, а растение/снег пропадает (как в ванильном поведении).
     */
    private boolean isRailReplaceable(BlockState state) {
        if (state.isReplaceable()) {
            return true;
        }
        // Снежный слой (SNOW) и снежный блок
        if (state.isIn(BlockTags.SNOW)) {
            return true;
        }
        // Трава, папоротники, цветы, кустарники — всё что ломается мечом за удар
        if (state.isIn(BlockTags.SWORD_EFFICIENT)) {
            return true;
        }
        // Страховка: явные блоки растительности (на случай отсутствия тега)
        return state.isOf(Blocks.SHORT_GRASS)
                || state.isOf(Blocks.TALL_GRASS)
                || state.isOf(Blocks.FERN)
                || state.isOf(Blocks.LARGE_FERN)
                || state.isOf(Blocks.DEAD_BUSH)
                || state.isOf(Blocks.VINE)
                || state.isOf(Blocks.SNOW);
    }

    private boolean placeRail(BlockPos placePos, int railSlot) {
        if (mc.player == null || mc.interactionManager == null) {
            return false;
        }

        setSelectedSlot(railSlot);

        BlockPos support = placePos.down();
        BlockHitResult hitResult = new BlockHitResult(
                new Vec3d(placePos.getX() + 0.5, placePos.getY(), placePos.getZ() + 0.5),
                Direction.UP,
                support,
                false
        );

        ActionResult result = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
        if (result.isAccepted()) {
            mc.player.swingHand(Hand.MAIN_HAND);
            return true;
        }
        return isRail(placePos);
    }

    private boolean placeTntMinecart(BlockPos railPos, int cartSlot) {
        if (mc.player == null || mc.interactionManager == null) {
            return false;
        }

        setSelectedSlot(cartSlot);

        BlockHitResult hitResult = new BlockHitResult(
                new Vec3d(railPos.getX() + 0.5, railPos.getY() + 0.15, railPos.getZ() + 0.5),
                Direction.UP,
                railPos,
                false
        );

        ActionResult result = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
        if (result.isAccepted()) {
            mc.player.swingHand(Hand.MAIN_HAND);
            return true;
        }
        return false;
    }

    private void restockMinecartSlot(int hotbarSlot) {
        if (mc.player == null || mc.interactionManager == null) {
            return;
        }
        if (hotbarSlot < 0 || hotbarSlot > 8) {
            return;
        }
        if (mc.player.getInventory().getStack(hotbarSlot).isOf(Items.TNT_MINECART)) {
            return;
        }

        int invSlot = searchItem(Items.TNT_MINECART, 9, 36);
        if (invSlot == -1) {
            return;
        }
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, invSlot, hotbarSlot, SlotActionType.SWAP, mc.player);
    }

    private boolean hasTntMinecart(BlockPos railPos) {
        if (mc.world == null) {
            return false;
        }
        Box checkBox = new Box(railPos).expand(0.2, 1.0, 0.2);
        for (Entity entity : mc.world.getOtherEntities(null, checkBox)) {
            if (entity instanceof TntMinecartEntity) {
                return true;
            }
        }
        return false;
    }

    // ======================= Инвентарь =======================

    private int searchItemHotbar(Item item) {
        return searchItem(item, 0, 9);
    }

    private int getSelectedSlot() {
        return mc.player.getInventory().selectedSlot;
    }

    private void setSelectedSlot(int slot) {
        if (mc.player == null || slot < 0 || slot > 8) {
            return;
        }
        mc.player.getInventory().selectedSlot = slot;
        if (mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        }
    }

    private int searchItem(Item item, int startInclusive, int endExclusive) {
        if (mc.player == null) {
            return -1;
        }
        for (int slot = startInclusive; slot < endExclusive; slot++) {
            if (mc.player.getInventory().getStack(slot).isOf(item)) {
                return slot;
            }
        }
        return -1;
    }

    private record LandingPrediction(BlockPos placePos, int ticksToImpact) {
    }
}
