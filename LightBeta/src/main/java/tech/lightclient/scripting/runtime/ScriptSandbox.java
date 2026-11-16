package tech.lightclient.scripting.runtime;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cheap static check run before a script is compiled.
 *
 * <p>The docs promise that a script reaching for files, sockets, threads or reflection simply
 * does not load, and says which symbol was rejected, instead of misbehaving at runtime. This is
 * a source-level guard rather than a real security boundary: a determined author can still get
 * around it, but it catches the honest mistakes and the obvious abuse, which is what it is for.</p>
 */
public final class ScriptSandbox {

    public static final class Violation {
        public final int line;
        public final String symbol;

        Violation(int line, String symbol) {
            this.line = line;
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return "line " + line + ": " + symbol + " is not allowed in scripts";
        }
    }

    /** Package/class prefixes and members a script may not mention. */
    private static final List<String> FORBIDDEN = List.of(
            "java.io.File",
            "java.io.FileWriter",
            "java.io.FileReader",
            "java.io.FileInputStream",
            "java.io.FileOutputStream",
            "java.io.RandomAccessFile",
            "java.nio.file.",
            "java.net.Socket",
            "java.net.ServerSocket",
            "java.net.URL",
            "java.net.URI",
            "java.net.URLConnection",
            "java.net.http.",
            "java.lang.reflect.",
            "java.lang.ClassLoader",
            "java.lang.Runtime",
            "java.lang.ProcessBuilder",
            "java.lang.Process",
            "java.lang.System.exit",
            "java.lang.Thread",
            "java.util.Timer",
            "java.util.concurrent.",
            "javax.script.",
            "sun.misc.",
            "kotlin.io.path.",
            "kotlin.concurrent.",
            "ProcessBuilder",
            "ClassLoader",
            "Runtime.getRuntime",
            "System.exit",
            "System.getProperty",
            "System.setProperty",
            "System.getenv",
            "System.load",
            "Thread(",
            "Thread.sleep",
            "::class.java.getDeclared",
            ".declaredFields",
            ".declaredMethods",
            ".getDeclaredField",
            ".getDeclaredMethod",
            ".javaClass.getField",
            "readText()",
            "writeText(",
            "readLines()",
            "printStackTrace",
            "net.minecraft.",
            "tech.lightclient."
    );

    /** Kotlin's own imports are pointless (everything is pre-imported) and a sandbox hole. */
    private static final Pattern IMPORT = Pattern.compile("(?m)^\\s*import\\s+(\\S+)");

    private ScriptSandbox() {}

    /** Returns the first violation found, or {@code null} when the source looks acceptable. */
    public static Violation check(String source) {
        if (source == null) return null;

        String[] lines = source.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            String line = stripStringsAndComments(lines[i]);
            if (line.isBlank()) continue;
            for (String bad : FORBIDDEN) {
                if (line.contains(bad)) {
                    return new Violation(i + 1, bad);
                }
            }
        }

        Matcher m = IMPORT.matcher(stripBlockComments(source));
        if (m.find()) {
            int line = 1;
            for (int i = 0; i < m.start(); i++) {
                if (source.charAt(i) == '\n') line++;
            }
            return new Violation(line, "import " + m.group(1) + " (imports are unnecessary, the API is already in scope)");
        }
        return null;
    }

    /**
     * Blanks out string literals and line comments so that a script merely printing the word
     * "Thread" in a chat message is not rejected.
     */
    private static String stripStringsAndComments(String line) {
        StringBuilder out = new StringBuilder(line.length());
        boolean inString = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (!inString && c == '/' && i + 1 < line.length() && line.charAt(i + 1) == '/') break;
            if (c == '"' && (i == 0 || line.charAt(i - 1) != '\\')) {
                inString = !inString;
                out.append(' ');
                continue;
            }
            out.append(inString ? ' ' : c);
        }
        return out.toString();
    }

    private static String stripBlockComments(String source) {
        return source.replaceAll("(?s)/\\*.*?\\*/", " ");
    }
}
