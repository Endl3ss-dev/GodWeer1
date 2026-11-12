package tech.lightclient.modules.impl.combat;

import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.entity.EventAttack;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;

@FunctionAdd(name = "NoFriendDamage", alias = "No Friend Damage", category = Category.Combat, description = "Отключает урон по друзьям")
public class NoFriendDamage extends Function {
    @EventHandler
    public void onAttack(EventAttack.Swing event) {
        if (event.getTarget() != null && LightClient.getFriendStorage().isFriend(event.getTarget().getName().getString())) {
            event.cancel();
        }
    }

    @EventHandler
    public void onAttack(EventAttack.Hurt event) {
        if (event.getTarget() != null && LightClient.getFriendStorage().isFriend(event.getTarget().getName().getString())) {
            event.cancel();
        }
    }
}
