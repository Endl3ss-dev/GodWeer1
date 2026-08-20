package fun.volon.api.utils.player;

import fun.volon.api.QClient;
import net.minecraft.client.MinecraftClient;

public class TimerManager implements QClient {
    
    public static void setTimer(float speed) {
        if (mc != null && mc.player != null) {
            mc.player.speed = speed;
        }
    }
    
    public static void reset() {
        setTimer(1.0f);
    }
}
