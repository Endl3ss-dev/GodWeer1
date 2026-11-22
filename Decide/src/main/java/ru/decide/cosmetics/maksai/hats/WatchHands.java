/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.cosmetics.maksai.hats;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.cosmetics.maksai.hats.HatManager;

@Environment(value=EnvType.CLIENT)
public final class WatchHands {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"DecideVisuals:WatchHands");
    private static final String CATEGORY = "watch";
    private static final Map<Integer, Hand[]> CACHE = new ConcurrentHashMap<Integer, Hand[]>();

    private WatchHands() {
    }

    public static boolean has(HatManager.Equipped equipped) {
        return equipped != null && CATEGORY.equals(equipped.entry().category()) && WatchHands.load(equipped.entry().id()).length > 0;
    }

    public static float[] frame(HatManager.Equipped equipped) {
        if (equipped == null || !CATEGORY.equals(equipped.entry().category())) {
            return null;
        }
        Hand[] hands = WatchHands.load(equipped.entry().id());
        if (hands.length == 0) {
            return null;
        }
        int maxBone = 0;
        for (Hand hand : hands) {
            maxBone = Math.max(maxBone, hand.index());
        }
        float[] matrices = new float[(maxBone + 1) * 12];
        for (int bone = 0; bone <= maxBone; ++bone) {
            int k = bone * 12;
            matrices[k] = 1.0f;
            matrices[k + 5] = 1.0f;
            matrices[k + 10] = 1.0f;
        }
        LocalTime now = LocalTime.now();
        double seconds = (double)now.getSecond() + (double)now.getNano() / 1.0E9;
        double minutes = (double)now.getMinute() + seconds / 60.0;
        double hours = (double)(now.getHour() % 12) + minutes / 60.0;
        for (Hand hand : hands) {
            double absolute = switch (hand.kind()) {
                case "hour" -> 30.0 * hours;
                case "minute" -> 6.0 * minutes;
                default -> 6.0 * seconds;
            };
            double degrees = absolute - (double)hand.angle();
            double radians = Math.toRadians(degrees);
            float cos = (float)Math.cos(radians);
            float sin = (float)Math.sin(radians);
            float py = hand.pivotY();
            float pz = hand.pivotZ();
            int k = hand.index() * 12;
            matrices[k] = 1.0f;
            matrices[k + 1] = 0.0f;
            matrices[k + 2] = 0.0f;
            matrices[k + 3] = 0.0f;
            matrices[k + 4] = 0.0f;
            matrices[k + 5] = cos;
            matrices[k + 6] = -sin;
            matrices[k + 7] = py * (1.0f - cos) + pz * sin;
            matrices[k + 8] = 0.0f;
            matrices[k + 9] = sin;
            matrices[k + 10] = cos;
            matrices[k + 11] = pz * (1.0f - cos) - py * sin;
        }
        return matrices;
    }

    private static Hand[] load(int id) {
        return CACHE.computeIfAbsent(id, WatchHands::readHands);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static Hand[] readHands(int id) {
        String path = "/assets/decide/cosmetics/hats/" + id + "/hands.json";
        try (InputStream in = WatchHands.class.getResourceAsStream(path);){
            if (in == null) {
                Hand[] handArray = new Hand[]{};
                return handArray;
            }
            JsonObject root = JsonParser.parseReader((Reader)new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            if (!root.has("hands") || !root.get("hands").isJsonArray()) {
                Hand[] handArray = new Hand[]{};
                return handArray;
            }
            JsonArray array = root.getAsJsonArray("hands");
            Hand[] hands = new Hand[array.size()];
            for (int i = 0; i < array.size(); ++i) {
                JsonObject hand = array.get(i).getAsJsonObject();
                JsonArray pivot = hand.getAsJsonArray("pivot");
                hands[i] = new Hand(hand.get("index").getAsInt(), hand.get("kind").getAsString(), pivot.get(0).getAsFloat(), pivot.get(1).getAsFloat(), pivot.get(2).getAsFloat(), hand.has("angle") ? hand.get("angle").getAsFloat() : 0.0f);
            }
            Hand[] handArray = hands;
            return handArray;
        }
        catch (Exception e) {
            LOGGER.warn("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u0440\u043e\u0447\u0438\u0442\u0430\u0442\u044c \u0441\u0442\u0440\u0435\u043b\u043a\u0438 \u0447\u0430\u0441\u043e\u0432 {}", (Object)id, (Object)e);
            return new Hand[0];
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Hand(int index, String kind, float pivotX, float pivotY, float pivotZ, float angle) {
    }
}

