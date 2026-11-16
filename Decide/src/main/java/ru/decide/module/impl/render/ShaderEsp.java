/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1304
 *  net.minecraft.class_1309
 *  net.minecraft.class_1542
 *  net.minecraft.class_1657
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_276
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 *  net.minecraft.class_746
 */
package ru.decide.module.impl.render;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_746;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.event_impl.WorldLoadEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.preview.ModulePreview;
import ru.decide.module.api.preview.PreviewContext;
import ru.decide.module.api.preview.PreviewSettings;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ButtonSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.ShaderEspOutlinePipeline;
import ru.decide.utils.render.ShaderEspRenderer;

@ModuleInfo(name="Shader ESP", category=Category.VISUALS, desc="\u0428\u0435\u0439\u0434\u0435\u0440\u043d\u044b\u0439 ESP \u0441 \u043e\u0440\u0435\u043e\u043b\u043e\u043c \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f \u0434\u043b\u044f \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439")
@Environment(value=EnvType.CLIENT)
public class ShaderEsp
extends Module
implements ModulePreview {
    private static ShaderEsp instance;
    public ButtonSetting previewButton = PreviewSettings.button(this);
    public BooleanSetting localPlayer = new BooleanSetting((Module)this, "\u041b\u043e\u043a\u0430\u043b\u044c\u043d\u044b\u0439 \u0438\u0433\u0440\u043e\u043a", true);
    public BooleanSetting players = new BooleanSetting((Module)this, "\u0418\u0433\u0440\u043e\u043a\u0438", true);
    public BooleanSetting ignoreNaked = new BooleanSetting((Module)this, "\u0418\u0433\u043d\u043e\u0440\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0433\u043e\u043b\u044b\u0445", false).setVisible(() -> this.players.getValue());
    public BooleanSetting mobs = new BooleanSetting((Module)this, "\u041c\u043e\u0431\u044b", true);
    public BooleanSetting items = new BooleanSetting((Module)this, "\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b", false);
    public ModeSetting typeColor = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0422\u0435\u043c\u0430", "\u0421\u0432\u043e\u0439");
    public ColorSetting tintColor = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442", -52395).setVisible(() -> this.typeColor.is("\u0421\u0432\u043e\u0439"));
    public SliderSetting saturation = new SliderSetting(this, "\u041d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u044c", 1.15f, 0.0f, 2.0f, 0.05f);
    public SliderSetting opacity = new SliderSetting(this, "\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c", 1.0f, 0.05f, 1.0f, 0.01f);
    public BooleanSetting friendColor = new BooleanSetting((Module)this, "\u0426\u0432\u0435\u0442 \u0434\u0440\u0443\u0437\u0435\u0439", true);
    public ColorSetting friendTint = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 \u0434\u0440\u0443\u0433\u0430", -11141291).setVisible(() -> this.friendColor.getValue());
    public BooleanSetting glow = new BooleanSetting((Module)this, "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435", true);
    public SliderSetting glowRadius = new SliderSetting(this, "\u0420\u0430\u0434\u0438\u0443\u0441 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", 0.55f, 0.0f, 1.0f, 0.01f).setVisible(() -> this.glow.getValue());
    public SliderSetting glowStrength = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", 1.6f, 0.0f, 4.0f, 0.05f).setVisible(() -> this.glow.getValue());
    public SliderSetting glowFalloff = new SliderSetting(this, "\u0421\u043f\u0430\u0434 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", 1.1f, 0.2f, 4.0f, 0.05f).setVisible(() -> this.glow.getValue());
    public BooleanSetting fill = new BooleanSetting((Module)this, "\u0417\u0430\u043b\u0438\u0432\u043a\u0430", true);
    public SliderSetting fillOpacity = new SliderSetting(this, "\u041f\u043b\u043e\u0442\u043d\u043e\u0441\u0442\u044c \u0437\u0430\u043b\u0438\u0432\u043a\u0438", 0.45f, 0.0f, 1.5f, 0.01f).setVisible(() -> this.fill.getValue());
    public SliderSetting innerGlow = new SliderSetting(this, "\u0412\u043d\u0443\u0442\u0440\u0435\u043d\u043d\u0435\u0435 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u0435", 0.6f, 0.0f, 2.0f, 0.05f).setVisible(() -> this.fill.getValue());
    public BooleanSetting outline = new BooleanSetting((Module)this, "\u041e\u0431\u0432\u043e\u0434\u043a\u0430", true);
    public ModeSetting outlineMode = new ModeSetting((Module)this, "\u0422\u0438\u043f \u043e\u0431\u0432\u043e\u0434\u043a\u0438", "\u0421\u043d\u0430\u0440\u0443\u0436\u0438", "\u0412\u043d\u0443\u0442\u0440\u0438", "\u041e\u0431\u0435").setVisible(() -> this.outline.getValue());
    public SliderSetting outlineWidth = new SliderSetting(this, "\u0428\u0438\u0440\u0438\u043d\u0430 \u043e\u0431\u0432\u043e\u0434\u043a\u0438", 2.0f, 1.0f, 5.0f, 1.0f).setVisible(() -> this.outline.getValue());
    public SliderSetting outlineStrength = new SliderSetting(this, "\u042f\u0440\u043a\u043e\u0441\u0442\u044c \u043e\u0431\u0432\u043e\u0434\u043a\u0438", 1.8f, 0.0f, 3.0f, 0.05f).setVisible(() -> this.outline.getValue());
    public SliderSetting outlineWhite = new SliderSetting(this, "\u0411\u0435\u043b\u0438\u0437\u043d\u0430 \u043e\u0431\u0432\u043e\u0434\u043a\u0438", 0.45f, 0.0f, 1.0f, 0.01f).setVisible(() -> this.outline.getValue());
    public BooleanSetting pulse = new BooleanSetting((Module)this, "\u041f\u0443\u043b\u044c\u0441\u0430\u0446\u0438\u044f", false);
    public SliderSetting pulseSpeed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043f\u0443\u043b\u044c\u0441\u0430\u0446\u0438\u0438", 1.0f, 0.1f, 5.0f, 0.1f).setVisible(() -> this.pulse.getValue());
    public SliderSetting pulseAmount = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u043f\u0443\u043b\u044c\u0441\u0430\u0446\u0438\u0438", 0.3f, 0.0f, 1.0f, 0.05f).setVisible(() -> this.pulse.getValue());
    public BooleanSetting shimmer = new BooleanSetting((Module)this, "\u0428\u0438\u043c\u043c\u0435\u0440", false);
    public SliderSetting shimmerWidth = new SliderSetting(this, "\u0428\u0438\u0440\u0438\u043d\u0430 \u0448\u0438\u043c\u043c\u0435\u0440\u0430", 0.04f, 0.01f, 0.15f, 0.01f).setVisible(() -> this.shimmer.getValue());
    public SliderSetting shimmerPeriod = new SliderSetting(this, "\u041f\u0435\u0440\u0438\u043e\u0434 \u0448\u0438\u043c\u043c\u0435\u0440\u0430", 5.0f, 1.0f, 15.0f, 0.5f).setVisible(() -> this.shimmer.getValue());
    public SliderSetting shimmerBrightness = new SliderSetting(this, "\u042f\u0440\u043a\u043e\u0441\u0442\u044c \u0448\u0438\u043c\u043c\u0435\u0440\u0430", 0.8f, 0.0f, 2.0f, 0.05f).setVisible(() -> this.shimmer.getValue());
    private final PreviewSettings previewSettings = PreviewSettings.withoutInterval(this, 4.0f, 0.0f);
    private static final Set<class_1297> allTargets;
    public class_276 customFboReference;
    private class_1297 previewDummy;

    public ShaderEsp() {
        instance = this;
    }

    public static ShaderEsp getInstance() {
        return instance;
    }

    public static boolean isEspTarget(class_1297 entity) {
        return allTargets.contains(entity);
    }

    public static Set<class_1297> getAllTargets() {
        return allTargets;
    }

    public int getColor() {
        if (this.typeColor.is("\u0422\u0435\u043c\u0430")) {
            return ColorUtil.getClientColor1(1);
        }
        return this.tintColor.getValue();
    }

    @Override
    protected void onEnable() {
        ShaderEspRenderer.getInstance().invalidate();
    }

    @Override
    protected void onDisable() {
        allTargets.clear();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        allTargets.clear();
        if (this.isEnabled()) {
            ShaderEspRenderer.getInstance().invalidate();
        }
    }

    @EventHandler
    public void onTick(EventTick event) {
        allTargets.clear();
        if (ShaderEsp.mc.field_1687 == null || ShaderEsp.mc.field_1724 == null) {
            return;
        }
        for (class_1297 entity : ShaderEsp.mc.field_1687.method_18112()) {
            if (!this.shouldTarget(entity)) continue;
            allTargets.add(entity);
        }
        if (this.previewDummy != null && !this.previewDummy.method_31481()) {
            allTargets.add(this.previewDummy);
        }
    }

    @Override
    public PreviewSettings previewSettings() {
        return this.previewSettings;
    }

    @Override
    public boolean previewNeedsDummy() {
        return true;
    }

    @Override
    public void previewStart(PreviewContext ctx) {
        this.previewDummy = ctx.dummy();
    }

    @Override
    public void previewTick(PreviewContext ctx) {
        this.previewDummy = ctx.dummy();
    }

    @Override
    public void previewSpawn(PreviewContext ctx) {
    }

    @Override
    public void previewStop() {
        this.previewDummy = null;
        allTargets.clear();
    }

    public void onRender3D() {
        if (ShaderEsp.mc.field_1687 == null || ShaderEsp.mc.field_1724 == null) {
            return;
        }
        if (allTargets.isEmpty()) {
            return;
        }
        class_276 fb = mc.method_1522();
        if (fb == null) {
            return;
        }
        class_276 outlineFbo = this.customFboReference;
        if (outlineFbo == null || outlineFbo.method_71639() == null) {
            return;
        }
        ShaderEspRenderer renderer = ShaderEspRenderer.getInstance();
        this.applySettings(renderer.params);
        renderer.renderFromFbo(outlineFbo, fb.field_1482, fb.field_1481);
    }

    private void applySettings(ShaderEspOutlinePipeline.Params p) {
        p.color = this.getColor();
        p.friendEnabled = this.friendColor.getValue();
        p.friendColor = this.friendTint.getValue();
        p.opacity = this.opacity.getValue().floatValue();
        p.saturation = this.saturation.getValue().floatValue();
        p.glowEnabled = this.glow.getValue();
        p.glowRadius = this.glowRadius.getValue().floatValue();
        p.glowStrength = this.glowStrength.getValue().floatValue();
        p.glowFalloff = this.glowFalloff.getValue().floatValue();
        p.fillEnabled = this.fill.getValue();
        p.fillOpacity = this.fillOpacity.getValue().floatValue();
        p.innerGlow = this.innerGlow.getValue().floatValue();
        p.outlineEnabled = this.outline.getValue();
        p.outlineMode = this.outlineMode.getIndex();
        p.outlineWidth = this.outlineWidth.getValue().intValue();
        p.outlineStrength = this.outlineStrength.getValue().floatValue();
        p.outlineWhite = this.outlineWhite.getValue().floatValue();
        p.pulseEnabled = this.pulse.getValue();
        p.pulseSpeed = this.pulseSpeed.getValue().floatValue();
        p.pulseAmount = this.pulseAmount.getValue().floatValue();
        p.shimmerEnabled = this.shimmer.getValue();
        p.shimmerWidth = this.shimmerWidth.getValue().floatValue();
        p.shimmerPeriodSec = this.shimmerPeriod.getValue().floatValue();
        p.shimmerBrightness = this.shimmerBrightness.getValue().floatValue();
    }

    private boolean shouldTarget(class_1297 entity) {
        if (entity instanceof class_1657) {
            class_1657 p = (class_1657)entity;
            if (p.method_5797() != null && p.method_5797().getString().startsWith("Ghost_")) {
                return false;
            }
            if (!this.players.getValue().booleanValue()) {
                return false;
            }
            if (this.ignoreNaked.getValue().booleanValue() && !ShaderEsp.hasArmor(p)) {
                return false;
            }
            if (entity.method_5767()) {
                return false;
            }
        } else if (entity instanceof class_746) {
            if (!this.localPlayer.getValue().booleanValue()) {
                return false;
            }
        } else if (entity instanceof class_1309) {
            if (!this.mobs.getValue().booleanValue()) {
                return false;
            }
            if (entity.method_5767()) {
                return false;
            }
        } else if (entity instanceof class_1542) {
            if (!this.items.getValue().booleanValue()) {
                return false;
            }
        } else {
            return false;
        }
        return this.isEntityVisible(entity);
    }

    private boolean isEntityVisible(class_1297 entity) {
        class_243 entityCenter;
        if (ShaderEsp.mc.field_1687 == null || ShaderEsp.mc.field_1724 == null) {
            return false;
        }
        class_243 cameraPos = ShaderEsp.mc.field_1773.method_19418().method_71156();
        class_3965 result = ShaderEsp.mc.field_1687.method_17742(new class_3959(cameraPos, entityCenter = new class_243(entity.method_23317(), entity.method_23318() + (double)entity.method_17682() / 2.0, entity.method_23321()), class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)ShaderEsp.mc.field_1724));
        return result.method_17783() == class_239.class_240.field_1333;
    }

    private static boolean hasArmor(class_1657 p) {
        return !p.method_6118(class_1304.field_6169).method_7960() || !p.method_6118(class_1304.field_6174).method_7960() || !p.method_6118(class_1304.field_6172).method_7960() || !p.method_6118(class_1304.field_6166).method_7960();
    }

    static {
        allTargets = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    }
}

