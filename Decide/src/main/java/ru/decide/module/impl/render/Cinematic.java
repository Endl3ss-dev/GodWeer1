/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.Std140Builder
 *  com.mojang.blaze3d.buffers.Std140SizeCalculator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_156
 *  net.minecraft.class_2960
 *  net.minecraft.class_9922
 */
package ru.decide.module.impl.render;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_156;
import net.minecraft.class_2960;
import net.minecraft.class_9922;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.other.Instance;
import ru.decide.utils.render.PostEffect;

@ModuleInfo(name="Cinematic", desc="\u0426\u0432\u0435\u0442\u043e\u043a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f, \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u044f\u0440\u043a\u0438\u0445 \u043c\u0435\u0441\u0442, \u0432\u0438\u043d\u044c\u0435\u0442\u043a\u0430 \u0438 \u0437\u0435\u0440\u043d\u043e", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public final class Cinematic
extends Module {
    private static final int BLOCK_SIZE = new Std140SizeCalculator().putVec4().putVec4().putFloat().putFloat().putFloat().putFloat().putFloat().putFloat().putFloat().putFloat().putFloat().putFloat().putFloat().get();
    private static final float BASE_HEIGHT = 1080.0f;
    private static final float VIGNETTE_UNIT = 0.3f;
    private static final float BLOOM_UNIT = 0.4f;
    private static final float GRAIN_UNIT = 0.02f;
    private static final Grade NEUTRAL = new Grade(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.7f, 28.0f, 0.0f, 0.0f);
    private static final Map<String, Grade> GRADES = Map.of("\u041e\u0431\u044b\u0447\u043d\u044b\u0439", NEUTRAL, "\u041a\u0438\u043d\u043e", new Grade(0.0f, 0.03f, 0.05f, 1.08f, 1.0f, 0.9f, 1.1f, 1.12f, 1.0f, 1.15f, 0.9f, 0.7f, 28.0f, 1.0f, 0.15f), "\u0422\u0451\u043f\u043b\u044b\u0439", new Grade(0.03f, 0.015f, 0.0f, 1.1f, 1.0f, 0.85f, 1.1f, 1.05f, 1.05f, 0.65f, 1.0f, 0.7f, 28.0f, 0.5f, 0.0f), "\u0425\u043e\u043b\u043e\u0434\u043d\u044b\u0439", new Grade(0.0f, 0.02f, 0.05f, 0.9f, 1.0f, 1.12f, 0.95f, 1.08f, 1.0f, 0.85f, 0.75f, 0.7f, 28.0f, 0.5f, 0.0f), "\u0421\u043e\u0447\u043d\u044b\u0439", new Grade(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.45f, 1.15f, 1.03f, 0.5f, 0.75f, 0.72f, 24.0f, 0.0f, 0.0f), "\u041c\u0435\u0447\u0442\u0430", new Grade(0.06f, 0.03f, 0.07f, 1.05f, 0.98f, 1.08f, 1.15f, 0.9f, 1.08f, 0.85f, 2.5f, 0.55f, 44.0f, 0.5f, 0.1f), "\u041d\u0443\u0430\u0440", new Grade(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.35f, 1.0f, 2.0f, 0.6f, 0.7f, 24.0f, 2.5f, 0.0f), "\u0417\u0430\u043a\u0430\u0442", new Grade(0.05f, 0.0f, 0.06f, 1.15f, 0.95f, 0.85f, 1.2f, 1.08f, 1.02f, 1.0f, 1.25f, 0.65f, 32.0f, 0.75f, 0.1f), "\u0420\u0435\u0442\u0440\u043e", new Grade(0.06f, 0.05f, 0.03f, 1.0f, 0.97f, 0.88f, 0.8f, 0.92f, 1.0f, 1.35f, 0.75f, 0.7f, 28.0f, 3.0f, 0.6f));
    public final ModeSetting preset = new ModeSetting((Module)this, "\u0421\u0442\u0438\u043b\u044c", "\u041e\u0431\u044b\u0447\u043d\u044b\u0439", "\u041a\u0438\u043d\u043e", "\u0422\u0451\u043f\u043b\u044b\u0439", "\u0425\u043e\u043b\u043e\u0434\u043d\u044b\u0439", "\u0421\u043e\u0447\u043d\u044b\u0439", "\u041c\u0435\u0447\u0442\u0430", "\u041d\u0443\u0430\u0440", "\u0417\u0430\u043a\u0430\u0442", "\u0420\u0435\u0442\u0440\u043e");
    public final SliderSetting strength = new SliderSetting(this, "\u0421\u0438\u043b\u0430", 1.0f, 0.0f, 1.0f, 0.05f);
    public final SliderSetting bloom = new SliderSetting(this, "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u044f\u0440\u043a\u0438\u0445 \u043c\u0435\u0441\u0442", 0.0f, 0.0f, 3.0f, 0.05f);
    public final SliderSetting saturation = new SliderSetting(this, "\u041d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u044c", 1.0f, 0.0f, 2.0f, 0.05f);
    public final SliderSetting contrast = new SliderSetting(this, "\u041a\u043e\u043d\u0442\u0440\u0430\u0441\u0442", 1.0f, 0.5f, 1.5f, 0.05f);
    public final SliderSetting brightness = new SliderSetting(this, "\u042f\u0440\u043a\u043e\u0441\u0442\u044c", 1.0f, 0.5f, 1.5f, 0.05f);
    public final SliderSetting vignette = new SliderSetting(this, "\u0412\u0438\u043d\u044c\u0435\u0442\u043a\u0430", 0.0f, 0.0f, 2.0f, 0.05f);
    public final SliderSetting grain = new SliderSetting(this, "\u0417\u0435\u0440\u043d\u043e", 0.0f, 0.0f, 3.0f, 0.05f);
    private final PostEffect bloomed = new PostEffect(class_2960.method_60655((String)"decide", (String)"cinematic"), "CinematicConfig", BLOCK_SIZE, 4, this::write);
    private final PostEffect toned = new PostEffect(class_2960.method_60655((String)"decide", (String)"cinematic_tone"), "CinematicConfig", BLOCK_SIZE, this::write);

    public Cinematic() {
        this.preset.onAction(() -> this.apply(this.preset.getValue()));
    }

    public static Cinematic getInstance() {
        return Instance.get(Cinematic.class);
    }

    public void render(class_9922 allocator) {
        if (!this.isEnabled() || this.untouched()) {
            return;
        }
        (this.bloom.getValue().floatValue() > 0.0f ? this.bloomed : this.toned).run(allocator);
    }

    private boolean untouched() {
        return this.preset.is("\u041e\u0431\u044b\u0447\u043d\u044b\u0439") && Cinematic.near(this.bloom, 0.0f) && Cinematic.near(this.saturation, 1.0f) && Cinematic.near(this.contrast, 1.0f) && Cinematic.near(this.brightness, 1.0f) && Cinematic.near(this.vignette, 0.0f) && Cinematic.near(this.grain, 0.0f);
    }

    private static boolean near(SliderSetting setting, float value) {
        return Math.abs(setting.getValue().floatValue() - value) < 1.0E-4f;
    }

    private void apply(String name) {
        Grade grade = GRADES.getOrDefault(name, NEUTRAL);
        this.bloom.set(Float.valueOf(grade.bloom()));
        this.vignette.set(Float.valueOf(grade.vignette()));
        this.grain.set(Float.valueOf(grade.grain()));
    }

    private void write(Std140Builder builder) {
        Grade grade = GRADES.getOrDefault(this.preset.getValue(), NEUTRAL);
        builder.putVec4(grade.liftRed(), grade.liftGreen(), grade.liftBlue(), 0.0f).putVec4(grade.gainRed(), grade.gainGreen(), grade.gainBlue(), 1.0f).putFloat(grade.saturation() * this.saturation.getValue().floatValue()).putFloat(grade.contrast() * this.contrast.getValue().floatValue()).putFloat(grade.exposure() * this.brightness.getValue().floatValue()).putFloat(this.vignette.getValue().floatValue() * 0.3f).putFloat(this.bloom.getValue().floatValue() * 0.4f).putFloat(grade.threshold()).putFloat(grade.radius() / 1080.0f).putFloat(this.grain.getValue().floatValue() * 0.02f).putFloat(grade.chroma()).putFloat(this.strength.getValue().floatValue()).putFloat((float)(class_156.method_658() % 1000000L) / 1000.0f);
    }

    @Environment(value=EnvType.CLIENT)
    private record Grade(float liftRed, float liftGreen, float liftBlue, float gainRed, float gainGreen, float gainBlue, float saturation, float contrast, float exposure, float vignette, float bloom, float threshold, float radius, float grain, float chroma) {
    }
}

