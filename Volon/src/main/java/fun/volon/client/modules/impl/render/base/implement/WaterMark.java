package fun.volon.client.modules.impl.render.base.implement;

import net.minecraft.client.util.math.MatrixStack;
import fun.volon.Volon;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.draggable.Draggable;
import fun.volon.api.utils.math.MathUtils;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.client.modules.impl.render.base.HudRectStyle;
import fun.volon.client.modules.impl.render.base.InterfaceProcessing;

import java.awt.*;

public class WaterMark extends InterfaceProcessing {
    /** MSDF icon font — новая иконка вместо черепа */
    private static final String ICON_GLYPH = "d";
    private static final float ICON_PLATE_SIZE = 16f;
    private static final int ICON_SIZE = 14;
    private boolean showFps = true;
    private boolean showMs = true;
    private boolean showServer = true;
    private boolean showTps = true;
    private boolean showTpsRow = true;
    private boolean showBps = false;  // Отключено, будет отдельно внизу
    private boolean showXyz = false;  // Отключено, будет отдельно внизу
    private boolean showServerRow = true;
    
    private static String cachedUsername = null;
    private static int cachedUid = -1;
    private static boolean hasLoadedFromFile = false;
    private int SKULL_ICON_SIZE;

    public WaterMark(Draggable draggable) {
        super(draggable);
    }

    public boolean isShowFps() {
        return showFps;
    }

    public void setShowFps(boolean showFps) {
        this.showFps = showFps;
    }

    public boolean isShowMs() {
        return showMs;
    }

    public void setShowMs(boolean showMs) {
        this.showMs = showMs;
    }

    public boolean isShowServer() {
        return showServer;
    }

    public void setShowServer(boolean showServer) {
        this.showServer = showServer;
    }

    public boolean isShowTps() {
        return showTps;
    }

    public void setShowTps(boolean showTps) {
        this.showTps = showTps;
    }

    public boolean isShowTpsRow() { return showTpsRow; }
    public void setShowTpsRow(boolean v) { this.showTpsRow = v; }

    public boolean isShowBps() { return showBps; }
    public void setShowBps(boolean v) { this.showBps = v; }

    public boolean isShowXyz() { return showXyz; }
    public void setShowXyz(boolean v) { this.showXyz = v; }

    public boolean isShowServerRow() { return showServerRow; }
    public void setShowServerRow(boolean v) { this.showServerRow = v; }

    @Override
    public void onRender(EventRender.Default eventRender) {
        DefaultStyle(eventRender);
        super.onRender(eventRender);
    }

