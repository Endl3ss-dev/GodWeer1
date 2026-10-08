package never.lose.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.entity.player.PlayerEntity;
import never.lose.NeverLose;
import never.lose.utils.client.ClientFiles;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class FriendManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MM.dd.yyyy");
    private static final List<FriendEntry> friends = new ArrayList<>();

    private FriendManager() {
    }

    public static void init() {
        loadFriends();
    }

    public static boolean addFriend(String name) {
        String nick = sanitize(name);
        if (nick.isEmpty()) {
            return false;
        }
        if (find(nick) != null) {
            return false;
        }
        friends.add(new FriendEntry(nick, today()));
        save();
        return true;
    }

    public static boolean removeFriend(String name) {
        String nick = sanitize(name);
        if (nick.isEmpty()) {
            return false;
        }
        boolean removed = false;
        Iterator<FriendEntry> iterator = friends.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().name().equalsIgnoreCase(nick)) {
                iterator.remove();
                removed = true;
            }
        }
        if (removed) {
            save();
        }
        return removed;
    }

    public static boolean isFriend(String name) {
        return find(name) != null;
    }

    public static boolean isFriend(PlayerEntity player) {
        if (player == null) {
            return false;
        }
        if (isFriend(player.getName().getString())) {
            return true;
        }
        try {
            return isFriend(player.getGameProfile().getName());
        } catch (Exception ignored) {
            return false;
        }
    }

    public static List<FriendEntry> getFriends() {
        return Collections.unmodifiableList(friends);
    }

    public static void loadFriends() {
        friends.clear();
        File file = ClientFiles.friendsFile();
        if (!file.exists()) {
            save();
            return;
        }
        try {
            JsonElement root = JsonParser.parseString(Files.readString(file.toPath(), StandardCharsets.UTF_8));
            if (root == null || !root.isJsonArray()) {
                return;
            }
            JsonArray array = root.getAsJsonArray();
            for (JsonElement element : array) {
                if (element == null || !element.isJsonObject()) {
                    continue;
                }
                JsonObject object = element.getAsJsonObject();
                if (!object.has("name")) {
                    continue;
                }
                String name = sanitize(object.get("name").getAsString());
                if (name.isEmpty() || find(name) != null) {
                    continue;
                }
                String date = object.has("date") ? object.get("date").getAsString() : today();
                if (date == null || date.isBlank()) {
                    date = today();
                }
                friends.add(new FriendEntry(name, date));
            }
        } catch (Exception e) {
            NeverLose.LOG.warn("[NeverLose] Failed to load friends: {}", e.getMessage());
        }
    }

    public static void save() {
        try {
            JsonArray array = new JsonArray();
            for (FriendEntry entry : friends) {
                JsonObject object = new JsonObject();
                object.addProperty("name", entry.name());
                object.addProperty("date", entry.date());
                array.add(object);
            }
            Files.writeString(ClientFiles.friendsFile().toPath(), GSON.toJson(array), StandardCharsets.UTF_8);
        } catch (Exception e) {
            NeverLose.LOG.warn("[NeverLose] Failed to save friends: {}", e.getMessage());
        }
    }

    private static FriendEntry find(String name) {
        String nick = sanitize(name);
        if (nick.isEmpty()) {
            return null;
        }
        for (FriendEntry entry : friends) {
            if (entry.name().equalsIgnoreCase(nick)) {
                return entry;
            }
        }
        return null;
    }

    private static String sanitize(String name) {
        if (name == null) {
            return "";
        }
        return name.replaceAll("§.", "").trim();
    }

    private static String today() {
        return LocalDate.now().format(DATE);
    }

    public record FriendEntry(String name, String date) {
        @Override
        public String toString() {
            return name;
        }
    }
}
