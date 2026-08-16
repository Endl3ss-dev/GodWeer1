package never.lose.utils.client;

import com.google.gson.Gson;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.awt.Color;

import static never.lose.NeverLose.mc;

public interface IClient {
    Gson gson = new Gson();

    default never.lose.NeverLose instance() {
        return never.lose.NeverLose.getInstance();
    }

    static boolean nullCheck() {
        return MinecraftClient.getInstance().world == null || MinecraftClient.getInstance().player == null;
    }

    default String key(int keyCode) {
        if (keyCode == -1) return "-";
        switch (keyCode) {
            case 342 -> { return "LALT"; }
            case 280 -> { return "CAPS"; }
            case 346 -> { return "RALT"; }
        }
        String s = InputUtil.fromKeyCode(keyCode, 0).getTranslationKey()
                .replace("key.keyboard.", "").replace(".", "");
        if (keyCode <= 7) s = "M" + s;
        return s.toUpperCase();
    }

    static Text gradient(String message, Color first, Color end, Boolean... params) {
        MutableText text = Text.empty();
        for (int i = 0; i < message.length(); i++) {
            text.append(Text.empty().append(String.valueOf(message.charAt(i))));
        }
        return text;
    }

    static float getPartialTicksI() {
        return mc.getRenderTickCounter().getTickDelta(true);
    }
}