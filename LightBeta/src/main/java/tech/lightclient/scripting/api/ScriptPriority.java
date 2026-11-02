package tech.lightclient.scripting.api;

import tech.lightclient.client.utils.eventbus.EventPriority;

/**
 * Handler ordering, mirroring the {@code Priority} table in the docs.
 * Values map onto the client's own {@link EventPriority} scale.
 */
public final class ScriptPriority {

    public static final int FIRST = EventPriority.REALLYHIGH;
    public static final int EARLY = EventPriority.HIGH;
    public static final int NORMAL = EventPriority.MEDIUM;
    public static final int LATE = EventPriority.LOW;
    public static final int LAST = EventPriority.LOWEST;

    private ScriptPriority() {}
}
