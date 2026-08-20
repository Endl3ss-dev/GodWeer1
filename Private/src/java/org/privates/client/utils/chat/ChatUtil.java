package org.privates.client.utils.chat;

import lombok.experimental.UtilityClass;
import net.minecraft.util.text.*;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.utils.render.color.ColorUtil;

@UtilityClass
public class ChatUtil implements IMinecraft {

    private boolean checkNull() {
        return mc.player == null;
    }

    public void addTextWithError(final Object message, final Object... objects) {
        addText("[" + TextFormatting.RED + "❌" + TextFormatting.RESET + "] " + message, objects);
    }


    public void addText(final Object message, final Object... objects) {
        if (checkNull()) return;
        if (message == null) {
            addText("Object is null");
            return;
        }

        IFormattableTextComponent component = new StringTextComponent(message.toString().formatted(objects).replace('&', TextFormatting.COLOR_CODE));

        mc.ingameGUI.getChatGUI().printChatMessage(component);
    }

    public void sendText(final Object message, final Object... objects) {
        if (checkNull()) return;
        if (message == null) {
            addText("Object is null");
            return;
        }
        mc.player.sendChatMessage(String.format(message.toString(), objects).replace(TextFormatting.COLOR_CODE, '&'));
    }

    public void sendTextPrintl(final Object message, final Object... objects) {
        if (checkNull()) return;
        if (message == null) {
            addText("Object is null");
            return;
        }
        System.out.printf((message) + "%n", objects);
    }

    public void print(String message) {
        addText(message);
    }

    public IFormattableTextComponent genGradientText(String text, int color1, int color2) {
        IFormattableTextComponent gradientComponent = new StringTextComponent("");
        int[] color = ColorUtil.genGradientForText(color1, color2, text.length());
        int i = 0;
        for (char ch : text.toCharArray()) {
            IFormattableTextComponent component = new StringTextComponent(String.valueOf(ch));
            Style style = new Style(Color.fromInt(color[i]), false, false, false, false, false, null, null, null, null);
            component.setStyle(style);
            gradientComponent.append(component);
            i++;
        }
        return gradientComponent;
    }
}
