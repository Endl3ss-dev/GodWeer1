package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "CrystalAura", category = Category.COMBAT, description = "Продвинутая Crystal PvP аура для HvH")
public class CrystalAura extends Module {

    public static CrystalAura getInstance() {
        return Instance.get(CrystalAura.class);
    }

    private final SliderSetting placeRange = new SliderSetting(this, "Дистанция размещения", 5.0F, 1.0F, 6.0F, 0.1F);
    private final SliderSetting breakRange = new SliderSetting(this, "Дистанция взрыва", 5.0F, 1.0F, 6.0F, 0.1F);
    private final SliderSetting targetRange = new SliderSetting(this, "Дистанция до цели", 12.0F, 5.0F, 20.0F, 0.5F);
    private final SliderSetting placeDelay = new SliderSetting(this, "Задержка размещения", 50, 0, 500, 5);
    private final SliderSetting breakDelay = new SliderSetting(this, "Задержка взрыва", 50, 0, 500, 5);

    private final BooleanSetting rotate = new BooleanSetting(this, "Ротация", true);
    private final ModeSetting rotateMode = new ModeSetting(this, "Режим ротации", "Моментальная", "Плавная");
    private final SliderSetting rotateSpeed = new SliderSetting(this, "Скорость ротации", 30.0F, 1.0F, 100.0F, 1.0F).setVisible(() -> rotateMode.is("Плавная"));

    private final BooleanSetting raytrace = new BooleanSetting(this, "Raytrace", true);
    private final BooleanSetting autoObsidian = new BooleanSetting(this, "Авто обсидиан", true);
    private final BooleanSetting onlyIfTargetAbove = new BooleanSetting(this, "Только если цель выше", true).setVisible(autoObsidian::getValue);

    private final BooleanSetting throughWalls = new BooleanSetting(this, "Через стены", true);
    private final BooleanSetting breakThroughWalls = new BooleanSetting(this, "Взрывать через стены", true);

    private final BooleanSetting antiSuicide = new BooleanSetting(this, "Анти-суицид", true);
    private final SliderSetting minHealth = new SliderSetting(this, "Минимальное здоровье", 8.0F, 1.0F, 20.0F, 0.5F).setVisible(antiSuicide::getValue);
    private final BooleanSetting checkTotem = new BooleanSetting(this, "Проверка тотема", true).setVisible(antiSuicide::getValue);
    private final BooleanSetting checkCrystalHeight = new BooleanSetting(this, "Проверка высоты", true).setVisible(antiSuicide::getValue);

    private final BooleanSetting targetOnBlock = new BooleanSetting(this, "Цель на блоке", true);
    private final BooleanSetting onlyHotbar = new BooleanSetting(this, "Только хотбар", true);

    private final BooleanSetting highlightBlocks = new BooleanSetting(this, "Подсветка блоков", true);
    private final SliderSetting highlightAlpha = new SliderSetting(this, "Прозрачность", 0.3F, 0.1F, 1.0F, 0.05F).setVisible(highlightBlocks::getValue);

    private long lastPlaceTime = 0;
    private long lastBreakTime = 0;
    private long lastObsidianPlaceTime = 0;
    private Vector2f currentRotation = new Vector2f(0, 0);
    private Vector2f targetRotation = null;
    private boolean isRotating = false;

    private final List<BlockPos> validPlacePositions = new ArrayList<>();
    private final List<BlockPos> obsidianPlacePositions = new ArrayList<>();
    private PlayerEntity currentTarget = null;

