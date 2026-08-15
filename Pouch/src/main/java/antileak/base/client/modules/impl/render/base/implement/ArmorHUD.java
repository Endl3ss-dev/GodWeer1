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
import antileak.base.api.utils.render.RenderUtils;
import antileak.base.api.utils.render.fonts.msdf.Font;
import antileak.base.api.utils.render.fonts.msdf.Fonts;
import antileak.base.client.modules.impl.render.base.InterfaceProcessing;

import java.util.ArrayList;
import java.util.List;

public class ArmorHUD extends InterfaceProcessing {

    private static final float BASE_MIN_WIDTH = 40f;
    private static final float ROW_HEIGHT = 10f;

    private final AnimationUtils widthAnimation = new AnimationUtils(70, 10.5f, Easings.QUAD_OUT);

    public ArmorHUD(Draggable draggable) {
        super(draggable);
    }

    private Font issue(int size) {
        return Fonts.getFont("suisse", size);
    }

    @Override
    public void onRender(EventRender.Default eventRender) {
        DefaultStyle(eventRender);
        super.onRender(eventRender);
    }

    public void DefaultStyle(EventRender.Default eventRender) {
        float x = draggable.getX();
        float y = draggable.getY();

        int colorTheme;
        if (!Punch.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            colorTheme = Punch.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        } else {
            colorTheme = ColorUtils.getThemeColor();
        }

        List<ArmorEntry> entries = collectArmor();
        if (entries.isEmpty()) {
            return;
        }
        
        int visibleCount = entries.size();
        float targetWidth = BASE_MIN_WIDTH;
        float targetHeight = 16f;

        Font titleFont = issue(14);
        Font iconFont = Fonts.getFont("icon", 13);
        String iconGlyph = "C";
        float iconW = iconFont != null ? iconFont.getWidth(iconGlyph) : 0f;
        float gap = 3.5f;

        float titleWidth = (titleFont != null ? titleFont.getWidth("Armor") : 30f) + iconW + gap + 10f; // 10f is padding (5f left, 5f right)
        if (titleWidth > targetWidth) targetWidth = titleWidth;

        float iconSize = 8f;
        for (ArmorEntry entry : entries) {
            String durText = entry.getDurabilityText();
            float durWidth = (durText != null) ? issue(11).getWidth(durText) : 0f;
            float rowWidth = durWidth + iconSize + 3f;
            if (rowWidth > targetWidth) targetWidth = rowWidth;
            targetHeight += ROW_HEIGHT;
        }

        if (visibleCount > 0) {
            targetHeight += 2f;
        }

        widthAnimation.update(targetWidth);
        float width = widthAnimation.getValue();
        float height = targetHeight;

        RenderUtils.drawShadow(eventRender.getContext().getMatrices(), x, y, width, height, 6f, 12f, ColorUtils.setAlphaColor(colorTheme, 80));
        RenderUtils.drawRoundedRect(eventRender.getContext().getMatrices(), x, y, width, height, 6f, ColorUtils.rgba(20, 20, 20, 230));
        RenderUtils.drawRoundedRect(eventRender.getContext().getMatrices(), x + width / 2f - 12f, y + height - 2f, 24f, 2f, 1f, ColorUtils.setAlphaColor(colorTheme, 255));
        if (titleFont != null) {
            titleFont.draw(eventRender.getContext().getMatrices(), "Armor", x + 5f, y + 6f, -1);
        }
        if (iconFont != null) {
            iconFont.draw(eventRender.getContext().getMatrices(), iconGlyph, x + width - 11f, y + 7f, colorTheme);
        }

        float offsetY = 18f;
        for (ArmorEntry entry : entries) {
            drawItemIcon(eventRender.getContext(), entry.stack, x + 2.5f, y + offsetY - 3f, 0.5f);

            String durText = entry.getDurabilityText();
            if (durText != null) {
                issue(11).draw(eventRender.getContext().getMatrices(), durText, x + iconSize + 4f, y + offsetY, ColorUtils.rgba(255, 255, 255, 255));
            }
            offsetY += ROW_HEIGHT;
        }

        draggable.setWidth(width);
        draggable.setHeight(height);
    }

    private List<ArmorEntry> collectArmor() {
        List<ArmorEntry> entries = new ArrayList<>();
        if (mc == null || mc.player == null) return entries;

        var armor = mc.player.getInventory().armor;
        for (int i = 3; i >= 0; i--) {
            ItemStack stack = armor.get(i);
            if (!stack.isEmpty()) {
                entries.add(new ArmorEntry(stack));
            }
        }
        return entries;
    }

    private void drawItemIcon(DrawContext context, ItemStack stack, float x, float y, float scale) {
        MatrixStack matrices = context.getMatrices();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        matrices.push();
        matrices.translate(x, y, 0.0f);
        matrices.scale(scale, scale, 1.0f);
        context.drawItem(stack, 0, 0);
        matrices.pop();
        RenderSystem.depthMask(true);
        RenderSystem.disableDepthTest();
    }

    private static final class ArmorEntry {
        private final ItemStack stack;

        private ArmorEntry(ItemStack stack) {
            this.stack = stack;
        }

        private String getDurabilityText() {
            if (!stack.isDamageable()) return null;
            int maxDmg = stack.getMaxDamage();
            int currentDmg = stack.getDamage();
            int remaining = maxDmg - currentDmg;
            int percent = remaining * 100 / maxDmg;
            return percent + "%";
        }
    }
}
