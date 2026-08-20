package org.privates.client.managers.module.impl.combat;

import lombok.Generated;
import net.minecraft.util.math.AxisAlignedBB;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;

@ModuleInfo(name = "Resolver", category = Category.COMBAT, description = "Ресольвер")

public class Resolver
        extends Module {
    public final SliderSetting backTick = new SliderSetting(this, "\u0411\u0435\u043a\u0442\u0438\u043a\u0438", 2.0f, 1.0f, 50.0f, 1.0f);
    public AxisAlignedBB resolvedBox;

    public static Resolver getInstance() {
        return Instance.get(Resolver.class);
    }

    @Generated
    public SliderSetting backTick() {
        return this.backTick;
    }

    @Generated
    public AxisAlignedBB resolvedBox() {
        return this.resolvedBox;
    }

    public static class Position {
        private final double x;
        private final double y;
        private final double z;
        public int ticks;
        final Resolver kill = Resolver.getInstance();

        public Position(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public boolean shouldRemove() {
            return (float) this.ticks++ > this.kill.backTick.getValue().floatValue();
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getZ() {
            return this.z;
        }
    }
}
