/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.screen.ScreenHandler
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventContainerRender;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.ScreenHandler;

public class EventHandledScreenRenderPre
extends EventContainerRender
implements CancellableEvent {
    public EventHandledScreenRenderPre(DrawContext param1, int param2, int param3, ScreenHandler param4) {
        super(param1, param2, param3, param4);
    }
}

