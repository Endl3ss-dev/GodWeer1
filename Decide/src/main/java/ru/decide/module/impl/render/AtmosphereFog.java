/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.minecraft.class_243
 *  net.minecraft.class_276
 *  net.minecraft.class_310
 *  net.minecraft.class_4184
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector4f
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.module.impl.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.manager.event_impl.WorldLoadEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.DelimiterSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.other.Instance;
import ru.decide.utils.render.AtmosphereFogPipeline;
import ru.decide.utils.sound.EffectSoundController;

@ModuleInfo(name="Atmosphere Fog", desc="\u041e\u0431\u044a\u0451\u043c\u043d\u044b\u0439 \u0442\u0443\u043c\u0430\u043d \u0441 \u0434\u0438\u043d\u0430\u043c\u0438\u0447\u0435\u0441\u043a\u0438\u043c\u0438 \u043b\u0443\u0447\u0430\u043c\u0438 \u0438 \u043f\u0440\u0435\u0441\u0435\u0442\u0430\u043c\u0438 \u0430\u0442\u043c\u043e\u0441\u0444\u0435\u0440\u044b", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public final class AtmosphereFog
extends Module {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"client/AtmosphereFog");
    private static final String PRESET_GOLDEN_DAWN = "\u0417\u043e\u043b\u043e\u0442\u043e\u0439 \u0440\u0430\u0441\u0441\u0432\u0435\u0442";
    private static final String PRESET_LILAC_SUNSET = "\u041b\u0438\u043b\u043e\u0432\u044b\u0439 \u0437\u0430\u043a\u0430\u0442";
    private static final String PRESET_MOONLIT_NIGHT = "\u041b\u0443\u043d\u043d\u0430\u044f \u043d\u043e\u0447\u044c";
    private static final String PRESET_CRIMSON_STORM = "\u0411\u0430\u0433\u0440\u043e\u0432\u0430\u044f \u0431\u0443\u0440\u044f";
    private static final String PRESET_MISTY_FOREST = "\u0422\u0443\u043c\u0430\u043d\u043d\u044b\u0439 \u043b\u0435\u0441";
    private static final String PRESET_THEME_COLOR = "\u0426\u0432\u0435\u0442 \u0438\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441\u0430";
    private static final String PRESET_CUSTOM_COLOR = "\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442";
    private static final String[] EXTRA_PRESET_NAMES = new String[]{"\u042f\u043d\u0442\u0430\u0440\u043d\u044b\u0439 \u043f\u043e\u043b\u0434\u0435\u043d\u044c", "\u0411\u0438\u0440\u044e\u0437\u043e\u0432\u044b\u0439 \u0431\u0435\u0440\u0435\u0433", "\u0421\u0430\u043f\u0444\u0438\u0440\u043e\u0432\u0430\u044f \u043d\u043e\u0447\u044c", "\u0418\u0437\u0443\u043c\u0440\u0443\u0434\u043d\u044b\u0439 \u043b\u0435\u0441", "\u0410\u043c\u0435\u0442\u0438\u0441\u0442\u043e\u0432\u044b\u0439 \u0437\u0430\u043a\u0430\u0442", "\u0420\u043e\u0437\u043e\u0432\u044b\u0439 \u043c\u0438\u0440\u0430\u0436", "\u041c\u0435\u0434\u043d\u044b\u0439 \u0433\u043e\u0440\u0438\u0437\u043e\u043d\u0442", "\u0421\u0435\u0440\u0435\u0431\u0440\u044f\u043d\u044b\u0439 \u0442\u0443\u043c\u0430\u043d", "\u0417\u043e\u043b\u043e\u0442\u0430\u044f \u043f\u044b\u043b\u044c", "\u041b\u0430\u0437\u0443\u0440\u043d\u044b\u0439 \u0448\u0442\u043e\u0440\u043c", "\u0411\u0430\u0433\u0440\u043e\u0432\u044b\u0439 \u0437\u0430\u043a\u0430\u0442", "\u041c\u044f\u0442\u043d\u044b\u0439 \u0440\u0430\u0441\u0441\u0432\u0435\u0442", "\u041b\u0430\u0432\u0430\u043d\u0434\u043e\u0432\u043e\u0435 \u043f\u043e\u043b\u0435", "\u041a\u043e\u0440\u0430\u043b\u043b\u043e\u0432\u044b\u0439 \u0431\u0440\u0438\u0437", "\u041f\u0435\u043f\u0435\u043b\u044c\u043d\u0430\u044f \u0434\u044b\u043c\u043a\u0430", "\u041b\u0435\u0434\u044f\u043d\u043e\u0439 \u0432\u043e\u0437\u0434\u0443\u0445", "\u0422\u0451\u043f\u043b\u044b\u0439 \u0432\u0435\u0447\u0435\u0440", "\u0425\u043e\u043b\u043e\u0434\u043d\u044b\u0439 \u043d\u0435\u043e\u043d", "\u041d\u0435\u0431\u0435\u0441\u043d\u044b\u0439 \u0441\u0432\u0435\u0442", "\u041b\u0443\u043d\u043d\u044b\u0439 \u0442\u0443\u043c\u0430\u043d", "\u0421\u043e\u043b\u043d\u0435\u0447\u043d\u044b\u0439 \u0434\u043e\u0436\u0434\u044c", "\u0413\u0440\u043e\u0437\u043e\u0432\u043e\u0435 \u043d\u0435\u0431\u043e", "\u0422\u0443\u043c\u0430\u043d\u043d\u044b\u0439 \u0431\u0435\u0440\u0435\u0433", "\u0425\u0432\u043e\u0439\u043d\u0430\u044f \u043c\u0433\u043b\u0430", "\u041f\u0443\u0441\u0442\u044b\u043d\u043d\u044b\u0439 \u0432\u0435\u0442\u0435\u0440", "\u041e\u043a\u0435\u0430\u043d\u0441\u043a\u0438\u0439 \u0431\u0440\u0438\u0437", "\u0413\u043e\u0440\u043d\u044b\u0439 \u0440\u0430\u0441\u0441\u0432\u0435\u0442", "\u0421\u0435\u0432\u0435\u0440\u043d\u043e\u0435 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u0435", "\u042e\u0436\u043d\u043e\u0435 \u0441\u0438\u044f\u043d\u0438\u0435", "\u0412\u0435\u0441\u0435\u043d\u043d\u0438\u0439 \u0442\u0443\u043c\u0430\u043d", "\u041e\u0441\u0435\u043d\u043d\u0438\u0439 \u0432\u0435\u0447\u0435\u0440", "\u0417\u0438\u043c\u043d\u0438\u0439 \u0432\u043e\u0437\u0434\u0443\u0445", "\u041b\u0435\u0442\u043d\u0438\u0439 \u0437\u043d\u043e\u0439", "\u0414\u043e\u0436\u0434\u0435\u0432\u0430\u044f \u0434\u044b\u043c\u043a\u0430", "\u0421\u043d\u0435\u0436\u043d\u0430\u044f \u043f\u044b\u043b\u044c", "\u0412\u0443\u043b\u043a\u0430\u043d\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u0437\u0430\u043a\u0430\u0442", "\u041a\u043e\u0441\u043c\u0438\u0447\u0435\u0441\u043a\u0430\u044f \u043c\u0433\u043b\u0430", "\u0417\u0432\u0451\u0437\u0434\u043d\u044b\u0439 \u0442\u0443\u043c\u0430\u043d", "\u041f\u043e\u043b\u044f\u0440\u043d\u044b\u0439 \u0434\u0435\u043d\u044c", "\u041f\u043e\u043b\u044f\u0440\u043d\u0430\u044f \u043d\u043e\u0447\u044c", "\u0418\u0437\u0443\u043c\u0440\u0443\u0434\u043d\u044b\u0439 \u0447\u0430\u0441", "\u0410\u043c\u0435\u0442\u0438\u0441\u0442\u043e\u0432\u044b\u0439 \u0447\u0430\u0441", "\u0420\u0443\u0431\u0438\u043d\u043e\u0432\u044b\u0439 \u0441\u0432\u0435\u0442", "\u0422\u043e\u043f\u0430\u0437\u043e\u0432\u044b\u0439 \u0441\u0432\u0435\u0442", "\u041e\u043d\u0438\u043a\u0441\u043e\u0432\u044b\u0439 \u0442\u0443\u043c\u0430\u043d", "\u0416\u0435\u043c\u0447\u0443\u0436\u043d\u044b\u0439 \u0441\u0432\u0435\u0442", "\u041c\u0435\u0434\u043e\u0432\u044b\u0439 \u0442\u0443\u043c\u0430\u043d", "\u041f\u0440\u044f\u043d\u044b\u0439 \u0437\u0430\u043a\u0430\u0442", "\u0422\u0438\u0445\u0438\u0439 \u0440\u0430\u0441\u0441\u0432\u0435\u0442", "\u041f\u043e\u0441\u043b\u0435\u0434\u043d\u0438\u0439 \u0441\u0432\u0435\u0442"};
    private static final String[] PRESET_NAMES = AtmosphereFog.buildPresetNames();
    private static final AtmospherePreset[] EXTRA_PRESETS = AtmosphereFog.buildExtraPresets();
    private static final int DEFAULT_CUSTOM_COLOR = -10053121;
    private static final float NIGHT_SUN_X_DIRECTION = -1.0f;
    private static final float ANIMATION_WRAP = 100000.0f;
    private static final float ANIMATION_TIME_SCALE = 1.0f;
    private static final float CHUNK_SIZE = 16.0f;
    private static final float TAU = (float)Math.PI * 2;
    public final DelimiterSetting generalSeparator = new DelimiterSetting(this, "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0435");
    public final ModeSetting preset = new ModeSetting((Module)this, "\u041f\u0440\u0435\u0441\u0435\u0442", PRESET_NAMES);
    public final SliderSetting intensity = new SliderSetting(this, "\u0418\u043d\u0442\u0435\u043d\u0441\u0438\u0432\u043d\u043e\u0441\u0442\u044c", 1.0f, 0.25f, 1.25f, 0.01f);
    public final DelimiterSetting soundSeparator = new DelimiterSetting(this, "\u0417\u0432\u0443\u043a \u043f\u0440\u0435\u0441\u0435\u0442\u0430");
    public final BooleanSetting effectSound = new BooleanSetting((Module)this, "\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u0437\u0432\u0443\u043a", true);
    public final SliderSetting effectSoundVolume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c", 16.0f, 0.0f, 100.0f, 1.0f).setVisible(this::isEffectSoundEnabled);
    public final BooleanSetting volumetricRays = new BooleanSetting((Module)this, "\u041e\u0431\u044a\u0451\u043c\u043d\u044b\u0435 \u043b\u0443\u0447\u0438", true);
    public final ColorSetting customColor = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442", -10053121).setVisible(() -> this.preset.is(PRESET_CUSTOM_COLOR));
    private static final int PAL_GOLDEN_DAWN = 0;
    private static final int PAL_LILAC_SUNSET = 1;
    private static final int PAL_MOONLIT_NIGHT = 2;
    private static final int PAL_CRIMSON_STORM = 3;
    private static final int PAL_MISTY_FOREST = 4;
    private static final int PAL_CUSTOM_TINT = 5;
    private static final AtmospherePreset GOLDEN_DAWN_PRESET = new AtmospherePreset(0, 0.28f, 82.0f, 24.0f, 4.0f, 0.58f, 0.78f, 0.35f, 0.72f, 1.15f, 0.82f, 2, 0.9f, 0.36f, 18.0f, 0.14f, -2771465, 0.18f, false, 0.12f);
    private static final AtmospherePreset LILAC_SUNSET_PRESET = new AtmospherePreset(1, 0.38f, 98.0f, 32.0f, 0.0f, 0.72f, 0.62f, 0.48f, 0.78f, 1.1f, 1.0f, 2, 1.08f, 0.44f, 22.0f, 0.2f, -811034, 0.1f, false, -0.055f);
    private static final AtmospherePreset MOONLIT_NIGHT_PRESET = new AtmospherePreset(2, 0.26f, 110.0f, 46.0f, 8.0f, 0.45f, 1.2f, 0.2f, 0.86f, 0.62f, 0.48f, 1, 0.74f, 0.22f, 14.0f, 0.08f, -4200449, 0.45f, false, 0.18f);
    private static final AtmospherePreset CRIMSON_STORM_PRESET = new AtmospherePreset(3, 0.55f, 86.0f, 44.0f, 0.0f, 0.92f, 0.42f, 0.72f, 0.48f, 0.9f, 1.18f, 2, 0.96f, 0.28f, 30.0f, 0.38f, -44237, 0.02f, false, 0.07f);
    private static final AtmospherePreset MISTY_FOREST_PRESET = new AtmospherePreset(4, 0.68f, 72.0f, 28.0f, 0.0f, 0.78f, 1.35f, 0.12f, 0.95f, 0.38f, 0.32f, 0, 0.55f, 0.7f, 10.0f, 0.05f, -2687058, 0.5f, true, 0.0f);
    private static final AtmospherePreset CUSTOM_TINT_PRESET = new AtmospherePreset(5, 0.34f, 84.0f, 26.0f, 2.0f, 0.62f, 0.74f, 0.32f, 0.78f, 0.92f, 0.78f, 2, 0.88f, 0.34f, 18.0f, 0.16f, -1, 0.14f, false, 0.1f);
    private static final float LILAC_ZENITH_R = 0.135f;
    private static final float LILAC_ZENITH_G = 0.125f;
    private static final float LILAC_ZENITH_B = 0.3f;
    private static final float LILAC_HW_R = 0.89f;
    private static final float LILAC_HW_G = 0.46f;
    private static final float LILAC_HW_B = 0.55f;
    private static final float LILAC_HC_R = 0.38f;
    private static final float LILAC_HC_G = 0.35f;
    private static final float LILAC_HC_B = 0.56f;
    private static final float LILAC_FW_R = 0.8f;
    private static final float LILAC_FW_G = 0.52f;
    private static final float LILAC_FW_B = 0.62f;
    private static final float LILAC_FC_R = 0.47f;
    private static final float LILAC_FC_G = 0.44f;
    private static final float LILAC_FC_B = 0.64f;
    private static final float LILAC_RAY_R = 0.92f;
    private static final float LILAC_RAY_G = 0.56f;
    private static final float LILAC_RAY_B = 0.72f;
    private static final float MOON_ZENITH_R = 0.025f;
    private static final float MOON_ZENITH_G = 0.04f;
    private static final float MOON_ZENITH_B = 0.12f;
    private static final float MOON_HW_R = 0.18f;
    private static final float MOON_HW_G = 0.23f;
    private static final float MOON_HW_B = 0.42f;
    private static final float MOON_HC_R = 0.08f;
    private static final float MOON_HC_G = 0.16f;
    private static final float MOON_HC_B = 0.35f;
    private static final float MOON_FW_R = 0.18f;
    private static final float MOON_FW_G = 0.25f;
    private static final float MOON_FW_B = 0.4f;
    private static final float MOON_FC_R = 0.1f;
    private static final float MOON_FC_G = 0.18f;
    private static final float MOON_FC_B = 0.32f;
    private static final float MOON_RAY_R = 0.52f;
    private static final float MOON_RAY_G = 0.68f;
    private static final float MOON_RAY_B = 1.0f;
    private static final float CRIM_ZENITH_R = 0.1f;
    private static final float CRIM_ZENITH_G = 0.025f;
    private static final float CRIM_ZENITH_B = 0.04f;
    private static final float CRIM_HW_R = 0.75f;
    private static final float CRIM_HW_G = 0.12f;
    private static final float CRIM_HW_B = 0.16f;
    private static final float CRIM_HC_R = 0.3f;
    private static final float CRIM_HC_G = 0.08f;
    private static final float CRIM_HC_B = 0.12f;
    private static final float CRIM_FW_R = 0.62f;
    private static final float CRIM_FW_G = 0.1f;
    private static final float CRIM_FW_B = 0.12f;
    private static final float CRIM_FC_R = 0.22f;
    private static final float CRIM_FC_G = 0.07f;
    private static final float CRIM_FC_B = 0.09f;
    private static final float CRIM_RAY_R = 1.0f;
    private static final float CRIM_RAY_G = 0.25f;
    private static final float CRIM_RAY_B = 0.2f;
    private static final float MISTY_ZENITH_R = 0.045f;
    private static final float MISTY_ZENITH_G = 0.095f;
    private static final float MISTY_ZENITH_B = 0.08f;
    private static final float MISTY_HW_R = 0.32f;
    private static final float MISTY_HW_G = 0.46f;
    private static final float MISTY_HW_B = 0.32f;
    private static final float MISTY_HC_R = 0.2f;
    private static final float MISTY_HC_G = 0.34f;
    private static final float MISTY_HC_B = 0.31f;
    private static final float MISTY_FW_R = 0.28f;
    private static final float MISTY_FW_G = 0.4f;
    private static final float MISTY_FW_B = 0.3f;
    private static final float MISTY_FC_R = 0.15f;
    private static final float MISTY_FC_G = 0.3f;
    private static final float MISTY_FC_B = 0.27f;
    private static final float MISTY_RAY_R = 0.72f;
    private static final float MISTY_RAY_G = 0.88f;
    private static final float MISTY_RAY_B = 0.68f;
    private static final float GOLD_ZENITH_R = 0.16f;
    private static final float GOLD_ZENITH_G = 0.19f;
    private static final float GOLD_ZENITH_B = 0.38f;
    private static final float GOLD_ZENITH_BLEND = 0.14f;
    private static final float GOLD_HW_R_SCALE = 1.12f;
    private static final float GOLD_HW_G_SCALE = 0.88f;
    private static final float GOLD_HW_B_SCALE = 0.62f;
    private static final float GOLD_HC_R = 0.56f;
    private static final float GOLD_HC_G = 0.62f;
    private static final float GOLD_HC_B = 0.8f;
    private static final float GOLD_FW_TR_R = 0.95f;
    private static final float GOLD_FW_TR_G = 0.55f;
    private static final float GOLD_FW_TR_B = 0.63f;
    private static final float GOLD_FW_BLEND = 0.42f;
    private static final float GOLD_FC_R = 0.6f;
    private static final float GOLD_FC_G = 0.67f;
    private static final float GOLD_FC_B = 0.82f;
    private static final float GOLD_RAY_R_SCALE = 1.08f;
    private static final float GOLD_RAY_G_SCALE = 0.94f;
    private static final float GOLD_RAY_B_SCALE = 0.72f;
    private static final float CUS_ZENITH_R = 0.085f;
    private static final float CUS_ZENITH_G = 0.1f;
    private static final float CUS_ZENITH_B = 0.2f;
    private static final float CUS_ZENITH_BLEND = 0.3f;
    private static final float CUS_HW_TR_G = 0.93f;
    private static final float CUS_HW_TR_B = 0.82f;
    private static final float CUS_HW_BLEND = 0.35f;
    private static final float CUS_HC_R = 0.52f;
    private static final float CUS_HC_G = 0.58f;
    private static final float CUS_HC_B = 0.74f;
    private static final float CUS_HC_BLEND = 0.28f;
    private static final float CUS_FW_TR_R = 0.97f;
    private static final float CUS_FW_TR_G = 0.93f;
    private static final float CUS_FW_TR_B = 0.88f;
    private static final float CUS_FW_BLEND = 0.45f;
    private static final float CUS_FC_R = 0.58f;
    private static final float CUS_FC_G = 0.63f;
    private static final float CUS_FC_B = 0.76f;
    private static final float CUS_FC_BLEND = 0.35f;
    private static final float CUS_RAY_TR_G = 0.96f;
    private static final float CUS_RAY_TR_B = 0.88f;
    private static final float CUS_RAY_BLEND = 0.3f;
    private static final float M_L_R = 0.41222146f;
    private static final float M_L_G = 0.53633255f;
    private static final float M_L_B = 0.051446f;
    private static final float M_M_R = 0.2119035f;
    private static final float M_M_G = 0.6806995f;
    private static final float M_M_B = 0.10739696f;
    private static final float M_S_R = 0.08830246f;
    private static final float M_S_G = 0.28171885f;
    private static final float M_S_B = 0.6299787f;
    private static final float OUT_R_L = 4.0767417f;
    private static final float OUT_R_M = 3.3077116f;
    private static final float OUT_R_S = 0.23096994f;
    private static final float OUT_G_L = -1.268438f;
    private static final float OUT_G_M = 2.6097574f;
    private static final float OUT_G_S = 0.34131938f;
    private static final float OUT_B_L = -0.0041960864f;
    private static final float OUT_B_M = 0.7034186f;
    private static final float OUT_B_S = 1.7076147f;
    private static final float SRGB_LINEAR_THRESHOLD = 0.04045f;
    private static final float SRGB_LINEAR_DIVISOR = 12.92f;
    private static final float SRGB_INPUT_OFFSET = 0.055f;
    private static final float SRGB_INPUT_SCALE = 1.055f;
    private static final double SRGB_GAMMA = 2.4;
    private static final float LINEAR_SRGB_THRESHOLD = 0.0031308f;
    private static final float LINEAR_SRGB_MULTIPLIER = 12.92f;
    private static final double SRGB_OUTPUT_SCALE = 1.055;
    private static final double INVERSE_SRGB_GAMMA = 0.4166666666666667;
    private static final double SRGB_OUTPUT_OFFSET = 0.055;
    private final float[] paletteRgb = new float[18];
    private final float[] uniforms = new float[92];
    private final Matrix4f inverseProjection = new Matrix4f();
    private final Matrix4f inverseView = new Matrix4f();
    private final Vector4f sunScreen = new Vector4f();
    private float animationTime;
    private long lastFrameNanos;

    public static AtmosphereFog getInstance() {
        return Instance.get(AtmosphereFog.class);
    }

    @Override
    public void setup() {
        super.setup();
        if (AtmosphereFogPipeline.deviceReady() && !AtmosphereFogPipeline.validate()) {
            LOGGER.warn("[AtmosphereFog] pipeline init failed, effect will not render");
        }
        ClientTickEvents.END_CLIENT_TICK.register(AtmosphereFog::clientTick);
    }

    private boolean isEffectSoundEnabled() {
        return this.effectSound.getValue();
    }

    private static void clientTick(class_310 client) {
        AtmosphereFog module = AtmosphereFog.getInstance();
        if (module == null || client == null) {
            return;
        }
        float volume = module.effectSound.getValue() != false ? module.effectSoundVolume.getValue().floatValue() / 100.0f * 0.28f : 0.0f;
        EffectSoundController.tick("atmosphere", module.isEnabled(), module.preset.getIndex(), volume);
    }

    @Override
    protected void onDisable() {
        EffectSoundController.stop("atmosphere");
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        AtmosphereFogPipeline.clear();
    }

    public void renderFog(class_276 renderTarget, Matrix4f positionMatrix, Matrix4f projectionMatrix, float tickDelta) {
        float sunZ;
        float sunY;
        float sunX;
        if (!this.isEnabled() || renderTarget == null) {
            return;
        }
        if (projectionMatrix == null || positionMatrix == null) {
            return;
        }
        if (AtmosphereFog.mc.field_1687 == null || AtmosphereFog.mc.field_1724 == null || AtmosphereFog.mc.field_1773 == null) {
            return;
        }
        if (renderTarget.field_1482 <= 0 || renderTarget.field_1481 <= 0) {
            return;
        }
        class_4184 camera = AtmosphereFog.mc.field_1773.method_19418();
        if (camera == null) {
            return;
        }
        class_243 camPos = camera.method_71156();
        AtmospherePreset active = this.getPreset();
        int baseColor = this.resolveBaseColor(active);
        this.updatePalette(active.paletteId, baseColor);
        float skyAngle = this.skyAngleRadians(tickDelta);
        if (active.followSkyAngle) {
            sunX = 0.0f;
            sunY = (float)Math.cos(skyAngle);
            sunZ = (float)Math.sin(skyAngle);
        } else {
            float sunHemisphere = -((float)Math.sin(skyAngle));
            float sunXSign = sunHemisphere >= 0.0f ? 1.0f : -1.0f;
            sunX = sunXSign * (float)Math.cos(active.sunElevationRadians);
            sunY = (float)Math.sin(active.sunElevationRadians);
            sunZ = 0.0f;
        }
        float intensityMultiplier = this.intensity.getValue().floatValue();
        int rayColor = active.paletteId == 5 ? baseColor : active.rayColor;
        float animationTime = this.advanceAnimationTime();
        float viewDistanceBlocks = (float)((Integer)AtmosphereFog.mc.field_1690.method_42503().method_41753()).intValue() * 16.0f;
        this.inverseProjection.set((Matrix4fc)projectionMatrix).invert();
        this.inverseView.set((Matrix4fc)positionMatrix).invert();
        this.inverseView.m30((float)camPos.field_1352);
        this.inverseView.m31((float)camPos.field_1351);
        this.inverseView.m32((float)camPos.field_1350);
        this.projectSunDirection(positionMatrix, projectionMatrix, sunX, sunY, sunZ);
        int o = 0;
        this.uniforms[o++] = renderTarget.field_1482;
        this.uniforms[o++] = renderTarget.field_1481;
        this.uniforms[o++] = animationTime;
        this.uniforms[o++] = viewDistanceBlocks;
        this.inverseProjection.get(this.uniforms, 4);
        this.inverseView.get(this.uniforms, 20);
        this.uniforms[36] = (float)camPos.field_1352;
        this.uniforms[37] = (float)camPos.field_1351;
        this.uniforms[38] = (float)camPos.field_1350;
        this.uniforms[39] = 0.0f;
        this.uniforms[40] = sunX;
        this.uniforms[41] = sunY;
        this.uniforms[42] = sunZ;
        this.uniforms[43] = 0.0f;
        this.uniforms[44] = this.sunScreen.x;
        this.uniforms[45] = this.sunScreen.y;
        this.uniforms[46] = this.sunScreen.z;
        this.uniforms[47] = 0.0f;
        this.uniforms[48] = active.fogDensity * intensityMultiplier;
        this.uniforms[49] = active.fogMaxHeight - active.fogHeightRange;
        this.uniforms[50] = active.fogMaxHeight;
        this.uniforms[51] = active.fogStart;
        this.uniforms[52] = active.fogNoise;
        this.uniforms[53] = active.fogScale;
        this.uniforms[54] = active.fogSpeed;
        this.uniforms[55] = active.softness;
        this.uniforms[56] = active.skyGlow * intensityMultiplier;
        this.uniforms[57] = this.volumetricRays.getValue() != false ? active.godRays * intensityMultiplier : 0.0f;
        this.uniforms[58] = active.rayStyle;
        this.uniforms[59] = active.rayLength;
        this.uniforms[60] = active.rayWidth;
        this.uniforms[61] = active.rayCount;
        this.uniforms[62] = active.rayMotion;
        this.uniforms[63] = active.whiteRayMix;
        this.uniforms[64] = (float)(rayColor >> 16 & 0xFF) / 255.0f;
        this.uniforms[65] = (float)(rayColor >> 8 & 0xFF) / 255.0f;
        this.uniforms[66] = (float)(rayColor & 0xFF) / 255.0f;
        this.uniforms[67] = 0.0f;
        int pal = 68;
        for (int i = 0; i < 6; ++i) {
            this.uniforms[pal + i * 4] = this.paletteRgb[i * 3];
            this.uniforms[pal + i * 4 + 1] = this.paletteRgb[i * 3 + 1];
            this.uniforms[pal + i * 4 + 2] = this.paletteRgb[i * 3 + 2];
            this.uniforms[pal + i * 4 + 3] = 0.0f;
        }
        AtmosphereFogPipeline.apply(renderTarget, this.uniforms);
    }

    private float advanceAnimationTime() {
        long now = System.nanoTime();
        if (this.lastFrameNanos == 0L) {
            this.lastFrameNanos = now;
        }
        float delta = (float)(now - this.lastFrameNanos) / 1.0E9f;
        this.lastFrameNanos = now;
        if (delta > 0.25f) {
            delta = 0.25f;
        }
        this.animationTime += delta * 1.0f;
        if (this.animationTime > 100000.0f) {
            this.animationTime = 0.0f;
        }
        return this.animationTime;
    }

    private float skyAngleRadians(float tickDelta) {
        long timeOfDay = AtmosphereFog.mc.field_1687.method_8532();
        float fraction = (float)(timeOfDay % 24000L) / 24000.0f;
        return fraction * ((float)Math.PI * 2);
    }

    private void projectSunDirection(Matrix4f viewMatrix, Matrix4f projectionMatrix, float sunX, float sunY, float sunZ) {
        this.sunScreen.set(sunX, sunY, sunZ, 0.0f);
        viewMatrix.transform(this.sunScreen);
        float visibility = AtmosphereFog.clamp(-this.sunScreen.z * 4.0f, 0.0f, 1.0f);
        if (visibility <= 1.0E-4f) {
            this.sunScreen.set(0.5f, 0.5f, 0.0f, 0.0f);
        } else {
            this.sunScreen.set(this.sunScreen.x * 1000.0f, this.sunScreen.y * 1000.0f, this.sunScreen.z * 1000.0f, 1.0f);
            projectionMatrix.transform(this.sunScreen);
            if (this.sunScreen.w <= 1.0E-4f) {
                this.sunScreen.set(0.5f, 0.5f, 0.0f, 0.0f);
            } else {
                this.sunScreen.set(this.sunScreen.x / this.sunScreen.w * 0.5f + 0.5f, this.sunScreen.y / this.sunScreen.w * 0.5f + 0.5f, visibility, 0.0f);
            }
        }
    }

    private AtmospherePreset getPreset() {
        int index = this.preset.getIndex();
        if (index >= 7) {
            return EXTRA_PRESETS[Math.min(index - 7, EXTRA_PRESETS.length - 1)];
        }
        if (this.preset.is(PRESET_LILAC_SUNSET)) {
            return LILAC_SUNSET_PRESET;
        }
        if (this.preset.is(PRESET_MOONLIT_NIGHT)) {
            return MOONLIT_NIGHT_PRESET;
        }
        if (this.preset.is(PRESET_CRIMSON_STORM)) {
            return CRIMSON_STORM_PRESET;
        }
        if (this.preset.is(PRESET_MISTY_FOREST)) {
            return MISTY_FOREST_PRESET;
        }
        if (this.preset.is(PRESET_THEME_COLOR) || this.preset.is(PRESET_CUSTOM_COLOR)) {
            return CUSTOM_TINT_PRESET;
        }
        return GOLDEN_DAWN_PRESET;
    }

    private int resolveBaseColor(AtmospherePreset active) {
        if (active.paletteId != 5) {
            return active.rayColor;
        }
        if (this.preset.is(PRESET_CUSTOM_COLOR)) {
            return this.customColor.getValue();
        }
        return ColorUtil.getClientColor1(1);
    }

    private void updatePalette(int paletteId, int argbColor) {
        float red = (float)(argbColor >> 16 & 0xFF) / 255.0f;
        float green = (float)(argbColor >> 8 & 0xFF) / 255.0f;
        float blue = (float)(argbColor & 0xFF) / 255.0f;
        if (paletteId >= 100) {
            this.updateExtraPalette(paletteId - 100, red, green, blue);
            return;
        }
        switch (paletteId) {
            case 1: {
                this.setPaletteColor(0, 0.135f, 0.125f, 0.3f);
                this.setPaletteColor(3, 0.89f, 0.46f, 0.55f);
                this.setPaletteColor(6, 0.38f, 0.35f, 0.56f);
                this.setPaletteColor(9, 0.8f, 0.52f, 0.62f);
                this.setPaletteColor(12, 0.47f, 0.44f, 0.64f);
                this.setPaletteColor(15, 0.92f, 0.56f, 0.72f);
                break;
            }
            case 2: {
                this.setPaletteColor(0, 0.025f, 0.04f, 0.12f);
                this.setPaletteColor(3, 0.18f, 0.23f, 0.42f);
                this.setPaletteColor(6, 0.08f, 0.16f, 0.35f);
                this.setPaletteColor(9, 0.18f, 0.25f, 0.4f);
                this.setPaletteColor(12, 0.1f, 0.18f, 0.32f);
                this.setPaletteColor(15, 0.52f, 0.68f, 1.0f);
                break;
            }
            case 3: {
                this.setPaletteColor(0, 0.1f, 0.025f, 0.04f);
                this.setPaletteColor(3, 0.75f, 0.12f, 0.16f);
                this.setPaletteColor(6, 0.3f, 0.08f, 0.12f);
                this.setPaletteColor(9, 0.62f, 0.1f, 0.12f);
                this.setPaletteColor(12, 0.22f, 0.07f, 0.09f);
                this.setPaletteColor(15, 1.0f, 0.25f, 0.2f);
                break;
            }
            case 4: {
                this.setPaletteColor(0, 0.045f, 0.095f, 0.08f);
                this.setPaletteColor(3, 0.32f, 0.46f, 0.32f);
                this.setPaletteColor(6, 0.2f, 0.34f, 0.31f);
                this.setPaletteColor(9, 0.28f, 0.4f, 0.3f);
                this.setPaletteColor(12, 0.15f, 0.3f, 0.27f);
                this.setPaletteColor(15, 0.72f, 0.88f, 0.68f);
                break;
            }
            case 5: {
                this.blendPerceptual(0, 0.085f, 0.1f, 0.2f, red, green, blue, 0.3f);
                this.blendPerceptual(3, red, green, blue, 1.0f, 0.93f, 0.82f, 0.35f);
                this.blendPerceptual(6, 0.52f, 0.58f, 0.74f, red, green, blue, 0.28f);
                this.blendPerceptual(9, red, green, blue, 0.97f, 0.93f, 0.88f, 0.45f);
                this.blendPerceptual(12, 0.58f, 0.63f, 0.76f, red, green, blue, 0.35f);
                this.blendPerceptual(15, red, green, blue, 1.0f, 0.96f, 0.88f, 0.3f);
                break;
            }
            default: {
                this.blendPerceptual(0, 0.16f, 0.19f, 0.38f, red, green, blue, 0.14f);
                this.setPaletteColor(3, AtmosphereFog.clampUnit(red * 1.12f), AtmosphereFog.clampUnit(green * 0.88f), AtmosphereFog.clampUnit(blue * 0.62f));
                this.setPaletteColor(6, 0.56f, 0.62f, 0.8f);
                this.blendPerceptual(9, red, green, blue, 0.95f, 0.55f, 0.63f, 0.42f);
                this.setPaletteColor(12, 0.6f, 0.67f, 0.82f);
                this.setPaletteColor(15, AtmosphereFog.clampUnit(red * 1.08f), AtmosphereFog.clampUnit(green * 0.94f), AtmosphereFog.clampUnit(blue * 0.72f));
            }
        }
    }

    private void updateExtraPalette(int index, float red, float green, float blue) {
        float phase = (float)(index % 7) / 6.0f;
        this.setPaletteColor(0, AtmosphereFog.clampUnit(red * 0.15f + 0.018f), AtmosphereFog.clampUnit(green * 0.16f + 0.024f), AtmosphereFog.clampUnit(blue * 0.22f + 0.06f));
        this.setPaletteColor(3, AtmosphereFog.clampUnit(red * (0.82f + phase * 0.22f) + 0.05f), AtmosphereFog.clampUnit(green * (0.72f + phase * 0.18f) + 0.04f), AtmosphereFog.clampUnit(blue * (0.78f + phase * 0.16f) + 0.05f));
        this.setPaletteColor(6, AtmosphereFog.clampUnit(red * 0.48f + 0.22f), AtmosphereFog.clampUnit(green * 0.5f + 0.22f), AtmosphereFog.clampUnit(blue * 0.62f + 0.26f));
        this.setPaletteColor(9, AtmosphereFog.clampUnit(red * 0.68f + 0.1f), AtmosphereFog.clampUnit(green * 0.66f + 0.1f), AtmosphereFog.clampUnit(blue * 0.78f + 0.12f));
        this.setPaletteColor(12, AtmosphereFog.clampUnit(red * 0.44f + 0.06f), AtmosphereFog.clampUnit(green * 0.46f + 0.07f), AtmosphereFog.clampUnit(blue * 0.58f + 0.1f));
        this.setPaletteColor(15, AtmosphereFog.clampUnit(red * 1.04f + 0.12f + phase * 0.06f), AtmosphereFog.clampUnit(green * 1.02f + 0.12f), AtmosphereFog.clampUnit(blue * 1.08f + 0.14f));
    }

    private void setPaletteColor(int offset, float red, float green, float blue) {
        this.paletteRgb[offset] = red;
        this.paletteRgb[offset + 1] = green;
        this.paletteRgb[offset + 2] = blue;
    }

    private void blendPerceptual(int offset, float fromRed, float fromGreen, float fromBlue, float toRed, float toGreen, float toBlue, float amount) {
        float t = AtmosphereFog.clampUnit(amount);
        float fromLinR = AtmosphereFog.srgbToLinear(fromRed);
        float fromLinG = AtmosphereFog.srgbToLinear(fromGreen);
        float fromLinB = AtmosphereFog.srgbToLinear(fromBlue);
        float toLinR = AtmosphereFog.srgbToLinear(toRed);
        float toLinG = AtmosphereFog.srgbToLinear(toGreen);
        float toLinB = AtmosphereFog.srgbToLinear(toBlue);
        float fromL = (float)Math.cbrt(0.41222146f * fromLinR + 0.53633255f * fromLinG + 0.051446f * fromLinB);
        float fromM = (float)Math.cbrt(0.2119035f * fromLinR + 0.6806995f * fromLinG + 0.10739696f * fromLinB);
        float fromS = (float)Math.cbrt(0.08830246f * fromLinR + 0.28171885f * fromLinG + 0.6299787f * fromLinB);
        float toL = (float)Math.cbrt(0.41222146f * toLinR + 0.53633255f * toLinG + 0.051446f * toLinB);
        float toM = (float)Math.cbrt(0.2119035f * toLinR + 0.6806995f * toLinG + 0.10739696f * toLinB);
        float toS = (float)Math.cbrt(0.08830246f * toLinR + 0.28171885f * toLinG + 0.6299787f * toLinB);
        float mixL = AtmosphereFog.lerp(fromL, toL, t);
        float mixM = AtmosphereFog.lerp(fromM, toM, t);
        float mixS = AtmosphereFog.lerp(fromS, toS, t);
        float l = mixL * mixL * mixL;
        float m = mixM * mixM * mixM;
        float s = mixS * mixS * mixS;
        this.setPaletteColor(offset, AtmosphereFog.linearToSrgb(4.0767417f * l - 3.3077116f * m + 0.23096994f * s), AtmosphereFog.linearToSrgb(-1.268438f * l + 2.6097574f * m - 0.34131938f * s), AtmosphereFog.linearToSrgb(-0.0041960864f * l - 0.7034186f * m + 1.7076147f * s));
    }

    private static float lerp(float start, float end, float amount) {
        return start + (end - start) * amount;
    }

    private static float srgbToLinear(float value) {
        if (value <= 0.04045f) {
            return value / 12.92f;
        }
        return (float)Math.pow((value + 0.055f) / 1.055f, 2.4);
    }

    private static float linearToSrgb(float value) {
        float v = AtmosphereFog.clampUnit(value);
        if (v <= 0.0031308f) {
            return v * 12.92f;
        }
        return (float)(1.055 * Math.pow(v, 0.4166666666666667) - 0.055);
    }

    private static float clampUnit(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static String[] buildPresetNames() {
        String[] base = new String[]{PRESET_GOLDEN_DAWN, PRESET_LILAC_SUNSET, PRESET_MOONLIT_NIGHT, PRESET_CRIMSON_STORM, PRESET_MISTY_FOREST, PRESET_THEME_COLOR, PRESET_CUSTOM_COLOR};
        String[] names = new String[base.length + EXTRA_PRESET_NAMES.length];
        System.arraycopy(base, 0, names, 0, base.length);
        System.arraycopy(EXTRA_PRESET_NAMES, 0, names, base.length, EXTRA_PRESET_NAMES.length);
        return names;
    }

    private static AtmospherePreset[] buildExtraPresets() {
        AtmospherePreset[] presets = new AtmospherePreset[EXTRA_PRESET_NAMES.length];
        for (int i = 0; i < presets.length; ++i) {
            float t = (float)i / (float)(presets.length - 1);
            float density = 0.24f + (float)(i % 10) * 0.034f;
            float maxHeight = 68.0f + (float)(i % 8) * 9.0f;
            float heightRange = 17.0f + (float)(i % 6) * 5.0f;
            float start = (float)(i % 5) * 2.2f;
            float noise = 0.44f + (float)(i % 7) * 0.06f;
            float scale = 0.52f + (float)(i % 11) * 0.07f;
            float speed = 0.07f + (float)(i % 9) * 0.06f;
            float softness = 0.48f + (float)(i % 6) * 0.085f;
            float skyGlow = 0.42f + (float)(i % 7) * 0.115f;
            float godRays = 0.34f + (float)(i % 9) * 0.075f;
            float rayLength = 0.52f + (float)(i % 10) * 0.06f;
            float rayWidth = 0.18f + (float)(i % 6) * 0.05f;
            float rayCount = 10.0f + (float)(i % 12) * 2.0f;
            float rayMotion = 0.04f + (float)(i % 8) * 0.03f;
            float whiteRayMix = (float)(i % 5) * 0.11f;
            boolean followSky = i % 7 == 0;
            float sunElevation = -0.2f + (float)(i % 9) * 0.055f;
            presets[i] = new AtmospherePreset(100 + i, density, maxHeight, heightRange, start, noise, scale, speed, softness, skyGlow, godRays, i % 3, rayLength, rayWidth, rayCount, rayMotion, AtmosphereFog.extraColor(i), whiteRayMix, followSky, sunElevation);
        }
        return presets;
    }

    private static int extraColor(int index) {
        float g;
        float r;
        float hue = ((float)index * 0.618034f + 0.04f) % 1.0f;
        float saturation = 0.54f + (float)(index % 5) * 0.08f;
        float value = 0.84f + (float)(index % 4) * 0.045f;
        float h = hue * 6.0f;
        int sector = (int)h;
        float f = h - (float)sector;
        float p = value * (1.0f - saturation);
        float q = value * (1.0f - saturation * f);
        float t = value * (1.0f - saturation * (1.0f - f));
        float b = switch (sector % 6) {
            case 0 -> {
                r = value;
                g = t;
                yield p;
            }
            case 1 -> {
                r = q;
                g = value;
                yield p;
            }
            case 2 -> {
                r = p;
                g = value;
                yield t;
            }
            case 3 -> {
                r = p;
                g = q;
                yield value;
            }
            case 4 -> {
                r = t;
                g = p;
                yield value;
            }
            default -> {
                r = value;
                g = p;
                yield q;
            }
        };
        int ri = Math.round(AtmosphereFog.clampUnit(r) * 255.0f);
        int gi = Math.round(AtmosphereFog.clampUnit(g) * 255.0f);
        int bi = Math.round(AtmosphereFog.clampUnit(b) * 255.0f);
        return 0xFF000000 | ri << 16 | gi << 8 | bi;
    }

    @Environment(value=EnvType.CLIENT)
    private static final class AtmospherePreset {
        private final int paletteId;
        private final float fogDensity;
        private final float fogMaxHeight;
        private final float fogHeightRange;
        private final float fogStart;
        private final float fogNoise;
        private final float fogScale;
        private final float fogSpeed;
        private final float softness;
        private final float skyGlow;
        private final float godRays;
        private final int rayStyle;
        private final float rayLength;
        private final float rayWidth;
        private final float rayCount;
        private final float rayMotion;
        private final int rayColor;
        private final float whiteRayMix;
        private final boolean followSkyAngle;
        private final float sunElevationRadians;

        private AtmospherePreset(int paletteId, float fogDensity, float fogMaxHeight, float fogHeightRange, float fogStart, float fogNoise, float fogScale, float fogSpeed, float softness, float skyGlow, float godRays, int rayStyle, float rayLength, float rayWidth, float rayCount, float rayMotion, int rayColor, float whiteRayMix, boolean followSkyAngle, float sunElevationRadians) {
            this.paletteId = paletteId;
            this.fogDensity = fogDensity;
            this.fogMaxHeight = fogMaxHeight;
            this.fogHeightRange = fogHeightRange;
            this.fogStart = fogStart;
            this.fogNoise = fogNoise;
            this.fogScale = fogScale;
            this.fogSpeed = fogSpeed;
            this.softness = softness;
            this.skyGlow = skyGlow;
            this.godRays = godRays;
            this.rayStyle = rayStyle;
            this.rayLength = rayLength;
            this.rayWidth = rayWidth;
            this.rayCount = rayCount;
            this.rayMotion = rayMotion;
            this.rayColor = rayColor;
            this.whiteRayMix = whiteRayMix;
            this.followSkyAngle = followSkyAngle;
            this.sunElevationRadians = sunElevationRadians;
        }
    }
}

