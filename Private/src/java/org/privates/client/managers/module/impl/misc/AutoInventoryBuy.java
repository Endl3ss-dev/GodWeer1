package org.privates.client.managers.module.impl.misc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.player.InventoryUtility;
import org.privates.lib.util.time.StopWatch;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Category3
@ModuleInfo(name = "AutoInventoryBuy", category = Category.MISC, description = "Автозакупка инвентаря по минимальной цене")
public class AutoInventoryBuy extends Module {
    private static final long SEARCH_WAIT_MS = 3000L;
    private static final long SEARCH_RESEND_MS = 3500L;
    private static final int MAX_REFRESH_ATTEMPTS = 5;

    private final ModeSetting serverMode = new ModeSetting(this, "Сервер", "SpookyTime", "FunTime");
    private final BindSetting openGui = new BindSetting(this, "Открыть GUI", GLFW.GLFW_KEY_O);
    private final SliderSetting delay = new SliderSetting(this, "Задержка", 500, 100, 2000, 50);
    private final SliderSetting bulkTolerance = new SliderSetting(this, "Bulk Tolerance %", 15, 0, 40, 1);
    private final BooleanSetting debugChat = new BooleanSetting(this, "Debug Chat", true);

    private final StopWatch tickWatch = new StopWatch();
    private final Gson gson = new Gson();
    private final Path dataFile = Paths.get("auto_inventory_buy.json");
    private final Pattern funTimePricePattern = Pattern.compile("\\$(\\d{1,3}(?:,? ?\\d{3})*)");

    private final List<Entry> entries = new CopyOnWriteArrayList<>();
    private final Map<String, Integer> enchantSelections = new ConcurrentHashMap<>();
    private long searchSentAt = 0L;
    private int refreshAttempts = 0;
    private Entry activeEntry;
    private String lastTargetId;
    private boolean searchSent;
    private boolean wasGuiPressed;

    public AutoInventoryBuy() {
        initEntries();
        loadData();
    }

    @Override
    protected void onDisable() {
        saveData();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        boolean guiPressed = GLFW.glfwGetKey(Minecraft.getInstance().getMainWindow().getHandle(), openGui.getValue()) == GLFW.GLFW_PRESS;
        if (guiPressed && !wasGuiPressed && mc.currentScreen == null) {
            mc.displayScreen(new AutoInventoryBuyGui(this));
        }
        wasGuiPressed = guiPressed;

        if (!isEnabled() || mc.player == null || !tickWatch.finished(delay.getValue().longValue())) {
            return;
        }

        Entry next = getNextMissingEntry();
        if (next == null) {
            activeEntry = null;
            lastTargetId = null;
            searchSent = false;
            dbg("done: no missing entries");
            return;
        }
        activeEntry = next;
        if (lastTargetId == null || !lastTargetId.equals(activeEntry.id)) {
            lastTargetId = activeEntry.id;
            searchSent = false;
            searchSentAt = 0L;
            refreshAttempts = 0;
            dbg("switch target -> " + activeEntry.id);
        }
        dbg("target: " + next.id + " inv=" + countInInventory(next) + "/" + next.targetCount + " maxUnit=" + next.maxUnitPrice);

        if (!(mc.currentScreen instanceof ChestScreen) || !isAuctionOpenedSafe()) {
            if (!searchSent || System.currentTimeMillis() - searchSentAt > SEARCH_RESEND_MS) {
                String command = "/ah search " + next.searchQuery;
                mc.player.sendChatMessage(command);
                searchSentAt = System.currentTimeMillis();
                searchSent = true;
                refreshAttempts = 0;
                dbg("send search: " + command);
            }
            tickWatch.reset();
            return;
        }

        long elapsed = System.currentTimeMillis() - searchSentAt;
        if (elapsed < SEARCH_WAIT_MS) {
            dbg("waiting results... " + elapsed + "ms");
            tickWatch.reset();
            return;
        }
        if (elapsed > SEARCH_RESEND_MS) {
            String command = "/ah search " + next.searchQuery;
            mc.player.sendChatMessage(command);
            searchSentAt = System.currentTimeMillis();
            dbg("resend search in auction: " + command);
            tickWatch.reset();
            return;
        }

        if (buyBestLot(next)) {
            searchSent = false;
            refreshAttempts = 0;
            mc.player.closeScreen();
            tickWatch.reset();
            dbg("buy flow: success");
            return;
        }

        if (mc.player.openContainer instanceof ChestContainer chest) {
            Slot updateSlot = chest.getSlot(49);
            if (updateSlot != null && updateSlot.getHasStack() && !updateSlot.getStack().isEmpty()) {
                InventoryUtility.clickSlotId(49, 0, ClickType.PICKUP, true);
                refreshAttempts++;
                dbg("refresh auction attempt #" + refreshAttempts);
            } else {
                dbg("refresh slot unavailable");
            }
        }

        if (refreshAttempts >= MAX_REFRESH_ATTEMPTS) {
            refreshAttempts = 0;
            searchSent = false;
            mc.player.closeScreen();
            dbg("max refresh reached -> close");
        }

        tickWatch.reset();
    }

