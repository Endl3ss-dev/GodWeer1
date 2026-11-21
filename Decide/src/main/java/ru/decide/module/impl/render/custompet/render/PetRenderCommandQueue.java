/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10017
 *  net.minecraft.class_10017$class_10018
 *  net.minecraft.class_10017$class_11680
 *  net.minecraft.class_10444$class_10445
 *  net.minecraft.class_1058
 *  net.minecraft.class_1087
 *  net.minecraft.class_11659
 *  net.minecraft.class_11659$class_11660
 *  net.minecraft.class_11659$class_11947
 *  net.minecraft.class_11683$class_11792
 *  net.minecraft.class_11785
 *  net.minecraft.class_11791
 *  net.minecraft.class_12075
 *  net.minecraft.class_1921
 *  net.minecraft.class_243
 *  net.minecraft.class_2561
 *  net.minecraft.class_2680
 *  net.minecraft.class_327$class_6415
 *  net.minecraft.class_3879
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_5481
 *  net.minecraft.class_630
 *  net.minecraft.class_777
 *  net.minecraft.class_811
 *  org.joml.Quaternionf
 */
package ru.decide.module.impl.render.custompet.render;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10017;
import net.minecraft.class_10444;
import net.minecraft.class_1058;
import net.minecraft.class_1087;
import net.minecraft.class_11659;
import net.minecraft.class_11683;
import net.minecraft.class_11785;
import net.minecraft.class_11791;
import net.minecraft.class_12075;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_327;
import net.minecraft.class_3879;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_5481;
import net.minecraft.class_630;
import net.minecraft.class_777;
import net.minecraft.class_811;
import org.joml.Quaternionf;
import ru.decide.module.impl.render.custompet.render.CustomPetRendererBridge;

@Environment(value=EnvType.CLIENT)
public class PetRenderCommandQueue
implements class_11659 {
    private final class_4597.class_4598 immediate;

    public PetRenderCommandQueue(class_4597.class_4598 immediate) {
        this.immediate = immediate;
    }

    public class_11785 method_73529(int batchingIndex) {
        return this;
    }

    public void method_73483(class_4587 matrices, class_1921 renderLayer, class_11659.class_11660 custom) {
        custom.render(matrices.method_23760(), this.immediate.method_73477(renderLayer));
    }

    public void method_73481(class_4587 matrices, class_2680 blockState, int light, int overlay, int color) {
        CustomPetRendererBridge.renderBlockAsEntity(blockState, matrices, (class_4597)this.immediate, light, overlay);
    }

    public void method_73479(class_4587 matrices, float shadowRadius, List<class_10017.class_11680> shadowPieces) {
    }

    public void method_73482(class_4587 matrices, class_243 pos, int color, class_2561 text, boolean background, int verticalOffset, double depthTestStart, class_12075 cameraState) {
    }

    public void method_73478(class_4587 matrices, float x, float y, class_5481 text, boolean background, class_327.class_6415 layerType, int color, int light, int backgroundColor, int backgroundOverflow) {
    }

    public void method_73488(class_4587 matrices, class_10017 renderState, Quaternionf rotation) {
    }

    public void method_73486(class_4587 matrices, class_10017.class_10018 leashData) {
    }

    public <S> void method_73490(class_3879<? super S> model, S renderState, class_4587 matrices, class_1921 renderLayer, int light, int overlay, int color, class_1058 sprite, int modelIndex, class_11683.class_11792 crumblingOverlayCommand) {
    }

    public void method_73494(class_630 modelPart, class_4587 matrices, class_1921 renderLayer, int light, int overlay, class_1058 sprite, boolean isEmissive, boolean zOffset, int color, class_11683.class_11792 crumblingOverlayCommand, int modelIndex) {
    }

    public void method_73485(class_4587 matrices, class_11791 movingBlockRenderState) {
    }

    public void method_73484(class_4587 matrices, class_1921 renderLayer, class_1087 blockStateModel, float x, float y, float z, int light, int overlay, int color) {
    }

    public void method_73480(class_4587 matrices, class_811 displayContext, int modelIndex, int light, int overlay, int[] tints, List<class_777> quads, class_1921 renderLayer, class_10444.class_10445 glint) {
    }

    public void method_74315(class_11659.class_11947 layeredCustom) {
    }
}

