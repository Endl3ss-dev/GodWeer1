package tech.lightclient.client.utils.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import tech.lightclient.LightClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Persistent inventory layouts. A layout contains the intended contents of all 36 player slots. */
public final class InventoryHelperStorage {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type LAYOUTS_TYPE = new TypeToken<List<Layout>>() {}.getType();
    private static final List<Layout> LAYOUTS = new ArrayList<>();
    private static boolean loaded;
    private static String selectedLayoutName;

    private InventoryHelperStorage() {
    }

    public static synchronized List<Layout> getLayouts() {
        ensureLoaded();
        return LAYOUTS.stream().map(Layout::copy).toList();
    }

    public static synchronized Layout getSelectedLayout() {
        ensureLoaded();
        if (selectedLayoutName == null) return null;
        return LAYOUTS.stream()
                .filter(layout -> layout.name.equalsIgnoreCase(selectedLayoutName))
                .findFirst()
                .map(Layout::copy)
                .orElse(null);
    }

    public static synchronized boolean select(String name) {
        ensureLoaded();
        Layout layout = find(name);
        if (layout == null) return false;
        selectedLayoutName = layout.name;
        save();
        return true;
    }

    public static synchronized Layout saveFromInventory(String rawName, List<Slot> slots) {
        ensureLoaded();
        String name = normalizeName(rawName);
        if (name == null || slots == null || slots.size() != 36) return null;

        Layout layout = new Layout(name, System.currentTimeMillis(), capture(slots));
        LAYOUTS.removeIf(existing -> existing.name.equalsIgnoreCase(name));
        LAYOUTS.add(layout);
        LAYOUTS.sort(Comparator.comparing(Layout::getName, String.CASE_INSENSITIVE_ORDER));
        selectedLayoutName = layout.name;
        save();
        return layout.copy();
    }

    public static synchronized boolean delete(String name) {
        ensureLoaded();
        Layout layout = find(name);
        if (layout == null) return false;
        boolean removed = LAYOUTS.remove(layout);
        if (removed && layout.name.equalsIgnoreCase(selectedLayoutName)) {
            selectedLayoutName = null;
        }
        if (removed) save();
        return removed;
    }

    private static List<SlotItem> capture(List<Slot> slots) {
        List<SlotItem> items = new ArrayList<>(36);
        for (Slot slot : slots) {
            ItemStack stack = slot.getStack();
            if (stack == null || stack.isEmpty()) {
                items.add(new SlotItem());
                continue;
            }
            items.add(new SlotItem(
                    Registries.ITEM.getId(stack.getItem()).toString(),
                    stack.getCount(),
                    stack.getName().getString()
            ));
        }
        return items;
    }

    private static Layout find(String rawName) {
        if (rawName == null) return null;
        return LAYOUTS.stream().filter(layout -> layout.name.equalsIgnoreCase(rawName.trim())).findFirst().orElse(null);
    }

    private static String normalizeName(String rawName) {
        if (rawName == null) return null;
        String name = rawName.trim();
        return name.isEmpty() || name.length() > 32 ? null : name;
    }

    private static void ensureLoaded() {
        if (loaded) return;
        loaded = true;
        File file = getFile();
        if (!file.exists()) return;
        try (FileReader reader = new FileReader(file)) {
            List<Layout> loadedLayouts = GSON.fromJson(reader, LAYOUTS_TYPE);
            if (loadedLayouts != null) {
                for (Layout layout : loadedLayouts) {
                    if (normalizeName(layout.name) != null && layout.items != null && layout.items.size() == 36) {
                        LAYOUTS.add(layout);
                    }
                }
                LAYOUTS.sort(Comparator.comparing(Layout::getName, String.CASE_INSENSITIVE_ORDER));
                if (!LAYOUTS.isEmpty()) {
                    selectedLayoutName = LAYOUTS.getFirst().name;
                }
            }
        } catch (IOException ignored) {
        }
    }

    private static void save() {
        File file = getFile();
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(LAYOUTS, LAYOUTS_TYPE, writer);
        } catch (IOException ignored) {
        }
    }

    private static File getFile() {
        return new File(new File(MinecraftClient.getInstance().runDirectory, LightClient.getName()), "inventory_layouts.json");
    }

    public static final class Layout {
        private String name;
        private long createdAt;
        private List<SlotItem> items;

        public Layout() {
        }

        private Layout(String name, long createdAt, List<SlotItem> items) {
            this.name = name;
            this.createdAt = createdAt;
            this.items = items;
        }

        public String getName() { return name; }
        public List<SlotItem> getItems() { return items == null ? List.of() : new ArrayList<>(items); }
        public Layout copy() { return new Layout(name, createdAt, getItems()); }
    }

    public static final class SlotItem {
        private String itemId;
        private int count;
        private String displayName;

        public SlotItem() {
        }

        private SlotItem(String itemId, int count, String displayName) {
            this.itemId = itemId;
            this.count = count;
            this.displayName = displayName;
        }

        public boolean isEmpty() { return itemId == null || itemId.isBlank(); }
        public String getItemId() { return itemId; }
        public int getCount() { return count; }
        public String getDisplayName() { return displayName; }
    }
}
