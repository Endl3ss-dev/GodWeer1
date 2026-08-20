/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleManager
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.Entity
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package minced.mixin.render;
import minced.core.ServiceRegistry;
import minced.core.BotView;
import minced.core.EventNoRender;
import minced.core.Event;
import minced.core.EventBus;
import minced.enums.fire;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ParticleManager.class})
public class ParticleManagerMixin {
    @Shadow
    protected ClientWorld field_3834;
    @Unique
    private boolean mincedbots$tickingParticles;

    @Inject(method={"tick"}, at={@At(value="HEAD")}, cancellable=true)
    private void mincedbots$syncWorldBeforeTick(CallbackInfo param1) {
        this.mincedbots$syncWorld();
        if (this.field_3834 == null) {
            param1.cancel();
            return;
        }
        this.mincedbots$tickingParticles = true;
    }

    @Inject(method={"tick"}, at={@At(value="RETURN")})
    private void mincedbots$finishTick(CallbackInfo param1) {
        this.mincedbots$tickingParticles = false;
    }

    @Inject(method={"createParticle"}, at={@At(value="HEAD")}, cancellable=true)
    private void mincedbots$guardCreateParticle(ParticleEffect param1, double param2, double param4, double param6, double param8, double param10, double param12, CallbackInfoReturnable<Particle> param14) {
        if (!this.mincedbots$tickingParticles) {
            this.mincedbots$syncWorld();
        }
        if (this.field_3834 == null) {
            param14.setReturnValue(null);
        }
    }

    @Inject(method={"addEmitter(Lnet/minecraft/entity/Entity;Lnet/minecraft/particle/ParticleEffect;)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void mincedbots$guardEmitter(Entity param1, ParticleEffect param2, CallbackInfo param3) {
        if (!this.mincedbots$tickingParticles) {
            this.mincedbots$syncWorld();
        }
        if (this.field_3834 == null) {
            param3.cancel();
        }
    }

    @Inject(method={"addEmitter(Lnet/minecraft/entity/Entity;Lnet/minecraft/particle/ParticleEffect;I)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void mincedbots$guardEmitterTimed(Entity param1, ParticleEffect param2, int param3, CallbackInfo param4) {
        if (!this.mincedbots$tickingParticles) {
            this.mincedbots$syncWorld();
        }
        if (this.field_3834 == null) {
            param4.cancel();
        }
    }

    @Unique
    private void mincedbots$syncWorld() {
        ClientWorld v4;
        MinecraftClient v1 = MinecraftClient.getInstance();
        ClientWorld v2 = v1.world;
        BotView v3 = (BotView)ServiceRegistry.method2118(BotView.class);
        if (v3 != null && v3.method2190() && (v4 = v3.method2200()) != null && v1.player == v3.method2217() && v1.getCameraEntity() == v3.method2217()) {
            v2 = v4;
        }
        if (this.field_3834 != v2) {
            ((ParticleManager)(Object)this).setWorld(v2);
        }
    }

    @Inject(method={"addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;"}, at={@At(value="HEAD")}, cancellable=true)
    public void addParticle(ParticleEffect param1, double param2, double param4, double param6, double param8, double param10, double param12, CallbackInfoReturnable<Particle> param14) {
        if (param1 != null && (param1.getType() == ParticleTypes.FIREWORK || param1.getType() == ParticleTypes.FLASH)) {
            return;
        }
        EventNoRender v15 = new EventNoRender(fire.smoke);
        EventBus.method3015(((Object)v15));
        if (v15.method2297()) {
            param14.setReturnValue(null);
        }
    }
}



