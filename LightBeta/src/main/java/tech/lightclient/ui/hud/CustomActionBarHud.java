package tech.lightclient.ui.hud;

import tech.lightclient.LightClient;
import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontObject;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.mixins.accessors.InGameHudAccessor;
import tech.lightclient.modules.api.FunctionManager;
import tech.lightclient.modules.impl.render.Interface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class CustomActionBarHud {
    public static final String ELEMENT_NAME = "Кастом экшонбар";
    private static final float TEXT_SIZE = 18;
    private static final float BOTTOM_MARGIN = 128f;

    private CustomActionBarHud() {
    }

    public static boolean shouldUseCustomActionBar() {
        FunctionManager manager = LightClient.getFunctionManager();
        if (manager == null || manager.getAnInterface() == null || !manager.getAnInterface().isState()) {
            return false;
        }
        return Interface.elements != null
                && Interface.elements.getByName(ELEMENT_NAME) != null
                && Interface.elements.getByName(ELEMENT_NAME).get();
    }

    public static void render(EventRender.Screen.Hud event) {
        if (!shouldUseCustomActionBar()) {
            return;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.inGameHud == null || mc.player == null || mc.world == null || mc.options.hudHidden) {
            return;
        }

        InGameHudAccessor accessor = (InGameHudAccessor) mc.inGameHud;
        Text message = accessor.getOverlayMessage();
        int remaining = accessor.getOverlayRemaining();
        if (message == null || remaining <= 0) {
            return;
        }

        FontObject font = FontRegistry.DELTA_HUD != null ? FontRegistry.DELTA_HUD : FontRegistry.DELTA_HUD;
        if (font == null) {
            return;
        }

        Renderer2D renderer = event.getRenderer();
        float alpha = Math.max(0f, Math.min(1f, remaining / 20f));
        float scale = Math.max(0.01f, Interface.getInterfaceScale());
        float viewportWidth = event.getViewportWidth() / scale;
        float viewportHeight = event.getViewportHeight() / scale;
        boolean chatOpen = mc.currentScreen instanceof ChatScreen;

        float textWidth = renderer.measureText(font, message, TEXT_SIZE).width;
        float x = (viewportWidth - textWidth) * 0.5f;
        float y = viewportHeight - BOTTOM_MARGIN - (chatOpen ? 14f : 0f);
        int textColor = accessor.isOverlayTinted()
                ? ClientColors.applyAlpha(Formatting.RED.getColorValue() == null ? ClientColors.TEXT.getRGB() : Formatting.RED.getColorValue(), alpha)
                : ClientColors.applyAlpha(ClientColors.TEXT.getRGB(), alpha);

        float textY = y + FontRegistry.centeredBaselineOffset(font, 'H', TEXT_SIZE);
        renderer.text(font, x, textY, TEXT_SIZE, message, textColor);
    }
}
