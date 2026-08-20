package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.input.KeyboardPressEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.managers.module.settings.impl.StringSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "CommandHelper", category = Category.MISC, description = "Помогает с командами если лень настраивать .macro")
public class CommandHelper extends Module {

    private final BindSetting homeBind = new BindSetting(this, "Home", -1);
    private final BindSetting spawnBind = new BindSetting(this, "Spawn", -1);
    private final BindSetting repairBind = new BindSetting(this, "Repair", -1);
    private final BindSetting rtp = new BindSetting(this, "Rtp", -1);
    private final BindSetting ahSellBind = new BindSetting(this, "Ah Sell", -1);

    private final StringSetting ahPrice = new StringSetting(this, "Цена продажи", "10", true)
            .onAction(this::validateNumberInput);

    public static CommandHelper getInstance() {
        return Instance.get(CommandHelper.class);
    }

    @EventHandler
    public void onKeyPress(KeyboardPressEvent event) {
        if (event.getScreen() != null || mc.player == null) return;

        handleCommand(event, homeBind, "/home");
        handleCommand(event, spawnBind, "/spawn");
        handleCommand(event, repairBind, "/repair");
        handleCommand(event, rtp, "/rtp");
        handleAhSellCommand(event);
    }

    private void handleCommand(KeyboardPressEvent event, BindSetting bind, String command) {
        if (event.isKey(bind.getValue())) {
            mc.player.sendChatMessage(command);
        }
    }

    private void handleAhSellCommand(KeyboardPressEvent event) {
        if (event.isKey(ahSellBind.getValue())) {
            String price = ahPrice.getValue().trim();
            if (isValidNumber(price)) {
                mc.player.sendChatMessage("/ah sell " + price);
            } else {
                ChatUtil.addTextWithError("Некорректная цена! Используйте только цифры.");
            }
        }
    }

    private void validateNumberInput() {
        String value = ahPrice.getValue();
        if (!isValidNumber(value)) {
            ChatUtil.addTextWithError("Ошибка ввода! Допустимы только цифры.");
            ahPrice.set("100000");
        }
    }

    private boolean isValidNumber(String input) {
        return input.matches("\\d+");
    }
}