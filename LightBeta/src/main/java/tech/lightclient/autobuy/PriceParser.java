package tech.lightclient.autobuy;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Парсинг лота из ItemStack: цена за единицу/итого, продавец, прочность, NBT-маркеры.
 * Дословный порт класса {@code a.r} из DimalaBuy (регулярки сохранены 1:1).
 */
public final class PriceParser {
    private static final Pattern UNIT_PRICE = Pattern.compile("Цена за 1 ед\\.?:\\s*([\\d\\s\\u00A0]+)");
    private static final Pattern TOTAL_PRICE = Pattern.compile("(?:^|\\n|\\r)Цена:\\s*([\\d\\s\\u00A0]+)");
    private static final Pattern DURABILITY = Pattern.compile("(?:Прочность|Прочка|Durability)[^\\d]*([\\d]+)\\s*%");
    private static final Pattern ITEM_ID = Pattern.compile("(minecraft|[a-z_]+):([a-z0-9_]+)");
    private static final Pattern SELLER = Pattern.compile("(?:Продавец|Seller|Игрок|Владелец)[:\\s]+([A-Za-z0-9_]{2,32})");

    private PriceParser() {
    }

    public static Lot parse(int slot, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        if (stack.getItem() == Items.BARRIER) {
            return Lot.barrier(slot);
        }

        String name = stack.getName().getString();

        StringBuilder loreBuilder = new StringBuilder();
        LoreComponent lore = stack.get(DataComponentTypes.LORE);
        if (lore != null) {
            for (Text line : lore.lines()) {
                loreBuilder.append(line.getString()).append('\n');
            }
        }
        String loreStr = loreBuilder.toString();

        long unit = parseNumber(UNIT_PRICE, loreStr);
        long total = parseNumber(TOTAL_PRICE, loreStr);

        String itemId = findItemId(loreStr);
        if (itemId == null) {
            itemId = Registries.ITEM.getId(stack.getItem()).toString();
        }

        int count = stack.getCount();
        if (unit <= 0L && total > 0L && count > 0) {
            unit = total / (long) count;
        }
        if (unit <= 0L) {
            return null;
        }

        int durability = parseDurability(stack, loreStr);

        String seller = "";
        Matcher sm = SELLER.matcher(loreStr);
        if (sm.find()) {
            seller = sm.group(1);
        }

        Map<String, String> pbv = new HashMap<>();
        Set<String> ench = new HashSet<>();
        Map<String, String> custom = new HashMap<>();
        boolean hasMarkers = extractMarkers(stack, pbv, ench, custom);

        return new Lot(slot, itemId, name, loreStr, seller, count, unit, total, durability, false,
                hasMarkers, pbv, ench, custom);
    }

    /**
     * Строит {@link Lot} из предмета инвентаря для сопоставления с записями списка
     * (без требования аукционной цены). itemId берётся из реестра.
     */
    public static Lot parseForMatch(ItemStack stack) {
        if (stack == null || stack.isEmpty() || stack.getItem() == Items.BARRIER) {
            return null;
        }
        String name = stack.getName().getString();
        StringBuilder loreBuilder = new StringBuilder();
        LoreComponent lore = stack.get(DataComponentTypes.LORE);
        if (lore != null) {
            for (Text line : lore.lines()) {
                loreBuilder.append(line.getString()).append('\n');
            }
        }
        String loreStr = loreBuilder.toString();
        String itemId = Registries.ITEM.getId(stack.getItem()).toString();
        int durability = parseDurability(stack, loreStr);

        Map<String, String> pbv = new HashMap<>();
        Set<String> ench = new HashSet<>();
        Map<String, String> custom = new HashMap<>();
        boolean hasMarkers = extractMarkers(stack, pbv, ench, custom);

        return new Lot(-1, itemId, name, loreStr, "", stack.getCount(), 0L, 0L, durability, false,
                hasMarkers, pbv, ench, custom);
    }

