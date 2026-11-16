/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.manager.event_impl;

import java.util.regex.Pattern;
import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.manager.events.Event;

@Environment(value=EnvType.CLIENT)
public class TextFactoryEvent
extends Event {
    private String text;

    public void replaceText(String protect, String replaced) {
        if (this.text == null || this.text.isEmpty() || protect == null || protect.isEmpty()) {
            return;
        }
        if (this.text.toLowerCase().contains(protect.toLowerCase())) {
            this.text = this.text.replaceAll("(?ui)" + Pattern.quote(protect), replaced);
        }
    }

    public void replaceRegex(String regex, String replaced) {
        if (this.text == null || this.text.isEmpty()) {
            return;
        }
        this.text = this.text.replaceAll(regex, replaced);
    }

    @Generated
    public void setText(String text) {
        this.text = text;
    }

    @Generated
    public String getText() {
        return this.text;
    }

    @Generated
    public TextFactoryEvent(String text) {
        this.text = text;
    }
}

