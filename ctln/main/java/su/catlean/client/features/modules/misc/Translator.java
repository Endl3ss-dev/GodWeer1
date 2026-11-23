// deobf by pusia
package su.catlean.client.features.modules.misc;

import su.catlean.utils.KotlinCompat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.ThreadManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.chat.ChatUtil;

public final class Translator extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Translator.class, "urLang", "getUrLang()Lsu/catlean/client/features/modules/utility/misc/translator/Translator$Lang;", 0), new PropertyReference1Impl(Translator.class, "outMessages", "getOutMessages()Lsu/catlean/d8;", 0) };
   @NotNull
   public static final Translator INSTANCE = new Translator();
   @NotNull
   private static final EnumSetting yourLanguage = SettingFactory.defaultImpl(INSTANCE, "your-language", Translator.YourLanguage.RU, null, null, 12, null);
   @NotNull
   private static final EnumSetting outMessages = SettingFactory.defaultImpl(INSTANCE, "out-messages", Translator.YourLanguage.UK, null, null, 12, null);
   @NotNull
   private static final List items;
   @Nullable
   private static String text;

   private Translator() {
      super("translator", SubCategories.getSubCategory11(), null, 4, null);
   }

   private final Translator.YourLanguage getUrLang() {
      return (Translator.YourLanguage)yourLanguage.computeObject(this, $$delegatedProperties[0]);
   }

   private final Translator.YourLanguage getOutMessages() {
      return (Translator.YourLanguage)outMessages.computeObject(this, $$delegatedProperties[1]);
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundSystemChatPacket) {
         ClientboundSystemChatPacket packet = (ClientboundSystemChatPacket)receivePacket.getPacket();
         String string = packet.content().getString();

         for (String item : (Iterable<String>)items) {
            if (KotlinCompat.contains(string, item, false, 2, null)) {
               return;
            }
         }

         ThreadManager instance = ThreadManager.INSTANCE;
         byte byteValue = 0;
         instance.getExecutorService().execute(new Translator.Task2(byteValue, string));
      }
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (sendPacket.getPacket() instanceof ServerboundChatPacket) {
         ServerboundChatPacket packet = (ServerboundChatPacket)sendPacket.getPacket();
         if (Objects.equals(packet.message(), text)) {
            return;
         }

         if (Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getMainHandItem().getItem(), Items.FILLED_MAP)
            || Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getOffhandItem().getItem(), Items.FILLED_MAP)) {
            return;
         }

         if (KotlinCompat.startsWith(packet.message(), "/", false, 2, null)) {
            return;
         }

         ThreadManager instance = ThreadManager.INSTANCE;
         byte byteValue = 0;
         instance.getExecutorService().execute(new Translator.Task(byteValue, packet));
         sendPacket.cancel();
      }
   }

   private final String computeText(String text, String text2) throws UnsupportedEncodingException, MalformedURLException {
      StringBuilder stringBuilder = new StringBuilder();
      String text3 = "https://translate.google.com/m?hl=en&sl=auto&tl=%s&ie=UTF-8&prev=_m&q=%s";
      Object[] objects = new Object[]{text2, null};
      byte byteValue = 1;
      CharSequence text4 = text;
      int value = 0;
      int length = text4.length() - 1;
      boolean flag2 = false;

      while (value <= length) {
         int value2 = !flag2 ? value : length;
         char charAt = text4.charAt(value2);
         boolean flag3 = charAt <= ' ';
         if (!flag2) {
            if (!flag3) {
               flag2 = true;
            } else {
               value++;
            }
         } else {
            if (!flag3) {
               break;
            }

            length--;
         }
      }

      String text5 = text4.subSequence(value, length + 1).toString();
      objects[byteValue] = URLEncoder.encode(text5, "UTF-8");
      URI uri = URI.create(String.format(text3, Arrays.copyOf(objects, objects.length)));

      try {
         var bufferedReader = new BufferedReader(new InputStreamReader(uri.toURL().openStream(), "UTF-8"));
         Throwable throwable = null;

         try {
            BufferedReader bufferedReader2 = bufferedReader;

            while (true) {
               String line = bufferedReader2.readLine();
               if (line == null) {
                  Unit instance = Unit.INSTANCE;
                  break;
               }

               stringBuilder.append(line + "\n");
            }
         } catch (Throwable exception) {
            throwable = exception;
            throw exception;
         } finally {
            CloseableKt.closeFinally(bufferedReader, throwable);
         }
      } catch (IOException exception2) {
         exception2.printStackTrace();
      }

      Matcher matcher = Pattern.compile("class=\"result-container\">([^<]*)</div>", 8).matcher(stringBuilder);
      matcher.find();
      String group = matcher.group(1);
      return group.length() == 0 ? "translation failed" : StringEscapeUtils.unescapeHtml4(group);
   }

   public static final String computeText(Translator translator, String text, String text2) {
      try {
         return translator.computeText(text, text2);
      } catch (java.io.UnsupportedEncodingException | java.net.MalformedURLException exception) {
         throw new RuntimeException(exception);
      }
   }

   public static final Translator.YourLanguage computeYourLanguage(Translator translator) {
      return translator.getUrLang();
   }

   public static final Translator.YourLanguage computeYourLanguage2(Translator translator) {
      return translator.getOutMessages();
   }

   public static final void setText(String text) {
      Translator.text = text;
   }

   static {
      String[] texts = new String[]{"was killed by", "was destroyed by", "has joined a game", "has left the game"};
      items = CollectionsKt.listOf(texts);
   }

   public static final class Task implements Runnable {
      public final int value;
      public final ServerboundChatPacket serverboundChatPacket;

      public Task(int value, ServerboundChatPacket serverboundChatPacket) {
         this.value = value;
         this.serverboundChatPacket = serverboundChatPacket;
      }

      @Override
      public final void run() {
         try {
            Thread.sleep(this.value);
         } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return;
         }

         try {
            String text = Translator.computeText(
               Translator.INSTANCE, this.serverboundChatPacket.message(), Translator.computeYourLanguage2(Translator.INSTANCE).name().toLowerCase(Locale.ROOT)
            );
            if (Objects.equals(this.serverboundChatPacket.message(), text)) {
               Translator.setText(this.serverboundChatPacket.message());
               ChatUtil.applyExecuteIfPossible(Translator.INSTANCE, this.serverboundChatPacket.message(), true);
            } else {
               Translator.setText(text);
               ChatUtil.applyExecuteIfPossible(Translator.INSTANCE, text, true);
            }
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   public static final class Task2 implements Runnable {
      public final int value;
      public final String text;

      public Task2(int value, String text) {
         this.value = value;
         this.text = text;
      }

      @Override
      public final void run() {
         try {
            Thread.sleep(this.value);
         } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return;
         }

         try {
            ChatUtil.defaultImpl(
               Translator.INSTANCE,
               ChatFormatting.WHITE.toString()
                  + Translator.computeText(Translator.INSTANCE, this.text, Translator.computeYourLanguage(Translator.INSTANCE).name().toLowerCase(Locale.ROOT)),
               false,
               2,
               null
            );
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   public static enum YourLanguage {
      AM,
      AR,
      EU,
      BN,
      BG,
      CA,
      CHR,
      HR,
      CS,
      DA,
      NL,
      EN,
      ET,
      FIL,
      FI,
      FR,
      DE,
      EL,
      GU,
      IW,
      HI,
      HU,
      IS,
      ID,
      IT,
      JA,
      KN,
      KO,
      LV,
      LT,
      MS,
      ML,
      MR,
      NO,
      PL,
      RO,
      RU,
      SR,
      SK,
      SL,
      ES,
      SW,
      SV,
      TA,
      TE,
      TH,
      TR,
      UR,
      UK,
      VI,
      CY,
      CN;

      private static final Translator.YourLanguage[] yourLanguages = $values();
      private static final EnumEntries<Translator.YourLanguage> enumEntries = EnumEntriesKt.enumEntries(yourLanguages);

      private YourLanguage() {
      }

      @NotNull
      public static EnumEntries<Translator.YourLanguage> getEnumEntries() {
         return enumEntries;
      }

      private static final Translator.YourLanguage[] $values() {
         return new Translator.YourLanguage[]{
            AM,
            AR,
            EU,
            BN,
            BG,
            CA,
            CHR,
            HR,
            CS,
            DA,
            NL,
            EN,
            ET,
            FIL,
            FI,
            FR,
            DE,
            EL,
            GU,
            IW,
            HI,
            HU,
            IS,
            ID,
            IT,
            JA,
            KN,
            KO,
            LV,
            LT,
            MS,
            ML,
            MR,
            NO,
            PL,
            RO,
            RU,
            SR,
            SK,
            SL,
            ES,
            SW,
            SV,
            TA,
            TE,
            TH,
            TR,
            UR,
            UK,
            VI,
            CY,
            CN
         };
      }
   }
}