package tech.lightclient.ui.hud.star;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;

import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.client.IMinecraft;
import tech.lightclient.client.utils.render.drag.DraggingManager;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.client.utils.render.drag.api.GridLine;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.modules.api.settings.impl.ModeListSetting;
import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.ui.hud.star.impl.EffectsElement;
import tech.lightclient.ui.hud.star.impl.FpsInlineElement;
import tech.lightclient.ui.hud.star.impl.ItemBindsElement;
import tech.lightclient.ui.hud.star.impl.KeyBindsElement;
import tech.lightclient.ui.hud.star.impl.NotifyElement;
import tech.lightclient.ui.hud.star.impl.TargetHudElement;
import tech.lightclient.ui.hud.star.impl.WatermarkElement;
import tech.lightclient.ui.hud.star.impl.WorldInlineElement;

public class StarHud implements IMinecraft {
    private static StarHud instance;

    public static StarHud get(Interface module) {
        if (instance == null) {
            instance = new StarHud(module);
        }
        return instance;
    }

    public static StarHud getInstance() {
        return instance;
    }

    private final List<HudElement> elements = new ArrayList<>();
    private final List<PopupMenu> popups = new ArrayList<>();
    private final STimer emptyTimer = new STimer();
    private final WatermarkElement watermark;
    private final EffectsElement effects;
    private final KeyBindsElement keyBinds;
    private final ItemBindsElement itemBinds;
    private final TargetHudElement targetHud;
    private final WorldInlineElement worldInline;
    private final FpsInlineElement fpsInline;
    private final NotifyElement notify;

    private StarHud(Interface module) {
        this.worldInline = new WorldInlineElement(module);
        this.effects = new EffectsElement(module);
        this.keyBinds = new KeyBindsElement(module);
        this.itemBinds = new ItemBindsElement(module);
        this.targetHud = new TargetHudElement(module);
        this.watermark = new WatermarkElement(module);
        this.fpsInline = new FpsInlineElement(module);
        this.notify = new NotifyElement(module);

        elements.add(watermark);
        elements.add(worldInline);
        elements.add(effects);
        elements.add(keyBinds);
        elements.add(itemBinds);
        elements.add(targetHud);
        elements.add(fpsInline);
        elements.add(notify);
    }

    public List<HudElement> getElements() {
        return elements;
    }

    public void render(EventRender.Screen.Hud event, ModeListSetting visibleElements) {
        applyVisibility(visibleElements);

        boolean anyDragging = false;
        for (HudElement element : elements) {
            if (element.dragging().isDragging()) {
                anyDragging = true;
                break;
            }
        }
        if (anyDragging && Interface.isHudEditorSmartLinesEnabled()) {
            drawGridLines(event);
        }

        for (HudElement element : elements) {
            element.render(event);
            element.setSelecting(element.dragging().isDragging() ? 1.0f : 0.0f);
        }
    }

    private void applyVisibility(ModeListSetting visibleElements) {
        boolean watermarkOn = enabled(visibleElements, "Ватермарка");
        boolean info = enabled(visibleElements, "Информация");
        boolean keybindsOn = enabled(visibleElements, "Активные бинды");
        boolean itemBindsOn = enabled(visibleElements, "Забинженные предметы");
        boolean targetOn = enabled(visibleElements, "Активный таргет");
        boolean effectsOn = enabled(visibleElements, "Эффекты");
        boolean fpsOn = enabled(visibleElements, "FPS");
        boolean notifyOn = enabled(visibleElements, "Нотификации");

        watermark.setVisible(watermarkOn);
        worldInline.setVisible(info);
        keyBinds.setVisible(keybindsOn);
        itemBinds.setVisible(itemBindsOn);
        targetHud.setVisible(targetOn);
        effects.setVisible(effectsOn);
        fpsInline.setVisible(fpsOn);
        notify.setVisible(notifyOn);
    }

    private static boolean enabled(ModeListSetting setting, String name) {
        var entry = setting.getByName(name);
        return entry != null && entry.get();
    }

    private void drawGridLines(EventRender.Screen.Hud event) {
        Renderer2D r = event.getRenderer();
        int w = mc.getWindow().getScaledWidth();
        int h = mc.getWindow().getScaledHeight();
        r.pushScale(HudElement.getGuiScale(event));
        try {
            for (GridLine line : DraggingManager.getActiveGridLines()) {
                if (line == null || !line.isActive()) {
                    continue;
                }
                int color = StarUtil.white(0.3f);
                if (line.getType() == GridLine.Type.VERTICAL) {
                    r.rect(line.getPos(), 0.0f, 1.0f, h, 0.0f, color);
                } else {
                    r.rect(0.0f, line.getPos(), w, 1.0f, 0.0f, color);
                }
            }
        } finally {
            r.popScale();
        }
    }

    public void updateDrag(double mouseX, double mouseY) {
        float maxWidth = mc.getWindow().getScaledWidth();
        float maxHeight = mc.getWindow().getScaledHeight();
        for (HudElement element : elements) {
            if (element.dragging().isDragging()) {
                element.updateDragFix(mouseX, mouseY, maxWidth, maxHeight);
            }
        }
    }

