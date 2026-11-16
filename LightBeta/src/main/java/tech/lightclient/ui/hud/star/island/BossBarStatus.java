package tech.lightclient.ui.hud.star.island;

import java.util.Map;
import java.util.UUID;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.entity.boss.BossBar;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.mixins.accessors.BossBarHudAccessor;
import tech.lightclient.ui.hud.CustomBossBarHud;
import tech.lightclient.ui.hud.star.StarUtil;

/**
 * Статус динамического острова, поглощающий серверный боссбар.
 * Боссбар не рисуется в привычном виде — его текст выводится в острове,
 * а слева показывается таймер-кругляш, цвет которого совпадает с цветом боссбара.
 */
public class BossBarStatus extends IslandStatus {
    /** Прогресс боссбара берётся напрямую из серверного процента, поэтому
     *  время, прошедшее за показ уведомления, вычитается автоматически. */
    private int glowColor = 0xFF6AA9FF;

    public BossBarStatus() {
        super("bossbar", "Боссбар");
    }

    private static ClientBossBar activeBossBar() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.inGameHud == null) {
            return null;
        }
        BossBarHud hud = mc.inGameHud.getBossBarHud();
        if (hud == null) {
            return null;
        }
        Map<UUID, ClientBossBar> bars = ((BossBarHudAccessor) hud).getBossBars();
        if (bars == null || bars.isEmpty()) {
            return null;
        }
        for (ClientBossBar bar : bars.values()) {
            if (bar != null) {
                return bar;
            }
        }
        return null;
    }

    /** Прогресс таймера [0, 1] — доля оставшегося времени боссбара. */
    private static float progressOf(ClientBossBar bar) {
        return Math.max(0.0f, Math.min(1.0f, bar.getPercent()));
    }

    @Override
    public void draw(EventRender.Screen.Hud event, Renderer2D r, IslandElement island, float x, float y, float alpha) {
        ClientBossBar bar = activeBossBar();
        if (bar == null) {
            return;
        }

        String text = bar.getName().getString();
        int color = resolveColor(bar.getColor());
        glowColor = color;

        width = 20.0f + FontRegistry.DELTA_HUD.getWidth(text, 7.0f);
        height = 15.0f;

        float shift = 10.0f * animation.getValue();
        float cx = x - 6.0f + shift + 3.5f;
        float cy = y + 4.0f + 3.5f;

        drawGlow(r, cx, cy, color, alpha);
        // Таймер тает против часовой стрелки от верхней точки, как у уведомлений.
        r.circleOutline(cx, cy, 3.5f, -90.0f, progressOf(bar),
                ClientColors.applyAlpha(color, alpha), 1.0f);

        r.text(FontRegistry.DELTA_HUD, x + 25.0f - shift,
                StarUtil.textBaseline(FontRegistry.DELTA_HUD, 'H', y + 1.5f, 12.0f, 7.0f),
                7.0f, text, StarUtil.text(alpha));
    }

    private void drawGlow(Renderer2D r, float cx, float cy, int color, float alpha) {
        float size = 15.0f;
        r.drawTexture(StarUtil.id("images/etc/glow.png"),
                cx - size / 2.0f, cy - size / 2.0f, size, size,
                ClientColors.applyAlpha(color, 0.35f * alpha));
    }

    private static int resolveColor(BossBar.Color color) {
        return switch (color) {
            case PINK -> 0xFFF26DCC;
            case BLUE -> 0xFF6AA9FF;
            case RED -> 0xFFFF5E5E;
            case GREEN -> 0xFF69E087;
            case YELLOW -> 0xFFFFD65C;
            case PURPLE -> 0xFFC18BFF;
            case WHITE -> 0xFFF3F3F3;
        };
    }

    @Override
    public int getColorArgb() {
        return glowColor;
    }

    @Override
    public boolean canShow() {
        return CustomBossBarHud.shouldUseCustomBossBar() && activeBossBar() != null;
    }
}
