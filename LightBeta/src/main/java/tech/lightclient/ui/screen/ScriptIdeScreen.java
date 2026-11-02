package tech.lightclient.ui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.render.CursorHelper;
import tech.lightclient.client.utils.render.animations.Easings;
import tech.lightclient.client.utils.render.animations.impl.SimpleLinearAnimation;
import tech.lightclient.client.utils.render.color.basic.ColorUtils;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.scripting.runtime.DeepSeekClient;
import tech.lightclient.scripting.runtime.ScriptConsole;
import tech.lightclient.scripting.runtime.ScriptInstance;
import tech.lightclient.scripting.runtime.ScriptManager;
import tech.lightclient.ui.gui.elements.TextInputField;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Script IDE sized like ClickGUI: centered panel, readable controls.
 */
public class ScriptIdeScreen extends Screen {

    private static final float PANEL_W = 510f;
    private static final float PANEL_H = 285f;
    private static final float PANEL_ROUNDING = 8f;
    private static final float PADDING = 8f;
    private static final float SIDEBAR_W = 96f;
    private static final float HEADER_H = 28f;
    private static final float PROMPT_H = 18f;
    private static final float BTN_H = 18f;
    private static final float LINE_H = 12f;
    private static final float GUTTER_W = 22f;
    private static final float NAME_H = 18f;
    private static final float NAME_W = 140f;
    private static final float TITLE_SIZE = 11f;
    private static final float BTN_TEXT_SIZE = 8.2f;
    private static final float SIDEBAR_TITLE_SIZE = 8.5f;
    private static final float FILE_TEXT_SIZE = 8.2f;
    private static final float CODE_SIZE = 9.2f;
    private static final float GUTTER_TEXT_SIZE = 8f;
    private static final float STATUS_SIZE = 8f;
    private static final float ROW_H = 16f;

    private final TextInputField nameInput = new TextInputField()
            .setFontSize(9f)
            .setPadding(6f, 0f)
            .setPlaceholder("имя файла")
            .setMaxLength(48);

    private final TextInputField promptInput = new TextInputField()
            .setFontSize(9f)
            .setPadding(6f, 1.5f)
            .setPlaceholder("Опишите скрипт и нажмите Сгенерировать")
            .setMultiline(false)
            .setMaxLength(4000);

    private final List<StringBuilder> lines = new ArrayList<>();
    private int cursorLine = 0;
    private int cursorCol = 0;
    private float editorScroll = 0f;
    private boolean editorFocused = true;

    private String status = "";
    private int statusColor = 0xFFA1A1AA;
    private volatile boolean generating = false;
    private String loadedId;

    private float panelX, panelY, panelW, panelH;
    private float scale = 1f;
    private float saveX, saveW, runX, runW, newX, newW, genX, genW, closeX, closeW, headerBtnY, btnH;
    private float editorX, editorY, editorW, editorH;
    private float promptX, promptY, promptW, promptH;
    private float listX, listY, listW, listH;
    private float nameX, nameY, nameW, nameH;
    private float lineH, gutterW, codeSize;
    private final List<float[]> fileRects = new ArrayList<>();
    private final List<String> fileIds = new ArrayList<>();
    private double mouseXRaw;
    private double mouseYRaw;
    private final SimpleLinearAnimation openAnimation = new SimpleLinearAnimation(240);
    private boolean closing = false;

    public ScriptIdeScreen(ScriptInstance open) {
        super(Text.literal("Script IDE"));
        openAnimation.setEasing(Easings.EASE_OUT_CUBIC);
        openAnimation.show();
        if (open != null) {
            loadInto(open);
        } else {
            lines.add(new StringBuilder("// Новый скрипт Light Client"));
            lines.add(new StringBuilder("name(\"MyScript\")"));
            lines.add(new StringBuilder("description(\"\")"));
            lines.add(new StringBuilder(""));
            lines.add(new StringBuilder("on<ClientTickEvent> {"));
            lines.add(new StringBuilder("    if (!inGame) return@on"));
            lines.add(new StringBuilder("}"));
            nameInput.setText("my-script");
        }
    }

