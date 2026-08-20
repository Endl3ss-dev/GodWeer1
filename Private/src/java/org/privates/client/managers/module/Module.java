package org.privates.client.managers.module;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;
import org.apache.commons.lang3.NotImplementedException;
import org.privates.client.Excellent;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.module.impl.misc.Notifications;
import org.privates.client.managers.module.impl.render.Hud;
import org.privates.client.managers.module.settings.Setting;
import org.privates.client.screen.notification.Notification;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.other.SoundUtil;
import org.privates.client.utils.render.font.StripFont;

import java.util.List;

@Data
public abstract class Module implements IMinecraft {
    private final List<Setting<?>> settings = new ObjectArrayList<>();
    private ModuleInfo moduleInfo;
    private String name;
    private Category category;
    private boolean enabled;
    private boolean autoEnabled;
    private boolean allowDisable;
    private boolean hidden;
    private int key;
    private final Animation animation = new Animation();
    private final StripFont stripFont = new StripFont();

    public Module() {
        Class<? extends Module> clazz = this.getClass();
        ModuleInfo moduleInfo = clazz.getAnnotation(ModuleInfo.class);

        if (moduleInfo == null) {
            throw new NotImplementedException("@ModuleInfo annotation not found on " + clazz.getSimpleName());
        }

        this.moduleInfo = moduleInfo;
        this.name = moduleInfo.name().trim().replaceAll(" ", "");
        this.category = moduleInfo.category();
        this.autoEnabled = moduleInfo.autoEnabled();
        this.allowDisable = moduleInfo.allowDisable();
        this.hidden = moduleInfo.hidden();
        this.key = moduleInfo.key();
        setup();
    }

    public void toggle() {
        setEnabled(!enabled);
        Excellent.inst().notifManager().add(new Notification(org.privates.client.screen.notification.NotificationType.INFO, String.format("%s %s", moduleInfo.name(), enabled ? "включен" : "выключен"), 2000));
    }

    public void setEnabled(final boolean enabled) {
        setEnabled(enabled, true);
    }

    public void setEnabled(final boolean enabled, boolean notification) {
        if (this.enabled == enabled || (!this.allowDisable && !enabled)) {
            return;
        }
        this.enabled = enabled;
        animation.setFromValue(enabled ? 0 : 1);
        animation.setToValue(enabled ? 1 : 0);
        animation.setValue(animation.getFromValue());
        animation.run(enabled ? 1 : 0, .5f, Easings.EXPO_OUT, false);

        Notifications notifications = Notifications.getInstance();
        if (!this.isHidden() && notification && notifications.isEnabled() && notifications.sound().getValue()) {
            SoundUtil.playSound(enabled ? "enabled.wav" : "disabled.wav", notifications.volume().getValue() / 100F);
        }

        // Notify Dynamic Island about module toggle
        if (Hud.getInstance() != null && Hud.getInstance().dynamicIslandRenderer() != null) {
            Hud.getInstance().dynamicIslandRenderer().onModuleToggle(this, enabled);
        }

        if (enabled) {
            superEnable();
        } else {
            superDisable();
        }
        //if (Hud.getInstance().checks().getValue("Keybinds") || Hud.getInstance().checks().getValue("ArrayList")) {
        //animation.setStart((enabled) ? 0 : 1);
        //}
    }

    private void eventEnable() {
        Excellent.eventHandler().subscribe(this);
    }

    private void eventDisable() {
        Excellent.eventHandler().unsubscribe(this);
    }

    private void superEnable() {
        if (mc.player != null) onEnable();
        eventEnable();
    }

    private void superDisable() {
        if (mc.player != null) onDisable();
        eventDisable();
    }

    public void setup() {
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public static List<Module> getModulesByCategory(Category category) {
        return org.privates.client.Excellent.inst().moduleManager().get(category);
    }
}