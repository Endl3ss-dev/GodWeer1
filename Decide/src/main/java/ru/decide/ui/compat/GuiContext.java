/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.compat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.ui.compat.AccentAPI;
import ru.decide.ui.compat.ColorAPI;
import ru.decide.ui.compat.FontAPI;
import ru.decide.ui.compat.PositionAPI;
import ru.decide.ui.compat.RenderAPI;
import ru.decide.ui.compat.RenderHelperAPI;
import ru.decide.ui.compat.SoundAPI;
import ru.decide.ui.compat.impl.DecideVisualsAccentAPI;
import ru.decide.ui.compat.impl.DecideVisualsColorAPI;
import ru.decide.ui.compat.impl.DecideVisualsFontAPI;
import ru.decide.ui.compat.impl.DecideVisualsPositionAPI;
import ru.decide.ui.compat.impl.DecideVisualsRenderAPI;
import ru.decide.ui.compat.impl.DecideVisualsRenderHelperAPI;
import ru.decide.ui.compat.impl.DecideVisualsSoundAPI;

@Environment(value=EnvType.CLIENT)
public final class GuiContext {
    private static GuiContext INSTANCE;
    private RenderAPI render;
    private FontAPI font;
    private ColorAPI color;
    private AccentAPI accent;
    private PositionAPI position;
    private SoundAPI sound;
    private RenderHelperAPI renderHelper;

    public static GuiContext get() {
        if (INSTANCE == null) {
            INSTANCE = new GuiContext();
        }
        return INSTANCE;
    }

    public static void registerDecideVisuals() {
        GuiContext.get().init(new DecideVisualsRenderAPI(), new DecideVisualsFontAPI(), new DecideVisualsColorAPI(), new DecideVisualsAccentAPI(), new DecideVisualsPositionAPI(), new DecideVisualsSoundAPI(), new DecideVisualsRenderHelperAPI());
    }

    public void init(RenderAPI render, FontAPI font, ColorAPI color, AccentAPI accent, PositionAPI position, SoundAPI sound, RenderHelperAPI renderHelper) {
        this.render = render;
        this.font = font;
        this.color = color;
        this.accent = accent;
        this.position = position;
        this.sound = sound;
        this.renderHelper = renderHelper;
    }

    public RenderAPI render() {
        return this.render;
    }

    public FontAPI font() {
        return this.font;
    }

    public ColorAPI color() {
        return this.color;
    }

    public AccentAPI accent() {
        return this.accent;
    }

    public PositionAPI position() {
        return this.position;
    }

    public SoundAPI sound() {
        return this.sound;
    }

    public RenderHelperAPI renderHelper() {
        return this.renderHelper;
    }
}

