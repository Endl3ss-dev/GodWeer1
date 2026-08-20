package org.privates.client.screen.rockstar;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.glfw.GLFW;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;

@Getter
@Setter
@Accessors(fluent = true)
public class RockstarScreen extends Screen implements IMinecraft {

    private final ArrayList<Category> categories = new ArrayList<>();
    private final float width, height;

    private float x, y;
    private Category selectedCategory;
    private float startX, startY;
    private boolean drag, input;
    private String find = "";
    private float findV;
    private float scaleV, scale;

    public RockstarScreen() {
        super(new StringTextComponent(""));
        width = 488;
        height = 318;
    }

    @Override
    protected void init() {

        if (x == 0 || y == 0) {
            x = mc.getMainWindow().getScaledWidth() / 2f - width / 2f;
            y = mc.getMainWindow().getScaledHeight() / 2f - height / 2f;
        }

        //categories.clear();
        if (categories.isEmpty()) {
            float offsetY = y + 48.5f;
            for (org.privates.client.managers.module.Category c : org.privates.client.managers.module.Category.values()) {
                Category category = new Category(this, c, x + 8, offsetY);
                offsetY += category.height() + 6f;
                categories.add(category);
            }
            selectedCategory = categories.get(0);
        }

        scale = 1;

        super.init();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        scaleV = Mathf.fast(scaleV, scale, 24);

        if (scale == 0 && scaleV <= .1f)
            super.closeScreen();

        //if (bindFramebuffer) {
        //}
        final int color = ColorUtil.getColor(24, 26, 30, scaleV);
        final Round round = Round.of(0);


        GlStateManager.pushMatrix();
        GlStateManager.translatef((x + 18), (y + height - 18), 1);
        GlStateManager.rotatef(180, 1, 0, 0);
        GlStateManager.translatef(-(x + 18), -(y + height - 18), 1);

        RenderUtil.bindTexture(new ResourceLocation("private/texture/avatar.jpg"));
        RenderUtil.Texture.customRound(matrixStack, x + 8, y + height - 28, 20, 20, 0, 0, scaleV, 0, 1f, Round.of(4));
        GlStateManager.popMatrix();

        RenderUtil.Rounded.roundedRect(matrixStack, x + 36, y, 1, height, color, round);
        RenderUtil.Rounded.roundedRect(matrixStack, x + 36, y + 24, width - 36, 1, color, round);

        for (Category category : categories)
            category.render(matrixStack, mouseX, mouseY, scaleV);

        Fonts.ROCKSTAR.draw(matrixStack, "h", x + width - Fonts.ROCKSTAR.getWidth("h", 9) - (12 - Fonts.ROCKSTAR.getHeight("h", 9) / 2f), y + 12 - Fonts.ROCKSTAR.getHeight("h", 9) / 2f, ColorUtil.getColor(255, 255, 255, scaleV), 9);

        if (input) {
            final float textHeight = Fonts.SF_MEDIUM.getHeight(find, 8) / 2f;
            Fonts.SF_MEDIUM.draw(matrixStack, find, x + 158 + (12 - textHeight), y + 12 - textHeight, ColorUtil.getColor(255, 255, 255, scaleV), 8);
        }

        if (drag) {

            final float diffX = mouseX - startX,
                    diffY = mouseY - startY;

            this.x += diffX;
            this.y += diffY;
            for (Category c : categories) {
                c.x(c.x() + diffX);
                c.y(c.y() + diffY);
                for (Module m : c.modules()) {
                    m.x(m.x() + diffX);
                    m.y(m.y() + diffY);
                    for (Setting s : m.settings()) {
                        s.x(s.x() + diffX);
                        s.y(s.y() + diffY);
                    }
                }
            }
            startY = mouseY;
            startX = mouseX;

        }
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (selectedCategory != null)
            selectedCategory.scroll((float) mouseX, (float) mouseY, (float) delta);

        if (Mathf.isHover(mouseX, mouseY, x + 158, y, width - 158, 24)) {
            input = !input;
            if (!input) {
                find = "";
                if (selectedCategory != null)
                    selectedCategory.loadModules(find);
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        startX = (float) mouseX;
        startY = (float) mouseY;

        if (Mathf.isHover(mouseX, mouseY, x, y, 36, 48) && button == 0) {
            drag = true;
            return super.mouseClicked(mouseX, mouseY, button);
        }

        for (Category category : categories)
            category.mouse((float) mouseX, (float) mouseY, button, true);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {

        drag = false;

        for (Category category : categories)
            category.mouse((float) mouseX, (float) mouseY, button, false);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Category category : categories)
            category.key(keyCode);

        if (input) {
            final String oldFind = find;
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_DELETE) {
                find = "";
                input = false;
            } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE)
                find = find.isEmpty() ? "" : find.substring(0, find.length() - 1);

            //if (!oldFind.equals(find) && selectedCategory != null && !find.trim().isEmpty()) {
            selectedCategory.loadModules(find);
            //}
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {

        for (Category category : categories)
            category.charTyped(codePoint);

        find += codePoint;
        if (!input)
            input = true;
        if (selectedCategory != null && !find.trim().isEmpty())
            selectedCategory.loadModules(find);
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void closeScreen() {
        scale = 0f;
        //super.closeScreen();
    }
}
