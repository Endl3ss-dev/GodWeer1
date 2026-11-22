/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10017
 *  net.minecraft.class_12075
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1920
 *  net.minecraft.class_2338
 *  net.minecraft.class_2374
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_4184
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_761
 *  net.minecraft.class_9799
 */
package ru.decide.cosmetics.custompet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10017;
import net.minecraft.class_12075;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1920;
import net.minecraft.class_2338;
import net.minecraft.class_2374;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4597;
import net.minecraft.class_761;
import net.minecraft.class_9799;
import ru.decide.Client;
import ru.decide.cosmetics.CosmeticManager;
import ru.decide.cosmetics.custompet.CustomPetCosmetics;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.impl.render.custompet.CustomPetWarmup;
import ru.decide.module.impl.render.custompet.control.CustomPetFollowerController;
import ru.decide.module.impl.render.custompet.control.DogPetController;
import ru.decide.module.impl.render.custompet.entity.CustomPetEntity;
import ru.decide.module.impl.render.custompet.render.CustomPetRenderState;
import ru.decide.module.impl.render.custompet.render.CustomPetRenderer;
import ru.decide.module.impl.render.custompet.render.CustomPetRendererBridge;
import ru.decide.module.impl.render.custompet.render.PetRenderCommandQueue;

@Environment(value=EnvType.CLIENT)
public final class CustomPetRuntime {
    private static final CustomPetRuntime INSTANCE = new CustomPetRuntime();
    private final class_310 mc = class_310.method_1551();
    private final CustomPetFollowerController localController = new CustomPetFollowerController();
    private final DogPetController dogController = new DogPetController();
    private final class_9799 renderAllocator = new class_9799(0x100000);
    private boolean failed;
    private boolean warmedUp;
    private int activeIndex = -1;
    private double prevTickX;
    private double prevTickY;
    private double prevTickZ;
    private double currTickX;
    private double currTickY;
    private double currTickZ;
    private long interpolationTick = -1L;
    private double smoothX;
    private double smoothY;
    private double smoothZ;
    private boolean smoothInitialized;

    private CustomPetRuntime() {
    }

    public static CustomPetRuntime getInstance() {
        return INSTANCE;
    }

    public void initialize() {
        Client.eventHandler().subscribe(this);
    }

    private CustomPetCosmetics.Spec selectedSpec() {
        Integer index = CosmeticManager.getInstance().getSelectedByType().get("companion");
        return index == null ? null : CustomPetCosmetics.specForIndex(index);
    }

    @EventHandler
    public void onTick(EventTick event) {
        CustomPetCosmetics.Spec spec = this.selectedSpec();
        if (this.mc.field_1724 == null || this.mc.field_1687 == null || this.mc.field_1724.method_31481() || spec == null) {
            this.resetAll();
            return;
        }
        if (!this.warmedUp) {
            this.warmedUp = true;
            CustomPetWarmup.warmup();
        }
        if (this.activeIndex != spec.index()) {
            this.resetAll();
            this.activeIndex = spec.index();
        }
        if (this.failed) {
            return;
        }
        try {
            if (spec.dog()) {
                this.dogController.tick((class_1657)this.mc.field_1724);
                return;
            }
            this.localController.tick((class_1657)this.mc.field_1724, spec.variant(), spec.owl());
            CustomPetEntity pet = this.localController.getPet();
            if (pet != null) {
                pet.setRobotType(spec.robotType());
                pet.method_5773();
            }
        }
        catch (Throwable throwable) {
            this.failed = true;
        }
    }

