package tech.lightclient.autobuy;

import tech.lightclient.autobuy.QClient;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.events.impl.client.TickEvent;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class AutoBuyEngine implements QClient {
    public static final AutoBuyEngine INSTANCE = new AutoBuyEngine();
    private static final Random RANDOM = ThreadLocalRandom.current();

    public final PriceAutoParser parser = new PriceAutoParser();
    public final AutoSeller seller = new AutoSeller();
    public final RelistManager relist = new RelistManager();
    public final AutoPay autoPay = new AutoPay();

    private int relistTicks = -1;
    private int parseTicks = -1;
    private boolean resumeAfterParse = false;

    private static final java.util.regex.Pattern BALANCE = java.util.regex.Pattern.compile(
            "(?iu)(?:\\u0431\\u0430\\u043b\\u0430\\u043d\\u0441|\\u043d\\u0430\\s+\\u0441\\u0447\\u0451\\u0442\\u0443|\\u043d\\u0430\\s+\\u0441\\u0447\\u0451\\u0442\\u0435|\\u043d\\u0430\\s+\\u0441\\u0447\\u0435\\u0442\\u0435|\\u0432\\u0430\\u0448\\s+\\u0441\\u0447\\u0451\\u0442|\\u0432\\u0430\\u0448\\s+\\u0441\\u0447\\u0435\\u0442)[^\\d\\-]*([\\d\\s\\u00A0.,]+)");

    // ?????????????????????? ??????????????
    private int clickCooldown = 0;
    private long lastHash = 0L;
    private int stallCounter = 0;
    private int refreshCooldown = 0;
    private int openCooldown = 0;
    private int noScreenTicks = 0;

    private volatile String targetItemId = null;
    private volatile String targetName = null;
    private volatile String targetKeyword = null;
    private volatile long targetMaxPrice = 0L;
    private volatile boolean targetRequireCustom = false;
    private volatile List<Marker> targetMarkers = null;

    private int actionCooldown = 0;
    private boolean watching = false;
    private String watchItemId = null;
    private int preBuyCount = 0;
    private int watchdog = 0;
    private PurchaseRecord pendingRecord = null;
    private static final int WATCHDOG_TICKS = 20;

    // ?????????????? ?????????????? ????????????
    private float targetYaw = Float.NaN;
    private float targetPitch = Float.NaN;
    private int headTurnTimer = 0;
    private int nextHeadTurnDelay = 0;
    private boolean turningHead = false;
    private boolean wasEnabled = false;
    private static final float TURN_SPEED_BASE = 8.0f;
    private float currentTurnSpeed = TURN_SPEED_BASE;

    // ????????????????????????
    private int randomActionDelay = 0;
    private int focusItemSlot = -1;
    private int mouseHoverTicks = 0;
    private int blinkTimer = 0;
    private int randomPauseTimer = 0;

    private float randomSpeedMultiplier = 1.0f;
    private int randomTickCounter = 0;
    private boolean aggressiveMode = false;

    // ?????????????? ????????????????????
    private int refreshCounter = 0;

    private AutoBuyEngine() {}

    @EventHandler
    public void onClientTick(TickEvent event) {
        if (mc.player == null || mc.interactionManager == null) return;

        AutoBuyConfig cfg = AutoBuyConfig.cfg();

        // AutoSell is a background state machine of its own. Run it before
        // parser/buyer early returns so a price scan cannot starve selling.
        // This also covers the case where only AutoSell was enabled.
        if (cfg.autoSell || seller.isActive()) {
            try { seller.tick(); } catch (Throwable ignored) {}
        }

        // The parser must be driven by the client tick, not by rendering or
        // by the player-tick hook. The latter may stop while the window is
        // minimized even though the client is still connected and ticking.
        if (parser.isActive()) {
            try {
                parser.tick();
            } catch (Throwable ignored) {}
            return;
        }

        // Restore the regular buyer after an automatically started parse.
        // PriceAutoParser temporarily disables normal buying while it scans
        // the auction pages.
        if (resumeAfterParse) {
            resumeAfterParse = false;
            cfg.enabled = true;
        }

        tickAutoParser(cfg);
        if (parser.isActive()) {
            try {
                parser.tick();
            } catch (Throwable ignored) {}
            return;
        }

        // ????????????????????????
        randomTickCounter++;
        if (randomTickCounter >= 3) {
            randomTickCounter = 0;
            randomSpeedMultiplier = 0.9f + RANDOM.nextFloat() * 0.3f;
            aggressiveMode = RANDOM.nextInt(5) < 2;
        }

        if (randomPauseTimer > 0) {
            randomPauseTimer--;
            return;
        }

        if (cfg.enabled) {
            if (!wasEnabled) {
                nextHeadTurnDelay = randomDelay(2, 20);
                currentTurnSpeed = TURN_SPEED_BASE * (0.9f + RANDOM.nextFloat() * 0.2f) * randomSpeedMultiplier;
                wasEnabled = true;
                // ???????????????????? ???????? ?????? ??????????????????, ?????????? ?????????? ???????????? ????????????????
            }
            // ?????????????? ???????????? ???????????? ???????? ???? ???????? ?? ????????
            headTurnTick();
        } else {
            if (wasEnabled) {
                turningHead = false;
                targetYaw = Float.NaN;
                targetPitch = Float.NaN;
                wasEnabled = false;
                // ???????????????????? ???????????????? ?????? ????????????????????
            }
        }

        // AutoSell must finish an already started sale even if the master
        // AutoBuy toggle was turned off while the server was processing it.
        if (!cfg.enabled) {
            if (cfg.autoRelist || relist.isActive()) {
                try { relist.tick(); } catch (Throwable ignored) {}
            }
            return;
        }

        if (randomActionDelay > 0) {
            randomActionDelay--;
            return;
        }

        if (cfg.autoPay && RANDOM.nextInt(2) == 0) {
            try { autoPay.tick(); } catch (Throwable ignored) {}
        }

        try {
            if (RANDOM.nextInt(2) == 0) relist.tick();
        } catch (Throwable ignored) {}
        if (relist.isActive()) return;

        if (seller.isActive()) return;

        try { scheduler(); } catch (Throwable ignored) {}
        try { scannerTick(); } catch (Throwable ignored) {}
        try { confirmTick(); } catch (Throwable ignored) {}

        try { instantRefreshTick(); } catch (Throwable ignored) {}

        blinkTick();
    }

    // ===== ???????????????? =====
    private void instantRefreshTick() {
        AutoBuyConfig cfg = AutoBuyConfig.cfg();
        if (!cfg.autoRefresh) return;

        Screen screen = mc.currentScreen;
        if (!(screen instanceof GenericContainerScreen gcs)) return;
        if (!isAuctionScreen(gcs)) return;

        GenericContainerScreenHandler handler = gcs.getScreenHandler();
        int lotSlots = (handler.getRows() - 1) * 9;
        long hash = pageHash(handler, lotSlots);

        refreshCounter++;

        if (refreshCounter >= 1 + RANDOM.nextInt(2)) {
            refreshCounter = 0;

            if (hash != lastHash) {
                lastHash = hash;
                stallCounter = 0;
                if (refreshCooldown <= 0) {
                    refresh(handler, handler.syncId, handler.getRows());
                    refreshCooldown = computeRefreshCooldownTicks(AutoBuyConfig.cfg());
                }
            } else {
                stallCounter++;
                if (stallCounter >= 1 + RANDOM.nextInt(2)) {
                    if (refreshCooldown <= 0) {
                        refresh(handler, handler.syncId, handler.getRows());
                        refreshCooldown = computeRefreshCooldownTicks(AutoBuyConfig.cfg());
                        stallCounter = 0;
                    }
                }
            }
        }
    }

    private void blinkTick() {
        if (blinkTimer > 0) {
            blinkTimer--;
            if (blinkTimer == 0) {
                focusItemSlot = -1;
                mouseHoverTicks = 0;
            }
        } else if (RANDOM.nextInt(40) == 0) {
            blinkTimer = randomDelay(1, 2);
        }
    }

    private void headTurnTick() {
        if (mc.player == null) return;

        if (RANDOM.nextInt(4) == 0) {
            currentTurnSpeed = TURN_SPEED_BASE * (0.8f + RANDOM.nextFloat() * 0.4f) * randomSpeedMultiplier;
        }

        if (turningHead) {
            if (headTurnTimer > 0) {
                headTurnTimer--;

                float currentYaw = mc.player.getYaw();
                float currentPitch = mc.player.getPitch();

                float yawDiff = MathHelper.wrapDegrees(targetYaw - currentYaw);
                float pitchDiff = MathHelper.wrapDegrees(targetPitch - currentPitch);

                float speedMod = 0.9f + RANDOM.nextFloat() * 0.2f;
                float stepYaw = Math.min(Math.abs(yawDiff), currentTurnSpeed * speedMod) * Math.signum(yawDiff);
                float stepPitch = Math.min(Math.abs(pitchDiff), currentTurnSpeed * 0.9f * speedMod) * Math.signum(pitchDiff);

                if (Math.abs(yawDiff) < 0.5f && Math.abs(pitchDiff) < 0.5f) {
                    turningHead = false;
                    nextHeadTurnDelay = randomDelay(2, 30);
                } else {
                    mc.player.setYaw(currentYaw + stepYaw);
                    mc.player.setPitch(currentPitch + stepPitch);
                }
            } else {
                turningHead = false;
                nextHeadTurnDelay = randomDelay(2, 30);
            }
        } else {
            if (nextHeadTurnDelay > 0) {
                nextHeadTurnDelay--;
            } else {
                startRandomHeadTurn();
            }
        }
    }

    private void startRandomHeadTurn() {
        if (mc.player == null) return;

        float currentYaw = mc.player.getYaw();
        float currentPitch = mc.player.getPitch();

        targetYaw = currentYaw + (RANDOM.nextFloat() * 160f - 80f);
        targetPitch = MathHelper.clamp(currentPitch + (RANDOM.nextFloat() * 60f - 30f), -80f, 80f);

        headTurnTimer = randomDelay(1, 10);
        turningHead = true;
        currentTurnSpeed = TURN_SPEED_BASE * (0.8f + RANDOM.nextFloat() * 0.4f) * randomSpeedMultiplier;
    }

    private void scheduler() {
        AutoBuyConfig cfg = AutoBuyConfig.cfg();

        if (!cfg.enabled) {
            resumeAfterParse = false;
            parseTicks = -1;
            relistTicks = -1;
            return;
        }

        if (resumeAfterParse) {
            resumeAfterParse = false;
            cfg.enabled = true;
        }

        if (cfg.autoRelist) {
            if (relistTicks < 0) {
                relistTicks = Math.max(5, cfg.relistIntervalSeconds * 10) + randomDelay(-20, 50);
            }
            if (--relistTicks <= 0) {
                int baseInterval = Math.max(5, cfg.relistIntervalSeconds * 10);
                relistTicks = baseInterval + randomDelay(-baseInterval/4, baseInterval/3);

                if (RANDOM.nextInt(3) == 0) {
                    relistTicks += randomDelay(5, 20);
                } else {
                    relist.start();
                }
                return;
            }
        } else {
            relistTicks = -1;
        }

        randomActionDelay = Math.max(0, computeActionCooldownTicks(cfg) - 1);
    }

    @EventHandler
    public void onPacket(EventPacket event) {
        if (!event.isReceive()) return;
        Packet<?> packet = event.getPacket();
        if (packet instanceof GameMessageS2CPacket msg) {
            AutoBuyConfig cfg = AutoBuyConfig.cfg();
            String raw = msg.content().getString().toLowerCase(Locale.ROOT);
            if (raw.contains("\u043d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u0441\u0440\u0435\u0434\u0441\u0442\u0432")
                    || raw.contains("insufficient funds")) onInsufficientFunds();
            if (raw.contains("\u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u0431\u043e\u043b\u044c\u0448\u0435 \u0432\u044b\u0441\u0442\u0430\u0432\u043b\u044f\u0442\u044c")
                    || raw.contains("auction is full")) seller.onAuctionFull();
            if (raw.contains("\u043a\u0443\u043f\u0438\u043b \u0443 \u0432\u0430\u0441")
                    || raw.contains("bought from you")) seller.onSold();
            if (raw.contains("\u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0432\u044b\u0441\u0442\u0430\u0432\u0438\u043b\u0438")
                    || raw.contains("listed successfully")
                    || raw.contains("successfully listed")) seller.onListed();
            if ((raw.contains("\u043f\u043e\u0434\u043e\u0436\u0434\u0438\u0442\u0435") || raw.contains("please wait"))
                    && (raw.contains("\u0434\u043b\u044f \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u044f \u044d\u0442\u043e\u0439 \u043a\u043e\u043c\u0430\u043d\u0434\u044b")
                    || raw.contains("this command") || raw.contains("cooldown"))) {
                seller.onCommandCooldown();
                openCooldown = Math.max(openCooldown, computeOpenCooldownTicks(cfg));
            }
            if (!raw.contains("\u043f\u043e\043f\043e\043b\043d\0435\043d")
                    && !raw.contains("\u043a\0443\043f\0438\043b \0443 \0432\0430\u0441")) {
                Long bal = parseBalance(raw);
                if (bal != null) autoPay.onBalance(bal);
            }
        }
    }

    private void scannerTick() {
        AutoBuyConfig cfg = AutoBuyConfig.cfg();
        if (clickCooldown > 0) clickCooldown--;
        if (refreshCooldown > 0) refreshCooldown--;
        if (openCooldown > 0) openCooldown--;

        if (RANDOM.nextInt(20) == 0) {
            randomPauseTimer = randomDelay(1, 3);
            return;
        }

        Screen screen = mc.currentScreen;
        if (!(screen instanceof GenericContainerScreen gcs)) {
            stallCounter = 0;
            if (screen == null) {
                noScreenTicks++;
                if (noScreenTicks >= Math.max(1, computeOpenCooldownTicks(cfg) / 2)) tryOpenAuction();
            } else {
                noScreenTicks = 0;
            }
            return;
        }
        noScreenTicks = 0;
        if (!isAuctionScreen(gcs)) {
            stallCounter = 0;
            return;
        }
        if (clickCooldown > 0) return;

        GenericContainerScreenHandler handler = gcs.getScreenHandler();
        int syncId = handler.syncId;
        int rows = handler.getRows();
        int lotSlots = (rows - 1) * 9;

        long hash = pageHash(handler, lotSlots);
        boolean changed = hash != lastHash;
        if (changed) {
            stallCounter = 0;
            lastHash = hash;
        } else {
            stallCounter++;
        }

        if (RANDOM.nextInt(8) == 0) {
            focusItemSlot = RANDOM.nextInt(lotSlots);
            mouseHoverTicks = randomDelay(1, 2);
        }
        if (mouseHoverTicks > 0) mouseHoverTicks--;

        List<Lot> lots = new ArrayList<>();
        for (int i = 0; i < lotSlots; i++) {
            ItemStack stack = handler.getSlot(i).getStack();
            Lot lot = PriceParser.parse(i, stack);
            if (lot != null && !lot.barrier) lots.add(lot);
        }

        if (RANDOM.nextInt(3) == 0) {
            java.util.Collections.shuffle(lots, RANDOM);
        }

        Lot target = null;
        ItemEntry targetEntry = null;

        int scanStart = lots.isEmpty() ? 0 : RANDOM.nextInt(lots.size());
        int maxScan = aggressiveMode ? lots.size() : Math.min(lots.size(), 6);

        for (int i = 0; i < maxScan; i++) {
            int idx = (scanStart + i) % lots.size();
            if (idx >= lots.size()) continue;
            Lot lot = lots.get(idx);
            ItemEntry entry = selectEntry(cfg, lot);
            if (entry != null) {
                target = lot;
                targetEntry = entry;
                break;
            }
        }

        if (target != null && targetEntry != null) {
            targetItemId = target.itemId;
            targetName = targetEntry.displayName;
            targetKeyword = targetEntry.loreKeyword;
            targetMaxPrice = targetEntry.maxUnitPrice;
            targetRequireCustom = targetEntry.requireCustom;
            targetMarkers = targetEntry.resolveMarkers();

            clickCooldown = computeClickCooldownTicks(cfg);
            if (RANDOM.nextInt(8) == 0) {
                clickCooldown += Math.max(1, computeActionCooldownTicks(cfg));
                targetItemId = null;
                return;
            }
            click(syncId, target.slot);
        }
    }

    private void tryOpenAuction() {
        AutoBuyConfig cfg = AutoBuyConfig.cfg();
        if (openCooldown > 0 || mc.player == null || mc.player.networkHandler == null) return;
        mc.player.networkHandler.sendChatCommand("ah");
        openCooldown = computeOpenCooldownTicks(cfg);
    }

    private ItemEntry selectEntry(AutoBuyConfig cfg, Lot lot) {
        String self = mc.player.getName().getString();
        if (self != null && lot.seller != null && self.equalsIgnoreCase(lot.seller)) return null;
        if (cfg.ignored(lot.seller)) return null;
        if (cfg.minPriceEnabled && cfg.minPrice > 0L && lot.unitPrice < cfg.minPrice) return null;

        ItemEntry best = null;
        int bestScore = -1;

        List<ItemEntry> entries = new ArrayList<>(cfg.entries);
        if (RANDOM.nextInt(2) == 0) {
            java.util.Collections.shuffle(entries, RANDOM);
        }

        for (ItemEntry e : entries) {
            if (!e.enabled || e.maxUnitPrice <= 0L) continue;
            if (ItemMatcher.matches(e, lot) && lot.unitPrice <= e.maxUnitPrice) {
                int score = (e.displayName != null ? e.displayName.length() * 2 : 0)
                        + (e.loreKeyword != null && !e.loreKeyword.isBlank() ? randomDelay(15, 50) : 0)
                        + (e.matchAnySword ? -randomDelay(10, 30) : 0)
                        + (e.requireCustom ? randomDelay(10, 30) : 0)
                        + (RANDOM.nextInt(20) - 10);
                if (score > bestScore) {
                    bestScore = score;
                    best = e;
                }
            }
        }
        return best;
    }

    public static boolean isAuctionScreen(GenericContainerScreen gcs) {
        GenericContainerScreenHandler handler = gcs.getScreenHandler();
        if (handler.getRows() != 6) return false;
        String title = gcs.getTitle().getString();
        if (title == null) return false;

        // Keep the check independent from the source-file encoding. Some
        // server titles use Russian, others use the English translation.
        String normalized = title.trim().toLowerCase(java.util.Locale.ROOT);
        return normalized.startsWith("\u0430\u0443\u043a\u0446\u0438\u043e\u043d")
                || normalized.startsWith("auction")
                || normalized.contains("\u0430\u0443\u043a\u0446\u0438\u043e\u043d")
                || normalized.contains("auction");
    }

    /**
     * Starts the price parser without requiring the auction screen or the
     * client window to be visible. All screen interaction is performed later
     * by the parser state machine on client ticks.
     */
    private void tickAutoParser(AutoBuyConfig cfg) {
        if (!cfg.autoParse) {
            parseTicks = -1;
            return;
        }

        if (parseTicks < 0) {
            int interval = Math.max(20, cfg.parseIntervalSeconds * 20);
            parseTicks = interval;
        }

        if (--parseTicks > 0) return;

        boolean restoreBuyer = cfg.enabled;
        parser.start();
        if (parser.isActive()) {
            resumeAfterParse = restoreBuyer;
            parseTicks = -1;
        } else {
            // No enabled entries or no player yet: retry after the normal
            // interval instead of repeatedly attempting every tick.
            parseTicks = Math.max(20, cfg.parseIntervalSeconds * 20);
        }
    }

    private void refresh(GenericContainerScreenHandler handler, int syncId, int rows) {
        if (refreshCooldown > 0) return;

        int total = rows * 9;
        int slot = -1;

        int cfgSlot = AutoBuyConfig.cfg().refreshSlot;
        if (cfgSlot >= 0 && cfgSlot < total) {
            ItemStack stack = handler.getSlot(cfgSlot).getStack();
            if (!stack.isEmpty()) {
                String id = Registries.ITEM.getId(stack.getItem()).toString();
                if (id.equals("minecraft:emerald")) {
                    slot = cfgSlot;
                }
            }
        }

        if (slot < 0) {
            slot = findItem(handler, (rows - 1) * 9, total, "minecraft:emerald");
        }

        if (slot >= 0) {
            click(syncId, slot);
            refreshCooldown = computeRefreshCooldownTicks(AutoBuyConfig.cfg());
            lastHash = 0L;
            stallCounter = 0;
        }
    }

    private int findItem(GenericContainerScreenHandler handler, int from, int to, String itemId) {
        List<Integer> slots = new ArrayList<>();
        for (int i = from; i < to; i++) {
            slots.add(i);
        }

        if (RANDOM.nextInt(2) == 0) {
            java.util.Collections.shuffle(slots, RANDOM);
        }

        for (int i : slots) {
            ItemStack stack = handler.getSlot(i).getStack();
            if (stack.isEmpty()) continue;
            String id = Registries.ITEM.getId(stack.getItem()).toString();
            if (id.equals(itemId)) return i;
        }
        return -1;
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

    private void confirmTick() {
        AutoBuyConfig cfg = AutoBuyConfig.cfg();

        if (watching) {
            watchdog--;
            if (watchItemId != null && countInInventory(watchItemId) > preBuyCount) {
                String boughtId = watchItemId;
                if (pendingRecord != null) {
                    pendingRecord.success = true;
                    AutoBuyConfig.cfg().addHistory(pendingRecord);
                }
                notifyPurchased();
                boolean sell = AutoBuyConfig.cfg().autoSell;
                clearWatch();
                if (sell && boughtId != null) seller.trigger();
            } else if (watchdog <= 0) {
                clearWatch();
            }
        }

        if (actionCooldown > 0) {
            actionCooldown--;
            return;
        }

        Screen screen = mc.currentScreen;
        if (!(screen instanceof GenericContainerScreen gcs) || !isConfirmScreen(gcs)) return;
        if (!cfg.enabled) return;

        GenericContainerScreenHandler handler = gcs.getScreenHandler();
        int syncId = handler.syncId;
        Lot lot = parseConfirmLot(handler);

        String expectedId = targetItemId;
        long expectedMax = targetMaxPrice;
        if (expectedId == null || expectedMax <= 0L) {
            cancelWithDelay(handler, syncId);
            return;
        }

        if (lot == null || !expectedId.equals(lot.itemId)) {
            cancelWithDelay(handler, syncId);
            targetItemId = null;
            return;
        }

        String keyword = targetKeyword;
        String name = targetName;
        if (keyword != null && !keyword.isBlank()) {
            boolean inLore = TextNormalizer.keywordIn(lot.lore, keyword);
            boolean inName = TextNormalizer.keywordIn(lot.name, keyword);
            if (!inLore && !inName) {
                cancelWithDelay(handler, syncId);
                targetItemId = null;
                return;
            }
            if (targetRequireCustom && lot.name != null
                    && lot.name.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT))) {
                cancelWithDelay(handler, syncId);
                targetItemId = null;
                return;
            }
        } else if (name != null && !name.isBlank() && !TextNormalizer.nameMatches(name, lot.name)) {
            cancelWithDelay(handler, syncId);
            targetItemId = null;
            return;
        }

        if (targetRequireCustom && !lot.hasMarkers) {
            cancelWithDelay(handler, syncId);
            targetItemId = null;
            return;
        }

        List<Marker> markers = targetMarkers;
        if (markers != null && !markers.isEmpty()) {
            boolean found = markers.stream().anyMatch(m -> ItemMatcher.markerMatches(m, lot));
            if (!found) {
                cancelWithDelay(handler, syncId);
                targetItemId = null;
                targetMarkers = null;
                return;
            }
        } else if (targetRequireCustom) {
            cancelWithDelay(handler, syncId);
            targetItemId = null;
            targetMarkers = null;
            return;
        }

        if (lot.unitPrice > expectedMax) {
            cancelWithDelay(handler, syncId);
            targetItemId = null;
            return;
        }

        int greenSlot = confirmSlot(handler);
        if (greenSlot >= 0) {
            actionCooldown = computeActionCooldownTicks(cfg);
            if (RANDOM.nextInt(10) == 0) {
                cancelWithDelay(handler, syncId);
                targetItemId = null;
                return;
            }
            click(syncId, greenSlot);
            watching = true;
            watchItemId = lot.itemId;
            preBuyCount = countInInventory(lot.itemId);
            watchdog = WATCHDOG_TICKS + RANDOM.nextInt(10);
            long total = lot.totalPrice > 0L ? lot.totalPrice : lot.unitPrice * Math.max(1, lot.count);
            String recLabel = (lot.name != null && !lot.name.isBlank()) ? lot.name : lot.itemId;
            pendingRecord = new PurchaseRecord(System.currentTimeMillis(), lot.itemId, recLabel,
                    lot.seller, lot.unitPrice, total, lot.count);
            targetItemId = null;
            targetMaxPrice = 0L;
            targetMarkers = null;
        }
    }

    private void cancelWithDelay(GenericContainerScreenHandler handler, int syncId) {
        actionCooldown = computeActionCooldownTicks(AutoBuyConfig.cfg());
        cancel(handler, syncId);
    }

    public static boolean isConfirmScreen(GenericContainerScreen gcs) {
        String title = gcs.getTitle().getString();
        if (title == null) return false;
        String normalized = title.trim().toLowerCase(Locale.ROOT);

        // The old source contained mojibake here ("????"), so confirmation
        // screens were never recognized and the green button was never
        // clicked. Keep both server language variants like Winlend does.
        return normalized.contains("\u043f\u043e\u043a\u0443\u043f\u043a\u0430 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u0430")
                || normalized.contains("\u043f\u043e\u0434\u0442\u0432\u0435\u0440\u0436\u0434\u0435\u043d\u0438\u0435")
                || normalized.contains("purchase")
                || normalized.contains("confirm");
    }

    private Lot parseConfirmLot(GenericContainerScreenHandler handler) {
        int total = handler.getRows() * 9;
        int start = RANDOM.nextInt(Math.max(1, total / 2));
        for (int i = start; i < total; i++) {
            ItemStack stack = handler.getSlot(i).getStack();
            if (stack.isEmpty()) continue;
            String id = Registries.ITEM.getId(stack.getItem()).toString();
            if (id.contains("stained_glass") || id.contains("concrete") || id.contains("wool")) continue;
            Lot lot = PriceParser.parse(i, stack);
            if (lot != null && !lot.barrier) return lot;
        }
        for (int i = 0; i < start; i++) {
            ItemStack stack = handler.getSlot(i).getStack();
            if (stack.isEmpty()) continue;
            String id = Registries.ITEM.getId(stack.getItem()).toString();
            if (id.contains("stained_glass") || id.contains("concrete") || id.contains("wool")) continue;
            Lot lot = PriceParser.parse(i, stack);
            if (lot != null && !lot.barrier) return lot;
        }
        return null;
    }

    private int confirmSlot(GenericContainerScreenHandler handler) {
        int total = handler.getRows() * 9;
        // Confirmation menus may use a green stained-glass pane, concrete,
        // wool, or a server-specific green button item. Scan deterministically
        // so the action is not randomly skipped on the first tick.
        for (int i = 0; i < total; i++) {
            String id = Registries.ITEM.getId(handler.getSlot(i).getStack().getItem()).toString();
            if (id.contains("lime_stained_glass") || id.contains("green_stained_glass")
                    || id.contains("lime_concrete") || id.contains("green_wool") || id.contains("lime_wool")) return i;
        }
        return -1;
    }

    private int cancelSlot(GenericContainerScreenHandler handler) {
        int total = handler.getRows() * 9;
        for (int i = 0; i < total; i++) {
            String id = Registries.ITEM.getId(handler.getSlot(i).getStack().getItem()).toString();
            if (id.contains("red_stained_glass") || id.contains("red_concrete") || id.contains("red_wool")) return i;
        }
        return -1;
    }

    private void cancel(GenericContainerScreenHandler handler, int syncId) {
        int red = cancelSlot(handler);
        if (red < 0) {
            mc.player.closeHandledScreen();
        } else {
            click(syncId, red);
        }
    }

    public synchronized void onInsufficientFunds() {
        if (watching) {
            if (pendingRecord != null) {
                pendingRecord.success = false;
                pendingRecord.failReason = "???????????????????????? ??????????????";
                AutoBuyConfig.cfg().addHistory(pendingRecord);
            }
            notify("??c[AutoBuy] ???????????????????????? ?????????????? ??? ?????????????? ????????????????");
            clearWatch();
        }
    }

    private int countInInventory(String itemId) {
        if (mc.player == null || itemId == null) return 0;
        PlayerInventory inv = mc.player.getInventory();
        int count = 0;
        int size = inv.size();
        int start = RANDOM.nextInt(Math.max(1, size / 2));
        for (int i = start; i < size; i++) {
            ItemStack stack = inv.getStack(i);
            if (!stack.isEmpty() && Registries.ITEM.getId(stack.getItem()).toString().equals(itemId)) {
                count += stack.getCount();
            }
        }
        for (int i = 0; i < start; i++) {
            ItemStack stack = inv.getStack(i);
            if (!stack.isEmpty() && Registries.ITEM.getId(stack.getItem()).toString().equals(itemId)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private void click(int syncId, int slot) {
        if (mc.interactionManager == null || mc.player == null) return;
        mc.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, mc.player);
    }

    private void clearWatch() {
        watching = false;
        watchItemId = null;
        preBuyCount = 0;
        watchdog = 0;
        pendingRecord = null;
    }

    private void notifyPurchased() {
        String label = pendingRecord != null ? pendingRecord.label : (watchItemId == null ? "?" : watchItemId);
        notify("??a[AutoBuy] ??????????????: ??f" + label);
    }

    private void notify(String text) {
        if (mc.player != null) mc.player.sendMessage(Text.literal(text), false);
    }

    private static Long parseBalance(String raw) {
        java.util.regex.Matcher m = BALANCE.matcher(raw);
        if (!m.find()) return null;
        String s = m.group(1).replaceAll("[\\s\\u00A0]+", "");
        int dot = s.indexOf('.');
        int comma = s.indexOf(',');
        int cut = dot < 0 ? comma : (comma < 0 ? dot : Math.min(dot, comma));
        if (cut >= 0) s = s.substring(0, cut);
        s = s.replaceAll("\\D", "");
        if (s.isEmpty()) return null;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void forceRefresh() {
        refreshCooldown = 0;
        lastHash = 0L;
        stallCounter = 999;
        refreshCounter = 999;

        Screen screen = mc.currentScreen;
        if (screen instanceof GenericContainerScreen gcs && isAuctionScreen(gcs)) {
            GenericContainerScreenHandler handler = gcs.getScreenHandler();
            refresh(handler, handler.syncId, handler.getRows());
        }
    }

    private int computeClickCooldownTicks(AutoBuyConfig cfg) {
        int baseMs = cfg.delayMsForStage(0.18f, 28, 210);
        int manualMs = Math.max(0, cfg.clickIntervalTicks) * 16;
        return Math.max(1, msToTicks(baseMs + manualMs + RANDOM.nextInt(28)));
    }

    private int computeRefreshCooldownTicks(AutoBuyConfig cfg) {
        if (cfg.refreshIntervalTicks > 0) {
            return Math.max(1, cfg.refreshIntervalTicks);
        }
        int baseMs = cfg.delayMsForStage(0.45f, 18, 160);
        return Math.max(0, msToTicks(baseMs + RANDOM.nextInt(20)));
    }

    private int computeActionCooldownTicks(AutoBuyConfig cfg) {
        int baseMs = cfg.delayMsForStage(0.70f, 10, 130);
        return Math.max(0, msToTicks(baseMs + RANDOM.nextInt(18)));
    }

    private int computeOpenCooldownTicks(AutoBuyConfig cfg) {
        int baseMs = cfg.delayMsForStage(1.0f, 200, 1150);
        return Math.max(1, msToTicks(baseMs + RANDOM.nextInt(60)));
    }

    private int msToTicks(int millis) {
        return (int) Math.ceil(Math.max(0, millis) / 50.0);
    }

    private static int randomDelay(int min, int max) {
        if (min >= max) return min;
        return min + RANDOM.nextInt(max - min + 1);
    }
}

