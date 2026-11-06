/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector2f
 */
package ru.decide.module.impl.display;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector2f;
import ru.decide.manager.event_impl.EventDisplay;
import ru.decide.manager.event_impl.EventUpdate;
import ru.decide.manager.event_impl.MousePressEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.DragSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.MultiBooleanSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.api.settings.impl.StringSetting;
import ru.decide.module.impl.display.interfaceimpl.ArmorHud;
import ru.decide.module.impl.display.interfaceimpl.Cooldowns;
import ru.decide.module.impl.display.interfaceimpl.Information;
import ru.decide.module.impl.display.interfaceimpl.KeyBinds;
import ru.decide.module.impl.display.interfaceimpl.MusicHud;
import ru.decide.module.impl.display.interfaceimpl.Notify;
import ru.decide.module.impl.display.interfaceimpl.Potions;
import ru.decide.module.impl.display.interfaceimpl.TargetHud;
import ru.decide.module.impl.display.interfaceimpl.WaterMark;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="InterFace", desc="\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0430 \u0438\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441\u0430 (HUD) \u043a\u043b\u0438\u0435\u043d\u0442\u0430", category=Category.HUD)
@Environment(value=EnvType.CLIENT)
public class InterFace
extends Module {
    public MultiBooleanSetting element = new MultiBooleanSetting((Module)this, "\u042d\u043b\u0435\u043c\u0435\u043d\u0442\u044b", new BooleanSetting("Water mark", true), new BooleanSetting("Information", true), new BooleanSetting("Key Binds", true), new BooleanSetting("Potions", true), new BooleanSetting("Cooldowns", true), new BooleanSetting("Music Player", true), new BooleanSetting("Notifications", true), new BooleanSetting("Target Hud", true), new BooleanSetting("Armor Hud", true));
    public SliderSetting volume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0443\u0432\u0435\u0434\u043e\u043c\u043b\u0435\u043d\u0438\u044f", 0.5f, 0.1f, 1.0f, 0.1f);
    public ModeSetting typeNotify = new ModeSetting((Module)this, "\u0422\u0438\u043f \u0443\u0432\u0435\u0434\u043e\u043c\u043b\u0435\u043d\u0438\u044f", "\u041f\u0435\u0440\u0432\u044b\u0439", "\u0412\u0442\u043e\u0440\u043e\u0439", "\u0422\u0440\u0435\u0442\u0438\u0439");
    public SliderSetting sizeHud = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u0438\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441\u0430", 1.0f, 0.5f, 1.5f, 0.05f);
    public SliderSetting alphaHUD = new SliderSetting(this, "\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0445\u0443\u0434\u0430", 0.6f, 0.0f, 0.9f, 0.1f);
    public StringSetting userName = new StringSetting((Module)this, "\u0418\u043c\u044f \u0432 \u0445\u0443\u0434\u0435", "User");
    public BooleanSetting notifyEffects = new BooleanSetting((Module)this, "\u044d\u0444\u0444\u0435\u043a\u0442\u0430\u0445", true);
    public BooleanSetting notifyModules = new BooleanSetting((Module)this, "\u043c\u043e\u0434\u0443\u043b\u044f\u0445", true);
    public BooleanSetting notifyArmor = new BooleanSetting((Module)this, "\u0431\u0440\u043e\u043d\u0435", true);
    public DragSetting waterMark = new DragSetting((Module)this, "WaterMark", new Vector2f(10.0f, 10.0f));
    public DragSetting information = new DragSetting((Module)this, "Information", new Vector2f(10.0f, 30.0f));
    public DragSetting keyBind = new DragSetting((Module)this, "Key Binds", new Vector2f(10.0f, 33.0f));
    public DragSetting potion = new DragSetting((Module)this, "Potions", new Vector2f(90.0f, 33.0f));
    public DragSetting cooldowns = new DragSetting((Module)this, "Cooldowns", new Vector2f(160.0f, 33.0f));
    public DragSetting music = new DragSetting((Module)this, "Music Player", new Vector2f(10.0f, 400.0f));
    public DragSetting notifications = new DragSetting((Module)this, "Notifications", new Vector2f(0.0f, 200.0f));
    public DragSetting targetHudDrag = new DragSetting((Module)this, "Target Hud", new Vector2f(90.0f, 40.0f));
    public DragSetting armorHud = new DragSetting((Module)this, "Armor Hud", new Vector2f(300.0f, 200.0f));
    private final WaterMark waterMarkElemnt = new WaterMark();
    private final Information informationElemnt = new Information();
    private final KeyBinds keyBinds = new KeyBinds();
    private final Potions potions = new Potions();
    private final Cooldowns cooldownsHud = new Cooldowns();
    private final MusicHud musicHud = new MusicHud();
    private final Notify notifyHud = new Notify();
    private final TargetHud targetHud = new TargetHud();
    private final ArmorHud armorHudElement = new ArmorHud();

    public static InterFace getInstance() {
        return Instance.get(InterFace.class);
    }

    public InterFace() {
        this.notifications.lockX = true;
    }

    @Override
    protected void onDisable() {
        this.musicHud.shutdown();
    }

    @EventHandler
    public void onUpdate(EventUpdate event) {
        if (InterFace.mc.field_1724 == null || InterFace.mc.field_1687 == null || !this.isEnabled()) {
            return;
        }
        if (this.element.getValue("Music Player")) {
            this.musicHud.onTick();
        }
        if (this.element.getValue("Notifications")) {
            this.notifyHud.onTick(this.notifyModules, this.notifyArmor, this.notifyEffects);
        }
    }

    @EventHandler
    public void onMousePress(MousePressEvent event) {
        if (!this.isEnabled()) {
            return;
        }
        if (this.element.getValue("Music Player")) {
            this.musicHud.onMouseClick(event);
        }
    }

    @EventHandler
    public void onDisplayEvent(EventDisplay eventDisplay) {
        if (InterFace.mc.field_1724 == null || InterFace.mc.field_1687 == null || !this.isEnabled()) {
            return;
        }
        try {
            if (this.element.getValue("Potions")) {
                this.potions.onRender(this.potion, this, eventDisplay);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (this.element.getValue("Cooldowns")) {
                this.cooldownsHud.onRender(this.cooldowns, this, eventDisplay);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (this.element.getValue("Information")) {
                this.informationElemnt.onRender(this.information, this);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (this.element.getValue("Key Binds")) {
                this.keyBinds.onRender(this.keyBind, this);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (this.element.getValue("Water mark")) {
                this.waterMarkElemnt.onRender(this.waterMark, this);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (this.element.getValue("Music Player")) {
                this.musicHud.onRender(this.music, this);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (this.element.getValue("Notifications")) {
                this.notifyHud.onRender(this.notifications, this, eventDisplay);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (this.element.getValue("Target Hud")) {
                this.targetHud.onRender(this.targetHudDrag, this, eventDisplay);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (this.element.getValue("Armor Hud")) {
                this.armorHudElement.onRender(this.armorHud, this, eventDisplay);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

