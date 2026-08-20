/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.Vec3d
 */
package minced.core;

import java.util.function.Function;
import minced.core.Easings;
import minced.core.AnimatedFloat;
import net.minecraft.util.math.Vec3d;

public class JumpCirclePoint {
    public boolean field7934 = false;
    public boolean field7935 = false;
    public boolean field7936 = false;
    static float field7937 = 0.1f;
    static float field7938 = 3.0f;
    public AnimatedFloat O = new AnimatedFloat(field7937, field7938, Easings.field11969);
    static float field7939 = 3.0f;
    public AnimatedFloat field7940 = new AnimatedFloat(0.0f, field7939, Easings.field11969);
    public long field7941 = System.currentTimeMillis();
    public Vec3d field7942;
    static float field7943 = 1.1f;

    public JumpCirclePoint(Vec3d param1) {
        this.field7942 = param1;
        this.O.setTarget(field7943);
        this.field7940.setTarget(1.0f);
    }
}

