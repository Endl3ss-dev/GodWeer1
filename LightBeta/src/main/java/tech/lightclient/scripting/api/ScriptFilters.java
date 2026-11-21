package tech.lightclient.scripting.api;

import java.util.function.Predicate;

/**
 * Ready-made entity predicates, exposed as {@code filters}, so the common
 * "who may I hit" question does not have to be rewritten in every script.
 */
public final class ScriptFilters {

    /** Alive, not you, not a friend. */
    public Predicate<WorldApi.ScriptEntity> attackable() {
        return e -> e != null && e.alive() && !e.isSelf() && !e.isAlly();
    }

    /** Other players only, excluding you and your friends. */
    public Predicate<WorldApi.ScriptEntity> enemyPlayers() {
        return e -> e != null && e.isPlayer() && e.alive() && !e.isSelf() && !e.isAlly();
    }

    public Predicate<WorldApi.ScriptEntity> players() {
        return e -> e != null && e.isPlayer() && !e.isSelf();
    }

    public Predicate<WorldApi.ScriptEntity> living() {
        return e -> e != null && e.isLiving() && e.alive();
    }

    public Predicate<WorldApi.ScriptEntity> allies() {
        return e -> e != null && e.isAlly();
    }

    /** Everything, including you. */
    public Predicate<WorldApi.ScriptEntity> any() {
        return e -> true;
    }

    /** Only entities of the given type id, e.g. "minecraft:zombie". */
    public Predicate<WorldApi.ScriptEntity> ofType(String typeId) {
        String want = typeId == null ? "" : (typeId.contains(":") ? typeId : "minecraft:" + typeId);
        return e -> e != null && e.type().equals(want);
    }
}
