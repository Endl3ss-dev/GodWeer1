/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.WeatherRendering
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.world.World
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package minced.mixin.world;
import minced.core.ServiceRegistry;
import minced.core.BotView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WeatherRendering;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value={WeatherRendering.class})
public abstract class WeatherRenderingMixin {
    @ModifyVariable(method={"renderPrecipitation(Lnet/minecraft/world/World;Lnet/minecraft/client/render/VertexConsumerProvider;IFLnet/minecraft/util/math/Vec3d;)V"}, at=@At(value="HEAD"), ordinal=0, argsOnly=true)
    private World mincedbots$restoreWorldForPrecipitation(World param1) {
        return WeatherRenderingMixin.mincedbots$restoreWorld(param1);
    }

    @ModifyVariable(method={"addParticlesAndSound(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/render/Camera;ILnet/minecraft/particle/ParticlesMode;)V"}, at=@At(value="HEAD"), ordinal=0, argsOnly=true)
    private ClientWorld mincedbots$restoreWorldForWeatherParticles(ClientWorld param1) {
        ClientWorld v3;
        World v2 = WeatherRenderingMixin.mincedbots$restoreWorld((World)param1);
        return v2 instanceof ClientWorld ? (v3 = (ClientWorld)v2) : null;
    }

    private static World mincedbots$restoreWorld(World param0) {
        ClientWorld v2;
        if (param0 != null) {
            return param0;
        }
        BotView v1 = (BotView)ServiceRegistry.method2118(BotView.class);
        if (v1 != null && v1.method2190() && (v2 = v1.method2200()) != null) {
            return v2;
        }
        return MinecraftClient.getInstance().world;
    }
}



