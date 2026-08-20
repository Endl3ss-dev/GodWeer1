package fun.volon.client.modules.impl.movement;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import fun.volon.api.QClient;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.implement.EventOnTravelPost;
import fun.volon.api.utils.player.MoveUtils;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.settings.implement.BooleanSetting;
import fun.volon.client.modules.settings.implement.FloatSetting;

import java.util.List;

public class SpeedAres2 extends Module implements QClient {

    public static SpeedAres2 INSTANCE = new SpeedAres2();

    private final FloatSetting speed = new FloatSetting("Скорость", 5.5f, 2.0f, 9.0f, 0.1f);
    private final FloatSetting boxExpand = new FloatSetting("Расширение бокса", 0.45f, 0.3f, 0.6f, 0.01f);
    private final BooleanSetting onlyPlayers = new BooleanSetting("Только игроки", true);
    private final BooleanSetting armorStands = new BooleanSetting("Стойки брони", false);

    public SpeedAres2() {
        super("SpeedAres2", "Ускорение при столкновении с сущностями", ModuleCategory.MOVEMENT);
        addSettings(speed, boxExpand, onlyPlayers, armorStands);
    }

    @EventLink
    private void onTravelPost(EventOnTravelPost event) {
        if (mc.player == null || mc.world == null) return;
        
        if (!MoveUtils.isMoving()) return;

        Box expandedBox = mc.player.getBoundingBox().expand(boxExpand.getValue().doubleValue());
        List<Entity> entities = mc.world.getOtherEntities(mc.player, expandedBox);

        boolean hasCollision = false;

        for (Entity entity : entities) {
            if (!isValidEntity(entity)) continue;
            
            if (entity.getBoundingBox().intersects(expandedBox)) {
                hasCollision = true;
                break;
            }
        }

        if (hasCollision) {
            applySpeedBoost();
        }
    }

    private boolean isValidEntity(Entity entity) {
        if (entity == mc.player) return false;

        if (onlyPlayers.isState()) {
            return entity instanceof PlayerEntity;
        }

        if (entity instanceof ArmorStandEntity) {
            return armorStands.isState();
        }

        return entity instanceof PlayerEntity;
    }

    private void applySpeedBoost() {
        if (mc.player == null) return;

        // Используем формулу из LeonWare: MoveUtil.forward() с множителем скорости
        double baseSpeed = speed.getValue().doubleValue() / 10.0;
        
        // Применяем скорость напрямую через setMotion
        MoveUtils.setMotion(baseSpeed);
    }
}

