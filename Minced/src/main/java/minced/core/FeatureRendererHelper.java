/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.mixin.client.rendering.LivingEntityRendererAccessor
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 */
package minced.core;

import net.fabricmc.fabric.mixin.client.rendering.LivingEntityRendererAccessor;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

public interface FeatureRendererHelper
extends FeatureRendererContext,
LivingEntityRendererAccessor {
    public void minced$render(LivingEntityRenderState var1, MatrixStack var2, VertexConsumerProvider var3, int var4);
}

