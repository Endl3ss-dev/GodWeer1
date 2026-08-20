/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.privates.baritone.api.utils;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BlockUtils {

    private static Map<String, Block> resourceCache = new HashMap<>();

    public static String blockToString(Block block) {
        ResourceLocation loc = Registry.BLOCK.getKey(block);
        String name = loc.getPath(); // normally, only write the part after the minecraft:
        if (!loc.getNamespace().equals("minecraft")) {
            // Baritone is running on top of forge with mods installed, perhaps?
            name = loc.toString(); // include the namespace with the colon
        }
        return name;
    }

    public static Block stringToBlockRequired(String name) {
        Block block = stringToBlockNullable(name);

        if (block == null) {
            throw new IllegalArgumentException(String.format("Invalid block name %s", name));
        }

        return block;
    }

    public static Block stringToBlockNullable(String name) {
        if (name == null) {
            return null;
        }
        String normalized = normalizeSelector(name);

        // do NOT just replace this with a computeWithAbsent, it isn't thread safe
        Block block = resourceCache.get(normalized); // map is never mutated in place so this is safe
        if (block != null) {
            return block;
        }
        if (resourceCache.containsKey(normalized)) {
            return null; // cached as null
        }
        block = Registry.BLOCK.getOptional(ResourceLocation.tryCreate(normalized.contains(":") ? normalized : "minecraft:" + normalized)).orElse(null);

        // Fallback: try exact path match among all registered blocks (namespace-agnostic)
        if (block == null) {
            for (ResourceLocation key : Registry.BLOCK.keySet()) {
                if (key != null && key.getPath().equals(normalized)) {
                    block = Registry.BLOCK.getOptional(key).orElse(null);
                    if (block != null) {
                        break;
                    }
                }
            }
        }

        Map<String, Block> copy = new HashMap<>(resourceCache); // read only copy is safe, wont throw concurrentmodification
        copy.put(normalized, block);
        resourceCache = copy;
        return block;
    }

    private static String normalizeSelector(String name) {
        String s = name.trim().toLowerCase(Locale.ROOT);
        // Strip accidental wrapping quotes from chat copy/paste
        if (s.length() >= 2 && ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))) {
            s = s.substring(1, s.length() - 1);
        }
        // Some wrappers may pass spaces instead of underscores
        s = s.replace(' ', '_');
        // Legacy aliases that players often type in commands
        if (s.equals("tallgrass")) {
            s = "grass";
        }
        return s;
    }

    private BlockUtils() {
    }
}
