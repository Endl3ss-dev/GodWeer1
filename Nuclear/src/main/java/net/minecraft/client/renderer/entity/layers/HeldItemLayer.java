package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import nuclear.control.Manager;
import nuclear.module.impl.render.CustomModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;

public class HeldItemLayer<T extends LivingEntity, M extends EntityModel<T> & IHasArm> extends LayerRenderer<T, M>
{
    public HeldItemLayer(IEntityRenderer<T, M> p_i50934_1_)
    {
        super(p_i50934_1_);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        boolean flag = entitylivingbaseIn.getPrimaryHand() == HandSide.RIGHT;
        ItemStack itemstack = flag ? entitylivingbaseIn.getHeldItemOffhand() : entitylivingbaseIn.getHeldItemMainhand();
        ItemStack itemstack1 = flag ? entitylivingbaseIn.getHeldItemMainhand() : entitylivingbaseIn.getHeldItemOffhand();

        if (!itemstack.isEmpty() || !itemstack1.isEmpty())
        {
            matrixStackIn.push();

            if (this.getEntityModel().isChild)
            {
                float f = 0.5F;
                matrixStackIn.translate(0.0D, 0.75D, 0.0D);
                matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            }

            this.func_229135_a_(entitylivingbaseIn, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HandSide.RIGHT, matrixStackIn, bufferIn, packedLightIn);
            this.func_229135_a_(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HandSide.LEFT, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.pop();
        }
    }

    private void func_229135_a_(LivingEntity p_229135_1_, ItemStack p_229135_2_, ItemCameraTransforms.TransformType p_229135_3_, HandSide p_229135_4_, MatrixStack p_229135_5_, IRenderTypeBuffer p_229135_6_, int p_229135_7_)
    {
        if (!p_229135_2_.isEmpty())
        {
            p_229135_5_.push();

            CustomModel customModel = Manager.FUNCTION_MANAGER.customModel;
            if (customModel != null && customModel.isState() && (customModel.mode.is("Заяц") || customModel.mode.is("Соник"))) {
                boolean applyTranslation = false;

                if (p_229135_1_ instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) p_229135_1_;
                    boolean isSelf = player == Minecraft.getInstance().player;
                    boolean isFriend = Manager.FRIEND_MANAGER.isFriend(player.getName().getString());
                    if (isSelf && customModel.entity.get("Себя")) {
                        applyTranslation = true;
                    } else if (!isSelf && customModel.entity.get("Игроки")) {
                        applyTranslation = true;
                    } else if (!isSelf && isFriend && customModel.entity.get("Друзья")) {
                        applyTranslation = true;
                    }
                }
                boolean flag = p_229135_4_ == HandSide.LEFT;

                if (applyTranslation) {
                    p_229135_5_.translate((flag ? 0.1 : -0.1), 0.4F, 0);
                }
            }

            this.getEntityModel().translateHand(p_229135_4_, p_229135_5_);
            p_229135_5_.rotate(Vector3f.XP.rotationDegrees(-90.0F));
            p_229135_5_.rotate(Vector3f.YP.rotationDegrees(180.0F));
            boolean flag = p_229135_4_ == HandSide.LEFT;
            if (customModel.mode.is("Заяц")) {
                p_229135_5_.translate((double)((float)(flag ? -1 : 1) / (13.0F)), 0.26D, -0.615D);
            } else if (customModel.mode.is("Соник")) {
                p_229135_5_.translate((double)((float)(flag ? -1 : 1) / (100.0F)), 0.110D, -0.565D);
            } else {
                p_229135_5_.translate((double)((float)(flag ? -1 : 1) / 16.0F), 0.125D, -0.625D);
            }
            Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(p_229135_1_, p_229135_2_, p_229135_3_, flag, p_229135_5_, p_229135_6_, p_229135_7_);
            p_229135_5_.pop();
        }
    }
}