    private void initEntries() {
        entries.clear();

        entries.add(new Entry("helmet_regular", "Незеритовый шлем", "незеритовый шлем", Items.NETHERITE_HELMET, 1, 0, stack -> true).setEnabled(false));
        entries.add(new Entry("chest_regular", "Незеритовый нагрудник", "незеритовый нагрудник", Items.NETHERITE_CHESTPLATE, 1, 0, stack -> true).setEnabled(false));
        entries.add(new Entry("legs_regular", "Незеритовые поножи", "незеритовые поножи", Items.NETHERITE_LEGGINGS, 1, 0, stack -> true).setEnabled(false));
        entries.add(new Entry("boots_regular", "Незеритовые ботинки", "незеритовые ботинки", Items.NETHERITE_BOOTS, 1, 0, stack -> true).setEnabled(false));

        entries.add(new Entry("helmet_crusher", "Шлем Крушителя", "шлем крушителя", Items.NETHERITE_HELMET, 1, 0, stack -> hasAtLeast(stack, Map.of(
                Enchantments.PROTECTION, 5, Enchantments.UNBREAKING, 5
        ))));
        entries.add(new Entry("chest_crusher", "Нагрудник Крушителя", "нагрудник крушителя", Items.NETHERITE_CHESTPLATE, 1, 0, stack -> hasAtLeast(stack, Map.of(
                Enchantments.PROTECTION, 5, Enchantments.UNBREAKING, 5
        ))));
        entries.add(new Entry("legs_crusher", "Поножи Крушителя", "поножи крушителя", Items.NETHERITE_LEGGINGS, 1, 0, stack -> hasAtLeast(stack, Map.of(
                Enchantments.PROTECTION, 5, Enchantments.UNBREAKING, 5
        ))));
        entries.add(new Entry("boots_crusher", "Ботинки Крушителя", "ботинки крушителя", Items.NETHERITE_BOOTS, 1, 0, stack -> hasAtLeast(stack, Map.of(
                Enchantments.PROTECTION, 5, Enchantments.UNBREAKING, 5, Enchantments.DEPTH_STRIDER, 3
        ))));

        entries.add(new Entry("sword_crusher", "Меч Крушителя", "меч крушителя", Items.NETHERITE_SWORD, 1, 0, stack -> hasAtLeast(stack, Map.of(
                Enchantments.SHARPNESS, 7, Enchantments.FIRE_ASPECT, 2, Enchantments.UNBREAKING, 5
        ))));
        entries.add(new Entry("sword_regular", "Незеритовый меч", "незеритовый меч", Items.NETHERITE_SWORD, 1, 0, stack ->
                hasAtLeastLevel(stack, Enchantments.SHARPNESS, getEnchantSelection("sword_sharpness", 5, 5, 7))
                        && hasAtLeastLevel(stack, Enchantments.FIRE_ASPECT, getEnchantSelection("sword_fire", 1, 1, 3))
                        && hasAtLeastIfPresent(stack, Enchantments.UNBREAKING, getEnchantSelection("sword_unbreaking", 3, 1, 5))
        ).setEnabled(false));

        entries.add(new Entry("pickaxe_crusher", "Кирка Крушителя", "кирка крушителя", Items.NETHERITE_PICKAXE, 1, 0, stack -> hasAtLeast(stack, Map.of(
                Enchantments.EFFICIENCY, 10, Enchantments.UNBREAKING, 5
        ))));
        entries.add(new Entry("pickaxe_regular", "Незеритовая кирка", "незеритовая кирка", Items.NETHERITE_PICKAXE, 1, 0, stack ->
                hasAtLeastLevel(stack, Enchantments.EFFICIENCY, getEnchantSelection("pickaxe_efficiency", 5, 3, 10))
                        && hasAtLeastIfPresent(stack, Enchantments.UNBREAKING, getEnchantSelection("pickaxe_unbreaking", 3, 1, 5))
        ).setEnabled(false));

        entries.add(new Entry("elytra", "Элитры (прочность >= 75%)", "элитры", Items.ELYTRA, 1, 0, this::isGoodElytra));
        entries.add(new Entry("firework", "Фейерверки", "фейерверк", Items.FIREWORK_ROCKET, 64, 0, s -> true));

        entries.add(new Entry("gapple_ench", "Зачарованное золотое яблоко", "зачарованное золотое яблоко", Items.ENCHANTED_GOLDEN_APPLE, 16, 0, s -> true));
        entries.add(new Entry("gapple", "Золотое яблоко", "золотое яблоко", Items.GOLDEN_APPLE, 64, 0, s -> true));
        entries.add(new Entry("carrot", "Золотая морковь", "золотая морковь", Items.GOLDEN_CARROT, 64, 0, s -> true));
        entries.add(new Entry("exp", "Пузырёк опыта", "пузырёк опыта", Items.EXPERIENCE_BOTTLE, 128, 0, s -> true));
        entries.add(new Entry("shulker", "Шалкер", "шалкер", Items.SHULKER_BOX, 1, 0, s -> true));
        entries.add(new Entry("drink_strength3", "Питьевое зелье силы III", "зелье силы", Items.POTION, 6, 0, stack ->
                hasPotionAmplifier(stack, 5, 2) || potionNameContains(stack, "зелье силы 3", "strength iii", "strength 3")
        ).setEnabled(false));
        entries.add(new Entry("drink_speed3", "Питьевое зелье скорости III", "зелье скорости", Items.POTION, 6, 0, stack ->
                hasPotionAmplifier(stack, 1, 2) || potionNameContains(stack, "зелье скорости 3", "speed iii", "speed 3")
        ).setEnabled(false));
        entries.add(new Entry("drink_healing", "Питьевое зелье исцеления", "зелье исцеления", Items.POTION, 6, 0, stack ->
                hasPotionAmplifier(stack, 6, 1) || potionNameContains(stack, "зелье исцеления", "healing")
        ).setEnabled(false));
        entries.add(new Entry("drink_invisibility", "Питьевое зелье невидимости", "зелье невидимости", Items.POTION, 4, 0, stack ->
                hasPotionAmplifier(stack, 14, 0) || potionNameContains(stack, "зелье невидимости", "invisibility")
        ).setEnabled(false));

        entries.add(new Entry("crossbow", "Арбалет", "арбалет", Items.CROSSBOW, 1, 0, stack ->
                hasExact(stack, Enchantments.QUICK_CHARGE, getEnchantSelection("crossbow_quick", 3, 1, 5))
                        && hasExact(stack, Enchantments.UNBREAKING, getEnchantSelection("crossbow_unbreaking", 3, 1, 5))
        ));
        entries.add(new Entry("bow", "Лук", "лук", Items.BOW, 1, 0, stack ->
                hasExact(stack, Enchantments.POWER, getEnchantSelection("bow_power", 5, 1, 5))
                        && hasExact(stack, Enchantments.FLAME, getEnchantSelection("bow_flame", 1, 1, 1))
        ));

        List<String> talismans = Arrays.asList(
                "Талисман Карателя",
                "Талисман Крушителя",
                "Талисман Раздора",
                "Талисман Тирана",
                "Талисман Ярости",
                "Талисман Вихря",
                "Талисман Мрака",
                "Талисман Демона"
        );
        for (String donor : talismans) {
            entries.add(new Entry("donor_" + donor.hashCode(), donor, donor, Items.TOTEM_OF_UNDYING, 1, 0, stack ->
                    TextFormatting.removeFormatting(stack.getDisplayName().getString()).toLowerCase().contains(donor.toLowerCase())
            ));
        }
        List<String> spheres = Arrays.asList(
                "Сфера Хаоса",
                "Сфера Бестии",
                "Сфера Преса",
                "Сфера Гидры",
                "Сфера Икара",
                "Сфера Эрида"
        );
        for (String donor : spheres) {
            entries.add(new Entry("donor_" + donor.hashCode(), donor, donor, Items.PLAYER_HEAD, 1, 0, stack ->
                    TextFormatting.removeFormatting(stack.getDisplayName().getString()).toLowerCase().contains(donor.toLowerCase())
            ));
        }
    }

