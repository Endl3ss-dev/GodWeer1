package nuclear.module.impl.render;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import nuclear.control.events.Event;
import nuclear.control.events.impl.render.EventRender;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.ColorSetting;
import nuclear.module.settings.imp.ModeSetting;
import nuclear.module.settings.imp.SliderSetting;
import nuclear.utils.render.ColorUtils;
import nuclear.utils.render.RenderUtils;

@ModuleDescriptor(
    name = "Pets",
    type = ModuleCategory.Render,
    desc = "Renders cosmetic follower pets around the player"
)
public class Pets extends Module {
    private final ModeSetting petType = new ModeSetting("Pet", "Orb", "Orb", "Fox", "Cat");
    private final SliderSetting petCount = new SliderSetting("Count", 2.0f, 1.0f, 5.0f, 1.0f);
    private final SliderSetting distance = new SliderSetting("Distance", 1.4f, 0.5f, 3.0f, 0.1f);
    private final SliderSetting bobbing = new SliderSetting("Bobbing", 0.25f, 0.0f, 0.8f, 0.05f);
    private final ColorSetting color = new ColorSetting("Color", ColorUtils.rgba(255, 170, 90, 180));

    public Pets() {
        addSettings(petType, petCount, distance, bobbing, color);
    }

    @Override
    public boolean onEvent(Event event) {
        if (!(event instanceof EventRender) || !((EventRender) event).isRender3D() || mc.player == null) {
            return false;
        }

        renderPets();
        return false;
    }

    private void renderPets() {
        Vector3d base = mc.player.getPositionVec();
        Vector3d look = mc.player.getLookVec().normalize();
        Vector3d behind = base.subtract(look.scale(distance.getValue().doubleValue()));
        int count = petCount.getValue().intValue();
        double time = (System.currentTimeMillis() % 10000L) / 1000.0;

        for (int index = 0; index < count; index++) {
            double angle = time * getSpeedMultiplier() + (Math.PI * 2.0 * index / Math.max(1, count));
            double radius = getOrbitRadius();
            double x = behind.x + Math.cos(angle) * radius;
            double y = behind.y + 0.3 + Math.sin(angle * 2.0) * bobbing.getValue().doubleValue();
            double z = behind.z + Math.sin(angle) * radius;

            Vector3d view = mc.getRenderManager().info.getProjectedView();
            AxisAlignedBB box = new AxisAlignedBB(
                x - 0.18, y, z - 0.18,
                x + 0.18, y + getPetHeight(), z + 0.18
            ).offset(-view.x, -view.y, -view.z);

            RenderUtils.Render3D.drawBox(box, color.get());
        }
    }

    private double getOrbitRadius() {
        if (petType.is("Fox")) {
            return 0.7;
        }
        if (petType.is("Cat")) {
            return 0.9;
        }
        return 0.55;
    }

    private double getPetHeight() {
        if (petType.is("Fox")) {
            return 0.5;
        }
        if (petType.is("Cat")) {
            return 0.42;
        }
        return 0.3;
    }

    private double getSpeedMultiplier() {
        if (petType.is("Fox")) {
            return 1.8;
        }
        if (petType.is("Cat")) {
            return 1.4;
        }
        return 2.4;
    }
}
