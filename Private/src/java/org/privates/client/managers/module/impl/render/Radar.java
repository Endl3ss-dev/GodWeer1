package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.player.PlayerEntity;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Radar", category = Category.RENDER, description = "Показывает игроков на мини-радаре")
public class Radar extends Module {

    public static Radar getInstance() {
        return Instance.get(Radar.class);
    }

    final SliderSetting x = new SliderSetting(this, "X", 10f, 0f, 1000f, 5f);
    final SliderSetting y = new SliderSetting(this, "Y", 10f, 0f, 1000f, 5f);
    final SliderSetting size = new SliderSetting(this, "Size", 100f, 50f, 200f, 5f);
    final SliderSetting range = new SliderSetting(this, "Range", 50f, 10f, 100f, 5f);

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (mc.player == null || mc.world == null) return;

        MatrixStack matrix = event.getMatrix();
        float radarX = x.getValue();
        float radarY = y.getValue();
        float radarSize = size.getValue();

        float centerX = radarX + radarSize / 2f;
        float centerY = radarY + radarSize / 2f;
        float half = radarSize / 2f;
        float maxRange = range.getValue();

        RenderUtil.Rounded.roundedRect(matrix, radarX, radarY, radarSize, radarSize,
                ColorUtil.getColor(0, 0, 0, 120), Round.of(5f));
        RenderUtil.Rounded.drawRoundOutline(matrix, radarX, radarY, radarSize, radarSize,
                5f, 1f, ColorUtil.getColor(255, 255, 255, 45));

        RenderUtil.Rounded.roundedRect(matrix, centerX - 1f, centerY - 1f, 2f, 2f,
                ColorUtil.getColor(255, 255, 255, 255), Round.of(1f));

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || !player.isAlive()) continue;

            double dist = mc.player.getDistance(player);
            if (dist > maxRange) continue;

            double diffX = player.getPosX() - mc.player.getPosX();
            double diffZ = player.getPosZ() - mc.player.getPosZ();
            double angle = Math.atan2(diffZ, diffX) - Math.toRadians(mc.player.rotationYaw + 90.0f);

            float normalized = (float) (dist / maxRange);
            float dotRadius = normalized * (half - 4f);

            float dotX = centerX + (float) Math.cos(angle) * dotRadius;
            float dotY = centerY + (float) Math.sin(angle) * dotRadius;

            int color = Excellent.inst().friendManager().isFriend(player.getGameProfile().getName())
                    ? ColorUtil.getColor(0, 255, 0, 255)
                    : ColorUtil.getColor(255, 0, 0, 255);

            RenderUtil.Rounded.roundedRect(matrix, dotX - 2f, dotY - 2f, 4f, 4f, color, Round.of(1.5f));
        }
    }
}

