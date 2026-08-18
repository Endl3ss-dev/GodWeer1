package nuclear.module.api;

import com.google.gson.JsonObject;
import nuclear.control.Manager;
import nuclear.control.events.Event;
import nuclear.module.TypeList;
import nuclear.module.impl.other.ClientSounds;
import nuclear.module.settings.Setting;
import nuclear.module.settings.imp.BindSetting;
import nuclear.module.settings.imp.BooleanSetting;
import nuclear.module.settings.imp.ColorSetting;
import nuclear.module.settings.imp.ModeSetting;
import nuclear.module.settings.imp.MultiBoxSetting;
import nuclear.module.settings.imp.SliderSetting;
import nuclear.module.settings.imp.TextSetting;
import nuclear.utils.IMinecraft;
import nuclear.utils.SoundUtils;
import nuclear.utils.anim.Animation;
import nuclear.utils.anim.animations.DecelerateAnimation;

public abstract class Module extends AbstractFeature implements IMinecraft {
    private final Annotation legacyDescriptor = getClass().getAnnotation(Annotation.class);
    private final ModuleDescriptor descriptor = getClass().getAnnotation(ModuleDescriptor.class);

    public TypeList category;
    public String name;
    public String desc;
    public int bind;
    public Animation animation = new DecelerateAnimation(250, 1.0);
    public boolean state;

    public Module() {
        initializeProperties();
    }

    public Module(String name, TypeList category) {
        this(name, category, "", 0);
    }

    public Module(String name, ModuleCategory category) {
        this(name, category.toTypeList(), "", 0);
    }

    public Module(String name, TypeList category, String desc) {
        this(name, category, desc, 0);
    }

    public Module(String name, ModuleCategory category, String desc) {
        this(name, category.toTypeList(), desc, 0);
    }

    public Module(String name, TypeList category, String desc, int bind) {
        this.name = name;
        this.category = category;
        this.desc = desc;
        this.bind = bind;
        this.state = false;
        init();
    }

    public Module(String name, ModuleCategory category, String desc, int bind) {
        this(name, category.toTypeList(), desc, bind);
    }

    public void init() {
    }

    private void initializeProperties() {
        this.name = resolveName();
        this.category = resolveCategory();
        this.state = false;
        this.bind = resolveBind();
        this.desc = resolveDescription();
    }

    private String resolveName() {
        if (legacyDescriptor != null) {
            return legacyDescriptor.name();
        }
        if (descriptor != null) {
            return descriptor.name();
        }
        return getClass().getSimpleName();
    }

    private TypeList resolveCategory() {
        if (legacyDescriptor != null) {
            return legacyDescriptor.type();
        }
        if (descriptor != null) {
            return descriptor.type().toTypeList();
        }
        return TypeList.Other;
    }

    private int resolveBind() {
        if (legacyDescriptor != null) {
            return legacyDescriptor.key();
        }
        if (descriptor != null) {
            return descriptor.key();
        }
        return 0;
    }

    private String resolveDescription() {
        if (legacyDescriptor != null) {
            return legacyDescriptor.desc();
        }
        if (descriptor != null) {
            return descriptor.desc();
        }
        return "";
    }

    public String getDescription() {
        if (legacyDescriptor != null) {
            return legacyDescriptor.desc();
        }
        if (descriptor != null) {
            return descriptor.desc();
        }
        return "No module description";
    }

    public void setStateNotUsing(boolean enabled) {
        this.state = enabled;
    }

    public void setState(boolean enabled) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if (!enabled) {
            onDisable();
        } else {
            onEnable();
        }

