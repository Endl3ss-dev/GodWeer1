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

import java.util.Locale;

/**
 * Авто-релист: раз в {@link AutoBuyConfig#relistIntervalSeconds} секунд снимает с продажи
 * собственные лоты и перевыставляет их с увеличенной скидкой.
 *
 * Цикл: открыть аукцион ({@code /ah}) → ЛКМ по эндер-сундуку с названием/лором
 * «Товары на продаже» → если на экране лотов нет предметов из нашего списка, выйти и продолжить
 * автобай → если есть, поочерёдно с задержкой ПКМ по каждому (предмет возвращается в инвентарь) →
 * передать продавцу {@link AutoSeller#triggerRelist()} для перевыставления.
 *
 * Пока релист активен, движок не сканирует аукцион (см. {@link AutoBuyEngine#onUpdate}).
 */
public class RelistManager implements QClient {
    private enum State {
        IDLE, OPEN_AUCTION, WAIT_AUCTION, OPEN_LISTINGS, WAIT_LISTINGS, COLLECT, FINISH
    }

    /** Название/лор эндер-сундука, открывающего список своих лотов. */
    private static final String BUTTON_KEYWORD = "товары на продаже";
    /** Подстрока заголовка экрана со своими лотами (в нижнем регистре). */
    private static final String LISTINGS_TITLE = "продаж";
    /** Задержка между поочерёдными кликами по лотам (тики). */
    private static final int COLLECT_DELAY = 10; // 0.5 c при 20 TPS
    private static final int MAX_COLLECT = 300;

    private State state = State.IDLE;
    private int delay = 0;
    private int waitTicks = 0;
    private int collected = 0;
    private int emptyScans = 0;

    private volatile boolean pending = false;

    /** Запросить цикл релиста. */
    public void start() {
        pending = true;
    }

    public boolean isActive() {
        return state != State.IDLE;
    }

    public String statusLine() {
        if (state == State.IDLE) {
            return "Релист: ожидание";
        }
        return "Релист: снято " + collected;
    }

    public synchronized void tick() {
        if (mc.player == null || mc.interactionManager == null) {
            state = State.IDLE;
            return;
        }
        if (delay > 0) {
            delay--;
            return;
        }
        switch (state) {
            case IDLE -> {
                if (!pending) {
                    return;
                }
                pending = false;
                collected = 0;
                emptyScans = 0;
                state = auctionOpen() ? State.OPEN_LISTINGS : State.OPEN_AUCTION;
            }
            case OPEN_AUCTION -> {
                if (mc.currentScreen != null) {
                    mc.player.closeHandledScreen();
                    delay = 6;
                } else {
                    mc.player.networkHandler.sendChatCommand("ah");
                    state = State.WAIT_AUCTION;
                    waitTicks = 60;
                }
            }
            case WAIT_AUCTION -> {
                if (auctionOpen()) {
                    state = State.OPEN_LISTINGS;
                    delay = 4;
                } else if (--waitTicks <= 0) {
                    goFinish();
                }
            }
            case OPEN_LISTINGS -> {
                if (!auctionOpen()) {
                    goFinish();
                    return;
                }
                GenericContainerScreenHandler handler =
                        ((GenericContainerScreen) mc.currentScreen).getScreenHandler();
                int slot = findListingsButton(handler);
                if (slot < 0) {
                    notify("§e[Релист] Эндер-сундук «Товары на продаже» не найден");
                    goFinish();
                    return;
                }
                click(handler.syncId, slot, 0);
                state = State.WAIT_LISTINGS;
                waitTicks = 60;
            }
            case WAIT_LISTINGS -> {
                if (listingsOpen()) {
                    state = State.COLLECT;
                    delay = 6;
                    emptyScans = 0;
                } else if (--waitTicks <= 0) {
                    goFinish();
                }
            }
            case COLLECT -> {
                if (!listingsOpen()) {
                    goFinish();
                    return;
                }
                GenericContainerScreenHandler handler =
                        ((GenericContainerScreen) mc.currentScreen).getScreenHandler();
                int slot = findMatchingListing(handler);
                if (slot < 0) {
                    // на текущей странице наших предметов нет — несколько подряд пустых сканов = конец
                    if (++emptyScans >= 3) {
                        goFinish();
                    } else {
                        delay = 4;
                    }
                } else {
                    emptyScans = 0;
                    // ПКМ — сервер снимает лот, предмет возвращается в инвентарь.
                    // Поочерёдно с задержкой, чтобы сервер успевал обновить экран.
                    click(handler.syncId, slot, 1);
                    collected++;
                    delay = COLLECT_DELAY;
                    if (collected >= MAX_COLLECT) {
                        goFinish();
                    }
                }
            }
            case FINISH -> {
                if (mc.currentScreen != null) {
                    mc.player.closeHandledScreen();
                    delay = 6;
                } else {
                    if (collected > 0) {
                        notify("§a[Релист] Снято лотов: §f" + collected + "§a — перевыставляю");
                        AutoBuyEngine.INSTANCE.seller.triggerRelist();
                    } else {
                        notify("§7[Релист] Своих лотов из списка нет");
                    }
                    state = State.IDLE;
                }
            }
            default -> state = State.IDLE;
        }
    }

