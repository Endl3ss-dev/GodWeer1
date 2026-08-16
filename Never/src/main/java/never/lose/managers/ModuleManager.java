package never.lose.managers;

import net.minecraft.client.MinecraftClient;
import never.lose.modules.api.Module;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.setting.BooleanSetting;
import never.lose.modules.impl.Combat.SexAura;
import never.lose.modules.impl.Combat.ItemSwap;
import never.lose.modules.impl.Misc.AutoAuth;
import never.lose.modules.impl.Movement.Sprint;
import never.lose.modules.impl.Player.DrinkUse;
import never.lose.modules.impl.Misc.ChatPlus;
import never.lose.modules.impl.Player.TapeMouse;
import never.lose.modules.impl.Render.ChinaHat;
import never.lose.modules.impl.Render.Cosmetics;
import never.lose.modules.impl.Render.ClickGui;
import never.lose.modules.impl.Render.Ambience;
import never.lose.modules.impl.Render.FreeLook;
import never.lose.modules.impl.Render.Box3D;
import never.lose.modules.impl.Render.Hands;
import never.lose.modules.impl.Render.ItemPhysic;
import never.lose.modules.impl.Render.Trails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ModuleManager {

    private static ModuleManager instance;

    private final List<Module> modules = new ArrayList<>();

    public static ModuleManager get() {
        if (instance == null) {
            instance = new ModuleManager();
        }
        return instance;
    }

    public void init() {
        modules.clear();
        modules.add(new ClickGui());
        modules.add(new SexAura());
        modules.add(new Sprint());
        modules.add(new ChinaHat());
        modules.add(new Cosmetics());
        modules.add(new FreeLook());
        modules.add(new ItemPhysic());
        modules.add(new Trails());
        modules.add(new Ambience());
        modules.add(new Hands());
        modules.add(new ChatPlus());
        modules.add(new AutoAuth());
        modules.add(new TapeMouse());
        modules.add(new Box3D());
        modules.add(new ItemSwap());
        modules.add(new DrinkUse());
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> get(ModuleCategory category) {
        return modules.stream()
                .filter(module -> module.getCategory() == category)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T get(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String compact = name.replace(" ", "");
        return modules.stream()
                .filter(module -> module.getName().replace(" ", "").equalsIgnoreCase(compact))
                .map(module -> (T) module)
                .findFirst()
                .orElse(null);
    }

    public <T extends Module> T get(Class<T> clazz) {
        return modules.stream()
                .filter(module -> clazz.isAssignableFrom(module.getClass()))
                .map(clazz::cast)
                .findFirst()
                .orElse(null);
    }

    public void handleKey(int key) {
        for (Module module : modules) {
            if (module.getKey() == key) {
                module.toggle();
            }
            for (var setting : module.getSettings()) {
                if (setting instanceof BooleanSetting booleanSetting && booleanSetting.getKey() == key) {
                    booleanSetting.toggle();
                }
            }
        }
    }

    public void tickAnimations() {
        for (Module module : modules) {
            module.getAnimation().run(module.isEnabled());
            for (var setting : module.getSettings()) {
                if (setting instanceof BooleanSetting booleanSetting) {
                    booleanSetting.getAnimation().run(booleanSetting.getValue());
                }
            }
        }
    }

    public void dispatchTick() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onTick();
            }
        }
    }

    public void dispatchRender3D(net.minecraft.client.util.math.MatrixStack matrices, float tickDelta) {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onRender3D(matrices, tickDelta);
            }
        }
    }

    public void dispatchRenderHandPre() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onRenderHandPre();
            }
        }
    }

    public void dispatchRenderHandPost() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onRenderHandPost();
            }
        }
    }

    public void dispatchChat(String message) {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onChat(message);
            }
        }
    }
}
