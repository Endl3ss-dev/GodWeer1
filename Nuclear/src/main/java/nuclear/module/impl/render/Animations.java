package nuclear.module.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3f;
import nuclear.control.events.Event;
import nuclear.control.events.impl.player.EventHandsRender;
import nuclear.control.events.impl.player.EventHotbarRender;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.ModeSetting;
import nuclear.module.settings.imp.SliderSetting;

@ModuleDescriptor(
    name = "Animations",
    type = ModuleCategory.Render,
    desc = "Applies custom hand and hotbar animation transforms"
)
public class Animations extends Module {
    private final ModeSetting handMode = new ModeSetting("Hand mode", "Slide", "Slide", "Spin", "Rise");
    private final SliderSetting handScale = new SliderSetting("Hand scale", 1.0f, 0.6f, 1.8f, 0.05f);
    private final SliderSetting handRotation = new SliderSetting("Hand rotation", 20.0f, 0.0f, 90.0f, 1.0f);
    private final SliderSetting hotbarScale = new SliderSetting("Hotbar scale", 1.0f, 0.7f, 1.5f, 0.05f);
    private final SliderSetting hotbarOffset = new SliderSetting("Hotbar Y offset", 0.0f, -12.0f, 12.0f, 1.0f);

    public Animations() {
        addSettings(handMode, handScale, handRotation, hotbarScale, hotbarOffset);
    }

    @Override
    public boolean onEvent(Event event) {
        if (mc.player == null) {
            return false;
        }

        if (event instanceof EventHandsRender.Pre) {
            transformHands((EventHandsRender.Pre) event);
        } else if (event instanceof EventHotbarRender.Pre) {
            transformHotbar((EventHotbarRender.Pre) event);
        }
        return false;
    }

    private void transformHands(EventHandsRender.Pre event) {
        MatrixStack stack = event.getStack();
        float swing = mc.player.getSwingProgress(event.getPart());
        float rotation = handRotation.getValue().floatValue();
        float scale = handScale.getValue().floatValue();
        float time = (System.currentTimeMillis() % 4000L) / 4000.0f;

        stack.scale(scale, scale, scale);

        if (handMode.is("Spin")) {
            stack.rotate(Vector3f.YP.rotationDegrees((time * 360.0f) + swing * rotation));
            stack.rotate(Vector3f.XP.rotationDegrees(rotation * 0.6f));
            return;
        }

        if (handMode.is("Rise")) {
            stack.translate(0.0, -0.15 + swing * 0.25, 0.0);
            stack.rotate(Vector3f.ZP.rotationDegrees(-rotation * swing));
            return;
        }

        stack.translate(-swing * 0.35, Math.sin(time * Math.PI * 2.0f) * 0.02, 0.0);
        stack.rotate(Vector3f.XP.rotationDegrees(rotation * swing));
    }

    private void transformHotbar(EventHotbarRender.Pre event) {
        MatrixStack stack = event.getStack();
        float scale = hotbarScale.getValue().floatValue();
        float offset = hotbarOffset.getValue().floatValue();

        stack.translate(0.0, offset, 0.0);
        stack.scale(scale, scale, 1.0f);
    }
}
