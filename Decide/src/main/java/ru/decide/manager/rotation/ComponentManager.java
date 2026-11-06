/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.manager.rotation;

import java.util.HashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.Client;
import ru.decide.manager.DragComponent;
import ru.decide.manager.rotation.Component;
import ru.decide.manager.rotation.FreeLookUtil;
import ru.decide.manager.rotation.RotationProcess;
import ru.decide.manager.rotation.TestRotation;

@Environment(value=EnvType.CLIENT)
public final class ComponentManager
extends HashMap<Class<? extends Component>, Component> {
    public void init() {
        this.add(new RotationProcess(), new FreeLookUtil(), new DragComponent(), new TestRotation());
        this.values().forEach((? super T component) -> Client.eventHandler().subscribe(component));
    }

    public void add(Component ... components) {
        for (Component component : components) {
            this.put(component.getClass(), component);
        }
    }

    public void unregister(Component ... components) {
        for (Component component : components) {
            Client.eventHandler().unsubscribe(component);
            this.remove(component.getClass());
        }
    }

    public <T extends Component> T get(Class<T> clazz) {
        return (T)((Component)this.values().stream().filter(component -> component.getClass() == clazz).map(clazz::cast).findFirst().orElse(null));
    }
}

