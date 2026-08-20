/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import minced.core.RotationManager;
import minced.core.RotationBase;
import minced.core.EventBus;
import minced.core.CameraRotation;
import minced.core.AimRotation;

public final class ComponentHandler
extends HashMap
implements Map {
    public RotationBase method2012(Class param12) {
        Stream<RotationBase> stream = this.values().stream().filter(param1 -> param1.getClass() == param12);
        Objects.requireNonNull(param12);
        return (RotationBase)stream.map(param12::cast).findFirst().orElse(null);
    }

    public void method2013(RotationBase ... param1) {
        for (RotationBase v5 : param1) {
            this.put(v5.getClass(), v5);
        }
    }

    public void method2015() {
        this.method2013(new RotationManager(), new AimRotation(), new CameraRotation());
        this.values().forEach(EventBus::method3013);
    }
}

