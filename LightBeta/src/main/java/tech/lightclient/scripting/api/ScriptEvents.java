package tech.lightclient.scripting.api;

import tech.lightclient.client.events.api.Event;
import tech.lightclient.LightClient;

/**
 * Events that the base client does not already publish but the scripting docs promise.
 *
 * <p>These are posted from {@link tech.lightclient.scripting.runtime.ScriptManager}, which watches
 * the client for the relevant transitions, so scripts get them without extra mixins.</p>
 */
public final class ScriptEvents {

    /** A tracked entity appeared. */
    public static final class EntitySpawn extends Event {
        private final WorldApi.ScriptEntity entity;

        public EntitySpawn(WorldApi.ScriptEntity entity) {
            this.entity = entity;
        }

        public WorldApi.ScriptEntity entity() {
            return entity;
        }
    }

    /** A tracked entity went away. */
    public static final class EntityRemove extends Event {
        private final WorldApi.ScriptEntity entity;

        public EntityRemove(WorldApi.ScriptEntity entity) {
            this.entity = entity;
        }

        public WorldApi.ScriptEntity entity() {
            return entity;
        }
    }

    /** A world finished loading: joining a server or changing dimension. */
    public static final class WorldLoad extends Event {
        private final String dimension;

        public WorldLoad(String dimension) {
            this.dimension = dimension;
        }

        public String dimension() {
            return dimension;
        }
    }

    /** A client module or a script was toggled. */
    public static final class ModuleToggle extends Event {
        private final String name;
        private final boolean enabled;
        private final boolean fromScript;

        public ModuleToggle(String name, boolean enabled, boolean fromScript) {
            this.name = name;
            this.enabled = enabled;
            this.fromScript = fromScript;
        }

        public String name() { return name; }

        public boolean enabled() { return enabled; }

        public boolean fromScript() { return fromScript; }
    }

    /** Posts an event, swallowing anything a listener throws. */
    public static void post(Event event) {
        try {
            var bus = LightClient.getEventBus();
            if (bus != null) bus.post(event);
        } catch (Throwable ignored) {
        }
    }

    private ScriptEvents() {}
}
