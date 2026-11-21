package tech.lightclient.client.utils.client.tweaks.crosshair;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public interface DrawContextFloatDrawTexture {
    default void light$drawGuiTexture(RenderPipeline pipeline, Identifier texture, float x, float y, int width, int height) {}
    default void light$drawGuiTexture(RenderPipeline pipeline, Identifier texture, float x, float y, int width, int height, boolean customRenderCulling) {
        light$drawGuiTexture(pipeline, texture, x, y, width, height);
    }
    default void light$drawGuiTextureColored(RenderPipeline pipeline, Identifier texture, float x, float y, int width, int height, int color, boolean customRenderCulling) {
        light$drawGuiTexture(pipeline, texture, x, y, width, height, customRenderCulling);
    }
    default void light$drawTextureDirect(RenderPipeline pipeline, Identifier texture, float x, float y, int width, int height, int color, boolean customRenderCulling) {
        light$drawGuiTextureColored(pipeline, texture, x, y, width, height, color, customRenderCulling);
    }
    default void light$drawItem(ItemStack stack, int x, int y, boolean customRenderCulling) {}
    default void light$drawItem(ItemStack stack, int x, int y, boolean customRenderCulling, String ignoredCullingOwnerKey) {
        light$drawItem(stack, x, y, customRenderCulling);
    }
}
