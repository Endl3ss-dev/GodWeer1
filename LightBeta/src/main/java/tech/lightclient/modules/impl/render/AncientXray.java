package tech.lightclient.modules.impl.render;

import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.player.PlayerInventoryUtil;
import tech.lightclient.client.utils.render.main.world.WorldGeometryEmitter;
import tech.lightclient.client.utils.render.main.world.WorldRenderLayers;
import tech.lightclient.client.utils.render.main.world.WorldRenderer;
import tech.lightclient.mixins.accessors.GameRendererAccessor;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ищет древние обломки после взрывов и подсвечивает найденные блоки.
 * Логика перенесена с AncientXray из Swagline и адаптирована под EventBus Light.
 */
@FunctionAdd(
        name = "AncientXray",
        alias = "Ancient Xray",
        category = Category.Render,
        description = "Поиск древних обломков после взрыва TNT"
)
public class AncientXray extends Function {
    private static final int[] EXPLOSION_DELAYS = {4, 10, 20, 40};
    private static final int SCAN_RADIUS = 28;
    private static final int DEBRIS_COLOR = 0x8000FF00;

    private final Set<BlockPos> foundBlocks = ConcurrentHashMap.newKeySet();
    private final List<DelayBlock> delayedScans = new ArrayList<>();

    private UseAction activeUse = UseAction.NONE;
    private Hand activeUseHand;
    private int activeUseInventorySlot = -1;
    private int activeUseRestoreSlot = -1;
    private long activeUseStartedAt;
    private boolean useKeyWasPressed;

    private enum UseAction {
        NONE,
        FIRE_RESISTANCE,
        GOLDEN_CARROT
    }

    private static final class DelayBlock {
        private final BlockPos pos;
        private int ticks;

        private DelayBlock(BlockPos pos, int ticks) {
            this.pos = pos;
            this.ticks = ticks;
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        synchronized (delayedScans) {
            Iterator<DelayBlock> iterator = delayedScans.iterator();
            while (iterator.hasNext()) {
                DelayBlock delayed = iterator.next();
                if (--delayed.ticks <= 0) {
                    checkBlocks(delayed.pos, SCAN_RADIUS);
                    iterator.remove();
                }
            }
        }

        long now = System.currentTimeMillis();

        if (tickSurvivalNeeds(now)) {
            return;
        }

        // AncientXray only discovers and renders targets. AncientFarm handles
        // flight, mining and TNT so this module never starts walking.
    }

    private boolean tickSurvivalNeeds(long now) {
        if (activeUse != UseAction.NONE) {
            continueActiveUse(now);
            return true;
        }

        if (mc.currentScreen != null || mc.player.isUsingItem()
                || !mc.world.getRegistryKey().equals(World.NETHER)) {
            return false;
        }

        StatusEffectInstance fireResistance = mc.player.getStatusEffect(StatusEffects.FIRE_RESISTANCE);
        if ((fireResistance == null || fireResistance.getDuration() <= 120)
                && beginUse(UseAction.FIRE_RESISTANCE, this::isFireResistancePotion)) {
            return true;
        }

        if (mc.player.getHungerManager().getFoodLevel() <= 14
                && beginUse(UseAction.GOLDEN_CARROT, stack -> stack.isOf(Items.GOLDEN_CARROT))) {
            return true;
        }

        return false;
    }

    private boolean beginUse(UseAction action, java.util.function.Predicate<ItemStack> predicate) {
        Slot slot = PlayerInventoryUtil.slots()
                .filter(candidate -> predicate.test(candidate.getStack()))
                .findFirst()
                .orElse(null);
        if (slot == null || mc.interactionManager == null) {
            return false;
        }

        activeUse = action;
        activeUseStartedAt = System.currentTimeMillis();
        activeUseRestoreSlot = mc.player.getInventory().getSelectedSlot();
        activeUseInventorySlot = -1;
        useKeyWasPressed = InputUtil.isKeyPressed(
                mc.getWindow(), mc.options.useKey.getDefaultKey().getCode());

        if (slot.id == 45) {
            activeUseHand = Hand.OFF_HAND;
        } else if (slot.id >= 36 && slot.id <= 44) {
            activeUseHand = Hand.MAIN_HAND;
            int hotbarSlot = slot.id - 36;
            mc.player.getInventory().setSelectedSlot(hotbarSlot);
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(hotbarSlot));
        } else {
            activeUseHand = Hand.MAIN_HAND;
            activeUseInventorySlot = slot.id;
            PlayerInventoryUtil.clickSlot(slot.id, activeUseRestoreSlot, SlotActionType.SWAP);
            PlayerInventoryUtil.updateSlots();
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(activeUseRestoreSlot));
        }

