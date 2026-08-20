package org.privates.client.screen.hud.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.item.AirItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.TextFormatting;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import org.privates.client.managers.component.impl.drag.DragComponent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;

import java.util.Arrays;
import java.util.List;

public class LeftHandItemRenderer extends AbstractHud implements IRenderer, IConfigurableHud {
    private final DragSetting drag;
    private float animatedWidth, animatedHeight;
    
    @Getter
    @Setter
    private float scaleV;
    @Getter
    @Setter
    private boolean blurEnabled = true;
    @Getter
    @Setter
    private float blurStrength = 10f;
    
    public float scale = 1.0f;
    public boolean showItemIcon = true;
    public boolean showItemName = true;
    
    private ItemStack lastItem = ItemStack.EMPTY;
    private float itemScale = 0f;
    
    public LeftHandItemRenderer(DragSetting drag) {
        this.drag = drag;
    }
    
    @Override
    public List<DragComponent.HudSetting> getHudSettings() {
        return Arrays.asList(
                new DragComponent.HudSetting("Scale", DragComponent.SettingType.SLIDER, () -> scale, v -> scale = v, 0.5f, 2.0f, 0.1f),
                new DragComponent.HudSetting("Show Item Icon", DragComponent.SettingType.BOOLEAN, () -> showItemIcon ? 1f : 0f, v -> showItemIcon = v > 0.5f),
                new DragComponent.HudSetting("Show Item Name", DragComponent.SettingType.BOOLEAN, () -> showItemName ? 1f : 0f, v -> showItemName = v > 0.5f)
        );
    }
    
    @Override
    public void render(Render2DEvent event) {
        boolean isPreview = mc.currentScreen instanceof ChatScreen;
        ItemStack offhandStack = isPreview ? new ItemStack(Items.TOTEM_OF_UNDYING) : 
                (mc.player != null ? mc.player.getHeldItemOffhand() : ItemStack.EMPTY);
        
        if (offhandStack.isEmpty() || offhandStack.getItem() instanceof AirItem) {
            scaleV = Mathf.fast(scaleV, 0, 16);
            if (Mathf.round(scaleV, .1d) <= 0) return;
        } else {
            scaleV = Mathf.fast(scaleV, 1, 16);
        }
        
        // Animate item change
        if (!ItemStack.areItemStacksEqual(lastItem, offhandStack)) {
            itemScale = 0f;
            lastItem = offhandStack.copy();
        }
        itemScale = Mathf.fast(itemScale, 1f, 12f);
        
        MatrixStack matrix = event.getMatrix();
        float x = drag.position.x;
        float y = drag.position.y;
        float finalScale = scale;
        
        float padding = 6f * finalScale;
        float fontSize = 7f * finalScale;
        float iconSize = 16f * finalScale;
        float radius = 6f * finalScale;
        float gap = 4f * finalScale;
        
        // Get item name with color
        String itemName = offhandStack.getDisplayName().getString();
        int itemColor = offhandStack.getRarity().color != null ? 
                offhandStack.getRarity().color.getColor() : 0xFFFFFFFF;
        
        // Calculate dimensions
        float contentWidth = 0f;
        if (showItemIcon) contentWidth += iconSize + gap;
        if (showItemName) contentWidth += font.getWidth(itemName, fontSize);
        
        float targetWidth = padding + contentWidth + padding;
        float targetHeight = padding + Math.max(iconSize, fontSize) + padding;
        
        animatedWidth = Mathf.fast(animatedWidth, targetWidth, 14);
        animatedHeight = Mathf.fast(animatedHeight, targetHeight, 14);
        
        if (x == 0 && y == 0) {
            float screenWidth = mc.getMainWindow().getScaledWidth();
            float screenHeight = mc.getMainWindow().getScaledHeight();
            drag.position.set(screenWidth / 2f - animatedWidth / 2f, screenHeight - animatedHeight - 10);
            x = drag.position.x;
            y = drag.position.y;
        }
        
        matrix.push();
        matrix.translate(x + animatedWidth / 2f, y + animatedHeight / 2f, 0);
        matrix.scale(scaleV, scaleV, scaleV);
        matrix.translate(-(x + animatedWidth / 2f), -(y + animatedHeight / 2f), 0);
        
        // Render background
        RenderUtil.CustomStyled.draw(matrix, x, y, animatedWidth, animatedHeight, 
                Round.of(radius), blurEnabled, blurStrength);
        
        // Render content
        float currentX = x + padding;
        float centerY = y + animatedHeight / 2f;
        
        if (showItemIcon) {
            float iconY = centerY - iconSize / 2f;
            
            GlStateManager.pushMatrix();
            GlStateManager.translatef(currentX + iconSize / 2f, iconY + iconSize / 2f, 0);
            GlStateManager.scalef(itemScale, itemScale, 1f);
            GlStateManager.translatef(-(iconSize / 2f), -(iconSize / 2f), 0);
            mc.getItemRenderer().renderItemIntoGUI(offhandStack, 0, 0);
            GlStateManager.popMatrix();
            
            currentX += iconSize + gap;
        }
        
        if (showItemName) {
            float textY = centerY - fontSize / 2f;
            font.draw(matrix, itemName, currentX, textY, itemColor, fontSize);
        }
        
        drag.size.set(animatedWidth, animatedHeight);
        matrix.pop();
    }
}
