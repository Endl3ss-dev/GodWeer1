package tech.lightclient.autobuy;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Одна запись в списке автобая (предмет + цена + правила сопоставления).
 * Порт класса {@code a.c} из DimalaBuy с добавленным флагом {@link #enabled}
 * (в оригинале предмет «включался» ненулевой ценой; здесь — явный тумблер).
 */
public class ItemEntry {
    public String itemId;
    public String displayName;
    public String searchQuery;
    public String loreKeyword;
    public long maxUnitPrice;
    /** Полная рыночная цена за 1 шт (минимум рынка), найденная при последнем парсинге. Основа для автосела. */
    public long marketPrice;
    public long minUnitPrice;
    public int minDurabilityPercent;
    public boolean isArmor;
    public boolean matchAnySword;
    public boolean requireCustom;
    /** Явный тумблер вкл/выкл для предмета (новое относительно оригинала). */
    public boolean enabled = true;
    public List<Marker> markers = new ArrayList<>();

    public ItemEntry() {
    }

    public ItemEntry(String itemId, long maxUnitPrice) {
        this.itemId = itemId;
        this.maxUnitPrice = maxUnitPrice;
    }

    public ItemEntry(String itemId, String displayName, long maxUnitPrice) {
        this.itemId = itemId;
        this.displayName = displayName;
        this.maxUnitPrice = maxUnitPrice;
    }

    /** Грубое сопоставление по id (для отладки/поиска). */
    public boolean matches(String otherItemId) {
        if (otherItemId == null) {
            return false;
        }
        if (matchAnySword) {
            return otherItemId.endsWith("_sword");
        }
        return otherItemId.equals(itemId);
    }

    /** Человекочитаемая подпись строки (порт {@code c.a()}). */
    public String label() {
        String s = (displayName != null && !displayName.isBlank()) ? displayName : itemId;
        if (loreKeyword != null && !loreKeyword.isBlank()) {
            s = s + " [" + loreKeyword + "]";
        }
        if (matchAnySword) {
            s = "Меч+" + s;
        }
        return s;
    }

    /** Запрос для /ah search (порт {@code c.b()}). */
    public String query() {
        if (searchQuery != null && !searchQuery.isBlank()) {
            return searchQuery;
        }
        return (displayName != null && !displayName.isBlank()) ? displayName : itemId;
    }

    /**
     * Маркеры этой записи; если их нет — пытается унаследовать из базы (порт {@code c.c()}).
     */
    public List<Marker> resolveMarkers() {
        if (markers != null && !markers.isEmpty()) {
            return markers;
        }
        String dn = displayName == null ? "" : displayName.trim().toLowerCase(Locale.ROOT);
        String sq = searchQuery == null ? "" : searchQuery.trim().toLowerCase(Locale.ROOT);

        for (ItemEntry db : ItemDatabase.LIST) {
            if (db.markers == null || db.markers.isEmpty()) {
                continue;
            }
            String dbName = db.displayName == null ? "" : db.displayName.toLowerCase(Locale.ROOT);
            String dbQuery = db.searchQuery == null ? "" : db.searchQuery.toLowerCase(Locale.ROOT);
            boolean idOk = (db.matchAnySword || (db.itemId != null && db.itemId.equals(this.itemId)))
                    && (!db.matchAnySword || (this.itemId != null && this.itemId.endsWith("_sword")));
            if (idOk) {
                if (!dn.isEmpty() && dn.equals(dbName)) {
                    return db.markers;
                }
                if (!sq.isEmpty() && sq.equals(dbQuery)) {
                    return db.markers;
                }
            }
        }
        return markers != null ? markers : new ArrayList<>();
    }
}
