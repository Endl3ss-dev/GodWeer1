package never.lose.ui.object;

public class RenderObject {

    public float x, y, width, height;

    public void render(int mouseX, int mouseY) {}

    public boolean click(int mouseX, int mouseY, int button) {
        return false;
    }

    public void release(int mouseX, int mouseY, int button) {}

    public void scroll(int mouseX, int mouseY, float scrollY) {}

    public void charTyped(char ch, int keyCode) {}

    public void keyPressed(int keyCode, int scanCode, int modifiers) {}
}