    @Override
    public void onEnable() {
        super.onEnable();
        resetState();
        if (mc.player != null) {
            currentRotation = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        resetState();
    }

    private void resetState() {
        lastPlaceTime = 0;
        lastBreakTime = 0;
        lastObsidianPlaceTime = 0;
        validPlacePositions.clear();
        obsidianPlacePositions.clear();
        currentTarget = null;
        isRotating = false;
        targetRotation = null;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        handleRotation();

        updateTarget();
        updateValidPositions();

        long now = System.currentTimeMillis();

        if (autoObsidian.getValue() && now - lastObsidianPlaceTime >= 100) {
            if (tryPlaceObsidian()) {
                lastObsidianPlaceTime = now;
                return;
            }
        }

        if (now - lastBreakTime >= breakDelay.getValue().intValue()) {
            EnderCrystalEntity crystal = findBestCrystal();
            if (crystal != null) {
                breakCrystal(crystal);
                lastBreakTime = now;
                return;
            }
        }

        if (now - lastPlaceTime >= placeDelay.getValue().intValue()) {
            BlockPos bestPos = findBestPlacePosition();
            if (bestPos != null) {
                placeCrystal(bestPos);
                lastPlaceTime = now;
            }
        }
    }

    @EventHandler
    public void onRender3D(Render3DLastEvent event) {
        if (!highlightBlocks.getValue() || validPlacePositions.isEmpty()) return;

        int baseColor = Theme.getInstance().clientColor();
        int alpha = (int) (highlightAlpha.getValue() * 255);

        for (BlockPos pos : validPlacePositions) {
            AxisAlignedBB box = new AxisAlignedBB(pos);
            if (!mc.worldRenderer.getClippinghelper().isBoundingBoxInFrustum(box)) continue;

            RenderUtil3D.drawBoundingBox(event.getMatrix(), box,
                    ColorUtil.replAlpha(baseColor, alpha), 2.0f, false, false);
        }

        if (autoObsidian.getValue()) {
            int obsidianColor = ColorUtil.rgba(255, 165, 0, alpha);
            for (BlockPos pos : obsidianPlacePositions) {
                AxisAlignedBB box = new AxisAlignedBB(pos);
                if (!mc.worldRenderer.getClippinghelper().isBoundingBoxInFrustum(box)) continue;

                RenderUtil3D.drawBoundingBox(event.getMatrix(), box,
                        obsidianColor, 2.0f, false, false);
            }
        }
    }

    private void handleRotation() {
        if (!rotate.getValue() || targetRotation == null) return;

        if (rotateMode.is("Моментальная")) {
            currentRotation = targetRotation;
            RotationComponent.update(new Rotation(currentRotation.x, currentRotation.y), 360, 360, 0, 5);
            isRotating = false;
            targetRotation = null;
        } else {
            float yawDiff = Math.abs(MathHelper.wrapDegrees(targetRotation.x - currentRotation.x));
            float pitchDiff = Math.abs(targetRotation.y - currentRotation.y);

            if (yawDiff < 1.0f && pitchDiff < 1.0f) {
                isRotating = false;
                targetRotation = null;
            } else {
                currentRotation = smoothRotate(targetRotation, currentRotation, 0.3f);
                RotationComponent.update(new Rotation(currentRotation.x, currentRotation.y), 360, 360, 0, 5);
            }
        }
    }

    private void updateTarget() {
        currentTarget = mc.world.getPlayers().stream()
                .filter(this::isValidTarget)
                .min(Comparator.comparingDouble(player -> mc.player.getDistance(player)))
                .orElse(null);
    }

    private boolean isValidTarget(PlayerEntity player) {
        if (player == mc.player) return false;
        if (player.removed || !player.isAlive()) return false;
        if (player.getHealth() <= 0) return false;
        if (Excellent.inst().friendManager().isFriend(player.getGameProfile().getName())) return false;

        double distance = mc.player.getDistance(player);
        if (distance > targetRange.getValue()) return false;

        return !AntiBot.getInstance().isEnabled() || !AntiBot.getInstance().isBot(player);
    }

    private void updateValidPositions() {
        validPlacePositions.clear();
        obsidianPlacePositions.clear();

        Vector3d playerPos = mc.player.getPositionVec();
        BlockPos playerBlockPos = new BlockPos(playerPos);
        int range = (int) Math.ceil(placeRange.getValue());
        BlockPos minPos = playerBlockPos.add(-range, -range, -range);
        BlockPos maxPos = playerBlockPos.add(range, range, range);

        for (BlockPos pos : BlockPos.getAllInBoxMutable(minPos, maxPos)) {
            double distance = playerPos.distanceTo(new Vector3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
            if (distance > placeRange.getValue()) continue;

            if (isValidPlacePosition(pos)) {
                validPlacePositions.add(pos.toImmutable());
            } else if (autoObsidian.getValue() && canPlaceObsidian(pos)) {
                obsidianPlacePositions.add(pos.toImmutable());
            }
        }
    }

    private boolean isValidPlacePosition(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (block != Blocks.OBSIDIAN && block != Blocks.BEDROCK) {
            return false;
        }

        BlockPos above = pos.up();
        BlockPos above2 = pos.up(2);

        if (!mc.world.getBlockState(above).isAir() || !mc.world.getBlockState(above2).isAir()) {
            return false;
        }

        if (!mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(above)).isEmpty()) {
            return false;
        }

        if (!throughWalls.getValue()) {
            if (!canSeeBlock(pos)) {
                return false;
            }
        }

        if (targetOnBlock.getValue() && currentTarget != null) {
            BlockPos targetPos = currentTarget.getPosition();
            double targetY = currentTarget.getPosY();
            double blockY = pos.getY();

            if (targetY < blockY || targetY > blockY + 2) {
                return false;
            }

            double horizontalDist = Math.sqrt(
                    Math.pow(targetPos.getX() - pos.getX(), 2) +
                            Math.pow(targetPos.getZ() - pos.getZ(), 2)
            );

            if (horizontalDist > 3.0) {
                return false;
            }
        }

        if (antiSuicide.getValue()) {
            return isSafeToPlace(pos);
        }

        return true;
    }

    private boolean canPlaceObsidian(BlockPos pos) {
        if (!mc.world.getBlockState(pos).isAir()) {
            return false;
        }

        if (!hasNeighborBlock(pos)) {
            return false;
        }

        if (!mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)).isEmpty()) {
            return false;
        }

