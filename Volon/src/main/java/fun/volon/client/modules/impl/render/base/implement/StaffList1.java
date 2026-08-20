package fun.volon.client.modules.impl.render.base.implement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import fun.volon.Volon;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.utils.animation.AnimationUtils;
import fun.volon.api.utils.animation.Easings;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.draggable.Draggable;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.api.utils.scissor.ScissorUtils;
import fun.volon.client.modules.impl.render.Interface;

import java.util.List;

public class StaffList1 extends StaffList {

    private Font text(int size) { return Fonts.getFont("suisse", size); }
    private Font icon(int size) { return Fonts.getFont("icon", size); }

    private static final String HEADER_GLYPH = "y";
    private static final String TITLE        = "Staff list";

    private static final float RADIUS       = 4f;
    private static final float PAD_X        = 5f;
    private static final float HEADER_H     = 13f;
    private static final float ROW_H        = 13f;
    private static final float BODY_PAD_TOP = 1f;
    private static final float BODY_PAD_BOT = 2f;
    private static final float ICON_GAP     = 2f;
    private static final float MIN_WIDTH    = 76f;
    private static final int   TEXT_FONT_SIZE = 12;
    private static final int   ICON_FONT_SIZE = 15;

    private static final float TEXT_BASE_Y = 5.3f;
    private static final float ICON_BASE_Y = 5.3f;

    private static final int TEXT_WHITE = ColorUtils.rgba(255, 255, 255, 255);

    private static int alphaFromPercent(float percent) {
        int a = (int) (255f * percent / 100f);
        if (a < 0) return 0;
        if (a > 255) return 255;
        return a;
    }
    private static int bodyBg() {
        return ColorUtils.rgba(7, 7, 9,
                alphaFromPercent(Interface.INSTANCE != null ? Interface.INSTANCE.staffList1BodyAlpha.get() : 57f));
    }
    private static int headerBg() {
        return ColorUtils.rgba(10, 10, 12,
                alphaFromPercent(Interface.INSTANCE != null ? Interface.INSTANCE.staffList1HeaderAlpha.get() : 85f));
    }

    private static int accentColor() {
        if (Volon.INSTANCE == null || Volon.INSTANCE.themeStorage == null) return ColorUtils.getThemeColor();
        var theme = Volon.INSTANCE.themeStorage.getThemes().getTheme();
        if (theme == null) return ColorUtils.getThemeColor();
        if ("Rainbow".equals(theme.getName())) return ColorUtils.getThemeColor();
        return theme.color != null && theme.color.length > 0 ? theme.color[0] : ColorUtils.getThemeColor();
    }

    private final AnimationUtils widthAnim = new AnimationUtils(76, 10.5f, Easings.QUAD_OUT);
    private long lastUpdate = 0;

    public StaffList1(Draggable draggable) {
        super(draggable);
    }

    @Override
    public void onRender(EventRender.Default eventRender) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;

