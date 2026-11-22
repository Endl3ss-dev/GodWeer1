package tech.lightclient.scripting.runtime;

import tech.lightclient.client.utils.eventbus.listeners.IListener;

/** Handle returned by {@code on<...>} allowing manual unsubscription. */
public class ScriptSubscription {
    private final ScriptInstance script;
    private final IListener listener;
    private boolean active = true;

    ScriptSubscription(ScriptInstance script, IListener listener) {
        this.script = script;
        this.listener = listener;
    }

    public void unsubscribe() {
        if (!active) return;
        active = false;
        script.removeSubscription(listener);
    }

    public boolean active() {
        return active;
    }
}
