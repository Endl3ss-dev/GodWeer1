package tech.lightclient.ui.hud;

import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.client.IMinecraft;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.render.animations.impl.SimpleLinearAnimation;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.impl.render.Interface;
import net.minecraft.client.gui.screen.ChatScreen;

public class ChatButtonsOverlay implements IMinecraft {
    public static final String HIDE_LABEL = "Скрыть информацию";
    public static final String RESET_LABEL = "Сбросить позиции";

    private static final float TEXT_SIZE = 9;
    private static final float ROUNDING = 5f;

    private final SimpleLinearAnimation hideAnim = new SimpleLinearAnimation(180);

    @EventHandler
    public void onRender(EventRender.Screen.Gui event) {
        if (!(mc.currentScreen instanceof ChatScreen) || LightClient.isHide()) {
            return;
        }

        int hideW = ChatOverlayState.hideBtnW;
        int hideH = ChatOverlayState.hideBtnH;
        int resetW = ChatOverlayState.resetBtnW;
        int resetH = ChatOverlayState.resetBtnH;
        if (hideW <= 0 || hideH <= 0 || resetW <= 0 || resetH <= 0) {
            return;
        }

        float hideXf = ChatOverlayState.hideBtnX;
        float hideYf = ChatOverlayState.hideBtnY;
        float hideWf = hideW;
        float hideHf = hideH;
        float resetXf = ChatOverlayState.resetBtnX;
        float resetYf = ChatOverlayState.resetBtnY;
        float resetWf = resetW;
        float resetHf = resetH;

        float scale = (float) mc.getWindow().getScaleFactor();
        float textSize = TEXT_SIZE * scale;
        float rounding = Interface.getHudRounding(ROUNDING * scale);

        if (ChatOverlayState.hideChatInfo) {
            hideAnim.show();
        } else {
            hideAnim.hide();
        }
        float p = hideAnim.getProgress();

        Renderer2D render = event.getRenderer();
        int hideBg = lerpColor(0xFF0F0F0F, 0xFF1B1B1B, p);
        int hideText = lerpColor(0xFFC8C8C8, 0xFFFFFFFF, p);
        render.rect(hideXf, hideYf, hideWf, hideHf, rounding, hideBg);
        render.rect(resetXf, resetYf, resetWf, resetHf, rounding, 0xFF0F0F0F);

        var hideMetrics = render.measureText(FontRegistry.DELTA_HUD, HIDE_LABEL, textSize);
        var resetMetrics = render.measureText(FontRegistry.DELTA_HUD, RESET_LABEL, textSize);
        float hideTextX = hideXf + (hideWf - hideMetrics.width) * 0.5f;
        float hideTextY = centeredTextY(hideYf, hideHf, textSize);
        float resetTextX = resetXf + (resetWf - resetMetrics.width) * 0.5f;
        float resetTextY = centeredTextY(resetYf, resetHf, textSize);
        render.text(FontRegistry.DELTA_HUD, hideTextX, hideTextY, textSize, HIDE_LABEL, hideText);
        render.text(FontRegistry.DELTA_HUD, resetTextX, resetTextY, textSize, RESET_LABEL, 0xFFFFFFFF);

        if (ChatOverlayState.barVisible) {
            render.rect(ChatOverlayState.barX, ChatOverlayState.barY,
                    ChatOverlayState.barW, ChatOverlayState.barH, 0, 0xFF000000);
        }

        HudSettingsOverlay.render(event);
    }

    private static float centeredTextY(float y, float height, float size) {
        float offset = FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_HUD, 'H', size);
        return y + height * 0.5f + offset;
    }

    private static int lerpColor(int from, int to, float t) {
        float clamped = Math.max(0f, Math.min(1f, t));
        int a0 = (from >>> 24) & 0xFF;
        int r0 = (from >>> 16) & 0xFF;
        int g0 = (from >>> 8) & 0xFF;
        int b0 = from & 0xFF;
        int a1 = (to >>> 24) & 0xFF;
        int r1 = (to >>> 16) & 0xFF;
        int g1 = (to >>> 8) & 0xFF;
        int b1 = to & 0xFF;
        int a = Math.round(a0 + (a1 - a0) * clamped);
        int r = Math.round(r0 + (r1 - r0) * clamped);
        int g = Math.round(g0 + (g1 - g0) * clamped);
        int b = Math.round(b0 + (b1 - b0) * clamped);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
