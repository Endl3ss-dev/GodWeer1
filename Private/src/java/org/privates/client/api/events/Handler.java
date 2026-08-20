package org.privates.client.api.events;


import org.privates.client.Excellent;

public abstract class Handler {
    public Handler() {
        Excellent.eventHandler().subscribe(this);
    }
}