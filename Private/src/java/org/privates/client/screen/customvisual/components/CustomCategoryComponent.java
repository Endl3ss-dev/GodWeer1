package org.privates.client.screen.customvisual.components;

public class CustomCategoryComponent extends PropertyComponent {
    public CustomCategoryComponent(String name) {
        super(name);
    }

    @Override
    public void render(net.mojang.blaze3d.matrix.MatrixStack matrix, float x, float y, float width, float height, int mouseX, int mouseY) {
        // Кастомный рендер категории (можно скопировать из CategoryComponent)
        org.privates.client.utils.render.draw.RenderUtil.Rounded.roundedRect(matrix, x, y + 5, width, height - 10,
                org.privates.client.utils.render.color.ColorUtil.getColor(40, 40, 50, 150), org.privates.client.utils.render.draw.Round.of(3));
        org.privates.client.utils.render.font.Fonts.SF_MEDIUM.draw(matrix, name, x + 8, y + height / 2 - 4,
                org.privates.client.managers.module.impl.client.Theme.getInstance().clientColor(), 8);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, float x, float y, float width, float height) {
        return false;
    }
} 