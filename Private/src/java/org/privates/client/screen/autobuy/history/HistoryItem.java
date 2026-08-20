package org.privates.client.screen.autobuy.history;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.screen.autobuy.Element;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.font.Fonts;

@Getter
public class HistoryItem implements Element {

    private final ItemStack item;

    @Setter
    private float x, y;

    private final float width;
    private final float height;
    private float delta, deltaV;

    @Setter
    private int price;
    @Setter
    private String author;
    @Setter
    private String name;

    public HistoryItem(ItemStack item, String name, int price, String author, float x, float y) {
        this.item = item;
        this.name = name;
        this.price = price;
        this.author = author;
        this.x = x;
        this.y = y;
        this.width = 123 - 8; // 123 - 4x2
        this.height = 16;
    }

    @Override
    public void render(MatrixStack matrixStack, float mouseX, float mouseY) {
        RectUtil.drawItemStack(item, x, y, true, height, height);
        Fonts.SF_MEDIUM.draw(matrixStack, name, x + 18, y - 2, -1, 6);
        Fonts.SF_MEDIUM.draw(matrixStack, "Куплен за: ", x + 18, y + Fonts.SF_MEDIUM.getHeight(name, 6) - 1, ColorUtil.getColor(137, 137, 137), 6);
        Fonts.SF_MEDIUM.draw(matrixStack, price + "¤", x + 18 + Fonts.SF_MEDIUM.getWidth("Куплен за: ", 6), y + Fonts.SF_MEDIUM.getHeight(name, 6) - 1, Theme.getInstance().textColor(), 6);
        Fonts.SF_MEDIUM.draw(matrixStack, "Продавец: ", x + 18, y + Fonts.SF_MEDIUM.getHeight(name, 6) + Fonts.SF_MEDIUM.getHeight("Куплен за: " + price, 6), ColorUtil.getColor(137, 137, 137), 6);
        Fonts.SF_MEDIUM.draw(matrixStack, author, x + 18 + Fonts.SF_MEDIUM.getWidth("Продавец: ", 6), y + Fonts.SF_MEDIUM.getHeight(name, 6) + Fonts.SF_MEDIUM.getHeight("Куплен за: " + price, 6), Theme.getInstance().textColor(), 6);
    }

    @Override
    public void click(float mouseX, float mouseY, int button, int action) {

    }

    @Override
    public void key(int key) {

    }
}
