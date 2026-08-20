/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.text.Text
 */
package minced.core;

import java.util.Comparator;
import java.util.Objects;
import net.minecraft.text.Text;

public class PlayerEntryComparator
implements Comparator {
    String field11680;
    Text field11681;
    boolean field11682;

    public int hashCode() {
        return Objects.hash(this.field11680);
    }

    public PlayerEntryComparator(String param1, Text param2, boolean param3) {
        this.field11680 = param1;
        this.field11681 = param2;
        this.field11682 = param3;
    }

    public String method3284() {
        return this.field11680;
    }

    @Override
    public boolean equals(Object param1) {
        if (this == param1) {
            return true;
        }
        if (param1 instanceof PlayerEntryComparator) {
            PlayerEntryComparator v2 = (PlayerEntryComparator)param1;
            return Objects.equals(this.field11680, v2.field11680);
        }
        return false;
    }

    public boolean method3285() {
        return this.field11682;
    }

    public Text method3286() {
        return this.field11681;
    }

    @Override
    public int compare(Object param1, Object param2) {
        return 0;
    }
}

