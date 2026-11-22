package tech.lightclient.scripting.api;

import tech.lightclient.LightClient;
import tech.lightclient.client.utils.render.main.core.Renderer2D;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A fragment (and optionally vertex) shader compiled on first use, mirroring the "Шейдеры" docs.
 *
 * <p>Compilation is deferred to the first draw because a GL context is only current during
 * rendering. That is also why {@code ready()} is still false on the line after {@code shader(...)}
 * returns, and why a script must draw unconditionally rather than bailing out on {@code !ready()}.</p>
 */
public final class ScriptShader {

    private final String name;
    private final String fragmentSource;
    private final String vertexSource;
    private final Map<String, Object[]> uniforms = new LinkedHashMap<>();

    private volatile boolean compiled = false;
    private volatile boolean failed = false;
    private volatile String error = null;

    public ScriptShader(String name, String fragmentSource, String vertexSource) {
        this.name = name;
        this.fragmentSource = fragmentSource;
        this.vertexSource = vertexSource;
    }

    public String name() {
        return name;
    }

    /** True once the shader has been compiled successfully. */
    public boolean ready() {
        return compiled && !failed;
    }

    /** Compilation error, or null while nothing has gone wrong yet. */
    public String error() {
        return error;
    }

    // ---- uniforms --------------------------------------------------------------------------

    public ScriptShader set(String uniform, float a) {
        uniforms.put(uniform, new Object[]{a});
        return this;
    }

    public ScriptShader set(String uniform, float a, float b) {
        uniforms.put(uniform, new Object[]{a, b});
        return this;
    }

    public ScriptShader set(String uniform, float a, float b, float c) {
        uniforms.put(uniform, new Object[]{a, b, c});
        return this;
    }

    public ScriptShader set(String uniform, float a, float b, float c, float d) {
        uniforms.put(uniform, new Object[]{a, b, c, d});
        return this;
    }

    public ScriptShader set(String uniform, int value) {
        uniforms.put(uniform, new Object[]{value});
        return this;
    }

    public ScriptShader set(String uniform, boolean value) {
        uniforms.put(uniform, new Object[]{value});
        return this;
    }

    public ScriptShader setArray(String uniform, float[] values) {
        Object[] boxed = new Object[values == null ? 0 : values.length];
        for (int i = 0; i < boxed.length; i++) boxed[i] = values[i];
        uniforms.put(uniform, boxed);
        return this;
    }

    /**
     * Draws the shader over a rectangle.
     *
     * <p>The client's 2D renderer does not currently expose a hook for user supplied GLSL, so
     * rather than pretending to draw something this reports a clear, one-off error through
     * {@link #error()} - which is exactly where the docs tell a script author to look.</p>
     */
    public void draw(Renderer2D renderer, float x, float y, float w, float h) {
        if (!compiled) {
            compiled = true;
            failed = true;
            error = "custom shaders are not supported by this client build";
            LightClient.LOGGER.warn("[Scripts] shader '{}' requested but custom shaders are unsupported", name);
        }
    }
}
