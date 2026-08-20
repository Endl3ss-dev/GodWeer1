package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.combat.AntiBot;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "PlayerBox", description = "Отрисовывает 3D боксы на игроках", category = Category.RENDER)
public class PlayerBox extends Module {

    final MultiBooleanSetting targetSetting = new MultiBooleanSetting(this, "Цель",
            new BooleanSetting("Игроки", true),
            new BooleanSetting("Мобы", false),
            new BooleanSetting("Животные", false),
            new BooleanSetting("Друзья", true)
    );
    final BooleanSetting showSelf = new BooleanSetting(this, "Показывать себя", false);
    final BooleanSetting fill = new BooleanSetting(this, "Заливка", true);
    final BooleanSetting outline = new BooleanSetting(this, "Обводка", true);
    final SliderSetting lineWidth = new SliderSetting(this, "Толщина линий", 1.5F, 0.5F, 5.0F, 0.1F).setVisible(outline::getValue);

    @EventHandler
    public void onRender3D(Render3DPosedEvent event) {
        if (mc.world == null || mc.player == null || mc.getRenderManager() == null) return;

        for (Entity entity : mc.world.getAllEntities()) {
            if (!isValidEntity(entity)) continue;
            renderEntityBox(event, entity);
        }
    }

    private boolean isValidEntity(Entity entity) {
        if (entity == null || !entity.isAlive()) return false;

        if (entity == mc.player) {
            if (!showSelf.getValue()) return false;
            return !mc.gameSettings.getPointOfView().firstPerson();
        }

        if (entity instanceof PlayerEntity player) {
            if (!targetSetting.getValue("Игроки")) return false;
            if (AntiBot.getInstance().isEnabled() && AntiBot.getInstance().isBot(player)) return false;
            boolean friend = Excellent.inst().friendManager().isFriend(player.getGameProfile().getName());
            if (friend) return targetSetting.getValue("Друзья");
            return true;
        }

        if (entity instanceof MobEntity) return targetSetting.getValue("Мобы");
        if (entity instanceof AnimalEntity) return targetSetting.getValue("Животные");
        return false;
    }

    private void renderEntityBox(Render3DPosedEvent event, Entity entity) {
        AxisAlignedBB bb = getEntityBox(entity, event.getPartialTicks());
        int color = getEntityColor(entity);

        if (fill.getValue()) {
            RenderUtil3D.drawFilledBoundingBox(event.getMatrix(), bb, ColorUtil.replAlpha(color, 50), false, false);
        }
        if (outline.getValue()) {
            RenderUtil3D.drawBoundingBox(event.getMatrix(), bb, ColorUtil.replAlpha(color, 255), lineWidth.getValue(), false, false);
        }
    }

    private AxisAlignedBB getEntityBox(Entity entity, float pt) {
        double ix = lerp(entity.lastTickPosX, entity.getPosX(), pt);
        double iy = lerp(entity.lastTickPosY, entity.getPosY(), pt);
        double iz = lerp(entity.lastTickPosZ, entity.getPosZ(), pt);

        double height = 1.8D;
        if (entity instanceof PlayerEntity player) {
            Pose pose = player.getPose();
            if (pose == Pose.FALL_FLYING) {
                height = 0.6D;
            } else if (pose == Pose.SWIMMING || pose == Pose.CROUCHING) {
                height = 0.9D;
            }
        } else {
            height = entity.getHeight();
        }
        return new AxisAlignedBB(ix - 0.3D, iy, iz - 0.3D, ix + 0.3D, iy + height, iz + 0.3D);
    }

    private double lerp(double start, double end, float progress) {
        return start + (end - start) * progress;
    }

    private int getEntityColor(Entity entity) {
        if (entity instanceof PlayerEntity && Excellent.inst().friendManager().isFriend(entity.getName().getString())) {
            return ColorUtil.rgba(32, 255, 32, 255);
        }
        return ColorUtil.replAlpha(Theme.getInstance().clientColor(), 255);
    }
}
