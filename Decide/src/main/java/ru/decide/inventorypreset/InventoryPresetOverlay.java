/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 *  net.minecraft.class_465
 *  org.joml.Matrix3x2fStack
 */
package ru.decide.inventorypreset;

import java.util.ArrayList;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_465;
import org.joml.Matrix3x2fStack;
import ru.decide.Client;
import ru.decide.inventorypreset.InventoryPresetManager;
import ru.decide.mixin.HandledScreenAccessor;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.Render2D;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.font.Font;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public final class InventoryPresetOverlay {
    private static final float PANEL_W = 118.0f;
    private static final float ROW_H = 21.0f;

    private InventoryPresetOverlay() {
    }

    public static void render(class_332 context, int mouseX, int mouseY) {
        class_310 mc = class_310.method_1551();
        if (Client.get() == null || Client.get().inventoryPresetManager() == null) {
            return;
        }
        class_437 class_4372 = mc.field_1755;
        if (!(class_4372 instanceof class_465)) {
            return;
        }
        class_465 screen = (class_465)class_4372;
        InventoryPresetManager manager = Client.get().inventoryPresetManager();
        HandledScreenAccessor accessor = (HandledScreenAccessor)screen;
        int guiX = accessor.getScreenX();
        int guiY = accessor.getScreenY();
        float scaleFix = 2.0f / (float)mc.method_22683().method_4495();
        Render2D.beginOverlay();
        InventoryPresetOverlay.drawItem(context, class_1802.field_8106.method_7854(), (float)(guiX + 160) / scaleFix, (float)(guiY - 11) / scaleFix, scaleFix);
        if (manager.activePreset().isEmpty()) {
            Render2D.endOverlay();
            return;
        }
        Map<String, InventoryPresetManager.MissingItem> missing = manager.missingItems();
        ArrayList<InventoryPresetManager.MissingItem> missingItems = new ArrayList<InventoryPresetManager.MissingItem>(missing.values());
        int visibleRows = Math.min(8, Math.max(1, missingItems.size()));
        int missingScroll = manager.missingScroll(missingItems.size(), visibleRows);
        float panelX = (float)(guiX + 182) / scaleFix;
        float panelY = (float)(guiY + 8) / scaleFix;
        float panelH = 31.0f + (float)visibleRows * 21.0f + 27.0f;
        float mx = (float)mouseX / scaleFix;
        float my = (float)mouseY / scaleFix;
        Font font = Fonts.sf_regular;
        RenderUtil.Render2D.glow(panelX, panelY, 118.0f, panelH - 0.5f, ColorUtil.getColor(0, 0.15f), 8.0f, 15.0f, 1.0f);
        RenderUtil.Blur.blur(panelX, panelY, 118.0f, panelH, 1.0f, 8.0f, ColorUtil.multAlpha(ColorUtil.multDark(ColorUtil.background(), 0.6f), 1.0f));
        RenderUtil.Render2D.outline(panelX, panelY, 118.0f, panelH, 0.5f, ColorUtil.replAlpha(ColorUtil.client(), 0.35f), 8.0f);
        Fonts.icon.draw("K", panelX + 8.0f, panelY + 11.0f, 6.5f, ColorUtil.client());
        font.draw("\u041d\u0435 \u0445\u0432\u0430\u0442\u0430\u0435\u0442", panelX + 19.0f, panelY + 10.0f, 6.5f, ColorUtil.getColor(200));
        if (missingItems.size() > visibleRows) {
            String counter = missingScroll + 1 + "\u2013" + Math.min(missingItems.size(), missingScroll + visibleRows) + " / " + missingItems.size();
            font.draw(counter, panelX + 118.0f - 8.0f - font.getWidth(counter, 5.5f), panelY + 10.5f, 5.5f, ColorUtil.getColor(255, 0.35f));
        }
        float rowY = panelY + 25.0f;
        if (missing.isEmpty()) {
            font.draw("\u0412\u0441\u0435 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b \u043d\u0430\u0439\u0434\u0435\u043d\u044b", panelX + 8.0f, rowY + 6.0f, 6.0f, ColorUtil.getColor(110, 220, 140, 1.0f));
        } else {
            for (int index = missingScroll; index < Math.min(missingItems.size(), missingScroll + visibleRows); ++index) {
                class_1799 stack;
                InventoryPresetManager.MissingItem item = (InventoryPresetManager.MissingItem)missingItems.get(index);
                boolean hovered = InventoryPresetOverlay.inside(mx, my, panelX + 4.0f, rowY, 110.0f, 19.0f);
                RenderUtil.Render2D.rect(panelX + 4.0f, rowY, 110.0f, 19.0f, ColorUtil.getColor(40, hovered ? 0.3f : 0.15f), 5.0f);
                if (hovered) {
                    RenderUtil.Render2D.outline(panelX + 4.0f, rowY, 110.0f, 19.0f, 0.5f, ColorUtil.replAlpha(ColorUtil.client(), 1.0f), 5.0f);
                }
                if (!(stack = item.stack()).method_7960()) {
                    InventoryPresetOverlay.drawItem(context, stack, panelX + 13.0f, rowY + 10.5f - 1.0f, scaleFix);
                }
                font.draw("x" + item.count(), panelX + 24.0f, rowY + 5.4f, 8.0f, ColorUtil.getColor(230));
                rowY += 21.0f;
            }
        }
        float arrangeY = panelY + panelH - 23.0f;
        boolean arrangeHover = InventoryPresetOverlay.inside(mx, my, panelX + 5.0f, arrangeY, 108.0f, 18.0f);
        boolean arranging = manager.isArranging();
        RenderUtil.Render2D.rect(panelX + 5.0f, arrangeY, 108.0f, 18.0f, arranging ? ColorUtil.getColor(200, 60, 60, 0.35f) : ColorUtil.replAlpha(ColorUtil.client(), arrangeHover ? 0.35f : 0.2f), 5.0f);
        RenderUtil.Render2D.outline(panelX + 5.0f, arrangeY, 108.0f, 18.0f, 0.5f, arranging ? ColorUtil.getColor(200, 60, 60, 1.0f) : ColorUtil.replAlpha(ColorUtil.client(), arrangeHover ? 1.0f : 0.4f), 5.0f);
        font.drawCentered(arranging ? "\u041e\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u044c" : "\u0420\u0430\u0437\u043b\u043e\u0436\u0438\u0442\u044c \u0432\u0441\u0451", panelX + 59.0f, arrangeY + 5.5f, 6.5f, ColorUtil.getColor(255, 0.9f));
        Render2D.endOverlay();
    }

    private static void drawItem(class_332 context, class_1799 stack, float centerX, float centerY, float scaleFix) {
        Matrix3x2fStack matrices = context.method_51448();
        matrices.pushMatrix();
        matrices.translate(centerX * scaleFix, centerY * scaleFix);
        matrices.scale(scaleFix, scaleFix);
        context.method_51427(stack, -8, -8);
        matrices.popMatrix();
    }

    private static boolean inside(float mouseX, float mouseY, float x, float y, float w, float h) {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }
}

