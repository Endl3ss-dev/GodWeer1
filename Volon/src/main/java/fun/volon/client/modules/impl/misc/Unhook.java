package fun.volon.client.modules.impl.misc;

import org.lwjgl.glfw.GLFW;
import fun.volon.Volon;
import fun.volon.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.settings.implement.BindSetting;
import fun.volon.client.modules.settings.implement.BooleanSetting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Unhook extends Module {
    public static Unhook INSTANCE = new Unhook();
    
    private final BindSetting unhookKey = new BindSetting("Unhook Key", -1);
    private final BooleanSetting allowBaritone = new BooleanSetting("Allow Baritone Commands", true);
    private final BooleanSetting clearGPS = new BooleanSetting("Очистить GPS", true);
    private final BooleanSetting clearLogs = new BooleanSetting("Очистить логи", true);
    private final BooleanSetting clearBaritone = new BooleanSetting("Удалить Baritone папку", true);
    private final BooleanSetting clearRecent = new BooleanSetting("Очистить Recent файлы", true);
    private final BooleanSetting blockBaritone = new BooleanSetting("Блокировать Baritone папку", true);
    private final BooleanSetting deepClean = new BooleanSetting("Глубокая очистка следов", true);
    
    private static boolean isUnhooked = false;
    private static final Map<Module, Boolean> savedStates = new HashMap<>();
    private static boolean clickGuiWasEnabled = false;
    private static long lastKeyPressTime = 0;
    private static final long KEY_PRESS_COOLDOWN = 200; // 200ms cooldown
    
    public Unhook() {
        super("Unhook", "Скрывает все функции читов по нажатию кнопки", ModuleCategory.MISC);
        addSettings(unhookKey, allowBaritone, clearGPS, clearLogs, clearBaritone, clearRecent, blockBaritone, deepClean);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        if (blockBaritone.isState()) {
            // Запускаем блокировщик baritone через UnhookHelper
            UnhookHelper.startBaritoneBlocker(mc.runDirectory);
        }
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        UnhookHelper.stopBaritoneBlocker(mc.runDirectory);
        if (isUnhooked) {
            restoreClient();
        }
    }
    
    /**
     * Проверка разрешены ли команды Baritone
     */
    public static boolean isBaritoneAllowed() {
        return INSTANCE != null && INSTANCE.allowBaritone.isState();
    }
    
    /**
     * Проверка нажатия кнопки unhook
     */
    public static void checkUnhookKey() {
        if (INSTANCE == null || !INSTANCE.isEnable()) {
            return;
        }
        
        int key = INSTANCE.unhookKey.getKey();
        if (key == -1) {
            return;
        }
        
        // Проверяем cooldown
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastKeyPressTime < KEY_PRESS_COOLDOWN) {
            return;
        }
        
        // Проверяем нажатие клавиши
        if (isKeyPressed(key)) {
            lastKeyPressTime = currentTime;
            toggleUnhook();
        }
    }
    
    /**
     * Переключение состояния unhook
     */
    private static void toggleUnhook() {
        if (isUnhooked) {
            restoreClient();
        } else {
            hideClient();
        }
    }
    
    /**
     * Скрытие всех функций клиента
     */
    private static void hideClient() {
        if (isUnhooked) return;
        
        isUnhooked = true;
        savedStates.clear();
        
        // Очистка GPS waypoints
        if (INSTANCE.clearGPS.isState()) {
            clearGPSWaypoints();
        }
        
        // Очистка логов
        if (INSTANCE.clearLogs.isState()) {
            clearClientLogs();
        }
        
        // Удаление папки Baritone
        if (INSTANCE.clearBaritone.isState()) {
            deleteBaritoneFolder();
        }
        
        // Очистка Recent файлов Windows
        if (INSTANCE.clearRecent.isState()) {
            clearWindowsRecent();
        }
        
        // Глубокая очистка следов
        if (INSTANCE.deepClean.isState()) {
            UnhookHelper.performDeepClean();
        }
        
        // ОТКЛЮЧЕНО: Переименование папки и перемещение JAR вызывает зависание
        // UnhookHelper.renameConfigFolder(true);
        // UnhookHelper.moveModJar(true, mc.runDirectory);
        
        // Сохраняем состояния всех модулей и выключаем их
        for (Module module : ModuleClass.INSTANCE.getObject()) {
            if (module == INSTANCE) continue; // Не трогаем сам Unhook
            
            savedStates.put(module, module.isEnable());
            
            if (module.isEnable()) {
                module.setEnabled(false);
            }
        }
        
        // Проверяем, был ли открыт ClickGui
        if (mc != null && mc.currentScreen != null) {
            String screenName = mc.currentScreen.getClass().getSimpleName();
            clickGuiWasEnabled = screenName.contains("ClickGui");
            if (clickGuiWasEnabled) {
                mc.setScreen(null);
            }
        }
        
        // Очищаем чат
        clearChat();
    }
    
    /**
     * Восстановление всех функций клиента
     */
    private static void restoreClient() {
        if (!isUnhooked) return;
        
        isUnhooked = false;
        
        // ОТКЛЮЧЕНО: Переименование папки и перемещение JAR вызывает зависание
        // UnhookHelper.renameConfigFolder(false);
        // UnhookHelper.moveModJar(false, mc.runDirectory);
        
        // Восстанавливаем состояния всех модулей
        for (Map.Entry<Module, Boolean> entry : savedStates.entrySet()) {
            Module module = entry.getKey();
            boolean wasEnabled = entry.getValue();
            
            if (wasEnabled && !module.isEnable()) {
                module.setEnabled(true);
            }
        }
        
        savedStates.clear();
    }
    
    /**
     * Проверка, активен ли unhook
     */
    public static boolean isUnhooked() {
        return isUnhooked;
    }
    
    /**
     * Очистка чата
     */
    private static void clearChat() {
        try {
            if (mc != null && mc.inGameHud != null && mc.inGameHud.getChatHud() != null) {
                mc.inGameHud.getChatHud().clear(false);
            }
        } catch (Exception e) {
            // Игнорируем ошибки
        }
    }
    
    private static void clearClientLogs() {
        try {
            File logsDir = new File(mc.runDirectory, "logs");
            if (logsDir.exists() && logsDir.isDirectory()) {
                UnhookHelper.deleteDirectory(logsDir.toPath());
                logsDir.mkdirs();
            }
            
            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            if (tempDir.exists()) {
                File[] tempFiles = tempDir.listFiles((dir, name) -> 
                    name.toLowerCase().contains("minecraft") || 
                    name.toLowerCase().contains("fabric") ||
                    name.toLowerCase().contains("java")
                );
                if (tempFiles != null) {
                    for (File file : tempFiles) {
                        try {
                            if (file.isFile()) file.delete();
                        } catch (Exception ignored) {}
                    }
                }
            }
            
            System.gc();
        } catch (Exception e) {
            // Игнорируем ошибки
        }
    }
    
    /**
     * Очистка GPS waypoints
     */
    private static void clearGPSWaypoints() {
        try {
            // Выполняем команду .gps remove если GPS модуль существует
            if (Volon.INSTANCE != null && Volon.INSTANCE.commandStorage != null) {
                // Пытаемся выполнить команду через command storage
                String prefix = Volon.INSTANCE.commandStorage.getPrefix();
                mc.player.networkHandler.sendChatMessage(prefix + "gps remove");
            }
            
            // Также пытаемся удалить файлы waypoints если они есть
            File waypointsFile = new File(mc.runDirectory, "waypoints.json");
            if (waypointsFile.exists()) {
                waypointsFile.delete();
            }
            
            File gpsFile = new File(mc.runDirectory, "gps.dat");
            if (gpsFile.exists()) {
                gpsFile.delete();
            }
        } catch (Exception e) {
            // Игнорируем ошибки
        }
    }
    
    private static void deleteBaritoneFolder() {
        try {
            File baritoneDir = new File(mc.runDirectory, "baritone");
            if (baritoneDir.exists() && baritoneDir.isDirectory()) {
                UnhookHelper.deleteDirectory(baritoneDir.toPath());
            }
        } catch (Exception e) {
            // Игнорируем ошибки
        }
    }
    
    /**
     * Очистка Recent файлов Windows
     */
    private static void clearWindowsRecent() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Очистка Recent папки Windows
                String userHome = System.getProperty("user.home");
                Path recentPath = Paths.get(userHome, "AppData", "Roaming", "Microsoft", "Windows", "Recent");
                
                if (Files.exists(recentPath)) {
                    Files.walk(recentPath)
                        .filter(Files::isRegularFile)
                        .forEach(file -> {
                            try {
                                Files.delete(file);
                            } catch (IOException ignored) {}
                        });
                }
                
                // Очистка Prefetch (требует прав администратора, может не сработать)
                try {
                    Path prefetchPath = Paths.get("C:\\Windows\\Prefetch");
                    if (Files.exists(prefetchPath)) {
                        Files.walk(prefetchPath)
                            .filter(Files::isRegularFile)
                            .filter(p -> p.toString().toLowerCase().contains("java") || 
                                       p.toString().toLowerCase().contains("minecraft"))
                            .forEach(file -> {
                                try {
                                    Files.delete(file);
                                } catch (IOException ignored) {}
                            });
                    }
                } catch (Exception ignored) {
                    // Игнорируем если нет прав
                }
            }
        } catch (Exception e) {
            // Игнорируем ошибки
        }
    }
    
    /**
     * Рекурсивное удаление директории
     */
    private static void deleteDirectory(File directory) {
        UnhookHelper.deleteDirectory(directory.toPath());
    }
    
    /**
     * Проверка нажатия клавиши (с поддержкой mouse buttons)
     */
    private static boolean isKeyPressed(int key) {
        if (mc == null || mc.getWindow() == null) {
            return false;
        }
        
        try {
            // Если это mouse button
            if (key >= 1000) {
                int mouseButton = key - 1000;
                return GLFW.glfwGetMouseButton(
                    mc.getWindow().getHandle(), 
                    mouseButton
                ) == GLFW.GLFW_PRESS;
            }
            
            // Обычная клавиша
            return GLFW.glfwGetKey(
                mc.getWindow().getHandle(), 
                key
            ) == GLFW.GLFW_PRESS;
        } catch (Exception e) {
            return false;
        }
    }
}
