// deobf by pusia
package su.catlean.client.commands.impl;

import su.catlean.utils.KotlinCompat;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.commands.Command;
import su.catlean.utils.MinecraftHolder;

public final class AngleCommand extends Command {
   @NotNull
   public static final AngleCommand INSTANCE = new AngleCommand();

   private AngleCommand() {
      super("angle");
   }

   @Override
   public void applyExecutes(LiteralArgumentBuilder builder) {
      ((LiteralArgumentBuilder)builder.then(
            this.computeRequiredArgumentBuilder("yaw", (ArgumentType)StringArgumentType.string())
               .then(this.computeRequiredArgumentBuilder("pitch", (ArgumentType)StringArgumentType.string()).executes(arg -> {
                  String argument = (String)arg.getArgument("yaw", String.class);
                  String argument2 = (String)arg.getArgument("pitch", String.class);
                  MinecraftHolder.getLocalPlayer().setYRot(computeAmount(argument, MinecraftHolder.getLocalPlayer().getYRot()));
                  MinecraftHolder.getLocalPlayer().setXRot(computeAmount(argument2, MinecraftHolder.getLocalPlayer().getXRot()));
                  return 1;
               }))
         ))
         .executes(arg2 -> {
            INSTANCE.updateText("angle <yaw> <pitch>");
            return 1;
         });
   }

   private static final float computeAmount(String text, float amount) {
      float amount2;
      if (KotlinCompat.startsWith(text, "~", false, 2, null)) {
         Float floatOrNull = StringsKt.toFloatOrNull(StringsKt.removePrefix(text, "~"));
         float value = floatOrNull != null ? floatOrNull : 0.0F;
         amount2 = amount + value;
      } else {
         Float floatOrNull2 = StringsKt.toFloatOrNull(text);
         amount2 = floatOrNull2 != null ? floatOrNull2 : amount;
      }

      return amount2;
   }
}