// deobf by pusia
package su.catlean.client.features.modules.player;

import su.catlean.utils.KotlinCompat;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.FriendManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.network.PacketUtil;

public final class AutoTpaAccept extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(AutoTpaAccept.class, "grief", "getGrief()Z", 0), new MutablePropertyReference1Impl(AutoTpaAccept.class, "onlyFriends", "getOnlyFriends()Z", 0) };
   @NotNull
   public static final AutoTpaAccept INSTANCE = new AutoTpaAccept();
   @NotNull
   private static final BooleanSetting grief = SettingFactory.defaultImpl(INSTANCE, "grief", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting onlyFriends = SettingFactory.defaultImpl(INSTANCE, "only-friends", true, null, null, 12, null);
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();

   private AutoTpaAccept() {
      super("auto-tpa-accept", SubCategories.getSubCategory10(), null, 4, null);
   }

   private final boolean getGrief() {
      return (Boolean)grief.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getOnlyFriends() {
      return (Boolean)onlyFriends.computeObject(this, $$delegatedProperties[1]);
   }

   @Flow
   public final void onReceivePacket(ReceivePacket event) {
      if (event.getPacket() instanceof ClientboundSystemChatPacket && stopWatch.testValue(250)) {
         String string = ((ClientboundSystemChatPacket)event.getPacket()).content().getString();
         if ((KotlinCompat.contains(string, "телепортироваться", false, 2, null) || KotlinCompat.contains(string, "tpaccept", false, 2, null))
            && (!this.getOnlyFriends() || FriendManager.INSTANCE.checkContains(this.fetchProfile(string)))) {
            PacketUtil.applySendCommand("tpaccept " + (this.getGrief() ? this.fetchProfile(string) : ""));
         }

         stopWatch.update();
      }
   }

   private final String fetchProfile(String text) {
      Iterable<PlayerInfo> listedOnlinePlayers = MinecraftHolder.getClientPacketListener().getListedOnlinePlayers();
      Iterator<PlayerInfo> iterator = listedOnlinePlayers.iterator();

      Object object;
      while (true) {
         if (iterator.hasNext()) {
            PlayerInfo object2 = iterator.next();
            PlayerInfo playerInfo = (PlayerInfo)object2;
            if (!KotlinCompat.contains(text, playerInfo.getProfile().name(), false, 2, null)) {
               continue;
            }

            object = (PlayerInfo)object2;
            break;
         }

         object = null;
         break;
      }

      object = object;
      if (object != null) {
         GameProfile profile = ((PlayerInfo)object).getProfile();
         if (profile != null) {
            String name = profile.name();
            if (name != null) {
               return name;
            }
         }
      }

      return "-";
   }

   static {
   }
}