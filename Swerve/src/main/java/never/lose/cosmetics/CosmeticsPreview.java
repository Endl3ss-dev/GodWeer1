package never.lose.cosmetics;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.util.math.MathHelper;

import java.nio.file.Path;
import java.util.UUID;

/**
 * 3D-предпросмотр аватара без его применения к игроку.
 *
 * Работает так: создаётся отдельный UserData, в него грузится аватар из папки,
 * а на время отрисовки игрока в реестре Figura временно подменяется запись владельца.
 * Если Figura нет или что-то отвалилось - состояние FAILED, GUI рисует 2D-превью.
 */
public final class CosmeticsPreview {

    public enum State {
        IDLE,
        LOADING,
        READY,
        FAILED
    }

    private static final long TICK_INTERVAL_MS = 50L;

    private volatile State state = State.IDLE;
    private volatile Object userData;
    private volatile Object avatar;
    private volatile String folder = "";

    private int generation;
    private long lastTick;
    private long loadStart;

    private float yaw = -22f;
    private float pitch = 4f;
    private float zoom = 1f;

    public State getState() {
        return state;
    }

    public String getFolder() {
        return folder;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getZoom() {
        return zoom;
    }

    public void rotate(float deltaYaw, float deltaPitch) {
        yaw += deltaYaw;
        pitch = MathHelper.clamp(pitch + deltaPitch, -45f, 45f);
    }

    public void zoom(float delta) {
        zoom = MathHelper.clamp(zoom + delta, 0.55f, 2.2f);
    }

    public void resetView() {
        yaw = -22f;
        pitch = 4f;
        zoom = 1f;
    }

    public void clear() {
        generation++;
        Object old = userData;
        userData = null;
        avatar = null;
        state = State.IDLE;
        folder = "";
        FiguraBridge.releaseUser(old);
    }

    public void request(CosmeticEntry entry) {
        if (entry == null) {
            clear();
            return;
        }

        if (entry.getFolder().equals(folder) && state != State.FAILED && state != State.IDLE) {
            return;
        }

        int local = ++generation;
        Object old = userData;

        userData = null;
        avatar = null;
        folder = entry.getFolder();
        loadStart = System.currentTimeMillis();

        FiguraBridge.releaseUser(old);

        if (!FiguraBridge.isAvailable()) {
            state = State.FAILED;
            return;
        }

        state = State.LOADING;
        Path path = entry.getPath();

        Thread thread = new Thread(() -> {
            Object created = FiguraBridge.createDetachedUser(path);

            if (local != generation) {
                FiguraBridge.releaseUser(created);
                return;
            }

            if (created == null) {
                state = State.FAILED;
                return;
            }

            userData = created;
        }, "Endless-Cosmetics-Preview-Load");

        thread.setDaemon(true);
        thread.start();
    }

    /** Вызывается раз за кадр: только дешёвая проверка готовности, без аллокаций. */
    public void update() {
        if (state != State.LOADING) {
            return;
        }

        Object data = userData;

        if (data == null) {
            if (System.currentTimeMillis() - loadStart > 15_000L) {
                state = State.FAILED;
            }

            return;
        }

        Object loaded = FiguraBridge.getPreviewAvatar(data);

        if (loaded != null) {
            avatar = loaded;
            state = State.READY;
            lastTick = 0L;
            return;
        }

        if (System.currentTimeMillis() - loadStart > 15_000L) {
            state = State.FAILED;
        }
    }

    private void tickAvatar() {
        Object current = avatar;

        if (current == null) {
            return;
        }

        long now = System.currentTimeMillis();

        if (now - lastTick < TICK_INTERVAL_MS) {
            return;
        }

        lastTick = now;
        FiguraBridge.tickAvatar(current);
    }

    /**
     * Рисует игрока с превью-аватаром в заданном прямоугольнике.
     * @return false, если превью недоступно и вызывающий должен нарисовать fallback.
     */
    public boolean render(DrawContext context, float x, float y, float width, float height) {
        if (state != State.READY) {
            return false;
        }

        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc == null || mc.player == null) {
            return false;
        }

        Object data = userData;

        if (data == null) {
            return false;
        }

        tickAvatar();

        int left = (int) (x + 4f);
        int top = (int) (y + 4f);
        int right = (int) (x + width - 4f);
        int bottom = (int) (y + height - 4f);
        int size = (int) (MathHelper.clamp(Math.min(width, height) * 0.60f, 28f, 90f) * zoom);

        float centerX = (left + right) * 0.5f;
        float centerY = (top + bottom) * 0.5f;
        float mouseX = centerX - (float) Math.tan(Math.toRadians(yaw)) * 40f;
        float mouseY = centerY - (float) Math.tan(Math.toRadians(pitch)) * 40f;

        UUID uuid = mc.player.getUuid();

        return FiguraBridge.renderWithDetachedUser(uuid, data, () ->
                InventoryScreen.drawEntity(context, left, top, right, bottom, size, 0.0625f,
                        mouseX, mouseY, mc.player));
    }
}