    private void goFinish() {
        state = State.FINISH;
        delay = 4;
    }

    // ===================== экраны =====================

    private boolean auctionOpen() {
        return mc.currentScreen instanceof GenericContainerScreen gcs && AutoBuyEngine.isAuctionScreen(gcs);
    }

    private boolean listingsOpen() {
        if (!(mc.currentScreen instanceof GenericContainerScreen gcs)) {
            return false;
        }
        String title = gcs.getTitle().getString().toLowerCase(Locale.ROOT);
        return title.contains(LISTINGS_TITLE);
    }

    /** Эндер-сундук, открывающий список своих лотов (по названию/лору). */
    private int findListingsButton(GenericContainerScreenHandler handler) {
        int total = handler.getRows() * 9;
        // сначала точное совпадение: эндер-сундук с нужным названием
        for (int i = 0; i < total; i++) {
            ItemStack stack = handler.getSlot(i).getStack();
            if (stack.isEmpty()) {
                continue;
            }
            String id = Registries.ITEM.getId(stack.getItem()).toString();
            if (id.equals("minecraft:ender_chest") && textContains(stack, BUTTON_KEYWORD)) {
                return i;
            }
        }
        // запасной вариант: любой предмет с нужным названием в лоре/имени
        for (int i = 0; i < total; i++) {
            ItemStack stack = handler.getSlot(i).getStack();
            if (!stack.isEmpty() && textContains(stack, BUTTON_KEYWORD)) {
                return i;
            }
        }
        return -1;
    }

    /** Первый слот контейнера с предметом, подходящим под запись нашего списка. */
    private int findMatchingListing(GenericContainerScreenHandler handler) {
        int rows = handler.getRows();
        int lotSlots = (rows - 1) * 9;
        for (int i = 0; i < lotSlots; i++) {
            ItemStack stack = handler.getSlot(i).getStack();
            if (stack.isEmpty()) {
                continue;
            }
            Lot lot = PriceParser.parseForMatch(stack);
            if (lot == null) {
                continue;
            }
            for (ItemEntry e : AutoBuyConfig.cfg().entries) {
                if (ItemMatcher.matches(e, lot)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private boolean textContains(ItemStack stack, String needle) {
        String name = stack.getName().getString().toLowerCase(Locale.ROOT);
        if (name.contains(needle)) {
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

    private void click(int syncId, int slot, int button) {
        if (mc.interactionManager != null && mc.player != null) {
            mc.interactionManager.clickSlot(syncId, slot, button, SlotActionType.PICKUP, mc.player);
        }
    }

    private void notify(String text) {
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal(text), false);
        }
    }
}
