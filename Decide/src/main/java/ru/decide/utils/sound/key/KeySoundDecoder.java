/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.jcraft.jogg.Packet
 *  com.jcraft.jogg.Page
 *  com.jcraft.jogg.StreamState
 *  com.jcraft.jogg.SyncState
 *  com.jcraft.jorbis.Block
 *  com.jcraft.jorbis.Comment
 *  com.jcraft.jorbis.DspState
 *  com.jcraft.jorbis.Info
 *  javazoom.jl.decoder.Bitstream
 *  javazoom.jl.decoder.Decoder
 *  javazoom.jl.decoder.Header
 *  javazoom.jl.decoder.JavaLayerException
 *  javazoom.jl.decoder.SampleBuffer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.utils.sound.key;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Locale;
import javax.sound.sampled.AudioFormat;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class KeySoundDecoder {
    private KeySoundDecoder() {
    }

    public static boolean isSupported(Path path) {
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return name.endsWith(".ogg") || name.endsWith(".wav") || name.endsWith(".mp3");
    }

    public static DecodedSound decode(Path path) throws IOException {
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        if (name.endsWith(".ogg")) {
            return KeySoundDecoder.readOgg(path);
        }
        return name.endsWith(".mp3") ? KeySoundDecoder.readMp3(path) : KeySoundDecoder.readWav(path);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static DecodedSound readOgg(Path path) throws IOException {
        SyncState sync = new SyncState();
        StreamState stream = new StreamState();
        Info info = new Info();
        Comment comment = new Comment();
        DspState dsp = new DspState();
        Block block = new Block(dsp);
        Page page = new Page();
        Packet packet = new Packet();
        sync.init();
        info.init();
        comment.init();
        ByteArrayOutputStream pcm = new ByteArrayOutputStream();
        int headers = 0;
        boolean ended = false;
        try (InputStream input = Files.newInputStream(path, new OpenOption[0]);){
            while (!ended) {
                int result = sync.pageout(page);
                if (result < 0) {
                    throw new IOException("Corrupt OGG page");
                }
                if (result == 0) {
                    int offset = sync.buffer(4096);
                    int count = input.read(sync.data, offset, 4096);
                    if (count < 0) {
                        break;
                    }
                    sync.wrote(count);
                    continue;
                }
                if (headers == 0) {
                    stream.init(page.serialno());
                }
                if (stream.pagein(page) < 0) {
                    throw new IOException("Invalid OGG stream");
                }
                while ((result = stream.packetout(packet)) != 0) {
                    int count;
                    if (result < 0) {
                        throw new IOException("Corrupt OGG packet");
                    }
                    if (headers < 3) {
                        if (info.synthesis_headerin(comment, packet) < 0) {
                            throw new IOException("Not OGG Vorbis audio");
                        }
                        if (++headers != 3) continue;
                        if (info.channels != 1 && info.channels != 2 || info.rate <= 0) {
                            throw new IOException("OGG must be mono or stereo");
                        }
                        if (dsp.synthesis_init(info) != 0) {
                            throw new IOException("Could not initialize Vorbis decoder");
                        }
                        block.init(dsp);
                        continue;
                    }
                    if (block.synthesis(packet) != 0) {
                        throw new IOException("Invalid Vorbis audio packet");
                    }
                    dsp.synthesis_blockin(block);
                    float[][][] samples = new float[1][][];
                    int[] indices = new int[info.channels];
                    while ((count = dsp.synthesis_pcmout((float[][][])samples, indices)) > 0) {
                        for (int i = 0; i < count; ++i) {
                            for (int channel = 0; channel < info.channels; ++channel) {
                                int value = Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, Math.round(samples[0][channel][indices[channel] + i] * 32767.0f)));
                                pcm.write(value & 0xFF);
                                pcm.write(value >>> 8 & 0xFF);
                            }
                        }
                        dsp.synthesis_read(count);
                    }
                }
                ended = page.eos() != 0;
            }
        }
        finally {
            block.clear();
            dsp.clear();
            stream.clear();
            info.clear();
            sync.clear();
        }
        if (!ended || headers != 3 || pcm.size() == 0) {
            throw new IOException("Incomplete OGG audio");
        }
        byte[] bytes = pcm.toByteArray();
        ByteBuffer data = ByteBuffer.allocateDirect(bytes.length).order(ByteOrder.LITTLE_ENDIAN);
        data.put(bytes).flip();
        return new DecodedSound(data, new AudioFormat(info.rate, 16, info.channels, true, false));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static DecodedSound readMp3(Path path) throws IOException {
        try (BufferedInputStream input = new BufferedInputStream(Files.newInputStream(path, new OpenOption[0]));){
            Bitstream stream = new Bitstream((InputStream)input);
            Decoder decoder = new Decoder();
            ByteArrayOutputStream pcm = new ByteArrayOutputStream();
            int rate = 0;
            int channels = 0;
            try {
                Header header;
                while ((header = stream.readFrame()) != null) {
                    try {
                        SampleBuffer frame = (SampleBuffer)decoder.decodeFrame(header, stream);
                        if (rate != 0 && (rate != frame.getSampleFrequency() || channels != frame.getChannelCount())) {
                            throw new IOException("MP3 changes sample rate or channel count");
                        }
                        rate = frame.getSampleFrequency();
                        channels = frame.getChannelCount();
                        short[] samples = frame.getBuffer();
                        for (int i = 0; i < frame.getBufferLength(); ++i) {
                            pcm.write(samples[i] & 0xFF);
                            pcm.write(samples[i] >>> 8 & 0xFF);
                        }
                    }
                    finally {
                        stream.closeFrame();
                    }
                }
            }
            catch (JavaLayerException e) {
                throw new IOException("Could not decode MP3", e);
            }
            finally {
                try {
                    stream.close();
                }
                catch (JavaLayerException e) {
                    throw new IOException("Could not close MP3 stream", e);
                }
            }
            if (pcm.size() == 0) {
                throw new IOException("MP3 contains no audio frames");
            }
            byte[] bytes = pcm.toByteArray();
            ByteBuffer data = ByteBuffer.allocateDirect(bytes.length).order(ByteOrder.LITTLE_ENDIAN);
            data.put(bytes).flip();
            DecodedSound decodedSound = new DecodedSound(data, new AudioFormat(rate, 16, channels, true, false));
            return decodedSound;
        }
    }

    private static DecodedSound readWav(Path path) throws IOException {
        try (RandomAccessFile input = new RandomAccessFile(path.toFile(), "r");){
            if (input.readInt() != 1380533830) {
                throw new IOException("Not a RIFF WAV file");
            }
            input.skipBytes(4);
            if (input.readInt() != 1463899717) {
                throw new IOException("Not a WAVE file");
            }
            int codec = 0;
            int channels = 0;
            int rate = 0;
            int bits = 0;
            int frameSize = 0;
            long dataStart = 0L;
            long dataSize = 0L;
            while (input.getFilePointer() + 8L <= input.length()) {
                int chunk = input.readInt();
                long size = Integer.toUnsignedLong(Integer.reverseBytes(input.readInt()));
                long start = input.getFilePointer();
                if (size > input.length() - start) {
                    throw new IOException("Truncated WAV chunk");
                }
                if (chunk == 1718449184) {
                    if (size < 16L) {
                        throw new IOException("Incomplete WAV format");
                    }
                    codec = Short.toUnsignedInt(Short.reverseBytes(input.readShort()));
                    channels = Short.toUnsignedInt(Short.reverseBytes(input.readShort()));
                    rate = Integer.reverseBytes(input.readInt());
                    input.skipBytes(4);
                    frameSize = Short.toUnsignedInt(Short.reverseBytes(input.readShort()));
                    bits = Short.toUnsignedInt(Short.reverseBytes(input.readShort()));
                    if (codec == 65534 && size >= 40L) {
                        input.skipBytes(8);
                        codec = Short.toUnsignedInt(Short.reverseBytes(input.readShort()));
                    }
                } else if (chunk == 1684108385) {
                    dataStart = start;
                    dataSize = size;
                }
                input.seek(start + size + (size & 1L));
            }
            if (codec != 1 && codec != 3 || channels != 1 && channels != 2 || rate <= 0 || bits != 8 && bits != 16 && bits != 24 && bits != 32 || codec == 3 && bits != 32 || frameSize != channels * (bits / 8)) {
                throw new IOException("WAV must use mono/stereo PCM (8/16/24/32-bit) or 32-bit float");
            }
            if (dataSize == 0L || dataSize > Integer.MAX_VALUE || dataSize % (long)frameSize != 0L) {
                throw new IOException("Invalid WAV audio length");
            }
            input.seek(dataStart);
            byte[] bytes = new byte[(int)dataSize];
            input.readFully(bytes);
            ByteBuffer source = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
            int sampleCount = (int)(dataSize / (long)(bits / 8));
            if (sampleCount > 0x3FFFFFFF) {
                throw new IOException("WAV audio is too large");
            }
            ByteBuffer pcm = ByteBuffer.allocateDirect(sampleCount * 2).order(ByteOrder.LITTLE_ENDIAN);
            for (int i = 0; i < sampleCount; ++i) {
                int value;
                if (bits == 8) {
                    value = (source.get() & 0xFF) - 128 << 8;
                } else if (bits == 16) {
                    value = source.getShort();
                } else if (bits == 24) {
                    value = source.get() & 0xFF | (source.get() & 0xFF) << 8 | source.get() << 16;
                    value >>= 8;
                } else {
                    value = source.getInt();
                    if (codec == 3) {
                        float sample = Float.intBitsToFloat(value);
                        value = Math.round(Math.max(-1.0f, Math.min(1.0f, sample)) * 32767.0f);
                    } else {
                        value >>= 16;
                    }
                }
                pcm.putShort((short)value);
            }
            pcm.flip();
            DecodedSound decodedSound = new DecodedSound(pcm, new AudioFormat(rate, 16, channels, true, false));
            return decodedSound;
        }
    }

    public static void amplify(ByteBuffer samples, int gain, boolean bigEndian) {
        if (gain == 1) {
            return;
        }
        ByteBuffer pcm = samples.duplicate().order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        int i = pcm.position();
        while (i + 1 < pcm.limit()) {
            int sample = pcm.getShort(i) * gain;
            pcm.putShort(i, (short)Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, sample)));
            i += 2;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class DecodedSound {
        public final ByteBuffer samples;
        public final AudioFormat format;

        public DecodedSound(ByteBuffer samples, AudioFormat format) {
            this.samples = samples;
            this.format = format;
        }
    }
}

