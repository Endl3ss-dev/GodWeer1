package antileak.base.client.modules.impl.render.base.implement;

import net.minecraft.client.util.math.MatrixStack;
import antileak.base.Punch;
import antileak.base.api.events.implement.EventRender;
import antileak.base.api.utils.color.ColorUtils;
import antileak.base.api.utils.draggable.Draggable;
import antileak.base.api.utils.math.MathUtils;
import antileak.base.api.utils.render.RenderUtils;
import antileak.base.api.utils.render.fonts.msdf.Font;
import antileak.base.api.utils.render.fonts.msdf.Fonts;
import antileak.base.api.utils.render.fonts.ttf.MCFontRenderer;
import antileak.base.client.modules.impl.render.base.InterfaceProcessing;

public class WaterMarkV2 extends InterfaceProcessing {

    private static final int WHITE_COLOR  = ColorUtils.rgba(255, 255, 255, 255);

    private static final float BAR_H       = 17f;
    private static final float H_PAD       = 5f;
    private static final float H_PAD_RIGHT = 2f;
    private static final float ELEMENT_GAP = 7f;
    private static final float SEP_GAP     = 2f;
    private static final float PILL_GAP    = 4f;
    private static final float ROW_GAP     = 3f;

    private boolean showFps    = true;
    private boolean showMs     = true;
    private boolean showServer = true;
    private boolean showCoords = true;
    private boolean showBps    = true;

    private static String storedUsername = "";
    private static net.minecraft.util.Identifier discordAvatarIdentifier = null;
    private static boolean avatarFetching = false;
    private static String lastAvatarId = null;

    private static void fetchDiscordAvatar(String userId, String avatarHash) {
        if (avatarFetching || avatarHash == null) return;
        avatarFetching = true;
        new Thread(() -> {
            try {
                String urlStr = "https://cdn.discordapp.com/avatars/" + userId + "/" + avatarHash + ".png?size=64";
                try (java.io.InputStream in = new java.net.URL(urlStr).openStream()) {
                    net.minecraft.client.texture.NativeImage img = net.minecraft.client.texture.NativeImage.read(in);
                    net.minecraft.client.MinecraftClient.getInstance().execute(() -> {
                        net.minecraft.client.texture.NativeImageBackedTexture tex = new net.minecraft.client.texture.NativeImageBackedTexture(img);
                        net.minecraft.util.Identifier id = net.minecraft.util.Identifier.of("punch", "discord_avatar_" + avatarHash);
                        net.minecraft.client.MinecraftClient.getInstance().getTextureManager().registerTexture(id, tex);
                        discordAvatarIdentifier = id;
                    });
                }
            } catch (Exception e) {
                // Ignore download errors
            } finally {
                avatarFetching = false;
            }
        }).start();
    }

    public WaterMarkV2(Draggable draggable) { super(draggable); }

    public static void setDiscordUser(String userId, String username, String avatar) {
        storedUsername = username;
        if (avatar != null && !avatar.equals(lastAvatarId)) {
            lastAvatarId = avatar;
            discordAvatarIdentifier = null;
            avatarFetching = false;
            fetchDiscordAvatar(userId, avatar);
        }
    }

    public static String getUsername() { return storedUsername; }

    public boolean isShowFps()    { return showFps; }
    public void setShowFps(boolean v)    { this.showFps = v; }
    public boolean isShowMs()     { return showMs; }
    public void setShowMs(boolean v)     { this.showMs = v; }
    public boolean isShowServer() { return showServer; }
    public void setShowServer(boolean v) { this.showServer = v; }
    public boolean isShowCoords() { return showCoords; }
    public void setShowCoords(boolean v) { this.showCoords = v; }
    public boolean isShowBps()    { return showBps; }
    public void setShowBps(boolean v)    { this.showBps = v; }

    @Override
    public void onRender(EventRender.Default eventRender) {
        renderStyle(eventRender);
        super.onRender(eventRender);
    }

