package tech.lightclient.scripting.api;

import tech.lightclient.scripting.runtime.ScriptConsole;

/** Script console logging, mirroring the {@code log} object in the docs. */
public final class LogApi {

    private final String scriptId;

    public LogApi(String scriptId) {
        this.scriptId = scriptId;
    }

    public void info(Object m) {
        ScriptConsole.info(scriptId, String.valueOf(m));
    }

    public void warn(Object m) {
        ScriptConsole.warn(scriptId, String.valueOf(m));
    }

    public void error(Object m) {
        ScriptConsole.error(scriptId, String.valueOf(m));
    }

    public void error(Object m, Throwable t) {
        Throwable root = t;
        while (root != null && root.getCause() != null && root.getCause() != root) root = root.getCause();
        String detail = root == null
                ? "unknown"
                : root.getClass().getSimpleName() + (root.getMessage() != null ? ": " + root.getMessage() : "");
        ScriptConsole.error(scriptId, m + " - " + detail);
    }
}
