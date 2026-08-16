package never.lose.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import never.lose.NeverLose;
import never.lose.managers.Theme;
import never.lose.utils.animation.Animation;
import never.lose.utils.animation.Easing;
import never.lose.utils.font.Fonts;
import never.lose.utils.input.CursorManager;
import never.lose.utils.render.Draw;
import never.lose.utils.render.Hover;
import never.lose.utils.render.Scissor;
import never.lose.utils.render.providers.ColorProvider;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ThemeEditor {

    private static final String[] SLOTS = {
            "Client theme",
            "Icon color",
            "Inactive text",
            "Active text",
            "Interface bg",
            "ClickGui bg"
    };

    private static final float W = 124f;
    private static final float TAB = 18f;
    private static final float ROW_H = 15f;
    private static final float PAD = 5f;
    private static final float BTN_H = 14f;
    private static final float BTN_GAP = 4f;

    private static final int[] DEFAULTS = {
            0xFF3C6EF5,
            0xFF3C6EF5,
            0xFF7E86A0,
            0xFFE8E8E8,
            0xFF000000,
            0xFF000000
    };

    private static final float SV_SIZE = 58f;
    private static final float HUE_W = 7f;
    private static final float HUE_GAP = 4f;
    private static final float PICKER_PAD = 5f;
    private static final float PICKER_W = SV_SIZE + HUE_GAP + HUE_W;

    private static final File DIR = new File("neverlose");
    private static final File FILE = new File(DIR, "theme.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Animation expandAnim = new Animation(Easing.QUINTIC_OUT, 380);
    private final Animation pickerAnim = new Animation(Easing.QUINTIC_OUT, 240);
    private final Animation appearAnim = new Animation(Easing.QUINTIC_OUT, 340);

    public void resetAppear() {
        appearAnim.reset(0f);
    }

    private boolean expanded;
    private int editingSlot = -1;
    private final float[] hsv = new float[3];
    private boolean draggingSV, draggingH;
    private float pickerX, pickerY;

    private final int[] colors = new int[SLOTS.length];

    private float x, y, height;

    public ThemeEditor() {
        Theme t = NeverLose.getInstance().getThemeManager().getCurrentTheme();
        colors[0] = t.colorClient;
        colors[1] = t.colorIcons;
        colors[2] = t.colorInactiveText;
        colors[3] = t.colorText;
        colors[4] = t.colorInterfaceBg;
        colors[5] = t.colorClickGui;
        load();
        applyColors();
    }

    private void applyColors() {
        Theme t = NeverLose.getInstance().getThemeManager().getCurrentTheme();
        t.setAccent(colors[0]);
        t.colorIcons = colors[1];
        t.colorInactiveText = colors[2];
        t.colorText = colors[3];
        t.colorInterfaceBg = colors[4];
        t.colorClickGui = colors[5];
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int sw = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int sh = MinecraftClient.getInstance().getWindow().getScaledHeight();

        expandAnim.run(expanded);
        float ep = expandAnim.getValue();

        float contentH = PAD * 2f + SLOTS.length * ROW_H + BTN_GAP + BTN_H;
        height = TAB + contentH * ep;
        x = sw - W - 4f;
        y = sh - height - 4f;

        appearAnim.run(true);
        float appear = appearAnim.getValue();
        float appearShift = (1f - appear) * (height + 14f);
        y += appearShift;

        Draw.blur(x, y, W, height, 8f, 0f, 0f, 8f, ColorProvider.rgba(200, 200, 200, 255), 14f);
        Draw.round(x - 0.5f, y - 0.5f, W + 1f, height + 1f, 8.5f, 0f, 0f, 8.5f, ColorProvider.rgba(48, 66, 122, 70));
        Draw.round(x, y, W, height, 8f, 0f, 0f, 8f, ColorProvider.setAlpha(ColorProvider.getColorClickGui(), 130));

        Draw.round(x, y, W, TAB, 8f, 0f, 0f, 8f, ColorProvider.setAlpha(ColorProvider.getColorHeaderBg(), 110));
        float titleSize = 8.5f;
        float titleW = Draw.width(Fonts.REGULAR, "Theme", titleSize);
        float arrowW = 7f;
        float groupGap = 5f;
        float groupTotal = titleW + groupGap + arrowW;
        float groupX = x + (W - groupTotal) / 2f;
        Draw.text(Fonts.REGULAR, "Theme", groupX, y + (TAB - titleSize) / 2f, ColorProvider.rgba(255, 255, 255, 255), titleSize);
        drawArrow(groupX + titleW + groupGap + arrowW / 2f, y + TAB / 2f, !expanded, ColorProvider.setAlpha(ColorProvider.getColorIcons(), 255));

        boolean tabHover = Hover.isHovered(mouseX, mouseY, x, y, W, TAB);
        if (tabHover) CursorManager.requestHand();

        if (ep > 0.01f) {
            Scissor.push();
            Scissor.setFromComponentCoordinates(x, y + TAB, W, height - TAB);

            float rowY = y + TAB + PAD;
            for (int i = 0; i < SLOTS.length; i++) {
                renderRow(i, x + 4f, rowY, W - 8f, mouseX, mouseY, ep);
                rowY += ROW_H;
            }

            float btnX = x + 4f;
            float btnY = rowY + BTN_GAP;
            float btnW = W - 8f;
            boolean btnHover = Hover.isHovered(mouseX, mouseY, btnX, btnY, btnW, BTN_H);
            if (btnHover && ep > 0.9f) CursorManager.requestHand();
            int btnBg = btnHover
                    ? ColorProvider.rgba(60, 78, 140, (int) (110 * ep))
                    : ColorProvider.rgba(48, 66, 122, (int) (70 * ep));
            Draw.round(btnX, btnY, btnW, BTN_H, 3f, btnBg);
            String label = "Reset";
            float lw = Draw.width(Fonts.REGULAR, label, 7f);
            Draw.text(Fonts.REGULAR, label, btnX + (btnW - lw) / 2f, btnY + (BTN_H - 7f) / 2f,
                    ColorProvider.rgba(230, 234, 245, (int) (255 * ep)), 7f);

            Scissor.unset();
            Scissor.pop();
        }

        if (draggingSV) {
            hsv[1] = clamp01((mouseX - pickerX) / SV_SIZE);
            hsv[2] = 1f - clamp01((mouseY - pickerY) / SV_SIZE);
            applyHSV();
        } else if (draggingH) {
            hsv[0] = clamp01((float) (mouseY - pickerY) / SV_SIZE);
            applyHSV();
        }

        pickerAnim.run(editingSlot >= 0 && expanded);
        if (pickerAnim.getValue() > 0.01f) {
            renderPicker(mouseX, mouseY, pickerAnim.getValue());
        }
    }

    private void renderRow(int slot, float rx, float ry, float rw, int mouseX, int mouseY, float ep) {
        boolean hov = Hover.isHovered(mouseX, mouseY, rx, ry, rw, ROW_H);
        boolean active = editingSlot == slot;
        if ((hov || active) && ep > 0.9f) CursorManager.requestHand();

        if (hov || active) {
            Draw.round(rx, ry, rw, ROW_H - 1.5f, 3f, ColorProvider.rgba(60, 78, 140, (int) (55 * ep)));
        }

        int textColor = active ? ColorProvider.getColorText() : ColorProvider.rgba(210, 214, 230, 255);
        Draw.text(Fonts.REGULAR, SLOTS[slot], rx + 4f, ry + 4f,
                ColorProvider.setAlpha(textColor, (int) (255 * ep)), 7f);

        float sw = 11f;
        float sx = rx + rw - sw - 3f;
        float sy = ry + (ROW_H - sw) / 2f - 0.75f;
        Draw.round(sx - 0.75f, sy - 0.75f, sw + 1.5f, sw + 1.5f, 3f, ColorProvider.rgba(255, 255, 255, (int) (60 * ep)));
        Draw.round(sx, sy, sw, sw, 2.5f, ColorProvider.setAlpha(colors[slot], (int) (255 * ep)));
    }

    private void renderPicker(int mouseX, int mouseY, float anim) {
        int a = (int) (255 * anim);
        float px = pickerX, py = pickerY;

        Draw.blur(px - PICKER_PAD, py - PICKER_PAD, PICKER_W + PICKER_PAD * 2f, SV_SIZE + PICKER_PAD * 2f, 5f,
                ColorProvider.rgba(200, 200, 200, (int) (255 * anim)), 12f);
        Draw.round(px - PICKER_PAD, py - PICKER_PAD, PICKER_W + PICKER_PAD * 2f, SV_SIZE + PICKER_PAD * 2f, 5f,
                ColorProvider.setAlpha(ColorProvider.getColorClickGui(), (int) (140 * anim)));

        int cHue = ColorProvider.setAlpha(Color.HSBtoRGB(hsv[0], 1f, 1f), a);
        int white = ColorProvider.rgba(255, 255, 255, a);
        int clearWhite = ColorProvider.rgba(255, 255, 255, 0);
        int black = ColorProvider.rgba(0, 0, 0, a);
        int clearBlack = ColorProvider.rgba(0, 0, 0, 0);
        Draw.round(px, py, SV_SIZE, SV_SIZE, 2f, cHue);
        Draw.round(px, py, SV_SIZE, SV_SIZE, 2f, white, white, clearWhite, clearWhite);
        Draw.round(px, py, SV_SIZE, SV_SIZE, 2f, clearBlack, black, black, clearBlack);

        float scx = px + hsv[1] * SV_SIZE;
        float scy = py + (1f - hsv[2]) * SV_SIZE;
        Draw.round(scx - 2.5f, scy - 2.5f, 5f, 5f, 2.5f, ColorProvider.rgba(0, 0, 0, (int) (180 * anim)));
        Draw.round(scx - 1.75f, scy - 1.75f, 3.5f, 3.5f, 1.75f, white);

        float hueX = px + SV_SIZE + HUE_GAP;
        for (float i = 0; i <= SV_SIZE; i += 0.5f) {
            Draw.round(hueX, py + i, HUE_W, 1f, 0f, ColorProvider.setAlpha(Color.HSBtoRGB(i / SV_SIZE, 1f, 1f), a));
        }
        float hcy = py + hsv[0] * SV_SIZE;
        Draw.round(hueX - 1.5f, hcy - 2f, HUE_W + 3f, 4f, 2f, ColorProvider.rgba(0, 0, 0, (int) (180 * anim)));
        Draw.round(hueX - 0.5f, hcy - 1f, HUE_W + 1f, 2f, 1f, white);
    }

    private void drawArrow(float cx, float cy, boolean up, int color) {
        float w = 7f, h = 4f;
        int rows = 8;
        float top = cy - h / 2f;
        for (int i = 0; i < rows; i++) {
            float frac = i / (float) (rows - 1);
            float rowW = up ? w * frac : w * (1f - frac);
            float ry = top + frac * h;
            Draw.round(cx - rowW / 2f, ry, rowW, h / rows + 0.5f, 0f, color);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Hover.isHovered(mouseX, mouseY, x, y, W, TAB) && button == 0) {
            expanded = !expanded;
            if (!expanded) editingSlot = -1;
            return true;
        }

        if (expandAnim.getValue() < 0.5f) {
            return Hover.isHovered(mouseX, mouseY, x, y, W, height);
        }

        if (editingSlot >= 0 && pickerAnim.getValue() > 0.5f) {
            if (Hover.isHovered(mouseX, mouseY, pickerX, pickerY, SV_SIZE, SV_SIZE) && button == 0) {
                draggingSV = true;
                hsv[1] = clamp01((float) (mouseX - pickerX) / SV_SIZE);
                hsv[2] = 1f - clamp01((float) (mouseY - pickerY) / SV_SIZE);
                applyHSV();
                return true;
            }
            if (Hover.isHovered(mouseX, mouseY, pickerX + SV_SIZE + HUE_GAP - 1f, pickerY, HUE_W + 2f, SV_SIZE) && button == 0) {
                draggingH = true;
                hsv[0] = clamp01((float) (mouseY - pickerY) / SV_SIZE);
                applyHSV();
                return true;
            }
            boolean insidePicker = Hover.isHovered(mouseX, mouseY,
                    pickerX - PICKER_PAD, pickerY - PICKER_PAD, PICKER_W + PICKER_PAD * 2f, SV_SIZE + PICKER_PAD * 2f);
            if (insidePicker) return true;
        }

        float btnY = y + TAB + PAD + SLOTS.length * ROW_H + BTN_GAP;
        if (Hover.isHovered(mouseX, mouseY, x + 4f, btnY, W - 8f, BTN_H) && button == 0) {
            resetToDefault();
            return true;
        }

        float rowY = y + TAB + PAD;
        for (int i = 0; i < SLOTS.length; i++) {
            if (Hover.isHovered(mouseX, mouseY, x + 4f, rowY, W - 8f, ROW_H) && button == 0) {
                editingSlot = (editingSlot == i) ? -1 : i;
                if (editingSlot >= 0) {
                    loadHSV(i);
                    pickerX = x - PICKER_W - PICKER_PAD - 4f;
                    pickerY = rowY + ROW_H / 2f - SV_SIZE / 2f;
                    float sh = MinecraftClient.getInstance().getWindow().getScaledHeight();
                    pickerY = Math.max(PICKER_PAD + 2f, Math.min(pickerY, sh - SV_SIZE - PICKER_PAD - 2f));
                }
                return true;
            }
            rowY += ROW_H;
        }

        return Hover.isHovered(mouseX, mouseY, x, y, W, height);
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingSV || draggingH) save();
        draggingSV = false;
        draggingH = false;
    }

    private void resetToDefault() {
        System.arraycopy(DEFAULTS, 0, colors, 0, colors.length);
        editingSlot = -1;
        applyColors();
        save();
    }

    public static void applyStartupTheme() {
        int[] c = DEFAULTS.clone();
        if (FILE.exists()) {
            try {
                JsonObject json = JsonParser.parseString(Files.readString(FILE.toPath())).getAsJsonObject();
                if (json.has("colors")) {
                    JsonArray arr = json.getAsJsonArray("colors");
                    for (int i = 0; i < Math.min(arr.size(), c.length); i++) {
                        c[i] = arr.get(i).getAsInt();
                    }
                }
            } catch (Exception ignored) {
            }
        }
        if (NeverLose.getInstance() == null || NeverLose.getInstance().getThemeManager() == null) return;
        Theme t = NeverLose.getInstance().getThemeManager().getCurrentTheme();
        t.setAccent(c[0]);
        t.colorIcons = c[1];
        t.colorInactiveText = c[2];
        t.colorText = c[3];
        t.colorInterfaceBg = c[4];
        t.colorClickGui = c[5];
    }

    private void applyHSV() {
        if (editingSlot < 0) return;
        colors[editingSlot] = Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]) | 0xFF000000;
        applyColors();
    }

    private void loadHSV(int slot) {
        Color c = new Color(colors[slot], true);
        Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsv);
    }

    private static float clamp01(double v) {
        return (float) Math.max(0.0, Math.min(1.0, v));
    }

    public void save() {
        try {
            if (!DIR.exists()) DIR.mkdirs();
            JsonObject json = new JsonObject();
            JsonArray arr = new JsonArray();
            for (int c : colors) arr.add(c);
            json.add("colors", arr);
            Files.writeString(FILE.toPath(), GSON.toJson(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (!FILE.exists()) return;
        try {
            JsonObject json = JsonParser.parseString(Files.readString(FILE.toPath())).getAsJsonObject();
            if (json.has("colors")) {
                JsonArray arr = json.getAsJsonArray("colors");
                for (int i = 0; i < Math.min(arr.size(), colors.length); i++) {
                    colors[i] = arr.get(i).getAsInt();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