    private void loadInto(ScriptInstance script) {
        loadedId = script.id;
        nameInput.setText(script.id);
        lines.clear();
        try {
            String src = Files.readString(script.file.toPath(), StandardCharsets.UTF_8);
            for (String line : src.split("\n", -1)) lines.add(new StringBuilder(line.replace("\r", "")));
        } catch (IOException e) {
            lines.add(new StringBuilder("// не удалось прочитать файл: " + e.getMessage()));
        }
        if (lines.isEmpty()) lines.add(new StringBuilder());
        cursorLine = 0;
        cursorCol = 0;
    }

    private float s(float logical) {
        return logical * scale;
    }

    @EventHandler
    public static void onGuiRender(EventRender.Screen.Gui event) {
        if (MinecraftClient.getInstance().currentScreen instanceof ScriptIdeScreen ide) {
            ide.draw(event.getRenderer());
        }
    }

    private void draw(Renderer2D r) {
        CursorHelper.ensureVisible();
        CursorHelper.setArrow();

        MinecraftClient mc = MinecraftClient.getInstance();
        float screenW = mc.getWindow().getFramebufferWidth();
        float screenH = mc.getWindow().getFramebufferHeight();
        scale = Math.max(1f, (float) mc.getWindow().getScaleFactor());
        int mouseX = (int) Math.round(mouseXRaw);
        int mouseY = (int) Math.round(mouseYRaw);

        int panelBg = ColorUtils.rgba(28, 28, 31, 240);
        int cardBg = ColorUtils.rgba(9, 9, 12, 190);
        int border = ColorUtils.rgba(42, 42, 48, 220);
        int textBright = ColorUtils.rgba(228, 228, 234, 255);
        int textDim = ColorUtils.rgba(150, 150, 162, 255);
        int textFaint = ColorUtils.rgba(110, 110, 122, 255);
        int accent = ColorUtils.rgba(
                (ClientColors.ICON.getRGB() >> 16) & 0xFF,
                (ClientColors.ICON.getRGB() >> 8) & 0xFF,
                ClientColors.ICON.getRGB() & 0xFF, 255);

        if (closing) {
            openAnimation.hide();
            if (openAnimation.isFinished() || openAnimation.getProgress() <= 0.001f) {
                mc.setScreen(null);
                return;
            }
        }

        float openProgress = openAnimation.getProgress();
        int dimA = Math.round(120f * openProgress);
        r.rect(0, 0, screenW, screenH, ColorUtils.rgba(0, 0, 0, dimA));
        if (openProgress <= 0.001f) {
            if (closing) {
                mc.setScreen(null);
            }
            return;
        }

        panelW = s(PANEL_W);
        panelH = s(PANEL_H);
        panelX = (screenW - panelW) * 0.5f;
        panelY = (screenH - panelH) * 0.5f;
        float openScale = 0.92f + 0.08f * openProgress;
        float centerX = screenW * 0.5f;
        float centerY = screenH * 0.5f;
        r.pushScale(openScale, openScale, centerX, centerY);
        r.pushAlpha(openProgress);
        try {
        float pad = s(PADDING);
        float rounding = s(PANEL_ROUNDING);
        btnH = s(BTN_H);
        lineH = s(LINE_H);
        gutterW = s(GUTTER_W);
        codeSize = s(CODE_SIZE);
        promptH = s(PROMPT_H);
        nameH = s(NAME_H);
        nameW = s(NAME_W);

        r.rect(panelX, panelY, panelW, panelH, rounding, panelBg);
        r.rectOutline(panelX, panelY, panelW, panelH, rounding, border, 1f);

        float headerH = s(HEADER_H);
        float titleSize = s(TITLE_SIZE);
        if (FontRegistry.SF_SEMIBOLD != null) {
            r.text(FontRegistry.SF_SEMIBOLD, panelX + pad,
                    panelY + headerH * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.SF_SEMIBOLD, 'H', titleSize),
                    titleSize, "Script IDE", textBright);
        }

        headerBtnY = panelY + (headerH - btnH) * 0.5f;
        float gap = s(4f);
        float right = panelX + panelW - pad;
        closeW = s(52f);
        closeX = right - closeW;
        button(r, closeX, headerBtnY, closeW, "Закрыть", false, mouseX, mouseY, cardBg, border, accent, textBright);
        genW = s(78f);
        genX = closeX - gap - genW;
        button(r, genX, headerBtnY, genW, generating ? "..." : "Сгенерир.", generating, mouseX, mouseY, cardBg, border, accent, textBright);
        runW = s(72f);
        runX = genX - gap - runW;
        button(r, runX, headerBtnY, runW, "Запуск", false, mouseX, mouseY, cardBg, border, accent, textBright);
        saveW = s(58f);
        saveX = runX - gap - saveW;
        button(r, saveX, headerBtnY, saveW, "Сохран.", false, mouseX, mouseY, cardBg, border, accent, textBright);
        newW = s(46f);
        newX = saveX - gap - newW;
        button(r, newX, headerBtnY, newW, "Новый", false, mouseX, mouseY, cardBg, border, accent, textBright);

        listX = panelX + pad;
        listY = panelY + headerH;
        listW = s(SIDEBAR_W);
        listH = panelH - headerH - pad;
        r.rect(listX, listY, listW, listH, s(6f), cardBg);
        r.rectOutline(listX, listY, listW, listH, s(6f), border, 1f);
        float sidebarTitle = s(SIDEBAR_TITLE_SIZE);
        if (FontRegistry.SF_SEMIBOLD != null) {
            r.text(FontRegistry.SF_SEMIBOLD, listX + s(8f), listY + s(14f), sidebarTitle, "Скрипты", textDim);
        }

        fileRects.clear();
        fileIds.clear();
        float rowH = s(ROW_H);
        float fileText = s(FILE_TEXT_SIZE);
        float fy = listY + s(22f);
        float listBottom = listY + listH - s(4f);
        for (ScriptInstance script : ScriptManager.get().all()) {
            if (fy + rowH > listBottom) break;
            boolean isOpen = script.id.equals(loadedId);
            boolean hovered = mouseX >= listX + s(4f) && mouseX <= listX + listW - s(4f)
                    && mouseY >= fy && mouseY <= fy + rowH;
            if (hovered) CursorHelper.setHand();
            if (isOpen || hovered) {
                r.rect(listX + s(4f), fy, listW - s(8f), rowH, s(4f),
                        ColorUtils.rgba(255, 255, 255, isOpen ? 26 : 14));
            }
            int col = script.status == ScriptInstance.Status.ERROR
                    ? ColorUtils.rgba(240, 110, 110, 255)
                    : (isOpen ? textBright : textDim);
            if (FontRegistry.SF_MEDIUM != null) {
                r.text(FontRegistry.SF_MEDIUM, listX + s(8f),
                        fy + rowH * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.SF_MEDIUM, 'H', fileText),
                        fileText, shorten(script.id, 14), col);
            }
            fileRects.add(new float[]{listX + s(4f), fy, listW - s(8f), rowH});
            fileIds.add(script.id);
            fy += rowH + s(2f);
        }

