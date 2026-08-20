package fun.volon.client.ui.clickgui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import fun.volon.Volon;
import fun.volon.api.QClient;
import fun.volon.api.storages.implement.ThemeStorage;
import fun.volon.api.utils.animation.gui.GuiAnimation;
import fun.volon.api.utils.animation.gui.GuiEasing;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.input.CursorManager;
import fun.volon.api.utils.math.HoveringUtils;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.api.utils.scissor.ScissorUtils;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

@Getter
@Setter
public class ThemeManagerWindow implements QClient {
    private float x, y, width = 110, height = 250;
    private boolean open;
    private final GuiAnimation openAnim = new GuiAnimation(GuiEasing.QUINTIC_OUT, 400);
    private final GuiAnimation scrollAnim = new GuiAnimation(GuiEasing.CUBIC_IN_OUT, 200);
    private final GuiAnimation pickerAnim = new GuiAnimation(GuiEasing.QUINTIC_OUT, 250);
    private final GuiAnimation presetScrollAnim = new GuiAnimation(GuiEasing.CUBIC_IN_OUT, 180);

    private float scroll, maxScroll;
    private float presetScroll, presetMaxScroll;

    private final ClickGui parent;

    private static final File THEME_DIR = new File("volon");
    private static final File THEME_FILE = new File(THEME_DIR, "themes.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static final String[] COLOR_NAMES = {
            "Основной", "Визуальные модули", "Текст", "Неактивный текст",
            "Цвет заголовков", "Текст заголовков", "Слайдер", "Круг слайдера",
            "Окно слайдера", "Индикатор", "Неактивный индикатор", "Кнопка",
            "Неактивная кнопка", "Разделитель", "Поле", "Неактивное поле",
            "Текст в подсказках", "Фон окон", "Цвет иконок", "Цвет клиента"
    };
    static final int COLOR_COUNT = COLOR_NAMES.length;

    private static final int[][] PRESETS = {
            {0xFFAA44FF, 0xFF0D0D10}, {0xFF44AAFF, 0xFF0A0A18},
            {0xFF44FFAA, 0xFF0A1810}, {0xFFFF4444, 0xFF1A0808},
            {0xFFFF9944, 0xFF1A1008}, {0xFF44FFEE, 0xFF081A18},
            {0xFFFF44AA, 0xFF1A0810}, {0xFF8844FF, 0xFF100A1A},
            {0xFFFFDD44, 0xFF1A1808}, {0xFF44FF44, 0xFF081A08},
            {0xFF4488FF, 0xFF080A1A},
    };

    private final java.util.List<SavedTheme> savedThemes = new ArrayList<>();

    private static class SavedTheme {
        String name;
        int[] colors;

        SavedTheme(String name, int[] colors) {
            this.name = name;
            this.colors = colors;
        }
    }

    private int[] currentColors;
    private int editingSlot = -1;
    private final float[] hsv = new float[3];
    private boolean draggingSV, draggingH;
    private float pickerPopupX, pickerPopupY;

    private String themeName = "";
    private boolean nameFieldFocused = false;
    private static final float HEADER_H = 20f;
    private static final float ROW_H = 16f;
    private static final float PRESET_D = 9f;
    private static final float PRESET_GAP = 1.5f;
    private static final float PRESET_ROW_H = PRESET_D + 4f;
    private static final float FIELD_H = 16f;
    private static final float BTN_H = 16f;
    private static final float FIXED_BOTTOM_H = 1f + 2f + PRESET_ROW_H + 2f + 1f + 2f + FIELD_H + 2f + BTN_H + 3f;
    private static final float SV_SIZE = 60f;
    private static final float HUE_W = 6f;
    private static final float PICKER_PAD = 4f;
    private static final float PICKER_W = SV_SIZE + PICKER_PAD + HUE_W;
    private static final float PICKER_H_TOT = SV_SIZE;

