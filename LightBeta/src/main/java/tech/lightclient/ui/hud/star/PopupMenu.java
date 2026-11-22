package tech.lightclient.ui.hud.star;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;

public class PopupMenu {
    // Геометрия повторяет список "Активные бинды": шапка и каждая строка —
    // самостоятельные прямоугольники одинаковых габаритов с тем же скруглением,
    // шрифтом и отступами.
    private static final float HEADER_HEIGHT = HudElement.HEADER_HEIGHT;
    private static final float ROW_HEIGHT = HudElement.ROW_HEIGHT;
    private static final float HEADER_GAP = HudListElement.GAP;
    private static final float ROW_GAP = HudListElement.ROW_GAP;
    private static final float ROUNDING = 7.0f;
    private static final float TEXT_PAD = 7.0f;
    private static final float ICON_PAD = 6.5f;
    private static final float TEXT_SIZE = 7.0f;
    private static final float LABEL_GAP = 6.0f;
    private static final float CHECK_SIZE = 6.0f;
    private static final float ICON_SIZE = 8.0f;

    public interface Item {
        float contentWidth();

        void render(Renderer2D r, float x, float y, float width, float alpha);

        boolean mouseClicked(double mouseX, double mouseY, int button, float x, float y, float width);
    }

    private final List<Item> items = new ArrayList<>();
    private final SAnim animation = new SAnim(300L, 0.0f, SEasing.BAKEK_SIZE);
    private final float minWidth;
    private final float offsetFactor;
    private String title = "Настройки";
    private boolean showing = true;
    private boolean closed;
    private Runnable onClose = () -> {
    };

    public float x;
    public float y;
    public float width;
    private float height;

    public PopupMenu(float x, float y, float minWidth) {
        this(x, y, minWidth, 6.0f);
    }

    public PopupMenu(float x, float y, float minWidth, float offsetFactor) {
        this.x = x;
        this.y = y;
        this.minWidth = minWidth;
        this.width = minWidth;
        this.offsetFactor = offsetFactor;
    }

    public PopupMenu title(String text) {
        this.title = text;
        return this;
    }

    public PopupMenu button(String label, String iconPath, Runnable action) {
        items.add(new ButtonItem(label, iconPath, action));
        return this;
    }

    public PopupMenu checkbox(String label, BooleanSupplier supplier, Consumer<Boolean> setter) {
        items.add(new CheckItem(label, supplier, setter));
        return this;
    }

    public PopupMenu onClose(Runnable runnable) {
        this.onClose = runnable;
        return this;
    }

    public void setShowing(boolean value) {
        if (!value && !this.closed && this.showing) {
            this.onClose.run();
            this.closed = true;
        }
        this.showing = value;
    }

    public boolean isShowing() {
        return this.showing;
    }

    public SAnim getAnimation() {
        return this.animation;
    }

    public float getHeight() {
        layout();
        return height;
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }

    private void layout() {
        float required = FontRegistry.DELTA_HUD.getWidth(title, TEXT_SIZE) + TEXT_PAD * 2.0f;
        for (Item item : items) {
            required = Math.max(required, item.contentWidth());
        }
        width = Math.max(minWidth, required);

        height = HEADER_HEIGHT;
        for (int i = 0; i < items.size(); i++) {
            height += (i == 0 ? HEADER_GAP : ROW_GAP) + ROW_HEIGHT;
        }
    }

    private float rowY(int index) {
        return y + HEADER_HEIGHT + HEADER_GAP + index * (ROW_HEIGHT + ROW_GAP);
    }

