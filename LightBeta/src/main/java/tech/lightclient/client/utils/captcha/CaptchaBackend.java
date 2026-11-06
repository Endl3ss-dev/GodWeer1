package tech.lightclient.client.utils.captcha;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.CertificatePinner;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.concurrent.atomic.AtomicBoolean;

/** Exact Delta backend packet transport used by CaptchaSolver. */
public final class CaptchaBackend {
    private static final String WS_URL = "wss://deltaclient.xyz/ws/";
    private static final byte[] KEY = HexFormat.of().parseHex("9f3b7c1e6a8d4205b1e7c9a2f4d60837");
    private static final Gson GSON = new Gson();
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .certificatePinner(new CertificatePinner.Builder()
                    .add("deltaclient.xyz", "sha256/tjzKnQqXiG8qfKkHSOtckEHsKNtsONSU9NN+d8vZ1XQ=")
                    .build())
            .build();

    private final AtomicBoolean running = new AtomicBoolean();
    private final AtomicBoolean connected = new AtomicBoolean();
    private volatile WebSocket socket;
    private volatile String token = "";
    private volatile Consumer<String> captchaCodeListener;

    public void setToken(String token) {
        this.token = token == null ? "" : token.trim();
    }

    public void setCaptchaCodeListener(Consumer<String> listener) {
        this.captchaCodeListener = listener;
    }

    public boolean isConnected() {
        return running.get() && connected.get() && socket != null;
    }

    public void connect() {
        // Delta opens this connection even when the native profile exposes an empty token;
        // the protocol value is still "-minecraft" in that case.
        if (running.get()) {
            return;
        }

        running.set(true);
        Request request = new Request.Builder()
                .url(WS_URL)
                .header("Sec-WebSocket-Protocol", token + "-minecraft")
                .build();
        socket = CLIENT.newWebSocket(request, new Listener());
    }

    public void disconnect() {
        running.set(false);
        connected.set(false);
        WebSocket current = socket;
        socket = null;
        if (current != null) {
            current.close(1000, "disconnect");
        }
    }

    public void sendCaptcha(byte[] image) {
        if (!isConnected() || image == null || image.length == 0) {
            return;
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("bytes", Base64.getEncoder().encodeToString(image));
        sendPacket("captcha", payload.toString());
    }

    private void sendPacket(String id, String payload) {
        WebSocket current = socket;
        if (current == null) {
            return;
        }

        JsonObject packet = new JsonObject();
        packet.addProperty("id", id);
        packet.addProperty("payload", encrypt(payload));
        current.send(GSON.toJson(packet));
    }

    private void handlePacket(String message) {
        try {
            JsonObject packet = JsonParser.parseString(message).getAsJsonObject();
            if (!packet.has("id") || !packet.has("payload")) {
                return;
            }

            String id = packet.get("id").getAsString();
            if (!"captcha".equals(id)) {
                return;
            }

            String payload = decrypt(packet.get("payload").getAsString());
            if (payload == null) {
                return;
            }

            JsonObject data = JsonParser.parseString(payload).getAsJsonObject();
            if (data.has("code") && !data.get("code").isJsonNull()) {
                Consumer<String> listener = captchaCodeListener;
                if (listener != null) {
                    listener.accept(data.get("code").getAsString());
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static String encrypt(String plain) {
        try {
            byte[] iv = new byte[12];
            RANDOM.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(KEY, "AES"), new GCMParameterSpec(128, iv));
            byte[] encrypted = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            byte[] result = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String decrypt(String encoded) {
        try {
            byte[] data = Base64.getDecoder().decode(encoded);
            if (data.length <= 12) {
                return null;
            }
            byte[] iv = Arrays.copyOfRange(data, 0, 12);
            byte[] encrypted = Arrays.copyOfRange(data, 12, data.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(KEY, "AES"), new GCMParameterSpec(128, iv));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            return null;
        }
    }

    private final class Listener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            connected.set(true);
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            handlePacket(text);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            if (socket == webSocket) {
                socket = null;
                connected.set(false);
                running.set(false);
            }
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            if (socket == webSocket) {
                socket = null;
                connected.set(false);
                running.set(false);
            }
        }
    }
}
