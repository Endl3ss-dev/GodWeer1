package tech.lightclient.ui.gui.elements;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import tech.lightclient.client.ClientColors;
import tech.lightclient.client.utils.player.PlayerUtils;
import tech.lightclient.client.utils.render.CursorHelper;
import tech.lightclient.client.utils.render.animations.Easings;
import tech.lightclient.client.utils.render.color.basic.ColorUtils;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.api.settings.api.Setting;
import tech.lightclient.scripting.mcp.McpServer;
import tech.lightclient.scripting.runtime.ScriptConsole;
import tech.lightclient.scripting.runtime.ScriptInstance;
import tech.lightclient.scripting.runtime.ScriptManager;
import tech.lightclient.ui.screen.ScriptIdeScreen;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * The Scripts tab: two large cards per row with a toggle, settings, and a bind, plus the
 * console and the IDE and MCP buttons in the header.
 */
public class CsScriptsWidget {

    private static final float WIDTH = 720f;
    private static final float HEIGHT = 600f;
    private static final float PADDING = 18f;
    private static final float HEADER_HEIGHT = 82f;
    private static final float CARD_GAP = 10f;
    private static final float CARD_ROUNDING = 7f;
    private static final float CARD_HEADER_H = 50f;
    private static final float CARD_PAD_X = 12f;
    private static final float CARD_PAD_Y = 10f;
    private static final float SETTINGS_GAP = 4f;
    private static final float SETTINGS_INSET = 10f;
    private static final float BTN_H = 34f;

    private float scroll = 0f;
    private float targetScroll = 0f;
    private long lastAnimTime = System.currentTimeMillis();

    /** Scripts whose settings are expanded. */
    private final java.util.Set<String> expanded = new java.util.HashSet<>();
    private final Map<String, Float> hoverAnims = new HashMap<>();
    private final Map<Setting<?>, CsSettingComponent<?>> settingComponents = new IdentityHashMap<>();

    /** Script currently waiting for a key to bind, if any. */
    private ScriptInstance bindingScript = null;

    private boolean consoleOpen = false;
    private boolean consolePinned = false;
    private float consoleScroll = 0f;

    private float lastX;
    private float lastY;
    private float listTopY;
    private float listBottomY;

    // header button rectangles, filled during draw
    private float reloadX, reloadW;
    private float folderX, folderW;
    private float ideX, ideW;
    private float mcpX, mcpW;
    private float consoleX, consoleW;
    private float headerBtnY;
    private float entranceProgress = 1f;

    public float getWidth() {
        return WIDTH;
    }

    public float getHeight() {
        return HEIGHT;
    }

    public void setEntranceProgress(float progress) {
        entranceProgress = Math.max(0f, Math.min(1f, progress));
    }

    public boolean isConsolePinned() {
        return consolePinned;
    }

    private static int withAlpha(int color, int alpha) {
        return (Math.max(0, Math.min(255, alpha)) << 24) | (color & 0x00FFFFFF);
    }

    // ---- drawing ---------------------------------------------------------------------------

