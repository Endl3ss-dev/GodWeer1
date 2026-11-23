// deobf by pusia
package su.catlean.client.features.modules.misc;

import su.catlean.utils.KotlinCompat;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.client.InputEvent;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BindSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.StringSetting;
import su.catlean.api.setting.values.Bind;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.CommandManager;
import su.catlean.client.services.FriendManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.network.PacketUtil;

public final class TeamCommunication extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(TeamCommunication.class, "xorKey", "getXorKey()Ljava/lang/String;", 0), new PropertyReference1Impl(TeamCommunication.class, "sendCoords", "getSendCoords()Lsu/catlean/dd;", 0), new PropertyReference1Impl(TeamCommunication.class, "tpHere", "getTpHere()Lsu/catlean/dd;", 0), new PropertyReference1Impl(TeamCommunication.class, "marryTp", "getMarryTp()Z", 0) };
   @NotNull
   public static final TeamCommunication INSTANCE = new TeamCommunication();
   @NotNull
   private static final StringSetting key = SettingFactory.defaultImpl(INSTANCE, "key", "catlean", null, null, 12, null);
   @NotNull
   private static final BindSetting sendCoords = SettingFactory.defaultImpl(INSTANCE, "send-coords", new Bind(0, false, false, 7, null), null, null, 12, null);
   @NotNull
   private static final BindSetting tpHere = SettingFactory.defaultImpl(INSTANCE, "tp-here", new Bind(0, false, false, 7, null), null, null, 12, null);
   @NotNull
   private static final BooleanSetting marryTp = SettingFactory.defaultImpl(INSTANCE, "marry-tp", false, null, null, 12, null);
   @NotNull
   private static final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

   private TeamCommunication() {
      super("team-communication", SubCategories.getSubCategory11(), null, 4, null);
   }

   private final String getXorKey() {
      return (String)key.computeObject(this, $$delegatedProperties[0]);
   }

   private final Bind getSendCoords() {
      return (Bind)sendCoords.computeObject(this, $$delegatedProperties[1]);
   }

   private final Bind getTpHere() {
      return (Bind)tpHere.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getMarryTp() {
      return (Boolean)marryTp.computeObject(this, $$delegatedProperties[3]);
   }

   @NotNull
   public final AtomicBoolean getAtomicBoolean() {
      return atomicBoolean;
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (sendPacket.getPacket() instanceof ServerboundChatPacket) {
         if (atomicBoolean.get()) {
            return;
         }

         ServerboundChatPacket packet = (ServerboundChatPacket)sendPacket.getPacket();
         atomicBoolean.set(true);
         ChatUtil.applyExecuteIfPossible(this, this.fetchBytes(packet.message), true);
         atomicBoolean.set(false);
         sendPacket.cancel();
      }
   }

   @Flow
   private final void onInput(InputEvent inputEvent) {
      if (inputEvent.getAction() == InputEvent.Action.Press) {
         if (inputEvent.getKey() == this.getTpHere().getValue()) {
            ChatUtil.applyExecuteIfPossible(this, this.getMarryTp() ? "action_marry_tp" : "action_tp", true);
         }

         if (inputEvent.getKey() == this.getSendCoords().getValue()) {
            ChatUtil.applyExecuteIfPossible(
               this, "xCoord" + (int)MinecraftHolder.getLocalPlayer().getX() + "zCoord" + (int)MinecraftHolder.getLocalPlayer().getZ() + "end", true
            );
         }
      }
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundSystemChatPacket) {
         ClientboundSystemChatPacket packet = (ClientboundSystemChatPacket)receivePacket.getPacket();
         String string = packet.content().getString();
         if (KotlinCompat.contains(string, "cat_", false, 2, null) && KotlinCompat.contains(string, "_lean", false, 2, null)) {
            String bytes2 = this.fetchBytes2(
               KotlinCompat.substringBefore(KotlinCompat.substringAfter(string, "cat_", null, 2, null), "_lean", null, 2, null)
            );
            String profile = this.fetchProfile(string);
            if (Intrinsics.areEqual(profile, MinecraftHolder.getLocalPlayer().getGameProfile().name())) {
               return;
            }

            if (KotlinCompat.contains(bytes2, "xCoord", false, 2, null)) {
               try {
                  int substringBeforedefault = Integer.parseInt(
                     KotlinCompat.substringBefore(KotlinCompat.substringAfter(bytes2, "xCoord", null, 2, null), "zCoord", null, 2, null)
                  );
                  int substringBeforedefault2 = Integer.parseInt(
                     KotlinCompat.substringBefore(KotlinCompat.substringAfter(bytes2, "zCoord", null, 2, null), "end", null, 2, null)
                  );
                  CommandManager.INSTANCE.setBlockPos(new BlockPos(substringBeforedefault, 0, substringBeforedefault2));
               } catch (Exception exception) {
               }

               return;
            }

            if (KotlinCompat.contains(bytes2, "action_marry_tp", false, 2, null)) {
               PacketUtil.applySendCommand("marry tp " + profile);
               return;
            }

            if (KotlinCompat.contains(bytes2, "action_tp", false, 2, null)) {
               PacketUtil.applySendCommand("tpa " + profile);
               return;
            }

            ChatUtil.defaultImpl(this, profile + ": " + bytes2, false, 2, null);
            if (FriendManager.INSTANCE.checkContains(profile)) {
               receivePacket.cancel();
            }
         }
      }
   }

   @NotNull
   public final String fetchBytes(String input) {
      byte[] bytes = input.getBytes(Charsets.UTF_8);
      String xorKey = this.getXorKey();
      byte[] bytes2 = xorKey.getBytes(Charsets.UTF_8);
      byte[] byteValues = new byte[bytes.length];
      int value = 0;

      for (int i = bytes.length; value < i; value++) {
         byteValues[value] = (byte)(bytes[value] ^ bytes2[value % bytes2.length]);
      }

      return "!cat_" + this.computeText(byteValues) + "_lean";
   }

   @NotNull
   public final String fetchBytes2(String input) {
      byte[] values = this.computeValues(input);
      String xorKey = this.getXorKey();
      byte[] bytes = xorKey.getBytes(Charsets.UTF_8);
      byte[] byteValues = new byte[values.length];
      int value = 0;

      for (int i = values.length; value < i; value++) {
         byteValues[value] = (byte)(values[value] ^ bytes[value % bytes.length]);
      }

      return new String(byteValues, Charsets.UTF_8);
   }

   @NotNull
   public final String computeText(byte[] bytes) {
      return KotlinCompat.replace(
         KotlinCompat.replace(KotlinCompat.replace(Base64.getEncoder().encodeToString(bytes), '+', '(', false, 4, null), '/', ')', false, 4, null),
         '=',
         '%',
         false,
         4,
         null
      );
   }

   @NotNull
   public final byte[] computeValues(String string) {
      return Base64.getDecoder()
         .decode(
            KotlinCompat.replace(
               KotlinCompat.replace(KotlinCompat.replace(string, '(', '+', false, 4, null), ')', '/', false, 4, null), '%', '=', false, 4, null
            )
         );
   }

   @NotNull
   public final String fetchProfile(String notSolved) {
      AtomicReference<String> atomicReference = new AtomicReference<>("FATAL ERROR");

      for (Object listedOnlinePlayer : MinecraftHolder.getClientPacketListener().getListedOnlinePlayers()) {
         PlayerInfo playerInfo = (PlayerInfo)listedOnlinePlayer;
         if (KotlinCompat.contains(notSolved, playerInfo.getProfile().name(), false, 2, null)) {
            atomicReference.set(playerInfo.getProfile().name());
         }
      }

      return (String)atomicReference.get();
   }

   static {
   }
}