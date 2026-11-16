/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Function
 *  com.sun.jna.Library
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.utils.sound.key;

import com.sun.jna.Function;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.utils.sound.key.KeySoundDecoder;

@Environment(value=EnvType.CLIENT)
public final class KeySoundEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"DecideVisuals:KeySounds");
    private static final ExecutorService DECODER = Executors.newFixedThreadPool(2, task -> {
        Thread thread = new Thread(task, "DecideVisuals-KeySoundDecode");
        thread.setDaemon(true);
        return thread;
    });
    private static final ExecutorService AUDIO = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "DecideVisuals-KeySoundAudio");
        thread.setDaemon(true);
        return thread;
    });
    private static final Map<Path, Map<Integer, CompletableFuture<KeySoundDecoder.DecodedSound>>> CACHE = new ConcurrentHashMap<Path, Map<Integer, CompletableFuture<KeySoundDecoder.DecodedSound>>>();
    private static final Map<KeySoundDecoder.DecodedSound, Integer> BUFFERS = new IdentityHashMap<KeySoundDecoder.DecodedSound, Integer>();
    private static final AtomicLong PLAYED = new AtomicLong();
    private static final int[] SOURCES = new int[32];
    private static volatile long generation;
    private static volatile float volume;
    private static volatile boolean unavailable;
    private static OpenAL al;
    private static Pointer device;
    private static Pointer context;
    private static Function threadContext;
    private static int nextSource;

    private KeySoundEngine() {
    }

    public static void play(Path file, float gain, int amplify) {
        if (gain <= 0.0f) {
            return;
        }
        long requested = generation;
        KeySoundEngine.load(file, amplify).thenAccept(sound -> AUDIO.execute(() -> {
            if (requested != generation || !KeySoundEngine.openAudio()) {
                return;
            }
            int buffer = KeySoundEngine.upload(sound);
            if (buffer == 0) {
                return;
            }
            int source = SOURCES[nextSource];
            nextSource = (nextSource + 1) % SOURCES.length;
            al.alSourceStop(source);
            al.alSourcei(source, 4105, buffer);
            al.alSourcef(source, 4106, Math.min(1.0f, gain));
            al.alSourcePlay(source);
            int error = al.alGetError();
            if (error == 0) {
                PLAYED.incrementAndGet();
            } else {
                LOGGER.warn("Could not play key sound {} (OpenAL {})", (Object)file, (Object)error);
            }
        }));
    }

    public static void warmup(List<Path> sounds, int amplify) {
        long requested = generation;
        for (Path file : sounds) {
            KeySoundEngine.load(file, amplify).thenAccept(sound -> AUDIO.execute(() -> {
                if (requested == generation && KeySoundEngine.openAudio()) {
                    KeySoundEngine.upload(sound);
                }
            }));
        }
    }

    public static void clearCache() {
        ++generation;
        CACHE.clear();
        AUDIO.execute(() -> {
            if (context != null) {
                for (int source : SOURCES) {
                    al.alSourceStop(source);
                    al.alSourcei(source, 4105, 0);
                }
                Object object = BUFFERS.values().iterator();
                while (object.hasNext()) {
                    int buffer = (Integer)object.next();
                    al.alDeleteBuffers(1, new int[]{buffer});
                }
                BUFFERS.clear();
            }
            unavailable = false;
        });
    }

    public static void setVolume(float value) {
        volume = Math.max(0.0f, Math.min(1.0f, value));
        AUDIO.execute(() -> {
            if (context != null) {
                al.alListenerf(4106, volume);
            }
        });
    }

    public static boolean isUnavailable() {
        return unavailable;
    }

    public static long getPlaybackCount() {
        return PLAYED.get();
    }

    private static CompletableFuture<KeySoundDecoder.DecodedSound> load(Path file, int amplify) {
        Path path = file.toAbsolutePath().normalize();
        int gain = Math.max(1, Math.min(16, amplify));
        Map variants = CACHE.computeIfAbsent(path, key -> new ConcurrentHashMap());
        return variants.computeIfAbsent(gain, key -> CompletableFuture.supplyAsync(() -> {
            try {
                KeySoundDecoder.DecodedSound sound = KeySoundDecoder.decode(path);
                KeySoundDecoder.amplify(sound.samples, gain, sound.format.isBigEndian());
                return sound;
            }
            catch (IOException | RuntimeException e) {
                LOGGER.warn("Could not load key sound {}", (Object)path, (Object)e);
                throw new CompletionException(e);
            }
        }, DECODER));
    }

    private static int upload(KeySoundDecoder.DecodedSound sound) {
        Integer existing = BUFFERS.get(sound);
        if (existing != null) {
            return existing;
        }
        int channels = sound.format.getChannels();
        if (sound.samples.remaining() == 0 || channels != 1 && channels != 2) {
            return 0;
        }
        int[] id = new int[1];
        al.alGenBuffers(1, id);
        al.alBufferData(id[0], channels == 1 ? 4353 : 4355, sound.samples, sound.samples.remaining(), Math.round(sound.format.getSampleRate()));
        int error = al.alGetError();
        if (error != 0) {
            al.alDeleteBuffers(1, id);
            LOGGER.warn("Could not upload key sound (OpenAL {})", (Object)error);
            return 0;
        }
        BUFFERS.put(sound, id[0]);
        return id[0];
    }

    private static boolean openAudio() {
        if (context != null) {
            return true;
        }
        if (unavailable) {
            return false;
        }
        try {
            al = (OpenAL)Native.load((String)KeySoundEngine.audioLibrary(), OpenAL.class);
            device = al.alcOpenDevice(null);
            if (device == null) {
                throw new IOException("OpenAL could not open the output device");
            }
            if (al.alcIsExtensionPresent(device, "ALC_EXT_thread_local_context") == 0) {
                throw new IOException("OpenAL does not support thread-local contexts");
            }
            Pointer function = al.alcGetProcAddress(device, "alcSetThreadContext");
            if (function == null) {
                throw new IOException("OpenAL has no alcSetThreadContext function");
            }
            threadContext = Function.getFunction((Pointer)function);
            context = al.alcCreateContext(device, null);
            if (context == null || (Byte)threadContext.invoke(Byte.TYPE, new Object[]{context}) == 0) {
                throw new IOException("OpenAL could not create the key sound context");
            }
            al.alListenerf(4106, volume);
            al.alGenSources(SOURCES.length, SOURCES);
            for (int source : SOURCES) {
                al.alSourcei(source, 514, 1);
            }
            int error = al.alGetError();
            if (error != 0) {
                throw new IOException("OpenAL source setup failed: " + error);
            }
            return true;
        }
        catch (IOException | LinkageError | ReflectiveOperationException | RuntimeException e) {
            LOGGER.error("Could not start independent key sound audio", e);
            KeySoundEngine.closeAudio();
            unavailable = true;
            return false;
        }
    }

    private static String audioLibrary() throws ReflectiveOperationException {
        block4: {
            try {
                String path;
                Class<?> alc = Class.forName("org.lwjgl.openal.ALC");
                Object provider = alc.getMethod("getFunctionProvider", new Class[0]).invoke(null, new Object[0]);
                Class<?> shared = Class.forName("org.lwjgl.system.SharedLibrary");
                if (shared.isInstance(provider) && (path = (String)shared.getMethod("getPath", new Class[0]).invoke(provider, new Object[0])) != null) {
                    return path;
                }
            }
            catch (ClassNotFoundException e) {
                String natives = System.getProperty("org.lwjgl.librarypath");
                if (natives == null) break block4;
                for (String name : new String[]{"OpenAL64.dll", "OpenAL32.dll", "libopenal.so", "openal.dylib"}) {
                    Path path = Paths.get(natives, name);
                    if (!Files.isRegularFile(path, new LinkOption[0])) continue;
                    return path.toString();
                }
            }
        }
        return System.getProperty("org.lwjgl.openal.libname", "openal");
    }

    private static void closeAudio() {
        if (context != null) {
            if (SOURCES[0] != 0) {
                al.alDeleteSources(SOURCES.length, SOURCES);
            }
            for (int buffer : BUFFERS.values()) {
                al.alDeleteBuffers(1, new int[]{buffer});
            }
            BUFFERS.clear();
            if (threadContext != null) {
                threadContext.invoke(Byte.TYPE, new Object[]{Pointer.NULL});
            }
            al.alcDestroyContext(context);
            context = null;
            Arrays.fill(SOURCES, 0);
        }
        if (device != null) {
            al.alcCloseDevice(device);
            device = null;
        }
    }

    static {
        volume = 1.0f;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                AUDIO.submit(KeySoundEngine::closeAudio).get(2L, TimeUnit.SECONDS);
            }
            catch (Exception exception) {
            }
            finally {
                AUDIO.shutdownNow();
                DECODER.shutdownNow();
            }
        }, "DecideVisuals-KeySoundShutdown"));
    }

    @Environment(value=EnvType.CLIENT)
    public static interface OpenAL
    extends Library {
        public Pointer alcOpenDevice(String var1);

        public byte alcCloseDevice(Pointer var1);

        public Pointer alcCreateContext(Pointer var1, int[] var2);

        public void alcDestroyContext(Pointer var1);

        public byte alcIsExtensionPresent(Pointer var1, String var2);

        public Pointer alcGetProcAddress(Pointer var1, String var2);

        public void alGenSources(int var1, int[] var2);

        public void alDeleteSources(int var1, int[] var2);

        public void alSourcei(int var1, int var2, int var3);

        public void alSourcef(int var1, int var2, float var3);

        public void alSourcePlay(int var1);

        public void alSourceStop(int var1);

        public void alGenBuffers(int var1, int[] var2);

        public void alDeleteBuffers(int var1, int[] var2);

        public void alBufferData(int var1, int var2, ByteBuffer var3, int var4, int var5);

        public void alListenerf(int var1, float var2);

        public int alGetError();
    }
}

