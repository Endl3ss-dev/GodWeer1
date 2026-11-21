package tech.lightclient.modules.impl.render;

import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.client.EventKey;
import tech.lightclient.client.events.impl.client.EventScroll;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.window.Browsers;
import tech.lightclient.client.utils.window.WorldWindow;
import tech.lightclient.client.utils.window.WorldWindowChromeLayout;
import tech.lightclient.client.utils.window.WorldWindowRenderer;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BindSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static tech.lightclient.client.utils.client.IMinecraft.mc;

@FunctionAdd(
        name = "Windows",
        alias = "Portals",
        category = Category.Render,
        description = "Interactive 3D browser windows in the world"
)
public class Portals extends Function {

    private static Portals instance;

    private static final String HOME = "https://duckduckgo.com";
    private static final String JS_BACKSPACE =
            "(function(){var e=document.activeElement;if(!e)return;"
                    + "if(e.value!==undefined&&e.selectionStart!=null){var s=e.selectionStart,t=e.selectionEnd;"
                    + "if(s!=t){e.value=e.value.slice(0,s)+e.value.slice(t);e.selectionStart=e.selectionEnd=s;}"
                    + "else if(s>0){e.value=e.value.slice(0,s-1)+e.value.slice(t);e.selectionStart=e.selectionEnd=s-1;}"
                    + "e.dispatchEvent(new Event('input',{bubbles:true}));}"
                    + "else if(e.isContentEditable){document.execCommand('delete',false,null);}})();";
    private static final String JS_DELETE =
            "(function(){var e=document.activeElement;if(!e)return;"
                    + "if(e.value!==undefined&&e.selectionStart!=null){var s=e.selectionStart,t=e.selectionEnd;"
                    + "if(s!=t){e.value=e.value.slice(0,s)+e.value.slice(t);e.selectionStart=e.selectionEnd=s;}"
                    + "else if(s<e.value.length){e.value=e.value.slice(0,s)+e.value.slice(s+1);e.selectionStart=e.selectionEnd=s;}"
                    + "e.dispatchEvent(new Event('input',{bubbles:true}));}"
                    + "else if(e.isContentEditable){document.execCommand('forwardDelete',false,null);}})();";

    private static final List<WorldWindow> WINDOWS = new ArrayList<>();
    private static volatile WorldWindow keyboardTarget;
    private static volatile WorldWindow addressWindow;
    private static WorldWindow scrollTarget;
    private static int lastPx;
    private static int lastPy;

    public final BindSetting spawnKey = new BindSetting("Open window", -1);
    public final BindSetting backKey = new BindSetting("Browser back", -1);
    public final SliderSetting distance = new SliderSetting("Spawn distance", 4f, 1.5f, 14f, 0.1f);
    public final SliderSetting width = new SliderSetting("Width", 5f, 1f, 18f, 0.1f);
    public final SliderSetting height = new SliderSetting("Height", 3.1f, 0.6f, 12f, 0.1f);

    private enum Mode {
        MOVE,
        RESIZE,
        CLOSE
    }

    private WorldWindow dragging;
    private Mode mode = Mode.MOVE;
    private boolean prevAnyBtn;
    private boolean prevBrowserBtn;
    private long lastNs = System.nanoTime();
    private boolean prevBack;
    private boolean prevFwd;

    public Portals() {
        instance = this;
        addSettings(spawnKey, backKey, distance, width, height);
    }

    public static boolean isWindowsActive() {
        return instance != null && instance.isState();
    }

    public static List<WorldWindow> windows() {
        return WINDOWS;
    }

    public static WorldWindow keyboardTarget() {
        return keyboardTarget;
    }

    public static WorldWindow addressWindow() {
        return addressWindow;
    }

    public static void addressChar(int codepoint) {
        WorldWindow window = addressWindow;
        if (window == null || !window.addressFocused) {
            return;
        }
        if (codepoint >= 32 && codepoint != 127) {
            window.address += new String(Character.toChars(codepoint));
        }
    }

    @Override
    public void onDisable() {
        // Keep the MCEF/Chromium profile alive while the module is toggled.
        // Closing every browser here destroys the current page context and
        // makes sites such as Google ask for a new login after re-enabling.
        suspendWindows();
        super.onDisable();
    }

