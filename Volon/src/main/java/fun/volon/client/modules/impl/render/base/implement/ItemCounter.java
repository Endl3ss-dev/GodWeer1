package fun.volon.client.modules.impl.render.base.implement;

import com.mojang.blaze3d.systems.RenderSystem;
import fun.volon.Volon;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.draggable.Draggable;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.client.modules.impl.render.base.HudRectStyle;
import fun.volon.client.modules.impl.render.base.InterfaceProcessing;

public class ItemCounter extends InterfaceProcessing {

    // Размеры блоков
    private static final float ICON_BLOCK_WIDTH  = 20f;
    private static final float ICON_BLOCK_HEIGHT = 18f;
    private static final float ROW_GAP           = 2f;   // отступ между строками
    private static final float BLOCK_GAP         = 2f;   // отступ между иконкой и текстом
    private static final float CORNER_RADIUS     = 3f;
    private static final float TEXT_PADDING_H    = 5f;   // горизонтальный отступ внутри текстового блока

    public ItemCounter(Draggable draggable) {
        super(draggable);
    }

    @Override
    public void onRender(EventRender.Default eventRender) {
        if (mc == null || mc.player == null) {
            return;
        }

        DrawContext context = eventRender.getContext();
        MatrixStack matrices = context.getMatrices();

        float x = draggable.getX();
        float y = draggable.getY();

        // Подсчёт предметов
        int fireworkCount = countItem(Items.FIREWORK_ROCKET.getDefaultStack().getItem());
        int totemCount    = countItem(Items.TOTEM_OF_UNDYING.getDefaultStack().getItem());

        String fireworkText = "x" + fireworkCount;
        String totemText    = "x" + totemCount;

        Font font = Fonts.getFont("suisse", 14);

        // Ширина текстового блока — подстраивается под самый длинный текст
        float textBlockWidth = TEXT_PADDING_H * 2;
        if (font != null) {
            float fw = font.getWidth(fireworkText);
            float tw = font.getWidth(totemText);
            textBlockWidth = Math.max(fw, tw) + TEXT_PADDING_H * 2;
        }
        // Минимальная ширина текстового блока
        if (textBlockWidth < 28f) textBlockWidth = 28f;

        float rowWidth  = ICON_BLOCK_WIDTH + BLOCK_GAP + textBlockWidth;
        float rowHeight = ICON_BLOCK_HEIGHT;

        int themeColor;
        if (Volon.INSTANCE != null && Volon.INSTANCE.themeStorage != null
                && !Volon.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            themeColor = Volon.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        } else {
            themeColor = ColorUtils.getThemeColor();
        }
        HudRectStyle rectStyle = getHudRectStyle();

        // --- Строка 1: Фейерверк ---
        float row1Y = y;
        drawRow(context, matrices, x, row1Y, rowWidth, rowHeight,
                textBlockWidth, themeColor, rectStyle,
                Items.FIREWORK_ROCKET.getDefaultStack(), fireworkText, font);

        // --- Строка 2: Тотем ---
        float row2Y = y + rowHeight + ROW_GAP;
        drawRow(context, matrices, x, row2Y, rowWidth, rowHeight,
                textBlockWidth, themeColor, rectStyle,
                Items.TOTEM_OF_UNDYING.getDefaultStack(), totemText, font);

        // Обновляем размеры для draggable
        float totalWidth  = rowWidth;
        float totalHeight = rowHeight * 2 + ROW_GAP;
        draggable.setWidth(totalWidth);
        draggable.setHeight(totalHeight);

        super.onRender(eventRender);
    }

    private void drawRow(DrawContext context, MatrixStack matrices,
                         float x, float y, float rowWidth, float rowHeight,
                         float textBlockWidth, int themeColor, HudRectStyle rectStyle,
                         ItemStack itemStack, String countText, Font font) {

        // Блок иконки — с поддержкой жидкого стекла
        drawHudBackground(matrices, x, y, ICON_BLOCK_WIDTH, rowHeight, themeColor);

        // Рисуем иконку предмета — центрируем по горизонтали и вертикали
        float iconSize = 16f;
        float iconOffsetX = (ICON_BLOCK_WIDTH - iconSize) * 0.5f;
        float iconOffsetY = (rowHeight - iconSize) * 0.5f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        matrices.push();
        matrices.translate(x + iconOffsetX, y + iconOffsetY, 0f);
        context.drawItem(itemStack, 0, 0);
        matrices.pop();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);

        // Блок текста — с поддержкой жидкого стекла и фоном
        float textBlockX = x + ICON_BLOCK_WIDTH + BLOCK_GAP;
        drawHudBackground(matrices, textBlockX, y, textBlockWidth, rowHeight, themeColor);

        // Текст количества — по центру горизонтально и вертикально
        if (font != null) {
            float textWidth  = font.getWidth(countText);
            float textHeight = font.getHeight();
            float textX = textBlockX + (textBlockWidth - textWidth) * 0.5f;
            float textY = y + (rowHeight - textHeight) * 1.5f;
            font.draw(matrices, countText, textX, textY, ColorUtils.rgba(255, 255, 255, 255));
        }
    }

    /**
     * Считает суммарное количество предметов заданного типа во всём инвентаре игрока.
     */
    private int countItem(net.minecraft.item.Item item) {
        if (mc.player == null) return 0;
        int count = 0;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return count;
    }
}
