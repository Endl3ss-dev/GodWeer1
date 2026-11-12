/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.manager.events.orbit;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.manager.events.orbit.ICancellable;
import ru.decide.manager.events.orbit.listeners.IListener;
import ru.decide.manager.events.orbit.listeners.LambdaListener;

@Environment(value=EnvType.CLIENT)
public interface IEventBus {
    public void registerLambdaFactory(String var1, LambdaListener.Factory var2);

    public boolean isListening(Class<?> var1);

    public <T> T post(T var1);

    public <T extends ICancellable> T post(T var1);

    public void subscribe(Object var1);

    public void subscribe(Class<?> var1);

    public void subscribe(IListener var1);

    public void unsubscribe(Object var1);

    public void unsubscribe(Class<?> var1);

    public void unsubscribe(IListener var1);
}