    private Entry getNextMissingEntry() {
        return entries.stream().filter(e -> e.enabled).filter(e -> countInInventory(e) < e.targetCount).findFirst().orElse(null);
    }

    private boolean buyBestLot(Entry target) {
        if (!(mc.player.openContainer instanceof ChestContainer chest)) {
            dbg("buyBestLot: not chest container");
            return false;
        }
        int needCount = Math.max(0, target.targetCount - countInInventory(target));
        if (needCount <= 0) {
            dbg("buyBestLot: already enough in inventory");
            return false;
        }

        String title = mc.currentScreen != null ? TextFormatting.removeFormatting(mc.currentScreen.getTitle().getString()).toLowerCase() : "";
        if (title.contains("confirm") || title.contains("подтверж")) {
            InventoryUtility.clickSlotId(0, 0, ClickType.QUICK_MOVE, true);
            dbg("confirm window -> click slot 0");
            return true;
        }

        List<Lot> lots = new ArrayList<>();
        int scanned = 0;
        for (Slot slot : chest.inventorySlots) {
            if (slot.slotNumber > 44 || !slot.getHasStack() || slot.getStack().isEmpty()) continue;
            scanned++;
            ItemStack stack = slot.getStack();

            if (!matches(target, stack)) {
                dbg("slot " + slot.slotNumber + " skip: mismatch item=" + stack.getItem() + " name=" + TextFormatting.removeFormatting(stack.getDisplayName().getString()));
                continue;
            }

            int totalPrice = serverMode.is("SpookyTime") ? getPriceSpookyTimeSetupLike(stack) : getPriceFunTime(stack);
            if (totalPrice <= 0) {
                dbg("slot " + slot.slotNumber + " skip: no price");
                continue;
            }

            int unitPrice = totalPrice / Math.max(1, stack.getCount());
            if (target.maxUnitPrice > 0 && unitPrice > target.maxUnitPrice) {
                dbg("slot " + slot.slotNumber + " skip: unit " + unitPrice + " > max " + target.maxUnitPrice);
                continue;
            }
            if (stack.getCount() > needCount) {
                dbg("slot " + slot.slotNumber + " skip: stack " + stack.getCount() + " > need " + needCount);
                continue;
            }

            dbg("slot " + slot.slotNumber + " ok: total=" + totalPrice + " count=" + stack.getCount() + " unit=" + unitPrice);
            lots.add(new Lot(slot.slotNumber, unitPrice, stack.getCount(), totalPrice));
        }
        dbg("scan summary: scanned=" + scanned + ", valid=" + lots.size());

        int minUnit = lots.stream().mapToInt(l -> l.unitPrice).min().orElse(Integer.MAX_VALUE);
        int toleranceUnit = (int) Math.ceil(minUnit * (1.0 + (bulkTolerance.getValue().doubleValue() / 100.0)));
        List<Lot> normalized = lots.stream().filter(l -> l.unitPrice <= toleranceUnit).toList();
        dbg("normalize: minUnit=" + minUnit + ", toleranceUnit=" + toleranceUnit + ", normalized=" + normalized.size());

        Lot best = normalized.stream()
                .min(Comparator
                        .comparingInt((Lot l) -> ceilDiv(needCount, l.count))
                        .thenComparingInt(l -> l.unitPrice)
                        .thenComparingInt(l -> -l.count)
                        .thenComparingInt(l -> l.totalPrice))
                .orElse(null);
        if (best == null) {
            dbg("best lot not found");
            return false;
        }

        InventoryUtility.clickSlotId(best.slot, 0, ClickType.QUICK_MOVE, true);
        dbg("buy click slot=" + best.slot + " unit=" + best.unitPrice + " count=" + best.count);
        return true;
    }

