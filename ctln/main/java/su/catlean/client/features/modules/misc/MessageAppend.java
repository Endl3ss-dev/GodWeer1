// deobf by pusia
package su.catlean.client.features.modules.misc;

import su.catlean.utils.KotlinCompat;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.random.Random;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.StringSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.CommandManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class MessageAppend extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(MessageAppend.class, "prefix", "getPrefix()Ljava/lang/String;", 0), new PropertyReference1Impl(MessageAppend.class, "postfix", "getPostfix()Ljava/lang/String;", 0), new PropertyReference1Impl(MessageAppend.class, "emoji", "getEmoji()Z", 0) };
   @NotNull
   public static final MessageAppend INSTANCE = new MessageAppend();
   @NotNull
   private static final StringSetting prefix = SettingFactory.defaultImpl(INSTANCE, "prefix", "> ", null, null, 12, null);
   @NotNull
   private static final StringSetting postfix = SettingFactory.defaultImpl(INSTANCE, "postfix", " [CatLean]", null, null, 12, null);
   @NotNull
   private static final BooleanSetting emoji = SettingFactory.defaultImpl(INSTANCE, "emoji", true, null, null, 12, null);
   @NotNull
   private static final String[] texts;
   @Nullable
   private static String text;

   private MessageAppend() {
      super("message-append", SubCategories.getSubCategory11(), null, 4, null);
   }

   private final String getPrefix() {
      return (String)prefix.computeObject(this, $$delegatedProperties[0]);
   }

   private final String getPostfix() {
      return (String)postfix.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getEmoji() {
      return (Boolean)emoji.computeObject(this, $$delegatedProperties[2]);
   }

   @NotNull
   public final String[] getTexts() {
      return texts;
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (MinecraftHolder.getMinecraft().player != null && MinecraftHolder.getMinecraft().level != null) {
         if (sendPacket.getPacket() instanceof ServerboundChatPacket) {
            String message = ((ServerboundChatPacket)sendPacket.getPacket()).message;
            if (Intrinsics.areEqual(message, text)) {
               return;
            }

            if (Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getMainHandItem().getItem(), Items.FILLED_MAP)
               || Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getOffhandItem().getItem(), Items.FILLED_MAP)) {
               return;
            }

            if (message.length() < 7) {
               CharSequence charSequence = message;
               int value = 0;

               for (int i = 0; i < charSequence.length(); i++) {
                  char charAt = charSequence.charAt(i);
                  if (Character.isDigit(charAt)) {
                     value++;
                  }
               }

               if (value >= 3) {
                  return;
               }
            }

            if (KotlinCompat.startsWith(message, "/", false, 2, null)
               || KotlinCompat.startsWith(message, CommandManager.INSTANCE.getText(), false, 2, null)) {
               return;
            }

            boolean flag = false;
            if (KotlinCompat.startsWith(message, "!", false, 2, null)) {
               message = message.substring(1);
               flag = true;
            }

            text = (flag ? "!" : "")
               + this.getPrefix()
               + message
               + this.getPostfix()
               + (this.getEmoji() ? " " + ArraysKt.random(texts, (Random)Random.Default) : "");
            MinecraftHolder.getLocalPlayer().connection.sendChat(text);
            sendPacket.cancel();
         }
      }
   }

   static {
      String[] texts2 = new String[]{
         "^_^",
         "^-^",
         "^o^",
         "^O^",
         "o_o",
         "O_O",
         "0_0",
         "x_x",
         ">_<",
         "-_-",
         ";_;",
         "T_T",
         "=_=",
         "v_v",
         "xD",
         "o.O",
         "O.o",
         "._.",
         "o_o;",
         "O_O;",
         ">:(",
         ":)",
         ":D",
         ":P",
         ":o",
         ":O",
         ":3",
         ":|",
         ":/",
         ":*",
         ";)",
         ">:D",
         ">:3c",
         "XD",
         "xP",
         "^_^",
         "(^-^)",
         "(>_<)",
         "(T_T)",
         "(U3O)",
         "o_o",
         "O_O",
         "0_0",
         "(v_v)",
         "(x_x)",
         "(^o^)",
         "(^O^)",
         "(;_;)",
         "(=_=)",
         "(^3^)",
         "(^w^)",
         "UwU",
         "OwO",
         ">w<",
         "xD",
         ";D",
         "(>o<)",
         "(._.)",
         "('~')",
         "(*_*)",
         "(¬_¬)",
         "(•_•)",
         "(°_°)",
         "(>_<)b"
      };
      texts = texts2;
   }
}