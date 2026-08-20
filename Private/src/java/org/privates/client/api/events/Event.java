package org.privates.client.api.events;

import org.privates.client.Excellent;

public class Event {
    public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    private boolean cancelled = false;

    public void cancel() {
        cancelled = true;
    }

    public void hook() {
        Excellent.eventHandler().post(this);
    }
}
