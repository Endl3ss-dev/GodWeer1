// deobf by pusia
package su.catlean.client.services;

import su.catlean.utils.KotlinCompat;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import kotlin.collections.CollectionsKt;
import kotlin.random.Random;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import kotlinx.serialization.DeserializationStrategy;
import kotlinx.serialization.internal.ArrayListSerializer;
import kotlinx.serialization.internal.StringSerializer;
import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonElement;
import kotlinx.serialization.json.JsonElementKt;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.category.Category;
import su.catlean.api.category.SubCategory;
import su.catlean.api.event.events.client.TickEvent;
import su.catlean.api.service.Service;
import su.catlean.client.features.modules.PromoModule;
import su.catlean.client.features.modules.esp.NameTags;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;

public final class OnlineUsersService implements Service {
   @NotNull
   public static final OnlineUsersService INSTANCE = new OnlineUsersService();
   private static int value = -1;
   private static boolean flag;
   private static boolean flag2;
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   @NotNull
   private static final StopWatch stopWatch2 = new StopWatch();
   @NotNull
   private static CopyOnWriteArrayList items = new CopyOnWriteArrayList();
   @NotNull
   private static CopyOnWriteArrayList items2 = new CopyOnWriteArrayList();
   @NotNull
   private static ConcurrentHashMap<String, Boolean> map = new ConcurrentHashMap();
   @NotNull
   private static ConcurrentHashMap<String, Boolean> map2 = new ConcurrentHashMap();
   @NotNull
   private static volatile List<Object> items3 = CollectionsKt.emptyList();

   private OnlineUsersService() {
   }

   @NotNull
   public final List<Object> getItems3() {
      return items3;
   }

   private final List<Object> getItems() {
      // [scrubbed] remote module injection: fetched https://api.catlean.su/v1/hellhitmodules and
      // registered PromoModule instances (hellhit.fun advertisement). Disabled entirely.
      return CollectionsKt.emptyList();
      /*
      HttpRequest httpRequest = HttpRequest.newBuilder(URI.create("https://api.catlean.su/v1/hellhitmodules")).GET().build();

      List emptyList;
      try {
         HttpResponse<String> send = HttpClient.newHttpClient().send(httpRequest, BodyHandlers.ofString());
         if (send.statusCode() != 200) {
            return CollectionsKt.emptyList();
         }

         Map jsonObject = (Map)JsonElementKt.getJsonObject(JsonProvider.getJson().parseToJsonElement((String)send.body()));
         var arrayList = new ArrayList();

         for (Entry entrySetItem : (Iterable<java.util.Map.Entry>)jsonObject.entrySet()) {
            String key = (String)entrySetItem.getKey();
            JsonElement value2 = (JsonElement)entrySetItem.getValue();
            Iterable items4 = ModuleManager.INSTANCE.getItems();
            var arrayList2 = new ArrayList();

            for (Object object : items4) {
               Category category = (Category)object;
               Iterable subCategories = category.getSubCategories();
               CollectionsKt.addAll(arrayList2, subCategories);
            }

            items4 = arrayList2;
            Iterator iterator = items4.iterator();

            Object promoModule;
            while (true) {
               if (!iterator.hasNext()) {
                  promoModule = null;
                  break;
               }

               arrayList2 = (ArrayList)iterator.next();
               SubCategory subCategory = (SubCategory)(Object)arrayList2;
               if (StringsKt.equals(subCategory.getId(), JsonElementKt.getJsonPrimitive(value2).getContent(), true)) {
                  promoModule = arrayList2;
                  break;
               }
            }

            promoModule = (SubCategory)promoModule;
            if ((SubCategory)promoModule == null) {
               promoModule = null;
            } else {
               SubCategory subCategory2 = (SubCategory)promoModule;
               promoModule = new PromoModule(key, subCategory2);
            }

            if (promoModule != null) {
               PromoModule promoModule2 = (PromoModule)promoModule;
               arrayList.add(promoModule2);
            }
         }

         emptyList = arrayList;
      } catch (Throwable exception) {
         emptyList = CollectionsKt.emptyList();
      }

      return emptyList;
   }
   */
   }

   @Flow
   private final void onTick(TickEvent tickEvent) {
      if (!flag2
            && MinecraftHolder.getMinecraft().getUser() != null
            && MinecraftHolder.getMinecraft().getUser().getName() != null
            && MinecraftHolder.getMinecraft().getUser().getName().length() > 0
         || stopWatch2.testValue2(150000)) {
         flag2 = true;
         ThreadManager instance = ThreadManager.INSTANCE;
         byte byteValue = 0;
         instance.getExecutorService().execute(new OnlineUsersService.Task3(byteValue));
      }

      if (!flag
            && MinecraftHolder.getMinecraft().getUser() != null
            && MinecraftHolder.getMinecraft().getUser().getName() != null
            && MinecraftHolder.getMinecraft().getUser().getName().length() > 0
         || stopWatch.testValue2(30000)) {
         flag = true;
         ThreadManager instance2 = ThreadManager.INSTANCE;
         byte byteValue2 = 0;
         instance2.getExecutorService().execute(new OnlineUsersService.Task(byteValue2));
      }
   }