    private boolean matches(Entry entry, ItemStack stack) {
        if (!entry.searchByNameOnly && stack.getItem() != entry.item && !matchesByName(entry, stack)) return false;
        return entry.matcher.test(stack);
    }

    private boolean matchesByName(Entry entry, ItemStack stack) {
        String stackName = cleanText(stack.getDisplayName().getString());
        String query = cleanText(entry.searchQuery);
        if (stackName.isEmpty() || query.isEmpty()) return false;

        if (stackName.contains(query)) return true;

        String[] tokens = query.split("\\s+");
        int matched = 0;
        int required = 0;
        for (String token : tokens) {
            if (token.length() < 3) continue;
            required++;
            if (stackName.contains(token)) matched++;
        }
        if (required == 0) return false;
        return matched >= Math.max(1, required - 1);
    }

    private String cleanText(String text) {
        if (text == null) return "";
        String clean = TextFormatting.removeFormatting(text);
        if (clean == null) return "";
        return clean.toLowerCase()
                .replace("ё", "е")
                .replaceAll("[^a-z\\p{IsCyrillic}0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String normalizeText(String text) {
        if (text == null) return "";
        String clean = TextFormatting.removeFormatting(text);
        if (clean == null) return "";
        return clean.toLowerCase()
                .replace('ё', 'е')
                .replaceAll("[^a-zа-я0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private int countInInventory(Entry entry) {
        int count = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && matches(entry, stack)) count += stack.getCount();
        }
        return count;
    }

