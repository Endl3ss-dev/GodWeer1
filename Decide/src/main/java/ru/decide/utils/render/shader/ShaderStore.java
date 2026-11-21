/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.shaders.ShaderType
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3298
 *  net.minecraft.class_3300
 */
package ru.decide.utils.render.shader;

import com.mojang.blaze3d.shaders.ShaderType;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import net.minecraft.class_3300;
import ru.decide.utils.render.shader.ShaderData;

@Environment(value=EnvType.CLIENT)
public final class ShaderStore {
    private static final byte[] KEY = "Decide//shader-veil//2026".getBytes(StandardCharsets.UTF_8);
    private static final String NAMESPACE = "decide";
    private static final String INCLUDE_DIR = "shaders/include/";

    private ShaderStore() {
    }

    public static String getSource(class_2960 id, ShaderType type) {
        if (id == null || !NAMESPACE.equals(id.method_12836())) {
            return null;
        }
        String ext = type == ShaderType.VERTEX ? "vsh" : "fsh";
        String key = id.method_12832() + "|" + ext;
        String enc = ShaderData.SOURCES.get(key);
        if (enc == null) {
            enc = ShaderData.joinChunks(key);
        }
        if (enc == null) {
            return null;
        }
        String raw = ShaderStore.decrypt(enc);
        class_2960 owner = class_2960.method_60655((String)id.method_12836(), (String)("shaders/" + id.method_12832() + "." + ext));
        String resolved = ShaderStore.resolveImports(raw, owner, new HashSet<String>());
        return ShaderStore.dedupeVersion(resolved);
    }

    private static String dedupeVersion(String source) {
        StringBuilder out = new StringBuilder(source.length());
        boolean versionSeen = false;
        for (String line : source.split("\n", -1)) {
            if (line.trim().startsWith("#version")) {
                if (versionSeen) continue;
                versionSeen = true;
            }
            out.append(line).append('\n');
        }
        return out.toString();
    }

    private static String decrypt(String base64) {
        byte[] data = Base64.getDecoder().decode(base64);
        byte[] out = new byte[data.length];
        for (int i = 0; i < data.length; ++i) {
            out[i] = (byte)(data[i] ^ KEY[i % KEY.length]);
        }
        return new String(out, StandardCharsets.UTF_8);
    }

    private static String resolveImports(String source, class_2960 owner, Set<String> seen) {
        StringBuilder out = new StringBuilder(source.length() + 256);
        for (String line : source.split("\n", -1)) {
            class_2960 inc;
            String trimmed = line.trim();
            if (trimmed.startsWith("#moj_import") && (inc = ShaderStore.parseImport(trimmed, owner)) != null) {
                String incSrc;
                if (!seen.add(inc.toString()) || (incSrc = ShaderStore.readResource(inc)) == null) continue;
                out.append(ShaderStore.resolveImports(incSrc, inc, seen));
                if (out.length() <= 0 || out.charAt(out.length() - 1) == '\n') continue;
                out.append('\n');
                continue;
            }
            out.append(line).append('\n');
        }
        return out.toString();
    }

    private static class_2960 parseImport(String line, class_2960 owner) {
        int lt = line.indexOf(60);
        int gt = line.indexOf(62);
        if (lt >= 0 && gt > lt) {
            String ref = line.substring(lt + 1, gt).trim();
            class_2960 id = class_2960.method_60654((String)ref);
            return class_2960.method_60655((String)id.method_12836(), (String)(INCLUDE_DIR + id.method_12832()));
        }
        int q1 = line.indexOf(34);
        int q2 = line.lastIndexOf(34);
        if (q1 >= 0 && q2 > q1) {
            String ref = line.substring(q1 + 1, q2).trim();
            String ownerPath = owner.method_12832();
            int slash = ownerPath.lastIndexOf(47);
            String dir = slash >= 0 ? ownerPath.substring(0, slash + 1) : "";
            return class_2960.method_60655((String)owner.method_12836(), (String)(dir + ref));
        }
        return null;
    }

    private static String readResource(class_2960 id) {
        String string;
        block11: {
            class_310 mc = class_310.method_1551();
            if (mc == null) {
                return null;
            }
            class_3300 rm = mc.method_1478();
            if (rm == null) {
                return null;
            }
            Optional res = rm.method_14486(id);
            if (res.isEmpty()) {
                return null;
            }
            InputStream in = ((class_3298)res.get()).method_14482();
            try {
                string = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                if (in == null) break block11;
            }
            catch (Throwable throwable) {
                try {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception e) {
                    return null;
                }
            }
            in.close();
        }
        return string;
    }
}

