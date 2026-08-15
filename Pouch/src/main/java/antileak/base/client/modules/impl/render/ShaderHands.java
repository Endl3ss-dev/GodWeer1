package antileak.base.client.modules.impl.render;

import antileak.base.api.events.EventLink;
import antileak.base.api.events.Priority;
import antileak.base.api.events.implement.EventRender;
import antileak.base.api.utils.render.hands.ShaderHandsRenderer;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.BooleanSetting;
import antileak.base.client.modules.settings.implement.FloatSetting;
import antileak.base.client.modules.settings.implement.ModeSetting;

public class ShaderHands extends Module {

    public static ShaderHands INSTANCE = new ShaderHands();
    private static final ShaderHandsRenderer RENDERER = ShaderHandsRenderer.getInstance();

    public final ModeSetting mode = new ModeSetting("Режим", "Свечение", "Свечение", "Красивый", "Аврора", "Дым");

    public final FloatSetting waveSpeed = new FloatSetting("Скорость волн", 1.2f, 0.1f, 5.0f, 0.1f)
            .visible(() -> mode.is("Красивый"));
    public final FloatSetting waveScale = new FloatSetting("Частота волн", 1.0f, 1.0f, 3.0f, 0.1f)
            .visible(() -> mode.is("Красивый"));

    public final FloatSetting outline = new FloatSetting("Ширина обводки", 1.2f, 0.1f, 5.0f, 0.1f)
            .visible(() -> !mode.is("Дым"));
    public final FloatSetting glow = new FloatSetting("Сила свечения", 1.0f, 0.0f, 5.0f, 0.1f)
            .visible(() -> !mode.is("Дым"));
    public final FloatSetting fill = new FloatSetting("Заливка", 0.6f, 0.0f, 1.0f, 0.01f)
            .visible(() -> !mode.is("Дым"));
    public final FloatSetting alpha = new FloatSetting("Прозрачность", 1.0f, 0.0f, 1.0f, 0.05f)
            .visible(() -> !mode.is("Дым"));

    public final FloatSetting smokeDensity = new FloatSetting("Густота дыма", 1.0f, 0.1f, 3.0f, 0.1f)
            .visible(() -> mode.is("Дым"));
    public final FloatSetting smokeSpeed = new FloatSetting("Скорость дыма", 0.5f, 0.1f, 3.0f, 0.1f)
            .visible(() -> mode.is("Дым"));
    public final FloatSetting smokeScale = new FloatSetting("Масштаб дыма", 8.0f, 2.0f, 20.0f, 1.0f)
            .visible(() -> mode.is("Дым"));
    public final FloatSetting smokeSpread = new FloatSetting("Разлёт дыма", 1.2f, 0.5f, 5.0f, 0.1f)
            .visible(() -> mode.is("Дым"));

    public ShaderHands() {
        super("ShaderHands", "Красивый шейдер на руки и предметы", ModuleCategory.RENDER);
        addSettings(
                mode,
                waveSpeed,
                waveScale,
                outline,
                glow,
                fill,
                alpha,
                smokeDensity,
                smokeSpeed,
                smokeScale,
                smokeSpread
        );
    }

    @EventLink(priority = Priority.MEDIUM)
    public void onRender2D(EventRender.Default event) {
        if (!isEnable()) return;
        RENDERER.renderOverlayIfPending(event.getPartialTicks());
    }
}