    private boolean isGoodElytra(ItemStack stack) {
        if (stack.getItem() != Items.ELYTRA || !stack.isDamageable()) return false;
        int max = stack.getMaxDamage();
        int left = max - stack.getDamage();
        return (int) ((left / (float) max) * 100.0f) >= 75;
    }

    private boolean hasPotionAmplifier(ItemStack stack, int effectId, int amp) {
        ListNBT list = stack.getOrCreateTag().getList("CustomPotionEffects", 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT nbt = list.getCompound(i);
            if (nbt.getByte("Id") == (byte) effectId && nbt.getByte("Amplifier") == (byte) amp) return true;
        }
        return false;
    }

    private boolean potionNameContains(ItemStack stack, String... names) {
        String itemName = cleanText(stack.getDisplayName().getString());
        if (itemName.isEmpty()) return false;
        for (String n : names) {
            if (itemName.contains(cleanText(n))) return true;
        }
        return false;
    }

    private boolean hasExact(ItemStack stack, Enchantment ench, int value) {
        return EnchantmentHelper.getEnchantments(stack).getOrDefault(ench, 0) == value;
    }

    private boolean hasAtLeastLevel(ItemStack stack, Enchantment ench, int minValue) {
        return EnchantmentHelper.getEnchantments(stack).getOrDefault(ench, 0) >= minValue;
    }

    private boolean hasAtLeastIfPresent(ItemStack stack, Enchantment ench, int minValue) {
        int level = EnchantmentHelper.getEnchantments(stack).getOrDefault(ench, -1);
        return level < 0 || level >= minValue;
    }

    private boolean hasMendingRule(ItemStack stack, int mode) {
        int lvl = EnchantmentHelper.getEnchantments(stack).getOrDefault(Enchantments.MENDING, 0);
        return mode <= 0 || lvl >= 1;
    }

    private boolean hasDepthRule(ItemStack stack, int depth) {
        return depth <= 0 || hasExact(stack, Enchantments.DEPTH_STRIDER, depth);
    }