        initFonts();

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdate > 500) {
            updateStaffCache();
            lastUpdate = currentTime;
        }
        updateAnimations();

        MatrixStack m = eventRender.getContext().getMatrices();
        float x = draggable.getX();
        float y = draggable.getY();

        Font hIcon  = icon(ICON_FONT_SIZE);
        Font hTitle = text(TEXT_FONT_SIZE);
        Font rName  = text(TEXT_FONT_SIZE);
        Font rStatus = text(TEXT_FONT_SIZE);

        List<String> visible = getVisiblePlayers();

        float headerGroupW = hIcon.getWidth(HEADER_GLYPH) + ICON_GAP + hTitle.getWidth(TITLE);
        float width = Math.max(MIN_WIDTH, PAD_X + headerGroupW + PAD_X);
        float bodyH = 0f;
        int visibleCount = 0;

        for (String name : visible) {
            float a = staffAnimations.getOrDefault(name, 0f);
            if (a <= 0.01f) continue;
            StaffData d = staffDataCache.get(name);
            if (d == null) continue;
            visibleCount++;

            String status = statusSymbol(d.status);
            float prefixW = 0f;
            for (PrefixSegment seg : d.segments) prefixW += seg.width12;
            float rowW = PAD_X + prefixW + rName.getWidth(name) + 6f + rStatus.getWidth(status) + PAD_X;
            if (rowW > width) width = rowW;
            bodyH += ROW_H * a;
        }

        widthAnim.update(width);
        width = widthAnim.getValue();
        float height = HEADER_H + (visibleCount > 0 ? BODY_PAD_TOP + bodyH + BODY_PAD_BOT : 0f);

        float scaleMul = 1f;
        if (Interface.INSTANCE != null) {
            scaleMul = Interface.INSTANCE.staffList1Scale.get() / 100f;
            if (scaleMul < 0.5f) scaleMul = 0.5f;
            if (scaleMul > 2.0f) scaleMul = 2.0f;
        }

        int bodyBg = bodyBg();
        int headerBg = headerBg();
        int headerAlpha = (headerBg >>> 24) & 0xFF;
        int bodyAlpha   = (bodyBg   >>> 24) & 0xFF;
        boolean shadow  = Interface.INSTANCE != null && Interface.INSTANCE.staffList1Shadow.isState();
        boolean outline = Interface.INSTANCE != null && Interface.INSTANCE.staffList1Outline.isState();
        float panelH = (visibleCount == 0) ? HEADER_H : height;

        m.push();
        m.translate(x, y, 0);
        m.scale(scaleMul, scaleMul, 1f);
        m.translate(-x, -y, 0);

        float scaledWidth = width * scaleMul;
        float scaledPanelH = panelH * scaleMul;

        if (isBlurEnabled()) {
            // blur отключён — вызывает краш
        }
        if (visibleCount == 0) {
            if (headerAlpha > 0) RenderUtils.drawRoundedRect(m, x, y, width, HEADER_H, RADIUS, headerBg);
        } else {
            float bodyY = y + HEADER_H;
            float bodyHeight = height - HEADER_H;
            if (headerAlpha > 0) {
                ScissorUtils.push();
                ScissorUtils.setFromComponentCoordinates(x, y, scaledWidth, HEADER_H * scaleMul);
                RenderUtils.drawRoundedRect(m, x, y, width, HEADER_H + 1f, RADIUS, RADIUS, 0f, 0f, headerBg);
                ScissorUtils.pop();
                ScissorUtils.unset();
            }
            if (bodyAlpha > 0) {
                ScissorUtils.push();
                ScissorUtils.setFromComponentCoordinates(x, bodyY, scaledWidth, bodyHeight * scaleMul);
                RenderUtils.drawRoundedRect(m, x, bodyY - 1f, width, bodyHeight + 1f, 0f, 0f, RADIUS, RADIUS, bodyBg);
                ScissorUtils.pop();
                ScissorUtils.unset();
            }
        }
        if (outline) {
            int outlineColor = ColorUtils.rgba(0, 0, 0, 220);
            RenderUtils.drawRoundedRectOutline(m, x, y, width, panelH, RADIUS, RADIUS, RADIUS, RADIUS, 0.6f, outlineColor);
        }
        if (shadow) {
            float strength = Interface.INSTANCE != null ? Interface.INSTANCE.staffList1ShadowStrength.get() : 70f;
            int sa = (int) (255f * strength / 100f);
            if (sa < 0) sa = 0; if (sa > 255) sa = 255;
            RenderUtils.drawShadowOuter(m, x, y, width, panelH, RADIUS, 10f, ColorUtils.rgba(0, 0, 0, sa));
        }

        float gx = x + (width - headerGroupW) / 2f;
        hIcon.drawStringWithShadow(m, HEADER_GLYPH, gx, y + ICON_BASE_Y, accentColor());
        hTitle.drawStringWithShadow(m, TITLE,
                gx + hIcon.getWidth(HEADER_GLYPH) + ICON_GAP, y + TEXT_BASE_Y, TEXT_WHITE);

        if (visibleCount > 0) {
            ScissorUtils.push();
            ScissorUtils.setFromComponentCoordinates(x, y, scaledWidth, height * scaleMul);

            float rowY = y + HEADER_H + BODY_PAD_TOP;
            for (String name : visible) {
                float a = staffAnimations.getOrDefault(name, 0f);
                if (a <= 0.01f) continue;
                StaffData d = staffDataCache.get(name);
                if (d == null) continue;

                int alpha = (int) (255 * a);
                int textBaseColor = ColorUtils.setAlphaColor(TEXT_WHITE, alpha);
                int statusColor = ColorUtils.setAlphaColor(getStatusColor(d.status), alpha);
                float textBaseY = rowY + TEXT_BASE_Y;

                float curX = x + PAD_X;
                for (PrefixSegment seg : d.segments) {
                    int c = ColorUtils.setAlphaColor(0xFF000000 | (seg.color & 0xFFFFFF), alpha);
                    rName.drawStringWithShadow(m, seg.text, curX, textBaseY, c);
                    curX += seg.width12;
                }
                rName.drawStringWithShadow(m, name, curX, textBaseY, textBaseColor);

                String status = statusSymbol(d.status);
                float statusX = x + width - PAD_X - rStatus.getWidth(status);
                rStatus.drawStringWithShadow(m, status, statusX, textBaseY, statusColor);

                rowY += ROW_H * a;
            }
            ScissorUtils.pop();
            ScissorUtils.unset();
        }

        m.pop();

        draggable.setWidth(scaledWidth);
        draggable.setHeight(height * scaleMul);
    }

    private static String statusSymbol(String status) {
        return switch (status) {
            case "VANISH" -> "-";
            case "GM3"    -> "±";
            default        -> "+";
        };
    }
}
