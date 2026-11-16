package tech.lightclient.client.utils.window;

import net.dimaskama.mcef.api.MCEFBrowser;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * Interactive browser window anchored in the world.
 */
public class WorldWindow {

    public Vec3d center;
    public Vec3d right;
    public Vec3d up;
    public double worldW;
    public double worldH;

    public String title = "Window";

    public static final double TAB_FRAC = 0.055;
    public static final double TOOLBAR_FRAC = 0.065;
    public static final double BAR_FRAC = 0.09;

    public float curX = -1f;
    public float curY = -1f;

    public static final class Tab {
        public MCEFBrowser browser;
        public String url;
        public String title = "Tab";
        public int w;
        public int h;

        public Tab(String url) {
            this.url = url;
        }
    }

    public final List<Tab> tabs = new ArrayList<>();
    public int active = 0;

    public MCEFBrowser browser;
    public int browserW;
    public int browserH;

    public String address = "";
    public boolean addressFocused;

    public double grabDistance;

    public WorldWindow(Vec3d center, Vec3d right, Vec3d up, double worldW, double worldH) {
        this.center = center;
        this.right = right;
        this.up = up;
        this.worldW = worldW;
        this.worldH = worldH;
    }

    public Tab tab() {
        return active >= 0 && active < tabs.size() ? tabs.get(active) : null;
    }

    public void syncActive() {
        Tab tab = tab();
        browser = tab == null ? null : tab.browser;
        browserW = tab == null ? 0 : tab.w;
        browserH = tab == null ? 0 : tab.h;
        if (!addressFocused) {
            address = tab == null ? "" : tab.url == null ? "" : tab.url;
        }
    }

    public Vec3d normal() {
        return right.crossProduct(up).normalize();
    }

    public Vec3d corner(double sx, double sy) {
        return center.add(right.multiply(sx * worldW * 0.5)).add(up.multiply(sy * worldH * 0.5));
    }
}
