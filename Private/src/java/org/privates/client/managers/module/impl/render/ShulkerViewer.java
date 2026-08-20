package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.render.draw.Project;
import org.privates.client.utils.render.draw.RenderUtil3D;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ShulkerViewer", category = Category.RENDER, description = "Показывает содержимое шалкера которые лежит на полу или в руке")
public class ShulkerViewer extends Module {

    private final SliderSetting offsetY = new SliderSetting(this, "Смещение Y", 0.5F, 0.0F, 2.0F, 0.1F);
    private final SliderSetting scale = new SliderSetting(this, "Масштаб", 0.7F, 0.4F, 1.5F, 0.1F);
    private final int backgroundColor = 0xAA000000; // Полупрозрачный черный

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (mc.player == null || mc.world == null || !isEnabled()) return;

        for (Entity entity : mc.world.getAllEntities()) {
            ItemStack stack = null;

            // Проверяем наличие шалкер-бокса в руках игрока
            if (entity instanceof PlayerEntity player) {
                ItemStack heldItem = player.getHeldItemMainhand();
                if (Block.getBlockFromItem(heldItem.getItem()) instanceof ShulkerBoxBlock) {
                    stack = heldItem;
                }
            }
            // Проверяем наличие шалкер-бокса на земле
            else if (entity instanceof ItemEntity itemEntity) {
                ItemStack entityItem = itemEntity.getItem();
                if (Block.getBlockFromItem(entityItem.getItem()) instanceof ShulkerBoxBlock) {
                    stack = entityItem;
                }
            }

            if (stack == null) continue;

            CompoundNBT tag = stack.getTag();
            if (tag == null || !tag.contains("BlockEntityTag", 10)) continue;

            CompoundNBT blockEntityTag = tag.getCompound("BlockEntityTag");
            if (!blockEntityTag.contains("Items", 9)) continue;

            NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(blockEntityTag, items);

            if (hasItems(items)) {
                renderShulkerContentsAtEntity(entity, items, event.getMatrix());
            }
        }
    }

    private boolean hasItems(NonNullList<ItemStack> items) {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static void drawRect(MatrixStack matrixStack, float left, float top, float right, float bottom, int color) {
        if (left < right) {
            float i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            float j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(matrixStack.getLast().getMatrix(), left, bottom, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(matrixStack.getLast().getMatrix(), right, bottom, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(matrixStack.getLast().getMatrix(), right, top, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(matrixStack.getLast().getMatrix(), left, top, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public void renderShulkerItems(NonNullList<ItemStack> items, MatrixStack matrixStack, float startX, float startY) {
        int index = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                if (index < items.size()) {
                    float x = startX + col * 18;
                    float y = startY + row * 18;
                    drawItemStack(items.get(index), x, y, matrixStack);
                }
                index++;
            }
        }
    }


    private void renderShulkerContentsAtEntity(Entity entity, NonNullList<ItemStack> items, MatrixStack matrix) {
        Vector3d interpolated = RenderUtil3D.interpolate(entity, mc.getRenderPartialTicks());
        AxisAlignedBB aabb = getEntityBox(entity, interpolated);

        Vector2f min = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
        Vector2f max = new Vector2f(Float.MIN_VALUE, Float.MIN_VALUE);

        // Проецируем все углы AABB на экран
        for (Vector3d vector : aabb.getCorners()) {
            Vector2f vec = Project.project2D(vector);
            if (vec.x != Float.MAX_VALUE && vec.y != Float.MAX_VALUE) {
                min.x = Math.min(min.x, vec.x);
                min.y = Math.min(min.y, vec.y);
                max.x = Math.max(max.x, vec.x);
                max.y = Math.max(max.y, vec.y);
            }
        }

        Vector4f position = new Vector4f(min.x, min.y, max.x, max.y);
        float offsetYValue = offsetY.getValue() * 20; // Увеличиваем смещение для лучшей видимости

        // Вычисляем позицию контейнера
        float x = position.x;
        float y = position.y - offsetYValue;
        float width = position.z - x;
        float centerX = x + (width / 2F);

        renderShulkerContents(items, matrix, centerX, y);
    }

    private void renderShulkerContents(NonNullList<ItemStack> items, MatrixStack matrixStack, float centerX, float startY) {
        int cols = 9;
        int rows = Math.min(3, (items.size() + cols - 1) / cols);
        int slotSize = (int) (18 * scale.getValue());
        int padding = 4;

        int width = cols * slotSize + padding * 2;
        int height = rows * slotSize + padding * 2;

        float startX = centerX - width / 2f;

        // Рисуем фон
        drawRect(matrixStack, startX, startY, startX + width, startY + height, backgroundColor);

        // Отрисовка предметов
        int itemCount = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (itemCount >= items.size()) break;

                ItemStack item = items.get(itemCount++);
                if (item.isEmpty()) continue;

                float posX = startX + col * slotSize + padding;
                float posY = startY + row * slotSize + padding;

                drawItemStack(item, posX, posY, matrixStack);
            }
        }
    }

    private AxisAlignedBB getEntityBox(Entity entity, Vector3d vec) {
        Vector3d size = new Vector3d(
                entity.getBoundingBox().maxX - entity.getBoundingBox().minX,
                entity.getBoundingBox().maxY - entity.getBoundingBox().minY,
                entity.getBoundingBox().maxZ - entity.getBoundingBox().minZ
        );

        return new AxisAlignedBB(
                vec.x - size.x / 2,
                vec.y,
                vec.z - size.z / 2,
                vec.x + size.x / 2,
                vec.y + size.y,
                vec.z + size.z / 2
        );
    }

    private void drawItemStack(ItemStack stack, float x, float y, MatrixStack matrixStack) {
        if (stack.isEmpty())
            return;

        RenderSystem.enableDepthTest();
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, (int) x, (int) y);

        if (stack.getCount() > 1) {
            String s = String.valueOf(stack.getCount());
            matrixStack.push();
            matrixStack.translate(0.0F, 0.0F, 200.0F);

            // Рисуем количество предметов на том же месте, где и сам предмет
            mc.fontRenderer.drawStringWithShadow(
                    matrixStack,
                    s,
                    x + 19 - mc.fontRenderer.getStringWidth(s),
                    y + 6 + 3,
                    0xFFFFFF
            );

            matrixStack.pop();
        }

        RenderSystem.disableDepthTest();
    }
}