    private boolean hasAtLeast(ItemStack stack, Map<Enchantment, Integer> enchants) {
        Map<Enchantment, Integer> current = EnchantmentHelper.getEnchantments(stack);
        for (Map.Entry<Enchantment, Integer> e : enchants.entrySet()) {
            if (current.getOrDefault(e.getKey(), 0) < e.getValue()) return false;
        }
        return true;
    }

    public int getEnchantSelection(String key, int defaultValue, int min, int max) {
        int value = enchantSelections.getOrDefault(key, defaultValue);
        value = Math.max(min, Math.min(max, value));
        enchantSelections.put(key, value);
        return value;
    }

    public void setEnchantSelection(String key, int value, int min, int max) {
        enchantSelections.put(key, Math.max(min, Math.min(max, value)));
        saveData();
    }

    private boolean isAuctionOpenedSafe() {
        if (!(mc.currentScreen instanceof ChestScreen)) return false;
        if (mc.player != null && mc.player.openContainer instanceof ChestContainer chest) {
            Slot updateSlot = chest.getSlot(49);
            if (updateSlot != null) return true;
        }
        String title = mc.currentScreen.getTitle().getString();
        if (title == null) return true;
        String t = TextFormatting.removeFormatting(title).toLowerCase();
        return t.contains("аук") || t.contains("поиск") || t.contains("auction") || t.contains("search");
    }

    private boolean isAuctionOpened() {
        if (!(mc.currentScreen instanceof ChestScreen)) return false;
        String title = mc.currentScreen.getTitle().getString();
        if (title == null) return false;
        String t = TextFormatting.removeFormatting(title).toLowerCase();
        return t.contains("аукцион") || t.contains("поиск") || t.contains("auction") || t.contains("search");
    }

