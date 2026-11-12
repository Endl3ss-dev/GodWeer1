/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.manager.event_impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class EventType {
    public static final byte START = -1;
    public static final byte PRE = 0;
    public static final byte ON = 1;
    public static final byte POST = 2;
    public static final byte SEND = 3;
    public static final byte RECIEVE = 4;
}

