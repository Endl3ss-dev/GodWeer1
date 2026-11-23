// deobf by pusia
package su.catlean.utils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.concurrent.ThreadsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.text.StringsKt;
import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonBuilder;
import kotlinx.serialization.json.JsonKt;

/**
 * Kotlin compiles default arguments to synthetic {@code name$default} methods that javac refuses to see.
 * The original Kotlin source called them implicitly; these keep the same mask semantics.
 */
public final class KotlinCompat {
    private KotlinCompat() {
    }

    public static boolean contains(CharSequence text, CharSequence other, boolean ignoreCase, int mask, Object marker) {
        return StringsKt.contains(text, other, (mask) == 0 && ignoreCase);
    }

    public static boolean contains(CharSequence text, char other, boolean ignoreCase, int mask, Object marker) {
        return StringsKt.contains(text, other, (mask) == 0 && ignoreCase);
    }

    public static String replace(String text, String from, String to, boolean ignoreCase, int mask, Object marker) {
        return StringsKt.replace(text, from, to, (mask) == 0 && ignoreCase);
    }

    public static String replace(String text, char from, char to, boolean ignoreCase, int mask, Object marker) {
        return StringsKt.replace(text, from, to, (mask) == 0 && ignoreCase);
    }

    public static String replaceFirst(String text, String from, String to, boolean ignoreCase, int mask, Object marker) {
        return StringsKt.replaceFirst(text, from, to, (mask) == 0 && ignoreCase);
    }

    public static boolean startsWith(String text, String prefix, boolean ignoreCase, int mask, Object marker) {
        return StringsKt.startsWith(text, prefix, (mask) == 0 && ignoreCase);
    }

    public static List<String> split(CharSequence text, String[] delimiters, boolean ignoreCase, int limit, int mask, Object marker) {
        return StringsKt.split(text, delimiters, (mask) == 0 && ignoreCase, (mask) != 0 ? 0 : limit);
    }

    public static List<String> split(CharSequence text, char[] delimiters, boolean ignoreCase, int limit, int mask, Object marker) {
        return StringsKt.split(text, delimiters, (mask) == 0 && ignoreCase, (mask) != 0 ? 0 : limit);
    }

    public static String substringAfter(String text, String delimiter, String missing, int mask, Object marker) {
        return StringsKt.substringAfter(text, delimiter, (mask) != 0 ? text : missing);
    }

    public static String substringBefore(String text, String delimiter, String missing, int mask, Object marker) {
        return StringsKt.substringBefore(text, delimiter, (mask) != 0 ? text : missing);
    }

    public static String substringBefore(String text, char delimiter, String missing, int mask, Object marker) {
        return StringsKt.substringBefore(text, delimiter, (mask) != 0 ? text : missing);
    }

    public static int indexOf(CharSequence text, char other, int start, boolean ignoreCase, int mask, Object marker) {
        return StringsKt.indexOf(text, other, (mask) != 0 ? 0 : start, (mask) == 0 && ignoreCase);
    }

    public static int indexOf(CharSequence text, String other, int start, boolean ignoreCase, int mask, Object marker) {
        return StringsKt.indexOf(text, other, (mask) != 0 ? 0 : start, (mask) == 0 && ignoreCase);
    }

    public static String readText(File file, Charset charset, int mask, Object marker) {
        return FilesKt.readText(file, (mask) != 0 ? StandardCharsets.UTF_8 : charset);
    }

    public static void writeText(File file, String text, Charset charset, int mask, Object marker) {
        FilesKt.writeText(file, text, (mask) != 0 ? StandardCharsets.UTF_8 : charset);
    }

    public static List<String> readLines(File file, Charset charset, int mask, Object marker) {
        return FilesKt.readLines(file, (mask) != 0 ? StandardCharsets.UTF_8 : charset);
    }

    public static Thread thread(boolean start, boolean daemon, ClassLoader loader, String name, int priority, Function0 block, int mask, Object marker) {
        return ThreadsKt.thread((mask) != 0 || start, (mask) == 0 && daemon, (mask) != 0 ? null : loader, (mask) != 0 ? null : name, (mask) != 0 ? -1 : priority, block);
    }

    public static String joinToString(Iterable items, CharSequence separator, CharSequence prefix, CharSequence postfix, int limit, CharSequence truncated, Function1 transform, int mask, Object marker) {
        return CollectionsKt.joinToString(items, (mask) != 0 ? ", " : separator, (mask) != 0 ? "" : prefix, (mask) != 0 ? "" : postfix, (mask) != 0 ? -1 : limit, (mask) != 0 ? "..." : truncated, (mask) != 0 ? null : transform);
    }

    public static Json Json(Json from, Function1<JsonBuilder, kotlin.Unit> builder, int mask, Object marker) {
        return JsonKt.Json((mask) != 0 ? Json.Default : from, builder);
    }
}