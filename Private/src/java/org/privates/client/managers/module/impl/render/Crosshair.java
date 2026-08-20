package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.util.math.MathHelper;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.api.interfaces.IWindow;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtilOLD;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Crosshair", category = Category.RENDER, description = "Прицел ебать наконецто добавил")
public class Crosshair extends Module implements IWindow {
    public static Crosshair getInstance() {
        return Instance.get(Crosshair.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Кастомный", "Кругляшок");


    private final BooleanSetting animate = new BooleanSetting(this, "Анимация удара", true);
    private final BooleanSetting dot = new BooleanSetting(this, "Точка", true).setVisible(() -> mode.is("Кастомный"));
    private final BooleanSetting tCrosshair = new BooleanSetting(this, "T-образный", false).setVisible(() -> mode.is("Кастомный"));
    private final SliderSetting width1 = new SliderSetting(this, "Ширина", 5, 0, 10, 1).setVisible(() -> mode.is("Кастомный"));
    private final SliderSetting height1 = new SliderSetting(this, "Высота", 5, 0, 10, 1).setVisible(() -> mode.is("Кастомный"));
    private final SliderSetting gap = new SliderSetting(this, "Точка", 3, 0, 10, 1).setVisible(() -> mode.is("Кастомный"));
    private final SliderSetting thickness = new SliderSetting(this, "Толщина", 1, 1, 10, 1).setVisible(() -> mode.is("Кастомный"));
    public Animation animation = new Animation();

    @EventHandler
    public void onRender(Render2DEvent event) {
        if (!mc.gameSettings.getPointOfView().equals(PointOfView.FIRST_PERSON)) return;
        MatrixStack matrix = event.getMatrix();
        double centerX = scaled().x / 2F;
        double centerY = scaled().y / 2F;

        double cwidth = this.width1.getValue().doubleValue();
        double cheight = this.height1.getValue().doubleValue();

        int color = -1;

        float thickness = this.thickness.getValue().floatValue();
        float swingProgress = mc.player.swingProgress;
        float sin = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        animation.update();
        if (animate.getValue())
            animation.run(sin, 0.05);

        if (mode.is("Кастомный")) {
            float gap = (float) (this.gap.getValue().floatValue() + (animate.getValue() ? (animation.getValue() * 7) : 0));
            if (dot.getValue()) {
                RectUtilOLD.drawRect(matrix, (float) (centerX - 0.5), (float) (centerY - 0.5), (float) (centerX - 0.5 + 1), (float) (centerY - 0.5 + 1), color);
            }
            // up
            int outlineColor = ColorUtil.getColor(0, 0, 0, 255); // чёрная обводка

            if (!tCrosshair.getValue()) {

                // up
                RectUtilOLD.drawRect(matrix,
                        (float) (centerX - (thickness / 2F) - 0.5f),
                        (float) (centerY - gap - cheight - 0.5f),
                        (float) (centerX - (thickness / 2F) + thickness + 0.5f),
                        (float) (centerY - gap - cheight + cheight + 0.5f),
                        outlineColor);
                RectUtilOLD.drawRect(matrix,
                        (float) (centerX - (thickness / 2F)),
                        (float) (centerY - gap - cheight),
                        (float) (centerX - (thickness / 2F) + thickness),
                        (float) (centerY - gap - cheight + cheight),
                        color);
            }

            // down
            RectUtilOLD.drawRect(matrix,
                    (float) (centerX - (thickness / 2F) - 0.5f),
                    (float) (centerY + gap - 0.5f),
                    (float) (centerX - (thickness / 2F) + thickness + 0.5f),
                    (float) (centerY + gap + cheight + 0.5f),
                    outlineColor);
            RectUtilOLD.drawRect(matrix,
                    (float) (centerX - (thickness / 2F)),
                    (float) (centerY + gap),
                    (float) (centerX - (thickness / 2F) + thickness),
                    (float) (centerY + gap + cheight),
                    color);

            // left
            RectUtilOLD.drawRect(matrix,
                    (float) (centerX - gap - cwidth - 0.5f),
                    (float) (centerY - (thickness / 2F) - 0.5f),
                    (float) (centerX - gap - cwidth + cwidth + 0.5f),
                    (float) (centerY - (thickness / 2F) + thickness + 0.5f),
                    outlineColor);
            RectUtilOLD.drawRect(matrix,
                    (float) (centerX - gap - cwidth),
                    (float) (centerY - (thickness / 2F)),
                    (float) (centerX - gap - cwidth + cwidth),
                    (float) (centerY - (thickness / 2F) + thickness),
                    color);

            // right
            RectUtilOLD.drawRect(matrix,
                    (float) (centerX + gap - 0.5f),
                    (float) (centerY - (thickness / 2F) - 0.5f),
                    (float) (centerX + gap + cwidth + 0.5f),
                    (float) (centerY - (thickness / 2F) + thickness + 0.5f),
                    outlineColor);
            RectUtilOLD.drawRect(matrix,
                    (float) (centerX + gap),
                    (float) (centerY - (thickness / 2F)),
                    (float) (centerX + gap + cwidth),
                    (float) (centerY - (thickness / 2F) + thickness),
                    color);


        }

    }

}
