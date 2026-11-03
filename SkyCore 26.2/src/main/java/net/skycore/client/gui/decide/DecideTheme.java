package net.skycore.client.gui.decide;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public enum DecideTheme {
    PURPLE("Purple", 0xFFD844EA, -1725690581, 0xFFF0F0F0, 0xFFA0A0A0, 0xFF18181B, 0xFFD844EA),
    NIGHT("Blue", 0xFF8BA2FF, -1726867157, 0xFFF0F0F0, 0xFFA0A0A0, 0xFF18181B, 0xFF8BA2FF),
    AKAR("Red", 0xFFFF8B8B, -1726280692, 0xFFF0F0F0, 0xFFA0A0A0, 0xFF18181B, 0xFFFF8B8B),
    VIOLKA("Violet", 0xFFA08BFF, -1726672860, 0xFFF0F0F0, 0xFFA0A0A0, 0xFF18181B, 0xFFA08BFF),
    TOXIS("Toxis", 0xFF97FF8B, -1726996979, 0xFFF0F0F0, 0xFFA0A0A0, 0xFF18181B, 0xFFB9FF8B),
    DECIDE("DecideVisual", 0xFFDFDFDF, -1728053248, 0xFFF0F0F0, 0xFFA0A0A0, 0xFF18181B, 0xFFDFDFDF),
    TURQUOISE("Turquoise", 0xFF60FFC6, -1726797010, 0xFFF0F0F0, 0xFFA0A0A0, 0xFF18181B, 0xFF60FFC6);

    private static final DecideTheme[] VALUES = values();
    private static final Path PREFS_FILE = Path.of("skycore/decide-gui.json");
    private static DecideTheme selected = NIGHT;
    private static DecideTheme preSelected = NIGHT;
    private static final DecideAnim.SatoshiAnim TRANSITION = new DecideAnim.SatoshiAnim(300L, 1.0f, true);
    private static boolean loaded;

    private final String title;
    private final int client;
    private final int rect;
    private final int text;
    private final int textDark;
    private final int rectTwo;
    private final int textClient;
    public final DecideAnim.SatoshiAnim dotAnim = new DecideAnim.SatoshiAnim(300L, 1.0f, false);

    DecideTheme(String title, int client, int rect, int text, int textDark, int rectTwo, int textClient) {
        this.title = title;
        this.client = client;
        this.rect = rect;
        this.text = text;
        this.textDark = textDark;
        this.rectTwo = rectTwo;
        this.textClient = textClient;
    }

    public static DecideTheme[] all() {
        return VALUES;
    }

    public String title() {
        return this.title;
    }

    public int client() {
        return this.client;
    }

    public int rect() {
        return this.rect;
    }

    public int text() {
        return this.text;
    }

    public int textDark() {
        return this.textDark;
    }

    public int rectTwo() {
        return this.rectTwo;
    }

    public int textClient() {
        return this.textClient;
    }

    public static DecideTheme selected() {
        ensureLoaded();
        return selected;
    }

    public static void select(DecideTheme theme) {
        if (theme == null || theme == selected) {
            return;
        }
        preSelected = selected;
        selected = theme;
        TRANSITION.resetToZero();
        TRANSITION.restartForwards();
        save();
    }

    public static int currentClientColor() {
        ensureLoaded();
        float t = TRANSITION.getOutput();
        return DecideDraw.overCol(preSelected.client, selected.client, t);
    }

    public static int currentBackgroundColor() {
        ensureLoaded();
        float t = TRANSITION.getOutput();
        return DecideDraw.overCol(preSelected.rect, selected.rect, t);
    }

    public static int currentLogoColor() {
        int base = currentClientColor();
        int secondary = DecideDraw.multDark(base, 0.72f);
        long phase = (System.currentTimeMillis() / 12L) % 360L;
        float wave = (phase >= 180L ? 360L - phase : phase) / 180.0f;
        return DecideDraw.overCol(base, secondary, wave * 0.35f);
    }

    public static void ensureLoaded() {
        if (loaded) {
            return;
        }
        loaded = true;
        try {
            if (Files.isRegularFile(PREFS_FILE)) {
                String jsonText = Files.readString(PREFS_FILE, StandardCharsets.UTF_8);
                JsonObject obj = JsonParser.parseString(jsonText).getAsJsonObject();
                if (obj.has("theme")) {
                    String name = obj.get("theme").getAsString();
                    for (DecideTheme t : VALUES) {
                        if (t.name().equalsIgnoreCase(name) || t.title.equalsIgnoreCase(name)) {
                            selected = t;
                            preSelected = t;
                            break;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        for (DecideTheme t : VALUES) {
            t.dotAnim.setDirection(t == selected);
        }
    }

    public static void save() {
        try {
            Path parent = PREFS_FILE.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            JsonObject obj = new JsonObject();
            obj.addProperty("theme", selected.name());
            Files.writeString(PREFS_FILE, obj.toString(), StandardCharsets.UTF_8);
        } catch (Exception ignored) {
        }
    }
}
