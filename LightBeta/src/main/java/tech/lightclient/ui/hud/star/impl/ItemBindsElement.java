package tech.lightclient.ui.hud.star.impl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.LightClient;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.impl.utils.ServerAssistant;
import tech.lightclient.ui.hud.star.HudElement;
import tech.lightclient.ui.hud.star.ItemDraw;
import tech.lightclient.ui.hud.star.KeyNames;
import tech.lightclient.ui.hud.star.PopupMenu;
import tech.lightclient.ui.hud.star.StarUtil;

public class ItemBindsElement extends HudElement {
    private static final String SETTING_PER_ROW = "perRow";

    public ItemBindsElement(Function module) {
        super(module, "Star Предметы", "icons/hud/keybinds.png", 120.0f, 120.0f);
    }

    private float perRow() {
        float value = floatSetting(scope(), SETTING_PER_ROW, 4.0f);
        if (value < 1.0f || value > 5.0f || value != Math.floor(value)) {
            return 4.0f;
        }
        return value;
    }

    @Override
    protected void update(EventRender.Screen.Hud event) {
        List<BoundItem> items = collectItems();
        if (items.isEmpty()) {
            width = 22.0f;
            height = 23.0f;
            setSelfVisible(false);
            return;
        }
        int columns = (int) Math.min(perRow(), items.size());
        int rows = (items.size() + columns - 1) / columns;
        width = columns * 26.0f - 5.0f;
        height = rows * 32.0f - 4.0f;
        setSelfVisible(true);
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    protected void draw(EventRender.Screen.Hud event, float alpha) {
        List<BoundItem> items = collectItems();
        if (items.isEmpty()) {
            return;
        }
        Renderer2D r = event.getRenderer();
        int columns = (int) Math.min(perRow(), items.size());

        for (int i = 0; i < items.size(); i++) {
            int col = i % columns;
            int row = i / columns;
            float cellX = x + col * 26.0f;
            float cellY = y + row * 32.0f;
            BoundItem bound = items.get(i);
            ItemStack stack = bound.item().getDefaultStack();

            drawClientRect(r, scope(), cellX, cellY, 22.0f, 23.0f, 4.0f, alpha, dragging.isDragging());

            r.pushAlpha(alpha);
            try {
                ItemDraw.drawItem(r, stack, cellX + 3.0f, cellY + 3.0f, 16.0f, alpha);
            } finally {
                r.popAlpha();
            }

            if (mc.player != null && mc.player.getItemCooldownManager().isCoolingDown(stack)) {
                float progress = MathHelper.clamp(
                        mc.player.getItemCooldownManager().getCooldownProgress(stack, getTickDelta()), 0.0f, 1.0f);
                float overlayHeight = 16.0f * progress;
                float overlayY = cellY + 3.0f + 16.0f * (1.0f - progress);
                r.rect(cellX + 3.0f, overlayY, 16.0f, overlayHeight, 3.0f,
                        ClientColors.applyAlpha(0xFF000000, Math.round(122.0f * alpha)));
            }

            String label;
            if (bound.remainingSeconds() > 0.0f) {
                label = String.format("%.1f", bound.remainingSeconds()).replace(",", ".");
            } else {
                label = keyLabel(bound.key());
            }
            if (label == null || label.isEmpty()) {
                continue;
            }
            if (label.length() > 3 && bound.remainingSeconds() <= 0.0f) {
                label = label.substring(0, 3);
            }
            float textWidth = FontRegistry.DELTA_HUD.getWidth(label, 6.0f);
            float chipX = cellX + (22.0f - textWidth) / 2.0f - 2.0f;
            float chipY = cellY + 17.5f;
            r.rect(chipX, chipY, textWidth + 4.0f, 8.0f, 2.0f, StarUtil.accent(Math.round(255.0f * alpha)));
            r.text(FontRegistry.DELTA_HUD, chipX + 2.0f,
                    StarUtil.textBaseline(FontRegistry.DELTA_HUD, 'H', chipY, 8.0f, 6.0f), 6.0f, label,
                    ClientColors.applyAlpha(0xFFFFFFFF, alpha));
        }
    }

    private static float getTickDelta() {
        try {
            return mc.getRenderTickCounter().getTickProgress(true);
        } catch (Throwable ignored) {
            return 0.0f;
        }
    }

    private List<BoundItem> collectItems() {
        List<BoundItem> result = new ArrayList<>();

        var manager = LightClient.getFunctionManager();
        ServerAssistant assistant = manager == null ? null : manager.getServerAssistant();
        if (assistant == null || !assistant.isState()) {
            return result;
        }

        for (ServerAssistant.KeyBind bind : assistant.getKeyBindings()) {
            if (bind == null || bind.setting() == null || !bind.setting().isVisible()) {
                continue;
            }
            int key = bind.setting().get();
            if (key <= 0 || key == GLFW.GLFW_KEY_UNKNOWN) {
                continue;
            }
            result.add(new BoundItem(bind.item(), key, 0.0f));
        }

        return result;
    }

    private String keyLabel(int key) {
        if (key <= 0 || key == GLFW.GLFW_KEY_UNKNOWN) {
            return "";
        }
        return KeyNames.get(key);
    }

    @Override
    public void fillSettingsPopup(PopupMenu popup) {
        popup.button("В ряду: " + (int) perRow() + " (сменить)", iconPath(), () -> {
            float next = perRow() >= 5.0f ? 1.0f : perRow() + 1.0f;
            setFloatSetting(scope(), SETTING_PER_ROW, next);
        });
    }

    @Override
    public String settingName() {
        return "Забинженные предметы";
    }

    private record BoundItem(Item item, int key, float remainingSeconds) {
    }
}
