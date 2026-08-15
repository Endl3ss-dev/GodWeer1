package antileak.base.client.modules.impl.player;

import antileak.base.Punch;
import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventBinding;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.BindSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

/**
 * ClickFriend - добавляет/удаляет друзей по настраиваемой кнопке
 */
public class ClickFriend extends Module {

    public static ClickFriend INSTANCE = new ClickFriend();

    private final BindSetting key = new BindSetting("Кнопка", -1);

    public ClickFriend() {
        super("ClickFriend", "Добавляет/удаляет друзей по кнопке", ModuleCategory.PLAYER);
        addSettings(key);
    }

    @EventLink
    public void onKey(EventBinding event) {
        if (event.getKey() != key.getKey()) {
            return;
        }

        handleClick();
    }

    /**
     * Обрабатывает клик по настроенной кнопке
     */
    private void handleClick() {
        if (mc.player == null || mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.ENTITY) {
            return;
        }

        EntityHitResult entityHit = (EntityHitResult) mc.crosshairTarget;
        Entity target = entityHit.getEntity();

        if (!(target instanceof PlayerEntity player)) {
            return;
        }

        // Не добавляем себя в друзья
        if (player == mc.player) {
            return;
        }

        String playerName = player.getName().getString();
        
        // Добавляем или удаляем из друзей
        if (Punch.INSTANCE.friendStorage.isFriend(playerName)) {
            Punch.INSTANCE.friendStorage.remove(playerName);
            mc.player.sendMessage(
                    net.minecraft.text.Text.literal("§c- §f" + playerName + " §7удален из друзей"),
                    false
            );
        } else {
            Punch.INSTANCE.friendStorage.add(playerName);
            mc.player.sendMessage(
                    net.minecraft.text.Text.literal("§a+ §f" + playerName + " §7добавлен в друзья"),
                    false
            );
        }
    }
}
