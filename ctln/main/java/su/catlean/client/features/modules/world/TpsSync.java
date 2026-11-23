// deobf by pusia
package su.catlean.client.features.modules.world;

import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.ServerInfoService;
import su.catlean.client.services.TickService;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class TpsSync extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new MutablePropertyReference1Impl(TpsSync.class, "protection", "getProtection()Z", 0)};
   @NotNull
   public static final TpsSync INSTANCE = new TpsSync();
   @NotNull
   private static final BooleanSetting kickProtection = SettingFactory.defaultImpl(INSTANCE, "kick-protection", true, null, null, 12, null);

   private TpsSync() {
      super("tps-sync", SubCategories.getSubCategory8(), null, 4, null);
   }

   private final boolean getProtection() {
      return (Boolean)kickProtection.computeObject(this, $$delegatedProperties[0]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (ServerInfoService.INSTANCE.getAmount() < 18.0F && MinecraftHolder.getLocalPlayer().tickCount > 100 && !ServerInfoService.INSTANCE.isFlag2()) {
         TickService.INSTANCE.setAmount2(ServerInfoService.INSTANCE.getAmount() / 20.0F);
      }

      if (this.getProtection()
         && MinecraftHolder.getLocalPlayer().tickCount > 1200
         && ServerInfoService.INSTANCE.getTime() > 1000L
         && !MinecraftHolder.getMinecraft().isLocalServer()
         && (MinecraftHolder.getLocalPlayer().onGround() || MinecraftHolder.getLocalPlayer().isFallFlying())) {
         playerUpdateEvent.cancel();
      }
   }

   static {
   }
}