    public boolean hasHoveredElement(double mouseX, double mouseY) {
        for (int i = elements.size() - 1; i >= 0; i--) {
            HudElement element = elements.get(i);
            if (element.externalVisibleRaw() && element.isHovered(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean popupsPresent = !popups.isEmpty();

        if (popupsPresent) {
            boolean consumedByPopup = false;
            for (PopupMenu popup : List.copyOf(popups)) {
                if (popup.mouseClicked(mouseX, mouseY, button)) {
                    consumedByPopup = true;
                }
            }
            if (!consumedByPopup || button != 0) {
                for (PopupMenu popup : List.copyOf(popups)) {
                    popup.setShowing(false);
                }
            } else {
                popups.removeIf(popup -> !popup.isShowing() && popup.getAnimation().getValue() <= 0.001f);
            }
            return true;
        }

        if (!chatScreenOpen()) {
            return false;
        }

        for (int i = elements.size() - 1; i >= 0; i--) {
            HudElement element = elements.get(i);
            if (!element.externalVisibleRaw()) {
                continue;
            }
            if (button == 1 && element.isHovered(mouseX, mouseY)) {
                openSettingsPopup(element, mouseX, mouseY);
                return true;
            }
            element.onMouseClicked(mouseX, mouseY, button);
            if (element.isHovered(mouseX, mouseY)) {
                return true;
            }
        }

        return false;
    }

    private void openSettingsPopup(HudElement element, double mouseX, double mouseY) {
        element.setSelecting(1.0f);
        PopupMenu popup = new PopupMenu((float) mouseX, (float) mouseY, HudListElement.MIN_WIDTH, 6.0f)
                .title("Настройки");
        element.fillSettingsPopup(popup);
        popup.onClose(() -> element.setSelecting(0.0f));
        popups.add(popup);
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        for (HudElement element : elements) {
            element.onMouseReleased(mouseX, mouseY, button);
        }
    }

    public boolean keyPressed(int keyCode, boolean ctrl, boolean shift) {
        if (keyCode != org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT
                && keyCode != org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT
                && keyCode != org.lwjgl.glfw.GLFW.GLFW_KEY_UP
                && keyCode != org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN) {
            return false;
        }
        HudElement targetEl = null;
        for (HudElement element : elements) {
            if (element.dragging().isDragging()) {
                targetEl = element;
                break;
            }
        }
        if (targetEl == null) {
            double mx = mc.mouse.getScaledX(mc.getWindow());
            double my = mc.mouse.getScaledY(mc.getWindow());
            for (int i = elements.size() - 1; i >= 0; i--) {
                HudElement element = elements.get(i);
                if (element.externalVisibleRaw() && element.isHovered(mx, my)) {
                    targetEl = element;
                    break;
                }
            }
        }
        if (targetEl == null) {
            return false;
        }
        Dragging dragged = targetEl.dragging();
        float step = shift ? Math.max(1.0f, Interface.getHudEditorGridSize()) : 1.0f;
        float dx = 0.0f;
        float dy = 0.0f;
        switch (keyCode) {
            case org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT -> dx = -step;
            case org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT -> dx = step;
            case org.lwjgl.glfw.GLFW.GLFW_KEY_UP -> dy = -step;
            case org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN -> dy = step;
            default -> {
                return false;
            }
        }
        DraggingManager.pushUndoSnapshot(dragged.getName(), dragged.getX(), dragged.getY());
        float newX = Math.max(0.0f, Math.min(mc.getWindow().getScaledWidth() - dragged.getWidth(), dragged.getX() + dx));
        float newY = Math.max(0.0f, Math.min(mc.getWindow().getScaledHeight() - dragged.getHeight(), dragged.getY() + dy));
        dragged.setX(newX);
        dragged.setY(newY);
        DraggingManager.saveNow();
        return true;
    }

    private boolean chatScreenOpen() {
        return MinecraftClient.getInstance().currentScreen instanceof ChatScreen;
    }

    public void renderPopups(EventRender.Screen.OverGui event) {
        if (popups.isEmpty()) {
            return;
        }
        if (!(mc.currentScreen instanceof ChatScreen)) {
            for (PopupMenu popup : List.copyOf(popups)) {
                popup.setShowing(false);
            }
        }

        float screenWidth = mc.getWindow().getScaledWidth();
        float screenHeight = mc.getWindow().getScaledHeight();
        Renderer2D r = event.getRenderer();
        r.pushScale(HudElement.getGuiScale(event));
        try {
            for (PopupMenu popup : List.copyOf(popups)) {
                if (popup.y + popup.getHeight() > screenHeight - 10.0f) {
                    popup.y = screenHeight - 10.0f - popup.getHeight();
                }
                if (popup.x + popup.width > screenWidth - 10.0f) {
                    popup.x = screenWidth - 10.0f - popup.width;
                }
                popup.render(r, 1.0f);
            }
        } finally {
            r.popScale();
        }
        popups.removeIf(popup -> !popup.isShowing() && popup.getAnimation().getValue() <= 0.001f);
    }

    public boolean isPopupOpen() {
        return !popups.isEmpty();
    }
}
