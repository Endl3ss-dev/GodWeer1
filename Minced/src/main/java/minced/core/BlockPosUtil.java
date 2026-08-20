/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  sg.ec.fh
 */
package minced.core;

import java.util.ArrayList;
import minced.core.BotAction;
import minced.core.BotSession;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import sg.ec.fh;
import sg.ec.chh;

public final class BlockPosUtil
implements BotAction {
    static double АР;
    private java.util.ArrayList у;
    private int нЯ;

    public void method424(BlockPos param1) {
        if (param1 != null) {
            this.у.add(param1.toImmutable());
        }
    }

    @Override
    public void method192(BotSession param12) {
        fh.з((BotSession)param12, param1 -> {
            if (this.у.isEmpty()) {
                param1.controller().cfr_renamed_294();
                return;
            }
            BlockPos v2 = (BlockPos)this.у.get(this.нЯ);
            Vec3d v3 = fh.ц((BlockPos)v2);
            if (param1.player().getPos().distanceTo(v3) <= АР) {
                this.нЯ = (this.нЯ + 1) % this.у.size();
                return;
            }
            param1.controller().cfr_renamed_5(v3);
            param1.controller().cfr_renamed_240(v3);
            param1.controller().бЪ(v2.getY() - param1.player().getBlockY() > 1 && param1.player().isOnGround());
        });
    }

    public boolean method425() {
        return this.у.isEmpty();
    }

    @Override
    public String method193() {
        return chh.н.method193();
    }

    public void method427() {
        this.у.clear();
        this.нЯ = 0;
    }

    public BlockPosUtil() {
        this.у = new ArrayList();
    }

    static {
        АР = 0.0;
    }
}