    public void DefaultStyle(EventRender.Default eventRender) {
        var matrices = eventRender.getContext().getMatrices();
        float x = draggable.getX();
        float y = draggable.getY();
        var brandFont = Fonts.getFont("suisse", 15);
        var iconNew14 = Fonts.getFont("iconnew", 14);
        var iconNew15 = Fonts.getFont("iconnew", 15);
        var icon14 = Fonts.getFont("icon", 14);
        Font iconFont = Fonts.getFont("icon", ICON_SIZE);
        var statsIconFont = iconNew14 != null ? iconNew14 : icon14;
        var suisse13 = Fonts.getFont("suisse", 13);

        float VolonRectH = 16;
        float iconPlateW = ICON_PLATE_SIZE;
        float iconPlateGap = 2.5f;
        String iconGlyph = "EvelPremium";
        int time = (int) ((System.currentTimeMillis() % 2000) / 2000f * 360f);
        float iconW = brandFont.getStringWidth(iconGlyph);
        float VolonRectX = x + iconPlateW + iconPlateGap;
        float iconX = VolonRectX + 4.0f;
        float iconY = y + 6.2f;
        int iconTop;
        if (!Volon.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            iconTop = Volon.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        } else {
            iconTop = ColorUtils.getThemeColor();
        }
        HudRectStyle rectStyle = getHudRectStyle();
        float rect2Pad = 3;
        int whiteColor = new Color(255, 255, 255, 255).getRGB();
        float textY = y + 6.8f;

        float VolonRectY = y;
        float VolonRectW = iconW + 8.0f;

        // ── Новая иконка вместо черепа ──────────────────────────────────────────
        float iconPlateX = x;
        float iconPlateY = y;
        drawHudBackground(matrices, iconPlateX, iconPlateY, iconPlateW, VolonRectH, iconTop);
        if (iconFont != null) {
            float iconGlyphW = iconFont.getWidth(ICON_GLYPH);
            float iconGlyphX = iconPlateX + (iconPlateW - iconGlyphW) * 0.5f;
            float iconGlyphY = iconPlateY + 6.4f;
            iconFont.drawGradientStringHorizontal(
                    matrices,
                    ICON_GLYPH,
                    iconGlyphX,
                    iconGlyphY,
                    ColorUtils.getThemeColor(time),
                    ColorUtils.getThemeColor(time + 120)
            );
        }

        // ── Блок "Evel" ────────────────────────────────────────────────────────
        drawHudBackground(matrices, VolonRectX, VolonRectY, VolonRectW, VolonRectH, iconTop);
        int logoShadow = ColorUtils.applyAlpha(iconTop, 0.32f);
        RenderUtils.drawShadow(matrices, iconX - 1.2f, iconY - 1.35f, iconW + 2.0f, 7.2f, 3, 5f, logoShadow);
        brandFont.drawGradientStringHorizontal(matrices, iconGlyph, iconX, iconY, ColorUtils.getThemeColor(time), ColorUtils.getThemeColor(time + 120));

        // ── Блок username + UID + fps + ms ────────────────────────────────────
        float rect2X = VolonRectX + VolonRectW + 2.5f;
        float rect2H = 15.85f;
        String iconGlyph2 = "e";
        float icon2Y = y + 7.45f;
        String fpsIconGlyph = "p";
        String pingIconGlyph = "y";
        float icon3Y = y + 7.25f;

        int fps = mc != null ? mc.getCurrentFps() : 0;
        String fpsValue = String.valueOf(fps);
        String fpsSuffix = "fps";
        String fpsText = fpsValue + fpsSuffix;

        int ping = 0;
        if (mc != null && mc.player != null && mc.getNetworkHandler() != null) {
            var entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (entry != null) ping = entry.getLatency();
        }
        String pingValue = String.valueOf(ping);
        String pingSuffix = "ms";
        String pingText = pingValue + pingSuffix;

        float contentW = rect2Pad;
        if (showFps) {
            contentW += statsIconFont != null ? statsIconFont.getStringWidth(fpsIconGlyph) + 2f : 0f;
            contentW += suisse13.getStringWidth(fpsText) + 2f;
        }
        if (showMs) {
            contentW += statsIconFont != null ? statsIconFont.getStringWidth(pingIconGlyph) + 2f : 0f;
            contentW += suisse13.getStringWidth(pingText) + 2f;
        }
        if (showTps) {
            String tpsIconGlyph = "b";
            float tpsVal = getServerTps();
            String tpsValueStr = formatOneDecimal(tpsVal);
            String tpsSuffix = "tps";
            String tpsText2 = tpsValueStr + tpsSuffix;
            contentW += statsIconFont != null ? statsIconFont.getStringWidth(tpsIconGlyph) + 2f : 0f;
            contentW += suisse13.getStringWidth(tpsText2) + 2f;
        }
        contentW += rect2Pad;

        float rect2W = contentW - 1.05f;
        drawHudBackground(matrices, rect2X, VolonRectY, rect2W, rect2H, iconTop);

        float drawX = rect2X + rect2Pad + 1.5f;

        if (showFps) {
            if (statsIconFont != null) {
                statsIconFont.drawGradientStringHorizontal(matrices, fpsIconGlyph, drawX, icon3Y, iconTop, iconTop);
                drawX += statsIconFont.getStringWidth(fpsIconGlyph) + 2f;
            }
            suisse13.drawString(matrices, fpsValue, drawX, textY, whiteColor);
            suisse13.drawString(matrices, fpsSuffix, drawX + suisse13.getStringWidth(fpsValue) - 1, textY, iconTop);
            drawX += suisse13.getStringWidth(fpsText) + 2f;
        }

        if (showMs) {
            if (statsIconFont != null) {
                statsIconFont.drawGradientStringHorizontal(matrices, pingIconGlyph, drawX, icon3Y, iconTop, iconTop);
                drawX += statsIconFont.getStringWidth(pingIconGlyph) + 2f;
            }
            suisse13.drawString(matrices, pingValue, drawX, textY, whiteColor);
            suisse13.drawString(matrices, pingSuffix, drawX + suisse13.getStringWidth(pingValue) - 0.5, textY, iconTop);
            drawX += suisse13.getStringWidth(pingText) + 2f;
        }

        if (showTps) {
            String tpsIconGlyph = "b";
            float tpsVal = getServerTps();
            String tpsValueStr = formatOneDecimal(tpsVal);
            String tpsSuffix = "tps";
            String tpsText2 = tpsValueStr + tpsSuffix;
            if (statsIconFont != null) {
                statsIconFont.drawGradientStringHorizontal(matrices, tpsIconGlyph, drawX, icon3Y, iconTop, iconTop);
                drawX += statsIconFont.getStringWidth(tpsIconGlyph) + 2f;
            }
            suisse13.drawString(matrices, tpsValueStr, drawX, textY, whiteColor);
            suisse13.drawString(matrices, tpsSuffix, drawX + suisse13.getStringWidth(tpsValueStr) - 1f, textY, iconTop);
        }

        float row2Y = y + VolonRectH + 2.5f;
        float row2H = 15.85f;
        float row2TextY = row2Y + 6.8f;
        float row2IconY = row2Y + 7.25f;

        Font infoIconFont = Fonts.getFont("icon", 16);
        float bottomRowW = 0f;
        float bottomCurX = x;

        // Убрали BPS и XYZ отсюда - они будут внизу экрана слева

        // IP сервера
        if (showServer) {
            String serverName = "Singleplayer";
            if (mc != null) {
                var info = mc.getCurrentServerEntry();
                if (info != null && info.address != null && !info.address.isEmpty()) {
                    serverName = formatServerNameForDisplay(info.address);
                }
            }
            String serverIconGlyph = "m";
            Font iconNew15local = Fonts.getFont("iconnew", 15);

            float srvContentW = rect2Pad;
            if (iconNew15local != null) srvContentW += iconNew15local.getStringWidth(serverIconGlyph) + 3f;
            srvContentW += suisse13.getStringWidth(serverName) + rect2Pad;
            if (srvContentW < 40f) srvContentW = 40f;

            drawHudBackground(matrices, bottomCurX, row2Y, srvContentW, row2H, iconTop);
            float srvDrawX = bottomCurX + rect2Pad + 1.5f;
            if (iconNew15local != null) {
                iconNew15local.drawGradientStringHorizontal(matrices, serverIconGlyph, srvDrawX - 1f, row2IconY - 0.3f, iconTop, iconTop);
                srvDrawX += iconNew15local.getStringWidth(serverIconGlyph) + 3f;
            }
            drawServerNameWithThemeParts(matrices, serverName, srvDrawX, row2TextY, iconTop, whiteColor);

            bottomRowW += srvContentW;
        }

        // ── Размеры draggable ──────────────────────────────────────────────────
        float topRowW = iconPlateW + iconPlateGap + VolonRectW + 2f + rect2W;
        boolean hasBottomRow = showServerRow;
        float totalW = hasBottomRow ? Math.max(topRowW, bottomRowW) : topRowW;
        float totalH = hasBottomRow ? VolonRectH + 2.5f + row2H : VolonRectH;

        draggable.setWidth(totalW);
        draggable.setHeight(totalH);
        
        // ─────────────────────────────────────────────────────────────────────────
        // НОВОЕ: XYZ и BPS снизу экрана слева
        // ─────────────────────────────────────────────────────────────────────────
        renderBottomLeftInfo(matrices, iconTop, suisse13, infoIconFont);
    }
    
