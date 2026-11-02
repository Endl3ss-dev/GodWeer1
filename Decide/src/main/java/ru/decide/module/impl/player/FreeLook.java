/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_5498
 *  org.lwjgl.glfw.GLFW
 */
package ru.decide.module.impl.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5498;
import org.lwjgl.glfw.GLFW;
import ru.decide.manager.event_impl.EventKey;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.manager.rotation.FreeLookUtil;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BindSetting;
import ru.decide.module.api.settings.impl.ModeSetting;

@ModuleInfo(name="Free Look", desc="\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u043a\u0440\u0443\u0442\u0438\u0442\u044c \u0441\u0432\u043e\u0431\u043e\u0434\u043d\u043e \u043a\u0430\u043c\u0435\u0440\u043e\u0439.", category=Category.UTILITIES)
@Environment(value=EnvType.CLIENT)
public class FreeLook
extends Module {
    public ModeSetting type = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c", "\u0417\u0430\u0436\u0430\u0442\u044c", "\u041d\u0430\u0436\u0430\u0442\u044c");
    public BindSetting bind = new BindSetting((Module)this, "\u041a\u043b\u0430\u0432\u0438\u0448\u0430", -1);
    private boolean looking;

    @Override
    public void onDisable() {
        if (this.looking) {
            this.stopLook();
        }
    }

    @EventHandler
    public void onKey(EventKey event) {
        int key = this.bind.get();
        if (key <= 0 || event.getKey() != key) {
            return;
        }
        if (this.type.is("\u041d\u0430\u0436\u0430\u0442\u044c")) {
            if (this.looking) {
                this.stopLook();
            } else {
                this.startLook();
            }
        } else if (!this.looking) {
            this.startLook();
        }
    }

    @EventHandler
    public void onTick(EventTick event) {
        int key = this.bind.get();
        if (key <= 0) {
            if (this.looking) {
                this.stopLook();
            }
            return;
        }
        if (this.type.is("\u0417\u0430\u0436\u0430\u0442\u044c")) {
            boolean down;
            boolean bl = down = mc.method_22683() != null && GLFW.glfwGetKey((long)mc.method_22683().method_4490(), (int)key) == 1;
            if (down && !this.looking) {
                this.startLook();
            } else if (!down && this.looking) {
                this.stopLook();
            }
        }
        if (this.looking) {
            FreeLookUtil.active = true;
        }
    }

    private void startLook() {
        if (FreeLook.mc.field_1724 == null) {
            return;
        }
        this.looking = true;
        FreeLookUtil.freeYaw = FreeLook.mc.field_1724.method_36454();
        FreeLookUtil.freePitch = FreeLook.mc.field_1724.method_36455();
        FreeLookUtil.forced = true;
        FreeLookUtil.active = true;
        FreeLook.mc.field_1690.method_31043(class_5498.field_26665);
    }

    private void stopLook() {
        this.looking = false;
        FreeLookUtil.forced = false;
        FreeLookUtil.active = false;
        if (FreeLook.mc.field_1690 != null) {
            FreeLook.mc.field_1690.method_31043(class_5498.field_26664);
        }
    }
}

