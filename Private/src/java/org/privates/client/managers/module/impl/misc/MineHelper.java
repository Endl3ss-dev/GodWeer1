package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.GameUpdateEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.draw.RenderUtil3D;
import org.privates.lib.util.time.StopWatch;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "MineHelper", category = Category.MISC, description = "Подсвечивает руды")
public class MineHelper extends Module {

    public static MineHelper getInstance() {
        return Instance.get(MineHelper.class);
    }

    private static final int SEARCH_RADIUS = 70;

    private final MultiBooleanSetting ores = new MultiBooleanSetting(this, "Типы руд",
            BooleanSetting.of("Уголь", true),
            BooleanSetting.of("Лазурит", true),
            BooleanSetting.of("Редстоун", true),
            BooleanSetting.of("Железо", true),
            BooleanSetting.of("Золото", true),
            BooleanSetting.of("Алмазы", true),
            BooleanSetting.of("Изумруды", true),
            BooleanSetting.of("Незерит", true)
    );

    private final BooleanSetting savePickaxe = new BooleanSetting(this, "Сохранять кирку", false);
    private final SliderSetting pickaxeDurability = new SliderSetting(this, "Прочность кирки", 100F, 1F, 1561F, 1F).setVisible(savePickaxe::getValue);
    private final BooleanSetting discardTrash = new BooleanSetting(this, "Выбрасывать мусор", false);

    private final Map<Block, Color> oreColors = new HashMap<>();
    private final StopWatch trashDropTimer = new StopWatch();
    private BlockPos lastPlayerPos = BlockPos.ZERO;
    private final Set<BlockPos> visiblePositions = new HashSet<>();

    public MineHelper() {
        initOreColors();
    }

    private void initOreColors() {
        oreColors.put(Blocks.COAL_ORE, new Color(0xFF505050, true));
        oreColors.put(Blocks.LAPIS_ORE, new Color(0xFF0000AA, true));
        oreColors.put(Blocks.REDSTONE_ORE, new Color(0xFFAA0000, true));
        oreColors.put(Blocks.IRON_ORE, new Color(0xFFC0C0C0, true));
        oreColors.put(Blocks.GOLD_ORE, new Color(0xFFFFAA00, true));
        oreColors.put(Blocks.DIAMOND_ORE, new Color(0xFF00FFFF, true));
        oreColors.put(Blocks.SUGAR_CANE, new Color(0xFF00AA00, true));
        oreColors.put(Blocks.ANCIENT_DEBRIS, new Color(0xFF4C3E36, true));
    }


    @EventHandler
    public void onGameUpdate(GameUpdateEvent e) {
        if (!isEnabled()) return;
        if (mc.player == null || mc.world == null) return;

        if (savePickaxe.getValue()) maybeSavePickaxe();

        if (discardTrash.getValue() && trashDropTimer.finished(2000)) {
            dropTrashItems();
            trashDropTimer.reset();
        }
    }

    @EventHandler
    public void onRender3D(Render3DLastEvent e) {
        if (!isEnabled()) return;
        if (mc.player == null || mc.world == null) return;

        BlockPos playerPos = mc.player.getPosition();
        if (playerPos.distanceSq(lastPlayerPos) > 4) {
            visiblePositions.clear();
            Set<Block> selectedBlocks = selectedBlocks();
            for (int x = playerPos.getX() - SEARCH_RADIUS; x <= playerPos.getX() + SEARCH_RADIUS; x++) {
                for (int y = playerPos.getY() - SEARCH_RADIUS; y <= playerPos.getY() + SEARCH_RADIUS; y++) {
                    for (int z = playerPos.getZ() - SEARCH_RADIUS; z <= playerPos.getZ() + SEARCH_RADIUS; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (playerPos.distanceSq(pos) <= SEARCH_RADIUS * SEARCH_RADIUS) {
                            Block block = mc.world.getBlockState(pos).getBlock();
                            if (selectedBlocks.contains(block)) visiblePositions.add(pos);
                        }
                    }
                }
            }
            lastPlayerPos = playerPos;
        }

        for (BlockPos pos : visiblePositions) {
            Block block = mc.world.getBlockState(pos).getBlock();
            Color color = oreColors.getOrDefault(block, Color.WHITE);
            RenderUtil3D.drawBoundingBox(e.getMatrix(), new AxisAlignedBB(pos), color.getRGB(), 1.5f, false, false);
        }
    }


    private Set<Block> selectedBlocks() {
        Set<Block> set = new HashSet<>();
        if (ores.getValue("Уголь")) set.add(Blocks.SUGAR_CANE);
        return set;
    }

    private void dropTrashItems() {
        if (mc.player == null || mc.player.openContainer == null || mc.playerController == null) return;
        Set<Block> trashBlocks = Set.of(
                Blocks.STONE, Blocks.COBBLESTONE, Blocks.DIRT, Blocks.COARSE_DIRT,
                Blocks.GRANITE, Blocks.ANDESITE, Blocks.DIORITE
        );
        for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            Block block = Block.getBlockFromItem(stack.getItem());
            if (trashBlocks.contains(block)) {
                int slotId = i + (i < 9 ? 36 : 0);
                mc.playerController.windowClick(mc.player.openContainer.windowId, slotId, 1, ClickType.THROW, mc.player);
            }
        }
    }

    private void maybeSavePickaxe() {
        ItemStack main = mc.player.getHeldItemMainhand();
        if (main.isEmpty()) return;
        if (!main.getItem().canHarvestBlock(Blocks.STONE.getDefaultState())) return;
        int dur = main.getMaxDamage() - main.getDamage();
        int minAllowed = pickaxeDurability.getValue().intValue();
        if (dur >= minAllowed) return;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty() || !stack.getItem().canHarvestBlock(Blocks.STONE.getDefaultState())) {
                mc.player.inventory.currentItem = i;
                break;
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        trashDropTimer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
