package swerve.ui.menu;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import swerve.Swerve;
import swerve.ui.menu.MenuText.Weight;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import org.joml.Quaternionf;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Примитивы отрисовки меню (порт Arcanic Visuals, GPL-3.0).
 *
 * <p>Адаптация на 1.21.4: у {@code GuiGraphics.blit} первым аргументом идёт RenderType-фабрика,
 * а UV — нормализованные float; слои (nextStratum) в 1.21.4 нет — порядок задаёт сам вызов.
 * Все координаты — в реальных пикселях окна, матрица позы масштабируется в {@link #begin(GuiGraphics)}.
 */
public final class Px {

    private Px() {
    }

    public static final ResourceLocation ATLAS = Swerve.id("textures/gui/atlas.png");
    public static final ResourceLocation GLOW = Swerve.id("textures/gui/glow.png");
    public static final ResourceLocation CHAMFER = Swerve.id("textures/gui/chamfer.png");

    public static final int MAX_R = 36;
    /** Общий множитель прозрачности: применяется ко всем примитивам. */
    public static float fade = 1;

    private static int atlasW = 1024;
    private static int atlasH = 1024;
    private static final Map<Integer, int[]> DISC = new HashMap<>();
    private static final Map<Integer, int[]> RING = new HashMap<>();
    private static int[] shadow;
    private static int[] hgrad;
    private static int[] vgrad;

    private static final Map<String, Integer> ICONS = new HashMap<>();
    private static final Map<String, Integer> KIT = new HashMap<>();
    private static final Map<String, String> ICON_ALIAS = new HashMap<>();
    private static int kitCols = 16;
    private static int kitRows = 8;

    /** Кватернион поворота: переиспользуется, чтобы в кадре не было new. */
    private static final Quaternionf ROT = new Quaternionf();

    static {
        try (var in = Px.class.getResourceAsStream("/assets/swerve/menu/atlas.json")) {
            JsonObject o = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .getAsJsonObject();
            for (var e : o.getAsJsonObject("disc").entrySet()) {
                var a = e.getValue().getAsJsonArray();
                DISC.put(Integer.parseInt(e.getKey()), new int[] {a.get(0).getAsInt(), a.get(1).getAsInt()});
            }
            for (var e : o.getAsJsonObject("ring").entrySet()) {
                var a = e.getValue().getAsJsonArray();
                RING.put(Integer.parseInt(e.getKey()), new int[] {a.get(0).getAsInt(), a.get(1).getAsInt()});
            }
            var s = o.getAsJsonArray("shadow");
            shadow = new int[] {s.get(0).getAsInt(), s.get(1).getAsInt(), s.get(2).getAsInt(), s.get(3).getAsInt()};
            var h = o.getAsJsonArray("hgrad");
            hgrad = new int[] {h.get(0).getAsInt(), h.get(1).getAsInt()};
            var v = o.getAsJsonArray("vgrad");
            vgrad = new int[] {v.get(0).getAsInt(), v.get(1).getAsInt()};
            var size = o.getAsJsonArray("size");
            atlasW = size.get(0).getAsInt();
            atlasH = size.get(1).getAsInt();
        } catch (Exception e) {
            throw new IllegalStateException("swerve menu atlas", e);
        }
        try (var in = Px.class.getResourceAsStream("/assets/swerve/menu/icons.json")) {
            JsonObject o = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .getAsJsonObject();
            for (var e : o.entrySet()) {
                ICONS.put(e.getKey(), e.getValue().getAsInt());
            }
        } catch (Exception e) {
            throw new IllegalStateException("swerve menu icons", e);
        }
        try (var in = Px.class.getResourceAsStream("/assets/swerve/menu/kit_icons.json")) {
            if (in != null) {
                JsonObject o = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                        .getAsJsonObject();
                kitCols = o.get("cols").getAsInt();
                kitRows = o.get("rows").getAsInt();
                for (var e : o.getAsJsonObject("icons").entrySet()) {
                    KIT.put(e.getKey(), e.getValue().getAsInt());
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("swerve menu kit icons", e);
        }
        String[][] alias = {
            {"combat", "sword"},
            {"players", "users"},
            {"world", "mountains"},
            {"effects", "sparkles"},
            {"hands", "tap"},
            {"cosmetics", "wizard-hat"},
            {"hud", "monitor"},
            {"search", "search"},
            {"presets", "bookmark"},
            {"move", "expand-h"},
            {"settings", "sliders"},
            {"sliders", "sliders"},
            {"sun", "sun"},
            {"moon", "moon"},
            {"x", "x"},
            {"check", "check"},
            {"chev-down", "chev-down"},
            {"chev-right", "chev-right"},
            {"chev-left", "chev-left"},
            {"plus", "plus"},
            {"arrow-right", "arrow-right"},
            {"reset", "undo"},
            {"key", "key"},
            {"save", "save"},
            {"trash", "trash"},
            {"knife", "sword"},
            {"sparkle", "sparkles"},
            {"modules", "apps"},
            {"user", "user-circle"},
            {"server", "server"},
            {"bolt", "bolt"},
            {"ping", "router"},
            {"clock", "pie"},
            {"bind", "keyboard"},
            {"keyboard", "keyboard"},
            // обычные имена (Lucide), которые используются в меню и HUD
            {"chevron-down", "chev-down"},
            {"chevron-right", "chev-right"},
            {"chevron-up", "chev-up"},
            {"sliders-horizontal", "sliders"},
            {"pipette", "pipette"},
            {"layout-grid", "apps"},
            {"layout-dashboard", "monitor"},
            {"rotate-ccw", "undo"},
            {"sparkles", "sparkles"},
            {"eye", "eye"},
            {"list", "list"},
            {"zap", "bolt"},
            {"shield", "shield"},
            {"swords", "sword"},
            {"sword", "sword"},
            {"palette", "swatch"},
            {"swatch-book", "swatch"},
            {"paintbrush", "roller"},
            {"brush", "roller"},
            {"house", "house"},
            {"download", "download"},
            {"trash-2", "trash"},
            {"bookmark", "bookmark"},
            {"flask-conical", "flask"},
            {"hand", "tap"},
            {"badge-check", "award"},
            {"medal", "award"},
            {"trophy", "award"},
            {"layers", "server"},
            {"timer", "pie"},
            {"wand-sparkles", "wizard-hat"},
            {"shirt", "wizard-hat"},
            {"mouse-pointer-click", "click"},
            {"user-round", "users"},
            {"compass", "mountains"},
            {"monitor", "monitor"},
            {"type", "type"},
            {"info", "alert-circle"},
            {"apps", "apps"},
            {"running", "running"},
            {"wrench", "wrench"},
            {"user-circle", "user-circle"},
            {"sword", "sword"},
            {"expand", "expand-h"},
        };
        for (String[] a : alias) {
            if (!KIT.containsKey(a[1])) {
                continue;
            }
            ICON_ALIAS.put(a[0], a[1]);
            if (!a[0].contains("-") || a[0].startsWith("chev") || a[0].endsWith("-s")) {
                ICON_ALIAS.put("a." + a[0], a[1]);
                ICON_ALIAS.put("a." + a[0] + "-s", a[1]);
            }
        }
    }

    public static int guiScale() {
        return (int) Minecraft.getInstance().getWindow().getGuiScale();
    }

    public static int width() {
        return Minecraft.getInstance().getWindow().getWidth();
    }

    public static int height() {
        return Minecraft.getInstance().getWindow().getHeight();
    }

    /** Переводит координаты GUI в реальные пиксели окна. */
    public static double px(double gui) {
        var w = Minecraft.getInstance().getWindow();
        return gui * w.getWidth() / (double) Math.max(1, w.getGuiScaledWidth());
    }

    public static void begin(GuiGraphics g) {
        var w = Minecraft.getInstance().getWindow();
        g.pose().pushPose();
        g.pose().scale(
                w.getGuiScaledWidth() / (float) Math.max(1, w.getWidth()),
                w.getGuiScaledHeight() / (float) Math.max(1, w.getHeight()),
                1f);
    }

    public static void end(GuiGraphics g) {
        g.pose().popPose();
    }

    private static int f(int c) {
        return fade >= 1 ? c : am(c, fade);
    }

    public static void rect(GuiGraphics g, int x, int y, int w, int h, int c) {
        c = f(c);
        if (w > 0 && h > 0 && (c >>> 24) != 0) {
            g.fill(x, y, x + w, y + h, c);
        }
    }

    private static void edge(GuiGraphics g, int x, int y, int w, int h, int c) {
        c = f(c);
        if (w > 0 && h > 0 && (c >>> 24) != 0) {
            g.fill(x, y, x + w, y + h, c);
        }
    }

    private static void sprite(GuiGraphics g, int x, int y, int u, int v, int w, int h, int c) {
        if (w <= 0 || h <= 0) {
            return;
        }
        // В 1.21.4 u/v в blit задаются в пикселях текстуры, а не в долях.
        g.blit(RenderType::text, ATLAS, x, y, u, v, w, h, w, h, atlasW, atlasH, f(c));
    }

    private static void stretched(GuiGraphics g, int x, int y, int w, int h, int u, int v, int uw, int vh, int c) {
        if (w <= 0 || h <= 0) {
            return;
        }
        g.blit(RenderType::text, ATLAS, x, y, u, v, w, h, uw, vh, atlasW, atlasH, f(c));
    }

    /** Скруглённый прямоугольник со сглаживанием по краям. */
    public static void round(GuiGraphics g, int x, int y, int w, int h, int r, int c) {
        if (w <= 0 || h <= 0 || (c >>> 24) == 0) {
            return;
        }
        r = Math.max(0, Math.min(Math.min(r, MAX_R), Math.min(w, h) / 2));
        if (r == 0) {
            rect(g, x, y, w, h, c);
            return;
        }
        int[] d = DISC.get(r);
        sprite(g, x, y, d[0], d[1], r, r, c);
        sprite(g, x + w - r, y, d[0] + r, d[1], r, r, c);
        sprite(g, x, y + h - r, d[0], d[1] + r, r, r, c);
        sprite(g, x + w - r, y + h - r, d[0] + r, d[1] + r, r, r, c);
        rect(g, x + r, y, w - 2 * r, r, c);
        rect(g, x + r, y + h - r, w - 2 * r, r, c);
        rect(g, x, y + r, w, h - 2 * r, c);
    }

    /** Обводка скруглённого прямоугольника в 1 пиксель. */
    public static void outline(GuiGraphics g, int x, int y, int w, int h, int r, int c) {
        if (w <= 0 || h <= 0 || (c >>> 24) == 0) {
            return;
        }
        r = Math.max(1, Math.min(Math.min(r, MAX_R), Math.min(w, h) / 2));
        int[] d = RING.get(r);
        sprite(g, x, y, d[0], d[1], r, r, c);
        sprite(g, x + w - r, y, d[0] + r, d[1], r, r, c);
        sprite(g, x, y + h - r, d[0], d[1] + r, r, r, c);
        sprite(g, x + w - r, y + h - r, d[0] + r, d[1] + r, r, r, c);
        edge(g, x + r, y, w - 2 * r, 1, c);
        edge(g, x + r, y + h - 1, w - 2 * r, 1, c);
        edge(g, x, y + r, 1, h - 2 * r, c);
        edge(g, x + w - 1, y + r, 1, h - 2 * r, c);
    }

    public static void circle(GuiGraphics g, int cx, int cy, int r, int c) {
        if (r <= 0 || (c >>> 24) == 0) {
            return;
        }
        if (r > MAX_R) {
            round(g, cx - r, cy - r, r * 2, r * 2, r, c);
            return;
        }
        int[] d = DISC.get(r);
        sprite(g, cx - r, cy - r, d[0], d[1], r * 2, r * 2, c);
    }

    public static void ring(GuiGraphics g, int cx, int cy, int r, int c) {
        if (r <= 0 || (c >>> 24) == 0) {
            return;
        }
        if (r > MAX_R) {
            outline(g, cx - r, cy - r, r * 2, r * 2, r, c);
            return;
        }
        int[] d = RING.get(r);
        sprite(g, cx - r, cy - r, d[0], d[1], r * 2, r * 2, c);
    }

    /** Мягкая тень вокруг прямоугольника: spread — размытие, c — цвет и сила. */
    public static void shadow(GuiGraphics g, int x, int y, int w, int h, int spread, int c) {
        int sx = shadow[0];
        int sy = shadow[1];
        int s = shadow[2];
        int b = shadow[3];
        int k = Math.max(4, spread);
        int bx = x - k;
        int by = y - k;
        int bw = w + k * 2;
        int bh = h + k * 2;
        int cb = Math.min(b, Math.min(bw, bh) / 2);
        int ccb = cb;
        // углы
        stretched(g, bx, by, ccb, ccb, sx, sy, b, b, c);
        stretched(g, bx + bw - ccb, by, ccb, ccb, sx + s - b, sy, b, b, c);
        stretched(g, bx, by + bh - ccb, ccb, ccb, sx, sy + s - b, b, b, c);
        stretched(g, bx + bw - ccb, by + bh - ccb, ccb, ccb, sx + s - b, sy + s - b, b, b, c);
        // рёбра
        stretched(g, bx + ccb, by, bw - 2 * ccb, ccb, sx + b, sy, s - 2 * b, b, c);
        stretched(g, bx + ccb, by + bh - ccb, bw - 2 * ccb, ccb, sx + b, sy + s - b, s - 2 * b, b, c);
        stretched(g, bx, by + ccb, ccb, bh - 2 * ccb, sx, sy + b, b, s - 2 * b, c);
        stretched(g, bx + bw - ccb, by + ccb, ccb, bh - 2 * ccb, sx + s - b, sy + b, b, s - 2 * b, c);
        stretched(g, bx + ccb, by + ccb, bw - 2 * ccb, bh - 2 * ccb, sx + s / 2, sy + s / 2, 1, 1, c);
    }

    public static void hgrad(GuiGraphics g, int x, int y, int w, int h, int c1, int c2) {
        if (w <= 0 || h <= 0) {
            return;
        }
        if ((c1 >>> 24) == 255 && (c2 >>> 24) == 255) {
            rect(g, x, y, w, h, c1);
            stretched(g, x, y, w, h, hgrad[0], hgrad[1], 256, 4, c2);
            return;
        }
        // полупрозрачные переходы: c1 обратной шкалой, c2 прямой (два спрайта вместо полос)
        if ((c1 >>> 24) != 0) {
            stretched(g, x, y, w, h, hgrad[0] + 256, hgrad[1], -256, 4, c1);
        }
        if ((c2 >>> 24) != 0) {
            stretched(g, x, y, w, h, hgrad[0], hgrad[1], 256, 4, c2);
        }
    }

    public static void vgrad(GuiGraphics g, int x, int y, int w, int h, int c1, int c2) {
        rect(g, x, y, w, h, c1);
        stretched(g, x, y, w, h, vgrad[0], vgrad[1], 4, 256, c2);
    }

    /** Скруглённая «таблетка» с горизонтальным градиентом. */
    public static void pill(GuiGraphics g, int x, int y, int w, int h, int c1, int c2) {
        int r = Math.min(h / 2, w / 2);
        if (w <= 2 * r) {
            round(g, x, y, w, h, r, c1);
            return;
        }
        int[] d = DISC.get(Math.max(1, r));
        sprite(g, x, y, d[0], d[1], r, r, c1);
        sprite(g, x, y + h - r, d[0], d[1] + r, r, r, c1);
        rect(g, x, y + r, r, h - 2 * r, c1);
        sprite(g, x + w - r, y, d[0] + r, d[1], r, r, c2);
        sprite(g, x + w - r, y + h - r, d[0] + r, d[1] + r, r, r, c2);
        rect(g, x + w - r, y + r, r, h - 2 * r, c2);
        hgrad(g, x + r, y, w - 2 * r, h, c1, c2);
    }

    /** Сглаженная дуга: start — радианы (0 = вправо), sweep — размах, цвет перетекает из c1 в c2. */
    public static void arc(GuiGraphics g, float cx, float cy, float r, int thickness, double start, double sweep,
                           int c1, int c2) {
        if (Math.abs(sweep) < 1e-3 || thickness <= 0) {
            return;
        }
        int dr = Math.max(1, Math.round(thickness / 2f));
        int[] d = DISC.get(Math.min(MAX_R, dr));
        int steps = Math.max(2, (int) Math.ceil(Math.abs(sweep) * r / Math.max(.8f, dr * .45f)));
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            double a = start + sweep * t;
            int x = Math.round((float) (cx + Math.cos(a) * r)) - dr;
            int y = Math.round((float) (cy + Math.sin(a) * r)) - dr;
            sprite(g, x, y, d[0], d[1], dr * 2, dr * 2, mix(c1, c2, (float) t));
        }
    }

    public static void glow(GuiGraphics g, int x, int y, int w, int h, int c) {
        c = f(c);
        if (w <= 0 || h <= 0 || (c >>> 24) == 0) {
            return;
        }
        g.blit(RenderType::text, GLOW, x, y, 0f, 0f, w, h, 256, 256, 256, 256, c);
    }

    private static final int CH_M = 48;
    private static final int CH_W = 1176;
    private static final int CH_H = 288;
    public static final int TL = 1;
    public static final int TR = 2;
    public static final int BR = 4;
    public static final int BL = 8;

    private static void cham(GuiGraphics g, int x, int y, int n, int row, int c) {
        if (n <= 0) {
            return;
        }
        n = Math.min(n, CH_M);
        g.blit(RenderType::text, CHAMFER, x, y, n * (n - 1) / 2f, row * (float) CH_M,
                n, n, n, n, CH_W, CH_H, f(c));
    }

    public static void wedge(GuiGraphics g, int x, int y, int n, int which, int c) {
        if ((c >>> 24) != 0) {
            cham(g, x, y, n, which, c);
        }
    }

    /** Прямоугольник со срезанными на 45° углами (биты углов: TL | TR | BR | BL). */
    public static void chamfer(GuiGraphics g, int x, int y, int w, int h, int cut, int corners, int c) {
        if (w <= 0 || h <= 0 || (c >>> 24) == 0) {
            return;
        }
        cut = Math.max(0, Math.min(Math.min(cut, CH_M), Math.min(w, h) / 2));
        if (cut == 0 || corners == 0) {
            rect(g, x, y, w, h, c);
            return;
        }
        rect(g, x + cut, y, w - 2 * cut, cut, c);
        rect(g, x, y + cut, w, h - 2 * cut, c);
        rect(g, x + cut, y + h - cut, w - 2 * cut, cut, c);
        if ((corners & TL) != 0) {
            cham(g, x, y, cut, 0, c);
        } else {
            rect(g, x, y, cut, cut, c);
        }
        if ((corners & TR) != 0) {
            cham(g, x + w - cut, y, cut, 1, c);
        } else {
            rect(g, x + w - cut, y, cut, cut, c);
        }
        if ((corners & BR) != 0) {
            cham(g, x + w - cut, y + h - cut, cut, 2, c);
        } else {
            rect(g, x + w - cut, y + h - cut, cut, cut, c);
        }
        if ((corners & BL) != 0) {
            cham(g, x, y + h - cut, cut, 3, c);
        } else {
            rect(g, x, y + h - cut, cut, cut, c);
        }
    }

    public static void seg(GuiGraphics g, float x0, float y0, float x1, float y1, float thick, int c) {
        float dx = x1 - x0;
        float dy = y1 - y0;
        float len = (float) Math.hypot(dx, dy);
        c = f(c);
        if (len < .5f || (c >>> 24) == 0) {
            return;
        }
        g.pose().pushPose();
        g.pose().translate(x0, y0, 0f);
        rotate2d(g, (float) Math.atan2(dy, dx));
        int t = Math.max(1, Math.round(thick));
        g.fill(0, -t / 2, Math.round(len), -t / 2 + t, c);
        g.pose().popPose();
    }

    /** Поворот в плоскости экрана: в 1.21.4 у PoseStack нет 2D-rotate, поэтому кватернион. */
    public static void rotate2d(GuiGraphics g, float angle) {
        ROT.identity();
        ROT.rotateZ(angle);
        g.pose().mulPose(ROT);
    }

    public static void translate(GuiGraphics g, float x, float y) {
        g.pose().translate(x, y, 0f);
    }

    public static void scale(GuiGraphics g, float x, float y) {
        g.pose().scale(x, y, 1f);
    }

    // ---- текст

    public static int clampPx(int px) {
        return Math.max(8, Math.min(32, px));
    }

    private static int clampMsdf(int px) {
        return Math.max(6, Math.min(96, px));
    }

    public static int textWidth(String s, Weight weight, int px) {
        return Math.round(MenuText.width(s, weight, clampMsdf(px)));
    }

    public static void text(GuiGraphics g, String s, int x, int y, Weight weight, int px, int c) {
        MenuText.draw(g, s, x, textY(y, px) + 7, weight, clampMsdf(px), f(c));
    }

    public static void textRight(GuiGraphics g, String s, int right, int y, Weight weight, int px, int c) {
        text(g, s, right - textWidth(s, weight, px), y, weight, px, c);
    }

    public static void textCenter(GuiGraphics g, String s, int cx, int y, Weight weight, int px, int c) {
        text(g, s, cx - textWidth(s, weight, px) / 2, y, weight, px, c);
    }

    /**
     * Измеренные метрики: глифы всегда стоят на базовой линии на 7 единиц ниже y,
     * высота заглавной буквы ~0.735 px. Текст и иконки, нарисованные в y,
     * центрируются внутри бокса [y, y + px].
     */
    public static int capHeight(int px) {
        return Math.round(px * .735f);
    }

    public static int textY(int y, int px) {
        return Math.round(y + px / 2f + capHeight(px) / 2f - 7);
    }

    public static int iconY(int y, int px) {
        int ih = Math.round(px * .82f);
        int bottom = 6 - px / 17;
        return Math.round(y + px / 2f + ih / 2f - bottom);
    }

    public static int baseline(int px) {
        return textY(0, px);
    }

    public static void textShadow(GuiGraphics g, String s, int x, int y, Weight weight, int px, int c) {
        int sh = Math.max(1, px / 12);
        int shadow = f(am(0xFF000000, ((c >>> 24) / 255f) * .55f));
        MenuText.draw(g, s, x + sh, textY(y, px) + 7 + sh, weight, clampMsdf(px), shadow);
        MenuText.draw(g, s, x, textY(y, px) + 7, weight, clampMsdf(px), f(c));
    }

    public static String ellipsize(String s, Weight weight, int px, int max) {
        if (textWidth(s, weight, px) <= max) {
            return s;
        }
        while (s.length() > 1 && textWidth(s + "…", weight, px) > max) {
            s = s.substring(0, s.length() - 1);
        }
        return s + "…";
    }

    // ---- иконки (набор выпечен в атлас kit/icons_<size>.png)

    public static boolean hasIcon(String name) {
        return ICONS.containsKey(name) || kitCell(name) >= 0;
    }

    public static void icon(GuiGraphics g, String name, int x, int y, int px, int c) {
        int kit = kitCell(name);
        if (kit >= 0) {
            kitIcon(g, kit, x, y, px, c);
            return;
        }
        if (kit == -2) {
            return;
        }
        // Lucide-глифы живут в TTF-провайдере, которого в 1.21.4 нет: остаются только kit-иконки.
    }

    private static int kitCell(String name) {
        if (KIT.isEmpty() || name == null) {
            return -1;
        }
        if (name.endsWith("~")) {
            return ICON_ALIAS.containsKey(name.substring(0, name.length() - 1)) ? -2 : -1;
        }
        if (name.startsWith("w.")) {
            return KIT.getOrDefault(name.substring(2), -1);
        }
        String n = ICON_ALIAS.get(name);
        if (n == null && name.endsWith("-s")) {
            n = ICON_ALIAS.get(name.substring(0, name.length() - 2));
        }
        return n == null ? -1 : KIT.getOrDefault(n, -1);
    }

    private static void kitIcon(GuiGraphics g, int cell, int x, int y, int px, int c) {
        px = Math.max(8, Math.min(40, px));
        c = f(c);
        if ((c >>> 24) == 0) {
            return;
        }
        ResourceLocation tex = Swerve.id("textures/gui/kit/icons_" + px + ".png");
        int texW = kitCols * px;
        int texH = kitRows * px;
        g.blit(RenderType::text, tex, x, y,
                (cell % kitCols) * px, (cell / kitCols) * px,
                px, px, px, px, texW, texH, c);
    }

    // ---- цвета

    public static int a(int c, int alpha) {
        return (Math.max(0, Math.min(255, alpha)) << 24) | (c & 0xFFFFFF);
    }

    public static int am(int c, float k) {
        return a(c, (int) ((c >>> 24) * Math.max(0, Math.min(1, k))));
    }

    public static int mix(int a, int b, float t) {
        if (t <= 0) {
            return a;
        }
        if (t >= 1) {
            return b;
        }
        int a1 = (a >>> 24) & 0xFF;
        int r1 = (a >> 16) & 0xFF;
        int g1 = (a >> 8) & 0xFF;
        int b1 = a & 0xFF;
        int a2 = (b >>> 24) & 0xFF;
        int r2 = (b >> 16) & 0xFF;
        int g2 = (b >> 8) & 0xFF;
        int b2 = b & 0xFF;
        int ra = (int) (a1 + (a2 - a1) * t);
        int rr = (int) (r1 + (r2 - r1) * t);
        int rg = (int) (g1 + (g2 - g1) * t);
        int rb = (int) (b1 + (b2 - b1) * t);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    }
}
