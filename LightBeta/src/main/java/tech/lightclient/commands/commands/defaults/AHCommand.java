package tech.lightclient.commands.commands.defaults;

import tech.lightclient.LightClient;
import tech.lightclient.autobuy.AutoBuyConfig;
import tech.lightclient.autobuy.AutoBuyEngine;
import tech.lightclient.autobuy.ItemEntry;
import tech.lightclient.autobuy.ItemMatcher;
import tech.lightclient.autobuy.Lot;
import tech.lightclient.autobuy.PriceParser;
import tech.lightclient.client.events.impl.client.TickEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.commands.Command;
import tech.lightclient.commands.argument.IArgConsumer;
import tech.lightclient.commands.exception.CommandException;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.client.resource.language.I18n;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

/** Port of Delta's .ah command, adapted to LightBeta's command/event API. */
public final class AHCommand extends Command {
    private static final long CACHE_TTL_MS = 15_000L;

    private final Map<String, CachedPrice> priceCache = new HashMap<>();
    private PendingSell pendingSell;

    public AHCommand() {
        super("ah");
        LightClient.getEventBus().subscribe(this);
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        if (mc.player == null) {
            return;
        }

        if (!args.hasAny()) {
            ItemStack stack = mc.player.getMainHandStack();
            if (stack.isEmpty()) {
                logDirect("Hold an item first.");
                return;
            }
            sendSearch(stack);
            return;
        }

        String action = args.getString().toLowerCase(Locale.ROOT);
        if (!action.equals("sell")) {
            logDirect("Usage: .ah | .ah sell [discount_percent]");
            return;
        }

        float discount = 0.0f;
        if (args.hasAny()) {
            try {
                discount = Float.parseFloat(args.getString());
            } catch (NumberFormatException e) {
                logDirect("Discount must be a number from 0 to 99.");
                return;
            }
        }
        if (args.hasAny()) {
            args.requireMax(0);
        }
        if (discount < 0.0f || discount > 99.0f) {
            logDirect("Discount must be a number from 0 to 99.");
            return;
        }

        ItemStack stack = mc.player.getMainHandStack();
        if (stack.isEmpty()) {
            logDirect("Hold an item first.");
            return;
        }

        ItemEntry entry = findEntry(stack);
        String itemId = Registries.ITEM.getId(stack.getItem()).toString();
        String key = entry != null ? entryKey(entry) : itemId;
        CachedPrice cached = priceCache.get(key);
        if (cached != null && System.currentTimeMillis() - cached.timeMs < CACHE_TTL_MS) {
            sell(entry, stack.getCount(), discount, cached.price);
            return;
        }

        pendingSell = new PendingSell(entry, itemId, stack.getName().getString(), stack.getCount(), discount, key);
        sendSearch(stack);
        logDirect("Searching AH prices...");
    }

    @EventHandler
    public void onTick(TickEvent event) {
        PendingSell request = pendingSell;
        if (request == null || mc.player == null || !(mc.currentScreen instanceof GenericContainerScreen screen)) {
            return;
        }
        if (!AutoBuyEngine.isAuctionScreen(screen)) {
            return;
        }

        GenericContainerScreenHandler handler = screen.getScreenHandler();
        List<Integer> prices = new ArrayList<>();
        int lotSlots = Math.max(0, (handler.getRows() - 1) * 9);
        for (int slot = 0; slot < Math.min(lotSlots, handler.slots.size()); slot++) {
            ItemStack stack = handler.getSlot(slot).getStack();
            Lot lot = PriceParser.parse(slot, stack);
            if (lot == null || lot.barrier || lot.unitPrice <= 0 || !matches(request, lot)) {
                continue;
            }
            prices.add((int) Math.min(Integer.MAX_VALUE, lot.unitPrice));
        }

        if (prices.isEmpty()) {
            return;
        }

        prices.sort(Comparator.naturalOrder());
        int reference = prices.get(Math.min(2, prices.size() - 1));
        int cheapest = prices.stream()
                .filter(price -> price >= reference * 0.75d)
                .findFirst()
                .orElse(reference);

        priceCache.put(request.key, new CachedPrice(cheapest, System.currentTimeMillis()));
        pendingSell = null;
        mc.player.closeHandledScreen();
        sell(request.entry, request.count, request.discount, cheapest);
    }

    private void sendSearch(ItemStack stack) {
        ItemEntry entry = findEntry(stack);
        String query = entry != null ? entry.query() : searchName(stack);
        mc.player.networkHandler.sendChatCommand("ah search " + query.replaceAll("\\s+", " ").trim());
    }

    private void sell(ItemEntry entry, int count, float discount, int unitPrice) {
        long total = Math.max(1L, Math.round(unitPrice * (1.0d - discount / 100.0d) * Math.max(1, count)));
        mc.player.networkHandler.sendChatCommand("ah sell " + total);
        logDirect("Selling " + (entry == null ? mc.player.getMainHandStack().getName().getString() : entry.label()) + " for " + total + ".");
    }

    private boolean matches(PendingSell request, Lot lot) {
        if (request.entry != null) {
            return ItemMatcher.matches(request.entry, lot);
        }
        return request.itemId.equals(lot.itemId);
    }

    private String searchName(ItemStack stack) {
        String name = stack.getName().getString()
                .replaceAll("§.", "")
                .replaceAll("\\[\\d+x\\d+\\]", "")
                .replace("⚡", "")
                .replace("xxx", "")
                .replace("⭐", "")
                .replace("[", "")
                .replace("]", "")
                .trim()
                .replaceAll("\\s+", " ");
        if (!name.isEmpty()) {
            return name;
        }
        String translated = I18n.translate(stack.getItem().getTranslationKey());
        return translated.equals(stack.getItem().getTranslationKey())
                ? Registries.ITEM.getId(stack.getItem()).getPath()
                : translated;
    }

    private ItemEntry findEntry(ItemStack stack) {
        Lot lot = PriceParser.parseForMatch(stack);
        if (lot == null) return null;
        return AutoBuyConfig.cfg().entries.stream()
                .filter(entry -> entry != null && ItemMatcher.matches(entry, lot))
                .findFirst()
                .orElse(null);
    }

    private String entryKey(ItemEntry entry) {
        return String.valueOf(entry.itemId) + "|" + String.valueOf(entry.displayName) + "|" + String.valueOf(entry.loreKeyword);
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (!args.hasAny()) return Stream.of("sell");
        if (args.hasExactlyOne() && args.peekString().equalsIgnoreCase("sell")) return Stream.of("0", "5", "10");
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Search AH and sell the held item at the market price";
    }

    @Override
    public List<String> getLongDesc() {
        return List.of(
                ".ah - search the held item",
                ".ah sell - sell the held item at the third cheapest price",
                ".ah sell <percent> - sell with a percentage discount"
        );
    }

    private record PendingSell(ItemEntry entry, String itemId, String displayName, int count, float discount, String key) {}
    private record CachedPrice(int price, long timeMs) {}
}
