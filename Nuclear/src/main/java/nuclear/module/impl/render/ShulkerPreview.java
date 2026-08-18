package nuclear.module.impl.render;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import nuclear.control.events.Event;
import nuclear.control.events.impl.player.EventRenderTooltip;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.BooleanSetting;

import java.util.ArrayList;
import java.util.List;

@ModuleDescriptor(name = "ShulkerPreview", type = ModuleCategory.Render, desc = "Отоброжает содержимое шалкера")
public class ShulkerPreview extends Module {
    private final BooleanSetting compactView = new BooleanSetting("Компактный вид", true);

    public ShulkerPreview() {
        addSettings(compactView);
    }

    @Override
    public boolean onEvent(Event event) {
        if (!(event instanceof EventRenderTooltip tooltip) || mc.currentScreen == null) {
            return false;
        }
        if (!isShulkerBox(tooltip.stack)) {
            return false;
        }

        List<ITextComponent> preview = buildPreview(tooltip.stack);
        if (preview.isEmpty()) {
            return false;
        }

        tooltip.setCancel(true);
        List<ITextComponent> tooltipLines = new ArrayList<>(mc.currentScreen.getTooltipFromItem(tooltip.stack));
        tooltipLines.add(StringTextComponent.EMPTY);
        tooltipLines.addAll(preview);
        mc.currentScreen.func_243308_b(tooltip.matrixStack, tooltipLines, tooltip.mouseX, tooltip.mouseY);
        return false;
    }

    private boolean isShulkerBox(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
    }

    private List<ITextComponent> buildPreview(ItemStack stack) {
        CompoundNBT blockEntityTag = stack.getChildTag("BlockEntityTag");
        if (blockEntityTag == null || !blockEntityTag.contains("Items", 9)) {
            return List.of(new StringTextComponent("Пустой шалкер").mergeStyle(TextFormatting.DARK_GRAY));
        }

        ListNBT items = blockEntityTag.getList("Items", 10);
        List<ITextComponent> result = new ArrayList<>();
        int shown = 0;

        for (int i = 0; i < items.size(); i++) {
            ItemStack nestedStack = ItemStack.read(items.getCompound(i));
            if (nestedStack.isEmpty()) {
                continue;
            }

            result.add(new StringTextComponent(nestedStack.getCount() + "x " + nestedStack.getDisplayName().getString()).mergeStyle(TextFormatting.GRAY));
            shown++;

            if (compactView.get() && shown >= 9 && i + 1 < items.size()) {
                result.add(new StringTextComponent("...").mergeStyle(TextFormatting.DARK_GRAY));
                break;
            }
        }

        if (result.isEmpty()) {
            result.add(new StringTextComponent("Пустой шалкер").mergeStyle(TextFormatting.DARK_GRAY));
        }
        return result;
    }
}
