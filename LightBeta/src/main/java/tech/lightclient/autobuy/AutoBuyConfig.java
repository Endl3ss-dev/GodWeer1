package tech.lightclient.autobuy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tech.lightclient.LightClient;

import java.io.File;
import net.minecraft.client.MinecraftClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AutoBuyConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static AutoBuyConfig INSTANCE = new AutoBuyConfig();

    // ----- глобальные флаги -----
    public boolean enabled = false;           // ВЫКЛ - мастер тумблер (только покупки)
    public int clickIntervalTicks = 1;
    public boolean autoRefresh = true;
    public int refreshIntervalTicks = -1;
    public int refreshSlot = -1;
    public boolean minPriceEnabled = true;
    public long minPrice = 15000L;
    public boolean autoSell = false;          // ВЫКЛ
    public int sellDiscountPercent = 5;
    public int relistDiscountPercent = 8;
    public boolean autoRelist = false;        // ВЫКЛ
    public int relistIntervalSeconds = 180;
    /** Авто-парсинг цен: работает НЕЗАВИСИМО от мастер-тумблера enabled */
    public boolean autoParse = true;          // ВКЛ по умолчанию, работает всегда
    public int parseIntervalSeconds = 600;
    public boolean autoPay = false;           // ВЫКЛ
    public long autoPayKeepBalance = 5_000_000L;
    public String autoPayNick = "";
    public int autoPayIntervalSeconds = 300;
    public int parsePercent = 80;
    public long defaultPriceIfMissing = 50_000_000L;

    // ----- Кривая времени (основная) -----
    public float delayPoint0 = 0.10f;
    public float delayPoint1 = 0.14f;
    public float delayPoint2 = 0.30f;
    public float delayPoint3 = 0.28f;

    // ----- Кривая рандомизации обновления (новая) -----
    public float refreshRandomizationPoint0 = 0.0f;
    public float refreshRandomizationPoint1 = 0.33f;
    public float refreshRandomizationPoint2 = 0.66f;
    public float refreshRandomizationPoint3 = 1.0f;

    // ----- данные -----
    public List<ItemEntry> entries = new ArrayList<>();
    public List<String> banList = new ArrayList<>();
    public List<PurchaseRecord> history = new ArrayList<>();
    public int historyMaxSize = 500;

    public static AutoBuyConfig cfg() {
        return INSTANCE;
    }

    public ItemEntry find(String itemId) {
        if (itemId == null) return null;
        for (ItemEntry e : entries) {
            if (e.enabled && e.maxUnitPrice > 0L && e.matches(itemId)) return e;
        }
        return null;
    }

    public boolean ignored(String nick) {
        if (nick == null || nick.isBlank()) return false;
        for (String s : banList) {
            if (s != null && s.equalsIgnoreCase(nick)) return true;
        }
        return false;
    }

    public void addBan(String nick) {
        if (nick == null) return;
        String trimmed = nick.trim();
        if (trimmed.isEmpty() || ignored(trimmed)) return;
        banList.add(trimmed);
        save();
    }

    public void removeBan(String nick) {
        banList.removeIf(s -> s != null && s.equalsIgnoreCase(nick));
        save();
    }

    public void addHistory(PurchaseRecord record) {
        if (record == null) return;
        history.add(0, record);
        while (history.size() > Math.max(1, historyMaxSize)) {
            history.remove(history.size() - 1);
        }
        save();
    }

    public void resetToDefaults() {
        enabled = false;
        autoSell = false;
        autoRelist = false;
        autoParse = true;    // Парсер всегда включен
        autoPay = false;
        resetDelayCurveDefaults();
        resetRefreshRandomizationDefaults();
        save();
    }

    public void resetDelayCurveDefaults() {
        delayPoint0 = 0.10f;
        delayPoint1 = 0.14f;
        delayPoint2 = 0.30f;
        delayPoint3 = 0.28f;
    }

    public void resetRefreshRandomizationDefaults() {
        refreshRandomizationPoint0 = 0.0f;
        refreshRandomizationPoint1 = 0.33f;
        refreshRandomizationPoint2 = 0.66f;
        refreshRandomizationPoint3 = 1.0f;
    }

    public void clampDelayCurve() {
        delayPoint0 = clamp01(delayPoint0);
        delayPoint1 = clamp01(delayPoint1);
        delayPoint2 = clamp01(delayPoint2);
        delayPoint3 = clamp01(delayPoint3);
    }

    public void clampRefreshRandomization() {
        refreshRandomizationPoint0 = clamp01(refreshRandomizationPoint0);
        refreshRandomizationPoint1 = clamp01(refreshRandomizationPoint1);
        refreshRandomizationPoint2 = clamp01(refreshRandomizationPoint2);
        refreshRandomizationPoint3 = clamp01(refreshRandomizationPoint3);
    }

    // ----- Методы для основной кривой времени -----

    public float getDelayPoint(int index) {
        return switch (index) {
            case 0 -> delayPoint0;
            case 1 -> delayPoint1;
            case 2 -> delayPoint2;
            default -> delayPoint3;
        };
    }

    public void setDelayPoint(int index, float value) {
        float clamped = clamp01(value);
        switch (index) {
            case 0 -> delayPoint0 = clamped;
            case 1 -> delayPoint1 = clamped;
            case 2 -> delayPoint2 = clamped;
            default -> delayPoint3 = clamped;
        }
    }

    public float sampleDelayCurve(float progress) {
        clampDelayCurve();
        float t = clamp01(progress);
        float scaled = t * 3.0f;
        int segment = Math.min(2, (int) Math.floor(scaled));
        float local = scaled - segment;
        float a = getDelayPoint(segment);
        float b = getDelayPoint(segment + 1);
        return a + (b - a) * local;
    }

    public int delayMsForStage(float progress, int fastMs, int slowMs) {
        return Math.round(fastMs + (slowMs - fastMs) * sampleDelayCurve(progress));
    }

    // ----- Методы для кривой рандомизации обновления (новая) -----

    public float getRefreshRandomizationPoint(int index) {
        return switch (index) {
            case 0 -> refreshRandomizationPoint0;
            case 1 -> refreshRandomizationPoint1;
            case 2 -> refreshRandomizationPoint2;
            default -> refreshRandomizationPoint3;
        };
    }

    public void setRefreshRandomizationPoint(int index, float value) {
        float clamped = clamp01(value);
        switch (index) {
            case 0 -> refreshRandomizationPoint0 = clamped;
            case 1 -> refreshRandomizationPoint1 = clamped;
            case 2 -> refreshRandomizationPoint2 = clamped;
            default -> refreshRandomizationPoint3 = clamped;
        }
        save();
    }

    /**
     * Получает задержку обновления в миллисекундах на основе графика рандомизации
     * @param progress случайное значение от 0 до 1
     * @return задержка в мс от 50 до 1000
     */
    public int getRefreshDelayMs(float progress) {
        clampRefreshRandomization();
        float t = clamp01(progress);
        float scaled = t * 3.0f;
        int segment = Math.min(2, (int) Math.floor(scaled));
        float local = scaled - segment;
        float a = getRefreshRandomizationPoint(segment);
        float b = getRefreshRandomizationPoint(segment + 1);
        float value = a + (b - a) * local;

        // Преобразуем в мс: от 50ms до 1000ms
        return 50 + (int)(value * 950);
    }

    private static Path path() {
        File dir = new File(MinecraftClient.getInstance().runDirectory, "light/autobuy");
        return new File(dir, "autobuy.json").toPath();
    }

    public static void save() {
        try {
            Path p = path();
            Files.createDirectories(p.getParent());
            Files.writeString(p, GSON.toJson(INSTANCE));
        } catch (Exception e) {
            System.err.println("[AutoBuy] save failed: " + e);
        }
    }

    public static void load() {
        try {
            Path p = path();
            if (Files.exists(p)) {
                String json = Files.readString(p);
                AutoBuyConfig loaded = GSON.fromJson(json, AutoBuyConfig.class);
                if (loaded != null) {
                    INSTANCE = loaded;
                    if (INSTANCE.entries == null) INSTANCE.entries = new ArrayList<>();
                    if (INSTANCE.banList == null) INSTANCE.banList = new ArrayList<>();
                    if (INSTANCE.history == null) INSTANCE.history = new ArrayList<>();
                    if (INSTANCE.autoPayNick == null) INSTANCE.autoPayNick = "";
                    INSTANCE.clampDelayCurve();
                    INSTANCE.clampRefreshRandomization();

                    // ВСЕГДА выключаем покупки при загрузке, но парсер оставляем
                    INSTANCE.enabled = false;
                    INSTANCE.autoSell = false;
                    INSTANCE.autoRelist = false;
                    INSTANCE.autoPay = false;
                    // autoParse не трогаем - пусть работает всегда
                }
            }
        } catch (Exception e) {
            System.err.println("[AutoBuy] load failed: " + e);
        }
        INSTANCE.reconcileWithDatabase();
        save();
    }

    public void reconcileWithDatabase() {
        boolean seed = entries.isEmpty();
        for (ItemEntry db : ItemDatabase.LIST) {
            ItemEntry existing = findByIdentity(db);
            if (existing == null) {
                if (!seed) continue;
                ItemEntry copy = new ItemEntry();
                copy.itemId = db.itemId;
                copy.displayName = db.displayName;
                copy.searchQuery = db.searchQuery;
                copy.loreKeyword = db.loreKeyword;
                copy.requireCustom = db.requireCustom;
                copy.matchAnySword = db.matchAnySword;
                copy.markers = new ArrayList<>(db.markers);
                copy.enabled = false;
                copy.maxUnitPrice = 0L;
                entries.add(copy);
            } else {
                existing.requireCustom = db.requireCustom;
                existing.matchAnySword = db.matchAnySword;
                if (existing.loreKeyword == null) existing.loreKeyword = db.loreKeyword;
                if (existing.searchQuery == null) existing.searchQuery = db.searchQuery;
                if (existing.markers == null || existing.markers.isEmpty()) {
                    existing.markers = new ArrayList<>(db.markers);
                }
            }
        }
    }

    private ItemEntry findByIdentity(ItemEntry db) {
        for (ItemEntry e : entries) {
            boolean sameId = e.itemId != null && e.itemId.equals(db.itemId);
            boolean sameName = norm(e.displayName).equals(norm(db.displayName));
            boolean sameLore = norm(e.loreKeyword).equals(norm(db.loreKeyword));
            if (sameId && sameName && sameLore && e.matchAnySword == db.matchAnySword) return e;
        }
        return null;
    }

    private static String norm(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
    }

    private static float clamp01(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }
}