    public void render(Renderer2D r, float globalAlpha) {
        layout();

        animation.setEasing(showing ? SEasing.BAKEK : SEasing.BAKEK_BACK);
        animation.update(showing ? 1.0f : 0.0f);
        float anim = animation.getValue();
        if (anim <= 0.001f) {
            if (!showing) {
                setShowing(false);
            }
            return;
        }
        float appear = anim * globalAlpha;

        float scaleIn = 0.5f + anim * 0.5f;
        float originX = x + width / offsetFactor;
        float originY = y + height / offsetFactor;
        r.pushScale(scaleIn, originX, originY);
        try {
            // Шапка — отдельный прямоугольник, текст центрируется по её высоте.
            HudElement.drawClientRect(r, "StarPopup", x, y, width, HEADER_HEIGHT, ROUNDING, appear, false);
            r.pushRoundedClipRect(x, y, width, HEADER_HEIGHT, ROUNDING, ROUNDING, ROUNDING, ROUNDING);
            try {
                r.text(FontRegistry.DELTA_HUD, x + TEXT_PAD,
                        StarUtil.textBaseline(FontRegistry.DELTA_HUD, 'H', y, HEADER_HEIGHT, TEXT_SIZE),
                        TEXT_SIZE, title, StarUtil.text(appear));
            } finally {
                r.popClipRect();
            }

            // Каждая настройка — собственный прямоугольник тех же габаритов, что и шапка.
            for (int i = 0; i < items.size(); i++) {
                float rowY = rowY(i);
                HudElement.drawClientRect(r, "StarPopup", x, rowY, width, ROW_HEIGHT, ROUNDING, appear, false);
                r.pushRoundedClipRect(x, rowY, width, ROW_HEIGHT, ROUNDING, ROUNDING, ROUNDING, ROUNDING);
                try {
                    items.get(i).render(r, x, rowY, width, appear);
                } finally {
                    r.popClipRect();
                }
            }
        } finally {
            r.popScale();
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).mouseClicked(mouseX, mouseY, button, x, rowY(i), width)) {
                return true;
            }
        }
        return isHovered(mouseX, mouseY);
    }

    private static float baseline(float topY, float boxHeight) {
        return StarUtil.textBaseline(FontRegistry.DELTA_HUD, 'H', topY, boxHeight, TEXT_SIZE);
    }

    private final class ButtonItem implements Item {
        private final String label;
        private final String iconPath;
        private final Runnable action;
        private final SAnim hover = new SAnim(300L, 0.0f, SEasing.FIGMA_EASE_IN_OUT);
        private float rx;
        private float ry;

        ButtonItem(String label, String iconPath, Runnable action) {
            this.label = label;
            this.iconPath = iconPath;
            this.action = action;
        }

        @Override
        public float contentWidth() {
            return TEXT_PAD + FontRegistry.DELTA_HUD.getWidth(label, TEXT_SIZE) + LABEL_GAP + ICON_SIZE + ICON_PAD;
        }

        @Override
        public void render(Renderer2D r, float x, float y, float width, float alpha) {
            this.rx = x;
            this.ry = y;
            hover.update(isInside());
            boolean danger = label.startsWith("Убрать") || label.startsWith("Скрыть");
            int base = danger ? blend(StarUtil.RED, 0xFFFFFFFF, 0.3f) : ClientColors.TEXT.getRGB();
            float brighten = 0.75f + 0.25f * hover.getValue();
            r.text(FontRegistry.DELTA_HUD, x + TEXT_PAD, baseline(y, ROW_HEIGHT), TEXT_SIZE, label,
                    ClientColors.applyAlpha(base, brighten * alpha));

            Identifier icon = StarUtil.id(iconPath);
            float iconX = x + width - ICON_SIZE - ICON_PAD;
            float iconY = y + (ROW_HEIGHT - ICON_SIZE) / 2.0f;
            r.drawTexture(icon, iconX, iconY, ICON_SIZE, ICON_SIZE,
                    ClientColors.applyAlpha(base, brighten * alpha));
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button, float x, float y, float width) {
            if (button == 0 && mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + ROW_HEIGHT) {
                action.run();
                return true;
            }
            return false;
        }

        private boolean isInside() {
            MinecraftClient client = MinecraftClient.getInstance();
            double mx = client.mouse.getScaledX(client.getWindow());
            double my = client.mouse.getScaledY(client.getWindow());
            return mx >= rx && my >= ry && mx <= rx + PopupMenu.this.width && my <= ry + ROW_HEIGHT;
        }
    }

    private final class CheckItem implements Item {
        private final String label;
        private final BooleanSupplier supplier;
        private final Consumer<Boolean> setter;
        private final SAnim enableAnim = new SAnim(300L, 0.0f, SEasing.FIGMA_EASE_IN_OUT);

        CheckItem(String label, BooleanSupplier supplier, Consumer<Boolean> setter) {
            this.label = label;
            this.supplier = supplier;
            this.setter = setter;
        }

        @Override
        public float contentWidth() {
            return TEXT_PAD + FontRegistry.DELTA_HUD.getWidth(label, TEXT_SIZE) + LABEL_GAP + CHECK_SIZE + ICON_PAD;
        }

        @Override
        public void render(Renderer2D r, float x, float y, float width, float alpha) {
            enableAnim.update(supplier.getAsBoolean() ? 1.0f : 0.0f);
            float brighten = 0.75f + 0.25f * enableAnim.getValue();
            r.text(FontRegistry.DELTA_HUD, x + TEXT_PAD, baseline(y, ROW_HEIGHT), TEXT_SIZE, label,
                    ClientColors.applyAlpha(ClientColors.TEXT.getRGB(), brighten * alpha));

            float checkAlpha = enableAnim.getValue() * alpha;
            if (checkAlpha > 0.01f) {
                // Галочка встаёт на место иконки шапки биндов — тот же правый отступ.
                r.drawTexture(StarUtil.id("icons/hud/check.png"),
                        x + width - CHECK_SIZE - ICON_PAD, y + (ROW_HEIGHT - CHECK_SIZE) / 2.0f,
                        CHECK_SIZE, CHECK_SIZE, StarUtil.text(checkAlpha));
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button, float x, float y, float width) {
            if (button == 0 && mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + ROW_HEIGHT) {
                setter.accept(!supplier.getAsBoolean());
                return true;
            }
            return false;
        }
    }

    private static int blend(int a, int b, float amount) {
        float t = Math.max(0.0f, Math.min(1.0f, amount));
        int ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
        int br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
        int rr = Math.round(ar + (br - ar) * t);
        int rg = Math.round(ag + (bg - ag) * t);
        int rb = Math.round(ab + (bb - ab) * t);
        return 0xFF000000 | (rr << 16) | (rg << 8) | rb;
    }
}
