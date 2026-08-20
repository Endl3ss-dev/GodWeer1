/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.UserApiService
 *  com.mojang.authlib.minecraft.UserApiService$UserProperties
 *  com.mojang.authlib.yggdrasil.ProfileResult
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.network.ClientPlayerInteractionManager
 *  net.minecraft.client.network.SocialInteractionsManager
 *  net.minecraft.client.session.ProfileKeys
 *  net.minecraft.client.session.Session
 *  net.minecraft.client.session.report.AbuseReportContext
 *  net.minecraft.client.session.telemetry.TelemetryManager
 *  net.minecraft.client.world.ClientWorld
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package minced.mixin.accessor;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.telemetry.TelemetryManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={MinecraftClient.class})
public interface MinecraftAccessor {
    @Accessor(value="session")
    @Mutable
    public void ias$session(Session var1);

    @Accessor(value="gameProfileFuture")
    @Mutable
    public void ias$gameProfileFuture(CompletableFuture<ProfileResult> var1);

    @Accessor(value="authenticationService")
    public YggdrasilAuthenticationService ias$authenticationService();

    @Accessor(value="userApiService")
    @Mutable
    public void ias$userApiService(UserApiService var1);

    @Accessor(value="userPropertiesFuture")
    @Mutable
    public void ias$userPropertiesFuture(CompletableFuture<UserApiService.UserProperties> var1);

    @Accessor(value="socialInteractionsManager")
    @Mutable
    public void ias$socialInteractionsManager(SocialInteractionsManager var1);

    @Accessor(value="telemetryManager")
    @Mutable
    public void ias$telemetryManager(TelemetryManager var1);

    @Accessor(value="profileKeys")
    @Mutable
    public void ias$profileKeys(ProfileKeys var1);

    @Accessor(value="abuseReportContext")
    @Mutable
    public void ias$abuseReportContext(AbuseReportContext var1);

    @Accessor(value="world")
    @Mutable
    public void mincedbots$setWorld(ClientWorld var1);

    @Accessor(value="player")
    @Mutable
    public void mincedbots$setPlayer(ClientPlayerEntity var1);

    @Accessor(value="interactionManager")
    @Mutable
    public void mincedbots$setInteractionManager(ClientPlayerInteractionManager var1);

    @Accessor(value="itemUseCooldown")
    public void mincedbots$setItemUseCooldown(int var1);

    @Invoker(value="doAttack")
    public boolean mincedbots$invokeDoAttack();
}



