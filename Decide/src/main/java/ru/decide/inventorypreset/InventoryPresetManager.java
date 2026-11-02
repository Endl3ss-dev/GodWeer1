/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_124
 *  net.minecraft.class_1304
 *  net.minecraft.class_1657
 *  net.minecraft.class_1713
 *  net.minecraft.class_1735
 *  net.minecraft.class_1799
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_490
 *  net.minecraft.class_7923
 */
package ru.decide.inventorypreset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_124;
import net.minecraft.class_1304;
import net.minecraft.class_1657;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_490;
import net.minecraft.class_7923;
import ru.decide.Client;
import ru.decide.inventorypreset.InventoryPreset;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.event_impl.WorldLoadEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.utils.math.ChatUtils;

@Environment(value=EnvType.CLIENT)
public final class InventoryPresetManager {
    private static final Path DIRECTORY = Path.of("C:/decide/client1_21_11/inventory-presets", new String[0]);
    private static final Path CURRENT_FILE = DIRECTORY.resolve("current.txt");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final class_1304[] ARMOR = new class_1304[]{class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166};
    private static final Pattern DECORATIVE_BRACKETS = Pattern.compile("^\\s*[\\[({<\u3010\u3016].*?[\\])}>\u3011\u3017]\\s*");
    private static final Pattern EDGE_DECORATION = Pattern.compile("^[^\\p{L}\\p{N}]+|[^\\p{L}\\p{N}]+$");
    private final class_310 mc = class_310.method_1551();
    private final Map<String, InventoryPreset> presets = new LinkedHashMap<String, InventoryPreset>();
    private final ArrayDeque<ClickStep> clickSteps = new ArrayDeque();
    private final Random random = new Random();
    private String activeName;
    private boolean arranging;
    private int arrangeSyncId = -1;
    private int recoverySlot = -1;
    private int missingScroll;
    private int noMovePasses;
    private long nextClickAt;

    public void init() {
        this.loadAll();
        Client.eventHandler().subscribe(this);
    }

    public List<InventoryPreset> presets() {
        return this.presets.values().stream().sorted(Comparator.comparingLong(InventoryPreset::createdAt).reversed()).toList();
    }

    public Optional<InventoryPreset> activePreset() {
        return Optional.ofNullable(this.activeName == null ? null : this.presets.get(InventoryPresetManager.key(this.activeName)));
    }

    public String activeName() {
        return this.activeName;
    }

    public boolean isArranging() {
        return this.arranging;
    }

    public int missingScroll(int total, int visible) {
        this.missingScroll = Math.max(0, Math.min(this.missingScroll, Math.max(0, total - visible)));
        return this.missingScroll;
    }

    public void scrollMissing(int direction, int total, int visible) {
        this.missingScroll = Math.max(0, Math.min(Math.max(0, total - visible), this.missingScroll + direction));
    }

    public InventoryPreset saveCurrent(String requestedName) {
        if (this.mc.field_1724 == null) {
            return null;
        }
        String name = InventoryPresetManager.normalizeName(requestedName);
        if (name.isBlank()) {
            return null;
        }
        ArrayList<InventoryPreset.Entry> slots = new ArrayList<InventoryPreset.Entry>(41);
        for (int i = 0; i < 36; ++i) {
            slots.add(InventoryPreset.Entry.fromStack(this.mc.field_1724.method_31548().method_5438(i)));
        }
        for (class_1304 equipmentSlot : ARMOR) {
            slots.add(InventoryPreset.Entry.fromStack(this.mc.field_1724.method_6118(equipmentSlot)));
        }
        slots.add(InventoryPreset.Entry.fromStack(this.mc.field_1724.method_6079()));
        InventoryPreset preset = new InventoryPreset(name, System.currentTimeMillis(), slots);
        this.presets.put(InventoryPresetManager.key(name), preset);
        this.save(preset);
        return preset;
    }

    public void activate(InventoryPreset preset) {
        if (preset == null) {
            return;
        }
        this.cancelArrange(false);
        this.activeName = preset.name();
        this.missingScroll = 0;
        this.saveCurrentName();
    }

