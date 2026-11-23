// deobf by pusia
package su.catlean.client.features.modules.player;

import su.catlean.utils.KotlinCompat;
import java.util.Locale;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.StringSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.NotificationManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.chat.MessageType;
import su.catlean.utils.network.PacketUtil;

public final class AutoAuth extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new PropertyReference1Impl(AutoAuth.class, "pass", "getPass()Ljava/lang/String;", 0)};
   @NotNull
   public static final AutoAuth INSTANCE = new AutoAuth();
   @NotNull
   private static final StringSetting password = SettingFactory.defaultImpl(INSTANCE, "password", "c1a2t3l4e5a6n", null, null, 12, null);

   private AutoAuth() {
      super("auto-auth", SubCategories.getSubCategory10(), null, 4, null);
   }

   private final String getPass() {
      return (String)password.computeObject(this, $$delegatedProperties[0]);
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundSystemChatPacket) {
         ClientboundSystemChatPacket packet = (ClientboundSystemChatPacket)receivePacket.getPacket();
         String lowerCase = packet.content().getString().toLowerCase(Locale.ROOT);
         if (this.getPass().length() == 0) {
            ChatUtil.defaultImpl(
               this, ChatFormatting.RED.toString() + ChatUtil.defaultImpl(this, "pass-is-empty", new Object[0], false, 4, null), false, 2, null
            );
            NotificationManager.defaultImpl(
               NotificationManager.INSTANCE,
               "Auto Auth",
               ChatUtil.defaultImpl(this, "pass-is-empty", new Object[0], false, 4, null),
               4,
               MessageType.ERROR,
               null,
               16,
               null
            );
            return;
         }

         if (KotlinCompat.contains(lowerCase, "/reg", false, 2, null)
            || KotlinCompat.contains(lowerCase, "/register", false, 2, null)
            || KotlinCompat.contains(lowerCase, "зарегистрируйтесь", false, 2, null)) {
            PacketUtil.applySendCommand("reg " + this.getPass() + " " + this.getPass());
            NotificationManager.defaultImpl(
               NotificationManager.INSTANCE,
               "Auto Auth",
               ChatUtil.defaultImpl(this, "reg-success", new Object[0], false, 4, null),
               4,
               MessageType.SUCCESS,
               null,
               16,
               null
            );
         } else if (KotlinCompat.contains(lowerCase, "авторизуйтесь", false, 2, null)
            || KotlinCompat.contains(lowerCase, "/l ", false, 2, null)
            || KotlinCompat.contains(lowerCase, "/login", false, 2, null)) {
            PacketUtil.applySendCommand("login " + this.getPass());
            NotificationManager.defaultImpl(
               NotificationManager.INSTANCE,
               "Auto Auth",
               ChatUtil.defaultImpl(this, "log-success", new Object[0], false, 4, null),
               4,
               MessageType.SUCCESS,
               null,
               16,
               null
            );
         }
      }
   }

   static {
   }
}