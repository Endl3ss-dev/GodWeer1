package antileak.base.client.modules.impl.render.base.implement;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import antileak.base.Punch;
import antileak.base.api.events.implement.EventRender;
import antileak.base.api.utils.animation.AnimationUtils;
import antileak.base.api.utils.animation.Easings;
import antileak.base.api.utils.color.ColorUtils;
import antileak.base.api.utils.draggable.Draggable;
import antileak.base.api.utils.input.KeyBoardUtils;
import antileak.base.api.utils.render.RenderUtils;
import antileak.base.api.utils.render.fonts.msdf.Font;
import antileak.base.api.utils.render.fonts.msdf.Fonts;
import antileak.base.client.modules.impl.misc.ServerHelper;
import antileak.base.client.modules.impl.render.base.InterfaceProcessing;

import java.util.ArrayList;
import java.util.List;

public class HelperBinds extends InterfaceProcessing {
    private final AnimationUtils widthAnimation = new AnimationUtils(0.0f, 10.5f, Easings.QUAD_OUT);

    public HelperBinds(Draggable draggable) {
        super(draggable);
    }

    private Font issue(int size) {
        return Fonts.getFont("suisse", size);
    }

    @Override
    public void onRender(EventRender.Default eventRender) {
        List<ServerHelper.HelperBind> binds = getVisibleBinds();
        if (binds.isEmpty()) {
            widthAnimation.update(0.0f);
            draggable.setWidth(0.0f);
            draggable.setHeight(0.0f);
            return;
        }
        DefaultStyle(eventRender, binds);
        super.onRender(eventRender);
    }

    private void DefaultStyle(EventRender.Default eventRender, List<ServerHelper.HelperBind> binds) {
        float x = draggable.getX();
        float y = draggable.getY();
        int colorTheme = getThemeColor();

        float height = 15f;
        float itemScale = 0.6f;
        float gap = 2f;
        float sidePad = 3.5f;
        
        Font boldFont = Fonts.getFont("suisse_bold", 11);
        if (boldFont == null) boldFont = issue(11);
        Font smallFont = issue(10);
        
        if (boldFont == null || smallFont == null) return;
        
        float totalWidth = 0f;

        // Pre-calculate total width for dragging
        for (ServerHelper.HelperBind bind : binds) {
            String keyName = KeyBoardUtils.getBindName(bind.bind().getKey()).toUpperCase();
            String countStr = String.valueOf(ServerHelper.INSTANCE.getItemCountForBind(bind));
            float itemW = 16f * itemScale;
            float textW = boldFont.getWidth(keyName);
            float countW = smallFont.getWidth(countStr);
            float pillW = sidePad + itemW + 2f + countW + 4f + textW + sidePad;
            totalWidth += pillW + gap;
        }
        totalWidth -= gap;

        widthAnimation.update(totalWidth);
        
        float cx = x;
        for (ServerHelper.HelperBind bind : binds) {
            String keyName = KeyBoardUtils.getBindName(bind.bind().getKey()).toUpperCase();
            String countStr = String.valueOf(ServerHelper.INSTANCE.getItemCountForBind(bind));

            float itemW = 16f * itemScale;
            float textW = boldFont.getWidth(keyName);
            float countW = smallFont.getWidth(countStr);
            float pillW = sidePad + itemW + 2f + countW + 4f + textW + sidePad;

            // Draw Pill Background
            RenderUtils.drawShadow(eventRender.getContext().getMatrices(), cx, y, pillW, height, 6f, 10f, ColorUtils.setAlphaColor(colorTheme, 80));
            RenderUtils.drawRoundedRect(eventRender.getContext().getMatrices(), cx, y, pillW, height, 6f, ColorUtils.rgba(20, 20, 20, 230));

            // Draw Item
            float itemX = cx + sidePad;
            float itemY = y + (height - itemW) / 2f;
            ItemStack displayStack = ServerHelper.INSTANCE.findStackForBind(bind);
            if (displayStack == null) displayStack = bind.stack();
            drawItemIcon(eventRender.getContext(), displayStack, itemX, itemY, itemScale);

            // Draw Count (next to item, slightly lower)
            smallFont.draw(eventRender.getContext().getMatrices(), countStr, itemX + itemW + 2f, itemY + itemW - 4f, ColorUtils.rgba(220, 220, 220, 255));

            // Draw KeyName
            float textX = itemX + itemW + 2f + countW + 4f;
            boldFont.draw(eventRender.getContext().getMatrices(), keyName, textX, y + 5f, ColorUtils.rgba(255, 255, 255, 255));

            cx += pillW + gap;
        }

        draggable.setWidth(totalWidth);
        draggable.setHeight(height);
    }

    private List<ServerHelper.HelperBind> getVisibleBinds() {
        ServerHelper serverHelper = ServerHelper.INSTANCE;
        List<ServerHelper.HelperBind> binds = new ArrayList<>();
        if (serverHelper == null || !serverHelper.isEnable()) return binds;

        for (ServerHelper.HelperBind bind : serverHelper.getActiveHelperBinds()) {
            if (bind.bind().getKey() != -1 && serverHelper.getItemCountForBind(bind) > 0) {
                binds.add(bind);
            }
        }

        return binds;
    }

    private void drawItemIcon(DrawContext context, ItemStack stack, float x, float y, float scale) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x, y, 0.0f);
        matrices.scale(scale, scale, 1.0f);
        context.drawItem(stack, 0, 0);
        matrices.pop();
    }

    private int getThemeColor() {
        if (!Punch.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            return Punch.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        }
        return ColorUtils.getThemeColor();
    }
}
