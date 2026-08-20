package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.AttackEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.other.Instance;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "NoFriendDamage", category = Category.COMBAT, description = "Ну кароче канселит урон по фредну")
public class NoFriendDamage extends Module {
    public static NoFriendDamage getInstance() {
        return Instance.get(NoFriendDamage.class);
    }

    @EventHandler

    public void onEvent(AttackEvent event) {
        if (event.getTarget() instanceof PlayerEntity player && Excellent.inst().friendManager().isFriend(TextFormatting.removeFormatting(player.getGameProfile().getName()))) {
            event.cancel();
        }
    }
}
