package fun.volon.client.modules.impl.render.base.implement;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import fun.volon.Volon;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.draggable.Draggable;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.client.modules.impl.render.base.InterfaceProcessing;

public class ArmorStatus extends InterfaceProcessing {

    public ArmorStatus(Draggable draggable) {
        super(draggable);
    }

    @Override
    public void onRender(EventRender.Default eventRender) {
        if (mc == null || mc.player == null) {
            return;
        }

        DrawContext context = eventRender.getContext();
        var matrices = context.getMatrices();

        // Используем позицию из draggable
        float x = draggable.getX();
        float y = draggable.getY();

        // Получаем броню и предмет во второй руке
        ItemStack helmet = mc.player.getInventory().getArmorStack(3);
        ItemStack chestplate = mc.player.getInventory().getArmorStack(2);
        ItemStack leggings = mc.player.getInventory().getArmorStack(1);
        ItemStack boots = mc.player.getInventory().getArmorStack(0);
        ItemStack offhand = mc.player.getOffHandStack();

        // Размеры слота
        float slotSize = 18f;
        float slotGap = 2f;
        float durabilityBarHeight = 2f;
        float durabilityBarGap = 1f;

        int colorTheme;
        if (!Volon.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            colorTheme = Volon.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        } else {
            colorTheme = ColorUtils.getThemeColor();
        }

        // Массив предметов для отрисовки (броня + оффханд)
        ItemStack[] items = {helmet, chestplate, leggings, boots, offhand};
        
        float currentX = x;
        float currentY = y;

        for (ItemStack item : items) {
            if (!item.isEmpty()) {
                // Рисуем фон слота
                RenderUtils.drawRoundedRect(matrices, currentX, currentY, slotSize, slotSize, 2f, 
                    ColorUtils.rgba(30, 30, 30, 180));
                
                // Рисуем обводку слота
                int outlineColor = ColorUtils.applyAlpha(colorTheme, 0.5f);
                RenderUtils.drawRoundedRectOutline(matrices, currentX, currentY, slotSize, slotSize, 2f, 1f, 
                    outlineColor, outlineColor, outlineColor, outlineColor);

                // Рисуем предмет
                matrices.push();
                matrices.translate(currentX + 1, currentY + 1, 0);
                context.drawItem(item, 0, 0);
                matrices.pop();

                // Рисуем полоску прочности под предметом
                if (item.isDamageable()) {
                    int maxDamage = item.getMaxDamage();
                    int damage = item.getDamage();
                    float durabilityPercent = 1.0f - ((float) damage / (float) maxDamage);
                    durabilityPercent = MathHelper.clamp(durabilityPercent, 0.0f, 1.0f);

                    float barY = currentY + slotSize + durabilityBarGap;
                    float barWidth = slotSize;

                    // Фон полоски
                    RenderUtils.drawRoundedRect(matrices, currentX, barY, barWidth, durabilityBarHeight, 1f,
                        ColorUtils.rgba(40, 40, 40, 200));

                    // Цвет полоски в зависимости от прочности
                    int barColor;
                    if (durabilityPercent > 0.5f) {
                        barColor = ColorUtils.rgba(85, 255, 85, 255); // Зеленый
                    } else if (durabilityPercent > 0.25f) {
                        barColor = ColorUtils.rgba(255, 255, 85, 255); // Желтый
                    } else {
                        barColor = ColorUtils.rgba(255, 85, 85, 255); // Красный
                    }

                    // Полоска прочности
                    if (durabilityPercent > 0.01f) {
                        RenderUtils.drawRoundedRect(matrices, currentX, barY, barWidth * durabilityPercent, 
                            durabilityBarHeight, 1f, barColor);
                    }
                }

                currentX += slotSize + slotGap;
            }
        }

        // Устанавливаем размеры для draggable (для перемещения элемента)
        float totalWidth = (slotSize + slotGap) * 5 - slotGap;
        float totalHeight = slotSize + durabilityBarGap + durabilityBarHeight;
        draggable.setWidth(totalWidth);
        draggable.setHeight(totalHeight);

        super.onRender(eventRender);
    }
}
