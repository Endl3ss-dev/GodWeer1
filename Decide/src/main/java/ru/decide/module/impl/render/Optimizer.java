/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_315
 *  net.minecraft.class_703
 */
package ru.decide.module.impl.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_703;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.event_impl.WorldLoadEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.MultiBooleanSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="Optimizer", desc="\u041e\u043f\u0442\u0438\u043c\u0438\u0437\u0430\u0446\u0438\u044f \u043e\u0442\u0440\u0438\u0441\u043e\u0432\u043a\u0438", category=Category.UTILITIES)
@Environment(value=EnvType.CLIENT)
public class Optimizer
extends Module {
    private static final int SAMPLES = 60;
    private static final double[] PARTICLE_DIST_SQ = new double[]{2304.0, 1024.0, 484.0};
    private static final double[] BLOCK_ENTITY_DIST_SQ = new double[]{2304.0, 1296.0, 576.0};
    private static final double[] ENTITY_TIER_CAP = new double[]{-1.0, 51.0, 29.0};
    private static final int[] RENDER_DISTANCE_CAP = new int[]{0, 12, 8};
    private static Optimizer instance;
    public final MultiBooleanSetting options = new MultiBooleanSetting((Module)this, "\u041e\u043f\u0446\u0438\u0438", new BooleanSetting("\u0421\u0438\u043d\u0445\u0440\u043e\u043d\u0438\u0437\u0430\u0446\u0438\u044f \u043a\u0430\u0434\u0440\u043e\u0432", true), new BooleanSetting("\u0411\u0435\u0437 \u0440\u0430\u0437\u043c\u044b\u0442\u0438\u044f", false), new BooleanSetting("\u0411\u0435\u0437 \u0442\u0440\u0430\u0432\u044b", false), new BooleanSetting("\u0411\u0435\u0437 \u0447\u0430\u0441\u0442\u0438\u0446", false), new BooleanSetting("\u0411\u0435\u0437 \u043f\u043e\u0433\u043e\u0434\u044b", false), new BooleanSetting("\u041e\u0442\u0441\u0435\u0447\u0435\u043d\u0438\u0435 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439", false), new BooleanSetting("\u041e\u043a\u043a\u043b\u044e\u0437\u0438\u044f \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439", true), new BooleanSetting("\u041e\u0433\u0440\u0430\u043d\u0438\u0447\u0435\u043d\u0438\u0435 \u0434\u0430\u043b\u044c\u043d\u043e\u0441\u0442\u0438", true));
    public final SliderSetting entityDistance = new SliderSetting(this, "\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439", 48.0f, 16.0f, 128.0f, 1.0f);
    public final SliderSetting blockEntityDistance = new SliderSetting(this, "\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u0431\u043b\u043e\u043a-\u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439", 32.0f, 8.0f, 96.0f, 1.0f);
    public final ModeSetting tier = new ModeSetting((Module)this, "\u0423\u0440\u043e\u0432\u0435\u043d\u044c", "\u041d\u0438\u0437\u043a\u0438\u0439", "\u0421\u0440\u0435\u0434\u043d\u0438\u0439", "\u0423\u043b\u044c\u0442\u0440\u0430");
    private final long[] frameTimes = new long[60];
    private int frameIndex;
    private int frameCount;
    private int currentFps = 60;
    private float qualityLevel = 1.0f;
    private int savedViewDistance = -1;
    private int appliedCap = -1;
    private int lastTier = -1;
    private boolean lastLimit;
    private int lastSeenDist = -1;

    public Optimizer() {
        instance = this;
    }

    public static Optimizer getInstance() {
        return Instance.get(Optimizer.class);
    }

    public static boolean active() {
        return instance != null && instance.isEnabled();
    }

    public int tierIndex() {
        if (this.tier.is("\u0423\u043b\u044c\u0442\u0440\u0430")) {
            return 2;
        }
        return this.tier.is("\u0421\u0440\u0435\u0434\u043d\u0438\u0439") ? 1 : 0;
    }

    private boolean option(String name) {
        return this.options.getValue(name);
    }

    public static boolean isNoBlur() {
        return Optimizer.active() && instance.option("\u0411\u0435\u0437 \u0440\u0430\u0437\u043c\u044b\u0442\u0438\u044f");
    }

    public static boolean isNoGrass() {
        return Optimizer.active() && instance.option("\u0411\u0435\u0437 \u0442\u0440\u0430\u0432\u044b");
    }

    public static boolean isNoParticles() {
        return Optimizer.active() && instance.option("\u0411\u0435\u0437 \u0447\u0430\u0441\u0442\u0438\u0446");
    }

    public static boolean isNoWeather() {
        return Optimizer.active() && instance.option("\u0411\u0435\u0437 \u043f\u043e\u0433\u043e\u0434\u044b");
    }

    public static boolean occludeEntities() {
        return Optimizer.active() && instance.option("\u041e\u043a\u043a\u043b\u044e\u0437\u0438\u044f \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439");
    }

    public static int occludeTier() {
        return instance == null ? 0 : instance.tierIndex();
    }

    public static boolean cullClouds() {
        return Optimizer.active() && instance.tierIndex() == 2;
    }

    public static float entityCullDist() {
        if (Optimizer.active() && instance.option("\u041e\u0442\u0441\u0435\u0447\u0435\u043d\u0438\u0435 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439")) {
            double cap = ENTITY_TIER_CAP[instance.tierIndex()];
            double distance = Optimizer.instance.entityDistance.getValue().floatValue();
            return (float)(cap > 0.0 ? Math.min(distance, cap) : distance);
        }
        return -1.0f;
    }

    public static float blockEntityCullDist() {
        if (!Optimizer.active()) {
            return -1.0f;
        }
        double cap = Math.sqrt(BLOCK_ENTITY_DIST_SQ[instance.tierIndex()]);
        if (instance.option("\u041e\u0442\u0441\u0435\u0447\u0435\u043d\u0438\u0435 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439")) {
            cap = Math.min(cap, (double)Optimizer.instance.blockEntityDistance.getValue().floatValue());
        }
        return (float)cap;
    }

    public static boolean allowParticle(class_703 particle) {
        if (!Optimizer.active()) {
            return true;
        }
        if (instance.option("\u0411\u0435\u0437 \u0447\u0430\u0441\u0442\u0438\u0446")) {
            return false;
        }
        class_310 client = class_310.method_1551();
        if (client == null || client.field_1773 == null) {
            return true;
        }
        class_243 cam = client.field_1773.method_19418().method_71156();
        if (cam == null) {
            return true;
        }
        class_238 box = particle.method_3064();
        double x = (box.field_1323 + box.field_1320) * 0.5;
        double dx = x - cam.field_1352;
        double y = (box.field_1322 + box.field_1325) * 0.5;
        double dy = y - cam.field_1351;
        double z = (box.field_1321 + box.field_1324) * 0.5;
        double dz = z - cam.field_1350;
        return dx * dx + dy * dy + dz * dz <= PARTICLE_DIST_SQ[instance.tierIndex()];
    }

    public static int blurSteps() {
        if (!Optimizer.active()) {
            return 5;
        }
        return Math.max(2, Math.round(2.0f + 3.0f * Optimizer.instance.qualityLevel));
    }

    public static float blurOffset() {
        if (!Optimizer.active()) {
            return 1.0f;
        }
        return 1.0f + 1.5f * (1.0f - Optimizer.instance.qualityLevel);
    }

    public static boolean fastGradient() {
        return Optimizer.active() && Optimizer.instance.qualityLevel < 0.7f;
    }

    public static boolean flatBackdrop() {
        return Optimizer.active() && Optimizer.instance.qualityLevel < 0.5f;
    }

    public static int estimatedFps() {
        return instance == null ? 60 : Optimizer.instance.currentFps;
    }

    public static float quality() {
        return Optimizer.active() ? Optimizer.instance.qualityLevel : 1.0f;
    }

    @Override
    protected void onEnable() {
        this.lastTier = -1;
        this.lastLimit = false;
        this.lastSeenDist = -1;
        this.enforceRenderDistance();
    }

    @Override
    protected void onDisable() {
        this.restoreRenderDistance();
        this.lastTier = -1;
        this.lastLimit = false;
        this.lastSeenDist = -1;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        this.restoreRenderDistance();
        this.lastTier = -1;
        this.lastLimit = false;
        this.lastSeenDist = -1;
    }

    @EventHandler
    public void onTick(EventTick e) {
        this.enforceRenderDistance();
    }

    @EventHandler
    public void onRender3D(EventRender3D e) {
        Optimizer.sampleFrame();
    }

    public static void sampleFrame() {
        long now;
        Optimizer self = instance;
        if (self == null || !self.isEnabled() || !self.option("\u0421\u0438\u043d\u0445\u0440\u043e\u043d\u0438\u0437\u0430\u0446\u0438\u044f \u043a\u0430\u0434\u0440\u043e\u0432")) {
            return;
        }
        self.frameTimes[self.frameIndex] = now = System.nanoTime();
        self.frameIndex = (self.frameIndex + 1) % 60;
        if (self.frameCount < 60) {
            ++self.frameCount;
        }
        if (self.frameCount < 2) {
            self.currentFps = 60;
            self.qualityLevel = 1.0f;
            return;
        }
        int newest = (self.frameIndex - 1 + 60) % 60;
        int oldest = self.frameCount < 60 ? 0 : self.frameIndex;
        long delta = self.frameTimes[newest] - self.frameTimes[oldest];
        if (delta > 0L) {
            self.currentFps = (int)Math.round((double)(self.frameCount - 1) * 1.0E9 / (double)delta);
        }
        self.qualityLevel = self.currentFps >= 55 ? 1.0f : (self.currentFps >= 35 ? 0.7f : (self.currentFps >= 20 ? 0.45f : 0.25f));
    }

    private void enforceRenderDistance() {
        class_315 options = class_310.method_1551().field_1690;
        if (options == null) {
            return;
        }
        int current = (Integer)options.method_42503().method_41753();
        int currentTier = this.tierIndex();
        boolean limit = this.option("\u041e\u0433\u0440\u0430\u043d\u0438\u0447\u0435\u043d\u0438\u0435 \u0434\u0430\u043b\u044c\u043d\u043e\u0441\u0442\u0438");
        if (currentTier == this.lastTier && limit == this.lastLimit && current == this.lastSeenDist) {
            return;
        }
        this.lastTier = currentTier;
        this.lastLimit = limit;
        int cap = RENDER_DISTANCE_CAP[currentTier];
        if (!limit || cap <= 0) {
            this.restoreRenderDistance();
            this.lastSeenDist = (Integer)options.method_42503().method_41753();
        } else if (current > cap) {
            if (this.savedViewDistance < 0) {
                this.savedViewDistance = current;
            }
            this.appliedCap = cap;
            options.method_42503().method_41748((Object)cap);
            this.lastSeenDist = cap;
        } else {
            this.lastSeenDist = current;
        }
    }

    private void restoreRenderDistance() {
        if (this.savedViewDistance < 0) {
            return;
        }
        try {
            class_315 options = class_310.method_1551().field_1690;
            if (options != null) {
                int current = (Integer)options.method_42503().method_41753();
                if (this.appliedCap < 0 || current == this.appliedCap) {
                    options.method_42503().method_41748((Object)this.savedViewDistance);
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.savedViewDistance = -1;
        this.appliedCap = -1;
    }
}

