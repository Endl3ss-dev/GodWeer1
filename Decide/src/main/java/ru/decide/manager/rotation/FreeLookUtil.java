/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3532
 */
package ru.decide.manager.rotation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3532;
import ru.decide.manager.event_impl.EventLook;
import ru.decide.manager.event_impl.EventPacket;
import ru.decide.manager.event_impl.EventRotation;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.manager.rotation.Component;
import ru.decide.utils.math.ServerUtil;

@Environment(value=EnvType.CLIENT)
public class FreeLookUtil
extends Component {
    public static boolean active;
    public static boolean forced;
    public static float freeYaw;
    public static float freePitch;

    public static void setActive(boolean value) {
        if (!value && forced) {
            return;
        }
        active = value;
    }

    @EventHandler
    public void onEvent(EventLook event) {
        if (active) {
            this.rotateTowards(event.getYaw(), event.getPitch());
            event.cancel();
        }
    }

    @EventHandler
    public void onEvent(EventRotation event) {
        if (active) {
            event.setYaw(freeYaw);
            event.setPitch(freePitch);
        } else {
            freeYaw = event.getYaw();
            freePitch = event.getPitch();
        }
    }

    @EventHandler
    public void onEvent(EventTick event) {
        ServerUtil.tick();
    }

    @EventHandler
    public void onPacket(EventPacket e) {
        ServerUtil.packet(e);
    }

    private void rotateTowards(double targetYaw, double targetPitch) {
        freePitch = class_3532.method_15363((float)((float)((double)freePitch + targetPitch * 0.15)), (float)-90.0f, (float)90.0f);
        freeYaw = (float)((double)freeYaw + targetYaw * 0.15);
    }
}

