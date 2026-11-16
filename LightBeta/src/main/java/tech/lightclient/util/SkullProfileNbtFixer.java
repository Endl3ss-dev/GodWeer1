package tech.lightclient.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.UUID;

/** Normalizes legacy skull profile NBT before Minecraft reads it. */
public final class SkullProfileNbtFixer {

    private SkullProfileNbtFixer() {
    }

    public static void normalize(NbtCompound blockEntityTag) {
        if (blockEntityTag == null) {
            return;
        }

        NbtCompound profile = getCompound(blockEntityTag, "profile", "Profile");
        if (profile == null) {
            return;
        }

        normalizeId(profile);
        normalizeProperties(profile);
    }

    private static boolean normalizeId(NbtCompound profile) {
        NbtElement idTag = profile.get("id");
        String sourceKey = "id";
        if (idTag == null) {
            idTag = profile.get("Id");
            sourceKey = "Id";
        }
        if (!(idTag instanceof NbtString stringId)) {
            return false;
        }

        String value = stringId.asString().orElse("");
        UUID uuid;
        try {
            uuid = UUID.fromString(value);
        } catch (IllegalArgumentException ignored) {
            return false;
        }

        if (!"id".equals(sourceKey)) {
            profile.remove(sourceKey);
        }
        profile.put("id", new NbtIntArray(new int[]{
                (int) (uuid.getMostSignificantBits() >> 32),
                (int) uuid.getMostSignificantBits(),
                (int) (uuid.getLeastSignificantBits() >> 32),
                (int) uuid.getLeastSignificantBits()
        }));
        return true;
    }

    private static boolean normalizeProperties(NbtCompound profile) {
        String sourceKey = profile.contains("properties") ? "properties"
                : profile.contains("Properties") ? "Properties" : null;
        if (sourceKey == null) {
            return false;
        }

        NbtElement propertiesTag = profile.get(sourceKey);
        if (isAlreadyModernList(propertiesTag)) {
            return false;
        }
        if (!(propertiesTag instanceof NbtCompound legacyProperties)) {
            return false;
        }

        NbtList rebuilt = new NbtList();
        for (String propertyName : legacyProperties.getKeys()) {
            NbtElement entries = legacyProperties.get(propertyName);
            if (entries instanceof NbtList list) {
                for (NbtElement entry : list) {
                    addRewritten(rebuilt, propertyName, entry);
                }
            } else if (entries instanceof NbtCompound single) {
                addRewritten(rebuilt, propertyName, single);
            } else if (entries instanceof NbtString rawValue) {
                NbtCompound out = new NbtCompound();
                out.putString("name", propertyName);
                out.putString("value", rawValue.asString().orElse(""));
                rebuilt.add(out);
            }
        }

        if (rebuilt.isEmpty()) {
            return false;
        }
        profile.remove(sourceKey);
        profile.put("properties", rebuilt);
        return true;
    }

    private static void addRewritten(NbtList target, String propertyName, NbtElement entry) {
        if (!(entry instanceof NbtCompound compound)) {
            return;
        }

        String value = firstString(compound, "value", "Value");
        if (value == null) {
            return;
        }

        String signature = firstString(compound, "signature", "Signature");
        NbtCompound out = new NbtCompound();
        out.putString("name", propertyName);
        out.putString("value", value);
        if (signature != null) {
            out.putString("signature", signature);
        }
        target.add(out);
    }

    private static String firstString(NbtCompound compound, String... keys) {
        for (String key : keys) {
            NbtElement tag = compound.get(key);
            if (tag instanceof NbtString string) {
                return string.asString().orElse(null);
            }
        }
        return null;
    }

    private static boolean isAlreadyModernList(NbtElement tag) {
        if (!(tag instanceof NbtList list) || list.isEmpty()) {
            return false;
        }
        NbtElement first = list.get(0);
        return first instanceof NbtCompound compound
                && compound.contains("name")
                && compound.contains("value");
    }

    private static NbtCompound getCompound(NbtCompound parent, String... keys) {
        for (String key : keys) {
            NbtElement tag = parent.get(key);
            if (tag instanceof NbtCompound compound) {
                return compound;
            }
        }
        return null;
    }
}
