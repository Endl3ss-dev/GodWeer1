package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.play.server.SMultiBlockChangePacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.events.world.WorldLoadEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;

import java.awt.*;
import java.util.*;
import java.util.List;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "XRay", category = Category.RENDER, description = "Подсвечивает выбранные блоки")
public class XRay extends Module {
    final ModeSetting mode = new ModeSetting(this, "Режим", "Обычный", "Funtime-Spooky")
            .onAction(this::resetState);
    final MultiItemSetting selectedBlocks = new MultiItemSetting(this, "Блоки", new ArrayList<>())
            .setVisible(() -> mode.is("Обычный"))
            .onAction(this::restartNormalScan);
    final SliderSetting scanRange = new SliderSetting(this, "Дальность скана", 16.0F, 4.0F, 48.0F, 1.0F)
            .setVisible(() -> mode.is("Обычный"))
            .onAction(this::restartNormalScan);
    final SliderSetting scanSpeed = new SliderSetting(this, "Блоков за тик", 384.0F, 64.0F, 4096.0F, 64.0F)
            .setVisible(() -> mode.is("Обычный"));
    final ColorSetting color = new ColorSetting(this, "Цвет подсветки", new Color(195, 162, 120, 255).getRGB());
    final BooleanSetting fill = new BooleanSetting(this, "Заливка", true);
    final SliderSetting fillAlpha = new SliderSetting(this, "Прозрачность заливки", 0.18F, 0.02F, 1.0F, 0.02F)
            .setVisible(fill::getValue);
    final SliderSetting outlineAlpha = new SliderSetting(this, "Прозрачность контура", 0.9F, 0.05F, 1.0F, 0.05F);
    final SliderSetting lineWidth = new SliderSetting(this, "Толщина линии", 1.5F, 0.5F, 4.0F, 0.1F);

    final List<BlockPos> spookyBlocks = new ArrayList<>();
    final Map<BlockPos, Integer> scannedBlocks = new HashMap<>();

    BlockPos scanOrigin = BlockPos.ZERO;
    int scanRadius;
    int scanMinY;
    int scanMaxY;
    int scanSide;
    int scanLayerSize;
    int scanTotal;
    int scanIndex;
    int scanGeneration;
    int nextRescanTick;
    boolean scanning;

    @Override
    protected void onEnable() {
        resetState();
    }

    @Override
    protected void onDisable() {
        resetState();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        resetState();
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (!mode.is("Funtime-Spooky")) {
            return;
        }
        if (event.getPacket() instanceof SMultiBlockChangePacket packet) {
            packet.func_244310_a((blockPos, blockState) -> {
                if (blockState.getBlock() == Blocks.ANCIENT_DEBRIS && !spookyBlocks.contains(blockPos)) {
                    spookyBlocks.add(blockPos.toImmutable());
                }
            });
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (!mode.is("Обычный") || mc.player == null || mc.world == null) {
            return;
        }
        if (selectedBlocks.getValue().isEmpty()) {
            if (!scannedBlocks.isEmpty()) {
                scannedBlocks.clear();
            }
            scanning = false;
            scanIndex = 0;
            return;
        }

        BlockPos playerPos = mc.player.getPosition();
        int newRange = scanRange.getValue().intValue();
        if (shouldRestartScan(playerPos, newRange)) {
            startScan(playerPos, newRange);
        }

        int blocksPerTick = scanSpeed.getValue().intValue();
        int processed = 0;
        while (scanning && processed < blocksPerTick && scanIndex < scanTotal) {
            scanNextBlock();
            processed++;
        }

        if (scanning && scanIndex >= scanTotal) {
            finishScan();
        }
    }

    @EventHandler
    public void onRender3D(Render3DLastEvent event) {
        if (mc.world == null) {
            return;
        }

        if (mode.is("Funtime-Spooky")) {
            spookyBlocks.removeIf(pos -> mc.world == null || mc.world.getBlockState(pos).getBlock() != Blocks.ANCIENT_DEBRIS);
            for (BlockPos pos : spookyBlocks) {
                renderBox(event, pos);
            }
            return;
        }

        Iterator<BlockPos> iterator = scannedBlocks.keySet().iterator();
        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            if (!matchesSelected(mc.world.getBlockState(pos))) {
                iterator.remove();
                continue;
            }
            renderBox(event, pos);
        }
    }