    private static void suspendWindows() {
        keyboardTarget = null;
        addressWindow = null;
        scrollTarget = null;
        for (WorldWindow window : WINDOWS) {
            window.addressFocused = false;
            window.curX = -1f;
            window.curY = -1f;
        }
    }

    @EventHandler
    public void onKey(EventKey event) {
        WorldWindow address = addressWindow;
        if (address != null && address.addressFocused && mc.currentScreen == null) {
            int key = event.getKey();
            if (event.getAction() != 0) {
                if (key == GLFW.GLFW_KEY_ESCAPE) {
                    unfocusAddress(address);
                } else if (key == GLFW.GLFW_KEY_ENTER) {
                    loadUrl(address, address.address);
                    unfocusAddress(address);
                } else if (key == GLFW.GLFW_KEY_BACKSPACE && !address.address.isEmpty()) {
                    address.address = address.address.substring(0, address.address.length() - 1);
                }
            }
            event.setCancelled(true);
            return;
        }

        WorldWindow typingTarget = keyboardTarget;
        if (typingTarget != null && event.getKey() == GLFW.GLFW_KEY_ESCAPE) {
            if (event.getAction() == 1) {
                if (typingTarget.browser != null) {
                    typingTarget.browser.setFocus(false);
                }
                keyboardTarget = null;
            }
            event.setCancelled(true);
            return;
        }

        if (typingTarget != null && typingTarget.browser != null && backKey.get() != -1
                && event.getAction() == 1 && event.isKeyDown(backKey.get())) {
            navigate(typingTarget, false);
            event.setCancelled(true);
            return;
        }

        if (typingTarget != null && typingTarget.browser != null && mc.currentScreen == null) {
            int key = event.getKey();
            if (key != GLFW.GLFW_KEY_ESCAPE && key < 1000) {
                try {
                    int scancode = GLFW.glfwGetKeyScancode(key);
                    KeyInput keyInput = new KeyInput(key, scancode, 0);
                    if (event.getAction() == 0) {
                        typingTarget.browser.onKeyReleased(keyInput);
                        return;
                    }
                    if (event.getAction() == 1 || event.getAction() == 2) {
                        typingTarget.browser.onKeyPressed(keyInput);
                        if (key == GLFW.GLFW_KEY_ENTER) {
                            typingTarget.browser.onCharTyped(new CharInput(13, 0));
                        } else if (key == GLFW.GLFW_KEY_TAB) {
                            typingTarget.browser.onCharTyped(new CharInput(9, 0));
                        } else if (key == GLFW.GLFW_KEY_BACKSPACE) {
                            jsExec(typingTarget, JS_BACKSPACE);
                        } else if (key == GLFW.GLFW_KEY_DELETE) {
                            jsExec(typingTarget, JS_DELETE);
                        }
                    }
                } catch (Throwable ignored) {
                }
                event.setCancelled(true);
                return;
            }
        }

        if (event.getAction() != 1 || spawnKey.get() == -1) {
            return;
        }
        if (!event.isKeyDown(spawnKey.get())) {
            return;
        }
        if (mc.player == null || mc.currentScreen != null) {
            return;
        }
        spawnInFront();
    }

