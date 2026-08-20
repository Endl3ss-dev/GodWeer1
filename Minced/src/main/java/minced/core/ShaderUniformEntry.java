/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.util.function.Supplier;
import minced.core.ShaderProgramLoader;

public class ShaderUniformEntry {
    String field8035;
    int field8036;
    Supplier field8037;

    public ShaderUniformEntry(ShaderProgramLoader param1, String param2, int param3, Supplier param4) {
        this.field8035 = param2;
        this.field8036 = param3;
        this.field8037 = param4;
    }
}

