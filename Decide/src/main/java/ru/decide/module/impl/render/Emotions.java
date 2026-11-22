/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 */
package ru.decide.module.impl.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_437;
import ru.decide.manager.event_impl.EventDisplay;
import ru.decide.manager.event_impl.EventKey;
import ru.decide.manager.event_impl.EventKeyRelease;
import ru.decide.manager.event_impl.EventLook;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.event_impl.MousePressEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BindSetting;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ButtonSetting;
import ru.decide.module.impl.render.emotions.EmotionWheelManager;
import ru.decide.screen.EmotionSettingsScreen;

@ModuleInfo(name="Emotions", desc="\u041a\u043e\u043b\u0435\u0441\u043e \u044d\u043c\u043e\u0446\u0438\u0439 \u0441 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0435\u0439 \u0438\u0433\u0440\u043e\u043a\u0430", category=Category.VISUALS, autoEnabled=true)
@Environment(value=EnvType.CLIENT)
public class Emotions
extends Module {
    private static Emotions instance;
    private final BindSetting wheelBind = new BindSetting((Module)this, "\u041a\u043e\u043b\u0435\u0441\u043e \u044d\u043c\u043e\u0446\u0438\u0439", 86);
    public final BooleanSetting noCancelOnMove = new BooleanSetting((Module)this, "\u041d\u0435 \u043e\u0442\u043c\u0435\u043d\u044f\u0442\u044c \u044d\u043c\u043e\u0446\u0438\u0438 \u043f\u0440\u0438 \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u0438", false);
    public final BooleanSetting f5OnEmotion = new BooleanSetting((Module)this, "F5 \u043f\u0440\u0438 \u044d\u043c\u043e\u0446\u0438\u0438", true);
    public final ButtonSetting openSettings = new ButtonSetting((Module)this, "\u041e\u0442\u043a\u0440\u044b\u0442\u044c \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u044d\u043c\u043e\u0446\u0438\u0439", () -> {
        class_310 mc = class_310.method_1551();
        mc.method_1507((class_437)new EmotionSettingsScreen(mc.field_1755));
    });

    public Emotions() {
        instance = this;
    }

    public static Emotions getInstance() {
        return instance;
    }

    public BindSetting getWheelBind() {
        return this.wheelBind;
    }

    public BooleanSetting getNoCancelOnMove() {
        return this.noCancelOnMove;
    }

    public BooleanSetting getF5OnEmotion() {
        return this.f5OnEmotion;
    }

    @EventHandler
    public void onKey(EventKey event) {
        EmotionWheelManager.getInstance().onKeyPress(event.getKey());
    }

    @EventHandler
    public void onKeyRelease(EventKeyRelease event) {
        EmotionWheelManager.getInstance().onKeyRelease(event.getKey());
    }

    @EventHandler
    public void onMouse(EventLook event) {
        EmotionWheelManager manager = EmotionWheelManager.getInstance();
        if (manager.isMouseCaptured()) {
            manager.onMouseMove(event.getYaw(), event.getPitch());
            event.cancel();
        }
    }

    @EventHandler
    public void onMousePress(MousePressEvent event) {
        EmotionWheelManager.getInstance().onMousePress(event.getButton());
    }

    @EventHandler
    public void onTick(EventTick event) {
        EmotionWheelManager.getInstance().onTick();
    }

    @EventHandler
    public void onDisplay(EventDisplay event) {
        EmotionWheelManager.getInstance().render();
    }

    @Override
    protected void onDisable() {
        EmotionWheelManager manager = EmotionWheelManager.getInstance();
        manager.stopEmote();
        manager.closeWheel(true);
    }
}

