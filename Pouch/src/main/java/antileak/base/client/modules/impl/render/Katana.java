package antileak.base.client.modules.impl.render;

import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.BooleanSetting;
import antileak.base.client.modules.settings.implement.FloatSetting;
import antileak.base.client.modules.settings.implement.ModeSetting;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.EntityType;

/**
 * Katana - 3D катана на спине игрока
 */
public class Katana extends Module {

    public static Katana INSTANCE = new Katana();

    // Выбор: на кого показывать катану
    public final ModeSetting target = new ModeSetting(
            "Показывать",
            "Себя",
            "Себя",
            "Всех игроков",
            "Себя и других"
    );

    // Подсветка вкл/выкл
    public final BooleanSetting glow = new BooleanSetting("Подсветка", true);

    // Уровень подсветки 0-100%
    public final FloatSetting glowLevel = new FloatSetting(
            "Уровень подсветки",
            50f,
            0f,
            100f,
            1f
    ).visible(() -> glow.isState());

    // Прозрачность заливки 0-100%
    public final FloatSetting fillAlpha = new FloatSetting(
            "Прозрачность заливки",
            20f,
            0f,
            100f,
            1f
    );

    // Прозрачность обводки 0-100%
    public final FloatSetting outlineAlpha = new FloatSetting(
            "Прозрачность обводки",
            85f,
            0f,
            100f,
            1f
    );

    @SuppressWarnings("unchecked")
    public Katana() {
        super("Katana", "3D катана на спине игрока", ModuleCategory.RENDER);
        addSettings(target, glow, glowLevel, fillAlpha, outlineAlpha);

        // Регистрируем рендерер катаны
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityType == EntityType.PLAYER) {
                registrationHelper.register(new KatanaRenderer(
                        (FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel>) entityRenderer
                ));
            }
        });
    }
}
