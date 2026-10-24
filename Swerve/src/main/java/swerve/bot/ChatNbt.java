package swerve.bot;

import java.nio.charset.StandardCharsets;

/**
 * Разбор сетевого NBT — в него Minecraft 1.20.2+ упаковывает текстовые
 * компоненты (чат, причины кика, названия окон).
 *
 * <p>Нам не нужна вся модель NBT: достаточно вытащить читаемый текст, чтобы
 * бот видел сообщения сервера (триггеры «ждать в чате», логи, авторизация).
 * Парсер терпим к мусору: обрезанные и незнакомые теги дают пустую строку,
 * а не исключение, иначе один странный пакет рвал бы сессию.
 *
 * <p>Старые сервера и некоторые плагины присылают в этих полях JSON-строку —
 * она приходит как NBT-тег String и попадает в результат как есть.
 */
final class ChatNbt {

    private static final int MAX_DEPTH = 16;
    private static final int MAX_LENGTH = 512;

    private ChatNbt() {
    }

    /** Читает компонент текста из начала буфера и возвращает плоскую строку. */
    static String read(PacketReader reader) {
        StringBuilder out = new StringBuilder();
        try {
            readTag(reader, out, 0, true);
        } catch (RuntimeException ignored) {
            // Битый или незнакомый тег — показываем то, что успели собрать.
        }
        String text = out.toString().trim();
        return text.length() > MAX_LENGTH ? text.substring(0, MAX_LENGTH) : text;
    }

    private static void readTag(PacketReader reader, StringBuilder out, int depth, boolean top) {
        if (depth > MAX_DEPTH) {
            reader.skip(reader.remaining());
            return;
        }
        int type = reader.readUnsignedByte();
        if (type == 0) {
            return;
        }
        if (top) {
            readString(reader, 1); // имя корневого тега, всегда пустое
        }
        readPayload(reader, type, out, depth);
    }

    private static void readPayload(PacketReader reader, int type, StringBuilder out, int depth) {
        switch (type) {
            case 1 -> reader.readByte();
            case 2 -> reader.readUnsignedShort();
            case 3 -> reader.readInt();
            case 4 -> reader.readLong();
            case 5 -> reader.readFloat();
            case 6 -> reader.readDouble();
            case 7 -> {
                int length = reader.readInt();
                reader.skip(Math.max(0, length));
            }
            case 8 -> appendText(out, readString(reader, 32767));
            case 9 -> {
                int elementType = reader.readUnsignedByte();
                int length = reader.readInt();
                for (int i = 0; i < length && i < 256; i++) {
                    readPayload(reader, elementType, out, depth + 1);
                }
            }
            case 10 -> readCompound(reader, out, depth + 1);
            case 11 -> {
                int length = reader.readInt();
                reader.skip(Math.max(0, length) * 4);
            }
            case 12 -> {
                int length = reader.readInt();
                reader.skip(Math.max(0, length) * 8);
            }
            default -> reader.skip(reader.remaining());
        }
    }

    private static void readCompound(PacketReader reader, StringBuilder out, int depth) {
        while (true) {
            if (depth > MAX_DEPTH || reader.isExhausted()) {
                return;
            }
            int type = reader.readUnsignedByte();
            if (type == 0) {
                return;
            }
            String name = readString(reader, 64);
            switch (name) {
                case "text" -> {
                    if (type == 8) {
                        appendText(out, readString(reader, 32767));
                    } else {
                        readPayload(reader, type, out, depth + 1);
                    }
                }
                case "translate" -> {
                    if (type == 8) {
                        String value = readString(reader, 32767);
                        appendText(out, value);
                    } else {
                        readPayload(reader, type, out, depth + 1);
                    }
                }
                case "extra", "with" -> readPayload(reader, type, out, depth + 1);
                case "score" -> {
                    if (type == 10) {
                        readCompound(reader, out, depth + 1);
                    } else {
                        readPayload(reader, type, out, depth + 1);
                    }
                }
                default -> readPayload(reader, type, out, depth + 1);
            }
        }
    }

    /**
     * NBT-строка: длина в байтах (u16) и modified UTF-8. Читаем байты и
     * декодируем как UTF-8 — иначе русский чат превращался бы в кашу: каждый
     * байт многобайтового символа нельзя выдавать за отдельный char.
     */
    private static String readString(PacketReader reader, int max) {
        int length = reader.readUnsignedShort();
        if (length > max * 4 || length > reader.remaining()) {
            reader.skip(reader.remaining());
            return "";
        }
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) reader.readByte();
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static void appendText(StringBuilder out, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (out.length() > 0) {
            out.append(' ');
        }
        out.append(value);
    }
}
