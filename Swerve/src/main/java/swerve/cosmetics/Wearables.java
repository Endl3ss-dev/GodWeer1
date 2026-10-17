package swerve.cosmetics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.player.AbstractClientPlayer;

import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * Гардероб: что надето на игроке и как это рисуется.
 *
 * <p>Своё состояние синхронизируется с другими клиентами через чат: строка вида
 * {@code SWCOS 14,15,20,25} — номера моделей по слотам. У клиентов Swerve такая строка
 * не показывается в чате, но разбирается и применяется к игроку-отправителю.
 */
public final class Wearables {

    /** Метка служебной строки чата. */
    public static final String MARKER = "SWCOS:";

    /** Слоты: крылья, рюкзак, питомец, головной убор. */
    public static final int SLOTS = 4;

    private static final int[] WORN = {-1, -1, -1, -1};
    private static final Map<UUID, int[]> REMOTE = new HashMap<>();

    private static boolean sync = true;
    private static long lastSent;
    private static boolean dirty = true;
    /** Игрок, чья модель сейчас рисуется (ставится в extractRenderState). */
    private static AbstractClientPlayer current;

    private Wearables() {
    }

    public static AbstractClientPlayer current() {
        return current;
    }

    public static void current(AbstractClientPlayer player) {
        current = player;
    }

    public static int[] worn() {
        return WORN;
    }

    public static int worn(int slot) {
        return WORN[slot];
    }

    public static boolean sync() {
        return sync;
    }

    public static void sync(boolean on) {
        sync = on;
        if (on) {
            dirty = true;
        }
        CosmeticStore.save();
    }

    /** Надеть модель в слот (n = -1 — снять). */
    public static void set(int slot, int n) {
        if (slot < 0 || slot >= SLOTS) {
            return;
        }
        if (WORN[slot] == n) {
            return;
        }
        WORN[slot] = n;
        dirty = true;
        CosmeticStore.save();
    }

    public static boolean isWorn(int slot, int n) {
        return WORN[slot] == n;
    }

    /** Применить состояние другого игрока. */
    public static void applyRemote(UUID id, int a, int b, int c, int d) {
        REMOTE.put(id, new int[] {a, b, c, d});
    }

    /** Что надето на другом игроке (null — ничего не известно). */
    public static int[] remote(UUID id) {
        return REMOTE.get(id);
    }

    public static void forget(UUID id) {
        REMOTE.remove(id);
    }

    /** Разбор служебной строки: SWCOS 14,15,20,25. */
    public static boolean parse(String text, int[] out) {
        int i = text.indexOf(MARKER);
        if (i < 0) {
            return false;
        }
        int p = i + MARKER.length();
        int[] v = new int[4];
        for (int s = 0; s < 4; s++) {
            while (p < text.length() && (text.charAt(p) == ' ' || text.charAt(p) == ',')) {
                p++;
            }
            int start = p;
            while (p < text.length() && (Character.isDigit(text.charAt(p)) || text.charAt(p) == '-')) {
                p++;
            }
            if (start == p) {
                return false;
            }
            try {
                v[s] = Integer.parseInt(text.substring(start, p));
            } catch (NumberFormatException e) {
                return false;
            }
        }
        out[0] = v[0];
        out[1] = v[1];
        out[2] = v[2];
        out[3] = v[3];
        return true;
    }

    /** Отправить своё состояние в чат (для других клиентов Swerve). */
    public static void send(boolean force) {
        if (!sync) {
            return;
        }
        boolean empty = WORN[0] < 0 && WORN[1] < 0 && WORN[2] < 0 && WORN[3] < 0;
        long now = System.currentTimeMillis();
        if (!force && !dirty && (empty || now - lastSent < 30_000L)) {
            return;
        }
        if (now - lastSent < 1_500L) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.getConnection() == null) {
            return;
        }
        lastSent = now;
        dirty = false;
        mc.getConnection().sendChat(MARKER + " " + WORN[0] + "," + WORN[1] + "," + WORN[2] + "," + WORN[3]);
    }

    /** Отметить, что состояние изменилось (нужна пересылка). */
    public static void touch() {
        dirty = true;
    }

    // ---- рендер

    /**
     * Надеть модели на игрока.
     *
     * @param items номера моделей по слотам (null — рисовать нечего)
     */
    public static void render(PlayerModel model, PoseStack pose, MultiBufferSource buffer, int light, int[] items) {
        if (items == null || model == null) {
            return;
        }
        for (int slot = 0; slot < SLOTS; slot++) {
            int n = items[slot];
            if (n < 0) {
                continue;
            }
            CosmeticItem it = CosmeticCatalogue.byNumber(n);
            if (it == null) {
                continue;
            }
            renderItem(model, pose, buffer, light, it);
        }
    }

    private static void renderItem(PlayerModel pm, PoseStack pose, MultiBufferSource buffer, int light, CosmeticItem it) {
        CosmeticGeometry geo = CosmeticGeometry.get(it.n());
        if (geo == null) {
            return;
        }
        pose.pushPose();
        float lift = 0;
        switch (it.pos()) {
            case 1 -> {
                pm.body.translateAndRotate(pose);
                lift = -.3f;
            }
            case 2 -> {
                pm.head.translateAndRotate(pose);
                lift = it.category() == 3 ? .5f : 0;
            }
            case 3 -> lift = .75f;
            case 4 -> {
                pm.rightArm.translateAndRotate(pose);
                lift = -.25f;
            }
            case 5 -> {
                pm.leftArm.translateAndRotate(pose);
                lift = -.25f;
            }
            case 6 -> {
                pm.rightLeg.translateAndRotate(pose);
                lift = -.35f;
            }
            case 7 -> {
                pm.leftLeg.translateAndRotate(pose);
                lift = -.35f;
            }
            default -> {
            }
        }
        // модель построена «вниз осью Y», косметика надевается вверх
        pose.mulPose(Axis.ZP.rotationDegrees(180));
        pose.translate(it.x(), it.y() + lift, it.z());
        if (it.yaw() != 0) {
            pose.mulPose(Axis.YP.rotationDegrees(it.yaw()));
        }
        if (it.pitch() != 0) {
            pose.mulPose(Axis.XP.rotationDegrees(it.pitch()));
        }
        if (it.roll() != 0) {
            pose.mulPose(Axis.ZP.rotationDegrees(it.roll()));
        }
        pose.scale(it.scale(), it.scale(), it.scale());
        long now = System.currentTimeMillis();
        float[] quads = geo.pose(now);
        float dv = geo.frameShift(now);
        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(geo.texture()));
        for (int i = 0; i + 22 < quads.length; i += 23) {
            float nx = quads[i + 20], ny = quads[i + 21], nz = quads[i + 22];
            for (int k = 0; k < 4; k++) {
                int o = i + k * 5;
                vc.addVertex(pose.last(), quads[o], quads[o + 1], quads[o + 2])
                        .setColor(255, 255, 255, 255)
                        .setUv(quads[o + 3], quads[o + 4] + dv)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(light)
                        .setNormal(pose.last(), nx, ny, nz);
            }
        }
        pose.popPose();
    }

    /** Что надето на игроке: своё состояние или присланное. */
    public static int[] stateOf(AbstractClientPlayer player) {
        if (player == null) {
            return null;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.player == player) {
            return WORN;
        }
        return REMOTE.get(player.getUUID());
    }
}
