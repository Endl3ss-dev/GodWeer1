package tech.lightclient.scripting.runtime;

/**
 * Enforces the documented time limits: 250 ms for a handler, command or button, and 5 s for the
 * top-level run of a script when it loads.
 *
 * <p>A handler cannot be interrupted safely from outside, so rather than killing threads this
 * measures how long the call took and reports an overrun afterwards. The script is then switched
 * off, which is what actually protects the game: the next tick will not call it again. A script
 * stuck in a genuinely infinite loop is caught by the deadline check that
 * {@link #checkDeadline()} performs on behalf of long-running script loops.</p>
 */
public final class ScriptWatchdog {

    public static final long HANDLER_LIMIT_MS = 250L;
    public static final long LOAD_LIMIT_MS = 5_000L;

    /** Thrown when a script overruns its budget. */
    public static final class Timeout extends RuntimeException {
        public Timeout(String message) {
            super(message);
        }
    }

    private static final ThreadLocal<Long> DEADLINE = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    private ScriptWatchdog() {}

    /** Runs a script callback under the handler budget. */
    public static void runGuarded(ScriptInstance script, String phase, Runnable body) {
        runGuarded(script, phase, HANDLER_LIMIT_MS, body);
    }

    public static void runGuarded(ScriptInstance script, String phase, long limitMs, Runnable body) {
        Long previousDeadline = DEADLINE.get();
        String previousName = CURRENT.get();
        long start = System.nanoTime();

        // Nested calls keep the outermost deadline so a handler cannot buy itself more time
        // by calling into another part of the script.
        if (previousDeadline == null) {
            DEADLINE.set(start + limitMs * 1_000_000L);
            CURRENT.set(script != null ? script.id : "?");
        }
        try {
            body.run();
        } finally {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000L;
            if (previousDeadline == null) {
                DEADLINE.remove();
                if (previousName == null) CURRENT.remove();
                else CURRENT.set(previousName);

                if (elapsedMs > limitMs && script != null) {
                    ScriptConsole.error(script.id, script.file.getName()
                            + ": " + phase + " ran for " + elapsedMs
                            + " ms without giving the game back (limit " + limitMs + " ms)");
                    script.setEnabled(false);
                }
            }
        }
    }

    /**
     * Called from inside script-facing loops. Throws once the current budget is spent, which is
     * how a runaway {@code while (true)} gets stopped instead of freezing the client.
     */
    public static void checkDeadline() {
        Long deadline = DEADLINE.get();
        if (deadline == null) return;
        if (System.nanoTime() > deadline) {
            String name = CURRENT.get();
            DEADLINE.remove();
            throw new Timeout((name == null ? "script" : name)
                    + " ran for more than its time budget without giving the game back");
        }
    }

    /** True while a script callback is on the stack of this thread. */
    public static boolean inScript() {
        return DEADLINE.get() != null;
    }
}
