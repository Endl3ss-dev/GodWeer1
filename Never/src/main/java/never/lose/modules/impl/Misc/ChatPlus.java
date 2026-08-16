package never.lose.modules.impl.Misc;

import net.minecraft.client.MinecraftClient;
import never.lose.modules.api.Module;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.ModuleInfo;
import never.lose.modules.api.setting.BooleanSetting;
import never.lose.modules.api.setting.SliderSetting;
import never.lose.modules.api.setting.StringSetting;

@ModuleInfo(name = "ChatPlus", desc = "Prefix, postfix, leet, общий чат и спамер", category = ModuleCategory.MISC)
public class ChatPlus extends Module {

    private static final int CHAT_LIMIT = 256;

    public final BooleanSetting prefix = new BooleanSetting("Prefix", false);
    public final StringSetting prefixText = new StringSetting("Текст Prefix", "", 64)
            .setVisible(prefix::getValue);

    public final BooleanSetting postfix = new BooleanSetting("Postfix", false);
    public final StringSetting postfixText = new StringSetting("Готовый postfix", "", 128)
            .setVisible(postfix::getValue);

    public final BooleanSetting leetSpeak = new BooleanSetting("LeetSpeak", false);
    public final BooleanSetting globalChat = new BooleanSetting("Общий чат", false);

    public final BooleanSetting spammer = new BooleanSetting("Спамер", false);
    public final StringSetting spamText = new StringSetting("Фразы", "привет|как дела", 256)
            .setVisible(spammer::getValue);
    public final SliderSetting spamDelay = new SliderSetting("Задержка", 4000, 1500, 30000, 100)
            .setVisible(spammer::getValue);

    private long lastSpam;
    private long nextDelay = 4000L;
    private int phraseIndex;
    private String cachedRaw;
    private String[] cachedPhrases = new String[0];
    private boolean sendingSpam;

    @Override
    public void onEnable() {
        lastSpam = 0L;
        phraseIndex = 0;
        nextDelay = (long) spamDelay.getValue();
        cachedRaw = null;
    }

    @Override
    public void onDisable() {
        lastSpam = 0L;
        phraseIndex = 0;
        cachedRaw = null;
        cachedPhrases = new String[0];
    }

    @Override
    public void onTick() {
        if (!spammer.getValue()) {
            return;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.player.networkHandler == null || mc.world == null) {
            return;
        }

        long now = System.currentTimeMillis();
        if (lastSpam != 0L && now - lastSpam < nextDelay) {
            return;
        }

        String phrase = nextPhrase();
        if (phrase == null) {
            return;
        }

        lastSpam = now;
        nextDelay = nextInterval(now);

        sendingSpam = true;
        try {
            if (phrase.charAt(0) == '/') {
                mc.player.networkHandler.sendChatCommand(phrase.substring(1));
                return;
            }
            mc.player.networkHandler.sendChatMessage(phrase);
        } finally {
            sendingSpam = false;
        }
    }

    public String processOutgoing(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }
        if (message.charAt(0) == '/') {
            return message;
        }

        String result = message;
        if (leetSpeak.getValue()) {
            result = applyLeet(result);
        }

        if (prefix.getValue()) {
            String value = prefixText.getValue();
            if (!value.isEmpty()) {
                result = value + result;
            }
        }

        if (postfix.getValue() && !sendingSpam) {
            String value = postfixText.getValue();
            if (!value.isEmpty()) {
                result = result + value;
            }
        }

        if (globalChat.getValue() && (result.isEmpty() || result.charAt(0) != '!')) {
            result = "!" + result;
        }

        if (result.length() > CHAT_LIMIT) {
            return result.substring(0, CHAT_LIMIT);
        }
        return result;
    }

    private String nextPhrase() {
        String[] phrases = phrases();
        if (phrases.length == 0) {
            return null;
        }
        if (phraseIndex >= phrases.length) {
            phraseIndex = 0;
        }
        String phrase = phrases[phraseIndex];
        phraseIndex++;
        if (phraseIndex >= phrases.length) {
            phraseIndex = 0;
        }
        return phrase;
    }

    private String[] phrases() {
        String raw = spamText.getValue();
        if (raw == cachedRaw) {
            return cachedPhrases;
        }

        cachedRaw = raw;
        if (raw == null || raw.isEmpty()) {
            cachedPhrases = new String[0];
            return cachedPhrases;
        }

        int count = 1;
        for (int i = 0; i < raw.length(); i++) {
            if (raw.charAt(i) == '|') {
                count++;
            }
        }

        String[] parsed = new String[count];
        int start = 0;
        int filled = 0;
        for (int i = 0; i <= raw.length(); i++) {
            if (i == raw.length() || raw.charAt(i) == '|') {
                String part = raw.substring(start, i).trim();
                if (!part.isEmpty()) {
                    parsed[filled++] = part;
                }
                start = i + 1;
            }
        }

        if (filled == parsed.length) {
            cachedPhrases = parsed;
            return cachedPhrases;
        }

        String[] compact = new String[filled];
        System.arraycopy(parsed, 0, compact, 0, filled);
        cachedPhrases = compact;
        return cachedPhrases;
    }

    private long nextInterval(long now) {
        long base = (long) spamDelay.getValue();
        if (base < 1500L) {
            base = 1500L;
        }
        long jitter = (base * (now & 7L)) / 100L;
        return base + jitter;
    }

    private static String applyLeet(String input) {
        int length = input.length();
        StringBuilder builder = new StringBuilder(length + 8);
        for (int i = 0; i < length; i++) {
            switch (input.charAt(i)) {
                case 'п', 'П' -> builder.append("II");
                case 'л', 'Л' -> builder.append("JI");
                case 'з', 'З' -> builder.append('3');
                case 'ь', 'Ь' -> builder.append('b');
                case 'г', 'Г' -> builder.append('7');
                case 'б', 'Б' -> builder.append('6');
                case 'в', 'В' -> builder.append('V');
                case 'ч', 'Ч' -> builder.append('4');
                case 'о', 'О' -> builder.append('0');
                case 'т', 'Т' -> builder.append('T');
                case 'я', 'Я' -> builder.append('R');
                case 'е', 'Е' -> builder.append('E');
                default -> builder.append(input.charAt(i));
            }
        }
        return builder.toString();
    }
}