    public ThemeManagerWindow(ClickGui parent) {
        this.parent = parent;
        currentColors = buildDefaultColors(0xFFAA44FF, 0xFF0D0D10);
        load();
        applyColors();
    }

    static int[] buildDefaultColors(int c1, int c2) {
        int[] c = new int[COLOR_COUNT];
        c[0] = c2;
        c[1] = c1;
        c[2] = 0xFFE8E8E8;
        c[3] = 0xFF888899;
        c[4] = 0xFF0A0A0D;
        c[5] = c1;
        c[6] = c1;
        c[7] = 0xFFFFFFFF;
        c[8] = 0xFF2A2A35;
        c[9] = c1;
        c[10] = 0xFF2A2A35;
        c[11] = c1;
        c[12] = 0xFF2A2A35;
        c[13] = 0xFF2A2A35;
        c[14] = 0xFF1A1A22;
        c[15] = c2;
        c[16] = 0xFFFFFFFF;
        c[17] = c2;
        c[18] = c1;
        c[19] = c1;
        return c;
    }

    private void applyColors() {
        if (Volon.INSTANCE == null || Volon.INSTANCE.themeStorage == null) return;
        ThemeStorage.Themes selected = Volon.INSTANCE.themeStorage.getThemes();
        if (selected == null) return;
        fun.volon.api.storages.implement.helpertstorages.Theme t = selected.getTheme();
        int[] c = currentColors;
        t.setAllColors(c[1], c[0],
                c[0], c[1], c[2], c[3],
                c[4], c[5], c[6], c[7],
                c[8], c[9], c[10], c[11],
                c[12], c[13], c[14], c[15],
                c[16], c[17], c[18], c[19]);
    }

