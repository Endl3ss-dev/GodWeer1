// deobf by pusia
package su.catlean.client.commands.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.File;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OS;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.ServiceConfig;
import su.catlean.api.setting.Setting;
import su.catlean.client.commands.Command;
import su.catlean.client.commands.args.ConfigArgumentType;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.ui.panel.components.ConfigType;
import su.catlean.client.services.ConfigFileHelper;
import su.catlean.client.services.LocalizationManager;
import su.catlean.client.services.ModuleManager;
import su.catlean.client.services.proxy.ProxyManager;

public final class ConfigCommand extends Command {
   @NotNull
   public static final ConfigCommand INSTANCE = new ConfigCommand();

   private ConfigCommand() {
      super("cfg");
   }

   @Override
   public void applyExecutes(LiteralArgumentBuilder builder) {
      builder.then(
         this.computeLiteralArgumentBuilder("list")
            .executes(
               arg -> {
                  StringBuilder stringBuilder = new StringBuilder(LocalizationManager.INSTANCE.computeText("commands.config.configs", new Object[0]));

                  for (ServiceConfig item : (Iterable<ServiceConfig>)ProxyManager.INSTANCE.getModuleDataSource().getItems()) {
                     stringBuilder.append(ChatFormatting.RESET)
                        .append("\n" + (Intrinsics.areEqual(item, ProxyManager.INSTANCE.getModuleDataSource().getServiceConfig()) ? ChatFormatting.GREEN : ""))
                        .append(item.getId())
                        .append(ChatFormatting.RESET);
                  }

                  INSTANCE.updateText(stringBuilder.toString());
                  return 1;
               }
            )
      );
      builder.then(this.computeLiteralArgumentBuilder("folder").executes(arg2 -> {
         try {
            OS platform = Util.getPlatform();
            File file = ProxyManager.INSTANCE.getModuleDataSource().getFile();
            OS value = platform;
            if (!file.exists()) {
               file.createNewFile();
            }

            value.openFile(file);
         } catch (Exception exception) {
            exception.printStackTrace();
         }

         return 1;
      }));
      builder.then(this.computeLiteralArgumentBuilder("reset").executes(arg3 -> {
         for (Module items2Item : (Iterable<Module>)ModuleManager.INSTANCE.getItems2()) {
            if (items2Item.getEnabled()) {
               items2Item.applyUnplug();
            }

            for (Setting items5Item : (Iterable<Setting>)items2Item.getItems5()) {
               items5Item.update();
            }
         }

         return 1;
      }));
      builder.then(
         this.computeLiteralArgumentBuilder("save")
            .then(this.computeRequiredArgumentBuilder("name", (ArgumentType)StringArgumentType.word()).executes(arg4 -> {
               String argument = (String)arg4.getArgument("name", String.class);
               ConfigFileHelper.applyOrdinal2(argument, ConfigType.MODULES);
               ConfigCommand instance = INSTANCE;
               LocalizationManager instance2 = LocalizationManager.INSTANCE;
               Object[] objects = new Object[]{argument};
               instance.updateText(instance2.computeText("commands.config.saved", objects));
               return 1;
            }))
      );
      builder.then(
         this.computeLiteralArgumentBuilder("load")
            .then(this.computeRequiredArgumentBuilder("name", ConfigArgumentType.companion.getConfigArgumentType()).executes(arg5 -> {
               String argument2 = (String)arg5.getArgument("name", String.class);
               ConfigFileHelper.applyOrdinal(argument2, ConfigType.MODULES);
               ConfigCommand instance3 = INSTANCE;
               LocalizationManager instance4 = LocalizationManager.INSTANCE;
               Object[] objects2 = new Object[]{argument2};
               instance3.updateText(instance4.computeText("commands.config.loaded", objects2));
               return 1;
            }))
      );
      builder.then(
         this.computeLiteralArgumentBuilder("remove")
            .then(this.computeRequiredArgumentBuilder("name", ConfigArgumentType.companion.getConfigArgumentType()).executes(arg6 -> {
               String argument3 = (String)arg6.getArgument("name", String.class);
               ConfigFileHelper.applyDelete(argument3, ConfigType.MODULES);
               ConfigCommand instance5 = INSTANCE;
               LocalizationManager instance6 = LocalizationManager.INSTANCE;
               Object[] objects3 = new Object[]{argument3};
               instance5.updateText(instance6.computeText("commands.config.removed", objects3));
               return 1;
            }))
      );
      builder.executes(arg7 -> {
         INSTANCE.updateText(ChatFormatting.RED.toString() + "cfg <load/remove/save> <id> / cfg list");
         return 1;
      });
   }
}