package tech.lightclient.modules.impl.render;

import tech.lightclient.client.events.impl.client.TickEvent;
import tech.lightclient.client.events.impl.render.EventCamera;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.player.rotation.SensUtility;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BindSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import org.lwjgl.glfw.GLFW;

@FunctionAdd(name = "FreeLook", alias = "Free Look", category = Category.Render, description = "Вращай камерой, не меняя направление персонажа")
public class FreeLook extends Function {
    public final BindSetting key = new BindSetting("Клавиша", GLFW.GLFW_KEY_LEFT_ALT);

    private boolean active;
    private float cameraYaw;
    private float cameraPitch;

    public FreeLook() {
        addSettings(key);
    }

    @EventHandler
    public void onUpdate(TickEvent event) {
        if (mc.player == null || mc.getWindow() == null) {
            active = false;
            return;
        }

        if (mc.currentScreen != null) {
            if (active) {
                active = false;
                mc.options.setPerspective(Perspective.FIRST_PERSON);
            }
            return;
        }

        boolean pressed = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), key.get()) == GLFW.GLFW_PRESS;
        if (pressed && !active) {
            active = true;
            cameraYaw = mc.player.getYaw();
            cameraPitch = mc.player.getPitch();
            mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
        } else if (!pressed && active) {
            active = false;
            mc.options.setPerspective(Perspective.FIRST_PERSON);
        }
    }

    @EventHandler
    public void onCamera(EventCamera event) {
        if (!isActive()) {
            return;
        }

        event.setCameraClip(true);
        event.cancel();
    }

    public boolean isActive() {
        return isState() && active && mc.currentScreen == null;
    }

    public void suspendForScreen() {
        if (!active) {
            return;
        }
        active = false;
        if (mc.options != null) {
            mc.options.setPerspective(Perspective.FIRST_PERSON);
        }
    }


    public float getCameraYaw() {
        return cameraYaw;
    }

    public float getCameraPitch() {
        return cameraPitch;
    }

    public void addRotation(float yawDelta, float pitchDelta) {
        float sensitivity = SensUtility.getGCDValue();
        cameraYaw += yawDelta * sensitivity;
        cameraPitch = Math.max(-90f, Math.min(90f, cameraPitch + pitchDelta * sensitivity));
    }
}

