package antileak.base.client.modules.impl.player;

import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventBinding;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.BindSetting;

public class SendCoords extends Module {

    public static SendCoords INSTANCE = new SendCoords();

    private final BindSetting sendBind = new BindSetting("Отправить", -1);

    public SendCoords() {
        super("SendCoords", "Пишет координаты в чат", ModuleCategory.PLAYER);
        addSettings(sendBind);
    }

    @EventLink
    public void onKey(EventBinding event) {
        if (event.getKey() != sendBind.getKey()) return;

        if (mc.player == null) return;

        int x = (int) mc.player.getX();
        int z = (int) mc.player.getZ();
        mc.player.networkHandler.sendChatMessage("! " + x + " " + z);
    }
}