    /**
     * Рендерит UID (в правом нижнем углу), BPS и XYZ в левом нижнем углу экрана
     */
    private void renderBottomLeftInfo(MatrixStack matrices, int themeColor, Font textFont, Font iconFont) {
        if (mc == null || mc.getWindow() == null) return;
        
        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();
        int whiteColor = new Color(255, 255, 255, 255).getRGB();
        
        float leftPad = 4f;
        float rightPad = 4f;
        float bottomPad = 4f;
        float blockHeight = 15.85f;
        float blockGap = 2.5f;
        float textPad = 3f;
        
        // XYZ (ниже всего слева)
        int px = mc.player != null ? (int) Math.floor(mc.player.getX()) : 0;
        int py = mc.player != null ? (int) Math.floor(mc.player.getY()) : 0;
        int pz = mc.player != null ? (int) Math.floor(mc.player.getZ()) : 0;
        String xyzStr = px + " " + py + " " + pz;
        String xyzIconGlyph = "x";
        
        float xyzWidth = textPad;
        if (iconFont != null) xyzWidth += iconFont.getWidth(xyzIconGlyph) + 3f;
        xyzWidth += textFont.getStringWidth(xyzStr) + textPad;
        if (xyzWidth < 40f) xyzWidth = 40f;
        
        float xyzY = screenHeight - bottomPad - blockHeight;
        float xyzX = leftPad;
        
        drawHudBackground(matrices, xyzX, xyzY, xyzWidth, blockHeight, themeColor);
        float xyzDrawX = xyzX + textPad + 1.5f;
        float xyzTextY = xyzY + 6.8f;
        float xyzIconY = xyzY + 7.25f;
        
        if (iconFont != null) {
            iconFont.draw(matrices, xyzIconGlyph, xyzDrawX, xyzIconY - 0.5f, themeColor);
            xyzDrawX += iconFont.getWidth(xyzIconGlyph) + 3f;
        }
        textFont.drawString(matrices, xyzStr, xyzDrawX, xyzTextY, whiteColor);
        
        // BPS (выше XYZ слева)
        double bps = MathUtils.calculateBPS();
        String bpsValue = formatTwoDecimals(bps);
        String bpsSuffix = "bps";
        String bpsText = bpsValue + bpsSuffix;
        String bpsIconGlyph = "c";
        
        float bpsWidth = textPad;
        if (iconFont != null) bpsWidth += iconFont.getWidth(bpsIconGlyph) + 3f;
        bpsWidth += textFont.getStringWidth(bpsText) + textPad;
        if (bpsWidth < 40f) bpsWidth = 40f;
        
        float bpsY = xyzY - blockGap - blockHeight;
        float bpsX = leftPad;
        
        drawHudBackground(matrices, bpsX, bpsY, bpsWidth, blockHeight, themeColor);
        float bpsDrawX = bpsX + textPad + 1.5f;
        float bpsTextY = bpsY + 6.8f;
        float bpsIconY = bpsY + 7.25f;
        
        if (iconFont != null) {
            iconFont.draw(matrices, bpsIconGlyph, bpsDrawX, bpsIconY - 0.5f, themeColor);
            bpsDrawX += iconFont.getWidth(bpsIconGlyph) + 3f;
        }
        textFont.drawString(matrices, bpsValue, bpsDrawX, bpsTextY, whiteColor);
        textFont.drawString(matrices, bpsSuffix, bpsDrawX + textFont.getStringWidth(bpsValue) - 1f, bpsTextY, themeColor);
    }

