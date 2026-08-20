/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package minced.core;

import java.util.function.Function;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class ScaleState
{
    double field7989;
    Matrix4f field7990;
    Matrix4f field7991;

    public ScaleState(double param1, Matrix4f param3, Matrix4f param4) {
        this.field7989 = param1;
        this.field7990 = new Matrix4f((Matrix4fc)param3);
        this.field7991 = new Matrix4f((Matrix4fc)param4);
    }
}

