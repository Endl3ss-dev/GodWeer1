package tech.lightclient.ui.hud.star.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.gui.screen.ChatScreen;

import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.settings.api.Setting;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.scripting.runtime.ScriptInstance;
import tech.lightclient.scripting.runtime.ScriptManager;
import tech.lightclient.ui.hud.star.HudListElement;
import tech.lightclient.ui.hud.star.KeyNames;
import tech.lightclient.ui.hud.star.PopupMenu;
import tech.lightclient.ui.hud.star.SAnim;
import tech.lightclient.ui.hud.star.SEasing;
import tech.lightclient.ui.hud.star.StarUtil;

public class KeyBindsElement extends HudListElement {
    private static final String SETTING_ALWAYS = "always";

    private final Map<Object, SAnim> animations = new ConcurrentHashMap<>();
    private List<Entry> sortedCache = List.of();

    public KeyBindsElement(Function module) {
        super(module, "Star Бинды", "icons/hud/active_binds.png", 10.0f, 120.0f);
    }

    private record Entry(Object owner, String label, int bind, boolean active) {
    }

    private static boolean isBound(int bind) {
        return bind != -1 && bind != 0;
    }

    private List<Entry> collectEntries() {
        List<Entry> result = new ArrayList<>();
        var manager = LightClient.getFunctionManager();
        if (manager != null) {
            try {
                for (Function function : manager.getVisibleFunctions()) {
                    if (isBound(function.getBind())) {
                        result.add(new Entry(function, function.getName(), function.getBind(), function.isState()));
                    }
                    for (Setting<?> setting : function.getSettings()) {
                        if (setting instanceof BooleanSetting bool && bool.isBound()) {
                            result.add(new Entry(bool, function.getAlias() + ": " + bool.getName(),
                                    bool.getBind(), bool.get()));
                        }
                    }
                }
            } catch (Throwable ignored) {
            }
        }
        try {
            for (ScriptInstance script : ScriptManager.get().all()) {
                if (script != null && isBound(script.bind)) {
                    result.add(new Entry(script, script.displayName(), script.bind, script.isEnabled()));
                }
            }
        } catch (Throwable ignored) {
        }
        return result;
    }

    @Override
    protected void update(EventRender.Screen.Hud event) {
        width = MIN_WIDTH;
        height = ROWS_TOP;

        List<Entry> entries = collectEntries();
        boolean hasAnimatedRows = false;
        boolean hasActiveRows = false;
        for (Entry entry : entries) {
            SAnim anim = animations.computeIfAbsent(entry.owner(), f -> new SAnim(300L, 0.0f, SEasing.BAKEK));
            anim.update(entry.active() ? 1.0f : 0.0f);
            if (anim.getValue() > 0.0f) {
                float rowWidth = FontRegistry.DELTA_HUD.getWidth(entry.label(), 7.0f)
                        + FontRegistry.DELTA_HUD.getWidth(KeyNames.get(entry.bind()), 7.0f) + 20.0f;
                width = Math.max(width, rowWidth);
            }
            if (anim.getValue() > 0.001f) {
                hasAnimatedRows = true;
            }
            if (entry.active()) {
                hasActiveRows = true;
            }
            height += (ROW_HEIGHT + ROW_GAP) * anim.getValue();
        }

        List<Entry> sorted = new ArrayList<>(entries);
        sorted.sort(Comparator.comparingDouble(e -> FontRegistry.DELTA_HUD.getWidth(e.label(), 7.0f)));
        sortedCache = sorted;

        // Keep the panel alive while a row animates in or out, exactly like the
        // effects list. Otherwise the panel snaps shut the instant a module is
        // toggled off, before its row finishes collapsing.
        setSelfVisible(hasAnimatedRows
                || hasActiveRows
                || mc.currentScreen instanceof ChatScreen
                || boolSetting(scope(), SETTING_ALWAYS, false));
    }

    @Override
    protected void drawRows(EventRender.Screen.Hud event, float alpha) {
        Renderer2D r = event.getRenderer();
        float offset = ROWS_TOP;

        for (Entry entry : sortedCache) {
            SAnim anim = animations.get(entry.owner());
            if (anim == null) {
                continue;
            }
            float animV = anim.getValue();
            if (animV <= 0.02f) {
                continue;
            }
            float off = -4.5f + 4.5f * animV;
            float rowY = y + offset + off;

            // Каждый бинд — собственный прямоугольник тех же габаритов, что и шапка.
            drawRow(r, rowY, ROW_HEIGHT, alpha * animV);
            r.pushRoundedClipRect((int) x, (int) rowY, (int) Math.ceil(width), (int) Math.ceil(ROW_HEIGHT),
                    rounding(), rounding(), rounding(), rounding());
            try {
                r.text(FontRegistry.DELTA_HUD, x + 7.0f * animV,
                        baselineMedium(rowY, ROW_HEIGHT, 7.0f), 7.0f, entry.label(),
                        StarUtil.text(alpha * animV));

                String keyLabel = KeyNames.get(entry.bind());
                float keyWidth = FontRegistry.DELTA_HUD.getWidth(keyLabel, 7.0f);
                r.text(FontRegistry.DELTA_HUD, x + width - keyWidth - 7.0f * animV,
                        baselineMedium(rowY, ROW_HEIGHT, 7.0f), 7.0f, keyLabel,
                        StarUtil.text(alpha * animV));
            } finally {
                r.popClipRect();
            }

            offset += (ROW_HEIGHT + ROW_GAP) * animV;
        }
    }

    @Override
    public void fillSettingsPopup(PopupMenu popup) {
        popup.checkbox("Показывать всегда", () -> boolSetting(scope(), SETTING_ALWAYS, false),
                value -> setBoolSetting(scope(), SETTING_ALWAYS, value));
    }

    @Override
    public String settingName() {
        return "Активные бинды";
    }

    @Override
    protected String title() {
        return "Активные Бинды";
    }
}
