package org.privates.client.screen.rockgui;

import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.screen.rockgui.windows.Window;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.text.TextBox;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RockGuiWindow implements IMinecraft {

    private float x, y, width, height;
    private boolean drag, canDrag = true;
    private float dragX, dragY;
    private Category current = Category.COMBAT;
    private Category prevCurrent = Category.COMBAT;
    private int prevIndex = 0;
    private Module opened, prevOpened, queue;
    private float scroll, settingsScroll;
    private final Animation scrollValue = new Animation().setSpeed(300).setEasing(Easings.CIRC_OUT);
    private final Animation settingsScrollValue = new Animation().setSpeed(300).setEasing(Easings.CIRC_OUT);
    private final List<Window> windows = new ArrayList<>();
    private TextBox input;
    private RockGuiRenderer renderer;

    public RockGuiWindow(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.renderer = new RockGuiRenderer();
        this.renderer.window = this;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (drag && canDrag) {
            x = mouseX + dragX;
            y = mouseY + dragY;
        }

        renderer.render(matrixStack, mouseX, mouseY, partialTicks);

        for (Window window : new ArrayList<>(windows)) {
            window.render(matrixStack, mouseX, mouseY, partialTicks);
            if (!window.getOpening().isForward() && window.getOpening().finished(false)) {
                windows.remove(window);
            }
        }
    }

    public void clicked(double mouseX, double mouseY, int button) {
        for (Window window : new ArrayList<>(windows)) {
            if (window.clicked(mouseX, mouseY, button)) return;
        }

        if (button == 0 && Mathf.isHover((float) mouseX, (float) mouseY, x, y, width, 29)) {
            drag = true;
            dragX = x - (float) mouseX;
            dragY = y - (float) mouseY;
        }
    }

    public void released(double mouseX, double mouseY, int button) {
        drag = false;

        for (Window window : new ArrayList<>(windows)) {
            window.released(mouseX, mouseY, button);
        }
    }

    public void dragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (Window window : new ArrayList<>(windows)) {
            window.dragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    public void scrolled(double mouseX, double mouseY, double delta) {
        for (Window window : new ArrayList<>(windows)) {
            if (window.scrolled(mouseX, mouseY, delta)) return;
        }

        if (Mathf.isHover((float) mouseX, (float) mouseY, x + 38, y + 29, 118, height - 29)) {
            scroll += (float) delta * 15;
            if (scroll > 0) scroll = 0;
        }

        if (opened != null && Mathf.isHover((float) mouseX, (float) mouseY, x + 156, y + 29, width - 156, height - 29)) {
            settingsScroll += (float) delta * 15;
        }
    }

    public void pressed(int keyCode, int scanCode, int modifiers) {
        for (Window window : new ArrayList<>(windows)) {
            window.pressed(keyCode, scanCode, modifiers);
        }
    }

    public void charTyped(char codePoint, int modifiers) {
        if (input != null) {
            input.charTyped(codePoint);
        }

        for (Window window : new ArrayList<>(windows)) {
            window.charTyped(codePoint, modifiers);
        }
    }

    public void tick() {
        scrollValue.run(scroll, 0.1f);
        scrollValue.update();

        settingsScrollValue.run(settingsScroll, 0.1f);
        settingsScrollValue.update();

        RockGuiRenderer.opening.update(); // ДОБАВИТЬ ЭТУ СТРОКУ

        for (Window window : new ArrayList<>(windows)) {
            window.tick();
        }
    }

    public void close() {
        RockGuiRenderer.opening.setForward(false);
    }

    public void changeCategory(Category category) {
        prevCurrent = current;
        prevIndex = current.ordinal();
        current = category;
        scroll = 0;
    }


    public GlyphType get(Category category) {
        return RockGuiRenderer.glyphes[category.ordinal()];
    }

    public List<SettingRect> get(Module module) {
        List<SettingRect> result = new ArrayList<>();
        for (SettingRect rect : RockGuiRenderer.settings) {
            if (rect.getParent().getParent() == module) {
                result.add(rect);
            }
        }
        return result;
    }
}