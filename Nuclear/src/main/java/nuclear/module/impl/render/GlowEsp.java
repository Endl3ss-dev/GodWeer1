package nuclear.module.impl.render;

import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import nuclear.control.Manager;
import nuclear.control.events.Event;
import nuclear.control.events.impl.render.EventRender;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.BooleanSetting;
import nuclear.module.settings.imp.ColorSetting;
import nuclear.module.settings.imp.ModeSetting;
import nuclear.module.settings.imp.MultiBoxSetting;
import nuclear.utils.math.TargetUtil;
import nuclear.utils.render.ColorUtils;
import nuclear.utils.render.RenderUtils;

@ModuleDescriptor(
    name = "GlowEsp",
    type = ModuleCategory.Render,
    desc = "Highlights players and items with animated glow colors"
)
public class GlowEsp extends Module {
    private final MultiBoxSetting targets = new MultiBoxSetting(
        "Targets",
        new BooleanSetting("Игроков", true),
        new BooleanSetting("Друзей", true),
        new BooleanSetting("Себя", false),
        new BooleanSetting("Предметы", true),
        new BooleanSetting("Голых", true)
    );
    private final ModeSetting colorMode = new ModeSetting("Color mode", "Dynamic", "Dynamic", "Static");
    private final ColorSetting staticColor = new ColorSetting("Color", ColorUtils.rgba(90, 170, 255, 180));
    private final ColorSetting friendColor = new ColorSetting("Friend color", ColorUtils.rgba(90, 255, 120, 180));
    private final ColorSetting itemColor = new ColorSetting("Item color", ColorUtils.rgba(255, 220, 120, 180));

    public GlowEsp() {
        addSettings(targets, colorMode, staticColor, friendColor, itemColor);
    }

    @Override
    public boolean onEvent(Event event) {
        if (!(event instanceof EventRender) || !((EventRender) event).isRender3D() || mc.player == null || mc.world == null) {
            return false;
        }

        for (Entity entity : mc.world.getAllEntities()) {
            if (!shouldRender(entity)) {
                continue;
            }
            renderEntityBox(entity, resolveColor(entity));
        }
        return false;
    }

    private boolean shouldRender(Entity entity) {
        if (entity == null || !entity.isAlive()) {
            return false;
        }
        if (entity == mc.player && mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON && !targets.get("Себя")) {
            return false;
        }
        return TargetUtil.isEntityTarget(entity, targets);
    }

    private int resolveColor(Entity entity) {
        if (entity instanceof ItemEntity) {
            return itemColor.get();
        }
        if (entity instanceof PlayerEntity && Manager.FRIEND_MANAGER != null && Manager.FRIEND_MANAGER.isFriend(entity.getName().getString())) {
            return friendColor.get();
        }
        if (colorMode.is("Static")) {
            return staticColor.get();
        }

        int base = staticColor.get();
        int shade = ColorUtils.darken(base, 0.45f);
        int index = entity.getEntityId() * 35 + mc.player.ticksExisted * 4;
        return ColorUtils.gradient(6, index, base, shade);
    }

    private void renderEntityBox(Entity entity, int color) {
        RenderUtils.Render3D.drawBox(
            entity.getBoundingBox().offset(
                -mc.getRenderManager().info.getProjectedView().x,
                -mc.getRenderManager().info.getProjectedView().y,
                -mc.getRenderManager().info.getProjectedView().z
            ),
            color
        );
    }
}