        if (currentTarget == null) return false;

        if (onlyIfTargetAbove.getValue()) {
            double targetY = currentTarget.getPosY();
            double blockY = pos.getY();

            if (targetY <= blockY) {
                return false;
            }
        }

        BlockPos above = pos.up();
        BlockPos above2 = pos.up(2);

        return mc.world.getBlockState(above).isAir() && mc.world.getBlockState(above2).isAir();
    }

    private boolean hasNeighborBlock(BlockPos pos) {
        Direction[] directions = {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        for (Direction dir : directions) {
            BlockPos neighbor = pos.offset(dir);
            Block block = mc.world.getBlockState(neighbor).getBlock();
            if (block != Blocks.AIR && block != Blocks.WATER && block != Blocks.LAVA) {
                return true;
            }
        }
        return false;
    }

    private boolean isSafeToPlace(BlockPos pos) {
        float health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        if (health <= minHealth.getValue()) {
            return false;
        }

        if (checkTotem.getValue()) {
            if (!hasTotem()) {
                return false;
            }
        }

        if (checkCrystalHeight.getValue()) {
            double crystalY = pos.getY() + 1.0;
            double playerY = mc.player.getPosY();
            double playerEyeY = mc.player.getPosYEye();

            if (playerY < crystalY && playerEyeY < crystalY + 1.0) {
                return false;
            }

            if (Math.abs(playerY - crystalY) < 2.0) {
                double distance = mc.player.getPositionVec().distanceTo(
                        new Vector3d(pos.getX() + 0.5, crystalY, pos.getZ() + 0.5)
                );
                return !(distance < 3.0);
            }
        }

        return true;
    }

    private boolean hasTotem() {
        ItemStack offhand = mc.player.getHeldItemOffhand();
        if (offhand.getItem() == Items.TOTEM_OF_UNDYING) {
            return true;
        }

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                return true;
            }
        }

        return false;
    }

    private boolean canSeeBlock(BlockPos pos) {
        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        Vector3d blockCenter = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        return mc.world.rayTraceBlocks(new net.minecraft.util.math.RayTraceContext(
                playerEyes, blockCenter,
                net.minecraft.util.math.RayTraceContext.BlockMode.OUTLINE,
                net.minecraft.util.math.RayTraceContext.FluidMode.NONE,
                mc.player
        )).getType() == net.minecraft.util.math.RayTraceResult.Type.MISS;
    }

    private BlockPos findBestPlacePosition() {
        if (validPlacePositions.isEmpty()) return null;

        if (currentTarget != null) {
            return validPlacePositions.stream()
                    .min(Comparator.comparingDouble(pos -> {
                        Vector3d crystalPos = new Vector3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                        return currentTarget.getPositionVec().distanceTo(crystalPos);
                    }))
                    .orElse(null);
        }

        return validPlacePositions.stream()
                .min(Comparator.comparingDouble(pos -> {
                    Vector3d crystalPos = new Vector3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                    return mc.player.getPositionVec().distanceTo(crystalPos);
                }))
                .orElse(null);
    }

    private EnderCrystalEntity findBestCrystal() {
        List<EnderCrystalEntity> crystals = mc.world.getEntitiesWithinAABB(
                EnderCrystalEntity.class,
                mc.player.getBoundingBox().grow(breakRange.getValue())
        );

        if (crystals.isEmpty()) return null;

        return crystals.stream()
                .filter(crystal -> !crystal.removed)
                .filter(crystal -> mc.player.getDistance(crystal) <= breakRange.getValue())
                .filter(crystal -> breakThroughWalls.getValue() || canSeeCrystal(crystal))
                .min(Comparator.comparingDouble(crystal -> {
                    if (currentTarget != null) {
                        return currentTarget.getDistance(crystal);
                    }
                    return mc.player.getDistance(crystal);
                }))
                .orElse(null);
    }

    private boolean canSeeCrystal(EnderCrystalEntity crystal) {
        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        Vector3d crystalPos = crystal.getPositionVec().add(0, crystal.getEyeHeight() * 0.5, 0);

        return mc.world.rayTraceBlocks(new net.minecraft.util.math.RayTraceContext(
                playerEyes, crystalPos,
                net.minecraft.util.math.RayTraceContext.BlockMode.OUTLINE,
                net.minecraft.util.math.RayTraceContext.FluidMode.NONE,
                mc.player
        )).getType() == net.minecraft.util.math.RayTraceResult.Type.MISS;
    }

    private boolean tryPlaceObsidian() {
        if (obsidianPlacePositions.isEmpty()) return false;

        int obsidianSlot = findObsidianSlot();
        if (obsidianSlot == -1) return false;

        BlockPos bestPos = obsidianPlacePositions.stream()
                .filter(pos -> currentTarget != null)
                .min(Comparator.comparingDouble(pos -> {
                    Vector3d blockCenter = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    return currentTarget.getPositionVec().distanceTo(blockCenter);
                }))
                .orElse(null);

        if (bestPos == null) return false;

        PlaceInfo placeInfo = getPlaceInfo(bestPos);
        if (placeInfo == null) return false;

        if (rotate.getValue()) {
            Vector2f rot = calculateRotation(placeInfo.hitVec);
            currentRotation = rot;
            RotationComponent.update(new Rotation(currentRotation.x, currentRotation.y), 360, 360, 0, 5);
        }

        placeBlock(bestPos, placeInfo, obsidianSlot);
        return true;
    }

    private void placeCrystal(BlockPos pos) {
        int crystalSlot = findCrystalSlot();
        if (crystalSlot == -1) return;

        Vector3d placePos = new Vector3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);

        if (rotate.getValue()) {
            Vector2f rot = calculateRotation(placePos);
            currentRotation = rot;
            RotationComponent.update(new Rotation(currentRotation.x, currentRotation.y), 360, 360, 0, 5);
        }

        int previousSlot = mc.player.inventory.currentItem;

        if (crystalSlot != previousSlot) {
            mc.player.inventory.currentItem = crystalSlot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(crystalSlot));
        }

        Vector3d hitVec = new Vector3d(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
        BlockRayTraceResult result = new BlockRayTraceResult(hitVec, Direction.UP, pos, false);

        mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, result));
        mc.player.connection.sendPacket(new net.minecraft.network.play.client.CAnimateHandPacket(Hand.MAIN_HAND));

        if (crystalSlot != previousSlot) {
            mc.player.inventory.currentItem = previousSlot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(previousSlot));
        }
    }

    private void breakCrystal(EnderCrystalEntity crystal) {
        if (rotate.getValue()) {
            Vector3d crystalPos = crystal.getPositionVec().add(0, crystal.getEyeHeight() * 0.5, 0);
            Vector2f rot = calculateRotation(crystalPos);
            currentRotation = rot;
            RotationComponent.update(new Rotation(currentRotation.x, currentRotation.y), 360, 360, 0, 5);
        }

        mc.playerController.attackEntity(mc.player, crystal);
        mc.player.swingArm(Hand.MAIN_HAND);
    }

    private void placeBlock(BlockPos pos, PlaceInfo info, int slot) {
        int previousSlot = mc.player.inventory.currentItem;

        if (slot != previousSlot) {
            mc.player.inventory.currentItem = slot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(slot));
        }

        BlockRayTraceResult result = new BlockRayTraceResult(info.hitVec, info.direction, info.neighbor, false);
        mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, result));
        mc.player.swingArm(Hand.MAIN_HAND);

        if (slot != previousSlot) {
            mc.player.inventory.currentItem = previousSlot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(previousSlot));
        }
    }

    private PlaceInfo getPlaceInfo(BlockPos pos) {
        Direction[] directions = {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        for (Direction direction : directions) {
            BlockPos neighbor = pos.offset(direction);
            Block block = mc.world.getBlockState(neighbor).getBlock();

            if (block != Blocks.AIR && block != Blocks.WATER && block != Blocks.LAVA) {
                Vector3d hitVec = new Vector3d(
                        neighbor.getX() + 0.5 + direction.getOpposite().getXOffset() * 0.5,
                        neighbor.getY() + 0.5 + direction.getOpposite().getYOffset() * 0.5,
                        neighbor.getZ() + 0.5 + direction.getOpposite().getZOffset() * 0.5
                );
                return new PlaceInfo(neighbor, direction.getOpposite(), hitVec);
            }
        }

        return null;
    }

    private int findCrystalSlot() {
        for (int i = 0; i < (onlyHotbar.getValue() ? 9 : 36); i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == Items.END_CRYSTAL) {
                return i;
            }
        }
        return -1;
    }

    private int findObsidianSlot() {
        for (int i = 0; i < (onlyHotbar.getValue() ? 9 : 36); i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof BlockItem blockItem) {
                if (blockItem.getBlock() == Blocks.OBSIDIAN) {
                    return i;
                }
            }
        }
        return -1;
    }

    private Vector2f calculateRotation(Vector3d targetPos) {
        Vector3d playerEyes = mc.player.getEyePosition(1.0F);
        Vector3d toTarget = targetPos.subtract(playerEyes).normalize();

        double horizontalLength = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
        float targetYaw = (float) Math.toDegrees(Math.atan2(toTarget.z, toTarget.x)) - 90.0F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(toTarget.y, horizontalLength));

        targetPitch = MathHelper.clamp(targetPitch, -90.0F, 90.0F);

        return new Vector2f(targetYaw, targetPitch);
    }

    private Vector2f smoothRotate(Vector2f target, Vector2f current, float speed) {
        float yawDiff = MathHelper.wrapDegrees(target.x - current.x);
        float pitchDiff = target.y - current.y;

        float smoothedYaw = current.x + yawDiff * speed;
        float smoothedPitch = current.y + pitchDiff * speed;

        return new Vector2f(smoothedYaw, smoothedPitch);
    }

    private record PlaceInfo(BlockPos neighbor, Direction direction, Vector3d hitVec) {
    }
}
