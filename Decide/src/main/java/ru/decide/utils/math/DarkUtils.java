/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Memory
 *  com.sun.jna.Native
 *  com.sun.jna.Platform
 *  com.sun.jna.Pointer
 *  com.sun.jna.platform.win32.WinDef$HWND
 *  com.sun.jna.win32.StdCallLibrary
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.glfw.GLFWNativeWin32
 */
package ru.decide.utils.math;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.StdCallLibrary;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFWNativeWin32;

@Environment(value=EnvType.CLIENT)
public class DarkUtils {
    public static void enableImmersiveDarkMode(long i_hwnd) {
        if (!Platform.isWindows()) {
            return;
        }
        long hwnd = GLFWNativeWin32.glfwGetWin32Window((long)i_hwnd);
        WinDef.HWND hwndJna = new WinDef.HWND(new Pointer(hwnd));
        int DWMWA_USE_IMMERSIVE_DARK_MODE = 20;
        Memory darkModeEnabled = new Memory(4L);
        darkModeEnabled.setInt(0L, 1);
        DwmApi.INSTANCE.DwmSetWindowAttribute(hwndJna, DWMWA_USE_IMMERSIVE_DARK_MODE, (Pointer)darkModeEnabled, 4);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface DwmApi
    extends StdCallLibrary {
        public static final DwmApi INSTANCE = (DwmApi)Native.loadLibrary((String)"dwmapi", DwmApi.class);

        public int DwmSetWindowAttribute(WinDef.HWND var1, int var2, Pointer var3, int var4);
    }
}

