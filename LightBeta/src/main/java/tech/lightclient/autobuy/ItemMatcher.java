package tech.lightclient.autobuy;

import java.util.List;

/**
 * Сопоставление лота с записью списка (порт {@code a.c.a(q)} и {@code a.c.a(i,q)}).
 */
public final class ItemMatcher {
    private ItemMatcher() {
    }

    /** Полная проверка: подходит ли лот под запись. */
    public static boolean matches(ItemEntry entry, Lot lot) {
        if (entry == null || lot == null) {
            return false;
        }

        if (entry.matchAnySword) {
            if (lot.itemId == null || !lot.itemId.endsWith("_sword")) {
                return false;
            }
        } else if (entry.itemId == null || !entry.itemId.equals(lot.itemId)) {
            return false;
        }

        List<Marker> markers = entry.resolveMarkers();
        if (markers != null && !markers.isEmpty()) {
            boolean any = false;
            for (Marker m : markers) {
                if (markerMatches(m, lot)) {
                    any = true;
                    break;
                }
            }
            if (!any) {
                return false;
            }
        } else if (entry.requireCustom) {
            return false;
        }

        if (entry.minDurabilityPercent > 0 && lot.durability < entry.minDurabilityPercent) {
            return false;
        }

        if (entry.loreKeyword != null && !entry.loreKeyword.isBlank()) {
            boolean inLore = TextNormalizer.keywordIn(lot.lore, entry.loreKeyword);
            boolean inName = TextNormalizer.keywordIn(lot.name, entry.loreKeyword);
            if (!inLore && !inName) {
                return false;
            }
        }

        boolean hasMarkers = markers != null && !markers.isEmpty();
        boolean hasName = entry.displayName != null && !entry.displayName.isBlank();
        boolean noKeyword = entry.loreKeyword == null || entry.loreKeyword.isBlank();

        if (!entry.matchAnySword && hasName && !TextNormalizer.nameMatches(entry.displayName, lot.name)) {
            return false;
        }
        if (entry.matchAnySword && hasName && hasMarkers && noKeyword
                && !TextNormalizer.nameMatches(entry.displayName, lot.name)) {
            return false;
        }
        return true;
    }

    /** Сопоставление одного NBT-маркера (порт {@code c.a(i,q)}). */
    public static boolean markerMatches(Marker marker, Lot lot) {
        if (marker == null || marker.type == null || marker.key == null || lot == null) {
            return false;
        }
        switch (marker.type) {
            case PBV -> {
                String v = lot.pbv.get(marker.key);
                if (v == null) {
                    return false;
                }
                return marker.value == null || marker.value.isEmpty() || v.equals(marker.value);
            }
            case CUSTOM_ENCH -> {
                return lot.customEnch.contains(marker.key);
            }
            case CUSTOM_DATA -> {
                String v = lot.customData.get(marker.key);
                if (v == null) {
                    return false;
                }
                return marker.value == null || marker.value.isEmpty() || v.equals(marker.value);
            }
            default -> {
                return false;
            }
        }
    }
}
