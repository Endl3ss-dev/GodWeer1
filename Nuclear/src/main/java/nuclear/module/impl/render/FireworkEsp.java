package nuclear.module.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import nuclear.control.events.Event;
import nuclear.control.events.impl.render.EventRender;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.ColorSetting;
import nuclear.utils.font.Fonts;
import nuclear.utils.render.ColorUtils;
import nuclear.utils.render.ProjectUtil;
import nuclear.utils.render.RenderUtils;
import org.joml.Vector2f;

@ModuleDescriptor(name = "FireworkEsp", type = ModuleCategory.Render, desc = "Показывает теги над фейерверками")
public class FireworkEsp extends Module {
    private final ColorSetting tagColor = new ColorSetting("Цвет", ColorUtils.rgba(255, 170, 70, 255));

    public FireworkEsp() {
        addSettings(tagColor);
    }

    @Override
    public boolean onEvent(Event event) {
        if (!(event instanceof EventRender render) || !render.isRender2D() || mc.player == null || mc.world == null) {
            return false;
        }

        renderTags(render.matrixStack);
        return false;
    }

    private void renderTags(MatrixStack matrixStack) {
        for (Entity entity : mc.world.getAllEntities()) {
            if (!(entity instanceof FireworkRocketEntity) || !entity.isAlive()) {
                continue;
            }

            Vector2f screen = ProjectUtil.project2D(entity.getPosX(), entity.getPosY() + entity.getHeight() + 0.15, entity.getPosZ());
            if (screen.x == Float.MAX_VALUE || screen.y == Float.MAX_VALUE) {
                continue;
            }

            String text = String.format("Firework %.1fs", entity.ticksExisted / 20.0f);
            float width = Fonts.newcode[13].getWidth(text);

            RenderUtils.Render2D.drawRoundedRect(screen.x - width / 2.0f - 3.0f, screen.y - 2.0f, width + 6.0f, Fonts.newcode[13].getFontHeight() + 3.0f, 1.0f, ColorUtils.rgba(10, 10, 10, 150));
            Fonts.newcode[13].drawCenteredString(matrixStack, text, screen.x, screen.y, tagColor.get());
        }
    }
}