    @EventHandler
    public void onScroll(EventScroll event) {
        WorldWindow target = scrollTarget;
        if (target == null || target.browser == null || mc.currentScreen != null) {
            return;
        }
        try {
            target.browser.onMouseScrolled(lastPx, lastPy, event.getVertical() * 30.0);
        } catch (Throwable ignored) {
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onWorldRender(EventRender.World event) {
        if (mc.world == null || mc.player == null || LightClient.isHide()) {
            return;
        }
        Browsers.ensureInit();
        if (WINDOWS.isEmpty()) {
            return;
        }

        Camera camera = mc.gameRenderer.getCamera();
        Vec3d camPos = camera.getCameraPos();
        Vec3d look = Vec3d.fromPolar(camera.getPitch(), camera.getYaw());

        long handle = mc.getWindow().getHandle();
        boolean rmb = mc.currentScreen == null
                && GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        boolean lmb = mc.currentScreen == null
                && GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        WorldWindow hovered = pick(camPos, look);
        double[] hit = hovered == null ? null : hitLocal(hovered, camPos, look);

        long nowNs = System.nanoTime();
        double dt = Math.min(0.05, (nowNs - lastNs) / 1_000_000_000.0);
        lastNs = nowNs;

        double tabFy = WorldWindow.TAB_FRAC;
        double toolFy = WorldWindow.TAB_FRAC + WorldWindow.TOOLBAR_FRAC;
        double barFy = 1.0 - WorldWindow.BAR_FRAC;
        double fx = hit == null ? 0 : (hit[0] + 1) / 2.0;
        double fy = hit == null ? 0 : (1 - hit[1]) / 2.0;

        for (WorldWindow window : WINDOWS) {
            window.curX = -1f;
            window.curY = -1f;
        }

        boolean anyBtn = rmb || lmb;
        boolean edge = anyBtn && !prevAnyBtn;
        boolean inContent = hit != null && fy > toolFy && fy < barFy;

        if (edge && hovered != null && hit != null && dragging == null) {
            if (fy < tabFy) {
                clickTabs(hovered, fx);
            } else if (fy < toolFy) {
                clickToolbar(hovered, fx);
            } else if (fy > barFy) {
                dragging = hovered;
                keyboardTarget = null;
                unfocusAddress(hovered);
                if (fx > 0.825) {
                    mode = Mode.CLOSE;
                } else if (fx > 0.375) {
                    mode = Mode.RESIZE;
                } else {
                    mode = Mode.MOVE;
                    dragging.grabDistance = camPos.distanceTo(hovered.center);
                }
            } else {
                unfocusAddress(hovered);
            }
        }
        if (!anyBtn) {
            if (dragging != null && mode == Mode.CLOSE && hovered == dragging
                    && hit != null && fy > barFy && fx > 0.825) {
                closeWindow(dragging);
            }
            dragging = null;
        }
        if (dragging != null && mode == Mode.MOVE) {
            Vec3d target = camPos.add(look.multiply(dragging.grabDistance));
            dragging.center = dragging.center.lerp(target, 1.0 - Math.exp(-14.0 * dt));
            faceCamera(dragging, camPos);
        } else if (dragging != null && mode == Mode.RESIZE) {
            Vec3d normal = dragging.normal();
            double denom = look.dotProduct(normal);
            if (Math.abs(denom) > 1.0e-6) {
                double t = dragging.center.subtract(camPos).dotProduct(normal) / denom;
                if (t > 0.05) {
                    Vec3d local = camPos.add(look.multiply(t)).subtract(dragging.center);
                    double tw = Math.max(1.0, Math.abs(local.dotProduct(dragging.right)) * 2.0);
                    double th = Math.max(0.8, Math.abs(local.dotProduct(dragging.up)) * 2.0);
                    double k = 1.0 - Math.exp(-18.0 * dt);
                    dragging.worldW += (tw - dragging.worldW) * k;
                    dragging.worldH += (th - dragging.worldH) * k;
                }
            }
        }

        if (hovered != null && hovered.browser != null && dragging == null && inContent) {
            double v = (fy - toolFy) / (barFy - toolFy);
            int px = clampPix((int) (fx * hovered.browserW), hovered.browserW);
            int py = clampPix((int) (v * hovered.browserH), hovered.browserH);
            hovered.curX = px;
            hovered.curY = py;
            scrollTarget = hovered;
            lastPx = px;
            lastPy = py;
            try {
                hovered.browser.onMouseMoved(px, py);
                int btn = lmb ? GLFW.GLFW_MOUSE_BUTTON_LEFT : (rmb ? GLFW.GLFW_MOUSE_BUTTON_RIGHT : -1);
                var click = new net.minecraft.client.gui.Click(px, py,
                        new net.minecraft.client.input.MouseInput(Math.max(0, btn), 0));
                boolean anyDown = lmb || rmb;
                if (anyDown && !prevBrowserBtn) {
                    boolean textField = false;
                    try {
                        textField = hovered.browser.getCursorType()
                                == net.minecraft.client.gui.cursor.StandardCursors.IBEAM;
                    } catch (Throwable ignored) {
                    }
                    hovered.browser.setFocus(true);
                    hovered.browser.onMouseClicked(click, false);
                    unfocusAddress(hovered);
                    keyboardTarget = textField ? hovered : null;
                } else if (!anyDown && prevBrowserBtn) {
                    hovered.browser.onMouseReleased(click);
                }
                prevBrowserBtn = anyDown;
            } catch (Throwable ignored) {
            }
        } else {
            prevBrowserBtn = false;
            scrollTarget = null;
        }

        boolean back = mc.currentScreen == null
                && GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_4) == GLFW.GLFW_PRESS;
        boolean fwd = mc.currentScreen == null
                && GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_5) == GLFW.GLFW_PRESS;
        if (hovered != null && hovered.browser != null) {
            if (back && !prevBack) {
                navigate(hovered, false);
            }
            if (fwd && !prevFwd) {
                navigate(hovered, true);
            }
        }
        prevBack = back;
        prevFwd = fwd;

        prevAnyBtn = anyBtn;
    }