    public void save() {
        try {
            if (!THEME_DIR.exists()) THEME_DIR.mkdirs();
            JsonObject json = new JsonObject();
            JsonArray arr = new JsonArray();
            for (int col : currentColors) arr.add(col);
            json.add("colors", arr);
            JsonArray saved = new JsonArray();
            for (SavedTheme t : savedThemes) {
                JsonObject obj = new JsonObject();
                obj.addProperty("name", t.name);
                JsonArray ca = new JsonArray();
                for (int col : t.colors) ca.add(col);
                obj.add("colors", ca);
                saved.add(obj);
            }
            json.add("savedThemes", saved);
            Files.writeString(THEME_FILE.toPath(), GSON.toJson(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (!THEME_FILE.exists()) return;
        try {
            JsonObject json = JsonParser.parseString(Files.readString(THEME_FILE.toPath())).getAsJsonObject();
            if (json.has("colors")) {
                JsonArray arr = json.getAsJsonArray("colors");
                for (int i = 0; i < Math.min(arr.size(), COLOR_COUNT); i++)
                    currentColors[i] = arr.get(i).getAsInt();
            }
            if (json.has("savedThemes")) {
                savedThemes.clear();
                for (JsonElement el : json.getAsJsonArray("savedThemes")) {
                    JsonObject obj = el.getAsJsonObject();
                    String name = obj.get("name").getAsString();
                    JsonArray ca = obj.getAsJsonArray("colors");
                    int[] cols = new int[COLOR_COUNT];
                    for (int i = 0; i < Math.min(ca.size(), COLOR_COUNT); i++)
                        cols[i] = ca.get(i).getAsInt();
                    savedThemes.add(new SavedTheme(name, cols));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float scrollAreaH() {
        return height - HEADER_H - FIXED_BOTTOM_H;
    }

    private float scrollContentH() {
        return COLOR_COUNT * ROW_H + 2f;
    }

    private void clampScroll() {
        maxScroll = Math.max(0, scrollContentH() - scrollAreaH());
        if (maxScroll > 0) scroll = MathHelper.clamp(scroll, -maxScroll, 0);
        else scroll = 0;
    }

    private int allPresetsCount() {
        return PRESETS.length + savedThemes.size();
    }

    private void clampPresetScroll() {
        float areaW = width - 8f;
        float totalW = allPresetsCount() * PRESET_D + (allPresetsCount() - 1) * PRESET_GAP;
        presetMaxScroll = Math.max(0, totalW - areaW);
        if (presetMaxScroll > 0) presetScroll = MathHelper.clamp(presetScroll, -presetMaxScroll, 0);
        else presetScroll = 0;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        openAnim.run(open);
        if (openAnim.getValue() < 0.01f) return;

        float anim = openAnim.getValue();
        float cx = x - (width * (1 - anim));
        int alphaInt = (int) (255 * anim);

        RenderUtils.drawBlur(matrices, cx, y, width, height, 8f, 20f, ColorUtils.rgba(75, 75, 75, alphaInt));
        RenderUtils.drawRoundedRect(matrices, cx, y, width, height, 8f,
                ColorUtils.setAlphaColor(ColorUtils.getColorWindowBg(), (int) (30 * anim)));
        RenderUtils.drawRoundedRect(matrices, cx, y, width, HEADER_H,
                8f, 8f, 0f, 0f,
                ColorUtils.rgba(0, 0, 0, (int) (80 * anim)));

        Font titleFont = Fonts.getFont("sf_regular", 17);
        String title = "Theme Editor";
        if (titleFont != null) {
            float titleW = titleFont.getStringWidth(title);
            Component.drawText(matrices, titleFont, title, cx + width / 2f - titleW / 2f, y + 6f,
                    ColorUtils.rgba(255, 255, 255, alphaInt));
        }

        clampScroll();
        clampPresetScroll();
        scrollAnim.run(scroll);
        presetScrollAnim.run(presetScroll);

        float scrollZoneY = y + HEADER_H;
        float scrollZoneH = scrollAreaH();

        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(cx, scrollZoneY, width, scrollZoneH);

        float off = scrollAnim.getValue();
        float curY = scrollZoneY + 2f + off;
        for (int i = 0; i < COLOR_COUNT; i++) {
            renderRow(matrices, i, cx + 3.5f, curY, width - 7f, mouseX, mouseY, anim);
            curY += ROW_H;
        }

        ScissorUtils.pop();

        float fixedY = y + height - FIXED_BOTTOM_H;

        RenderUtils.drawRoundedRect(matrices, cx + 4, fixedY, width - 8, 0.5f, 0f,
                ColorUtils.rgba(255, 255, 255, (int) (25 * anim)));
        fixedY += 1f + 2f;
        float presetAreaX = cx + 4f;
        float presetAreaW = width - 8f;
        float presetOff = presetScrollAnim.getValue();
        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(presetAreaX, fixedY, presetAreaW, PRESET_ROW_H);
        float circleX = presetAreaX + presetOff;
        float circleY = fixedY + (PRESET_ROW_H - PRESET_D) / 2f;
        for (int[] preset : PRESETS) {
            boolean hov = HoveringUtils.isHovered(mouseX, mouseY, circleX, circleY, PRESET_D, PRESET_D);
            if (hov) CursorManager.requestHand();
            RenderUtils.drawRoundedRect(matrices, circleX, circleY, PRESET_D, PRESET_D, PRESET_D / 2f - 0.5f,
                    ColorUtils.setAlphaColor(preset[0], alphaInt));
            circleX += PRESET_D + PRESET_GAP;
        }
        for (SavedTheme st : savedThemes) {
            boolean hov = HoveringUtils.isHovered(mouseX, mouseY, circleX, circleY, PRESET_D, PRESET_D);
            if (hov) CursorManager.requestHand();
            RenderUtils.drawRoundedRect(matrices, circleX, circleY, PRESET_D, PRESET_D, PRESET_D / 2f - 0.5f,
                    ColorUtils.setAlphaColor(st.colors[1], alphaInt));
            circleX += PRESET_D + PRESET_GAP;
        }
        ScissorUtils.pop();
        fixedY += PRESET_ROW_H + 2f;

        RenderUtils.drawRoundedRect(matrices, cx + 4, fixedY, width - 8, 0.5f, 0f,
                ColorUtils.rgba(255, 255, 255, (int) (25 * anim)));
        fixedY += 1f + 2f;

        float fieldX = cx + 4f, fieldW = width - 8f;
        int accent = ColorUtils.setAlphaColor(currentColors[1], (int) (55 * anim));
        int accentHov = ColorUtils.setAlphaColor(currentColors[1], (int) (90 * anim));

        RenderUtils.drawRoundedRect(matrices, fieldX, fixedY, fieldW, FIELD_H, 3f, accent);
        Font fieldFont = Fonts.getFont("sf_regular", 14);
        String displayName = themeName.isEmpty() ? "Название" : themeName;
        int nameColor = themeName.isEmpty()
                ? ColorUtils.rgba(200, 200, 200, (int) (160 * anim))
                : ColorUtils.rgba(255, 255, 255, (int) (255 * anim));
        Component.drawText(matrices, fieldFont, displayName, fieldX + 4f, fixedY + 4f, nameColor);
        fixedY += FIELD_H + 2f;

        boolean btnHov = HoveringUtils.isHovered(mouseX, mouseY, fieldX, fixedY, fieldW, BTN_H);
        if (btnHov) CursorManager.requestHand();
        RenderUtils.drawRoundedRect(matrices, fieldX, fixedY, fieldW, BTN_H, 3f, btnHov ? accentHov : accent);
        String btnText = "Создать тему";
        if (fieldFont != null) {
            float btnTextW = fieldFont.getStringWidth(btnText);
            Component.drawText(matrices, fieldFont, btnText, fieldX + fieldW / 2f - btnTextW / 2f, fixedY + 4f,
                    ColorUtils.rgba(255, 255, 255, (int) (255 * anim)));
        }

        pickerAnim.run(editingSlot >= 0);
        if (pickerAnim.getValue() > 0.01f)
            renderPickerPopup(matrices, pickerPopupX, pickerPopupY, mouseX, mouseY,
                    pickerAnim.getValue() * anim);
    }

    private void renderRow(MatrixStack matrices, int slot, float rx, float ry, float rw,
                           int mouseX, int mouseY, float anim) {
        boolean hov = HoveringUtils.isHovered(mouseX, mouseY, rx, ry, rw, ROW_H);
        boolean active = editingSlot == slot;
        if (hov || active) CursorManager.requestHand();
        if (active)
            RenderUtils.drawRoundedRect(matrices, rx, ry, rw, ROW_H, 2f,
                    ColorUtils.rgba(60, 60, 80, (int) (60 * anim)));
        Font font = Fonts.getFont("sf_regular", 14);
        Component.drawText(matrices, font, COLOR_NAMES[slot], rx + 3f, ry + 4f,
                ColorUtils.rgba(210, 210, 210, (int) (255 * anim)));
        float cd = 8f;
        RenderUtils.drawRoundedRect(matrices, rx + rw - cd - 3f, ry + ROW_H / 2f - cd / 2f, cd, cd, cd / 2f - 1f,
                ColorUtils.setAlphaColor(currentColors[slot], (int) (255 * anim)));
    }

    private void renderPickerPopup(MatrixStack matrices, float px, float py, int mouseX, int mouseY, float alphaAnim) {
        int ai = (int) (255 * alphaAnim);
        float p = PICKER_PAD;
        RenderUtils.drawBlur(matrices, px - p, py - p, PICKER_W + p * 2, PICKER_H_TOT + p * 2, 5f, 20f,
                ColorUtils.rgba(75, 75, 75, (int) (255 * alphaAnim)));
        RenderUtils.drawRoundedRect(matrices, px - p, py - p, PICKER_W + p * 2, PICKER_H_TOT + p * 2, 5f,
                ColorUtils.rgba(14, 14, 16, (int) (130 * alphaAnim)));

        int cHue = ColorUtils.setAlphaColor(Color.HSBtoRGB(hsv[0], 1F, 1F), ai);
        int cW = ColorUtils.rgba(255, 255, 255, ai);
        int cCW = ColorUtils.rgba(255, 255, 255, 0);
        int cB = ColorUtils.rgba(0, 0, 0, ai);
        int cCB = ColorUtils.rgba(0, 0, 0, 0);

        RenderUtils.drawRoundedRect(matrices, px, py, SV_SIZE, SV_SIZE, 2f, cHue);
        RenderUtils.drawGradientRect(matrices, px, py, SV_SIZE, SV_SIZE, 2f, cW, cCW, cW, cCW);
        RenderUtils.drawGradientRect(matrices, px, py, SV_SIZE, SV_SIZE, 2f, cCB, cCB, cB, cB);

        float scx = px + hsv[1] * SV_SIZE, scy = py + (1 - hsv[2]) * SV_SIZE;
        RenderUtils.drawRoundedRect(matrices, scx - 3f, scy - 3f, 6f, 6f, 3f,
                ColorUtils.rgba(0, 0, 0, (int) (180 * alphaAnim)));
        RenderUtils.drawRoundedRect(matrices, scx - 2f, scy - 2f, 4f, 4f, 2f,
                ColorUtils.rgba(255, 255, 255, ai));

        float hueX = px + SV_SIZE + 4f;
        for (float i = 0; i <= SV_SIZE; i += 0.5f) {
            RenderUtils.drawRoundedRect(matrices, hueX, py + i, HUE_W, 1f, 0f,
                    ColorUtils.setAlphaColor(Color.HSBtoRGB(i / SV_SIZE, 1F, 1F), ai));
        }
        float hcy = py + hsv[0] * SV_SIZE;
        RenderUtils.drawRoundedRect(matrices, hueX - 1.5f, hcy - 2.5f, HUE_W + 3f, 5f, 2f,
                ColorUtils.rgba(0, 0, 0, (int) (180 * alphaAnim)));
        RenderUtils.drawRoundedRect(matrices, hueX - 0.5f, hcy - 1.5f, HUE_W + 1f, 3f, 1f,
                ColorUtils.rgba(255, 255, 255, ai));

        if (draggingSV) {
            hsv[1] = Math.max(0, Math.min(1, ((float) mouseX - px) / SV_SIZE));
            hsv[2] = 1F - Math.max(0, Math.min(1, ((float) mouseY - py) / SV_SIZE));
            applyHSV();
        } else if (draggingH) {
            hsv[0] = Math.max(0, Math.min(1, ((float) mouseY - py) / SV_SIZE));
            applyHSV();
        }
    }

    private void applyHSV() {
        if (editingSlot < 0) return;
        currentColors[editingSlot] = Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]) | 0xFF000000;
        applyColors();
    }

    private void loadHSV(int slot) {
        Color c = new Color(currentColors[slot], true);
        Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsv);
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (!open || openAnim.getValue() < 0.9f) return;

        float anim = openAnim.getValue();
        float cx = x - (width * (1 - anim));

        if (editingSlot >= 0 && pickerAnim.getValue() > 0.5f) {
            float px = pickerPopupX, py = pickerPopupY;
            if (HoveringUtils.isHovered(mouseX, mouseY, px, py, SV_SIZE, SV_SIZE) && button == 0) {
                draggingSV = true;
                return;
            }
            if (HoveringUtils.isHovered(mouseX, mouseY, px + SV_SIZE + 4, py, HUE_W + 4, SV_SIZE) && button == 0) {
                draggingH = true;
                return;
            }
            if (!HoveringUtils.isHovered(mouseX, mouseY, px - PICKER_PAD, py - PICKER_PAD,
                    PICKER_W + PICKER_PAD * 2, PICKER_H_TOT + PICKER_PAD * 2)) {
                editingSlot = -1;
            }
        }

        float scrollZoneY = y + HEADER_H;
        float scrollZoneH = scrollAreaH();
        if (HoveringUtils.isHovered(mouseX, mouseY, cx, scrollZoneY, width, scrollZoneH)) {
            float off = scrollAnim.getValue();
            float curY = scrollZoneY + 2f + off;
            for (int i = 0; i < COLOR_COUNT; i++) {
                float rx = cx + 3.5f, rw = width - 7f;
                if (HoveringUtils.isHovered(mouseX, mouseY, rx, curY, rw, ROW_H) && button == 0) {
                    editingSlot = (editingSlot == i) ? -1 : i;
                    if (editingSlot >= 0) {
                        loadHSV(i);
                        pickerPopupX = cx + width + 4f;
                        pickerPopupY = curY + ROW_H / 2f - PICKER_H_TOT / 2f;
                    }
                    return;
                }
                curY += ROW_H;
            }
        }

        float fixedY = y + height - FIXED_BOTTOM_H + 1f + 2f;

        float presetAreaX = cx + 4f;
        float presetAreaW = width - 8f;
        float presetOff = presetScrollAnim.getValue();
        float circleX = presetAreaX + presetOff;
        float circleY = fixedY + (PRESET_ROW_H - PRESET_D) / 2f;
        for (int[] preset : PRESETS) {
            if (HoveringUtils.isHovered(mouseX, mouseY, circleX, circleY, PRESET_D, PRESET_D)
                    && circleX >= presetAreaX && circleX + PRESET_D <= presetAreaX + presetAreaW
                    && button == 0) {
                currentColors = buildDefaultColors(preset[0], preset[1]);
                applyColors();
                save();
                return;
            }
            circleX += PRESET_D + PRESET_GAP;
        }
        for (SavedTheme st : savedThemes) {
            if (HoveringUtils.isHovered(mouseX, mouseY, circleX, circleY, PRESET_D, PRESET_D)
                    && circleX >= presetAreaX && circleX + PRESET_D <= presetAreaX + presetAreaW
                    && button == 0) {
                currentColors = st.colors.clone();
                applyColors();
                save();
                return;
            }
            circleX += PRESET_D + PRESET_GAP;
        }
        fixedY += PRESET_ROW_H + 2f + 1f + 2f;

        float fieldX = cx + 4f, fieldW = width - 8f;
        nameFieldFocused = HoveringUtils.isHovered(mouseX, mouseY, fieldX, fixedY, fieldW, FIELD_H) && button == 0;
        fixedY += FIELD_H + 2f;

        if (HoveringUtils.isHovered(mouseX, mouseY, fieldX, fixedY, fieldW, BTN_H) && button == 0) {
            String name = themeName.trim().isEmpty() ? "Theme " + (savedThemes.size() + 1) : themeName.trim();
            savedThemes.add(new SavedTheme(name, currentColors.clone()));
            save();
            themeName = "";
            nameFieldFocused = false;
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingSV || draggingH) save();
        draggingSV = false;
        draggingH = false;
    }

    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!open) return;
        float anim = openAnim.getValue();
        float cx = x - (width * (1 - anim));

        float presetFixedY = y + height - FIXED_BOTTOM_H + 1f + 2f;
        if (HoveringUtils.isHovered(mouseX, mouseY, cx + 4f, presetFixedY, width - 8f, PRESET_ROW_H)) {
            presetScroll += (float) (verticalAmount * 12f);
            clampPresetScroll();
            return;
        }

        if (HoveringUtils.isHovered(mouseX, mouseY, cx, y + HEADER_H, width, scrollAreaH())) {
            scroll += (float) (verticalAmount * 20f);
            clampScroll();
        }
    }

    public void charTyped(char chr) {
        if (!nameFieldFocused) return;
        if (chr >= 32 && themeName.length() < 24) themeName += chr;
    }

    public void keyPressed(int keyCode) {
        if (!nameFieldFocused) return;
        if (keyCode == 259 && !themeName.isEmpty())
            themeName = themeName.substring(0, themeName.length() - 1);
        if (keyCode == 256) nameFieldFocused = false;
    }
}
