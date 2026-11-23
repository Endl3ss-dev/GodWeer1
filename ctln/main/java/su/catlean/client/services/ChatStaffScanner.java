// deobf by pusia
package su.catlean.client.services;

import su.catlean.utils.KotlinCompat;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Parameters;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.events.network.AfterReceivePacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.client.features.modules.esp.PlayerListNames;
import su.catlean.client.features.ui.hud.widgets.StaffWidget;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class ChatStaffScanner {
   @NotNull
   public static final ChatStaffScanner INSTANCE = new ChatStaffScanner();
   @NotNull
   private static final CopyOnWriteArrayList items = new CopyOnWriteArrayList();
   @NotNull
   private static List items2 = new ArrayList();
   @NotNull
   private static final String[] texts;

   private ChatStaffScanner() {
   }

   @NotNull
   public final List getItems2() {
      return items2;
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (StaffWidget.INSTANCE.getEnabled()) {
         if (!items.isEmpty()) {
            items.removeIf(arg -> ((PlayerListNames)arg).getPlayerInfo().getTeam() == null && ((PlayerListNames)arg).isFlag());
            Iterable items3 = items;
            items2 = CollectionsKt.toMutableList(CollectionsKt.sortedWith(items3, new ChatStaffScanner.Ordering()));
         }
      }
   }

   @Flow
   private final void onAfterReceivePacket(AfterReceivePacket afterReceivePacket) {
      if (StaffWidget.INSTANCE.getEnabled()) {
         if (afterReceivePacket.getPacket() instanceof ClientboundSetPlayerTeamPacket) {
            ClientboundSetPlayerTeamPacket packet = (ClientboundSetPlayerTeamPacket)afterReceivePacket.getPacket();
            if (packet.getTeamAction() == Action.ADD) {
               for (String player : packet.getPlayers()) {
                  GameProfile gameProfile = new GameProfile(UUID.fromString("66123666-622-6666-6666-666612366600"), player);
                  boolean flag = true;
                  GameProfile gameProfile2 = gameProfile;
                  PlayerListNames playerListNames = new PlayerListNames(gameProfile2, flag);
                  if (Intrinsics.areEqual(playerListNames.getProfile().name(), MinecraftHolder.getLocalPlayer().getGameProfile().name())) {
                     return;
                  }

                  if (((Parameters)packet.getParameters().get()).playerPrefix().getString().length() > 0) {
                     List take;
                     label51: {
                        String string = this.fetchTabListDisplayName(playerListNames.getPlayerInfo()).getString();
                        CharSequence stripColor = StringUtil.stripColor(string.toLowerCase(Locale.getDefault()));
                        Regex regex = new Regex(" ");
                        byte byteValue = 0;
                        List<String> split = regex.split(stripColor, byteValue);
                        if (!split.isEmpty()) {
                           ListIterator<String> listIterator = split.listIterator(split.size());

                           while (listIterator.hasPrevious()) {
                              String previous = (String)listIterator.previous();
                              if (previous.length() != 0) {
                                 take = CollectionsKt.take(split, listIterator.nextIndex() + 1);
                                 break label51;
                              }
                           }
                        }

                        take = CollectionsKt.emptyList();
                     }

                     Collection collection = take;
                     short shortValue = 0;
                     String[] array = (String[])collection.toArray(new String[0]);
                     ThreadManager instance = ThreadManager.INSTANCE;
                     shortValue = 250;
                     instance.getExecutorService().execute(new ChatStaffScanner.ScanTask(shortValue, playerListNames, array));
                  }
               }
            }
         }
      }
   }

   @NotNull
   public final MutableComponent fetchTabListDisplayName(PlayerInfo entry) {
      return entry.getTabListDisplayName() != null
         ? this.fetchGameMode(entry, entry.getTabListDisplayName().copy())
         : this.fetchGameMode(entry, PlayerTeam.formatNameForTeam((Team)entry.getTeam(), (Component)Component.literal(entry.getProfile().name())));
   }

   private final MutableComponent fetchGameMode(PlayerInfo playerInfo, MutableComponent mutableComponent) {
      return playerInfo.getGameMode() == GameType.SPECTATOR ? mutableComponent.withStyle(ChatFormatting.ITALIC) : mutableComponent;
   }

   private final void updateTexts(String[] texts) {
      if (texts.length >= 2) {
         ;
      }
   }

   private final boolean checkAnyMatch(String text) {
      String stripColor = StringUtil.stripColor(text.toLowerCase(Locale.ROOT));
      if (KotlinCompat.contains(stripColor, "§", false, 2, null)) {
         return false;
      } else {
         CharSequence charSequence = stripColor;
         String[] texts2 = new String[]{" "};

         for (String splitdefaultItem : (Iterable<String>)KotlinCompat.split(charSequence, texts2, false, 0, 6, null)) {
            if (Arrays.stream(texts).anyMatch(arg -> KotlinCompat.startsWith(splitdefaultItem, arg, false, 2, null))) {
               return true;
            }
         }

         for (PlayerInfo onlinePlayer : MinecraftHolder.getLocalPlayer().connection.getOnlinePlayers()) {
            if (Intrinsics.areEqual(onlinePlayer.getProfile().name(), stripColor)) {
               return true;
            }
         }

         return false;
      }
   }

   @Nullable
   public final Player fetchName(String name) {
      for (AbstractClientPlayer player : MinecraftHolder.getClientLevel().players()) {
         if (Intrinsics.areEqual(name, player.getName().getString())) {
            return (Player)player;
         }
      }

      return null;
   }

   private final int computeValue(PlayerListNames playerListNames) {
      byte var12 = 0;
      CharSequence stripColor = StringUtil.stripColor(playerListNames.getText().toLowerCase(Locale.ROOT));
      char[] chs = new char[]{' '};
      List<String> splitdefault = KotlinCompat.split(stripColor, chs, false, 0, 6, null);
      byte byteValue = 11;

      for (String text : (Iterable<String>)splitdefault) {
         label76: {
            label75: {
               label74: {
                  label73: {
                     switch (text.hashCode()) {
                        case -1220931666:
                           if (text.equals("helper")) {
                              var12 = 8;
                              break label76;
                           }
                           break;
                        case -536475072:
                           if (text.equals("ml.admin")) {
                              var12 = 1;
                              break label76;
                           }
                           break;
                        case -446765093:
                           if (text.equals("st.helper")) {
                              var12 = 6;
                              break label76;
                           }
                           break;
                        case -148051614:
                           if (text.equals("st.moder")) {
                              break label74;
                           }
                           break;
                        case 3867:
                           if (text.equals("yt")) {
                              var12 = 10;
                              break label76;
                           }
                           break;
                        case 119920:
                           if (text.equals("yt+")) {
                              var12 = 9;
                              break label76;
                           }
                           break;
                        case 92668751:
                           if (text.equals("admin")) {
                              break label75;
                           }
                           break;
                        case 104069935:
                           if (text.equals("moder")) {
                              break label73;
                           }
                           break;
                        case 404719092:
                           if (text.equals("s.moder")) {
                              break label74;
                           }
                           break;
                        case 805824061:
                           if (text.equals("helper+")) {
                              var12 = 7;
                              break label76;
                           }
                           break;
                        case 1007120555:
                           if (text.equals("j.moder")) {
                              var12 = 5;
                              break label76;
                           }
                           break;
                        case 1023145917:
                           if (text.equals("админ")) {
                              break label75;
                           }
                           break;
                        case 1113750001:
                           if (text.equals("модератор")) {
                              break label73;
                           }
                           break;
                        case 1902965990:
                           if (text.equals("gl.moder")) {
                              var12 = 2;
                              break label76;
                           }
                     }

                     var12 = 11;
                     break label76;
                  }

                  var12 = 4;
                  break label76;
               }

               var12 = 3;
               break label76;
            }

            var12 = 0;
         }

         byte byteValue2 = var12;
         if (byteValue2 < byteValue) {
            byteValue = byteValue2;
         }
      }

      return byteValue;
   }

   public static final int computeValue(ChatStaffScanner chatStaffScanner, PlayerListNames playerListNames) {
      return chatStaffScanner.computeValue(playerListNames);
   }

   public static final boolean testChatStaffScanner(ChatStaffScanner chatStaffScanner, String text) {
      return chatStaffScanner.checkAnyMatch(text);
   }

   public static final CopyOnWriteArrayList getItems() {
      return items;
   }

   public static final void updateChatStaffScanner(ChatStaffScanner chatStaffScanner, String[] texts) {
      chatStaffScanner.updateTexts(texts);
   }

   static {
      String[] texts2 = new String[]{
         "админ",
         "admin",
         "ml.admin",
         "moder",
         "st.moder",
         "s.moder",
         "gl.moder",
         "j.moder",
         "модератор",
         "модер",
         "st.helper",
         "хелпер",
         "helper",
         "helper",
         "yt"
      };
      texts = texts2;
   }

   public static final class Ordering implements Comparator {
      public Ordering() {
      }

      @Override
      public final int compare(Object object, Object object2) {
         PlayerListNames object3 = (PlayerListNames)object;
         Comparable value = ChatStaffScanner.computeValue(ChatStaffScanner.INSTANCE, object3);
         object3 = (PlayerListNames)object2;
         Comparable comparable = value;
         return ComparisonsKt.compareValues(comparable, ChatStaffScanner.computeValue(ChatStaffScanner.INSTANCE, object3));
      }
   }

   public static final class ScanTask implements Runnable {
      public final int value;
      public final PlayerListNames playerListNames;
      public final String[] texts;

      public ScanTask(int value, PlayerListNames playerListNames, String[] texts) {
         this.value = value;
         this.playerListNames = playerListNames;
         this.texts = texts;
      }

      @Override
      public final void run() {
         try {
            Thread.sleep(this.value);
         } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return;
         }
         if (ChatStaffScanner.testChatStaffScanner(ChatStaffScanner.INSTANCE, this.playerListNames.getText())
            && ChatStaffScanner.getItems().add(this.playerListNames)) {
            ChatStaffScanner.updateChatStaffScanner(ChatStaffScanner.INSTANCE, this.texts);
         }
      }
   }
}