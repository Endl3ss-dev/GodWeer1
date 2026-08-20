/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.entity.ItemEntityRenderer
 *  net.minecraft.client.render.entity.state.ItemEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.util.math.RotationAxis
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.render;
import minced.core.ClientUtil;
import minced.core.Minced;
import minced.module.visual.ItemPhysic;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dreamix.fabricloader.VMBridge;

@Mixin(value={ItemEntityRenderer.class})
public abstract class MixinItemEntityRenderer
implements ClientUtil {
    @Unique
    private boolean isOnGround = false;
    @Unique
    private float entityAge = 0.0f;
    @Unique
    private float uniqueOffset = 0.0f;
    private static float field9337 = 1.0f;
    private static float field9338 = 1.0f;

    @Inject(method={"updateRenderState"}, at={@At(value="HEAD")})
    private void onUpdateRenderState(ItemEntity param1, ItemEntityRenderState param2, float param3, CallbackInfo param4) {
        this.isOnGround = param1.isOnGround();
        this.entityAge = param2.age;
        this.uniqueOffset = param2.uniqueOffset;
    }

    @ModifyVariable(method={"render"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/render/entity/ItemEntityRenderer;renderStack(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/ItemStackEntityRenderState;Lnet/minecraft/util/math/random/Random;)V"), ordinal=0)
    private MatrixStack modifyMatrixStack(MatrixStack param1, ItemEntityRenderState param2, MatrixStack param3, VertexConsumerProvider param4, int param5) {
        ItemPhysic v6 = (ItemPhysic)Minced.getInstance().getModuleManager().method187(ItemPhysic.class);
        if (v6 != null && v6.isEnabled()) {
            float v7 = ItemEntity.getRotation((float)this.entityAge, (float)this.uniqueOffset);
            if (this.isOnGround) {
                param1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
            } else {
                param1.multiply(RotationAxis.POSITIVE_Y.rotation(v7));
            }
        }
        return param1;
    }

    static {
        VMBridge.identifyClass(MixinItemEntityRenderer.class, "Sdt64sFe");
    }
}



