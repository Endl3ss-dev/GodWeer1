/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.client.network.OtherClientPlayerEntity
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.Vec3d
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  sg.ec.f14
 */
package minced.mixin.entity;
import com.mojang.authlib.GameProfile;
import minced.core.EntityInterpolation;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import ru.dreamix.fabricloader.VMBridge;
import sg.ec.f14;

@Mixin(value={OtherClientPlayerEntity.class})
public class MixinOtherClientPlayerEntity
extends AbstractClientPlayerEntity
implements f14 {
    @Unique
    private double backUpX;
    @Unique
    private double backUpY;
    @Unique
    private double backUpZ;
    private static double field12722 = 1.0;
    private static double field12723 = 1.0;

    public MixinOtherClientPlayerEntity(ClientWorld param1, GameProfile param2) {
        super(param1, param2);
    }

    public void resolve() {
        this.backUpX = this.getX();
        this.backUpY = this.getY();
        this.backUpZ = this.getZ();
        Vec3d v1 = this.getPos();
        Vec3d v3 = new Vec3d(((EntityInterpolation)((Object)this)).getPrevServerX(), ((EntityInterpolation)((Object)this)).getPrevServerY(), ((EntityInterpolation)((Object)this)).getPrevServerZ());
        Vec3d v4 = new Vec3d(this.serverX, this.serverY, this.serverZ);
        Vec3d v2 = v1.distanceTo(v3) > v1.distanceTo(v4) ? v4 : v3;
        this.setPos(v2.x, v2.y, v2.z);
    }

    public void releaseResolver() {
        if (this.backUpY != field12722) {
            this.setPos(this.backUpX, this.backUpY, this.backUpZ);
            this.backUpY = field12723;
        }
    }

    static {
        VMBridge.identifyClass(MixinOtherClientPlayerEntity.class, "xXsE1SeB");
    }
}



