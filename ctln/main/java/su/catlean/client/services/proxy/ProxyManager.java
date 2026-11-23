// deobf by pusia
package su.catlean.client.services.proxy;

import su.catlean.utils.KotlinCompat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.random.Random;
import kotlin.text.StringsKt;
import kotlinx.serialization.DeserializationStrategy;
import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonKt;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Proxy;
import su.catlean.api.data.ProxyConfig;
import su.catlean.api.data.ServiceConfig;
import su.catlean.api.event.events.FilesDraggedEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.service.Service;
import su.catlean.api.service.sources.DataSource;
import su.catlean.client.features.ui.panel.PanelScreen;
import su.catlean.client.features.ui.panel.panels.ConfigPanel;
import su.catlean.client.features.ui.panel.panels.ProxyPanel;
import su.catlean.client.services.ClientDirectories;
import su.catlean.client.services.LocalizationManager;
import su.catlean.client.services.ModuleManager;
import su.catlean.client.services.ThreadManager;
import su.catlean.client.services.WidgetManager;
import su.catlean.client.services.sources.FriendsDataSource;
import su.catlean.client.services.sources.InventoryDataSource;
import su.catlean.client.services.sources.MacroDataSource;
import su.catlean.client.services.sources.ModuleDataSource;
import su.catlean.client.services.sources.ProxyDataSource;
import su.catlean.client.services.sources.SharedDataSource;
import su.catlean.client.services.sources.ThemeDataSource;
import su.catlean.client.services.sources.WaypointDataSource;
import su.catlean.client.services.sources.WidgetDataSource;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;

public final class ProxyManager implements Service {
   @NotNull
   public static final ProxyManager INSTANCE = new ProxyManager();
   @NotNull
   private static final Json json = KotlinCompat.Json(null, arg -> {
      arg.setPrettyPrint(true);
      arg.setIgnoreUnknownKeys(true);
      arg.setEncodeDefaults(true);
      return Unit.INSTANCE;
   }, 1, null);
   @NotNull
   private static final SharedDataSource sharedDataSource = new SharedDataSource(json);
   @NotNull
   private static final WidgetDataSource widgetDataSource = new WidgetDataSource(json, sharedDataSource);
   @NotNull
   private static final ModuleDataSource moduleDataSource = new ModuleDataSource(json, sharedDataSource);
   @NotNull
   private static final ThemeDataSource themeDataSource = new ThemeDataSource(json, sharedDataSource);
   @NotNull
   private static final WaypointDataSource waypointDataSource = new WaypointDataSource(json);
   @NotNull
   private static final ProxyDataSource proxyDataSource = new ProxyDataSource(json);
   @NotNull
   private static final MacroDataSource macroDataSource = new MacroDataSource(json);
   @NotNull
   private static final FriendsDataSource friendsDataSource = new FriendsDataSource(json);
   @NotNull
   private static final InventoryDataSource inventoryDataSource = new InventoryDataSource(json, sharedDataSource);
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();

   private ProxyManager() {
   }

   @NotNull
   public final SharedDataSource getSharedDataSource() {
      return sharedDataSource;
   }

   @NotNull
   public final WidgetDataSource getWidgetDataSource() {
      return widgetDataSource;
   }

   @NotNull
   public final ModuleDataSource getModuleDataSource() {
      return moduleDataSource;
   }

   @NotNull
   public final ThemeDataSource getThemeDataSource() {
      return themeDataSource;
   }

   @NotNull
   public final WaypointDataSource getWaypointDataSource() {
      return waypointDataSource;
   }

   @NotNull
   public final ProxyDataSource getProxyDataSource() {
      return proxyDataSource;
   }

   @NotNull
   public final MacroDataSource getMacroDataSource() {
      return macroDataSource;
   }

   @NotNull
   public final FriendsDataSource getFriendsDataSource() {
      return friendsDataSource;
   }

   @NotNull
   public final InventoryDataSource getInventoryDataSource() {
      return inventoryDataSource;
   }

