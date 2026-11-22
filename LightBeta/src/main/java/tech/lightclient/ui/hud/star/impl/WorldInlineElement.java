package tech.lightclient.ui.hud.star.impl;

import net.minecraft.client.network.ServerInfo;

import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.client.utils.server.ServerTPSManager;
import tech.lightclient.modules.api.Function;

public class WorldInlineElement extends InlineElement {
    private final InlineValue cords = new InlineValue("coords", "Координаты");
    private final InlineValue server = new InlineValue("server", "Сервер");
    private final InlineValue tps = new InlineValue("tps", "TPS", "TPS");

    public WorldInlineElement(Function module) {
        super(module, "Star Мир", "icons/hud/world.png", 10.0f, 10.0f);
        values.add(cords);
        values.add(server);
        values.add(tps);
    }

    public WorldInlineElement(Function module, Dragging dragging) {
        super(module, dragging, "Star Мир", "icons/hud/world.png");
        values.add(cords);
        values.add(server);
        values.add(tps);
    }

    @Override
    protected void updateValues() {
        if (mc.player == null) {
            return;
        }
        // Формат координат в стиле нового HUD: "123x 45y 67z".
        cords.update(String.format("%sx %sy %sz",
                Math.round(mc.player.getX()), Math.round(mc.player.getY()), Math.round(mc.player.getZ())));

        ServerInfo entry = mc.getCurrentServerEntry();
        String address = entry == null ? null : entry.address;
        boolean compact = boolSetting(scope(), "compact_server", true);
        String name = address;
        if (address != null && compact) {
            int colon = address.indexOf(':');
            String host = colon >= 0 ? address.substring(0, colon) : address;
            String[] parts = host.split("\\.");
            name = parts.length > 0 ? parts[0] : host;
        }
        server.update(address == null ? "Одиночная игра" : name, address == null ? "" : address);

        try {
            float value = ServerTPSManager.getInstance().getTPS();
            String text = String.format("%.1f", value).replace(",", ".");
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            tps.update(text);
        } catch (Throwable ignored) {
            tps.update("20");
        }
    }

    @Override
    public String settingName() {
        return "Информация";
    }

    @Override
    protected float hitboxWidth() {
        return dragging.getWidth();
    }

    @Override
    protected float hitboxHeight() {
        return dragging.getHeight();
    }
}
