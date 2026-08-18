package nuclear.module.impl.other;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import nuclear.control.events.Event;
import nuclear.control.events.impl.player.EventUpdate;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.BooleanSetting;
import nuclear.module.settings.imp.SliderSetting;
import nuclear.module.settings.imp.TextSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ModuleDescriptor(
    name = "AutoBuy",
    type = ModuleCategory.Other,
    desc = "Buys configured auction items from chest-based auction screens"
)
public class AutoBuy extends Module {
    private static final Pattern NUMBER_RE = Pattern.compile("(\\d[\\d\\s_]*)");

    private final TextSetting wantedItems = new TextSetting("Wanted", "totem,elytra,crystal");
    private final SliderSetting maxPrice = new SliderSetting("Max price", 100000.0f, 100.0f, 10000000.0f, 100.0f);
    private final SliderSetting actionDelay = new SliderSetting("Delay", 150.0f, 50.0f, 1500.0f, 50.0f);
    private final BooleanSetting autoConfirm = new BooleanSetting("Auto confirm", true);

    private long lastActionTime;

    public AutoBuy() {
        addSettings(wantedItems, maxPrice, actionDelay, autoConfirm);
    }

    @Override
    public boolean onEvent(Event event) {
        if (!(event instanceof EventUpdate) || mc.player == null || mc.currentScreen == null) {
            return false;
        }

        if (!(mc.player.openContainer instanceof ChestContainer)) {
            return false;
        }

        ChestContainer chest = (ChestContainer) mc.player.openContainer;
        String title = mc.currentScreen.getTitle().getString().toLowerCase(Locale.ROOT);

        if (isConfirmationScreen(title)) {
            confirmPurchase(chest);
        } else if (isAuctionScreen(title)) {
            scanAuction(chest);
        }
        return false;
    }

    private boolean isAuctionScreen(String title) {
        return title.contains("аук")
            || title.contains("auction")
            || title.contains("рынок");
    }

    private boolean isConfirmationScreen(String title) {
        return autoConfirm.get() && (
            title.contains("подтверж")
                || title.contains("confirm")
                || title.contains("покупк")
        );
    }

    private void scanAuction(ChestContainer chest) {
        if (!canAct()) {
            return;
        }

        int chestSize = chest.getLowerChestInventory().getSizeInventory();
        for (int slot = 0; slot < chestSize; slot++) {
            ItemStack stack = chest.getLowerChestInventory().getStackInSlot(slot);
            if (stack.isEmpty() || !matchesWanted(stack)) {
                continue;
            }

            long price = extractPrice(stack);
            if (price <= 0L || price > maxPrice.getValue().longValue()) {
                continue;
            }

            mc.playerController.windowClick(chest.windowId, slot, 0, ClickType.PICKUP, mc.player);
            lastActionTime = System.currentTimeMillis();
            break;
        }
    }

    private void confirmPurchase(ChestContainer chest) {
        if (!canAct()) {
            return;
        }

        int chestSize = chest.getLowerChestInventory().getSizeInventory();
        for (int slot = 0; slot < chestSize; slot++) {
            ItemStack stack = chest.getLowerChestInventory().getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }

            String name = stack.getDisplayName().getString().toLowerCase(Locale.ROOT);
            if (name.contains("подтверд") || name.contains("confirm") || name.contains("купить")) {
                mc.playerController.windowClick(chest.windowId, slot, 0, ClickType.PICKUP, mc.player);
                lastActionTime = System.currentTimeMillis();
                return;
            }
        }

        if (chestSize > 11) {
            mc.playerController.windowClick(chest.windowId, 11, 0, ClickType.PICKUP, mc.player);
            lastActionTime = System.currentTimeMillis();
        }
    }

    private boolean matchesWanted(ItemStack stack) {
        String normalizedName = stack.getDisplayName().getString().toLowerCase(Locale.ROOT);
        String registryName = stack.getItem().getRegistryName() == null
            ? ""
            : stack.getItem().getRegistryName().toString().toLowerCase(Locale.ROOT);

        for (String token : splitWantedItems()) {
            if (normalizedName.contains(token) || registryName.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private List<String> splitWantedItems() {
        String[] raw = wantedItems.get().toLowerCase(Locale.ROOT).split("[,;]");
        List<String> tokens = new ArrayList<String>();
        for (String value : raw) {
            String trimmed = value.trim();
            if (!trimmed.isEmpty()) {
                tokens.add(trimmed);
            }
        }
        return tokens;
    }

    private long extractPrice(ItemStack stack) {
        List<ITextComponent> tooltip = stack.getTooltip(mc.player, ITooltipFlag.TooltipFlags.NORMAL);
        for (ITextComponent line : tooltip) {
            String text = line.getString().replace("§", "").toLowerCase(Locale.ROOT);
            if (!(text.contains("$") || text.contains("цена") || text.contains("price") || text.contains("монет"))) {
                continue;
            }

            Matcher matcher = NUMBER_RE.matcher(text);
            if (matcher.find()) {
                String digits = matcher.group(1).replace(" ", "").replace("_", "");
                try {
                    return Long.parseLong(digits);
                } catch (NumberFormatException ignored) {
                    return -1L;
                }
            }
        }
        return -1L;
    }

    private boolean canAct() {
        return System.currentTimeMillis() - lastActionTime >= actionDelay.getValue().longValue();
    }
}
