/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;
import java.util.concurrent.CopyOnWriteArrayList;
import minced.core.EventListener;

class ListenerList
extends CopyOnWriteArrayList
implements Iterable,
Collection,
SequencedCollection,
List {
    static long cfr_renamed_131;

    EventListener field8022;

    ListenerList(EventListener param1) {
        this.field8022 = param1;
        this.add(this.field8022);
    }

    static {
        cfr_renamed_131 = 0L;
    }
}



