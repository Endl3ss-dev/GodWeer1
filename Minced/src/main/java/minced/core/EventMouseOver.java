/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.network.codec.PacketEncoder
 *  net.minecraft.util.math.Vec2f
 *  net.minecraft.util.math.Vec3d
 *  sLM.4ZXYEo
 */
package minced.core;

import net.minecraft.network.codec.PacketEncoder;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import sLM._4ZXYEo;

public class EventMouseOver
implements PacketEncoder {
    @Override
    public void encode(Object p0, Object p1) { }

    static String cfr_renamed_291;

    Vec3d field12539;
    Vec2f field12540;

    public int hashCode() {
        Vec3d v3 = this.method3561();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        Vec2f v4 = this.method3563();
        v2 = v2 * 59 + (v4 == null ? 43 : v4.hashCode());
        return v2;
    }

    public Vec3d method3561() {
        return this.field12539;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventMouseOver)) {
            return false;
        }
        EventMouseOver v2 = (EventMouseOver)param1;
        if (!v2.method3562(this)) {
            return false;
        }
        Vec3d v3 = this.method3561();
        Vec3d v4 = v2.method3561();
        if (v3 == null ? v4 != null : !v3.equals(v4)) {
            return false;
        }
        Vec2f v5 = this.method3563();
        Vec2f v6 = v2.method3563();
        return !(v5 == null ? v6 != null : !v5.equals(v6));
    }

    public EventMouseOver(Vec3d param1, Vec2f param2) {
        this.field12539 = param1;
        this.field12540 = param2;
    }

    protected boolean method3562(Object param1) {
        return param1 instanceof EventMouseOver;
    }

    public Vec2f method3563() {
        return this.field12540;
    }

    public void method3564(Vec3d param1) {
        this.field12539 = param1;
    }

    public void method3565(Vec2f param1) {
        this.field12540 = param1;
    }

    public String toString() {
        return _4ZXYEo.Цж("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method3561()), (String)String.valueOf(this.method3563()));
    }

    static {
        cfr_renamed_291 = "EventMouseOver(position=\u0001, rotation=\u0001)";
    }
}

