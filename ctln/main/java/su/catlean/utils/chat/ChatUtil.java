// deobf by pusia
package su.catlean.utils.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import kotlin.text.StringsKt;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.modules.Module;
import su.catlean.client.services.LocalizationManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.utils.MinecraftHolder;

public final class ChatUtil {
   public static final void updateModule(Module self, String message, MessageType type, int value, ItemStack itemStack) {
      NotificationManager.INSTANCE.applyExecute(self.getText3(), message, value, type, itemStack);
   }

   public static void defaultImpl(Module module, String text, MessageType messageType, int value, ItemStack itemStack, int value2, Object object) {
      if ((value2 & 2) != 0) {
         messageType = MessageType.INFO;
      }

      if ((value2 & 4) != 0) {
         value = 2;
      }

      if ((value2 & 8) != 0) {
         itemStack = null;
      }

      updateModule(module, text, messageType, value, itemStack);
   }

   public static final void applyExecuteIfPossible(Module self, String message, boolean flag) {
      if (flag) {
         MinecraftHolder.getClientPacketListener().sendChat(message);
      } else {
         ChatFormatting green = ChatFormatting.GREEN;
         ChatFormatting gray = ChatFormatting.GRAY;
         ChatFormatting gold = ChatFormatting.GOLD;
         String text = StringsKt.trim(self.getText3()).toString();
         ChatFormatting gray2 = ChatFormatting.GRAY;
         String text2 = text;
         ChatFormatting chatFormatting = gold;
         ChatFormatting chatFormatting2 = gray;
         Component nullToEmpty = Component.nullToEmpty(green + "> " + chatFormatting2 + "[" + chatFormatting + text2 + gray2 + "] " + message);
         if (MinecraftHolder.getMinecraft().isSameThread()) {
            MinecraftHolder.getLocalPlayer().sendSystemMessage(nullToEmpty);
         } else {
            MinecraftHolder.getMinecraft().executeIfPossible(() -> MinecraftHolder.getLocalPlayer().sendSystemMessage(nullToEmpty));
         }
      }
   }

   public static void defaultImpl(Module module, String text, boolean flag, int value, Object object) {
      if ((value & 2) != 0) {
         flag = false;
      }

      applyExecuteIfPossible(module, text, flag);
   }

   @NotNull
   public static final String computeText(Module self, String message, Object[] args, boolean flag) {
      String text = "modules." + (flag ? "common" : self.getCategory().getId() + "." + self.getId()) + "." + message;
      LocalizationManager instance = LocalizationManager.INSTANCE;
      Object[] args2 = args;
      var arrayList = new ArrayList(args.length);
      int value = 0;

      for (int i = args.length; value < i; value++) {
         Object object = args2[value];
         String text2 = "null cannot be cast to non-null type java.lang.Object";
         arrayList.add(object);
      }

      List list = arrayList;
      Collection collection = list;
      Object[] array = collection.toArray(new Object[0]);
      return instance.computeText(text, Arrays.copyOf(array, array.length));
   }

   public static String defaultImpl(Module module, String text, Object[] objects, boolean flag, int value, Object object) {
      if ((value & 4) != 0) {
         flag = false;
      }

      return computeText(module, text, objects, flag);
   }
}