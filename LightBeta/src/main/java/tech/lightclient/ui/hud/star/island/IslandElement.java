package tech.lightclient.ui.hud.star.island;

import java.util.ArrayList;
import java.util.List;

import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.modules.api.Function;
import tech.lightclient.ui.hud.star.HudElement;
import tech.lightclient.ui.hud.star.PopupMenu;
import tech.lightclient.ui.hud.star.SAnim;
import tech.lightclient.ui.hud.star.SEasing;

public class IslandElement extends HudElement {
    private final List<IslandStatus> statuses = new ArrayList<>();
    private final SAnim widthAnim = new SAnim(500L, 48.0f, SEasing.BAKEK_SIZE);
    private final SAnim heightAnim = new SAnim(500L, 15.0f, SEasing.BAKEK_SIZE);

    public IslandElement(Function module) {
        super(module, "Star Остров", "icons/hud/island.png", 0.0f, 0.0f);

        statuses.add(new NotificationStatus());
        statuses.add(new BossBarStatus());
        statuses.add(new DefaultStatus());
    }

    @Override
    protected void position(EventRender.Screen.Hud event) {
        int viewportWidth = mc.getWindow().getScaledWidth();
        IslandStatus active = activeStatus();
        float targetWidth = Math.max(20.0f, active.width);
        widthAnim.update(targetWidth);
        heightAnim.update(active.height);
        width = Math.max(20.0f, widthAnim.getValue());
        height = Math.max(15.0f, heightAnim.getValue());
        x = viewportWidth / 2.0f - width / 2.0f;
        y = 7.0f;
    }

    @Override
    protected void update(EventRender.Screen.Hud event) {
        IslandStatus active = activeStatus();
        for (IslandStatus status : statuses) {
            status.getAnimation().update(status == active ? 1.0f : 0.0f);
        }
        setSelfVisible(true);
    }

    @Override
    protected void draw(EventRender.Screen.Hud event, float alpha) {
        if (alpha <= 0.02f) {
            return;
        }
        Renderer2D r = event.getRenderer();

        drawClientRect(r, scope(), x, y, width, height, 7.0f, alpha, dragging.isDragging());

        IslandStatus active = activeStatus();
        r.pushRoundedClipRect((int) x, (int) y, (int) Math.ceil(width), (int) Math.ceil(height), 7.0f, 7.0f, 7.0f, 7.0f);
        try {
            if (active.getAnimation().getValue() >= 0.999f) {
                active.draw(event, r, this, x, y, alpha);
            } else {
                for (IslandStatus status : statuses) {
                    float sa = status.getAnimation().getValue();
                    if (sa > 0.001f) {
                        status.draw(event, r, this, x, y, sa * alpha);
                    }
                }
            }
        } finally {
            r.popClipRect();
        }
    }

    private IslandStatus activeStatus() {
        for (IslandStatus status : statuses) {
            if (isStatusSelected(status) && status.canShow()) {
                return status;
            }
        }
        return statuses.get(statuses.size() - 1);
    }

    private boolean isStatusSelected(IslandStatus status) {
        if (statuses.indexOf(status) == statuses.size() - 1) {
            return true;
        }
        return boolSetting(scope(), "status_" + status.id(), true);
    }

    @Override
    public void fillSettingsPopup(PopupMenu popup) {
        for (int i = 0; i < statuses.size() - 1; i++) {
            IslandStatus status = statuses.get(i);
            popup.checkbox(status.label(), () -> boolSetting(scope(), "status_" + status.id(), true),
                    value -> setBoolSetting(scope(), "status_" + status.id(), value));
        }
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public String settingName() {
        return "Ватермарка";
    }
}
