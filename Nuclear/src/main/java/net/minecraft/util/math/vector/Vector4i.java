package net.minecraft.util.math.vector;

public class Vector4i {
    public int x, y, z, w;

    public Vector4i(int x, int y, int z, int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public int getW() { return w; }
}
