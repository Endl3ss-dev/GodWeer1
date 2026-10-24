package swerve.bot;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Чтение тела пакета: обратная сторона {@link PacketWriter}.
 *
 * <p>Выход за границы буфера не бросает исключения: неудачное чтение
 * возвращает 0/пустую строку, а {@link #isExhausted()} сообщает, что данные
 * кончились. Так разбор незнакомого пакета не роняет сессию бота.
 */
public final class PacketReader {

    private final byte[] data;
    private int index;

    public PacketReader(byte[] data) {
        this.data = data == null ? new byte[0] : data;
    }

    public boolean isExhausted() {
        return this.index >= this.data.length;
    }

    public int remaining() {
        return this.data.length - this.index;
    }

    public int readVarInt() {
        int value = 0;
        int position = 0;
        while (true) {
            if (this.index >= this.data.length || position > 35) {
                return value;
            }
            int current = this.data[this.index++] & 0xFF;
            value |= (current & 0x7F) << position;
            if ((current & 0x80) == 0) {
                return value;
            }
            position += 7;
        }
    }

    public String readString(int max) {
        int length = readVarInt();
        if (length < 0 || length > max * 4 || this.index + length > this.data.length) {
            this.index = this.data.length;
            return "";
        }
        String value = new String(this.data, this.index, length, StandardCharsets.UTF_8);
        this.index += length;
        return value;
    }

    public boolean readBoolean() {
        return readByte() != 0;
    }

    public int readByte() {
        return this.index < this.data.length ? this.data[this.index++] & 0xFF : 0;
    }

    public int readUnsignedByte() {
        return readByte();
    }

    public int readUnsignedShort() {
        return (readByte() << 8) | readByte();
    }

    public int readInt() {
        return (readByte() << 24) | (readByte() << 16) | (readByte() << 8) | readByte();
    }

    public long readLong() {
        long value = 0L;
        for (int i = 0; i < 8; i++) {
            value = (value << 8) | readByte();
        }
        return value;
    }

    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    public UUID readUuid() {
        return new UUID(readLong(), readLong());
    }

    /** Пропустить {@code count} байт. */
    public void skip(int count) {
        this.index = Math.min(this.data.length, this.index + Math.max(0, count));
    }

    public byte[] readRest() {
        int left = remaining();
        byte[] rest = new byte[Math.max(0, left)];
        if (left > 0) {
            System.arraycopy(this.data, this.index, rest, 0, left);
        }
        this.index = this.data.length;
        return rest;
    }
}
