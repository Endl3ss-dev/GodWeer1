// deobf by pusia
package su.catlean;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.IPasteable;
import su.catlean.api.addon.impl.AddonLoader;
import su.catlean.api.event.events.client.InitEvent;
import su.catlean.api.event.events.client.PostTitleScreenInitEvent;
import su.catlean.client.features.ui.VersionInfoScreen;
import su.catlean.client.render.font.FontLoader;
import su.catlean.client.services.ClientVersion;
import su.catlean.client.services.LocalizationManager;
import su.catlean.client.services.ShutdownHooks;
import su.catlean.client.services.discord.DiscordRpcBootstrap;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class CatLean implements IPasteable {
   @NotNull
   public static final CatLean INSTANCE = new CatLean();

   // [deobf] private in the Kotlin original (object); public here so the default Java
   // entrypoint adapter can instantiate it (javac output has no @kotlin.Metadata).
   public CatLean() {
   }

   public void onInitializeClient() {
      ShutdownHooks.applyAddShutdownHook(this);
      DiscordRpcBootstrap.applyPlugAll(this);
      AddonLoader.INSTANCE.applyError();
   }

   @Flow
   private final void onInit(InitEvent initEvent) {
      PreLaunch.INSTANCE.applySetVisible();
      FontLoader.applyPlug(this);
      LocalizationManager.INSTANCE.applyEntrySet();
   }

   @Flow
   private final void onPostTitleScreenInit(PostTitleScreenInitEvent postTitleScreenInitEvent) {
      if (ClientVersion.isFlag()) {
         MinecraftHolder.getMinecraft().gui.setScreen(new VersionInfoScreen());
      }
   }
}