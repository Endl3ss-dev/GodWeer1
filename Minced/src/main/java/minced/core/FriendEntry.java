/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  sLM.4ZXYEo
 */
package minced.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;
import sLM._4ZXYEo;

public class FriendEntry
{
    public java.time.LocalDateTime н;
    public java.lang.String ы;
    public String toString() {
        return _4ZXYEo.ио("makeConcatWithConstants", new Object[]{}, (String)this.ы, (String)this.н.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public FriendEntry(String param1, LocalDateTime param2) {
        this.ы = param1;
        this.н = param2;
    }

    public LocalDateTime cfr_underscore() {
        return this.н;
    }

    public String method2633() {
        return this.ы;
    }
}



