// deobf by pusia
package su.catlean.client.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.ChatFormatting;
import su.catlean.api.data.ServiceConfig;
import su.catlean.api.data.WidgetConfig;
import su.catlean.client.commands.impl.BindCommand;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.ui.panel.components.ConfigType;
import su.catlean.client.services.proxy.ProxyManager;
import su.catlean.client.services.sources.ModuleDataSource;
import su.catlean.client.services.sources.WidgetDataSource;
import su.catlean.utils.KeyNames;

public final class ConfigFileHelper {
   public static final void applyOrdinal(String id, ConfigType type) {
      switch (ConfigFileHelper.WhenMappings.values[type.ordinal()]) {
         case 1:
            ModuleManager instance = ModuleManager.INSTANCE;
            boolean flag = false;
            ProxyManager.INSTANCE
               .getModuleDataSource()
               .applyRemoveIf(instance.getItems2(), ProxyManager.INSTANCE.getSharedDataSource().getSharedConfig().getModulesConfig());
            ServiceConfig serviceConfig = ProxyManager.INSTANCE.getModuleDataSource().getServiceConfig();
            ProxyManager.INSTANCE.getSharedDataSource().getSharedConfig().updateText(id);
            Iterable items = ProxyManager.INSTANCE.getModuleDataSource().getItems();
            ModuleDataSource moduleDataSource = ProxyManager.INSTANCE.getModuleDataSource();
            boolean flag2 = false;
            Iterator iterator = items.iterator();

            Object object;
            while (true) {
               if (!iterator.hasNext()) {
                  object = null;
                  break;
               }

               Object object2 = iterator.next();
               ServiceConfig serviceConfig2 = (ServiceConfig)object2;
               if (Intrinsics.areEqual(serviceConfig2.getId(), id)) {
                  object = object2;
                  break;
               }
            }

            ServiceConfig serviceConfig3 = (ServiceConfig)object;
            if ((ServiceConfig)object == null) {
               return;
            }

            moduleDataSource.updateServiceConfig(serviceConfig3);
            instance.update();
            instance.applyParseBoolean(serviceConfig);
            break;
         case 2:
            WidgetManager instance2 = WidgetManager.INSTANCE;
            boolean flag3 = false;
            ProxyManager.INSTANCE
               .getWidgetDataSource()
               .applyRemoveIf(instance2.getItems(), ProxyManager.INSTANCE.getSharedDataSource().getSharedConfig().getWidgetsConfig());
            ProxyManager.INSTANCE.getSharedDataSource().getSharedConfig().updateText2(id);
            Iterable items2 = ProxyManager.INSTANCE.getWidgetDataSource().getItems();
            WidgetDataSource widgetDataSource = ProxyManager.INSTANCE.getWidgetDataSource();
            boolean flag4 = false;
            Iterator iterator2 = items2.iterator();

            Object object3;
            while (true) {
               if (!iterator2.hasNext()) {
                  object3 = null;
                  break;
               }

               Object object4 = iterator2.next();
               WidgetConfig widgetConfig = (WidgetConfig)object4;
               if (Intrinsics.areEqual(widgetConfig.getId(), id)) {
                  object3 = object4;
                  break;
               }
            }

            WidgetConfig widgetConfig2 = (WidgetConfig)object3;
            if ((WidgetConfig)object3 == null) {
               return;
            }

            widgetDataSource.updateWidgetConfig(widgetConfig2);
            instance2.update();
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      BindCommand.INSTANCE.updateText(ChatFormatting.WHITE.toString() + "Key Binds:");
      Iterable<Module> items22 = ModuleManager.INSTANCE.getItems2();
      var arrayList = new ArrayList();

      for (Object object5 : items22) {
         Module module = (Module)object5;
         if (module.getBind().getValue() != -1) {
            arrayList.add(object5);
         }
      }

      for (Module module2 : (Iterable<Module>)arrayList) {
         BindCommand instance3 = BindCommand.INSTANCE;
         String text3 = module2.getText3();
         ChatFormatting white = ChatFormatting.WHITE;
         String text = KeyNames.INSTANCE.computeText(module2.getBind().getValue());
         ChatFormatting chatFormatting = white;
         instance3.updateText(text3 + "- " + chatFormatting + text);
      }
   }

   public static final void applyDelete(String id, ConfigType type) {
      switch (ConfigFileHelper.WhenMappings.values[type.ordinal()]) {
         case 1:
            if (Intrinsics.areEqual(id, ProxyManager.INSTANCE.getModuleDataSource().getServiceConfig().getId())) {
               applyOrdinal(((ServiceConfig)CollectionsKt.first(ProxyManager.INSTANCE.getModuleDataSource().getItems())).getId(), type);
            } else if (ProxyManager.INSTANCE.getModuleDataSource().getItems().size() == 1) {
               applyOrdinal2("default", type);
               applyOrdinal("default", type);
            }

            ProxyManager.INSTANCE.getModuleDataSource().getItems().removeIf(arg -> Intrinsics.areEqual(((ServiceConfig)arg).getId(), id));
            new File(ProxyManager.INSTANCE.getModuleDataSource().getFile(), id + ".ctlon").delete();
            break;
         case 2:
            if (Intrinsics.areEqual(id, ProxyManager.INSTANCE.getWidgetDataSource().getWidgetConfig().getId())) {
               applyOrdinal(((WidgetConfig)CollectionsKt.first(ProxyManager.INSTANCE.getWidgetDataSource().getItems())).getId(), type);
            } else if (ProxyManager.INSTANCE.getWidgetDataSource().getItems().size() == 1) {
               applyOrdinal2("default", type);
               applyOrdinal("default", type);
            }

            ProxyManager.INSTANCE.getWidgetDataSource().getItems().removeIf(arg2 -> Intrinsics.areEqual(((WidgetConfig)arg2).getId(), id));
            new File(ProxyManager.INSTANCE.getWidgetDataSource().getFile(), id + ".ctlon").delete();
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }
   }

   public static final void applyOrdinal2(String id, ConfigType type) {
      switch (ConfigFileHelper.WhenMappings.values[type.ordinal()]) {
         case 1:
            ModuleDataSource moduleDataSource = ProxyManager.INSTANCE.getModuleDataSource();
            moduleDataSource.applyRemoveIf(ModuleManager.INSTANCE.getItems2(), id);
            moduleDataSource.applyEncodeToString();
            break;
         case 2:
            WidgetDataSource widgetDataSource = ProxyManager.INSTANCE.getWidgetDataSource();
            widgetDataSource.applyRemoveIf(WidgetManager.INSTANCE.getItems(), id);
            widgetDataSource.applyEncodeToString();
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[ConfigType.values().length];

         try {
            values2[ConfigType.MODULES.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[ConfigType.WIDGETS.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         values = values2;
      }
   }
}