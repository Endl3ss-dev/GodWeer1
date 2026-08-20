package org.privates.client.screen.flatgui;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.joml.Vector2f;

import java.util.function.Supplier;

@Getter
@Setter
@Accessors(fluent = true)
public abstract class AbstractPanel {
    protected final Vector2f position = new Vector2f();
    protected final Vector2f size = new Vector2f();
    protected float alpha = 1F;
    private Supplier<Boolean> visibleSupplier = () -> true;

    public abstract void resize(Minecraft minecraft, int width, int height);

    public abstract void init();

    public abstract void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks);

    public abstract boolean mouseClicked(double mouseX, double mouseY, int button);

    public abstract boolean mouseReleased(double mouseX, double mouseY, int button);

    public abstract boolean keyPressed(int keyCode, int scanCode, int modifiers);

    public abstract boolean keyReleased(int keyCode, int scanCode, int modifiers);

    public abstract boolean charTyped(char codePoint, int modifiers);

    public abstract void onClose();

    public boolean hover(double mouseX, double mouseY) {
        return isHover(mouseX, mouseY, position.x, position.y, size.x, size.y);
    }

    public boolean hovered(double mouseX, double mouseY, float x, float y, float width, float height) {
        return isHover(mouseX, mouseY, x, y, width, height);
    }

    public AbstractPanel setVisible(Supplier<Boolean> visibleSupplier) {
        this.visibleSupplier = visibleSupplier != null ? visibleSupplier : () -> true;
        return this;
    }


    public boolean visible() {
        return visibleSupplier.get();
    }

    public boolean isHover(double mouseX, double mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}