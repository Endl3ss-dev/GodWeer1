package org.privates.client.managers.module.impl.player;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.annotations.Rage;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.managers.module.settings.impl.StringSetting;
import org.privates.client.utils.chat.ChatUtil;

@Category3
@Rage
@Getter
@Accessors(fluent = true)
@ModuleInfo(name = "Fake Message", description = "Выводит поддельное сообщение от имени других игроков", category = Category.MISC)
public class FakeMessage extends Module {
    private final ModeSetting mode = new ModeSetting(this, "Режим", "РиллиВорлд");
    private final ModeSetting donate = new ModeSetting(this, "Донат",
            "Player",
            "Hero",
            "Titan",
            "Avenger",
            "Overlord",
            "Magister",
            "Imperator",
            "Dragon",
            "Bull",
            "Rabbit",
            "Tiger",
            "Dracula",
            "Bunny",
            "Hydra",
            "Cobra",
            "Media",
            "YT",
            "D. Helper",
            "Helper",
            "ML. Moder",
            "Moder",
            "Moder+",
            "ST. Moder",
            "GL. Moder",
            "ML. Admin",
            "Admin",
            "Свой"
    );

    private final StringSetting nickname = new StringSetting(this, "Ник", "Raccoon");
    private final StringSetting message = new StringSetting(this, "Сообщение", "бустит!");
    private final StringSetting customDonate = new StringSetting(this, "Донат", "ꔨ");

    private final BooleanSetting globalMessage = new BooleanSetting(this, "Глобальный чат", true);
    private final BooleanSetting rwPlus = new BooleanSetting(this, "ReallyWorld Plus", false);

    private final SliderSetting globalDonate = new SliderSetting(this, "Расстояние можду индикатором чата и донатом", 1F, 0F, 3F, 1F);

    public FakeMessage() {
        customDonate.setVisible(() -> donate.is("Свой"));
    }

    @Override
    protected void onEnable() {
        String channel = (globalMessage.getValue() ? TextFormatting.GOLD : TextFormatting.DARK_AQUA) + "▣";
        String plus = rwPlus.getValue() ? (TextFormatting.BOLD + "" + TextFormatting.GOLD + "+" + TextFormatting.RESET) : "";
        String donatePart = "";
        String donateVal = donate.getValue();
        if (!"Нет".equalsIgnoreCase(donateVal)) {
            String donateSymbol = switch (donateVal) {
                case "Player" -> "ꔀ";
                case "Hero" -> "ꔄ";
                case "Titan" -> "ꔈ";
                case "Avenger" -> "ꔒ";
                case "Overlord" -> "ꔖ";
                case "Magister" -> "ꔠ";
                case "Imperator" -> "ꔤ";
                case "Dragon" -> "ꔨ";
                case "Bull" -> "ꔲ";
                case "Rabbit" -> "ꕒ";
                case "Tiger" -> "ꔶ";
                case "Dracula" -> "ꕄ";
                case "Bunny" -> "ꕖ";
                case "Hydra" -> "ꕀ";
                case "Cobra" -> "ꕈ";
                case "Media" -> "ꔁ";
                case "YT" -> "ꔅ";
                case "D. Helper" -> "ꕠ";
                case "Helper" -> "ꔉ";
                case "ML. Moder" -> "ꔓ";
                case "Moder" -> "ꔗ";
                case "Moder+" -> "ꔡ";
                case "ST. Moder" -> "ꔥ";
                case "GL. Moder" -> "ꔩ";
                case "ML. Admin" -> "ꔳ";
                case "Admin" -> "ꔷ";
                case "Свой" -> customDonate.getValue();
                default -> "ꔀ";
            };
            donatePart = TextFormatting.RESET + donateSymbol;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(channel);
        if (!donatePart.isEmpty()) {
            int spacing = Math.round(globalDonate.getValue());
            for (int i = 0; i < spacing; i++) {
                sb.append(" ");
            }
            sb.append(donatePart);
        }
        sb.append(" ").append(TextFormatting.GRAY).append(nickname.getValue()).append(plus);
        sb.append(" ").append(TextFormatting.DARK_GRAY).append("» ");
        sb.append(TextFormatting.RESET).append(message.getValue());

        ChatUtil.addText(sb.toString());
        setEnabled(false);
    }
}