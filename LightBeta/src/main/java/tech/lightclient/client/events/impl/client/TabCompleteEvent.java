package tech.lightclient.client.events.impl.client;

import tech.lightclient.client.events.api.Event;

public class TabCompleteEvent extends Event {
    public final String prefix;
    public String[] completions;

    public TabCompleteEvent(String prefix) {
        this.prefix = prefix;
        this.completions = null;
    }
}