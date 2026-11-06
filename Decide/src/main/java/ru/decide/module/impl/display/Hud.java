/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1293
 *  net.minecraft.class_310
 *  org.joml.Vector2f
 */
package ru.decide.module.impl.display;

import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1293;
import net.minecraft.class_310;
import org.joml.Vector2f;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.DragSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.MultiBooleanSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.theme.ThemeColor;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.satoshi.EaseInOutQuad;
import ru.decide.utils.render.GifTexture;

@ModuleInfo(name="Hud", desc="\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0430 \u0438\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441\u0430 (HUD) \u043a\u043b\u0438\u0435\u043d\u0442\u0430", category=Category.HUD)
@Environment(value=EnvType.CLIENT)
public class Hud
extends Module {
    public BooleanSetting waterMark = new BooleanSetting("\u041b\u043e\u0433\u043e \u043a\u043b\u0438\u0435\u043d\u0442\u0430", true);
    public BooleanSetting cortds = new BooleanSetting("\u041a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b", true);
    public BooleanSetting bpstps = new BooleanSetting("BPS \u0438 TPS", true);
    public BooleanSetting porionList = new BooleanSetting("\u0410\u043a\u0442\u0438\u0432\u043d\u044b\u0435 \u044d\u0444\u0444\u0435\u043a\u0442\u044b", true);
    public BooleanSetting keybindslist = new BooleanSetting("\u0410\u043a\u0442\u0438\u0432\u043d\u044b\u0435 \u043a\u043b\u0430\u0432\u0438\u0448\u0438", true);
    public BooleanSetting arrayListHude = new BooleanSetting("\u0410\u0440\u0440\u0430\u0439 \u043b\u0438\u0441\u0442", false);
    public BooleanSetting armorHud = new BooleanSetting("Armor", true);
    public BooleanSetting inventoryHud = new BooleanSetting("Inventory", true);
    public MultiBooleanSetting elempt = new MultiBooleanSetting((Module)this, "\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0442\u044c", this.waterMark, this.keybindslist, this.porionList, this.bpstps, this.cortds, this.arrayListHude, this.armorHud, this.inventoryHud);
    public SliderSetting volume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0443\u0432\u0435\u0434\u043e\u043c\u043b\u0435\u043d\u0438\u044f", 0.5f, 0.1f, 1.0f, 0.1f);
    public ModeSetting typeNotify = new ModeSetting((Module)this, "\u0422\u0438\u043f \u0443\u0432\u0435\u0434\u043e\u043c\u043b\u0435\u043d\u0438\u044f", "\u041f\u0435\u0440\u0432\u044b\u0439", "\u0412\u0442\u043e\u0440\u043e\u0439", "\u0422\u0440\u0435\u0442\u0438\u0439");
    public Animation withpan = new Animation();
    public DragSetting watermark_drag = new DragSetting(this, "watermark_draggings");
    public DragSetting keybinds_drag = new DragSetting(this, "Keybinds_dragings");
    public DragSetting potions_drag = new DragSetting(this, "Potions_dragings");
    public DragSetting clickbinds_drag = new DragSetting((Module)this, "ClickBinds_dragings", new Vector2f(8.0f, 100.0f));
    public DragSetting arraylist_drag = new DragSetting((Module)this, "ArrayList_dragings", new Vector2f(10000.0f, 4.0f));
    public DragSetting armor_drag = new DragSetting((Module)this, "Armor_dragings", new Vector2f(160.0f, 90.0f));
    public DragSetting inventory_drag = new DragSetting((Module)this, "Inventory_dragings", new Vector2f(160.0f, 170.0f));
    public DragSetting cords_drag = new DragSetting((Module)this, "Cords_dragings", new Vector2f(-999.0f, -999.0f));
    public DragSetting bps_drag = new DragSetting((Module)this, "Bps_dragings", new Vector2f(-999.0f, -999.0f));
    public DragSetting tps_drag = new DragSetting((Module)this, "Tps_dragings", new Vector2f(-999.0f, -999.0f));
    private float smoothMaxKeyWidth = -1.0f;
    public Animation mainAnim = new Animation();
    public Animation keybindsWidthAnim = new Animation();
    public Animation keybindsMaxKeyWidthAnim = new Animation();
    private final Map<String, EffectData> displayedEffects = new LinkedHashMap<String, EffectData>();
    ru.decide.utils.animation.satoshi.Animation animation = new EaseInOutQuad(200, 1.0);
    public class_310 mc = class_310.method_1551();
    private double prevX;
    private double prevY;
    private double prevZ;
    private long prevTime;
    private float bps;
    private GifTexture testGif;
    private boolean gifLoaded = false;

    public static boolean getShadow() {
        return false;
    }

    public static int getVisualsColor() {
        return ThemeColor.getVisualColor();
    }

    public static int getHudColor() {
        return ThemeColor.getHudColor();
    }

    public static float getAlpha() {
        return ThemeColor.getOpacity() < 0.98f ? ThemeColor.getOpacity() : 0.98f;
    }

    @Environment(value=EnvType.CLIENT)
    private static class EffectData {
        String name;
        String duration;
        Animation animation;
        boolean active;
        boolean negative;
        int durationTicks;
        int lvl;
        class_1293 effectInstance;

        public EffectData(String name, String duration, int lvl, boolean negative, int durationTicks, class_1293 effectInstance) {
            this.name = name;
            this.duration = duration;
            this.lvl = lvl;
            this.negative = negative;
            this.durationTicks = durationTicks;
            this.animation = new Animation();
            this.active = true;
            this.effectInstance = effectInstance;
        }
    }
}

