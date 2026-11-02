package swerve.bot;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Сборка тела пакета: примитивы протокола Minecraft.
 *
 * <p>Своя реализация вместо ByteBuf: headless-клиенту нужны только VarInt,
 * строки и числа, а лишняя зависимость от сетевого стека игры усложнила бы
 * отладку. Все методы пишут в {@link ByteArrayOutputStream}, готовые байты
 * забирает {@link #toByteArray()}.
 */
public final class PacketWriter {

    private final ByteArrayOutputStream out = new ByteArrayOutputStream(64);

    public PacketWriter varInt(int value) {
        while ((value & 0xFFFFFF80) != 0) {
            this.out.write((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        this.out.write(value);
        return this;
    }

    public PacketWriter string(String value) {
        byte[] bytes = (value == null ? "" : value).getBytes(StandardCharsets.UTF_8);
        varInt(bytes.length);
        this.out.write(bytes, 0, bytes.length);
        return this;
    }

    public PacketWriter bool(boolean value) {
        this.out.write(value ? 1 : 0);
        return this;
    }

    public PacketWriter byteValue(int value) {
        this.out.write(value & 0xFF);
        return this;
    }

    public PacketWriter shortValue(int value) {
        this.out.write((value >>> 8) & 0xFF);
        this.out.write(value & 0xFF);
        return this;
    }

    public PacketWriter intValue(int value) {
        this.out.write((value >>> 24) & 0xFF);
        this.out.write((value >>> 16) & 0xFF);
        this.out.write((value >>> 8) & 0xFF);
        this.out.write(value & 0xFF);
        return this;
    }

    public PacketWriter longValue(long value) {
        for (int shift = 56; shift >= 0; shift -= 8) {
            this.out.write((int) (value >>> shift) & 0xFF);
        }
        return this;
    }

    public PacketWriter floatValue(float value) {
        return intValue(Float.floatToIntBits(value));
    }

    public PacketWriter doubleValue(double value) {
        return longValue(Double.doubleToLongBits(value));
    }

    public PacketWriter uuid(UUID value) {
        longValue(value.getMostSignificantBits());
        longValue(value.getLeastSignificantBits());
        return this;
    }

    public PacketWriter bytes(byte[] value) {
        if (value != null && value.length > 0) {
            this.out.write(value, 0, value.length);
        }
        return this;
    }

    public int size() {
        return this.out.size();
    }

    public byte[] toByteArray() {
        return this.out.toByteArray();
    }
}
