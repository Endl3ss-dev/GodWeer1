package never.lose.modules.api;

import never.lose.managers.BindManager;
import never.lose.managers.CfgManager;
import never.lose.modules.api.setting.Setting;
import never.lose.utils.animation.Animation;
import never.lose.utils.animation.Easing;

import java.util.ArrayList;
import java.util.List;

public class Module {

    private final String name;
    private final String desc;
    private final ModuleCategory category;
    private int key;
    private boolean enabled;
    private final Animation animation = new Animation(Easing.BACK_OUT, 450);
    private final List<Setting> settings = new ArrayList<>();
    private boolean settingsCached;

    public Module() {
        ModuleInfo information = getClass().getAnnotation(ModuleInfo.class);
        if (information == null) {
            throw new IllegalStateException(getClass().getName() + " is missing @ModuleInfo");
        }
        this.name = information.name();
        this.desc = information.desc();
        this.category = information.category();
        this.key = information.key();
    }

    public List<Setting> getSettings() {
        if (!settingsCached) {
            for (java.lang.reflect.Field field : this.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(this);
                    if (value instanceof Setting setting) {
                        settings.add(setting);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            settingsCached = true;
        }
        return settings;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) {
                onEnable();
            } else {
                onDisable();
            }
            CfgManager.markDirty();
        }
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onTick() {
    }

    public void onRender3D(net.minecraft.client.util.math.MatrixStack matrices, float tickDelta) {
    }

    public void onRenderHandPre() {
    }

    public void onRenderHandPost() {
    }

    public void onChat(String message) {
    }

    public void toggle() {
        setEnabled(!isEnabled());
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        if (this.key == key) {
            return;
        }
        this.key = key;
        BindManager.onModuleKeyChanged();
    }

    public void setKeySilent(int key) {
        this.key = key;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Animation getAnimation() {
        return animation;
    }
}