   private final void applyGetDeclaredFields() {
      File[] files = ClientDirectories.getFiles();
      int value = 0;

      for (int i = files.length; value < i; value++) {
         File file = files[value];
         file.mkdirs();
      }

      Object[] declaredFields = this.getClass().getDeclaredFields();
      Object[] objects = declaredFields;
      var arrayList = new ArrayList();
      int value2 = 0;

      for (int i2 = declaredFields.length; value2 < i2; value2++) {
         Object object = objects[value2];
         Field field = (Field)object;
         if (DataSource.class.isAssignableFrom(field.getType())) {
            arrayList.add(object);
         }
      }

      for (Object object2 : arrayList) {
         Field field2 = (Field)object2;
         field2.setAccessible(true);
         try {
            ((DataSource)field2.get(INSTANCE)).applyCreateNewFile();
         } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
         }
      }
   }

   public final void applyGetDeclaredFields2() {
      moduleDataSource.applyRemoveIf(ModuleManager.INSTANCE.getItems2(), moduleDataSource.getServiceConfig().getId());
      widgetDataSource.applyRemoveIf(WidgetManager.INSTANCE.getItems(), widgetDataSource.getWidgetConfig().getId());
      Object[] declaredFields = this.getClass().getDeclaredFields();
      Object[] objects = declaredFields;
      var arrayList = new ArrayList();
      int value = 0;

      for (int i = declaredFields.length; value < i; value++) {
         Object object = objects[value];
         Field field = (Field)object;
         if (DataSource.class.isAssignableFrom(field.getType())) {
            arrayList.add(object);
         }
      }

      for (Object object2 : arrayList) {
         Field field2 = (Field)object2;
         field2.setAccessible(true);
         try {
            ((DataSource)field2.get(INSTANCE)).applyEncodeToString();
         } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
         }
      }
   }

   @Flow
   private final void onFilesDragged(FilesDraggedEvent filesDraggedEvent) {
      File file = new File(CollectionsKt.first(filesDraggedEvent.getPaths()).toString());
      if (MinecraftHolder.getMinecraft().gui.screen() instanceof PanelScreen && ProxyPanel.INSTANCE.getDialog() != null) {
         if (KotlinCompat.contains(file.getName(), ".txt", false, 2, null)) {
            try {
               var bufferedReader = new BufferedReader(new FileReader(file));
               Throwable throwable = null;

               try {
                  BufferedReader bufferedReader2 = bufferedReader;

                  while (bufferedReader2.ready()) {
                     List take;
                     label135: {
                        CharSequence line = bufferedReader2.readLine();
                        String[] texts = new String[]{":"};
                        List<String> splitdefault = KotlinCompat.split(line, texts, false, 0, 6, null);
                        if (!splitdefault.isEmpty()) {
                           ListIterator<String> listIterator = splitdefault.listIterator(splitdefault.size());

                           while (listIterator.hasPrevious()) {
                              String previous = (String)listIterator.previous();
                              if (previous.length() != 0) {
                                 take = CollectionsKt.take(splitdefault, listIterator.nextIndex() + 1);
                                 break label135;
                              }
                           }
                        }

                        take = CollectionsKt.emptyList();
                     }

                     Collection collection = take;
                     String[] array = (String[])collection.toArray(new String[0]);
                     ProxyChecker instance = ProxyChecker.INSTANCE;
                     String text = "proxy_" + (int)(Random.Default.nextFloat() * 10000);
                     String text2 = array[0];
                     Integer intOrNull = StringsKt.toIntOrNull(array[1]);
                     Proxy proxy = new Proxy(text, text2, intOrNull != null ? intOrNull : 80, array[2], array[3], false, 0, 64, null);
                     instance.testProxy(proxy);
                  }

                  Unit instance2 = Unit.INSTANCE;
               } catch (Throwable exception) {
                  throwable = exception;
                  throw exception;
               } finally {
                  CloseableKt.closeFinally(bufferedReader, throwable);
               }

               ProxyPanel.INSTANCE.setDialog(null);
               ProxyPanel.INSTANCE.applyClear();
            } catch (Exception exception2) {
               LocalPlayer player = MinecraftHolder.getMinecraft().player;
               if (player != null) {
                  player.sendSystemMessage(Component.nullToEmpty(LocalizationManager.INSTANCE.computeText("gui.proxy.import-error", new Object[0])));
               }
            }
         }

         if (KotlinCompat.contains(file.getName(), ".ctlon", false, 2, null)) {
            try {
               List proxies = proxyDataSource.getProxyConfig().getProxies();
               Json json2 = json;
               String textdefault = KotlinCompat.readText(file, null, 1, null);
               json2.getSerializersModule();
               proxies.addAll(((ProxyConfig)json2.decodeFromString((DeserializationStrategy)ProxyConfig.companion.getKSerializer(), textdefault)).getProxies());
               ProxyPanel.INSTANCE.setDialog(null);
               ProxyPanel.INSTANCE.applyClear();
            } catch (Exception exception3) {
               LocalPlayer player2 = MinecraftHolder.getMinecraft().player;
               if (player2 != null) {
                  player2.sendSystemMessage(Component.nullToEmpty(LocalizationManager.INSTANCE.computeText("gui.proxy.import-error", new Object[0])));
               }
            }

            return;
         }
      }

      if (KotlinCompat.contains(file.getName(), ".ctlon", false, 2, null)) {
         try {
            List<Object> items = moduleDataSource.getItems();
            Json json3 = json;
            String textdefault2 = KotlinCompat.readText(file, null, 1, null);
            json3.getSerializersModule();
            items.add(json3.decodeFromString((DeserializationStrategy)ServiceConfig.companion.getKSerializer(), textdefault2));
         } catch (Exception exception4) {
            exception4.printStackTrace();
         }

         ConfigPanel.INSTANCE.applyClear();
      }
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      StopWatch stopWatch2 = stopWatch;
      int value = MinecraftHolder.getMinecraft().gui.screen() instanceof PanelScreen ? 2500 : '\uea60';
      if (stopWatch2.testValue2(value)) {
         ThreadManager instance = ThreadManager.INSTANCE;
         byte byteValue = 0;
         instance.getExecutorService().execute(new ProxyManager.Task(byteValue));
      }
   }

   static {
      INSTANCE.applyGetDeclaredFields();
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
         ProxyManager.INSTANCE.applyGetDeclaredFields2();
         File[] files = ClientDirectories.getFiles();
         int value2 = 0;

         for (int i = files.length; value2 < i; value2++) {
            File file = files[value2];
            file.mkdirs();
         }

         ProxyManager.INSTANCE.getModuleDataSource().applyCreateNewFile();
         ProxyManager.INSTANCE.getWidgetDataSource().applyCreateNewFile();
         ConfigPanel.INSTANCE.update();
      }
   }
}