package never.lose.modules.impl.Render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import never.lose.modules.api.Module;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.ModuleInfo;

@ModuleInfo(name = "FreeLook", desc = "Свободный обзор камеры от третьего лица", category = ModuleCategory.RENDER)
public class FreeLook extends Module {

    private float cameraYaw;
    private float cameraPitch;
    private boolean active;

    public float getCameraYaw() {
        return cameraYaw;
    }

    public float getCameraPitch() {
        return cameraPitch;
    }

    public boolean isActive() {
        return isEnabled() && active;
    }

    public void addRotation(double deltaX, double deltaY) {
        cameraYaw += (float) deltaX * 0.15F;
        cameraPitch = Math.max(-90.0F, Math.min(90.0F, cameraPitch + (float) deltaY * 0.15F));
    }

    @Override
    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options == null) {
            return;
        }
        cameraYaw = mc.player.getYaw();
        cameraPitch = mc.player.getPitch();
        mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
        active = true;
    }

    @Override
    public void onDisable() {
        active = false;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options != null) {
            mc.options.setPerspective(Perspective.FIRST_PERSON);
        }
    }
}