    private void renderStyle(EventRender.Default event) {
        var matrices = event.getContext().getMatrices();
        float x = draggable.getX();
        float y = draggable.getY();

        float barH = 19f;
        float hPad = 6f;
        float pillGap = 5f;
        float rowGap = 5f;

        var textFont = Fonts.getFont("suisse_bold", 13);
        if (textFont == null) textFont = Fonts.getFont("suisse", 13);
        var nameFont = Fonts.getFont("suisse", 13);
        if (nameFont == null) nameFont = textFont;
        
        var smallFont = Fonts.getFont("suisse", 10);
        if (smallFont == null) smallFont = Fonts.getFont("suisse", 13);
        var logoFont = Fonts.getFont("logo", 17);
        var iconNew14 = Fonts.getFont("iconnew", 14);
        var myfont14 = antileak.base.api.utils.render.fonts.ttf.Fonts.getFont("myfont.ttf", 14f);
        var myfont15 = antileak.base.api.utils.render.fonts.ttf.Fonts.getFont("myfont.ttf", 15f);
        var myfont16 = antileak.base.api.utils.render.fonts.ttf.Fonts.getFont("myfont.ttf", 16f);

        int themeColor = getThemeColor();
        int whiteColor = WHITE_COLOR;
        int grayColor = ColorUtils.rgba(170, 170, 170, 255);
        int sepColor = ColorUtils.rgba(80, 80, 80, 255);
        int bgColor = ColorUtils.rgba(15, 15, 15, 230);
        int shadowColor = ColorUtils.setAlphaColor(themeColor, 80);

        String brandText = "Punch";
        String username  = getDisplayUsername().trim();

        int fps = mc != null ? mc.getCurrentFps() : 0;
        String fpsText = String.valueOf(fps);

        int ping = 0;
        if (mc != null && mc.player != null && mc.getNetworkHandler() != null) {
            var entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (entry != null) ping = entry.getLatency();
        }
        String pingText = String.valueOf(ping);

        String serverIp = "deluxe.spookytime.net";
        if (mc != null && mc.getCurrentServerEntry() != null) {
            serverIp = mc.getCurrentServerEntry().address;
        }

        String coordsText = "0, 0, 0";
        if (mc != null && mc.player != null) {
            int px = (int) Math.floor(mc.player.getX());
            int py = (int) Math.floor(mc.player.getY());
            int pz = (int) Math.floor(mc.player.getZ());
            coordsText = px + ", " + py + ", " + pz;
        }

        double bps = (mc != null && mc.player != null) ? MathUtils.calculateBPS() : 0;
        String bpsText = formatOneDecimal((float) bps);

        // --- Calculate Widths ---
        float brandIconW = logoFont != null ? logoFont.getStringWidth("p") : 0f;
        float brandW = hPad + brandIconW + (logoFont != null ? 3f : 0f) + textFont.getStringWidth(brandText) + hPad;

        float infoW = hPad;
        boolean firstInfo = true;
        
        if (!username.isEmpty()) {
            infoW += 10f + 4f + nameFont.getStringWidth(username);
            firstInfo = false;
        }
        
        if (showFps) {
            if (!firstInfo) infoW += 3f + textFont.getStringWidth("|") + 3f;
            float iw = myfont14 != null ? myfont14.getStringWidth("f") : 0f;
            infoW += iw + 2f + textFont.getStringWidth(fpsText) + 1f + smallFont.getStringWidth("FPS");
            firstInfo = false;
        }
        
        if (showMs) {
            if (!firstInfo) infoW += 3f + textFont.getStringWidth("|") + 3f;
            float iw = iconNew14 != null ? iconNew14.getStringWidth("m") : 0f;
            infoW += iw + 2f + textFont.getStringWidth(pingText) + 1f + smallFont.getStringWidth("ms");
            firstInfo = false;
        }
        infoW += hPad;

        boolean hasRow2 = showServer || showCoords || showBps;
        float row2W = 0f;
        
        if (showServer) {
            row2W += hPad + textFont.getStringWidth(serverIp) + hPad;
        }
        
        float coordsW = 0f;
        if (showCoords) {
            float iw = myfont15 != null ? myfont15.getStringWidth("j") : 0f;
            coordsW += hPad + iw + 3f + textFont.getStringWidth(coordsText) + hPad;
        }
        
        float bpsW = 0f;
        if (showBps) {
            float iw = myfont16 != null ? myfont16.getStringWidth("i") : 0f;
            bpsW += hPad + iw + 3f + textFont.getStringWidth(bpsText) + 1f + smallFont.getStringWidth("BPS") + hPad;
        }

        // Layout calculation
        float totalRow1W = brandW + pillGap + infoW;
        
        float row2TotalW = row2W;
        if (showCoords) row2TotalW += (row2TotalW > 0 ? pillGap : 0) + coordsW;
        if (showBps) row2TotalW += (row2TotalW > 0 ? pillGap : 0) + bpsW;
        
        float totalW = Math.max(totalRow1W, row2TotalW);
        float totalH = barH + (hasRow2 ? rowGap + barH : 0f);

        // --- Render Brand Pill ---
        drawCustomBar(matrices, x, y, brandW, barH, bgColor, shadowColor);
        float bx = x + hPad;
        if (logoFont != null) {
            logoFont.drawString(matrices, "p", bx, y + 6f, themeColor);
            bx += brandIconW + 3f;
        }
        textFont.drawString(matrices, brandText, bx, y + 8f, themeColor);

        // --- Render Info Pill ---
        float ix = x + brandW + pillGap;
        drawCustomBar(matrices, ix, y, infoW, barH, bgColor, shadowColor);
        float cx = ix + hPad;
        boolean drawnInfo = false;

        if (!username.isEmpty()) {
            if (discordAvatarIdentifier != null) {
                RenderUtils.drawRoundedTexture(matrices, discordAvatarIdentifier, cx, y + 4.5f, 10f, 10f, 2f);
            } else {
                RenderUtils.drawPlayerHead(matrices, mc != null && mc.player != null ? mc.player.getName().getString() : username, cx, y + 4.5f, 10f, 2f);
            }
            cx += 10f + 4f;
            nameFont.drawString(matrices, username, cx, y + 8f, whiteColor);
            cx += nameFont.getStringWidth(username);
            drawnInfo = true;
        }

        if (showFps) {
            if (drawnInfo) {
                cx += 3f;
                textFont.drawString(matrices, "|", cx, y + 8f, sepColor);
                cx += textFont.getStringWidth("|") + 3f;
            }
            if (myfont14 != null) {
                myfont14.drawString("f", cx, y + 8.5f, themeColor);
                cx += myfont14.getStringWidth("f") + 2f;
            }
            textFont.drawString(matrices, fpsText, cx, y + 8f, whiteColor);
            cx += textFont.getStringWidth(fpsText) + 1f;
            smallFont.drawString(matrices, "FPS", cx, y + 9.5f, grayColor);
            cx += smallFont.getStringWidth("FPS");
            drawnInfo = true;
        }

        if (showMs) {
            if (drawnInfo) {
                cx += 3f;
                textFont.drawString(matrices, "|", cx, y + 8f, sepColor);
                cx += textFont.getStringWidth("|") + 3f;
            }
            if (iconNew14 != null) {
                iconNew14.drawString(matrices, "m", cx, y + 8.5f, themeColor);
                cx += iconNew14.getStringWidth("m") + 2f;
            }
            textFont.drawString(matrices, pingText, cx, y + 8f, whiteColor);
            cx += textFont.getStringWidth(pingText) + 1f;
            smallFont.drawString(matrices, "ms", cx, y + 9.5f, grayColor);
            cx += smallFont.getStringWidth("ms");
        }

        // --- Render Row 2 ---
        if (hasRow2) {
            float y2 = y + barH + rowGap;
            float r2x = x;
            
            if (showServer) {
                drawCustomBar(matrices, r2x, y2, row2W, barH, bgColor, shadowColor);
                float sx = r2x + hPad;
                textFont.drawString(matrices, serverIp, sx, y2 + 8f, whiteColor);
                r2x += row2W + pillGap;
            }
            
            if (showCoords) {
                drawCustomBar(matrices, r2x, y2, coordsW, barH, bgColor, shadowColor);
                float cx2 = r2x + hPad;
                if (myfont15 != null) {
                    myfont15.drawString("j", cx2, y2 + 8f, themeColor);
                    cx2 += myfont15.getStringWidth("j") + 3f;
                }
                textFont.drawString(matrices, coordsText, cx2, y2 + 8f, whiteColor);
                r2x += coordsW + pillGap;
            }
            
            if (showBps) {
                drawCustomBar(matrices, r2x, y2, bpsW, barH, bgColor, shadowColor);
                float bx2 = r2x + hPad;
                if (myfont16 != null) {
                    myfont16.drawString("i", bx2, y2 + 8f, themeColor);
                    bx2 += myfont16.getStringWidth("i") + 3f;
                }
                textFont.drawString(matrices, bpsText, bx2, y2 + 8f, whiteColor);
                bx2 += textFont.getStringWidth(bpsText) + 1f;
                smallFont.drawString(matrices, "BPS", bx2, y2 + 9.5f, grayColor);
            }
        }

        draggable.setWidth(totalW);
        draggable.setHeight(totalH);
    }

    private void drawCustomBar(MatrixStack matrices, float x, float y, float w, float h, int bgColor, int shadowColor) {
        RenderUtils.drawShadow(matrices, x, y, w, h, 6f, 12f, shadowColor);
        RenderUtils.drawRoundedRect(matrices, x, y, w, h, 6f, bgColor);
    }

    private int getThemeColor() {
        if (Punch.INSTANCE != null && Punch.INSTANCE.themeStorage != null) {
            var theme = Punch.INSTANCE.themeStorage.getThemes().getTheme();
            if (!theme.getName().equals("Rainbow")) {
                return theme.color[0];
            }
        }
        return antileak.base.api.utils.color.ColorUtils.getThemeColor();
    }

    private String getDisplayUsername() {
        if (storedUsername != null && !storedUsername.isEmpty()) return storedUsername;
        if (mc != null && mc.player != null) return mc.player.getName().getString();
        return "";
    }

    private String formatOneDecimal(float value) {
        int scaled = Math.round(value * 10f);
        return (scaled / 10) + "." + Math.abs(scaled % 10);
    }
}
