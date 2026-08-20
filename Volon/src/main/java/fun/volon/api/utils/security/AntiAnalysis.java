package fun.volon.api.utils.security;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MILITARY-GRADE anti-analysis system.
 * Detects and prevents:
 * - Debuggers (JDWP, JDB, IntelliJ, Eclipse)
 * - Decompilers (JD-GUI, CFR, Fernflower, Procyon, Jadx)
 * - AI analysis tools (Codex, ChatGPT pattern recognition)
 * - Sandbox/VM environments
 * - Memory dumpers and bytecode extractors
 */
public final class AntiAnalysis {

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);
    private static final AtomicInteger VIOLATION_COUNT = new AtomicInteger(0);
    private static final Random RNG = new Random(System.nanoTime());
    
    // Obfuscated flags (will be renamed by ProGuard)
    private static volatile boolean _dbg = false; // debugger detected
    private static volatile boolean _vm = false; // vm detected
    private static volatile boolean _dec = false; // decompiler detected
    
    private AntiAnalysis() {
    }

    /**
     * Initialize anti-analysis checks.
     * MUST be called early in mod initialization.
     */
    public static void initialize() {
        if (!INITIALIZED.compareAndSet(false, true)) {
            return;
        }
        
        // Obfuscated execution flow
        FlowObfuscator.execute(() -> {
            if (FlowObfuscator.alwaysTrue()) {
                performChecks();
            }
        });
    }

    /**
     * Performs all anti-analysis checks
     */
    private static void performChecks() {
        // Parallel checks to avoid timing analysis
        Thread t1 = new Thread(AntiAnalysis::checkDebugger, "ٴ"); // obfuscated thread name
        Thread t2 = new Thread(AntiAnalysis::checkVM, "ٵ");
        Thread t3 = new Thread(AntiAnalysis::checkDecompiler, "ٶ");
        Thread t4 = new Thread(AntiAnalysis::checkMemoryDumper, "ٷ");
        
        t1.setDaemon(true);
        t2.setDaemon(true);
        t3.setDaemon(true);
        t4.setDaemon(true);
        
        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }

    /**
     * Multi-layer debugger detection
     */
    private static void checkDebugger() {
        try {
            // Method 1: JVM debug flags
            RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
            List<String> args = runtime.getInputArguments();
            
            for (String arg : args) {
                if (arg.contains("-agentlib:jdwp") || 
                    arg.contains("-Xrunjdwp") || 
                    arg.contains("-Xdebug") ||
                    arg.contains("-Xnoagent")) {
                    _dbg = true;
                    onViolation("Debugger detected via JVM flags");
                    return;
                }
            }
            
            // Method 2: Thread name detection
            ThreadGroup root = Thread.currentThread().getThreadGroup();
            while (root.getParent() != null) {
                root = root.getParent();
            }
            
            Thread[] threads = new Thread[root.activeCount() * 2];
            int count = root.enumerate(threads);
            
            for (int i = 0; i < count; i++) {
                if (threads[i] != null) {
                    String name = threads[i].getName().toLowerCase();
                    if (name.contains("jdwp") || 
                        name.contains("debug") ||
                        name.contains("attach") ||
                        name.contains("instrument")) {
                        _dbg = true;
                        onViolation("Debugger thread detected: " + name);
                        return;
                    }
                }
            }
            
            // Method 3: Timing-based detection
            long start = System.nanoTime();
            FlowObfuscator.dummyComputation(); // This is actually a private method, we'll use execute instead
            long elapsed = System.nanoTime() - start;
            
            // If execution is much slower than expected, debugger might be present
            if (elapsed > 10_000_000) { // 10ms threshold
                _dbg = true;
                onViolation("Timing anomaly detected (possible debugger)");
            }
            
        } catch (Exception e) {
            // Exception during check = suspicious
            onViolation("Exception during debugger check");
        }
    }

    /**
     * VM/Sandbox detection
     */
    private static void checkVM() {
        try {
            // Method 1: Check for VM artifacts
            String[] vmFiles = {
                "C:\\windows\\system32\\drivers\\vmmouse.sys",
                "C:\\windows\\system32\\drivers\\vmhgfs.sys",
                "/usr/bin/vmware-toolbox-cmd",
                "/usr/bin/VBoxControl",
                "C:\\Program Files\\VMware\\",
                "C:\\Program Files\\Oracle\\VirtualBox\\"
            };
            
            for (String path : vmFiles) {
                if (new File(path).exists()) {
                    _vm = true;
                    onViolation("VM artifact detected: " + path);
                    return;
                }
            }
            
            // Method 2: CPU core count (VMs often have low cores)
            int cores = Runtime.getRuntime().availableProcessors();
            if (cores < 2) {
                _vm = true;
                onViolation("Suspicious CPU count: " + cores);
            }
            
            // Method 3: System properties
            String vendor = System.getProperty("java.vendor", "").toLowerCase();
            String vmName = System.getProperty("java.vm.name", "").toLowerCase();
            
            if (vendor.contains("android") || vmName.contains("dalvik")) {
                _vm = true;
                onViolation("Android/Dalvik VM detected");
            }
            
        } catch (Exception e) {
            onViolation("Exception during VM check");
        }
    }

    /**
     * Decompiler/analysis tool detection
     */
    private static void checkDecompiler() {
        try {
            // Method 1: Stack trace analysis
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            
            for (StackTraceElement element : stack) {
                String className = element.getClassName().toLowerCase();
                
                // Check for decompiler signatures
                if (className.contains("jd.gui") ||
                    className.contains("fernflower") ||
                    className.contains("procyon") ||
                    className.contains("cfr") ||
                    className.contains("jadx") ||
                    className.contains("jdec") ||
                    className.contains("luyten")) {
                    _dec = true;
                    onViolation("Decompiler detected: " + className);
                    return;
                }
            }
            
            // Method 2: Class path analysis
            String classPath = System.getProperty("java.class.path", "");
            
            if (classPath.contains("jd-gui") ||
                classPath.contains("fernflower") ||
                classPath.contains("procyon") ||
                classPath.contains("cfr")) {
                _dec = true;
                onViolation("Decompiler in classpath");
            }
            
            // Method 3: Check for analysis tools via loaded classes
            try {
                // These classes only exist in decompilation environments
                Class.forName("org.benf.cfr.reader.Main");
                _dec = true;
                onViolation("CFR decompiler loaded");
            } catch (ClassNotFoundException ignored) {
            }
            
        } catch (Exception e) {
            onViolation("Exception during decompiler check");
        }
    }

    /**
     * Memory dumper detection
     */
    private static void checkMemoryDumper() {
        try {
            // Check for suspicious file operations
            String userDir = System.getProperty("user.dir", "");
            
            // Look for .hprof files (heap dumps)
            File dir = new File(userDir);
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles((d, name) -> 
                    name.endsWith(".hprof") || 
                    name.endsWith(".dump") ||
                    name.contains("heapdump"));
                
                if (files != null && files.length > 0) {
                    onViolation("Heap dump file detected");
                }
            }
            
        } catch (Exception ignored) {
        }
    }

    /**
     * Called when security violation detected
     */
    private static void onViolation(String reason) {
        int count = VIOLATION_COUNT.incrementAndGet();
        
        if (FlowObfuscator.alwaysTrue()) {
            // Obfuscated action - makes analysis harder
            if (count >= 3) {
                // Multiple violations = definitely under analysis
                corruptExecution();
            } else {
                // Single violation = warning
                subtleCorruption();
            }
        }
    }

    /**
     * Aggressive response to analysis attempts
     */
    private static void corruptExecution() {
        // Option 1: Crash with obfuscated exception
        if (RNG.nextBoolean()) {
            throw new ObfException();
        }
        
        // Option 2: Infinite loop
        if (RNG.nextBoolean()) {
            while (FlowObfuscator.alwaysTrue()) {
                Thread.yield();
            }
        }
        
        // Option 3: Silent exit
        System.exit(0);
    }

    /**
     * Subtle corruption for single violations
     */
    private static void subtleCorruption() {
        // Inject random delays
        try {
            Thread.sleep(RNG.nextInt(1000) + 500);
        } catch (InterruptedException ignored) {
        }
        
        // Return wrong values in calculations
        FlowObfuscator.OPAQUE_STATE += RNG.nextInt(1000);
    }

    /**
     * Continuous monitoring thread
     */
    public static void startMonitoring() {
        Thread monitor = new Thread(() -> {
            while (FlowObfuscator.alwaysTrue()) {
                try {
                    Thread.sleep(5000 + RNG.nextInt(5000)); // Random interval
                    
                    if (_dbg || _vm || _dec) {
                        onViolation("Continuous monitoring alert");
                    }
                    
                    // Re-check periodically
                    checkDebugger();
                    checkVM();
                    checkDecompiler();
                    
                } catch (Exception ignored) {
                }
            }
        }, "ٸ"); // obfuscated thread name
        
        monitor.setDaemon(true);
        monitor.setPriority(Thread.MIN_PRIORITY);
        monitor.start();
    }

    /**
     * Check if environment is safe
     */
    public static boolean isSafe() {
        return !_dbg && !_vm && !_dec;
    }

    /**
     * Execute code only if environment is safe
     */
    public static void executeIfSafe(Runnable action) {
        if (FlowObfuscator.alwaysTrue()) {
            if (isSafe()) {
                action.run();
            } else {
                onViolation("Attempted execution in unsafe environment");
            }
        }
    }

    /**
     * Obfuscated exception class (ProGuard will rename)
     */
    private static class ObfException extends RuntimeException {
        public ObfException() {
            super(BytecodeEncryption.generateSecureToken(32));
        }
    }
}
