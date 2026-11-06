/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10017
 *  net.minecraft.class_10201
 *  net.minecraft.class_10442
 *  net.minecraft.class_11697
 *  net.minecraft.class_11786
 *  net.minecraft.class_1297
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_330
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597
 *  net.minecraft.class_5599
 *  net.minecraft.class_5617$class_5618
 *  net.minecraft.class_776
 *  net.minecraft.class_897
 *  net.minecraft.class_898
 */
package ru.decide.module.impl.render.custompet.render;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10017;
import net.minecraft.class_10201;
import net.minecraft.class_10442;
import net.minecraft.class_11697;
import net.minecraft.class_11786;
import net.minecraft.class_1297;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_330;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_5599;
import net.minecraft.class_5617;
import net.minecraft.class_776;
import net.minecraft.class_897;
import net.minecraft.class_898;
import ru.decide.module.impl.render.custompet.entity.CustomPetEntity;
import ru.decide.module.impl.render.custompet.render.CustomPetRenderState;
import ru.decide.module.impl.render.custompet.render.CustomPetRenderer;

@Environment(value=EnvType.CLIENT)
public final class CustomPetRendererBridge {
    private static class_310 minecraft;
    private static class_898 dispatcher;
    private static class_776 blockRenderManager;
    private static class_10442 itemModelManager;
    private static class_330 mapRenderer;
    private static class_327 font;
    private static Supplier<class_5599> modelSetSupplier;
    private static class_10201 equipmentModelLoader;
    private static class_11697 atlasManager;
    private static class_11786 playerSkinCache;
    private static class_897<? super CustomPetEntity, ?> renderer;

    private CustomPetRendererBridge() {
    }

    public static void bootstrap(class_898 entityRenderManager, class_310 minecraftClient, class_776 blockRenderManager, class_10442 itemModelManager, class_330 mapRenderer, class_11697 atlasManager, class_327 textRenderer, Supplier<class_5599> supplier, class_10201 equipmentModelLoader, class_11786 playerSkinCache) {
        dispatcher = entityRenderManager;
        minecraft = minecraftClient;
        CustomPetRendererBridge.blockRenderManager = blockRenderManager;
        CustomPetRendererBridge.itemModelManager = itemModelManager;
        CustomPetRendererBridge.mapRenderer = mapRenderer;
        CustomPetRendererBridge.atlasManager = atlasManager;
        font = textRenderer;
        modelSetSupplier = supplier;
        CustomPetRendererBridge.equipmentModelLoader = equipmentModelLoader;
        CustomPetRendererBridge.playerSkinCache = playerSkinCache;
        renderer = null;
    }

    public static void reload() {
        renderer = null;
    }

    private static class_897<? super CustomPetEntity, ?> getOrCreateRenderer() {
        if (renderer == null) {
            class_5617.class_5618 context = new class_5617.class_5618(dispatcher, itemModelManager, mapRenderer, blockRenderManager, minecraft.method_1478(), modelSetSupplier.get(), equipmentModelLoader, atlasManager, font, playerSkinCache);
            renderer = new CustomPetRenderer(context);
        }
        return renderer;
    }

    public static <S extends class_10017> class_897<?, ? super S> getCustomRenderer(S s) {
        if (dispatcher == null || minecraft == null) {
            return null;
        }
        return s instanceof CustomPetRenderState ? CustomPetRendererBridge.getOrCreateRenderer() : null;
    }

    public static CustomPetRenderer getPetRenderer() {
        if (dispatcher == null || minecraft == null) {
            return null;
        }
        return (CustomPetRenderer)CustomPetRendererBridge.getOrCreateRenderer();
    }

    public static void renderBlockAsEntity(class_2680 state, class_4587 matrices, class_4597 provider, int light, int overlay) {
        if (blockRenderManager == null) {
            return;
        }
        blockRenderManager.method_3353(state, matrices, provider, light, overlay);
    }

    public static <T extends class_1297> class_897<? super T, ?> getCustomRenderer(T t) {
        if (dispatcher == null || minecraft == null) {
            return null;
        }
        if (t instanceof CustomPetEntity) {
            return CustomPetRendererBridge.getOrCreateRenderer();
        }
        return null;
    }
}

