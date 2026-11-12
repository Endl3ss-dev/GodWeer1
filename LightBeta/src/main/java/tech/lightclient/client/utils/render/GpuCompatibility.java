package tech.lightclient.client.utils.render;

import org.lwjgl.opengl.GL11;

public final class GpuCompatibility {
    private static Boolean amd;

    private GpuCompatibility() {
    }

    public static boolean isAmd() {
        if (amd != null) {
            return amd;
        }
        try {
            String vendor = GL11.glGetString(GL11.GL_VENDOR);
            String renderer = GL11.glGetString(GL11.GL_RENDERER);
            if (vendor == null && renderer == null) {
                return false;
            }
            String gpu = ((vendor == null ? "" : vendor) + ' '
                    + (renderer == null ? "" : renderer)).toLowerCase();
            amd = gpu.contains("amd") || gpu.contains("ati") || gpu.contains("radeon");
            return amd;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
