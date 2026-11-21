package tech.lightclient.client.events.api;

import tech.lightclient.client.utils.eventbus.ICancellable;
import lombok.Getter;
import lombok.Setter;

public class Event implements ICancellable {
    @Getter @Setter
    boolean cancelled = false;

    public void cancel() {
        cancelled = true;
    }

    public void uncancel() {
        cancelled = false;
    }
}
