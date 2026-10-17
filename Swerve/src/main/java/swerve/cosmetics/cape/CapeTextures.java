package swerve.cosmetics.cape;

import com.mojang.blaze3d.platform.NativeImage;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import swerve.Swerve;

/**
 * Текстуры плащей грузятся в фоне: сборка кадра идёт в отдельном потоке,
 * загрузка в GPU — на потоке клиента.
 */
public final class CapeTextures {

    private record Done(ResourceLocation id, NativeImage image) {}

    private static final ExecutorService WORKER = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "swerve-cape-texture");
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY + 1);
        return t;
    });

    private static final Set<ResourceLocation> PENDING = ConcurrentHashMap.newKeySet();
    private static final ConcurrentLinkedQueue<Done> FINISHED = new ConcurrentLinkedQueue<>();
    private static final Map<ResourceLocation, Boolean> OURS = new ConcurrentHashMap<>();

    private CapeTextures() {
    }

    public static boolean registered(ResourceLocation id) {
        return OURS.containsKey(id);
    }

    /** Собирает картинку в фоновом потоке и регистрирует её, когда она готова. */
    public static void request(ResourceLocation id, Callable<NativeImage> make) {
        if (registered(id) || !PENDING.add(id)) {
            return;
        }
        WORKER.execute(() -> {
            NativeImage image = null;
            try {
                image = make.call();
            } catch (Exception e) {
                Swerve.LOGGER.warn("cape texture {}: {}", id, e.toString());
            }
            if (image != null) {
                FINISHED.add(new Done(id, image));
            } else {
                PENDING.remove(id);
            }
        });
    }

    /** Поток клиента: загружает готовые картинки в GPU, не дольше бюджета. */
    public static void pump(long budgetNanos) {
        if (FINISHED.isEmpty()) {
            return;
        }
        long end = System.nanoTime() + budgetNanos;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return;
        }
        Done done;
        while ((done = FINISHED.poll()) != null) {
            try {
                mc.getTextureManager().register(done.id(), new DynamicTexture(done.image()));
                OURS.put(done.id(), Boolean.TRUE);
            } catch (RuntimeException e) {
                Swerve.LOGGER.warn("cape texture {}: {}", done.id(), e.toString());
            }
            PENDING.remove(done.id());
            if (System.nanoTime() > end) {
                break;
            }
        }
    }

    /** Освобождает текстуру (например, при смене рисунка). */
    public static void release(ResourceLocation id) {
        if (id == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && OURS.remove(id) != null) {
            try {
                mc.getTextureManager().release(id);
            } catch (RuntimeException ignored) {
            }
        }
    }
}