    private static void clearAllWindows() {
        for (WorldWindow window : List.copyOf(WINDOWS)) {
            WorldWindowRenderer.dispose(window);
        }
        WINDOWS.clear();
        keyboardTarget = null;
        addressWindow = null;
        scrollTarget = null;
    }

    private static void jsExec(WorldWindow window, String code) {
        if (window == null || window.browser == null) {
            return;
        }
        try {
            Object cef = window.browser.getClass().getMethod("getCefBrowser").invoke(window.browser);
            if (cef == null) {
                return;
            }
            cef.getClass().getMethod("executeJavaScript", String.class, String.class, int.class)
                    .invoke(cef, code, "", 0);
        } catch (Throwable ignored) {
        }
    }

    private static void navigate(WorldWindow window, boolean forward) {
        if (window == null || window.browser == null) {
            return;
        }
        try {
            Object cef = window.browser.getClass().getMethod("getCefBrowser").invoke(window.browser);
            if (cef == null) {
                return;
            }
            String can = forward ? "canGoForward" : "canGoBack";
            String go = forward ? "goForward" : "goBack";
            Object ok = cef.getClass().getMethod(can).invoke(cef);
            if (Boolean.TRUE.equals(ok)) {
                cef.getClass().getMethod(go).invoke(cef);
            }
        } catch (Throwable ignored) {
        }
    }

    private static void reload(WorldWindow window) {
        try {
            Object cef = window.browser.getClass().getMethod("getCefBrowser").invoke(window.browser);
            if (cef != null) {
                cef.getClass().getMethod("reload").invoke(cef);
            }
        } catch (Throwable ignored) {
        }
    }

    private static void loadUrl(WorldWindow window, String input) {
        if (window == null || input == null) {
            return;
        }
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        String url;
        if (trimmed.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*")) {
            url = trimmed;
        } else if (trimmed.contains(".") && !trimmed.contains(" ")) {
            url = "https://" + trimmed;
        } else {
            url = "https://duckduckgo.com/?q=" + URLEncoder.encode(trimmed, StandardCharsets.UTF_8);
        }
        WorldWindow.Tab tab = window.tab();
        if (tab != null) {
            tab.url = url;
        }
        try {
            Object cef = window.browser.getClass().getMethod("getCefBrowser").invoke(window.browser);
            if (cef != null) {
                cef.getClass().getMethod("loadURL", String.class).invoke(cef, url);
            }
        } catch (Throwable ignored) {
        }
    }

    private static void unfocusAddress(WorldWindow window) {
        if (window != null) {
            window.addressFocused = false;
        }
        if (addressWindow == window) {
            addressWindow = null;
        }
    }

    private static int clampPix(int value, int max) {
        return value < 0 ? 0 : Math.min(value, max - 1);
    }

    private void spawnInFront() {
        Camera camera = mc.gameRenderer.getCamera();
        Vec3d camPos = camera.getCameraPos();
        Vec3d forward = Vec3d.fromPolar(camera.getPitch(), camera.getYaw());
        Vec3d center = camPos.add(forward.multiply(distance.get()));
        WorldWindow window = new WorldWindow(center, new Vec3d(1, 0, 0), new Vec3d(0, 1, 0),
                width.get(), height.get());
        window.title = "Browser";
        window.tabs.add(new WorldWindow.Tab(HOME));
        window.active = 0;
        faceCamera(window, camPos);
        WINDOWS.add(window);
    }

