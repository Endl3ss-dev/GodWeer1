package org.privates.client.managers.module.impl.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.optifine.http.HttpUtils;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.screen.ai.FriendAIChatScreen;
import org.privates.client.utils.keyboard.Keyboard;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@ModuleInfo(name = "FriendAI", category = Category.MISC, autoEnabled = true, allowDisable = false)
public class FriendAI extends Module {
    private final Minecraft mc = Minecraft.getInstance();
    private final BindSetting openChatBind = new BindSetting(this, "Открыть AI-чат", Keyboard.KEY_I.getKey(), true);

    public Minecraft getMc() {
        return mc;
    }

    public BindSetting getOpenChatBind() {
        return openChatBind;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        Integer key = openChatBind.getValue();
        if (key != null && Keyboard.isKeyDown(key)) {
            mc.displayScreen(new FriendAIChatScreen());
        }
    }

    public static String askGemini(String userMsg) {
        final String API_KEY = "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";
        final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
        final String MODEL = "meta-llama/llama-3-70b-instruct";

        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", MODEL);

            JsonArray messages = new JsonArray();
            JsonObject message = new JsonObject();
            message.addProperty("role", "user");
            message.addProperty("content", userMsg);
            messages.add(message);

            requestBody.add("messages", messages);
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + API_KEY);
            headers.put("Content-Type", "application/json");
            headers.put("Accept", "application/json");
            headers.put("Accept-Charset", "UTF-8");
            String response = HttpUtils.post(API_URL, headers, requestBody.toString().getBytes(StandardCharsets.UTF_8));

            if (response == null || response.isEmpty()) {
                return "Ошибка: пустой ответ от сервера";
            }
            JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

            if (jsonResponse.has("error")) {
                return "Ошибка API: " + jsonResponse.get("error").getAsString();
            }

            JsonArray choices = jsonResponse.getAsJsonArray("choices");
            if (choices == null || choices.size() == 0) {
                return "Нет доступных ответов от AI";
            }

            JsonObject firstChoice = choices.get(0).getAsJsonObject();
            JsonObject msg = firstChoice.getAsJsonObject("message");
            String content = msg.get("content").getAsString();

            return content.isEmpty() ? "AI не предоставил ответ" : content;

        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при запросе к AI: " + e.getMessage();
        }
    }
}