    private int getPriceSpookyTime(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("display", 10)) {
            CompoundNBT display = tag.getCompound("display");
            if (display.contains("Lore", 9)) {
                ListNBT lore = display.getList("Lore", 8);
                for (int j = 0; j < lore.size(); ++j) {
                    String cleanLine = TextFormatting.removeFormatting(lore.getString(j));
                    if (cleanLine == null) continue;
                    String lower = cleanLine.toLowerCase();
                    if (!lower.contains("цена") && !lower.contains("price") && !lower.contains("$") && !lower.contains("монет"))
                        continue;
                    String clean = cleanLine.replaceAll("[^0-9]", "");
                    if (!clean.isEmpty()) return Integer.parseInt(clean);
                }
            }
        }
        return -1;
    }

    private int getPriceSpookyTimeSetupLike(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag == null || !tag.contains("display", 10)) {
            return -1;
        }
        CompoundNBT display = tag.getCompound("display");
        if (!display.contains("Lore", 9)) {
            return -1;
        }

        ListNBT lore = display.getList("Lore", 8);
        for (int j = 0; j < lore.size(); ++j) {
            String loreLine = lore.getString(j);
            try {
                com.google.gson.JsonObject object = new com.google.gson.JsonParser().parse(loreLine).getAsJsonObject();
                if (object.has("extra") && object.getAsJsonArray("extra").size() > 2) {
                    com.google.gson.JsonArray array = object.getAsJsonArray("extra");
                    com.google.gson.JsonObject title = array.get(1).getAsJsonObject();
                    if (title.has("text") && title.get("text").getAsString().trim().toLowerCase().contains("цена")) {
                        String line = array.get(2).getAsJsonObject().get("text").getAsString().trim().substring(1).replaceAll("[ ,]", "");
                        if (!line.isEmpty()) {
                            return Integer.parseInt(line);
                        }
                    }
                }
            } catch (Exception ignored) {
                String cleanLine = TextFormatting.removeFormatting(loreLine);
                if (cleanLine == null) {
                    continue;
                }
                String clean = cleanLine.replaceAll("[^0-9]", "");
                if (!clean.isEmpty()) {
                    return Integer.parseInt(clean);
                }
            }
        }
        return -1;
    }

    private int getPriceFunTime(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("display", 10)) {
            CompoundNBT display = tag.getCompound("display");
            if (display.contains("Lore", 9)) {
                ListNBT lore = display.getList("Lore", 8);
                for (int j = 0; j < lore.size(); ++j) {
                    String loreLine = TextFormatting.removeFormatting(lore.getString(j));
                    if (loreLine == null) continue;

                    Matcher matcher = funTimePricePattern.matcher(loreLine);
                    if (matcher.find()) {
                        String priceString = matcher.group(1).replaceAll(",", "").replaceAll(" ", "");
                        if (!priceString.isEmpty()) return Integer.parseInt(priceString);
                    }

                    String lower = loreLine.toLowerCase();
                    if (lower.contains("цена") || lower.contains("price") || lower.contains("монет")) {
                        String clean = loreLine.replaceAll("[^0-9]", "");
                        if (!clean.isEmpty()) return Integer.parseInt(clean);
                    }
                }
            }
        }
        return -1;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void requestPriceScan(Entry entry) {
        if (mc.player != null) {
            String cmd = "/ah search " + entry.searchQuery;
            mc.player.sendChatMessage(cmd);
            searchSent = true;
            searchSentAt = System.currentTimeMillis();
            activeEntry = entry;
            dbg("manual search: " + cmd);
        }
    }

    private void dbg(String message) {
        if (!debugChat.getValue()) return;
        ChatUtil.addText("§7[§dAutoInventoryBuy§7] §f" + message);
    }

    private int ceilDiv(int a, int b) {
        if (b <= 0) return Integer.MAX_VALUE;
        return (a + b - 1) / b;
    }

    public void saveData() {
        try {
            List<EntryState> out = new ArrayList<>();
            for (Entry e : entries) out.add(new EntryState(e.id, e.enabled, e.targetCount, e.maxUnitPrice));
            PersistedData persisted = new PersistedData();
            persisted.entries = out;
            persisted.enchantSelections = new HashMap<>(enchantSelections);
            Files.write(dataFile, gson.toJson(persisted).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ignored) {
        }
    }

    private void loadData() {
        try {
            if (!Files.exists(dataFile)) return;
            String json = new String(Files.readAllBytes(dataFile));
            if (json.trim().isEmpty()) return;

            List<EntryState> in = null;
            try {
                PersistedData persisted = gson.fromJson(json, PersistedData.class);
                if (persisted != null) {
                    in = persisted.entries;
                    if (persisted.enchantSelections != null) enchantSelections.putAll(persisted.enchantSelections);
                }
            } catch (Exception ignored) {
            }
            if (in == null) {
                Type oldType = new TypeToken<List<EntryState>>() {
                }.getType();
                in = gson.fromJson(json, oldType);
            }
            if (in == null) return;
            for (EntryState state : in) {
                Entry entry = entries.stream().filter(e -> e.id.equals(state.id)).findFirst().orElse(null);
                if (entry != null) {
                    entry.enabled = state.enabled;
                    entry.targetCount = Math.max(0, state.targetCount);
                    entry.maxUnitPrice = Math.max(0, state.maxUnitPrice);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static class Entry {
        public final String id;
        public final String name;
        public final String searchQuery;
        public final Item item;
        public final Predicate<ItemStack> matcher;
        public int targetCount;
        public int maxUnitPrice;
        public boolean enabled = true;
        public boolean searchByNameOnly;

        public Entry(String id, String name, String searchQuery, Item item, int targetCount, int maxUnitPrice, Predicate<ItemStack> matcher) {
            this.id = id;
            this.name = name;
            this.searchQuery = searchQuery;
            this.item = item;
            this.targetCount = targetCount;
            this.maxUnitPrice = maxUnitPrice;
            this.matcher = matcher;
        }

        public Entry setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Entry setSearchByNameOnly(boolean searchByNameOnly) {
            this.searchByNameOnly = searchByNameOnly;
            return this;
        }
    }

    private static class EntryState {
        String id;
        boolean enabled;
        int targetCount;
        int maxUnitPrice;

        public EntryState(String id, boolean enabled, int targetCount, int maxUnitPrice) {
            this.id = id;
            this.enabled = enabled;
            this.targetCount = targetCount;
            this.maxUnitPrice = maxUnitPrice;
        }
    }

    private static class PersistedData {
        List<EntryState> entries;
        Map<String, Integer> enchantSelections;
    }

    private record Lot(int slot, int unitPrice, int count, int totalPrice) {
    }
}
