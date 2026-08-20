/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;


public final class BotTimings
{
    public long Ь;
    public long cfr_renamed_126;
    public int нЧ;
    public String cfr_renamed_127;

    public long l() {
        return this.Ь;
    }

    public long method194() {
        return this.cfr_renamed_126;
    }

    public int method195() {
        return this.нЧ;
    }

    public void method196(long param1) {
        this.Ь = param1;
    }

    public boolean method197(long param1) {
        return param1 - this.Ь >= this.cfr_renamed_126;
    }

    public String method198() {
        return this.cfr_renamed_127;
    }

    public BotTimings(int param1, String param2, long param3) {
        this.нЧ = param1;
        this.cfr_renamed_127 = param2;
        this.cfr_renamed_126 = param3;
    }
}

