package nuclear.module.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import nuclear.control.events.Event;
import nuclear.control.events.impl.render.EventRender;
import nuclear.module.TypeList;
import nuclear.module.api.Annotation;
import nuclear.module.api.Module;
import nuclear.utils.font.Fonts;
import nuclear.utils.render.ProjectUtil;
import nuclear.utils.render.RenderUtils;
import org.joml.Vector2f;

import static nuclear.utils.render.ColorUtils.rgba;

@Annotation(name = "LootTracker", type = TypeList.Render, desc = "Показывает залутаные спавнера и вагонетки")
public class LootTracker extends Module {

    public LootTracker() {
        addSettings();
    }

    @Override
    public boolean onEvent(Event event) {
        if (!(event instanceof EventRender e) || mc.player == null || mc.world == null) {
            return false;
        }

        if (e.isRender2D()) {
            renderLoot(e.matrixStack);
        }
        return false;
    }

    private void renderLoot(MatrixStack stack) {
        for (TileEntity tileEntity : mc.world.loadedTileEntityList) {
            if (tileEntity instanceof MobSpawnerTileEntity spawner) {
                processLootSource(stack, spawner.getPos(), "Spawner", getSpawnerDelay(spawner));
            }
        }

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof ChestMinecartEntity chestMinecart) {
                processLootSource(stack, chestMinecart.getPosition(), "Minecart", -1);
            }
        }
    }

    private void processLootSource(MatrixStack stack, BlockPos pos, String type, int spawnerDelay) {
        if (type.equals("Spawner") && !hasSingleChestNearby(pos)) return;

        boolean isLooted;

        if (type.equals("Spawner")) {
            isLooted = (spawnerDelay > 0 && spawnerDelay != 20) || isPlayerWasNear(pos);
        } else {
            isLooted = isPlayerWasNear(pos);
        }

        String name = isLooted ? "Залутано" : "Не залутано";
        TextFormatting format = isLooted ? TextFormatting.RED : TextFormatting.GREEN;

        renderTag(stack, pos, format + name);
    }

    private boolean isPlayerWasNear(BlockPos pos) {
        int radius = 20;
        for (int x = -radius; x <= radius; x += 2) {
            for (int y = -radius; y <= radius; y += 2) {
                for (int z = -radius; z <= radius; z += 2) {
                    BlockPos checkPos = pos.add(x, y, z);
                    BlockState state = mc.world.getBlockState(checkPos);

                    if (state.getBlock() == Blocks.AIR) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int getSpawnerDelay(MobSpawnerTileEntity spawner) {
        try {
            CompoundNBT tag = new CompoundNBT();
            spawner.getSpawnerBaseLogic().write(tag);
            return tag.contains("Delay") ? tag.getShort("Delay") : 20;
        } catch (Exception e) {
            return 20;
        }
    }

    private void renderTag(MatrixStack stack, BlockPos blockPos, String text) {
        double x = blockPos.getX() + 0.5;
        double y = blockPos.getY() + 1.5;
        double z = blockPos.getZ() + 0.5;

        Vector2f vec = ProjectUtil.project2D(x, y, z);
        if (vec.x == Float.MAX_VALUE || vec.y == Float.MAX_VALUE) return;

        float width = Fonts.newcode[13].getWidth(text);
        float height = Fonts.newcode[13].getFontHeight();

        RenderUtils.Render2D.drawRoundedRect(vec.x - (width / 2) - 2, vec.y - 2.5f, width + 4, height + 2, 1f, rgba(11, 11, 11, 165));
        Fonts.newcode[13].drawCenteredString(stack, text, vec.x, vec.y, -1);
    }

    private boolean hasSingleChestNearby(BlockPos spawnerPos) {
        int radius = 3;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = spawnerPos.add(x, y, z);
                    BlockState state = mc.world.getBlockState(checkPos);
                    if (state.getBlock() instanceof ChestBlock) {
                        if (state.get(ChestBlock.TYPE) == ChestType.SINGLE) return true;
                    }
                }
            }
        }
        return false;
    }
}