    public void draw(Renderer2D render, float x, float y, float alpha, int mouseX, int mouseY, float screenW, float screenH) {
        lastX = x;
        lastY = y;

        float dt = updateAnimTime();
        float alphaFactor = Math.max(0f, Math.min(1f, alpha / 255f));

        int textBright = ColorUtils.rgba(228, 228, 234, (int) alpha);
        int textDim = ColorUtils.rgba(161, 161, 170, (int) alpha);
        int textFaint = ColorUtils.rgba(113, 113, 122, (int) alpha);
        int cardBg = ColorUtils.rgba(9, 9, 12, (int) (110 * alphaFactor));
        int borderCol = ColorUtils.rgba(39, 39, 42, (int) (190 * alphaFactor));
        int accent = withAlpha(ClientColors.ICON.getRGB(), (int) (255 * alphaFactor));
        float cardsProgress = Easings.EASE_IN_OUT_CUBIC.ease(Math.max(0f, Math.min(1f, entranceProgress / 0.45f)));
        float actionsProgress = Easings.EASE_IN_OUT_CUBIC.ease(Math.max(0f, Math.min(1f, (entranceProgress - 0.45f) / 0.30f)));
        float titleProgress = Easings.EASE_IN_OUT_CUBIC.ease(Math.max(0f, Math.min(1f, (entranceProgress - 0.75f) / 0.25f)));

        if (FontRegistry.DELTA_CLICKGUI_MEDIUM != null && titleProgress > 0.001f) {
            render.pushAlpha(titleProgress);
            render.pushTranslation(0f, -24f * (1f - titleProgress));
            render.text(FontRegistry.DELTA_CLICKGUI_MEDIUM, x + PADDING, y + 34f, 22f, "Scripts", textBright);
            render.popTransform();
            render.popAlpha();
        }

        render.pushClipRect((int) x, (int) y, (int) WIDTH, (int) HEADER_HEIGHT);
        render.pushAlpha(actionsProgress);
        render.pushTranslation(0f, -40f * (1f - actionsProgress));
        drawHeaderButtons(render, x, y, alphaFactor, mouseX, mouseY, dt, cardBg, borderCol, accent, textBright);
        render.popTransform();
        render.popAlpha();
        render.popClipRect();

        List<ScriptInstance> scripts = ScriptManager.get().all();

        float contentTop = y + HEADER_HEIGHT + PADDING;
        listTopY = contentTop;
        listBottomY = y + getHeight() - PADDING;
        float listH = listBottomY - listTopY;
        float cardW = (WIDTH - PADDING * 2f - CARD_GAP) * 0.5f;

        float total = 0f;
        for (int row = 0; row < scripts.size(); row += 2) {
            float rowHeight = cardHeight(scripts.get(row), cardW);
            if (row + 1 < scripts.size()) {
                rowHeight = Math.max(rowHeight, cardHeight(scripts.get(row + 1), cardW));
            }
            total += rowHeight + CARD_GAP;
        }
        total = Math.max(0f, total - CARD_GAP);

        float maxScroll = Math.max(0, total - listH);
        targetScroll = Math.max(0, Math.min(targetScroll, maxScroll));
        scroll += (targetScroll - scroll) * 0.18f;
        if (Math.abs(targetScroll - scroll) < 0.1f) scroll = targetScroll;

        render.pushClipRect((int) x, (int) listTopY, (int) WIDTH, (int) listH);
        render.pushTranslation(0f, -90f * (1f - cardsProgress));

        if (scripts.isEmpty()) {
            if (FontRegistry.DELTA_CLICKGUI != null) {
                String line1 = "Нет скриптов";
                String line2 = "Положите .kts файл в папку скриптов или откройте IDE";
                float cy = listTopY + listH * 0.5f;
                render.text(FontRegistry.DELTA_CLICKGUI,
                        x + (WIDTH - FontRegistry.DELTA_CLICKGUI.getWidth(line1, 16f)) * 0.5f, cy - 6f, 16f, line1, textDim);
                render.text(FontRegistry.DELTA_CLICKGUI,
                        x + (WIDTH - FontRegistry.DELTA_CLICKGUI.getWidth(line2, 12f)) * 0.5f, cy + 14f, 12f, line2, textFaint);
            }
        }

        float cardY = listTopY - scroll;
        for (int row = 0; row < scripts.size(); row += 2) {
            ScriptInstance left = scripts.get(row);
            float rowHeight = cardHeight(left, cardW);
            if (row + 1 < scripts.size()) {
                rowHeight = Math.max(rowHeight, cardHeight(scripts.get(row + 1), cardW));
            }

            float h = cardHeight(left, cardW);
            if (cardY + h >= listTopY - 5f && cardY <= listBottomY + 5f) {
                drawCard(render, left, x + PADDING, cardY, cardW, alphaFactor, alpha, mouseX, mouseY, dt);
            }
            if (row + 1 < scripts.size()) {
                ScriptInstance right = scripts.get(row + 1);
                h = cardHeight(right, cardW);
                if (cardY + h >= listTopY - 5f && cardY <= listBottomY + 5f) {
                    drawCard(render, right, x + PADDING + cardW + CARD_GAP, cardY, cardW, alphaFactor, alpha, mouseX, mouseY, dt);
                }
            }
            cardY += rowHeight + CARD_GAP;
        }

        render.popTransform();
        render.popClipRect();

        if (consoleOpen) {
            drawConsole(render, x, y, alphaFactor, mouseX, mouseY);
        }
    }

