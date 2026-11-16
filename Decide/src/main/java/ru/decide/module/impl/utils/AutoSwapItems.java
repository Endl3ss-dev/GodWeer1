/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1799
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_9334
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.module.impl.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_9334;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class AutoSwapItems {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoSwapItems.class);
    private static final int MAX_SLOTS = 6;
    private final class_310 mc = class_310.method_1551();
    private final class_1799[] items = new class_1799[6];
    private int count = 3;
    private boolean writable = true;

    public AutoSwapItems() {
        for (int i = 0; i < 6; ++i) {
            this.items[i] = class_1799.field_8037;
        }
    }

    public void setCount(int count) {
        this.count = Math.max(1, Math.min(6, count));
    }

    public int getCount() {
        return this.count;
    }

    public class_1799 get(int index) {
        if (index < 0 || index >= this.count) {
            return class_1799.field_8037;
        }
        return this.items[index].method_7972();
    }

    private Path file() {
        return this.mc.field_1697.toPath().resolve("nur-swap").resolve("items.json");
    }

    public void load() {
        if (this.mc.field_1687 == null) {
            return;
        }
        for (int i = 0; i < 6; ++i) {
            this.items[i] = class_1799.field_8037;
        }
        this.writable = true;
        if (!Files.exists(this.file(), new LinkOption[0])) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(this.file(), StandardCharsets.UTF_8);){
            JsonArray array = JsonParser.parseReader((Reader)reader).getAsJsonArray();
            for (int i = 0; i < Math.min(array.size(), 6); ++i) {
                this.items[i] = array.get(i).isJsonNull() ? class_1799.field_8037 : (class_1799)class_1799.field_24671.parse((DynamicOps)this.mc.field_1687.method_30349().method_57093((DynamicOps)JsonOps.INSTANCE), (Object)array.get(i)).getOrThrow();
            }
        }
        catch (IOException | RuntimeException exception) {
            this.writable = false;
            LOGGER.warn("Unable to load Auto Swap items", (Throwable)exception);
            this.report("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b Auto Swap; \u0444\u0430\u0439\u043b \u043e\u0441\u0442\u0430\u0432\u043b\u0435\u043d \u0431\u0435\u0437 \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u0439");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean set(int index, class_1799 stack) {
        boolean exception222;
        if (index < 0) return false;
        if (index >= this.count) return false;
        if (this.mc.field_1687 == null) return false;
        if (!this.writable) {
            return false;
        }
        class_1799 previous = this.items[index];
        this.items[index] = stack.method_7972();
        Path temporary = null;
        try {
            JsonArray array = new JsonArray();
            for (int i = 0; i < this.count; ++i) {
                array.add((JsonElement)(this.items[i].method_7960() ? JsonNull.INSTANCE : this.encode(this.items[i])));
            }
            Path target = this.file();
            Files.createDirectories(target.getParent(), new FileAttribute[0]);
            temporary = Files.createTempFile(target.getParent(), "items-", ".tmp", new FileAttribute[0]);
            try (BufferedWriter writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8, new OpenOption[0]);){
                GSON.toJson((JsonElement)array, (Appendable)writer);
            }
            try {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (AtomicMoveNotSupportedException exception222) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
            exception222 = true;
            if (temporary == null) return exception222;
        }
        catch (IOException | RuntimeException exception4) {
            this.items[index] = previous;
            LOGGER.warn("Unable to save Auto Swap items", (Throwable)exception4);
            this.report("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0441\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u043f\u0440\u0435\u0434\u043c\u0435\u0442 Auto Swap");
            boolean bl = false;
            return bl;
        }
        try {
            Files.deleteIfExists(temporary);
            return exception222;
        }
        catch (IOException exception3) {
            LOGGER.warn("Unable to remove temporary Auto Swap file", (Throwable)exception3);
        }
        return exception222;
        finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                }
                catch (IOException exception5) {
                    LOGGER.warn("Unable to remove temporary Auto Swap file", (Throwable)exception5);
                }
            }
        }
    }

    public Predicate<class_1799> predicate(int index) {
        if (index < 0 || index >= this.count || this.items[index].method_7960() || this.mc.field_1687 == null) {
            return stack -> false;
        }
        JsonElement expected = this.encode(this.stripDamage(this.items[index].method_46651(1)));
        return stack -> !stack.method_7960() && this.mc.field_1687 != null && AutoSwapItems.equivalent(expected, this.encode(this.stripDamage(stack.method_46651(1))));
    }

    private class_1799 stripDamage(class_1799 stack) {
        stack.method_57381(class_9334.field_49629);
        return stack;
    }

    private JsonElement encode(class_1799 stack) {
        return (JsonElement)class_1799.field_24671.encodeStart((DynamicOps)this.mc.field_1687.method_30349().method_57093((DynamicOps)JsonOps.INSTANCE), (Object)stack).getOrThrow();
    }

    static boolean equivalent(JsonElement expected, JsonElement actual) {
        JsonObject first;
        if (expected instanceof JsonObject) {
            first = (JsonObject)expected;
            if (actual instanceof JsonObject) {
                JsonObject second = (JsonObject)actual;
                if (!first.keySet().equals(second.keySet())) {
                    return false;
                }
                for (String key : first.keySet()) {
                    if (AutoSwapItems.equivalent(first.get(key), second.get(key))) continue;
                    return false;
                }
                return true;
            }
        }
        if (expected instanceof JsonArray) {
            first = (JsonArray)expected;
            if (actual instanceof JsonArray) {
                JsonArray second = (JsonArray)actual;
                if (first.size() != second.size()) {
                    return false;
                }
                ArrayList remaining = new ArrayList(second.asList());
                for (JsonElement element2 : first) {
                    int match = -1;
                    for (int i = 0; i < remaining.size(); ++i) {
                        if (!AutoSwapItems.equivalent(element2, (JsonElement)remaining.get(i))) continue;
                        match = i;
                        break;
                    }
                    if (match == -1) {
                        return false;
                    }
                    remaining.remove(match);
                }
                return true;
            }
        }
        return expected != null && expected.equals(actual);
    }

    private void report(String message) {
        if (this.mc.field_1724 != null) {
            this.mc.field_1724.method_7353((class_2561)class_2561.method_43470((String)message), false);
        }
    }
}