    public void WaveStyle(EventRender.Default eventRender) {
        float x = draggable.getX(), y = draggable.getY();
        var matrices = eventRender.getContext().getMatrices();
        var waveFont = Fonts.getFont("wave", 30);
        String watermarkText = "Volon";

        int indexColor = ColorUtils.getThemeColor(90);
        int indexColor2 = ColorUtils.getThemeColor(180);
        int indexColor3 = ColorUtils.getThemeColor(270);
        int indexColor4 = ColorUtils.getColor(360);
        float glowWidth = 95.0f + waveFont.getStringWidth("ful");

        RenderUtils.drawShadow(matrices, x, y, glowWidth, 12, 10, 15, indexColor4, indexColor2, indexColor, indexColor3);
        waveFont.drawGradientStringHorizontal(matrices, watermarkText, x, y, indexColor, indexColor2);

        draggable.setWidth(Math.max(glowWidth, waveFont.getStringWidth(watermarkText)));
        draggable.setHeight(12);
    }

    private String formatTwoDecimals(double value) {
        int scaled = (int) Math.round(value * 100.0D);
        int fraction = Math.abs(scaled % 100);
        return (scaled / 100) + "." + (fraction < 10 ? "0" : "") + fraction;
    }

    private void drawServerNameWithThemeParts(MatrixStack matrices, String serverName, float x, float y, int themeColor, int whiteColor) {
        var font = Fonts.getFont("suisse", 13);
        String[] parts = serverName.split("\\.");
        if (parts.length < 2) {
            font.drawString(matrices, serverName, x, y, whiteColor);
            return;
        }

        String mainPart = String.join(".", java.util.Arrays.copyOf(parts, parts.length - 1));
        String suffixPart = "." + parts[parts.length - 1];

        font.drawString(matrices, mainPart, x, y, whiteColor);
        float suffixX = x + font.getStringWidth(mainPart) - 2f;
        font.drawString(matrices, suffixPart, suffixX, y, themeColor);
    }

    private String formatServerNameForDisplay(String serverName) {
        if (serverName == null || serverName.isEmpty()) {
            return "";
        }

        String host = serverName;
        int portIndex = host.indexOf(':');
        if (portIndex > 0) {
            host = host.substring(0, portIndex);
        }

        String[] parts = host.split("\\.");
        if (parts.length >= 3) {
            return String.join(".", java.util.Arrays.copyOfRange(parts, 1, parts.length));
        }
        return host;
    }


    private float getServerTps() {
        if (Volon.INSTANCE == null || Volon.INSTANCE.tpsCalc == null) {
            return 20.0f;
        }
        return Math.max(0.0f, Math.min(20.0f, Volon.INSTANCE.tpsCalc.getTPS()));
    }

    private String formatOneDecimal(float value) {
        int scaled = Math.round(value * 10.0f);
        return (scaled / 10) + "." + Math.abs(scaled % 10);
    }
}