    @EventHandler
    public void onRender3D(EventRender3D event) {
        if (this.mc.field_1724 == null || this.mc.field_1687 == null) {
            return;
        }
        CustomPetCosmetics.Spec spec = this.selectedSpec();
        if (spec == null || this.failed) {
            return;
        }
        try {
            float tickDelta = event.getTickDelta();
            if (spec.dog()) {
                this.dogController.render(event, tickDelta, (class_1657)this.mc.field_1724, spec.dachshund());
                return;
            }
            CustomPetEntity pet = this.localController.getPet();
            if (pet == null) {
                return;
            }
            CustomPetRenderer renderer = CustomPetRendererBridge.getPetRenderer();
            if (renderer == null) {
                return;
            }
            class_12075 cameraState = CustomPetRuntime.buildCameraRenderState(this.mc.field_1773.method_19418());
            CustomPetRenderState state = renderer.createRenderState(pet, null);
            state.field_61820 = class_761.method_23794((class_1920)this.mc.field_1687, (class_2338)pet.method_24515());
            renderer.method_62354((class_1297)pet, (class_10017)state, tickDelta);
            class_243 renderPos = this.interpolateRenderPos(pet, tickDelta);
            class_4597.class_4598 immediate = class_4597.method_22991((class_9799)this.renderAllocator);
            PetRenderCommandQueue queue = new PetRenderCommandQueue(immediate);
            event.getMatrixStack().method_22903();
            event.getMatrixStack().method_22904(renderPos.method_10216() - cameraState.field_63078.field_1352, renderPos.method_10214() - cameraState.field_63078.field_1351, renderPos.method_10215() - cameraState.field_63078.field_1350);
            renderer.render(state, event.getMatrixStack(), queue, cameraState);
            event.getMatrixStack().method_22909();
            immediate.method_22993();
        }
        catch (Throwable throwable) {
            this.failed = true;
        }
    }

    private class_243 interpolateRenderPos(CustomPetEntity pet, float tickDelta) {
        double groundY;
        long worldTime = this.mc.field_1687.method_75260();
        if (this.interpolationTick != worldTime) {
            this.prevTickX = this.currTickX;
            this.prevTickY = this.currTickY;
            this.prevTickZ = this.currTickZ;
            this.currTickX = pet.method_23317();
            this.currTickY = pet.method_23318();
            this.currTickZ = pet.method_23321();
            this.interpolationTick = worldTime;
        }
        double renderedX = this.prevTickX + (this.currTickX - this.prevTickX) * (double)tickDelta;
        double renderedY = this.prevTickY + (this.currTickY - this.prevTickY) * (double)tickDelta;
        double renderedZ = this.prevTickZ + (this.currTickZ - this.prevTickZ) * (double)tickDelta;
        if (!this.smoothInitialized) {
            this.smoothX = renderedX;
            this.smoothY = renderedY;
            this.smoothZ = renderedZ;
            this.smoothInitialized = true;
        } else {
            double alpha = 0.55;
            this.smoothX += (renderedX - this.smoothX) * alpha;
            this.smoothY += (renderedY - this.smoothY) * alpha;
            this.smoothZ += (renderedZ - this.smoothZ) * alpha;
        }
        if (!pet.isAirborneMode() && this.smoothY < (groundY = pet.getGroundSurfaceY(this.smoothX, this.smoothY, this.smoothZ)) - 0.1) {
            this.smoothY = groundY;
        }
        return new class_243(this.smoothX, this.smoothY, this.smoothZ);
    }

    private static class_12075 buildCameraRenderState(class_4184 camera) {
        class_12075 cameraState = new class_12075();
        cameraState.field_63079 = true;
        cameraState.field_63078 = camera.method_71156();
        cameraState.field_63077 = class_2338.method_49638((class_2374)camera.method_71156());
        cameraState.field_63080 = camera.method_71156();
        cameraState.field_63081 = camera.method_23767();
        return cameraState;
    }

    private void resetAll() {
        if (this.activeIndex == -1 && !this.failed) {
            return;
        }
        this.localController.reset();
        this.dogController.reset();
        this.failed = false;
        this.activeIndex = -1;
        this.interpolationTick = -1L;
        this.smoothInitialized = false;
    }
}

