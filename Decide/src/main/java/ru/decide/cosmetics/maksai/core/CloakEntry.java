/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.cosmetics.maksai.core;

import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class CloakEntry {
    public final int id;
    public final String name;
    public final List<String> colors;
    public final List<String> tags;
    public final int k;
    public final int frames;
    public final int frametime;
    private final String searchKey;

    public CloakEntry(int id, String name, List<String> colors, List<String> tags, int k, int frames, int frametime) {
        this.id = id;
        this.name = name != null && !name.isBlank() ? name : "#" + id;
        this.colors = colors == null ? List.of() : List.copyOf(colors);
        this.tags = tags == null ? List.of() : List.copyOf(tags);
        this.k = k;
        this.frames = Math.max(1, frames);
        this.frametime = frametime;
        this.searchKey = (this.name + " " + String.join((CharSequence)" ", this.colors) + " " + String.join((CharSequence)" ", this.tags)).toLowerCase(Locale.ROOT);
    }

    public boolean animated() {
        return this.frames > 1 && this.frametime > 0;
    }

    public boolean matches(String query) {
        if (query == null) {
            return true;
        }
        String q = query.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) {
            return true;
        }
        for (String part : q.split("\\s+")) {
            if (this.searchKey.contains(part)) continue;
            return false;
        }
        return true;
    }

    public String thumbPath() {
        return "/assets/decide/cosmetics/thumbs/" + this.id + ".png";
    }

    public String texturePath() {
        return "/assets/decide/cosmetics/cloaks/" + this.id + ".png";
    }
}