    private void drawHeaderButtons(Renderer2D render, float x, float y, float alphaFactor, int mouseX, int mouseY,
                                   float dt, int cardBg, int borderCol, int accent, int textBright) {
        headerBtnY = y + 8f;
        float right = x + WIDTH - PADDING;

        consoleW = 96f;
        consoleX = right - consoleW;
        drawButton(render, consoleX, headerBtnY, consoleW, "Консоль", consoleOpen, alphaFactor, mouseX, mouseY, cardBg, borderCol, accent, textBright);

        mcpW = 70f;
        mcpX = consoleX - 8f - mcpW;
        drawButton(render, mcpX, headerBtnY, mcpW, "MCP", McpServer.get().isRunning(), alphaFactor, mouseX, mouseY, cardBg, borderCol, accent, textBright);

        ideW = 70f;
        ideX = mcpX - 8f - ideW;
        drawButton(render, ideX, headerBtnY, ideW, "IDE", false, alphaFactor, mouseX, mouseY, cardBg, borderCol, accent, textBright);

        folderW = 100f;
        folderX = ideX - 8f - folderW;
        drawButton(render, folderX, headerBtnY, folderW, "Папка", false, alphaFactor, mouseX, mouseY, cardBg, borderCol, accent, textBright);

        reloadW = 100f;
        reloadX = folderX - 8f - reloadW;
        drawButton(render, reloadX, headerBtnY, reloadW, "Обновить", false, alphaFactor, mouseX, mouseY, cardBg, borderCol, accent, textBright);
    }