        float bodyX = listX + listW + pad;
        float bodyW = panelX + panelW - pad - bodyX;
        nameX = bodyX;
        nameY = listY;
        r.rect(nameX, nameY, nameW, nameH, s(5f), cardBg);
        r.rectOutline(nameX, nameY, nameW, nameH, s(5f), nameInput.isFocused() ? accent : border, 1f);
        nameInput.setFontSize(s(9f)).setPadding(s(6f), 0f);
        nameInput.render(r, nameX, nameY, nameW, nameH, 1f);
        if (FontRegistry.SF_MEDIUM != null) {
            float extSize = s(STATUS_SIZE);
            r.text(FontRegistry.SF_MEDIUM, nameX + nameW + s(6f),
                    nameY + nameH * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.SF_MEDIUM, 'H', extSize),
                    extSize, ".kts", textFaint);
            if (!status.isEmpty()) {
                float statusX = nameX + nameW + s(28f);
                float maxStatusW = bodyX + bodyW - statusX;
                r.text(FontRegistry.SF_MEDIUM, statusX,
                        nameY + nameH * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.SF_MEDIUM, 'H', extSize),
                        extSize, shorten(status, Math.max(12, (int) (maxStatusW / (extSize * 0.55f)))), statusColor);
            }
        }

        editorX = bodyX;
        editorY = nameY + nameH + s(6f);
        editorW = bodyW;
        // Compact prompt plaque: bottom stays on the previous bottom edge, height hugs text.
        float promptBottomEdge = panelY + panelH - pad;
        promptH = s(PROMPT_H);
        promptX = bodyX;
        promptW = bodyW;
        // Anchor bottom to the old bottom edge (plaque "sits" on that level).
        promptY = promptBottomEdge - promptH;
        editorH = (promptY - s(6f)) - editorY;
        if (editorH < s(40f)) editorH = s(40f);
        r.rect(editorX, editorY, editorW, editorH, s(6f), ColorUtils.rgba(7, 7, 10, 230));
        r.rectOutline(editorX, editorY, editorW, editorH, s(6f), editorFocused ? accent : border, 1f);
        drawEditor(r, textBright, textFaint, accent);

        r.rect(promptX, promptY, promptW, promptH, s(6f), cardBg);
        r.rectOutline(promptX, promptY, promptW, promptH, s(6f), promptInput.isFocused() ? accent : border, 1f);
        // Tight vertical padding: edges sit close to the text.
        promptInput.setFontSize(s(9f)).setPadding(s(6f), s(1.5f));
        promptInput.render(r, promptX, promptY, promptW, promptH, 1f);
        } finally {
            r.popAlpha();
            r.popScale();
        }
    }

    private void drawEditor(Renderer2D r, int textBright, int textFaint, int accent) {
        float padY = s(5f);
        float innerTop = editorY + padY;
        float innerH = editorH - padY * 2f;
        int visible = Math.max(1, (int) (innerH / lineH));

        if (cursorLine < editorScroll) editorScroll = cursorLine;
        if (cursorLine >= editorScroll + visible) editorScroll = cursorLine - visible + 1;
        editorScroll = Math.max(0, Math.min(editorScroll, Math.max(0, lines.size() - visible)));

        r.pushClipRect((int) editorX, (int) innerTop, (int) editorW, (int) innerH);

        int start = (int) editorScroll;
        int end = Math.min(lines.size(), start + visible);
        float gutterText = s(GUTTER_TEXT_SIZE);
        float ly = innerTop + lineH * 0.75f;

        for (int i = start; i < end; i++) {
            String line = lines.get(i).toString();
            String number = String.valueOf(i + 1);
            if (FontRegistry.SF_MEDIUM != null) {
                r.text(FontRegistry.SF_MEDIUM,
                        editorX + gutterW - s(5f) - FontRegistry.SF_MEDIUM.getWidth(number, gutterText),
                        ly, gutterText, number, i == cursorLine ? accent : textFaint);
            }
            drawHighlighted(r, editorX + gutterW, ly, line, textBright);
            if (i == cursorLine && editorFocused && FontRegistry.SF_MEDIUM != null
                    && (System.currentTimeMillis() / 500) % 2 == 0) {
                int col = Math.min(cursorCol, line.length());
                float cxPos = editorX + gutterW + FontRegistry.SF_MEDIUM.getWidth(line.substring(0, col), codeSize);
                r.rect(cxPos, ly - lineH * 0.7f, Math.max(1f, s(1f)), lineH * 0.9f, accent);
            }
            ly += lineH;
        }
        r.popClipRect();
    }

    private void drawHighlighted(Renderer2D r, float x, float y, String line, int defaultCol) {
        if (FontRegistry.SF_MEDIUM == null) return;
        final int KEYWORD = ColorUtils.rgba(197, 134, 192, 255);
        final int STRING = ColorUtils.rgba(206, 145, 120, 255);
        final int NUMBER = ColorUtils.rgba(181, 206, 168, 255);
        final int COMMENT = ColorUtils.rgba(106, 153, 85, 255);
        final int API = ColorUtils.rgba(86, 156, 214, 255);
        int commentAt = line.indexOf("//");
        float cx = x;
        float size = codeSize;
        String code = commentAt >= 0 ? line.substring(0, commentAt) : line;
        int i = 0;
        while (i < code.length()) {
            char c = code.charAt(i);
            if (c == '"') {
                int endQuote = i + 1;
                while (endQuote < code.length() && (code.charAt(endQuote) != '"' || code.charAt(endQuote - 1) == '\\')) endQuote++;
                String token = code.substring(i, Math.min(code.length(), endQuote + 1));
                r.text(FontRegistry.SF_MEDIUM, cx, y, size, token, STRING);
                cx += FontRegistry.SF_MEDIUM.getWidth(token, size);
                i = endQuote + 1;
                continue;
            }
            if (Character.isLetterOrDigit(c) || c == '_') {
                int wordEnd = i;
                while (wordEnd < code.length()
                        && (Character.isLetterOrDigit(code.charAt(wordEnd)) || code.charAt(wordEnd) == '_')) wordEnd++;
                String token = code.substring(i, wordEnd);
                int col = defaultCol;
                if (KEYWORDS.contains(token)) col = KEYWORD;
                else if (API_WORDS.contains(token)) col = API;
                else if (Character.isDigit(token.charAt(0))) col = NUMBER;
                r.text(FontRegistry.SF_MEDIUM, cx, y, size, token, col);
                cx += FontRegistry.SF_MEDIUM.getWidth(token, size);
                i = wordEnd;
                continue;
            }
            String token = String.valueOf(c);
            r.text(FontRegistry.SF_MEDIUM, cx, y, size, token, defaultCol);
            cx += FontRegistry.SF_MEDIUM.getWidth(token, size);
            i++;
        }
        if (commentAt >= 0) {
            String token = line.substring(commentAt);
            r.text(FontRegistry.SF_MEDIUM, cx, y, size, token, COMMENT);
        }
    }

    private static final java.util.Set<String> KEYWORDS = java.util.Set.of(
            "val", "var", "fun", "if", "else", "for", "while", "return", "when", "is", "in", "as",
            "true", "false", "null", "class", "object", "this", "it", "by", "do", "break", "continue",
            "try", "catch", "finally", "throw", "and", "or", "not");

    private static final java.util.Set<String> API_WORDS = java.util.Set.of(
            "name", "description", "key", "requireApi", "on", "onEnable", "onDisable", "onUnload",
            "checkBox", "slider", "rangeSlider", "input", "selectable", "combo", "colorPicker",
            "hotkey", "button", "chat", "player", "world", "client", "log", "notify", "timer",
            "afterTicks", "everyTicks", "nextTick", "onClientThread", "storage", "config", "command",
            "inGame", "whenInGame", "raycast", "control", "interaction", "inventory", "rotations",
            "text", "game", "filters", "enabled", "Colors", "Key", "Postfixes", "NotifyKind", "Priority");

    private void button(Renderer2D r, float bx, float by, float bw, String label, boolean active,
                        int mouseX, int mouseY, int cardBg, int border, int accent, int textBright) {
        boolean hovered = mouseX >= bx && mouseX <= bx + bw && mouseY >= by && mouseY <= by + btnH;
        if (hovered) CursorHelper.setHand();
        int bg = active ? ColorUtils.rgba(
                (ClientColors.ICON.getRGB() >> 16) & 0xFF,
                (ClientColors.ICON.getRGB() >> 8) & 0xFF,
                ClientColors.ICON.getRGB() & 0xFF, 170)
                : (hovered ? ColorUtils.rgba(255, 255, 255, 24) : cardBg);
        float round = s(5f);
        r.rect(bx, by, bw, btnH, round, bg);
        r.rectOutline(bx, by, bw, btnH, round, active ? accent : border, 1f);
        if (FontRegistry.SF_SEMIBOLD == null) return;
        float size = s(BTN_TEXT_SIZE);
        r.text(FontRegistry.SF_SEMIBOLD, bx + (bw - FontRegistry.SF_SEMIBOLD.getWidth(label, size)) * 0.5f,
                by + btnH * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.SF_SEMIBOLD, 'H', size),
                size, label, textBright);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        double sf = MinecraftClient.getInstance().getWindow().getScaleFactor();
        mouseXRaw = mouseX * sf;
        mouseYRaw = mouseY * sf;
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        startClosing();
    }

    private void startClosing() {
        if (closing) return;
        closing = true;
        openAnimation.hide();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean mouseClicked(Click click, boolean isRepeated) {
        double sf = MinecraftClient.getInstance().getWindow().getScaleFactor();
        double mx = click.x() * sf;
        double my = click.y() * sf;
        int button = click.button();

        if (panelW > 0 && (mx < panelX || mx > panelX + panelW || my < panelY || my > panelY + panelH)) {
            editorFocused = false;
            if (nameInput.isFocused()) nameInput.blur();
            if (promptInput.isFocused()) promptInput.blur();
            return true;
        }

        if (my >= headerBtnY && my <= headerBtnY + btnH) {
            if (within(mx, closeX, closeW)) { close(); return true; }
            if (within(mx, genX, genW)) { generate(); return true; }
            if (within(mx, runX, runW)) { save(true); return true; }
            if (within(mx, saveX, saveW)) { save(false); return true; }
            if (within(mx, newX, newW)) { newScript(); return true; }
        }

        for (int i = 0; i < fileRects.size(); i++) {
            float[] rect = fileRects.get(i);
            if (mx >= rect[0] && mx <= rect[0] + rect[2] && my >= rect[1] && my <= rect[1] + rect[3]) {
                ScriptInstance script = ScriptManager.get().byId(fileIds.get(i));
                if (script != null) loadInto(script);
                return true;
            }
        }

        if (mx >= nameX && mx <= nameX + nameW && my >= nameY && my <= nameY + nameH) {
            nameInput.mouseClicked(mx, my, button, nameX, nameY, nameW, nameH);
            editorFocused = false;
            return true;
        }

        if (mx >= promptX && mx <= promptX + promptW && my >= promptY && my <= promptY + promptH) {
            promptInput.mouseClicked(mx, my, button, promptX, promptY, promptW, promptH);
            editorFocused = false;
            return true;
        }

        if (mx >= editorX && mx <= editorX + editorW && my >= editorY && my <= editorY + editorH) {
            editorFocused = true;
            if (nameInput.isFocused()) nameInput.blur();
            if (promptInput.isFocused()) promptInput.blur();
            float padY = s(5f);
            float useLineH = lineH > 0 ? lineH : s(LINE_H);
            float useGutter = gutterW > 0 ? gutterW : s(GUTTER_W);
            float useCode = codeSize > 0 ? codeSize : s(CODE_SIZE);
            int line = (int) editorScroll + (int) ((my - (editorY + padY)) / useLineH);
            cursorLine = Math.max(0, Math.min(lines.size() - 1, line));
            String text = lines.get(cursorLine).toString();
            float target = (float) (mx - editorX - useGutter);
            int col = text.length();
            if (FontRegistry.SF_MEDIUM != null) {
                for (int i = 0; i <= text.length(); i++) {
                    if (FontRegistry.SF_MEDIUM.getWidth(text.substring(0, i), useCode) >= target) {
                        col = i;
                        break;
                    }
                }
            }
            cursorCol = col;
            return true;
        }

        editorFocused = false;
        return super.mouseClicked(click, isRepeated);
    }

    private static boolean within(double v, float start, float length) {
        return v >= start && v <= start + length;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        double sf = MinecraftClient.getInstance().getWindow().getScaleFactor();
        double mx = mouseX * sf;
        double my = mouseY * sf;
        if (mx >= editorX && mx <= editorX + editorW && my >= editorY && my <= editorY + editorH) {
            editorScroll = Math.max(0, editorScroll - (float) vertical * 3f);
            return true;
        }
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        int key = keyInput.key();
        int mods = keyInput.modifiers();
        if (nameInput.isFocused()) {
            if (key == GLFW.GLFW_KEY_ESCAPE) { nameInput.blur(); return true; }
            if (nameInput.keyPressed(key, mods)) return true;
        }
        if (promptInput.isFocused()) {
            if (key == GLFW.GLFW_KEY_ESCAPE) { promptInput.blur(); return true; }
            if (key == GLFW.GLFW_KEY_ENTER && (mods & GLFW.GLFW_MOD_CONTROL) != 0) {
                generate();
                return true;
            }
            if (promptInput.keyPressed(key, mods)) return true;
        }
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        if (editorFocused) {
            boolean ctrl = (mods & GLFW.GLFW_MOD_CONTROL) != 0;
            if (ctrl && key == GLFW.GLFW_KEY_S) { save(false); return true; }
            if (ctrl && key == GLFW.GLFW_KEY_R) { save(true); return true; }
            return editorKey(key, mods);
        }
        return super.keyPressed(keyInput);
    }

    private boolean editorKey(int key, int mods) {
        StringBuilder line = lines.get(cursorLine);
        switch (key) {
            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (cursorCol > 0) {
                    line.deleteCharAt(cursorCol - 1);
                    cursorCol--;
                } else if (cursorLine > 0) {
                    StringBuilder previous = lines.get(cursorLine - 1);
                    cursorCol = previous.length();
                    previous.append(line);
                    lines.remove(cursorLine);
                    cursorLine--;
                }
                return true;
            }
            case GLFW.GLFW_KEY_DELETE -> {
                if (cursorCol < line.length()) line.deleteCharAt(cursorCol);
                else if (cursorLine < lines.size() - 1) {
                    line.append(lines.get(cursorLine + 1));
                    lines.remove(cursorLine + 1);
                }
                return true;
            }
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                String tail = line.substring(Math.min(cursorCol, line.length()));
                line.setLength(Math.min(cursorCol, line.length()));
                String indent = "";
                for (int i = 0; i < line.length() && (line.charAt(i) == ' ' || line.charAt(i) == '\t'); i++) {
                    indent += line.charAt(i);
                }
                if (line.toString().trim().endsWith("{")) indent += "    ";
                lines.add(cursorLine + 1, new StringBuilder(indent + tail));
                cursorLine++;
                cursorCol = indent.length();
                return true;
            }
            case GLFW.GLFW_KEY_TAB -> {
                line.insert(Math.min(cursorCol, line.length()), "    ");
                cursorCol += 4;
                return true;
            }
            case GLFW.GLFW_KEY_LEFT -> {
                if (cursorCol > 0) cursorCol--;
                else if (cursorLine > 0) {
                    cursorLine--;
                    cursorCol = lines.get(cursorLine).length();
                }
                return true;
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                if (cursorCol < line.length()) cursorCol++;
                else if (cursorLine < lines.size() - 1) {
                    cursorLine++;
                    cursorCol = 0;
                }
                return true;
            }
            case GLFW.GLFW_KEY_UP -> {
                if (cursorLine > 0) {
                    cursorLine--;
                    cursorCol = Math.min(cursorCol, lines.get(cursorLine).length());
                }
                return true;
            }
            case GLFW.GLFW_KEY_DOWN -> {
                if (cursorLine < lines.size() - 1) {
                    cursorLine++;
                    cursorCol = Math.min(cursorCol, lines.get(cursorLine).length());
                }
                return true;
            }
            case GLFW.GLFW_KEY_HOME -> { cursorCol = 0; return true; }
            case GLFW.GLFW_KEY_END -> { cursorCol = line.length(); return true; }
            case GLFW.GLFW_KEY_V -> {
                if ((mods & GLFW.GLFW_MOD_CONTROL) != 0) {
                    String clip = MinecraftClient.getInstance().keyboard.getClipboard();
                    if (clip != null && !clip.isEmpty()) insertText(clip);
                    return true;
                }
            }
            default -> {
            }
        }
        return false;
    }

    private void insertText(String value) {
        String[] parts = value.replace("\r", "").split("\n", -1);
        StringBuilder line = lines.get(cursorLine);
        String tail = line.substring(Math.min(cursorCol, line.length()));
        line.setLength(Math.min(cursorCol, line.length()));
        line.append(parts[0]);
        if (parts.length == 1) {
            cursorCol = line.length();
            line.append(tail);
            return;
        }
        for (int i = 1; i < parts.length; i++) {
            lines.add(cursorLine + i, new StringBuilder(parts[i]));
        }
        cursorLine += parts.length - 1;
        StringBuilder last = lines.get(cursorLine);
        cursorCol = last.length();
        last.append(tail);
    }

    @Override
    public boolean charTyped(CharInput charInput) {
        char c = (char) charInput.codepoint();
        if (nameInput.isFocused()) return nameInput.charTyped(c);
        if (promptInput.isFocused()) return promptInput.charTyped(c);
        if (editorFocused && c >= 32 && c != 127) {
            StringBuilder line = lines.get(cursorLine);
            line.insert(Math.min(cursorCol, line.length()), c);
            cursorCol++;
            return true;
        }
        return super.charTyped(charInput);
    }

    @Override
    public boolean mouseReleased(Click click) {
        nameInput.mouseReleased(click.button());
        promptInput.mouseReleased(click.button());
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        double sf = MinecraftClient.getInstance().getWindow().getScaleFactor();
        nameInput.tickDrag(click.x() * sf, click.y() * sf);
        promptInput.tickDrag(click.x() * sf, click.y() * sf);
        return super.mouseDragged(click, deltaX, deltaY);
    }

    private String sourceText() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) out.append('\n');
            out.append(lines.get(i));
        }
        return out.toString();
    }

    private void newScript() {
        loadedId = null;
        lines.clear();
        lines.add(new StringBuilder("name(\"NewScript\")"));
        lines.add(new StringBuilder(""));
        lines.add(new StringBuilder("chat.print(\"привет\")"));
        cursorLine = 0;
        cursorCol = 0;
        nameInput.setText("new-script");
        setStatus("новый скрипт", 0xFFA1A1AA);
    }

    private void save(boolean thenEnable) {
        String name = nameInput.getText().trim();
        if (name.isEmpty()) {
            setStatus("укажите имя файла", 0xFFF07070);
            return;
        }
        try {
            var file = ScriptManager.get().deploy(name, sourceText());
            loadedId = file.getName().replaceAll("\\.kts$", "");
            setStatus("сохранено: " + file.getName(), 0xFF7FD88F);
            if (thenEnable) {
                final String id = loadedId;
                new java.util.Timer(true).schedule(new java.util.TimerTask() {
                    int tries = 0;
                    @Override
                    public void run() {
                        ScriptInstance script = ScriptManager.get().byId(id);
                        if (script == null || ++tries > 60) { cancel(); return; }
                        if (script.status == ScriptInstance.Status.READY) {
                            MinecraftClient.getInstance().execute(() -> script.setEnabled(true));
                            setStatus("запущен: " + id, 0xFF7FD88F);
                            cancel();
                        } else if (script.status == ScriptInstance.Status.ERROR) {
                            setStatus("ошибка: " + shorten(script.error, 80), 0xFFF07070);
                            cancel();
                        }
                    }
                }, 200, 200);
            }
        } catch (IOException e) {
            setStatus("не удалось сохранить: " + e.getMessage(), 0xFFF07070);
        }
    }

    private void generate() {
        if (generating) return;
        String prompt = promptInput.getText().trim();
        if (prompt.isEmpty()) {
            setStatus("опишите задачу в поле ниже", 0xFFF0C05A);
            return;
        }
        generating = true;
        setStatus("запрос к DeepSeek...", 0xFFA1A1AA);
        ScriptConsole.info("ide", "DeepSeek: " + shorten(prompt, 80));
        String existing = sourceText().trim();
        String request = existing.isEmpty() || existing.startsWith("// Новый скрипт")
                ? prompt
                : prompt + "\n\nТекущий код скрипта, его нужно доработать:\n```kotlin\n" + existing + "\n```";
        DeepSeekClient.generate(request, (script, error) -> {
            generating = false;
            if (error != null) {
                setStatus("DeepSeek: " + shorten(error, 90), 0xFFF07070);
                ScriptConsole.error("ide", "DeepSeek: " + error);
                return;
            }
            MinecraftClient.getInstance().execute(() -> {
                lines.clear();
                for (String line : script.split("\n", -1)) lines.add(new StringBuilder(line.replace("\r", "")));
                if (lines.isEmpty()) lines.add(new StringBuilder());
                cursorLine = 0;
                cursorCol = 0;
                editorScroll = 0f;
                setStatus("код получен, проверьте и сохраните", 0xFF7FD88F);
                ScriptConsole.info("ide", "DeepSeek returned " + lines.size() + " lines");
            });
        });
    }

    private void setStatus(String text, int color) {
        status = text;
        statusColor = color;
    }

    private static String shorten(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, Math.max(0, max - 1)) + "…";
    }
}
