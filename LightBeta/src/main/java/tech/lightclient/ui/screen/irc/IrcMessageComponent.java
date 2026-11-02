package tech.lightclient.ui.screen.irc;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.utils.render.color.basic.ColorUtils;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontObject;
import tech.lightclient.client.utils.render.main.text.FontRegistry;

import java.awt.*;

public class IrcMessageComponent {
    private static final float PADDING_X = 12f;
    private static final float PADDING_Y = 8f;
    private static final float SENDER_SIZE = 13f;
    private static final float CONTENT_SIZE = 12f;
    private static final float TIME_SIZE = 10f;
    private static final float ROUNDING = 6f;
    private static final float SPACING = 4f;

    private final IrcChatHistory.IrcMessage message;
    private float height;
    private float width;

    public IrcMessageComponent(IrcChatHistory.IrcMessage message, float maxWidth) {
        this.message = message;
        this.width = maxWidth;
        calculateHeight();
    }

    private void calculateHeight() {
        FontObject font = getFontObject();

        float senderHeight = font.getLineHeight(SENDER_SIZE);

        float contentHeight = font.getLineHeight(CONTENT_SIZE);

        float timeHeight = font.getLineHeight(TIME_SIZE);
        
        this.height = PADDING_Y * 2 + senderHeight + SPACING + contentHeight + SPACING + timeHeight;
    }

    public void draw(Renderer2D renderer, float x, float y, float alpha) {
        FontObject font = getFontObject();

        int bgColor = ClientColors.applyAlpha(
            ColorUtils.darkenWithAlpha(ClientColors.BACKGROUND.getRGB(), 0.3f),
            alpha
        );
        renderer.rect(x, y, width, height, ROUNDING, bgColor);

        int outlineColor = ClientColors.applyAlpha(Color.WHITE.getRGB(), 0.08f * alpha);
        renderer.rectOutline(x, y, width, height, ROUNDING, outlineColor, 1f);
        
        float currentY = y + PADDING_Y;

        String senderText = message.getSender();
        int senderColor = ClientColors.applyAlpha(ClientColors.ICON.getRGB(), alpha);
        float senderY = currentY + FontRegistry.centeredBaselineOffset(font, 'H', SENDER_SIZE);
        renderer.text(font, x + PADDING_X, senderY, SENDER_SIZE, senderText, senderColor);
        currentY += font.getLineHeight(SENDER_SIZE) + SPACING;

        String contentText = message.getContent();
        int contentColor = ClientColors.applyAlpha(ClientColors.TEXT.getRGB(), alpha);
        float contentY = currentY + FontRegistry.centeredBaselineOffset(font, 'H', CONTENT_SIZE);
        renderer.text(font, x + PADDING_X, contentY, CONTENT_SIZE, contentText, contentColor);
        currentY += font.getLineHeight(CONTENT_SIZE) + SPACING;

        String timeText = message.getFormattedTime();
        int timeColor = ClientColors.applyAlpha(Color.GRAY.getRGB(), alpha * 0.7f);
        float timeY = currentY + FontRegistry.centeredBaselineOffset(font, 'H', TIME_SIZE);
        renderer.text(font, x + PADDING_X, timeY, TIME_SIZE, timeText, timeColor);
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    private FontObject getFontObject() {
        FontObject font = FontRegistry.SF_MEDIUM;
        if (font == null) {
            font = FontRegistry.INTER;
        }
        return font;
    }
}