    private void drawButton(Renderer2D render, float bx, float by, float bw, String label, boolean active,
                            float alphaFactor, int mouseX, int mouseY, int cardBg, int borderCol, int accent, int textBright) {
        boolean hovered = mouseX >= bx && mouseX <= bx + bw && mouseY >= by && mouseY <= by + BTN_H;
        if (hovered) CursorHelper.setHand();

        int bg = active ? withAlpha(ClientColors.ICON.getRGB(), (int) (170 * alphaFactor))
                : (hovered ? ColorUtils.rgba(255, 255, 255, (int) (22 * alphaFactor)) : cardBg);
        render.rect(bx, by, bw, BTN_H, 8f, bg);
        render.rectOutline(bx, by, bw, BTN_H, 8f, active ? accent : borderCol, 1f);
        if (FontRegistry.DELTA_CLICKGUI_MEDIUM != null) {
            float size = 12.5f;
            float ty = by + BTN_H * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_CLICKGUI_MEDIUM, 'H', size);
            render.text(FontRegistry.DELTA_CLICKGUI_MEDIUM, bx + (bw - FontRegistry.DELTA_CLICKGUI_MEDIUM.getWidth(label, size)) * 0.5f,
                    ty, size, label, textBright);
        }
    }

    private void drawCard(Renderer2D render, ScriptInstance script, float cx, float cy, float cw,
                          float alphaFactor, float alpha, int mouseX, int mouseY, float dt) {
        boolean loading = script.status == ScriptInstance.Status.LOADING;
        boolean error = script.status == ScriptInstance.Status.ERROR;
        float h = cardHeight(script, cw);

        int cardBg = ColorUtils.rgba(9, 9, 12, (int) (110 * alphaFactor));
        int borderCol = error
                ? ColorUtils.rgba(220, 70, 70, (int) (200 * alphaFactor))
                : ColorUtils.rgba(39, 39, 42, (int) (190 * alphaFactor));

        if (tech.lightclient.modules.impl.render.ClickGui.isSimpleBlurEnabled()) {
            render.prepareBlur(tech.lightclient.modules.impl.render.ClickGui.getSimpleBlurStrength());
            render.blur(cx, cy, cw, h, CARD_ROUNDING, alphaFactor);
        }
        render.rect(cx, cy, cw, h, CARD_ROUNDING, cardBg);
        render.rectOutline(cx, cy, cw, h, CARD_ROUNDING, borderCol, 1f);

        // A card being compiled is inert and dimmed, as the docs describe.
        float contentAlpha = loading ? alphaFactor * 0.45f : alphaFactor;
        int textBright = ColorUtils.rgba(228, 228, 234, (int) (255 * contentAlpha));
        int textDim = ColorUtils.rgba(161, 161, 170, (int) (255 * contentAlpha));

        String subtitle = error ? script.error
                : loading ? "компиляция..."
                : script.description;
        boolean hasSubtitle = subtitle != null && !subtitle.isEmpty() && FontRegistry.DELTA_CLICKGUI != null;
        float nameSize = 15f;
        float nameLineH = FontRegistry.DELTA_CLICKGUI_MEDIUM.getLineHeight(nameSize);
        float subtitleSize = 11f;
        float subtitleLineH = hasSubtitle ? FontRegistry.DELTA_CLICKGUI.getLineHeight(subtitleSize) : 0f;
        float textBlockH = hasSubtitle ? nameLineH + 7f + subtitleLineH : nameLineH;
        float textBlockTop = cy + (CARD_HEADER_H - textBlockH) * 0.5f;
        float nameY = textBlockTop + nameLineH * 0.5f
                + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_CLICKGUI_MEDIUM, 'H', nameSize);
        render.text(FontRegistry.DELTA_CLICKGUI_MEDIUM, cx + CARD_PAD_X, nameY, nameSize, trim(script.displayName(), 20), textBright);

        if (hasSubtitle) {
            int subCol = error ? ColorUtils.rgba(240, 120, 120, (int) (255 * alphaFactor)) : textDim;
            float subtitleY = textBlockTop + nameLineH + 7f + subtitleLineH * 0.5f
                    + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_CLICKGUI, 'H', subtitleSize);
            render.text(FontRegistry.DELTA_CLICKGUI, cx + CARD_PAD_X, subtitleY, subtitleSize,
                    trim(subtitle, 38), subCol);
        }

        // toggle
        float toggleW = 30f;
        float toggleH = 15f;
        float toggleX = cx + cw - CARD_PAD_X - toggleW;
        float toggleY = cy + (CARD_HEADER_H - toggleH) * 0.5f;
        boolean on = script.isEnabled();
        int trackCol = on ? withAlpha(ClientColors.ICON.getRGB(), (int) (220 * contentAlpha))
                : ColorUtils.rgba(60, 60, 68, (int) (200 * contentAlpha));
        render.rect(toggleX, toggleY, toggleW, toggleH, toggleH * 0.5f, trackCol);
        float knobR = toggleH * 0.5f - 3f;
        float knobX = on ? toggleX + toggleW - knobR - 3f : toggleX + knobR + 3f;
        render.circle(knobX, toggleY + toggleH * 0.5f, knobR, 0f, 1f,
                ColorUtils.rgba(245, 246, 250, (int) (255 * contentAlpha)));

        // bind
        boolean listening = bindingScript == script;
        String bindLabel = listening ? "..." : (script.bind > 0 ? PlayerUtils.getBindName(script.bind) : "бинд");
        float bindSize = 11f;
        float bindW = Math.max(36f, FontRegistry.DELTA_CLICKGUI.getWidth(bindLabel, bindSize) + 12f);
        float bindX = toggleX - 7f - bindW;
        float bindH = 18f;
        float bindY = cy + (CARD_HEADER_H - bindH) * 0.5f;
        boolean bindHovered = mouseX >= bindX && mouseX <= bindX + bindW && mouseY >= bindY && mouseY <= bindY + bindH;
        if (bindHovered) CursorHelper.setHand();
        render.rect(bindX, bindY, bindW, bindH, 6f,
                ColorUtils.rgba(255, 255, 255, (int) ((listening || bindHovered ? 26 : 14) * contentAlpha)));
        render.text(FontRegistry.DELTA_CLICKGUI,
                bindX + (bindW - FontRegistry.DELTA_CLICKGUI.getWidth(bindLabel, bindSize)) * 0.5f,
                bindY + bindH * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_CLICKGUI, 'H', bindSize),
                bindSize, bindLabel, listening ? withAlpha(ClientColors.ICON.getRGB(), (int) (255 * contentAlpha)) : textDim);

        // settings expander
        List<Setting<?>> settings = script.nativeSettings();
        if (!settings.isEmpty()) {
            String dots = expanded.contains(script.id) ? "\u2013" : "\u2022\u2022\u2022";
            float dotsW = 22f;
            float dotsX = bindX - 5f - dotsW;
            boolean dotsHovered = mouseX >= dotsX && mouseX <= dotsX + dotsW && mouseY >= bindY && mouseY <= bindY + bindH;
            if (dotsHovered) CursorHelper.setHand();
            render.text(FontRegistry.DELTA_CLICKGUI_MEDIUM,
                    dotsX + (dotsW - FontRegistry.DELTA_CLICKGUI_MEDIUM.getWidth(dots, 13f)) * 0.5f,
                    bindY + bindH * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_CLICKGUI_MEDIUM, 'H', 13f),
                    13f, dots, dotsHovered ? textBright : textDim);
        }

        // settings body
        if (expanded.contains(script.id) && !settings.isEmpty()) {
            float innerW = cw - CARD_PAD_X * 2f - SETTINGS_INSET * 2f;
            float sy = cy + CARD_HEADER_H;
            for (Setting<?> setting : settings) {
                CsSettingComponent<?> comp = component(setting, innerW);
                if (!comp.shouldRender()) continue;
                float sh = comp.getAnimatedHeight();
                if (sh <= 0.01f) continue;
                comp.draw(render, cx + CARD_PAD_X + SETTINGS_INSET, sy, mouseX, mouseY, (int) (255 * contentAlpha));
                sy += sh + SETTINGS_GAP * comp.getVisibilityProgress();
            }
        }
    }

    private void drawConsole(Renderer2D render, float x, float y, float alphaFactor, int mouseX, int mouseY) {
        float w = WIDTH - PADDING * 2f;
        float h = 240f;
        float cx = x + PADDING;
        float cy = y + getHeight() - PADDING - h;

        render.rect(cx, cy, w, h, 10f, ColorUtils.rgba(8, 8, 11, (int) (245 * alphaFactor)));
        render.rectOutline(cx, cy, w, h, 10f, ColorUtils.rgba(48, 48, 54, (int) (220 * alphaFactor)), 1f);

        int textBright = ColorUtils.rgba(228, 228, 234, (int) (255 * alphaFactor));
        render.text(FontRegistry.DELTA_CLICKGUI_MEDIUM, cx + 12f, cy + 22f, 14f, "Консоль скриптов", textBright);

        // pin and clear
        float btnY = cy + 8f;
        float pinW = 70f;
        float clearW = 70f;
        float clearX = cx + w - 12f - clearW;
        float pinX = clearX - 8f - pinW;
        drawButton(render, pinX, btnY, pinW, consolePinned ? "Откреп." : "Закреп.", consolePinned, alphaFactor,
                mouseX, mouseY, ColorUtils.rgba(20, 20, 26, (int) (200 * alphaFactor)),
                ColorUtils.rgba(48, 48, 54, (int) (200 * alphaFactor)),
                withAlpha(ClientColors.ICON.getRGB(), (int) (255 * alphaFactor)), textBright);
        drawButton(render, clearX, btnY, clearW, "Очистить", false, alphaFactor,
                mouseX, mouseY, ColorUtils.rgba(20, 20, 26, (int) (200 * alphaFactor)),
                ColorUtils.rgba(48, 48, 54, (int) (200 * alphaFactor)),
                withAlpha(ClientColors.ICON.getRGB(), (int) (255 * alphaFactor)), textBright);

        float lineH = 13f;
        float bodyTop = cy + 40f;
        float bodyH = h - 48f;
        render.pushClipRect((int) cx, (int) bodyTop, (int) w, (int) bodyH);

        List<ScriptConsole.Line> lines = ScriptConsole.snapshot();
        int visible = (int) (bodyH / lineH);
        int start = Math.max(0, lines.size() - visible - (int) consoleScroll);
        int end = Math.min(lines.size(), start + visible);

        float ly = bodyTop + 10f;
        for (int i = start; i < end; i++) {
            ScriptConsole.Line line = lines.get(i);
            int levelCol = switch (line.level) {
                case WARN -> ColorUtils.rgba(240, 200, 90, (int) (255 * alphaFactor));
                case ERROR -> ColorUtils.rgba(240, 110, 110, (int) (255 * alphaFactor));
                default -> withAlpha(ClientColors.ICON.getRGB(), (int) (255 * alphaFactor));
            };
            // INFO keeps its text white so only real problems colour the whole row.
            int bodyCol = line.level == ScriptConsole.Level.INFO ? textBright : levelCol;

            String head = "[" + line.time + "] [" + line.level + "] [" + line.script + "] ";
            render.text(FontRegistry.DELTA_CLICKGUI, cx + 12f, ly, 11f, head, levelCol);
            float headW = FontRegistry.DELTA_CLICKGUI.getWidth(head, 11f);
            render.text(FontRegistry.DELTA_CLICKGUI, cx + 12f + headW, ly, 11f, line.text, bodyCol);
            ly += lineH;
        }
        render.popClipRect();
    }

    // ---- geometry --------------------------------------------------------------------------

    private float cardHeight(ScriptInstance script, float cw) {
        float h = CARD_HEADER_H;
        if (expanded.contains(script.id)) {
            float innerW = cw - CARD_PAD_X * 2f - SETTINGS_INSET * 2f;
            float sh = 0f;
            for (Setting<?> setting : script.nativeSettings()) {
                CsSettingComponent<?> comp = component(setting, innerW);
                if (!comp.shouldRender()) continue;
                float ch = comp.getAnimatedHeight();
                if (ch <= 0.01f) continue;
                sh += ch + SETTINGS_GAP * comp.getVisibilityProgress();
            }
            if (sh > 0f) h += sh + CARD_PAD_Y * 2f;
        }
        return h;
    }

    private CsSettingComponent<?> component(Setting<?> setting, float width) {
        CsSettingComponent<?> comp = settingComponents.get(setting);
        if (comp == null) {
            comp = CsSettingComponent.create(setting, width);
            settingComponents.put(setting, comp);
        } else {
            comp.setWidth(width);
        }
        return comp;
    }

    // ---- input -----------------------------------------------------------------------------

    public boolean mouseClicked(double mouseX, double mouseY, int button, float x, float y, float screenW, float screenH) {
        // A pending bind swallows the next click.
        if (bindingScript != null) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) bindingScript.bind = -1;
            else bindingScript.bind = 1000 + button;
            bindingScript = null;
            return true;
        }

        if (consoleOpen) {
            float w = WIDTH - PADDING * 2f;
            float h = 240f;
            float cx = x + PADDING;
            float cy = y + getHeight() - PADDING - h;
            float btnY = cy + 8f;
            float clearX = cx + w - 12f - 70f;
            float pinX = clearX - 8f - 70f;
            if (mouseY >= btnY && mouseY <= btnY + BTN_H) {
                if (mouseX >= pinX && mouseX <= pinX + 70f) {
                    consolePinned = !consolePinned;
                    return true;
                }
                if (mouseX >= clearX && mouseX <= clearX + 70f) {
                    ScriptConsole.clear();
                    return true;
                }
            }
            if (mouseX >= cx && mouseX <= cx + w && mouseY >= cy && mouseY <= cy + h) return true;
        }

        // header buttons
        if (mouseY >= headerBtnY && mouseY <= headerBtnY + BTN_H) {
            if (inX(mouseX, reloadX, reloadW)) {
                ScriptManager.get().reloadAll();
                return true;
            }
            if (inX(mouseX, folderX, folderW)) {
                ScriptManager.get().openFolder();
                return true;
            }
            if (inX(mouseX, ideX, ideW)) {
                MinecraftClient.getInstance().setScreen(new ScriptIdeScreen(null));
                return true;
            }
            if (inX(mouseX, mcpX, mcpW)) {
                McpServer.get().toggle();
                return true;
            }
            if (inX(mouseX, consoleX, consoleW)) {
                consoleOpen = !consoleOpen;
                return true;
            }
        }

        if (mouseY < listTopY || mouseY > listBottomY) return false;

        float cardW = (WIDTH - PADDING * 2f - CARD_GAP) * 0.5f;
        float cardY = listTopY - scroll;

        List<ScriptInstance> scripts = ScriptManager.get().all();
        for (int row = 0; row < scripts.size(); row += 2) {
            ScriptInstance left = scripts.get(row);
            float rowHeight = cardHeight(left, cardW);
            if (mouseY >= cardY && mouseY <= cardY + rowHeight && mouseX >= x + PADDING && mouseX <= x + PADDING + cardW) {
                if (handleCardClick(left, mouseX, mouseY, button, x + PADDING, cardY, cardW)) return true;
            }
            if (row + 1 < scripts.size()) {
                ScriptInstance right = scripts.get(row + 1);
                float rightHeight = cardHeight(right, cardW);
                rowHeight = Math.max(rowHeight, rightHeight);
                float rightX = x + PADDING + cardW + CARD_GAP;
                if (mouseY >= cardY && mouseY <= cardY + rightHeight && mouseX >= rightX && mouseX <= rightX + cardW) {
                    if (handleCardClick(right, mouseX, mouseY, button, rightX, cardY, cardW)) return true;
                }
            }
            cardY += rowHeight + CARD_GAP;
        }
        return false;
    }

    private boolean handleCardClick(ScriptInstance script, double mouseX, double mouseY, int button,
                                    float cx, float cy, float cw) {
        // Right-clicking any part of a script card removes the script from disk and unloads it.
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            ScriptManager.get().delete(script.id);
            expanded.remove(script.id);
            if (bindingScript == script) bindingScript = null;
            return true;
        }
        boolean inHeader = mouseY <= cy + CARD_HEADER_H;

        if (inHeader) {
            float toggleW = 30f;
            float toggleX = cx + cw - CARD_PAD_X - toggleW;
            float bindLabelW;
            String bindLabel = script.bind > 0 ? PlayerUtils.getBindName(script.bind) : "бинд";
            bindLabelW = Math.max(36f, FontRegistry.DELTA_CLICKGUI.getWidth(bindLabel, 11f) + 12f);
            float bindX = toggleX - 7f - bindLabelW;
            float dotsX = bindX - 5f - 22f;

            // middle click on the card is the documented shortcut for setting a bind
            if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                bindingScript = script;
                return true;
            }
            if (mouseX >= toggleX && mouseX <= toggleX + toggleW) {
                if (script.status == ScriptInstance.Status.ERROR) return true;
                script.toggle();
                return true;
            }
            if (mouseX >= bindX && mouseX <= bindX + bindLabelW) {
                bindingScript = script;
                return true;
            }
            if (!script.nativeSettings().isEmpty() && mouseX >= dotsX && mouseX <= dotsX + 22f) {
                toggleExpanded(script);
                return true;
            }
            return true;
        }

        // clicks inside the settings body
        if (expanded.contains(script.id)) {
            float innerW = cw - CARD_PAD_X * 2f - SETTINGS_INSET * 2f;
            float sy = cy + CARD_HEADER_H;
            for (Setting<?> setting : script.nativeSettings()) {
                CsSettingComponent<?> comp = component(setting, innerW);
                if (!comp.shouldRender()) continue;
                float sh = comp.getAnimatedHeight();
                if (sh <= 0.01f) continue;
                if (mouseY >= sy && mouseY <= sy + sh) {
                    comp.mouseClicked(mouseX, mouseY, button, cx + CARD_PAD_X + SETTINGS_INSET, sy);
                    return true;
                }
                sy += sh + SETTINGS_GAP * comp.getVisibilityProgress();
            }
        }
        return true;
    }

    private void toggleExpanded(ScriptInstance script) {
        if (!expanded.remove(script.id)) expanded.add(script.id);
    }

    private static boolean inX(double mouseX, float bx, float bw) {
        return mouseX >= bx && mouseX <= bx + bw;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double verticalAmount, float x, float y, float screenW, float screenH) {
        if (consoleOpen) {
            float h = 240f;
            float cy = y + getHeight() - PADDING - h;
            if (mouseY >= cy && mouseY <= cy + h) {
                consoleScroll = Math.max(0f, consoleScroll + (float) verticalAmount * 3f);
                return true;
            }
        }
        if (mouseX >= x && mouseX <= x + WIDTH && mouseY >= listTopY && mouseY <= listBottomY) {
            targetScroll -= (float) verticalAmount * 40f;
            return true;
        }
        return false;
    }

    public boolean keyPressed(int keyCode) {
        if (bindingScript != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE) {
                bindingScript.bind = -1;
            } else if (keyCode != GLFW.GLFW_KEY_LEFT_SHIFT && keyCode != GLFW.GLFW_KEY_RIGHT_SHIFT
                    && keyCode != GLFW.GLFW_KEY_LEFT_CONTROL && keyCode != GLFW.GLFW_KEY_RIGHT_CONTROL
                    && keyCode != GLFW.GLFW_KEY_LEFT_ALT && keyCode != GLFW.GLFW_KEY_RIGHT_ALT) {
                bindingScript.bind = keyCode;
            }
            bindingScript = null;
            return true;
        }
        if (CsSettingComponent.listeningBindComp != null) {
            CsSettingComponent.listeningBindComp.keyPressed(keyCode);
            return true;
        }
        if (CsStringComponent.focusedStringComp != null) {
            CsStringComponent.focusedStringComp.keyPressed(keyCode);
            return true;
        }
        if (consoleOpen && keyCode == GLFW.GLFW_KEY_ESCAPE) {
            consoleOpen = false;
            return true;
        }
        return false;
    }

    public boolean charTyped(char c) {
        if (CsStringComponent.focusedStringComp != null) {
            CsStringComponent.focusedStringComp.charTyped(c);
            return true;
        }
        return false;
    }

    public void mouseReleased(int button) {
    }

    public void tickDrag(double mx, double my) {
    }

    // ---- helpers ---------------------------------------------------------------------------

    private static String trim(String value, int maxChars) {
        if (value == null) return "";
        String single = value.replace('\n', ' ').replace('\r', ' ');
        return single.length() <= maxChars ? single : single.substring(0, maxChars) + "...";
    }

    private float updateAnimTime() {
        long now = System.currentTimeMillis();
        long dt = now - lastAnimTime;
        lastAnimTime = now;
        return Math.min(dt, 50);
    }
}
