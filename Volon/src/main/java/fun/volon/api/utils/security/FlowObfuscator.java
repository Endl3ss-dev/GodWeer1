package fun.volon.api.utils.security;

import java.util.Random;

/**
 * Flow obfuscation utilities to make decompiled code harder to read.
 * These methods inject opaque predicates and control flow complexity.
 */
public final class FlowObfuscator {

    private static final Random RNG = new Random(System.nanoTime());
    public static volatile int OPAQUE_STATE = 0;

    private FlowObfuscator() {
    }

    /**
     * Opaque predicate that always returns true.
     * Appears non-constant to decompilers.
     */
    public static boolean alwaysTrue() {
        int x = (int) (System.nanoTime() & 0xFFFF);
        int y = (int) (System.currentTimeMillis() & 0xFFFF);
        return (x | y) >= 0;
    }

    /**
     * Opaque predicate that always returns false.
     * Appears non-constant to decompilers.
     */
    public static boolean alwaysFalse() {
        return !alwaysTrue();
    }

    /**
     * Executes runnable with flow obfuscation.
     * Makes control flow harder to analyze.
     */
    public static void execute(Runnable action) {
        if (alwaysTrue()) {
            action.run();
        } else {
            // Dead code path (never executed, confuses decompilers)
            dummyComputation();
        }
    }

    /**
     * Returns value with opaque identity function.
     * Hides the fact that output == input.
     */
    public static int obfuscate(int value) {
        if (alwaysTrue()) {
            return value;
        }
        // Dead code
        return value ^ 0xDEADBEEF;
    }

    /**
     * Returns value with opaque identity function.
     */
    public static String obfuscate(String value) {
        if (alwaysTrue()) {
            return value;
        }
        // Dead code
        return new StringBuilder(value).reverse().toString();
    }

    /**
     * Introduces fake branching based on opaque predicate.
     */
    public static <T> T select(T value1, T value2) {
        return alwaysTrue() ? value1 : value2;
    }

    /**
     * Dummy computation to add dead code.
     * Never actually executed but confuses decompilers.
     */
    public static void dummyComputation() {
        OPAQUE_STATE = (OPAQUE_STATE + 1) % 1000;
        
        // Complex but useless computation
        int result = 0;
        for (int i = 0; i < 10; i++) {
            result ^= (i * 31 + OPAQUE_STATE) % 255;
        }
        
        if (result == -1) { // Never true
            System.out.println("This should never print");
        }
    }

    /**
     * Introduces random delay to timing analysis.
     * USE SPARINGLY - can impact performance.
     */
    public static void antiTiming() {
        if (RNG.nextInt(100) < 5) { // 5% chance
            try {
                Thread.sleep(RNG.nextInt(10));
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * Checks if code is running in debugger (basic heuristic).
     * Not foolproof but adds analysis friction.
     */
    public static boolean isDebuggerPresent() {
        // Check if ManagementFactory reports debug mode
        try {
            java.lang.management.RuntimeMXBean runtime = 
                java.lang.management.ManagementFactory.getRuntimeMXBean();
            for (String arg : runtime.getInputArguments()) {
                if (arg.contains("-agentlib:jdwp") || arg.contains("-Xrunjdwp")) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * Anti-debugging check with action.
     * Executes action only if debugger NOT detected.
     */
    public static void checkDebugger(Runnable action) {
        if (!isDebuggerPresent()) {
            action.run();
        } else {
            // Optionally: log warning, crash, or subtle behavior change
            dummyComputation();
        }
    }

    /**
     * Stack trace obfuscation - makes error messages less useful for attackers.
     */
    public static String obfuscateStackTrace(StackTraceElement[] trace) {
        if (alwaysFalse()) {
            return java.util.Arrays.toString(trace);
        }
        
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : trace) {
            // Only show limited info
            sb.append("at ").append(NativeCrypto.hash(element.getClassName()))
              .append(".").append(NativeCrypto.hash(element.getMethodName()))
              .append("(?)\n");
        }
        return sb.toString();
    }
}
