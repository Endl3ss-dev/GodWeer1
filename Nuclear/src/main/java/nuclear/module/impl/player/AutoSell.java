package nuclear.module.impl.player;

import net.minecraft.item.ItemStack;
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

@ModuleDescriptor(
    name = "AutoSell",
    type = ModuleCategory.Player,
    desc = "Sells configured inventory items with per-item auction prices"
)
public class AutoSell extends Module {
    private final TextSetting listings = new TextSetting("Listings", "totem:5000;elytra:30000;crystal:1500");
    private final SliderSetting delayMs = new SliderSetting("Delay", 600.0f, 100.0f, 5000.0f, 100.0f);
    private final BooleanSetting onlyWithoutScreen = new BooleanSetting("Only without GUI", true);

    private long lastSellTime;

    public AutoSell() {
        addSettings(listings, delayMs, onlyWithoutScreen);
    }

    @Override
    public boolean onEvent(Event event) {
        if (!(event instanceof EventUpdate) || mc.player == null || mc.world == null) {
            return false;
        }

        if (onlyWithoutScreen.get() && mc.currentScreen != null) {
            return false;
        }
        if (System.currentTimeMillis() - lastSellTime < delayMs.getValue().longValue()) {
            return false;
        }

        SaleEntry match = findMatch();
        if (match == null) {
            return false;
        }

        selectSlot(match.slot);
        mc.player.sendChatMessage("/ah sell " + match.price);
        lastSellTime = System.currentTimeMillis();
        return false;
    }

    private SaleEntry findMatch() {
        List<SaleRule> rules = parseRules();
        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }

            String displayName = stack.getDisplayName().getString().toLowerCase(Locale.ROOT);
            String registryName = stack.getItem().getRegistryName() == null
                ? ""
                : stack.getItem().getRegistryName().toString().toLowerCase(Locale.ROOT);

            for (SaleRule rule : rules) {
                if (displayName.contains(rule.name) || registryName.contains(rule.name)) {
                    return new SaleEntry(slot, rule.price);
                }
            }
        }
        return null;
    }

    private void selectSlot(int slot) {
        if (slot < 9) {
            mc.player.inventory.currentItem = slot;
            return;
        }
        mc.playerController.pickItem(slot);
    }

    private List<SaleRule> parseRules() {
        List<SaleRule> rules = new ArrayList<SaleRule>();
        String[] entries = listings.get().toLowerCase(Locale.ROOT).split(";");
        for (String entry : entries) {
            String trimmed = entry.trim();
            if (trimmed.isEmpty() || !trimmed.contains(":")) {
                continue;
            }

            String[] parts = trimmed.split(":", 2);
            try {
                rules.add(new SaleRule(parts[0].trim(), Long.parseLong(parts[1].trim())));
            } catch (NumberFormatException ignored) {
            }
        }
        return rules;
    }

    private static class SaleRule {
        private final String name;
        private final long price;

        private SaleRule(String name, long price) {
            this.name = name;
            this.price = price;
        }
    }

    private static class SaleEntry {
        private final int slot;
        private final long price;

        private SaleEntry(int slot, long price) {
            this.slot = slot;
            this.price = price;
        }
    }
}
