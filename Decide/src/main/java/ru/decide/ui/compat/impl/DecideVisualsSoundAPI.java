/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.compat.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.ui.compat.SoundAPI;
import ru.decide.utils.other.GuiSounds;

@Environment(value=EnvType.CLIENT)
public final class DecideVisualsSoundAPI
implements SoundAPI {
    @Override
    public void play(String name) {
        if (name == null) {
            return;
        }
        switch (name.toLowerCase()) {
            case "open": {
                GuiSounds.open();
                break;
            }
            case "close": {
                GuiSounds.close();
                break;
            }
            case "click": {
                GuiSounds.button();
                break;
            }
            case "toggle": {
                GuiSounds.toggle(true);
                break;
            }
            case "slider_grab": {
                GuiSounds.sliderGrab();
                break;
            }
            case "slider_tick": {
                GuiSounds.sliderTick(0.5f);
                break;
            }
            case "slider_release": {
                GuiSounds.sliderRelease();
                break;
            }
            case "bind_start": {
                GuiSounds.bindStart();
                break;
            }
            case "bind_set": {
                GuiSounds.bindSet();
                break;
            }
            case "bind_reset": {
                GuiSounds.bindReset();
                break;
            }
            case "edit_start": {
                GuiSounds.editStart();
                break;
            }
            case "edit_commit": {
                GuiSounds.editCommit();
                break;
            }
            case "edit_cancel": {
                GuiSounds.editCancel();
                break;
            }
            case "type": {
                GuiSounds.type();
                break;
            }
            case "erase": {
                GuiSounds.erase();
                break;
            }
            case "search_clear": {
                GuiSounds.searchClear();
                break;
            }
            case "scroll": {
                GuiSounds.scroll();
                break;
            }
            case "expand": {
                GuiSounds.expand(true);
                break;
            }
            default: {
                GuiSounds.button();
            }
        }
    }
}