    private void clickTabs(WorldWindow window, double fx) {
        keyboardTarget = null;
        unfocusAddress(window);
        if (fx > WorldWindowChromeLayout.PLUS_X) {
            window.tabs.add(new WorldWindow.Tab(HOME));
            window.active = window.tabs.size() - 1;
            window.syncActive();
            return;
        }
        int count = Math.max(1, window.tabs.size());
        double tabW = WorldWindowChromeLayout.PLUS_X / count;
        int index = (int) (fx / tabW);
        if (index < 0 || index >= window.tabs.size()) {
            return;
        }
        double rx = fx - index * tabW;
        if (rx > tabW * 0.80) {
            closeTab(window, index);
        } else {
            window.active = index;
            window.syncActive();
        }
    }

    private void clickToolbar(WorldWindow window, double fx) {
        if (fx >= WorldWindowChromeLayout.BACK_X && fx < WorldWindowChromeLayout.FWD_X) {
            navigate(window, false);
            unfocusAddress(window);
        } else if (fx >= WorldWindowChromeLayout.FWD_X && fx < WorldWindowChromeLayout.RELOAD_X) {
            navigate(window, true);
            unfocusAddress(window);
        } else if (fx >= WorldWindowChromeLayout.RELOAD_X && fx < WorldWindowChromeLayout.ADDR_X) {
            reload(window);
            unfocusAddress(window);
        } else if (fx >= WorldWindowChromeLayout.ADDR_X && fx < WorldWindowChromeLayout.ADDR_R) {
            keyboardTarget = null;
            window.addressFocused = true;
            WorldWindow.Tab tab = window.tab();
            window.address = tab != null && tab.url != null ? tab.url : "";
            addressWindow = window;
        }
    }

    private void closeTab(WorldWindow window, int index) {
        if (index < 0 || index >= window.tabs.size()) {
            return;
        }
        WorldWindow.Tab tab = window.tabs.get(index);
        if (tab.browser != null) {
            try {
                tab.browser.close();
            } catch (Throwable ignored) {
            }
        }
        window.tabs.remove(index);
        if (window.tabs.isEmpty()) {
            closeWindow(window);
            return;
        }
        if (window.active >= window.tabs.size()) {
            window.active = window.tabs.size() - 1;
        }
        window.syncActive();
    }

    private void closeWindow(WorldWindow window) {
        if (keyboardTarget == window) {
            keyboardTarget = null;
        }
        if (addressWindow == window) {
            addressWindow = null;
        }
        WINDOWS.remove(window);
        WorldWindowRenderer.dispose(window);
    }

    private static void faceCamera(WorldWindow window, Vec3d camPos) {
        Vec3d normal = camPos.subtract(window.center);
        if (normal.lengthSquared() < 1.0e-6) {
            return;
        }
        normal = normal.normalize();
        Vec3d right = new Vec3d(-normal.z, 0, normal.x);
        if (right.lengthSquared() < 1.0e-6) {
            right = new Vec3d(1, 0, 0);
        }
        right = right.normalize();
        Vec3d up = normal.crossProduct(right).normalize();
        window.right = right;
        window.up = up;
    }

    private static double[] hitLocal(WorldWindow window, Vec3d camPos, Vec3d look) {
        Vec3d normal = window.normal();
        double denom = look.dotProduct(normal);
        if (Math.abs(denom) < 1.0e-6) {
            return null;
        }
        double t = window.center.subtract(camPos).dotProduct(normal) / denom;
        if (t <= 0.05) {
            return null;
        }
        Vec3d local = camPos.add(look.multiply(t)).subtract(window.center);
        return new double[]{
                -local.dotProduct(window.right) / (window.worldW * 0.5),
                -local.dotProduct(window.up) / (window.worldH * 0.5),
                t
        };
    }

    private WorldWindow pick(Vec3d camPos, Vec3d look) {
        WorldWindow best = null;
        double bestT = Double.MAX_VALUE;
        for (WorldWindow window : WINDOWS) {
            Vec3d normal = window.normal();
            double denom = look.dotProduct(normal);
            if (Math.abs(denom) < 1.0e-6) {
                continue;
            }
            double tHit = window.center.subtract(camPos).dotProduct(normal) / denom;
            if (tHit <= 0.05 || tHit >= bestT) {
                continue;
            }
            Vec3d point = camPos.add(look.multiply(tHit));
            Vec3d local = point.subtract(window.center);
            if (Math.abs(local.dotProduct(window.right)) <= window.worldW * 0.5
                    && Math.abs(local.dotProduct(window.up)) <= window.worldH * 0.5) {
                best = window;
                bestT = tHit;
            }
        }
        return best;
    }
}
