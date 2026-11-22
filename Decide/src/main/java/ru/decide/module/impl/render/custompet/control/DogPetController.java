/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10799
 *  net.minecraft.class_12247
 *  net.minecraft.class_1657
 *  net.minecraft.class_1921
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4608
 *  net.minecraft.class_9799
 */
package ru.decide.module.impl.render.custompet.control;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1657;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_9799;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.module.impl.render.richdog.PetBrain;
import ru.decide.module.impl.render.richdog.PetModel;

@Environment(value=EnvType.CLIENT)
public final class DogPetController {
    private static final class_310 mc = class_310.method_1551();
    private static final class_2960 TEX_JACK_RUSSELL = class_2960.method_60655((String)"decide", (String)"textures/richdog/djekrussel.png");
    private static final class_2960 TEX_DACHSHUND = class_2960.method_60655((String)"decide", (String)"textures/richdog/taksa.png");
    private static final Map<class_2960, class_1921> LAYERS = new HashMap<class_2960, class_1921>();
    private final PetModel model = new PetModel();
    private final PetBrain brain = new PetBrain();
    private final class_9799 allocator = new class_9799(262144);

    public void tick(class_1657 player) {
        if (player == null) {
            return;
        }
        this.brain.setEntity(player);
        this.brain.onUpdate();
    }

    public void reset() {
        this.brain.setEntity(null);
    }

    public void render(EventRender3D e, float tickDelta, class_1657 player, boolean dachshund) {
        if (player == null || DogPetController.mc.field_1687 == null) {
            return;
        }
        this.brain.setEntity(player);
        class_4587 matrices = e.getMatrixStack();
        class_243 cam = DogPetController.mc.field_1773.method_19418().method_71156();
        class_243 render = this.brain.getPos().method_1020(cam);
        class_2960 texture = dachshund ? TEX_DACHSHUND : TEX_JACK_RUSSELL;
        matrices.method_22903();
        matrices.method_22904(render.field_1352, render.field_1351, render.field_1350);
        this.model.setupAnim((float)player.field_6012 + tickDelta, this.brain);
        class_4597.class_4598 immediate = class_4597.method_22991((class_9799)this.allocator);
        class_4588 consumer = immediate.method_73477(DogPetController.layer(texture));
        this.model.render(matrices, consumer, 0xF000F0, class_4608.field_21444, this.brain);
        immediate.method_22993();
        matrices.method_22909();
    }

    private static class_1921 layer(class_2960 id) {
        return LAYERS.computeIfAbsent(id, tex -> class_1921.method_75940((String)("custompet_dog_" + tex.method_12832().replace('/', '_').replace('.', '_')), (class_12247)class_12247.method_75927((RenderPipeline)class_10799.field_56904).method_75934("Sampler0", tex).method_75929(262144).method_75938()));
    }
}

