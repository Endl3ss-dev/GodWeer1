// deobf by pusia
package su.catlean.client.services.discord;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.IPasteable;
import su.catlean.api.event.events.render.SplashOverlayEvent;
import su.catlean.client.features.modules.esp.WaypointRenderer;
import su.catlean.client.render.EntityPreviewRenderer;
import su.catlean.client.render.Pipelines;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.services.BreakingBlocksService;
import su.catlean.client.services.ChatStaffScanner;
import su.catlean.client.services.ChestOpenTracker;
import su.catlean.client.services.ChunkScanner;
import su.catlean.client.services.CommandManager;
import su.catlean.client.services.CrystalPredictor;
import su.catlean.client.services.InteractionManager;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.LocalizationManager;
import su.catlean.client.services.MacroManager;
import su.catlean.client.services.ModuleManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.OnlineUsersService;
import su.catlean.client.services.PlayerPredictionService;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.ServerInfoService;
import su.catlean.client.services.SoundManager;
import su.catlean.client.services.ThreadManager;
import su.catlean.client.services.TickService;
import su.catlean.client.services.WidgetManager;
import su.catlean.client.services.proxy.ProxyChecker;
import su.catlean.client.services.proxy.ProxyManager;
import su.catlean.gofra.Flow;
import su.catlean.gofra.Gofra;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.async.TaskScheduler;
import su.catlean.utils.player.PlayerTargetUtil;
import su.catlean.utils.render.BillboardRenderer;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;
import su.catlean.utils.world.WorldUtil;

public final class DiscordRpcBootstrap {
   public static final void applyPlugAll(IPasteable self) {
      Gofra instance = Gofra.INSTANCE;
      Object[] objects = new Object[]{
         self,
         ProxyManager.INSTANCE,
         ModuleManager.INSTANCE,
         DiscordRpcBootstrap.Holder.INSTANCE,
         CrystalPredictor.INSTANCE,
         WidgetManager.INSTANCE,
         ChatStaffScanner.INSTANCE,
         ServerInfoService.INSTANCE,
         RotationManager.INSTANCE,
         InventoryManager.INSTANCE,
         CommandManager.INSTANCE,
         TickService.INSTANCE,
         NotificationManager.INSTANCE,
         WorldUtil.INSTANCE,
         PlayerTargetUtil.INSTANCE,
         BillboardRenderer.INSTANCE,
         MacroManager.INSTANCE,
         WaypointRenderer.INSTANCE,
         ProxyChecker.INSTANCE,
         ThreadManager.INSTANCE,
         DiscordRpcService.INSTANCE,
         BreakingBlocksService.INSTANCE,
         InteractionManager.INSTANCE,
         ChestOpenTracker.INSTANCE,
         EntityPreviewRenderer.INSTANCE,
         SoundManager.INSTANCE,
         OnlineUsersService.INSTANCE,
         PlayerPredictionService.INSTANCE,
         TaskScheduler.INSTANCE,
         ChunkScanner.INSTANCE,
         Render2D.INSTANCE,
         Render3D.INSTANCE,
         Pipelines.INSTANCE,
         LocalizationManager.INSTANCE
      };
      instance.plugAll(objects);
   }

   public static final class Holder {
      @NotNull
      public static final DiscordRpcBootstrap.Holder INSTANCE = new DiscordRpcBootstrap.Holder();
      @NotNull
      private static final SvgTexture svgTexture;
      @NotNull
      private static final Animation animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 250L);

      private Holder() {
      }

      @Flow
      private final void onSplashOverlay(SplashOverlayEvent splashOverlayEvent) {
      }

      static {
         // [deobf] original clinit decrypted the asset path via the ZKM DES shell; value lost in
         // sanitization. Reconstructed as "hhlogo" (the 230x50 hellhit.fun banner in assets/catlean/vector/).
         svgTexture = new SvgTexture("hhlogo", 230, 50);
      }
   }
}