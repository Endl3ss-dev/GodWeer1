/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.cosmetics.melee;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.cosmetics.melee.BakedMeleeModel;
import ru.decide.cosmetics.melee.MeleeGeoModel;
import ru.decide.cosmetics.melee.MeleeGeoParser;
import ru.decide.cosmetics.melee.MeleeRegistry;
import ru.decide.cosmetics.melee.MeleeSkin;

@Environment(value=EnvType.CLIENT)
public final class MeleePreloader {
    private static final String LOADER_THREAD = "decide-melee-loader";
    private static final int MAX_CACHED = 48;
    private static final Map<String, BakedMeleeModel> CACHE = new ConcurrentHashMap<String, BakedMeleeModel>();
    private static final Set<String> LOADING = ConcurrentHashMap.newKeySet();

    private MeleePreloader() {
    }

    public static BakedMeleeModel get(MeleeSkin skin) {
        if (skin == null) {
            return null;
        }
        BakedMeleeModel baked = CACHE.get(skin.getGeometryKey());
        if (baked == null) {
            MeleePreloader.preload(skin);
        }
        return baked;
    }

    public static MeleeGeoModel definition(MeleeSkin skin) {
        if (skin == null) {
            return null;
        }
        try {
            String json = MeleePreloader.read(skin.modelResource());
            return json == null ? null : MeleeGeoParser.parse(json);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static void preload(MeleeSkin skin) {
        if (skin == null || CACHE.containsKey(skin.getGeometryKey()) || !LOADING.add(skin.getGeometryKey())) {
            return;
        }
        Thread thread = new Thread(() -> MeleePreloader.load(skin), LOADER_THREAD);
        thread.setDaemon(true);
        thread.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void load(MeleeSkin skin) {
        try {
            String modelJson = MeleePreloader.read(skin.modelResource());
            String animJson = MeleePreloader.read(skin.animationResource());
            if (modelJson == null || animJson == null) {
                return;
            }
            MeleeGeoModel model = MeleeGeoParser.parse(modelJson);
            if (model == null) {
                return;
            }
            CACHE.put(skin.getGeometryKey(), BakedMeleeModel.bake(model, animJson));
            MeleePreloader.evict(skin.getGeometryKey());
        }
        catch (Exception exception) {
        }
        finally {
            LOADING.remove(skin.getGeometryKey());
        }
    }

    private static void evict(String keepKey) {
        if (CACHE.size() <= 48) {
            return;
        }
        MeleeSkin equipped = MeleeRegistry.equipped();
        String keep = equipped == null ? null : equipped.getGeometryKey();
        CACHE.keySet().removeIf(key -> !key.equals(keep) && !key.equals(keepKey));
    }

    private static String read(String resource) {
        String string;
        block8: {
            InputStream in = MeleePreloader.class.getResourceAsStream(resource);
            try {
                String string2 = string = in == null ? null : new String(in.readAllBytes(), StandardCharsets.UTF_8);
                if (in == null) break block8;
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

