package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.annotations.Funtime;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.managers.module.settings.impl.StringSetting;
import org.privates.client.utils.other.Instance;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Category1
@Getter
@Funtime
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ChatSkamer", category = Category.MISC, description = "Скамит людей в чате")
public class ChatSkammer extends Module {

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Эвент", "Игра", "Кастом").set("Эвент");
    private final ModeSetting messageType = new ModeSetting(this, "Тип отправки", "Глобал", "Лс").set("Глобал");

    private final StringSetting amount = new StringSetting(this, "Сумма скама", "100000", true);
    private final StringSetting customMessage = new StringSetting(this, "Кастом сообщение", "", false);
    private final SliderSetting cooldown = new SliderSetting(this, "Кулдаун (сек)", 10, 1, 60, 1);

    private long lastMessageTime = 0;
    private final Random random = new Random();

    public static ChatSkammer getInstance() {
        return Instance.get(ChatSkammer.class);
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        long currentTime = System.currentTimeMillis();
        long cooldownMillis = (long) (cooldown.getValue() * 1000);

        if (currentTime - lastMessageTime >= cooldownMillis) {
            String baseMessage;

            switch (mode.getValue()) {
                case "Эвент" -> baseMessage = "!All тепаю на эвент за " + amount.getValue();
                case "Игра" -> baseMessage = "!All кто скинет больше всего денег, получит талисман крушителя";
                case "Кастом" -> baseMessage = customMessage.getValue();
                default -> baseMessage = "";
            }

            if (baseMessage.isEmpty()) return;

            String finalMessage = baseMessage;

            // Обработка типа отправки
            if (messageType.is("Лс")) {
                // Получаем список игроков
                List<String> players = mc.getConnection().getPlayerInfoMap().stream()
                        .map(NetworkPlayerInfo::getGameProfile)
                        .map(profile -> profile.getName())
                        .filter(name -> !name.equals(mc.player.getName()))
                        .collect(Collectors.toList());

                if (!players.isEmpty()) {
                    String target = players.get(random.nextInt(players.size()));
                    finalMessage = "/m " + target + " " + baseMessage;
                } else {
                    return; // Никого нет для ЛС
                }
            }

            mc.player.sendChatMessage(finalMessage);
            lastMessageTime = currentTime;
        }
    }
}
