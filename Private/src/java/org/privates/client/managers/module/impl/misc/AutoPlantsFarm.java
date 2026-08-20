package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
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
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.InvUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoPlantsFarm", category = Category.MISC, description = "Автоматически собирает и сажает нарост")
public class AutoPlantsFarm extends Module {

    public static AutoPlantsFarm getInstance() {
        return Instance.get(AutoPlantsFarm.class);
    }

    private final SliderSetting delay = new SliderSetting(this, "Задержка (мс)", 100, 50, 500, 10);
    private final SliderSetting scanDelay = new SliderSetting(this, "Задержка сканирования (мс)", 1000, 500, 5000, 100);
    private final SliderSetting searchRange = new SliderSetting(this, "Дистанция поиска", 50.0F, 10.0F, 100.0F, 5.0F);
    private final BooleanSetting rotate = new BooleanSetting(this, "Ротация", true);
    private final BooleanSetting autoWalk = new BooleanSetting(this, "Авто подход", true);
    private final SliderSetting walkRange = new SliderSetting(this, "Дистанция подхода", 2.5F, 1.0F, 4.0F, 0.1F).setVisible(autoWalk::getValue);
    private final BooleanSetting collectDrops = new BooleanSetting(this, "Подбирать дроп", true);
    private final SliderSetting dropRange = new SliderSetting(this, "Дистанция дропа", 3.0F, 1.0F, 10.0F, 0.5F).setVisible(collectDrops::getValue);
    private final BooleanSetting hwMode = new BooleanSetting(this, "HW Mode", false);
    private final BooleanSetting outline = new BooleanSetting(this, "Подсветка блока", true);
    private final SliderSetting outlineThickness = new SliderSetting(this, "Толщина линии", 2.0F, 0.5F, 5.0F, 0.1F).setVisible(outline::getValue);
    private final BooleanSetting fill = new BooleanSetting(this, "Заливка", true).setVisible(outline::getValue);
    private final SliderSetting fillAlpha = new SliderSetting(this, "Прозрачность заливки", 0.3F, 0.1F, 1.0F, 0.05F).setVisible(() -> outline.getValue() && fill.getValue());

    private final StopWatch timer = new StopWatch();
    private final StopWatch walkTimer = new StopWatch();
    private final StopWatch scanTimer = new StopWatch();

    private BlockPos targetBlock = null;
    private ItemEntity targetDrop = null;
    private FarmState currentState = FarmState.IDLE;
    private Vector2f currentLerpRotation = new Vector2f(0, 0);

    private final List<BlockPos> cachedMatureWarts = new CopyOnWriteArrayList<>();
    private final List<BlockPos> cachedEmptySoulSand = new CopyOnWriteArrayList<>();
    private volatile boolean isScanning = false;

    private static final List<Item> VALID_TOOLS = List.of(
            Items.DIAMOND_HOE,
            Items.NETHERITE_HOE,
            Items.DIAMOND_AXE,
            Items.NETHERITE_AXE
    );

    private enum FarmState {
        IDLE,
        WALKING,
        ROTATING,
        BREAKING,
        PLANTING,
        COLLECTING_DROP,
        WALKING_TO_DROP
    }

    @Override
    public void onEnable() {
        super.onEnable();
        targetBlock = null;
        targetDrop = null;
        currentState = FarmState.IDLE;
        cachedMatureWarts.clear();
        cachedEmptySoulSand.clear();
        isScanning = false;
        if (mc.player != null) {
            currentLerpRotation = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
        }
        timer.reset();
        walkTimer.reset();
        scanTimer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        targetBlock = null;
        targetDrop = null;
        currentState = FarmState.IDLE;
        cachedMatureWarts.clear();
        cachedEmptySoulSand.clear();
        if (mc.player != null) {
            mc.gameSettings.keyBindForward.setPressed(false);
        }
    }

    private double getSquaredDistance(Vector3d from, double x, double y, double z) {
        double dx = x - from.x;
        double dy = y - from.y;
        double dz = z - from.z;
        return dx * dx + dy * dy + dz * dz;
    }

