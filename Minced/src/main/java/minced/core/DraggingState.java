/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DraggingState
{
    Map field156;
    Map field157 = new LinkedHashMap();
    LinkedHashMap field158;
    List field159;

    public Map method99() {
        return this.field156;
    }

    public Map method100() {
        return this.field157;
    }

    public DraggingState() {
        this.field156 = new LinkedHashMap();
        this.field158 = new LinkedHashMap();
        this.field159 = new ArrayList();
    }

    public List method101() {
        return this.field159;
    }

    public LinkedHashMap method102() {
        return this.field158;
    }
}

