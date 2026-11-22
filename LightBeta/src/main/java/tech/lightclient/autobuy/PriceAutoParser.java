package tech.lightclient.autobuy;

import tech.lightclient.autobuy.QClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Авто-парсер цен: прогоняет {@code /ah search} по включённым предметам,
 * находит минимальную рыночную цену за единицу и выставляет лимит
 * = цена * {@link AutoBuyConfig#parsePercent}% (порт класса {@code a.p} из DimalaBuy).
 *
 * Перед прогоном открывает аукцион и кликает по воронке «Сортировка»
 * {@link #PRE_SORT_CLICKS} раз (интервал 0.5с), чтобы выставить нужный порядок выдачи;
 * после прогона снова открывает аукцион и кликает по ней {@link #POST_SORT_CLICKS} раз,
 * оставляя аукцион открытым — дальше движок продолжает автобай.
 */
public class PriceAutoParser implements QClient {
    private enum State {
        IDLE, PRE_OPEN, PRE_WAIT, PRE_SORT,
        NEXT, WAIT_SCREEN, SCAN, WAIT_PAGE, ADVANCE,
        POST_OPEN, POST_WAIT, POST_SORT
    }

    private static final int MAX_PAGES = 25;
    /** Название воронки сортировки (в нижнем регистре) и её предмет. */
    private static final String SORT_KEYWORD = "сортировка";
    private static final String SORT_ITEM = "minecraft:hopper";
    private static final int SORT_CLICK_DELAY = 10; // 0.5 c при 20 TPS
    private static final int PRE_SORT_CLICKS = 4;
    private static final int POST_SORT_CLICKS = 2;

    private State state = State.IDLE;
    private List<ItemEntry> queue = new ArrayList<>();
    private int index = 0;
    private int delay = 0;
    private long bestPrice = -1L;
    private int page = 0;
    private long pageHash = 0L;
    private int counter = 0;
    private int sortClicksLeft = 0;
    private String label = "";

    public synchronized void start() {
        if (state != State.IDLE) {
            stop();
            return;
        }
        if (mc.player == null) {
            return;
        }

        // The parser can be started independently from the main AutoBuy
        // toggle (from the auction button). Make sure minimizing or focusing
        // another window does not pause the client while it is scanning.
        mc.options.pauseOnLostFocus = false;

        queue = new ArrayList<>();
        for (ItemEntry e : AutoBuyConfig.cfg().entries) {
            if (e.enabled) {
                queue.add(e);
            }
        }
        if (queue.isEmpty()) {
            notify("§e[AutoBuy] Нет включённых предметов для парсинга");
            return;
        }
        index = 0;
        delay = 0;
        counter = 0;
        sortClicksLeft = 0;
        label = "сортировка";
        state = State.PRE_OPEN;
        AutoBuyConfig.cfg().enabled = false;
        notify("§e[AutoBuy] Парсинг цен: " + queue.size() + " предмет(ов)");
    }

    public synchronized void stop() {
        state = State.IDLE;
        queue.clear();
        index = 0;
        label = "";
    }

    public boolean isActive() {
        return state != State.IDLE;
    }

    public String statusLine() {
        if (state == State.IDLE) {
            return "Парсер цен: ожидание";
        }
        int shown = Math.min(index + 1, Math.max(1, queue.size()));
        return "Парсер: " + shown + "/" + queue.size() + " — " + label;
    }

    public synchronized void tick() {
        if (state == State.IDLE) {
            return;
        }
        if (mc.player == null) {
            stop();
            return;
        }
        if (delay > 0) {
            delay--;
            return;
        }
        switch (state) {
            case PRE_OPEN -> preOpen();
            case PRE_WAIT -> preWait();
            case PRE_SORT -> preSort();
            case NEXT -> next();
            case WAIT_SCREEN -> waitScreen();
            case SCAN -> scan();
            case WAIT_PAGE -> waitPage();
            case ADVANCE -> advance();
            case POST_OPEN -> postOpen();
            case POST_WAIT -> postWait();
            case POST_SORT -> postSort();
            default -> {
            }
        }
    }

    // ===================== сортировка до/после парса =====================

    private void preOpen() {
        if (auctionOpen()) {
            sortClicksLeft = PRE_SORT_CLICKS;
            state = State.PRE_SORT;
            delay = 6;
        } else if (mc.currentScreen != null) {
            mc.player.closeHandledScreen();
            delay = 6;
        } else {
            mc.player.networkHandler.sendChatCommand("ah");
            state = State.PRE_WAIT;
            counter = 60;
        }
    }

    private void preWait() {
        if (auctionOpen()) {
            sortClicksLeft = PRE_SORT_CLICKS;
            state = State.PRE_SORT;
            delay = 6;
        } else if (--counter <= 0) {
            // аукцион не открылся — парсим без пере-сортировки, чтобы не зависнуть
            state = State.NEXT;
            delay = 8;
        }
    }

    private void preSort() {
        if (!auctionOpen() || sortClicksLeft <= 0) {
            state = State.NEXT;
            delay = 8;
            return;
        }
        int slot = findSortButton(currentHandler());
        if (slot < 0) {
            notify("§e[AutoBuy] Воронка «Сортировка» не найдена");
            state = State.NEXT;
            delay = 8;
            return;
        }
        click(currentHandler().syncId, slot);
        sortClicksLeft--;
        delay = SORT_CLICK_DELAY;
    }

    private void postOpen() {
        label = "сортировка";
        if (auctionOpen()) {
            sortClicksLeft = POST_SORT_CLICKS;
            state = State.POST_SORT;
            delay = 6;
        } else if (mc.currentScreen != null) {
            mc.player.closeHandledScreen();
            delay = 6;
        } else {
            mc.player.networkHandler.sendChatCommand("ah");
            state = State.POST_WAIT;
            counter = 60;
        }
    }

    private void postWait() {
        if (auctionOpen()) {
            sortClicksLeft = POST_SORT_CLICKS;
            state = State.POST_SORT;
            delay = 6;
        } else if (--counter <= 0) {
            finishParsing();
        }
    }

    private void postSort() {
        if (!auctionOpen() || sortClicksLeft <= 0) {
            finishParsing();
            return;
        }
        int slot = findSortButton(currentHandler());
        if (slot < 0) {
            finishParsing();
            return;
        }
        click(currentHandler().syncId, slot);
        sortClicksLeft--;
        delay = SORT_CLICK_DELAY;
    }

    /** Завершение парса: аукцион остаётся открытым, движок продолжит автобай. */
    private void finishParsing() {
        notify("§a[AutoBuy] Парсинг окончен");
        AutoBuyConfig.save();
        stop();
    }

    private void next() {
        if (index >= queue.size()) {
            // все предметы спарсены — пере-сортировка под автобай и выход
            state = State.POST_OPEN;
            counter = 60;
            delay = 4;
            return;
        }
        ItemEntry entry = queue.get(index);
        String name = (entry.displayName != null && !entry.displayName.isBlank()) ? entry.displayName : entry.itemId;
        if (entry.loreKeyword != null && !entry.loreKeyword.isBlank()) {
            name = name + " [" + entry.loreKeyword + "]";
        }
        label = name;
        bestPrice = -1L;
        page = 0;
        pageHash = 0L;

        if (mc.currentScreen != null) {
            mc.player.closeHandledScreen();
            delay = 8;
        } else {
            String query = entry.query().replaceAll("[«»\"']", "").trim();
            mc.player.networkHandler.sendChatCommand("ah search " + query);
            state = State.WAIT_SCREEN;
            delay = 0;
            counter = 60;
        }
    }

    private void waitScreen() {
        counter--;
        if (counter <= 0) {
            finalizeItem();
            return;
        }
        if (mc.currentScreen instanceof GenericContainerScreen gcs && AutoBuyEngine.isAuctionScreen(gcs)) {
            state = State.SCAN;
            counter = 25;
        }
    }

    private void scan() {
        if (!(mc.currentScreen instanceof GenericContainerScreen gcs) || !AutoBuyEngine.isAuctionScreen(gcs)) {
            finalizeItem();
            return;
        }
        GenericContainerScreenHandler handler = gcs.getScreenHandler();
        int rows = handler.getRows();
        int lotSlots = (rows - 1) * 9;
        ItemEntry entry = queue.get(index);

        for (int i = 0; i < lotSlots; i++) {
            ItemStack stack = handler.getSlot(i).getStack();
            Lot lot = PriceParser.parse(i, stack);
            if (lot != null && !lot.barrier && ItemMatcher.matches(entry, lot)) {
                if (bestPrice < 0L || lot.unitPrice < bestPrice) {
                    bestPrice = lot.unitPrice;
                }
            }
        }

        counter--;
        if (counter > 0) {
            return;
        }

        // для брони и при ненайденной цене листаем дальше (до 25 страниц)
        boolean needMore = isArmor(entry) && bestPrice < 0L && page + 1 < MAX_PAGES;
        if (!needMore) {
            finalizeItem();
            return;
        }
        int pageBtn = findPageButton(handler, rows);
        if (pageBtn < 0) {
            finalizeItem();
            return;
        }
        pageHash = pageHash(handler, lotSlots);
        click(handler.syncId, pageBtn);
        state = State.WAIT_PAGE;
        delay = 4;
        counter = 40;
        page++;
    }

    private void waitPage() {
        if (!(mc.currentScreen instanceof GenericContainerScreen gcs) || !AutoBuyEngine.isAuctionScreen(gcs)) {
            finalizeItem();
            return;
        }
        GenericContainerScreenHandler handler = gcs.getScreenHandler();
        int rows = handler.getRows();
        int lotSlots = (rows - 1) * 9;
        if (pageHash(handler, lotSlots) != pageHash) {
            state = State.SCAN;
            counter = 25;
            return;
        }
        counter--;
        if (counter <= 0) {
            finalizeItem();
        }
    }

    private void advance() {
        if (mc.currentScreen != null) {
            mc.player.closeHandledScreen();
            delay = 6;
        } else {
            index++;
            state = State.NEXT;
            delay = 10;
        }
    }

    private void finalizeItem() {
        ItemEntry entry = queue.get(index);
        AutoBuyConfig cfg = AutoBuyConfig.cfg();
        int percent = Math.max(1, Math.min(100, cfg.parsePercent));
        // если на рынке предмет не найден — ставим 0 (предмет фактически выключен)
        long limit = bestPrice > 0L ? bestPrice * (long) percent / 100L : 0L;
        entry.maxUnitPrice = limit;
        // полную рыночную цену сохраняем отдельно — на ней строится автосел (цена − %).
        // если на рынке не нашли, прошлое значение не затираем
        if (bestPrice > 0L) {
            entry.marketPrice = bestPrice;
        }
        AutoBuyConfig.save();
        state = State.ADVANCE;
        delay = 4;
    }

    // ===================== вспомогательное =====================

    private boolean auctionOpen() {
        return mc.currentScreen instanceof GenericContainerScreen gcs && AutoBuyEngine.isAuctionScreen(gcs);
    }

    private GenericContainerScreenHandler currentHandler() {
        return ((GenericContainerScreen) mc.currentScreen).getScreenHandler();
    }

    /** Слот воронки «Сортировка» в аукционе (по предмету и названию/лору), либо -1. */
    private int findSortButton(GenericContainerScreenHandler handler) {
        int total = handler.getRows() * 9;
        for (int i = 0; i < total; i++) {
            ItemStack stack = handler.getSlot(i).getStack();
            if (stack.isEmpty()) {
                continue;
            }
            String id = Registries.ITEM.getId(stack.getItem()).toString();
            if (id.equals(SORT_ITEM) && textContains(stack, SORT_KEYWORD)) {
                return i;
            }
        }
        // запасной вариант: любой предмет с нужным названием в имени/лоре
        for (int i = 0; i < total; i++) {
            ItemStack stack = handler.getSlot(i).getStack();
            if (!stack.isEmpty() && textContains(stack, SORT_KEYWORD)) {
                return i;
            }
        }
        return -1;
    }

    private boolean textContains(ItemStack stack, String needle) {
        if (stack.getName().getString().toLowerCase(Locale.ROOT).contains(needle)) {
            return true;
        }
        LoreComponent lore = stack.get(DataComponentTypes.LORE);
        if (lore != null) {
            for (Text line : lore.lines()) {
                if (line.getString().toLowerCase(Locale.ROOT).contains(needle)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isArmor(ItemEntry e) {
        if (e == null || e.itemId == null) {
            return false;
        }
        String id = e.itemId;
        return id.endsWith("_helmet") || id.endsWith("_chestplate") || id.endsWith("_leggings") || id.endsWith("_boots");
    }

    private int findPageButton(GenericContainerScreenHandler handler, int rows) {
        int from = (rows - 1) * 9;
        int to = rows * 9;
        int found = -1;
        for (int i = from; i < to; i++) {
            ItemStack stack = handler.getSlot(i).getStack();
            if (!stack.isEmpty()) {
                String id = Registries.ITEM.getId(stack.getItem()).toString();
                if (id.equals("minecraft:lime_dye") || id.equals("minecraft:green_dye")) {
                    found = i;
                }
            }
        }
        return found;
    }

    private long pageHash(GenericContainerScreenHandler handler, int count) {
        long h = 1L;
        for (int i = 0; i < count; i++) {
            ItemStack stack = handler.getSlot(i).getStack();
            h = h * 31L + (stack.isEmpty() ? 0L
                    : (long) Registries.ITEM.getRawId(stack.getItem()) * 113L + stack.getCount());
        }
        return h;
    }

    private void click(int syncId, int slot) {
        if (mc.interactionManager != null && mc.player != null) {
            mc.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, mc.player);
        }
    }

    private void notify(String text) {
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal(text), false);
        }
    }
}