    private double getSquaredDistance(Vector3d from, Vector3d to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        return dx * dx + dy * dy + dz * dz;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (hwMode.getValue() && !hasValidTool()) {
            return;
        }

        if (scanTimer.finished(scanDelay.getValue().longValue()) && !isScanning) {
            startAsyncScan();
            scanTimer.reset();
        }

        switch (currentState) {
            case IDLE -> handleIdle();
            case WALKING -> handleWalking();
            case ROTATING -> handleRotating();
            case BREAKING -> handleBreaking();
            case PLANTING -> handlePlanting();
            case WALKING_TO_DROP -> handleWalkingToDrop();
            case COLLECTING_DROP -> handleCollectingDrop();
        }
    }

    private void startAsyncScan() {
        isScanning = true;
        Thread scanThread = new Thread(() -> {
            try {
                scanBlocks();
            } finally {
                isScanning = false;
            }
        }, "AutoPlantsFarm-Scanner");
        scanThread.setDaemon(true);
        scanThread.start();
    }

    private void scanBlocks() {
        if (mc.player == null || mc.world == null) return;

        Vector3d playerPos = mc.player.getPositionVec();
        BlockPos playerBlockPos = mc.player.getPosition();
        int rangeInt = searchRange.getValue().intValue();

        List<BlockPos> newMatureWarts = new ArrayList<>();
        List<BlockPos> newEmptySoulSand = new ArrayList<>();

        int minX = playerBlockPos.getX() - rangeInt;
        int maxX = playerBlockPos.getX() + rangeInt;
        int minY = Math.max(0, playerBlockPos.getY() - rangeInt);
        int maxY = Math.min(255, playerBlockPos.getY() + rangeInt);
        int minZ = playerBlockPos.getZ() - rangeInt;
        int maxZ = playerBlockPos.getZ() + rangeInt;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = minY; y <= maxY; y++) {
                    if (mc.world == null) return;

                    BlockPos pos = new BlockPos(x, y, z);

                    try {
                        if (!mc.world.isBlockLoaded(pos)) continue;

                        BlockState state = mc.world.getBlockState(pos);

                        if (state.getBlock() == Blocks.NETHER_WART) {
                            if (state.get(NetherWartBlock.AGE) == 3) {
                                newMatureWarts.add(pos);
                            }
                        } else if (state.getBlock() == Blocks.SOUL_SAND) {
                            BlockPos above = pos.up();
                            if (mc.world.getBlockState(above).isAir()) {
                                newEmptySoulSand.add(pos);
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        newMatureWarts.sort(Comparator.comparingDouble(pos ->
                getSquaredDistance(playerPos, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5)));
        newEmptySoulSand.sort(Comparator.comparingDouble(pos ->
                getSquaredDistance(playerPos, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5)));

        cachedMatureWarts.clear();
        cachedMatureWarts.addAll(newMatureWarts);
        cachedEmptySoulSand.clear();
        cachedEmptySoulSand.addAll(newEmptySoulSand);
    }

    private boolean hasValidTool() {
        Item mainHandItem = mc.player.getHeldItemMainhand().getItem();
        return VALID_TOOLS.contains(mainHandItem);
    }

    private int findValidToolSlot() {
        for (int i = 0; i < 9; i++) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (VALID_TOOLS.contains(item)) {
                return i;
            }
        }
        return -1;
    }

    private void handleIdle() {
        if (!timer.finished(delay.getValue().longValue())) return;

        if (collectDrops.getValue()) {
            targetDrop = findNearestWartDrop();
            if (targetDrop != null) {
                double distance = mc.player.getPositionVec().distanceTo(targetDrop.getPositionVec());
                if (distance > 1.5) {
                    currentState = FarmState.WALKING_TO_DROP;
                    walkTimer.reset();
                    timer.reset();
                    return;
                } else {
                    currentState = FarmState.COLLECTING_DROP;
                    timer.reset();
                    return;
                }
            }
        }

        targetBlock = findMatureWart();

        if (targetBlock == null) {
            targetBlock = findEmptySoulSand();
        }

        if (targetBlock != null) {
            BlockState state = mc.world.getBlockState(targetBlock);
            if (state.getBlock() == Blocks.NETHER_WART && state.get(NetherWartBlock.AGE) == 3) {
                if (autoWalk.getValue() && getDistanceToBlock(targetBlock) > walkRange.getValue()) {
                    currentState = FarmState.WALKING;
                } else {
                    currentState = FarmState.ROTATING;
                }
            } else if (state.getBlock() == Blocks.SOUL_SAND) {
                BlockPos above = targetBlock.up();
                if (mc.world.getBlockState(above).isAir()) {
                    if (autoWalk.getValue() && getDistanceToBlock(targetBlock) > walkRange.getValue()) {
                        currentState = FarmState.WALKING;
                    } else {
                        currentState = FarmState.PLANTING;
                    }
                }
            }
        }
        timer.reset();
    }

    private void handleWalkingToDrop() {
        if (targetDrop == null || !targetDrop.isAlive()) {
            mc.gameSettings.keyBindForward.setPressed(false);
            currentState = FarmState.IDLE;
            targetDrop = null;
            return;
        }

        double distance = mc.player.getPositionVec().distanceTo(targetDrop.getPositionVec());

        if (distance <= 1.5) {
            mc.gameSettings.keyBindForward.setPressed(false);
            currentState = FarmState.COLLECTING_DROP;
            walkTimer.reset();
            return;
        }

        Vector3d dropPos = targetDrop.getPositionVec();
        Vector3d playerPos = mc.player.getPositionVec();
        Vector3d direction = dropPos.subtract(playerPos).normalize();

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        mc.player.rotationYaw = lerpAngle(0.3f, mc.player.rotationYaw, targetYaw);

        mc.gameSettings.keyBindForward.setPressed(true);

        if (walkTimer.finished(10000)) {
            mc.gameSettings.keyBindForward.setPressed(false);
            currentState = FarmState.IDLE;
            targetDrop = null;
            walkTimer.reset();
        }
    }

    private void handleCollectingDrop() {
        if (targetDrop == null || !targetDrop.isAlive()) {
            currentState = FarmState.IDLE;
            targetDrop = null;
            return;
        }

        double distance = mc.player.getPositionVec().distanceTo(targetDrop.getPositionVec());
        if (distance > 1.5) {
            currentState = FarmState.WALKING_TO_DROP;
            walkTimer.reset();
            return;
        }

        if (timer.finished(200)) {
            currentState = FarmState.IDLE;
            targetDrop = null;
            timer.reset();
        }
    }

    private ItemEntity findNearestWartDrop() {
        Vector3d playerPos = mc.player.getPositionVec();

        return mc.world.getEntitiesWithinAABB(ItemEntity.class,
                        mc.player.getBoundingBox().grow(dropRange.getValue()))
                .stream()
                .filter(item -> item.getItem().getItem() == Items.NETHER_WART)
                .filter(ItemEntity::isAlive)
                .min(Comparator.comparingDouble(item -> getSquaredDistance(playerPos, item.getPositionVec())))
                .orElse(null);
    }

    private void handleWalking() {
        if (targetBlock == null) {
            currentState = FarmState.IDLE;
            mc.gameSettings.keyBindForward.setPressed(false);
            return;
        }

        double distance = getDistanceToBlock(targetBlock);

        if (distance <= walkRange.getValue()) {
            mc.gameSettings.keyBindForward.setPressed(false);
            BlockState state = mc.world.getBlockState(targetBlock);
            if (state.getBlock() == Blocks.NETHER_WART && state.get(NetherWartBlock.AGE) == 3) {
                currentState = FarmState.ROTATING;
            } else if (state.getBlock() == Blocks.SOUL_SAND) {
                currentState = FarmState.PLANTING;
            } else {
                currentState = FarmState.IDLE;
            }
            walkTimer.reset();
            return;
        }

        Vector3d blockCenter = new Vector3d(targetBlock.getX() + 0.5, targetBlock.getY(), targetBlock.getZ() + 0.5);
        Vector3d playerPos = mc.player.getPositionVec();
        Vector3d direction = blockCenter.subtract(playerPos).normalize();

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        mc.player.rotationYaw = lerpAngle(0.3f, mc.player.rotationYaw, targetYaw);

        mc.gameSettings.keyBindForward.setPressed(true);

        if (walkTimer.finished(15000)) {
            mc.gameSettings.keyBindForward.setPressed(false);
            currentState = FarmState.IDLE;
            targetBlock = null;
            walkTimer.reset();
        }
    }

    private void handleRotating() {
        if (targetBlock == null) {
            currentState = FarmState.IDLE;
            return;
        }

        if (rotate.getValue()) {
            Vector3d blockCenter = new Vector3d(targetBlock.getX() + 0.5, targetBlock.getY() + 0.5, targetBlock.getZ() + 0.5);
            currentLerpRotation = smoothRotate(blockCenter, currentLerpRotation);
            Rotation rotation = new Rotation(currentLerpRotation.x, currentLerpRotation.y);
            RotationComponent.update(rotation, 180, 90, 0, 5);

            float yawDiff = Math.abs(MathHelper.wrapDegrees(currentLerpRotation.x - getTargetYaw(blockCenter)));
            float pitchDiff = Math.abs(currentLerpRotation.y - getTargetPitch(blockCenter));

            if (yawDiff < 5.0f && pitchDiff < 5.0f) {
                currentState = FarmState.BREAKING;
                timer.reset();
            }
        } else {
            currentState = FarmState.BREAKING;
            timer.reset();
        }
    }

    private void handleBreaking() {
        if (targetBlock == null) {
            currentState = FarmState.IDLE;
            return;
        }

        BlockState state = mc.world.getBlockState(targetBlock);
        if (state.getBlock() != Blocks.NETHER_WART) {
            cachedMatureWarts.remove(targetBlock);
            currentState = FarmState.IDLE;
            targetBlock = null;
            return;
        }

        if (hwMode.getValue()) {
            int toolSlot = findValidToolSlot();
            if (toolSlot != -1 && mc.player.inventory.currentItem != toolSlot) {
                mc.player.connection.sendPacket(new CHeldItemChangePacket(toolSlot));
                mc.player.inventory.currentItem = toolSlot;
            }
        }

        mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, targetBlock, Direction.UP));
        mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, targetBlock, Direction.UP));
        mc.player.swingArm(Hand.MAIN_HAND);

        cachedMatureWarts.remove(targetBlock);

        BlockPos soulSandPos = targetBlock.down();
        if (mc.world.getBlockState(soulSandPos).getBlock() == Blocks.SOUL_SAND) {
            targetBlock = soulSandPos;
            currentState = FarmState.PLANTING;
        } else {
            targetBlock = null;
            currentState = FarmState.IDLE;
        }
        timer.reset();
    }

    private void handlePlanting() {
        if (targetBlock == null) {
            currentState = FarmState.IDLE;
            return;
        }

        if (!timer.finished(delay.getValue().longValue())) return;

        BlockPos plantPos = targetBlock.up();
        if (!mc.world.getBlockState(plantPos).isAir()) {
            cachedEmptySoulSand.remove(targetBlock);
            currentState = FarmState.IDLE;
            targetBlock = null;
            return;
        }

        Slot wartSlot = InvUtil.getSlot(Items.NETHER_WART);
        if (wartSlot == null) {
            int hotbarSlot = InvUtil.getItemInHotBar(Items.NETHER_WART);
            if (hotbarSlot == -1) {
                currentState = FarmState.IDLE;
                targetBlock = null;
                return;
            }
            wartSlot = mc.player.container.inventorySlots.get(hotbarSlot + 36);
        }

        if (rotate.getValue()) {
            Vector3d blockCenter = new Vector3d(targetBlock.getX() + 0.5, targetBlock.getY() + 1.0, targetBlock.getZ() + 0.5);
            currentLerpRotation = smoothRotate(blockCenter, currentLerpRotation);
            Rotation rotation = new Rotation(currentLerpRotation.x, currentLerpRotation.y);
            RotationComponent.update(rotation, 180, 90, 0, 5);
        }

        int slotId = wartSlot.slotNumber;
        int previousSlot = mc.player.inventory.currentItem;

        if (slotId >= 36 && slotId < 45) {
            mc.player.connection.sendPacket(new CHeldItemChangePacket(slotId - 36));
        }

        Vector3d hitVec = new Vector3d(targetBlock.getX() + 0.5, targetBlock.getY() + 1.0, targetBlock.getZ() + 0.5);
        BlockRayTraceResult result = new BlockRayTraceResult(hitVec, Direction.UP, targetBlock, false);

        mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, result));
        mc.player.swingArm(Hand.MAIN_HAND);

        if (slotId >= 36 && slotId < 45) {
            mc.player.connection.sendPacket(new CHeldItemChangePacket(previousSlot));
        }

        cachedEmptySoulSand.remove(targetBlock);
        currentState = FarmState.IDLE;
        targetBlock = null;
        timer.reset();
    }

    private BlockPos findMatureWart() {
        if (cachedMatureWarts.isEmpty()) return null;

        for (BlockPos pos : cachedMatureWarts) {
            try {
                if (!mc.world.isBlockLoaded(pos)) continue;
                BlockState state = mc.world.getBlockState(pos);
                if (state.getBlock() == Blocks.NETHER_WART && state.get(NetherWartBlock.AGE) == 3) {
                    return pos;
                }
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private BlockPos findEmptySoulSand() {
        if (cachedEmptySoulSand.isEmpty()) return null;

        for (BlockPos pos : cachedEmptySoulSand) {
            try {
                if (!mc.world.isBlockLoaded(pos)) continue;
                BlockState state = mc.world.getBlockState(pos);
                if (state.getBlock() == Blocks.SOUL_SAND) {
                    BlockPos above = pos.up();
                    if (mc.world.getBlockState(above).isAir()) {
                        return pos;
                    }
                }
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private double getDistanceToBlock(BlockPos pos) {
        return mc.player.getPositionVec().distanceTo(new Vector3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
    }

    private Vector2f smoothRotate(Vector3d targetPos, Vector2f currentRotation) {
        Vector3d playerEyes = mc.player.getEyePosition(1.0F);
        Vector3d toTarget = targetPos.subtract(playerEyes).normalize();

        double horizontalLength = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
        float targetYaw = (float) Math.toDegrees(Math.atan2(toTarget.z, toTarget.x)) - 90.0F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(toTarget.y, horizontalLength));

        targetPitch = MathHelper.clamp(targetPitch, -90.0F, 90.0F);

        float smoothing = 0.5f;
        float smoothedYaw = lerpAngle(smoothing, currentRotation.x, targetYaw);
        float smoothedPitch = MathHelper.lerp(smoothing, currentRotation.y, targetPitch);

        return new Vector2f(smoothedYaw, smoothedPitch);
    }

    private float getTargetYaw(Vector3d targetPos) {
        Vector3d playerEyes = mc.player.getEyePosition(1.0F);
        Vector3d toTarget = targetPos.subtract(playerEyes).normalize();
        return (float) Math.toDegrees(Math.atan2(toTarget.z, toTarget.x)) - 90.0F;
    }

    private float getTargetPitch(Vector3d targetPos) {
        Vector3d playerEyes = mc.player.getEyePosition(1.0F);
        Vector3d toTarget = targetPos.subtract(playerEyes).normalize();
        double horizontalLength = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
        return MathHelper.clamp((float) -Math.toDegrees(Math.atan2(toTarget.y, horizontalLength)), -90.0F, 90.0F);
    }

    private float lerpAngle(float delta, float start, float end) {
        float diff = MathHelper.wrapDegrees(end - start);
        return start + diff * delta;
    }

    @EventHandler
    public void onRender3D(Render3DLastEvent event) {
        if (!outline.getValue() || mc.world == null) return;

        BlockPos renderPos = null;

        if (targetBlock != null) {
            BlockState state = mc.world.getBlockState(targetBlock);
            if (state.getBlock() == Blocks.SOUL_SAND) {
                renderPos = targetBlock.up();
            } else {
                renderPos = targetBlock;
            }
        }

        if (renderPos == null && targetDrop != null && targetDrop.isAlive()) {
            renderPos = targetDrop.getPosition();
        }

        if (renderPos == null) return;

        MatrixStack matrixStack = event.getMatrix();
        matrixStack.push();
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrixStack.getLast().getMatrix());

        double renderX = mc.getRenderManager().renderPosX();
        double renderY = mc.getRenderManager().renderPosY();
        double renderZ = mc.getRenderManager().renderPosZ();
        RenderSystem.translated(-renderX, -renderY, -renderZ);

        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableCull();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        RenderSystem.lineWidth(outlineThickness.getValue());

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        int baseColor = Theme.getInstance().clientColor();
        int outlineColor = baseColor;
        int fillColor = ColorUtil.replAlpha(baseColor, (int) (fillAlpha.getValue() * 255));

        double minX = renderPos.getX();
        double minY = renderPos.getY();
        double minZ = renderPos.getZ();
        double maxX = renderPos.getX() + 1.0;
        double maxY = renderPos.getY() + 1.0;
        double maxZ = renderPos.getZ() + 1.0;

        if (fill.getValue()) {
            bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            RenderUtil3D.renderFilledBox(bufferBuilder, minX, minY, minZ, maxX, maxY, maxZ, fillColor);
            tessellator.draw();
        }

        bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        RenderUtil3D.renderWireframeBox(bufferBuilder, minX, minY, minZ, maxX, maxY, maxZ, outlineColor);
        tessellator.draw();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
        matrixStack.pop();
    }
}