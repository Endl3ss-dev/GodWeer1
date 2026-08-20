package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.EntityRemoveEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.other.Instance;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "LeaveTracker", category = Category.MISC, description = "Показывает куда ливнул тип уже не ворк вроде")
public class LeaveTracker extends Module {
    public static LeaveTracker getInstance() {
        return Instance.get(LeaveTracker.class);
    }

    @EventHandler
    public void onEvent(EntityRemoveEvent event) {
        if (event.getEntity() instanceof AbstractClientPlayerEntity clientPlayer) {
            if (clientPlayer instanceof ClientPlayerEntity || mc.player.getDistance(clientPlayer) < 100) {
                return;
            }
            float x = (float) Mathf.round(clientPlayer.getPosX(), 1);
            float y = (float) Mathf.round(clientPlayer.getPosY(), 1);
            float z = (float) Mathf.round(clientPlayer.getPosZ(), 1);

            ChatUtil.addText(String.format("Игрок %s телепортировался на координаты xyz: %s, %s, %s", clientPlayer.getGameProfile().getName(), x, y, z));
        }
    }
}