        this.state = enabled;
    }

    public void toggle() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        this.state = !this.state;

        if (!this.state) {
            onDisable();
        } else {
            onEnable();
        }

        if (Manager.NOTIFICATION_MANAGER != null) {
            Manager.NOTIFICATION_MANAGER.add(
                this.name + " " + (this.state ? "enabled" : "disabled"),
                "Function Debug",
                3,
                this.category
            );
        }

        if (Manager.FUNCTION_MANAGER == null) {
            return;
        }

        ClientSounds clientSounds = Manager.FUNCTION_MANAGER.clientSounds;
        if (clientSounds != null && clientSounds.state) {
            String mode = clientSounds.soundMode.get();
            float volume = clientSounds.volume.getValue().floatValue();
            String soundFile = mode.equalsIgnoreCase("Pop")
                ? (this.state ? "popenable.wav" : "popdisable.wav")
                : (this.state ? "Function_ON.wav" : "Function_OFF.wav");
            SoundUtils.playSound(soundFile, volume, false);
        }
    }

    public boolean isState() {
        return this.state;
    }

    protected void onDisable() {
    }

    protected void onEnable() {
    }

    public JsonObject save() {
        JsonObject object = new JsonObject();
        object.addProperty("bind", this.bind);
        object.addProperty("state", this.state);

        for (Setting setting : getSettingList()) {
            String settingName = setting.getName();
            switch (setting.getType()) {
                case BOOLEAN_OPTION -> object.addProperty(settingName, ((BooleanSetting) setting).get());
                case SLIDER_SETTING -> object.addProperty(settingName, ((SliderSetting) setting).getValue().floatValue());
                case MODE_SETTING -> object.addProperty(settingName, ((ModeSetting) setting).getIndex());
                case COLOR_SETTING -> object.addProperty(settingName, ((ColorSetting) setting).get());
                case MULTI_BOX_SETTING -> ((MultiBoxSetting) setting).options.forEach(
                    option -> object.addProperty(option.getName(), option.get())
                );
                case BIND_SETTING -> object.addProperty(settingName, ((BindSetting) setting).getKey());
                case TEXT_SETTING -> object.addProperty(settingName, ((TextSetting) setting).text);
                default -> {
                }
            }
        }
        return object;
    }

    public void load(JsonObject object, boolean start) {
        this.bind = resolveBind();
        if (!start) {
            setState(false);
        } else {
            setStateNotUsing(false);
        }

        if (object != null) {
            if (object.has("bind")) {
                this.bind = object.get("bind").getAsInt();
            }
            if (object.has("state")) {
                if (start) {
                    setStateNotUsing(object.get("state").getAsBoolean());
                } else {
                    setState(object.get("state").getAsBoolean());
                }
            }

            for (Setting setting : getSettingList()) {
                String settingName = setting.getName();
                switch (setting.getType()) {
                    case BOOLEAN_OPTION -> {
                        if (object.has(settingName)) {
                            ((BooleanSetting) setting).set(object.get(settingName).getAsBoolean());
                        } else {
                            ((BooleanSetting) setting).resetToDefault();
                        }
                    }
                    case SLIDER_SETTING -> {
                        if (object.has(settingName)) {
                            ((SliderSetting) setting).setValue((float) object.get(settingName).getAsDouble());
                        } else {
                            ((SliderSetting) setting).resetToDefault();
                        }
                    }
                    case MODE_SETTING -> {
                        if (object.has(settingName)) {
                            ((ModeSetting) setting).setIndex(object.get(settingName).getAsInt());
                        } else {
                            ((ModeSetting) setting).resetToDefault();
                        }
                    }
                    case BIND_SETTING -> {
                        if (object.has(settingName)) {
                            ((BindSetting) setting).setKey(object.get(settingName).getAsInt());
                        } else {
                            ((BindSetting) setting).resetToDefault();
                        }
                    }
                    case COLOR_SETTING -> {
                        if (object.has(settingName)) {
                            ((ColorSetting) setting).setValue(object.get(settingName).getAsInt());
                        } else {
                            ((ColorSetting) setting).resetToDefault();
                        }
                    }
                    case MULTI_BOX_SETTING -> ((MultiBoxSetting) setting).options.forEach(option -> {
                        if (object.has(option.getName())) {
                            option.set(object.get(option.getName()).getAsBoolean());
                        } else {
                            option.resetToDefault();
                        }
                    });
                    case TEXT_SETTING -> {
                        if (object.has(settingName)) {
                            ((TextSetting) setting).text = object.get(settingName).getAsString();
                        } else {
                            ((TextSetting) setting).resetToDefault();
                        }
                    }
                    default -> {
                    }
                }
            }
        } else {
            for (Setting setting : getSettingList()) {
                switch (setting.getType()) {
                    case BOOLEAN_OPTION -> ((BooleanSetting) setting).resetToDefault();
                    case SLIDER_SETTING -> ((SliderSetting) setting).resetToDefault();
                    case MODE_SETTING -> ((ModeSetting) setting).resetToDefault();
                    case BIND_SETTING -> ((BindSetting) setting).resetToDefault();
                    case COLOR_SETTING -> ((ColorSetting) setting).resetToDefault();
                    case MULTI_BOX_SETTING -> ((MultiBoxSetting) setting).options.forEach(BooleanSetting::resetToDefault);
                    case TEXT_SETTING -> ((TextSetting) setting).resetToDefault();
                    default -> {
                    }
                }
            }
        }
    }

    public abstract boolean onEvent(Event event);
}
