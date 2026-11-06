/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_124
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_5250
 *  net.minecraft.class_5251
 */
package ru.decide.utils.math;

import java.awt.Color;
import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.colors.ColorUtil;

@Environment(value=EnvType.CLIENT)
public final class ChatUtils
implements IMinecraft {
    public static void addChatMessage(String text) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1705 == null || mc.field_1705.method_1743() == null) {
            System.out.println("[Nightly Logs] " + text);
            return;
        }
        class_5250 message = class_2561.method_43470((String)"").method_10852(ChatUtils.gradientText("DecideVisuals", new Color(ColorUtil.fade(90)), new Color(ColorUtil.fade(180)))).method_10852((class_2561)class_2561.method_43470((String)" \u00bb ").method_27692(class_124.field_1080)).method_10852((class_2561)class_2561.method_43470((String)text).method_27692(class_124.field_1068));
        mc.field_1705.method_1743().method_1812((class_2561)message);
    }

    public static void addChatMessageDev(String text) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1705 == null || mc.field_1705.method_1743() == null) {
            System.out.println("[Nightly Logs] " + text);
            return;
        }
        class_5250 message = class_2561.method_43470((String)"").method_10852(ChatUtils.gradientText("DecideVisuals [Dev]", new Color(ColorUtil.fade(90)), new Color(ColorUtil.fade(180)))).method_10852((class_2561)class_2561.method_43470((String)" \u00bb ").method_27692(class_124.field_1080)).method_10852((class_2561)class_2561.method_43470((String)text).method_27692(class_124.field_1068));
        mc.field_1705.method_1743().method_1812((class_2561)message);
    }

    public static class_2561 gradientText(String text, Color start, Color end) {
        class_5250 result = class_2561.method_43473();
        int length = text.length();
        for (int i = 0; i < length; ++i) {
            float ratio = (float)i / (float)(length - 1);
            int r = (int)((float)start.getRed() * (1.0f - ratio) + (float)end.getRed() * ratio);
            int g = (int)((float)start.getGreen() * (1.0f - ratio) + (float)end.getGreen() * ratio);
            int b = (int)((float)start.getBlue() * (1.0f - ratio) + (float)end.getBlue() * ratio);
            class_5251 mcColor = class_5251.method_27717((int)(r << 16 | g << 8 | b));
            class_5250 charText = class_2561.method_43470((String)String.valueOf(text.charAt(i))).method_27661().method_27694(style -> style.method_27703(mcColor));
            result = result.method_27661().method_10852((class_2561)charText);
        }
        return result;
    }

    public static class_2561 solidText(String text, int color) {
        class_5251 mcColor = class_5251.method_27717((int)color);
        return class_2561.method_43470((String)text).method_27694(style -> style.method_27703(mcColor));
    }

    @Generated
    private ChatUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

