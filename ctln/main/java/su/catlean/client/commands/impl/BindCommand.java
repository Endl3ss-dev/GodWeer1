// deobf by pusia
package su.catlean.client.commands.impl;

import su.catlean.utils.KotlinCompat;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.ArrayList;
import java.util.Locale;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.values.Bind;
import su.catlean.client.commands.Command;
import su.catlean.client.commands.args.BindArgumentType;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.misc.ClientInterface;
import su.catlean.client.services.LocalizationManager;
import su.catlean.client.services.ModuleManager;
import su.catlean.utils.KeyNames;
import su.catlean.utils.chat.ComponentUtil;

public final class BindCommand extends Command {
   @NotNull
   public static final BindCommand INSTANCE = new BindCommand();

   private BindCommand() {
      super("bind");
   }

   @Override
   public void applyExecutes(LiteralArgumentBuilder builder) {
      builder.then(
         this.computeRequiredArgumentBuilder("module", BindArgumentType.companion.getBindArgumentType())
            .then(
               this.computeRequiredArgumentBuilder("key", (ArgumentType)StringArgumentType.word())
                  .executes(
                     arg -> {
                        Module argument = (Module)arg.getArgument("module", Module.class);
                        String argument2 = (String)arg.getArgument("key", String.class);
                        if (argument2 == null) {
                           INSTANCE.updateText(LocalizationManager.INSTANCE.computeText("commands.bind.no-such-button", new Object[0]));
                           return 1;
                        } else {
                           int value;
                           if (!StringsKt.equals(argument2, "none", true) && !StringsKt.equals(argument2, "null", true)) {
                              int value2;
                              try {
                                 value2 = InputConstants.getKey("key.keyboard." + argument2.toLowerCase(Locale.ROOT)).getValue();
                              } catch (NumberFormatException exception) {
                                 INSTANCE.updateText(LocalizationManager.INSTANCE.computeText("commands.bind.no-such-button", new Object[0]));
                                 return 1;
                              }

                              value = value2;
                           } else {
                              value = -1;
                           }

                           int value3 = value;
                           if (value3 == 0) {
                              INSTANCE.updateText(LocalizationManager.INSTANCE.computeText("commands.bind.no-such-button", new Object[0]));
                              return 1;
                           } else {
                              argument.updateBind(
                                 new Bind(value3, false, !Intrinsics.areEqual(argument2, "M") && KotlinCompat.contains(argument2, "M", false, 2, null))
                              );
                              INSTANCE.applyAppend(
                                 (Component)ComponentUtil.computeMutableComponent3(
                                    ComponentUtil.computeMutableComponent2(
                                       ComponentUtil.computeMutableComponent(ComponentUtil.getMutableComponent(), argument.getText3()),
                                       LocalizationManager.INSTANCE.computeText("commands.bind.bound-to", new Object[0])
                                    ),
                                    argument2.toUpperCase(Locale.ROOT)
                                 )
                              );
                              return 1;
                           }
                        }
                     }
                  )
            )
      );
      builder.then(
         this.computeLiteralArgumentBuilder("list")
            .executes(
               arg2 -> {
                  StringBuilder stringBuilder = new StringBuilder(LocalizationManager.INSTANCE.computeText("commands.bind.binds", new Object[0]));

                  for (Module items2Item : (Iterable<Module>)ModuleManager.INSTANCE.getItems2()) {
                     if (!Intrinsics.areEqual(KeyNames.INSTANCE.computeText(items2Item.getBind().getValue()), "None")) {
                        stringBuilder.append("\n" + items2Item.getText3())
                           .append("-> ")
                           .append(KeyNames.INSTANCE.computeText(items2Item.getBind().getValue()))
                           .append(items2Item.getBind().isFlag() ? "[hold]" : "");
                     }
                  }

                  INSTANCE.updateText(stringBuilder.toString());
                  return 1;
               }
            )
      );
      builder.then(this.computeLiteralArgumentBuilder("clear").executes(arg3 -> {
         Iterable<Module> items2 = ModuleManager.INSTANCE.getItems2();
         var arrayList = new ArrayList();

         for (Object object : items2) {
            Module module = (Module)object;
            if (!Intrinsics.areEqual(module, ClientInterface.INSTANCE)) {
               arrayList.add(object);
            }
         }

         for (Object object2 : arrayList) {
            Module module2 = (Module)object2;
            module2.updateBind(new Bind(0, false, false, 7, null));
         }

         return 1;
      }));
      builder.then(this.computeLiteralArgumentBuilder("reset").executes(arg4 -> {
         Iterable<Module> items22 = ModuleManager.INSTANCE.getItems2();
         var arrayList2 = new ArrayList();

         for (Object object3 : items22) {
            Module module3 = (Module)object3;
            if (!Intrinsics.areEqual(module3, ClientInterface.INSTANCE)) {
               arrayList2.add(object3);
            }
         }

         for (Object object4 : arrayList2) {
            Module module4 = (Module)object4;
            module4.updateBind(new Bind(0, false, false, 7, null));
         }

         return 1;
      }));
      builder.executes(arg5 -> {
         INSTANCE.updateText(ChatFormatting.RED.toString() + "bind <id> <key> / bind list / bind clear");
         return 1;
      });
   }
}