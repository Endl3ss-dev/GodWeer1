/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class Setting {
    Runnable changeListener;
    public Object value;
    boolean visible = true;
    Supplier visibilitySupplier = null;
    String name;

    public void setChangeListener(Runnable param1) {
        this.changeListener = param1;
    }

    public void setValue(Object param1) {
        this.value = param1;
        if (this.changeListener != null) {
            this.changeListener.run();
        }
    }

    public void setVisible(boolean param1) {
        this.visible = param1;
    }

    public Object getRawValue() {
        return this.value;
    }

    public boolean isVisible() {
        return this.visibilitySupplier != null ? Boolean.TRUE.equals(this.visibilitySupplier.get()) : this.visible;
    }

    public void setName(String param1) {
        this.name = param1;
    }

    public void setValueSilently(Object param1) {
        this.value = param1;
    }

    public Setting visibleWhen(Supplier param1) {
        this.visibilitySupplier = param1;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }

    public Supplier getVisibilitySupplier() {
        return this.visibilitySupplier;
    }

    public Setting visibleWhen(BooleanSupplier param1) {
        Objects.requireNonNull(param1);
        this.visibilitySupplier = param1::getAsBoolean;
        return this;
    }

    public Runnable getChangeListener() {
        return this.changeListener;
    }

    public void setVisibilitySupplier(Supplier param1) {
        this.visibilitySupplier = param1;
    }

    public Setting(String param1, Object param2) {
        this.name = param1;
        this.value = param2;
    }
}
