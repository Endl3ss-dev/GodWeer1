package antileak.base.client.modules.impl.combat;

import net.minecraft.entity.player.PlayerEntity;
import antileak.base.Punch;
import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventAttackEntity;
import antileak.base.client.modules.Module;

public class NoFriendDamage extends Module {

    public static NoFriendDamage INSTANCE = new NoFriendDamage();

    public NoFriendDamage() {
        super("NoFriendDamage", "Полностью убирает урон по .friend", ModuleCategory.COMBAT);
    }

    @EventLink
    public void onAttack(EventAttackEntity event) {
        if (mc.player == null || mc.world == null) return;

        if (!(event.getTarget() instanceof PlayerEntity target)) return;

        String name = target.getName().getString();

        if (Punch.INSTANCE.friendStorage.isFriend(name)) {
            event.cancel();
        }
    }
}
