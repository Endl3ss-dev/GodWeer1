/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.cosmetics.maksai.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class LruCache<K, V> {
    private final int max;
    private final BiConsumer<K, V> onEvict;
    private final LinkedHashMap<K, V> map = new LinkedHashMap(16, 0.75f, true);

    public LruCache(int max, BiConsumer<K, V> onEvict) {
        this.max = Math.max(1, max);
        this.onEvict = onEvict;
    }

    public V get(K k) {
        return this.map.get(k);
    }

    public boolean containsKey(K k) {
        return this.map.containsKey(k);
    }

    public int size() {
        return this.map.size();
    }

    public void put(K k, V v) {
        V old = this.map.put(k, v);
        if (old != null && old != v) {
            this.onEvict.accept(k, old);
        }
        while (this.map.size() > this.max) {
            Map.Entry<K, V> eldest = this.map.entrySet().iterator().next();
            this.map.remove(eldest.getKey());
            this.onEvict.accept(eldest.getKey(), eldest.getValue());
        }
    }

    public void clear() {
        for (Map.Entry<K, V> e : this.map.entrySet()) {
            this.onEvict.accept(e.getKey(), e.getValue());
        }
        this.map.clear();
    }
}