    public void deactivate() {
        this.cancelArrange(false);
        this.activeName = null;
        this.missingScroll = 0;
        this.saveCurrentName();
    }

    public void delete(InventoryPreset preset) {
        if (preset == null) {
            return;
        }
        this.presets.remove(InventoryPresetManager.key(preset.name()));
        if (this.activeName != null && this.activeName.equalsIgnoreCase(preset.name())) {
            this.deactivate();
        }
        try {
            Files.deleteIfExists(this.fileFor(preset.name()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class_1799 currentStack(int logicalSlot) {
        if (this.mc.field_1724 == null) {
            return class_1799.field_8037;
        }
        if (logicalSlot >= 0 && logicalSlot < 36) {
            return this.mc.field_1724.method_31548().method_5438(logicalSlot);
        }
        if (logicalSlot >= 36 && logicalSlot < 40) {
            return this.mc.field_1724.method_6118(ARMOR[logicalSlot - 36]);
        }
        return logicalSlot == 40 ? this.mc.field_1724.method_6079() : class_1799.field_8037;
    }

    public SlotState stateFor(int logicalSlot) {
        InventoryPreset preset = this.activePreset().orElse(null);
        if (preset == null) {
            return SlotState.NONE;
        }
        InventoryPreset.Entry expected = preset.slot(logicalSlot);
        if (expected.isEmpty()) {
            return SlotState.CORRECT;
        }
        if (expected.matches(this.currentStack(logicalSlot))) {
            return SlotState.CORRECT;
        }
        return this.missingCounts(preset).containsKey(expected.matchKey()) ? SlotState.MISSING : SlotState.WRONG_SLOT;
    }

    public Map<String, MissingItem> missingItems() {
        InventoryPreset preset = this.activePreset().orElse(null);
        if (preset == null) {
            return Map.of();
        }
        Map<String, Integer> counts = this.missingCounts(preset);
        LinkedHashMap<String, MissingItem> result = new LinkedHashMap<String, MissingItem>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            InventoryPreset.Entry source = preset.slots().stream().filter(slot -> slot.matchKey().equals(entry.getKey())).findFirst().orElse(InventoryPreset.Entry.EMPTY);
            result.put(entry.getKey(), new MissingItem(source, entry.getValue()));
        }
        return result;
    }

    public boolean isComplete() {
        InventoryPreset preset = this.activePreset().orElse(null);
        if (preset == null || this.mc.field_1724 == null) {
            return false;
        }
        for (int i = 0; i < 41; ++i) {
            InventoryPreset.Entry expected = preset.slot(i);
            if (expected.isEmpty() || expected.matches(this.currentStack(i))) continue;
            return false;
        }
        return true;
    }

    public boolean startArrange() {
        if (this.activePreset().isEmpty() || this.mc.field_1724 == null || !(this.mc.field_1755 instanceof class_490)) {
            return false;
        }
        if (!this.mc.field_1724.field_7512.method_34255().method_7960()) {
            ChatUtils.addChatMessage("\u0421\u043d\u0430\u0447\u0430\u043b\u0430 \u043f\u043e\u043b\u043e\u0436\u0438\u0442\u0435 \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u0441 \u043a\u0443\u0440\u0441\u043e\u0440\u0430 \u0432 \u0441\u043b\u043e\u0442");
            return false;
        }
        this.clickSteps.clear();
        this.arranging = true;
        this.arrangeSyncId = this.mc.field_1724.field_7512.field_7763;
        this.recoverySlot = -1;
        this.noMovePasses = 0;
        this.nextClickAt = 0L;
        return true;
    }

    public void cancelArrange(boolean notify) {
        if (this.arranging && notify) {
            ChatUtils.addChatMessage("\u0420\u0430\u0441\u043a\u043b\u0430\u0434\u043a\u0430 \u043f\u0440\u0435\u0441\u0435\u0442\u0430 \u043e\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0430");
        }
        this.recoverCursor();
        this.arranging = false;
        this.clickSteps.clear();
        this.arrangeSyncId = -1;
        this.recoverySlot = -1;
        this.noMovePasses = 0;
        this.nextClickAt = 0L;
    }

    public void searchAuction(MissingItem missing) {
        if (this.mc.field_1724 == null || missing == null) {
            return;
        }
        String query = InventoryPresetManager.cleanSearchName(missing.entry().displayName());
        if (query.isBlank()) {
            class_1799 fallback = missing.entry().toStack();
            String string = query = fallback.method_7960() ? missing.entry().itemId() : fallback.method_7964().getString();
        }
        if (!(query = InventoryPresetManager.cleanSearchName(query)).isBlank()) {
            this.mc.field_1724.field_3944.method_45730("ah search " + query);
        }
    }

    public static String cleanSearchName(String source) {
        if (source == null) {
            return "";
        }
        String value = class_124.method_539((String)source);
        if (value == null) {
            return "";
        }
        value = DECORATIVE_BRACKETS.matcher(value).replaceFirst("");
        String[] words = (value = value.replaceAll("\\s+", " ").trim()).split(" ");
        if (words.length >= 3 && words[0].equalsIgnoreCase(words[words.length - 1])) {
            value = String.join((CharSequence)" ", Arrays.copyOfRange(words, 1, words.length - 1));
        }
        value = EDGE_DECORATION.matcher(value.trim()).replaceAll("").trim();
        return value.replaceAll("\\s+", " ");
    }

    public static int logicalToScreenSlot(int logical) {
        if (logical >= 0 && logical < 9) {
            return 36 + logical;
        }
        if (logical >= 9 && logical < 36) {
            return logical;
        }
        if (logical >= 36 && logical < 40) {
            return 5 + (logical - 36);
        }
        return logical == 40 ? 45 : -1;
    }

    public static int screenToLogicalSlot(int screenSlot) {
        if (screenSlot >= 36 && screenSlot <= 44) {
            return screenSlot - 36;
        }
        if (screenSlot >= 9 && screenSlot <= 35) {
            return screenSlot;
        }
        if (screenSlot >= 5 && screenSlot <= 8) {
            return 36 + (screenSlot - 5);
        }
        return screenSlot == 45 ? 40 : -1;
    }

    @EventHandler
    public void onTick(EventTick event) {
        if (this.activePreset().isPresent() && !this.arranging && this.mc.field_1724 != null && this.mc.field_1724.field_7512.method_34255().method_7960() && this.isComplete()) {
            this.activeName = null;
            this.saveCurrentName();
        }
        if (!this.arranging) {
            return;
        }
        if (this.mc.field_1724 == null || this.mc.field_1761 == null || !(this.mc.field_1755 instanceof class_490) || this.mc.field_1724.field_7512.field_7763 != this.arrangeSyncId) {
            this.cancelArrange(true);
            return;
        }
        if (System.currentTimeMillis() < this.nextClickAt) {
            return;
        }
        if (this.clickSteps.isEmpty() && !this.mc.field_1724.field_7512.method_34255().method_7960()) {
            if (!this.queueCursorRecovery()) {
                this.nextClickAt = System.currentTimeMillis() + 150L;
            }
            return;
        }
        if (this.clickSteps.isEmpty() && !this.planNextMove()) {
            if (this.isComplete()) {
                this.arranging = false;
                ChatUtils.addChatMessage("\u041f\u0440\u0435\u0441\u0435\u0442 \u043f\u043e\u043b\u043d\u043e\u0441\u0442\u044c\u044e \u0440\u0430\u0437\u043b\u043e\u0436\u0435\u043d");
                this.deactivate();
                return;
            }
            if (++this.noMovePasses >= 20) {
                this.arranging = false;
                ChatUtils.addChatMessage("\u041d\u0435\u0442 \u0431\u0435\u0437\u043e\u043f\u0430\u0441\u043d\u043e\u0433\u043e \u043f\u0435\u0440\u0435\u043c\u0435\u0449\u0435\u043d\u0438\u044f: \u043f\u0440\u043e\u0432\u0435\u0440\u044c\u0442\u0435 \u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432 \u0438 \u0441\u0432\u043e\u0431\u043e\u0434\u043d\u044b\u0439 \u0441\u043b\u043e\u0442");
            } else {
                this.nextClickAt = System.currentTimeMillis() + 100L;
            }
            return;
        }
        ClickStep step = this.clickSteps.peekFirst();
        if (step == null) {
            return;
        }
        if (step.slot() < 0 || step.slot() >= this.mc.field_1724.field_7512.field_7761.size()) {
            this.clickSteps.clear();
            this.nextClickAt = System.currentTimeMillis() + 150L;
            return;
        }
        if (!step.matches(this.mc.field_1724.field_7512.method_34255(), this.mc.field_1724.field_7512.method_7611(step.slot()).method_7677(), step.actionType() == class_1713.field_7791 ? this.mc.field_1724.field_7512.method_7611(InventoryPresetManager.logicalToScreenSlot(step.button())).method_7677() : class_1799.field_8037)) {
            this.clickSteps.clear();
            if (!this.mc.field_1724.field_7512.method_34255().method_7960()) {
                this.queueCursorRecovery();
            }
            this.nextClickAt = System.currentTimeMillis() + 150L;
            return;
        }
        this.clickSteps.pollFirst();
        this.mc.field_1761.method_2906(this.mc.field_1724.field_7512.field_7763, step.slot(), step.button(), step.actionType(), (class_1657)this.mc.field_1724);
        this.noMovePasses = 0;
        long baseDelay = step.actionType() == class_1713.field_7791 || step.button() == 1 ? 35L : 65L;
        this.nextClickAt = System.currentTimeMillis() + baseDelay + (long)this.random.nextInt(31);
        if (this.clickSteps.isEmpty() && this.mc.field_1724.field_7512.method_34255().method_7960()) {
            this.recoverySlot = -1;
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        this.cancelArrange(false);
    }

    private boolean planNextMove() {
        int target;
        InventoryPreset preset = this.activePreset().orElse(null);
        if (preset == null) {
            return false;
        }
        for (target = 36; target < 40; ++target) {
            if (!this.planTargetIfNeeded(preset, target)) continue;
            return true;
        }
        if (this.planTargetIfNeeded(preset, 40)) {
            return true;
        }
        for (target = 0; target < 9; ++target) {
            if (!this.planTargetIfNeeded(preset, target)) continue;
            return true;
        }
        for (target = 9; target < 36; ++target) {
            if (!this.planTargetIfNeeded(preset, target)) continue;
            return true;
        }
        return false;
    }

    private boolean planTargetIfNeeded(InventoryPreset preset, int target) {
        InventoryPreset.Entry wanted = preset.slot(target);
        class_1799 targetStack = this.currentStack(target);
        return !wanted.isEmpty() && !wanted.matches(targetStack) && this.planTargetMove(preset, wanted, target, targetStack);
    }

    private boolean planTargetMove(InventoryPreset preset, InventoryPreset.Entry wanted, int target, class_1799 targetStack) {
        int targetScreen = InventoryPresetManager.logicalToScreenSlot(target);
        if (targetScreen < 0) {
            return false;
        }
        if (wanted.sameItem(targetStack)) {
            int source = this.findSource(preset, wanted, target, false);
            if (source < 0) {
                return false;
            }
            class_1799 sourceStack = this.currentStack(source);
            int sourceScreen = InventoryPresetManager.logicalToScreenSlot(source);
            if (sourceScreen < 0) {
                return false;
            }
            if (!class_1799.method_31577((class_1799)sourceStack, (class_1799)targetStack)) {
                return this.queueWholeMove(source, target, sourceStack, targetStack);
            }
            int needed = wanted.count() - targetStack.method_7947();
            if (sourceStack.method_7947() > needed) {
                return this.queuePartialMove(sourceScreen, targetScreen, sourceStack, targetStack, needed);
            }
            this.recoverySlot = sourceScreen;
            StackSnapshot sourceSnapshot = StackSnapshot.of(sourceStack);
            StackSnapshot targetSnapshot = StackSnapshot.of(targetStack);
            this.addPickup(sourceScreen, StackSnapshot.EMPTY, sourceSnapshot);
            this.addPickup(targetScreen, sourceSnapshot, targetSnapshot);
            int remainder = Math.max(0, sourceStack.method_7947() + targetStack.method_7947() - targetStack.method_7914());
            if (remainder > 0) {
                this.addPickup(sourceScreen, sourceSnapshot.withCount(remainder), StackSnapshot.EMPTY);
            }
            return true;
        }
        int source = this.findSource(preset, wanted, target, true);
        if (source < 0) {
            source = this.findSource(preset, wanted, target, false);
        }
        if (source < 0) {
            return false;
        }
        int sourceScreen = InventoryPresetManager.logicalToScreenSlot(source);
        if (sourceScreen < 0) {
            return false;
        }
        class_1799 sourceStack = this.currentStack(source);
        if (targetStack.method_7960() && sourceStack.method_7947() > wanted.count()) {
            return this.queuePartialMove(sourceScreen, targetScreen, sourceStack, targetStack, wanted.count());
        }
        return this.queueWholeMove(source, target, sourceStack, targetStack);
    }

    private boolean queueWholeMove(int source, int target, class_1799 sourceStack, class_1799 targetStack) {
        int sourceScreen = InventoryPresetManager.logicalToScreenSlot(source);
        int targetScreen = InventoryPresetManager.logicalToScreenSlot(target);
        if (sourceScreen < 0 || targetScreen < 0) {
            return false;
        }
        this.recoverySlot = -1;
        StackSnapshot sourceSnapshot = StackSnapshot.of(sourceStack);
        StackSnapshot targetSnapshot = StackSnapshot.of(targetStack);
        if (target >= 0 && target < 9) {
            this.addSwap(sourceScreen, target, sourceSnapshot, targetSnapshot);
            return true;
        }
        if (source >= 0 && source < 9) {
            this.addSwap(targetScreen, source, targetSnapshot, sourceSnapshot);
            return true;
        }
        int buffer = this.chooseHotbarBuffer();
        if (buffer < 0) {
            return false;
        }
        int bufferScreen = InventoryPresetManager.logicalToScreenSlot(buffer);
        StackSnapshot bufferSnapshot = StackSnapshot.of(this.currentStack(buffer));
        this.addSwap(sourceScreen, buffer, sourceSnapshot, bufferSnapshot);
        this.addSwap(targetScreen, buffer, targetSnapshot, sourceSnapshot);
        this.addSwap(sourceScreen, buffer, bufferSnapshot, targetSnapshot);
        return true;
    }

    private int chooseHotbarBuffer() {
        for (int logical = 0; logical < 9; ++logical) {
            if (!this.currentStack(logical).method_7960()) continue;
            return logical;
        }
        return 0;
    }

    private boolean queuePartialMove(int sourceScreen, int targetScreen, class_1799 sourceStack, class_1799 targetStack, int amount) {
        if (amount <= 0 || sourceStack.method_7960() || sourceStack.method_7947() <= amount) {
            return false;
        }
        if (!targetStack.method_7960() && !class_1799.method_31577((class_1799)sourceStack, (class_1799)targetStack)) {
            return false;
        }
        if (targetStack.method_7947() + amount > sourceStack.method_7914()) {
            return false;
        }
        this.recoverySlot = sourceScreen;
        StackSnapshot source = StackSnapshot.of(sourceStack);
        this.addPickup(sourceScreen, StackSnapshot.EMPTY, source);
        for (int moved = 0; moved < amount; ++moved) {
            StackSnapshot cursorBefore = source.withCount(sourceStack.method_7947() - moved);
            int targetCount = targetStack.method_7947() + moved;
            StackSnapshot targetBefore = targetCount == 0 ? StackSnapshot.EMPTY : source.withCount(targetCount);
            this.addPickup(targetScreen, 1, cursorBefore, targetBefore);
        }
        int remainder = sourceStack.method_7947() - amount;
        if (remainder > 0) {
            this.addPickup(sourceScreen, source.withCount(remainder), StackSnapshot.EMPTY);
        }
        return true;
    }

    private int findSource(InventoryPreset preset, InventoryPreset.Entry wanted, int target, boolean requireEnoughCount) {
        for (int logical = 0; logical < 41; ++logical) {
            InventoryPreset.Entry expectedHere;
            class_1799 stack;
            if (logical == target || !wanted.sameItem(stack = this.currentStack(logical)) || requireEnoughCount && stack.method_7947() < wanted.count() || !(expectedHere = preset.slot(logical)).isEmpty() && expectedHere.matches(stack)) continue;
            return logical;
        }
        return -1;
    }

    private void addPickup(int slot, StackSnapshot expectedCursor, StackSnapshot expectedSlot) {
        this.addPickup(slot, 0, expectedCursor, expectedSlot);
    }

    private void addPickup(int slot, int button, StackSnapshot expectedCursor, StackSnapshot expectedSlot) {
        this.clickSteps.add(new ClickStep(slot, button, class_1713.field_7790, expectedCursor, expectedSlot, StackSnapshot.EMPTY));
    }

    private void addSwap(int slot, int hotbarSlot, StackSnapshot expectedSlot, StackSnapshot expectedHotbar) {
        this.clickSteps.add(new ClickStep(slot, hotbarSlot, class_1713.field_7791, StackSnapshot.EMPTY, expectedSlot, expectedHotbar));
    }

    private void recoverCursor() {
        if (this.mc.field_1724 == null || this.mc.field_1761 == null || !(this.mc.field_1755 instanceof class_490) || this.mc.field_1724.field_7512.method_34255().method_7960()) {
            return;
        }
        int destination = this.findCursorDestination(this.mc.field_1724.field_7512.method_34255());
        if (destination >= 0) {
            this.mc.field_1761.method_2906(this.mc.field_1724.field_7512.field_7763, destination, 0, class_1713.field_7790, (class_1657)this.mc.field_1724);
        }
    }

    private boolean queueCursorRecovery() {
        if (this.mc.field_1724 == null || this.mc.field_1724.field_7512.method_34255().method_7960()) {
            return true;
        }
        class_1799 cursor = this.mc.field_1724.field_7512.method_34255();
        int destination = this.findCursorDestination(cursor);
        if (destination < 0) {
            return false;
        }
        this.clickSteps.add(new ClickStep(destination, 0, class_1713.field_7790, StackSnapshot.of(cursor), StackSnapshot.of(this.mc.field_1724.field_7512.method_7611(destination).method_7677()), StackSnapshot.EMPTY));
        this.recoverySlot = -1;
        return true;
    }

    private int findCursorDestination(class_1799 cursor) {
        if (this.recoverySlot >= 0 && this.recoverySlot < this.mc.field_1724.field_7512.field_7761.size() && this.canPlaceCursor(this.recoverySlot, cursor)) {
            return this.recoverySlot;
        }
        for (int logical = 0; logical < 36; ++logical) {
            int screenSlot = InventoryPresetManager.logicalToScreenSlot(logical);
            if (!this.canPlaceCursor(screenSlot, cursor)) continue;
            return screenSlot;
        }
        int offhand = InventoryPresetManager.logicalToScreenSlot(40);
        return this.canPlaceCursor(offhand, cursor) ? offhand : -1;
    }

    private boolean canPlaceCursor(int screenSlot, class_1799 cursor) {
        if (screenSlot < 0 || screenSlot >= this.mc.field_1724.field_7512.field_7761.size()) {
            return false;
        }
        class_1735 slot = this.mc.field_1724.field_7512.method_7611(screenSlot);
        if (!slot.method_7680(cursor)) {
            return false;
        }
        class_1799 existing = slot.method_7677();
        return existing.method_7960() || class_1799.method_31577((class_1799)existing, (class_1799)cursor) && existing.method_7947() + cursor.method_7947() <= existing.method_7914();
    }

    private Map<String, Integer> missingCounts(InventoryPreset preset) {
        class_1799 cursor;
        LinkedHashMap<String, Integer> required = new LinkedHashMap<String, Integer>();
        HashMap available = new HashMap();
        ArrayList<InventoryPreset.Entry> variants = new ArrayList<InventoryPreset.Entry>();
        for (InventoryPreset.Entry entry : preset.slots()) {
            if (entry.isEmpty()) continue;
            required.merge(entry.matchKey(), entry.count(), Integer::sum);
            if (!variants.stream().noneMatch(existing -> existing.matchKey().equals(entry.matchKey()))) continue;
            variants.add(entry);
        }
        for (int i = 0; i < 41; ++i) {
            class_1799 stack = this.currentStack(i);
            if (stack.method_7960()) continue;
            variants.stream().filter(variant -> variant.sameItem(stack)).findFirst().ifPresent(variant -> available.merge(variant.matchKey(), stack.method_7947(), Integer::sum));
        }
        if (this.mc.field_1724 != null && !(cursor = this.mc.field_1724.field_7512.method_34255()).method_7960()) {
            variants.stream().filter(variant -> variant.sameItem(cursor)).findFirst().ifPresent(variant -> available.merge(variant.matchKey(), cursor.method_7947(), Integer::sum));
        }
        LinkedHashMap<String, Integer> missing = new LinkedHashMap<String, Integer>();
        for (Map.Entry entry : required.entrySet()) {
            int deficit = (Integer)entry.getValue() - available.getOrDefault(entry.getKey(), 0);
            if (deficit <= 0) continue;
            missing.put((String)entry.getKey(), deficit);
        }
        return missing;
    }

    private void loadAll() {
        this.presets.clear();
        try {
            String selected;
            Files.createDirectories(DIRECTORY, new FileAttribute[0]);
            try (Stream<Path> files = Files.list(DIRECTORY);){
                files.filter(path -> path.getFileName().toString().endsWith(".json")).forEach(this::load);
            }
            if (Files.exists(CURRENT_FILE, new LinkOption[0]) && this.presets.containsKey(InventoryPresetManager.key(selected = Files.readString(CURRENT_FILE, StandardCharsets.UTF_8).trim()))) {
                this.activeName = this.presets.get(InventoryPresetManager.key(selected)).name();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);){
            JsonObject root = JsonParser.parseReader((Reader)reader).getAsJsonObject();
            String name = root.has("name") ? root.get("name").getAsString() : path.getFileName().toString();
            long created = root.has("createdAt") ? root.get("createdAt").getAsLong() : 0L;
            ArrayList<InventoryPreset.Entry> slots = new ArrayList<InventoryPreset.Entry>();
            JsonArray array = root.getAsJsonArray("slots");
            if (array != null) {
                for (JsonElement element2 : array) {
                    Object potionSignature;
                    JsonObject object = element2.getAsJsonObject();
                    String item = object.has("item") ? object.get("item").getAsString() : "";
                    int count = object.has("count") ? object.get("count").getAsInt() : 0;
                    String displayName = object.has("displayName") ? object.get("displayName").getAsString() : "";
                    Object object2 = potionSignature = object.has("potionSignature") ? object.get("potionSignature").getAsString() : "";
                    if (((String)potionSignature).isBlank() && InventoryPreset.Entry.canHavePotionContents(item) && !displayName.isBlank()) {
                        potionSignature = "legacy-name:" + displayName;
                    }
                    String potionData = object.has("potionData") ? object.get("potionData").toString() : "";
                    class_2960 id = class_2960.method_12829((String)item);
                    slots.add(id != null && class_7923.field_41178.method_10250(id) ? new InventoryPreset.Entry(item, count, displayName, (String)potionSignature, potionData) : InventoryPreset.Entry.EMPTY);
                }
            }
            InventoryPreset preset = new InventoryPreset(name, created, slots);
            this.presets.put(InventoryPresetManager.key(name), preset);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void save(InventoryPreset preset) {
        JsonObject root = new JsonObject();
        root.addProperty("name", preset.name());
        root.addProperty("createdAt", (Number)preset.createdAt());
        JsonArray slots = new JsonArray();
        for (InventoryPreset.Entry entry : preset.slots()) {
            JsonObject object = new JsonObject();
            object.addProperty("item", entry.itemId());
            object.addProperty("count", (Number)entry.count());
            if (!entry.displayName().isBlank()) {
                object.addProperty("displayName", entry.displayName());
            }
            if (!entry.potionSignature().isBlank()) {
                object.addProperty("potionSignature", entry.potionSignature());
            }
            if (!entry.potionData().isBlank()) {
                object.add("potionData", JsonParser.parseString((String)entry.potionData()));
            }
            slots.add((JsonElement)object);
        }
        root.add("slots", (JsonElement)slots);
        try {
            Files.createDirectories(DIRECTORY, new FileAttribute[0]);
            try (BufferedWriter writer = Files.newBufferedWriter(this.fileFor(preset.name()), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);){
                GSON.toJson((JsonElement)root, (Appendable)writer);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCurrentName() {
        try {
            Files.createDirectories(DIRECTORY, new FileAttribute[0]);
            Files.writeString(CURRENT_FILE, (CharSequence)(this.activeName == null ? "" : this.activeName), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path fileFor(String name) {
        return DIRECTORY.resolve(InventoryPresetManager.normalizeName(name).toLowerCase(Locale.ROOT).replace(' ', '_') + ".json");
    }

    private static String normalizeName(String value) {
        if (value == null) {
            return "";
        }
        String result = value.replaceAll("[\\\\/:*?\"<>|]", "").replaceAll("\\s+", " ").trim();
        return result.length() > 32 ? result.substring(0, 32).trim() : result;
    }

    private static String key(String name) {
        return name == null ? "" : name.toLowerCase(Locale.ROOT);
    }

    @Environment(value=EnvType.CLIENT)
    public static enum SlotState {
        NONE,
        CORRECT,
        WRONG_SLOT,
        MISSING;

    }

    @Environment(value=EnvType.CLIENT)
    public record MissingItem(InventoryPreset.Entry entry, int count) {
        public class_1799 stack() {
            return this.entry.toStack();
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record ClickStep(int slot, int button, class_1713 actionType, StackSnapshot expectedCursor, StackSnapshot expectedSlot, StackSnapshot expectedHotbar) {
        private boolean matches(class_1799 cursor, class_1799 slotStack, class_1799 hotbarStack) {
            return this.expectedCursor.matches(cursor) && this.expectedSlot.matches(slotStack) && (this.actionType != class_1713.field_7791 || this.expectedHotbar.matches(hotbarStack));
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record StackSnapshot(String itemId, int count, String components, boolean empty) {
        private static final StackSnapshot EMPTY = new StackSnapshot("", 0, "", true);

        private static StackSnapshot of(class_1799 stack) {
            if (stack == null || stack.method_7960()) {
                return EMPTY;
            }
            return new StackSnapshot(class_7923.field_41178.method_10221((Object)stack.method_7909()).toString(), stack.method_7947(), stack.method_57353().toString(), false);
        }

        private StackSnapshot withCount(int newCount) {
            return this.empty || newCount <= 0 ? EMPTY : new StackSnapshot(this.itemId, newCount, this.components, false);
        }

        private boolean matches(class_1799 stack) {
            if (this.empty) {
                return stack == null || stack.method_7960();
            }
            if (stack == null || stack.method_7960() || stack.method_7947() != this.count) {
                return false;
            }
            return this.itemId.equals(class_7923.field_41178.method_10221((Object)stack.method_7909()).toString()) && this.components.equals(stack.method_57353().toString());
        }
    }
}