    private static boolean extractMarkers(ItemStack stack, Map<String, String> pbv, Set<String> ench,
                                          Map<String, String> custom) {
        boolean hasMarkers = false;

        try {
            if (stack.get(DataComponentTypes.CUSTOM_NAME) != null) {
                hasMarkers = true;
            }
        } catch (Throwable ignored) {
        }

        try {
            ItemEnchantmentsComponent enchants = stack.get(DataComponentTypes.ENCHANTMENTS);
            if (enchants != null && !enchants.isEmpty()) {
                hasMarkers = true;
            }
        } catch (Throwable ignored) {
        }

        try {
            NbtComponent nbtComp = stack.get(DataComponentTypes.CUSTOM_DATA);
            if (nbtComp == null || nbtComp.isEmpty()) {
                return hasMarkers;
            }
            hasMarkers = true;
            NbtCompound nbt = nbtComp.copyNbt();
            if (nbt == null) {
                return hasMarkers;
            }

            NbtElement pbvEl = nbt.get("PublicBukkitValues");
            if (pbvEl instanceof NbtCompound pbvNbt) {
                for (String key : pbvNbt.getKeys()) {
                    pbv.put(key, nbtToString(pbvNbt.get(key)));
                }
            }

            NbtElement enchEl = nbt.get("Enchantments");
            if (enchEl instanceof NbtList enchList) {
                for (NbtElement el : enchList) {
                    if (el instanceof NbtCompound compound) {
                        NbtElement idEl = compound.get("id");
                        if (idEl != null) {
                            String id = nbtToString(idEl);
                            if (id != null && !id.isEmpty() && !id.startsWith("minecraft:")) {
                                ench.add(id);
                            }
                        }
                    }
                }
            }

            for (String key : nbt.getKeys()) {
                if (key.equals("PublicBukkitValues") || key.equals("Enchantments")
                        || key.equals("display") || key.equals("HideFlags") || key.startsWith("VV|")) {
                    continue;
                }
                NbtElement el = nbt.get(key);
                if (el == null) {
                    continue;
                }
                if (el instanceof NbtCompound child) {
                    for (String childKey : child.getKeys()) {
                        NbtElement childEl = child.get(childKey);
                        if (childEl != null && !(childEl instanceof NbtCompound) && !(childEl instanceof NbtList)) {
                            custom.put(key + "." + childKey, nbtToString(childEl));
                        }
                    }
                } else if (!(el instanceof NbtList)) {
                    custom.put(key, nbtToString(el));
                }
            }
        } catch (Throwable ignored) {
        }

        return hasMarkers;
    }

    private static String nbtToString(NbtElement el) {
        if (el == null) {
            return "";
        }
        String s = el.toString();
        if (!(el instanceof NbtString)) {
            if (!s.isEmpty()) {
                char last = s.charAt(s.length() - 1);
                if ("bBsSlLfFdD".indexOf(last) >= 0 && s.length() > 1) {
                    String trimmed = s.substring(0, s.length() - 1);
                    if (trimmed.matches("-?\\d+(\\.\\d+)?")) {
                        return trimmed;
                    }
                }
            }
            return s;
        }
        if (s.length() >= 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
            s = s.substring(1, s.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\");
        } else if (s.length() >= 2 && s.charAt(0) == '\'' && s.charAt(s.length() - 1) == '\'') {
            s = s.substring(1, s.length() - 1).replace("\\'", "'").replace("\\\\", "\\");
        }
        return s;
    }

    private static int parseDurability(ItemStack stack, String lore) {
        Matcher m = DURABILITY.matcher(lore);
        if (m.find()) {
            try {
                return clamp(Integer.parseInt(m.group(1)));
            } catch (NumberFormatException ignored) {
            }
        }
        if (stack.isDamageable() && stack.getMaxDamage() > 0) {
            int left = stack.getMaxDamage() - stack.getDamage();
            return clamp((int) Math.round((double) left * 100.0 / (double) stack.getMaxDamage()));
        }
        return 100;
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(100, v));
    }

    private static long parseNumber(Pattern pattern, String text) {
        Matcher m = pattern.matcher(text);
        if (!m.find()) {
            return -1L;
        }
        String digits = m.group(1).replaceAll("[\\s\\u00A0]+", "");
        if (digits.isEmpty()) {
            return -1L;
        }
        try {
            return Long.parseLong(digits);
        } catch (NumberFormatException e) {
            return -1L;
        }
    }

    private static String findItemId(String text) {
        Matcher m = ITEM_ID.matcher(text);
        return m.find() ? m.group(0) : null;
    }
}
