package org.privates.client.managers.module;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    COMBAT("Combat", "a"),
    MOVEMENT("Movement", "b"),
    PLAYER("Player", "d"),
    RENDER("Render", "c"),
    MISC("Misc", "e");

    private final String name;
    private final String icon;
}