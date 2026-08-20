package fun.volon.client.modules.impl.misc;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import fun.volon.Volon;
import fun.volon.api.utils.replace.ReplaceUtils;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.settings.implement.BooleanSetting;
import fun.volon.client.modules.settings.implement.ModeSetting;
import fun.volon.client.modules.settings.implement.TextSetting;
import fun.volon.mixin.ChatScreenAccessor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NameProtect extends Module {

    public static final NameProtect INSTANCE = new NameProtect();
    private final BooleanSetting friends = new BooleanSetting("Скрывать друзей", true);
    private final BooleanSetting grief = new BooleanSetting("Скрывать гриф", false);
    private final TextSetting nickname = new TextSetting("Никнейм", "Volon", 32);

    // Донат-ранг, который будет отображаться в скорборде вместо реального префикса
    private final BooleanSetting fakeDonat = new BooleanSetting("Фейк донат", false);
    private final ModeSetting donatRank = new ModeSetting("Донат ранг", "Нет",
            "Нет",
            // ──── Стандартные донаты ────
            "PLAYER", "HERO", "TITAN", "AVENGER", "OVERLORD", "MAGISTER", "IMPERATOR", "DRAGON",
            // ──── Редкие / особые ────
            "BULL", "TIGER", "VAMPIRE", "BUNNY", "COBRA", "HYDRA", "RABBIT", "GOD",
            // ──── Сезонные / ивентовые ────
            "WINTER", "SAKURA", "SUMMER", "HALLOWEEN",
            // ──── Медиа / партнёры ────
            "YT", "TIKTOK", "TIKTOK+", "MEDIA", "YOUTUBE",
            // ──── Дополнительные ────
            "PRINCE", "PHOENIX", "GUARDIAN", "KRATOS", "PHANTOM", "CUSTOM",
            // ──── Из второго сервера (Myanmar/Burma unicode) ────
            "D.HELPER",
            // ──── Стафф (отображается как донат, использовать осторожно) ────
            "HELPER", "ML.MODER", "MODER", "MODER+", "ST.MODER", "GL.MODER",
            "ML.ADMIN", "ADMIN", "CURATOR", "SPECTATOR", "DEVELOPER",
            // ──── Донаты из нового набора (текстовые) ────
            "Hydra", "Cerberus", "Triton", "Phoenix", "Pandar", "Heat", "Cold",
            "Kronos", "Summer", "Winter", "Phobos", "Ares", "Aristocrat"
    ).visible(fakeDonat::isState);

    private static final int PATCH_CACHE_LIMIT = 512;
    private final Map<String, String> patchCache = new LinkedHashMap<>(PATCH_CACHE_LIMIT, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > PATCH_CACHE_LIMIT;
        }
    };

    private NameProtect() {
        super("NameProtect", "Скрывает никнеймы", ModuleCategory.MISC);
        addSettings(friends, grief, nickname, fakeDonat, donatRank);
    }

    /**
     * Возвращает строку выбранного донат-ранга (символы для scoreboard prefix),
     * или null если фейк-донат выключен или выбран "Нет".
     * Строка содержит unicode-символ доната, как он стоит в scoreboard.
     */
    public String getFakeDonatePrefix() {
        if (!fakeDonat.isState() || donatRank.is("Нет")) return null;
        return DONATE_RANK_UNICODE.getOrDefault(donatRank.getCurrent(), null);
    }

    /**
     * Карта: название ранга → unicode-символ, используемый на сервере в scoreboard prefix.
     * Порядок соответствует ReplaceSymbols.
     */
    private static final Map<String, String> DONATE_RANK_UNICODE = new java.util.LinkedHashMap<>();
    static {
        // Первый набор (ꔀ–ꕉ)
        DONATE_RANK_UNICODE.put("PLAYER",    "\uA500");
        DONATE_RANK_UNICODE.put("HERO",      "\uA504");
        DONATE_RANK_UNICODE.put("TITAN",     "\uA508");
        DONATE_RANK_UNICODE.put("AVENGER",   "\uA512");
        DONATE_RANK_UNICODE.put("OVERLORD",  "\uA516");
        DONATE_RANK_UNICODE.put("MAGISTER",  "\uA520");
        DONATE_RANK_UNICODE.put("IMPERATOR", "\uA524");
        DONATE_RANK_UNICODE.put("DRAGON",    "\uA528");
        DONATE_RANK_UNICODE.put("D.HELPER",  "\uA560");
        DONATE_RANK_UNICODE.put("BULL",      "\uA532");
        DONATE_RANK_UNICODE.put("TIGER",     "\uA536");
        DONATE_RANK_UNICODE.put("VAMPIRE",   "\uA544");
        DONATE_RANK_UNICODE.put("BUNNY",     "\uA556");
        DONATE_RANK_UNICODE.put("COBRA",     "\uA548");
        DONATE_RANK_UNICODE.put("HYDRA",     "\uA540");
        DONATE_RANK_UNICODE.put("RABBIT",    "\uA552");
        DONATE_RANK_UNICODE.put("GOD",       "\uA541");
        DONATE_RANK_UNICODE.put("HELPER",    "\uA509");
        DONATE_RANK_UNICODE.put("ML.MODER",  "\uA513");
        DONATE_RANK_UNICODE.put("MODER",     "\uA517");
        DONATE_RANK_UNICODE.put("MODER+",    "\uA521");
        DONATE_RANK_UNICODE.put("ST.MODER",  "\uA525");
        DONATE_RANK_UNICODE.put("GL.MODER",  "\uA529");
        DONATE_RANK_UNICODE.put("ML.ADMIN",  "\uA533");
        DONATE_RANK_UNICODE.put("ADMIN",     "\uA537");
        DONATE_RANK_UNICODE.put("MEDIA",     "\uA501");
        DONATE_RANK_UNICODE.put("YT",        "\uA505");
        DONATE_RANK_UNICODE.put("HELPER",    "\uA509");
        // Второй набор (Myanmar unicode)
        DONATE_RANK_UNICODE.put("HERO",      "\u1032"); // ူ  (уже перезапишет, но ок — первый важнее)
        DONATE_RANK_UNICODE.put("TITAN",     "\u102A"); // ဪ
        DONATE_RANK_UNICODE.put("PRINCE",    "\u1024"); // ဤ
        DONATE_RANK_UNICODE.put("PHOENIX",   "\u1026"); // ဦ
        DONATE_RANK_UNICODE.put("OVERLORD",  "\u102C"); // ာ
        DONATE_RANK_UNICODE.put("GUARDIAN",  "\u102B"); // ါ
        DONATE_RANK_UNICODE.put("KRATOS",    "\u1004"); // ့
        DONATE_RANK_UNICODE.put("PHANTOM",   "\u1022"); // ဢ
        DONATE_RANK_UNICODE.put("CUSTOM",    "\u1034"); // ဴ
        DONATE_RANK_UNICODE.put("WINTER",    "\u102C"); // ိ — \u102D
        DONATE_RANK_UNICODE.put("SAKURA",    "\u103A"); // း
        DONATE_RANK_UNICODE.put("SUMMER",    "\u1025"); // ဥ
        DONATE_RANK_UNICODE.put("HALLOWEEN", "\u1023"); // ဣ
        DONATE_RANK_UNICODE.put("TIKTOK",    "\u1035"); // ဵ
        DONATE_RANK_UNICODE.put("TIKTOK+",   "\u1036"); // ံ
        DONATE_RANK_UNICODE.put("MEDIA",     "\u1029"); // ဩ
        DONATE_RANK_UNICODE.put("YOUTUBE",   "\u1021"); // အ
        DONATE_RANK_UNICODE.put("CURATOR",   "\u1027"); // ဧ
        DONATE_RANK_UNICODE.put("SPECTATOR", "\u103F"); // ္
        DONATE_RANK_UNICODE.put("DEVELOPER", "\u101E"); // သ
        // Текстовые донаты (без специальных символов, просто текст)
        DONATE_RANK_UNICODE.put("Hydra",      "hydra");
        DONATE_RANK_UNICODE.put("Cerberus",   "cerberus");
        DONATE_RANK_UNICODE.put("Triton",     "triton");
        DONATE_RANK_UNICODE.put("Phoenix",    "phoenix");
        DONATE_RANK_UNICODE.put("Pandar",     "pandar");
        DONATE_RANK_UNICODE.put("Heat",       "heat");
        DONATE_RANK_UNICODE.put("Cold",       "cold");
        DONATE_RANK_UNICODE.put("Kronos",     "kronos");
        DONATE_RANK_UNICODE.put("Summer",     "summer");
        DONATE_RANK_UNICODE.put("Winter",     "winter");
        DONATE_RANK_UNICODE.put("Phobos",     "phobos");
        DONATE_RANK_UNICODE.put("Ares",       "ares");
        DONATE_RANK_UNICODE.put("Aristocrat", "aristocrat");
    }

    public String patch(String text) {
        if (text == null) {
            return null;
        }
        if (!shouldPatch()) {
            return text;
        }

        String cacheKey = getPatchCacheKey(text);
        String cached = patchCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        String out = text;
        String replacement = getReplacementName();
        out = replaceIgnoreCase(out, mc.getSession().getUsername(), replacement);
        if (friends.isState() && Volon.INSTANCE != null && Volon.INSTANCE.friendStorage != null) {
            for (String friend : Volon.INSTANCE.friendStorage.getFriends()) {
                out = replaceIgnoreCase(out, friend, replacement);
            }
        }
        out = patchGrief(out);
        patchCache.put(cacheKey, out);
        return out;
    }

    public String patchIncomingText(String text) {
        return patch(text);
    }

    public Text patchText(Text text) {
        if (text == null) {
            return null;
        }

        if (!shouldPatch()) {
            return text;
        }

        Text output = text;
        String replacement = getReplacementName();
        output = ReplaceUtils.replace(output, mc.getSession().getUsername(), replacement);
        if (friends.isState() && Volon.INSTANCE != null && Volon.INSTANCE.friendStorage != null) {
            for (String friend : Volon.INSTANCE.friendStorage.getFriends()) {
                output = ReplaceUtils.replace(output, friend, replacement);
            }
        }
        return output;
    }

    public String getReplacementName() {
        String value = nickname.get();
        return value == null || value.isBlank() ? "Volon" : value;
    }

    public boolean shouldHideGrief() {
        return grief.isState();
    }

    private String replaceIgnoreCase(String text, String target, String replacement) {
        if (text == null || target == null || target.isEmpty()) {
            return text;
        }
        int firstIndex = indexOfIgnoreCase(text, target, 0);
        if (firstIndex < 0) {
            return text;
        }

        StringBuilder out = new StringBuilder(text.length() + replacement.length());
        int from = 0;
        int index = firstIndex;
        while (index >= 0) {
            out.append(text, from, index).append(replacement);
            from = index + target.length();
            index = indexOfIgnoreCase(text, target, from);
        }
        out.append(text, from, text.length());
        return out.toString();
    }

    private int indexOfIgnoreCase(String text, String target, int from) {
        int max = text.length() - target.length();
        for (int i = Math.max(0, from); i <= max; i++) {
            if (text.regionMatches(true, i, target, 0, target.length())) {
                return i;
            }
        }
        return -1;
    }

    private String patchGrief(String text) {
        if (text == null || !grief.isState()) {
            return text;
        }

        String out = text.replaceAll("Анархия-\\d+", "VolonClient.fun");
        out = out.replaceAll("ГРИФ #\\d+", "VolonClient.fun");
        return out;
    }

    private String getPatchCacheKey(String text) {
        String username = mc != null && mc.getSession() != null ? mc.getSession().getUsername() : "";
        int friendsHash = 0;
        if (friends.isState() && Volon.INSTANCE != null && Volon.INSTANCE.friendStorage != null) {
            List<String> friendList = Volon.INSTANCE.friendStorage.getFriends();
            friendsHash = friendList.hashCode();
        }
        return username + '\u0001'
                + getReplacementName() + '\u0001'
                + friends.isState() + '\u0001'
                + grief.isState() + '\u0001'
                + friendsHash + '\u0001'
                + text;
    }

    private boolean shouldPatch() {
        return isEnable() && mc != null && mc.player != null && mc.world != null && !isFriendRemoveInputActive();
    }

    private boolean isFriendRemoveInputActive() {
        if (!(mc.currentScreen instanceof ChatScreen chatScreen)) {
            return false;
        }

        TextFieldWidget chatField = ((ChatScreenAccessor) chatScreen).Volon$getChatField();
        if (chatField == null) {
            return false;
        }

        String input = chatField.getText();
        if (input == null) {
            return false;
        }

        String normalized = input.trim().toLowerCase();
        String prefix = Volon.INSTANCE != null && Volon.INSTANCE.commandStorage != null
                ? Volon.INSTANCE.commandStorage.getPrefix().toLowerCase()
                : ".";
        return normalized.startsWith(prefix + "friend remove");
    }
}