        mc.interactionManager.interactItem(mc.player, activeUseHand);
        mc.options.useKey.setPressed(true);
        return true;
    }

    private void continueActiveUse(long now) {
        if (mc.player == null || mc.interactionManager == null) {
            finishActiveUse();
            return;
        }

        mc.options.useKey.setPressed(true);
        if ((!mc.player.isUsingItem() && now - activeUseStartedAt > 250L)
                || now - activeUseStartedAt > 6000L) {
            finishActiveUse();
        }
    }

    private void finishActiveUse() {
        if (mc.options != null) {
            mc.options.useKey.setPressed(useKeyWasPressed);
        }

        if (mc.player != null && activeUseInventorySlot >= 0) {
            PlayerInventoryUtil.clickSlot(activeUseInventorySlot, activeUseRestoreSlot, SlotActionType.SWAP);
            PlayerInventoryUtil.updateSlots();
        }

        if (mc.player != null && activeUseRestoreSlot >= 0) {
            mc.player.getInventory().setSelectedSlot(activeUseRestoreSlot);
            if (mc.getNetworkHandler() != null) {
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(activeUseRestoreSlot));
            }
        }

        activeUse = UseAction.NONE;
        activeUseHand = null;
        activeUseInventorySlot = -1;
        activeUseRestoreSlot = -1;
        activeUseStartedAt = 0L;
    }

    private boolean isFireResistancePotion(ItemStack stack) {
        if (stack == null || stack.isEmpty()
                || !(stack.getItem() instanceof PotionItem)
                || stack.getItem() instanceof SplashPotionItem) {
            return false;
        }

        for (StatusEffectInstance effect : stack.getOrDefault(
                net.minecraft.component.DataComponentTypes.POTION_CONTENTS,
                net.minecraft.component.type.PotionContentsComponent.DEFAULT).getEffects()) {
            if (effect.getEffectType().equals(StatusEffects.FIRE_RESISTANCE)) {
                return true;
            }
        }
        return false;
    }

    public BlockPos getNearestFoundBlock() {
        if (mc.player == null || mc.world == null) return null;
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (BlockPos pos : foundBlocks) {
            if (!mc.world.getBlockState(pos).isOf(Blocks.ANCIENT_DEBRIS)) {
                foundBlocks.remove(pos);
                continue;
            }

            double distance = mc.player.squaredDistanceTo(Vec3d.ofCenter(pos));
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = pos;
            }
        }
        return nearest;
    }

    public void removeFoundBlock(BlockPos pos) {
        if (pos != null) foundBlocks.remove(pos);
    }

    @EventHandler
    public void onPacket(EventPacket event) {
        if (!event.isReceive() || mc.world == null) {
            return;
        }

        Packet<?> packet = event.getPacket();
        if (packet instanceof ExplosionS2CPacket explosion) {
            BlockPos center = BlockPos.ofFloored(explosion.center()).toImmutable();
            synchronized (delayedScans) {
                for (int delay : EXPLOSION_DELAYS) {
                    delayedScans.add(new DelayBlock(center, delay));
                }
            }
        } else if (packet instanceof BlockUpdateS2CPacket update) {
            handleBlockUpdate(update.getPos(), update.getState().getBlock());
        } else if (packet instanceof ChunkDeltaUpdateS2CPacket delta) {
            delta.visitUpdates((pos, state) -> handleBlockUpdate(pos, state.getBlock()));
        }
    }

    private void handleBlockUpdate(BlockPos pos, Block block) {
        BlockPos immutable = pos.toImmutable();
        if (block == Blocks.ANCIENT_DEBRIS) {
            if (isValidDebris(immutable)) foundBlocks.add(immutable);
            return;
        }

        foundBlocks.remove(immutable);
    }

    private void checkBlocks(BlockPos center, int radius) {
        if (mc.world == null) {
            return;
        }

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    mutable.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    if (!isValidDebris(mutable)) {
                        continue;
                    }

                    BlockPos immutable = mutable.toImmutable();
                    foundBlocks.add(immutable);
                }
            }
        }
    }

    @EventHandler
    public void onRenderWorld(EventRender.World event) {
        if (mc.world == null || mc.player == null || foundBlocks.isEmpty()) {
            return;
        }

        Camera camera = mc.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getCameraPos();
        float fov = ((GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, event.getTicks(), true);

        try (WorldRenderer renderer = WorldRenderer.begin(
                mc,
                mc.getRenderTickCounter(),
                camera,
                event.getMatrixStack().peek().getPositionMatrix(),
                mc.gameRenderer.getBasicProjectionMatrix(fov))) {
            // The first AncientXray build rendered debris as a half-transparent
            // green overlay that was visible through blocks.  The plain
            // no-depth layer does not enable blending, so use the translucent
            // variant to preserve that glow instead of drawing an opaque box.
            RenderLayer layer = WorldRenderLayers.POSITION_COLOR_QUADS_TRANSLUCENT_NO_DEPTH();
            WorldGeometryEmitter emitter = new WorldGeometryEmitter(
                    camera, new MatrixStack().peek(), renderer.getBuffer(layer));

            for (BlockPos pos : foundBlocks) {
                if (!mc.world.getBlockState(pos).isOf(Blocks.ANCIENT_DEBRIS)) {
                    foundBlocks.remove(pos);
                    continue;
                }

                emitBlockFill(emitter,
                        pos.getX() - cameraPos.x,
                        pos.getY() - cameraPos.y,
                        pos.getZ() - cameraPos.z,
                        DEBRIS_COLOR);
            }
            renderer.flush();
        }
    }

    private void emitBlockFill(WorldGeometryEmitter emitter, double x, double y, double z, int color) {
        Vec3d p000 = new Vec3d(x, y, z);
        Vec3d p001 = new Vec3d(x, y, z + 1.0);
        Vec3d p010 = new Vec3d(x, y + 1.0, z);
        Vec3d p011 = new Vec3d(x, y + 1.0, z + 1.0);
        Vec3d p100 = new Vec3d(x + 1.0, y, z);
        Vec3d p101 = new Vec3d(x + 1.0, y, z + 1.0);
        Vec3d p110 = new Vec3d(x + 1.0, y + 1.0, z);
        Vec3d p111 = new Vec3d(x + 1.0, y + 1.0, z + 1.0);

        emitter.emitQuad(p000, p100, p110, p010, color, color, color, color);
        emitter.emitQuad(p001, p011, p111, p101, color, color, color, color);
        emitter.emitQuad(p000, p001, p101, p100, color, color, color, color);
        emitter.emitQuad(p010, p110, p111, p011, color, color, color, color);
        emitter.emitQuad(p000, p010, p011, p001, color, color, color, color);
        emitter.emitQuad(p100, p101, p111, p110, color, color, color, color);
    }

    private boolean isValidDebris(BlockPos pos) {
        if (mc.world == null || !mc.world.getBlockState(pos).isOf(Blocks.ANCIENT_DEBRIS)) {
            return false;
        }
        return checkAirNearby(pos) && !checkDebrisNearby(pos)
                && !checkQuartzOrGold(pos) && checkAirLavaNearbyBig(pos);
    }

    private boolean checkDebrisNearby(BlockPos pos) {
        int count = 0;
        for (int x = -3; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 3; z++) {
                    if (mc.world.getBlockState(pos.add(x, y, z)).isOf(Blocks.ANCIENT_DEBRIS)
                            && ++count > 6) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkAirNearby(BlockPos pos) {
        int count = 0;
        for (Direction direction : Direction.values()) {
            Block block = mc.world.getBlockState(pos.offset(direction)).getBlock();
            if (block == Blocks.AIR || block == Blocks.LAVA || block == Blocks.CAVE_AIR) {
                if (++count >= 2) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkQuartzOrGold(BlockPos pos) {
        int count = 0;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block block = mc.world.getBlockState(pos.add(x, y, z)).getBlock();
                    if ((block == Blocks.NETHER_QUARTZ_ORE || block == Blocks.NETHER_GOLD_ORE)
                            && ++count >= 4) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkAirLavaNearbyBig(BlockPos pos) {
        int count = 0;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block block = mc.world.getBlockState(pos.add(x, y, z)).getBlock();
                    if (block == Blocks.AIR || block == Blocks.LAVA || block == Blocks.CAVE_AIR) {
                        if (++count >= 4) {
                            return true;
                        }
                    }
                }
            }
        }
        return count >= 4;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        clearState();
    }

    @Override
    public void onDisable() {
        finishActiveUse();
        clearState();
        super.onDisable();
    }

    private void clearState() {
        foundBlocks.clear();
        synchronized (delayedScans) {
            delayedScans.clear();
        }
    }
}
