/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 *  net.minecraft.class_5498
 *  org.joml.Vector2f
 */
package ru.decide.command.impl;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import net.minecraft.class_5498;
import org.joml.Vector2f;
import ru.decide.Client;
import ru.decide.command.Command;
import ru.decide.manager.event_impl.EventDisplay;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.math.ChatUtils;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public class GpsCommand
extends Command
implements IMinecraft {
    private static final class_2960 ARROW_TEX = class_2960.method_60655((String)"decide", (String)"textures/arrow.png");
    private static final float SIZE = 20.0f;
    private static final float RADIUS = 30.0f;
    private Vector2f target = null;

    public GpsCommand() {
        super("gps", ".gps <set|off> <x> <z>", "\u0421\u0442\u0440\u0435\u043b\u043a\u0430 \u043d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435 \u0432\u0435\u0434\u0443\u0449\u0430\u044f \u043a \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u0430\u043c");
        Client.eventHandler().subscribe(this);
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            this.showHelp();
            return;
        }
        switch (args[0].toLowerCase()) {
            case "set": {
                if (args.length < 3) {
                    ChatUtils.addChatMessage("\u00a77\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: \u00a7f.gps set <x> <z>");
                    return;
                }
                try {
                    float x = Float.parseFloat(args[1]);
                    float z = Float.parseFloat(args[2]);
                    this.target = new Vector2f(x, z);
                    ChatUtils.addChatMessage("\u00a7aGPS \u00a77\u2192 \u00a7f" + (int)x + "\u00a77, \u00a7f" + (int)z);
                }
                catch (NumberFormatException ex) {
                    ChatUtils.addChatMessage("\u00a7c\u041e\u0448\u0438\u0431\u043a\u0430: \u043d\u0435\u0432\u0435\u0440\u043d\u044b\u0435 \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b");
                }
                break;
            }
            case "off": {
                this.target = null;
                ChatUtils.addChatMessage("\u00a77GPS \u0441\u0431\u0440\u043e\u0448\u0435\u043d");
                break;
            }
            default: {
                this.showHelp();
            }
        }
    }

    @Override
    public List<String> getSuggestions(String subPrefix) {
        return List.of("set", "off").stream().filter(s -> s.startsWith(subPrefix.toLowerCase())).toList();
    }

    @EventHandler(priority=-500)
    public void onDisplay(EventDisplay e) {
        if (GpsCommand.mc.field_1724 == null || GpsCommand.mc.field_1687 == null) {
            return;
        }
        if (this.target == null) {
            return;
        }
        if (GpsCommand.mc.field_1690.field_1842) {
            return;
        }
        if (!GpsCommand.mc.field_1690.method_31044().equals((Object)class_5498.field_26664)) {
            return;
        }
        double dx = (double)this.target.x + 0.5 - GpsCommand.mc.field_1724.method_23317();
        double dz = (double)this.target.y + 0.5 - GpsCommand.mc.field_1724.method_23321();
        int dst = (int)Math.sqrt(dx * dx + dz * dz);
        float targetScale = 2.0f;
        float currentScale = mc.method_22683().method_4495();
        float scaleFix = targetScale / currentScale;
        int screenWidth = (int)((float)mc.method_22683().method_4486() / scaleFix);
        int screenHeight = (int)((float)mc.method_22683().method_4502() / scaleFix);
        float middleW = (float)screenWidth / 2.0f;
        float middleH = (float)screenHeight / 2.0f - 90.0f;
        float angleToTarget = (float)(-(Math.atan2(dx, dz) * 57.29577951308232));
        float realYaw = angleToTarget - GpsCommand.mc.field_1773.method_19418().method_19330();
        float rad = (float)Math.toRadians(realYaw);
        float posY = middleH - 30.0f;
        float offset = posY + 10.0f - middleH;
        float cx = middleW - offset * (float)Math.sin(rad);
        float cy = middleH + offset * (float)Math.cos(rad);
        int color = ColorUtil.fade(1);
        Client.get().render2D().getTexturePipeline().drawGlowTexture(ARROW_TEX, cx - 10.0f, cy - 10.0f, 20.0f, 20.0f, 0.0f, 0.0f, 1.0f, 1.0f, new int[]{color, color, color, color}, new float[]{0.0f, 0.0f, 0.0f, 0.0f}, 1.0f, realYaw);
        Fonts.sf_regular.drawCentered(dst + "\u043c", cx, cy + 10.0f + 4.0f, 6.0f, ColorUtil.getColor(255, 0.85f));
    }

    private void showHelp() {
        ChatUtils.addChatMessage("\u00a77.gps \u00a7fset \u00a77<x> <z> \u00a78| \u00a77.gps \u00a7foff");
    }
}

