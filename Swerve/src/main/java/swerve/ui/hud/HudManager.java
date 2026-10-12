package swerve.ui.hud;

import swerve.Swerve;
import swerve.ui.newgui.GuiStyle;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

// Единая точка отрисовки HUD: один HudRenderCallback на весь клиент,
// арбитраж перетаскивания (активен ровно один элемент) и изоляция падений элементов.
public final class HudManager {

    private static final List<HudElement> ELEMENTS = new ArrayList<>();

    private static HudElement dragTarget;
    private static boolean initialized;

    private HudManager() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        HudPositionStore.load();
        HudSettingsController.init();
        HudRenderCallback.EVENT.register((context, deltaTracker) -> render(context));
    }

    public static void register(HudElement element) {
        if (element != null) {
            ELEMENTS.add(element);
        }
    }

    private static void render(GuiGraphics context) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || minecraft.player == null || minecraft.level == null) {
            return;
        }

        // [+] Курсор опрашивается всегда: элементы читают кнопки и при открытом экране
        // (ПКМ по элементу в чате открывает меню настроек). Драг по-прежнему только без экранов,
        // иначе элементы перехватывали бы клики GUI.
        HudPointer.update(minecraft);

        if (minecraft.screen == null) {
            updateDrag(minecraft);
        } else {
            // Клик по GUI не должен доживать до драга HUD после закрытия экрана.
            HudPointer.discardPress();
            if (dragTarget != null) {
                dragTarget.endDrag();
                dragTarget = null;
            }
        }

        for (int i = 0, size = ELEMENTS.size(); i < size; i++) {
            HudElement element = ELEMENTS.get(i);
            try {
                element.renderElement(context.pose());
            } catch (Exception exception) {
                Swerve.LOGGER.error("HUD element '{}' failed to render", element.getName(), exception);
                element.setEnabled(false);
            }
        }
    }

    private static void updateDrag(Minecraft minecraft) {
        if (dragTarget != null) {
            if (HudPointer.isLeftDown()) {
                dragTarget.drag(minecraft, HudPointer.getX(), HudPointer.getY());
            } else {
                dragTarget.endDrag();
                dragTarget = null;
            }
            return;
        }

        if (!HudPointer.consumePress()) {
            return;
        }

        double mouseX = HudPointer.getX();
        double mouseY = HudPointer.getY();
        for (int i = ELEMENTS.size() - 1; i >= 0; i--) {
            HudElement element = ELEMENTS.get(i);
            if (element.acceptsDrag(minecraft) && element.hitTest(mouseX, mouseY)) {
                element.beginDrag(mouseX, mouseY);
                dragTarget = element;
                GuiStyle.playSound("mode_pick.wav", 0.05f);
                return;
            }
        }
    }
}
