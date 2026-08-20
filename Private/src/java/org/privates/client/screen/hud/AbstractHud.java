package org.privates.client.screen.hud;

import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;

public abstract class AbstractHud {
    Animation animation = new Animation();

    public void update(float value) {
        animation.update();
        animation.run(value, 0.25, Easings.SINE_OUT, true);
    }

    public float animValue() {
        return animation.get();
    }

    public int clientColor() {
        return ColorUtil.multAlpha(theme().clientColor(), animValue());
    }

    public int textColor() {
        return ColorUtil.multAlpha(theme().textColor(), animValue());
    }

    public int textAccentColor() {
        return ColorUtil.multAlpha(theme().textAccentColor(), animValue());
    }

    public int iconColor() {
        return ColorUtil.multAlpha(theme().iconColor(), animValue());
    }

    public Theme theme() {
        return Theme.getInstance();
    }
    
    // Default implementation for 3D world rendering
    public void renderInWorld(Render3DLastEvent event) {
        // Override in subclasses to implement 3D world rendering
    }
}
