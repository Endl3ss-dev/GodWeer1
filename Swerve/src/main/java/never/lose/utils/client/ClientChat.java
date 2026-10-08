package never.lose.utils.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public final class ClientChat {

    public static final int ACCENT = 0x3C6EF5;
    public static final int TEXT = 0xE8E8E8;
    public static final int MUTED = 0x7E86A0;

    private ClientChat() {
    }

    public static void message(String body) {
        add(prefix().append(literal(body, TEXT)));
    }

    public static void raw(String body) {
        add(literal(body, TEXT));
    }

    public static void raw(String body, int color) {
        add(literal(body, color));
    }

    public static void item(String left, String right) {
        add(literal("[" + left + "]", ACCENT)
                .append(literal(" - ", MUTED))
                .append(literal("[" + right + "]", TEXT)));
    }

    public static MutableText prefix() {
        return literal("[NeverLose] ", ACCENT);
    }

    public static void add(Text text) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.inGameHud == null || text == null) {
            return;
        }
        mc.inGameHud.getChatHud().addMessage(text);
    }

    private static MutableText literal(String value, int color) {
        return Text.literal(value == null ? "" : value).styled(style -> style.withColor(color));
    }
}
