package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.network.play.client.CResourcePackStatusPacket;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.other.Instance;
@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "SRPSpoof", category = Category.MISC, description = "Не дает загрузить рп")
public class SRPSpoof extends Module {
    public static SRPSpoof getInstance() {
        return Instance.get(SRPSpoof.class);
    }

    @EventHandler
    public void onEvent(PacketEvent event) {
        if (event.getPacket() instanceof CResourcePackStatusPacket wrapper) {
            wrapper.action = CResourcePackStatusPacket.Action.SUCCESSFULLY_LOADED;
        }
    }
}