   public final void applyOfString(String name) {
      // [scrubbed] telemetry heartbeat: POSTed the (XOR "sahur1337" + base64, reversible) encoded
      // username to https://api.catlean.su/v1/add. Removed — no data leaves the client.
   }

   @NotNull
   public final List computeItems(String end) {
      HttpRequest httpRequest = HttpRequest.newBuilder(URI.create("https://api.catlean.su/v1/" + end)).GET().build();

      List arrayList;
      try {
         HttpResponse<String> send = HttpClient.newHttpClient().send(httpRequest, BodyHandlers.ofString());
         List decodeFromString;
         if (send.statusCode() == 200) {
            Json json = JsonProvider.getJson();
            String body = (String)send.body();
            json.getSerializersModule();
            decodeFromString = (List)json.decodeFromString((DeserializationStrategy)(new ArrayListSerializer(StringSerializer.INSTANCE)), body);
         } else {
            decodeFromString = new ArrayList();
         }

         arrayList = decodeFromString;
      } catch (Exception exception) {
         arrayList = new ArrayList();
      }

      return arrayList;
   }

   public final boolean checkContains(String string) {
      String string2 = string + "1f";
      if (map.contains(string2)) {
         return (Boolean)map.get(string2);
      } else {
         String bytes = this.fetchBytes(string2);
         boolean contains = Lists.newArrayList(items.iterator()).contains(bytes);
         map.put(string2, contains);
         return contains;
      }
   }

   public final boolean checkContains2(String string) {
      String string2 = string + "1f";
      if (map2.contains(string2)) {
         return (Boolean)map2.get(string2);
      } else {
         String bytes = this.fetchBytes(string2);
         boolean contains = Lists.newArrayList(items2.iterator()).contains(bytes);
         map2.put(string2, contains);
         return contains;
      }
   }

   private final String fetchBytes(String text) {
      byte[] bytes = text.getBytes(Charsets.UTF_8);
      String text2 = "sahur1337";
      byte[] bytes2 = text2.getBytes(Charsets.UTF_8);
      byte[] byteValues = new byte[bytes.length];
      int value2 = 0;

      for (int i = bytes.length; value2 < i; value2++) {
         byteValues[value2] = (byte)(bytes[value2] ^ bytes2[value2 % bytes2.length]);
      }

      return this.computeText(byteValues);
   }

   private final String computeText(byte[] values) {
      return KotlinCompat.replace(
         KotlinCompat.replace(KotlinCompat.replace(Base64.getEncoder().encodeToString(values), '@', '(', false, 4, null), '*', ')', false, 4, null),
         '^',
         '%',
         false,
         4,
         null
      );
   }

   public static final String computeText(OnlineUsersService onlineUsersService, String text) {
      return onlineUsersService.fetchBytes(text);
   }

   public static final int getValue() {
      return value;
   }

   public static final CopyOnWriteArrayList getItems2() {
      return items;
   }

   public static final CopyOnWriteArrayList getItems4() {
      return items2;
   }

   public static final void setItems3(List items) {
      items3 = items;
   }

   public static final List<Object> computeItems(OnlineUsersService onlineUsersService) {
      return onlineUsersService.getItems();
   }

   static {
      value = Random.Default.nextInt(0, 99999999);
      ThreadManager instance = ThreadManager.INSTANCE;
      byte byteValue = 0;
      instance.getExecutorService().execute(new OnlineUsersService.Task2(byteValue));
   }

      public static final class Task implements Runnable {
      public final int value;

      public Task(int value) {
         this.value = value;
      }

      @Override
      public final void run() {
         try {
            Thread.sleep(this.value);
         } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return;
         }
         List items = OnlineUsersService.INSTANCE.computeItems("online");
         OnlineUsersService.getItems2().clear();
         OnlineUsersService.getItems2().addAll(items);
         List items2 = OnlineUsersService.INSTANCE.computeItems("hhonline");
         OnlineUsersService.getItems4().clear();
         OnlineUsersService.getItems4().addAll(items2);
      }
   }

   public static final class Task2 implements Runnable {
      public final int value;

      public Task2(int value) {
         this.value = value;
      }

      @Override
      public final void run() {
         try {
            Thread.sleep(this.value);
         } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return;
         }
         OnlineUsersService.setItems3(OnlineUsersService.computeItems(OnlineUsersService.INSTANCE));
      }
   }

   public static final class Task3 implements Runnable {
      public final int value;

      public Task3(int value) {
         this.value = value;
      }

      @Override
      public final void run() {
         // [scrubbed] telemetry task: encoded the username (XOR "sahur1337" + base64) and POSTed it
         // to https://api.catlean.su/v1/add. Removed — the client no longer reports anything.
      }
   }
}