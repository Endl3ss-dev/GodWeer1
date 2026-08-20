package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Cube3D", category = Category.RENDER, description = "Рендерит 3D кубы в мире")
public class Cube3D extends Module {

    public static Cube3D getInstance() {
        return Instance.get(Cube3D.class);
    }

    final ModeSetting mode = new ModeSetting(this, "Режим", "Вращающийся", "Статичный", "Контур");
    final SliderSetting count = new SliderSetting(this, "Количество", 20.0f, 5.0f, 100.0f, 5.0f);
    final SliderSetting radius = new SliderSetting(this, "Радиус", 30.0f, 10.0f, 100.0f, 5.0f);
    final SliderSetting size = new SliderSetting(this, "Размер", 0.5f, 0.1f, 2.0f, 0.1f);
    final SliderSetting minHeight = new SliderSetting(this, "Мин. высота", 5.0f, 0.0f, 50.0f, 5.0f);
    final SliderSetting maxHeight = new SliderSetting(this, "Макс. высота", 30.0f, 10.0f, 100.0f, 5.0f);
    final ColorSetting color = new ColorSetting(this, "Цвет", new Color(100, 200, 255, 200).getRGB());
    final SliderSetting alpha = new SliderSetting(this, "Прозрачность", 0.8f, 0.05f, 1.0f, 0.05f);
    final SliderSetting rotationSpeed = new SliderSetting(this, "Скорость вращения", 1.0f, 0.1f, 5.0f, 0.1f).setVisible(() -> mode.is("Вращающийся"));
    final SliderSetting lineWidth = new SliderSetting(this, "Толщина линий", 2.0f, 0.5f, 5.0f, 0.5f).setVisible(() -> mode.is("Контур"));
    final BooleanSetting rainbow = new BooleanSetting(this, "Радуга", false);

    final List<CubePosition> cubes = new ArrayList<>();
    long lastUpdateTime = 0L;

    @Override
    protected void onEnable() {
        super.onEnable();
        generateCubes();
    }

    @EventHandler
    public void onRender(Render3DLastEvent event) {
        if (mc.player == null || mc.world == null) return;

        long now = System.currentTimeMillis();
        if (cubes.isEmpty() || cubes.size() != count.getValue().intValue()) {
            generateCubes();
            lastUpdateTime = now;
        } else if (now - lastUpdateTime > 10000L) {
            generateCubes();
            lastUpdateTime = now;
        }

        renderCubes(event);
    }

    private void renderCubes(Render3DLastEvent event) {
        String renderMode = mode.getValue();
        int total = cubes.size();
        float baseSize = size.getValue();
        float baseAlpha = alpha.getValue();
        float lWidth = lineWidth.getValue();
        long now = System.currentTimeMillis();

        for (int i = 0; i < total; i++) {
            CubePosition c = cubes.get(i);
            int rgb = rainbow.getValue() ? getRainbowColor(i, total) : color.getValue();
            int cFill = ColorUtil.replAlpha(rgb, (int) (baseAlpha * 255.0f));
            int cOutline = ColorUtil.replAlpha(rgb, 255);

            float cubeSize = baseSize;
            if ("Вращающийся".equals(renderMode)) {
                float pulse = (float) Math.sin((now * 0.0015f * rotationSpeed.getValue()) + i * 0.35f) * 0.22f;
                cubeSize = Math.max(0.05f, baseSize + pulse * baseSize);
            }

            float half = cubeSize * 0.5f;
            AxisAlignedBB bb = new AxisAlignedBB(
                    c.x - half, c.y - half, c.z - half,
                    c.x + half, c.y + half, c.z + half
            );

            if ("Контур".equals(renderMode)) {
                RenderUtil3D.drawBoundingBox(event.getMatrix(), bb, cOutline, lWidth, false, false);
            } else {
                RenderUtil3D.drawFilledBoundingBox(event.getMatrix(), bb, cFill, false, false);
                RenderUtil3D.drawBoundingBox(event.getMatrix(), bb, cOutline, 1.0f, false, false);
            }
        }
    }

    private void generateCubes() {
        cubes.clear();
        if (mc.player == null) return;

        Random random = new Random();
        Vector3d p = mc.player.getPositionVec();

        int amount = count.getValue().intValue();
        float r = radius.getValue();
        float hMin = minHeight.getValue();
        float hMax = maxHeight.getValue();

        for (int i = 0; i < amount; i++) {
            double ang = random.nextDouble() * Math.PI * 2.0;
            double dist = random.nextDouble() * r;
            double x = p.x + Math.cos(ang) * dist;
            double y = p.y + hMin + random.nextDouble() * (hMax - hMin);
            double z = p.z + Math.sin(ang) * dist;
            cubes.add(new CubePosition(x, y, z));
        }
    }

    private int getRainbowColor(int index, int total) {
        float hue = (float) index / (float) Math.max(1, total);
        return Color.HSBtoRGB(hue, 0.8f, 1.0f);
    }

    private static final class CubePosition {
        final double x;
        final double y;
        final double z;

        CubePosition(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
