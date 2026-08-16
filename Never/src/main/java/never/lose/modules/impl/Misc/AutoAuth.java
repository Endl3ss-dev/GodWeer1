package never.lose.modules.impl.Misc;

import never.lose.modules.api.Module;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.ModuleInfo;
import never.lose.modules.api.setting.StringSetting;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@ModuleInfo(name = "AutoAuth", desc = "Автоматически логинит или регистрирует вас", category = ModuleCategory.MISC)
public class AutoAuth extends Module {

    public final StringSetting password = new StringSetting("Пароль", randomPassword());

    private long lastSend;

    @Override
    public void onEnable() {
        lastSend = 0L;
    }

    @Override
    public void onChat(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        String stripped = message.replaceAll("§.", "");
        String pass = password.getValue();
        Map<String, String> commands = new HashMap<>();
        commands.put("/login", "/login " + pass);
        commands.put("/l", "/l " + pass);
        commands.put("/register", "/register " + pass + " " + pass);
        commands.put("/reg", "/reg " + pass + " " + pass);

        if (System.currentTimeMillis() - lastSend < 1000L) {
            return;
        }

        for (String token : stripped.split(" ")) {
            String command = commands.get(token.toLowerCase());
            if (command != null && net.minecraft.client.MinecraftClient.getInstance().player != null) {
                net.minecraft.client.MinecraftClient.getInstance().player.networkHandler.sendChatCommand(command.substring(1));
                lastSend = System.currentTimeMillis();
                setEnabled(false);
                break;
            }
        }
    }

    private static String randomPassword() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            builder.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return builder.toString();
    }
}