    private boolean shouldRestartScan(BlockPos playerPos, int newRange) {
        if (!scanning && scanIndex < scanTotal) {
            return false;
        }
        if (!scanning && scanIndex >= scanTotal && mc.player.ticksExisted < nextRescanTick) {
            return false;
        }
        if (scanTotal == 0) {
            return true;
        }
        if (newRange != scanRadius) {
            return true;
        }
        return playerPos.distanceSq(scanOrigin) > 16.0D;
    }

    private void startScan(BlockPos origin, int range) {
        scanOrigin = origin.toImmutable();
        scanRadius = range;
        scanMinY = Math.max(0, origin.getY() - range);
        scanMaxY = Math.min(mc.world.getHeight() - 1, origin.getY() + range);
        scanSide = range * 2 + 1;
        scanLayerSize = scanSide * scanSide;
        scanTotal = scanLayerSize * (scanMaxY - scanMinY + 1);
        scanIndex = 0;
        scanGeneration++;
        scanning = true;
    }

    private void scanNextBlock() {
        int yOffset = scanIndex / scanLayerSize;
        int horizontalIndex = scanIndex % scanLayerSize;
        int zOffset = horizontalIndex / scanSide;
        int xOffset = horizontalIndex % scanSide;
        int worldX = scanOrigin.getX() - scanRadius + xOffset;
        int worldY = scanMinY + yOffset;
        int worldZ = scanOrigin.getZ() - scanRadius + zOffset;
        scanIndex++;

        BlockPos pos = new BlockPos(worldX, worldY, worldZ);
        if (!mc.world.isBlockPresent(pos)) {
            return;
        }

        BlockState state = mc.world.getBlockState(pos);
        if (matchesSelected(state)) {
            scannedBlocks.put(pos.toImmutable(), scanGeneration);
        }
    }

    private void finishScan() {
        scannedBlocks.entrySet().removeIf(entry -> entry.getValue() != scanGeneration);
        scanning = false;
        nextRescanTick = mc.player.ticksExisted + 20;
    }

    private boolean matchesSelected(BlockState state) {
        return !state.isAir() && selectedBlocks.contains(state.getBlock().asItem());
    }

    private void renderBox(Render3DLastEvent event, BlockPos pos) {
        AxisAlignedBB box = new AxisAlignedBB(pos);
        int baseColor = color.getValue();
        int fillColor = ColorUtil.setAlpha(baseColor, Math.round(((baseColor >> 24) & 255) * fillAlpha.getValue()));
        int outlineColor = ColorUtil.setAlpha(baseColor, Math.round(((baseColor >> 24) & 255) * outlineAlpha.getValue()));

        if (fill.getValue()) {
            RenderUtil3D.drawFilledBoundingBox(event.getMatrix(), box, fillColor, false, false);
        }
        RenderUtil3D.drawBoundingBox(event.getMatrix(), box, outlineColor, lineWidth.getValue(), false, false);
    }

    private void restartNormalScan() {
        if (!mode.is("Обычный")) {
            return;
        }
        scannedBlocks.clear();
        scanIndex = scanTotal;
        scanning = false;
        nextRescanTick = 0;
    }

    private void resetState() {
        spookyBlocks.clear();
        scannedBlocks.clear();
        scanOrigin = BlockPos.ZERO;
        scanRadius = 0;
        scanMinY = 0;
        scanMaxY = 0;
        scanSide = 0;
        scanLayerSize = 0;
        scanTotal = 0;
        scanIndex = 0;
        scanGeneration = 0;
        nextRescanTick = 0;
        scanning = false;
    }
}
