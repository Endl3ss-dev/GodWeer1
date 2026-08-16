package never.lose.screen.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import never.lose.ui.object.RenderObject;
import never.lose.utils.render.Render;
import never.lose.managers.ThemeManager;
import never.lose.utils.color.ClientColors;
import never.lose.utils.math.MathUtil;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class PlayerCard extends RenderObject {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    private static final Color HEAD_STROKE = new Color(255, 255, 255, 25);
    private static final float HEAD_SIZE = 20f;
    private static final float HEAD_RADIUS = 6f;

    private final Runnable onPress;
    private float hoverAnim = 0f;
    private float opacity = 1f;
    private String cachedName = "Player";
    private String cachedDate = "";

    private PlayerCard(Runnable onPress) {
        this.onPress = onPress;
    }

    public static PlayerCard build(float x, float y, float w, float h, Runnable onPress) {
        PlayerCard card = new PlayerCard(onPress);
        card.x = x;
        card.y = y;
        card.width = w;
        card.height = h;
        return card;
    }

    public void updateInfo() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null && mc.getSession() != null) {
            cachedName = mc.getSession().getUsername();
        }
        cachedDate = LocalDate.now().format(DATE_FMT);
    }

    public void setOpacity(float opacity) {
        this.opacity = Math.max(0f, Math.min(1f, opacity));
    }

    private Color faded(Color c) {
        return Render.injectAlpha(c, c.getAlpha() * opacity);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        boolean hovered = MathUtil.isInPosition(x, y, width, height, mouseX, mouseY);
        hoverAnim = MathUtil.fastAnim(hoverAnim, hovered ? 1f : 0f, 10f);

        Color bg = faded(ThemeManager.interpolateColor(
                new Color(255, 255, 255, 6),
                new Color(255, 255, 255, 18), hoverAnim));
        Color stroke = faded(ThemeManager.interpolateColor(
                new Color(255, 255, 255, 12),
                new Color(255, 255, 255, 35), hoverAnim));

        Color blurColor = Render.injectAlpha(Color.WHITE, 255f * opacity);
        Render.blur(x, y, width, height, 6, 6, 6, 6, 1, 16,
                blurColor, blurColor, blurColor, blurColor);
        Render.outline(x, y, width, height, 6, 6, 6, 6, 0.6f, 0f,
                bg, bg, bg, bg, stroke, stroke, stroke, stroke);

        float textX = x + 33f;
        float nameW = Render.textWidth(cachedName, "sfbold", 9);
        Render.textCentered(cachedName, textX + nameW / 2f, y + 8f,
                "sfbold", 9, faded(ClientColors.FORE_COLOR));

        float dateW = Render.textWidth(cachedDate, "sfmedium", 7);
        Render.textCentered(cachedDate, textX + dateW / 2f, y + 20f,
                "sfmedium", 7, faded(new Color(160, 160, 160, 180)));
    }

    public void renderHead() {
        UUID uuid = UUID.nameUUIDFromBytes(
                ("OfflinePlayer:" + cachedName).getBytes(StandardCharsets.UTF_8));

        Identifier skinId = DefaultSkinHelper.getSkinTextures(uuid).texture();
        AbstractTexture skin = MinecraftClient.getInstance().getTextureManager().getTexture(skinId);

        float hx = x + 7f;
        float hy = y + 7f;

        Color tex = Render.injectAlpha(Color.WHITE, 255f * opacity);

        // лицо (8..16) и слой шлема (40..48) режутся по радиусу в texture.fsh
        Render.texture(skin, hx, hy, HEAD_SIZE, HEAD_SIZE,
                8f / 64f, 8f / 64f, 8f / 64f, 8f / 64f,
                HEAD_RADIUS, HEAD_RADIUS, HEAD_RADIUS, HEAD_RADIUS,
                tex, tex, tex, tex);
        Render.texture(skin, hx, hy, HEAD_SIZE, HEAD_SIZE,
                40f / 64f, 8f / 64f, 8f / 64f, 8f / 64f,
                HEAD_RADIUS, HEAD_RADIUS, HEAD_RADIUS, HEAD_RADIUS,
                tex, tex, tex, tex);

        Color stroke = faded(HEAD_STROKE);
        Render.outline(hx, hy, HEAD_SIZE, HEAD_SIZE,
                HEAD_RADIUS, HEAD_RADIUS, HEAD_RADIUS, HEAD_RADIUS, 0.6f, 0f,
                TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT,
                stroke, stroke, stroke, stroke);
    }

    public boolean click(int mouseX, int mouseY, int button) {
        if (onPress != null && opacity > 0.99f
                && MathUtil.isInPosition(x, y, width, height, mouseX, mouseY)) {
            onPress.run();
        }
        return false;
    }
}