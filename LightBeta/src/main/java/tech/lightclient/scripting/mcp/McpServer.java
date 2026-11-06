package tech.lightclient.scripting.mcp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import net.minecraft.client.MinecraftClient;
import org.json.JSONArray;
import org.json.JSONObject;
import tech.lightclient.LightClient;
import tech.lightclient.scripting.runtime.ScriptConsole;
import tech.lightclient.ui.hud.NotificationsOverlay;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.concurrent.Executors;

/**
 * Exposes the scripting runtime over MCP so an AI agent can write a script, deploy it, switch it
 * on and read the console without the user shuttling files about.
 *
 * <p>The server binds to loopback only, and every request must carry the token that forms part of
 * the URL. The token is effectively a password: anyone holding it can run code in this client.</p>
 */
public final class McpServer {

    private static final McpServer INSTANCE = new McpServer();

    public static McpServer get() {
        return INSTANCE;
    }

    private HttpServer server;
    private int port = 0;
    private String token = "";
    private boolean autoStart = false;

    private McpServer() {}

    public boolean isRunning() {
        return server != null;
    }

    public int port() {
        return port;
    }

    public String url() {
        return "http://127.0.0.1:" + port + "/mcp/" + token;
    }

    // ---- settings file ---------------------------------------------------------------------

    private static File settingsFile() {
        File dir = new File(MinecraftClient.getInstance().runDirectory, "light" + File.separator + "scripts");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "mcp.properties");
    }

    /** Restores the saved port and token, and starts the server if it was left enabled. */
    public void loadAndAutoStart() {
        try {
            File f = settingsFile();
            if (f.exists()) {
                Properties p = new Properties();
                try (var in = Files.newBufferedReader(f.toPath(), StandardCharsets.UTF_8)) {
                    p.load(in);
                }
                port = Integer.parseInt(p.getProperty("port", "0"));
                token = p.getProperty("token", "");
                autoStart = Boolean.parseBoolean(p.getProperty("enabled", "false"));
            }
            if (port <= 0) port = 21000 + new SecureRandom().nextInt(2000);
            if (token.isEmpty()) token = newToken();
            // Starting quietly on launch is the documented behaviour of leaving the box ticked.
            if (autoStart) start(false);
        } catch (Throwable t) {
            LightClient.LOGGER.warn("[MCP] could not load settings", t);
        }
    }

    private void save() {
        try {
            Properties p = new Properties();
            p.setProperty("port", String.valueOf(port));
            p.setProperty("token", token);
            p.setProperty("enabled", String.valueOf(autoStart));
            try (var out = Files.newBufferedWriter(settingsFile().toPath(), StandardCharsets.UTF_8)) {
                p.store(out, "light-client MCP server");
            }
        } catch (Throwable t) {
            LightClient.LOGGER.warn("[MCP] could not save settings", t);
        }
    }

    private static String newToken() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    // ---- lifecycle -------------------------------------------------------------------------

    public void toggle() {
        if (isRunning()) stop();
        else start(true);
    }

    public void start(boolean announce) {
        if (isRunning()) return;
        try {
            if (port <= 0) port = 21000 + new SecureRandom().nextInt(2000);
            if (token.isEmpty()) token = newToken();

            // Loopback only: nothing outside this machine can reach it.
            server = HttpServer.create(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), port), 0);
            server.createContext("/mcp/" + token, this::handle);
            server.setExecutor(Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "light-mcp");
                t.setDaemon(true);
                return t;
            }));
            server.start();
            autoStart = true;
            save();

            ScriptConsole.info("mcp", "server listening on " + url());
            if (announce) {
                copyUrlToClipboard();
                NotificationsOverlay.push("MCP запущен, ссылка в буфере обмена", 2200, NotificationsOverlay.Kind.ON);
            }
        } catch (IOException e) {
            server = null;
            ScriptConsole.error("mcp", "could not start: " + e.getMessage());
            if (announce) {
                NotificationsOverlay.push("MCP: порт занят, смените порт", 2400, NotificationsOverlay.Kind.OFF);
            }
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
        autoStart = false;
        save();
        ScriptConsole.info("mcp", "server stopped");
    }

    public void copyUrlToClipboard() {
        MinecraftClient.getInstance().execute(() -> {
            try {
                MinecraftClient.getInstance().keyboard.setClipboard(url());
            } catch (Throwable ignored) {
            }
        });
    }

    /** Issues a fresh token, invalidating the old link immediately. */
    public void regenerateToken() {
        boolean wasRunning = isRunning();
        if (wasRunning) {
            server.stop(0);
            server = null;
        }
        token = newToken();
        save();
        if (wasRunning) start(true);
        else copyUrlToClipboard();
    }

    /** Moves to another port, for when the current one is taken. */
    public void changePort() {
        boolean wasRunning = isRunning();
        if (wasRunning) {
            server.stop(0);
            server = null;
        }
        port = 21000 + new SecureRandom().nextInt(2000);
        save();
        if (wasRunning) start(true);
    }

    // ---- request handling ------------------------------------------------------------------

    private void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                respond(exchange, 405, new JSONObject().put("error", "use POST"));
                return;
            }
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JSONObject request = body.isBlank() ? new JSONObject() : new JSONObject(body);

            // Accept both a bare {tool, arguments} shape and the JSON-RPC envelope MCP clients use.
            String method = request.optString("method", request.optString("tool", ""));
            JSONObject params = request.optJSONObject("params");
            Object id = request.opt("id");

            if (method.equals("initialize")) {
                respondRpc(exchange, id, McpTools.initializeResult());
                return;
            }
            if (method.equals("tools/list")) {
                respondRpc(exchange, id, new JSONObject().put("tools", McpTools.listTools()));
                return;
            }
            if (method.equals("notifications/initialized")) {
                respond(exchange, 200, new JSONObject());
                return;
            }

            String toolName;
            JSONObject arguments;
            if (method.equals("tools/call")) {
                toolName = params == null ? "" : params.optString("name", "");
                arguments = params == null ? new JSONObject() : params.optJSONObject("arguments");
            } else {
                toolName = method;
                arguments = request.optJSONObject("arguments");
            }
            if (arguments == null) arguments = new JSONObject();

            JSONObject result = McpTools.call(toolName, arguments);
            if (id != null || method.startsWith("tools/")) {
                // MCP expects tool output wrapped as content.
                JSONObject wrapped = new JSONObject()
                        .put("content", new JSONArray().put(new JSONObject()
                                .put("type", "text")
                                .put("text", result.toString())));
                respondRpc(exchange, id, wrapped);
            } else {
                respond(exchange, 200, result);
            }
        } catch (Throwable t) {
            respond(exchange, 500, new JSONObject().put("error", t.getMessage() == null
                    ? t.getClass().getSimpleName() : t.getMessage()));
        }
    }

    private void respondRpc(HttpExchange exchange, Object id, JSONObject result) throws IOException {
        JSONObject envelope = new JSONObject()
                .put("jsonrpc", "2.0")
                .put("result", result);
        if (id != null) envelope.put("id", id);
        respond(exchange, 200, envelope);
    }

    private void respond(HttpExchange exchange, int code, JSONObject payload) throws IOException {
        byte[] bytes = payload.toString().getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }
}
