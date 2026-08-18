package nuclear.module.impl.render;

import net.minecraft.entity.Entity;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import nuclear.control.events.Event;
import nuclear.control.events.impl.render.EventRender;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.ColorSetting;
import nuclear.utils.render.ColorUtils;
import nuclear.utils.render.RenderUtils;

@ModuleDescriptor(name = "PotionRadius", type = ModuleCategory.Render, desc = "Показывает радиус попадания зелья")
public class PotionRadius extends Module {
    private static final double SPLASH_RADIUS = 4.0;

    private final ColorSetting splashColor = new ColorSetting("Цвет всплеска", ColorUtils.rgba(90, 180, 255, 180));
    private final ColorSetting lingeringColor = new ColorSetting("Цвет облака", ColorUtils.rgba(190, 110, 255, 180));

    public PotionRadius() {
        addSettings(splashColor, lingeringColor);
    }

    @Override
    public boolean onEvent(Event event) {
        if (!(event instanceof EventRender render) || !render.isRender3D() || mc.player == null || mc.world == null) {
            return false;
        }

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof PotionEntity potion) {
                renderSplashRadius(potion);
            } else if (entity instanceof AreaEffectCloudEntity cloud) {
                renderCloudRadius(cloud);
            }
        }
        return false;
    }

    private void renderSplashRadius(PotionEntity potion) {
        Vector3d landing = predictLanding(potion);
        AxisAlignedBB box = new AxisAlignedBB(
            landing.x - SPLASH_RADIUS,
            landing.y,
            landing.z - SPLASH_RADIUS,
            landing.x + SPLASH_RADIUS,
            landing.y + 0.15,
            landing.z + SPLASH_RADIUS
        ).offset(-mc.getRenderManager().info.getProjectedView().x, -mc.getRenderManager().info.getProjectedView().y, -mc.getRenderManager().info.getProjectedView().z);

        RenderUtils.Render3D.drawBox(box, splashColor.get());
    }

    private void renderCloudRadius(AreaEffectCloudEntity cloud) {
        double radius = cloud.getRadius();
        AxisAlignedBB box = new AxisAlignedBB(
            cloud.getPosX() - radius,
            cloud.getPosY(),
            cloud.getPosZ() - radius,
            cloud.getPosX() + radius,
            cloud.getPosY() + 0.15,
            cloud.getPosZ() + radius
        ).offset(-mc.getRenderManager().info.getProjectedView().x, -mc.getRenderManager().info.getProjectedView().y, -mc.getRenderManager().info.getProjectedView().z);

        RenderUtils.Render3D.drawBox(box, lingeringColor.get());
    }

    private Vector3d predictLanding(PotionEntity potion) {
        Vector3d position = potion.getPositionVec();
        Vector3d motion = potion.getMotion();
        Vector3d lastPosition = position;

        for (int i = 0; i < 120; i++) {
            lastPosition = position;
            position = position.add(motion);
            motion = motion.scale(0.99).subtract(0.0, 0.05, 0.0);

            BlockRayTraceResult hit = mc.world.rayTraceBlocks(new RayTraceContext(
                lastPosition,
                position,
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                potion
            ));

            if (hit.getType() == RayTraceResult.Type.BLOCK) {
                return hit.getHitVec();
            }

            if (position.y <= 0.0) {
                return position;
            }
        }

        return position;
    }
}
