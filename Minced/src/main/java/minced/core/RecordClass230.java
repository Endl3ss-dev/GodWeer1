package minced.core;

import java.util.function.Supplier;

public final class RecordClass230 {
    public final char icon;
    public final Supplier consumer;

    public RecordClass230(char icon, Supplier consumer) {
        this.icon = icon;
        this.consumer = consumer;
    }

    public char icon() { return icon; }
    public Supplier consumer() { return consumer; }
}
