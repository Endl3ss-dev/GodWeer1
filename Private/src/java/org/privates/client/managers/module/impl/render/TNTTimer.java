package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.joml.Vector2f;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.Project;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "TNTTimer", category = Category.RENDER, description = "Таймер до взрыва динамита")
public class TNTTimer extends Module {
    public static TNTTimer getInstance() {
        return Instance.get(TNTTimer.class);
    }

    private final ItemStack tntStack = new ItemStack(Items.TNT);

    @EventHandler
    public void onEvent(Render2DEvent event) {
        boolean tntExists = false;
        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof TNTEntity tnt) {
                tntExists = true;
                final String name = Mathf.round(tnt.getFuse() / 20.0F, 1) + " сек";
                Vector3d pos = RenderUtil3D.interpolate(tnt, mc.getRenderPartialTicks());
                Vector2f vec = Project.project2D(pos.x, pos.y + tnt.getHeight() + 0.5, pos.z);
                if (vec.x == Float.MAX_VALUE && vec.y == Float.MAX_VALUE) return;

                final FontRenderer font = mc.fontRenderer;

                float width = font.getStringWidth(name);
                int black = ColorUtil.getColor(0, 0.5F);

                float halfWidth = width / 2.0F;
                RectUtil.drawRect(event.getMatrix(), vec.x - halfWidth, vec.y, width, font.FONT_HEIGHT, black);
                font.drawString(event.getMatrix(), TextFormatting.RED + name, 0.5F + vec.x - halfWidth, 1F + vec.y, ColorUtil.WHITE);
            }
        }

        // Безопасная отрисовка иконки TNT слева сверху
        if (tntExists) {
            int x = 8;
            int y = 8;
            ItemRenderer itemRenderer = mc.getItemRenderer();
            MatrixStack matrixStack = event.getMatrix();


            // Сохраняем состояние матрицы
            matrixStack.push();
            // Можно добавить масштабирование, если нужно:
            // matrixStack.translate(x, y, 0);
            // matrixStack.scale(1.0F, 1.0F, 1.0F);

            // Рисуем предмет
            itemRenderer.renderItemAndEffectIntoGUI(tntStack, x, y);

            // Восстанавливаем состояние матрицы
            matrixStack.pop();
        }
    }
}